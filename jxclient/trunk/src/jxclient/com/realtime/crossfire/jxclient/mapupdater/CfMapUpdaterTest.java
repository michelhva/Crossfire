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
package com.realtime.crossfire.jxclient.mapupdater;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Regression tests for class {@link CfMapUpdater}.
 *
 * @author Andreas Kirschbaum
 */
public class CfMapUpdaterTest extends TestCase
{
    /**
     * A .png file of size 64x64.
     */
    private static final byte[] png32x32 =
    {
        (byte)0x89, (byte)0x50, (byte)0x4e, (byte)0x47, (byte)0x0d, (byte)0x0a, (byte)0x1a, (byte)0x0a,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x20,
        (byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x73, (byte)0x7a, (byte)0x7a,
        (byte)0xf4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x73, (byte)0x52, (byte)0x47,
        (byte)0x42, (byte)0x00, (byte)0xae, (byte)0xce, (byte)0x1c, (byte)0xe9, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x1a, (byte)0x49, (byte)0x44, (byte)0x41, (byte)0x54, (byte)0x58, (byte)0xc3,
        (byte)0xed, (byte)0xc1, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x82,
        (byte)0x20, (byte)0xff, (byte)0xaf, (byte)0x6e, (byte)0x48, (byte)0x40, (byte)0x01, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0xef, (byte)0x06, (byte)0x10, (byte)0x20, (byte)0x00, (byte)0x01,
        (byte)0x97, (byte)0xf7, (byte)0x57, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x49, (byte)0x45, (byte)0x4e, (byte)0x44, (byte)0xae, (byte)0x42, (byte)0x60, (byte)0x82,
    };

    /**
     * A .png file of size 64x64.
     */
    private static final byte[] png64x64 =
    {
        (byte)0x89, (byte)0x50, (byte)0x4e, (byte)0x47, (byte)0x0d, (byte)0x0a, (byte)0x1a, (byte)0x0a,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x40,
        (byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xaa, (byte)0x69, (byte)0x71,
        (byte)0xde, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x73, (byte)0x52, (byte)0x47,
        (byte)0x42, (byte)0x00, (byte)0xae, (byte)0xce, (byte)0x1c, (byte)0xe9, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x27, (byte)0x49, (byte)0x44, (byte)0x41, (byte)0x54, (byte)0x78, (byte)0xda,
        (byte)0xed, (byte)0xc1, (byte)0x01, (byte)0x0d, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xc2,
        (byte)0xa0, (byte)0xf7, (byte)0x4f, (byte)0x6d, (byte)0x0e, (byte)0x37, (byte)0xa0, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x77,
        (byte)0x03, (byte)0x40, (byte)0x40, (byte)0x00, (byte)0x01, (byte)0xaf, (byte)0x7a, (byte)0x0e,
        (byte)0xe8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4e,
        (byte)0x44, (byte)0xae, (byte)0x42, (byte)0x60, (byte)0x82,
    };

    /**
     * Create a new instance.
     *
     * @param name the test case name
     */
    public CfMapUpdaterTest(final String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(CfMapUpdaterTest.class);
    }

    /**
     * Run the regression tests.
     *
     * @param args The command line arguments (ignored).
     */
    public static void main(final String[] args)
    {
        TestRunner.run(suite());
    }

    /**
     * Check that a cleared multi-tile face causes all affected tiles to become
     * fog-of-war.
     */
    public void testFogOfWar1()
    {
        final Faces faces = new Faces();
        defineFace(faces, 1, "M", png64x64);
        defineFace(faces, 2, "_", png32x32);

        final CfMapUpdater mapUpdater = new CfMapUpdater();

        mapUpdater.processNewmap(5, 5);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(0, 0, 0, 2, faces);
        mapUpdater.processMapFace(1, 0, 0, 2, faces);
        mapUpdater.processMapFace(0, 1, 0, 2, faces);
        mapUpdater.processMapFace(1, 1, 0, 2, faces);
        mapUpdater.processMapFace(1, 1, 6, 1, faces);
        mapUpdater.processMapEnd(true);
        assertEquals(""
            +"[H0=_,T6=M][H0=_,T6=M]\n"
            +"[H0=_,T6=M][H0=_,H6=M]\n"
            , toString(mapUpdater.getMap(), 0, 0, 2, 2));

        mapUpdater.processMapBegin();
        mapUpdater.processMapClear(1, 0);
        mapUpdater.processMapEnd(true);
        assertEquals(""
            +"[H0=_,T6=M][#,H0=_,T6=M]\n"
            +"[H0=_,T6=M][H0=_,H6=M]\n"
            , toString(mapUpdater.getMap(), 0, 0, 2, 2));

        mapUpdater.processMapBegin();
        mapUpdater.processMapClear(1, 1);
        mapUpdater.processMapEnd(true);
        assertEquals(""
            +"[H0=_][#,H0=_]\n"
            +"[H0=_][#,H0=_,H6=M]\n"
            , toString(mapUpdater.getMap(), 0, 0, 2, 2));
    }

    /**
     * Check that a regression causing display artifacts is fixed.
     */
    public void testDisplayArtifacts1()
    {
        final Faces faces = new Faces();

        defineFace(faces, 307, "behemoth.x31", png64x64);
        defineFace(faces, 308, "behemoth.x32", png64x64);
        defineFace(faces, 309, "behemoth.x33", png64x64);
        defineFace(faces, 310, "behemoth.x71", png64x64);
        defineFace(faces, 932, "charwoman.132", png32x32);
        defineFace(faces, 4607, "woodfloor.111", png32x32);
        defineFace(faces, 312, "behemoth.x73", png64x64);

        final CfMapUpdater mapUpdater = new CfMapUpdater();

        mapUpdater.processNewmap(10, 10);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(7, 8, 0, 4607, faces);
        mapUpdater.processMapFace(8, 8, 0, 4607, faces);
        mapUpdater.processMapFace(9, 8, 0, 4607, faces);
        mapUpdater.processMapFace(7, 9, 0, 4607, faces);
        mapUpdater.processMapFace(8, 9, 0, 4607, faces);
        mapUpdater.processMapFace(9, 9, 0, 4607, faces);
        mapUpdater.processMapFace(9, 9, 6, 312, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(26);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(9, 9, 6, 307, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(27);
        mapUpdater.processTick(28);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(9, 9, 6, 308, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(29);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(0, 5, 6, 0, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(30);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(9, 9, 6, 309, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(31);
        mapUpdater.processTick(32);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(9, 9, 6, 308, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(33);
        mapUpdater.processTick(34);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(9, 9, 6, 0, faces);
        mapUpdater.processMapFace(10, 9, 6, 307, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(35);
        mapUpdater.processTick(36);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(0, 6, 6, 932, faces);
        mapUpdater.processMapFace(9, 9, 6, 312, faces);
        mapUpdater.processMapClear(10, 9);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(37);
        mapUpdater.processTick(38);
        mapUpdater.processMapBegin();
        mapUpdater.processMapFace(8, 9, 6, 310, faces);
        mapUpdater.processMapFace(9, 9, 6, 0, faces);
        mapUpdater.processMapEnd(true);

        mapUpdater.processTick(39);

        assertEquals(""
            +"[H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111][]\n"
            +"[H0=woodfloor.111,T6=behemoth.x71][H0=woodfloor.111,H6=behemoth.x71][H0=woodfloor.111][#,H6=behemoth.x31]\n"
            , toString(mapUpdater.getMap(), 7, 8, 4, 2));
    }

    private static String toString(final CfMap map, final int x0, final int y0, final int w, final int h)
    {
        final StringBuilder sb = new StringBuilder();
        for (int y = y0; y < y0+h; y++)
        {
            for (int x = x0; x < x0+w; x++)
            {
                sb.append('[');

                boolean firstFace = true;

                if (map.isFogOfWar(x, y))
                {
                    if (!firstFace)
                    {
                        sb.append(',');
                    }
                    sb.append('#');
                    firstFace = false;
                }

                for (int l = 0; l < CrossfireMap2Command.NUM_LAYERS; l++)
                {
                    final Face face = map.getFace(x, y, l);
                    if (face != null)
                    {
                        if (!firstFace)
                        {
                            sb.append(',');
                        }
                        sb.append('H');
                        sb.append(l);
                        sb.append('=');
                        sb.append(face.getName());
                        firstFace = false;
                    }

                    final CfMapSquare headMapSquare = map.getHeadMapSquare(x, y, l);
                    if (headMapSquare != null)
                    {
                        final Face headFace = headMapSquare.getFace(l);
                        if (!firstFace)
                        {
                            sb.append(',');
                        }
                        sb.append('T');
                        sb.append(l);
                        sb.append('=');
                        sb.append(headFace.getName());
                        firstFace = false;
                    }
                }
                sb.append(']');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void defineFace(final Faces faces, final int face, final String name, final byte[] data)
    {
        faces.setFace(face, 0, name);
        faces.setImage(face, data, 0, data.length);
    }
}
