/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.swt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.swt.widgets.Item;
import org.jemmy.JemmyException;
import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;

/**
 *
 * @author shura
 * @author erikgreijus
 */
public class ItemWrap<T extends Item> extends Wrap<T> {

    private final Wrap<?> parent;

    public ItemWrap(Wrap<?> parent, T node) {
        super(new Environment(parent.getEnvironment()), node);
        this.parent = parent;
    }

    @Property(Wrap.TEXT_PROP_NAME)
    public String getText() {
        return new GetAction<String>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getText());
            }
        }.dispatch(getEnvironment());
    }

    /**
     * @return The visible part of this item's bounds (intersection of this and the parent's screen bounds)
     */
    @Override
    public Rectangle getScreenBounds() {
        try {
            final Method m = getControl().getClass().getMethod("getBounds");
            org.eclipse.swt.graphics.Rectangle bounds =
                    new GetAction<org.eclipse.swt.graphics.Rectangle>() {

                @Override
                public void run(Object... parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    setResult((org.eclipse.swt.graphics.Rectangle) m.invoke(getControl()));
                }
            }.dispatch(getEnvironment());
            Rectangle parentBounds = parent.getScreenBounds();
            return parentBounds.intersection(new Rectangle(bounds.x + parentBounds.x, bounds.y + parentBounds.y, bounds.width, bounds.height));
        } catch (IllegalArgumentException | SecurityException ex) {
            throw new JemmyException("Unable to get bounds on " + getControl(), ex);
        } catch (NoSuchMethodException ex) {
            throw new JemmyException("Bounds are not supported by " + getControl().getClass().getName(), ex);
        }
    }
}
