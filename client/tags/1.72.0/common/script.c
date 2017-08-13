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
 * Handles the client-side scripting interface.
 *
 * Each script is an external process that keeps two pipes open between the
 * client and the script (one in each direction).  When the script starts, it
 * defaults to receiving no data from the client.  Normally, the first command
 * it sends to the client will be a request to have certain types of data sent
 * to the script as the client receives them from the server (such as drawinfo
 * commands).  The script can also request current information from the
 * client, such as the contents of the inventory or the map data (either live
 * or last viewed "fog-of-war" data).  The script can also send commands for
 * the client to pass to the server.
 *
 * Script Commands:
 *
 * watch {command type}
 *   whenever the server sends the given command type to the client, also send
 *   a copy to the script.
 *   Note that this checked before the client processes the command, so it will
 *   automatically handle new options that may be added in the future.
 *   If the command type is NULL, all commands are watched.
 *
 * unwatch {command type}
 *   turn off a previous watch command.  There may be a slight delay in
 *   response before the command is processed, so some unwanted data may
 *   still be sent to the script.
 *
 * request {data type}
 *   have the client send the given data to the script.
 *
 * issue [{repeat} {must_send}] {command}
 *   issue the specified command to the server.
 *   if {repeat} isn't numeric then the command is sent directly
 *   For "lock" and "mark" only, the parameters are converted to binary.
 *
 * draw {color} {text}
 *   display the text in the specified color as if the server had sent
 *   a drawinfo command.
 *
 * monitor
 *   send the script a copy of every command that is sent to the server.
 *
 * unmonitor
 *   turn off monitoring.
 *
 * sync {#}
 *   wait until the server has acknowledged all but {#} commands have been
 *   received
 *
 * To implement this:
 *
 * Processing script commands: gtk/gx11.c:do_network() and
 * x11/x11.c:event_loop() are modified to also watch for input from scripts
 * in the select() call, in which case script_process(fd) in this file is
 * called.
 *
 * Handling watches: common/client.c:DoClient() is modified to pass a copy
 * of each command to script_watch() before checking for it in the table.
 *
 * Handling of monitor: common/player.c:send_command() is modified to pass
 * a copy of each command to script_monitor() before sending to the server.
 *
 * Handling of requests: global variables are directly accessed from within
 * this file.
 *
 * Handling of issues: send_command() is called directly.  Note that this
 * command will be sent to any scripts that are monitoring output.
 *
 * Launching new scripts: common/player.c:extended_command() is extended to
 * add a check for "script <scriptname>" as an additional command, calling
 * script_init().  Also added is the "scripts" command to list all running
 * scripts, the "scriptkill" command to terminate a script (close the pipes
 * and assume it takes the hint), and the "scripttell" command to send a
 * message to a running script.
 */

/*
 * Include files
 */

/*
 * This does not work under Windows for now.  Someday this will be fixed :)
 */

#include "client.h"

#include <ctype.h>

#ifndef WIN32
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/wait.h>
/* for SIGHUP */
#include <signal.h>
#endif

#include "external.h"
#include "mapdata.h"
#include "p_cmd.h"
#include "script.h"

/*
 * Data structures
 */
struct script {
    char *name; /* the script name */
    char *params; /* the script parameters, if any */
#ifndef WIN32
    int out_fd; /* the file descriptor to which the client writes to the script */
    int in_fd; /* the file descriptor from which we read commands from the script */
#else
    HANDLE out_fd; /* the file descriptor to which the client writes to the script */
    HANDLE in_fd; /* the file descriptor from which we read commands from the script */
#endif /* WIN32 */
    int monitor; /* true if this script is monitoring commands sent to the server */
    int num_watch; /* number of commands we're watching */
    char **watch; /* array of commands that we're watching */
    int cmd_count; /* bytes already read in */
    char cmd[1024]; /* command from the script */
#ifndef WIN32
    int pid;
#else
    DWORD pid;   /* Handle to Win32 process ID */
    HANDLE process; /* Handle of Win32 process */
#endif
    int sync_watch;
};

/*
 * Global variables
 */
static struct script *scripts = NULL;

static int num_scripts = 0;

/*
 * Prototypes
 */
static int script_by_name(const char *name);

static void script_dead(int i);

static void script_process_cmd(int i);

static void send_map(int i, int x, int y);

static void script_send_item(int i, const char *head, const item *it);


/*
 * Functions
 */

#ifdef WIN32

#define write(x, y, z) emulate_write(x, y, z)
#define read(x, y, z) emulate_read(x, y, z)

static int emulate_read(HANDLE fd, char *buf, int len)
{
    DWORD dwBytesRead;
    BOOL rc;

    FlushFileBuffers(fd);
    rc = ReadFile(fd, buf, len, &dwBytesRead, NULL);
    if (rc == FALSE) {
        return(-1);
    }
    buf[dwBytesRead] = '\0';

    return(dwBytesRead);
}

static int emulate_write(HANDLE fd, const char *buf, int len)
{
    DWORD dwBytesWritten;
    BOOL rc;

    rc = WriteFile(fd, buf, len, &dwBytesWritten, NULL);
    FlushFileBuffers(fd);
    if (rc == FALSE) {
        return(-1);
    }

    return(dwBytesWritten);
}

#endif /* WIN32 */

void script_init(const char *cparams) {
#ifndef WIN32
    int pipe1[2], pipe2[2];
    int pid;
    char *name, *args, params[MAX_BUF];

    if (!cparams) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Please specify a script to start. For help, type "
                      "'help script'.");
        return;
    }

    /* cparams is a const value, so copy the data into a buffer */
    strncpy(params, cparams, MAX_BUF - 1);
    params[MAX_BUF - 1] = '\0';

    /* Get name and args */
    name = params;
    args = name;
    while (*args && *args != ' ') {
        ++args;
    }
    while (*args && *args == ' ') {
        *args++ = '\0';
    }
    if (*args == 0) {
        args = NULL;
    }

    /* Open two pipes, one for stdin and the other for stdout. */
    if (pipe(pipe1) != 0) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Unable to start script--pipe failed");
        return;
    }
    if (pipe(pipe2) != 0) {
        close(pipe1[0]);
        close(pipe1[1]);

        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Unable to start script--pipe failed");
        return;
    }

    /* Fork */
    pid = fork();
    if (pid == -1) {
        close(pipe1[0]);
        close(pipe1[1]);
        close(pipe2[0]);
        close(pipe2[1]);
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Unable to start script--fork failed");
        return;
    }

    /* Child--set stdin/stdout to the pipes, then exec */
    if (pid == 0) {
        size_t i;
        int r;
        char *argv[256];

        /* Fill in argv[] */
        argv[0] = name;
        i = 1;
        while (args && *args && i < sizeof(argv)/sizeof(*argv)-1) {
            argv[i++] = args;
            while (*args && *args != ' ') {
                ++args;
            }
            while (*args && *args == ' ') {
                *args++ = '\0';
            }
        }
        argv[i] = NULL;

        /* Clean up file descriptor space */
        r = dup2(pipe1[0], 0);
        if (r != 0) {
            fprintf(stderr, "Script Child: Failed to set pipe1 as stdin\n");
        }
        r = dup2(pipe2[1], 1);
        if (r != 1) {
            fprintf(stderr, "Script Child: Failed to set pipe2 as stdout\n");
        }
        for (i = 3; i < 100; ++i) {
            close(i);
        }

        /* EXEC */
        r = execvp(argv[0], argv);

        /* If we get here, then there's been an failure of some sort.
         * In my case, it's often that I don't know what script name to
         * give to /script, so exec() can't find the script.
         *
         * Forward the error back to the client, using the script pipes.
         */

        if (r != -1) {
            printf("draw %d Script child: no error, but no execvp().\n", NDI_RED);
        } else {
            printf("draw %d Script child failed to start: %s\n", NDI_RED, strerror(errno));
        }

        exit(1);
    }

    /* Close the child's pipe ends */
    close(pipe1[0]);
    close(pipe2[1]);

    if (fcntl(pipe1[1], F_SETFL, O_NDELAY) == -1) {
        LOG(LOG_WARNING, "common::script_init", "Error on fcntl.");
    }

    /* g_realloc script array to add new entry; fill in the data */
    scripts = g_realloc(scripts, sizeof(scripts[0])*(num_scripts+1));

    if (scripts == NULL) {
        LOG(LOG_ERROR, "script_init",
                "Could not allocate memory: %s", strerror(errno));
        exit(EXIT_FAILURE);
    }

    scripts[num_scripts].name = g_strdup(name);
    scripts[num_scripts].params = args ? g_strdup(args) : NULL;
    scripts[num_scripts].out_fd = pipe1[1];
    scripts[num_scripts].in_fd = pipe2[0];
    scripts[num_scripts].monitor = 0;
    scripts[num_scripts].num_watch = 0;
    scripts[num_scripts].watch = NULL;
    scripts[num_scripts].cmd_count = 0;
    scripts[num_scripts].pid = pid;
    scripts[num_scripts].sync_watch = -1;
    ++num_scripts;
