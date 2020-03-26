/*
 * Crossfire -- cooperative multi-player graphical RPG and adventure game
 *
 * Copyright (c) 1999-2013 Mark Wedel and the Crossfire Development Team
 * Copyright (c) 1992 Frank Tore Johansen
 *
 * Crossfire is free software and comes with ABSOLUTELY NO WARRANTY. You are
 * welcome to redistribute it under certain conditions. For details, see the
 * 'LICENSE' and 'COPYING' files.
 *
 * The authors can be reached via e-mail to crossfire-devel@real-time.com
 */

/**
 * @file
 * Text-drawing functions for the message window
 */

#include "client.h"

#include <gtk/gtk.h>

#include "image.h"
#include "main.h"
#include "gtk2proto.h"

/** Color names set by the user in the gtkrc file. */
static const char *const usercolorname[NUM_COLORS] = {
    "black",                /* 0  */
    "white",                /* 1  */
    "darkblue",             /* 2  */
    "red",                  /* 3  */
    "orange",               /* 4  */
    "lightblue",            /* 5  */
    "darkorange",           /* 6  */
    "green",                /* 7  */
    "darkgreen",            /* 8  *//* Used for window background color */
    "grey",                 /* 9  */
    "brown",                /* 10 */
    "yellow",               /* 11 */
    "tan"                   /* 12 */
};

/**
 * A mapping of font numbers to style based on the rcfile content.
 */
static const char *font_style_names[NUM_FONTS] = {
    "info_font_normal",
    "info_font_arcane",
    "info_font_strange",
    "info_font_fixed",
    "info_font_hand"
};
/**
 * @} EndOf GTK V2 Font Style Definitions.
 */

/**
 * The number of supported message panes (normal + critical).  This define is
 * meant to support anything that iterates over all the information panels.
 * It does nothing to help remove or document hardcoded panel numbers
 * throughout the code.
 *
 * @todo Create defines for each panel and replace panel numbers with the
 * defines describing the panel.  This integer declaration is to that
 * account.c knows how many are being used here, and can add appropriately.
 */
#define NUM_TEXT_VIEWS  2

extern  const char * const usercolorname[NUM_COLORS];

Info_Pane info_pane[NUM_TEXT_VIEWS];

extern  const char * const colorname[NUM_COLORS];

/*
 * The idea behind the msg_type_names is to provide meaningful names that the
 * client can use to load/save these values, in particular, the GTK-V2 client
 * uses these to find styles on how to draw the different msg types.  We could
 * set this up as a two dimension array instead, but that probably is not as
 * efficient when the number of subtypes varies so wildly.  The 0 subtypes are
 * used for general cases (describe the entire class of those message types).
 * Note also that the names here are verbose - the actual code that uses these
 * will expand it further.  In practice, there should never be entries with
 * both the same type/subtype (each subtype should be unique) - if so, the
 * results are probably unpredictable on which one the code would use.
 */
#include "msgtypes.h"

static int max_subtype=0, has_style=0;

/**
 * @{
 * @name GTK V2 Message Control System.
 * Supports a client-side implementation of what used to be provided by the
 * server output-count and output-sync commands.  These defines presently
 * control the way the system works.  The hardcoded values here are temporary
 * and shall give way to client commands and/or a GUI method of configuring
 * the system.  Turn off the output count/sync by setting MESSAGE_COUNT_MAX
 * to 1.  It should be safe to experiment with most values as long as none of
 * them are set less than 1, and as long as the two buffer sizes are set to
 * reasonable values (buffer sizes include the terminating null character).
 */

static void
message_callback(int orig_color, int type, int subtype, char *message);

GtkWidget *msgctrl_window;              /**< The message control dialog
                                         *   where routing and buffer
                                         *   configuration is set up.
                                         */
GtkWidget *msgctrl_table;               /**< The message control table
                                         *   where routing and buffer
                                         *   configuration is set up.
                                         */
#define MESSAGE_BUFFER_COUNT 10         /**< The maximum number of messages
                                         *   to concurrently monitor for
                                         *   duplicate occurances.
                                         */
#define MESSAGE_BUFFER_SIZE  56         /**< The maximum allowable size of
                                         *   messages that are checked for
                                         *   duplicate reduction.
                                         */
#define COUNT_BUFFER_SIZE    16         /**< The maximum size of the tag
                                         *   that indicates the number of
                                         *   times a message occured while
                                         *   buffered.  Example: "4 times "
                                         */
#define MESSAGE_COUNT_MAX    16         /**< The maximum number of times a
                                         *   buffered message may repeat
                                         *   before it is sent to a client
                                         *   panel for for display.
                                         */
#define MESSAGE_AGE_MAX      16         /**< The maximum time in client
                                         *   ticks, that a message resides in
                                         *   a buffer before it is sent to a
                                         *   client panel for display.  8
                                         *   ticks is roughly 1 second.
                                         */
/** @struct info_buffer_t
  * @brief A buffer record that supports suppression of duplicate messages.
  * This buffer holds data for messages that are monitored for suppression of
  * duplicates.  The buffer holds all data passed to message_callback(),
  * including type, subtype, suggested color, and the text.  Age and count
  * fields are provided to track the time a message is in the buffer, and how
  * many times it occured during the time it is buffered.
  */
struct info_buffer_t {
    int  age;                             /**< The length of time a message
                                         *   spends in the buffer, measured in
                                         *   client ticks.
                                         */
    int  count;                           /**< The number of times a buffered
                                         *   message is detected while it is
                                         *   buffered.  A count of -1
                                         *   indicates the buffer is empty.
                                         */
    int  orig_color;                      /**< Message data:  The suggested
                                         *   color to display the text in.
                                         */
    int  type;                            /**< Message data:  Classification
                                         *   of the buffered message.
                                         */
    int  subtype;                         /**< Message data:  Sub-class of
                                         *   the buffered message.
                                         */
    char message[MESSAGE_BUFFER_SIZE];    /**< Message data:  Message text.
                                         */
} info_buffer[MESSAGE_BUFFER_COUNT];    /**< Several buffers that support
                                         *   suppression of duplicates even
                                         *   even when the duplicates are
                                         *   alternate with other messages.
                                         */
/** @struct buffer_parameter_t
 *  @brief A container for a single buffer control parameter like output count
 *  or time.  The structure holds a widget pointer, a state variable to track
 *  the widget value, and a default value.
 */
typedef struct {
    GtkWidget* ptr;                       /**< Spinbutton widget pointer.
                                         */
    guint state;                          /**< The state of the spinbutton.
                                         */
    const guint default_state;            /**< The state of the spinbutton.
                                         */
} buffer_parameter_t;

/** @struct buffer_control_t
 *  @brief A container for all of the buffer control parameters like output
 *  count and time.  The structure holds widget pointers, a state variables to
 *  track the parameter values, and the client built-in defaults.  Only the
 *  final initializer for output_count and output_time is used as a default.
 */
struct buffer_control_t {
    buffer_parameter_t count;             /**< Output count control & default */
    buffer_parameter_t timer;             /**< Output time control & default  */
} buffer_control = {
    /*
     * { uninitialized_pointer, uninitialized_state, default_value     },
     */
    { NULL,                  0,                   MESSAGE_COUNT_MAX },
    { NULL,                  0,                   MESSAGE_AGE_MAX   }
};
/** @struct boolean_widget_t
 *  @brief A container that holds the pointer and state of a checkbox control.
 *  Each Message Control dialog checkbox is tracked in one of these structs.
 */
typedef struct {
    GtkWidget* ptr;                       /**< Checkbox widget pointer.
                                         */
    gboolean state;                       /**< The state of the checkbox.
                                         */
} boolean_widget_t;

/** @struct message_control_t
 *  @brief A container for all of the checkboxes associated with a single
 *  message type.
 */
typedef struct {
    boolean_widget_t buffer;              /**< Checkbox widget and state for a
                                         *   single message type.
                                         */
    boolean_widget_t pane[NUM_TEXT_VIEWS];/**< Checkbox widgets and state for
                                         *   each client-supported message
                                         *   panel.
                                         */
} message_control_t;

message_control_t
msgctrl_widgets[MSG_TYPE_LAST-1];   /**< All of the checkbox widgets for
                                         *   the entire message control
                                         *   dialog.
                                         */
