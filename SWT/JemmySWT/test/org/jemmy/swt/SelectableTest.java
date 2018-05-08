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

import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Table;
import org.jemmy.interfaces.Selectable;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Control;
import org.jemmy.interfaces.Parent;
import org.jemmy.control.Wrap;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author shura
 */
public class SelectableTest {

    public SelectableTest() {
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

    Parent<Control> shell;

    @BeforeMethod
    public void setUp() {
        shell = Shells.SHELLS.lookup().as(Parent.class, Control.class);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void tab() {
        Wrap<? extends TabFolder> tab = shell.lookup(TabFolder.class).wrap();
        Selectable tabs = tab.as(Selectable.class);
        tabs.selector().select("Toolbar");
        tab.waitProperty(Selectable.STATE_PROP_NAME, "Toolbar");
        tabs.selector().select("Table");
        tab.waitProperty(Selectable.STATE_PROP_NAME, "Table");
    }

    @Test
    public void table() {
        shell.lookup(TabFolder.class).as(Selectable.class).selector().select("Table");
        Wrap<? extends Table> table = shell.lookup(Table.class).wrap();
        Selectable<Integer> rows = table.as(Selectable.class, Integer.class);
        rows.selector().select(1);
        table.waitProperty(Selectable.STATE_PROP_NAME, 1);
        rows.selector().select(2);
        table.waitProperty(Selectable.STATE_PROP_NAME, 2);
        rows.selector().select(0);
        table.waitProperty(Selectable.STATE_PROP_NAME, 0);
    }

    @Test
    public void tree() {
        shell.lookup(TabFolder.class).as(Selectable.class).selector().select("Tree");
        Wrap<? extends Tree> tree = shell.lookup(Tree.class).wrap();
        Selectable<String> rows = tree.as(Selectable.class, String.class);
        rows.selector().select("1");
        tree.waitProperty(Selectable.STATE_PROP_NAME, "1");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        rows.selector().select("11");
        tree.waitProperty(Selectable.STATE_PROP_NAME, "11");
        rows.selector().select("0");
        tree.waitProperty(Selectable.STATE_PROP_NAME, "0");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        rows.selector().select("01");
        tree.waitProperty(Selectable.STATE_PROP_NAME, "01");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        rows.selector().select("010");
        tree.waitProperty(Selectable.STATE_PROP_NAME, "010");
    }

}

