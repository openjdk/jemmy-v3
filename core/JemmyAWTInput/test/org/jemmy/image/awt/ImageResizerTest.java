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
package org.jemmy.image.awt;

import java.awt.image.BufferedImage;

import org.jemmy.image.Image;
import org.jemmy.image.ImageComparator;
import org.jemmy.image.awt.AWTImage;
import org.jemmy.image.awt.ResizeImageComparator;
import org.jemmy.image.awt.ResizeImageComparator.ResizeMode;
import org.jemmy.image.awt.StrictImageComparator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertEquals;

/**
 *
 * @author mrkam
 */
public class ImageResizerTest {

    public ImageResizerTest() {
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

    /**
     * Test of default value of ResizeMode, of class ImageResizer.
     */
    @Test
    public void testDefaultResizeMode() {
        System.out.println("testDefaultResizeMode");
        ResizeMode expResult = ResizeMode.PROPORTIONAL_RESIZE;
        ResizeMode result = new ResizeImageComparator(null).resizeMode;
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultResizeMode method, of class ImageResizer.
     */
    @Test
    public void testProportionalResizeMode() {
        System.out.println("testProportionalResizeMode");

        new ResizeImageComparator(new ImageComparator() {

            public Image compare(Image image1,
                                 Image image2) {
                assertEquals(50, ((AWTImage) image2).getSize().width);
                assertEquals(50, ((AWTImage) image2).getSize().height);
                return null;
            }

            public String getID() {
                return "test";
            }
        }).compare(new AWTImage(new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB)),
                new AWTImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)));
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedNoResize() {
        System.out.println("getResizedNoResize");
        final AWTImage image1 =
                new AWTImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB));
        final AWTImage image2 =
                new AWTImage(new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB));

        new ResizeImageComparator(ResizeMode.NO_RESIZE, new ImageComparator() {

            public Image compare(Image im1,
                    Image im2) {
                assertEquals(image1, im1);
                assertEquals(image2, im2);
                return null;
            }

            public String getID() {
                return "test";
            }
        }).compare(image1, image2);
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedProportional1() {
        System.out.println("getResizedProportional1");
        BufferedImage image1 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertNull(new ResizeImageComparator(ResizeMode.PROPORTIONAL_RESIZE,
                new StrictImageComparator()).compare(new AWTImage(image1), new AWTImage(image2)));
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedProportional2() {
        System.out.println("getResizedProportional2");
        BufferedImage image1 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 =
                new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB);
        assertNull(new ResizeImageComparator(ResizeMode.PROPORTIONAL_RESIZE,
                new StrictImageComparator()).compare(new AWTImage(image1), new AWTImage(image2)));
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedProportional3() {
        System.out.println("getResizedProportional3");
        final AWTImage image1 =
                new AWTImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB));
        final AWTImage image2 =
                new AWTImage(new BufferedImage(10, 15, BufferedImage.TYPE_INT_RGB));
        new ResizeImageComparator(ResizeMode.PROPORTIONAL_RESIZE,
                new ImageComparator() {

                    public Image compare(Image im1,
                            Image im2) {
                        assertEquals(image1, im1);
                        assertEquals(image2, im2);
                        return null;
                    }

                    public String getID() {
                        return "test";
                    }
                }).compare(image1, image2);
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedArbitrary1() {
        System.out.println("getResizedArbitrary1");
        BufferedImage image1 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertNull(new ResizeImageComparator(ResizeMode.ARBITRARY_RESIZE,
                new StrictImageComparator()).compare(new AWTImage(image1), new AWTImage(image2)));
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedArbitrary2() {
        System.out.println("getResizedArbitrary2");
        BufferedImage image1 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 =
                new BufferedImage(15, 15, BufferedImage.TYPE_INT_RGB);
        assertNull(new ResizeImageComparator(ResizeMode.ARBITRARY_RESIZE,
                new StrictImageComparator()).compare(new AWTImage(image1), new AWTImage(image2)));
    }

    /**
     * Test of getResized method, of class ImageResizer.
     */
    @Test
    public void testGetResizedArbitrary3() {
        System.out.println("getResizedArbitrary3");
        BufferedImage image1 =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 =
                new BufferedImage(10, 15, BufferedImage.TYPE_INT_RGB);
        assertNull(new ResizeImageComparator(ResizeMode.ARBITRARY_RESIZE,
                new StrictImageComparator()).compare(new AWTImage(image1), new AWTImage(image2)));
    }
}
