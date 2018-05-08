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

import org.jemmy.JemmyException;
import org.jemmy.TimeoutExpiredException;
import org.jemmy.env.Timeout;

/**
 *
 * @author shura
 */
public class Waiter {
    /**
     *
     */
    public static final Timeout DEFAULT_DELTA = new Timeout("default.wait.delta", 100);
    private long waitTime;
    private long delta;

    /**
     *
     * @param waitTime
     * @param delta
     */
    public Waiter(Timeout waitTime, Timeout delta) {
        this.waitTime = waitTime.getValue();
        this.delta = delta.getValue();
    }

    /**
     *
     * @param waitTime
     */
    public Waiter(Timeout waitTime) {
        this.waitTime = waitTime.getValue();
        this.delta = DEFAULT_DELTA.getValue();
    }

    /**
     *
     * @param <T>
     * @param state
     * @return
     */
    public <T> T waitState(State<T> state) {
        long start = System.currentTimeMillis();
        T res;
        while( System.currentTimeMillis() < start + waitTime) {
            res = state.reached();
            if(res != null) {
                return res;
            }
            try {
                Thread.sleep(delta);
            } catch (InterruptedException ex) {
                throw new JemmyException("Wait interrupted for: ", state);
            }
        }
        return null;
    }

    /**
     *
     * @param <T>
     * @param value
     * @param state
     * @return
     */
    public <T> T waitValue(final T value, final State<T> state) {
        State<T> st = new State<T>() {
            public T reached() {
                T res = state.reached();
                if(res != null && res.equals(value)) {
                    return res;
                } else {
                    return null;
                }
            }
        };
        return waitState(st);
    }

    /**
     *
     * @param <T>
     * @param state
     * @return
     */
    public <T> T ensureState(State<T> state) {
        T res = waitState(state);
        if(res == null) {
            throw new TimeoutExpiredException("State '" + state + "' has not been reached in " + waitTime + " milliseconds");
        }
        return res;
    }
    /**
     *
     * @param <T>
     * @param value
     * @param state
     * @return
     */
    public <T> T ensureValue(T value, State<T> state) {
        T res = waitValue(value, state);
        if (res == null) {
            throw new TimeoutExpiredException("State '" + state + "' has not been reached in " + waitTime + " milliseconds");
        }
        return res;
    }
}
