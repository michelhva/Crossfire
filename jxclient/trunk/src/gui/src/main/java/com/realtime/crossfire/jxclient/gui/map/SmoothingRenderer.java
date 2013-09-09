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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.map;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesProvider;
import com.realtime.crossfire.jxclient.faces.SmoothFaces;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import java.awt.Graphics;
import java.util.Arrays;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer for painting smoothed faces into map views.
 * @author tchize
 * @author Andreas Kirschbaum
 */
public class SmoothingRenderer {

    /**
     * Border weight for west.
     */
    private static final int BORDER_WEST = 1;

    /**
     * Border weight for north.
     */
    private static final int BORDER_NORTH = 2;

    /**
     * Border weight for east.
     */
    private static final int BORDER_EAST = 4;

    /**
     * Border weight for south.
     */
    private static final int BORDER_SOUTH = 8;

    /**
     * Corner weight for northwest.
     */
    private static final int CORNER_NORTHWEST = 1;

    /**
     * Corner weight for northeast.
     */
    private static final int CORNER_NORTHEAST = 2;

    /**
     * Corner weight for southeast.
     */
    private static final int CORNER_SOUTHEAST = 4;

    /**
     * Corner weight for southwest.
     */
    private static final int CORNER_SOUTHWEST = 8;

    /**
     * X offset for map coordinate calculation. Maps index to x offset.
     */
    @NotNull
    private static final int[] DX = {
        1,
        2,
        2,
        2,
        1,
        0,
        0,
        0,
    };

    /**
     * Y offset for map coordinate calculation. Maps index to y offset.
     */
    @NotNull
    private static final int[] DY = {
        0,
        0,
        1,
        2,
        2,
        2,
        1,
        0,
    };

    /**
     * Weight (x coordinate) in smoothing face of a corner. Maps index to corner
     * weight.
     */
    @NotNull
    private static final int[] BORDER_WEIGHT = {
        BORDER_NORTH,
        0,
        BORDER_EAST,
        0,
        BORDER_SOUTH,
        0,
        BORDER_WEST,
        0,
    };

    /**
     * Weight (x coordinate) in smoothing face of a corner. Maps index to corner
     * weight.
     */
    @NotNull
    private static final int[] CORNER_WEIGHT = {
        0,
        CORNER_NORTHEAST,
        0,
        CORNER_SOUTHEAST,
        0,
        CORNER_SOUTHWEST,
        0,
        CORNER_NORTHWEST,
    };

    /**
     * Corner excludes due to borders. Maps index to corner weight.
     */
    @NotNull
    private static final int[] BORDER_CORNER_EXCLUDE = {
        CORNER_NORTHWEST+CORNER_NORTHEAST,
        0,
        CORNER_NORTHEAST+CORNER_SOUTHEAST,
        0,
        CORNER_SOUTHEAST+CORNER_SOUTHWEST,
        0,
        CORNER_SOUTHWEST+CORNER_NORTHWEST,
        0
    };

    /**
     * The {@link SmoothFaces} to use.
     */
    @NotNull
    private final SmoothFaces smoothFaces;

    /**
     * The {@link FacesProvider} for looking up faces.
     */
    @NotNull
    private final FacesProvider facesProvider;

    /**
     * Surrounding {@link CfMapSquare }map squares} having non-zero smooth
     * levels.
     */
    @NotNull
    private final CfMapSquare[][] layerNode = new CfMapSquare[3][3];

    /**
     * Smooth values corresponding to {@link #layerNode}. Maps index to smooth
     * value.
     */
    @NotNull
    private final int[] smoothValue = new int[8];

    /**
     * Face index of the smooth face corresponding to {@link #layerNode}. Maps
     * index to face index.
     */
    @NotNull
    private final int[] smoothFace = new int[8];

    /**
     * Marks the indexes that have been painted.
     */
    @NotNull
    private final boolean[] done = new boolean[8];

    /**
     * Temporary variable.
     */
    @NotNull
    private final boolean[] isUnknownImage = new boolean[1];

    /**
     * Creates a new instance.
     * @param smoothFaces the smooth faces to use
     * @param facesProvider the face provider for looking up faces
     */
    protected SmoothingRenderer(@NotNull final SmoothFaces smoothFaces, @NotNull final FacesProvider facesProvider) {
        this.smoothFaces = smoothFaces;
        this.facesProvider = facesProvider;
    }

