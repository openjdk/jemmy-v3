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
import org.jemmy.Rectangle;
import org.jemmy.control.ControlInterfaces;
import org.jemmy.control.ControlType;
import org.jemmy.control.MethodProperties;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.dock.DefaultParent;
import org.jemmy.dock.DockInfo;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.TypeControlInterface;
import org.jemmy.lookup.ControlList;
import org.jemmy.lookup.ParentImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ControlType(Collection.class)
@MethodProperties({"isEmpty", "size"})
@ControlInterfaces({Parent.class})
@DockInfo(generateSubtypeLookups = true)
public class CollectionWrap<T extends Collection> extends Wrap<T> {

    @DefaultParent("collections")
    public static <C extends Collection> Parent<? super Collection> getRoot(Class<C> cType) {
        return CollectionRoot.PARENT;
    }

    private final Parent<Object> parent;

    protected CollectionWrap(Environment env, T node) {
        super(env, node);
        parent = new ParentImpl<>(env, Object.class, new ControlList() {
            @Override
            public List<?> getControls() {
                return new ArrayList<>(getControl());
            }
        }, new Wrapper() {
            @Override
            public <T> Wrap<? extends T> wrap(Class<T> controlClass, T control) {
                return (Wrap<T>)new ObjectWrap<T>(getEnvironment(), control);
            }
        });
    }

    @Override
    public Rectangle getScreenBounds() {
        return new Rectangle();
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> aClass) {
        if(Parent.class.isAssignableFrom(aClass)) {
            return true;
        }
        return super.is(aClass);
    }

    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> aClass) {
        if(Parent.class.isAssignableFrom(aClass)) {
            return (INTERFACE) parent;
        }
        return super.as(aClass);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> boolean is(Class<INTERFACE> aClass, Class<TYPE> type) {
        if(Parent.class.isAssignableFrom(aClass) && type.equals(Object.class)) {
            return true;
        }
        return super.is(aClass, type);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> aClass, Class<TYPE> type) {
        if(Parent.class.isAssignableFrom(aClass) && type.equals(Object.class)) {
            return (INTERFACE) parent;
        }
        return super.as(aClass, type);
    }
}
