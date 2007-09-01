char *rcsid_gtk2_main_c =
    "$Id$";
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2005-2007 Mark Wedel & Crossfire Development Team

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

    The author can be reached via e-mail to crossfire@metalforge.org
*/

/*
 * Initial main.c file generated by Glade. Edit as required.
 * Glade will not overwrite this file.
 */

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#ifdef WIN32
#include <windows.h>
#endif
#include <gtk/gtk.h>
#include <glade/glade.h>
#include <stdio.h>
#include <errno.h>

#include "main.h"
#include "client.h"
#include "image.h"
#include "gtk2proto.h"
#include "script.h"
#include "metaserver.h"
#include "mapdata.h"

GtkWidget *window_root, *magic_map;

/* Sets up the basic colors. */
const char *colorname[NUM_COLORS] = {
"Black",                /* 0  */
"White",                /* 1  */
"Navy",                 /* 2  */
"Red",                  /* 3  */
"Orange",               /* 4  */
"DodgerBlue",           /* 5  */
"DarkOrange2",          /* 6  */
"SeaGreen",             /* 7  */
"DarkSeaGreen",         /* 8  */        /* Used for window background color */
"Grey50",               /* 9  */
"Sienna",               /* 10 */
"Gold",                 /* 11 */
"Khaki"                 /* 12 */
};

/* These are the names as set by the user within the rc file.
 * We use lower case to be consistent, but also change the names
 * to be more generic instead of specific X11 color names.
 */
const char *usercolorname[NUM_COLORS] = {
"black",                /* 0  */
"white",                /* 1  */
"darkblue",             /* 2  */
"red",                  /* 3  */
"orange",               /* 4  */
"lightblue",            /* 5  */
"darkorange",           /* 6  */
"green",                /* 7  */
"darkgreen",            /* 8  */        /* Used for window background color */
"grey"  ,               /* 9  */
"brown",                /* 10 */
"yellow",               /* 11 */
"tan"                   /* 12 */
};

char dialog_xml_file[MAX_BUF] = DIALOG_XML_FILENAME;
char dialog_xml_path[MAX_BUF] = "";
char window_xml_file[MAX_BUF] = WINDOW_XML_FILENAME;
char window_xml_path[MAX_BUF] = "";
GdkColor root_color[NUM_COLORS];
struct timeval timeout;
extern int maxfd;
gint    csocket_fd=0;
static uint8
	updatekeycodes=FALSE;

extern int time_map_redraw;

#ifdef WIN32 /* Win32 scripting support */
#define PACKAGE_DATA_DIR "."

int do_scriptout()
{
    script_process(NULL);
    return(TRUE);
}
#endif /* WIN32 */

int do_timeout()
{
    if (cpl.showmagic) magic_map_flash_pos();
    if (cpl.spells_updated) update_spell_information();
    if (!tick) {
	inventory_tick();
	mapdata_animation();
    }
    return TRUE;
}

/* X11 client doesn't care about this */
void client_tick(uint32 tick)
{
    inventory_tick();
    mapdata_animation();
}

/* Called from disconnect command - that closes the socket -
 * we just need to do the gtk cleanup.
 */
void cleanup_connection()
{
    if (csocket_fd) {
        gdk_input_remove(csocket_fd);
        csocket_fd=0;
        gtk_main_quit();
    }
}

void
on_window_destroy_event                (GtkObject       *object,
                                        gpointer         user_data)
{
#ifdef WIN32
    script_killall();
#endif

    LOG(LOG_INFO,"gtk::client_exit","Exiting with return value 0.");
    exit(0);
}


