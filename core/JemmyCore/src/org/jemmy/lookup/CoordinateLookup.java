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

/**
 * Lookup that matches all the controls with the bounds that lay inside
 * the given rectangle. The way to determine bounds of any control is
 * implemented by the child class in @{@linkplain #getBounds(java.lang.Object)}
 * method.
 *
 * @author shura
 */
public abstract class CoordinateLookup<CONTROL> implements LookupCriteria<CONTROL>{

    private Rectangle area;

    /**
     *
     * @param area Rectangle area to lookup up for controls that reside inside it
     */
    public CoordinateLookup(Rectangle area) {
        this.area = area;
    }

    /**
     *
     * @return Rectangle area to lookup up for controls that reside inside it
     */
    protected Rectangle getArea() {
        return area;
    }

    /**
     *
     * @param control todo document
     * @return logical bounds for the control that has to be inside the given
     * rectangle to match the lookup
     */
    protected abstract Rectangle getBounds(CONTROL control);

    public boolean check(CONTROL control) {
        Rectangle a = getArea();
        Rectangle bounds = getBounds(control);
        return bounds.x >= a.x && bounds.y >= a.y &&
                (bounds.x + bounds.width) <= (a.x + a.width) &&
                (bounds.y + bounds.height) <= (a.y + a.height);
    }

    @Override
    public String toString() {
        return "CoordinateLookup[area=" + area + "]";
    }
}