/** @struct msgctrl_data_t
 *  @brief Descriptive message type names with pane routing and buffer enable.
 *  A single struct defines a hard-coded, player-friendly, descriptive name to
 *  use for a single message type.  All other fields in the structure define
 *  routing of messages to either or both client message panels, and whether
 *  or not messages of this type are passed through the duplicate suppression
 *  buffer system.  This struct is intended to be used as the base type of an
 *  array that contains one struct per message type defined in newclient.h.
 *  The hard-coding of the descriptive name for the message type here is not
 *  ideal as it would be nicer to have it alongside the MSG_TYPE_*  defines.
 */
struct msgctrl_data_t {
    const char * description;             /**< A descriptive name to give to
                                         *   a message type when displaying it
                                         *   for a player.  These values
                                         *   should be kept in sync with the
                                         *   MSG_TYPE_* declarations in
                                         *   ../../common/shared/newclient.h
                                         */
    const gboolean buffer;                /**< Whether or not to consider the
                                         *   message type for output-count
                                         *   buffering.  0/1 == disable/enable
                                         *   duplicate suppression
                                         *   (output-count).
                                         */
    const gboolean pane[NUM_TEXT_VIEWS];  /**< The routing instructions for a
                                         *   single message type.  For each
                                         *   pane, 0/1 == disable/enable
                                         *   display of the message type in
                                         *   the associated client message
                                         *   pane.
                                         */
} msgctrl_defaults[MSG_TYPE_LAST-1] =   /**< A data structure to track how
                                         *   to handle each message type in
                                         *   with respect to panel routing and
                                         *   output count.
                                         */

{
    /*
     * { "description",                    buffer, {  pane[0], pane[1] } },
     */
    { "Books",                           FALSE, {     TRUE,   FALSE } },
    { "Cards",                           FALSE, {     TRUE,   FALSE } },
    { "Paper",                           FALSE, {     TRUE,   FALSE } },
    { "Signs & Magic Mouths",            FALSE, {     TRUE,   FALSE } },
    { "Monuments",                       FALSE, {     TRUE,   FALSE } },
    { "Dialogs (Altar/NPC/Magic Ear)"  , FALSE, {     TRUE,   FALSE } },
    { "Message of the day",              FALSE, {     TRUE,   FALSE } },
    { "Administrative",                  FALSE, {     TRUE,   FALSE } },
    { "Shops",                            TRUE, {     TRUE,   FALSE } },
    { "Command responses",               FALSE, {     TRUE,   FALSE } },
    { "Changes to attributes",            TRUE, {     TRUE,    TRUE } },
    { "Skill-related messages",           TRUE, {     TRUE,   FALSE } },
    { "Apply results",                    TRUE, {     TRUE,   FALSE } },
    { "Attack results",                   TRUE, {     TRUE,   FALSE } },
    { "Player communication",            FALSE, {     TRUE,    TRUE } },
    { "Spell results",                    TRUE, {     TRUE,   FALSE } },
    { "Item information",                 TRUE, {     TRUE,   FALSE } },
    { "Miscellaneous",                    TRUE, {     TRUE,   FALSE } },
    { "Victim notification",             FALSE, {     TRUE,    TRUE } },
    { "Client-generated messages",       FALSE, {     TRUE,   FALSE } }
};
/**
 * @} EndOf GTK V2 Message Control System.
 */

/**
 * Sets attributes in the text tag from a style.  Best I can gather, there is
 * no way to take all of the attributes from a style and apply them directly to
 * a text tag, hence this function to do the work.  GtkTextTags also know what
 * attributes are set and which are not set - thus, you can apply multiple tags
 * to the same text, and get all of the effects.  For styles, that isn't the
 * case - a style contains all of the information.  So this function also
 * compares the loaded style from the base style, and only sets the attributes
 * that are different.
 *
 * @param tag        Text tag to set values on.
 * @param style      Style name to get values from.
 * @param base_style Base style for the widget to compare against.
 */
void set_text_tag_from_style(GtkTextTag *tag, GtkStyle *style, const GtkStyle * const base_style)
{
    g_object_set(tag, "foreground-set", FALSE, NULL);
    g_object_set(tag, "background-set", FALSE, NULL);
    g_object_set(tag, "font-desc", NULL, NULL);

    if (memcmp(
                &style->fg[GTK_STATE_NORMAL],
                &base_style->fg[GTK_STATE_NORMAL],
                sizeof(GdkColor)))

    {
        g_object_set(tag, "foreground-gdk", &style->fg[GTK_STATE_NORMAL], NULL);
    }

    if (memcmp(
                &style->bg[GTK_STATE_NORMAL],
                &base_style->bg[GTK_STATE_NORMAL],
                sizeof(GdkColor)))

    {
        g_object_set(tag, "background-gdk", &style->bg[GTK_STATE_NORMAL], NULL);
    }

    if (style->font_desc != base_style->font_desc) {
        g_object_set(tag, "font-desc", style->font_desc, NULL);
    }
}

/**
 * Adds the various tags to the next buffer.  If textbuf is non-null, then it
 * also sets the text buffer for that pane to textbuf.  This is called right
 * now by info_get_styles() below and from within the account code.
 *
 * @param pane     Message panel number to add buffer to.
 * @param textbuf  Text buffer to apply tags to.  It is allowed to be null if
 *                 info_pane[pane].textbuffer has already been set.
 */
void add_tags_to_textbuffer(Info_Pane *pane, GtkTextBuffer *textbuf)
{
    int i;

    if (textbuf) {
        pane->textbuffer = textbuf;
    }

    for (i = 0; i < MSG_TYPE_LAST; i++)
        pane->msg_type_tags[i] =
            calloc(max_subtype + 1, sizeof(GtkTextTag*));

    for (i = 0; i < NUM_FONTS; i++) {
        pane->font_tags[i] = NULL;
    }

    for (i = 0; i < NUM_COLORS; i++) {
        pane->color_tags[i] = NULL;
    }
    /*
     * These tag definitions never change - we don't get them from the
     * settings file (maybe we should), so we only need to allocate them once.
     */
    pane->bold_tag =
        gtk_text_buffer_create_tag(pane->textbuffer,
                                   "bold", "weight", PANGO_WEIGHT_BOLD, NULL);

    pane->italic_tag =
        gtk_text_buffer_create_tag(pane->textbuffer,
                                   "italic", "style", PANGO_STYLE_ITALIC, NULL);

    pane->underline_tag =
        gtk_text_buffer_create_tag(pane->textbuffer,
                                   "underline", "underline", PANGO_UNDERLINE_SINGLE, NULL);
    /*
     * This is really a convenience - so multiple tags may be passed when
     * drawing text, but once a NULL tag is found, that signifies no more
     * tags.  Rather than having to set up an array to pass in, instead, an
     * empty tag is passed in so that calling semantics remain consistent,
     * just differing in what tags are passed in.
     */
    if (!pane->default_tag)
        pane->default_tag =
            gtk_text_buffer_create_tag(pane->textbuffer, "default", NULL);
}

/**
 * This is like add_tags_to_textbuffer above, but styles can be changed during
 * the run of the client.  So this has to be separate to note it it might be a
 * reload.
 *
 * @param pane       Message panel number to update.
 * @param base_style Base style if retrieved - may be null.
 */
void add_style_to_textbuffer(Info_Pane *pane, GtkStyle *base_style)
{
    int i;
    char    style_name[MAX_BUF];
    GtkStyle    *tmp_style;

    if (base_style) {
        /*
         * Old message/color support.
         */
        for (i = 0; i < NUM_COLORS; i++) {
            snprintf(style_name, MAX_BUF, "info_%s", usercolorname[i]);

            tmp_style =
                gtk_rc_get_style_by_paths(
                    gtk_settings_get_default(), NULL, style_name, G_TYPE_NONE);

            if (tmp_style) {
                if (!pane->color_tags[i]) {
                    pane->color_tags[i] =
                        gtk_text_buffer_create_tag(
                            pane->textbuffer, NULL, NULL);
                }
                set_text_tag_from_style(
                    pane->color_tags[i], tmp_style, base_style);
            } else {
                if (pane->color_tags[i]) {
                    gtk_text_tag_table_remove(
                        gtk_text_buffer_get_tag_table(
                            pane->textbuffer), pane->color_tags[i]);
                    pane->color_tags[i] = NULL;
                }
            }
        }

        /* Font type support */
        for (i = 0; i < NUM_FONTS; i++) {
            tmp_style =
                gtk_rc_get_style_by_paths(
                    gtk_settings_get_default(),
                    NULL, font_style_names[i], G_TYPE_NONE);

            if (tmp_style) {
                if (!pane->font_tags[i]) {
                    pane->font_tags[i] =
                        gtk_text_buffer_create_tag(
                            pane->textbuffer, NULL, NULL);
                }
                set_text_tag_from_style(
                    pane->font_tags[i], tmp_style, base_style);
            } else {
                if (pane->font_tags[i]) {
                    gtk_text_tag_table_remove(
                        gtk_text_buffer_get_tag_table(pane->textbuffer),
                        pane->font_tags[i]);
                    pane->font_tags[i] = NULL;
                }
            }
        }
    } else {

        for (i = 0; i < NUM_COLORS; i++) {
            if (pane->color_tags[i]) {
                gtk_text_tag_table_remove(
                    gtk_text_buffer_get_tag_table(
                        pane->textbuffer), pane->color_tags[i]);
                pane->color_tags[i] = NULL;
            }
        }
        /* Font type support */
        for (i = 0; i < NUM_FONTS; i++) {
            if (pane->font_tags[i]) {
                gtk_text_tag_table_remove(
                    gtk_text_buffer_get_tag_table(
                        pane->textbuffer), pane->font_tags[i]);
                pane->font_tags[i] = NULL;
            }
        }
    }
}

