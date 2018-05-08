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
package org.jemmy.input;

import org.jemmy.action.Action;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import org.jemmy.interfaces.Keyboard;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Modifier;

/**
 * KeyDriver
 *
 * @author Alexandre Iline(alexandre.iline@sun.com)
 */
public class KeyboardImpl implements Keyboard {

    CharBindingMap<KeyboardButton, KeyboardModifier> map;
    Environment env;
    Wrap<?> target;
    RobotDriver robotDriver;
    private boolean detached;
    /**
     * Constructs a KeyRobotDriver object.
     * @param target
     */
    public KeyboardImpl(Wrap<?> target) {
        //TODO: super(target.getEnvironment().getTimeout(RobotDriver.ROBOT_DELAY_TIMEOUT_NAME));
        robotDriver = new RobotDriver(target.getEnvironment());
        this.env = target.getEnvironment();
        this.map = target.getEnvironment().getBindingMap();
        this.target = target;
    }

    static {
        //TODO: Environment.getEnvironment().setTimeout(new Timeout(RobotDriver.ROBOT_DELAY_TIMEOUT_NAME, 10));
        Environment.getEnvironment().setTimeout(new Timeout(PUSH.getName(), 100));
        Environment.getEnvironment().setBindingMap(new DefaultCharBindingMap());
    }

    private void runAction(Action action) {
        if(detached) {
            target.getEnvironment().getExecutor().executeDetached(target.getEnvironment(), false, action);
        } else {
            target.getEnvironment().getExecutor().execute(target.getEnvironment(), false, action);
        }
    }

    /**
     *
     * @return Environment
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
     *
     * @param kbdButton
     * @param modifiers
     * @param pushTime
     */
    public void pushKey(final KeyboardButton kbdButton, final Modifier modifiers[], final Timeout pushTime) {
        runAction(new Action() {
            public void run(Object... parameters) {
                if(target.is(Focusable.class)) target.as(Focusable.class).focuser().focus();
                pressKey(kbdButton, modifiers);
                pushTime.sleep();
                releaseKey(kbdButton, modifiers);
            }
            @Override
            public String toString() {
                return "push " + kbdButton + " key with " + modifiers + " modifiers";
            }
        });
    }

    /**
     *
     * @param keyChar
     * @param pushTime
     */
    @Override
    public void typeChar(char keyChar, Timeout pushTime) {
        pushKey(pushTime, map.getCharKey(keyChar), map.getCharModifiers(keyChar));
    }

    /**
     * Press the keyboard key specified by kbdButton preceding with
     * pressing of modifier buttons specified by modifiers
     * @param kbdButton one of InputEvent.VK_* constants
     * @param modifiers combination of InputEvent.*_DOWN_MASK constants
     * @see java.awt.event.InputEvent
     */
    @Override
    public void pressKey(final KeyboardButton kbdButton, final Modifier... modifiers) {
        runAction(new Action() {
            public void run(Object... parameters) {
                robotDriver.pressKey(kbdButton, modifiers);
            }
            @Override
            public String toString() {
                return "press " + kbdButton + " key with " + modifiers + " modifiers";
            }
        });
    }

    /**
     * Release the keyboard key specified by kbdButton and then release
     * all the modifier keys specified by modifiers
     * @param kbdButton one of InputEvent.VK_* constants
     * @param modifiers combination of InputEvent.*_DOWN_MASK constants
     * @see java.awt.event.InputEvent
     */
    @Override
    public void releaseKey(final KeyboardButton kbdButton, final Modifier... modifiers) {
        runAction(new Action() {
            public void run(Object... parameters) {
                robotDriver.releaseKey(kbdButton, modifiers);
            }
            @Override
            public String toString() {
                return "press " + kbdButton + " key with " + modifiers + " modifiers";
            }
        });
    }

    /**
     *
     * @param kbdButton
     */
    @Override
    public void pressKey(KeyboardButton kbdButton) {
        pressKey(kbdButton, new Modifier[]{});
    }

    /**
     *
     * @param kbdButton
     */
    @Override
    public void releaseKey(KeyboardButton kbdButton) {
        releaseKey(kbdButton, new Modifier[]{});
    }

    /**
     *
     * @param kbdButton
     * @param modifiers
     */
    @Override
    public void pushKey(KeyboardButton kbdButton, Modifier... modifiers) {
        pushKey(kbdButton, modifiers, getEnvironment().getTimeout(PUSH.getName()));
    }

    /**
     *
     * @param kbdButton
     */
    @Override
    public void pushKey(KeyboardButton kbdButton) {
        pushKey(kbdButton, new Modifier[]{});
    }

    /**
     *
     * @param keyChar
     */
    @Override
    public void typeChar(char keyChar) {
        typeChar(keyChar, getEnvironment().getTimeout(PUSH.getName()));
    }

    @Override
    public Keyboard detached() {
        detached = true;
        return this;
    }

    @Override
    public void pushKey(Timeout pushTime, KeyboardButton key, Modifier... modifiers) {
        pushKey(key, modifiers, pushTime);
    }
}
