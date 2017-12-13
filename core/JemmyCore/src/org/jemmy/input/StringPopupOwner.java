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

import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.PopupOwner;

/**
 *
 * @author shura
 */
public abstract class StringPopupOwner<T> extends StringCriteriaList<T>
        implements PopupOwner<T>{

    public StringPopupOwner(Wrap<?> menuOwner) {
        super(menuOwner.getEnvironment());
    }

    public void push(Point p, String... texts) {
        if(texts.length == 0) {
            throw new IllegalArgumentException("Menu path length should be greater than 0");
        }
        menu(p).push(createCriteriaList(texts));
    }

    public void select(Point p, String... texts) {
        if(texts.length == 0) {
            throw new IllegalArgumentException("Menu path length should be greater than 0");
        }
        menu(p).select(createCriteriaList(texts));
    }

}