/**
 * Loads up values from the style file.  Note that the actual name of the
 * style file is set elsewhere.
 *
 * This function is designed so that it should be possible to call it multiple
 * times - it will release old style data and load up new values.  In this
 * way, a user should be able to change styles on the fly and have things
 * work.
 */
void info_get_styles(void)
{
    unsigned int i, j;
    static int has_init=0;
    GtkStyle    *tmp_style, *base_style;

    if (!has_init) {
        /*
         * We want to set up a 2 dimensional array of msg_type_tags to
         * correspond to all the types/subtypes, so looking up any value is
         * really easy.  We know the size of the types, but don't know the
         * number of subtypes - no single declared value.  So we just parse
         * the msg_type_names to find that, then know how big to make the
         * other dimension.  We could allocate different number of entries for
         * each type, but that makes processing a bit harder (no single value
         * on the number of subtypes), and this extra memory usage shouldn't
         * really be at all significant.
         */
        for (i = 0; i < sizeof(msg_type_names) / sizeof(Msg_Type_Names); i++) {
            if (msg_type_names[i].subtype > max_subtype) {
                max_subtype = msg_type_names[i].subtype;
            }
        }
        for (j = 0; j < NUM_TEXT_VIEWS; j++) {
            add_tags_to_textbuffer(&info_pane[j], NULL);

        }
        has_init = 1;
    }
    base_style = gtk_rc_get_style_by_paths(gtk_settings_get_default(), NULL,
                                           "info_default", G_TYPE_NONE);
    if (!base_style) {
        LOG(LOG_INFO, "info.c::info_get_styles",
            "Unable to find base style info_default"
            " - will not process most info tag styles!");
    }

    has_style = 0;

    /*
     * If we don't have a base style tag, we can't process these other tags,
     * as we need to be able to do a difference, and doing a difference from
     * nothing (meaning, taking everything in style) still doesn't work really
     * well.
     */
    if (base_style) {
        /*
         * This processes the type/subtype styles.  We look up the names in
         * the array to find what name goes to what number.
         */
        for (i = 0; i < sizeof(msg_type_names) / sizeof(Msg_Type_Names); i++) {
            int type, subtype;

            char style_name[MAX_BUF];
            snprintf(style_name, sizeof(style_name),
                     "msg_%s", msg_type_names[i].style_name);
            type =  msg_type_names[i].type;
            subtype = msg_type_names[i].subtype;

            tmp_style =
                gtk_rc_get_style_by_paths(
                    gtk_settings_get_default(), NULL, style_name, G_TYPE_NONE);

            for (j = 0; j < NUM_TEXT_VIEWS; j++) {
                /*
                 * If we have a style for this, update the tag that goes along
                 * with this.  If we don't have a tag for this style, create
                 * it.
                 */
                if (tmp_style) {
                    if (!info_pane[j].msg_type_tags[type][subtype]) {
                        info_pane[j].msg_type_tags[type][subtype] =
                            gtk_text_buffer_create_tag(
                                info_pane[j].textbuffer, NULL, NULL);
                    }
                    set_text_tag_from_style(
                        info_pane[j].msg_type_tags[type][subtype],
                        tmp_style, base_style);
                    has_style = 1;
                } else {
                    /*
                     * No setting for this type/subtype, so remove tag if
                     * there is one.
                     */
                    if (info_pane[j].msg_type_tags[type][subtype]) {
                        gtk_text_tag_table_remove(
                            gtk_text_buffer_get_tag_table(
                                info_pane[j].textbuffer),
                            info_pane[j].msg_type_tags[type][subtype]);
                        info_pane[j].msg_type_tags[type][subtype] = NULL;
                    }
                }
            }
        }
        for (j = 0; j < NUM_TEXT_VIEWS; j++) {
            add_style_to_textbuffer(&info_pane[j], base_style);
        }
    } else {
        /*
         * There is no base style - this should not normally be the case with
         * any real setting files, but certainly can be the case if the user
         * selected the 'None' setting.  So in this case, we just free all the
         * text tags.
         */
        has_style = 0;
        for (i = 0; i < sizeof(msg_type_names) / sizeof(Msg_Type_Names); i++) {
            int type, subtype;

            type = msg_type_names[i].type;
            subtype = msg_type_names[i].subtype;

            for (j = 0; j < NUM_TEXT_VIEWS; j++) {
                if (info_pane[j].msg_type_tags[type][subtype]) {
                    gtk_text_tag_table_remove(
                        gtk_text_buffer_get_tag_table(
                            info_pane[j].textbuffer),
                        info_pane[j].msg_type_tags[type][subtype]);
                    info_pane[j].msg_type_tags[type][subtype] = NULL;
                }
            }
        }
        for (j = 0; j < NUM_TEXT_VIEWS; j++) {
            add_style_to_textbuffer(&info_pane[j], NULL);
        }
    }
}

/**
 * Initialize the information panels in the client.  These panels are the
 * client areas where text is drawn.
 *
 * @param window_root Pointer to the parent (root) application window.
 */
void info_init(GtkWidget *window_root)
{
    int i;
    GtkTextIter end;
    char    widget_name[MAX_BUF];

    for (i = 0; i < NUM_TEXT_VIEWS; i++) {
        snprintf(widget_name, MAX_BUF, "textview_info%d", i+1);
        info_pane[i].textview =
            GTK_WIDGET(gtk_builder_get_object(window_xml, widget_name));

        snprintf(widget_name, MAX_BUF, "scrolledwindow_textview%d", i+1);

        info_pane[i].scrolled_window =
            GTK_WIDGET(gtk_builder_get_object(window_xml, widget_name));

        gtk_text_view_set_wrap_mode(
            GTK_TEXT_VIEW(info_pane[i].textview), GTK_WRAP_WORD);

        info_pane[i].textbuffer =
            gtk_text_view_get_buffer(GTK_TEXT_VIEW(info_pane[i].textview));

        info_pane[i].adjustment =
            gtk_scrolled_window_get_vadjustment(
                GTK_SCROLLED_WINDOW(info_pane[i].scrolled_window));

        gtk_text_buffer_get_end_iter(info_pane[i].textbuffer, &end);

        info_pane[i].textmark =
            gtk_text_buffer_create_mark(
                info_pane[i].textbuffer, NULL, &end, FALSE);

        gtk_widget_realize(info_pane[i].textview);
    }

    /*info_get_styles();*/
    info_buffer_init();

    /* Register callbacks for all message types */
    for (i = 0; i < MSG_TYPE_LAST; i++) {
        setTextManager(i, message_callback);
    }
}

/**
 * Adds some data to the text buffer of the specified information panel using
 * the appropriate tags to provide the desired formatting.  Note that the
 * style within the users theme determines how a particular type/subtype is
 * drawn.
 *
 * @param pane      The client message panel to write a message to.
 * @param message   A pointer to some text to show in a client message window.
 * @param type      The message type - see the MSG_TYPE values in newclient.h.
 * @param subtype   Message subtype - see MSG_TYPE_*_* values in newclient.h.
 * @param bold      If true, should be in bold text.
 * @param italic    If true, should be in italic text
 * @param font      Which font to use - resolved to an actual font style based
 *                  on the user's theme file.
 * @param color     String name of the color.
 * @param underline If true, draw underlined text.
 */
