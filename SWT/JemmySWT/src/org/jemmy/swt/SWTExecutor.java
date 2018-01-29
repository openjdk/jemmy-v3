/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.swt;

import org.eclipse.swt.widgets.Display;
import org.jemmy.action.AbstractExecutor;
import org.jemmy.action.Action;
import org.jemmy.env.Environment;

/**
 *
 * @author shura
 */
public class SWTExecutor extends AbstractExecutor {

    @Override
    public void executeQueue(Environment env, final Action action, final Object[] parameters) {
        if (!isInAction()) {
            env.getOutput(QUEUE_ACTION_OUTPUT).println("Running action on event queue: " + action);
        }
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                action.execute(parameters);
            }
        });
    }

    @Override
    public void executeQueueDetached(Environment env, final Action action, final Object[] parameters) {
        if (!isInAction()) {
            env.getOutput().println("Running detached action on event queue: " + action);
        }
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                action.execute(parameters);
            }
        });
    }

    @Override
    public boolean isOnQueue() {
        return Display.getDefault().getThread() == Thread.currentThread();
    }

    @Override
    protected boolean isQuiet() {
        return true;
    }
}
