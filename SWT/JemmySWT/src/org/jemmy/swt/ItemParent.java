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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Item;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.lookup.AbstractParent;
import org.jemmy.lookup.ControlList;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.lookup.PlainLookup;

/**
 *
 * @author shura
 */
public abstract class ItemParent<T extends Item> extends AbstractParent<T> {

    private final Wrap<?> itemListWrap;
    final ControlList controList;
    private final Class<T> type;
    final Wrapper wrapper;

    public ItemParent(Wrap<?> itemListWrap, Class<T> type) {
        this.itemListWrap = itemListWrap;
        this.type = type;
        controList = new ControlList() {

            public List<?> getControls() {
                List<T> res = getItems();
                return (res != null) ? res : new ArrayList<T>();
            }
        };
        wrapper = new ItemWrapper(ItemParent.this.itemListWrap);
    }

    protected abstract List<T> getItems();

    public <ST extends T> Lookup<ST> lookup(Class<ST> controlClass, LookupCriteria<ST> criteria) {
        return new PlainLookup<ST>(itemListWrap.getEnvironment(),
                controList, wrapper, controlClass, criteria);
    }

    public Lookup<T> lookup(LookupCriteria<T> criteria) {
        return new PlainLookup<T>(itemListWrap.getEnvironment(),
                controList, wrapper, type, criteria);
    }

    public Class<T> getType() {
        return type;
    }

    static class ItemWrapper implements Wrapper {

        Wrap<?> wrap;

        public ItemWrapper(Wrap<?> wrap) {
            this.wrap = wrap;
        }

        public <T> Wrap<? extends T> wrap(Class<T> controlClass, T control) {
            return new ItemWrap(wrap, (Item) control);
        }
    }
}
