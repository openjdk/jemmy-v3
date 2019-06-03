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
package org.jemmy.input.awt;


import org.jemmy.Point;
import java.awt.event.InputEvent;

import org.jemmy.Rectangle;
import org.jemmy.env.Timeout;
import org.jemmy.env.Environment;
import org.jemmy.image.Image;
import static org.jemmy.input.awt.AWTRobotInputFactory.*;

import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifiers;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse.MouseButton;


/**
 * @author shura
 * @author mrkam
 *
 */
public class RobotDriver {

    private static boolean haveOldPos = false;
    private static int smoothness;
    private static double oldX;
    private static double oldY;

    static {
        Environment.getEnvironment().setTimeout(
                new Timeout(ROBOT_DELAY_TIMEOUT_NAME, 10));
        Environment.getEnvironment().setPropertyIfNotSet(
                AWTRobotInputFactory.ROBOT_MOUSE_SMOOTHNESS_PROPERTY,
                Integer.toString(Integer.MAX_VALUE));
        smoothness =  Integer.parseInt(
                (String)Environment.getEnvironment().getProperty(
                AWTRobotInputFactory.ROBOT_MOUSE_SMOOTHNESS_PROPERTY));
    }

    /**
     * Sets mouse smoothness
     * @param mouseSmoothness the maximum distance in pixels between
     * mouse positions during movement
     * @see #moveMouse(Point)
     */
    public static void setMouseSmoothness(int mouseSmoothness) {
        smoothness = mouseSmoothness;
    }

    /**
     * Gets mouse smoothness
     * @return the maximum distance in pixels between
     * mouse positions during movement
     * @see #setMouseSmoothness(int)
     * @see #moveMouse(Point)
     */
    public static int getMouseSmoothness() {
        return smoothness;
    }

    /**
     * Constructs a RobotDriver object.
     * @param autoDelay Time for <code>Robot.setAutoDelay(long)</code> method.
     */
    public RobotDriver(Timeout autoDelay) {
        RobotExecutor.get().setAutoDelay(autoDelay);
    }

    /**
     * Constructs a RobotDriver object.
     * @param env Environment with ROBOT_DELAY_TIMEOUT_NAME timeout
     * @see AWTRobotInputFactory#ROBOT_DELAY_TIMEOUT_NAME
     */
    public RobotDriver(Environment env) {
        this(env.getTimeout(ROBOT_DELAY_TIMEOUT_NAME));
    }

    /**
     * Capture an image of specified rectangular area of screen
     * @param screenRect area on screen that will be captured
     * @return image of specified rectangular area of screen
     */
    public static Image createScreenCapture(Rectangle screenRect) {
        return RobotExecutor.get().createScreenCapture(screenRect);
    }

    /**
     * Presses mouse button specified by mouseButton preceding pressing of
     * modifier keys or buttons specified by modifiers
     * @param mouseButton One of MouseEvent.BUTTON*_MASK
     * @param modifiers Combination of InputEvent.*_DOWN_MASK
     * @see java.awt.event.InputEvent
     * @see java.awt.event.MouseEvent
     */
    public void pressMouse(MouseButton mouseButton, Modifier... modifiers) {
        pressModifiers(modifiers);
        makeAnOperation("mousePress",
                new Object[]{mouseButton},
                new Class[]{MouseButton.class});
    }

    /**
     * Releases mouse button specified by mouseButton then releasing
     * modifier keys or buttons specified by modifiers
     * @param mouseButton One of MouseEvent.BUTTON*_MASK
     * @param modifiers Combination of InputEvent.*_DOWN_MASK
     * @see java.awt.event.InputEvent
     * @see java.awt.event.MouseEvent
     */
    public void releaseMouse(MouseButton mouseButton, Modifier... modifiers) {
        makeAnOperation("mouseRelease",
                new Object[]{mouseButton},
                new Class[]{MouseButton.class});
        releaseModifiers(modifiers);
    }

