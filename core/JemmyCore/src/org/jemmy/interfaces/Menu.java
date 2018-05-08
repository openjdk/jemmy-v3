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

package org.jemmy.interfaces;

import org.jemmy.lookup.LookupCriteria;

/**
 * Represents a hierarchical structure (hence extending TreeSelector) in which
 * elements not only could be selected but also "pushes", which is an action
 * typically performed with menu.<br/>
 * @author shura
 * @see MenuOwner
 */
public interface Menu<T> extends TreeSelector<T>{

    /**
     * Pushes a menu item conforming to the criteria. That would mean that all
     * intermediate items get expanded and the menus are shown, etc., etc. It is
     * up to implementation whether to call select first or to do it somehow
     * differently.
     * @param criteria used one for one level. In case of a menu bar,
     * for example, first criteria is to be used to find a top level menu, second -
     * to find a menu underneath, etc
     */
    public abstract void push(LookupCriteria<T>... criteria);

}
