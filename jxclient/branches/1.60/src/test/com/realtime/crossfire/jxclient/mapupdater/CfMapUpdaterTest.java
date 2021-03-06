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

package com.realtime.crossfire.jxclient.mapupdater;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FaceImages;
import com.realtime.crossfire.jxclient.faces.FaceImagesUtils;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.TestFacesManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import java.io.IOException;
import javax.swing.ImageIcon;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Regression tests for {@link CfMapUpdater}.
 * @author Andreas Kirschbaum
 * @noinspection MagicNumber
 */
public class CfMapUpdaterTest extends TestCase {

    /**
     * A .png file of size 64x64.
     */
    @NotNull
    private static final byte[] PNG32X32 = {
        (byte)0x89,
        (byte)0x50,
        (byte)0x4e,
        (byte)0x47,
        (byte)0x0d,
        (byte)0x0a,
        (byte)0x1a,
        (byte)0x0a,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x0d,
        (byte)0x49,
        (byte)0x48,
        (byte)0x44,
        (byte)0x52,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x20,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x20,
        (byte)0x08,
        (byte)0x06,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x73,
        (byte)0x7a,
        (byte)0x7a,
        (byte)0xf4,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01,
        (byte)0x73,
        (byte)0x52,
        (byte)0x47,
        (byte)0x42,
        (byte)0x00,
        (byte)0xae,
        (byte)0xce,
        (byte)0x1c,
        (byte)0xe9,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x1a,
        (byte)0x49,
        (byte)0x44,
        (byte)0x41,
        (byte)0x54,
        (byte)0x58,
        (byte)0xc3,
        (byte)0xed,
        (byte)0xc1,
        (byte)0x01,
        (byte)0x01,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x82,
        (byte)0x20,
        (byte)0xff,
        (byte)0xaf,
        (byte)0x6e,
        (byte)0x48,
        (byte)0x40,
        (byte)0x01,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0xef,
        (byte)0x06,
        (byte)0x10,
        (byte)0x20,
        (byte)0x00,
        (byte)0x01,
        (byte)0x97,
        (byte)0xf7,
        (byte)0x57,
        (byte)0xd7,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x49,
        (byte)0x45,
        (byte)0x4e,
        (byte)0x44,
        (byte)0xae,
        (byte)0x42,
        (byte)0x60,
        (byte)0x82,
    };

    /**
     * A .png file of size 64x64.
     */
    @NotNull
    private static final byte[] PNG64X64 = {
        (byte)0x89,
        (byte)0x50,
        (byte)0x4e,
        (byte)0x47,
        (byte)0x0d,
        (byte)0x0a,
        (byte)0x1a,
        (byte)0x0a,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x0d,
        (byte)0x49,
        (byte)0x48,
        (byte)0x44,
        (byte)0x52,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x40,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x40,
        (byte)0x08,
        (byte)0x06,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0xaa,
        (byte)0x69,
        (byte)0x71,
        (byte)0xde,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01,
        (byte)0x73,
        (byte)0x52,
        (byte)0x47,
        (byte)0x42,
        (byte)0x00,
        (byte)0xae,
        (byte)0xce,
        (byte)0x1c,
        (byte)0xe9,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x27,
        (byte)0x49,
        (byte)0x44,
        (byte)0x41,
        (byte)0x54,
        (byte)0x78,
        (byte)0xda,
        (byte)0xed,
        (byte)0xc1,
        (byte)0x01,
        (byte)0x0d,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0xc2,
        (byte)0xa0,
        (byte)0xf7,
        (byte)0x4f,
        (byte)0x6d,
        (byte)0x0e,
        (byte)0x37,
        (byte)0xa0,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x80,
        (byte)0x77,
        (byte)0x03,
        (byte)0x40,
        (byte)0x40,
        (byte)0x00,
        (byte)0x01,
        (byte)0xaf,
        (byte)0x7a,
        (byte)0x0e,
        (byte)0xe8,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x49,
        (byte)0x45,
        (byte)0x4e,
        (byte)0x44,
        (byte)0xae,
        (byte)0x42,
        (byte)0x60,
        (byte)0x82,
    };

