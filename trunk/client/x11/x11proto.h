#include <external.h>

/* png.c */
extern uint8 *rescale_rgba_data(uint8 *data, int *width, int *height, int scale);
extern long pngx_find_color(Display *display, Colormap *cmap, int red, int green, int blue);
extern int init_pngx_loader(Display *display);
extern int png_to_xpixmap(Display *display, Drawable draw, unsigned char *data, int len, Pixmap *pix, Pixmap *mask, Colormap *cmap, unsigned long *width, unsigned long *height);
extern int rgba_to_xpixmap(Display *display, Drawable draw, uint8 *pixels, Pixmap *pix, Pixmap *mask, Colormap *cmap, unsigned long width, unsigned long height);
extern int create_and_rescale_image_from_data(Cache_Entry *ce, int pixmap_num, uint8 *rgba_data, int width, int height);
extern void get_map_image_size(int face, uint8 *w, uint8 *h);
/* sound.c */
extern void signal_pipe(int i);
extern int init_sounds(void);
extern void SoundCmd(unsigned char *data, int len);
/* x11.c */
extern void event_loop(void);
extern void end_windows(void);
extern void write_ch(char key);
extern void draw_prompt(const char *str);
extern void draw_info(const char *str, int color);
extern void draw_color_info(int colr, const char *buf);
extern void draw_stats(int redraw);
extern void draw_message_window(int redraw);
extern void open_container(item *op);
extern void close_container(item *op);
extern void draw_lists(void);
extern void set_weight_limit(uint32 wlim);
extern int get_info_width(void);
extern void menu_clear(void);
extern char *get_metaserver(void);
extern void check_x_events(void);
extern int init_windows(int argc, char **argv);
extern void display_map_newmap(void);
extern void display_mapcell_pixmap(int ax, int ay);
extern void resize_map_window(int x, int y);
extern void x_set_echo(void);
extern void display_map_doneupdate(int redraw);
extern int associate_cache_entry(Cache_Entry *ce, int pixnum);
extern void redisplay_stats(void);
extern void display_map_startupdate(void);
extern void draw_magic_map(void);
extern void magic_map_flash_pos(void);
extern void reset_image_data(void);
extern void save_winpos(void);
extern void set_window_pos(void);
extern void load_defaults(void);
extern void save_defaults(void);
extern void command_show(char *params);
extern int main(int argc, char *argv[]);
/* xutil.c */
extern void init_cache_data(void);
extern int allocate_colors(Display *disp, Window w, long screen_num, Colormap *colormap, XColor discolor[16]);
extern void parse_keybind_line(char *buf, int line, int standard);
extern void init_keys(void);
extern void parse_key_release(KeyCode kc, KeySym ks);
extern void parse_key(char key, KeyCode keycode, KeySym keysym, int repeated);
extern void configure_keys(KeyCode k, KeySym keysym);
extern int find_face_in_private_cache(char *face, int checksum);
extern void image_update_download_status(int start, int end, int total);
extern void allocate_map(struct Map *new_map, int ax, int ay);
extern void reset_map(void);
extern void print_darkness(void);
extern void print_map(void);
extern void set_map_darkness(int x, int y, uint8 darkness);
extern void display_mapscroll(int dx, int dy);
extern void reset_map_data(void);