/* main loop iteration related stuff */
void do_network() {
    fd_set tmp_read;
    int pollret;

    if (csocket.fd==-1) {
	if (csocket_fd) {
	    gdk_input_remove(csocket_fd);
	    csocket_fd=0;
	    gtk_main_quit();
	}
	return;
    }

    FD_ZERO(&tmp_read);
    FD_SET(csocket.fd, &tmp_read);
    script_fdset(&maxfd,&tmp_read);
    pollret = select(maxfd, &tmp_read, NULL, NULL, &timeout);
    if (pollret==-1) {
	LOG(LOG_WARNING,"gtk::do_network", "Got errno %d on select call.", errno);
    }
    else if ( pollret>0 ) {
	if (FD_ISSET(csocket.fd, &tmp_read)) {
	    DoClient(&csocket);
#ifndef WIN32
	    if ( pollret > 1 ) script_process(&tmp_read);
#endif
	}
	else {
	    script_process(&tmp_read);
	}
    }
    /* DoClient now closes the socket, so we need to check for
     * this here - with the socket being closed, this function
     * will otherwise never be called again.
     */
    if (csocket.fd==-1) {
	if (csocket_fd) {
	    gdk_input_remove(csocket_fd);
	    csocket_fd=0;
	    gtk_main_quit();
	}
	return;
    }

#ifdef HAVE_SDL
    if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_SDL) sdl_gen_map(FALSE);
    else
#endif
#ifdef HAVE_OPENGL
    if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_OPENGL) opengl_gen_map(FALSE);
    else
#endif
    draw_map(FALSE);

    draw_lists();
}


void event_loop(void)
{
    gint fleep;
    extern int do_timeout(void);
    int tag;

    if (MAX_TIME==0) {
	timeout.tv_sec = 0;
	timeout.tv_usec = 0;
    }
    maxfd = csocket.fd + 1;

    if (MAX_TIME!=0) {
	timeout.tv_sec = 0;/* MAX_TIME / 1000000;*/
	timeout.tv_usec = 0;/* MAX_TIME % 1000000;*/
    }

    fleep =  gtk_timeout_add (10,
			  (GtkFunction) do_timeout,
			  NULL);

#ifdef WIN32
    gtk_timeout_add (25, (GtkFunction) do_scriptout, NULL);
#endif

    if (csocket.fd==-1) {
	if (csocket_fd) {
	    gdk_input_remove(csocket_fd);
	    csocket_fd=0;
	    gtk_main_quit();
	}
	return;
    }
    csocket_fd = gdk_input_add ((gint) csocket.fd,
                              GDK_INPUT_READ,
                              (GdkInputFunction) do_network, &csocket);
    tag = csocket_fd;

    gtk_main();
    gtk_timeout_remove(tag);

    LOG(LOG_INFO,"gtk::event_loop","gtk_main exited, returning from event_loop");
}


/* Usage routine.  All clients should support server, port and
 * display options, with -pix and -xpm also suggested.  -split
 * does not need to be supported - it is in this copy because
 * the old code supported it.
 */

static void usage(char *progname)
{
    puts("Usage of gcfclient:\n\n");
    puts("-cache           - Cache images for future use.");
    puts("-nocache         - Do not cache images (default action).");
    puts("-darkness        - Enables darkness code (default)");
    puts("-nodarkness      - Disables darkness code");
    puts("-display <name>  - Use <name> instead if DISPLAY environment variable.");
    puts("-download_all_faces - Download all needed faces before play starts");
    puts("-echo            - Echo the bound commands");
    puts("-noecho          - Do not echo the bound commands (default)");
    puts("-faceset <name>  - Use faceset <name> if available");
    puts("-fasttcpsend     - Send data immediately to server, may increase bandwidth");
    puts("-nofasttcpsend   - Disables fasttcpsend");
    puts("-fog             - Enable fog of war code");
    puts("-help            - Display this message.");
    puts("-loglevel <val>  - Set default logging level (0 is most verbose)");
    puts("-iconscale %%    - Set icon scale percentage");
    puts("-mapscale %%     - Set map scale percentage");
    puts("-mapsize xXy     - Set the mapsize to be X by Y spaces. (default 11x11)");
    puts("-splash          - Display the splash screen (default)");
    puts("-nosplash        - Don't display the splash screen (startup logo)");
    puts("-opengl          - Use opengl drawing code");
    puts("-pixmap          - Use pixmap drawing code");
    puts("-port <number>   - Use port <number> instead of the standard port number");
    puts("-sdl             - Use sdl for drawing png (may not work on all hardware");
    puts("-server <name>   - Connect to <name> instead of localhost.");
    puts("-showicon        - Print status icons in inventory window");
    puts("-smooth          - Enable smooth");
    puts("-nosmooth        - Disable smooth (default)");
    puts("-sound           - Enable sound output (default).");
    puts("-nosound         - Disable sound output.");
    puts("-sound_server <path> - Executable to use to play sounds.");
    puts("-resists <val>   - Control look of resistances.");
    puts("-split           - Use split windows.");
    puts("-splitinfo       - Use two information windows, segregated by information type.");
    puts("-timemapredraw   - Print out timing information for map generation");
    puts("-triminfowindow  - Trims size of information window(s)");
    puts("-notriminfowindow  - Do not trims size of information window(s) (default)");
    puts("-updatekeycodes  - Update the saved bindings for this keyboard.");
    puts("-window_xml <file> - Glade Designer client UI layout XML file.");
    puts("-dialog_xml <file> - Glade Designer popup dialog XML file.");

    exit(0);
}

