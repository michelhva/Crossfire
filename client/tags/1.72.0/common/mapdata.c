/*
 * Crossfire -- cooperative multi-player graphical RPG and adventure game
 *
 * Copyright (c) 1999-2013 Mark Wedel and the Crossfire Development Team
 * Copyright (c) 1992 Frank Tore Johansen
 *
 * Crossfire is free software and comes with ABSOLUTELY NO WARRANTY. You are
 * welcome to redistribute it under certain conditions. For details, please
 * see COPYING and LICENSE.
 *
 * The authors can be reached via e-mail at <crossfire@metalforge.org>.
 */

/**
 * @file
 * Map processing functions.
 */

#include "client.h"

#include <assert.h>
#include <stdbool.h>

#include "external.h"
#include "mapdata.h"

/**
 * Size of virtual map.
 */
#define FOG_MAP_SIZE 512

/**
 * After shifting the virtual map: new minimum distance of the view area to the
 * new virtual map border.
 */
#define FOG_BORDER_MIN 128

/**
 * Maximum size of a big face image in tiles. Larger faces will be clipped top/left.
 */
#define MAX_FACE_SIZE 16

/* Max it can currently be.  Important right now because
 * animation has to look at everything that may be viewable,
 * and reducing this size basically reduces processing it needs
 * to do by 75% (64^2 vs 33^2)
 */
#define CURRENT_MAX_VIEW    33

/**
 * The struct BigCell describes a tile *outside* the view area. head contains
 * the head (as sent by the server), tail contains the expanded big face. tail
 * is *not* set for the head cell, that is (for example) a big face with size
 * 2x3 occupies exactly 6 entries: 1 head and 5 tails.
 *
 * next and prev for a doubly linked list of all currently active entries.
 * Unused entries are set to NULL.
 *
 * x, y, and layer contain the position of the cell in the bigfaces[] array.
 * This information allows to find the corresponding bigfaces[] cell when
 * iterating through the next pointers.
 */
struct BigCell {
    struct BigCell *next;
    struct BigCell *prev;

    struct MapCellLayer head;
    struct MapCellLayer tail;

    guint16 x, y;
    guint8 layer;
};


static void recenter_virtual_map_view(int diff_x, int diff_y);
static void mapdata_get_image_size(int face, guint8 *w, guint8 *h);


/**
 * Viewable map size, in number of tiles.
 */
static int width, height;


/**
 * Contains the head of a list of all currently active big faces outside the
 * view area. All entries are part of bigfaces[].
 */
static struct BigCell *bigfaces_head;

/**
 * The variable bigfaces[] contains information about big faces (faces with a
 * width or height >1). The viewable area bigfaces[0..width-1][0..height-1] is
 * unused.
 */
static struct BigCell bigfaces[MAX_VIEW][MAX_VIEW][MAXLAYERS];


struct Map the_map;

/**
 * Clear cells the_map.cells[x][y..y+len_y-1].
 */
static void clear_cells(int x, int y, int len_y) {
    int clear_cells_i, j;
    memset(&the_map.cells[(x)][(y)], 0, sizeof(the_map.cells[(x)][(y)])*(len_y));

    for (clear_cells_i = 0; clear_cells_i < (len_y); clear_cells_i++) {
        for (j=0; j < MAXLAYERS; j++) {
            the_map.cells[(x)][(y)+clear_cells_i].heads[j].size_x = 1;
            the_map.cells[(x)][(y)+clear_cells_i].heads[j].size_y = 1;
        }
    }
}

/**
 * Get the stored map cell at the given map coordinate.
 */
struct MapCell *mapdata_cell(int x, int y) {
    return &the_map.cells[x][y];
}

/**
 * Determine whether the map data contains the given cell.
 */
bool mapdata_contains(int x, int y) {
    if (x < 0 || y < 0 || the_map.x <= x || the_map.y <= y) {
        return false;
    }

    return true;
}

bool mapdata_can_smooth(int x, int y, int layer) {
    return (mapdata_cell(x, y)->heads[layer].face == 0 && layer > 0) ||
            mapdata_cell(x, y)->smooth[layer];
}

/**
 * Determine the size of the internal fog-of-war map.
 */
void mapdata_size(int *x, int *y) {
    if (x != NULL) {
        *x = the_map.x;
    }

    if (y != NULL) {
        *y = the_map.y;
    }
}

/**
 * Update darkness information. This function is called whenever a map1a
 * command from the server was received.
 *
 * x and y are absolute coordinates into the_map.cells[].
 *
 * darkness is the new darkness value.
 */
static void set_darkness(int x, int y, int darkness)
{
    mapdata_cell(x, y)->have_darkness = 1;
    if (mapdata_cell(x, y)->darkness == darkness) {
        return;
    }

    mapdata_cell(x, y)->darkness = darkness;
    mapdata_cell(x, y)->need_update = 1;

    /* pretty ugly - since the light code with pngximage uses neighboring
     * spaces to adjust the darkness, we now need to let the neighbors know
     * they should update their darkness now.
     */
    if (use_config[CONFIG_DISPLAYMODE] == CFG_DM_SDL
            && (use_config[CONFIG_LIGHTING] == CFG_LT_PIXEL
                ||  use_config[CONFIG_LIGHTING] == CFG_LT_PIXEL_BEST)) {
        if (x > 1) {
            the_map.cells[x-1][y].need_update = 1;
        }
        if (y > 1) {
            the_map.cells[x][y-1].need_update = 1;
        }
        if (x < width-1) {
            the_map.cells[x+1][y].need_update = 1;
        }
        if (y < height-1) {
            the_map.cells[x][y+1].need_update = 1;
        }
    }
}

