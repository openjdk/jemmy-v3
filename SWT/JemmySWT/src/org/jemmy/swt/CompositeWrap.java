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

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;
import org.jemmy.lookup.Any;
import org.jemmy.lookup.ControlHierarchy;
import org.jemmy.lookup.HierarchyLookup;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;

/**
 *
 * @author shura, erikgreijus
 * @param <T>
 */
@ControlType(Composite.class)
public class CompositeWrap<T extends Composite> extends ControlWrap<T> implements Parent<Control> {

    private final SWTHierarchy swth;
    public CompositeWrap(Environment env, T node) {
        super(env, node);
        swth = new SWTHierarchy();
    }

    public <ST extends org.eclipse.swt.widgets.Control> Lookup<ST> lookup(Class<ST> controlClass, LookupCriteria<ST> criteria) {
        return new HierarchyLookup(getEnvironment(), swth, Shells.SHELLS.swtWrapper, controlClass, criteria);
    }

    public <ST extends Control> Lookup<ST> lookup(Class<ST> controlClass) {
        return lookup(controlClass, new Any<ST>());
    }

    public Lookup<org.eclipse.swt.widgets.Control> lookup(LookupCriteria<org.eclipse.swt.widgets.Control> criteria) {
        return lookup(getType(), criteria);
    }

    public Lookup<Control> lookup() {
        return lookup(new Any<Control>());
    }

    public Class<org.eclipse.swt.widgets.Control> getType() {
        return org.eclipse.swt.widgets.Control.class;
    }

    private class SWTHierarchy implements ControlHierarchy {

        public List<?> getControls() {
            GetAction<org.eclipse.swt.widgets.Control[]> action = new GetAction<org.eclipse.swt.widgets.Control[]>() {
                public void run(Object... parameters) {
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            if (!getControl().isDisposed()) {
                                setResult(getControl().getChildren());
                            } else {
                                setResult(new Control[0]);
                            }
                        }
                    });
                }
            };
            getEnvironment().getExecutor().execute(getEnvironment(), true, action);
            return Arrays.asList(action.getResult());
        }

        public List<?> getChildren(final Object subParent) {
            if(!(subParent instanceof Composite)) return null;
            GetAction<org.eclipse.swt.widgets.Control[]> action = new GetAction<org.eclipse.swt.widgets.Control[]>() {
                public void run(Object... parameters) {
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            Composite subParentComposite = Composite.class.cast(subParent);
                            if (!subParentComposite.isDisposed()) {
                                setResult(subParentComposite.getChildren());
                            } else {
                                setResult(new Control[0]);
                            }
                        }
                    });
                }
            };
            getEnvironment().getExecutor().execute(getEnvironment(), true, action);
            return Arrays.asList(action.getResult());
        }

        public Object getParent(final Object child) {
            GetAction<Object> action = new GetAction<Object>() {
                public void run(Object... parameters) {
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            Widget childWidget = Widget.class.cast(child);
                            if (!childWidget.isDisposed()) {
                                setResult(((org.eclipse.swt.widgets.Control)child).getParent());
                            } else {
                                setResult(null);
                            }
                        }
                    });
                }
            };
            getEnvironment().getExecutor().execute(getEnvironment(), true, action);
            return action.getResult();
        }
    }

}
