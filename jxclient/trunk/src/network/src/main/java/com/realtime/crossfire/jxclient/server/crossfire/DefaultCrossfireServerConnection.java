/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.account.CharacterInformation;
import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.character.ClassRaceInfo;
import com.realtime.crossfire.jxclient.character.NewCharInfo;
import com.realtime.crossfire.jxclient.faces.AskfaceFaceQueueListener;
import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.protocol.Map2;
import com.realtime.crossfire.jxclient.protocol.MessageTypes;
import com.realtime.crossfire.jxclient.protocol.UpdItem;
import com.realtime.crossfire.jxclient.server.server.DefaultServerConnection;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.socket.UnknownCommandException;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import com.realtime.crossfire.jxclient.util.HexCodec;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link CrossfireServerConnection}.
 * @author Andreas Kirschbaum
 */
public class DefaultCrossfireServerConnection extends AbstractCrossfireServerConnection {

    /**
     * The default map width when no "setup mapsize" command has been sent.
     */
    private static final int DEFAULT_MAP_WIDTH = 11;

    /**
     * The default map height when no "setup mapsize" command has been sent.
     */
    private static final int DEFAULT_MAP_HEIGHT = 11;

    /**
     * Pattern to split a string by ":".
     */
    @NotNull
    private static final Pattern PATTERN_DOT = Pattern.compile(":");

    /**
     * Pattern to split a string by "|".
     */
    @NotNull
    private static final Pattern PATTERN_BAR = Pattern.compile("\\|+");

    /**
     * Pattern to split a string by spaces.
     */
    @NotNull
    private static final Pattern PATTERN_SPACE = Pattern.compile(" ");

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_NAME = 1;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_CLASS = 2;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_RACE = 3;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_LEVEL = 4;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_FACE = 5;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_PARTY = 6;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_MAP = 7;

    /**
     * Parameter type in the "accountplayers" command.
     */
    private static final int ACL_FACE_NUM = 8;

    /**
     * Archetype name of a "replyinfo startingmap" entry.
     */
    private static final int INFO_MAP_ARCH_NAME = 1;

    /**
     * Proper name of a "replyinfo startingmap" entry.
     */
    private static final int INFO_MAP_NAME = 2;

    /**
     * Description of a "replyinfo startingmap" entry.
     */
    private static final int INFO_MAP_DESCRIPTION = 3;

    /**
     * The {@link Model} instance that is updated.
     */
    @NotNull
    private final Model model;

    /**
     * The physical server connection.
     */
    @NotNull
    private final DefaultServerConnection defaultServerConnection;

    /**
     * The map width in tiles that is negotiated with the server.
     */
    private int preferredMapWidth = 17;

    /**
     * The map height in tiles that is negotiated with the server.
     */
    private int preferredMapHeight = 13;

    /**
     * The map width that is being negotiated with the server. Set to {@code 0}
     * when not negotiating.
     */
    private int pendingMapWidth;

    /**
     * The map height that is being negotiated with the server. Set to {@code 0}
     * when not negotiating.
     */
    private int pendingMapHeight;

    /**
     * The currently active map width.
     */
    private int currentMapWidth = DEFAULT_MAP_WIDTH;

    /**
     * The currently active map height.
     */
    private int currentMapHeight = DEFAULT_MAP_HEIGHT;

    /**
     * The {@link NumLookObjects} instance for negotiating the size of the
     * ground view.
     */
    @NotNull
    private final NumLookObjects numLookObjects;

    /**
     * Buffer to build commands to send. It is shared between all sendXxx()
     * functions. It is used to synchronize these functions.
     */
    @NotNull
    private final byte[] writeBuffer = new byte[65536];

    /**
     * A byte buffer using {@link #writeBuffer} to store the data.
     */
    @NotNull
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(writeBuffer);

    /**
     * The packet id for the next "ncom" command to send.
     */
    private int packet = 1;

    /**
     * The command prefix for the "accountlogin" command.
     */
    @NotNull
    private static final byte[] ACCOUNT_LOGIN_PREFIX = {
        'a',
        'c',
        'c',
        'o',
        'u',
        'n',
        't',
        'l',
        'o',
        'g',
        'i',
        'n',
        ' ',
    };

    /**
     * The command prefix for the "accountplay" command.
     */
    @NotNull
    private static final byte[] ACCOUNT_PLAY_PREFIX = {
        'a',
        'c',
        'c',
        'o',
        'u',
        'n',
        't',
        'p',
        'l',
        'a',
        'y',
        ' ',
    };

    /**
     * The command prefix for the "accountaddplayer" command.
     */
    @NotNull
    private static final byte[] ACCOUNT_ADD_PLAYER_PREFIX = {
        'a',
        'c',
        'c',
        'o',
        'u',
        'n',
        't',
        'a',
        'd',
        'd',
        'p',
        'l',
        'a',
        'y',
        'e',
        'r',
        ' ',
    };

    /**
     * The command prefix for the "accountnew" command.
     */
    @NotNull
    private static final byte[] ACCOUNT_NEW_PREFIX = {
        'a',
        'c',
        'c',
        'o',
        'u',
        'n',
        't',
        'n',
        'e',
        'w',
        ' ',
    };

    /**
     * The command prefix for the "accountpw" command.
     */
    @NotNull
    private static final byte[] ACCOUNT_PASSWORD_PREFIX = {
        'a',
        'c',
        'c',
        'o',
        'u',
        'n',
        't',
        'p',
        'w',
        ' ',
    };

    /**
     * The command prefix for the "createplayer" command.
     */
    @NotNull
    private static final byte[] CREATE_PLAYER_PREFIX = {
        'c',
        'r',
        'e',
        'a',
        't',
        'e',
        'p',
        'l',
        'a',
        'y',
        'e',
        'r',
        ' ',
    };

    /**
     * The command prefix for the "addme" command.
     */
    @NotNull
    private static final byte[] ADDME_PREFIX = {
        'a',
        'd',
        'd',
        'm',
        'e',
        ' ',
    };

    /**
     * The command prefix for the "apply" command.
     */
    @NotNull
    private static final byte[] APPLY_PREFIX = {
        'a',
        'p',
        'p',
        'l',
        'y',
        ' ',
    };

    /**
     * The command prefix for the "askface" command.
     */
    @NotNull
    private static final byte[] ASKFACE_PREFIX = {
        'a',
        's',
        'k',
        'f',
        'a',
        'c',
        'e',
        ' ',
    };

    /**
     * The command prefix for the "examine" command.
     */
    @NotNull
    private static final byte[] EXAMINE_PREFIX = {
        'e',
        'x',
        'a',
        'm',
        'i',
        'n',
        'e',
        ' ',
    };

    /**
     * The command prefix for the "lock" command.
     */
    @NotNull
    private static final byte[] LOCK_PREFIX = {
        'l',
        'o',
        'c',
        'k',
        ' ',
    };

    /**
     * The command prefix for the "lookat" command.
     */
    @NotNull
    private static final byte[] LOOKAT_PREFIX = {
        'l',
        'o',
        'o',
        'k',
        'a',
        't',
        ' ',
    };

    /**
     * The command prefix for the "mark" command.
     */
    @NotNull
    private static final byte[] MARK_PREFIX = {
        'm',
        'a',
        'r',
        'k',
        ' ',
    };

    /**
     * The command prefix for the "move" command.
     */
    @NotNull
    private static final byte[] MOVE_PREFIX = {
        'm',
        'o',
        'v',
        'e',
        ' ',
    };

    /**
     * The command prefix for the "ncom" command.
     */
    @NotNull
    private static final byte[] NCOM_PREFIX = {
        'n',
        'c',
        'o',
        'm',
        ' ',
    };

    /**
     * The command prefix for the "reply" command.
     */
    @NotNull
    private static final byte[] REPLY_PREFIX = {
        'r',
        'e',
        'p',
        'l',
        'y',
        ' ',
    };

    /**
     * The command prefix for the "requestinfo" command.
     */
    @NotNull
    private static final byte[] REQUESTINFO_PREFIX = {
        'r',
        'e',
        'q',
        'u',
        'e',
        's',
        't',
        'i',
        'n',
        'f',
        'o',
        ' ',
    };

    /**
     * The command prefix for the "setup" command.
     */
    @NotNull
    private static final byte[] SETUP_PREFIX = {
        's',
        'e',
        't',
        'u',
        'p',
    }; // note that this command does not have a trailing space

    /**
     * The command prefix for the "toggleextendedtext" command.
     */
    @NotNull
    private static final byte[] TOGGLEEXTENDEDTEXT_PREFIX = {
        't',
        'o',
        'g',
        'g',
        'l',
        'e',
        'e',
        'x',
        't',
        'e',
        'n',
        'd',
        'e',
        'd',
        't',
        'e',
        'x',
        't',
    }; // note that this command does not have a trailing space

    /**
     * The command prefix for the "version" command.
     */
    @NotNull
    private static final byte[] VERSION_PREFIX = {
        'v',
        'e',
        'r',
        's',
        'i',
        'o',
        'n',
        ' ',
    };

    /**
     * The version information to send when connecting to the server.
     */
    @NotNull
    private final String version;

    /**
     * The appender to write protocol commands to. May be {@code null} to not
     * write anything.
     */
    @Nullable
    private final DebugWriter debugProtocol;

    /**
     * The current connection state.
     */
    @NotNull
    private ClientSocketState clientSocketState = ClientSocketState.CONNECTING;

    /**
     * The account name. Set to {@code null} if no account name is known.
     */
    @Nullable
    private String accountName;

    /**
     * The login method version supported by the server we're connected to.
     */
    private int loginMethod;

    /**
     * The {@link CrossfireUpdateMapListener} to be notified. Set to {@code
     * null} if unset.
     */
    @Nullable
    private CrossfireUpdateMapListener crossfireUpdateMapListener;

    /**
     * The {@link AskfaceFaceQueueListener AskfaceQueueListeners} to notify.
     */
    @NotNull
    private final EventListenerList2<AskfaceFaceQueueListener> askfaceFaceQueueListeners = new EventListenerList2<>();

    /**
     * If non-{@code null}: the last sent "requestinfo" command for which no
     * "replyinfo" response has been received yet.
     */
    @Nullable
    private String sendingRequestInfo;

    /**
     * Pending "requestinfo" commands that will be sent as soon {@link
     * #sendingRequestInfo} is unset.
     */
    @NotNull
    private final List<String> pendingRequestInfos = new ArrayList<>();

    /**
     * The {@link ClientSocketListener} attached to the server socket.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
    private final ClientSocketListener clientSocketListener = new ClientSocketListener() {

        @Override
        public void connecting() {
            // ignore
        }

        @Override
        public void connected() {
            DefaultCrossfireServerConnection.this.connected();
        }

        @Override
        public void packetReceived(@NotNull final ByteBuffer packet) throws UnknownCommandException {
            processPacket(packet);
        }

        @Override
        public void packetSent(@NotNull final byte[] buf, final int len) {
            // ignore
        }

        @Override
        public void disconnecting(@NotNull final String reason, final boolean isError) {
            // ignore
        }

        @Override
        public void disconnected(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param model the model instance to update
     * @param debugProtocol if non-{@code null}, write all protocol commands to
     * this writer
     * @param version the version information to send to the server when
     * connecting
     * @throws IOException if an internal error occurs
     */
    public DefaultCrossfireServerConnection(@NotNull final Model model, @Nullable final DebugWriter debugProtocol, @NotNull final String version) throws IOException {
        super(model);
        this.model = model;
        defaultServerConnection = new DefaultServerConnection(model, debugProtocol);
        this.version = version;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        this.debugProtocol = debugProtocol;
        addClientSocketListener(clientSocketListener);
        //noinspection ThisEscapedInObjectConstruction
        numLookObjects = new NumLookObjects(this, debugProtocol);
    }

    @Override
    public void setCrossfireUpdateMapListener(@Nullable final CrossfireUpdateMapListener listener) {
        if (listener != null && crossfireUpdateMapListener != null) {
            throw new IllegalStateException("listener already set to "+crossfireUpdateMapListener.getClass().getName());
        }
        crossfireUpdateMapListener = listener;
    }

    @Override
    public void start() {
        defaultServerConnection.start();
    }

    @Override
    public void stop() throws InterruptedException {
        defaultServerConnection.stop();
    }

    /**
     * Called after the server connection has been established.
     */
    private void connected() {
        pendingMapWidth = 0;
        pendingMapHeight = 0;
        numLookObjects.connected();
        setCurrentMapSize(DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT);

        fireNewMap();

        setClientSocketState(ClientSocketState.CONNECTING, ClientSocketState.VERSION);
        sendVersion(1023, 1027, version);
    }

