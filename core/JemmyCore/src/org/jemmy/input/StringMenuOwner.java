/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.input;

import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Menu;
import org.jemmy.interfaces.MenuOwner;
import org.jemmy.lookup.LookupCriteria;

/**
 * In most cases menu has a text associated with every menu item. This interface
 * makes it easy to push the menu based on that text information.
 * @author shura
 */
public abstract class StringMenuOwner<T> extends StringCriteriaList<T>
        implements MenuOwner<T> {

    private static final String MENU_PATH_LENGTH_ERROR = "Menu path length should be greater than 0";

    public StringMenuOwner(Wrap<?> menuOwner) {
        super(menuOwner.getEnvironment());
    }

    /**
     * Pushes the menu using one string for one level of the menu. Comparison
     * is done according to the policy.
     * @param texts todo document
     * @see #getPolicy()
     */
    public void push(String... texts) {
        if(texts.length == 0) {
            throw new IllegalArgumentException(MENU_PATH_LENGTH_ERROR);
        }
        menu().push(createCriteriaList(texts));
    }

    /**
     * A shortcut to <code>menu().push(LookupCriteria ...)</code>
     * @see #menu()
     * @see Menu#push(LookupCriteria[])
     * @param criteria the lookup criteria
     */
    public void push(LookupCriteria<T>... criteria) {
        menu().push(criteria);
    }

    /**
     * Select a menu item using one string for one level of the menu. Comparison
     * is done according to the policy.
     * @param texts todo document
     * @return wrap for the last selected item
     * @see #getPolicy()
     */
    public Wrap<? extends T> select(String... texts) {
        if(texts.length == 0) {
            throw new IllegalArgumentException(MENU_PATH_LENGTH_ERROR);
        }
        return menu().select(createCriteriaList(texts));
    }

    /**
     * A shortcut to <code>menu().select(LookupCriteria ...)</code>
     * @see #menu()
     * @see Menu#select(LookupCriteria[])
     * @param criteria the lookup criteria
     * @return todo document
     */
    public Wrap<? extends T> select(LookupCriteria<T>... criteria) {
        return menu().select(criteria);
    }

}