static void mark_resmooth(int x, int y, int layer)
{
    int sdx,sdy;
    if (mapdata_cell(x, y)->smooth[layer]>1) {
        for (sdx=-1; sdx<2; sdx++)
            for (sdy=-1; sdy<2; sdy++)
                if ( (sdx || sdy) /* ignore (0,0) */
                        &&  ( (x+sdx >0) && (x+sdx < FOG_MAP_SIZE) &&  /* only inside map */
                              (y+sdy >0) && (y+sdy < FOG_MAP_SIZE) ) ) {
                    the_map.cells[x+sdx][y+sdy].need_resmooth=1;
                }
    }
}
/**
 * Clear a face from the_map.cells[].
 *
 * x, y, and layer are the coordinates of the head and layer relative to
 * pl_pos.
 *
 * w and h give the width and height of the face to clear.
 */
static void expand_clear_face(int x, int y, int w, int h, int layer)
{
    int dx, dy;
    struct MapCell *cell;

    assert(0 <= x && x < FOG_MAP_SIZE);
    assert(0 <= y && y < FOG_MAP_SIZE);
    assert(1 <= w && w <= MAX_FACE_SIZE);
    assert(1 <= h && h <= MAX_FACE_SIZE);

    assert(0 <= x-w+1 && x-w+1 < FOG_MAP_SIZE);
    assert(0 <= y-h+1 && y-h+1 < FOG_MAP_SIZE);

    cell = mapdata_cell(x, y);

    for (dx = 0; dx < w; dx++) {
        for (dy = !dx; dy < h; dy++) {
            struct MapCellLayer *tail = &the_map.cells[x-dx][y-dy].tails[layer];
            assert(0 <= x-dx && x-dx < FOG_MAP_SIZE);
            assert(0 <= y-dy && y-dy < FOG_MAP_SIZE);
            assert(0 <= layer && layer < MAXLAYERS);

            /* Do not clear faces that already have been overwritten by another
             * face.
             */
            if (tail->face == cell->heads[layer].face
                    && tail->size_x == dx
                    && tail->size_y == dy) {
                tail->face = 0;
                tail->size_x = 0;
                tail->size_y = 0;
                the_map.cells[x-dx][y-dy].need_update = 1;
            }
            mark_resmooth(x-dx,y-dy,layer);
        }
    }

    cell->heads[layer].face = 0;
    cell->heads[layer].animation = 0;
    cell->heads[layer].animation_speed = 0;
    cell->heads[layer].animation_left = 0;
    cell->heads[layer].animation_phase = 0;
    cell->heads[layer].size_x = 1;
    cell->heads[layer].size_y = 1;
    cell->need_update = 1;
    cell->need_resmooth = 1;
    mark_resmooth(x,y,layer);
}

/**
 * Clear a face from the_map.cells[].
 *
 * x, y, and layer are the coordinates of the head and layer relative to
 * pl_pos.
 */
static void expand_clear_face_from_layer(int x, int y, int layer)
{
    const struct MapCellLayer *cell;

    assert(0 <= x && x < FOG_MAP_SIZE);
    assert(0 <= y && y < FOG_MAP_SIZE);
    assert(0 <= layer && layer < MAXLAYERS);

    cell = &mapdata_cell(x, y)->heads[layer];
    if (cell->size_x && cell->size_y) {
        expand_clear_face(x, y, cell->size_x, cell->size_y, layer);
    }
}

/**
 * Update a face into the_map.cells[].
 *
 * x, y, and layer are the coordinates and layer of the head relative to
 * pl_pos.
 *
 * face is the new face to set.
 * if clear is set, clear this face.  If not set, don't clear.  the reason
 * clear may not be set is because this is an animation update - animations
 * must all be the same size, so when we set the data for the space,
 * we will just overwrite the old data.  Problem with clearing is that
 * clobbers the animation data.
 */
static void expand_set_face(int x, int y, int layer, gint16 face, int clear)
{
    struct MapCell *cell;
    int dx, dy;
    guint8 w, h;

    assert(0 <= x && x < FOG_MAP_SIZE);
    assert(0 <= y && y < FOG_MAP_SIZE);
    assert(0 <= layer && layer < MAXLAYERS);

    cell = mapdata_cell(x, y);

    if (clear) {
        expand_clear_face_from_layer(x, y, layer);
    }

    mapdata_get_image_size(face, &w, &h);
    assert(1 <= w && w <= MAX_FACE_SIZE);
    assert(1 <= h && h <= MAX_FACE_SIZE);
    cell->heads[layer].face = face;
    cell->heads[layer].size_x = w;
    cell->heads[layer].size_y = h;
    cell->need_update=1;
    mark_resmooth(x,y,layer);

    for (dx = 0; dx < w; dx++) {
        for (dy = !dx; dy < h; dy++) {
            struct MapCellLayer *tail = &the_map.cells[x-dx][y-dy].tails[layer];
            assert(0 <= x-dx && x-dx < FOG_MAP_SIZE);
            assert(0 <= y-dy && y-dy < FOG_MAP_SIZE);
            assert(0 <= layer && layer < MAXLAYERS);

            tail->face = face;
            tail->size_x = dx;
            tail->size_y = dy;
            the_map.cells[x-dx][y-dy].need_update = 1;
            mark_resmooth(x-dx,y-dy,layer);
        }
    }
}

/**
 * Clear a face from bigfaces[].
 *
 * x, y, and layer are the coordinates and layer of the head relative to
 * pl_pos.
 *
 * w and h give the width and height of the face to clear.
 *
 * If set_need_update is set, all affected tiles are marked as "need_update".
 */
