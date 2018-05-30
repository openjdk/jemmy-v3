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


import org.jemmy.control.Wrap;

/**
 * This defines an interface to provide flexible way to control
 * test execution from outside. Check Wrap sources code for use case.
 * The interface is not intended to be used directly from test.
 * @see org.jemmy.control.Wrap
 * @author shura
 */
public interface ControlInterfaceFactory {
    /**
     * Instantiates interface.
     * @param <INTERFACE> ControlInterface type
     * @param control control to provide the interface for
     * @param interfaceClass ControlInterface type
     * @return ControlInterface instance or null for an unknown type
     */
    public <INTERFACE extends ControlInterface> INTERFACE create(Wrap<?> control, Class<INTERFACE> interfaceClass);
    /**
     *
     * Instantiates interface.
     * @param <TYPE> todo document
     * @param <INTERFACE> ControlInterface type
     * @param control control to provide the interface for
     * @param interfaceClass ControlInterface type
     * @param type Incapsulated type
     * @return ControlInterface instance or null for an unknown type
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE create(Wrap<?> control, Class<INTERFACE> interfaceClass, Class<TYPE> type);
}
