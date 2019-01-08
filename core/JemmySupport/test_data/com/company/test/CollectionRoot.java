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

import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.lookup.ControlList;
import org.jemmy.lookup.ParentImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionRoot {
    public static final List<Collection> COLLECTIONS = new ArrayList<>();
    public static final Parent<Collection> PARENT = new ParentImpl<Collection>(Environment.getEnvironment(),
            Collection.class, new CollectionList(),
            new CollectionWrapper());


    private static class CollectionList implements ControlList {
        @Override
        public List<Collection> getControls() {
            return COLLECTIONS;
        }
    }

    private static class CollectionWrapper implements Wrapper {
        @Override
        public <T> Wrap<? extends T> wrap(Class<T> controlClass, T control) {
            if(control instanceof List) {
                return new ListWrap(Environment.getEnvironment(), (List)control);
            } else {
                return new CollectionWrap(Environment.getEnvironment(), (Collection)control);
            }
        }
    }
}
