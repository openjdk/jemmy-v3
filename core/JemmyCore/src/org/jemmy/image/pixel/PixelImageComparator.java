/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

import java.util.Arrays;
import org.jemmy.Dimension;
import org.jemmy.env.Environment;
import org.jemmy.image.Image;
import org.jemmy.image.ImageComparator;
import org.jemmy.image.pixel.Raster.Component;

/**
 * @author shura
 */
public abstract class PixelImageComparator implements ImageComparator {

    static {
        Environment.getEnvironment().setPropertyIfNotSet(RasterComparator.class,
                new PixelEqualityRasterComparator(0));
//                new MaxDistanceComparator((double)1/0x8f));
    }

    private RasterComparator comparator = null;
    private Environment env = null;

    public PixelImageComparator(RasterComparator comparator) {
        this.comparator = comparator;
    }

    public PixelImageComparator(Environment env) {
        this.env = env;
    }

    public synchronized RasterComparator getRasterComparator() {
        if(comparator == null) {
            return env.getProperty(RasterComparator.class);
        } else {
            return comparator;
        }
    }

    public static Dimension computeDiffSize(Raster one, Raster two) {
        if (one.getSize().equals(two.getSize())) {
            return one.getSize();
        } else {
            return null;
        }
    }

    public Image compare(Image image1, Image image2) {
        Raster pi1 = toRaster(image1);
        Raster pi2 = toRaster(image2);
        if (!getRasterComparator().compare(pi1, pi2)) {
            return toImage(computeDifference(pi1, pi2));
        } else {
            return null;
        }
    }

    public WriteableRaster computeDifference(Raster image1, Raster image2) {
        Dimension size = computeDiffSize(image1, image2);
        if (size == null) {
            size = new Dimension(Math.max(image1.getSize().width, image2.getSize().width),
                    Math.max(image1.getSize().height, image2.getSize().height));
        }
        WriteableRaster res = createDiffRaster(image1, image2);
        double[] colors1 = new double[image1.getSupported().length];
        double[] colors2 = new double[image2.getSupported().length];
        double[] colorsRes = new double[res.getSupported().length];
        for (int x = 0; x < size.width; x++) {
            for (int y = 0; y < size.height; y++) {
                if (x < image1.getSize().width && y < image1.getSize().height) {
                    image1.getColors(x, y, colors1);
                } else {
                    Arrays.fill(colors1, 0);
                }
                if (x < image2.getSize().width && y < image2.getSize().height) {
                    image2.getColors(x, y, colors2);
                } else {
                    Arrays.fill(colors2, 1);
                }
                calcDiffColor(image1.getSupported(), colors1, image2.getSupported(), colors2,
                        res.getSupported(), colorsRes);
                res.setColors(x, y, colorsRes);
            }
        }
        return res;
    }

    private static final Component[] diffComponents = {
        Component.RED, Component.BLUE, Component.GREEN
    };

    protected void calcDiffColor(Raster.Component[] comps1, double[] colors1,
            Raster.Component[] comps2, double[] colors2, Raster.Component[] compsRes, double[] colorsRes) {
        double square1, square2;
        double dist = 0;

        for (Component c : diffComponents) {
            square1 = getComponentValue(comps1, colors1, c);
            square2 = getComponentValue(comps2, colors2, c);
            dist += (square2 - square1) * (square2 - square1);
        }
        for (Component c : diffComponents) {
            colorsRes[arrayIndexOf(compsRes, c)] = Math.sqrt(dist) / Math.sqrt(3);
        }
        colorsRes[arrayIndexOf(compsRes, Component.ALPHA)] = 1;
    }

    public String getID() {
        return getRasterComparator().getID();
    }

    protected abstract Image toImage(Raster image);

    protected abstract Raster toRaster(Image image);

    protected abstract WriteableRaster createDiffRaster(Raster r1, Raster r2);

    public static int arrayIndexOf(Raster.Component[] comps, Raster.Component comp) {
        for (int i = 0; i < comps.length; i++) {
            if (comp == comps[i]) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown component " + comp);
    }

    /**
     * Returns color component value using its alpha information
     *
     * @param components available color components
     * @param colors color components values
     * @param comp required color component
     *
     * @return value of the required color component.
     * If pixel is not opaque, then it is blended with white
     * opaque background
     */
    protected double getComponentValue(Component[] components, double[] colors, Component comp) {

        double result = colors[arrayIndexOf(components, comp)];

        if(result < 0.0 || result > 1.0) throw new IllegalStateException("Component value = " + result);

        //Find alpha index if exists
        int idxAlpha = -1;
        try {
            idxAlpha = arrayIndexOf(components, Component.ALPHA);
        } catch (IllegalArgumentException ex) {
        }

        //If alpha component is available
        if (idxAlpha != -1) {
            double alpha = colors[idxAlpha];

            if(alpha < 0.0 || alpha > 1.0) throw new IllegalStateException("Alpha value = " + alpha);

            //If not opaque
            if (alpha < 1.0) {
                //Blend with opaque white
                result = Math.min(1.0, alpha * result + 1 - alpha);
            }
        }

        return result;
    }
}