static void expand_clear_bigface(int x, int y, int w, int h, int layer, int set_need_update)
{
    int dx, dy;
    struct MapCellLayer *head;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);
    assert(1 <= w && w <= MAX_FACE_SIZE);
    assert(1 <= h && h <= MAX_FACE_SIZE);

    head = &bigfaces[x][y][layer].head;

    for (dx = 0; dx < w && dx <= x; dx++) {
        for (dy = !dx; dy < h && dy <= y; dy++) {
            struct MapCellLayer *tail = &bigfaces[x-dx][y-dy][layer].tail;
            assert(0 <= x-dx && x-dx < MAX_VIEW);
            assert(0 <= y-dy && y-dy < MAX_VIEW);
            assert(0 <= layer && layer < MAXLAYERS);

            /* Do not clear faces that already have been overwritten by another
             * face.
             */
            if (tail->face == head->face
                    && tail->size_x == dx
                    && tail->size_y == dy) {
                tail->face = 0;
                tail->size_x = 0;
                tail->size_y = 0;

                if (0 <= x-dx && x-dx < width
                        && 0 <= y-dy && y-dy < height) {
                    assert(0 <= pl_pos.x+x-dx && pl_pos.x+x-dx < FOG_MAP_SIZE);
                    assert(0 <= pl_pos.y+y-dy && pl_pos.y+y-dy < FOG_MAP_SIZE);
                    if (set_need_update) {
                        the_map.cells[pl_pos.x+x-dx][pl_pos.y+y-dy].need_update = 1;
                    }
                }
            }
        }
    }

    head->face = 0;
    head->size_x = 1;
    head->size_y = 1;
}

/**
 * Clear a face from bigfaces[].
 *
 * x, y, and layer are the coordinates and layer of the head relative to
 * pl_pos.
 *
 * If set_need_update is set, all affected tiles are marked as "need_update".
 */
static void expand_clear_bigface_from_layer(int x, int y, int layer, int set_need_update)
{
    struct BigCell *headcell;
    const struct MapCellLayer *head;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);
    assert(0 <= layer && layer < MAXLAYERS);

    headcell = &bigfaces[x][y][layer];
    head = &headcell->head;
    if (head->face != 0) {
        assert(headcell->prev != NULL || headcell == bigfaces_head);

        /* remove from bigfaces_head list */
        if (headcell->prev != NULL) {
            headcell->prev->next = headcell->next;
        }
        if (headcell->next != NULL) {
            headcell->next->prev = headcell->prev;
        }
        if (bigfaces_head == headcell) {
            assert(headcell->prev == NULL);
            bigfaces_head = headcell->next;
        } else {
            assert(headcell->prev != NULL);
        }
        headcell->prev = NULL;
        headcell->next = NULL;

        expand_clear_bigface(x, y, head->size_x, head->size_y, layer, set_need_update);
    } else {
        assert(headcell->prev == NULL && headcell != bigfaces_head);
        assert(head->size_x == 1);
        assert(head->size_y == 1);
    }
}

/**
 * Update a face into bigfaces[].
 *
 * x, y, and layer are the coordinates and layer of the head relative to
 * pl_pos.
 *
 * face is the new face to set.
 */
static void expand_set_bigface(int x, int y, int layer, gint16 face, int clear)
{
    struct BigCell *headcell;
    struct MapCellLayer *head;
    int dx, dy;
    guint8 w, h;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);
    assert(0 <= layer && layer < MAXLAYERS);

    headcell = &bigfaces[x][y][layer];
    head = &headcell->head;
    if (clear) {
        expand_clear_bigface_from_layer(x, y, layer, 1);
    }

    /* add to bigfaces_head list */
    if (face != 0) {
        assert(headcell->prev == NULL);
        assert(headcell->next == NULL);
        assert(headcell != bigfaces_head);
        if (bigfaces_head != NULL) {
            assert(bigfaces_head->prev == NULL);
            bigfaces_head->prev = headcell;
        }
        headcell->next = bigfaces_head;
        bigfaces_head = headcell;
    }

    mapdata_get_image_size(face, &w, &h);
    assert(1 <= w && w <= MAX_FACE_SIZE);
    assert(1 <= h && h <= MAX_FACE_SIZE);
    head->face = face;
    head->size_x = w;
    head->size_y = h;

    for (dx = 0; dx < w && dx <= x; dx++) {
        for (dy = !dx; dy < h && dy <= y; dy++) {
            struct MapCellLayer *tail = &bigfaces[x-dx][y-dy][layer].tail;
            assert(0 <= x-dx && x-dx < MAX_VIEW);
            assert(0 <= y-dy && y-dy < MAX_VIEW);
            assert(0 <= layer && layer < MAXLAYERS);

            tail->face = face;
            tail->size_x = dx;
            tail->size_y = dy;

            if (0 <= x-dx && x-dx < width
                    && 0 <= y-dy && y-dy < height) {
                assert(0 <= pl_pos.x+x-dx && pl_pos.x+x-dx < FOG_MAP_SIZE);
                assert(0 <= pl_pos.y+y-dy && pl_pos.y+y-dy < FOG_MAP_SIZE);
                the_map.cells[pl_pos.x+x-dx][pl_pos.y+y-dy].need_update = 1;
            }
        }
    }
}

/**
 * Mark a face as "need_update".
 *
 * x and y are the coordinates of the head relative to pl_pos.
 *
 * w and h is the size of the face.
 */
static void expand_need_update(int x, int y, int w, int h)
{
    int dx, dy;

    assert(0 <= x && x < FOG_MAP_SIZE);
    assert(0 <= y && y < FOG_MAP_SIZE);
    assert(1 <= w && w <= MAX_FACE_SIZE);
    assert(1 <= h && h <= MAX_FACE_SIZE);

    assert(0 <= x-w+1 && x-w+1 < FOG_MAP_SIZE);
    assert(0 <= y-h+1 && y-h+1 < FOG_MAP_SIZE);

    for (dx = 0; dx < w; dx++) {
        for (dy = 0; dy < h; dy++) {
            struct MapCell *cell = &the_map.cells[x-dx][y-dy];
            assert(0 <= x-dx && x-dx < FOG_MAP_SIZE);
            assert(0 <= y-dy && y-dy < FOG_MAP_SIZE);
            cell->need_update = 1;
        }
    }
}

/**
 * Mark a face as "need_update".
 *
 * x, y, and layer are the coordinates and layer of the head relative to
 * pl_pos.
 */
