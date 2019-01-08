/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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
package com.company.test;

import org.jemmy.control.ControlInterfaces;
import org.jemmy.control.ControlType;
import org.jemmy.control.MethodProperties;
import org.jemmy.dock.DefaultParent;
import org.jemmy.dock.DockInfo;
import org.jemmy.dock.PreferredParent;
import org.jemmy.dock.Shortcut;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.jemmy.interfaces.Selector;

import java.util.Collection;
import java.util.List;

@ControlType(List.class)
@DockInfo
@MethodProperties({"iterator"})
@ControlInterfaces({Selectable.class})
@PreferredParent(CollectionWrap.class)
public class ListWrap<T extends List> extends CollectionWrap<T> implements Selectable<Object> {

    @DefaultParent("collections")
    public static <C extends Collection> Parent<? super Collection> getRoot(Class<C> cType) {
        return CollectionRoot.PARENT;
    }

    protected ListWrap(Environment env, T node) {
        super(env, node);
    }

    @Override
    public List<Object> getStates() {
        return getControl();
    }

    private volatile Object state = null;

    @Override
    @Shortcut
    public Object getState() {
        return state;
    }

    @Override
    @Shortcut
    public Selector<Object> selector() {
        return new Selector<Object>() {
            @Override
            public void select(Object o) {
                if(getControl().contains(o))
                    state = o;
                else
                    throw new IllegalStateException(o + "is not contained");
            }
        };
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }
}
