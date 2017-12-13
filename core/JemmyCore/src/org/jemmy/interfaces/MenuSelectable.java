/*
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.interfaces;

import org.jemmy.lookup.LookupCriteria;

/**
 * Represents a hierarchical structure in which elements not only could be
 * selected but also "pushes", which is an action typically performed with
 * menu.<br/>
 *
 * @author erikgreijus
 * @param <T>
 */
public interface MenuSelectable<T> extends Menu<T> {

    /**
     * Ensures state of a menu item conforming to the criteria. That would mean
     * that all intermediate items get expanded and the menus are shown.
     * Selection depends on if the desired state matches the current state or
     * not. I.e selection of the last criteria happens only if the state differs
     * from desiredSelectionState
     *
     * @param desiredSelectionState The desired selection state of the leaf menu
     * item.
     * @param criteria used one for one level. In case of a menu bar, for
     * example, first criteria is to be used to find a top level menu, second to
     * - find a menu underneath, etc.
     */
    public void push(boolean desiredSelectionState, LookupCriteria<T>... criteria);

    /**
     * Returns the current selection state of the menu item conforming to the
     * criteria. That would mean that all intermediate items get expanded and
     * the menus are shown.
     *
     * @param criteria used one for one level. In case of a menu bar, for
     * example, first criteria is to be used to find a top level menu, second to
     * - find a menu underneath, etc.
     * @return True if the menu item is selected. Otherwise false.
     */
    public boolean getState(LookupCriteria<T>... criteria);
}
