/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.swt;

import java.util.Arrays;
import org.eclipse.swt.widgets.List;
import org.jemmy.Point;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.env.Environment;
import org.jemmy.input.KeyboardSelectable;
import org.jemmy.input.KeyboardSelector;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Selector;

/**
 *
 * @author shura
 */
@ControlType(List.class)
public class ListWrap<T extends List> extends ControlWrap<T> implements KeyboardSelectable<String>, Focusable {

    private KeyboardSelector<String> selector = null;

    public ListWrap(Environment env, T node) {
        super(env, node);
    }

    public java.util.List<String> getStates() {
        return new GetAction<java.util.List<String>>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(Arrays.asList(getControl().getItems()));
            }
        }.dispatch(getEnvironment());
    }

    public String getState() {
        return new GetAction<String>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().getSelection()[0]);
            }
        }.dispatch(getEnvironment());
    }

    public int selection() {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().getSelectionIndex());
            }
        }.dispatch(getEnvironment());
    }

    public boolean isVertical() {
        return true;
    }

    public int index(String item) {
        return getStates().indexOf(item);
    }

    public Selector<String> selector() {
        if (selector == null) {
            selector = new KeyboardSelector<String>(this, this);
        }
        return selector;
    }

    public Class<String> getType() {
        return String.class;
    }

    @Override
    public ClickFocus focuser() {
        if (focuser == null) {
            super.focuser().pnt = new Point(1, 1);
        }
        return focuser;
    }
}
