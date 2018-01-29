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

import org.eclipse.swt.widgets.Scrollable;
import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.env.Environment;

/**
 * Base wrapper for scrollable controls such as Table and Tree
 *
 * @author erikgreijus
 * @param <T>
 */
@ControlType(Scrollable.class)
public class ScrollableWrap<T extends Scrollable> extends ControlWrap<T> {

    public ScrollableWrap(Environment env, T node) {
        super(env, node);
    }

    /**
     * Returns the screen bounds of this Scrollable
     *
     * @return A Rectangle describing the screen bounds of this Scrollable
     */
    @Override
    public Rectangle getScreenBounds() {
        return getScreenBounds(Scrollable.class.cast(getControl()), getEnvironment());
    }

    /**
     * Returns the screen bounds of this Scrollable
     *
     * @param scrollable This Scrollable
     * @param env The environment
     * @return A Rectangle describing the screen bounds of this Scrollable
     */
    public static Rectangle getScreenBounds(Scrollable scrollable, Environment env) {
        Rectangle res = getBounds(scrollable, env);
        org.eclipse.swt.graphics.Point loc = getScreenLocation(scrollable, env);
        res.x = loc.x;
        res.y = loc.y;
        return res;
    }

    /**
     * Returns a rectangle describing the visible part of the supplied
     * Scrollable.
     *
     * @see org.eclipse.swt.widgets.Scrollable#getClientArea()
     * @param scrollable The Scrollable for which to get the client area
     * @param env The Jemmy Environment
     * @return A rectangle describing the visible part of this scrollable
     */
    public static Rectangle getBounds(final Scrollable scrollable, Environment env) {
        GetAction<org.eclipse.swt.graphics.Rectangle> action = new GetAction<org.eclipse.swt.graphics.Rectangle>() {

            @Override
            public void run(Object... parameters) {
                setResult(scrollable.getClientArea());
            }

            @Override
            public String toString() {
                return "Getting bounds (client area) for " + scrollable;
            }
        };
        env.getExecutor().execute(env, true, action);
        org.eclipse.swt.graphics.Rectangle res = action.getResult();
        return new Rectangle(res.x, res.y, res.width, res.height);
    }
}
