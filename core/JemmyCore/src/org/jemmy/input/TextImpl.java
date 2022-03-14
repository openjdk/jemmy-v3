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
package org.jemmy.input;

import org.jemmy.action.Action;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Keyboard;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifiers;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Text;
import org.jemmy.timing.State;
import static org.jemmy.interfaces.Keyboard.KeyboardButtons.*;

/**
 *
 * @author shura
 */
public abstract class TextImpl implements Text {

    private static final int DEFAULT_SELECT_ALL_CLICK_COUNT = 3;
    private Wrap<?> target;
    private int selectAllClickCount = DEFAULT_SELECT_ALL_CLICK_COUNT;
    boolean keyboardSelection;

    protected TextImpl(Wrap<?> target, boolean keyboardSelection) {
        this.target = target;
        this.keyboardSelection = keyboardSelection;
    }

    protected TextImpl(Wrap<?> target) {
        this(target, false);
    }

    public Wrap<?> getWrap() {
        return target;
    }

    /**
     * Types text into the control. Wrap may implement Focusable.
     *
     * @see Focusable
     * @param newText the new text
     */
    public void type(final String newText) {
        target.getEnvironment().getExecutor().execute(target.getEnvironment(), false, new Action() {
            public void run(Object... parameters) {
                if (target.is(Focusable.class)) {
                    target.as(Focusable.class).focuser().focus();
                }
                char[] chars = newText.toCharArray();
                Keyboard kb = target.keyboard();
                for (char c : chars) {
                    kb.typeChar(c);
                }
                target.getEnvironment().getWaiter(Wrap.WAIT_STATE_TIMEOUT.getName()).ensureState(new State<Object>() {
                    public Object reached() {
                        return text().contains(newText) ? "" : null;
                    }

                    @Override
                    public String toString() {
                        return "text() equals '" + newText + "', text() = '" + text() + "'";
                    }
                });
            }

            @Override
            public String toString() {
                return "typing text \"" + newText + "\"";
            }
        });
    }

    /**
     * Selects all text within component by clicking 3 times on it or using
     * keyboard depending on the second argument passed to {@linkplain
     * #TextImpl(org.jemmy.control.Wrap, boolean) constructor}. Override if
     * needed.<p>
     *
     * <b>Warning!</b> In Java keyboard selection doesn't work with NumLock
     * turned On due to CR 4966137 'Robot presses Numpad del key instead of
     * normal Del key'.
     */
    protected void selectAll() {
        if (!keyboardSelection) {
            target.mouse().click(selectAllClickCount, target.getClickPoint());
        } else {
            Keyboard kbrd = target.keyboard();
            kbrd.pushKey(KeyboardButtons.HOME);
            kbrd.pushKey(KeyboardButtons.END, KeyboardModifiers.SHIFT_DOWN_MASK);
            kbrd.pushKey(KeyboardButtons.DELETE);
        }
    }

    /**
     * Clears text by pressing End and then Delete and Backspace until the text
     * is cleared.
     */
    public void clear() {
        target.getEnvironment().getExecutor().execute(target.getEnvironment(),
                false, new Action() {
            public void run(Object... parameters) {
                if (target.is(Focusable.class)) {
                    target.as(Focusable.class).focuser().focus();
                }
                if (text() == null) {
                    return;
                }
                String os = System.getProperty("os.name").toLowerCase();
                if(os.equals("mac os x"))
                    target.keyboard().pushKey(RIGHT, KeyboardModifiers.META_DOWN_MASK);
                else
                    target.keyboard().pushKey(END);
                while (!text().isEmpty() && withinAllowedTime()) {
                    System.out.println("text().isEmpty() " + text().isEmpty() + " length = " + text().length());
                    target.keyboard().pushKey(BACK_SPACE);
                }
            }

            @Override
            public String toString() {
                return "clearing text";
            }
        });
    }
}
