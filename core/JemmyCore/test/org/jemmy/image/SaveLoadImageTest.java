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
package org.jemmy.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.jemmy.Dimension;
import org.jemmy.image.pixel.PNGLoader;
import org.jemmy.image.pixel.PNGSaver;
import org.jemmy.image.pixel.Raster;
import org.jemmy.image.pixel.WriteableRaster;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;


/**
 *
 * @author shura
 */
public class SaveLoadImageTest {

    public SaveLoadImageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }
    @Test
    public void hello() throws IOException {
        int [][][] data = new int[99][99][3];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int k = 0; k < data[i][j].length; k++) {
                    data[i][j][k] = 0;
                }
                data[i][j][i%3] = (int)((((double)j)/data[i].length)*0xFF);
            }
        }
        Raster img = new RasterImpl(data.length, data[0].length, data);
        File out = new File(System.getProperty("user.dir") + File.separator + "out.png");
        new PNGSaver(out).encode(img);
        Raster res = new PNGLoader(new FileInputStream(out)) {

            @Override
            protected WriteableRaster createRaster(int width, int height) {
                return new RasterImpl(width, height);
            }
        }.decode();
        out = new File(System.getProperty("user.dir") + File.separator + "out2.png");
        new PNGSaver(out).encode(img); //jusr for a reference
        double[] oColors = new double[3];
        double[] rColors = new double[3];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                img.getColors(i, j, oColors);
                res.getColors(i, j, rColors);
                for (int k = 0; k < oColors.length; k++) {
                    if(oColors[k] != rColors[k]) {
                        System.out.println("Different as (" + i + "," + j + "),"+k+":");
                        System.out.println(oColors[k] +" != "+ rColors[k]);
                        fail();
                    }
                }
            }
        }
    }

    private class RasterImpl implements WriteableRaster {


        Component[] RGB = new Component[] {Component.RED, Component.GREEN, Component.BLUE};
        Dimension size;
        int[][][] data;

        public RasterImpl(int w, int h) {
            size = new Dimension(w, h);
            data = new int[w][h][RGB.length];
        }

        public RasterImpl(int w, int h, int[][][] data) {
            size = new Dimension(w, h);
            this.data = data;
        }

        public Dimension getSize() {
            return size;
        }

        public void getColors(int x, int y, double[] colors) {
            for (int i = 0; i < colors.length; i++) {
                colors[i] = (double)data[x][y][i]/0xFF;
            }
        }

        public Component[] getSupported() {
            return RGB;
        }

        public void setColors(int x, int y, double[] values) {
            for (int i = 0; i < values.length; i++) {
                data[x][y][i] = (int)(values[i] * 0xFF);
            }
        }
    }
}
