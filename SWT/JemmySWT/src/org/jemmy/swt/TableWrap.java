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
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.jemmy.interfaces.Selector;
import org.jemmy.interfaces.TypeControlInterface;

/**
 *
 * @author shura
 */
@ControlType(Table.class)
public class TableWrap<T extends Table> extends ScrollableWrap<T> implements Selectable<Integer> {

    private ItemParent<TableItem> items = null;
    private List<Integer> states = null;
    private ItemParent<TableColumn> columns = null;
    private IndexItemSelector selector = null;

    public TableWrap(Environment env, T node) {
        super(env, node);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TableItem.class.equals(type)) {
            List<TableItem> itemList = Arrays.asList(new GetAction<TableItem[]>() {

                @Override
                public void run(Object... parameters) {
                    setResult(getControl().getItems());
                }
            }.dispatch(getEnvironment()));
            if (items == null) {
                items = new ItemParent<TableItem>(this, TableItem.class) {

                    @Override
                    protected List<TableItem> getItems() {
                        return Arrays.asList(new GetAction<TableItem[]>() {

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
        if (interfaceClass.equals(Parent.class) && TableColumn.class.equals(type)) {
            if (columns == null) {
                columns = new ItemParent<TableColumn>(this, TableColumn.class) {

                    @Override
                    protected List<TableColumn> getItems() {
                        return Arrays.asList(new GetAction<TableColumn[]>() {

                            @Override
                            public void run(Object... parameters) {
                                setResult(getControl().getColumns());
                            }
                        }.dispatch(getEnvironment()));
                    }
                };
            }
            return (INTERFACE) columns;
        }
        return super.as(interfaceClass, type);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> boolean is(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TableItem.class.equals(type)) {
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

    public List<Integer> getStates() {
        if (states == null) {
            states = new ArrayList<Integer>();
        }
        int size = getItemCount();
        if (states.size() != size) {
            states.clear();
            for (int i = 0; i < size; i++) {
                states.add(i);
            }
        }
        return states;
    }

    @Property(Selectable.STATE_PROP_NAME)
    public Integer getState() {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getSelectionIndex());
            }
        }.dispatch(getEnvironment());
    }

    public Selector<Integer> selector() {
        if (selector == null) {
            if (items == null) {
                as(Parent.class, TableItem.class);
            }
            selector = new IndexItemSelector(items);
        }
        return selector;
    }

    public Class<Integer> getType() {
        return Integer.class;
    }
}
