/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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

import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Selector;

/**
 *
 * @author shura
 */
public class KeyboardSelector<T> implements Selector<T> {

    private final Wrap<?> wrap;
    private final KeyboardSelectable<T> control;

    public KeyboardSelector(Wrap<?> wrap, KeyboardSelectable<T> control) {
        this.wrap = wrap;
        this.control = control;
    }

    public void select(T state) {
        wrap.as(Focusable.class).focuser().focus();
        int to = control.index(state);
        int from = control.selection();
        KeyboardButton btt;
        if (control.isVertical()) {
            btt = (to > from)
                    ? KeyboardButtons.DOWN : KeyboardButtons.UP;
        } else {
            btt = (to > from)
                    ? KeyboardButtons.RIGHT : KeyboardButtons.LEFT;
        }
        for (int i = 0; i < Math.abs(to - from); i++) {
            wrap.keyboard().pushKey(btt);
        }
    }
}
