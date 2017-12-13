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

import org.jemmy.Dimension;
import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Window;

/**
 *
 * @author shura
 */
public class WindowImpl implements Window {

    private Wrap control;
    private int offset;

    /**
     *
     * @param control
     * @param offset
     */
    public WindowImpl(Wrap control, int offset) {
        this.control = control;
        this.offset = offset;
    }

    /**
     *
     * @param dest
     */
    public void move(Point dest) {
        Point start = control.getClickPoint();
        Point target = new Point(start.x + dest.x, start.y + dest.y);
        control.drag().dnd(start, control, target);
    }

    /**
     *
     * @param size
     * @param direction
     */
    public void resize(Dimension size, Direction direction) {
        Point start = null, target = null;
        Dimension sizeDiff = new Dimension(size.width - control.getScreenBounds().width, size.height - control.getScreenBounds().height);
        switch(direction) {
            case NORTHWEST:
                start = new Point(offset, offset);
                target = new Point(start.x - sizeDiff.width, start.y - sizeDiff.height);
                break;
            case NORTHEAST:
                start = new Point(control.getScreenBounds().width - offset - 1, offset);
                target = new Point(start.x + sizeDiff.width, start.y - sizeDiff.height);
                break;
            case SOUTHEAST:
                start = new Point(control.getScreenBounds().width - offset - 1, control.getScreenBounds().height - offset - 1);
                target = new Point(start.x + sizeDiff.width, start.y + sizeDiff.height);
                break;
            case SOUTHWEST:
                start = new Point(offset, control.getScreenBounds().height - offset - 1);
                target = new Point(start.x - sizeDiff.width, start.y + sizeDiff.height);
                break;
        }
        control.drag().dnd(start, control, target);
    }

}
