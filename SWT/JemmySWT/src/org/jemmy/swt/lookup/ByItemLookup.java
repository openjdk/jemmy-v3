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
package org.jemmy.swt.lookup;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.jemmy.resources.StringComparePolicy;

/**
 *
 * @param <T>
 * @author klara, erikgreijus
 */
public class ByItemLookup<T extends Composite> extends QueueLookup<T> {

    private final StringComparePolicy policy;
    private final String text;

    public ByItemLookup(String text) {
        this(text, StringComparePolicy.SUBSTRING);
    }

    public ByItemLookup(String text, StringComparePolicy policy) {
        this.policy = policy;
        this.text = text;
    }

    @Override
    public boolean doCheck(final T control) {
        Object[] items = null;
        if (Tree.class.isInstance(control)) {
            items = Tree.class.cast(control).getItems();
        } else if (ToolBar.class.isInstance(control)) {
            items = ToolBar.class.cast(control).getItems();
        } else if (CTabFolder.class.isInstance(control)) {
            items = CTabFolder.class.cast(control).getItems();
        } else if (TabFolder.class.isInstance(control)) {
            items = TabFolder.class.cast(control).getItems();
        } else if (Table.class.isInstance(control)) {
            items = Table.class.cast(control).getItems();
        } else if (Combo.class.isInstance(control)) {
            items = Combo.class.cast(control).getItems();
        } else {
            System.err.println("Class " + control.getClass() + " does not match any supported class in ByItemLookup");
            return false;
        }
        if (items instanceof Item[]) {
            for (Item item : Item[].class.cast(items)) {
                if (policy.compare(text, item.getText())) {
                    return true;
                } else {
                    try {
                        item.getClass().getMethod("getText", int.class);
                        return ByItemStringsLookup.getTexts(item).stream().map((textElement) -> policy.compare(text, textElement)).anyMatch((matches) -> (matches));
                    } catch (NoSuchMethodException e) {
                        // silently ignore the error to not clutter the logs (not all objects implement the method)
                    } catch (SecurityException e) {
                        System.err.println("SecurityException when using reflection to get method 'getText(int)' from " + item);
                        e.printStackTrace();
                    }
                }
            }
        } else if (items instanceof String[]) {
            for (String stringItem : String[].class.cast(items)) {
                if (policy.compare(text, stringItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Text = " + text;
    }
}
