if HAVE_LIBDL
LIBDL=-ldl
AM_LDFLAGS=-export-dynamic
else
LIBDL=
AM_LDFLAGS=
endif
#note all .a are here twice for compilation problems
AM_LIBTOOLFLAGS= --preserve-dup-deps
LINK = $(LIBTOOL) --tag=CC $(AM_LIBTOOLFLAGS) $(LIBTOOLFLAGS) \
        --mode=link $(CCLD) $(AM_CFLAGS) $(CFLAGS) $(AM_LDFLAGS) \
       $(LDFLAGS) -o $@

LDADD = $(top_builddir)/server/libserver.a \
        $(top_builddir)/common/libcross.a \
        $(top_builddir)/random_maps/librandom_map.a \
        $(top_builddir)/socket/libsocket.a \
        $(top_builddir)/types/libtypes.a \
        $(LIBDL) @CHECK_LIBS@

../../../types/libtypes.a:
	( cd ../../../types ; make libtypes.a )

../../logs/unit/server:
	(mkdir ../../logs; mkdir ../../logs/unit; mkdir ../../logs/unit/server)

../../../common/libcross.a:
	( cd ../../../common ; make libcross.a )

../../../socket/libsocket.a:
	( cd ../../../socket ; make libsocket.a )

../../../random_maps/librandom_map.a:
	( cd ../../../random_maps ; make librandom_map.a )

../../../server/libserver.a:
	( cd ../../../server ; make libserver.a )

# tests not implemented for now
#TESTS=check_alchemy check_apply check_ban check_build_map check_c_chat check_c_misc check_c_move check_c_new  check_commands  check_c_range check_c_wiz check_daemon check_disease check_egoitem check_gods check_hiscore check_init check_login check_main check_monster check_move check_pets check_player check_plugins check_resurrection check_rune check_shop check_skills check_skill_util check_spell_attack check_spell_effect check_spell_util check_swamp check_swap check_time check_timers check_weather check_win32
TESTS=check_attack check_c_object comet_perf check_c_party

CHECK_ROOT=../../
CHECK_FOLDER=unit/server
CHECK_PARENT_NAME=Unit
include $(top_srcdir)/check-fragment.am

clean-local:
	$(RM) .autorun.xml

check-local: check-html
clean-local: clean-unit
