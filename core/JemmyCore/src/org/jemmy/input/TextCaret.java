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

import org.jemmy.control.Wrap;
import org.jemmy.interfaces.CaretOwner;
import org.jemmy.interfaces.IntervalSelector;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;

/**
 *
 * @author shura
 */
public class TextCaret extends CaretImpl implements IntervalSelector {

    /**
     *
     * @param wrap
     * @param scroll
     */
    public TextCaret(Wrap<?> wrap, CaretOwner scroll) {
        super(wrap, scroll);
        addScrollAction(new KeyboardScrollAction(KeyboardButtons.LEFT, KeyboardButtons.RIGHT));
    }

    /**
     *
     * @param down
     * @param downMods
     * @param up
     * @param upMods
     */
    public void addNavKeys(KeyboardButton down, KeyboardModifier[] downMods,
            KeyboardButton up, KeyboardModifier[] upMods) {
        addScrollAction(new CaretImpl.KeyboardScrollAction(down, downMods, up, upMods));
    }

    /**
     *
     * @param down
     * @param up
     */
    public void addNavKeys(KeyboardButton down, KeyboardButton up) {
        addNavKeys(down, new KeyboardModifier[0], up, new KeyboardModifier[0]);
    }

    public void selectTo(double value) {
        getWrap().keyboard().pressKey(KeyboardButtons.SHIFT);
        to(value);
        getWrap().keyboard().releaseKey(KeyboardButtons.SHIFT);
    }

    public void selectTo(Direction condition) {
        getWrap().keyboard().pressKey(KeyboardButtons.SHIFT);
        to(condition);
        getWrap().keyboard().releaseKey(KeyboardButtons.SHIFT);
    }
}
