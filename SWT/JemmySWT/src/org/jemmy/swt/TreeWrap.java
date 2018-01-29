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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jemmy.Point;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.input.KeyboardSelectable;
import org.jemmy.input.KeyboardSelector;
import org.jemmy.input.StringTree;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.jemmy.interfaces.Selector;
import org.jemmy.interfaces.TypeControlInterface;

/**
 *
 * @author shura, erikgreijus
 * @param <T>
 */
@ControlType(Tree.class)
public class TreeWrap<T extends Tree> extends ScrollableWrap<T> implements KeyboardSelectable<String>, Focusable {

    private KeyboardSelector<String> selector = null;
    private ItemParent<TreeItem> items;
    private SWTTree tree = null;

    public TreeWrap(Environment env, T node) {
        super(env, node);
    }

    private void addItems(TreeItem[] items, LinkedList<TreeItem> list) {
        for (TreeItem item : items) {
            list.add(item);
            if (item.getExpanded()) {
                addItems(item.getItems(), list);
            }
        }
    }

    public List<TreeItem> getItems() {
        return new GetAction<List<TreeItem>>() {

            @Override
            public void run(Object... parameters) throws Exception {
                LinkedList<TreeItem> res = new LinkedList<>();
                addItems(getControl().getItems(), res);
                setResult(res);
            }
        }.dispatch(getEnvironment());
    }

    public TreeItem getSelectedItem() {
        return new GetAction<TreeItem>() {

            @Override
            public void run(Object... parameters) throws Exception {
                if (getControl().getSelection().length > 0) {
                    setResult(getControl().getSelection()[0]);
                } else {
                    setResult(null);
                }
            }
        }.dispatch(getEnvironment());
    }

    @Override
    public int selection() {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) throws Exception {
                if (getControl().getSelection().length > 0) {
                    TreeItem selection = getControl().getSelection()[0];
                    setResult(0);
                    if (selection != null) {
                        List<TreeItem> items = getItems();
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i) == selection) {
                                setResult(i);
                            }
                        }
                    }
                } else {
                    setResult(0);
                }

            }
        }.dispatch(getEnvironment());
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    public int index(TreeItem item) {
        return getItems().indexOf(item);
    }

    @Override
    public Selector<String> selector() {
        if (selector == null) {
            selector = new KeyboardSelector<>(this, this);
        }
        return selector;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * Click point for Tree (top of the visible part)
     *
     * @return The click point of this Tree
     */
    @Override
    @Property(Wrap.CLICKPOINT_PROP_NAME)
    public Point getClickPoint() {
        return new Point(getScreenBounds().getWidth() / 2, 1);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TreeItem.class.equals(type)) {
            if (items == null) {
                items = new ItemParent<TreeItem>(this, TreeItem.class) {

                    @Override
                    protected List<TreeItem> getItems() {
                        return TreeWrap.this.getItems();
                    }
                };
            }
            return (INTERFACE) items;
        }
        if (org.jemmy.interfaces.Tree.class.isAssignableFrom(interfaceClass)
                && interfaceClass.isAssignableFrom(StringTree.class) && TreeItem.class.equals(type)) {
            if (tree == null) {
                tree = new SWTTree(this);
            }
            return (INTERFACE) tree;
        }
        return super.as(interfaceClass, type);
    }

    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if (org.jemmy.interfaces.Tree.class.isAssignableFrom(interfaceClass)
                && interfaceClass.isAssignableFrom(StringTree.class)) {
            if (tree == null) {
                tree = new SWTTree(this);
            }
            return (INTERFACE) tree;
        }
        return super.as(interfaceClass);
    }

    @Override
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> boolean is(Class<INTERFACE> interfaceClass, Class<TYPE> type) {
        if (interfaceClass.equals(Parent.class) && TreeItem.class.equals(type)) {
            return true;
        }
        if (org.jemmy.interfaces.Tree.class.isAssignableFrom(interfaceClass)
                && interfaceClass.isAssignableFrom(StringTree.class) && TreeItem.class.equals(type)) {
            return true;
        }
        return super.is(interfaceClass, type);
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if (org.jemmy.interfaces.Tree.class.isAssignableFrom(interfaceClass)
                && interfaceClass.isAssignableFrom(StringTree.class)) {
            return true;
        }
        return super.is(interfaceClass);
    }

    @Override
    public int index(String item) {
        return getStates().indexOf(item);
    }

    @Property(Selectable.STATES_PROP_NAME)
    @Override
    public List<String> getStates() {
        return new GetAction<List<String>>() {

            @Override
            public void run(Object... parameters) throws Exception {
                List<TreeItem> itms = getItems();
                List<String> res = new ArrayList<>(itms.size());
                itms.stream().forEach((i) -> {
                    res.add(i.getText());
                });
                setResult(res);
            }
        }.dispatch(getEnvironment());
    }

    @Property(Selectable.STATE_PROP_NAME)
    @Override
    public String getState() {
        return new GetAction<String>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult((getSelectedItem() == null) ? "" : getSelectedItem().getText());
            }
        }.dispatch(getEnvironment());
    }
}