static void expand_need_update_from_layer(int x, int y, int layer)
{
    struct MapCellLayer *head;

    assert(0 <= x && x < FOG_MAP_SIZE);
    assert(0 <= y && y < FOG_MAP_SIZE);
    assert(0 <= layer && layer < MAXLAYERS);

    head = &mapdata_cell(x, y)->heads[layer];
    if (head->face != 0) {
        expand_need_update(x, y, head->size_x, head->size_y);
    } else {
        assert(head->size_x == 1);
        assert(head->size_y == 1);
    }
}

void mapdata_init(void)
{
    int x, y;
    int i;

    if (the_map.cells == NULL) {
        the_map.cells = g_malloc(
                            sizeof(*the_map.cells)*FOG_MAP_SIZE+
                            sizeof(**the_map.cells)*FOG_MAP_SIZE*FOG_MAP_SIZE);
        if (the_map.cells == NULL) {
            LOG(LOG_ERROR, "mapdata_init", "%s\n", "out of memory");
            exit(1);
        }

        /* Skip past the first row of pointers to rows and assign the
         * start of the actual map data
         */
        the_map.cells[0] = (struct MapCell *)((char *)the_map.cells+(sizeof(struct MapCell *)*FOG_MAP_SIZE));

        /* Finish assigning the beginning of each row relative to the
         * first row assigned above
         */
        for (i = 0; i < FOG_MAP_SIZE; i++) {
            the_map.cells[i] = the_map.cells[0]+i*FOG_MAP_SIZE;
        }
        the_map.x = FOG_MAP_SIZE;
        the_map.y = FOG_MAP_SIZE;
    }

    width = 0;
    height = 0;
    pl_pos.x = FOG_MAP_SIZE/2-width/2;
    pl_pos.y = FOG_MAP_SIZE/2-height/2;

    for (x = 0; x < FOG_MAP_SIZE; x++) {
        clear_cells(x, 0, FOG_MAP_SIZE);
    }

    for (y = 0; y < MAX_VIEW; y++) {
        for (x = 0; x < MAX_VIEW; x++) {
            for (i = 0; i < MAXLAYERS; i++) {
                bigfaces[x][y][i].next = NULL;
                bigfaces[x][y][i].prev = NULL;
                bigfaces[x][y][i].head.face = 0;
                bigfaces[x][y][i].head.size_x = 1;
                bigfaces[x][y][i].head.size_y = 1;
                bigfaces[x][y][i].tail.face = 0;
                bigfaces[x][y][i].tail.size_x = 0;
                bigfaces[x][y][i].tail.size_y = 0;
                bigfaces[x][y][i].x = x;
                bigfaces[x][y][i].y = y;
                bigfaces[x][y][i].layer = i;
            }
        }
    }
    bigfaces_head = NULL;
}

void mapdata_set_size(int viewx, int viewy)
{
    mapdata_init();

    width = viewx;
    height = viewy;
    pl_pos.x = FOG_MAP_SIZE/2-width/2;
    pl_pos.y = FOG_MAP_SIZE/2-height/2;
}

int mapdata_is_inside(int x, int y)
{
    return(x >= 0 && x < width && y >= 0 && y < height);
}

/* mapdate_clear_space() is used by Map2Cmd()
 * Basically, server has told us there is nothing on
 * this space.  So clear it.
 */
void mapdata_clear_space(int x, int y)
{
    int px, py;
    int i;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    if (x < width && y < height) {
        /* tile is visible */

        /* visible tile is now blank ==> do not clear but mark as cleared */
        if (!mapdata_cell(px, py)->cleared) {
            mapdata_cell(px, py)->cleared = 1;
            mapdata_cell(px, py)->need_update = 1;

            for (i=0; i < MAXLAYERS; i++)
                if (mapdata_cell(px, py)->heads[i].face) {
                    expand_need_update_from_layer(px, py, i);
                }
        }
    } else {
        /* tile is invisible (outside view area, i.e. big face update) */

        for (i = 0; i < MAXLAYERS; i++) {
            expand_set_bigface(x, y, i, 0, TRUE);
        }
    }
}


/* With map2, we basically process a piece of data at a time.  Thus,
 * for each piece, we don't know what the final state of the space
 * will be.  So once Map2Cmd() has processed all the information for
 * a space, it calls mapdata_set_check_space() which can see if
 * the space is cleared or other inconsistencies.
 */
void mapdata_set_check_space(int x, int y)
{
    int px, py;
    int is_blank;
    int i;
    struct MapCell *cell;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;

    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);


    is_blank=1;
    cell = mapdata_cell(px, py);
    for (i=0; i < MAXLAYERS; i++) {
        if (cell->heads[i].face>0 || cell->tails[i].face>0) {
            is_blank=0;
            break;
        }
    }

    if (cell->have_darkness) {
        is_blank=0;
    }

    /* We only care if this space needs to be blanked out */
    if (!is_blank) {
        return;
    }

    if (x < width && y < height) {
        /* tile is visible */

        /* visible tile is now blank ==> do not clear but mark as cleared */
        if (!mapdata_cell(px, py)->cleared) {
            mapdata_cell(px, py)->cleared = 1;
            mapdata_cell(px, py)->need_update = 1;

            for (i=0; i < MAXLAYERS; i++) {
                expand_need_update_from_layer(px, py, i);
            }
        }
    }
}



/* This just sets the darkness for a space.
 * Used by Map2Cmd()
 */
void mapdata_set_darkness(int x, int y, int darkness)
{
    int px, py;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    /* Ignore darkness information for tile outside the viewable area: if
     * such a tile becomes visible again, it is either "fog of war" (and
     * darkness information is ignored) or it will be updated (including
     * the darkness information).
     */
    if (darkness != -1 && x < width && y < height) {
        set_darkness(px, py, 255-darkness);
    }
}