    /**
     * Moves mouse to the specified mouse. When previous mouse location is
     * remembered mouse moved smoothly between the points according to
     * mouse smoothness parameter. Otherwise it jumps to the specified point
     * @param point Position on the screen where to move mouse
     * @see #setMouseSmoothness(int)
     * @see #getMouseSmoothness()
     */
    public void moveMouse(Point point) {
        double targetX = point.x;
        double targetY = point.y;
        if (haveOldPos && (oldX != targetX || oldY != targetY)) {
            double currX = oldX;
            double currY = oldY;
            double hyp = Math.sqrt((targetX - currX) * (targetX - currX) +
                    (targetY - currY) * (targetY - currY));
            double steps = Math.ceil(hyp / Math.min(hyp, smoothness));
            double vx = (targetX - currX) / steps;
            double vy = (targetY - currY) / steps;
            assert (long)vx * vx + (long)vy * vy <= (long)smoothness * smoothness;
            while (Math.round(currX) != Math.round(targetX) ||
                    Math.round(currY) != Math.round(targetY)) {
                currX += vx;
                currY += vy;
                makeAnOperation("mouseMove", new Object[]{
                            new Integer((int) Math.round(currX)),
                            new Integer((int) Math.round(currY))},
                        new Class[]{Integer.TYPE, Integer.TYPE});
            }
        } else {
            makeAnOperation("mouseMove",
                    new Object[]{new Integer(point.x), new Integer(point.y)},
                    new Class[]{Integer.TYPE, Integer.TYPE});
        }
        haveOldPos = true;
        oldX = targetX;
        oldY = targetY;
    }

    /**
     * Clicks the mouse button specified by mouseButton at the specified point
     * specified number of times preceding it by pressing the modifiers key or
     * buttons and ending by releasing them. The last click is as long as
     * mouseClick timeout
     * @param point Screen location where to click mouse
     * @param clickCount Number of clicks
     * @param mouseButton One of MouseEvent.BUTTON*_MASK
     * @param modifiers Combination of InputEvent.*_DOWN_MASK
     * @param mouseClick Timeout of the last click
     * @see java.awt.event.InputEvent
     * @see java.awt.event.MouseEvent
     */
    public void clickMouse(Point point, int clickCount, MouseButton mouseButton, Timeout mouseClick, Modifier... modifiers) {
        pressModifiers(modifiers);
        moveMouse(point);
        makeAnOperation("mousePress", new Object[]{mouseButton}, new Class[]{MouseButton.class});
        for (int i = 1; i < clickCount; i++) {
            makeAnOperation("mouseRelease", new Object[]{mouseButton}, new Class[]{MouseButton.class});
            makeAnOperation("mousePress", new Object[]{mouseButton}, new Class[]{MouseButton.class});
        }
        mouseClick.sleep();
        makeAnOperation("mouseRelease", new Object[]{mouseButton}, new Class[]{MouseButton.class});
        releaseModifiers(modifiers);
    }

    /**
     * @deprecated Implementation doesn't seem to be correct as it ignores mouseButton and modifiers
     * @param point todo document
     * @param mouseButton One of MouseEvent.BUTTON*_MASK
     * @param modifiers todo document
     */
    public void dragMouse(Point point, int mouseButton, int modifiers) {
        moveMouse(point);
    }

    /**
     * Performs drag and drop from startPoint to endPoint using specified
     * mouseButton and modifiers to perform it.
     * @param startPoint Screen coordinates of drag start point
     * @param endPoint Screen coordinates of drag end point
     * @param mouseButton One of MouseEvent.BUTTON*_MASK
     * @param modifiers Combination of InputEvent.*_DOWN_MASK
     * @param before Timeout between pressing mouse at the startPoint and
     * mouse move
     * @param after Timeout between mouse move to the endPoint and mouse
     * release
     */
    public void dragNDrop(Point startPoint, Point endPoint, MouseButton mouseButton, Modifier modifiers[], Timeout before, Timeout after) {
        moveMouse(startPoint);
        pressMouse(mouseButton, modifiers);
        before.sleep();
        moveMouse(endPoint);
        after.sleep();
        releaseMouse(mouseButton, modifiers);
    }

