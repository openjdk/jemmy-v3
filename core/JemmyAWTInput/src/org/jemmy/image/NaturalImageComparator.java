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


import java.awt.Color;
import java.awt.image.BufferedImage;
import org.jemmy.image.pixel.AverageDistanceComparator;
import org.jemmy.image.pixel.MaxDistanceComparator;


/**
 * Compares two images naturally
 * (i.e. ignoring slight difference between pixel colors).
 *
 * @author KAM
 */
public class NaturalImageComparator extends BufferedImageComparator {

    /**
     * Creates comparator with the default sensitivity value = 0.02
     * (around 5 in 0-255 color component value).
     * @see #NaturalImageComparator(double)
     */
    public NaturalImageComparator() {
        this(0.02);
    }

    /**
     * Creates comparator with the specified sensitivity value
     * @param sensitivity Maximum threshold for 3-D distance between colors
     * in 3-D sRGB color space for pixels to be considered equal.
     * Meaningful values are between 0 and approx 1.733. 0 means colors should
     * be equal to pass the comparison, 1.733 (which is more than square root
     * of 3) means that comparison will be passed even if the colors are
     * completely different. You could also use {@linkplain
     * #findSensitivity(java.awt.image.BufferedImage, java.awt.image.BufferedImage)
     * findSensitivity()} method to obtain necessary sensitivity value.
     */
    public NaturalImageComparator(double sensitivity) {
        super(new MaxDistanceComparator(sensitivity));
    }
    public void setSensitivity(double sensitivity) {
        ((MaxDistanceComparator)getRasterComparator()).setThreshold(sensitivity);
    }
    public double getSensitivity() {
        return ((MaxDistanceComparator)getRasterComparator()).getThreshold();
    }
}