    /**
     * A .png file of size 128x256.
     */
    @NotNull
    private static final byte[] PNG128X256 = {
        (byte)0x89,
        (byte)0x50,
        (byte)0x4e,
        (byte)0x47,
        (byte)0x0d,
        (byte)0x0a,
        (byte)0x1a,
        (byte)0x0a,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x0d,
        (byte)0x49,
        (byte)0x48,
        (byte)0x44,
        (byte)0x52,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x80,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01,
        (byte)0x00,
        (byte)0x08,
        (byte)0x06,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x7b,
        (byte)0xf9,
        (byte)0x7e,
        (byte)0xa7,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x01,
        (byte)0x73,
        (byte)0x52,
        (byte)0x47,
        (byte)0x42,
        (byte)0x00,
        (byte)0xae,
        (byte)0xce,
        (byte)0x1c,
        (byte)0xe9,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x95,
        (byte)0x49,
        (byte)0x44,
        (byte)0x41,
        (byte)0x54,
        (byte)0x78,
        (byte)0xda,
        (byte)0xed,
        (byte)0xc1,
        (byte)0x01,
        (byte)0x01,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x80,
        (byte)0x90,
        (byte)0xfe,
        (byte)0xaf,
        (byte)0xee,
        (byte)0x08,
        (byte)0x0a,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x80,
        (byte)0xaa,
        (byte)0x01,
        (byte)0x01,
        (byte)0x1e,
        (byte)0x00,
        (byte)0x01,
        (byte)0xbc,
        (byte)0x1b,
        (byte)0xb9,
        (byte)0x6f,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x00,
        (byte)0x49,
        (byte)0x45,
        (byte)0x4e,
        (byte)0x44,
        (byte)0xae,
        (byte)0x42,
        (byte)0x60,
        (byte)0x82,
    };

    /**
     * Creates a new instance.
     * @param name the test case name
     */
    public CfMapUpdaterTest(@NotNull final String name) {
        super(name);
    }

    /**
     * Creates a new test suite containing this test case.
     * @return the new test suite
     */
    @NotNull
    public static Test suite() {
        return new TestSuite(CfMapUpdaterTest.class);
    }

    /**
     * Runs the regression tests.
     * @param args the command line arguments (ignored)
     */
    public static void main(@NotNull final String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Checks that a cleared multi-tile face causes all affected tiles to become
     * fog-of-war.
     * @throws IOException if an error occurs
     */
    public void testFogOfWar1() throws IOException {
        final FaceCache faceCache = new FaceCache();
        final FacesManager facesManager = new TestFacesManager(faceCache);
        defineFace(faceCache, 1, "M", PNG64X64);
        defineFace(faceCache, 2, "_", PNG32X32);

        final CfMapUpdater mapUpdater = new CfMapUpdater(facesManager);

        mapUpdater.processNewMap(5, 5);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(0, 0, 0), 2, true);
        mapUpdater.processMapFace(new Location(1, 0, 0), 2, true);
        mapUpdater.processMapFace(new Location(0, 1, 0), 2, true);
        mapUpdater.processMapFace(new Location(1, 1, 0), 2, true);
        mapUpdater.processMapFace(new Location(1, 1, 6), 1, true);
        mapUpdater.processMapEnd(true);
        assertEquals(""+"[H0=_,T6=M][H0=_,T6=M]\n"+"[H0=_,T6=M][H0=_,H6=M]\n", toString(mapUpdater.getMap(), 0, 0, 2, 2));

        mapUpdater.processMapBegin();
        mapUpdater.processMapClear(1, 0);
        mapUpdater.processMapEnd(true);
        assertEquals(""+"[H0=_,T6=M][#,H0=_,T6=M]\n"+"[H0=_,T6=M][H0=_,H6=M]\n", toString(mapUpdater.getMap(), 0, 0, 2, 2));

        mapUpdater.processMapBegin();
        mapUpdater.processMapClear(1, 1);
        mapUpdater.processMapEnd(true);
        assertEquals(""+"[H0=_][#,H0=_,T6=M]\n"+"[H0=_][#,H0=_,H6=M]\n", toString(mapUpdater.getMap(), 0, 0, 2, 2));
    }

