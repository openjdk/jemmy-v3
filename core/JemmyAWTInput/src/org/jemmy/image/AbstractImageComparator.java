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


/**
 * Common part of most ImageComparators.
 *
 * @author KAM
 * @deprecated Use classes from org.jemmy.image.pixel package instead.
 */
@Deprecated
public abstract class AbstractImageComparator implements ImageComparator {

    /**
     * Checks whether images have difference.
     * @param image1 First image to compare.
     * @param image2 Second image to compare.
     * @return true if images have no difference, false otherwise.
     */
    public abstract boolean noDifference(BufferedImage image1, BufferedImage image2);

    /**
     * {@inheritDoc}
     */
    public BufferedImage compare(BufferedImage image1, BufferedImage image2) {
        if (noDifference(image1, image2)) {
            return null;
        } else {
            return ImageTool.subtractImage(image1, image2);
        }
    }
}
