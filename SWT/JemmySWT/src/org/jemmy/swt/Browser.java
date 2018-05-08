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

import java.awt.AWTException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.jemmy.action.GetAction;
import org.jemmy.browser.BrowserDescriptor;
import org.jemmy.browser.HierarchyDescriptor;
import org.jemmy.browser.HierarchyView;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.env.Environment;
import org.jemmy.lookup.ControlHierarchy;
import org.jemmy.lookup.ControlList;

/**
 *
 * @author shura, erikgreijus
 */
public class Browser implements BrowserDescriptor {

    public static void main(final String[] args) throws AWTException {
        HierarchyView.startApp(args);
        try {
            new HierarchyView(new Browser()).setVisible(true);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return "SWT hierarchy";
    }

    public ControlList getHierarchy() {
        return new ControlHierarchy() {

            public List<?> getChildren(final Object subParent) {
                return Arrays.asList(new GetAction<Object[]>() {

                    public void run(Object... parameters) {
                        Display.getDefault().syncExec(new Runnable() {

                            public void run() {
                                if (subParent instanceof Composite) {
                                    setResult(((Composite) subParent).getChildren());
                                } else {
                                    setResult(new Object[0]);
                                }
                            }
                        });
                    }
                }.dispatch(Environment.getEnvironment()));
            }

            public Object getParent(final Object child) {
                return new GetAction<Object>() {

                    public void run(Object... parameters) {
                        Display.getDefault().syncExec(new Runnable() {

                            public void run() {
                                if (child instanceof Control) {
                                    setResult(((Control) child).getParent());
                                } else if (child instanceof Item) {
                                    setResult("");
                                } else {
                                    setResult(null);
                                }
                            }
                        });
                    }
                }.dispatch(Environment.getEnvironment());
            }

            public List<?> getControls() {
                return Arrays.asList(new GetAction<Shell[]>() {

                    public void run(Object... parameters) {
                        Display.getDefault().syncExec(new Runnable() {

                            public void run() {
                                setResult(Display.getDefault().getShells());
                            }
                        });
                    }
                }.dispatch(Environment.getEnvironment()));
            }
        };
    }

    public Wrapper getWrapper() {
        return Shells.SHELLS.swtWrapper;
    }

    public HierarchyDescriptor getSubHierarchyDescriptor(Wrap wrap) {
        if (wrap.getControl() instanceof Control) {
            ItemsGet ig = new ItemsGet((Control) wrap.getControl());
            Display.getDefault().syncExec(ig);
            if (ig.items != null) {
                return new ItemHierarchyDescriptor(wrap, TableItem.class,
                        ig.items);
            }
        }
        return null;
    }

    class ItemsGet implements Runnable {

        Item[] items;
        Control control;

        public ItemsGet(Control control) {
            this.control = control;
        }

        public void run() {
            if (control instanceof Table) {
                items = ((Table) control).getItems();
            } else if (control instanceof Tree) {
                items = ((Tree) control).getItems();
            } else if (control instanceof ToolBar) {
                items = ((ToolBar) control).getItems();
            } else if (control instanceof TabFolder) {
                items = ((TabFolder) control).getItems();
            } else if (control instanceof CTabFolder) {
                items = ((CTabFolder) control).getItems();
            }
        }
    }

    private class ItemHierarchyDescriptor implements HierarchyDescriptor {

        Wrap<?> parent;
        ItemParent<?> items;

        public ItemHierarchyDescriptor(Wrap<?> parent, Class type, final Item[] items) {
            this.parent = parent;
            this.items = new ItemParent(parent, type) {

                @Override
                protected List getItems() {
                    return Arrays.asList(items);
                }
            };
        }

        public ControlList getHierarchy() {
            return items.controList;
        }

        public Wrapper getWrapper() {
            return items.wrapper;
        }

        public HierarchyDescriptor getSubHierarchyDescriptor(Wrap wrap) {
            return null;
        }
    }
}