static void add_to_textbuf(Info_Pane *pane, const char *message,
                           int type, int subtype,
                           int bold, int italic,
                           int font, const char *color, int underline)
{
    GtkTextIter end;
    GdkRectangle rect;
    int scroll_to_end=0, color_num;
    GtkTextTag      *color_tag=NULL, *type_tag=NULL;

    /*
     * Lets see if the defined color matches any of our defined colors.  If we
     * get a match, set color_tag.  If color_tag is null, we either don't have
     * a match, we don't have a defined tag for the color, or we don't have a
     * color, use the default tag.  It would be nice to know if color is a sub
     * value set with [color] tag, or is part of the message itself - if we're
     * just being passed NDI_RED in the draw_ext_info from the server, we
     * really don't care about that - the type/subtype styles should really be
     * what determines what color to use.
     */
    if (color) {
        for (color_num = 0; color_num < NUM_COLORS; color_num++)
            if (!g_ascii_strcasecmp(usercolorname[color_num], color)) {
                break;
            }
        if (color_num < NUM_COLORS) {
            color_tag = pane->color_tags[color_num];
        }
    }

    if (!color_tag) {
        color_tag = pane->default_tag;
    }

    /*
     * Following block of code deals with the type/subtype.  First, we check
     * and make sure the passed in values are legal.  If so, first see if
     * there is a particular style for the type/subtype combo, if not, fall
     * back to one just for the type.
     */
    type_tag = pane->default_tag;

    /* Clear subtype on MSG_TYPE_CLIENT if max_subtype is not set
     * Errors are generated during initialization, before max_subtype
     * has been set, so we can not route to a specific pane.
     * We also want to make sure we do not hit the pane->msg_type_tags
     * code, as that is not initialzed yet.
     */
    if (type == MSG_TYPE_CLIENT && !max_subtype) {
        /* We would set subtype = 0, but that'd be a dead assignment. */
    } else if (type >= MSG_TYPE_LAST
               || subtype >= max_subtype
               || type < 0 || subtype < 0 ) {
        LOG(LOG_ERROR, "info.c::add_to_textbuf",
            "type (%d) >= MSG_TYPE_LAST (%d) or "
            "subtype (%d) >= max_subtype (%d)\n",
            type, MSG_TYPE_LAST, subtype, max_subtype);
    } else {
        if (pane->msg_type_tags[type][subtype]) {
            type_tag = pane->msg_type_tags[type][subtype];
        } else if (pane->msg_type_tags[type][0]) {
            type_tag = pane->msg_type_tags[type][0];
        }
    }

    gtk_text_view_get_visible_rect(
        GTK_TEXT_VIEW(pane->textview), &rect);

    /* Simple panes (like those of the login windows) don't have adjustments
     * set (and if they did, we wouldn't want to scroll to end in any case),
     * so check here on what to do.
     */
    if (pane->adjustment &&
        (gtk_adjustment_get_value(pane->adjustment) + rect.height) >=
            gtk_adjustment_get_upper(pane->adjustment)) {
        scroll_to_end = 1;
    }

    gtk_text_buffer_get_end_iter(pane->textbuffer, &end);

    gtk_text_buffer_insert_with_tags(
        pane->textbuffer, &end, message, strlen(message),
        bold ? pane->bold_tag : pane->default_tag,
        italic ? pane->italic_tag : pane->default_tag,
        underline ? pane->underline_tag : pane->default_tag,
        pane->font_tags[font] ?
        pane->font_tags[font] : pane->default_tag,
        color_tag, type_tag, NULL);

    if (scroll_to_end)
        gtk_text_view_scroll_mark_onscreen(
            GTK_TEXT_VIEW(pane->textview), pane->textmark);
}

/**
 * This just does the work of taking text (which may have markup) and putting
 * it into the target pane.  This is a lower level than the draw_ext_info()
 * below, as it does not do message routing.  This is called from
 * draw_ext_info() below, as well as account.c to update news/motd/rules.
 *
 * @param pane    Pointer to the pane info to draw info.
 * @param message Message that is parsed and displayed.
 * @param type    Type of the message - for default coloring information.
 * @param subtype Subtype of message - used for default coloring information.
 * @param orig_color Legacy color hint not based on type that is used when a
 *                   theme does not define a style for the message.
 * @note  Note both type and subtype are the values passed to draw_ext_info().
 */

void add_marked_text_to_pane(Info_Pane *pane, const char *message, int type, int subtype, int orig_color)
{
    char *marker, *current, *original;
    int bold=0, italic=0, font=0, underline=0;
    const char *color=NULL; /**< Only if we get a [color] tag should we care,
                             *   otherwise, the type/subtype should dictate
                             *   color (unless no style set!)
                             */

    current = g_strdup(message);
    original = current;         /* Just so we know what to free */

    /*
     * If there is no style information, or if a specific style has not been
     * set for the type/subtype of this message, allow orig_color to set the
     * color of the text.  The orig_color handling here adds compatibility
     * with former draw_info() calls that gave a color hint.  The color hint
     * still works now in the event that the theme has not set a style for the
     * message type.
     */
    if (! has_style || pane->msg_type_tags[type][subtype] == 0) {
        if (orig_color <0 || orig_color>NUM_COLORS) {
            LOG(LOG_ERROR, "info.c::draw_ext_info",
                "Passed invalid color from server: %d, max allowed is %d\n",
                orig_color, NUM_COLORS);
        } else {
            /*
             * Not efficient - we have a number, but convert it to a string,
             * at which point add_to_textbuf() converts it back to a number.
             */
            color = usercolorname[orig_color];
        }
    }

    while ((marker = strchr(current, '[')) != NULL) {
        *marker = 0;

        if (strlen(current) > 0)
            add_to_textbuf(pane, current, type, subtype,
                           bold, italic, font, color, underline);

        current = marker + 1;

        if ((marker = strchr(current, ']')) == NULL) {
            free(original);
            return;
        }

        *marker = 0;
        if (!strcmp(current, "b")) {
            bold = TRUE;
        } else if (!strcmp(current,  "/b")) {
            bold = FALSE;
        } else if (!strcmp(current,  "i")) {
            italic = TRUE;
        } else if (!strcmp(current,  "/i")) {
            italic = FALSE;
        } else if (!strcmp(current,  "ul")) {
            underline = TRUE;
        } else if (!strcmp(current,  "/ul")) {
            underline = FALSE;
        } else if (!strcmp(current,  "fixed")) {
            font = FONT_FIXED;
        } else if (!strcmp(current,  "arcane")) {
            font = FONT_ARCANE;
        } else if (!strcmp(current,  "hand")) {
            font = FONT_HAND;
        } else if (!strcmp(current,  "strange")) {
            font = FONT_STRANGE;
        } else if (!strcmp(current,  "print")) {
            font = FONT_NORMAL;
        } else if (!strcmp(current,  "/color")) {
            color = NULL;
        } else if (!strncmp(current, "color=", 6)) {
            color = current + 6;
        } else
            LOG(LOG_INFO, "info.c::message_callback",
                "unrecognized tag: [%s]\n", current);

        current = marker + 1;
    }

    add_to_textbuf(
        pane, current, type, subtype,
        bold, italic, font, color, underline);

    add_to_textbuf(
        pane, "\n", type, subtype,
        bold, italic, font, color, underline);

    free(original);

}

/**
 * A message processor that accepts messages along with meta information color
 * and type.  The message type and subtype are analyzed to select font and
 * other text attributes.  All gtk-v2 client messages pass through this
 * processor before being output.  Before addition of the output buffering
 * feature, this was the message callback function.  It is a separate function
 * so that it can be called both by the callback, and but buffer maintenance
 * functions.
 *
 * Client-sourced messages generally should be passed directly to this handler
 * instead of to the callback.  This will save some overhead as the callback
 * implements a system that coalesces duplicate messages - a feature that is
 * not really applicable to most messages that do not come from the server.
 *
 * @param orig_color Suggested text color that type/subtype can over-ride.
 * @param type       Message type. See MSG_TYPE definitions in newclient.h.
 * @param subtype    Message subtype. See MSG_TYPE_*_* values in newclient.h.
 * @param message    The message text to display.
 */
