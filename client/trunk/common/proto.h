/* commands.c */
void ReplyInfoCmd(guint8 *buf, int len);
void SetupCmd(char *buf, int len);
void ExtendedInfoSetCmd(char *data, int len);
void AddMeFail(char *data, int len);
void AddMeSuccess(char *data, int len);
void GoodbyeCmd(char *data, int len);
void AnimCmd(unsigned char *data, int len);
void SmoothCmd(unsigned char *data, int len);
void DrawInfoCmd(char *data, int len);
void setTextManager(int type, ExtTextManager callback);
void DrawExtInfoCmd(char *data, int len);
void use_skill(int skill_id);
void StatsCmd(unsigned char *data, int len);
void handle_query(char *data, int len);
void send_reply(const char *text);
void PlayerCmd(unsigned char *data, int len);
void item_actions(item *op);
void Item2Cmd(unsigned char *data, int len);
void UpdateItemCmd(unsigned char *data, int len);
void DeleteItem(unsigned char *data, int len);
void DeleteInventory(unsigned char *data, int len);
void AddspellCmd(unsigned char *data, int len);
void UpdspellCmd(unsigned char *data, int len);
void DeleteSpell(unsigned char *data, int len);
void NewmapCmd(unsigned char *data, int len);
void Map2Cmd(unsigned char *data, int len);
void map_scrollCmd(char *data, int len);
int ExtSmooth(unsigned char *data, int len, int x, int y, int layer);
void MapExtendedCmd(unsigned char *data, int len);
void MagicMapCmd(unsigned char *data, int len);
void SinkCmd(unsigned char *data, int len);
void TickCmd(guint8 *data, int len);
void PickupCmd(guint8 *data, int len);
void FailureCmd(char *buf, int len);
void AccountPlayersCmd(char *buf, int len);
void free_all_race_class_info(Race_Class_Info *data, int num_entries);
/* image.c */
void init_common_cache_data(void);
void requestface(int pnum, char *facename);
void finish_face_cmd(int pnum, guint32 checksum, int has_sum, char *face, int faceset);
void reset_image_cache_data(void);
void Face2Cmd(guint8 *data, int len);
void Image2Cmd(guint8 *data, int len);
void display_newpng(int face, guint8 *buf, int buflen, int setnum);
void get_image_info(guint8 *data, int len);
void get_image_sums(char *data, int len);
/* init.c */
void VersionCmd(char *data, int len);
void SendVersion(ClientSocket csock);
void SendAddMe(ClientSocket csock);
void client_init(void);
void reset_player_data(void);
void client_reset(void);
/* item.c */
guint8 get_type_from_name(const char *name);
void update_item_sort(item *it);
const char *get_number(guint32 i);
void free_all_items(item *op);
item *locate_item(gint32 tag);
void remove_item(item *op);
void remove_item_inventory(item *op);
item *create_new_item(item *env, gint32 tag);
int num_free_items(void);
void set_item_values(item *op, char *name, gint32 weight, guint16 face, guint16 flags, guint16 anim, guint16 animspeed, guint32 nrof, guint16 type);
void toggle_locked(item *op);
void send_mark_obj(item *op);
item *player_item(void);
item *map_item(void);
void update_item(int tag, int loc, char *name, int weight, int face, int flags, int anim, int animspeed, guint32 nrof, int type);
void print_inventory(item *op);
void animate_objects(void);
int can_write_spell_on(item *it);
void inscribe_magical_scroll(item *scroll, Spell *spell);
/* misc.c */
int make_path_to_file(char *filename);
void LOG(LogLevel level, const char *origin, const char *format, ...);
/* newsocket.c */
void SockList_Init(SockList *sl, guint8 *buf);
void SockList_AddChar(SockList *sl, char c);
void SockList_AddShort(SockList *sl, guint16 data);
void SockList_AddInt(SockList *sl, guint32 data);
void SockList_AddString(SockList *sl, const char *str);
int SockList_Send(SockList *sl, GSocketConnection* c);
char GetChar_String(const unsigned char *data);
int GetInt_String(const unsigned char *data);
gint64 GetInt64_String(const unsigned char *data);
short GetShort_String(const unsigned char *data);
bool SockList_ReadPacket(GSocketConnection c[static 1], SockList sl[static 1],
                         size_t len, GError** error);
int cs_print_string(GSocketConnection* c, const char *str, ...);
/* p_cmd.c */
/* player.c */
void new_player(long tag, char *name, long weight, long face);
void look_at(int x, int y);
void client_send_apply(int tag);
void client_send_examine(int tag);
void client_send_move(int loc, int tag, int nrof);
void stop_fire(void);
void clear_fire(void);
void clear_run(void);
void fire_dir(int dir);
void stop_run(void);
void run_dir(int dir);
int send_command(const char *command, int repeat, int must_send);
void CompleteCmd(unsigned char *data, int len);
void command_take(const char *command, const char *cpnext);
/* script.c */
/* script_lua.c */
void script_lua_load(const char *name);
void script_lua_list(const char *param);
void script_lua_kill(const char *param);
void script_lua_stats(void);
int script_lua_command(const char *command, const char *param);
