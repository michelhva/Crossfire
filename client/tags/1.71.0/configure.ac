# IMPORTANT:  Do not specify a version number with a "-" in it.  The number is
# used to create RPMs, and some RPM build tools do not allow use of dash in
# the version number.

AC_INIT([crossfire-client], [1.71.0])
AC_CONFIG_AUX_DIR(utils)
AC_CONFIG_HEADERS([common/config.h])
AC_CONFIG_SRCDIR(crossfire-client.spec)
AC_CANONICAL_TARGET
AM_INIT_AUTOMAKE

# These are subdirectories that have make files in them
CF_SUBDIRS="common pixmaps"

##############################################################################
# Configuration Options
##############################################################################
AC_ARG_WITH(loglevel, [AS_HELP_STRING([--with-logevel=level],
            [Default logging level (default 0, debug)])],
            [MINLOGLEVEL="$withval"], [MINLOGLEVEL=0])

# Add support for dmalloc, if the user wants it.
AM_WITH_DMALLOC

#### Client Options ####
AC_ARG_ENABLE(sdl, [AS_HELP_STRING([--disable-sdl],
              [SDL renderer (enabled by default)])],
              [], [use_sdl=yes])

AC_ARG_ENABLE(metaserver2, [AS_HELP_STRING([--disable-metaserver2],
              [Metaserver2 support (enabled by default)])],
              [], [metaserver2=yes])

AC_ARG_ENABLE(lua, [AS_HELP_STRING([--enable-lua],
              [Lua scripting interface (disabled by default)])],
              [enable_lua=yes], [enable_lua=no])

AC_ARG_ENABLE(opengl, [AS_HELP_STRING([--disable-opengl],
              [OpenGL renderer (enabled by default)])],
              [], [enable_opengl=yes])

#### Sound Options ####
AC_ARG_ENABLE(sound, [AS_HELP_STRING([--disable-sound],
              [Disable sound (built by default)])],
              [], [enable_sound=yes])

AC_ARG_WITH(sound-dir, [AS_HELP_STRING([--with-sound-dir=dir],
            [Directory where the sounds are installed])],
            SOUNDDIR="$withval")

##############################################################################
# Program Checks
##############################################################################
AC_PROG_CC
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_MAKE_SET
AC_PROG_RANLIB

AC_PATH_PROG(AR, ar)
AC_PATH_PROG(CP, cp)
AC_PATH_PROG(DEPEND, makedepend)
AC_PATH_PROG(MKDIR, mkdir)
AC_PATH_PROG(PERL, perl)
AC_PATH_PROG(PKG_CONFIG, pkg-config)
AC_PATH_PROG(RM, rm)
AC_PATH_PROG(TAR, tar)

# Check if svnversion is available to get the rev number
AC_PATH_PROG(SVNVERSION, svnversion)

##############################################################################
# Library Checks
##############################################################################
# Find out if X is available. The variable have_x is set if libX11 is
# found to mimic AC_PATH_XTRA.
AC_PATH_X
if test -n "$PKG_CONFIG"; then
    AC_MSG_CHECKING([pkg-config files for X11 are available])
    PKG_CHECK_EXISTS([x11],[
	x11_pkgconfig=yes
	have_x=yes
	],[
	x11_pkgconfig=no
    ])
    AC_MSG_RESULT([$x11_pkgconfig])
else
    x11_pkgconfig=no
fi

# Use the autoconf macro if no pkg-config files
if test "$x11_pkgconfig" = yes; then
    PKG_CHECK_MODULES([X11], [x11])
elif test "$have_x" = yes; then
    AC_PATH_XTRA
    test -z "$X11_CFLAGS" && X11_CFLAGS="$X_CFLAGS"
    test -z "$X11_LIBS" && X11_LIBS="$X_LIBS -lX11"
fi

# FIXME: This sound dir specification overwrites the user-specified one.
SOUNDDIR="${datadir}/crossfire-client/sounds"

networklibs="yes"

case "$target" in
    alpha-dec-osf*)
        # If we are not using gcc, we want the ansi version of cc.
        if test -z "$GCC"; then
        #   CFLAGS="$CFLAGS -std1 -warnprotos" # Not yet ready for warnprotos...
            CFLAGS="$CFLAGS -std1"
        fi
    ;;
    # SGI systems don't need -lnsl -lsocket
    mips*-sgi-irix*)
        networklibs="no"
    ;;
    # linux systems don't appear to need -lnsl
    *-linux-*)
        networklibs="no"
    ;;
    *-*-solaris2*)
        need_r_flag="yes"
    ;;
    *-*-mingw32*)
        win32="yes"
        # -mms-bitfields is needed for GTK+ in Windows.  It is set
        # automatically by pkg-config, but it is also needed in common, so we
        # explicitely set it
        CFLAGS="$CFLAGS -mms-bitfields"
        WIN32_LIBS="-lwsock32 -lwinmm"
        AC_SUBST(WIN32_LIBS)
    ;;
    *)
    ;;
esac