    /**
     * Processes a received packet. This function does not avoid buffer
     * underflow exceptions when reading data from the packet. Instead, a {@code
     * try...catch} clause is used to detect invalid packets.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet cannot be parsed
     */
    private void processPacket(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        try {
            packet.mark();
            switch (packet.get()) {
            case 'a':
                switch (packet.get()) {
                case 'c':
                    //accountplayers
                    if (packet.get() != 'c') {
                        break;
                    }
                    if (packet.get() != 'o') {
                        break;
                    }
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'n') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 'p') {
                        break;
                    }
                    if (packet.get() != 'l') {
                        break;
                    }
                    if (packet.get() != 'a') {
                        break;
                    }
                    if (packet.get() != 'y') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'r') {
                        break;
                    }
                    if (packet.get() != 's') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("recv accountplayers");
                    }
                    processAccountPlayers(packet);
                    return;

                case 'd':
                    if (packet.get() != 'd') {
                        break;
                    }
                    switch (packet.get()) {
                    case 'k':
                        if (packet.get() != 'n') {
                            break;
                        }
                        if (packet.get() != 'o') {
                            break;
                        }
                        if (packet.get() != 'w') {
                            break;
                        }
                        if (packet.get() != 'l') {
                            break;
                        }
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != 'd') {
                            break;
                        }
                        if (packet.get() != 'g') {
                            break;
                        }
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processAddKnowledge(packet);
                        return;

