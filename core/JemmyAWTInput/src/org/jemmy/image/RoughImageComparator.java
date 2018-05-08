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

import org.jemmy.image.pixel.PixelEqualityRasterComparator;

/**
 * Compares two images roughly (i.e. not all of the pixel colors should match).
 *
 * @author Alexandre Iline (alexandre.iline@sun.com), KAM <mrkam@mail.ru>
 */
public class RoughImageComparator extends BufferedImageComparator {

    /**
     * Creates a comparator with
     * <code>roughness</code> allowed roughness.
     *
     * @param roughness Allowed comparision roughness.
     */
    public RoughImageComparator(double roughness) {
        super(new PixelEqualityRasterComparator(roughness));
    }
    public void setRoughness(double roughness) {
        ((PixelEqualityRasterComparator)getRasterComparator()).setThreshold(roughness);
    }
    public double getRoughness() {
        return ((PixelEqualityRasterComparator)getRasterComparator()).getThreshold();
    }
}