/* Sets smooth information for layer */
void mapdata_set_smooth(int x, int y, int smooth, int layer)
{
    static int dx[8]= {0,1,1,1,0,-1,-1,-1};
    static int dy[8]= {-1,-1,0,1,1,1,0,-1};
    int rx, ry, px, py, i;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    if (mapdata_cell(px, py)->smooth[layer] != smooth) {
        for (i=0; i<8; i++) {
            rx=px+dx[i];
            ry=py+dy[i];
            if ( (rx<0) || (ry<0) || (the_map.x<=rx) || (the_map.y<=ry)) {
                continue;
            }
            the_map.cells[rx][ry].need_resmooth=1;
        }
        mapdata_cell(px, py)->need_resmooth=1;
        mapdata_cell(px, py)->smooth[layer] = smooth;
    }
}

/* If old cell data is set and is to be cleared, clear it.
 * This used to be in mapdata_set_face_layer(), however it needs to be
 * called here, earlier in the Map2Cmd() because otherwise darkness
 * doesn't work went sent before the layer data when that square was
 * going to be cleared. This is used by the Map2Cmd()
 */
void mapdata_clear_old(int x, int y)
{
    int px, py;
    int i;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    if (x < width && y < height)
        if (mapdata_cell(px, py)->cleared) {
            for (i=0; i < MAXLAYERS; i++) {
                expand_clear_face_from_layer(px, py, i);
            }

            mapdata_cell(px, py)->darkness = 0;
            mapdata_cell(px, py)->have_darkness = 0;
        }
}

/* This is vaguely related to the mapdata_set_face() above, but rather
 * than take all the faces, takes 1 face and the layer this face is
 * on.  This is used by the Map2Cmd()
 */
void mapdata_set_face_layer(int x, int y, gint16 face, int layer)
{
    int px, py;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    if (x < width && y < height) {
        mapdata_cell(px, py)->need_update = 1;
        if (face >0) {
            expand_set_face(px, py, layer, face, TRUE);
        } else {
            expand_clear_face_from_layer(px, py, layer);
        }

        mapdata_cell(px, py)->cleared = 0;
    } else {
        expand_set_bigface(x, y, layer, face, TRUE);
    }
}


/* This is vaguely related to the mapdata_set_face() above, but rather
 * than take all the faces, takes 1 face and the layer this face is
 * on.  This is used by the Map2Cmd()
 */
void mapdata_set_anim_layer(int x, int y, guint16 anim, guint8 anim_speed, int layer)
{
    int px, py;
    int i, face, animation, phase, speed_left;

    assert(0 <= x && x < MAX_VIEW);
    assert(0 <= y && y < MAX_VIEW);

    px = pl_pos.x+x;
    py = pl_pos.y+y;
    assert(0 <= px && px < FOG_MAP_SIZE);
    assert(0 <= py && py < FOG_MAP_SIZE);

    animation = anim & ANIM_MASK;
    face = 0;

    /* Random animation is pretty easy */
    if ((anim & ANIM_FLAGS_MASK) == ANIM_RANDOM) {
        phase = g_random_int() % animations[animation].num_animations;
        face = animations[animation].faces[phase];
        speed_left = anim_speed % g_random_int();
    } else if ((anim & ANIM_FLAGS_MASK) == ANIM_SYNC) {
        animations[animation].speed = anim_speed;
        phase = animations[animation].phase;
        speed_left = animations[animation].speed_left;
        face = animations[animation].faces[phase];
    }

    if (x < width && y < height) {
        mapdata_cell(px, py)->need_update = 1;
        if (mapdata_cell(px, py)->cleared) {
            for (i=0; i < MAXLAYERS; i++) {
                expand_clear_face_from_layer(px, py, i);
            }

            mapdata_cell(px, py)->darkness = 0;
            mapdata_cell(px, py)->have_darkness = 0;
        }
        if (face >0) {
            expand_set_face(px, py, layer, face, TRUE);
            mapdata_cell(px, py)->heads[layer].animation = animation;
            mapdata_cell(px, py)->heads[layer].animation_phase = phase;
            mapdata_cell(px, py)->heads[layer].animation_speed = anim_speed;
            mapdata_cell(px, py)->heads[layer].animation_left = speed_left;
        } else {
            expand_clear_face_from_layer(px, py, layer);
        }

        mapdata_cell(px, py)->cleared = 0;

    } else {
        expand_set_bigface(x, y, layer, face, TRUE);
    }
}


void mapdata_scroll(int dx, int dy)
{
    int x, y;

    recenter_virtual_map_view(dx, dy);

    if (want_config[CONFIG_MAPSCROLL] && display_mapscroll(dx, dy)) {
        struct BigCell *cell;

        /* Mark all tiles as "need_update" that are overlapped by a big face
         * from outside the view area.
         */
        for (cell = bigfaces_head; cell != NULL; cell = cell->next) {
            for (x = 0; x < cell->head.size_x; x++) {
                for (y = !x; y < cell->head.size_y; y++) {
                    if (0 <= cell->x-x && cell->x-x < width
                            && 0 <= cell->y-y && cell->y-y < height) {
                        the_map.cells[pl_pos.x+cell->x-x][pl_pos.y+cell->y-y].need_update = 1;
                    }
                }
            }
        }
    } else {
        /* Emulate map scrolling by redrawing all tiles. */
        for (x = 0; x < width; x++) {
            for (y = 0; y < height; y++) {
                the_map.cells[pl_pos.x+x][pl_pos.y+y].need_update = 1;
            }
        }
    }

    pl_pos.x += dx;
    pl_pos.y += dy;

    /* clear all newly visible tiles */
    if (dx > 0) {
        for (y = 0; y < height; y++) {
            for (x = width-dx; x < width; x++) {
                the_map.cells[pl_pos.x+x][pl_pos.y+y].cleared = 1;
                the_map.cells[pl_pos.x+x][pl_pos.y+y].need_update = 1;
            }
        }
    } else {
        for (y = 0; y < height; y++) {
            for (x = 0; x < -dx; x++) {
                the_map.cells[pl_pos.x+x][pl_pos.y+y].cleared = 1;
                the_map.cells[pl_pos.x+x][pl_pos.y+y].need_update = 1;
            }
        }
    }

    if (dy > 0) {
        for (x = 0; x < width; x++) {
            for (y = height-dy; y < height; y++) {
                the_map.cells[pl_pos.x+x][pl_pos.y+y].cleared = 1;
                the_map.cells[pl_pos.x+x][pl_pos.y+y].need_update = 1;
            }
        }
    } else {
        for (x = 0; x < width; x++) {
            for (y = 0; y < -dy; y++) {
                the_map.cells[pl_pos.x+x][pl_pos.y+y].cleared = 1;
                the_map.cells[pl_pos.x+x][pl_pos.y+y].need_update = 1;
            }
        }
    }

    /* Remove all big faces outside the view area. */
    while (bigfaces_head != NULL) {
        expand_clear_bigface_from_layer(bigfaces_head->x, bigfaces_head->y, bigfaces_head->layer, 0);
    }
}

