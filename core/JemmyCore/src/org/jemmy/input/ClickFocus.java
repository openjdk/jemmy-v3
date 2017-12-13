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
package org.jemmy.input;

import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Focus;

/**
 * Simple Focus implementation which clicks on a control to give focus.
 * @author shura
 */
public class ClickFocus<CONTROL> implements Focus {

    Wrap<? extends CONTROL> topControl;
    Point clickPoint;

    /**
     *
     * @param topControl a control to click on. Node that this could be
     * a control itself (the one we're giving the focus to) or a subcontrol
     * of it.
     */
    public ClickFocus(Wrap<? extends CONTROL> topControl, Point clickPoint) {
        this.topControl = topControl;
        this.clickPoint = clickPoint;
    }

    public ClickFocus(Wrap<? extends CONTROL> topControl) {
        this(topControl, null);
    }

    protected Wrap<? extends CONTROL> getTopControl() {
        return topControl;
    }

    /**
     * @{@inheritDoc}
     */
    public void focus() {
        if (clickPoint == null) {
            topControl.mouse().click();
        } else {
            topControl.mouse().click(1, clickPoint);
        }
    }
}
