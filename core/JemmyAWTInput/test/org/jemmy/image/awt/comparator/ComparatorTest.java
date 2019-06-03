/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.image.awt.comparator;

import java.awt.Color;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jemmy.image.awt.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 *
 * @author shura
 */
public class ComparatorTest {

    public ComparatorTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void average() {
        BufferedImage golden =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/golden-averagedistance.png"), true);
        BufferedImage actual =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/actual-averagedistance.png"), true);
        assertNotNull(new AverageDistanceImageComparator(.001).compare(new AWTImage(golden), new AWTImage(actual)));
    }

    @Test
    public void strict() {
        BufferedImage golden =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/golden-strict.png"), true);
        BufferedImage actual =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/actual-strict.png"), true);
        assertNotNull(new StrictImageComparator().compare(new AWTImage(golden), new AWTImage(actual)));
    }

    @Test
    public void strict_hyperlink() throws FileNotFoundException, IOException {
        BufferedImage golden =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/golden-hyperlink.png"), true);
        BufferedImage actual =
                PNGDecoder.decode(getClass().getClassLoader().
                getResourceAsStream("org/jemmy/image/awt/comparator/actual-hyperlink.png"), true);
        BufferedImage diff = ((AWTImage)new StrictImageComparator().compare(new AWTImage(golden), new AWTImage(actual))).getTheImage();
        //assertNull(diff);
        //barbashov sucks!
        //he submits similar images
        new PNGEncoder(new File("/tmp/aaa.png")).encode(diff);
        assertNotNull(diff);
        for (int x = 0; x < diff.getWidth(); x++) {
            for (int y = 0; y < diff.getHeight(); y++) {
                /*
                if(new Color(golden.getRGB(x, y)).getAlpha() != 255) {
                    System.out.println("Haha!");
                }
                if(new Color(actual.getRGB(x, y)).getAlpha() != 255) {
                    System.out.println("Haha!");
                }
                 *
                 */
                Color color = new Color(diff.getRGB(x, y));
                if (color.getRed() != 0 || color.getGreen() != 0 || color.getBlue() != 0) {
                    return;
                }
            }
        }
        fail("There got to be non black pixels.");
    }
}
