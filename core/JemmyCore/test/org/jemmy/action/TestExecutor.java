/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.jemmy.JemmyException;
import org.jemmy.env.Environment;

import javax.swing.SwingUtilities;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestExecutor extends AbstractExecutor {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final Thread runner = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable r = queue.take();
                    r.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public TestExecutor() {
        runner.start();
    }

    @Override
    public void executeQueue(Environment env, Action action, Object... parameters) {
        try {
            boolean[] completed = {false};
            queue.put(() -> {
                synchronized (completed) {
                    try {
                        System.out.println("starting");
                        action.execute(parameters);
                    } finally {
                        System.out.println("completed");
                        completed[0] = true;
                        completed.notify();
                    }
                }
            });
            while (true) {
                synchronized (completed) {
                    if(completed[0])
                        break;
                    completed.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new JemmyException("", e);
        }
    }

    @Override
    public void executeQueueDetached(Environment env, Action action, Object... parameters) {
        try {
            queue.put(() -> action.execute(parameters));
        } catch (InterruptedException e) {
            throw new JemmyException("", e);
        }
    }

    @Override
    public boolean isOnQueue() {
        return Thread.currentThread() == runner;
    }

    @Override
    protected boolean isQuiet() {
        return false;
    }
}
