/* Including external.h makes sures that the type definitions agree */

#include <external.h>
/* config.c */
extern void applyconfig(void);
extern void saveconfig(void);
extern void configdialog(GtkWidget *widget);
extern void load_defaults(void);
extern void save_defaults(void);
/* gx11.c */
extern void do_network(void);
extern void event_loop(void);
extern void end_windows(void);
extern void animate_list(void);
extern void button_map_event(GtkWidget *widget, GdkEventButton *event);
extern void gtk_command_history(int direction);
extern void gtk_complete_command(void);
extern void draw_prompt(const char *str);
extern void draw_info(const char *str, int color);
extern void draw_color_info(int colr, const char *buf);
extern void draw_stats(int redraw);
extern void create_stat_bar(GtkWidget *mtable, gint row, gchar *label, gint bar, GtkWidget **plabel);
extern void reset_stat_bars(void);
extern void resize_resistance_table(int resists_show);
extern void draw_message_window(int redraw);
extern void draw_all_list(itemlist *l);
extern void open_container(item *op);
extern void close_container(item *op);
extern void count_callback(GtkWidget *widget, GtkWidget *entry);
extern void create_notebook_page(GtkWidget *notebook, GtkWidget **list, GtkWidget **lists, gchar **label);
extern void draw_lists(void);
extern void aboutdialog(GtkWidget *widget);
extern void cclist_button_event(GtkWidget *gtklist, gint row, gint column, GdkEventButton *event);
extern void disconnect(GtkWidget *widget);
extern void chelpdialog(GtkWidget *widget);
extern void shelpdialog(GtkWidget *widget);
extern void new_menu_pickup(GtkWidget *button, int val);
extern void menu_pickup0(void);
extern void menu_pickup1(void);
extern void menu_pickup2(void);
extern void menu_pickup3(void);
extern void menu_pickup4(void);
extern void menu_pickup5(void);
extern void menu_pickup6(void);
extern void menu_pickup7(void);
extern void menu_pickup10(void);
extern void menu_who(void);
extern void menu_apply(void);
extern void menu_cast(void);
extern void menu_search(void);
extern void menu_disarm(void);
extern void menu_spells(void);
extern void menu_clear(void);
extern void sexit(void);
extern void create_splash(void);
extern void destroy_splash(void);
extern void create_windows(void);
extern void set_weight_limit(uint32 wlim);
extern int get_info_width(void);
extern void do_clearlock(void);
extern void x_set_echo(void);
extern int do_timeout(void);
#ifdef WIN32
extern int do_scriptout(void);
#endif
extern void draw_magic_map(void);
extern void magic_map_flash_pos(void);
extern void get_window_coord(GtkWidget *win, int *x, int *y, int *wx, int *wy, int *w, int *h);
extern void save_winpos(void);
extern void command_show(char *params);
extern void set_window_pos(void);
extern int init_windows(int argc, char **argv);
extern void display_map_doneupdate(int redraw);
extern void display_map_newmap(void);
extern void resize_map_window(int x, int y);
extern void display_map_startupdate(void);
extern char *get_metaserver(void);
extern int main(int argc, char *argv[]);
/* image.c */
extern int create_and_rescale_image_from_data(Cache_Entry *ce, int pixmap_num, uint8 *rgba_data, int width, int height);
extern int associate_cache_entry(Cache_Entry *ce, int pixnum);
extern void reset_image_data(void);
extern void image_update_download_status(int start, int end, int total);
extern void get_map_image_size(int face, uint8 *w, uint8 *h);
/* keys.c */
extern void init_keys(void);
extern void keyrelfunc(GtkWidget *widget, GdkEventKey *event, GtkWidget *window);
extern void keyfunc(GtkWidget *widget, GdkEventKey *event, GtkWidget *window);
extern void draw_keybindings(GtkWidget *keylist);
extern void bind_callback(GtkWidget *gtklist, GdkEventButton *event);
extern void ckeyunbind(GtkWidget *gtklist, GdkEventButton *event);
extern void ckeyentry_callback(GtkWidget *widget, GdkEventKey *event, GtkWidget *window);
extern void ckeyclear(void);
/* map.c */
extern void allocate_map(struct Map *new_map, int ax, int ay);
extern void reset_map(void);
extern void print_darkness(void);
extern void print_map(void);
extern void set_map_darkness(int x, int y, uint8 darkness);
extern void display_mapscroll(int dx, int dy);
extern void reset_map_data(void);
extern GdkBitmap *createpartialmask(GdkBitmap *mask, int x, int y, int width, int height, int maskwidth, int maskheight);
extern void drawsmooth(int mx, int my, int layer, int picx, int picy);
extern void gtk_draw_map(int redraw);
/* png.c */
extern uint8 *png_to_data(uint8 *data, int len, uint32 *width, uint32 *height);
extern uint8 *rescale_rgba_data(uint8 *data, int *width, int *height, int scale);
extern int rgba_to_gdkpixmap(GdkWindow *window, uint8 *data, int width, int height, GdkPixmap **pix, GdkBitmap **mask, GdkColormap *colormap);
extern int png_to_gdkpixmap(GdkWindow *window, uint8 *data, int len, GdkPixmap **pix, GdkBitmap **mask, GdkColormap *colormap);
/* sdl.c */
void init_SDL( GtkWidget* sdl_window, int just_lightmap);
void sdl_gen_map(int redraw);
void sdl_mapscroll(int dx, int dy);
/* sound.c */
extern void signal_pipe(int i);
extern int init_sounds(void);
extern void SoundCmd(unsigned char *data, int len);
