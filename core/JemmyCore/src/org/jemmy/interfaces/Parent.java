/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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

import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;

/**
 * Represents a container for UI controls.
 * @param <T> type of the control.
 * @author shura
 */
public interface Parent<T> extends TypeControlInterface<T> {
    /**
     * Searcher the hierarchy for objects extending <code>ST</code> which fit
     * the criteria.
     * @see Lookup
     * @see LookupCriteria
     * @param <ST>
     * @param controlClass
     * @param criteria
     * @return an instance of Lookup, which holds found controls.
     */
    public <ST extends T> Lookup<ST> lookup(Class<ST> controlClass, LookupCriteria<ST> criteria);
    /**
     * Same as <code>lookup(controlClass, new Any<ST>())</code>
     * @see #lookup(java.lang.Class, org.jemmy.lookup.LookupCriteria)
     * @param <ST>
     * @param controlClass
     * @return an instance of Lookup, which holds found controls.
     */
    public <ST extends T> Lookup<ST> lookup(Class<ST> controlClass);
    /**
     * Searcher the hierarchy for objects extending <code>T</code> which fit
     * the criteria.
     * @see Lookup
     * @see LookupCriteria
     * @param criteria
     * @return an instance of Lookup, which holds found controls.
     */
    public Lookup<T> lookup(LookupCriteria<T> criteria);
    /**
     * Same as <code>lookup(new Any<T>())</code>
     * @see #lookup(org.jemmy.lookup.LookupCriteria)
     * @return an instance of Lookup, which holds found controls.
     */
    public Lookup<T> lookup();
}
