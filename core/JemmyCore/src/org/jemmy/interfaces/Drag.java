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

package org.jemmy.interfaces;


import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.dock.Shortcut;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Mouse.MouseButton;


/**
 *
 * @author shura
 */
public interface Drag extends ControlInterface {
    public static final Timeout BEFORE_DRAG_TIMEOUT = new Timeout("Control.before.drag", 500);
    public static final Timeout BEFORE_DROP_TIMEOUT = new Timeout("Control.after.drag", 500);
    public static final Timeout IN_DRAG_TIMEOUT = new Timeout("Control.in.drag", 10);

    /**
     * @param targetPoint target point specified in component local coordinates
     */
    @Shortcut
    public void dnd(Point targetPoint);
    /**
     * @param target todo document
     * @param targetPoint target point specified in target component local coordinates
     */
    @Shortcut
    public void dnd(Wrap target, Point targetPoint);
    /**
     *
     * @param point source point specified in component local coordinates
     * @param target todo document
     * @param targetPoint target point specified in target component local coordinates
     */
    @Shortcut
    public void dnd(Point point, Wrap target, Point targetPoint);
    /**
     *
     * @param point source point specified in component local coordinates
     * @param target todo document
     * @param targetPoint target point specified in target component local coordinates
     * @param button todo document
     */
    @Shortcut
    public void dnd(Point point, Wrap target, Point targetPoint, MouseButton button);
    /**
     *
     * @param point source point specified in component local coordinates
     * @param target todo document
     * @param targetPoint target point specified in target component local coordinates
     * @param button todo document
     * @param modifiers todo document
     */
    @Shortcut
    public void dnd(Point point, Wrap target, Point targetPoint, MouseButton button, Modifier... modifiers);
}