/* parse_args: Parses the command line options, and does some
 * variable initialization.
 *
 * This function returns 0 on success, nonzero on failure.
 */

int parse_args(int argc, char **argv)
{
    int on_arg=1;
    char *display_name="";
    load_defaults();

#ifndef WIN32
    strcpy(VERSION_INFO,"GTK2 Unix Client " FULL_VERSION);
#else
    strcpy(VERSION_INFO,"GTK2 Win32 Client " FULL_VERSION);
#endif
    /* Set this global so we get skill experience - gtk client can display
     * it, so lets get the info.
     */
    want_skill_exp=1;
    for (on_arg=1; on_arg<argc; on_arg++) {
	if (!strcmp(argv[on_arg],"-cache")) {
	    want_config[CONFIG_CACHE]= TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nocache")) {
	    want_config[CONFIG_CACHE]= FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-darkness")) {
	    want_config[CONFIG_DARKNESS]= TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nodarkness")) {
	    want_config[CONFIG_DARKNESS]= FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-display")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-display requires a display name");
		return 1;
	    }
	    display_name = argv[on_arg];
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-download_all_faces")) {
	    want_config[CONFIG_DOWNLOAD]= TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-echo")) {
	    want_config[CONFIG_ECHO]= TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-noecho")) {
	    want_config[CONFIG_ECHO]= FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-faceset")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-faceset requires a faceset name/number");
		return 1;
	    }
	    face_info.want_faceset = argv[on_arg];
	    continue;
	}
	else if( !strcmp( argv[on_arg],"-fog")) {
	    want_config[CONFIG_FOGWAR]= TRUE;
	    continue;
	}
	else if( !strcmp( argv[on_arg],"-nofog")) {
	    want_config[CONFIG_FOGWAR]= FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-help")) {
	    usage(argv[0]);
	    continue;
	}
	else if( !strcmp( argv[on_arg],"-iconscale")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-iconscale requires a percentage value");
		return 1;
	    }
	    want_config[CONFIG_ICONSCALE] = atoi(argv[on_arg]);
	    if (want_config[CONFIG_ICONSCALE] < 25 || want_config[CONFIG_ICONSCALE]>200) {
		LOG(LOG_WARNING,"gtk::init_windows","Valid range for -iconscale is 25 through 200");
		want_config[CONFIG_ICONSCALE]=100;
		return 1;
	    }
	    continue;
	}
	else if( !strcmp( argv[on_arg],"-mapscale")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-mapscale requires a percentage value");
		return 1;
	    }
	    want_config[CONFIG_MAPSCALE] = atoi(argv[on_arg]);
	    if (want_config[CONFIG_MAPSCALE] < 25 || want_config[CONFIG_MAPSCALE]>200) {
		LOG(LOG_WARNING,"gtk::init_windows","Valid range for -mapscale is 25 through 200");
		want_config[CONFIG_MAPSCALE]=100;
		return 1;
	    }
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-mapsize")) {
	    char *cp, x, y=0;
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-mapsize requires a XxY value");
		return 1;
	    }
	    x = atoi(argv[on_arg]);
	    for (cp = argv[on_arg]; *cp!='\0'; cp++)
		if (*cp == 'x' || *cp == 'X') break;

	    if (*cp==0) {
		LOG(LOG_WARNING,"gtk::init_windows","-mapsize requires both and X and Y value (ie, XxY - note the\nx in between.");
	    } else {
		y = atoi(cp+1);
	    }
	    if (x<9 || y<9) {
		LOG(LOG_WARNING,"gtk::init_windows","map size must be positive values of at least 9");
	    } else if (x>MAP_MAX_SIZE || y>MAP_MAX_SIZE) {
		LOG(LOG_WARNING,"gtk::init_windows","Map size can not be larger than %d x %d", MAP_MAX_SIZE, MAP_MAX_SIZE);

	    } else {
		want_config[CONFIG_MAPWIDTH]=x;
		want_config[CONFIG_MAPHEIGHT]=y;
	    }
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-fasttcpsend")) {
	    want_config[CONFIG_FASTTCP] = TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nofasttcpsend")) {
	    want_config[CONFIG_FASTTCP] = FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-opengl")) {