    /**
     * Draw the smoothing information at given position of map, for a given
     * limit smoothlevel, at a given layer. This operation may be recursive, if
     * all layer above current are to be drawn too.
     * @param graphics where to draw (graphics)
     * @param x the x-coordinate of the map square to draw, in map coordinates
     * @param y the y-coordinate of the map square to draw, in map coordinates
     * @param px the x offset for painting
     * @param py the y offset painting
     * @param layer the layer to paint
     * @param map the rendered map
     * @param tileSize the size of one tile in pixel
     */
    public void paintSmooth(@NotNull final Graphics graphics, final int x, final int y, final int px, final int py, final int layer, @NotNull final CfMap map, final int tileSize) {
        final int level = map.getSmooth(x, y, layer);
        if (level <= 0) {
            return;
        }
        for (int deltaX = 0; deltaX <= 2; deltaX++) {
            for (int deltaY = 0; deltaY <= 2; deltaY++) {
                if (deltaX != 0 || deltaY != 0) {
                    final CfMapSquare mapSquare = map.getMapSquare(x+deltaX-1, y+deltaY-1);
                    //false warning: cannot annotate with @Nullable
                    //noinspection AssignmentToNull
                    layerNode[deltaX][deltaY] = mapSquare.getSmooth(layer) > 0 ? mapSquare : null;
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            final CfMapSquare node = layerNode[DX[i]][DY[i]];
            if (node == null) {
                smoothValue[i] = 0;
                smoothFace[i] = 0;
            } else {
                final int smoothlevel = node.getSmooth(layer);
                if (smoothlevel <= level) {
                    smoothValue[i] = 0;
                    smoothFace[i] = 0;
                } else {
                    smoothValue[i] = smoothlevel;
                    final Face nodeFace = node.getFace(layer);
                    smoothFace[i] = nodeFace == null ? 0 : smoothFaces.getSmoothFace(nodeFace.getFaceNum());
                }
            }
        }

        // Now we have a list of smoothlevel higher than current square. There
        // are at most 8 different levels. so... let's check 8 times for the
        // lowest one (we draw from bottom to top!).
        Arrays.fill(done, false);
        while (true) {
            int lowest = -1;
            for (int i = 0; i < 8; i++) {
                if (smoothValue[i] > 0 && !done[i] && (lowest < 0 || smoothValue[i] < smoothValue[lowest])) {
                    lowest = i;
                }
            }
            if (lowest < 0) {
                // No more smooth to do on this square here we know 'what' to
                // smooth.
                break;
            }
            final int currentSmoothFace = smoothFace[lowest];
            // We need to calculate the weight for border and weight for
            // corners. Then we 'mark done' the corresponding squares.
            // First, the border, which may exclude some corners.
            int weight = 0;
            int weightC = 15;
            for (int i = 0; i < 8; i++) {
                if (smoothValue[i] == smoothValue[lowest] && smoothFace[i] == currentSmoothFace) {
                    done[i] = true;
                    weight += BORDER_WEIGHT[i];
                    weightC &= ~BORDER_CORNER_EXCLUDE[i];
                } else {
                    weightC &= ~CORNER_WEIGHT[i];
                }
            }
            if (currentSmoothFace == 0) {
                continue;
            }

            final ImageIcon imageIcon = facesProvider.getImageIcon(currentSmoothFace, isUnknownImage);
            if (isUnknownImage[0]) {
                map.squarePendingFace(x, y, currentSmoothFace);
            } else {
                if (weight > 0) {
                    drawImage(graphics, px, py, tileSize*weight, 0, imageIcon, tileSize);
                }
                if (weightC > 0) {
                    drawImage(graphics, px, py, tileSize*weightC, tileSize, imageIcon, tileSize);
                }
            }
        }
    }

    /**
     * Draws a <code>tileSize</code> x <code>tileSize</code> part of an {@link
     * ImageIcon}.
     * @param graphics the graphics to paint to
     * @param dstX the x coordinate to paint to
     * @param dstY the y coordinate to paint to
     * @param srcX the x coordinate to copy from <code>imageIcon</code>
     * @param srcY the y coordinate to copy from <code>imageIcon</code>
     * @param imageIcon the image icon to copy from
     * @param tileSize the size in pixels to copy
     */
    private static void drawImage(@NotNull final Graphics graphics, final int dstX, final int dstY, final int srcX, final int srcY, @NotNull final ImageIcon imageIcon, final int tileSize) {
        graphics.drawImage(imageIcon.getImage(), dstX, dstY, dstX+tileSize, dstY+tileSize, srcX, srcY, srcX+tileSize, srcY+tileSize, null);
    }

} // class SmoothingRenderer