void draw_ext_info(int orig_color, int type, int subtype, const char *message)
{
    int type_err=0;   /**< When 0, the type is valid and may be used to pick
                       *   the panel routing, otherwise the message can only
                       *   go to the main message pane.
                       */
    int pane;
    char *stamp = NULL;
    const char *draw = NULL;

    if (want_config[CONFIG_TIMESTAMP]) {
        time_t curtime;
        struct tm *ltime;
        stamp = calloc(1, strlen(message) + 7);
        curtime = time(NULL);
        ltime = localtime(&curtime);
        strftime(stamp, 6, "%I:%M", ltime);
        strcat(stamp, " ");
        strcat(stamp, message);
        draw = stamp;
    } else {
        draw = message;
    }

    /*
     * A valid message type is required to index into the msgctrl_widgets
     * array.  If an invalid type is identified, log an error as any message
     * without a valid type should be hunted down and assigned an appropriate
     * type.
     */
    if ((type < 1) || (type >= MSG_TYPE_LAST)) {
        LOG(LOG_ERROR, "info.c::draw_ext_info",
            "Invalid message type: %d", type);
        type_err = 1;
    }
    /*
     * Route messages to any one of the client information panels based on the
     * type of the message text.  If a message with an invalid type comes in,
     * it goes to the main message panel (0).  Messages can actually be sent
     * to more than one panel if the player so desires.
     */
    for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
        /*
         * If the message type is invalid, then the message must go to pane 0,
         * otherwise the msgctrl_widgets[].pane[pane].state setting determines
         * whether to send the message to a particular pane or not.  The type
         * is one-based, so must be decremented when referencing
         * msgctrl_widgets[];
         */
        if (type_err != 0) {
            if (pane != 0) {
                break;
            }
        } else {
            if (msgctrl_widgets[type - 1].pane[pane].state == FALSE) {
                continue;
            }
        }
        add_marked_text_to_pane(&info_pane[pane], draw, type, subtype, orig_color);
    }

    if (want_config[CONFIG_TIMESTAMP]) {
        free(stamp);
    }
}

/**
 * @defgroup GTKv2OutputCountSync GTK V2 client output count/sync functions.
 * @{
 */

/**
 * Output count/sync message buffer initialization to set all buffers empty.
 * Called only once at client start from info_init(), the function initializes
 * all message buffers to the empty state (count == -1).  At a minimum, age,
 * count, and message should be initialized.  Type, subtype, and orig_color
 * are also set just for an extra measure of safety.
 */
void info_buffer_init(void)
{
    int loop;

    for (loop = 0; loop < MESSAGE_BUFFER_COUNT; loop += 1) {
        info_buffer[loop].count = -1;
        info_buffer[loop].age = 0;
        info_buffer[loop].type = 0;
        info_buffer[loop].subtype = 0;
        info_buffer[loop].orig_color = 0;
        info_buffer[loop].message[0] = '\0';
    };
}

/**
 * Handles message buffer flushes, and, as needed, displays the text.  Flushed
 * buffers have their count set to -1.  On flush, the message text is output
 * only when the message count is greater than zero.  If the message text is
 * displayed, and if the count is greater than one, it is prepended to the
 * message in the form "N * times ".  This function is called whenever a
 * message must be ejected from the output count/sync system buffers.  Note
 * that the message details are preserved when the buffer is flushed.  This
 * allows the buffer contents to be re-used if another message with the same
 * text comes in before the buffer is re-used for a different message.
 *
 * @param id The message control buffer to flush (0 - MESSAGE_BUFFER_COUNT).
 */
void info_buffer_flush(const int id)
{
    char output_buffer[MESSAGE_BUFFER_SIZE  /* Buffer for output big enough */
                       +COUNT_BUFFER_SIZE]; /* to hold both count and text. */
    /*
     * Messages are output with no output-count at the time they are first
     * placed in a buffer, so do not bother displaying it again if another
     * instance of the message was not seen after the initial buffering.
     */
    if (info_buffer[id].count > 0) {
        /*
         * Report the number of times the message was seen only if it was seen
         * after having been initially buffered.
         */
        if (info_buffer[id].count > 1) {
            snprintf(output_buffer, sizeof(output_buffer), "%u times %s",
                     info_buffer[id].count, info_buffer[id].message);
            /*
             * Output the message count and message text.
             */
            draw_ext_info(
                info_buffer[id].orig_color,
                info_buffer[id].type,
                info_buffer[id].subtype,
                output_buffer);
        } else
            /*
             * Output only the message text.
             */
            draw_ext_info(
                info_buffer[id].orig_color,
                info_buffer[id].type,
                info_buffer[id].subtype,
                info_buffer[id].message);
    };
    /*
     * Mark the buffer newly emptied.
     */
    info_buffer[id].count = -1;
}

/**
 * Output count/sync buffer maintainer adds buffer time and output messages.
 * For every tick, age active messages so it eventually gets displayed.  If
 * the data in an buffer reaches the maximum permissible age or message
 * occurance count, it is ejected and displayed.  Inactive buffers are also
 * aged so that the oldest empty buffer is used first when a new message
 * comes in.
 */
void info_buffer_tick(void)
{
    int loop;

    for (loop = 0; loop < MESSAGE_BUFFER_COUNT; loop += 1) {
        if (info_buffer[loop].count > -1) {
            if ((info_buffer[loop].age < (int) buffer_control.timer.state)
                    &&  (info_buffer[loop].count < (int) buffer_control.count.state)) {
                /*
                 * The buffer has data in it, and has not reached maximum age,
                 * so bump the age up a notch.
                 */
                info_buffer[loop].age += 1;
            } else {
                /*
                 * The data has been in the buffer too long, so either display
                 * it (and report how many times it was seen while in the
                 * buffer) or simply expire the buffer content if duplicates
                 * did not occur.
                 */
                info_buffer_flush(loop);
            }
        } else {
            /*
             * Overflow-protected aging of empty or inactive buffers.  Aging
             * of inactive buffers is the reason overflow must be handled.
             */
            if (info_buffer[loop].age < info_buffer[loop].age + 1) {
                info_buffer[loop].age += 1;
            }
        }
    }
}

/**
 * A callback to accept messages along with meta information color and type.
 * Unlike the GTK V1 client, we don't do anything tricky like popups with
 * different message types, but the output-count/sync features do consider
 * message type, etc.  To allow user-defined buffering rules all messages
 * need to pass through a common processor.  This callback is the interface
 * for the output buffering.  Even if output buffering could be bypassed, it
 * is still necessary to pass messages through a common interface to handle
 * style, theme, and display panel configuration.  This callback routes all
 * messages to the appropriate handler for pre-display processing
 * (draw_ext_info()).
 *
 * It is recommended that client-sourced messages be passed directly to
 * draw_ext_info() instead of through the callback to avoid unnecessary
 * processing.  MSG_TYPE_CLIENT messages are deliberately not buffered here
 * because they are generally unique, adminstrative messages that should not
 * be delayed.
 *
 * @param orig_color Suggested text color that type/subtype can over-ride.
 * @param type       Message type. See MSG_TYPE definitions in newclient.h.
 * @param subtype    Message subtype. See MSG_TYPE_*_* values in newclient.h.
 * @param message    The message text to display.
 */
