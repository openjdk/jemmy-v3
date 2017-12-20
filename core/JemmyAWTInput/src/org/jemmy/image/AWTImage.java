/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.image;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jemmy.Dimension;
import org.jemmy.JemmyException;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.image.pixel.*;


/**
 *
 * @author shura
 */
public class AWTImage implements Image, WriteableRaster {

    /**
     *
     */
    public static final String OUTPUT = AWTImage.class.getName() + ".OUTPUT";
    public static final String PNG_FILE = ".png";

    /**
     * Get the value of imageRoot. The field is used to store images by relative
     * path within the root. Images should be pointed out by full file names if
     * the value is null.
     *
     * @return the value of imageRoot
     */
    public static File getImageRoot() {
        ImageStore res = Environment.getEnvironment().getProperty(ImageStore.class);
        if(!(res instanceof PNGFileImageStore)) {
            throw new IllegalStateException("Unsupported ImageStore: " + res.getClass().getName());
        }
        return ((PNGFileImageStore)res).getRoot();
    }

    /**
     * Set the value of imageRoot. If null, an image ID should be full path.
     *
     * @param imageRoot new value of imageRoot
     */
    public static void setImageRoot(File imageRoot) {
        Environment.getEnvironment().setProperty(ImageStore.class, new PNGFileImageStore(imageRoot));
    }

    /**
     * Gets comparator to be used by default.
     *
     * @see Wrap#waitImage(org.jemmy.image.Image, java.lang.String,
     * java.lang.String)
     * @see ImageComparator
     * @return default comparator
     */
    public static ImageComparator getComparator() {
        return Environment.getEnvironment().getProperty(ImageComparator.class);
    }

    /**
     * Sets comparator to be used by default.
     *
     * @see Wrap#waitImage(org.jemmy.image.Image, java.lang.String,
     * java.lang.String)
     * @see ImageComparator
     * @param comparator
     */
    public static void setComparator(ImageComparator comparator) {
        Environment.getEnvironment().setProperty(ImageComparator.class, comparator);
    }

    static {
        Environment.getEnvironment().setPropertyIfNotSet(ImageComparator.class,
                new BufferedImageComparator(Environment.getEnvironment()));
        Environment.getEnvironment().setPropertyIfNotSet(ImageStore.class, new PNGFileImageStore());
    }

    private BufferedImage image;

    /**
     * Creates an instance
     *
     * @param img
     */
    public AWTImage(BufferedImage img) {
        this.image = img;
    }

    public BufferedImage getTheImage() {
        return image;
    }

    /**
     * Compares using current comparator.
     *
     * @see AWTImage#getComparator()
     * @param img
     * @return diff image.
     */
    public Image compareTo(Image img) {
        return getComparator().compare(this, img);
    }

    /**
     * Saves to a filesystem. fileName is expected to be a full path, unless
     * imageRoot is specified. ".png" extension is added automatically if not
     * specified.
     *
     * @see AWTImage#getImageRoot()
     * @param fileName full or relative file name
     */
    public void save(String fileName) {
        try {
            String fullPath = fileName;
            File imageRoot = getImageRoot();
            if (imageRoot != null) {
                fullPath = imageRoot.getAbsolutePath() + File.separator + fileName;
            }
            if (!fullPath.toLowerCase().endsWith(PNG_FILE)) {
                fullPath += PNG_FILE;
            }
            new PNGImageSaver().save(image, fullPath);
            Environment.getEnvironment().getOutput(OUTPUT).println("Image saved to " + fullPath);
        } catch (IOException ex) {
            throw new JemmyException("Unable to save image", ex, fileName);
        }
    }

    public Dimension getSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public void getColors(int x, int y, double[] colors) {
        int orig = image.getRGB(x, y);
        int ivalue;
        for (Raster.Component c : getSupported()) {
            switch (c) {
                case ALPHA:
                    ivalue = (orig & 0xFF000000) >>> 0x18;
                    break;
                case RED:
                    ivalue = (orig & 0xFF0000) >>> 0x10;
                    break;
                case GREEN:
                    ivalue = (orig & 0xFF00) >>> 0x8;
                    break;
                case BLUE:
                    ivalue = (orig & 0xFF);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown color component" + c);
            }
            colors[PixelImageComparator.arrayIndexOf(getSupported(), c)] = (double) ivalue / 0xFF;
        }
    }

    public void setColors(int x, int y, double[] colors) {
        int rgb = 0;
        double value;
        int ivalue;
        for (Raster.Component c : getSupported()) {
            value = colors[PixelImageComparator.arrayIndexOf(getSupported(), c)];
            if (value < 0 || value > 1) {
                throw new IllegalArgumentException("Color component value should be within (0, 1). Gotten: " + value);
            }
            ivalue = (int) Math.round(value * 0xFF);
            if (ivalue > 0xFF) {
                throw new IllegalStateException("Component value is greater than 0xFF: " + ivalue);
            }
            switch (c) {
                case ALPHA:
                    rgb |= (ivalue << 0x18);
                    break;
                case RED:
                    rgb |= (ivalue << 0x10);
                    break;
                case GREEN:
                    rgb |= (ivalue << 0x8);
                    break;
                case BLUE:
                    rgb |= ivalue;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown color component: " + c);
            }
        }
        getTheImage().setRGB(x, y, rgb);
    }

    public Component[] getSupported() {
        return new Component[]{Component.RED, Component.BLUE, Component.GREEN, Component.ALPHA};
    }
}
