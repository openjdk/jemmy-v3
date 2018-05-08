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
package org.jemmy.control;

import org.jemmy.JemmyException;
import org.jemmy.interfaces.Selectable;
import org.jemmy.interfaces.Selector;
import org.jemmy.interfaces.Showable;
import org.jemmy.timing.State;

/**
 *
 * @param <CONTROL>
 * @param <STATE>
 * @author shura
 */
public class SelectorImpl<CONTROL, STATE> implements Selector<STATE> {

    Wrap<? extends CONTROL> target;
    Selectable<STATE> selectable;

    /**
     *
     * @param target
     * @param selectable
     */
    public SelectorImpl(Wrap<? extends CONTROL> target, Selectable<STATE> selectable) {
        this.target = target;
        this.selectable = selectable;
    }

    /**
     *
     * @param state
     */
    @SuppressWarnings("unchecked")
    public void select(final STATE state) {
        if (target.is(Showable.class)) {
            target.as(Showable.class).shower().show();
        }
        int attempts = 0;
        if (!selectable.getState().equals(state)) {
            do {
                final STATE currentState = selectable.getState();
                if (attempts >= selectable.getStates().size()) {
                    throw new JemmyException("State is not reached in " + attempts + " attempts", state);
                }
                target.mouse().click(clickCount(state));
                target.getEnvironment().getWaiter(Wrap.WAIT_STATE_TIMEOUT.getName()).ensureState(new State() {

                    public Object reached() {
                        return selectable.getState().equals(currentState) ? null : "";
                    }

                    @Override
                    public String toString() {
                        return "selectable state (" + selectable.getState() + ") equals '" + state + "'";
                    }

                });
                attempts++;
            } while (!selectable.getState().equals(state));
        }
    }

    private int clickCount(STATE state) {
        int current = selectable.getStates().indexOf(selectable.getState());
        int desired = selectable.getStates().indexOf(state);
        if (desired >= current) {
            return desired - current;
        } else {
            return selectable.getStates().size() - current + desired;
        }
    }

    private class StateChangeState implements State<STATE> {

        Selectable<STATE> source;
        STATE original;

        public StateChangeState(Selectable<STATE> source) {
            this.source = source;
            this.original = source.getState();
        }

        public STATE reached() {
            return (source.getState() != original) ? source.getState() : null;
        }
    }
}