static void
message_callback(int orig_color, int type, int subtype, char *message)
{
    int search;                         /* Loop for searching the buffers.  */
    int found;                          /* Which buffer a message is in.    */
    int empty;                          /* The ID of an empty buffer.       */
    int oldest;                         /* Oldest non-empty buffer found.   */
    int empty_age;                      /* Age of oldest empty buffer.      */
    int oldest_age;                     /* Age of oldest non-empty buffer.  */

    /*
     * Any message that has an invalid type cannot be buffered.  An error is
     * not logged here as draw_ext_info() is where all messages pass through.
     *
     * A legacy switch to prevent message folding is to set the color of the
     * message to NDI_UNIQUE.  This over-rides the player preferences.
     *
     * Usually msgctrl_widgets[] is used to determine whether or not messages
     * are buffered as it is where the player sets buffering preferences.  The
     * type must be decremented when used to index into msgctrl_widgets[].
     *
     * The system also declines to buffer messages over a set length as most
     * messages that need coalescing are short.  Most messages that are long
     * are usually unique and should not be delayed.  >= allows for the null
     * at the end of the string in the buffer. IE. If the buffer size is 40,
     * only 39 chars can be put into it to ensure room for a null character.
     */
    if ((type <  1)
            ||  (type >= MSG_TYPE_LAST)
            ||  (orig_color == NDI_UNIQUE)
            ||  (msgctrl_widgets[type - 1].buffer.state == FALSE)
            ||  (strlen(message) >= MESSAGE_BUFFER_SIZE)) {
        /*
         * If the message buffering feature is off, simply pass the message on
         * to the parser that will determine the panel routing and style.
         */
        draw_ext_info(orig_color, type, subtype, message);
    } else {
        empty  = -1;       /* Default:  Buffers are empty until proven full */
        found  = -1;       /* Default:  Incoming message is not in a buffer */
        oldest = -1;       /* Default:  Oldest active buffer ID is unknown  */
        empty_age= -1;     /* Default:  Oldest empty buffer age is unknown  */
        oldest_age= -1;    /* Default:  Oldest active buffer age is unknown */

        for (search = 0; search < MESSAGE_BUFFER_COUNT; search += 1) {
            /*
             * 1) Find the oldest empty or inactive buffer, if one exists.
             * 2) Find the oldest non-empty/active buffer in case we need to
             *    eject a message to make room for a new message.
             * 3) Find a buffer that matches the incoming message, whether the
             *    buffer is active or not.
             */
            if (info_buffer[search].count < 0) {
                /*
                 * We want to find the oldest empty buffer.  If a new message
                 * that is not already buffered comes in, this is the ideal
                 * place to put it.
                 */
                if ((info_buffer[search].age > empty_age)) {
                    empty_age = info_buffer[search].age;
                    empty = search;
                }
            } else {
                /*
                 * The buffer is not empty, so process it to find the oldest
                 * buffered message.  If a new message comes in that is not
                 * already buffered, and if there are no empty buffers
                 * available, the oldest message will be pushed out to make
                 * room for the new one.
                 */
                if (info_buffer[search].age > oldest_age) {
                    oldest_age = (info_buffer[search].age);
                    oldest = search;
                }
            }
            /*
             * Check all buffers, inactive and active, to see if the incoming
             * message matches an existing buffer.  Because empty buffers are
             * re-used if they match, it should not be possible for more than
             * one buffer to match, so do not bother searching after the first
             * match is found.
             */
            if (found < 0) {
                if (! strcmp(message, info_buffer[search].message)) {
                    found = search;
                }
            }
        }

#if 0
        LOG(LOG_DEBUG, "info.c::message_callback", "\n           "
            "type: %d-%d empty: %d found: %d oldest: %d oldest_age: %d",
            type, subtype, empty, found, oldest, oldest_age);
#endif

        /*
         * If the incoming message is already buffered, then increment the
         * message count and exit, otherwise add the message to the buffer.
         */
        if (found > -1) {
            /*
             * If the found buffer was inactive, this automatically activates
             * it, and sets the count to one to ensure printing of the message
             * occurance as messages are pre-printed only when they are
             * inserted into a buffer after not being found.
             */
            if (info_buffer[found].count == -1) {
                info_buffer[found].count += 1;
                info_buffer[found].age = 0;
            }
            info_buffer[found].count += 1;
        } else {
            /*
             * The message was not found in a buffer, so check if there is an
             * available buffer.  If not, dump the oldest buffer to make room,
             * then mark it empty.
             */
            if (empty == -1) {
                if (oldest > -1) {
                    /*
                     * The oldest message is getting kicked out of the buffer
                     * to make room for a new message coming in.
                     */
                    info_buffer_flush(oldest);
                } else {
                    LOG(LOG_ERROR, "info.c::message_callback",
                        "Buffer full; oldest unknown", strlen(message));
                }
            }
            /*
             * To avoid delaying player notification in cases where multiple
             * messages might not occur, or especially if a message is really
             * important to get right away, go ahead an output the message
             * without a count at the time it is first put into a buffer.  As
             * this message has already been output, the buffer count is set
             * zero, so that info_buffer_flush() will not re-display it if a
             * duplicate does not occur while this message is in the buffer.
             */
            draw_ext_info(orig_color, type, subtype, message);
            /*
             * There should always be an empty buffer at this point, but just
             * in case, recheck before putting the new message in the buffer.
             * Do not log another error as one was just logged, but instead
             * just output the message that came in without passing it through
             * the buffer system.
             */
            if (empty > -1) {
                /*
                 * Copy the incoming message to the empty buffer.
                 */
                info_buffer[empty].age = 0;
                info_buffer[empty].count = 0;
                info_buffer[empty].orig_color = orig_color;
                info_buffer[empty].type = type;
                info_buffer[empty].subtype = subtype;
                strcpy(info_buffer[empty].message, message);
            }
        }
    }
}

/**
 * @} */ /* EndOf GTKv2OutputCountSync
 */

/**
 * Clears all the message panels.  It is not clear why someone would use it,
 * but is called from the common area, and so is supported here.
 */
void menu_clear(void)
{
    int i;

    for (i=0; i < NUM_TEXT_VIEWS; i++) {
        gtk_text_buffer_set_text(info_pane[i].textbuffer, "", 0);
    }
}

/**
 * Initialize the message control panel by populating it with descriptions of
 * each message type along with checkboxes that are used to configure the
 * routing and duplicate suppression system.  If previously saved settings are
 * found on disk, they are loaded and applied, otherwise the built in client
 * defaults are loaded and applied.  This initialization must occur after the
 * info_init() function runs.
 *
 * @param window_root The client main window
 */
void msgctrl_init(GtkWidget *window_root)
{
    GtkTableChild* child;               /* Used to get number of title rows */
    GtkWidget*     widget;              /* Used to connect widgets          */
    GtkTable*      table;               /* The table of checkbox controls   */
    GList*         list;                /* Iterator: table children         */
    guint          pane;                /* Iterator: client message panes   */
    guint          type;                /* Iterator: message types          */
    guint          row;                 /* Attachement for current widget   */
    gint           title_rows = -1;     /* Title rows in msgctrl_table as
                                         * defined in layout designer.  -1
                                         * means there are no title rows.
                                         */
    /*
     * Get the window pointer and a pointer to the tree of widgets it contains
     */
    msgctrl_window = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_window"));

    g_signal_connect((gpointer) msgctrl_window, "delete_event",
                     G_CALLBACK(gtk_widget_hide_on_delete), NULL);
    /*
     * Initialize the spinbutton pointers.
     */
    buffer_control.count.ptr =
        GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_spinbutton_count"));

    buffer_control.timer.ptr =
        GTK_WIDGET(gtk_builder_get_object(dialog_xml,"msgctrl_spinbutton_timer"));

    /*
     * Locate the table widget to fill with controls and its structure.
     */
    msgctrl_table = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_table"));
    table = GTK_TABLE(msgctrl_table);
    /*
     * How many title rows were set up in the table?  The title rows are the
     * non-empty rows.  Row numbers are zero-based.  IMPORTANT: It is assumed
     * any row with at least one widget has widgets in all columns.  WARNING:
     * This assumption is unwise if client layouts begin to be implemented to
     * have fewer message panes than the code supports!
     */
    for (list = gtk_container_get_children(GTK_CONTAINER(table)); list; list = list->next) {
        child = list->data;
        if ((child->widget != 0) && (child->top_attach > title_rows)) {
            title_rows = child->top_attach;
        }
    }

    /*
     * The table is defined in the dialog created with the design tool, but
     * the dimensions of the table are not known at design time, so it must be
     * resized and built up at run-time.
     *
     * The table columns are:  message type description, message buffer
     * enable, and one enable per message pane supported by the client code.
     * The client layout might not support all of the panes, but all of them
     * will be put into the table.
     *
     * The table rows are: the header rows + the number of message types that
     * the client and server support.  We assume the XML file designer did
     * properly set up the header rows.  Since MSG_TYPE_LAST is 1 more than
     * the actual number of types, and since title_rows is one less than the
     * actual number of header rows, they balance out when added together.
     */
    gtk_table_resize(table,
                     (guint)(MSG_TYPE_LAST + title_rows), (guint)(1 + 1 + NUM_TEXT_VIEWS));
    /*
     * Now we need to put labels and checkboxes in each of the empty rows and
     * initialize the state of the checkboxes to match the default settings.
     * It helps if we change title_rows to a one-based number.  Walk through
     * each message type and set the corresponding row of the table it needs
     * to go with.  type is one-based.  The msgctrl_defaults and _widget
     * arrays are zero based.
     */
    title_rows += 1;
    for (type = 0; type < MSG_TYPE_LAST - 1; type += 1) {
        row = type + title_rows;
        /*
         * The message type description.  Just put the the message type name
         * in a label, left-justified with some padding to keep it away from
         * the dialog frame and perhaps the neighboring checkbox.
         */
        widget = gtk_label_new(msgctrl_defaults[type].description);
        gtk_misc_set_alignment(GTK_MISC(widget), 0.0f, 0.5f);
        gtk_misc_set_padding(GTK_MISC(widget), 2, 0);
        gtk_table_attach_defaults(table, widget, 0, 1, row, row + 1);
        gtk_widget_show(widget);
        /*
         * The buffer enable/disable.  Display a check box that is preset to
         * the built-in default setting.
         */
        msgctrl_widgets[type].buffer.ptr = gtk_check_button_new();
        gtk_table_attach_defaults(
            table, msgctrl_widgets[type].buffer.ptr, 1, 2, row, row + 1);
        gtk_widget_show(msgctrl_widgets[type].buffer.ptr);
        /*
         * The message pane routings.  Display a check box that is preset to
         * the built in defaults.
         */
        /**
         * @todo  Panes that are unsupported in the current layout should
         * always have their routing disabled, and should disallow user
         * interaction with the control but this logic is not yet implemented.
         */
        for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
            msgctrl_widgets[type].pane[pane].ptr = gtk_check_button_new();
            gtk_table_attach_defaults(
                table, msgctrl_widgets[type].pane[pane].ptr,
                pane + 2, pane + 3, row, row + 1);
            gtk_widget_show(msgctrl_widgets[type].pane[pane].ptr);
        }
    }
    /*
     * Initialize the state variables for the checkbox and spinbutton controls
     * on the message control dialog and then set all the widgets to match the
     * client defautl settings.
     */
    default_msgctrl_configuration();
    load_msgctrl_configuration();

    /*
     * Connect the control's buttons to the appropriate handlers.
     */
    widget = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_button_save"));
    g_signal_connect((gpointer) widget, "clicked",
                     G_CALLBACK(on_msgctrl_button_save_clicked), NULL);

    widget = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_button_load"));
    g_signal_connect((gpointer) widget, "clicked",
                     G_CALLBACK(on_msgctrl_button_load_clicked), NULL);

    widget = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_button_defaults"));
    g_signal_connect((gpointer) widget, "clicked",
                     G_CALLBACK(on_msgctrl_button_defaults_clicked), NULL);

    widget = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_button_apply"));
    g_signal_connect((gpointer) widget, "clicked",
                     G_CALLBACK(on_msgctrl_button_apply_clicked), NULL);

    widget = GTK_WIDGET(gtk_builder_get_object(dialog_xml, "msgctrl_button_close"));
    g_signal_connect((gpointer) widget, "clicked",
                     G_CALLBACK(on_msgctrl_button_close_clicked), NULL);
}

