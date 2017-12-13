/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.jemmy.image.pixel;

import java.util.List;
import org.jemmy.Dimension;
import org.jemmy.image.pixel.Raster.Component;

/**
 *
 * @author shura
 */
public abstract class ColorMappingComparator implements RasterComparator {

    final private ColorMap left;
    final private ColorMap right;
    private RasterComparator subComparator;

    /**
     *
     * @param left
     * @param right
     * @param subComparator
     */
    public ColorMappingComparator(ColorMap left, ColorMap right,
            RasterComparator subComparator) {
        this.subComparator = subComparator;
        this.left = left;
        this.right = right;
    }

    /**
     *
     * @return
     */
    public RasterComparator getSubComparator() {
        return subComparator;
    }

    /**
     *
     * @param subComparator
     */
    public void setSubComparator(RasterComparator subComparator) {
        this.subComparator = subComparator;
    }

    /**
     *
     * @param both
     * @param subComparator
     */
    public ColorMappingComparator(ColorMap both, RasterComparator subComparator) {
        this(both, both, subComparator);
    }

    /**
     *
     * @param image1
     * @param image2
     * @return
     */
    public boolean compare(Raster image1, Raster image2) {
        return subComparator.compare(map(image1, left), map(image2, right));
    }

    /**
     *
     * @param image
     * @param map
     * @return
     */
    public WriteableRaster map(Raster image, ColorMap map) {
        WriteableRaster res = createView(image.getSize());
        double[] colors = new double[image.getSupported().length];
        double[] newColors = new double[image.getSupported().length];
        for (int x = 0; x < image.getSize().width; x++) {
            for (int y = 0; y < image.getSize().height; y++) {
                image.getColors(x, y, colors);
                map.map(image.getSupported(), colors, newColors);
                res.setColors(x, y, newColors);
            }
        }
        return res;
    }

    /**
     *
     * @param size
     * @return
     */
    protected abstract WriteableRaster createView(Dimension size);

    /**
     *
     * @return
     */
    public String getID() {
        return ColorMappingComparator.class.getName() + ":" +
                left.getID() + "," + right.getID() + "(" +
                subComparator.getID() + ")";
    }

    /**
     *
     */
    public interface ColorMap {

        /**
         *
         * @param components
         * @param values
         * @param newValues
         */
        public void map(Component[] components, double[] values, double[] newValues);
        /**
         *
         * @return
         */
        public String getID();
    }
}
