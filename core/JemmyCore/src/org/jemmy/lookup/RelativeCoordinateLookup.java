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

/**
 *
 * @param <CONTROL>
 * @author shura
 */
public abstract class RelativeCoordinateLookup<CONTROL> extends CoordinateLookup<CONTROL> {

    private static final int MAX_SCREEN_SIZE = 100000;

    private Wrap wrap;
    private boolean includeControl;
    private int hr;
    private int vr;

    /**
     *
     * @param wrap
     * @param includeControl
     * @param hr
     * @param vr
     */
    public RelativeCoordinateLookup(Wrap wrap, boolean includeControl, int hr, int vr) {
        super(wrap.getScreenBounds());
        this.wrap = wrap;
        this.includeControl = includeControl;
        this.hr = hr;
        this.vr = vr;
    }

    /**
     *
     * @return
     */
    @Override
    protected Rectangle getArea() {
        return constructArea(wrap, includeControl, hr, vr);
    }

    private static Rectangle constructArea(Wrap wrap, boolean includeControl, int hr, int vr) {
        Rectangle res = new Rectangle();
        res.width = MAX_SCREEN_SIZE;
        res.height = MAX_SCREEN_SIZE;
        Rectangle bounds = wrap.getScreenBounds();
        if (hr != 0) {
            if (hr < 0) {
                res.x = -MAX_SCREEN_SIZE + bounds.x + (includeControl ? bounds.width : 0);
            } else {
                res.x = bounds.x + (includeControl ? 0 : bounds.width);
            }
        } else {
            res.x = -MAX_SCREEN_SIZE / 2 + bounds.x + (includeControl ? bounds.width / 2 : 0);
        }
        if (vr != 0) {
            if (vr < 0) {
                res.y = -MAX_SCREEN_SIZE + bounds.y + (includeControl ? bounds.height : 0);
            } else {
                res.y = bounds.y + (includeControl ? 0 : bounds.height);
            }
        } else {
            res.y = -MAX_SCREEN_SIZE / 2 + bounds.y + (includeControl ? bounds.height / 2 : 0);
        }
        return res;
    }

}
