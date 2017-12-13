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

import java.util.List;
import org.jemmy.control.Property;
import org.jemmy.dock.Shortcut;

/**
 * Interface for a control with one selected state among the number of them.
 * @param <STATE>
 * @author shura, KAM
 */
public interface Selectable<STATE> extends TypeControlInterface<STATE> {

    /**
     *
     */
    public static final String STATES_PROP_NAME = "states";
    public static final String STATE_PROP_NAME = "state";

    /**
     * Returns all available states.
     * @return List of all states.
     */
    @Property(STATES_PROP_NAME)
    public List<STATE> getStates();

    /**
     * Returns currently selected state.
     * @return Selected state.
     */
    @Property(STATE_PROP_NAME)
    public STATE getState();

    /**
     * Returns Selector class instance that has methods to select state.
     * @return Selector class instance for the control.
     */
    @Shortcut
    public Selector<STATE> selector();

}