    /**
     * Checks that a regression causing display artifacts is fixed.
     * @throws IOException if an error occurs
     */
    public void testDisplayArtifacts1() throws IOException {
        final FaceCache faceCache = new FaceCache();
        final FacesManager facesManager = new TestFacesManager(faceCache);
        defineFace(faceCache, 307, "behemoth.x31", PNG64X64);
        defineFace(faceCache, 308, "behemoth.x32", PNG64X64);
        defineFace(faceCache, 309, "behemoth.x33", PNG64X64);
        defineFace(faceCache, 310, "behemoth.x71", PNG64X64);
        defineFace(faceCache, 932, "charwoman.132", PNG32X32);
        defineFace(faceCache, 4607, "woodfloor.111", PNG32X32);
        defineFace(faceCache, 312, "behemoth.x73", PNG64X64);

        final CfMapUpdater mapUpdater = new CfMapUpdater(facesManager);

        mapUpdater.processNewMap(10, 10);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(7, 8, 0), 4607, true);
        mapUpdater.processMapFace(new Location(8, 8, 0), 4607, true);
        mapUpdater.processMapFace(new Location(9, 8, 0), 4607, true);
        mapUpdater.processMapFace(new Location(7, 9, 0), 4607, true);
        mapUpdater.processMapFace(new Location(8, 9, 0), 4607, true);
        mapUpdater.processMapFace(new Location(9, 9, 0), 4607, true);
        mapUpdater.processMapFace(new Location(9, 9, 6), 312, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(26);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(9, 9, 6), 307, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(27);
        mapUpdater.getMapAnimations().tick(28);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(9, 9, 6), 308, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(29);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(0, 5, 6), 0, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(30);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(9, 9, 6), 309, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(31);
        mapUpdater.getMapAnimations().tick(32);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(9, 9, 6), 308, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(33);
        mapUpdater.getMapAnimations().tick(34);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(9, 9, 6), 0, true);
        mapUpdater.processMapFace(new Location(10, 9, 6), 307, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(35);
        mapUpdater.getMapAnimations().tick(36);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(0, 6, 6), 932, true);
        mapUpdater.processMapFace(new Location(9, 9, 6), 312, true);
        mapUpdater.processMapClear(10, 9);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(37);
        mapUpdater.getMapAnimations().tick(38);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(8, 9, 6), 310, true);
        mapUpdater.processMapFace(new Location(9, 9, 6), 0, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.getMapAnimations().tick(39);

        assertEquals(""+"[H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111][]\n"+"[H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111,H6=behemoth.x71][H0=woodfloor.111][#,H6=behemoth.x31]\n", toString(mapUpdater.getMap(), 7, 8, 4, 2));
    }

    /**
     * Checks that a regression causing display artifacts is fixed.
     * @throws IOException if an error occurs
     */
    public void testDisplayArtifacts2() throws IOException {
        final FaceCache faceCache = new FaceCache();
        final FacesManager facesManager = new TestFacesManager(faceCache);
        defineFace(faceCache, 7, "a.x11", PNG64X64);
        defineFace(faceCache, 8, "b.x12", PNG64X64);

        final CfMapUpdater mapUpdater = new CfMapUpdater(facesManager);

        mapUpdater.processNewMap(10, 10);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(5, 10, 6), 7, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(4, 10, 6), 8, true);
        mapUpdater.processMapClear(5, 10);
        mapUpdater.processMapEnd(true);

        assertEquals(""+"[T6=b.x12][T6=b.x12][]\n"+"[T6=b.x12][H6=b.x12][#,H6=a.x11]\n", toString(mapUpdater.getMap(), 3, 9, 3, 2));
    }

    /**
     * Checks that a regression causing display artifacts is fixed.
     * @throws IOException if an error occurs
     */
    public void testDisplayArtifacts3() throws IOException {
        final FaceCache faceCache = new FaceCache();
        final FacesManager facesManager = new TestFacesManager(faceCache);
        final CfMapUpdater mapUpdater = new CfMapUpdater(facesManager);

        mapUpdater.processNewMap(23, 16);
        defineFace(faceCache, 1316, "demon_lord.x11", PNG128X256);

        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(new Location(4, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapScroll(-1, 0);
        mapUpdater.processMapFace(new Location(5, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapClear(5, 17);
        mapUpdater.processMapFace(new Location(6, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapScroll(-1, 0);
        mapUpdater.processMapFace(new Location(7, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapScroll(-1, 0);
        mapUpdater.processMapFace(new Location(8, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);

        mapUpdater.processMapBegin();
        mapUpdater.processMapScroll(-1, 0);
        mapUpdater.processMapFace(new Location(9, 17, 6), 1316, true);
        mapUpdater.processMapEnd(true);
        assertEquals(""+"[][T6=demon_lord.x11][T6=demon_lord.x11][T6=demon_lord.x11][T6=demon_lord.x11][][][][]\n", toString(mapUpdater.getMap(), 5, 10, 9, 1));

        mapUpdater.processMapBegin();
        mapUpdater.processMapScroll(-1, 0);
        mapUpdater.processMapFace(new Location(11, 17, 6), 1316, true);
        assertEquals(""+"[][][][T6=demon_lord.x11][T6=demon_lord.x11][T6=demon_lord.x11][T6=demon_lord.x11][][]\n", toString(mapUpdater.getMap(), 5, 10, 9, 1));
    }

    /**
     * Returns a string representation of a rectangular area of a {@link CfMap}
     * instance.
     * @param map the map instance
     * @param x0 the left border of the area
     * @param y0 the top border of the area
     * @param w the width of the area
     * @param h the height of the area
     * @return the string representation
     */
    @NotNull
    private static String toString(@NotNull final CfMap map, final int x0, final int y0, final int w, final int h) {
        final StringBuilder sb = new StringBuilder();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (map) {
            for (int y = y0; y < y0+h; y++) {
                for (int x = x0; x < x0+w; x++) {
                    sb.append('[');

                    boolean firstFace = true;

                    if (map.isFogOfWar(x, y)) {
                        sb.append('#');
                        firstFace = false;
                    }

                    for (int l = 0; l < Map2.NUM_LAYERS; l++) {
                        final Face face = map.getFace(x, y, l);
                        if (face != null) {
                            if (!firstFace) {
                                sb.append(',');
                            }
                            sb.append('H');
                            sb.append(l);
                            sb.append('=');
                            sb.append(face.getFaceName());
                            firstFace = false;
                        }

                        final CfMapSquare headMapSquare = map.getHeadMapSquare(x, y, l);
                        if (headMapSquare != null) {
                            final Face headFace = headMapSquare.getFace(l);
                            if (!firstFace) {
                                sb.append(',');
                            }
                            sb.append('T');
                            sb.append(l);
                            sb.append('=');
                            sb.append(headFace == null ? "null" : headFace.getFaceName());
                            firstFace = false;
                        }
                    }
                    sb.append(']');
                }
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Adds a new face to a {@link FaceCache}.
     * @param faceCache the face cache to add to
     * @param faceNum the face number to add
     * @param faceName the face name to add
     * @param data the face data as .png
     */
    private static void defineFace(@NotNull final FaceCache faceCache, final int faceNum, @NotNull final String faceName, @NotNull final byte[] data) {
        final Face face = new Face(faceNum, faceName, 0);
        faceCache.addFace(face);

        final ImageIcon originalImageIcon;
        try {
            originalImageIcon = new ImageIcon(data);
        } catch (final IllegalArgumentException ex) {
            fail("Invalid .png data for face "+face+": "+ex.getMessage());
            throw new AssertionError(ex);
        }

        if (originalImageIcon.getIconWidth() <= 0 || originalImageIcon.getIconHeight() <= 0) {
            fail("Invalid .png size for face "+face);
            throw new AssertionError();
        }

        final FaceImages faceImages = FaceImagesUtils.newFaceImages(originalImageIcon);
        face.setFaceImages(faceImages);
    }

}
