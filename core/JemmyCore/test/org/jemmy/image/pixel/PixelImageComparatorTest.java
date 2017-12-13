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

import org.jemmy.env.Environment;
import org.jemmy.image.Image;
import org.jemmy.image.pixel.Raster.Component;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class PixelImageComparatorTest {

    private static Component[] supportedComps;

    private PixelImageComparator comparator;

    @BeforeClass
    public static void setUpClass() throws Exception {
         supportedComps = new Component[] {
            Component.RED, Component.GREEN, Component.BLUE, Component.ALPHA
        };
    }

    @BeforeMethod
    public void setUp() {
         comparator = new PixelImageComparator(new Environment()) {
            @Override
            protected Image toImage(Raster image) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            protected Raster toRaster(Image image) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            protected WriteableRaster createDiffRaster(Raster r1, Raster r2) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test
    public void testGetComponentsValueFunction() {
        //Translucent colors
        final double alpha = 0.5;
        double[] black = new double[] { 0.0, 0.0, 0.0, alpha };
        double[] white = new double[] { 1.0, 1.0, 1.0, alpha };
        double result;

        result = comparator.getComponentValue(supportedComps, black, Component.RED);
        AssertJUnit.assertEquals(0.5, result, 0.001);

        result = comparator.getComponentValue(supportedComps, white, Component.RED);
        AssertJUnit.assertEquals(1.0, result, 0.001);

        //Opaque colors
        black =  new double[] { 0.0, 0.0, 0.0, 1.0 };
        white = new double[] { 1.0, 1.0, 1.0, 1.0 };

        result = comparator.getComponentValue(supportedComps, black, Component.RED);
        AssertJUnit.assertEquals(0.0, result, 0.001);

        result = comparator.getComponentValue(supportedComps, white, Component.RED);
        AssertJUnit.assertEquals(1.0, result, 0.001);

        //Transparent colors
        black =  new double[] { 0.0, 0.0, 0.0, 0.0 };
        white = new double[] { 1.0, 1.0, 1.0, 0.0 };

        result = comparator.getComponentValue(supportedComps, black, Component.RED);
        AssertJUnit.assertEquals(1.0, result, 0.001);

        result = comparator.getComponentValue(supportedComps, white, Component.RED);
        AssertJUnit.assertEquals(1.0, result, 0.001);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testAlphaOutOfBounds() {
        double[] colors = new double[] { 0.0, 0.0, 0.1, 1.1 };
        comparator.getComponentValue(supportedComps, colors, Component.RED);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testColorComponentOutOfBounds() {
        double[] colors = new double[] { 1.1, 1.1, 1.1, 1.0 };
        comparator.getComponentValue(supportedComps, colors, Component.RED);
    }
}
