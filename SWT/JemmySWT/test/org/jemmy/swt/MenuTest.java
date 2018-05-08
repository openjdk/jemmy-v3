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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.input.StringMenuOwner;
import org.jemmy.input.StringPopupOwner;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.PopupOwner;
import org.jemmy.interfaces.Selectable;
import org.jemmy.resources.StringComparePolicy;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author shura
 */
public class MenuTest {

    public MenuTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        new Thread(new Runnable() {

            public void run() {
                Items.main(null);
            }
        }).start();
        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    static Wrap<? extends Shell> shell;

    @BeforeMethod
    public void setUp() {
        shell = Shells.SHELLS.lookup().wrap();
        ((Selectable)shell.as(Parent.class, Control.class).lookup(TabFolder.class).as(Selectable.class)).selector().select("Toolbar");
    }

    @AfterMethod
    public void tearDown() {
    }
    String[][] menus = new String[][]{
        {"menu1", "item10"},
        {"menu0", "item00"},
        {"menu1", "item11"},
        {"menu0", "menu00", "item000"},
        {"menu1", "item12"},
        {"menu0", "item01"},};

    @Test
    public void bar() throws InterruptedException {
        for (String[] item : menus) {
            shell.as(StringMenuOwner.class).push(item);
            shell.waitProperty(Wrap.TEXT_PROP_NAME, item[item.length - 1] + " selected");
        }
    }

    @Test
    public void popup() throws InterruptedException {
        Wrap<? extends ToolBar> toolbar = shell.as(Parent.class, Control.class).
                lookup(ToolBar.class).wrap();
        for (String[] item : menus) {
            toolbar.as(StringPopupOwner.class).push(PopupOwner.CENTER, item);
            shell.waitProperty(Wrap.TEXT_PROP_NAME, item[item.length - 1] + " selected");
        }
    }

    @Test
    public void popupPoint() throws InterruptedException {
        Wrap<? extends ToolBar> toolbar = shell.as(Parent.class, Control.class).
                lookup(ToolBar.class).wrap();
        toolbar.as(StringPopupOwner.class).push(new Point(1, 1), menus[0]);
        toolbar.as(StringPopupOwner.class).push(new Point(1, 1), menus[3]);
    }

    @Test
    public void bySubString() {
        StringMenuOwner menu = shell.as(StringMenuOwner.class);
        menu.setPolicy(StringComparePolicy.SUBSTRING);
        menu.push("0", "menu", "");
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "item000 selected");
    }
}