#ifndef HAVE_OPENGL
	    LOG(LOG_WARNING,"gtk::init_windows","client not compiled with opengl support.  Ignoring -opengl");
#else
	    want_config[CONFIG_DISPLAYMODE] = CFG_DM_OPENGL;
#endif
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-pixmap")) {
	    want_config[CONFIG_DISPLAYMODE] = CFG_DM_PIXMAP;
	}
	else if (!strcmp(argv[on_arg],"-port")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-port requires a port number");
		return 1;
	    }
	    want_config[CONFIG_PORT] = atoi(argv[on_arg]);
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-sdl")) {
#ifndef HAVE_SDL
	    LOG(LOG_WARNING,"gtk::init_windows","client not compiled with sdl support.  Ignoring -sdl");
#else
	    want_config[CONFIG_DISPLAYMODE] = CFG_DM_SDL;
#endif
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-server")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-server requires a host name");
		return 1;
	    }
	    server = argv[on_arg];
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-showicon")) {
	    want_config[CONFIG_SHOWICON] = TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-smooth")) {
	    want_config[CONFIG_SMOOTH] = TRUE;
	}
	else if (!strcmp(argv[on_arg],"-nosmooth")) {
	    want_config[CONFIG_SMOOTH] = FALSE;
	}
	else if (!strcmp(argv[on_arg],"-sound")) {
	    want_config[CONFIG_SOUND] = TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nosound")) {
	    want_config[CONFIG_SOUND] = FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-sound_server")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-sound_server requires an executable pathname");
		return 1;
	    }
	    sound_server = argv[on_arg];
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-split")) {
	    want_config[CONFIG_SPLITWIN]=TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nosplit")) {
	    want_config[CONFIG_SPLITWIN]=FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-resists")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-resists requires a value");
		return 1;
	    }
	    want_config[CONFIG_RESISTS]=atoi(argv[on_arg]);
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-loglevel")) {
	    extern int MINLOG;

	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows","-loglevel requires a value");
		return 1;
	    }
	    MINLOG = atoi(argv[on_arg]);
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-splitinfo")) {
	    want_config[CONFIG_SPLITINFO]=TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-timemapredraw")) {
	    time_map_redraw=TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-triminfowindow")) {
	    want_config[CONFIG_TRIMINFO] = TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-notriminfowindow")) {
	    want_config[CONFIG_TRIMINFO] = FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-updatekeycodes")) {
	    updatekeycodes=TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-splash")) {
	    want_config[CONFIG_SPLASH] = TRUE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-nosplash")) {
	    want_config[CONFIG_SPLASH] = FALSE;
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-window_xml")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows",
                    "-window_xml requires a glade xml file name");
		return 1;
	    }
	    strncpy (window_xml_path, argv[on_arg], MAX_BUF-1);
	    continue;
	}
	else if (!strcmp(argv[on_arg],"-dialog_xml")) {
	    if (++on_arg == argc) {
		LOG(LOG_WARNING,"gtk::init_windows",
                    "-dialog_xml requires a glade xml file name");
		return 1;
	    }
	    strncpy (dialog_xml_path, argv[on_arg], MAX_BUF-1);
	    continue;
	}
	else {
	    LOG(LOG_WARNING,"gtk::init_windows","Do not understand option %s", argv[on_arg]);
	    usage(argv[0]);
	    return 1;
	}
    }

    /* Move this after the parsing of command line options,
     * since that can change the default log level.
     */
    LOG(LOG_INFO,"Client Version",VERSION_INFO);

    /* Now copy over the values just loaded */
    for (on_arg=0; on_arg<CONFIG_NUMS; on_arg++) {
        use_config[on_arg] = want_config[on_arg];
    }

    image_size = DEFAULT_IMAGE_SIZE * use_config[CONFIG_ICONSCALE] / 100;
    map_image_size = DEFAULT_IMAGE_SIZE * use_config[CONFIG_MAPSCALE] / 100;
    map_image_half_size = DEFAULT_IMAGE_SIZE * use_config[CONFIG_MAPSCALE] / 200;
    if (!use_config[CONFIG_CACHE]) use_config[CONFIG_DOWNLOAD] = FALSE;

    mapdata_init();

    return 0;
}



