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

import org.jemmy.Rectangle;
import org.jemmy.Point;
import java.util.Arrays;
import org.jemmy.action.Action;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse;
import static org.jemmy.interfaces.Mouse.CLICK;
import org.jemmy.interfaces.Showable;

/**
 *
 * @author shura
 */
public class MouseImpl implements Mouse {

    private Wrap<?> target;
    private RobotDriver robotDriver;
    private boolean detached = false;

    static {
        if (Environment.getEnvironment().getTimeout(CLICK) == null) {
            Environment.getEnvironment().setTimeout(MouseImpl.CLICK);
        }
    }

    /**
     *
     * @param target
     */
    public MouseImpl(Wrap<?> target) {
        this.target = target;
        this.robotDriver = new RobotDriver(new Timeout("", 10));
    }

    public Mouse detached() {
        this.detached = true;
        return this;
    }

    private void runAction(Action action) {
        if (detached) {
            target.getEnvironment().getExecutor().executeDetached(target.getEnvironment(), false, action);
        } else {
            target.getEnvironment().getExecutor().execute(target.getEnvironment(), false, action);
        }
    }

    /**
     *
     */
    @Override
    public void press() {
        press(MouseButtons.BUTTON1);
    }

    /**
     *
     * @param button
     */
    @Override
    public void press(MouseButton button) {
        press(button, new Modifier[]{});
    }

     /**
     *
     * @param button
     * @param modifiers
     */
    @Override
    public void press(final MouseButton button, final Modifier... modifiers) {
        runAction(new Action() {

            public void run(Object... parameters) {
                robotDriver.pressMouse(button, modifiers);
            }

            @Override
            public String toString() {
                return "pressing mouse button " + button + " with " + modifiers + " modifiers";
            }
        });
    }

    /**
     *
     */
    public void release() {
        release(MouseButtons.BUTTON1);
    }

    /**
     *
     * @param button
     */
    @Override
    public void release(MouseButton button) {
        release(button, new Modifier[]{});
    }

    /**
     *
     * @param button
     * @param modifiers
     */
    @Override
    public void release(final MouseButton button, final Modifier... modifiers) {
        runAction(new Action() {

            public void run(Object... parameters) {
                robotDriver.releaseMouse(button, modifiers);
            }

            @Override
            public String toString() {
                return "releasing mouse button " + button + " with " + modifiers + " modifiers";
            }
        });
    }

    /**
     *
     */
    public void move() {
        move(target.getClickPoint());
    }

    /**
     *
     * @param p
     */
    public void move(final Point p) {
        runAction(new Action() {

            public void run(Object... parameters) {
                robotDriver.moveMouse(getAbsolute(target, p));
            }

            @Override
            public String toString() {
                return "moving mouse to " + p;
            }
        });
    }

    /**
     *
     */
    public void click() {
        this.click(1);
    }

    /**
     *
     * @param count
     */
    public void click(int count) {
        this.click(count, null);
    }

    /**
     *
     * @param count
     * @param p Point to click, if null {@linkplain Wrap#getClickPoint()
     * Wrap.getClickPoint()} method is invoked to get the point to click.
     */
    public void click(int count, Point p) {
        this.click(count, p, MouseButtons.BUTTON1);
    }

    /**
     *
     * @param count
     * @param p Point to click, if null {@linkplain Wrap#getClickPoint()
     * Wrap.getClickPoint()} method is invoked to get the point to click.
     * @param button
     */
    @Override
    public void click(int count, Point p, MouseButton button) {
        click(count, p, button, new Modifier[] {});
    }

    /**
     *
     * @param count
     * @param p Point to click, if null {@linkplain Wrap#getClickPoint()
     * Wrap.getClickPoint()} method is invoked to get the point to click.
     * @param button
     * @param modifiers
     */
    @Override
    public void click(final int count, final Point p, final MouseButton button, final Modifier... modifiers) {
        runAction(new Action() {

            public void run(Object... parameters) {
                if (target.is(Showable.class)) {
                    target.as(Showable.class).shower().show();
                }
                robotDriver.clickMouse(getAbsolute(target,
                        p == null ? target.getClickPoint() : p),
                        count, button, target.getEnvironment().getTimeout(CLICK), modifiers);
            }

            @Override
            public String toString() {
                return "clicking " + button + " mouse button " + count + " times at " + p + " with " + Arrays.toString(modifiers) + " modifiers";
            }
        });
    }

    static Point getAbsolute(Wrap<?> target, Point p) {
        Rectangle screenBounds = target.getScreenBounds();
        return new Point(p.x + screenBounds.x, p.y + screenBounds.y);
    }

    private void turn(final Point p, final int amount, final Modifier... modifiers) {
        runAction(new Action() {

            public void run(Object... parameters) {
                if (target.is(Showable.class)) {
                    target.as(Showable.class).shower().show();
                }
                robotDriver.turnWheel(getAbsolute(target,
                        p == null ? target.getClickPoint() : p),
                        amount, modifiers);
            }

            @Override
            public String toString() {
                return "turning wheel to " + amount + " with " + Arrays.toString(modifiers) + " modifiers";
            }
        });
    }

    public void turnWheel(Point point, final int amount, Modifier... modifiers) {
        turn(point, amount, modifiers);
    }

    public void turnWheel(Point point, final int amount) {
        turn(point, amount, new Modifier[]{});
    }

    public void turnWheel(final int amount) {
        turn(null, amount, new Modifier[]{});
    }
}
