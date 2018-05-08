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
package org.jemmy.swt.lookup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Item;
import org.jemmy.resources.StringComparePolicy;

/**
 *
 * @author erikgreijus
 * @param <T>
 */
public class ByItemStringsLookup<T extends Item> extends QueueLookup<T> {

    private final StringComparePolicy policy;
    private final String text;

    /**
     *
     * @param text The text to use for matching
     */
    public ByItemStringsLookup(String text) {
        this(text, StringComparePolicy.SUBSTRING);
    }

    /**
     *
     * @param text The text to use for matching
     * @param policy The policy to use when matching the text
     */
    public ByItemStringsLookup(String text, StringComparePolicy policy) {
        this.policy = policy;
        this.text = text;
    }

    public static <V extends Item> List<String> getTexts(V item) {
        List<String> result = new ArrayList<>();
        result.add(item.getText());
        try {
            Method getText = item.getClass().getMethod("getText", int.class);
            Method getParent = item.getClass().getMethod("getParent");
            Object parent = getParent.invoke(item);
            Method getColumnCount = parent.getClass().getMethod("getColumnCount");
            int columnCount = (int) getColumnCount.invoke(parent);
            for (int i = 0; i < columnCount; i++) {
                result.add((String) getText.invoke(item, i));
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            System.err.println("Exception when using reflection to get additional text elements: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean doCheck(T control) {
        // this lookup only supports objects with a getText(int) method
        try {
            control.getClass().getMethod("getText", int.class);
            return getTexts(control).stream().map((textElement) -> policy.compare(text, textElement)).anyMatch((matches) -> (matches));
        } catch (NoSuchMethodException e) {
            System.err.println("Class " + control.getClass() + " isn't supported by " + this.getClass().getSimpleName() + " (i.e has no getText(int) method");
            return false;
        }
    }

    @Override
    public String toString() {
        return "Text = " + text;
    }
}
