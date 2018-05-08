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
package org.jemmy.timing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import org.jemmy.JemmyException;
import org.jemmy.action.Action;
import org.jemmy.action.GetAction;
import org.jemmy.timing.TimedCriteria;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;

/**
 *
 * @author shura
 */
public class Timeline<T> {

    private LinkedList<TimedAction> list = new LinkedList<TimedAction>();
    private Environment env;
    private T control;
    private long startTime = 0;
    private long tic = 10;

    public Timeline(long startTime, Environment env, T control) {
        this.startTime = startTime;
        this.env = env;
        this.control = control;
    }

    public Timeline(long startTime, Wrap<? extends T> wrap) {
        this(startTime, wrap.getEnvironment(), wrap.getControl());
    }

    public Timeline(Environment env, T control) {
        this(0, env, control);
    }

    public Timeline(Wrap<T> wrap) {
        this(wrap.getEnvironment(), wrap.getControl());
    }

    public synchronized final Timeline<T> schedule(long when, TimedCriteria<T> frame) {
        if (when >= 0) {
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).when >= when) {
                        list.add(i, new TimedAction(frame, when));
                        return this;
                    }
                }
                list.add(new TimedAction(frame, when));
            } else {
                list.add(new TimedAction(frame, when));
            }
        }
        return this;
    }

    public synchronized final Timeline<T> schedule(long start, long until, long delta, TimedCriteria<T> frame) {
        long when = start;
        do {
            schedule(when, frame);
        } while ((when += delta) <= until);
        return this;
    }

    synchronized TimedAction next(long until) {
        if (list.get(0).when <= until) {
            return remove();
        }
        return null;
    }

    synchronized TimedAction remove() {
        return list.removeFirst();
    }

    public void start() {
        if (startTime > 0) {
            long before = System.currentTimeMillis();
            while (System.currentTimeMillis() < startTime + before) {
                try {
                    Thread.sleep(tic);
                } catch (InterruptedException ex) {
                    throw new JemmyException("Sleep interrupted.", ex);
                }
            }
        }
        long start = System.currentTimeMillis();
        while (hasMore()) {
            long now = System.currentTimeMillis() - start;
            TimedAction next = next(now);
            if (next != null) {
                env.getExecutor().execute(env,
                        true, next, Long.valueOf(now));
                if (!next.getResult()) {
                    throw new JemmyException("Check failed on " + now + ":", next.criteria);
                }
            }
            try {
                Thread.sleep(tic);
            } catch (InterruptedException ex) {
                throw new JemmyException("Sleep interrupted.", ex);
            }
        }
    }

    public boolean hasMore() {
        return list.size() > 0;
    }

    private class TimedAction extends GetAction<Boolean> {

        long when;
        TimedCriteria<T> criteria;

        public TimedAction(TimedCriteria<T> criteria, long when) {
            this.criteria = criteria;
            this.when = when;
        }

        @Override
        public void run(Object... parameters) {
            setResult(criteria.check(control, (Long) parameters[0]));
        }
    }
}
