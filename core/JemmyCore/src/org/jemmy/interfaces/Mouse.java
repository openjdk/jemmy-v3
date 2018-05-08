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
import org.jemmy.dock.Shortcut;
import org.jemmy.env.Timeout;

/**
 *
 * @author shura
 */
public interface Mouse extends ControlInterface {
    /**
     *
     */
    public static final Timeout CLICK = new Timeout("mouse.click", 100);
    /**
     *
     */
    @Shortcut
    public void press();
    /**
     *
     * @param button
     */
    @Shortcut
    public void press(MouseButton button);
    /**
     *
     * @param button
     * @param modifiers
     */
    @Shortcut
    public void press(MouseButton button, Modifier... modifiers);
    /**
     *
     */
    @Shortcut
    public void release();
    /**
     *
     * @param button
     */
    @Shortcut
    public void release(MouseButton button);
    /**
     *
     * @param button
     * @param modifiers
     */
    @Shortcut
    public void release(MouseButton button, Modifier... modifiers);
    /**
     *
     */
    @Shortcut
    public void move();
    /**
     *
     * @param p
     */
    @Shortcut
    public void move(Point p);
    /**
     *
     */
    @Shortcut
    public void click();
    /**
     *
     * @param count
     */
    @Shortcut
    public void click(int count);
    /**
     *
     * @param count
     * @param p
     */
    @Shortcut
    public void click(int count, Point p);
    /**
     *
     * @param count
     * @param p
     * @param button
     */
    @Shortcut
    public void click(int count, Point p, MouseButton button);
    /**
     *
     * @param count
     * @param p
     * @param button
     * @param modifiers
     */
    @Shortcut
    public void click(int count, Point p, MouseButton button, Modifier... modifiers);

    /*
     * This method turns mouse wheel.
     * @parem amount Positive or negative
     */
    @Shortcut
    public void turnWheel(int amount);

    /*
     * This method turns mouse wheel.
     * @parem amount Positive or negative
     */
    @Shortcut
    public void turnWheel(Point point, int amount);

    /*
     * This method turns mouse wheel.
     * @parem amount Positive or negative
     */
    @Shortcut
    public void turnWheel(Point point, int amount, Modifier... modifiers);

    /**
     * Detaches the implementation so that all actions of it will be ran detached.
     * @see org.jemmy.action.ActionExecutor#executeDetached(org.jemmy.env.Environment, org.jemmy.action.Action, java.lang.Object[])
     * @return
     */
    public Mouse detached();

    /**
     * Mouse button interface (i. e. BUTTON1, BUTTON2, etc.)
     * created to left the possibility for extention as enums can't be extended
     */
    public static interface MouseButton extends Button {

    }

    /**
     * Mouse modifier interface (i. e. BUTTON1_DOWN_MASK)
     * created to left the possibility for extention as enums can't be extended
     */
    public static interface MouseModifier extends Modifier {

    }

    /**
     * Mouse modifiers enum (i. e. BUTTON1_DOWN_MASK)
     * to be used in Keyboard interface methods
     */
    public static enum MouseModifiers implements MouseModifier {

        /**
         *
         */
        BUTTON1_DOWN_MASK,
        /**
         *
         */
        BUTTON2_DOWN_MASK,
        /**
         *
         */
        BUTTON3_DOWN_MASK
    }

    /**
     * Mouse Buttons
     */
    public static enum MouseButtons implements MouseButton {
        /**
         *
         */
        BUTTON1,
        /**
         *
         */
        BUTTON2,
        /**
         *
         */
        BUTTON3
    }

}
