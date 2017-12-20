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
package org.jemmy.input;


import java.awt.event.InputEvent;
import org.jemmy.control.*;
import org.jemmy.Point;
import org.jemmy.action.Action;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Mouse.MouseButton;
import org.jemmy.interfaces.Drag;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse;
import org.jemmy.interfaces.Mouse.MouseButtons;
import org.jemmy.interfaces.Showable;


/**
 *
 * @author shura
 */
public class DragImpl implements Drag {

    /**
     *
     */
    public static final int DND_POINTS = 10;

    static {
        Environment.getEnvironment().setTimeout(BEFORE_DRAG_TIMEOUT);
        Environment.getEnvironment().setTimeout(BEFORE_DROP_TIMEOUT);
        Environment.getEnvironment().setTimeout(IN_DRAG_TIMEOUT);
    }

    private Wrap<?> source;

    /**
     *
     * @param source
     */
    public DragImpl(Wrap<?> source) {
        this.source = source;
    }

    /**
     *
     * @param targetPoint
     */
    public void dnd(Point targetPoint) {
        dnd(source, targetPoint);
    }

    /**
     *
     * @param target
     * @param targetPoint
     */
    public void dnd(Wrap target, Point targetPoint) {
        dnd(source.getClickPoint(), target, targetPoint);
    }

    /**
     *
     * @param point
     * @param target
     * @param targetPoint
     */
    public void dnd(Point point, Wrap target, Point targetPoint) {
        dnd(point, target, targetPoint, MouseButtons.BUTTON1);
    }

    /**
     *
     * @param point
     * @param target
     * @param targetPoint
     * @param button
     */
    public void dnd(Point point, Wrap target, Point targetPoint, MouseButton button) {
        dnd(point, target, targetPoint, button, new Modifier[]{});
    }

    /**
     *
     * @param point
     * @param target
     * @param targetPoint
     * @param button
     * @param modifiers
     */
    public void dnd(Point pointParam, final Wrap target, final Point targetPoint, final MouseButton button, final Modifier... modifiers) {
        final Point point = pointParam == null ? source.getClickPoint() : pointParam;
        source.getEnvironment().getExecutor().execute(target.getEnvironment(), false, new Action() {
            public void run(Object... parameters) {
                if(source.is(Showable.class)) ((Showable)source.as(Showable.class)).shower().show();
                source.mouse().move(point);
                source.mouse().press(button, modifiers);
                source.getEnvironment().getTimeout(BEFORE_DRAG_TIMEOUT.getName()).sleep();
                Point intermediatePoint = new Point();
                int xDistance = target.getScreenBounds().x + targetPoint.x - source.getScreenBounds().x - point.x;
                int yDistance = target.getScreenBounds().y + targetPoint.y - source.getScreenBounds().y - point.y;
                int startX = point.x + source.getScreenBounds().x;
                int startY = point.y + source.getScreenBounds().y;
                int endX = startX + xDistance;
                int endY = startY + yDistance;
                for(int i = 0; i < DND_POINTS + 1; i++) {
                    intermediatePoint.x = startX + xDistance * i / DND_POINTS - source.getScreenBounds().x;
                    intermediatePoint.y = startY + yDistance * i / DND_POINTS - source.getScreenBounds().y;
                    source.mouse().move(intermediatePoint);
                    source.getEnvironment().getTimeout(IN_DRAG_TIMEOUT.getName()).sleep();
                }
                source.mouse().move(new Point(endX - source.getScreenBounds().x, endY - source.getScreenBounds().y));
                //target.mouse().move(targetPoint);
                source.getEnvironment().getTimeout(BEFORE_DROP_TIMEOUT.getName()).sleep();
                target.mouse().release(button, modifiers);
            }

            @Override
            public String toString() {
                return "grag'n'drop from " + point + " to " + targetPoint + " of " + target.getClass() + " with mouse button " + button + " with " + modifiers + " modifiers";
            }

        }, button, modifiers);
    }
}
