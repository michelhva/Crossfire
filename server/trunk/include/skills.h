/**
 * @file
 * Skill-related defines, including subtypes.
 */

#ifndef SKILLS_H
#define SKILLS_H

/**
 * @enum SK Skill subtypes
 *
 * This list is just a subtype <-> skill (code wise) in the
 * server translation.  In theory, the processing of the different
 * skills could be done via strncmp
 * This list doesn't really try to identify what the skills do.
 * The order of this list has no special meaning.  0 is not used
 * to denote improperly set objects.
 */
enum SK {
    SK_LOCKPICKING           = 1,   /**< Lockpicking. */
    SK_HIDING                = 2,   /**< Hiding. */
    SK_SMITHERY              = 3,   /**< Smithery. */
    SK_BOWYER                = 4,   /**< Bowyer. */
    SK_JEWELER               = 5,   /**< Jeweler. */
    SK_ALCHEMY               = 6,   /**< Alchemy. */
    SK_STEALING              = 7,   /**< Stealing. */
    SK_LITERACY              = 8,   /**< Literacy. */
    SK_BARGAINING            = 9,   /**< Bargaining. */
    SK_JUMPING               = 10,  /**< Jumping. */
    SK_DET_MAGIC             = 11,  /**< Detect magic. */
    SK_ORATORY               = 12,  /**< Oratory. */
    SK_SINGING               = 13,  /**< Singing. */
    SK_DET_CURSE             = 14,  /**< Detect curse. */
    SK_FIND_TRAPS            = 15,  /**< Find traps. */
    SK_MEDITATION            = 16,  /**< Meditation. */
    SK_PUNCHING              = 17,  /**< Punching. */
    SK_FLAME_TOUCH           = 18,  /**< Flame-touch. */
    SK_KARATE                = 19,  /**< Karate. */
    SK_CLIMBING              = 20,  /**< Climbing. */
    SK_WOODSMAN              = 21,  /**< Woodsman. */
    SK_INSCRIPTION           = 22,  /**< Inscription.*/
    SK_ONE_HANDED_WEAPON     = 23,  /**< One handed weapon. */
    SK_MISSILE_WEAPON        = 24,  /**< Missile weapon. */
    SK_THROWING              = 25,  /**< Throwing. */
    SK_USE_MAGIC_ITEM        = 26,  /**< Use magic item. */
    SK_DISARM_TRAPS          = 27,  /**< Disarm traps. */
    SK_SET_TRAP              = 28,  /**< Set traps, unused. */
    SK_THAUMATURGY           = 29,  /**< Thaumaturgy. */
    SK_PRAYING               = 30,  /**< Praying. */
    SK_CLAWING               = 31,  /**< Clawing. */
    SK_LEVITATION            = 32,  /**< Levitation. */
    SK_SUMMONING             = 33,  /**< Summoning. */
    SK_PYROMANCY             = 34,  /**< Pyromancy. */
    SK_EVOCATION             = 35,  /**< Evocation. */
    SK_SORCERY               = 36,  /**< Sorcery. */
    SK_TWO_HANDED_WEAPON     = 37,  /**< Two handed weapons. */
    SK_WRAITH_FEED           = 38,  /**< Wraith feed. */
    SK_HARVESTING            = 39,  /**< Harvesting. */
    SK_AIR_MAGIC             = 40,  /**< Air magic, unused. */
    SK_EARTH_MAGIC           = 41,  /**< Earth magic, unused. */
    SK_WATER_MAGIC           = 42,  /**< Water magic, unused. */
    SK_FIRE_MAGIC            = 43,  /**< Fire magic, unused. */
};

/**
 * This is the maximum number of skills the game may handle.
 * There is no direct mapping to the subtype, as multiple skills may share
 * the same subtype with a different name.
 */
#define MAX_SKILLS              50

/**
 * @defgroup SK_EXP_xxx Experience flags
 * This is used in the exp functions - basically what to do if
 * the player doesn't have the skill he should get exp in.
 */
/*@{*/
#define SK_EXP_ADD_SKILL        0   /**< Give the player the skill. */
#define SK_EXP_TOTAL            1   /**< Give player exp to total, no skill. */
#define SK_EXP_NONE             2   /**< Player gets nothing. */
#define SK_SUBTRACT_SKILL_EXP   3   /**< Used when removing exp. */
/*@}*/

/** True if op is using skill, false else. */
#define USING_SKILL(op, skill)  ((op)->chosen_skill && (op)->chosen_skill->subtype == skill)

/**
 * This macro is used in fix_object() to define if this is a sill
 * that should be used to calculate wc's and the like.
 */
#define IS_COMBAT_SKILL(num) \
    ((num == SK_PUNCHING) \
    || (num == SK_FLAME_TOUCH) \
    || (num == SK_KARATE) \
    || (num == SK_ONE_HANDED_WEAPON) \
    || (num == SK_MISSILE_WEAPON) \
    || (num == SK_THROWING) \
    || (num == SK_CLAWING) \
    || (num == SK_TWO_HANDED_WEAPON) \
    || (num == SK_WRAITH_FEED))

/**
 * Like IS_COMBAT_SKILL above, but instead this is used to determine
 * how many mana points the player has.
 */
#define IS_MANA_SKILL(num) \
    ((num == SK_SORCERY) \
    || (num == SK_EVOCATION) \
    || (num == SK_PYROMANCY) \
    || (num == SK_SUMMONING) \
    || (num==SK_AIR_MAGIC) \
    || (num==SK_EARTH_MAGIC) \
    || (num==SK_FIRE_MAGIC) \
    || (num==SK_WATER_MAGIC))


/**
 * Currently only one of these, but put the define here to make
 * it easier to expand it in the future */
#define IS_GRACE_SKILL(num) \
    (num == SK_PRAYING)

extern const char *skill_names[MAX_SKILLS];
extern int skill_faces[MAX_SKILLS];

extern int get_skill_client_code(const char *skill_name);

#ifdef WANT_UNARMED_SKILLS
/** Table of unarmed attack skills.  Terminated by 0.  This
 * is also the list that we should try to use skills when
 * automatically applying one for the player.
 * Note it is hardcoded in the skill_util.c that dragons always
 * want clawing if possible.
 * included in a #ifdef so we don't get bunches of warnings about
 * unused values.  it is located here instead of a .c file to make
 * updates easier and put it in a more central place - it shouldn't
 * change very often, but it make sense to have it with the enumerated
 * skill numbers above.
 * This should probably be removed and made a player preferance instead.
 */
static uint8_t unarmed_skills[] = {
    SK_KARATE,
    SK_CLAWING,
    SK_FLAME_TOUCH,
    SK_PUNCHING,
    SK_WRAITH_FEED,
    0
};

/* Just in case one file includes this more than once */
#undef WANT_UNARMED_SKILLS

#endif

#endif /* SKILLS_H */
