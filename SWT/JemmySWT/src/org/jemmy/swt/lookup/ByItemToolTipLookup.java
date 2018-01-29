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

import java.lang.reflect.Method;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.jemmy.resources.StringComparePolicy;

/**
 *
 * @param <T>
 * @author klara
 */
public class ByItemToolTipLookup<T extends Composite> extends QueueLookup<T> {

    private final StringComparePolicy policy;
    private final String text;

    public ByItemToolTipLookup(String text) {
        this(text, StringComparePolicy.SUBSTRING);
    }

    protected ByItemToolTipLookup(String text, StringComparePolicy policy) {
        this.policy = policy;
        this.text = text;
    }

    public boolean doCheck(final T control) {
        Item[] items = null;
        if (ToolBar.class.isInstance(control)) {
            items = ToolBar.class.cast(control).getItems();
        } else if (CTabFolder.class.isInstance(control)) {
            items = CTabFolder.class.cast(control).getItems();
        } else if (TabFolder.class.isInstance(control)) {
            items = TabFolder.class.cast(control).getItems();
        } else {
            System.err.println("Class " + control.getClass() + " does not match any supported class in ByItemLookup");
            return false;
        }
        try {
            for (Item item : items) {
                Method mthd = item.getClass().getMethod("getToolTipText");
                if (policy.compare(text, (String) mthd.invoke(item))) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Exception when using reflection to get tooltip text from items of " + control);
                e.printStackTrace();
        }
        return false;
    }
}

