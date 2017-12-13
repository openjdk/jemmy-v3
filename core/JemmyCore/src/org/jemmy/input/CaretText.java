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
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import org.jemmy.interfaces.Text;

/**
 *
 * @author shura
 */
public abstract class CaretText extends AbstractCaretOwner implements Text {

    static {
        Environment.getEnvironment().setPropertyIfNotSet(RegexCaretDirection.REGEX_FLAGS, 0);
    }

    TextCaret caret;
    TextImpl text;
    Wrap<?> wrap;

    /**
     *
     * @param wrap
     */
    public CaretText(Wrap<?> wrap) {
        this.wrap = wrap;
        text = new TextImpl(wrap) {
            public String text() {
                return CaretText.this.text();
            }
        };
    }

    public TextCaret caret() {
        if (caret == null) {
            initCaret();
        }
        return caret;
    }

    protected void initCaret() {
        caret = new TextCaret(wrap, this);
    }

    /**
     *
     * @return
     */
    protected int getFlags() {
        return (Integer)wrap.getEnvironment().
                getProperty(RegexCaretDirection.REGEX_FLAGS, 0);
    }

    public void type(String newText) {
        text.type(newText);
    }

    public void clear() {
        text.clear();
    }

    /**
     * Moves caret to a beginning/end of an <code>index</code>'th occurance of the regex.
     * @param regex
     * @param front
     * @param index
     */
    public void to(String regex, boolean front, int index) {
        caret().to(new RegexCaretDirection(this, this, regex, getFlags(), front, index));
    }

    /**
     * Moves caret to a beginning/end of the first occurance of the regex.
     * @param regex
     * @param front
     */
    public void to(String regex, boolean front) {
        to(regex, front, 0);
    }

    /**
     * Moves caret to a beginning the first occurance of the regex.
     * @param regex
     */
    public void to(String regex) {
        to(regex, true);
    }

    /**
     *
     * @param left
     * @param leftMods
     * @param right
     * @param rightMods
     */
    public void addNavKeys(KeyboardButton left, KeyboardModifier[] leftMods,
            KeyboardButton right, KeyboardModifier[] rightMods) {
        caret().addNavKeys(left, leftMods, right, rightMods);
    }

    /**
     *
     * @param left
     * @param right
     */
    public void addNavKeys(KeyboardButton left, KeyboardButton right) {
        addNavKeys(left, new KeyboardModifier[0], right, new KeyboardModifier[0]);
    }
}