void mapdata_newmap(void)
{
    int x, y;

    /* Clear the_map.cells[]. */
    for (x = 0; x < FOG_MAP_SIZE; x++) {
        clear_cells(x, 0, FOG_MAP_SIZE);
        for (y = 0; y < FOG_MAP_SIZE; y++) {
            mapdata_cell(x, y)->need_update = 1;
        }
    }

    /* Clear bigfaces[]. */
    while (bigfaces_head != NULL) {
        expand_clear_bigface_from_layer(bigfaces_head->x, bigfaces_head->y, bigfaces_head->layer, 0);
    }
}

/**
 * Check if the given map tile is a valid slot in the map array.
 */
static bool mapdata_has_tile(int x, int y, int layer) {
    if (0 <= x && x < width && 0 <= y && y < height) {
        if (0 <= layer && layer < MAXLAYERS) {
            return true;
        }
    }

    return false;
}

/**
 * Return the face number of a single-square pixmap at the given map tile.
 * @param x X-coordinate of tile on-screen
 * @param y Y-coordinate of tile on-screen
 * @param layer
 * @return Pixmap face number, or zero if the tile does not exist
 */
gint16 mapdata_face(int x, int y, int layer) {
    if (!mapdata_has_tile(x, y, layer)) {
        return 0;
    }

    return the_map.cells[pl_pos.x+x][pl_pos.y+y].heads[layer].face;
}

gint16 mapdata_face_info(int mx, int my, int layer, int *dx, int *dy) {
    struct MapCellLayer *head = &the_map.cells[mx][my].heads[layer];
    struct MapCellLayer *tail = &the_map.cells[mx][my].tails[layer];
    if (head->face != 0) {
        const int width = head->size_x, height = head->size_y;
        *dx = 1 - width, *dy = 1 - height;
        return head->face;
    } else if (tail->face != 0) {
        struct MapCellLayer *head_ptr = &the_map.cells[mx + tail->size_x][my + tail->size_y].heads[layer];
        const int width = head_ptr->size_x, height = head_ptr->size_y;
        *dx = tail->size_x - width + 1, *dy = tail->size_y - height + 1;
        return tail->face;
    } else {
        return 0;
    }
}

/**
 * Return the face number of a multi-square pixmap at the given map tile.
 * @param x X-coordinate of tile on-screen
 * @param y Y-coordinate of tile on-screen
 * @param layer
 * @param ww
 * @param hh
 * @return Pixmap face number, or zero if the tile does not exist
 */
gint16 mapdata_bigface(int x, int y, int layer, int *ww, int *hh) {
    gint16 result;

    if (!mapdata_has_tile(x, y, layer)) {
        return 0;
    }

    result = the_map.cells[pl_pos.x+x][pl_pos.y+y].tails[layer].face;
    if (result != 0) {
        int clear_bigface;
        int dx = the_map.cells[pl_pos.x+x][pl_pos.y+y].tails[layer].size_x;
        int dy = the_map.cells[pl_pos.x+x][pl_pos.y+y].tails[layer].size_y;
        int w = the_map.cells[pl_pos.x+x+dx][pl_pos.y+y+dy].heads[layer].size_x;
        int h = the_map.cells[pl_pos.x+x+dx][pl_pos.y+y+dy].heads[layer].size_y;
        assert(1 <= w && w <= MAX_FACE_SIZE);
        assert(1 <= h && h <= MAX_FACE_SIZE);
        assert(0 <= dx && dx < w);
        assert(0 <= dy && dy < h);

        /* Now check if we are about to display an obsolete big face: such a
         * face has a cleared ("fog of war") head but the current tile is not
         * fog of war. Since the server would have sent an appropriate head
         * tile if it was already valid, just clear the big face and do not
         * return it.
         */
        if (the_map.cells[pl_pos.x+x][pl_pos.y+y].cleared) {
            /* Current face is a "fog of war" tile ==> do not clear
             * old information.
             */
            clear_bigface = 0;
        } else {
            if (x+dx < width && y+dy < height) {
                /* Clear face if current tile is valid but the
                 * head is marked as cleared.
                 */
                clear_bigface = the_map.cells[pl_pos.x+x+dx][pl_pos.y+y+dy].cleared;
            } else {
                /* Clear face if current tile is valid but the
                 * head is not set.
                 */
                clear_bigface = bigfaces[x+dx][y+dy][layer].head.face == 0;
            }
        }

        if (!clear_bigface) {
            *ww = w-1-dx;
            *hh = h-1-dy;
            return(result);
        }

        assert(the_map.cells[pl_pos.x+x][pl_pos.y+y].tails[layer].face == result);
        expand_clear_face_from_layer(pl_pos.x+x+dx, pl_pos.y+y+dy, layer);
        assert(the_map.cells[pl_pos.x+x][pl_pos.y+y].tails[layer].face == 0);
    }

    result = bigfaces[x][y][layer].tail.face;
    if (result != 0) {
        int dx = bigfaces[x][y][layer].tail.size_x;
        int dy = bigfaces[x][y][layer].tail.size_y;
        int w = bigfaces[x+dx][y+dy][layer].head.size_x;
        int h = bigfaces[x+dx][y+dy][layer].head.size_y;
        assert(0 <= dx && dx < w);
        assert(0 <= dy && dy < h);
        *ww = w-1-dx;
        *hh = h-1-dy;
        return(result);
    }

    *ww = 1;
    *hh = 1;
    return(0);
}

