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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jemmy.JemmyException;
import org.jemmy.interfaces.CaretOwner;
import org.jemmy.interfaces.Text;

/**
 *
 * @author shura
 */
public class RegexCaretDirection extends CaretImpl.DirectionToPosition {

    /**
     *
     */
    public static final String REGEX_FLAGS = "";

    String regex;
    boolean front;
    int index;
    Text text;
    int flags;

    /**
     *
     * @param text
     * @param caretOwner
     * @param regex
     * @param flags
     * @param front
     * @param index
     */
    public RegexCaretDirection(Text text, CaretOwner caretOwner, String regex, int flags, boolean front, int index) {
        super(caretOwner, 0);
        this.text = text;
        this.regex = regex;
        this.flags = flags;
        this.front = front;
        this.index = index;
        if (index < 0) {
            throw new JemmyException("Index must be >=0 but is " + index, regex);
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected double position() {
        return position(text, regex, flags, front, index);
    }

    /**
     *
     * @param text
     * @param regex
     * @param flags
     * @param front
     * @param index
     * @return
     */
    public static int position(Text text, String regex, int flags, boolean front, int index) {
        Matcher matcher = Pattern.compile(regex, flags).matcher(text.text());
        for (int i = 0; i <= index; i++) {
            if (!matcher.find()) {
                throw new JemmyException(index + "'s occurance of \"" + regex + "\" not found.", text);
            }
        }
        return front ? matcher.start() : matcher.end();
    }
}
