/* alchemy.c */
char *cauldron_sound(void);
void attempt_do_alchemy(object *caster, object *cauldron);
int content_recipe_value(object *op);
int numb_ob_inside(object *op);
object *attempt_recipe(object *caster, object *cauldron, int ability, recipe *rp, int nbatches);
void adjust_product(object *item, int lvl, int yield);
object *make_item_from_recipe(object *cauldron, recipe *rp);
object *find_transmution_ob(object *first_ingred, recipe *rp);
void alchemy_failure_effect(object *op, object *cauldron, recipe *rp, int danger);
void remove_contents(object *first_ob, object *save_item);
int calc_alch_danger(object *caster, object *cauldron);
/* apply.c */
int apply_potion(object *op, object *tmp);
int check_item(object *op, char *item);
void eat_item(object *op, char *item);
int check_weapon_power(object *who, int improvs);
int improve_weapon_stat(object *op, object *improver, object *weapon, signed char *stat, int sacrifice_count, char *statname);
int prepare_weapon(object *op, object *improver, object *weapon);
int improve_weapon(object *op, object *improver, object *weapon);
int check_improve_weapon(object *op, object *tmp);
int improve_armour(object *op, object *improver, object *armour);
int convert_item(object *item, object *converter);
int apply_container(object *op, object *sack);
int esrv_apply_container(object *op, object *sack);
void move_apply(object *trap, object *victim, object *originator);
void do_learn_spell(object *op, int spell, int special_prayer);
void do_forget_spell(object *op, int spell);
void apply_scroll(object *op, object *tmp, int dir);
int dragon_eat_flesh(object *op, object *meal);
void apply_poison(object *op, object *tmp);
int is_legal_2ways_exit(object *op, object *exit);
int manual_apply(object *op, object *tmp, int aflag);
int player_apply(object *pl, object *op, int aflag, int quiet);
void player_apply_below(object *pl);
object *get_item_from_body_location(object *start, int loc);
int unapply_for_ob(object *who, object *op, int aflags);
int can_apply_object(object *who, object *op);
int apply_special(object *who, object *op, int aflags);
int monster_apply_special(object *who, object *op, int aflags);
int auto_apply(object *op);
void fix_auto_apply(mapstruct *m);
void eat_special_food(object *who, object *food);
void apply_lighter(object *who, object *lighter);
void scroll_failure(object *op, int failure, int power);
void apply_changes_to_player(object *pl, object *change);
/* attack.c */
int did_make_save_item(object *op, int type, object *originator);
void save_throw_object(object *op, int type, object *originator);
int hit_map(object *op, int dir, int type);
void attack_message(int dam, int type, object *op, object *hitter);
int attack_ob(object *op, object *hitter);
object *hit_with_arrow(object *op, object *victim);
void tear_down_wall(object *op);
int hit_player_attacktype(object *op, object *hitter, int dam, uint32 attacknum, int magic);
int kill_object(object *op, int dam, object *hitter, int type);
int hit_player(object *op, int dam, object *hitter, int type);
void poison_player(object *op, object *hitter, int dam);
void slow_player(object *op, object *hitter, int dam);
void confuse_player(object *op, object *hitter, int dam);
void blind_player(object *op, object *hitter, int dam);
void paralyze_player(object *op, object *hitter, int dam);
void deathstrike_player(object *op, object *hitter, int *dam);
int adj_attackroll(object *hitter, object *target);
int is_aimed_missile(object *op);
/* ban.c */
int checkbanned(char *login, char *host);
/* c_chat.c */
int command_say(object *op, char *params);
int command_shout(object *op, char *params);
int command_tell(object *op, char *params);
int command_reply(object *op, char *params);
int command_nod(object *op, char *params);
int command_dance(object *op, char *params);
int command_kiss(object *op, char *params);
int command_bounce(object *op, char *params);
int command_smile(object *op, char *params);
int command_cackle(object *op, char *params);
int command_laugh(object *op, char *params);
int command_giggle(object *op, char *params);
int command_shake(object *op, char *params);
int command_puke(object *op, char *params);
int command_growl(object *op, char *params);
int command_scream(object *op, char *params);
int command_sigh(object *op, char *params);
int command_sulk(object *op, char *params);
int command_hug(object *op, char *params);
int command_cry(object *op, char *params);
int command_poke(object *op, char *params);
int command_accuse(object *op, char *params);
int command_grin(object *op, char *params);
int command_bow(object *op, char *params);
int command_clap(object *op, char *params);
int command_blush(object *op, char *params);
int command_burp(object *op, char *params);
int command_chuckle(object *op, char *params);
int command_cough(object *op, char *params);
int command_flip(object *op, char *params);
int command_frown(object *op, char *params);
int command_gasp(object *op, char *params);
int command_glare(object *op, char *params);
int command_groan(object *op, char *params);
int command_hiccup(object *op, char *params);
int command_lick(object *op, char *params);
int command_pout(object *op, char *params);
int command_shiver(object *op, char *params);
int command_shrug(object *op, char *params);
int command_slap(object *op, char *params);
int command_smirk(object *op, char *params);
int command_snap(object *op, char *params);
int command_sneeze(object *op, char *params);
int command_snicker(object *op, char *params);
int command_sniff(object *op, char *params);
int command_snore(object *op, char *params);
int command_spit(object *op, char *params);
int command_strut(object *op, char *params);
int command_thank(object *op, char *params);
int command_twiddle(object *op, char *params);
int command_wave(object *op, char *params);
int command_whistle(object *op, char *params);
int command_wink(object *op, char *params);
int command_yawn(object *op, char *params);
int command_beg(object *op, char *params);
int command_bleed(object *op, char *params);
int command_cringe(object *op, char *params);
int command_think(object *op, char *params);
/* c_misc.c */
void map_info(object *op);
int command_body(object *op, char *params);
int command_spell_reset(object *op, char *params);
int command_motd(object *op, char *params);
int command_bug(object *op, char *params);
void malloc_info(object *op);
void current_map_info(object *op);
int command_who(object *op, char *params);
int command_malloc(object *op, char *params);
int command_mapinfo(object *op, char *params);
int command_maps(object *op, char *params);
int command_strings(object *op, char *params);
int command_sstable(object *op, char *params);
int command_time(object *op, char *params);
int command_archs(object *op, char *params);
int command_hiscore(object *op, char *params);
int command_debug(object *op, char *params);
int command_dumpbelow(object *op, char *params);
int command_wizpass(object *op, char *params);
int command_dumpallobjects(object *op, char *params);
int command_dumpfriendlyobjects(object *op, char *params);
int command_dumpallarchetypes(object *op, char *params);
int command_ssdumptable(object *op, char *params);
int command_dumpmap(object *op, char *params);
int command_dumpallmaps(object *op, char *params);
int command_printlos(object *op, char *params);
int command_version(object *op, char *params);
void bug_report(char *reportstring);
int command_output_sync(object *op, char *params);
int command_output_count(object *op, char *params);
int command_listen(object *op, char *params);
int command_statistics(object *pl, char *params);
int command_fix_me(object *op, char *params);
int command_players(object *op, char *paramss);
int command_logs(object *op, char *params);
int command_applymode(object *op, char *params);
int command_usekeys(object *op, char *params);
int command_resistances(object *op, char *params);
int command_help(object *op, char *params);
int onoff_value(char *line);
int command_quit(object *op, char *params);
int command_sound(object *op, char *params);
void receive_player_name(object *op, char k);
void receive_player_password(object *op, char k);
int explore_mode(void);
int command_title(object *op, char *params);
int command_save(object *op, char *params);
int command_peaceful(object *op, char *params);
int command_wimpy(object *op, char *params);
int command_brace(object *op, char *params);
int command_style_map_info(object *op, char *params);
int command_kill_pets(object *op, char *params);
/* c_move.c */
int command_east(object *op, char *params);
int command_north(object *op, char *params);
int command_northeast(object *op, char *params);
int command_northwest(object *op, char *params);
int command_south(object *op, char *params);
int command_southeast(object *op, char *params);
int command_southwest(object *op, char *params);
int command_west(object *op, char *params);
int command_stay(object *op, char *params);
int command_search(object *op, char *params);
int command_disarm(object *op, char *params);
/* c_new.c */
int execute_newserver_command(object *pl, char *command);
int command_run(object *op, char *params);
int command_run_stop(object *op, char *params);
int command_fire(object *op, char *params);
int command_fire_stop(object *op, char *params);
int bad_command(object *op, char *params);
/* c_object.c */
object *find_best_object_match(object *pl, char *params);
int command_uskill(object *pl, char *params);
int command_rskill(object *pl, char *params);
int command_apply(object *op, char *params);
int sack_can_hold(object *pl, object *sack, object *op, int nrof);
void pick_up(object *op, object *alt);
int command_take(object *op, char *params);
void put_object_in_sack(object *op, object *sack, object *tmp, long nrof);
void drop_object(object *op, object *tmp, long nrof);
void drop(object *op, object *tmp);
int command_dropall(object *op, char *params);
int command_drop(object *op, char *params);
int command_examine(object *op, char *params);
object *find_marked_object(object *op);
int command_mark(object *op, char *params);
void examine_monster(object *op, object *tmp);
char *long_desc(object *tmp, object *pl);
void examine(object *op, object *tmp);
void inventory(object *op, object *inv);
int command_pickup(object *op, char *params);
void set_pickup_mode(object *op, int i);
int command_search_items(object *op, char *params);
/* c_party.c */
partylist *form_party(object *op, char *params, partylist *firstparty, partylist *lastparty);
char *find_party(int partynumber, partylist *party);
partylist *find_party_struct(int partynumber);
int confirm_party_password(object *op);
void receive_party_password(object *op, char k);
void send_party_message(object *op, char *msg);
int command_gsay(object *op, char *params);
int command_party(object *op, char *params);
/* c_range.c */
int command_invoke(object *op, char *params);
int command_cast(object *op, char *params);
int command_prepare(object *op, char *params);
int command_cast_spell(object *op, char *params, char command);
int legal_range(object *op, int r);
void change_spell(object *op, char k);
int command_rotateshoottype(object *op, char *params);
int command_throw(object *op, char *params);
int command_rotatespells(object *op, char *params);
/* c_wiz.c */
int command_hide(object *op, char *params);
int command_setgod(object *op, char *params);
int command_kick(object *op, char *params);
int command_save_overlay(object *op, char *params);
int command_shutdown(object *op, char *params);
int command_goto(object *op, char *params);
int command_generate(object *op, char *params);
int command_freeze(object *op, char *params);
int command_summon(object *op, char *params);
int command_teleport(object *op, char *params);
int command_create(object *op, char *params);
int command_inventory(object *op, char *params);
int command_skills(object *op, char *params);
int command_dump(object *op, char *params);
int command_patch(object *op, char *params);
int command_remove(object *op, char *params);
int command_free(object *op, char *params);
int command_addexp(object *op, char *params);
int command_speed(object *op, char *params);
int command_stats(object *op, char *params);
int command_abil(object *op, char *params);
int command_reset(object *op, char *params);
int command_nowiz(object *op, char *params);
int command_dm(object *op, char *params);
int command_invisible(object *op, char *params);
int command_learn_spell(object *op, char *params);
int command_learn_special_prayer(object *op, char *params);
int command_forget_spell(object *op, char *params);
int command_listplugins(object *op, char *params);
int command_loadplugin(object *op, char *params);
int command_unloadplugin(object *op, char *params);
int command_unloadplugin(object *op, char *params);
/* commands.c */
void init_commands(void);
CommFunc find_oldsocket_command(char *cmd);
CommFunc find_oldsocket_command2(char *cmd);
int parse_string(object *op, char *str);
int parse_command(object *op, char *str);
/* daemon.c */
FILE *BecomeDaemon(char *filename);
/* disease.c */
int move_disease(object *disease);
int remove_symptoms(object *disease);
object *find_symptom(object *disease);
int check_infection(object *disease);
int infect_object(object *victim, object *disease, int force);
int do_symptoms(object *disease);
int grant_immunity(object *disease);
int move_symptom(object *symptom);
int check_physically_infect(object *victim, object *hitter);
object *find_disease(object *victim);
int cure_disease(object *sufferer, object *caster);
int reduce_symptoms(object *sufferer, int reduction);
/* egoitem.c */
object *create_artifact(object *op, char *artifactname);
int apply_power_crystal(object *op, object *crystal);
/* hiscore.c */
char *spool(char *bp, char *error);
void check_score(object *op);
void display_high_score(object *op, int max, char *match);
/* gods.c */
int lookup_god_by_name(char *name);
object *find_god(char *name);
void pray_at_altar(object *pl, object *altar);
void become_follower(object *op, object *new_god);
int worship_forbids_use(object *op, object *exp_obj, uint32 flag, char *string);
void stop_using_item(object *op, int type, int number);
void update_priest_flag(object *god, object *exp_ob, uint32 flag);
char *determine_god(object *op);
archetype *determine_holy_arch(object *god, const char *type);
void god_intervention(object *op, object *god);
int god_examines_priest(object *op, object *god);
int god_examines_item(object *god, object *item);
int get_god(object *priest);
int tailor_god_spell(object *spellop, object *caster);
void lose_priest_exp(object *pl, int loss);
/* init.c */
void set_logfile(char *val);
void call_version(void);
void showscores(void);
void set_debug(void);
void unset_debug(void);
void set_mondebug(void);
void set_dumpmon1(void);
void set_dumpmon2(void);
void set_dumpmon3(void);
void set_dumpmon4(void);
void set_dumpmon5(void);
void set_dumpmon6(void);
void set_dumpmon7(void);
void set_dumpmon8(void);
void set_dumpmon9(void);
void set_dumpmont(char *name);
void set_daemon(void);
void set_datadir(char *path);
void set_confdir(char *path);
void set_localdir(char *path);
void set_mapdir(char *path);
void set_archetypes(char *path);
void set_treasures(char *path);
void set_uniquedir(char *path);
void set_playerdir(char *path);
void set_tmpdir(char *path);
void showscoresparm(char *data);
void set_csport(char *val);
void init(int argc, char **argv);
void usage(void);
void help(void);
void init_beforeplay(void);
void init_startup(void);
void compile_info(void);
void rec_sigsegv(int i);
void rec_sigint(int i);
void rec_sighup(int i);
void rec_sigquit(int i);
void rec_sigpipe(int i);
void rec_sigbus(int i);
void rec_sigterm(int i);
void fatal_signal(int make_core, int close_sockets);
void init_signals(void);
void setup_library(void);
void init_races(void);
void dump_races(void);
void add_to_racelist(char *race_name, object *op);
racelink *get_racelist(void);
racelink *find_racelink(char *name);
/* login.c */
void emergency_save(int flag);
void delete_character(char *name, int new);
int verify_player(char *name, char *password);
int check_name(player *me, char *name);
int create_savedir_if_needed(char *savedir);
void destroy_object(object *op);
int save_player(object *op, int flag);
long calculate_checksum(char *filename, int checkdouble);
void copy_file(char *filename, FILE *fpout);
void check_login(object *op);
/* main.c */
void version(object *op);
void info_keys(object *op);
void start_info(object *op);
char *crypt_string(char *str, char *salt);
int check_password(char *typed, char *crypted);
void enter_player_savebed(object *op);
void leave_map(object *op);
void set_map_timeout(mapstruct *oldmap);
char *clean_path(char *file);
char *unclean_path(char *src);
void enter_exit(object *op, object *exit_ob);
void process_active_maps(void);
void process_players1(mapstruct *map);
void process_players2(mapstruct *map);
void process_events(mapstruct *map);
void clean_tmp_files(void);
void cleanup(void);
void leave(player *pl, int draw_exit);
int forbid_play(void);
void do_specials(void);
int main(int argc, char **argv);
/* monster.c */
object *check_enemy(object *npc, rv_vector *rv);
object *find_nearest_living_creature(object *npc);
object *find_enemy(object *npc, rv_vector *rv);
int check_wakeup(object *op, object *enemy, rv_vector *rv);
int move_randomly(object *op);
int move_monster(object *op);
int can_hit(object *ob1, object *ob2, rv_vector *rv);
object *monster_choose_random_spell(object *monster);
int monster_cast_spell(object *head, object *part, object *pl, int dir, rv_vector *rv);
int monster_use_scroll(object *head, object *part, object *pl, int dir, rv_vector *rv);
int monster_use_skill(object *head, object *part, object *pl, int dir);
int monster_use_range(object *head, object *part, object *pl, int dir);
int monster_use_bow(object *head, object *part, object *pl, int dir);
int check_good_weapon(object *who, object *item);
int check_good_armour(object *who, object *item);
void monster_check_pickup(object *monster);
int monster_can_pick(object *monster, object *item);
void monster_apply_below(object *monster);
void monster_check_apply(object *mon, object *item);
void npc_call_help(object *op);
int dist_att(int dir, object *ob, object *enemy, object *part, rv_vector *rv);
int run_att(int dir, object *ob, object *enemy, object *part, rv_vector *rv);
int hitrun_att(int dir, object *ob, object *enemy);
int wait_att(int dir, object *ob, object *enemy, object *part, rv_vector *rv);
int disthit_att(int dir, object *ob, object *enemy, object *part, rv_vector *rv);
int wait_att2(int dir, object *ob, object *enemy, object *part, rv_vector *rv);
void circ1_move(object *ob);
void circ2_move(object *ob);
void pace_movev(object *ob);
void pace_moveh(object *ob);
void pace2_movev(object *ob);
void pace2_moveh(object *ob);
void rand_move(object *ob);
void check_earthwalls(object *op, int x, int y);
void check_doors(object *op, int x, int y);
void communicate(object *op, char *txt);
int talk_to_npc(object *op, object *npc, char *txt);
int talk_to_wall(object *npc, char *txt);
object *find_mon_throw_ob(object *op);
int can_detect_enemy(object *op, object *enemy, rv_vector *rv);
int stand_in_light(object *op);
int can_see_enemy(object *op, object *enemy);
/* move.c */
int move_object(object *op, int dir);
int move_ob(object *op, int dir, object *originator);
int transfer_ob(object *op, int x, int y, int randomly, object *originator);
int teleport(object *teleporter, uint8 tele_type, object *user);
void recursive_roll(object *op, int dir, object *pusher);
int try_fit(object *op, int x, int y);
int roll_ob(object *op, int dir, object *pusher);
int push_ob(object *who, int dir, object *pusher);
/* pets.c */
object *get_pet_enemy(object *pet, rv_vector *rv);
void terminate_all_pets(object *owner);
void remove_all_pets(mapstruct *map);
void follow_owner(object *ob, object *owner);
void pet_move(object *ob);
/* player.c */
player *find_player(char *plname);
void display_motd(object *op);
int playername_ok(char *cp);
int add_player(NewSocket *ns);
archetype *get_player_archetype(archetype *at);
object *get_nearest_player(object *mon);
int path_to_player(object *mon, object *pl, int mindiff);
void give_initial_items(object *pl, treasurelist *items);
void get_name(object *op);
void get_password(object *op);
void play_again(object *op);
int receive_play_again(object *op, char key);
void confirm_password(object *op);
void get_party_password(object *op, int partyid);
int roll_stat(void);
void roll_stats(object *op);
void Roll_Again(object *op);
void Swap_Stat(object *op, int Swap_Second);
int key_roll_stat(object *op, char key);
int key_change_class(object *op, char key);
int key_confirm_quit(object *op, char key);
void flee_player(object *op);
int check_pick(object *op);
object *find_arrow(object *op, char *type);
int fire_bow(object *op, object *part, int dir);
void fire_misc_object(object *op, int dir);
void fire(object *op, int dir);
object *find_key(object *pl, object *container, object *door);
void move_player_attack(object *op, int dir);
int move_player(object *op, int dir);
int handle_newcs_player(object *op);
int save_life(object *op);
void remove_unpaid_objects(object *op, object *env);
char *gravestone_text(object *op);
void do_some_living(object *op);
void kill_player(object *op);
void loot_object(object *op);
void fix_weight(void);
void fix_luck(void);
void cast_dust(object *op, object *throw_ob, int dir);
void make_visible(object *op);
int is_true_undead(object *op);
int hideability(object *ob);
void do_hidden_move(object *op);
int stand_near_hostile(object *who);
int player_can_view(object *pl, object *op);
int action_makes_visible(object *op);
int op_on_battleground(object *op, int *x, int *y);
void dragon_ability_gain(object *who, int atnr, int level);
/* plugins.c */
CommArray_s *find_plugin_command(char *cmd, object *op);
void displayPluginsList(object *op);
int findPlugin(char *id);
void initPlugins(void);
void removeOnePlugin(char *id);
void initOnePlugin(char *pluginfile);
CFParm *CFWLog(CFParm *PParm);
CFParm *CFWNewInfoMap(CFParm *PParm);
CFParm *CFWSpringTrap(CFParm *PParm);
CFParm *CFWCastSpell(CFParm *PParm);
CFParm *CFWCmdRSkill(CFParm *PParm);
CFParm *CFWBecomeFollower(CFParm *PParm);
CFParm *CFWPickup(CFParm *PParm);
CFParm *CFWGetMapObject(CFParm *PParm);
CFParm *CFWESRVSendItem(CFParm *PParm);
CFParm *CFWFindPlayer(CFParm *PParm);
CFParm *CFWManualApply(CFParm *PParm);
CFParm *CFWCmdDrop(CFParm *PParm);
CFParm *CFWCmdTake(CFParm *PParm);
CFParm *CFWTransferObject(CFParm *PParm);
CFParm *CFWCmdTitle(CFParm *PParm);
CFParm *CFWKillObject(CFParm *PParm);
CFParm *CFWDoForgetSpell(CFParm *PParm);
CFParm *CFWDoLearnSpell(CFParm *PParm);
CFParm *CFWCheckSpellKnown(CFParm *PParm);
CFParm *CFWESRVSendInventory(CFParm *PParm);
CFParm *CFWCreateArtifact(CFParm *PParm);
CFParm *CFWGetArchetype(CFParm *PParm);
CFParm *CFWUpdateSpeed(CFParm *PParm);
CFParm *CFWUpdateObject(CFParm *PParm);
CFParm *CFWFindAnimation(CFParm *PParm);
CFParm *CFWGetArchetypeByObjectName(CFParm *PParm);
CFParm *CFWInsertObjectInMap(CFParm *PParm);
CFParm *CFWReadyMapName(CFParm *PParm);
CFParm *CFWAddExp(CFParm *PParm);
CFParm *CFWDetermineGod(CFParm *PParm);
CFParm *CFWFindGod(CFParm *PParm);
CFParm *CFWDumpObject(CFParm *PParm);
CFParm *CFWLoadObject(CFParm *PParm);
CFParm *CFWRemoveObject(CFParm *PParm);
CFParm *CFWAddString(CFParm *PParm);
CFParm *CFWAddRefcount(CFParm *PParm);
CFParm *CFWFreeString(CFParm *PParm);
CFParm *CFWGetFirstMap(CFParm *PParm);
CFParm *CFWGetFirstPlayer(CFParm *PParm);
CFParm *CFWGetFirstArchetype(CFParm *PParm);
CFParm *CFWQueryCost(CFParm *PParm);
CFParm *CFWQueryMoney(CFParm *PParm);
CFParm *CFWPayForItem(CFParm *PParm);
CFParm *CFWPayForAmount(CFParm *PParm);
CFParm *CFWNewDrawInfo(CFParm *PParm);
CFParm *CFWMovePlayer(CFParm *PParm);
CFParm *CFWMoveObject(CFParm *PParm);
CFParm *CFWSendCustomCommand(CFParm *PParm);
CFParm *CFWCFTimerCreate(CFParm *PParm);
CFParm *CFWCFTimerDestroy(CFParm *PParm);
CFParm *CFWSetAnimation(CFParm *PParm);
CFParm *CFWCommunicate(CFParm *PParm);
CFParm *CFWFindBestObjectMatch(CFParm *PParm);
CFParm *CFWApplyBelow(CFParm *PParm);
CFParm *CFWFreeObject(CFParm *PParm);
CFParm *CFWObjectCreateClone(CFParm *PParm);
CFParm *CFWTeleportObject(CFParm *PParm);
CFParm *RegisterGlobalEvent(CFParm *PParm);
CFParm *UnregisterGlobalEvent(CFParm *PParm);
void GlobalEvent(CFParm *PParm);
/* resurrection.c */
void dead_player(object *op);
int cast_raise_dead_spell(object *op, int dir, int spell_type, object *corpseobj);
int resurrection_fails(int levelcaster, int leveldead);
int resurrect_player(object *op, char *playername, int rspell);
void dead_character(char *name);
int dead_player_exists(char *name);
/* rune.c */
int write_rune(object *op, int dir, int inspell, int level, char *runename);
void move_rune(object *op);
void rune_attack(object *op, object *victim);
void spring_trap(object *trap, object *victim);
int dispel_rune(object *op, int dir, int risk);
int trap_see(object *op, object *trap);
int trap_show(object *trap, object *where);
int trap_disarm(object *disarmer, object *trap, int risk);
void trap_adjust(object *trap, int difficulty);
int cast_generic_rune(object *op, object *caster, int dir, char *stringarg, int type);
/* shop.c */
int query_cost(object *tmp, object *who, int flag);
char *cost_string_from_value(int cost);
char *query_cost_string(object *tmp, object *who, int flag);
int query_money(object *op);
int pay_for_amount(int to_pay, object *pl);
int pay_for_item(object *op, object *pl);
int pay_from_container(object *op, object *pouch, int to_pay);
int get_payment2(object *pl, object *op);
int get_payment(object *pl);
void sell_item(object *op, object *pl);
void shop_listing(object *op);
/* skills.c */
int attempt_steal(object *op, object *who);
int adj_stealchance(object *op, object *victim, int roll);
int steal(object *op, int dir);
int pick_lock(object *pl, int dir);
int attempt_pick_lock(object *door, object *pl);
int hide(object *op);
int attempt_hide(object *op);
int jump(object *pl, int dir);
int skill_ident(object *pl);
int do_skill_detect_curse(object *pl);
int do_skill_detect_magic(object *pl);
int do_skill_ident2(object *tmp, object *pl, int obj_class);
int do_skill_ident(object *pl, int obj_class);
int use_oratory(object *pl, int dir);
int singing(object *pl, int dir);
int find_traps(object *pl);
int pray(object *pl);
void meditate(object *pl);
int write_on_item(object *pl, char *params);
int write_note(object *pl, object *item, char *msg);
int write_scroll(object *pl, object *scroll);
int remove_trap(object *op, int dir);
int skill_throw(object *op, int dir, char *params);
object *find_throw_ob(object *op, char *request);
object *make_throw_ob(object *orig);
void do_throw(object *op, object *toss_item, int dir);
/* skill_util.c */
void find_skill_exp_name(object *pl, object *exp, int index);
int do_skill(object *op, int dir, char *string);
int calc_skill_exp(object *who, object *op);
float calc_stat_mult(object *who, int sk);
int get_weighted_skill_stat_sum(object *who, int sk);
void init_new_exp_system(void);
void dump_skills(void);
void init_exp_obj(void);
void link_skills_to_exp(void);
int check_link(int stat, object *exp);
void read_skill_params(void);
int lookup_skill_by_name(char *string);
int check_skill_to_fire(object *who);
int check_skill_to_apply(object *who, object *item);
int init_player_exp(object *pl);
void unlink_skill(object *skillop);
int link_player_skills(object *pl);
int link_player_skill(object *pl, object *skillop);
int learn_skill(object *pl, object *scroll);
void show_skills(object *op);
int use_skill(object *op, char *string);
int change_skill(object *who, int sk_index);
int change_skill_to_skill(object *who, object *skl);
int attack_melee_weapon(object *op, int dir, char *string);
int attack_hth(object *pl, int dir, char *string);
int skill_attack(object *tmp, object *pl, int dir, char *string);
int do_skill_attack(object *tmp, object *op, char *string);
int hth_damage(object *target, object *pl);
int SK_level(object *op);
float get_skill_time(object *op, int skillnr);
int get_skill_stat1(object *op);
int get_skill_stat2(object *op);
int get_skill_stat3(object *op);
int get_weighted_skill_stats(object *op);
object *get_skill_from_inventory(object *op, const char *skname);
/* spell_effect.c */
void prayer_failure(object *op, int failure, int power);
void cast_mana_storm(object *op, int lvl);
void cast_magic_storm(object *op, object *tmp, int lvl);
void aggravate_monsters(object *op);
int recharge(object *op);
void polymorph_living(object *op);
void polymorph_melt(object *who, object *op);
void polymorph_item(object *who, object *op);
void polymorph(object *op, object *who);
int cast_polymorph(object *op, int dir);
int cast_create_food(object *op, object *caster, int dir, char *stringarg);
int cast_speedball(object *op, int dir, int type);
int probe(object *op, int dir);
int cast_invisible(object *op, object *caster, int spell_type);
int cast_earth2dust(object *op, object *caster);
int cast_wor(object *op, object *caster);
int cast_wow(object *op, int dir, int ability, SpellTypeFrom item);
int perceive_self(object *op);
int cast_create_town_portal(object *op, object *caster, int dir);
int cast_destruction(object *op, object *caster, int dam, int attacktype);
int magic_wall(object *op, object *caster, int dir, int spell_type);
int cast_light(object *op, object *caster, int dir);
int dimension_door(object *op, int dir);
int cast_heal(object *op, int dir, int spell_type);
int cast_regenerate_spellpoints(object *op);
int cast_change_attr(object *op, object *caster, int dir, int spell_type);
int summon_pet(object *op, int dir, SpellTypeFrom item);
int create_bomb(object *op, object *caster, int dir, int spell_type, char *name);
void animate_bomb(object *op);
int fire_cancellation(object *op, int dir, archetype *at, int magic);
void move_cancellation(object *op);
void cancellation(object *op);
int cast_create_missile(object *op, object *caster, int dir, char *stringarg);
int alchemy(object *op);
int remove_curse(object *op, int type, SpellTypeFrom src);
int cast_identify(object *op);
int cast_detection(object *op, int type);
int cast_pacify(object *op, object *weap, archetype *arch, int spellnum);
int summon_fog(object *op, object *caster, int dir, int spellnum);
int create_the_feature(object *op, object *caster, int dir, int spell_effect);
int cast_transfer(object *op, int dir);
int drain_magic(object *op, int dir);
void counterspell(object *op, int dir);
int summon_hostile_monsters(object *op, int n, char *monstername);
int cast_charm(object *op, object *caster, archetype *arch, int spellnum);
int cast_charm_undead(object *op, object *caster, archetype *arch, int spellnum);
object *choose_cult_monster(object *pl, object *god, int summon_level);
int summon_cult_monsters(object *op, int old_dir);
int summon_avatar(object *op, object *caster, int dir, archetype *at, int spellnum);
object *fix_summon_pet(archetype *at, object *op, int dir, int type);
int cast_consecrate(object *op);
int finger_of_death(object *op, object *caster, int dir);
int animate_weapon(object *op, object *caster, int dir, archetype *at, int spellnum);
int cast_daylight(object *op);
int cast_nightfall(object *op);
int cast_faery_fire(object *op, object *caster);
int make_object_glow(object *op, int radius, int time);
int cast_cause_disease(object *op, object *caster, int dir, archetype *disease_arch, int type);
void move_aura(object *aura);
void move_peacemaker(object *op);
int cast_cause_conflict(object *op, object *caster, archetype *spellarch, int type);
/* spell_util.c */
void init_spells(void);
void dump_spells(void);
void spell_effect(int spell_type, int x, int y, mapstruct *map, object *originator);
spell *find_spell(int spelltype);
int path_level_mod(object *caster, int base_level, int spell_type);
int casting_level(object *caster, int spell_type);
int check_spell_known(object *op, int spell_type);
int cast_spell(object *op, object *caster, int dir, int type, int ability, SpellTypeFrom item, char *stringarg);
int cast_create_obj(object *op, object *caster, object *new_op, int dir);
int summon_monster(object *op, object *caster, int dir, archetype *at, int spellnum);
int fire_bolt(object *op, object *caster, int dir, int type, int magic);
int fire_arch(object *op, object *caster, int dir, archetype *at, int type, int magic);
int fire_arch_from_position(object *op, object *caster, sint16 x, sint16 y, int dir, archetype *at, int type, int magic);
int cast_cone(object *op, object *caster, int dir, int strength, int spell_type, archetype *spell_arch, int magic);
void check_cone_push(object *op);
void cone_drop(object *op);
void move_cone(object *op);
void fire_a_ball(object *op, int dir, int strength);
void explosion(object *op);
void forklightning(object *op, object *tmp);
int reflwall(mapstruct *m, int x, int y, object *sp_op);
void move_bolt(object *op);
void move_golem(object *op);
void control_golem(object *op, int dir);
void move_missile(object *op);
void explode_object(object *op);
void check_fired_arch(object *op);
void move_fired_arch(object *op);
void drain_rod_charge(object *rod);
void fix_rod_speed(object *rod);
object *find_target_for_friendly_spell(object *op, int dir);
void move_ball_lightning(object *op);
int can_see_monsterP(mapstruct *m, int x, int y, int dir);
int spell_find_dir(mapstruct *m, int x, int y, object *exclude);
int SP_level_dam_adjust(object *op, object *caster, int spell_type);
int SP_level_strength_adjust(object *op, object *caster, int spell_type);
int SP_level_spellpoint_cost(object *op, object *caster, int spell_type);
void move_swarm_spell(object *op);
void fire_swarm(object *op, object *caster, int dir, archetype *swarm_type, int spell_type, int n, int magic);
int create_aura(object *op, object *caster, archetype *aura_arch, int spell_type, int magic);
int look_up_spell_by_name(object *op, char *spname);
void put_a_monster(object *op, char *monstername);
void shuffle_attack(object *op, int change_face);
void init_spell_param(void);
object *get_pointed_target(object *op, int dir);
int cast_smite_spell(object *op, object *caster, int dir, int type);
/* swamp.c */
void walk_on_deep_swamp(object *op, object *victim);
void move_deep_swamp(object *op);
/* swap.c */
void read_map_log(void);
void swap_map(mapstruct *map);
void check_active_maps(void);
mapstruct *map_least_timeout(char *except_level);
void swap_below_max(char *except_level);
int players_on_map(mapstruct *m, int show_all);
void flush_old_maps(void);
/* time.c */
void remove_door(object *op);
void remove_door2(object *op);
void generate_monster(object *gen);
void regenerate_rod(object *rod);
void remove_force(object *op);
void remove_blindness(object *op);
void remove_confusion(object *op);
void execute_wor(object *op);
void poison_more(object *op);
void move_gate(object *op);
void move_timed_gate(object *op);
void move_detector(object *op);
void animate_trigger(object *op);
void move_hole(object *op);
object *stop_item(object *op);
void fix_stopped_item(object *op, mapstruct *map, object *originator);
object *fix_stopped_arrow(object *op);
void move_arrow(object *op);
void change_object(object *op);
void move_teleporter(object *op);
void move_player_changer(object *op);
void move_firewall(object *op);
void move_firechest(object *op);
void move_player_mover(object *op);
void move_creator(object *op);
void move_marker(object *op);
int process_object(object *op);
/* timers.c */
void cftimer_process_timers(void);
int cftimer_create(int id, long delay, object *ob, int mode);
int cftimer_destroy(int id);
int cftimer_find_free_id(void);
/* weather.c */
void set_darkness_map(mapstruct *m);
void dawn_to_dusk(timeofday_t *tod);
void tick_the_clock(void);
void init_weather(void);
void perform_weather(void);
void write_pressuremap(void);
void read_pressuremap(void);
void init_pressure(void);
void write_winddirmap(void);
void read_winddirmap(void);
void write_windspeedmap(void);
void read_windspeedmap(void);
void init_wind(void);
void write_humidmap(void);
void read_humidmap(void);
void write_elevmap(void);
void read_elevmap(void);
void write_watermap(void);
void read_watermap(void);
void init_humid_elev(void);
void write_temperaturemap(void);
void read_temperaturemap(void);
void init_temperature(void);
void update_humid(void);
void temperature_calc(int x, int y, timeofday_t *tod);
void smooth_pressure(void);
void perform_pressure(void);
void smooth_wind(void);
int polar_distance(int x, int y, int equator);
int humid_tile(int x, int y);
int similar_direction(int a, int b);
char *weathermap_to_worldmap_corner(int wx, int wy, int *x, int *y, int dir);