# Detect pthreads libraries
#
# pthreads is required by both metaservers.  Only metaserver2 is optional, so
# we cannot build any client if pthreads is not present.
#
# AX_PTHREAD([ACTION-IF-FOUND[, ACTION-IF-NOT-FOUND]])
#
# Default is to define HAVE_PTHREAD.  PTHREAD_CC and PTHREAD_LIBS are set.
# Both compile and link with these flags.
#
AX_PTHREAD(,AC_MSG_ERROR(POSIX threads not found.  Cannot build any client.))

# Check for required GTK libraries.
AM_PATH_GTK_2_0([2.24.0], [CF_SUBDIRS="$CF_SUBDIRS gtk-v2"],
                [AC_MSG_ERROR(Missing required GTK libraries.)])

AM_MAINTAINER_MODE
AC_SUBST(PACKAGE_LIBS)
AC_SUBST(INTLLIBS)

# sound.c needs sqrt.  So does png (on some systems).  Lua also needs it, so
# put it near the top
#
AC_CHECK_LIB(m, sqrt)

AC_SEARCH_LIBS(pthread_create, [pthread, pthreadgc, pthreadgc1, pthreadgc2], [AC_DEFINE(HAVE_LIBPTHREAD, 1, [Define to 1 if you have the 'pthread' library.])])

# Test for Lua only if it has been explicitly enabled by the user.
if test "$enable_lua" = "yes"; then
    PKG_CHECK_MODULES([LUA], [lua-5.1], [have_lua=yes])
    AC_DEFINE(HAVE_LUA, 1, [Define to use Lua 5.x])
fi

if eval "test x$need_r_flag = xyes"; then
    LDFLAGS="$LDFLAGS"
fi

# Check for additional X11 libraries.

# Save and modify CPPFLAGS and LIBS (to include X and GTK paths temporarily).
gcfc_save_CPPFLAGS="$CPPFLAGS"
gcfc_save_LIBS="$LIBS"
CPPFLAGS="$GTK_CFLAGS $CPPFLAGS $OPENGL_CFLAGS"
LIBS="$LIBS $GTK_LIBS $X11_LIBS"

# Need to check for Xext while we still have X11_LIBS set.
AC_CHECK_LIB(Xext, main, X11_LIBS="$X11_LIBS -lXext")

# Restore previous CPPFLAGS and LIBS.
CPPFLAGS="$gcfc_save_CPPFLAGS"
LIBS="$gcfc_save_LIBS"
AC_SUBST(X11_CFLAGS)
AC_SUBST(X11_LIBS)

# Check for OpenGL if it is enabled.
if test "$enable_opengl" = "yes"; then
    PKG_CHECK_MODULES([gl], [gl], [have_opengl=yes], [no_gl=yes])
    PKG_CHECK_MODULES([glu], [glu], [have_opengl=yes], [no_glu=yes])

    if test "$have_opengl" = "yes"; then
        OPENGL_CFLAGS="${gl_CFLAGS} ${glu_CFLAGS}"
        OPENGL_LIBS="${gl_LIBS} ${glu_LIBS}"

        AC_DEFINE(HAVE_OPENGL, 1, [Define if you have OpenGL.])
        AC_SUBST(OPENGL_CFLAGS)
        AC_SUBST(OPENGL_LIBS)

        opengl="yes"
    else
        opengl="no"
    fi
else
    opengl="no"
fi

# Check for sound libraries if it is enabled.
if test "$enable_sound" = "yes"; then
    PKG_CHECK_MODULES([SDL_mixer], [SDL_mixer], [sdl_sound=yes], [sdl_sound=no])

    if test "$sdl_sound" = "yes"; then
        CFLAGS="${CFLAGS} ${SDL_mixer_CFLAGS}"
        SND_LIBS="${SDL_mixer_LIBS}"

        AC_SUBST(SND_LIBS)
        CF_SUBDIRS="${CF_SUBDIRS} sound-src";
    else
        AC_MSG_WARN([Cannot find SDL_Mixer; not enabling sound support.])
    fi
else
    # Set this to 'no' so that it is properly displayed in the results.
    sdl_sound=no
fi

AC_SUBST(GUI_OBJS)
AC_SUBST(GUI_SRCS)
AC_SUBST(LDFLAGS)
AC_SUBST(TARGET)
AC_SUBST(CF_SUBDIRS)

# Check for SDL 1.1.3 and sdl_image.  Note SDL_image does not ship with an
# sdl-image-config, so I'll just assume it is in the same dir as SDL.
if eval "test x$use_sdl = xyes"; then
    AM_PATH_SDL(1.1.3)

    if eval "test x$no_sdl = x"; then
        AC_CHECK_LIB( SDL_image, IMG_LoadPNG_RW,
            have_sdlimage="yes", have_sdlimage="no", $SDL_CFLAGS $SDL_LIBS)
        if eval "test x$have_sdlimage = xyes"; then
            AC_MSG_NOTICE([sdl image detected!])
            SDL_LIBS="$SDL_LIBS -lSDL_image"
        else
            no_sdl="yes"
        fi
    fi

    if eval "test x$no_sdl = x"; then
        AC_DEFINE(HAVE_SDL, 1, [Define if you have SDL])
    else
        use_sdl="no"
    fi
