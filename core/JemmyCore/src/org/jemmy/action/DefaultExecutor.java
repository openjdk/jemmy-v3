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


/**
 *
 * @author shura
 */
public class DefaultExecutor extends AbstractExecutor {

    /**
     *
     */
    public DefaultExecutor() {
    }

    /**
     * Executes through the ActionQueue as there is no system dispatch thread.
     * @param env {@inheritDoc }
     * @param action {@inheritDoc }
     * @param parameters {@inheritDoc }
     * @see Action#run(java.lang.Object[])
     */
    public void executeQueue(Environment env, Action action, Object... parameters) {
        execute(env, false, action, parameters);
    }

    /**
     * Executes through the ActionQueue as there is no system dispatch thread.
     * @param env {@inheritDoc }
     * @param action {@inheritDoc }
     * @param parameters {@inheritDoc }
     * @see Action#run(java.lang.Object[])
     */
    public void executeQueueDetached(Environment env, Action action, Object... parameters) {
        executeDetached(env, false, action, parameters);
    }

    /**
     * Always returns false as there is no system dispatch thread.
     * @return always false.
     */
    public boolean isOnQueue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isQuiet() {
        return actionsInQueue() == 0 && !isInAction();
    }

}