#else /* WIN32 */

    char *name, *args;
    char params[ MAX_BUF ];
    SECURITY_ATTRIBUTES saAttr;
    PROCESS_INFORMATION piProcInfo;
    STARTUPINFO siStartupInfo;
    HANDLE hChildStdinRd, hChildStdinWr, hChildStdinWrDup, hChildStdoutRd;
    HANDLE hChildStdoutWr, hChildStdoutRdDup, hSaveStdin, hSaveStdout;

    if (!cparams) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Please specifiy a script to launch!");
        return;
    }

    strncpy(params, cparams, MAX_BUF-1);
    params[MAX_BUF-1] = '\0';

    /* Get name and args */
    name = params;
    args = name;
    while (*args && *args != ' ') {
        ++args;
    }
    while (*args && *args == ' ') {
        *args++ = '\0';
    }
    if (*args == 0) {
        args = NULL;
    }

    saAttr.nLength = sizeof(SECURITY_ATTRIBUTES);
    saAttr.bInheritHandle = TRUE;
    saAttr.lpSecurityDescriptor = NULL;

    hSaveStdout = GetStdHandle(STD_OUTPUT_HANDLE);
    if (!CreatePipe(&hChildStdoutRd, &hChildStdoutWr, &saAttr, 0)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: stdout CreatePipe() failed");
        return;
    }

    if (!SetStdHandle(STD_OUTPUT_HANDLE, hChildStdoutWr)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: failed to redirect stdout using SetStdHandle()");
        return;
    }

    if (!DuplicateHandle(GetCurrentProcess(), hChildStdoutRd, GetCurrentProcess(), &hChildStdoutRdDup, 0, FALSE, DUPLICATE_SAME_ACCESS)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: failed to duplicate stdout using DuplicateHandle()");
        return;
    }

    CloseHandle(hChildStdoutRd);

    hSaveStdin = GetStdHandle(STD_INPUT_HANDLE);
    if (!CreatePipe(&hChildStdinRd, &hChildStdinWr, &saAttr, 0)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: stdin CreatePipe() failed");
        return;
    }

    if (!SetStdHandle(STD_INPUT_HANDLE, hChildStdinRd)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: failed to redirect stdin using SetStdHandle()");
        return;
    }

    if (!DuplicateHandle(GetCurrentProcess(), hChildStdinWr, GetCurrentProcess(), &hChildStdinWrDup, 0, FALSE, DUPLICATE_SAME_ACCESS)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: failed to duplicate stdin using DuplicateHandle()");
        return;
    }

    CloseHandle(hChildStdinWr);

    ZeroMemory(&piProcInfo, sizeof(PROCESS_INFORMATION));
    ZeroMemory(&siStartupInfo, sizeof(STARTUPINFO));
    siStartupInfo.cb = sizeof(STARTUPINFO);

    if (args) {
        args[-1] = ' ';
    }

    if (!CreateProcess(NULL, name, NULL, NULL, TRUE, CREATE_NEW_PROCESS_GROUP, NULL, NULL, &siStartupInfo, &piProcInfo)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: CreateProcess() failed");
        return;
    }

    CloseHandle(piProcInfo.hThread);

    if (args) {
        args[-1] = '\0';
    }

    if (!SetStdHandle(STD_INPUT_HANDLE, hSaveStdin)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: restoring original stdin failed");
        return;
    }

    if (!SetStdHandle(STD_OUTPUT_HANDLE, hSaveStdout)) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Script support: restoring original stdout failed");
        return;
    }

    /* g_realloc script array to add new entry; fill in the data */
    scripts = g_realloc(scripts, sizeof(scripts[0])*(num_scripts+1));

    if (scripts == NULL) {
        LOG(LOG_ERROR, "script_init",
                "Could not allocate memory: %s", strerror(errno));
        exit(EXIT_FAILURE);
    }

    scripts[num_scripts].name = g_strdup(name);
    scripts[num_scripts].params = args ? g_strdup(args) : NULL;
    scripts[num_scripts].out_fd = hChildStdinWrDup;
    scripts[num_scripts].in_fd = hChildStdoutRdDup;
    scripts[num_scripts].monitor = 0;
    scripts[num_scripts].num_watch = 0;
    scripts[num_scripts].watch = NULL;
    scripts[num_scripts].cmd_count = 0;
    scripts[num_scripts].pid = piProcInfo.dwProcessId;
    scripts[num_scripts].process = piProcInfo.hProcess;
    scripts[num_scripts].sync_watch = -1;
    ++num_scripts;