fi

AC_SUBST(SDL_CFLAGS)
AC_SUBST(SDL_LIBS)

# We need to check for socket/nsl here - some versions of the windowing
# systems need these to properly link with X (I thought AC_PATH_XTRA should
# take care of this, but apparently is does not).  If it is not here, the
# check for xpm fails, and configure quits.

if eval "test x$networklibs = xyes"; then
    AC_CHECK_LIB(nsl, main, LIBS="$LIBS -lnsl")
    AC_CHECK_LIB(socket, main, LIBS="$LIBS -lsocket")
fi

LIBCURL_CHECK_CONFIG()
LIBS="$LIBS $LIBCURL"
CPPFLAGS="$CPPFLAGS $LIBCURL_CPPFLAGS"

AC_SUBST(LIBS)

# png on some systems need the zlib, so check for it
AC_CHECK_LIB(z, main)

AC_CHECK_LIB(png, main, , [AC_MSG_ERROR([You must have the png library installed to compile the client])])

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([curl/curl.h fcntl.h pthread.h string.h sys/ioctl.h sys/select.h sys/time.h unistd.h])

# Metaserver support is pretty critical to the client, so fail unless the
# user explicity tells us to compile it without metaserver2 support.
if eval "test x$ac_cv_header_curl_curl_h = xno" ; then
    if eval "test x$metaserver2 = xyes"; then
        AC_MSG_ERROR([curl/curl.h header not found, but metaserver2 support is enabled.  Install header file or use --disable-metaserver2]);
    fi
fi

# Used to control default behavior when client starts up - even if user has
# necessary files, if they use --disable-metaserver2, they won't try to get
# data from those servers.
#
if eval "test x$metaserver2 = xyes"; then
    AC_DEFINE_UNQUOTED(METASERVER2, 1, [If metaserver2 support is enabled.])
else
    AC_DEFINE_UNQUOTED(METASERVER2, 0, [If metaserver2 support is enabled.])
fi

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST
AC_TYPE_SIZE_T
AC_HEADER_TIME

AC_CHECK_SIZEOF(long)
AC_CHECK_SIZEOF(long long)

# Checks for library functions.
AC_PROG_GCC_TRADITIONAL
AC_FUNC_VPRINTF
AC_CHECK_FUNCS([getaddrinfo mkdir socket strcspn sysconf])

AC_SUBST(XPM)
AC_SUBST(SOUNDDIR)
AC_SUBST(LIBXPM_LIB)

# Run evals on the original variable names so that they get expanded to the
# actual path - otherwise, the substitution below will contain the unexpanded
# variables that don't resolve.

test "x$prefix" = xNONE && prefix=$ac_default_prefix
test "x$exec_prefix" = xNONE && exec_prefix='${prefix}'

ndatadir=`eval echo ${datadir}`
ndatadir=`eval echo ${ndatadir}/crossfire-client`

# Datadir should be working directory in Windows, as there is no set
# installation directory (choosable at installation time)
if eval "test x$win32 = xyes" ; then
    ndatadir="."
fi

# Do this twice to cover variables within variables.
nbindir=`eval echo ${bindir}`
nbindir=`eval echo ${nbindir}`

AC_DEFINE_UNQUOTED(CF_DATADIR, "${ndatadir}", [Location of read-only machine independent data.])
AC_DEFINE_UNQUOTED(BINDIR, "${nbindir}", [Location of other binaries.])
AC_DEFINE_UNQUOTED(MINLOGLEVEL, ${MINLOGLEVEL}, [Default logging level.])

AC_SUBST(CF_DATADIR)

# Make all Makefiles, even if CF_SUBDIRS are not set for that directory.
# since CF_SUBDIRS won't be set in the top level Makefile, we don't descend
# into these subdirs.

AC_CONFIG_FILES([
    Makefile common/Makefile
    sound-src/Makefile gtk-v2/Makefile pixmaps/Makefile
    gtk-v2/ui/Makefile gtk-v2/src/Makefile gtk-v2/themes/Makefile
])
AC_OUTPUT

##############################################################################
# Configuration Summary
##############################################################################
AC_MSG_NOTICE([Configuration Summary ----------------------------------------------])
AC_MSG_NOTICE([])
AC_MSG_NOTICE([  Installation Paths])
AC_MSG_NOTICE([    Prefix                   ${prefix}])
AC_MSG_NOTICE([    Binaries                 $nbindir])
AC_MSG_NOTICE([    Configuration            $sysconfdir])
AC_MSG_NOTICE([    Data                     $ndatadir])
AC_MSG_NOTICE([])
AC_MSG_NOTICE([  Build Options])
AC_MSG_NOTICE([    SDL renderer             $use_sdl])
AC_MSG_NOTICE([    OpenGL renderer          $opengl])
AC_MSG_NOTICE([    Sound (SDL_mixer)        $sdl_sound])
AC_MSG_NOTICE([])
AC_MSG_NOTICE([  Scripting Options])
AC_MSG_NOTICE([    Build Lua interface?     $enable_lua])
