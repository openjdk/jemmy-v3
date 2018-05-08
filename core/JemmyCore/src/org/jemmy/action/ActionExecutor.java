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

package org.jemmy.action;


import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;


/**
 * Interface to execute user's action <b>at appropriate moment</b>.
 * @author shura
 */
public interface ActionExecutor {

    /**
     *
     */
    public static final String ACTION_EXECUTOR_PROPERTY = "action.executor";

    /**
     * Schedules to execute an action and waits for it to finish.
     * @param env Environment.
     * @param dispatch if true the action is executed on UI system dispatch
     * thread. This is usually necessary to invoke methods of the UI to get
     * the correct state or to update it.
     * @param action Action to execute.
     * @param parameters Parameters to pass to
     * {@linkplain Action#run(java.lang.Object[]) action.run()} method.
     */
    public void execute(Environment env, boolean dispatch, Action action, Object... parameters);

    /**
     * Schedules to execute an action and exits immediately. Used to be called
     * DoSomethingNoBlock operations in jemmy2.
     * @param env Environment.
     * @param dispatch if true the action is executed on UI system dispatch
     * thread. This is usually necessary to invoke methods of the UI to get
     * the correct state or to update it.
     * @param action Action to execute.
     * @param parameters Parameters to pass to
     * {@linkplain Action#run(java.lang.Object[]) action.run()} method.
     */
    public void executeDetached(Environment env, boolean dispatch, Action action, Object... parameters);

    /**
     * Checks whether the current thread is already performing an action.
     * @return true if the current thread is already performing an action.
     * @see AbstractExecutor#isDispatchThread()
     */
    public boolean isInAction();

    /**
     * Waits for no activities to be going on. Implementation may be different
     * for different mechanisms.
     * @param waitTime maximum time for waiting.
     * @see AbstractExecutor#isQuiet()
     */
    public void waitQuiet(Timeout waitTime);
}