/* This is used by the opengl logic.
 * Basically the opengl code draws the the entire image,
 * and doesn't care if if portions are off the edge
 * (opengl takes care of that).  So basically, this
 * function returns only if the head for a space is set,
 * otherwise, returns 0 - we don't care about the tails
 * or other details really.
 */
gint16 mapdata_bigface_head(int x, int y, int layer, int *ww, int *hh) {
    gint16 result;

    if (!mapdata_has_tile(x, y, layer)) {
        return 0;
    }

    result = bigfaces[x][y][layer].head.face;
    if (result != 0) {
        int w = bigfaces[x][y][layer].head.size_x;
        int h = bigfaces[x][y][layer].head.size_y;
        *ww = w;
        *hh = h;
        return(result);
    }

    *ww = 1;
    *hh = 1;
    return(0);
}

/**
 * Check if current map position is out of bounds if shifted by (dx, dy). If
 * so, shift the virtual map so that the map view is within bounds again.
 *
 * Assures that [pl_pos.x-MAX_FACE_SIZE..pl_pos.x+MAX_VIEW+1] is within the
 * bounds of the virtual map area. This covers the area a map1a command may
 * affect plus a one tile border.
 */
static void recenter_virtual_map_view(int diff_x, int diff_y)
{
    int new_x, new_y;
    int shift_x, shift_y;
    int src_x, src_y;
    int dst_x, dst_y;
    int len_x, len_y;
    int sx;
    int dx;
    int i;

    /* shift player position in virtual map */
    new_x = pl_pos.x+diff_x;
    new_y = pl_pos.y+diff_y;

    /* determine neccessary amount to shift */

    /* if(new_x < 1) is not possible: a big face may reach up to
     * (MAX_FACE_SIZE-1) tiles to the left of pl_pos. Therefore maintain a
     * border of at least MAX_FACE_SIZE to the left of the virtual map
     * edge.
     */
    if (new_x < MAX_FACE_SIZE) {
        shift_x = FOG_BORDER_MIN+MAX_FACE_SIZE-new_x;
        /* This yields: new_x+shift_x == FOG_BORDER_MIN+MAX_FACE_SIZE,
         * i.e. left border is FOG_BORDER_MIN+MAX_FACE_SIZE after
         * shifting.
         */
    } else if (new_x+MAX_VIEW > FOG_MAP_SIZE) {
        shift_x = FOG_MAP_SIZE-FOG_BORDER_MIN-MAX_VIEW-new_x;
        /* This yields: new_x+shift_x ==
         * FOG_MAP_SIZE-FOG_BODER_MIN-MAX_VIEW, i.e. right border is
         * FOGBORDER_MIN after shifting.
         */
    } else {
        shift_x = 0;
    }

    /* Same as above but for y. */
    if (new_y < MAX_FACE_SIZE) {
        shift_y = FOG_BORDER_MIN+MAX_FACE_SIZE-new_y;
    } else if (new_y+MAX_VIEW > FOG_MAP_SIZE) {
        shift_y = FOG_MAP_SIZE-FOG_BORDER_MIN-MAX_VIEW-new_y;
    } else {
        shift_y = 0;
    }

    /* No shift neccessary? ==> nothing to do. */
    if (shift_x == 0 && shift_y == 0) {
        return;
    }

    /* If shifting at all: maintain a border size of FOG_BORDER_MIN to all
     * directions. For example: if pl_pos=30/MAX_FACE_SIZE, and map_scroll is
     * 0/-1: shift pl_pos to FOG_BORDER_MIN+1/FOG_BORDER_MIN+1, not to
     * 30/FOG_BORDER_MIN+1.
     */
    if (shift_x == 0) {
        if (new_x < FOG_BORDER_MIN+MAX_FACE_SIZE) {
            shift_x = FOG_BORDER_MIN+MAX_FACE_SIZE-new_x;
        } else if (new_x+MAX_VIEW+FOG_BORDER_MIN > FOG_MAP_SIZE) {
            shift_x = FOG_MAP_SIZE-FOG_BORDER_MIN-MAX_VIEW-new_x;
        }
    }
    if (shift_y == 0) {
        if (new_y < FOG_BORDER_MIN+MAX_FACE_SIZE) {
            shift_y = FOG_BORDER_MIN+MAX_FACE_SIZE-new_y;
        } else if (new_y+MAX_VIEW+FOG_BORDER_MIN > FOG_MAP_SIZE) {
            shift_y = FOG_MAP_SIZE-FOG_BORDER_MIN-MAX_VIEW-new_y;
        }
    }

    /* Shift for more than virtual map size? ==> clear whole virtual map
     * and recenter.
     */
    if (shift_x <= -FOG_MAP_SIZE || shift_x >= FOG_MAP_SIZE
            || shift_y <= -FOG_MAP_SIZE || shift_y >= FOG_MAP_SIZE) {
        for (dx = 0; dx < FOG_MAP_SIZE; dx++) {
            clear_cells(dx, 0, FOG_MAP_SIZE);
        }

        pl_pos.x = FOG_MAP_SIZE/2-width/2;
        pl_pos.y = FOG_MAP_SIZE/2-height/2;
        return;
    }

    /* Move player position. */
    pl_pos.x += shift_x;
    pl_pos.y += shift_y;

    /* Actually shift the virtual map by shift_x/shift_y */
    if (shift_x < 0) {
        src_x = -shift_x;
        dst_x = 0;
        len_x = FOG_MAP_SIZE+shift_x;
    } else {
        src_x = 0;
        dst_x = shift_x;
        len_x = FOG_MAP_SIZE-shift_x;
    }

    if (shift_y < 0) {
        src_y = -shift_y;
        dst_y = 0;
        len_y = FOG_MAP_SIZE+shift_y;
    } else {
        src_y = 0;
        dst_y = shift_y;
        len_y = FOG_MAP_SIZE-shift_y;
    }

    if (shift_x < 0) {
        for (sx = src_x, dx = dst_x, i = 0; i < len_x; sx++, dx++, i++) {
            /* srcx!=dstx ==> can use memcpy since source and
             * destination to not overlap.
             */
            memcpy(&the_map.cells[dx][dst_y], &the_map.cells[sx][src_y], len_y*sizeof(the_map.cells[dx][dst_y]));
        }
    } else if (shift_x > 0) {
        for (sx = src_x+len_x-1, dx = dst_x+len_x-1, i = 0; i < len_x; sx--, dx--, i++) {
            /* srcx!=dstx ==> can use memcpy since source and
             * destination to not overlap.
             */
            memcpy(&the_map.cells[dx][dst_y], &the_map.cells[sx][src_y], len_y*sizeof(the_map.cells[dx][dst_y]));
        }
    } else {
        assert(src_x == dst_x);
        for (dx = src_x, i = 0; i < len_x; dx++, i++) {
            /* srcx==dstx ==> use memmove since source and
             * destination probably do overlap.
             */
            memmove(&the_map.cells[dx][dst_y], &the_map.cells[dx][src_y], len_y*sizeof(the_map.cells[dx][dst_y]));
        }
    }

    /* Clear newly opened area */
    for (dx = 0; dx < dst_x; dx++) {
        clear_cells(dx, 0, FOG_MAP_SIZE);
    }
    for (dx = dst_x+len_x; dx < FOG_MAP_SIZE; dx++) {
        clear_cells(dx, 0, FOG_MAP_SIZE);
    }
    if (shift_y > 0) {
        for (dx = 0; dx < len_x; dx++) {
            clear_cells(dx+dst_x, 0, shift_y);
        }
    } else if (shift_y < 0) {
        for (dx = 0; dx < len_x; dx++) {
            clear_cells(dx+dst_x, FOG_MAP_SIZE+shift_y, -shift_y);
        }
    }
}

