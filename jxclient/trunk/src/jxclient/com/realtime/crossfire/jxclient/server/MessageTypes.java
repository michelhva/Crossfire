//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.server;

/**
 * Encapsulates the message type numbers for drawextinfo messages.
 *
 * @author Andreas Kirschbaum
 */
public class MessageTypes
{
    /** Internally used drawextinfo message type: a query message has been received. */
    public static final int MSG_TYPE_QUERY = 30;

    /** drawextinfo message type: character did read a book. */
    public static final int MSG_TYPE_BOOK = 1;
    /** drawextinfo message type: character did read a card. */
    public static final int MSG_TYPE_CARD = 2;
    /** drawextinfo message type: character did read a paper. */
    public static final int MSG_TYPE_PAPER = 3;
    /** drawextinfo message type: character did read a sign. */
    public static final int MSG_TYPE_SIGN = 4;
    /** drawextinfo message type: character did read a monument. */
    public static final int MSG_TYPE_MONUMENT = 5;
    /** drawextinfo message type: a NPC/magic mouth/altar/etc. talks. */
    public static final int MSG_TYPE_DIALOG = 6;
    /** drawextinfo message type: motd text. */
    public static final int MSG_TYPE_MOTD = 7;
    /** drawextinfo message type: general server message. */
    public static final int MSG_TYPE_ADMIN = 8;
    /** drawextinfo message type: shop related message. */
    public static final int MSG_TYPE_SHOP = 9;
    /** drawextinfo message type: response to command processing. */
    public static final int MSG_TYPE_COMMAND = 10;
    /** drawextinfo message type: attribute (stats, resistances, etc.) change
     * message. */
    public static final int MSG_TYPE_ATTRIBUTE = 11;
    /** drawextinfo message type: message related to using skills. */
    public static final int MSG_TYPE_SKILL = 12;
    /** drawextinfo message type: an object was applied. */
    public static final int MSG_TYPE_APPLY = 13;
    /** drawextinfo message type: attack related message. */
    public static final int MSG_TYPE_ATTACK = 14;
    /** drawextinfo message type: communication between players. */
    public static final int MSG_TYPE_COMMUNICATION = 15;
    /** drawextinfo message type: spell related information. */
    public static final int MSG_TYPE_SPELL = 16;
    /** drawextinfo message type: item related information. */
    public static final int MSG_TYPE_ITEM = 17;
    /** drawextinfo message type: message that does not fit in any other category. */
    public static final int MSG_TYPE_MISC = 18;
    /** drawextinfo message type: something bad is happening to the player. */
    public static final int MSG_TYPE_VICTIM = 19;

    public static final int MSG_TYPE_BOOK_CLASP_1 = 1;
    public static final int MSG_TYPE_BOOK_CLASP_2 = 2;
    public static final int MSG_TYPE_BOOK_ELEGANT_1 = 3;
    public static final int MSG_TYPE_BOOK_ELEGANT_2 = 4;
    public static final int MSG_TYPE_BOOK_QUARTO_1 = 5;
    public static final int MSG_TYPE_BOOK_QUARTO_2 = 6;
    public static final int MSG_TYPE_BOOK_SPELL_EVOKER = 8;
    public static final int MSG_TYPE_BOOK_SPELL_PRAYER = 9;
    public static final int MSG_TYPE_BOOK_SPELL_PYRO = 10;
    public static final int MSG_TYPE_BOOK_SPELL_SORCERER = 11;
    public static final int MSG_TYPE_BOOK_SPELL_SUMMONER = 12;

    /**
     * Private constructor to prevent instantiation.
     */
    private MessageTypes()
    {
    }

    /**
     * Return all defined message types.
     */
    public static int[] getAllTypes()
    {
        return new int[] {
            MSG_TYPE_BOOK,
            MSG_TYPE_CARD,
            MSG_TYPE_PAPER,
            MSG_TYPE_SIGN,
            MSG_TYPE_MONUMENT,
            MSG_TYPE_DIALOG,
            MSG_TYPE_MOTD,
            MSG_TYPE_ADMIN,
            MSG_TYPE_SHOP,
            MSG_TYPE_COMMAND,
            MSG_TYPE_ATTRIBUTE,
            MSG_TYPE_SKILL,
            MSG_TYPE_APPLY,
            MSG_TYPE_ATTACK,
            MSG_TYPE_COMMUNICATION,
            MSG_TYPE_SPELL,
            MSG_TYPE_ITEM,
            MSG_TYPE_MISC,
            MSG_TYPE_VICTIM,
        };
    }

    /**
     * Parse a string into a message type.
     *
     * @param str The string to parse.
     *
     * @return The message type.
     *
     * @throws UnknownCommandException If the string is unknown.
     */
    public static int parseMessageType(final String str) throws UnknownCommandException
    {
        if (str.equals("BOOK")) return MSG_TYPE_BOOK;
        if (str.equals("CARD")) return MSG_TYPE_CARD;
        if (str.equals("PAPER")) return MSG_TYPE_PAPER;
        if (str.equals("SIGN")) return MSG_TYPE_SIGN;
        if (str.equals("MONUMENT")) return MSG_TYPE_MONUMENT;
        if (str.equals("DIALOG")) return MSG_TYPE_DIALOG;
        if (str.equals("MOTD")) return MSG_TYPE_MOTD;
        if (str.equals("ADMIN")) return MSG_TYPE_ADMIN;
        if (str.equals("SHOP")) return MSG_TYPE_SHOP;
        if (str.equals("COMMAND")) return MSG_TYPE_COMMAND;
        if (str.equals("ATTRIBUTE")) return MSG_TYPE_ATTRIBUTE;
        if (str.equals("SKILL")) return MSG_TYPE_SKILL;
        if (str.equals("APPLY")) return MSG_TYPE_APPLY;
        if (str.equals("ATTACK")) return MSG_TYPE_ATTACK;
        if (str.equals("COMMUNICATION")) return MSG_TYPE_COMMUNICATION;
        if (str.equals("SPELL")) return MSG_TYPE_SPELL;
        if (str.equals("ITEM")) return MSG_TYPE_ITEM;
        if (str.equals("MISC")) return MSG_TYPE_MISC;
        if (str.equals("VICTIM")) return MSG_TYPE_VICTIM;
        if (str.equals("QUERY")) return MSG_TYPE_QUERY;
        throw new UnknownCommandException(str);
    }
}
