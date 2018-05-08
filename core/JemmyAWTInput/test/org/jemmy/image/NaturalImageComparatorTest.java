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
package org.jemmy.image;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import javax.imageio.ImageIO;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;

/**
 *
 * @author KAM
 */
public class NaturalImageComparatorTest {

    public NaturalImageComparatorTest() {
    }
    static final int NUMBER_OF_IMAGES = 3;
    static BufferedImage[] images;

    @BeforeClass
    public static void setUpClass() throws Exception {
        images = new BufferedImage[NUMBER_OF_IMAGES];
        for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
            images[i] = ImageIO.read(NaturalImageComparatorTest.class.getResource("image" + (i + 1) + ".jpg"));
        }
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
     * Test of compare method, of class NaturalImageComparator.
     */
    @Test
    public void testCompare1() {
        System.out.println("compare1");
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image1.createGraphics();
        g.setColor(new Color(0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        BufferedImage image2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image2.createGraphics();
        g.setColor(new Color(0.52f, 0.5f, 0.5f));
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        NaturalImageComparator instance = new NaturalImageComparator();
        assertNull(instance.compare(new AWTImage(image1), new AWTImage(image2)));
    }

    /**
     * Test of compare method, of class NaturalImageComparator.
     */
    @Test
    public void testCompare2() {
        System.out.println("compare2");
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image1.createGraphics();
        g.setColor(new Color(0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        BufferedImage image3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image3.createGraphics();
        g.setColor(new Color(0.51f, 0.51f, 0.51f));
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        NaturalImageComparator instance = new NaturalImageComparator();
        assertNull(instance.compare(new AWTImage(image1), new AWTImage(image3)));
    }

    /**
     * Test of compare method, of class NaturalImageComparator.
     */
    @Test
    public void testCompare3() {
        System.out.println("compare3");
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image1.createGraphics();
        g.setColor(new Color(0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, 10, 10);
        g.dispose();

        BufferedImage image3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        g = image3.createGraphics();
        g.setColor(new Color(0.5f, 0.5f, 0.5f));
        g.fillRect(0, 0, 10, 10);
        g.setColor(new Color(0.53f, 0.5f, 0.5f));
        g.fillRect(3, 3, 1, 1);
        g.dispose();

        NaturalImageComparator instance = new NaturalImageComparator();
        assertNotNull(instance.compare(new AWTImage(image1), new AWTImage(image3)));
    }

    /**
     * Test of compare method, of class NaturalImageComparator.
     */
    @Test
    public void testCompare4() {
        System.out.println("compare4");
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(10, 11, BufferedImage.TYPE_INT_RGB);
        BufferedImage image3 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        NaturalImageComparator instance = new NaturalImageComparator();
        assertNotNull(instance.compare(new AWTImage(image1), new AWTImage(image3)));
    }

    /**
     * Test of compare method, of class NaturalImageComparator.
     */
    @Test
    public void testCompare5() {
        System.out.println("compare5");
        Graphics2D g;

        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image3 = new BufferedImage(11, 10, BufferedImage.TYPE_INT_RGB);

        NaturalImageComparator instance = new NaturalImageComparator();
        assertNotNull(instance.compare(new AWTImage(image1), new AWTImage(image3)));
    }

    /**
     * Test of compare method, of class NaturalImageComparator.
     *
     * @throws IOException
     */
    @Test
    public void testCompare() throws IOException {
        System.out.println("compare");
        boolean[][][] expected = {
            // NO_RESIZE
            {
                {true, false, false},
                {false, true, false},
                {false, false, true}
            },
            // PROPORTIONAL_RESIZE
            {
                {true, true, false},
                {true, true, false},
                {false, false, true}
            },
            // ARBITRARY_RESIZE
            {
                {true, true, true},
                {true, true, true},
                {true, true, true}
            }
        };
        for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
            BufferedImage image1 = images[i];
            for (int j = i; j < NUMBER_OF_IMAGES; j++) {
                BufferedImage image2 = images[j];
                System.out.println("\nimage " + i + " " + image1.getWidth()
                        + "x" + image1.getHeight());
                System.out.println("image " + j + " " + image2.getWidth() + "x"
                        + image2.getHeight());
                for (ResizeImageComparator.ResizeMode resizeMode :
                        ResizeImageComparator.ResizeMode.values()) {

                    System.out.println("\n " + resizeMode);
                    AWTImage.setComparator(new ResizeImageComparator(resizeMode,
                            new NaturalImageComparator(1.1)));
                    Image awtImage1 = new AWTImage(image1);
                    Image awtImage2 = new AWTImage(image2);
                    boolean expResult = expected[resizeMode.ordinal()][i][j];
                    Image diff = awtImage1.compareTo(awtImage2);
                    boolean result = diff == null;
                    if (diff != null) {
                        diff.save("diff" + i + j + resizeMode + ".png");
                    }
                    assertEquals("Failed comparison for image " + i + " with "
                            + "image " + j + ", resizeMode = " + resizeMode,
                            expResult, result);
                }
            }
        }
    }

    @Test
    public void testFXDiff() {
        System.out.println("fxDiff");
        AWTImage.setComparator(new NaturalImageComparator());
        ClasspathImageLoader cil = new ClasspathImageLoader();
        cil.setClassLoader(this.getClass().getClassLoader());
        cil.setRootPackage(this.getClass().getPackage());
        Image im1 = cil.load("AreaChart_a.png");
        Image im2 = cil.load("AreaChart_a_res.png");
        Image diff = im1.compareTo(im2);
        if (diff != null) {
            diff.save("testFXDiff_1to2.png");
            checkDiff(diff);
        }
        assertNotNull("Images has to be different", diff);

        diff = im2.compareTo(im1);
        if (diff != null) {
            diff.save("testFXDiff_2to1.png");
            checkDiff(diff);
        }
        assertNotNull("Images has to be different", diff);
    }

    /**
     * http://javafx-jira.kenai.com/browse/JMY-202
     * Jemmy produces completely black diffs in some cases
     */
    @Test
    public void testFXDiff2() {
        System.out.println("fxDiff2");
        AWTImage.setComparator(new NaturalImageComparator());
        ClasspathImageLoader cil = new ClasspathImageLoader();
        cil.setClassLoader(this.getClass().getClassLoader());
        cil.setRootPackage(this.getClass().getPackage());

        BufferedImage img1 = ((AWTImage) cil.load("AreaChart_a.png")).getTheImage();
        BufferedImage img3 = new BufferedImage(img1.getWidth(), img1.getHeight(), 3);
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                img3.setRGB(x, y, img1.getRGB(x, y));
            }
        }
        Image im1 = new AWTImage(img3);
        Image im2 = cil.load("AreaChart_a_res.png");
        Image diff = im1.compareTo(im2);
        if (diff != null) {
            diff.save("testFXDiff2_1to2.png");
            checkDiff(diff);
        }
        assertNotNull("Images has to be different", diff);

        diff = im2.compareTo(im1);
        if (diff != null) {
            diff.save("testFXDiff2_2to1.png");
            checkDiff(diff);
        }
        assertNotNull("Images has to be different", diff);
    }

    private void checkDiff(Image diff) {
        BufferedImage im = ((AWTImage) diff).getTheImage();
        int[] data = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
        for (int d : data) {
            if ((d & 0xffffff) != 0) {
                System.out.println("d = " + Integer.toBinaryString(d));
                return;
            }
            if (d != 0) {
                System.out.println("d = " + Integer.toBinaryString(d));
            }
        }
        fail("Diff is completely black");
    }
}
