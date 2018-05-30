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

import org.jemmy.Point;
import org.jemmy.Rectangle;
import org.jemmy.control.Wrap;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Caret;
import org.jemmy.interfaces.CaretOwner;
import org.jemmy.interfaces.Scroll;

/**
 * Performs scrolling by clicking at a certain position.
 * @author shura
 */
public abstract class ScrollerImpl extends CaretImpl {

    /**
     * @deprecated Use AdvancedScroller.SCROLL_TIMEOUT
     */
    public static final Timeout SCROLL_TIMEOUT = CaretImpl.SCROLL_TIMEOUT;

    Scroll scroll;

    public ScrollerImpl(Wrap target, CaretOwner caret) {
        super(target, caret);
        scroll = new CaretScroll(caret);
        addScrollAction(new ScrollAction() {

            public void scrollTo(int direction) {
                getWrap().mouse().click(1, getScrollClickPoint(direction > 0));
            }
        });
    }

    /**
     * @param increase <code>true</code> to increase, <code>false</code> to decrease the value
     * @return  a point to click in order to decrease/increase the value
     */
    protected abstract Point getScrollClickPoint(boolean increase);

    /**
     * An auxiliary function to calculate click point, on the appropriate side
     * of the control depending on the parameters.
     * @param c the control wrapper
     * @param horizontal - horizontal or vertical
     * @param increase - increase or decrease
     * @param offset distance from the border
     * @return the point instance
     */
    public static Point createScrollPoint(Wrap c, boolean horizontal, boolean increase, int offset) {
        return createScrollPoint(c.getScreenBounds(), horizontal, increase, offset);
    }

    public static Point createScrollPoint(Rectangle bounds, boolean horizontal, boolean increase, int offset) {
        if(horizontal) {
            return new Point(increase ? (bounds.width - 1 - offset) : offset, bounds.height / 2);
        } else {
            return new Point(bounds.width / 2, increase ? (bounds.height - 1 - offset) : offset);
        }
    }

    public static class CaretScroll implements Scroll {

        CaretOwner co;

        public CaretScroll(CaretOwner co) {
            this.co = co;
        }

        public double maximum() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public double minimum() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public double position() {
            return co.position();
        }

        public Caret caret() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void to(double position) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
