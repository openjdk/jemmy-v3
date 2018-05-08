/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.jemmy.interfaces.Selector;
import org.jemmy.interfaces.TypeControlInterface;
import org.jemmy.lookup.Lookup;

/**
 *
 * @author shura, erikgreijus
 * @param <T>
 */
@ControlType(TabFolder.class)
public class TabFolderWrap<T extends TabFolder> extends ControlWrap<T> implements Selectable<String> {

    private ItemParent<TabItem> items = null;
    private TextItemSelector selector = null;

    public TabFolderWrap(Environment env, T node) {
        super(env, node);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TabItem.class.equals(type)) {
            if (items == null) {
                items = new ItemParent<TabItem>(this, TabItem.class) {

                    @Override
                    protected List<TabItem> getItems() {
                        return Arrays.asList(new GetAction<TabItem[]>() {

                            @Override
                            public void run(Object... parameters) {
                                setResult(getControl().getItems());
                            }
                        }.dispatch(getEnvironment()));
                    }
                };
            }
            return (INTERFACE) items;
        }
        return super.as(interfaceClass, type);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> boolean is(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TabItem.class.equals(type)) {
            return true;
        }
        return super.is(interfaceClass, type);
    }

    public int getItemCount() {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getItemCount());
            }
        }.dispatch(getEnvironment());
    }

    @Property(Selectable.STATES_PROP_NAME)
    public List<String> getStates() {
        return new GetAction<List<String>>() {

            @Override
            public void run(Object... parameters) {
                Lookup<TabItem> lookup = as(Parent.class, TabItem.class).lookup();
                ArrayList<String> res = new ArrayList<String>();
                for (int i = 0; i < lookup.size(); i++) {
                    res.add(lookup.get(i).getText());
                }
                setResult(res);
            }
        }.dispatch(getEnvironment());
    }

    @Property(Selectable.STATE_PROP_NAME)
    public String getState() {
        return new GetAction<String>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getSelection()[0].getText());
            }
        }.dispatch(getEnvironment());
    }

    public Selector<String> selector() {
        if (selector == null) {
            if (items == null) {
                as(Parent.class, TabItem.class);
            }
            selector = new TextItemSelector(items);
        }
        return selector;
    }

    public Class<String> getType() {
        return String.class;
    }
}