/**
 * Return the size of a face in tiles. The returned size is at between 1 and
 * MAX_FACE_SIZE (inclusive).
 */
static void mapdata_get_image_size(int face, guint8 *w, guint8 *h)
{
    get_map_image_size(face, w, h);
    if (*w < 1) {
        *w = 1;
    }
    if (*h < 1) {
        *h = 1;
    }
    if (*w > MAX_FACE_SIZE) {
        *w = MAX_FACE_SIZE;
    }
    if (*h > MAX_FACE_SIZE) {
        *h = MAX_FACE_SIZE;
    }
}

/* This basically goes through all the map spaces and does the necessary
 * animation.
 */
void mapdata_animation(void)
{
    int x, y, layer, face;
    struct MapCellLayer *cell;


    /* For synchronized animations, what we do is set the initial values
     * in the mapdata to the fields in the animations[] array.  In this way,
     * the code below the iterates the spaces doesn't need to do anything
     * special.  But we have to update the animations[] array here to
     * keep in sync.
     */
    for (x=0; x < MAXANIM; x++) {
        if (animations[x].speed) {
            animations[x].speed_left++;
            if (animations[x].speed_left >= animations[x].speed) {
                animations[x].speed_left=0;
                animations[x].phase++;
                if (animations[x].phase >= animations[x].num_animations) {
                    animations[x].phase=0;
                }
            }
        }
    }

    for (x=0; x < CURRENT_MAX_VIEW; x++) {
        for (y=0; y < CURRENT_MAX_VIEW; y++) {

            /* Short cut some processing here.  It makes sense to me
             * not to animate stuff out of view
             */
            if (the_map.cells[pl_pos.x + x][pl_pos.y + y].cleared) {
                continue;
            }

            for (layer=0; layer<MAXLAYERS; layer++) {
                /* Using the cell structure just makes life easier here */
                cell = &the_map.cells[pl_pos.x+x][pl_pos.y+y].heads[layer];

                if (cell->animation) {
                    cell->animation_left++;
                    if (cell->animation_left >= cell->animation_speed) {
                        cell->animation_left=0;
                        cell->animation_phase++;
                        if (cell->animation_phase >= animations[cell->animation].num_animations) {
                            cell->animation_phase=0;
                        }
                        face = animations[cell->animation].faces[cell->animation_phase];

                        /* I don't think we send any to the client, but it is possible
                         * for animations to have blank faces.
                         */
                        if (face >0) {
                            expand_set_face(pl_pos.x + x, pl_pos.y + y, layer, face, FALSE);
                        } else {
                            expand_clear_face_from_layer(pl_pos.x + x, pl_pos.y + y , layer);
                        }
                    }
                }
                cell = &bigfaces[x][y][layer].head;
                if (cell->animation) {
                    cell->animation_left++;
                    if (cell->animation_left >= cell->animation_speed) {
                        cell->animation_left=0;
                        cell->animation_phase++;
                        if (cell->animation_phase >= animations[cell->animation].num_animations) {
                            cell->animation_phase=0;
                        }
                        face = animations[cell->animation].faces[cell->animation_phase];

                        /* I don't think we send any to the client, but it is possible
                         * for animations to have blank faces.
                         */
                        expand_set_bigface(x, y, layer, face, FALSE);
                    }
                }
            }
        }
    }
}
