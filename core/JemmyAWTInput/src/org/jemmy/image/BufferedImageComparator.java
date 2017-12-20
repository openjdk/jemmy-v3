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
import org.jemmy.env.Environment;
import org.jemmy.image.pixel.PixelImageComparator;
import org.jemmy.image.pixel.Raster;
import org.jemmy.image.pixel.RasterComparator;
import org.jemmy.image.pixel.WriteableRaster;

/**
 *
 * @author shura
 */
public class BufferedImageComparator extends PixelImageComparator {

    public BufferedImageComparator(RasterComparator comparator) {
        super(comparator);
    }

    public BufferedImageComparator(Environment env) {
        super(env);
    }

    @Override
    protected Image toImage(Raster image) {
        if(image instanceof AWTImage) {
            return (AWTImage)image;
        } else {
            throw new IllegalArgumentException("Unrecognized image type" + image.getClass().getName());
        }
    }

    @Override
    protected Raster toRaster(Image image) {
        if(image instanceof AWTImage) {
            return (AWTImage)image;
        } else {
            throw new IllegalArgumentException("Unrecognized image type" + image.getClass().getName());
        }
    }

    @Override
    protected WriteableRaster createDiffRaster(Raster r1, Raster r2) {
        AWTImage img2 = (AWTImage) r2;
        AWTImage img1 = (AWTImage) r1;
        return new AWTImage(new BufferedImage(
                Math.max(img1.getSize().width, img2.getSize().width),
                Math.max(img1.getSize().height, img2.getSize().height),
                img1.getTheImage().getType()));
    }
}