                    case 'm':
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != '_') {
                            break;
                        }
                        switch (packet.get()) {
                        case 'f':
                            if (packet.get() != 'a') {
                                break;
                            }
                            if (packet.get() != 'i') {
                                break;
                            }
                            if (packet.get() != 'l') {
                                break;
                            }
                            if (packet.get() != 'e') {
                                break;
                            }
                            if (packet.get() != 'd') {
                                break;
                            }
                            if (packet.hasRemaining()) {
                                break;
                            }
                            if (debugProtocol != null) {
                                debugProtocol.debugProtocolWrite("recv addme_failed");
                            }
                            processAddmeFailed(packet);
                            return;

                        case 's':
                            if (packet.get() != 'u') {
                                break;
                            }
                            if (packet.get() != 'c') {
                                break;
                            }
                            if (packet.get() != 'c') {
                                break;
                            }
                            if (packet.get() != 'e') {
                                break;
                            }
                            if (packet.get() != 's') {
                                break;
                            }
                            if (packet.get() != 's') {
                                break;
                            }
                            if (packet.hasRemaining()) {
                                break;
                            }
                            if (debugProtocol != null) {
                                debugProtocol.debugProtocolWrite("recv addme_success");
                            }
                            processAddmeSuccess(packet);
                            return;
                        }
                        break;

                    case 'q':
                        if (packet.get() != 'u') {
                            break;
                        }
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != 's') {
                            break;
                        }
                        if (packet.get() != 't') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processAddQuest(packet);
                        return;

                    case 's':
                        if (packet.get() != 'p') {
                            break;
                        }
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != 'l') {
                            break;
                        }
                        if (packet.get() != 'l') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processAddSpell(packet);
                        return;
                    }
                    break;

                case 'n':
                    if (packet.get() != 'i') {
                        break;
                    }
                    if (packet.get() != 'm') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processAnim(packet);
                    return;
                }
                break;

            case 'c':
                if (packet.get() != 'o') {
                    break;
                }
                if (packet.get() != 'm') {
                    break;
                }
                if (packet.get() != 'c') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processComc(packet);
                return;

            case 'd':
                switch (packet.get()) {
                case 'e':
                    if (packet.get() != 'l') {
                        break;
                    }
                    switch (packet.get()) {
                    case 'i':
                        switch (packet.get()) {
                        case 'n':
                            if (packet.get() != 'v') {
                                break;
                            }
                            if (packet.get() != ' ') {
                                break;
                            }
                            processDelInv(packet);
                            return;

                        case 't':
                            if (packet.get() != 'e') {
                                break;
                            }
                            if (packet.get() != 'm') {
                                break;
                            }
                            if (packet.get() != ' ') {
                                break;
                            }
                            processDelItem(packet);
                            return;
                        }
                        break;

                    case 's':
                        if (packet.get() != 'p') {
                            break;
                        }
                        if (packet.get() != 'e') {
                            break;
                        }
                        if (packet.get() != 'l') {
                            break;
                        }
                        if (packet.get() != 'l') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processDelSpell(packet);
                        return;
                    }
                    break;

                case 'r':
                    if (packet.get() != 'a') {
                        break;
                    }
                    if (packet.get() != 'w') {
                        break;
                    }
                    switch (packet.get()) {
                    case 'e':
                        if (packet.get() != 'x') {
                            break;
                        }
                        if (packet.get() != 't') {
                            break;
                        }
                        if (packet.get() != 'i') {
                            break;
                        }
                        if (packet.get() != 'n') {
                            break;
                        }
                        if (packet.get() != 'f') {
                            break;
                        }
                        if (packet.get() != 'o') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processDrawExtInfo(packet);
                        return;

                    case 'i':
                        if (packet.get() != 'n') {
                            break;
                        }
                        if (packet.get() != 'f') {
                            break;
                        }
                        if (packet.get() != 'o') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processDrawInfo(packet);
                        return;
                    }
                    break;
                }
                break;

            case 'E':
                if (packet.get() != 'x') {
                    break;
                }
                if (packet.get() != 't') {
                    break;
                }
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'n') {
                    break;
                }
                if (packet.get() != 'd') {
                    break;
                }
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'd') {
                    break;
                }
                switch (packet.get()) {
                case 'I':
                    if (packet.get() != 'n') {
                        break;
                    }
                    if (packet.get() != 'f') {
                        break;
                    }
                    if (packet.get() != 'o') {
                        break;
                    }
                    if (packet.get() != 'S') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processExtendedInfoSet(packet);
                    return;

                case 'T':
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'x') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 'S') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processExtendedTextSet(packet);
                    return;
                }
                break;

            case 'f':
                if (packet.get() != 'a') {
                    break;
                }
                switch (packet.get()) {
                case 'c':
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != '2') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processFace2(packet);
                    return;

                case 'i':
                    if (packet.get() != 'l') {
                        break;
                    }
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'r') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processFailure(packet);
                    return;
                }
                break;

            case 'g':
                if (packet.get() != 'o') {
                    break;
                }
                if (packet.get() != 'o') {
                    break;
                }
                if (packet.get() != 'd') {
                    break;
                }
                if (packet.get() != 'b') {
                    break;
                }
                if (packet.get() != 'y') {
                    break;
                }
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processGoodbye(packet);
                return;

            case 'i':
                switch (packet.get()) {
                case 'm':
                    if (packet.get() != 'a') {
                        break;
                    }
                    if (packet.get() != 'g') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != '2') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processImage2(packet);
                    return;

                case 't':
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'm') {
                        break;
                    }
                    if (packet.get() != '2') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processItem2(packet);
                    return;
                }
                break;

            case 'm':
                switch (packet.get()) {
                case 'a':
                    switch (packet.get()) {
                    case 'g':
                        if (packet.get() != 'i') {
                            break;
                        }
                        if (packet.get() != 'c') {
                            break;
                        }
                        if (packet.get() != 'm') {
                            break;
                        }
                        if (packet.get() != 'a') {
                            break;
                        }
                        if (packet.get() != 'p') {
                            break;
                        }
                        if (packet.get() != ' ') {
                            break;
                        }
                        processMagicMap(packet);
                        return;

                    case 'p':
                        switch (packet.get()) {
                        case '2':
                            if (packet.get() != ' ') {
                                break;
                            }
                            processMap2(packet);
                            return;

                        case 'e':
                            if (packet.get() != 'x') {
                                break;
                            }
                            if (packet.get() != 't') {
                                break;
                            }
                            if (packet.get() != 'e') {
                                break;
                            }
                            if (packet.get() != 'n') {
                                break;
                            }
                            if (packet.get() != 'd') {
                                break;
                            }
                            if (packet.get() != 'e') {
                                break;
                            }
                            if (packet.get() != 'd') {
                                break;
                            }
                            if (packet.get() != ' ') {
                                break;
                            }
                            processMapExtended(packet);
                            return;
                        }
                        break;
                    }
                    break;

                case 'u':
                    if (packet.get() != 's') {
                        break;
                    }
                    if (packet.get() != 'i') {
                        break;
                    }
                    if (packet.get() != 'c') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processMusic(packet);
                    return;
                }
                break;

            case 'n':
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'w') {
                    break;
                }
                if (packet.get() != 'm') {
                    break;
                }
                if (packet.get() != 'a') {
                    break;
                }
                if (packet.get() != 'p') {
                    break;
                }
                processNewMap(packet);
                return;

            case 'p':
                switch (packet.get()) {
                case 'i':
                    if (packet.get() != 'c') {
                        break;
                    }
                    if (packet.get() != 'k') {
                        break;
                    }
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'p') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processPickup(packet);
                    return;

                case 'l':
                    if (packet.get() != 'a') {
                        break;
                    }
                    if (packet.get() != 'y') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'r') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processPlayer(packet);
                    return;
                }
                break;

            case 'q':
                if (packet.get() != 'u') {
                    break;
                }
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'r') {
                    break;
                }
                if (packet.get() != 'y') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processQuery(packet);
                return;

            case 'r':
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'p') {
                    break;
                }
                if (packet.get() != 'l') {
                    break;
                }
                if (packet.get() != 'y') {
                    break;
                }
                if (packet.get() != 'i') {
                    break;
                }
                if (packet.get() != 'n') {
                    break;
                }
                if (packet.get() != 'f') {
                    break;
                }
                if (packet.get() != 'o') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processReplyInfo(packet);
                return;

            case 's':
                switch (packet.get()) {
                case 'e':
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'p') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processSetup(packet);
                    return;

                case 'm':
                    if (packet.get() != 'o') {
                        break;
                    }
                    if (packet.get() != 'o') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 'h') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processSmooth(packet);
                    return;

                case 'o':
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'n') {
                        break;
                    }
                    if (packet.get() != 'd') {
                        break;
                    }
                    switch (packet.get()) {
                    case ' ':
                        processSound(packet);
                        return;

                    case '2':
                        if (packet.get() != ' ') {
                            break;
                        }
                        processSound2(packet);
                        return;
                    }
                    break;

                case 't':
                    if (packet.get() != 'a') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 's') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processStats(packet);
                    return;
                }
                break;

            case 't':
                if (packet.get() != 'i') {
                    break;
                }
                if (packet.get() != 'c') {
                    break;
                }
                if (packet.get() != 'k') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processTick(packet);
                return;

            case 'u':
                if (packet.get() != 'p') {
                    break;
                }
                if (packet.get() != 'd') {
                    break;
                }
                switch (packet.get()) {
                case 'i':
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'm') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processUpdItem(packet);
                    return;

                case 'q':
                    if (packet.get() != 'u') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 's') {
                        break;
                    }
                    if (packet.get() != 't') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processUpdQuest(packet);
                    return;

                case 's':
                    if (packet.get() != 'p') {
                        break;
                    }
                    if (packet.get() != 'e') {
                        break;
                    }
                    if (packet.get() != 'l') {
                        break;
                    }
                    if (packet.get() != 'l') {
                        break;
                    }
                    if (packet.get() != ' ') {
                        break;
                    }
                    processUpdSpell(packet);
                    return;
                }
                break;

            case 'v':
                if (packet.get() != 'e') {
                    break;
                }
                if (packet.get() != 'r') {
                    break;
                }
                if (packet.get() != 's') {
                    break;
                }
                if (packet.get() != 'i') {
                    break;
                }
                if (packet.get() != 'o') {
                    break;
                }
                if (packet.get() != 'n') {
                    break;
                }
                if (packet.get() != ' ') {
                    break;
                }
                processVersion(packet);
                return;
            }
        } catch (final IllegalArgumentException ex) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("IllegalArgumentException while command parsing: "+ex+"\n"+hexDump(packet), ex);
            }
        } catch (final BufferUnderflowException ex) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("BufferUnderflowException while command parsing: "+ex+"\n"+hexDump(packet), ex);
            }
        } catch (final ArrayIndexOutOfBoundsException ex) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("ArrayIndexOutOfBoundsException while command parsing: "+ex+"\n"+hexDump(packet), ex);
            }
        } catch (final StringIndexOutOfBoundsException ex) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("StringIndexOutOfBoundsException while command parsing: "+ex+"\n"+hexDump(packet), ex);
            }
        } catch (final UnknownCommandException ex) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("UnknownCommandException while command parsing: "+ex+"\n"+hexDump(packet), ex);
            }
            throw ex;
        }

        packet.position(0);
        final String command = extractCommand(packet);
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv invalid command: "+command+"\n"+hexDump(packet));
        }
        throw new UnknownCommandException("Cannot parse command: "+command);
    }

    /**
     * Processes the payload data for a map2 coordinate command.
     * @param packet the packet contents
     * @param x the x-coordinate of the currently processed square
     * @param y the y-coordinate of the currently processed square
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2Coordinate(@NotNull final ByteBuffer packet, final int x, final int y) throws UnknownCommandException {
        while (true) {
            final int lenType = getInt1(packet);
            if (lenType == 0xFF) {
                break;
            }

            final int len = (lenType>>5)&7;
            final int type = lenType&31;
            switch (type) {
            case Map2.COORD_CLEAR_SPACE:
                cmdMap2CoordinateClearSpace(x, y, len);
                break;

            case Map2.COORD_DARKNESS:
                cmdMap2CoordinateDarkness(packet, x, y, len);
                break;

            case Map2.COORD_LAYER0:
            case Map2.COORD_LAYER1:
            case Map2.COORD_LAYER2:
            case Map2.COORD_LAYER3:
            case Map2.COORD_LAYER4:
            case Map2.COORD_LAYER5:
            case Map2.COORD_LAYER6:
            case Map2.COORD_LAYER7:
            case Map2.COORD_LAYER8:
            case Map2.COORD_LAYER9:
                cmdMap2CoordinateLayer(packet, x, y, len, type-Map2.COORD_LAYER0);
                break;
            }
        }
    }

    /**
     * Processes the payload data for a map2 coordinate "clear_space"
     * sub-command.
     * @param x the x-coordinate of the currently processed square
     * @param y the y-coordinate of the currently processed square
     * @param len the payload length
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2CoordinateClearSpace(final int x, final int y, final int len) throws UnknownCommandException {
        if (len != 0) {
            throw new UnknownCommandException("map2 command contains clear command with length "+len);
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 "+x+"/"+y+" clear");
        }
        fireMapClear(x, y);
    }

    /**
     * Processes the payload data for a map2 coordinate "darkness" sub-command.
     * @param packet the packet contents
     * @param x the x-coordinate of the currently processed square
     * @param y the y-coordinate of the currently processed square
     * @param len the payload length
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2CoordinateDarkness(@NotNull final ByteBuffer packet, final int x, final int y, final int len) throws UnknownCommandException {
        if (len != 1) {
            throw new UnknownCommandException("map2 command contains darkness command with length "+len);
        }
        final int darkness = getInt1(packet);
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 "+x+"/"+y+" darkness="+darkness);
        }
        fireMapDarkness(x, y, darkness);
    }

    /**
     * Processes the payload data for a map2 coordinate "layer" sub-command.
     * @param packet the packet contents
     * @param x the x-coordinate of the currently processed square
     * @param y the y-coordinate of the currently processed square
     * @param len the payload length
     * @param layer the layer to update
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2CoordinateLayer(@NotNull final ByteBuffer packet, final int x, final int y, final int len, final int layer) throws UnknownCommandException {
        if (len < 2) {
            throw new UnknownCommandException("map2 command contains image command with length "+len);
        }
        final Location location = new Location(x, y, layer);
        final int face = getInt2(packet);
        if ((face&Map2.FACE_ANIMATION) == 0) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv map2 "+location+" face="+face);
            }
            fireMapFace(location, face);
        } else {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv map2 "+location+" anim="+(face&Map2.ANIM_MASK)+" type="+((face>>Map2.ANIM_TYPE_SHIFT)&Map2.ANIM_TYPE_MASK));
            }
            fireMapAnimation(location, face&Map2.ANIM_MASK, (face>>Map2.ANIM_TYPE_SHIFT)&Map2.ANIM_TYPE_MASK);
        }
        if (len == 3) {
            cmdMap2CoordinateLayer3(packet, location, face);
        } else if (len == 4) {
            cmdMap2CoordinateLayer4(packet, location, face);
        } else if (len != 2) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv map2 "+x+"/"+y+"/"+layer+" <invalid>");
            }
            throw new UnknownCommandException("map2 command contains image command with length "+len);
        }
    }

    /**
     * Processes the additional payload data for a map2 coordinate "layer"
     * sub-command having 4 bytes payload.
     * @param packet the packet contents
     * @param location the location
     * @param face the face number
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2CoordinateLayer3(@NotNull final ByteBuffer packet, @NotNull final Location location, final int face) throws UnknownCommandException {
        if (face == 0) {
            throw new UnknownCommandException("map2 command contains smoothing or animation information for empty face");
        }

        if ((face&Map2.FACE_ANIMATION) == 0) {
            final int smooth = getInt1(packet);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv map2 "+location+" smooth="+smooth);
            }
            fireMapSmooth(location, smooth);
        } else {
            final int animSpeed = getInt1(packet);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv map2 "+location+" anim_speed="+animSpeed);
            }
            fireMapAnimationSpeed(location, animSpeed);
        }
    }

    /**
     * Processes the additional payload data for a map2 coordinate "layer"
     * sub-command having 4 bytes payload.
     * @param packet the packet contents
     * @param location the location
     * @param face the face number
     * @throws UnknownCommandException if the command cannot be parsed
     */
    private void cmdMap2CoordinateLayer4(@NotNull final ByteBuffer packet, @NotNull final Location location, final int face) throws UnknownCommandException {
        if (face == 0) {
            throw new UnknownCommandException("map2 command contains smoothing or animation information for empty face");
        }

        final int animSpeed = getInt1(packet);
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 "+location+" anim_speed="+animSpeed);
        }
        fireMapAnimationSpeed(location, animSpeed);

        final int smooth = getInt1(packet);
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 "+location+" smooth="+smooth);
        }
        fireMapSmooth(location, smooth);
    }

    /**
     * Requests a change of the map size from the server.
     * @param mapWidth the map width to request
     * @param mapHeight the map height to request
     */
    private void negotiateMapSize(final int mapWidth, final int mapHeight) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("negotiateMapSize: "+mapWidth+"x"+mapHeight);
        }

        if (clientSocketState == ClientSocketState.CONNECTING || clientSocketState == ClientSocketState.VERSION || clientSocketState == ClientSocketState.CONNECT_FAILED) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("negotiateMapSize: clientSocketState="+clientSocketState+", ignoring");
            }
            return;
        }
        if (pendingMapWidth != 0 || pendingMapHeight != 0) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("negotiateMapSize: already negotiating, ignoring");
            }
            return;
        }
        if (currentMapWidth == mapWidth && currentMapHeight == mapHeight) {
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("negotiateMapSize: same as current map size, ignoring");
            }
            return;
        }
        pendingMapWidth = mapWidth;
        pendingMapHeight = mapHeight;
        sendSetup("mapsize "+pendingMapWidth+"x"+pendingMapHeight);
    }

    /**
     * Handles the replyinfo server to client command.
     * @param infoType the info_type parameter
     * @param packet the packet payload data
     * @throws IOException if an I/O error occurs
     * @throws UnknownCommandException if the packet cannot be parsed
     */
    private void cmdReplyinfo(@NotNull final String infoType, final ByteBuffer packet) throws IOException, UnknownCommandException {
        synchronized (writeBuffer) {
            if (sendingRequestInfo != null && sendingRequestInfo.equals(infoType)) {
                sendingRequestInfo = null;
            }
        }
        sendPendingRequestInfo();

        switch (infoType) {
        case "image_info":
            processImageInfoReplyinfo(packet);
            break;

        case "skill_info":
            processSkillInfoReplyinfo(packet);
            break;

        case "exp_table":
            processExpTableReplyinfo(packet);
            break;

        case "knowledge_info":
            processKnowledgeInfoReplyinfo(packet);
            break;

        case "startingmap":
            processStartingMapReplyinfo(packet);
            break;

        case "race_list":
            processRaceListReplyinfo(packet);
            break;

        case "class_list":
            processClassListReplyinfo(packet);
            break;

        case "race_info":
            processClassRaceInfoReplyinfo(packet, true);
            break;

        case "class_info":
            processClassRaceInfoReplyinfo(packet, false);
            break;

        case "newcharinfo":
            processNewCharInfoReplyinfo(packet);
            break;

        default:
            System.err.println("Ignoring unexpected replyinfo type '"+infoType+"'.");
            break;
        }
    }

    /**
     * Processes a "replyinfo image_info" block.
     * @param packet the packet to process
     * @throws IOException if the packet cannot be parsed
     */
    private static void processImageInfoReplyinfo(@NotNull final ByteBuffer packet) throws IOException {
        final byte[] data = new byte[packet.remaining()];
        packet.get(data);
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            try (InputStreamReader isr = new InputStreamReader(is)) {
                try (BufferedReader d = new BufferedReader(isr)) {
                    final String info = d.readLine();
                    if (info == null) {
                        throw new IOException("Truncated parameter in image_info");
                    }
                    //noinspection UnusedAssignment
                    final int nrPics = Integer.parseInt(info);
                    // XXX: replyinfo image_info not implemented
                }
            }
        }
    }

    /**
     * Processes a "replyinfo skill_info" block.
     * @param packet the packet to process
     * @throws IOException if the packet cannot be parsed
     */
    private void processSkillInfoReplyinfo(@NotNull final ByteBuffer packet) throws IOException {
        model.getSkillSet().clearSkills();
        final byte[] data = new byte[packet.remaining()];
        packet.get(data);
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            try (InputStreamReader isr = new InputStreamReader(is)) {
                try (BufferedReader d = new BufferedReader(isr)) {
                    while (true) {
                        final CharSequence r = d.readLine();
                        if (r == null) {
                            break;
                        }

                        final String[] sk = PATTERN_DOT.split(r, 3);
                        if (sk.length < 2 || sk.length > 3) {
                            System.err.println("Ignoring skill definition for invalid skill: "+r+".");
                            continue;
                        }

                        final int skillId;
                        try {
                            skillId = Integer.parseInt(sk[0]);
                        } catch (final NumberFormatException ignored) {
                            System.err.println("Ignoring skill definition for invalid skill: "+r+".");
                            continue;
                        }

                        if (skillId < Stats.CS_STAT_SKILLINFO || skillId >= Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS) {
                            System.err.println("Ignoring skill definition for invalid skill id "+skillId+": "+r+".");
                            continue;
                        }

                        int face = -1;
                        if (sk.length > 2) {
                            try {
                                face = Integer.parseInt(sk[2]);
                            } catch (final NumberFormatException ignored) {
                                System.err.println("Ignoring skill definition for invalid face: "+r+".");
                                continue;
                            }
                        }
                        model.getSkillSet().addSkill(skillId, sk[1], face);
                    }
                }
            }
        }
    }

    /**
     * Processes a "replyinfo exp_table" block.
     * @param packet the packet to process
     */
    private void processExpTableReplyinfo(@NotNull final ByteBuffer packet) {
        final int numLevels = getInt2(packet);
        final long[] expTable = new long[numLevels];
        for (int level = 1; level < numLevels; level++) {
            expTable[level] = getInt8(packet);
        }
        if (packet.hasRemaining()) {
            System.err.println("Ignoring excess data at end of exp_table");
        }

        model.getExperienceTable().setExpTable(expTable);

        if (loginMethod == 0) {
            setClientSocketState(ClientSocketState.REQUESTINFO, ClientSocketState.ADDME);
            sendAddme();
        } else {
            setClientSocketState(ClientSocketState.REQUESTINFO, ClientSocketState.ACCOUNT_INFO);
            fireManageAccount();
        }
    }

    /**
     * Processes a "replyinfo knowledge_info" block.
     * @param packet the packet to process
     * @throws IOException if the packet cannot be parsed
     */
    private void processKnowledgeInfoReplyinfo(@NotNull final ByteBuffer packet) throws IOException {
        model.getKnowledgeManager().clearTypes();
        final byte[] data = new byte[packet.remaining()];
        packet.get(data);
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            try (InputStreamReader isr = new InputStreamReader(is)) {
                try (BufferedReader d = new BufferedReader(isr)) {
                    while (true) {
                        final CharSequence r = d.readLine();
                        if (r == null) {
                            break;
                        }

                        final String[] sk = PATTERN_DOT.split(r);
                        if (sk.length != 4) {
                            System.err.println("Ignoring knowledge definition for invalid knowledge: "+r+".");
                            continue;
                        }

                        final int face;
                        try {
                            face = Integer.parseInt(sk[2]);
                        } catch (final NumberFormatException ignored) {
                            System.err.println("Ignoring knowledge definition for invalid face: "+r+".");
                            continue;
                        }

                        model.getKnowledgeManager().addKnowledgeType(sk[0], sk[1], face, sk[3].equals("1"));
                    }
                }
            }
        }
    }

    /**
     * Processes a "replyinfo startingmap" block.
     * @param packet the packet to process
     */
    private void processStartingMapReplyinfo(@NotNull final ByteBuffer packet) {
        final StartingMapBuilder sb = new StartingMapBuilder();
        while (packet.hasRemaining()) {
            final int type = getInt1(packet);
            final int length = getInt2(packet);
            switch (type) {
            case INFO_MAP_ARCH_NAME:
                sb.setArchName(getString(packet, length));
                break;

            case INFO_MAP_NAME:
                sb.setName(getString(packet, length));
                break;

            case INFO_MAP_DESCRIPTION:
                sb.setDescription(getString(packet, length));
                break;

            default:
                System.err.println("Ignoring startingmap type "+type);
                break;
            }
        }
        model.getNewCharacterInformation().setStartingMapList(sb.finish());
    }

    /**
     * Processes a "replyinfo race_list" block.
     * @param packet the packet to process
     */
    private void processRaceListReplyinfo(@NotNull final ByteBuffer packet) {
        while (packet.remaining() > 0 && packet.get(packet.position()) == '|') {
            packet.get();
        }
        final CharSequence raceList = getString(packet, packet.remaining());
        final String[] races = PATTERN_BAR.split(raceList);
        model.getNewCharacterInformation().setRaceList(races);

        for (String race : races) {
            sendQueuedRequestinfo("race_info "+race);
        }
    }

    /**
     * Processes a "replyinfo class_list" block.
     * @param packet the packet to process
     */
    private void processClassListReplyinfo(@NotNull final ByteBuffer packet) {
        while (packet.remaining() > 0 && packet.get(packet.position()) == '|') {
            packet.get();
        }
        final CharSequence classList = getString(packet, packet.remaining());
        final String[] classes = PATTERN_BAR.split(classList);
        model.getNewCharacterInformation().setClassList(classes);

        for (String class_ : classes) {
            sendQueuedRequestinfo("class_info "+class_);
        }
    }

    /**
     * Processes a "replyinfo race_info" or "replyinfo class_info" block.
     * @param packet the packet to process
     * @param raceInfo if set, a "race_info" packet is parsed; if unset, a
     * "class_info" packet is parsed
     * @throws UnknownCommandException if the packet cannot be parsed
     */
    private void processClassRaceInfoReplyinfo(@NotNull final ByteBuffer packet, final boolean raceInfo) throws UnknownCommandException {
        final String raceName = getStringDelimiter(packet, '\n');
        final ClassRaceInfoBuilder rb = new ClassRaceInfoBuilder(raceName);
        while (packet.hasRemaining()) {
            final String type = getStringDelimiter(packet, ' ');
            switch (type) {
            case "name":
                rb.setName(getString(packet, getInt1(packet)));
                break;

            case "msg":
                rb.setMsg(getString(packet, getInt2(packet)));
                break;

            case "stats":
                parseClassRaceInfoStats(packet, rb);
                break;

            case "choice":
                parseClassRaceInfoChoice(packet, rb);
                break;

            default:
                System.err.println("Ignoring race_info type "+type);
                break;
            }
        }
        final ClassRaceInfo classRaceInfo = rb.finish();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv replyinfo "+(raceInfo ? "race_info" : "class_info")+" "+classRaceInfo);
        }
        if (raceInfo) {
            model.getNewCharacterInformation().addRaceInfo(classRaceInfo);
        } else {
            model.getNewCharacterInformation().addClassInfo(classRaceInfo);
        }
    }

    /**
     * Processes a "replyinfo newcharinfo" block.
     * @param packet the packet to process
     * @throws UnknownCommandException if the packet cannot be parsed
     */
    private void processNewCharInfoReplyinfo(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final NewCharInfoBuilder newCharInfoBuilder = new NewCharInfoBuilder();
        while (packet.hasRemaining()) {
            final int len = getInt1(packet)-1;
            final String line = getString(packet, len);
            getInt1(packet); // skip trailing \0 byte

            final String[] tokens = PATTERN_SPACE.split(line, 3);
            if (tokens.length != 3) {
                throw new UnknownCommandException("syntax error in replyinfo newcharinfo: "+line);
            }
            final String typeString = tokens[0];
            final String variableName = tokens[1];
            final String values = tokens[2];
            switch (typeString) {
            case "R":
                parseNewCharInfoValue(newCharInfoBuilder, true, variableName, values);
                break;

            case "O":
                parseNewCharInfoValue(newCharInfoBuilder, false, variableName, values);
                break;

            case "V":
                parseNewCharInfoValues(newCharInfoBuilder, variableName, values);
                break;

            case "I":
                parseNewCharInfoInformational(variableName, values);
                break;

            default:
                throw new UnknownCommandException("unknown type '"+typeString+"' in replyinfo newcharinfo: "+line);
            }
        }
        final NewCharInfo newCharInfo = newCharInfoBuilder.finish();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv replyinfo newcharinfo "+newCharInfo);
        }
        model.getNewCharacterInformation().setNewCharInfo(newCharInfo);
    }

    /**
     * Parses an 'R' or 'O' entry of a "replyinfo newcharinfo" packet.
     * @param newCharInfoBuilder the new char info builder instance to update
     * @param required whether the entry is required or optional
     * @param variableName the variable name of the entry
     * @param values the values of the variable
     * @throws UnknownCommandException if the entry cannot be parsed
     */
    private static void parseNewCharInfoValue(@NotNull final NewCharInfoBuilder newCharInfoBuilder, final boolean required, @NotNull final String variableName, @NotNull final String values) throws UnknownCommandException {
        if (variableName.equals("race")) {
            if (!values.equals("requestinfo")) {
                throw new UnknownCommandException(variableName+"="+values+" is not supported in replyinfo newcharinfo");
            }
            newCharInfoBuilder.setRaceChoice();
        } else if (variableName.equals("class")) {
            if (!values.equals("requestinfo")) {
                throw new UnknownCommandException(variableName+"="+values+" is not supported in replyinfo newcharinfo");
            }
            newCharInfoBuilder.setClassChoice();
        } else if (variableName.equals("startingmap")) {
            if (!values.equals("requestinfo")) {
                throw new UnknownCommandException(variableName+"="+values+" is not supported in replyinfo newcharinfo");
            }
            newCharInfoBuilder.setStartingMapChoice();
        } else if (!required) {
            System.err.println("unknown variable name '"+variableName+"' in replyinfo newcharinfo");
        } else {
            throw new UnknownCommandException("unknown variable name '"+variableName+"' in replyinfo newcharinfo");
        }
    }

    /**
     * Parses a 'V' entry of a "replyinfo newcharinfo" packet.
     * @param newCharInfoBuilder the new char info builder instance to update
     * @param variableName the variable name of the entry
     * @param values the values of the variable
     * @throws UnknownCommandException if the entry cannot be parsed
     */
    private static void parseNewCharInfoValues(@NotNull final NewCharInfoBuilder newCharInfoBuilder, @NotNull final String variableName, @NotNull final String values) throws UnknownCommandException {
        switch (variableName) {
        case "points":
            final int points;
            try {
                points = Integer.parseInt(values);
            } catch (final NumberFormatException ignored) {
                throw new UnknownCommandException("'"+variableName+"' variable in replyinfo newcharinfo has invalid value '"+values+"'.");
            }
            newCharInfoBuilder.setPoints(points);
            break;

        case "statrange":
            final String[] tmp = PATTERN_SPACE.split(values, 2);
            if (tmp.length != 2) {
                throw new UnknownCommandException("'"+variableName+"' variable in replyinfo newcharinfo has invalid value '"+values+"'.");
            }
            final int minValue;
            final int maxValue;
            try {
                minValue = Integer.parseInt(tmp[0]);
                maxValue = Integer.parseInt(tmp[1]);
            } catch (final NumberFormatException ignored) {
                throw new UnknownCommandException("'"+variableName+"' variable in replyinfo newcharinfo has invalid value '"+values+"'.");
            }
            newCharInfoBuilder.setStatRange(minValue, maxValue);
            break;

        case "statname":
            newCharInfoBuilder.setStatNames(PATTERN_SPACE.split(values));
            break;

        default:
            throw new UnknownCommandException("unknown variable name '"+variableName+"' in replyinfo newcharinfo");
        }
    }

    /**
     * Parses an 'I' entry of a "replyinfo newcharinfo" packet.
     * @param variableName the variable name of the entry
     * @param values the values of the variable
     */
    private static void parseNewCharInfoInformational(@NotNull final String variableName, @NotNull final String values) {
        System.err.println("ignoring informational "+variableName+"="+values+" in replyinfo newcharinfo");
    }

    /**
     * Parses a "stats" entry of a "replyinfo race_info" or "replyinfo
     * class_info" packet.
     * @param packet the packet's contents
     * @param rb the class race info builder to update
     * @throws UnknownCommandException if the packet cannot be parsed
     */
    private static void parseClassRaceInfoStats(@NotNull final ByteBuffer packet, @NotNull final ClassRaceInfoBuilder rb) throws UnknownCommandException {
        while (packet.hasRemaining()) {
            final int statNo = getInt1(packet);
            switch (statNo) {
            case 0:
                return;

            case Stats.CS_STAT_HP:
            case Stats.CS_STAT_MAXHP:
            case Stats.CS_STAT_SP:
            case Stats.CS_STAT_MAXSP:
            case Stats.CS_STAT_STR:
            case Stats.CS_STAT_INT:
            case Stats.CS_STAT_WIS:
            case Stats.CS_STAT_DEX:
            case Stats.CS_STAT_CON:
            case Stats.CS_STAT_CHA:
            case Stats.CS_STAT_LEVEL:
            case Stats.CS_STAT_WC:
            case Stats.CS_STAT_AC:
            case Stats.CS_STAT_DAM:
            case Stats.CS_STAT_ARMOUR:
            case Stats.CS_STAT_FOOD:
            case Stats.CS_STAT_POW:
            case Stats.CS_STAT_GRACE:
            case Stats.CS_STAT_MAXGRACE:
            case Stats.CS_STAT_FLAGS:
            case Stats.CS_STAT_RACE_STR:
            case Stats.CS_STAT_RACE_INT:
            case Stats.CS_STAT_RACE_WIS:
            case Stats.CS_STAT_RACE_DEX:
            case Stats.CS_STAT_RACE_CON:
            case Stats.CS_STAT_RACE_CHA:
            case Stats.CS_STAT_RACE_POW:
            case Stats.CS_STAT_BASE_STR:
            case Stats.CS_STAT_BASE_INT:
            case Stats.CS_STAT_BASE_WIS:
            case Stats.CS_STAT_BASE_DEX:
            case Stats.CS_STAT_BASE_CON:
            case Stats.CS_STAT_BASE_CHA:
            case Stats.CS_STAT_BASE_POW:
            case Stats.CS_STAT_APPLIED_STR:
            case Stats.CS_STAT_APPLIED_INT:
            case Stats.CS_STAT_APPLIED_WIS:
            case Stats.CS_STAT_APPLIED_DEX:
            case Stats.CS_STAT_APPLIED_CON:
            case Stats.CS_STAT_APPLIED_CHA:
            case Stats.CS_STAT_APPLIED_POW:
            case Stats.CS_STAT_GOLEM_HP:
            case Stats.CS_STAT_GOLEM_MAXHP:
                final short int2Param = (short)getInt2(packet);
                rb.setStatAdjustment(statNo, int2Param);
                break;

            case Stats.CS_STAT_EXP:
            case Stats.CS_STAT_SPEED:
            case Stats.CS_STAT_WEAP_SP:
            case Stats.CS_STAT_WEIGHT_LIM:
            case Stats.CS_STAT_SPELL_ATTUNE:
            case Stats.CS_STAT_SPELL_REPEL:
            case Stats.CS_STAT_SPELL_DENY:
                final int int4Param = getInt4(packet);
                rb.setStatAdjustment(statNo, int4Param);
                break;

            case Stats.CS_STAT_EXP64:
                final long int8Param = getInt8(packet);
                rb.setStatAdjustment(statNo, int8Param);
                break;

            case Stats.CS_STAT_RANGE:
            case Stats.CS_STAT_TITLE:
                final int length = getInt1(packet);
                final String strParam = getString(packet, length);
                System.err.println("replyinfo race/class_info: string stat "+statNo+" not implemented");
                break;

            default:
                if (Stats.CS_STAT_RESIST_START <= statNo && statNo < Stats.CS_STAT_RESIST_START+Stats.RESIST_TYPES) {
                    final short int2Param2 = (short)getInt2(packet);
                    rb.setStatAdjustment(statNo, int2Param2);
                } else if (Stats.CS_STAT_SKILLINFO <= statNo && statNo < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS) {
                    final int level = getInt1(packet);
                    final long experience = getInt8(packet);
                    System.err.println("replyinfo race/class_info: skill stat "+statNo+" not implemented");
                } else {
                    throw new UnknownCommandException("unknown stat value: "+statNo);
                }
                break;
            }
        }

        throw new UnknownCommandException("truncated stats entry in replyinfo race/class_info");
    }

    /**
     * Parses a "choice" entry of a "replyinfo race_info" or "replyinfo
     * class_info" packet.
     * @param packet the packet's contents
     * @param rb the race class race info builder to update
     */
    private static void parseClassRaceInfoChoice(@NotNull final ByteBuffer packet, @NotNull final ClassRaceInfoBuilder rb) {
        final String choiceName = getString(packet, getInt1(packet));
        final String choiceDescription = getString(packet, getInt1(packet));
        final String archName = getString(packet, getInt1(packet));
        final String archDesc = getString(packet, getInt1(packet));
        final Map<String, String> choices = new LinkedHashMap<>();
        choices.put(archName, archDesc);
        while (true) {
            final int archNameLength = getInt1(packet);
            if (archNameLength == 0) {
                break;
            }
            final String archName2 = getString(packet, archNameLength);
            final String archDesc2 = getString(packet, getInt1(packet));
            choices.put(archName2, archDesc2);
        }
        rb.addChoice(new Choice(choiceName, choiceDescription, choices));
    }

    /**
     * Processes an 'accountplayers' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processAccountPlayers(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();

        if (accountName == null) {
            throw new UnknownCommandException("accountplayers without account");
        }
        fireStartAccountList(accountName);

        // number of characters
        final int total = getInt1(packet);
        final AccountPlayerBuilder accountPlayerBuilder = new AccountPlayerBuilder();
        for (int count = 0; count < total; count++) {
            final CharacterInformation characterInformation = parseAccountPlayer(packet, accountPlayerBuilder);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv accountplayers entry: "+characterInformation);
            }
            fireAddAccount(characterInformation);
        }
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("invalid accountplayers reply, pos="+packet.position());
        }

        fireEndAccountList(total);

        packet.reset();
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes one account entry of an 'accountplayers' server command.
     * @param packet the packet's payload
     * @param accountPlayerBuilder the account player builder to use
     * @return the parsed account entry
     * @throws UnknownCommandException if the account entry cannot be parsed
     */
    @NotNull
    private CharacterInformation parseAccountPlayer(@NotNull final ByteBuffer packet, @NotNull final AccountPlayerBuilder accountPlayerBuilder) throws UnknownCommandException {
        while (true) {
            if (!packet.hasRemaining()) {
                throw new UnknownCommandException("truncated accountplayers reply");
            }

            final int len = getInt1(packet);
            if (len == 0) {
                break;
            }

            final int type = getInt1(packet);
            switch (type) {
            case ACL_NAME:
                accountPlayerBuilder.setName(getString(packet, len-1));
                break;

            case ACL_CLASS:
                accountPlayerBuilder.setClass(getString(packet, len-1));
                break;

            case ACL_RACE:
                accountPlayerBuilder.setRace(getString(packet, len-1));
                break;

            case ACL_LEVEL:
                accountPlayerBuilder.setLevel(getInt2(packet));
                break;

            case ACL_FACE:
                accountPlayerBuilder.setFace(getString(packet, len-1));
                break;

            case ACL_PARTY:
                accountPlayerBuilder.setParty(getString(packet, len-1));
                break;

            case ACL_MAP:
                accountPlayerBuilder.setMap(getString(packet, len-1));
                break;

            case ACL_FACE_NUM:
                accountPlayerBuilder.setFaceNumber(getInt2(packet));
                break;

            default:
                // ignore those values we don't understand
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("recv accountplayers unknown="+type);
                }
                packet.position(packet.position()+len-1);
                break;
            }
        }

        return accountPlayerBuilder.finish();
    }

    /**
     * Processes an 'account_failed' server command.
     * @param packet the packet's payload
     */
    private void processAddmeFailed(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        // XXX: addme_failed command not implemented
        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes an 'account_success' server command.
     * @param packet the packet's payload
     */
    private void processAddmeSuccess(@NotNull final ByteBuffer packet) {
        final int args = packet.position();

        if (clientSocketState != ClientSocketState.CONNECTED) {
            if (clientSocketState == ClientSocketState.ADDME) {
                // servers without account support
                setClientSocketState(ClientSocketState.ADDME, ClientSocketState.CONNECTED);
            } else if (clientSocketState == ClientSocketState.ACCOUNT_INFO) {
                fireStartPlaying();
                setClientSocketState(ClientSocketState.ACCOUNT_INFO, ClientSocketState.CONNECTED);
            }
            negotiateMapSize(preferredMapWidth, preferredMapHeight);
        }

        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes an 'addquest' server command.
     * @param packet the packet's payload
     */
    private void processAddQuest(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        while (packet.hasRemaining()) {
            final int code = getInt4(packet);
            final int titleLength = getInt2(packet);
            final String title = getString(packet, titleLength);
            final int face = getInt4(packet);
            final int replay = getInt1(packet);
            final int parent = getInt4(packet);
            final int end = getInt1(packet);
            final int stepLength = getInt2(packet);
            final String step = stepLength > 0 ? getString(packet, stepLength) : "";
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv addquest code="+code+" title="+title+" face="+face+"replay="+replay+" end="+end+" desc="+step);
            }
            model.getQuestsManager().addQuest(code, title, face, replay == 1, parent, end == 1, step);
        }
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an "addacknowledge" block.
     * @param packet the packet to process
     */
    private void processAddKnowledge(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        while (packet.hasRemaining()) {
            final int code = getInt4(packet);
            final int typeLength = getInt2(packet);
            final String type = getString(packet, typeLength);
            final int titleLength = getInt2(packet);
            final String title = getString(packet, titleLength);
            final int face = getInt4(packet);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv addknowledge code="+code+"type="+type+"title="+title+" face="+face);
            }
            model.getKnowledgeManager().addKnowledge(code, type, title, face);
        }
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an 'addspell' server command.
     * @param packet the packet's payload
     */
    private void processAddSpell(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        while (packet.hasRemaining()) {
            final int tag = getInt4(packet);
            final int level = getInt2(packet);
            final int castingTime = getInt2(packet);
            final int mana = getInt2(packet);
            final int grace = getInt2(packet);
            final int damage = getInt2(packet);
            final int skill = getInt1(packet);
            final int path = getInt4(packet);
            final int face = getInt4(packet);
            final int nameLength = getInt1(packet);
            final String name = getString(packet, nameLength);
            final int messageLength = getInt2(packet);
            final String message = getString(packet, messageLength);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv addspell tag="+tag+" lvl="+level+" time="+castingTime+" sp="+mana+" gr="+grace+" dam="+damage+" skill="+skill+" path="+path+" face="+face+" name="+name+" msg="+message);
            }
            model.getSpellsManager().addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
        }
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an 'anim' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processAnim(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int num = getInt2(packet);
        final int flags = getInt2(packet);
        final int[] faces = new int[packet.remaining()/2];
        if (faces.length <= 0) {
            throw new UnknownCommandException("no faces in anim command");
        }
        Arrays.setAll(faces, i -> getInt2(packet));
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of anim command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv anim num="+num+" flags="+flags+" faces="+Arrays.toString(faces));
        }
        if ((num&~0x1FFF) != 0) {
            throw new UnknownCommandException("invalid animation id "+num);
        }
        fireAddAnimation(num&0x1FFF, flags, faces);
        notifyPacketWatcherListenersShortArray(packet, args);
    }

    /**
     * Processes a 'comc' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processComc(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int packetNo = getInt2(packet);
        final int time = getInt4(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of comc command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv comc no="+packetNo+" time="+time);
        }
        fireCommandComcReceived(packetNo, time);
        notifyPacketWatcherListenersShortInt(packet, args);
    }

    /**
     * Processes a 'delinv' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processDelInv(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        int tag = 0;
        do {
            tag = tag*10+parseDigit(packet.get());
        } while (packet.hasRemaining());
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of delinv command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv delinv tag="+tag);
        }
        fireDelinvReceived(tag);
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'delitem' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processDelItem(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int[] tags = new int[packet.remaining()/4];
        Arrays.setAll(tags, i -> getInt4(packet));
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of delitem command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv delitem tags="+Arrays.toString(tags));
        }
        fireDelitemReceived(tags);
        notifyPacketWatcherListenersIntArray(packet, args);
    }

    /**
     * Processes a 'delspell' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processDelSpell(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int tag = getInt4(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of delspell command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv delspell tag="+tag);
        }
        model.getSpellsManager().deleteSpell(tag);
        notifyPacketWatcherListenersIntArray(packet, args);
    }

    /**
     * Processes a 'drawextinfo' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processDrawExtInfo(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        int color = 0;
        do {
            color = color*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        int type = 0;
        do {
            type = type*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        int subtype = 0;
        do {
            subtype = subtype*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        final String message = getString(packet, packet.remaining());
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv drawextinfo color="+color+" type="+type+"/"+subtype+" msg="+message);
        }
        drawextinfo(color, type, subtype, message);
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'drawinfo' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processDrawInfo(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        int color = 0;
        do {
            color = color*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        final String message = getString(packet, packet.remaining());
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv drawinfo color="+color+" msg="+message);
        }
        drawInfo(message, color);
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes an 'ExtendedInfoSet' server command.
     * @param packet the packet's payload
     */
    private void processExtendedInfoSet(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        do {
            final int startPos = packet.position();
            while (packet.hasRemaining() && packet.get(packet.position()) != ' ') {
                packet.get();
            }
            final String string = newString(packet, startPos, packet.position()-startPos);
            packet.get();
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv ExtendedInfoSet "+string);
            }
            // XXX: ExtendedInfoSet command not implemented
        } while (packet.hasRemaining());
        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes an 'ExtendedTextSet' server command.
     * @param packet the packet's payload
     */
    private void processExtendedTextSet(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        while (true) {
            final int startPos = packet.position();
            while (packet.hasRemaining() && packet.get(packet.position()) != ' ') {
                packet.get();
            }
            final String type = newString(packet, startPos, packet.position()-startPos);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv ExtendedTextSet "+type);
            }
            // XXX: ExtendedTextSet command not implemented
            if (!packet.hasRemaining()) {
                break;
            }
            packet.get();
        }
        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes a 'face2' server command.
     * @param packet the packet's payload
     */
    private void processFace2(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        final int faceNum = getInt2(packet);
        final int faceSetNum = getInt1(packet);
        final int faceChecksum = getInt4(packet);
        final String faceName = getString(packet, packet.remaining()).intern();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv face2 num="+faceNum+" set="+faceSetNum+" checksum="+faceChecksum+" name="+faceName);
        }
        fireFaceReceived(faceNum, faceSetNum, faceChecksum, faceName);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'failure' server command.
     * @param packet the packet's payload
     */
    private void processFailure(@NotNull final ByteBuffer packet) {
        final String full = getString(packet, packet.remaining());
        final String command;
        final String message;
        final int idx = full.indexOf(' ');
        if (idx == -1) {
            command = full;
            message = "";
        } else {
            command = full.substring(0, idx);
            message = full.substring(idx+1);
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv failure command="+command+" message="+message);
        }
        fireFailure(command, message);
    }

    /**
     * Processes a 'goodbye' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processGoodbye(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of goodbye command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv goodbye");
        }
        // XXX: goodbye command not implemented
        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes an 'image2' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processImage2(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int faceNum = getInt4(packet);
        final int faceSetNum = getInt1(packet);
        final int len = getInt4(packet);
        if (packet.remaining() != len) {
            throw new UnknownCommandException("excess data at end of image2 command");
        }
        final int faceDataPosition = packet.position();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv image2 face="+faceNum+" set="+faceSetNum+" len="+len);
        }
        packet.position(faceDataPosition);
        for (AskfaceFaceQueueListener listener : askfaceFaceQueueListeners) {
            listener.faceReceived(faceNum, faceSetNum, packet);
        }
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an 'item2' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processItem2(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int location = getInt4(packet);
        while (packet.hasRemaining()) {
            final int tag = getInt4(packet);
            final int flags = getInt4(packet);
            final int weight = getInt4(packet);
            final int faceNum = getInt4(packet);
            final int nameLength = getInt1(packet);
            final String[] names = getString(packet, nameLength).split("\0", 2);
            final String name = names[0].intern();
            final String namePl = names.length < 2 ? name : names[1].intern();
            final int anim = getInt2(packet);
            final int animSpeed = getInt1(packet);
            final int nrof = getInt4(packet);
            final int type = getInt2(packet);
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("recv item2 location="+location+" tag="+tag+" flags="+flags+" weight="+weight+" face="+faceNum+" name="+name+" name_pl="+namePl+" anim="+anim+" anim_speed="+animSpeed+" nrof="+nrof+" type="+type);
            }
            fireAddItemReceived(location, tag, flags, weight, faceNum, name, namePl, anim, animSpeed, nrof, type);
        }
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of item2 command");
        }
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'magicmap' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processMagicMap(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();

        final boolean widthSign = packet.get(packet.position()) == '-';
        if (widthSign) {
            packet.get();
        }
        int width = 0;
        do {
            width = width*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();
        if (widthSign) {
            width = -width;
        }

        final boolean heightSign = packet.get(packet.position()) == '-';
        if (heightSign) {
            packet.get();
        }
        int height = 0;
        do {
            height = height*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();
        if (heightSign) {
            height = -height;
        }

        final boolean pxSign = packet.get(packet.position()) == '-';
        if (pxSign) {
            packet.get();
        }
        int px = 0;
        do {
            px = px*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();
        if (pxSign) {
            px = -px;
        }

        final boolean pySign = packet.get(packet.position()) == '-';
        if (pySign) {
            packet.get();
        }
        int py = 0;
        do {
            py = py*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();
        if (pySign) {
            py = -py;
        }

        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv magicmap size="+width+"x"+height+" player="+px+"/"+py+" len="+packet.remaining());
        }
        if (packet.remaining() != width*height) {
            throw new UnknownCommandException("invalid magicmap command");
        }

        final byte[][] data = new byte[height][width];
        for (int y = 0; y < height; y++) {
            packet.get(data[y]);
        }
        fireMagicMap(-px+(currentMapWidth-1)/2, -py+(currentMapHeight-1)/2, data);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'map2' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processMap2(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 begin");
        }
        if (crossfireUpdateMapListener != null) {
            synchronized (crossfireUpdateMapListener.mapBegin()) {
                while (packet.hasRemaining()) {
                    final int coord = getInt2(packet);
                    final int x = ((coord>>10)&0x3F)-Map2.COORD_OFFSET;
                    final int y = ((coord>>4)&0x3F)-Map2.COORD_OFFSET;
                    final int coordType = coord&0xF;

                    switch (coordType) {
                    case Map2.TYPE_COORDINATE:
                        cmdMap2Coordinate(packet, x, y);
                        break;

                    case Map2.TYPE_SCROLL:
                        if (debugProtocol != null) {
                            debugProtocol.debugProtocolWrite("recv map2 "+x+"/"+y+" scroll");
                        }
                        assert crossfireUpdateMapListener != null;
                        crossfireUpdateMapListener.mapScroll(x, y);
                        break;

                    default:
                        if (debugProtocol != null) {
                            debugProtocol.debugProtocolWrite("recv map2 "+x+"/"+y+" <invalid>");
                        }
                        throw new UnknownCommandException("map2 command contains unexpected coordinate type "+coordType);
                    }
                }
                assert crossfireUpdateMapListener != null;
                crossfireUpdateMapListener.mapEnd();
            }
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv map2 end");
        }
        notifyPacketWatcherListenersShortArray(packet, args);
    }

    /**
     * Processes a 'mapextended' server command.
     * @param packet the packet's payload
     */
    private void processMapExtended(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv mapextended");
        }
        // XXX: "MapExtended" command not yet implemented
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'music' server command.
     * @param packet the packet's payload
     */
    private void processMusic(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        final String music = getString(packet, packet.remaining());
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv music "+music);
        }
        fireMusicReceived(music);
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'newmap' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processNewMap(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of newmap command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv newmap");
        }
        fireNewMap();
        notifyPacketWatcherListenersNoData(packet, args);
    }

    /**
     * Processes a 'pickup' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processPickup(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int pickupOptions = getInt4(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of pickup command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv pickup options="+pickupOptions);
        }
        firePickupChanged(pickupOptions);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'pickup' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processPlayer(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int tag = getInt4(packet);
        final int weight = getInt4(packet);
        final int faceNum = getInt4(packet);
        final int nameLength = getInt1(packet);
        final String name = getString(packet, nameLength);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of player command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv player tag="+tag+" weight="+weight+" face="+faceNum+" name="+name);
        }
        firePlayerReceived(tag, weight, faceNum, name);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'pickup' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processQuery(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        int flags = 0;
        do {
            flags = flags*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        final String text = getString(packet, packet.remaining());
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv query flags="+flags+" text="+text);
        }
        // XXX: hack to process "What is your name?" prompt even before addme_success is received
        if (clientSocketState != ClientSocketState.CONNECTED) {
            setClientSocketState(ClientSocketState.ADDME, ClientSocketState.CONNECTED);
            negotiateMapSize(preferredMapWidth, preferredMapHeight);
        }
        fireCommandQueryReceived(text, flags);
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'replyinfo' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processReplyInfo(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int startPos = packet.position();
        while (packet.hasRemaining() && packet.get(packet.position()) != '\n' && packet.get(packet.position()) != ' ') {
            packet.get();
        }
        final String infoType = newString(packet, startPos, packet.position()-startPos);
        if (packet.hasRemaining()) {
            packet.get();
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv replyinfo type="+infoType+" len="+packet.remaining());
        }
        try {
            cmdReplyinfo(infoType, packet);
        } catch (final IOException ex) {
            throw new UnknownCommandException("invalid replyinfo command: "+ex.getMessage(), ex);
        }
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'replyinfo' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private void processSetup(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final List<String> options = new ArrayList<>();
        while (packet.hasRemaining()) {
            while (packet.get(packet.position()) == ' ') {
                packet.get();
            }
            final int startPos = packet.position();
            while (packet.hasRemaining() && packet.get(packet.position()) != ' ') {
                packet.get();
            }
            options.add(newString(packet, startPos, packet.position()-startPos));
            if (packet.hasRemaining()) {
                packet.get();
            }
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv setup "+options);
        }
        if (options.size()%2 != 0) {
            throw new UnknownCommandException("odd number of arguments in setup command");
        }
        for (int i = 0; i+1 < options.size(); i += 2) {
            final String option = options.get(i);
            final String value = options.get(i+1);
            switch (option) {
            case "spellmon":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("Error: the server is too old for this client since it does not support the spellmon=1 setup option.");
                }
                break;

            case "sound2":
                // ignore: if the server sends sound info it is processed
                break;

            case "exp64":
                // Ignored since it only enables additional/improved stat
                // commands but the old version is also supported.
                break;

            case "newmapcmd":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("Error: the server is too old for this client since it does not support the newmapcmd=1 setup option.");
                }
                break;

            case "facecache":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the facecache=1 setup option.");
                }
                break;

            case "extendedTextInfos":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the extendedTextInfos=1 setup option.");
                }
                break;

            case "itemcmd":
                if (!value.equals("2")) {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the itemcmd=2 setup option.");
                }
                break;

            case "mapsize":
                final String[] tmp = value.split("x", 2);
                if (tmp.length != 2) {
                    throw new UnknownCommandException("the server returned 'setup mapsize "+value+"'.");
                }
                final int thisMapWidth;
                final int thisMapHeight;
                try {
                    thisMapWidth = Integer.parseInt(tmp[0]);
                    thisMapHeight = Integer.parseInt(tmp[1]);
                } catch (final NumberFormatException ignored) {
                    throw new UnknownCommandException("the server returned 'setup mapsize "+value+"'.");
                }
                if (pendingMapWidth == 0 || pendingMapHeight == 0) {
                    System.err.println("the server sent an unexpected 'setup mapsize "+value+"'.");
                } else if (pendingMapWidth == thisMapWidth && pendingMapHeight == thisMapHeight) {
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    setCurrentMapSize(thisMapWidth, thisMapHeight);
                    if (thisMapWidth != preferredMapWidth && thisMapHeight != preferredMapHeight) {
                        negotiateMapSize(preferredMapWidth, preferredMapHeight);
                    }
                } else if (pendingMapWidth > thisMapWidth && pendingMapHeight > thisMapHeight) {
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(thisMapWidth, thisMapHeight);
                } else if (pendingMapWidth > thisMapWidth) {
                    final int tmpMapHeight = pendingMapHeight;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(thisMapWidth, tmpMapHeight);
                } else if (pendingMapHeight > thisMapHeight) {
                    final int tmpMapWidth = pendingMapWidth;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(tmpMapWidth, thisMapHeight);
                } else if (pendingMapWidth == thisMapWidth) {
                    final int tmpMapHeight = pendingMapHeight+2;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(thisMapWidth, tmpMapHeight);
                } else if (pendingMapHeight == thisMapHeight) {
                    final int tmpMapWidth = pendingMapWidth+2;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(tmpMapWidth, thisMapHeight);
                } else if (pendingMapWidth <= pendingMapHeight) {
                    final int tmpMapWidth = pendingMapWidth+2;
                    final int tmpMapHeight = pendingMapHeight;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(tmpMapWidth, tmpMapHeight);
                } else {
                    final int tmpMapWidth = pendingMapWidth;
                    final int tmpMapHeight = pendingMapHeight+2;
                    pendingMapWidth = 0;
                    pendingMapHeight = 0;
                    negotiateMapSize(tmpMapWidth, tmpMapHeight);
                }
                break;

            case "map2cmd":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the map2cmd=1 setup option.");
                }
                break;

            case "darkness":
                // do not care
                break;

            case "tick":
                if (!value.equals("1")) {
                    throw new UnknownCommandException("the server is too old for this client since it does not support the tick=1 setup option.");
                }
                break;

            case "num_look_objects":
                numLookObjects.processSetupNumLookObjects(value);
                break;

            case "faceset":
                // ignore: we do not care about the face set
                break;

            case "want_pickup":
                // ignore: we do not care whether this option has been ignored
                break;

            case "extended_stats":
                // ignore: we do not care whether this option has been ignored
                break;

            case "loginmethod":
                if (value.equals("FALSE")) {
                    loginMethod = 0;
                    continue;
                }

                final int method;
                try {
                    method = Integer.parseInt(value);
                } catch (final NumberFormatException ignored) {
                    throw new UnknownCommandException("the server returned 'setup loginmethod "+value+"'.");
                }
                if (method < 0 || method > 2) {
                    throw new UnknownCommandException("the server returned 'setup loginmethod "+value+"'.");
                }
                loginMethod = method;
                break;

            case "notifications":
                // ignore: we do not care whether this option has been ignored
                break;

            default:
                System.err.println("Warning: ignoring unknown setup option from server: "+option+"="+value);
                break;
            }
        }

        if (options.size() != 2 || !options.get(0).equals("mapsize") && !options.get(0).equals("num_look_objects")) {
            setClientSocketState(ClientSocketState.SETUP, ClientSocketState.REQUESTINFO);
            sendRequestinfo("skill_info 1");
            sendRequestinfo("exp_table");
            sendRequestinfo("knowledge_info");
            sendQueuedRequestinfo("image_info");
            sendQueuedRequestinfo("startingmap");
            sendQueuedRequestinfo("race_list");
            sendQueuedRequestinfo("class_list");
            sendQueuedRequestinfo("newcharinfo");
            sendToggleextendedtext(MessageTypes.getAllTypes());
        }
        notifyPacketWatcherListenersAscii(packet, args);
    }

    /**
     * Processes a 'smooth' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processSmooth(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int faceNo = getInt2(packet);
        final int smoothPic = getInt2(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of smooth command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv smooth face="+faceNo+" smooth_pic="+smoothPic);
        }
        model.getSmoothFaces().updateSmoothFace(faceNo, smoothPic);
        notifyPacketWatcherListenersShortArray(packet, args);
    }

    /**
     * Processes a 'sound' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processSound(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int x = packet.get();
        final int y = packet.get();
        final int num = getInt2(packet);
        final int type = getInt1(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of sound command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv sound pos="+x+"/"+y+" num="+num+" type="+type);
        }
        fireCommandSoundReceived(x, y, num, type);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'sound2' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processSound2(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int x = packet.get();
        final int y = packet.get();
        final int dir = packet.get();
        final int volume = getInt1(packet);
        final int type = getInt1(packet);
        final int actionLength = getInt1(packet);
        final String action = getString(packet, actionLength);
        final int nameLength = getInt1(packet);
        final String name = getString(packet, nameLength);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of sound2 command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv sound2 pos="+x+"/"+y+" dir="+dir+" volume="+volume+" type="+type+" action="+action+" name="+name);
        }
        fireCommandSound2Received(x, y, dir, volume, type, action, name);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'stats' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processStats(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        while (packet.hasRemaining()) {
            final int stat = getInt1(packet);
            switch (stat) {
            case Stats.CS_STAT_HP:
            case Stats.CS_STAT_MAXHP:
            case Stats.CS_STAT_SP:
            case Stats.CS_STAT_MAXSP:
            case Stats.CS_STAT_STR:
            case Stats.CS_STAT_INT:
            case Stats.CS_STAT_WIS:
            case Stats.CS_STAT_DEX:
            case Stats.CS_STAT_CON:
            case Stats.CS_STAT_CHA:
            case Stats.CS_STAT_LEVEL:
            case Stats.CS_STAT_WC:
            case Stats.CS_STAT_AC:
            case Stats.CS_STAT_DAM:
            case Stats.CS_STAT_ARMOUR:
            case Stats.CS_STAT_FOOD:
            case Stats.CS_STAT_POW:
            case Stats.CS_STAT_GRACE:
            case Stats.CS_STAT_MAXGRACE:
            case Stats.CS_STAT_FLAGS:
            case Stats.CS_STAT_RACE_STR:
            case Stats.CS_STAT_RACE_INT:
            case Stats.CS_STAT_RACE_WIS:
            case Stats.CS_STAT_RACE_DEX:
            case Stats.CS_STAT_RACE_CON:
            case Stats.CS_STAT_RACE_CHA:
            case Stats.CS_STAT_RACE_POW:
            case Stats.CS_STAT_BASE_STR:
            case Stats.CS_STAT_BASE_INT:
            case Stats.CS_STAT_BASE_WIS:
            case Stats.CS_STAT_BASE_DEX:
            case Stats.CS_STAT_BASE_CON:
            case Stats.CS_STAT_BASE_CHA:
            case Stats.CS_STAT_BASE_POW:
            case Stats.CS_STAT_APPLIED_STR:
            case Stats.CS_STAT_APPLIED_INT:
            case Stats.CS_STAT_APPLIED_WIS:
            case Stats.CS_STAT_APPLIED_DEX:
            case Stats.CS_STAT_APPLIED_CON:
            case Stats.CS_STAT_APPLIED_CHA:
            case Stats.CS_STAT_APPLIED_POW:
            case Stats.CS_STAT_GOLEM_HP:
            case Stats.CS_STAT_GOLEM_MAXHP:
                final short int2Param = (short)getInt2(packet);
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("recv stats stat="+stat+" int2="+int2Param+"="+(int2Param&0xFFFF));
                }
                model.getStats().setStatInt2(stat, int2Param);
                notifyPacketWatcherListenersStats(stat, int2Param);
                break;

            case Stats.CS_STAT_EXP:
            case Stats.CS_STAT_SPEED:
            case Stats.CS_STAT_WEAP_SP:
            case Stats.CS_STAT_WEIGHT_LIM:
            case Stats.CS_STAT_SPELL_ATTUNE:
            case Stats.CS_STAT_SPELL_REPEL:
            case Stats.CS_STAT_SPELL_DENY:
                final int int4Param = getInt4(packet);
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("recv stats stat="+stat+" int4="+int4Param);
                }
                model.getStats().setStatInt4(stat, int4Param);
                notifyPacketWatcherListenersStats(stat, int4Param);
                break;

            case Stats.CS_STAT_EXP64:
                final long int8Param = getInt8(packet);
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("recv stats stat="+stat+" int8="+int8Param);
                }
                model.getStats().setStatInt8(stat, int8Param);
                notifyPacketWatcherListenersStats(stat, int8Param);
                break;

            case Stats.CS_STAT_RANGE:
            case Stats.CS_STAT_TITLE:
                final int length = getInt1(packet);
                final String strParam = getString(packet, length);
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("recv stats stat="+stat+" str="+strParam);
                }
                model.getStats().setStatString(stat, strParam);
                notifyPacketWatcherListenersStats(stat, strParam);
                break;

            default:
                if (Stats.CS_STAT_RESIST_START <= stat && stat < Stats.CS_STAT_RESIST_START+Stats.RESIST_TYPES) {
                    final short int2Param2 = (short)getInt2(packet);
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("recv stats stat="+stat+" int2="+int2Param2);
                    }
                    model.getStats().setStatInt2(stat, int2Param2);
                    notifyPacketWatcherListenersStats(stat, int2Param2);
                } else if (Stats.CS_STAT_SKILLINFO <= stat && stat < Stats.CS_STAT_SKILLINFO+Stats.CS_NUM_SKILLS) {
                    final int level = getInt1(packet);
                    final long experience = getInt8(packet);
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("recv stats stat="+stat+" level="+level+" experience="+experience);
                    }
                    model.getStats().setStatSkill(stat, level, experience);
                    notifyPacketWatcherListenersStats(stat, level, experience);
                } else {
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("recv stats stat="+stat+" <unknown parameter>");
                    }
                    throw new UnknownCommandException("unknown stat value: "+stat);
                }
                break;
            }
        }
    }

    /**
     * Processes a 'tick' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processTick(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int tickNo = getInt4(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of tick command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv tick "+tickNo);
        }
        fireTick(tickNo);
        notifyPacketWatcherListenersIntArray(packet, args);
    }

    /**
     * Processes an 'upditem' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processUpdItem(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int flags = getInt1(packet);
        final int tag = getInt4(packet);
        final int valLocation = (flags&UpdItem.UPD_LOCATION) == 0 ? 0 : getInt4(packet);
        final int valFlags = (flags&UpdItem.UPD_FLAGS) == 0 ? 0 : getInt4(packet);
        final int valWeight = (flags&UpdItem.UPD_WEIGHT) == 0 ? 0 : getInt4(packet);
        final int valFaceNum = (flags&UpdItem.UPD_FACE) == 0 ? 0 : getInt4(packet);
        final String valName;
        final String valNamePl;
        if ((flags&UpdItem.UPD_NAME) == 0) {
            valName = "";
            valNamePl = "";
        } else {
            final int nameLength = getInt1(packet);
            int namePlIndex = 0;
            while (namePlIndex < nameLength && packet.get(packet.position()+namePlIndex) != 0) {
                namePlIndex++;
            }
            valName = newString(packet, packet.position(), namePlIndex);
            valNamePl = namePlIndex+1 < nameLength ? newString(packet, packet.position()+namePlIndex+1, nameLength-(namePlIndex+1)) : valName;
            packet.position(packet.position()+nameLength);
        }
        final int valAnim = (flags&UpdItem.UPD_ANIM) == 0 ? 0 : getInt2(packet);
        final int valAnimSpeed = (flags&UpdItem.UPD_ANIMSPEED) == 0 ? 0 : getInt1(packet);
        final int valNrof = (flags&UpdItem.UPD_NROF) == 0 ? 0 : getInt4(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of upditem command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv upditem flags="+flags+" tag="+tag+" loc="+valLocation+" flags="+valFlags+" weight="+valWeight+" face="+valFaceNum+" name="+valName+" name_pl="+valNamePl+" anim="+valAnim+" anim_speed="+valAnimSpeed+" nrof="+valNrof);
        }
        fireUpditemReceived(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an 'updquest' server command.
     * @param packet the packet's payload
     */
    private void processUpdQuest(@NotNull final ByteBuffer packet) {
        final int args = packet.position();
        final int code = getInt4(packet);
        final int end = getInt1(packet);
        final int stepLength = getInt2(packet);
        final String step = stepLength > 0 ? getString(packet, stepLength) : "";

        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv updquest code="+code+" end="+end+" description="+step);
        }
        model.getQuestsManager().updateQuest(code, end == 1, step);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes an 'updspell' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processUpdSpell(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        final int flags = getInt1(packet);
        final int tag = getInt4(packet);
        final int mana = (flags&SpellsManager.UPD_SP_MANA) == 0 ? 0 : getInt2(packet);
        final int grace = (flags&SpellsManager.UPD_SP_GRACE) == 0 ? 0 : getInt2(packet);
        final int damage = (flags&SpellsManager.UPD_SP_DAMAGE) == 0 ? 0 : getInt2(packet);
        if (packet.hasRemaining()) {
            throw new UnknownCommandException("excess data at end of updspell command");
        }
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv updspell flags="+flags+" tag="+tag+" sp="+mana+" gr="+grace+" dam="+damage);
        }
        model.getSpellsManager().updateSpell(flags, tag, mana, grace, damage);
        notifyPacketWatcherListenersMixed(packet, args);
    }

    /**
     * Processes a 'version' server command.
     * @param packet the packet's payload
     * @throws UnknownCommandException if the packet is invalid
     */
    private void processVersion(@NotNull final ByteBuffer packet) throws UnknownCommandException {
        final int args = packet.position();
        int csval = 0;
        do {
            csval = csval*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        int scval = 0;
        do {
            scval = scval*10+parseDigit(packet.get());
        } while (packet.get(packet.position()) != ' ');
        packet.get();

        final String vinfo = getString(packet, packet.remaining());
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("recv version cs="+csval+" sc="+scval+" info="+vinfo);
        }
        setClientSocketState(ClientSocketState.VERSION, ClientSocketState.SETUP);
        sendSetup("want_pickup 1", "faceset 0", "sound2 3", "exp64 1", "map2cmd 1", "darkness 1", "newmapcmd 1", "facecache 1", "extendedTextInfos 1", "itemcmd 2", "spellmon 1", "tick 1", "extended_stats 1", "loginmethod 1", "notifications 2");
        model.getStats().setSimpleWeaponSpeed(scval >= 1029);

        notifyPacketWatcherListenersAscii(packet, args);
    }

    @Override
    public void sendAccountLogin(@NotNull final String login, @NotNull final String password) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send accountlogin "+login);
        }
        accountName = login;
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ACCOUNT_LOGIN_PREFIX);
            final byte[] loginBytes = login.getBytes(UTF8);
            byteBuffer.put((byte)loginBytes.length);
            byteBuffer.put(loginBytes);
            final byte[] passwordBytes = password.getBytes(UTF8);
            byteBuffer.put((byte)passwordBytes.length);
            byteBuffer.put(passwordBytes);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }

    }

    @Override
    public void sendAddme() {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send addme");
        }
        defaultServerConnection.writePacket(ADDME_PREFIX, ADDME_PREFIX.length);
    }

    @Override
    public void sendApply(final int tag) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send apply tag="+tag);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(APPLY_PREFIX);
            putDecimal(tag);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendAskface(final int faceNum) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send askface face="+faceNum);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ASKFACE_PREFIX);
            putDecimal(faceNum);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void addFaceReceivedListener(@NotNull final AskfaceFaceQueueListener listener) {
        askfaceFaceQueueListeners.add(listener);
    }

    @Override
    public void sendExamine(final int tag) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send examine tag="+tag);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(EXAMINE_PREFIX);
            putDecimal(tag);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendLock(final boolean val, final int tag) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send lock tag="+tag+" val="+val);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(LOCK_PREFIX);
            byteBuffer.put((byte)(val ? 1 : 0));
            byteBuffer.putInt(tag);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendLookat(final int dx, final int dy) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send lookat pos="+dx+"/"+dy);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(LOOKAT_PREFIX);
            putDecimal(dx);
            byteBuffer.put((byte)' ');
            putDecimal(dy);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendMark(final int tag) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send mark tag="+tag);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(MARK_PREFIX);
            byteBuffer.putInt(tag);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendMove(final int to, final int tag, final int nrof) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send move tag="+tag+" to="+to+" nrof="+nrof);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(MOVE_PREFIX);
            putDecimal(to);
            byteBuffer.put((byte)' ');
            putDecimal(tag);
            byteBuffer.put((byte)' ');
            putDecimal(nrof);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public int sendNcom(final int repeat, @NotNull final String command) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send ncom no="+packet+" repeat="+repeat+" cmd="+command);
        }
        final int thisPacket;
        synchronized (writeBuffer) {
            thisPacket = packet++&0x00FF;
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(NCOM_PREFIX);
            byteBuffer.putShort((short)thisPacket);
            byteBuffer.putInt(repeat);
            byteBuffer.put(command.getBytes(UTF8));
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
        return thisPacket;
    }

    @Override
    public void sendReply(@NotNull final String text) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send reply text="+text);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(REPLY_PREFIX);
            byteBuffer.put(text.getBytes(UTF8));
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
        fireReplySent();
    }

    @Override
    public void sendRequestinfo(@NotNull final String infoType) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send requestinfo type="+infoType);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(REQUESTINFO_PREFIX);
            byteBuffer.put(infoType.getBytes(UTF8));
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
            sendingRequestInfo = PATTERN_SPACE.split(infoType, 2)[0];
        }
    }

    @Override
    public void sendSetup(@NotNull final String... options) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send setup options="+Arrays.toString(options));
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(SETUP_PREFIX);
            if (options.length <= 0) {
                byteBuffer.put((byte)' ');
            } else {
                for (String option : options) {
                    byteBuffer.put((byte)' ');
                    byteBuffer.put(option.getBytes(UTF8));
                }
            }
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendToggleextendedtext(@NotNull final int... types) {
        if (types.length <= 0) {
            return;
        }

        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send toggleextendedtext types="+Arrays.toString(types));
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(TOGGLEEXTENDEDTEXT_PREFIX);
            for (int type : types) {
                byteBuffer.put((byte)' ');
                putDecimal(type);
            }
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendVersion(final int csval, final int scval, @NotNull final String vinfo) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send version cs="+csval+" sc="+scval+" info="+vinfo);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(VERSION_PREFIX);
            putDecimal(csval);
            byteBuffer.put((byte)' ');
            putDecimal(scval);
            byteBuffer.put((byte)' ');
            byteBuffer.put(vinfo.getBytes(UTF8));
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Appends an integer in decimal ASCII representation to {@link
     * #byteBuffer}.
     * @param value the value to append
     */
    private void putDecimal(final int value) {
        if (value == 0) {
            byteBuffer.put((byte)'0');
        } else {
            byteBuffer.put(Integer.toString(value).getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    /**
     * Parses a character into an integer.
     * @param ch the character to parse
     * @return the integer representing the character
     * @throws UnknownCommandException if {@code ch} is not a digit
     */
    private static int parseDigit(final byte ch) throws UnknownCommandException {
        final int digit = ch-'0';
        if (digit < 0 || digit > 9) {
            throw new UnknownCommandException("not a digit: "+ch);
        }
        return digit;
    }

    @Override
    public void setPreferredMapSize(final int preferredMapWidth, final int preferredMapHeight) {
        final int preferredMapWidth2 = Math.max(1, preferredMapWidth|1);
        final int preferredMapHeight2 = Math.max(1, preferredMapHeight|1);
        if (this.preferredMapWidth == preferredMapWidth2 && this.preferredMapHeight == preferredMapHeight2) {
            return;
        }

        this.preferredMapWidth = preferredMapWidth2;
        this.preferredMapHeight = preferredMapHeight2;

        negotiateMapSize(this.preferredMapWidth, this.preferredMapHeight);
    }

    /**
     * Sets the current map size as negotiated with the server.
     * @param currentMapWidth the new map width
     * @param currentMapHeight the new map height
     */
    private void setCurrentMapSize(final int currentMapWidth, final int currentMapHeight) {
        if (this.currentMapWidth == currentMapWidth && this.currentMapHeight == currentMapHeight) {
            return;
        }

        this.currentMapWidth = currentMapWidth;
        this.currentMapHeight = currentMapHeight;
        fireNewMap();
    }

    /**
     * Notifies all listeners that a "newmap" command has been received.
     */
    private void fireNewMap() {
        if (crossfireUpdateMapListener != null) {
            crossfireUpdateMapListener.newMap(currentMapWidth, currentMapHeight);
        }
    }

    @Override
    public void setPreferredNumLookObjects(final int preferredNumLookObjects) {
        numLookObjects.setPreferredNumLookObjects(preferredNumLookObjects);
    }

    @Nullable
    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public void connect(@NotNull final String hostname, final int port) {
        accountName = null;
        clearFailure();
        clientSocketState = ClientSocketState.CONNECTING;
        setClientSocketState(ClientSocketState.CONNECTING, ClientSocketState.CONNECTING);
        defaultServerConnection.connect(hostname, port);
    }

    @Override
    public void disconnect(@NotNull final String reason) {
        defaultServerConnection.disconnect(reason);
    }

    @Override
    public void addClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        defaultServerConnection.addClientSocketListener(clientSocketListener);
    }

    @Override
    public void removeClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        defaultServerConnection.removeClientSocketListener(clientSocketListener);
    }

    /**
     * Updates the {@link #clientSocketState}.
     * @param prevState the expected current state
     * @param nextState the next state
     */
    private void setClientSocketState(@NotNull final ClientSocketState prevState, @NotNull final ClientSocketState nextState) {
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("connection state: "+nextState);
        }
        if (clientSocketState != prevState) {
            System.err.println("Warning: connection state is "+clientSocketState+" when switching to state "+nextState+", expecting state "+prevState);
        }
        clientSocketState = nextState;
        model.getGuiStateManager().setClientSocketState(nextState);
        numLookObjects.setClientSocketState(clientSocketState);
    }

    @Override
    public void sendAccountPlay(@NotNull final String name) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send accountplay "+name);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ACCOUNT_PLAY_PREFIX);
            byteBuffer.put(name.getBytes(UTF8));
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }

        final String tmpAccountName = accountName;
        if (tmpAccountName != null) {
            fireSelectCharacter(tmpAccountName, name);
        }
    }

    @Override
    public void sendAccountLink(final int force, @NotNull final String login, @NotNull final String password) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send accountaddplayer "+login);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ACCOUNT_ADD_PLAYER_PREFIX);
            byteBuffer.put((byte)force);
            final byte[] loginBytes = login.getBytes(UTF8);
            byteBuffer.put((byte)loginBytes.length);
            byteBuffer.put(loginBytes);
            final byte[] passwordBytes = password.getBytes(UTF8);
            byteBuffer.put((byte)passwordBytes.length);
            byteBuffer.put(passwordBytes);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendAccountCreate(@NotNull final String login, @NotNull final String password) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send accountnew "+login);
        }
        accountName = login;
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ACCOUNT_NEW_PREFIX);
            final byte[] loginBytes = login.getBytes(UTF8);
            byteBuffer.put((byte)loginBytes.length);
            byteBuffer.put(loginBytes);
            final byte[] passwordBytes = password.getBytes(UTF8);
            byteBuffer.put((byte)passwordBytes.length);
            byteBuffer.put(passwordBytes);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendAccountCharacterCreate(@NotNull final String login, @NotNull final String password) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send createplayer "+login);
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(CREATE_PLAYER_PREFIX);
            final byte[] loginBytes = login.getBytes(UTF8);
            byteBuffer.put((byte)loginBytes.length);
            byteBuffer.put(loginBytes);
            final byte[] passwordBytes = password.getBytes(UTF8);
            byteBuffer.put((byte)passwordBytes.length);
            byteBuffer.put(passwordBytes);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    @Override
    public void sendAccountPassword(@NotNull final String currentPassword, @NotNull final String newPassword) {
        clearFailure();
        if (debugProtocol != null) {
            debugProtocol.debugProtocolWrite("send accountpw");
        }
        synchronized (writeBuffer) {
            byteBuffer.clear();
            //noinspection AccessToStaticFieldLockedOnInstance
            byteBuffer.put(ACCOUNT_PASSWORD_PREFIX);
            final byte[] currentPasswordBytes = currentPassword.getBytes(UTF8);
            byteBuffer.put((byte)currentPasswordBytes.length);
            byteBuffer.put(currentPasswordBytes);
            final byte[] newPasswordBytes = newPassword.getBytes(UTF8);
            byteBuffer.put((byte)newPasswordBytes.length);
            byteBuffer.put(newPasswordBytes);
            defaultServerConnection.writePacket(writeBuffer, byteBuffer.position());
        }
    }

    /**
     * Extracts and removes a 1 byte integer from a {@link ByteBuffer} at it's
     * current position.
     * @param byteBuffer the byte buffer
     * @return the integer
     */
    private static int getInt1(@NotNull final ByteBuffer byteBuffer) {
        return byteBuffer.get()&0xFF;
    }

    /**
     * Extracts and removes a 2 byte integer from a {@link ByteBuffer} at it's
     * current position.
     * @param byteBuffer the byte buffer
     * @return the integer
     */
    private static int getInt2(@NotNull final ByteBuffer byteBuffer) {
        return byteBuffer.getShort()&0xFFFF;
    }

    /**
     * Extracts and removes a 4 byte integer from a {@link ByteBuffer} at it's
     * current position.
     * @param byteBuffer the byte buffer
     * @return the integer
     */
    private static int getInt4(@NotNull final ByteBuffer byteBuffer) {
        return byteBuffer.getInt();
    }

    /**
     * Extracts and removes an 8 byte integer from a {@link ByteBuffer} at it's
     * current position.
     * @param byteBuffer the byte buffer
     * @return the integer
     */
    private static long getInt8(@NotNull final ByteBuffer byteBuffer) {
        return byteBuffer.getLong();
    }

    /**
     * Extracts and removes a string from a {@link ByteBuffer} at it's current
     * position.
     * @param byteBuffer the byte buffer
     * @param len the length of the string
     * @return the string
     */
    @NotNull
    private static String getString(@NotNull final ByteBuffer byteBuffer, final int len) {
        final byte[] tmp = new byte[len];
        byteBuffer.get(tmp);
        return new String(tmp, UTF8);
    }

    /**
     * Extracts and removes a string from a {@link ByteBuffer} at it's current
     * position.
     * @param byteBuffer the byte buffer
     * @param delimiter the delimiter that ends the string
     * @return the string
     */
    @NotNull
    private static String getStringDelimiter(@NotNull final ByteBuffer byteBuffer, final char delimiter) {
        final int position = byteBuffer.position();
        final int remaining = byteBuffer.remaining();
        int len;
        for (len = 0; len < remaining; len++) {
            if (byteBuffer.get(position+len) == delimiter) {
                break;
            }
        }
        final byte[] tmp = new byte[len];
        byteBuffer.get(tmp);
        if (len < remaining) {
            byteBuffer.get(); // skip delimiter
        }
        return new String(tmp, UTF8);
    }

    /**
     * Returns a hex-dump of a {@link ByteBuffer}.
     * @param byteBuffer the byte buffer
     * @return the hex-dump
     */
    @NotNull
    private static String hexDump(@NotNull final ByteBuffer byteBuffer) {
        final int len = byteBuffer.limit();
        final byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = byteBuffer.get(i);
        }
        return HexCodec.hexDump(data, 0, len);
    }

    /**
     * Returns the currently negotiated setup value of "num_look_objects".
     * @return the current size of the ground view
     */
    public int getCurrentNumLookObjects() {
        return numLookObjects.getCurrentNumLookObjects();
    }

    /**
     * Waits until {@link #getCurrentNumLookObjects()} is stable. This function
     * returns as soon as the negotiation with the Crossfire server is
     * complete.
     * @throws InterruptedException if the current thread was interrupted
     */
    public void waitForCurrentNumLookObjectsValid() throws InterruptedException {
        numLookObjects.waitForCurrentNumLookObjectsValid();
    }

    private void fireMapClear(final int x, final int y) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapClear(x, y);
    }

    private void fireMapDarkness(final int x, final int y, final int darkness) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapDarkness(x, y, darkness);
    }

    private void fireMapFace(@NotNull final Location location, final int face) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapFace(location, face);
    }

    private void fireMapAnimation(@NotNull final Location location, final int animationNum, final int animationType) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapAnimation(location, animationNum, animationType);
    }

    private void fireMapSmooth(@NotNull final Location location, final int smooth) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapSmooth(location, smooth);
    }

    private void fireMapAnimationSpeed(@NotNull final Location location, final int animSpeed) {
        assert crossfireUpdateMapListener != null;
        crossfireUpdateMapListener.mapAnimationSpeed(location, animSpeed);
    }

    private void fireAddAnimation(final int animation, final int flags, @NotNull final int[] faces) {
        if (crossfireUpdateMapListener != null) {
            crossfireUpdateMapListener.addAnimation(animation, flags, faces);
        }
    }

    private void fireMagicMap(final int x, final int y, @NotNull final byte[][] data) {
        if (crossfireUpdateMapListener != null) {
            synchronized (crossfireUpdateMapListener.mapBegin()) {
                assert crossfireUpdateMapListener != null;
                crossfireUpdateMapListener.magicMap(x, y, data);
                assert crossfireUpdateMapListener != null;
                crossfireUpdateMapListener.mapEnd();
            }
        }
        fireMagicMap();
    }

    /**
     * Sends a "requestinfo" packet asynchronously.
     * @param infoType the packet's payload
     */
    private void sendQueuedRequestinfo(@NotNull final String infoType) {
        synchronized (writeBuffer) {
            pendingRequestInfos.add(infoType);
        }
        sendPendingRequestInfo();
    }

    /**
     * Sends the next asynchronous "requestinfo" packet if possible.
     */
    private void sendPendingRequestInfo() {
        final String infoType;
        synchronized (writeBuffer) {
            //noinspection VariableNotUsedInsideIf
            if (sendingRequestInfo != null) {
                return;
            }
            if (pendingRequestInfos.isEmpty()) {
                return;
            }
            infoType = pendingRequestInfos.remove(0);
        }
        sendRequestinfo(infoType);
    }

}
