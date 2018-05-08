/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.lookup;

import org.jemmy.Rectangle;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.image.Image;

/**
 *
 * @author shura
 */
public class ImageLookup<T> implements LookupCriteria<T> {
    private Wrapper wrapper;
    private Image image;
    private Class<T> type;
    private Rectangle subArea;
    private Image lastDiff = null;
    private Image lastImage = null;

    public ImageLookup(Wrapper wrapper, Class<T> type, Image image, Rectangle subArea) {
        this.wrapper = wrapper;
        this.type = type;
        this.image = image;
        this.subArea = subArea;
    }

    public ImageLookup(Wrapper wrapper, Class<T> type, Image image) {
        this(wrapper, type, image, null);
    }

    public boolean check(T control) {
        Wrap<?> wrap = wrapper.wrap(type, control);
        if(subArea == null) {
            lastImage = wrap.getScreenImage();
        } else {
            lastImage = wrap.getScreenImage(subArea);
        }
        return (lastDiff = image.compareTo(lastImage)) == null;
    }

    public Image getLastDiff() {
        return lastDiff;
    }

    public Image getLastImage() {
        return lastImage;
    }
}