#endif /* WIN32 */
}

void script_sync(int commdiff)
{
    int i;

    if (commdiff < 0) {
        commdiff +=256;
    }
    for (i = 0; i < num_scripts; ++i) {
        if (commdiff <= scripts[i].sync_watch && scripts[i].sync_watch >= 0) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "sync %d\n", commdiff);
            write(scripts[i].out_fd, buf, strlen(buf));
            scripts[i].sync_watch = -1;
        }
    }
}

void script_list(void)
{
    if (num_scripts == 0) {
        draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "No scripts are currently running");
    } else {
        int i;
        char buf[1024];

        snprintf(buf, sizeof(buf), "%d scripts currently running:", num_scripts);
        draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
        for (i = 0; i < num_scripts; ++i) {
            if (scripts[i].params) {
                snprintf(buf, sizeof(buf), "%d %s  %s", i+1, scripts[i].name, scripts[i].params);
            } else {
                snprintf(buf, sizeof(buf), "%d %s", i+1, scripts[i].name);
            }
            draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
        }
    }
}

void script_kill(const char *params)
{
    int i;

    /* Verify that the number is a valid array entry */
    i = script_by_name(params);
    if (i < 0 || i >= num_scripts) {
        draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "No such running script");
        return;
    }
#ifndef WIN32
    kill(scripts[i].pid, SIGHUP);
#else
    GenerateConsoleCtrlEvent(CTRL_BREAK_EVENT, scripts[i].pid);
#endif /* WIN32 */
    draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                  "Killed script.");
    script_dead(i);
}

#ifdef WIN32
void script_killall(void)
{
    while (num_scripts > 0) {
        GenerateConsoleCtrlEvent(CTRL_BREAK_EVENT, scripts[0].pid);
        script_dead(0);
    }
}
#endif /* WIN32 */

void script_fdset(int *maxfd, fd_set *set)
{
#ifndef WIN32
    int i;

    for (i = 0; i < num_scripts; ++i) {
        FD_SET(scripts[i].in_fd, set);
        if (scripts[i].in_fd >= *maxfd) {
            *maxfd = scripts[i].in_fd+1;
        }
    }
#endif /* WIN32 */
}

void script_process(fd_set *set)
{
    int i;
    int r;
#ifdef WIN32
    DWORD nAvailBytes = 0;
    char cTmp;
    BOOL bRC;
    DWORD dwStatus;
    BOOL bStatus;
#endif

    /* Determine which script's fd is set */
    for (i = 0; i < num_scripts; ++i) {
#ifndef WIN32
        if (FD_ISSET(scripts[i].in_fd, set))
#else
        bStatus = GetExitCodeProcess(scripts[i].process, &dwStatus);
        bRC = PeekNamedPipe(scripts[i].in_fd, &cTmp, 1, NULL, &nAvailBytes, NULL);
        if (nAvailBytes)
#endif /* WIN32 */
        {
            /* Read in script[i].cmd */
            r = read(scripts[i].in_fd, scripts[i].cmd+scripts[i].cmd_count, sizeof(scripts[i].cmd)-scripts[i].cmd_count-1);
            if (r > 0) {
                scripts[i].cmd_count += r;
            }
#ifndef WIN32
            else if (r == 0 || errno == EBADF)
#else
            else if (r == 0 || GetLastError() == ERROR_BROKEN_PIPE)
#endif
            {
                /* Script has exited; delete it */
                script_dead(i);
                return;
            }
            /* If a newline or full buffer has been reached, process it */
            scripts[i].cmd[scripts[i].cmd_count] = 0; /* terminate string */
            while (scripts[i].cmd_count == sizeof(scripts[i].cmd)-1
#ifndef WIN32
                    || strchr(scripts[i].cmd, '\n'))
#else
                    || strchr(scripts[i].cmd, '\r\n'))
#endif /* WIN32 */
            {
                script_process_cmd(i);
                scripts[i].cmd[scripts[i].cmd_count] = 0; /* terminate string */
            }
            return; /* Only process one script at a time */
        }
#ifdef WIN32
        else if (!bRC || (bStatus && (dwStatus != STILL_ACTIVE))) { /* Error: assume dead */
            script_dead(i);
        }
#endif /* WIN32 */
    }
}

