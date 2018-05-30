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
package org.jemmy.input;

import java.util.ArrayList;
import org.jemmy.JemmyException;
import org.jemmy.Point;
import org.jemmy.action.Action;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Caret;
import org.jemmy.interfaces.CaretOwner;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import org.jemmy.interfaces.Mouse.MouseButtons;

/**
 *
 * @author shura
 */
public class CaretImpl implements Caret {

    /**
     * Time to sleep between scrolling actions
     */
    public static final Timeout SCROLL_TIMEOUT =
            new Timeout("ScrollerImpl.scroll", 1);

    static {
        Environment.getEnvironment().initTimeout(SCROLL_TIMEOUT);
    }
    private Wrap<?> wrap;
    private CaretOwner caretOwner;
    private ArrayList<ScrollAction> actions;

    /**
     * @param wrap the control wrapper instance
     * @param caretOwner only position() is used
     */
    public CaretImpl(Wrap<?> wrap, CaretOwner caretOwner) {
        this.wrap = wrap;
        this.caretOwner = caretOwner;
        actions = new ArrayList<ScrollAction>();
    }

    public Wrap<?> getWrap() {
        return wrap;
    }

    protected void addScrollAction(ScrollAction action) {
        actions.add(0, action);
    }

    public void to(final double value) {
        to(new DirectionToPosition(caretOwner, value));
    }

    public void to(final Caret.Direction direction) {
        wrap.getEnvironment().getExecutor().execute(wrap.getEnvironment(), false, new Action() {

            @Override
            public void run(Object... parameters) {
                if (direction.to() == 0) {
                    return;
                }
                if (wrap.is(Focusable.class)) {
                    wrap.as(Focusable.class).focuser().focus();
                }
                int orig = direction.to();
                if (orig == 0) {
                    return;
                }
                double prevPos = caretOwner.position();
                double prevDist = Double.MAX_VALUE;
                for (int i = 0; i < actions.size(); i++) {
                    while (!isInterrupted() && (direction.to() * orig) >= 0) {
                        actions.get(i).scrollTo(orig);
                        wrap.getEnvironment().getTimeout(SCROLL_TIMEOUT).sleep();
                        //if didn't move - use the smaller adjustment
                        //like, puching up when in the first line
                        if(caretOwner.position() == prevPos) {
                            //if did not move and there are more - move to next
                            if(i < actions.size() - 1) {
                                break;
                            } else {
                                //try more and finally fail by timeout
                                //throw new JemmyException("Unable to scoll.", wrap);
                            }
                        }
                        prevPos = caretOwner.position();
                        if (direction.to() == 0) {
                            return;
                        }
                    }
                    orig = direction.to();
                }
            }

            @Override
            public String toString() {
                return "Scrolling to " + direction.toString() + " condition";
            }
        });
    }

    /**
     * @deprecated Use ApproximateCaretOwner.ToPosition or PreciseCaretOwner.ToPosition
     */
    public static class DirectionToPosition implements Direction {

        private double value;
        private CaretOwner caret;
        private double precision;

        public DirectionToPosition(CaretOwner caret, double value, double precision) {
            this.value = value;
            this.caret = caret;
            this.precision = precision;
        }

        public DirectionToPosition(CaretOwner caret, double value) {
            this(caret, value, 0);
        }

        public int to() {
            double diff = position() - caret.position();
            return (diff == 0) ? 0 : ((diff > 0) ? 1 : -1);
        }

        @Override
        public String toString() {
            return "value == " + position();
        }

        protected double position() {
            return value;
        }
    }

    /**
     *
     */
    protected static interface ScrollAction {

        public void scrollTo(int direction);
    }

    protected class MouseScrollAction implements ScrollAction {

        Point up, down;
        KeyboardModifier[] upMods, downMods;

        public MouseScrollAction(Point down, KeyboardModifier[] downMods, Point up, KeyboardModifier[] upMods) {
            this.up = up;
            this.down = down;
            this.upMods = upMods;
            this.downMods = downMods;
        }

        public MouseScrollAction(Point down, Point up) {
            this(up, new KeyboardModifier[0], up, new KeyboardModifier[0]);
        }

        public void scrollTo(int direction) {
            wrap.mouse().click(1, (direction > 0) ? up : down, MouseButtons.BUTTON1,
                    (direction > 0) ? upMods : downMods);
        }
    }

    protected class KeyboardScrollAction implements ScrollAction {

        KeyboardButton down, up;
        KeyboardModifier[] downMods, upMods;

        public KeyboardScrollAction(KeyboardButton down, KeyboardModifier[] downMods, KeyboardButton up, KeyboardModifier[] upMods) {
            this.down = down;
            this.up = up;
            this.downMods = downMods;
            this.upMods = upMods;
        }

        public KeyboardScrollAction(KeyboardButton down, KeyboardButton up) {
            this(down, new KeyboardModifier[0], up, new KeyboardModifier[0]);
        }

        public void scrollTo(int direction) {
            wrap.keyboard().pushKey((direction > 0) ? up : down,
                    (direction > 0) ? upMods : downMods);
        }
    }
}
