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

import org.jemmy.Dimension;
import org.jemmy.image.Image;
import org.jemmy.image.ImageComparator;

/**
 *
 * @author shura
 */
public abstract class ResizeComparator implements ImageComparator {

    private ImageComparator subComparator;
    private Mode mode;

    /**
     *
     */
    public enum Mode {

        /**
         *
         */
        LEFT,
        /**
         *
         */
        RIGTH,
        /**
         *
         */
        MAX
    };

    /**
     *
     * @param subComparator
     * @param mode
     */
    public ResizeComparator(ImageComparator subComparator, Mode mode) {
        this.subComparator = subComparator;
        this.mode = mode;
    }

    /**
     *
     * @param subComparator
     */
    public ResizeComparator(ImageComparator subComparator) {
        this(subComparator, Mode.MAX);
    }

    /**
     *
     * @return
     */
    public ImageComparator getSubComparator() {
        return subComparator;
    }

    /**
     *
     * @param subComparator
     */
    public void setSubComparator(ImageComparator subComparator) {
        this.subComparator = subComparator;
    }

    /**
     *
     * @param image1
     * @param image2
     * @return
     */
    public Image compare(Image image1, Image image2) {
        if(image1 == null || image2 == null) {
            return (image1 == null) ? image2 : image1;
        }
        Dimension size;
        switch (mode) {
            case LEFT:
                size = getSize(image1);
                break;
            case RIGTH:
                size = getSize(image2);
                break;
            case MAX:
                Dimension size1 = getSize(image1);
                Dimension size2 = getSize(image2);
                size = new Dimension(Math.max(size1.width, size2.width),
                        Math.max(size1.height, size2.height));
                break;
            default:
                throw new IllegalStateException("mode is not recognized");
        }
        Image r1 = resize(image1, size);
        Image r2 = resize(image2, size);
        if(r1 == null) {
            return image1;
        }
        if(r2 == null) {
            return image2;
        }
        return subComparator.compare(r1, r2);
    }

    /**
     *
     * @param image
     * @param size
     * @return
     */
    abstract public Image resize(Image image, Dimension size);

    /**
     *
     * @param image
     * @return
     */
    abstract public Dimension getSize(Image image);

    /**
     *
     * @return
     */
    public String getID() {
        return ResizeComparator.class.getName() + "(" + subComparator.getID() + ")";
    }
}