void script_watch(const char *cmd, const guint8 *data_initial, const int data_len, const enum CmdFormat format)
{
    int i;
    int w;
    int l, len;
    const guint8 *data;

    /* For each script... */
    for (i = 0; i < num_scripts; ++i) {
        /* For each watch... */
        for (w = 0; w < scripts[i].num_watch; ++w) {
            len = data_len;
            /* Does this command match our watch? */
            l = strlen(scripts[i].watch[w]);
            if (!l || strncmp(cmd, scripts[i].watch[w], l) == 0) {
                char buf[10240];

                data = data_initial;
                if (!len) {
                    snprintf(buf, sizeof(buf), "watch %s\n", cmd);
                } else
                    switch (format) {
                    case ASCII:
                        snprintf(buf, sizeof(buf), "watch %s %s\n", cmd, data);
                        break;

                    case SHORT_INT:
                        snprintf(buf, sizeof(buf), "watch %s %d %d\n", cmd, GetShort_String(data), GetInt_String(data+2));
                        break;

                    case SHORT_ARRAY: {
                        int be;
                        int p;

                        be = snprintf(buf, sizeof(buf), "watch %s", cmd);
                        for (p = 0; p*2 < len && p < 100; ++p) {
                            be += snprintf(buf+be, sizeof(buf)-be, " %d", GetShort_String(data+p*2));
                        }
                        be += snprintf(buf+be, sizeof(buf)-be, "\n");
                    }
                    break;

                    case INT_ARRAY: {
                        int be;
                        int p;

                        be = snprintf(buf, sizeof(buf), "watch %s", cmd);
                        for (p = 0; p*4 < len; ++p) {
                            be += snprintf(buf+be, sizeof(buf)-be, " %d", GetInt_String(data+p*4));
                        }
                        be += snprintf(buf+be, sizeof(buf)-be, "\n");
                    }
                    break;

                    case STATS: {
                        /*
                         * We cheat here and log each stat as a separate command, even
                         * if the server sent a bunch of updates as a single message;
                         * most scripts will be easier to write if they only parse a fixed
                         * format.
                         */
                        int be = 0;
                        while (len) {
                            int c; /* which stat */

                            be += snprintf(buf+be, sizeof(buf)-be, "watch %s", cmd);
                            c = *data;
                            ++data;
                            --len;
                            if (c >= CS_STAT_RESIST_START && c <= CS_STAT_RESIST_END) {
                                be += snprintf(buf+be, sizeof(buf)-be, " resists %d %d\n", c, GetShort_String(data));
                                data += 2;
                                len -= 2;
                            } else if (c >= CS_STAT_SKILLINFO && c < (CS_STAT_SKILLINFO+CS_NUM_SKILLS)) {
                                be += snprintf(buf+be, sizeof(buf)-be, " skill %d %d %" G_GINT64_FORMAT "\n", c, *data, GetInt64_String(data+1));
                                data += 9;
                                len -= 9;
                            } else
                                switch (c) {
                                case CS_STAT_HP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " hp %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_MAXHP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " maxhp %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_SP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " sp %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_MAXSP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " maxsp %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_GRACE:
                                    be += snprintf(buf+be, sizeof(buf)-be, " grace %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_MAXGRACE:
                                    be += snprintf(buf+be, sizeof(buf)-be, " maxgrace %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_STR:
                                    be += snprintf(buf+be, sizeof(buf)-be, " str %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_INT:
                                    be += snprintf(buf+be, sizeof(buf)-be, " int %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_POW:
                                    be += snprintf(buf+be, sizeof(buf)-be, " pow %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_WIS:
                                    be += snprintf(buf+be, sizeof(buf)-be, " wis %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_DEX:
                                    be += snprintf(buf+be, sizeof(buf)-be, " dex %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_CON:
                                    be += snprintf(buf+be, sizeof(buf)-be, " con %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_CHA:
                                    be += snprintf(buf+be, sizeof(buf)-be, " cha %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_EXP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " exp %d\n", GetInt_String(data));
                                    data += 4;
                                    len -= 4;
                                    break;

                                case CS_STAT_EXP64:
                                    be += snprintf(buf+be, sizeof(buf)-be, " exp %" G_GINT64_FORMAT "\n", GetInt64_String(data));
                                    data += 8;
                                    len -= 8;
                                    break;

                                case CS_STAT_LEVEL:
                                    be += snprintf(buf+be, sizeof(buf)-be, " level %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_WC:
                                    be += snprintf(buf+be, sizeof(buf)-be, " wc %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_AC:
                                    be += snprintf(buf+be, sizeof(buf)-be, " ac %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_DAM:
                                    be += snprintf(buf+be, sizeof(buf)-be, " dam %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_ARMOUR:
                                    be += snprintf(buf+be, sizeof(buf)-be, " armour %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_SPEED:
                                    be += snprintf(buf+be, sizeof(buf)-be, " speed %d\n", GetInt_String(data));
                                    data += 4;
                                    len -= 4;
                                    break;

                                case CS_STAT_FOOD:
                                    be += snprintf(buf+be, sizeof(buf)-be, " food %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_WEAP_SP:
                                    be += snprintf(buf+be, sizeof(buf)-be, " weap_sp %d\n", GetInt_String(data));
                                    data += 4;
                                    len -= 4;
                                    break;

                                case CS_STAT_FLAGS:
                                    be += snprintf(buf+be, sizeof(buf)-be, " flags %d\n", GetShort_String(data));
                                    data += 2;
                                    len -= 2;
                                    break;

                                case CS_STAT_WEIGHT_LIM:
                                    be += snprintf(buf+be, sizeof(buf)-be, " weight_lim %d\n", GetInt_String(data));
                                    data += 4;
                                    len -= 4;
                                    break;

                                case CS_STAT_RANGE: {
                                    int rlen = *data;
                                    ++data;
                                    --len;
                                    be += snprintf(buf+be, sizeof(buf)-be, " range %*.*s\n", rlen, rlen, data);
                                    data += rlen;
                                    len -= rlen;
                                    break;
                                }

                                case CS_STAT_TITLE: {
                                    int rlen = *data;
                                    ++data;
                                    --len;
                                    be += snprintf(buf+be, sizeof(buf)-be, " title %*.*s\n", rlen, rlen, data);
                                    data += rlen;
                                    len -= rlen;
                                    break;
                                }

                                default:
                                    be += snprintf(buf+be, sizeof(buf)-be, " unknown %d %d bytes left\n", c, len);
                                    len = 0;
                                }
                        }
                    }
                    break;

                    case MIXED:
                        /* magicmap */
                        /* mapextended */
                        /* item1 item2 */
                        /* upditem */
                        /* image image2 */
                        /* face face1 face2 */
                        /* sound */
                        /* player */
                        /*
                         * If we find that scripts need data from any of the above, we can
                         * write special-case code as with stats.  In the meantime, fall
                         * through and just give a hex dump.  Script writers should not
                         * depend on that data format.
                         */
                    case NODATA:
                    default: {
                        int be;
                        int p;

                        /*we may receive an null data, in which case len has no meaning*/
                        if (!data) {
                            len = 0;
                        }
                        be = snprintf(buf, sizeof(buf), "watch %s %d bytes unparsed:", cmd, len);
                        for (p = 0; p < len && p < 100; ++p) {
                            be += snprintf(buf+be, sizeof(buf)-be, " %02x", data[p]);
                        }
                        be += snprintf(buf+be, sizeof(buf)-be, "\n");
                    }
                    break;
                    }
                write(scripts[i].out_fd, buf, strlen(buf));
            }
        }
    }
}

void script_monitor(const char *command, int repeat, int must_send)
{
    int i;

    /* For each script... */
    for (i = 0; i < num_scripts; ++i) {
        /* Do we send the command? */
        if (scripts[i].monitor) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "monitor %d %d %s\n", repeat, must_send, command);
            write(scripts[i].out_fd, buf, strlen(buf));
        }
    }
}

void script_monitor_str(const char *command)
{
    int i;

    /* For each script... */
    for (i = 0; i < num_scripts; ++i) {
        /* Do we send the command? */
        if (scripts[i].monitor) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "monitor %s\n", command);
            write(scripts[i].out_fd, buf, strlen(buf));
        }
    }
}

void script_tell(const char *params)
{
    int i;
    char *p;

    if (params == NULL) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "Which script do you want to talk to?");
        return;
    }

    /* Local copy for modifications */
    char params_cpy[MAX_BUF]; 
    snprintf(params_cpy, MAX_BUF-1, "%s", params);
    p = strchr(params_cpy, ' ');
    if (p == NULL) {
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "What do you want to tell the script?");
        return;
    }
    while (*p == ' ') {
        *p++ = '\0';
    }

    /* Find the script */
    i = script_by_name(params_cpy);
    if (i < 0) {
        draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT,
                      "No such running script");
        return;
    }

    /* Send the message */
    write(scripts[i].out_fd, "scripttell ", 11);
    write(scripts[i].out_fd, p, strlen(p));
    write(scripts[i].out_fd, "\n", 1);
}

static int script_by_name(const char *name)
{
    int i;
    int l;

    if (name == NULL) {
        return(num_scripts == 1 ? 0 : -1);
    }

    /* Parse script number */
    if (isdigit(*name)) {
        i = atoi(name);
        --i;
        if (i >= 0 && i < num_scripts) {
            return(i);
        }
    }

    /* Parse script name */
    l = 0;
    while (name[l] && name[l] != ' ') {
        ++l;
    }
    for (i = 0; i < num_scripts; ++i) {
        if (strncmp(name, scripts[i].name, l) == 0) {
            return(i);
        }
    }
    return(-1);
}

static void script_dead(int i)
{
    int w;

    /* Release resources */
#ifndef WIN32
    close(scripts[i].in_fd);
    close(scripts[i].out_fd);
#else
    CloseHandle(scripts[i].in_fd);
    CloseHandle(scripts[i].out_fd);
    CloseHandle(scripts[i].process);
#endif
    free(scripts[i].name);
    free(scripts[i].params);
    for (w = 0; w < scripts[i].num_watch; ++w) {
        free(scripts[i].watch[w]);
    }
    free(scripts[i].watch);

#ifndef WIN32
    waitpid(-1, NULL, WNOHANG);
#endif

    /* Move scripts with higher index numbers down one slot */
    if (i < (num_scripts-1)) {
        memmove(&scripts[i], &scripts[i+1], sizeof(scripts[i])*(num_scripts-i-1));
    }

    /* Update our count */
    --num_scripts;
}

static void send_map(int i, int x, int y)
{
    char buf[1024];

    if (!mapdata_contains(x, y)) {
        snprintf(buf, sizeof(buf), "request map %d %d unknown\n", x, y);
        write(scripts[i].out_fd, buf, strlen(buf));
    }
    /*** FIXME *** send more relevant data ***/
    snprintf(buf, sizeof(buf), "request map %d %d  %d %c %c %c %c"
             " smooth %d %d %d heads %d %d %d tails %d %d %d\n",
             x, y, mapdata_cell(x, y)->darkness,
             mapdata_cell(x, y)->need_update ? 'y' : 'n',
             mapdata_cell(x, y)->have_darkness ? 'y' : 'n',
             mapdata_cell(x, y)->need_resmooth ? 'y' : 'n',
             mapdata_cell(x, y)->cleared ? 'y' : 'n',
             mapdata_cell(x, y)->smooth[0], mapdata_cell(x, y)->smooth[1], mapdata_cell(x, y)->smooth[2],
             mapdata_cell(x, y)->heads[0].face, mapdata_cell(x, y)->heads[1].face, mapdata_cell(x, y)->heads[2].face,
             mapdata_cell(x, y)->tails[0].face, mapdata_cell(x, y)->tails[1].face, mapdata_cell(x, y)->tails[2].face
            );
    write(scripts[i].out_fd, buf, strlen(buf));
}

/**
 * Process a single script command from the given script. This function
 * removes the processed command from the buffer when finished.
 * @param i Index of the script to process a command from
 */
static void script_process_cmd(int i) {
    char cmd[1024];
    char *c;

    // Find the length of the command up to the trailing newline.
    int l = strcspn(scripts[i].cmd, "\n") + 1;

    // Copy a single command up until the newline into a buffer.
    g_strlcpy(cmd, scripts[i].cmd, l);

    // If a carriage return is present, trim it out as well.
    char *cr = strchr(cmd, '\r');
    if (cr != NULL) {
        *cr = '\0';
    }

    // Remove a single command from the script command buffer.
    if (l < scripts[i].cmd_count) {
        memmove(scripts[i].cmd, scripts[i].cmd + l, scripts[i].cmd_count - l);
        scripts[i].cmd_count -= l;
    } else {
        scripts[i].cmd_count = 0;
    }

    /*
     * Now the data in scripts[i] is ready for the next read.
     * We have a complete command in cmd[].
     * Process it.
     */
    /*
     * Script commands
     *
     * watch <command type>
     * unwatch <command type>
     * request <data type>
     * issue <repeat> <must_send> <command>
     * localcmd <command> [<params>]
     * draw <color> <text>
     * monitor
     * unmonitor
     */
    if (strncmp(cmd, "sync", 4) == 0) {
        c = cmd+4;
        while (*c && *c != ' ') {
            ++c;
        }
        while (*c == ' ') {
            ++c;
        }
        scripts[i].sync_watch = -1;
        if (isdigit(*c)) {
            scripts[i].sync_watch = atoi(c);
        }
        script_sync(csocket.command_sent - csocket.command_received); /* in case we are already there */
    } else if (strncmp(cmd, "watch", 5) == 0) {
        c = cmd+5;
        while (*c && *c != ' ') {
            ++c;
        }
        while (*c == ' ') {
            ++c;
        }
        c = g_strdup(c);
        scripts[i].watch = g_realloc(scripts[i].watch, (scripts[i].num_watch+1)*sizeof(scripts[i].watch[1]));
        scripts[i].watch[scripts[i].num_watch] = c;
        ++scripts[i].num_watch;
    } else if (strncmp(cmd, "unwatch", 7) == 0) {
        int w;

        c = cmd+7;
        while (*c && *c != ' ') {
            ++c;
        }
        while (*c == ' ') {
            ++c;
        }
        for (w = 0; w < scripts[i].num_watch; ++w) {
            if (strcmp(c, scripts[i].watch[w]) == 0) {
                free(scripts[i].watch[w]);
                while (w+1 < scripts[i].num_watch) {
                    scripts[i].watch[w] = scripts[i].watch[w+1];
                    ++w;
                }
                --scripts[i].num_watch;
                break;
            }
        }
    } else if (strncmp(cmd, "request", 7) == 0) {
        c = cmd+7;
        while (*c && *c != ' ') {
            ++c;
        }
        while (*c == ' ') {
            ++c;
        }
        if (!*c) {
            return;    /* bad request */
        }
        /*
         * Request information from the client's view of the world
         * (mostly defined in client.h)
         *
         * Valid requests:
         *
         *   player       Return the player's tag and title
         *   range        Return the type and name of the currently selected range attack
         *   stat <type>  Return the specified stats
         *   stat stats   Return Str,Con,Dex,Int,Wis,Pow,Cha
         *   stat cmbt    Return wc,ac,dam,speed,weapon_sp
         *   stat hp      Return hp,maxhp,sp,maxsp,grace,maxgrace,food
         *   stat xp      Return level,xp,skill-1 level,skill-1 xp,...
         *   stat resists Return resistances
         *   stat paths   Return spell paths: attuned, repelled, denied.
         *   weight       Return maxweight, weight
         *   flags        Return flags (fire, run)
         *   items inv    Return a list of items in the inventory, one per line
         *   items actv   Return a list of inventory items that are active, one per line
         *   items on     Return a list of items under the player, one per line
         *   items cont   Return a list of items in the open container, one per line
         *   map pos      Return the players x,y within the current map
         *   map near     Return the 3x3 grid of the map centered on the player
         *   map all      Return all the known map information
         *   map <x> <y>  Return the information about square x,y in the current map
         *   skills       Return a list of all skill names, one per line (see also stat xp)
         *   spells       Return a list of known spells, one per line
         */
        if (strncmp(c, "player", 6) == 0) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "request player %d %s\n", cpl.ob->tag, cpl.title);
            write(scripts[i].out_fd, buf, strlen(buf));
        } else if (strncmp(c, "range", 5) == 0) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "request range %s\n", cpl.range);
            write(scripts[i].out_fd, buf, strlen(buf));
        } else if (strncmp(c, "weight", 5) == 0) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "request weight %d %d\n", cpl.stats.weight_limit, (int)(cpl.ob->weight*1000));
            write(scripts[i].out_fd, buf, strlen(buf));
        } else if (strncmp(c, "stat ", 5) == 0) {
            c += 4;
            while (*c && *c != ' ') {
                ++c;
            }
            while (*c == ' ') {
                ++c;
            }
            if (!*c) {
                return;    /* bad request */
            }
            /*
             *   stat stats   Return Str,Con,Dex,Int,Wis,Pow,Cha
             *   stat cmbt    Return wc,ac,dam,speed,weapon_sp
             *   stat hp      Return hp,maxhp,sp,maxsp,grace,maxgrace,food
             *   stat xp      Return level,xp,skill-1 level,skill-1 xp,...
             *   stat resists Return resistances
             */
            if (strncmp(c, "stats", 5) == 0) {
                char buf[1024];

                snprintf(buf, sizeof(buf), "request stat stats %d %d %d %d %d %d %d\n", cpl.stats.Str, cpl.stats.Con, cpl.stats.Dex, cpl.stats.Int, cpl.stats.Wis, cpl.stats.Pow, cpl.stats.Cha);
                write(scripts[i].out_fd, buf, strlen(buf));
            } else if (strncmp(c, "cmbt", 4) == 0) {
                char buf[1024];

                snprintf(buf, sizeof(buf), "request stat cmbt %d %d %d %d %d\n", cpl.stats.wc, cpl.stats.ac, cpl.stats.dam, cpl.stats.speed, cpl.stats.weapon_sp);
                write(scripts[i].out_fd, buf, strlen(buf));
            } else if (strncmp(c, "hp", 2) == 0) {
                char buf[1024];

                snprintf(buf, sizeof(buf), "request stat hp %d %d %d %d %d %d %d\n", cpl.stats.hp, cpl.stats.maxhp, cpl.stats.sp, cpl.stats.maxsp, cpl.stats.grace, cpl.stats.maxgrace, cpl.stats.food);
                write(scripts[i].out_fd, buf, strlen(buf));
            } else if (strncmp(c, "xp", 2) == 0) {
                char buf[1024];
                int s;

                snprintf(buf, sizeof(buf), "request stat xp %d %" G_GINT64_FORMAT, cpl.stats.level, cpl.stats.exp);
                write(scripts[i].out_fd, buf, strlen(buf));
                for (s = 0; s < MAX_SKILL; ++s) {
                    snprintf(buf, sizeof(buf), " %d %" G_GINT64_FORMAT, cpl.stats.skill_level[s], cpl.stats.skill_exp[s]);
                    write(scripts[i].out_fd, buf, strlen(buf));
                }
                write(scripts[i].out_fd, "\n", 1);
            } else if (strncmp(c, "resists", 7) == 0) {
                char buf[1024];
                int s;

                snprintf(buf, sizeof(buf), "request stat resists");
                write(scripts[i].out_fd, buf, strlen(buf));
                for (s = 0; s < 30; ++s) {
                    snprintf(buf, sizeof(buf), " %d", cpl.stats.resists[s]);
                    write(scripts[i].out_fd, buf, strlen(buf));
                }
                write(scripts[i].out_fd, "\n", 1);
            } else if (strncmp(c, "paths", 2) == 0) {
                char buf[1024];

                snprintf(buf, sizeof(buf), "request stat paths %d %d %d\n", cpl.stats.attuned, cpl.stats.repelled, cpl.stats.denied);
                write(scripts[i].out_fd, buf, strlen(buf));
            }
        } else if (strncmp(c, "flags", 5) == 0) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "request flags %d %d %d %d\n", cpl.stats.flags, cpl.fire_on, cpl.run_on, cpl.no_echo);
            write(scripts[i].out_fd, buf, strlen(buf));
        } else if (strncmp(c, "items ", 6) == 0) {
            c += 5;
            while (*c && *c != ' ') {
                ++c;
            }
            while (*c == ' ') {
                ++c;
            }
            if (!*c) {
                return;    /* bad request */
            }
            /*
             *   items inv    Return a list of items in the inventory, one per line
             *   items actv   Return a list of inventory items that are active, one per line
             *   items on     Return a list of items under the player, one per line
             *   items cont   Return a list of items in the open container, one per line
             */
            if (strncmp(c, "inv", 3) == 0) {
                char *buf;
                item *it;

                for (it = cpl.ob->inv; it; it = it->next) {
                    script_send_item(i, "request items inv ", it);
                }
                buf = "request items inv end\n";
                write(scripts[i].out_fd, buf, strlen(buf));
            }
            if (strncmp(c, "actv", 4) == 0) {
                char *buf;
                item *it;

                for (it = cpl.ob->inv; it; it = it->next) {
                    if (it->applied) {
                        script_send_item(i, "request items actv ", it);
                    }
                }
                buf = "request items actv end\n";
                write(scripts[i].out_fd, buf, strlen(buf));
            }
            if (strncmp(c, "on", 2) == 0) {
                char *buf;
                item *it;

                for (it = cpl.below->inv; it; it = it->next) {
                    script_send_item(i, "request items on ", it);
                }
                buf = "request items on end\n";
                write(scripts[i].out_fd, buf, strlen(buf));
            }
            if (strncmp(c, "cont", 4) == 0) {
                char *buf;
                item *it;

                if (cpl.container) {
                    for (it = cpl.container->inv; it; it = it->next) {
                        script_send_item(i, "request items cont ", it);
                    }
                }
                buf = "request items cont end\n";
                write(scripts[i].out_fd, buf, strlen(buf));
            }
        } else if (strncmp(c, "map ", 4) == 0) {
            int x, y;

            c += 3;
            while (*c && *c != ' ') {
                ++c;
            }
            while (*c == ' ') {
                ++c;
            }
            if (!*c) {
                return;    /* bad request */
            }
            /*
             *   map pos      Return the players x,y within the current map
             *   map near     Return the 3x3 grid of the map centered on the player
             *   map all      Return all the known map information
             *   map <x> <y>  Return the information about square x,y in the current map
             */
            if (strncmp(c, "pos", 3) == 0) {
                char buf[1024];

                snprintf(buf, sizeof(buf), "request map pos %d %d\n", pl_pos.x+use_config[CONFIG_MAPWIDTH]/2, pl_pos.y+use_config[CONFIG_MAPHEIGHT]/2);
                write(scripts[i].out_fd, buf, strlen(buf));
            } else if (strncmp(c, "near", 4) == 0) {
                for (y = 0; y < 3; ++y)
                    for (x = 0; x < 3; ++x)
                        send_map(i,
                                 x+pl_pos.x+use_config[CONFIG_MAPWIDTH]/2-1,
                                 y+pl_pos.y+use_config[CONFIG_MAPHEIGHT]/2-1
                                );
            } else if (strncmp(c, "all", 3) == 0) {
                char *endmsg = "request map end\n";
                int sizex, sizey;

                mapdata_size(&sizex, &sizey);

                for (y = 0; y < sizey; y++) {
                    for (x = 0; x < sizex; x++) {
                        send_map(i, x, y);
                    }
                }

                write(scripts[i].out_fd, endmsg, strlen(endmsg));
            } else {
                while (*c && !isdigit(*c)) {
                    ++c;
                }
                if (!*c) {
                    return;    /* No x specified */
                }
                x = atoi(c);
                while (*c && *c != ' ') {
                    ++c;
                }
                while (*c && !isdigit(*c)) {
                    ++c;
                }
                if (!*c) {
                    return;    /* No y specified */
                }
                y = atoi(c);
                send_map(i, x, y);
            }
        } else if (strncmp(c, "skills", 6) == 0) {
            char buf[1024];
            int s;

            for (s = 0; s < CS_NUM_SKILLS; s++) {
                if (skill_names[s]) {
                    snprintf(buf, sizeof(buf), "request skills %d %s\n", CS_STAT_SKILLINFO + s, skill_names[s]);
                    write(scripts[i].out_fd, buf, strlen(buf));
                }
            }
            snprintf(buf, sizeof(buf), "request skills end\n");
            write(scripts[i].out_fd, buf, strlen(buf));
        } else if (strncmp(c, "spells", 6) == 0) {
            char buf[1024];
            Spell *spell;

            for (spell = cpl.spelldata; spell; spell = spell->next) {
                snprintf(buf, sizeof(buf), "request spells %d %d %d %d %d %d %d %d %s\n",
                        spell->tag, spell->level, spell->sp, spell->grace,
                        spell->skill_number, spell->path, spell->time,
                        spell->dam, spell->name);
                write(scripts[i].out_fd, buf, strlen(buf));
            }
            snprintf(buf, sizeof(buf), "request spells end\n");
            write(scripts[i].out_fd, buf, strlen(buf));
        } else {
            char buf[1024];

            snprintf(buf, sizeof(buf), "Script %d %s malfunction; unimplemented request:", i+1, scripts[i].name);
            draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
            draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, cmd);
        }
    } else if (strncmp(cmd, "issue", 5) == 0) {
        int repeat;
        int must_send;

        c = cmd+5;
        while (*c && *c == ' ') {
            ++c;
        }
        if (*c && (isdigit(*c) || *c == '-')) { /* repeat specified; use send_command() */
            repeat = atoi(c);
            while (*c && *c != ' ') {
                ++c;
            }
            while (*c && !isdigit(*c) && *c != '-') {
                ++c;
            }
            if (!*c) {
                return;    /* No must_send specified */
            }
            must_send = atoi(c);
            while (*c && *c != ' ') {
                ++c;
            }
            if (!*c) {
                return;    /* No command specified */
            }
            while (*c == ' ') {
                ++c;
            }
            if (repeat != -1) {
                int r;

                r = send_command(c, repeat, must_send);
                if (r != 1) {
                    char buf[1024];

                    snprintf(buf, sizeof(buf), "Script %d %s malfunction; command not sent", i+1, scripts[i].name);
                    draw_ext_info(
                        NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
                    draw_ext_info(
                        NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, cmd);
                }
            }
        } else {
            c = cmd+5;
            while (*c && *c != ' ') {
                ++c;
            }
            while (*c == ' ') {
                ++c;
            }

            /*
             * Check special cases: "mark <tag>" or "lock <new state> <tag>"
             */
            if (strncmp(c, "mark", 4) == 0) {
                int tag;
                SockList sl;
                guint8 buf[MAX_BUF];

                c += 4;

                while (*c && !isdigit(*c)) {
                    ++c;
                }
                if (!*c) {
                    return;    /* No tag specified */
                }
                tag = atoi(c);

                SockList_Init(&sl, buf);
                SockList_AddString(&sl, "mark ");
                SockList_AddInt(&sl, tag);
                SockList_Send(&sl, csocket.fd);
            } else if (strncmp(c, "lock", 4) == 0) {
                int tag, locked;
                SockList sl;
                guint8 buf[MAX_BUF];

                c += 4;

                while (*c && !isdigit(*c)) {
                    ++c;
                }
                if (!*c) {
                    return;    /* No state specified */
                }
                locked = atoi(c);
                while (*c && *c != ' ') {
                    ++c;
                }
                while (*c && !isdigit(*c)) {
                    ++c;
                }
                if (!*c) {
                    return;    /* No tag specified */
                }
                tag = atoi(c);

                SockList_Init(&sl, buf);
                SockList_AddString(&sl, "lock ");
                SockList_AddChar(&sl, locked);
                SockList_AddInt(&sl, tag);
                SockList_Send(&sl, csocket.fd);
            } else {
                cs_print_string(csocket.fd, "%s", c);
            }
        }
    } else if (strncmp(cmd, "localcmd", 8) == 0) {
        char *param;

        c = cmd+8;
        while (*c == ' ') {
            c++;
        }
        param = c;
        while ((*param != '\0') && (*param != ' ')) {
            param++;
        }
        if (*param == ' ') {
            *param = '\0';
            param++;
        } else {
            param = NULL;
        }

        if (!handle_local_command(c, param)) {
            char buf[1024];

            snprintf(buf, sizeof(buf), "Script %s malfunction; localcmd not understood", scripts[i].name);
            draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
            snprintf(buf, sizeof(buf), "Script <<localcmd %s %s>>", c, (param == NULL) ? "" : param);
            draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
        }
    } else if (strncmp(cmd, "draw", 4) == 0) {
        int color;

        c = cmd+4;
        while (*c && !isdigit(*c)) {
            ++c;
        }
        if (!*c) {
            return;    /* No color specified */
        }
        color = atoi(c);
        while (*c && *c != ' ') {
            ++c;
        }
        if (!*c) {
            return;    /* No message specified */
        }
        while (*c == ' ') {
            ++c;
        }
        draw_ext_info(color, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, c);
    } else if (strncmp(cmd, "monitor", 7) == 0) {
        scripts[i].monitor = 1;
    } else if (strncmp(cmd, "unmonitor", 9) == 0) {
        scripts[i].monitor = 0;
    } else {
        char buf[1024];

        snprintf(buf, sizeof(buf), "Script %d %s malfunction; invalid command:", i+1, scripts[i].name);
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, buf);
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SCRIPT, cmd);
    }
}

/*
 * script_send_item()
 *
 * Send one line to the script with item information.
 *
 * A header string is passed in.  The format is:
 *
 * <header>  tag num weight flags type name
 *
 * flags are a bitmask:
 * unidentified, magic, cursed, damned, unpaid, locked, applied, open, was_open, inv_updated
 *      512        256     128     64      32       16      8       4      2         1
 */
static void script_send_item(int i, const char *head, const item *it)
{
    char buf[4096];
    int flags;

    flags = it->flagsval&F_UNIDENTIFIED?1:0;
    flags = (flags<<1)|it->magical;
    flags = (flags<<1)|it->cursed;
    flags = (flags<<1)|it->damned;
    flags = (flags<<1)|it->unpaid;
    flags = (flags<<1)|it->locked;
    flags = (flags<<1)|it->applied;
    flags = (flags<<1)|it->open;
    flags = (flags<<1)|it->was_open;
    flags = (flags<<1)|it->inv_updated;
    snprintf(buf, sizeof(buf), "%s%d %d %d %d %d %s\n", head, it->tag, it->nrof, (int)(it->weight*1000+0.5), flags, it->type, it->d_name);
    write(scripts[i].out_fd, buf, strlen(buf));
}
