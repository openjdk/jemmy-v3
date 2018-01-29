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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jemmy.action.GetAction;
import org.jemmy.control.DefaultWrapper;
import org.jemmy.env.Environment;
import org.jemmy.input.AWTRobotInputFactory;
import org.jemmy.lookup.AbstractParent;
import org.jemmy.lookup.ControlHierarchy;
import org.jemmy.lookup.HierarchyLookup;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;

/**
 *
 * @author shura, erikgreijus
 */
public class Shells extends AbstractParent<Shell> {

    public static final Shells SHELLS = new Shells();
    private final Environment env;
    private final ShellList shells;
    final DefaultWrapper swtWrapper;

    private Shells() {
        this(new Environment(Environment.getEnvironment()));
    }

    private Shells(Environment env) {
        this.env = env;
        env.setExecutor(new SWTExecutor());
        shells = new ShellList(env);
        swtWrapper = new DefaultWrapper(env);
        swtWrapper.addAnnotated(ShellWrap.class, ControlWrap.class,
                CompositeWrap.class, TextWrap.class, TabFolderWrap.class,
                ToolBarWrap.class, TableWrap.class, ListWrap.class, ComboWrap.class,
                TreeWrap.class, CTabFolderWrap.class, CComboWrap.class, ScrollableWrap.class);
        env.setInputFactory(new AWTRobotInputFactory());
    }

    public <ST extends Shell> Lookup<ST> lookup(Class<ST> controlClass, LookupCriteria<ST> criteria) {
        return new HierarchyLookup(env, shells, swtWrapper, controlClass, criteria);
    }

    public Lookup<Shell> lookup(LookupCriteria<Shell> criteria) {
        return lookup(getType(), criteria);
    }

    public Environment getEnvironment() {
        return env;
    }

    public Class<Shell> getType() {
        return Shell.class;
    }

    private static class ShellList implements ControlHierarchy {

        Environment env;

        public ShellList(Environment env) {
            this.env = env;
        }

        public List<?> getControls() {
            GetAction<Shell[]> action = new GetAction<Shell[]>() {

                public void run(Object... parameters) {
                    Display.getDefault().syncExec(new Runnable() {

                        public void run() {
                            try {
                                setResult(Display.getDefault().getShells());
                            } catch (SWTException e) {
                                setResult(new Shell[0]);
                            }
                        }
                    });
                }
            };
            env.getExecutor().execute(env, true, action);
            return Arrays.asList(action.getResult());
        }

        public List<?> getChildren(final Object subParent) {
            if (!(subParent instanceof Shell)) {
                return null;
            }
            GetAction<Shell[]> action = new GetAction<Shell[]>() {

                public void run(Object... parameters) {
                    Display.getDefault().syncExec(new Runnable() {

                        public void run() {
                            try {
                                setResult(Shell.class.cast(subParent).getShells());
                            } catch (SWTException e) {
                                setResult(new Shell[0]);
                            }
                        }
                    });
                }
            };
            env.getExecutor().execute(env, true, action);
            return Arrays.asList(action.getResult());
        }

        public Object getParent(Object child) {
            //dunno how to do this
            return null;
        }
    }
}
