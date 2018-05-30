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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.jemmy.Point;
import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.control.Wrap;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Focus;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.PopupOwner;

/**
 *
 * @author shura
 * @author erikgreijus
 */
@ControlType(org.eclipse.swt.widgets.Control.class)
public class ControlWrap<T extends org.eclipse.swt.widgets.Control> extends Wrap<T> implements Focusable {

    protected ClickFocus focuser;
    protected PopupOwner popup = null;

    public ControlWrap(Environment env, T node) {
        super(env, node);
    }

    @Override
    public Rectangle getScreenBounds() {
        return getScreenBounds(getControl(), getEnvironment());
    }

    public static Rectangle getScreenBounds(Control control, Environment env) {
        Rectangle res = getBounds(control, env);
        org.eclipse.swt.graphics.Point loc = getScreenLocation(control, env);
        res.x = loc.x;
        res.y = loc.y;
        return res;
    }

    public static org.eclipse.swt.graphics.Point getScreenLocation(final org.eclipse.swt.widgets.Control control, Environment env) {
        GetAction<org.eclipse.swt.graphics.Point> action = new GetAction<org.eclipse.swt.graphics.Point>() {

            public void run(Object... parameters) {
                setResult(Display.getDefault().map(control, null, new org.eclipse.swt.graphics.Point(0, 0)));
            }

            @Override
            public String toString() {
                return "Getting parent for " + control;
            }
        };
        env.getExecutor().execute(env, true, action);
        return action.getResult();
    }

    public Shell getShell() {
        GetAction<Shell> action = new GetAction<Shell>() {

            public void run(Object... parameters) {
                setResult(getControl().getShell());
            }

            @Override
            public String toString() {
                return "Getting shell for " + getControl();
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    public boolean hasFocus() {
        GetAction<Boolean> action = new GetAction<Boolean>() {

            public void run(Object... parameters) {
                setResult(getControl().isFocusControl());
            }

            @Override
            public String toString() {
                return "Getting shell for " + getControl();
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    public Menu menu() {
        GetAction<Menu> action = new GetAction<Menu>() {

            public void run(Object... parameters) {
                setResult(getControl().getMenu());
            }

            @Override
            public String toString() {
                return "Getting shell for " + getControl();
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    public static Composite getParent(final org.eclipse.swt.widgets.Control control, Environment env) {
        GetAction<Composite> action = new GetAction<Composite>() {

            public void run(Object... parameters) {
                setResult(control.getParent());
            }

            @Override
            public String toString() {
                return "Getting parent for " + control;
            }
        };
        env.getExecutor().execute(env, true, action);
        return action.getResult();
    }

    public ClickFocus focuser() {
        if (focuser == null) {
            focuser = new ClickFocus();
        }
        return focuser;
    }

    public static Rectangle getBounds(final org.eclipse.swt.widgets.Control c, Environment env) {
        GetAction<org.eclipse.swt.graphics.Rectangle> action = new GetAction<org.eclipse.swt.graphics.Rectangle>() {

            public void run(Object... parameters) {
                setResult(c.getBounds());
            }

            @Override
            public String toString() {
                return "Getting bounds for " + c;
            }
        };
        env.getExecutor().execute(env, true, action);
        org.eclipse.swt.graphics.Rectangle res = action.getResult();
        return new Rectangle(res.x, res.y, res.width, res.height);
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if (PopupOwner.class.isAssignableFrom(interfaceClass)) {
            return menu() != null;
        }
        //disable focusable for now
        //TODO fix
        if (interfaceClass.equals(Focusable.class)) {
            return false;
        }
        return super.is(interfaceClass);
    }

    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if (PopupOwner.class.isAssignableFrom(interfaceClass)) {
            if (popup == null) {
                popup = new SWTPopupOwner(this);
            }
            return (INTERFACE) popup;
        }
        return super.as(interfaceClass);
    }

    protected class ClickFocus implements Focus {

        Point pnt = null;
        int clickCount = 1;

        public void focus() {
            if (!hasFocus()) {
                mouse().click(clickCount, pnt);
            }
        }
    }

    @Property(Wrap.NAME_PROP_NAME)
    public String name() {
        GetAction<String> action = new GetAction<String>() {
            @Override
            public void run(Object... parameters) {
                setResult((String) getControl().getData("name"));
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Property(Wrap.TEXT_PROP_NAME)
    public String text() {
        GetAction<String> action = new GetAction<String>() {
            @Override
            public void run(Object... parameters) {
                String result = "";
                try {
                    Method mthd = getControl().getClass().getMethod("getText");
                    result = (String) mthd.invoke(getControl());
                } catch (NoSuchMethodException e) {
                    System.err.println("Exception when using reflection to get text from " + getControl());
                    e.printStackTrace();
                } catch (SecurityException e) {
                    System.err.println("Exception when using reflection to get text from " + getControl());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.err.println("Exception when using reflection to get text from " + getControl());
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.err.println("Exception when using reflection to get text from " + getControl());
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    System.err.println("Exception when using reflection to get text from " + getControl());
                    e.printStackTrace();
                }
                setResult(result);
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }
}
