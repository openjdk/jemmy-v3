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

package org.jemmy.lookup;

import java.io.PrintStream;
import org.jemmy.interfaces.*;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;


/**
 * A searcheable container of a set on UI controls.
 * @param <CONTROL>
 * @author shura
 */
public interface Lookup<CONTROL> extends Parent<CONTROL> {
    /**
     * Default wait control timeout.
     * @see Timeout
     * @see Environment
     */
    public static final Timeout WAIT_CONTROL_TIMEOUT = new Timeout("wait.control", 10000);
    /**
     * Reruns the search until the number of found components is equal or greater
     * than required.
     * @param count
     * @return this or another Lookup instance.
     */
    public Lookup<? extends CONTROL> wait(int count);
    /**
     * Creates an instance of the Wrap class for one of the found UI controls.
     * @see Wrap
     * @param index
     * @return
     */
    public Wrap<? extends CONTROL> wrap(int index);
    /**
     * Same as <code>wrap(0)</code>
     * @see #wrap(int)
     * @return
     */
    public Wrap<? extends CONTROL> wrap();

    /**
     * Returns one of the found UI controls itself.
     * @param index
     * @return
     */
    public CONTROL get(int index);
    /**
     * Same as <code>get(0)</code>
     * @see #get(int)
     * @return
     */
    public CONTROL get();

    /**
     * Same as <code>wrap(index).as(interfaceClass)</code>
     * @param <INTERFACE>
     * @param index
     * @param interfaceClass
     * @return
     * @see #wrap(int)
     * @see Wrap#as(java.lang.Class)
     */
    public <INTERFACE extends ControlInterface> INTERFACE as(int index, Class<INTERFACE> interfaceClass);
    /**
     * Same as <code>wrap().as(interfaceClass)</code>
     * @param <INTERFACE>
     * @param interfaceClass
     * @return
     * @see #wrap()
     * @see Wrap#as(java.lang.Class)
     */
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass);
    /**
     * Same as <code>wrap(index).as(interfaceClass, type)</code>
     * @param <TYPE>
     * @param <INTERFACE>
     * @param index
     * @param interfaceClass
     * @param type
     * @return
     * @see #wrap(int)
     * @see Wrap#as(java.lang.Class, java.lang.Class)
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(int index, Class<INTERFACE> interfaceClass, Class<TYPE> type);
    /**
     * Same as <code>wrap().as(interfaceClass, type)</code>
     * @param <TYPE>
     * @param <INTERFACE>
     * @param interfaceClass
     * @param type
     * @return
     * @see #wrap(int)
     * @see Wrap#as(java.lang.Class, java.lang.Class)
     */
    public <TYPE, INTERFACE extends TypeControlInterface<TYPE>> INTERFACE as(Class<INTERFACE> interfaceClass, Class<TYPE> type);

    /**
     *
     * @return
     */
    public int size();

    /**
     *
     * @param out
     */
    public void dump(PrintStream out);
}