/**
 * Update the state of the message control dialog so the configuration matches
 * the currently selected settings.  Do not call this before msgctrl_widgets[]
 * is initialized.  It also really only makes sense to call it if changes have
 * been made to msgctrl_widgets[].
 */
void update_msgctrl_configuration(void)
{
    guint pane;                         /* Client-supported message pane    */
    guint type;                         /* Message type                     */

    gtk_spin_button_set_value(
        GTK_SPIN_BUTTON(buffer_control.count.ptr),
        (gdouble) buffer_control.count.state);

    gtk_spin_button_set_value(
        GTK_SPIN_BUTTON(buffer_control.timer.ptr),
        (gdouble) buffer_control.timer.state);

    for (type = 0; type < MSG_TYPE_LAST - 1; type += 1) {
        gtk_toggle_button_set_active(
            GTK_TOGGLE_BUTTON(msgctrl_widgets[type].buffer.ptr),
            msgctrl_widgets[type].buffer.state);
        for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
            gtk_toggle_button_set_active(
                GTK_TOGGLE_BUTTON(msgctrl_widgets[type].pane[pane].ptr),
                msgctrl_widgets[type].pane[pane].state);
        }
    }
}

/**
 * Applies the current state of the checkboxes to the msgctrl_widgets state
 * variables and saves the settings to disk so the configuration persists
 * across client sessions.
 */
void save_msgctrl_configuration(void)
{
    char  pathbuf[MAX_BUF];             /* Buffer for a save file path name */
    char  textbuf[MAX_BUF];             /* Buffer for output to save file   */
    FILE* fptr;                         /* Message Control savefile pointer */
    guint pane;                         /* Client-supported message pane    */
    guint type;                         /* Message type                     */

    read_msgctrl_configuration();       /* Apply the displayed settings 1st */

    snprintf(pathbuf, sizeof(pathbuf), "%s/msgs", config_dir);

    if (make_path_to_file(pathbuf) == -1) {
        LOG(LOG_WARNING,
            "gtk-v2::save_msgctrl_configuration","Error creating %s",pathbuf);
        snprintf(textbuf, sizeof(textbuf),
                 "Error creating %s, Message Control settings not saved.",pathbuf);
        draw_ext_info(
            NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_ERROR, textbuf);
        return;
    }
    if ((fptr = fopen(pathbuf, "w")) == NULL) {
        snprintf(textbuf, sizeof(textbuf),
                 "Error opening %s, Message Control settings not saved.", pathbuf);
        draw_ext_info(
            NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_ERROR, textbuf);
        return;
    }

    /*
     * It might be best to check the status of all writes, but it is not done.
     */
    fprintf(fptr, "# Message Control System Configuration\n");
    fprintf(fptr, "#\n");
    fprintf(fptr, "# Count:  1-96\n");
    fprintf(fptr, "#\n");
    fprintf(fptr, "C %u\n", buffer_control.count.state);
    fprintf(fptr, "#\n");
    fprintf(fptr, "# Timer:  1-96 (8 ~= one second)\n");
    fprintf(fptr, "#\n");
    fprintf(fptr, "T %u\n", buffer_control.timer.state);
    fprintf(fptr, "#\n");
    fprintf(fptr, "# type, buffer, pane[0], pane[1]...\n");
    fprintf(fptr, "# Do not edit the 'type' field.\n");
    fprintf(fptr, "# 0 == disable; 1 == enable.\n");
    fprintf(fptr, "#\n");
    for (type = 0; type < MSG_TYPE_LAST - 1; type += 1) {
        fprintf(
            fptr, "M %02d %d ", type+1, msgctrl_widgets[type].buffer.state);
        for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
            fprintf(fptr, "%d ", msgctrl_widgets[type].pane[pane].state);
        }
        fprintf(fptr, "\n");
    }
    fprintf(fptr, "#\n# End of Message Control System Configuration\n");
    fclose(fptr);

    LOG(LOG_DEBUG, "gtk-v2::save_msgctrl_configuration",
            "Message control settings saved to '%s'", pathbuf);

    snprintf(textbuf, sizeof(textbuf), "Message control settings saved!");
    draw_ext_info(NDI_BLUE, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_CONFIG, textbuf);
}

/**
 * Setup the state of the message control dialog so the configuration matches
 * a previously saved configuration.
 */
