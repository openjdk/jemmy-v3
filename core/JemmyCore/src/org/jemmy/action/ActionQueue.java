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
package org.jemmy.action;


import java.util.LinkedList;
import org.jemmy.JemmyException;
import org.jemmy.TimeoutExpiredException;


/**
 *
 * @author shura, KAM
 */
class ActionQueue {

    private Thread queueThread;
    private final LinkedList<ActionRecord> queue;
    private boolean stop = false;

    public ActionQueue() {
        queue = new LinkedList<ActionRecord>();
        queueThread = new Thread(new Runnable() {

            public void run() {
                int size;
                while (!stop) {
                    synchronized (queue) {
                        size = queue.size();
                        if (size == 0) {
                            try {
                                queue.wait();
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                    if (size > 0) {
                        ActionRecord r;
                        synchronized (queue) {
                            r = queue.poll();
                        }
                        try {
                            r.execute();
                        } catch (Exception e) {
                            System.err.println("Action '" + r + "' failed with the following exception: ");
                            e.printStackTrace(System.err);
                            System.err.flush();
                        }
                        r.setCompleted(true);
                    }
                }
            }
        }, "ActionQueue.queueThread");
        queueThread.start();
    }

    public int actionsInQueue() {
        synchronized(queue) {
            return queue.size();
        }
    }

    public void stop() {
        stop = true;
    }

    /**
     * Returns internal ActionQueue event dispatching thread
     * @return queue dispatching thread of ActionQueue object
     */
    public Thread getQueueThread() {
        return queueThread;
    }

    /**
     * Schedules execution of an action throught the internal ActionQueue queue
     * and exits immediately
     * @param action action to execute
     * @param parameters parameters to pass to action.run() method
     */
    public void invoke(Action action, Object... parameters) {
        synchronized (queue) {
            queue.add(new ActionRecord(action, parameters));
            queue.notifyAll();
        }
    }

    /**
     * Schedules execution of an action through the internal ActionQueue queue
     * and waits until it is completed
     * @param action action to execute
     * @param parameters parameters to pass to action.run() method
     */
    public void invokeAndWait(Action action, Object... parameters) {
        ActionRecord r = new ActionRecord(action, parameters);
        synchronized (queue) {
            queue.add(r);
            queue.notifyAll();
        }
        r.waitCompleted();

        if (r.failed()) {
            throw new JemmyException("Action '" + r + "' invoked through ActionQueue failed", r.getThrowable());
        }
    }

    private class ActionRecord {

        Action action;
        Object[] parameters;
        boolean completed;
        boolean started;

        public ActionRecord(Action action, Object[] parameters) {
            this.action = action;
            this.parameters = parameters;
        }

        public boolean failed() {
            return action.failed();
        }

        public Throwable getThrowable() {
            return action.getThrowable();
        }

        public Object[] getParameters() {
            return parameters;
        }

        public boolean isCompleted() {
            return completed;
        }

        public synchronized void setCompleted(boolean completed) {
            this.completed = completed;
            notifyAll();
        }

        public void execute() {
            synchronized (this) {
                started = true;
                notifyAll();
            }
            action.execute(parameters);
        }

        public synchronized void waitCompleted() {
            try {
                while (!started) {
                    wait();
                }
                if (!completed) {
                    wait(action.getAllowedTime());
                    if (!completed) {
                        action.interrupt();
                        throw new TimeoutExpiredException("Action did not finish in " + action.getAllowedTime() + " ms: " + action);
                    }
                }
            } catch (InterruptedException ex) {
            }
        }

        @Override
        public String toString() {
            return action.toString();
        }
    }
}