    /**
     * Presses a key.
     * @param kbdButton Key code (<code>KeyEventVK_*</code> field.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void pressKey(KeyboardButton kbdButton, Modifier... modifiers) {
        pressModifiers(modifiers);
        makeAnOperation("keyPress",
                new Object[]{kbdButton},
                new Class[]{KeyboardButton.class});
    }

    /**
     * Releases a key.
     * @param kbdButton Key code (<code>KeyEventVK_*</code> field.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void releaseKey(KeyboardButton kbdButton, Modifier... modifiers) {
        makeAnOperation("keyRelease",
                new Object[]{kbdButton},
                new Class[]{KeyboardButton.class});
        releaseModifiers(modifiers);
    }

    /**
     * Turns the wheel.
     * @param p todo document
     * @param amount Either positive or negative
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    public void turnWheel(Point p, int amount, Modifier... modifiers) {
        pressModifiers(modifiers);
        moveMouse(p);
        java.awt.Robot r = null;
        makeAnOperation("mouseWheel",
                new Object[]{amount},
                new Class[]{Integer.TYPE});
        releaseModifiers(modifiers);
    }

    /**
     * Performs a single operation.
     * @param method a name of <code>java.awt.Robot</code> method.
     * @param params method parameters
     * @param paramClasses method parameters classes
     */
    public void makeAnOperation(final String method, final Object[] params, final Class[] paramClasses) {
        RobotExecutor.get().makeAnOperation(method, params, paramClasses);
    }

    final static int SHIFT_MASK = InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK;
    final static int ALT_GRAPH_MASK = InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.ALT_GRAPH_MASK;
    final static int ALT_MASK = InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK;
    final static int META_MASK = InputEvent.META_DOWN_MASK | InputEvent.META_MASK;
    final static int CTRL_MASK = InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK;

    /**
     * Presses modifiers keys by robot.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    protected void pressModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) { // TODO: ALT_GRAPH_MASK?
            if (modifier == KeyboardModifiers.ALT_DOWN_MASK) {
                pressKey(KeyboardButtons.ALT);
            } else if (modifier == KeyboardModifiers.CTRL_DOWN_MASK) {
                pressKey(KeyboardButtons.CONTROL);
            } else if (modifier == KeyboardModifiers.META_DOWN_MASK) {
                pressKey(KeyboardButtons.META);
            } else if (modifier == KeyboardModifiers.SHIFT_DOWN_MASK) {
                pressKey(KeyboardButtons.SHIFT);
            }
        }
    }

    /**
     * Releases modifiers keys by robot.
     * @param modifiers a combination of <code>InputEvent.*_MASK</code> fields.
     */
    protected void releaseModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) { // TODO: ALT_GRAPH_MASK?
            if (modifier == KeyboardModifiers.ALT_DOWN_MASK) {
                releaseKey(KeyboardButtons.ALT);
            } else if (modifier == KeyboardModifiers.CTRL_DOWN_MASK) {
                releaseKey(KeyboardButtons.CONTROL);
            } else if (modifier == KeyboardModifiers.META_DOWN_MASK) {
                releaseKey(KeyboardButtons.META);
            } else if (modifier == KeyboardModifiers.SHIFT_DOWN_MASK) {
                releaseKey(KeyboardButtons.SHIFT);
            }
        }
    }
    /**
     * If java.awt.Robot is running in other JVM, it shutdowns that JVM
     * @see AWTRobotInputFactory#runInOtherJVM(boolean)
     */
    public static void exit() {
        RobotExecutor.get().exit();
    }
}
