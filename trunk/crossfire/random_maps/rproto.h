/*
 *   This file was automatically generated by version 1.7 of cextract.
 *   Manual editing not recommended.
 *
 *   Created: Mon Mar 15 11:05:54 1999
 */
#ifndef __CEXTRACT__
#ifdef __STDC__

extern mapstruct *generate_random_map ( char *InFileName, char *OutFileName );
extern char **layoutgen ( void );
extern char **symmetrize_layout ( char **maze, int sym );
extern char ** rotate_layout ( char **maze, int rotation );
extern void roomify_layout ( char **maze );
extern int can_make_wall ( char **maze, int dx, int dy, int dir );
extern int make_wall ( char **maze, int x, int y, int dir );
extern void doorify_layout ( char **maze );
extern void write_map_parameters_to_string ( char *buf );
extern char **map_gen_onion ( int xsize, int ysize, int option, int layers );
extern void centered_onion ( char **maze, int xsize, int ysize, int option, int layers );
extern void bottom_centered_onion ( char **maze, int xsize, int ysize, int option, int layers );
extern void draw_onion ( char **maze, float *xlocations, float *ylocations, int layers );
extern void make_doors ( char **maze, float *xlocations, float *ylocations, int layers, int options );
extern void bottom_right_centered_onion ( char **maze, int xsize, int ysize, int option, int layers );
extern char **maze_gen ( int xsize, int ysize, int option );
extern void make_wall_free_list ( int xsize, int ysize );
extern void pop_wall_point ( int *x, int *y );
extern int find_free_point ( char **maze, int *x, int *y, int xc, int yc, int xsize, int ysize );
extern void fill_maze_full ( char **maze, int x, int y, int xsize, int ysize );
extern void fill_maze_sparse ( char **maze, int x, int y, int xsize, int ysize );
extern int rmap_lex_read ( void );
extern void rmaprestart ( FILE *input_file );
extern void rmap_load_buffer_state ( void );
extern int load_parameters ( FILE *fp, int bufstate );
extern int select_regular_files ( struct dirent *the_entry );
extern mapstruct *find_style ( char *dirname, char *stylename, int difficulty );
extern object *pick_random_object ( mapstruct *style );
extern mapstruct *make_map_floor ( char **layout, char *floorstyle );
extern int surround_flag ( char **layout, int i, int j );
extern int surround_flag2 ( char **layout, int i, int j );
extern int surround_flag3 ( mapstruct *map, int i, int j );
extern int surround_flag4 ( mapstruct *map, int i, int j );
extern void make_map_walls ( mapstruct *map, char **layout, char *w_style );
extern object *pick_joined_wall ( object *the_wall, char **layout, int i, int j );
extern object * retrofit_joined_wall ( mapstruct *the_map, int i, int j, int insert_flag );
extern void insert_multisquare_ob_in_map ( object *new_obj, mapstruct *map );
extern void place_monsters ( mapstruct *map, char *monsterstyle, int difficulty );
extern void put_doors ( mapstruct *the_map, char **maze, char *doorstyle );
extern int obj_count_in_map ( mapstruct *map, int x, int y );
extern void put_decor ( mapstruct *map, char **maze, char *decorstyle, int decor_option );
extern void place_exits ( mapstruct *map, char **maze, char *exitstyle, int orientation );
extern int wall_blocked ( mapstruct *m, int x, int y );
extern void place_treasure ( mapstruct *map, char **layout, char *treasure_style, int treasureoptions );
extern object * place_chest ( int treasureoptions, int x, int y, mapstruct *map, mapstruct *style_map, int n_treasures );
extern object *find_closest_monster ( mapstruct *map, int x, int y );
extern void keyplace ( mapstruct *map, int x, int y, char *keycode, int door_flag, int n_keys );
extern object *find_monster_in_room_recursive ( char **layout, mapstruct *map, int x, int y );
extern object *find_monster_in_room ( mapstruct *map, int x, int y );
extern void find_spot_in_room_recursive ( char **layout, int x, int y );
extern void find_spot_in_room ( mapstruct *map, int x, int y, int *kx, int *ky );
extern void find_enclosed_spot ( mapstruct *map, int *cx, int *cy );
extern void remove_monsters ( int x, int y, mapstruct *map );
extern object ** surround_by_doors ( mapstruct *map, int x, int y, int opts );
extern object *door_in_square ( mapstruct *map, int x, int y );
extern void find_doors_in_room_recursive ( char **layout, mapstruct *map, int x, int y, object **doorlist, int *ndoors );
extern object** find_doors_in_room ( mapstruct *map, int x, int y );
extern void lock_and_hide_doors ( object **doorlist, mapstruct *map, int opts );
extern void nuke_map_region ( mapstruct *map, int xstart, int ystart, int xsize, int ysize );
extern void include_map_in_map ( mapstruct *dest_map, mapstruct *in_map, int x, int y );
extern int find_spot_for_submap ( mapstruct *map, char **layout, int *ix, int *iy, int xsize, int ysize );
extern void place_fountain_with_specials ( mapstruct *map );
extern void place_specials_in_map ( mapstruct *map, char **layout );
extern void write_parameters_to_string(char *buf,
										  int xsize_n,
										  int ysize_n,
										  char *wallstyle_n,
										  char *floorstyle_n,
										  char *monsterstyle_n,

										  char *treasurestyle_n,
										  char *layoutstyle_n,
										  char *decorstyle_n,
										  char *doorstyle_n,
										  char *exitstyle_n,
										  char *final_map_n,
										  char *this_map_n,

										  int layoutoptions1_n,
										  int layoutoptions2_n,
										  int layoutopitons3_n,
										  int symmetry_n,
										  int dungeon_depth_n,
										  int dungeon_level_n,

										  int difficulty_n,
										  int difficulty_given_n,
										  int decoroptions_n,
										  int orientation_n,
										  int origin_x_n,
										  int origin_y_n,
										  int random_seed_n,
										  int treasureoptions_n
										  ) ;
void copy_object_with_inv(object *src, object *dest);

#endif /* __STDC__ */
#endif /* __CEXTRACT__ */
