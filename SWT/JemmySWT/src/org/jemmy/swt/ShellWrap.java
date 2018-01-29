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

import java.awt.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.MenuOwner;

/**
 *
 * @author shura
 */
@ControlType(Shell.class)
public class ShellWrap<T extends Shell> extends CompositeWrap<T> {

    final SWTMenuOwner menuBar;

    public ShellWrap(Environment env, T node) {
        super(env, node);
        menuBar = new SWTMenuOwner(this, true);
    }

    public static Rectangle getBounds(final Shell s, Environment env) {
        GetAction<org.eclipse.swt.graphics.Rectangle> action = new GetAction<org.eclipse.swt.graphics.Rectangle>() {

            public void run(Object... parameters) {
                setResult(s.getBounds());
            }

            @Override
            public String toString() {
                return "Getting bounds for " + s;
            }

        };
        env.getExecutor().execute(env, true, action);
        org.eclipse.swt.graphics.Rectangle res = action.getResult();
        return new Rectangle(res.x, res.y, res.width, res.height);
    }

    public static Rectangle getClientArea(final Shell s, Environment env) {
        GetAction<org.eclipse.swt.graphics.Rectangle> action = new GetAction<org.eclipse.swt.graphics.Rectangle>() {

            public void run(Object... parameters) {
                setResult(s.getClientArea());
            }

            @Override
            public String toString() {
                return "Getting client area for " + s;
            }

        };
        env.getExecutor().execute(env, true, action);
        org.eclipse.swt.graphics.Rectangle res = action.getResult();
        return new Rectangle(res.x, res.y, res.width, res.height);
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

    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if(MenuOwner.class.isAssignableFrom(interfaceClass)) {
            return (INTERFACE) menuBar;
        }
        return super.as(interfaceClass);
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if(MenuOwner.class.isAssignableFrom(interfaceClass)) {
            return true;
        }
        return super.is(interfaceClass);
    }
}
