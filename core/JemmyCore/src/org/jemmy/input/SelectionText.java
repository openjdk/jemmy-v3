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
import org.jemmy.interfaces.Caret;
import org.jemmy.interfaces.IntervalSelectable;

/**
 *
 * @author shura
 */
public abstract class SelectionText extends CaretText implements IntervalSelectable {

    /**
     *
     * @param wrap
     */
    public SelectionText(Wrap<?> wrap) {
        super(wrap);
    }

    /**
     * Selects <code>index</code>'th occurance of the regex.
     * @param regex
     * @param index
     */
    public void select(String regex, int index) {
        to(regex, true, index);
        caret().selectTo(new RegexCaretDirection(this, this, regex, getFlags(), false, index));
    }

    /**
     * Selects first occurance of the regex.
     * @param regex
     */
    public void select(String regex) {
        select(regex, 0);
    }

    /**
     * Retuns the selection portion of the text.
     * @return
     */
    public String selection() {
        int a = (int) anchor(); int p = (int) position();
        int start = (a < p) ? a : p;
        int end = (a < p) ? p : a;
        return text().substring(start, end);
    }

    @Override
    protected void initCaret() {
        caret = new TextCaret(wrap, this) {
            @Override
            public void to(Caret.Direction direction) {
                int orig = direction.to();
                if (orig == 0) {
                    return;
                }
                if (anchor() - position() != 0) {
                    super.to(direction);         // clear selection
                }
                super.to(direction);
            }
        };
    }
}
