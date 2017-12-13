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

import org.jemmy.interfaces.Button;
import org.jemmy.interfaces.Modifier;

/**
 *
 * Defines char-to-key binding.  The generation of a symbol will,
 * in general, require modifier keys to be pressed prior to pressing
 * a primary key.  Classes that implement <code>CharBindingMap</code>
 * communicate what modifiers and primary key are required to generate
 * a given symbol.
 *
 * @param <BUTTON>
 * @param <MODIFIER>
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public interface CharBindingMap<BUTTON extends Button, MODIFIER extends Modifier> {

    /**
     * Returns the code of the primary key used to type a symbol.
     * @param c Character.
     * @return a Button constant.
     */
    public BUTTON getCharKey(char c);

    /**
     * Returns the modifiers that should be pressed to type a symbol.
     * @param c Character.
     * @return an array of Modifier constants.
     */
    public MODIFIER[] getCharModifiers(char c);
}