int
main (int argc, char *argv[])
{
    int i, got_one=0;
    static char file_cache[ MAX_BUF ];
    GdkGeometry geometry;
    GladeXML *xml_tree;

#ifdef ENABLE_NLS
    bindtextdomain (GETTEXT_PACKAGE, PACKAGE_LOCALE_DIR);
    bind_textdomain_codeset (GETTEXT_PACKAGE, "UTF-8");
    textdomain (GETTEXT_PACKAGE);
#endif

    gtk_set_locale ();
    gtk_init (&argc, &argv);

    /* parse_args() has to come after init_client_vars() */
    init_client_vars();
    use_config[CONFIG_MAPWIDTH] = want_config[CONFIG_MAPWIDTH] = 25;
    use_config[CONFIG_MAPHEIGHT] = want_config[CONFIG_MAPHEIGHT] = 25;


    parse_args(argc, argv);
    load_theme(FALSE);

    csocket.inbuf.buf=malloc(MAXSOCKBUF);

#ifdef WIN32 /* def WIN32 */
    maxfd = 0; /* This is ignored on win32 platforms */

    /* This is required for sockets to be used under win32 */
    {
	WORD Version = 0x0202;
	WSADATA wsaData;
	if (WSAStartup( Version, &wsaData ) != 0) {
	    LOG(LOG_CRITICAL,"gtk::main", "Couldn't load winsock!");
	    exit(1);
	}
    }
#else /* def WIN32 */
#ifdef HAVE_SYSCONF
    maxfd = sysconf(_SC_OPEN_MAX);
#else
    maxfd = getdtablesize();
#endif
#endif /* def WIN32 */

    if (init_sounds() == -1)
	use_config[CONFIG_SOUND] = FALSE;
    else use_config[CONFIG_SOUND] = TRUE;

    /*
     * Load Glade XML layout files for the main client window and for the other
     * popup dialogs.  The popup dialogs must all have the "visible" attribute
     * set to "no" so they are not shown initially.
     *
     * NOTE:  glade_init() is implicitly called on glade_xml_new().
     *
     * First, load up the common dialogs.  If the XML file path is already set,
     * it is because a command-line parameter was used to specify it.  If not
     * set, construct the path to the file from the default path and name
     * settings.
     */
    if (! dialog_xml_path[0]) {
        strncat(dialog_xml_path, XML_PATH_DEFAULT, MAX_BUF-1);
        strncat(dialog_xml_path, dialog_xml_file,
            MAX_BUF-strlen(dialog_xml_path)-1);
    }
    dialog_xml = glade_xml_new(dialog_xml_path, NULL, NULL);
    if (! dialog_xml) {
        fprintf (stderr, "Failed to load xml file: %s\n", dialog_xml_path);
        exit(-1);
    }

    /*
     * Next, load up the root window.  If the XML file path is already set, it
     * is because a command-line parameter was used to specify it.  If not set,
     * construct the path to the file from the default settings, and any values
     * loaded from the gdefaults2 file.
     */
    if (! window_xml_path[0]) {
        strncat(window_xml_path, XML_PATH_DEFAULT, MAX_BUF-1);
        strncat(window_xml_path, window_xml_file,
            MAX_BUF-strlen(window_xml_path)-1);
    }
    window_xml = glade_xml_new(window_xml_path, NULL, NULL);
    if (! window_xml) {
        fprintf (stderr, "Failed to load xml file: %s\n", window_xml_path);
        exit(-1);
    }

    /* Begin connecting signals for the root window loaded by libglade. */
    window_root = glade_xml_get_widget(window_xml, "window_root");

    g_signal_connect_swapped ((gpointer) window_root, "key_press_event",
        G_CALLBACK (keyfunc), GTK_OBJECT (window_root));
    g_signal_connect_swapped ((gpointer) window_root, "key_release_event",
        G_CALLBACK (keyrelfunc), GTK_OBJECT (window_root));
    g_signal_connect ((gpointer) window_root, "destroy",
        G_CALLBACK (on_window_destroy_event), NULL);

    /* Purely arbitrary min window size */
    geometry.min_width=800;
    geometry.min_height=600;

    gtk_window_set_geometry_hints(GTK_WINDOW(window_root), window_root,
				  &geometry, GDK_HINT_MIN_SIZE);


    /* Set up colors before doing the other initialization functions */
    for (i=0; i<NUM_COLORS; i++) {
	if ( !gdk_color_parse(colorname[i], &root_color[i])) {
	    fprintf(stderr, "gdk_color_parse failed (%s)\n",colorname[i]);
	}
        if ( !gdk_color_alloc (gtk_widget_get_colormap (window_root),
			       &root_color[i])) {
            fprintf(stderr, "gdk_color_alloc failed\n");
	}
    }


    inventory_init(window_root);
    info_init(window_root);
    keys_init(window_root);
    stats_init(window_root);
    config_init(window_root);
    pickup_init(window_root);

    load_window_positions(window_root);

    /* We want this as late as possible in the process. This way,
     * adjustments that the widgets make on initialization are not
     * visible - this is most important with the inventory widget
     * which has to create the panes and fill in the data - if
     * the window_root is shown before that, there is a brief glimpse
     * of the glade layout, which, IMO, doesn't look great.  Also,
     * it should be faster to realize this as later as possible.
     */
    gtk_widget_show (window_root);

    map_init(window_root);

    xml_tree = glade_get_widget_tree(GTK_WIDGET(window_root));
    magic_map = glade_xml_get_widget(xml_tree, "drawingarea_magic_map");

    snprintf( file_cache, MAX_BUF, "%s/.crossfire/servers.cache", getenv( "HOME" ) );
    cached_server_file = file_cache;

    init_cache_data();

    /* Loop to connect to server/metaserver and play the game */
    while (1) {
	reset_client_vars();
	clear_stat_mapping();
	csocket.inbuf.len=0;
	csocket.cs_version=0;

	/* Perhaps not the best assumption, but we are taking it that
	 * if the player has not specified a server (ie, server
	 * matches compiled in default), we use the meta server.
	 * otherwise, use the server provided, bypassing metaserver.
	 * Also, if the player has already played on a server once (defined
	 * by got_one), go to the metaserver.  That gives them the oppurtunity
	 * to quit the client or select another server.  We should really add
	 * an entry for the last server there also.
	 */

	if (!server || got_one) {
	    char *ms;

	    draw_splash();
	    metaserver_get_info(meta_server, meta_port);
	    ms=get_metaserver();
	    negotiate_connection(use_config[CONFIG_SOUND]);
	} else {
	    csocket.fd=init_connection(server, use_config[CONFIG_PORT]);
	    if (csocket.fd == -1) { /* specified server no longer valid */
		server = NULL;
		continue;
	    }
	    negotiate_connection(use_config[CONFIG_SOUND]);
	}

	got_one=1;


	event_loop();
	/* if event_loop has exited, we most likely of lost our connection, so we
	 * loop again to establish a new one.
	 */

	remove_item_inventory(cpl.ob);
	/* We know the following is the private map structure in
	 * item.c.  But we don't have direct access to it, so
	 * we still use locate.
	 */
	remove_item_inventory(locate_item(0));
	draw_look_list();

	mapdata_reset();
	/* Need to reset the images so they match up properly and prevent
	 * memory leaks.
	 */
	reset_image_data();
    }
    exit(0);	/* never reached */

    return 0;
}


/* Gets a specified windows coordinates.
 */

void get_window_coord(GtkWidget *win,
                 int *x,int *y,
                 int *wx,int *wy,
                 int *w,int *h)
{
    int tmp;

    gdk_window_get_geometry (win->window, x, y, w, h, &tmp);
    gdk_window_get_origin (win->window, wx, wy);
    *wx -= *x;
    *wy -= *y;
}