void load_msgctrl_configuration(void)
{
    char  pathbuf[MAX_BUF];             /* Buffer for a save file path name */
    char  textbuf[MAX_BUF];             /* Buffer for input from save file  */
    char  recordtype;                   /* Savefile data entry type found   */
    char* cptr;                         /* Pointer used when reading data   */
    FILE* fptr;                         /* Message Control savefile pointer */
    guint pane;                         /* Client-supported message pane    */
    guint type;                         /* Message type                     */
    guint error;                        /* Savefile parsing status          */
    message_control_t statebuf;         /* Holding area for savefile values */
    buffer_parameter_t countbuf;        /* Holding area for savefile values */
    buffer_parameter_t timerbuf;        /* Holding area for savefile values */
    guint cvalid, tvalid, mvalid;       /* Counts the valid entries found   */

    snprintf(pathbuf, sizeof(pathbuf), "%s/msgs", config_dir);

    if ((fptr = fopen(pathbuf, "r")) == NULL) {
        snprintf(textbuf, sizeof(textbuf),
                 "Error opening %s, Message Control settings not loaded.",pathbuf);
        draw_ext_info(
            NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_ERROR, textbuf);
        return;
    }
    /*
     * When we parse the file we buffer each entire record before any values
     * are applied to the client message control configuration.  If any
     * problems are found at all, the entire record is skipped and the file
     * is reported as corrupt.  Even if individual records are corrupt, the
     * rest of the file is processed.
     *
     * If more than one record for the same error type exists the last one is
     * used, but if too many records are found the file is reported as corrupt
     * even though it accepts all the data.
     */
    error = 0;
    cvalid = 0;
    tvalid = 0;
    mvalid = 0;
    while(fgets(textbuf, MAX_BUF-1, fptr) != NULL) {
        if (textbuf[0] == '#' || textbuf[0] == '\n') {
            continue;
        }

        /*
         * Identify the savefile record type found.
         */
        cptr = strtok(textbuf, "\t ");
        if ((cptr == NULL)
                || ((*cptr != 'C') && (*cptr != 'T') && (*cptr != 'M'))) {
            error += 1;
            continue;
        }
        recordtype = *cptr;

        /*
         * Process the following fields by record type
         */
        if (recordtype == 'C') {
            cptr = strtok(NULL, "\n");
            if ((cptr == NULL)
                    ||  (sscanf(cptr, "%u", &countbuf.state) != 1)
                    ||  (countbuf.state < 1)
                    ||  (countbuf.state > 96)) {
                error += 1;
                continue;
            }
        }
        if (recordtype == 'T') {
            cptr = strtok(NULL, "\n");
            if ((cptr == NULL)
                    ||  (sscanf(cptr, "%u", &timerbuf.state) != 1)
                    ||  (timerbuf.state < 1)
                    ||  (timerbuf.state > 96)) {
                error += 1;
                continue;
            }
        }
        if (recordtype == 'M') {
            cptr = strtok(NULL, "\t ");
            if ((cptr == NULL)
                    ||  (sscanf(cptr, "%d", &type) != 1)
                    ||  (type < 1)
                    ||  (type >= MSG_TYPE_LAST)) {
                error += 1;
                continue;
            }
            cptr = strtok(NULL, "\t ");
            if ((cptr == NULL)
                    ||  (sscanf(cptr, "%d", &statebuf.buffer.state) != 1)
                    ||  (statebuf.buffer.state < 0)
                    ||  (statebuf.buffer.state > 1)) {
                error += 1;
                continue;
            }
            for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
                cptr = strtok(NULL, "\t ");
                if ((cptr == NULL)
                        ||  (sscanf(cptr, "%d", &statebuf.pane[pane].state) != 1)
                        ||  (statebuf.pane[pane].state < 0)
                        ||  (statebuf.pane[pane].state > 1)) {
                    error += 1;
                    continue;
                }
            }
            /*
             * Ignore the record if it has too many fields.  This might be a
             * bit strict, but it does help enforce the file integrity in the
             * event that the the number of supported panels increases in the
             * future.
             */
            cptr = strtok(NULL, "\n");
            if (cptr != NULL) {
                error += 1;
                continue;
            }
        }

        /*
         * Remember, type is one-based, but the index into an array is zero-
         * based, so adjust type.  Also, since the record parsed out fine,
         * increment the number of valid records found.  Apply all the values
         * read to the buffer_control structure and msgctrl_widgets[] array so
         * the dialog can be updated when all data has been read.
         */
        if (recordtype == 'C') {
            buffer_control.count.state = countbuf.state;
            cvalid += 1;
        }
        if (recordtype == 'T') {
            buffer_control.timer.state = timerbuf.state;
            tvalid += 1;
        }
        if (recordtype == 'M') {
            type -= 1;
            msgctrl_widgets[type].buffer.state = statebuf.buffer.state;
            for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
                msgctrl_widgets[type].pane[pane].state =
                    statebuf.pane[pane].state;
            }
            mvalid += 1;
        }
    }
    fclose(fptr);
    /*
     * If there was any oddity with the data file, report it as corrupted even
     * if some of the values were used.  A corrupted file can be uncorrupted
     * by loading it and saving it again.  A found value is needed for count,
     * timer, and each message type.
     */
    if ((error > 0)
            ||  (cvalid != 1)
            ||  (tvalid != 1)
            ||  (mvalid != MSG_TYPE_LAST - 1)) {
        snprintf(textbuf, sizeof(textbuf),
                 "Corrupted Message Control settings in %s.", pathbuf);
        draw_ext_info(
            NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_ERROR, textbuf);
        LOG(LOG_ERROR, "gtk-v2::load_msgctrl_configuration",
            "Error loading %s. %s\n", pathbuf, textbuf);
    }
    /*
     * If any data was accepted from the save file, report that settings were
     * loaded.  Apply the loaded values to the Message Control dialog checkbox
     * widgets.  so they reflect the states that were previously saved.
     */
    if ((cvalid + tvalid + mvalid) > 0) {
        LOG(LOG_DEBUG, "gtk-v2::load_msgctrl_configuration",
                "Message control settings loaded from '%s'", pathbuf);
        update_msgctrl_configuration(); /* Update checkboxes w/ loaded data */
    }
}

/**
 * Setup the state of the message control dialog so the configuration matches
 * the default settings built in to the client.
 *
 * Iterate through each message type.  For each, copy the built-in client
 * default to the Message Control dialog state variables.  All supported
 * defaults are copied, not just the ones supported by the layout.
 */
void default_msgctrl_configuration(void)
{
    guint pane;                         /* Client-supported message pane    */
    guint type;                         /* Message type                     */

    buffer_control.count.state = (guint) buffer_control.count.default_state;
    buffer_control.timer.state = (guint) buffer_control.timer.default_state;
    for (type = 0; type < MSG_TYPE_LAST - 1; type += 1) {
        msgctrl_widgets[type].buffer.state = msgctrl_defaults[type].buffer;
        for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
            msgctrl_widgets[type].pane[pane].state =
                msgctrl_defaults[type].pane[pane];
        }
    }
    update_msgctrl_configuration();
}

/**
 * Reads the state of the message control dialog and applies the settings to
 * the msgctrl_widgets[] state variables that control the message routing
 * and duplicate suppression system.
 */
void read_msgctrl_configuration(void)
{
    guint pane;                         /* Client-supported message pane    */
    guint type;                         /* Message type                     */

    buffer_control.count.state =
        gtk_spin_button_get_value_as_int(
            GTK_SPIN_BUTTON(buffer_control.count.ptr));
    buffer_control.timer.state =
        gtk_spin_button_get_value_as_int(
            GTK_SPIN_BUTTON(buffer_control.timer.ptr));
    /*
     * Iterate through each message type.  For each, record the value of the
     * message duplicate suppression checkbox, and also obtain the routing
     * settings for all client supported panels (even if the layout does not
     * support them all.
     */
    for (type = 0; type < MSG_TYPE_LAST - 1; type += 1) {
        msgctrl_widgets[type].buffer.state =
            gtk_toggle_button_get_active(
                GTK_TOGGLE_BUTTON(msgctrl_widgets[type].buffer.ptr));
        for (pane = 0; pane < NUM_TEXT_VIEWS; pane += 1) {
            msgctrl_widgets[type].pane[pane].state =
                gtk_toggle_button_get_active(
                    GTK_TOGGLE_BUTTON(msgctrl_widgets[type].pane[pane].ptr));
        }
    }
}

/**
 * When the message control dialog save button is pressed, the currently shown
 * settings are applied for immediate use and they are saved to disk so the
 * settings persist across client sessions.  Saved settings automatically
 * load and apply when the client is started.
 *
 * @param button
 * @param user_data
 */
void
on_msgctrl_button_save_clicked          (GtkButton       *button,
        gpointer         user_data)
{
    read_msgctrl_configuration();
    save_msgctrl_configuration();
}

/**
 * When the message control dialog load button is pressed, the settings last
 * saved are restored and applied.  It may be used to "undo" both applied and
 * unapplied setting changes.
 *
 * @param button
 * @param user_data
 */
void
on_msgctrl_button_load_clicked          (GtkButton       *button,
        gpointer         user_data)
{
    load_msgctrl_configuration();
}

/**
 * When the message control dialog defaults button is pressed, the default
 * settings built into the client are restored and applied.
 *
 * @param button
 * @param user_data
 */
void
on_msgctrl_button_defaults_clicked      (GtkButton       *button,
        gpointer         user_data)
{
    default_msgctrl_configuration();
}

/**
 * When the message control dialog apply button is pressed, the currently
 * displayed settings are applied.  The dialog is not dismissed, but remains
 * open for further adjustments to be made.
 *
 * @param button
 * @param user_data
 */
void
on_msgctrl_button_apply_clicked         (GtkButton       *button,
        gpointer         user_data)
{
    read_msgctrl_configuration();
}

/**
 * When the message control dialog close button is pressed, the currently
 * displayed settings are applied and the dialog is dismissed.
 *
 * @param button
 * @param user_data
 */
void
on_msgctrl_button_close_clicked         (GtkButton       *button,
        gpointer         user_data)
{
    read_msgctrl_configuration();
    gtk_widget_hide(msgctrl_window);
}

/**
 * Shows the message control dialog when the menu item is activated.  The
 * settings shown on the dialog when it is activated are the settings
 * currently in use.
 *
 * @param menuitem
 * @param user_data
 */
void
on_msgctrl_activate                    (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    gtk_widget_show(msgctrl_window);
}
