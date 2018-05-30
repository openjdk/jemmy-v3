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


import java.awt.image.BufferedImage;
import org.jemmy.image.pixel.AverageDistanceComparator;


/**
 * Compares two images calculating average color distance between pixels and
 * comparing it to the threshold value. See {@linkplain NaturalImageComparator
 * NaturalImageComparator} for color comparison details.
 *
 * @author KAM
 */
public class AverageDistanceImageComparator extends BufferedImageComparator {

    /**
     * Creates comparator with the default sensitivity value = 0.02
     * (around 5 in 0-255 color component value).
     * @see #AverageDistanceImageComparator(double)
     */
    public AverageDistanceImageComparator() {
        this(0.02);
    }

    /**
     * Creates comparator with the specified sensitivity value
     * @param sensitivity Maximum threshold for average 3-D distance between
     * colors in 3-D sRGB color space for images to be considered equal.
     * Meaningful values lay between 0 and approx 1.733. 0 means colors should
     * be equal to pass the comparison, 1.733 (which is more than square root
     * of 3) means that comparison will be passed even if all the colors are
     * completely different.
     */
    public AverageDistanceImageComparator(double sensitivity) {
        super(new AverageDistanceComparator(sensitivity));
    }

    public void setSensitivity(double sensitivity) {
        ((AverageDistanceComparator)getRasterComparator()).setThreshold(sensitivity);
    }
    public double getSensitivity() {
        return ((AverageDistanceComparator)getRasterComparator()).getThreshold();
    }
}
