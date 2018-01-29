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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Parent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author shura
 */
public class ItemsTest {

    public ItemsTest() {
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
    Wrap<? extends Shell> shell;

    @BeforeMethod
    public void setUp() {
        shell = Shells.SHELLS.lookup().wrap();
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void tabs() throws InterruptedException {
        Wrap<? extends TabFolder> tabbedPane = Shells.SHELLS.lookup().
                as(Parent.class, Control.class).lookup(TabFolder.class).wrap();
        Parent<TabItem> prnt = tabbedPane.as(Parent.class, TabItem.class);
        prnt.lookup(new ByTextItem<TabItem>("Table")).wrap().mouse().click();
        prnt.lookup().wrap().mouse().click();
    }

    private <T extends Control> Wrap<? extends T> select(String tab, Class<T> cls) {
        Parent<Control> parent = shell.as(Parent.class, Control.class);
        Wrap<? extends TabFolder> tabbedPane = parent.lookup(TabFolder.class).wrap();
        Parent<TabItem> prnt = tabbedPane.as(Parent.class, TabItem.class);
        prnt.lookup(new ByTextItem<TabItem>(tab)).wrap().mouse().click();
        return parent.lookup(cls).wrap();
    }

    @Test
    public void tool() throws InterruptedException {
        Parent<ToolItem> tb = select("Toolbar", ToolBar.class).as(Parent.class,
                ToolItem.class);
        tb.lookup(new ByTextItem<ToolItem>("1")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "ToolItem 1");
        tb.lookup(new ByTextItem<ToolItem>("2")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "ToolItem 2");
        tb.lookup().wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "ToolItem 0");
    }

    @Test
    public void table() throws InterruptedException {
        Parent<TableItem> tb = select("Table", Table.class).as(Parent.class,
                TableItem.class);
        tb.lookup().wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {A:0}");
        tb.lookup(new ByTextItem<TableItem>("3")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {A:3}");
        tb.lookup(new ByTextItem<TableItem>("6")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {A:6}");
    }

    @Test
    public void tableByItem() throws InterruptedException {
        select("Table", Table.class).as(Parent.class,
                TableItem.class);
        Parent<Control> parent = shell.as(Parent.class, Control.class);
        Wrap<? extends Table> tableB = parent.lookup(Table.class, new ByItemLookup<Table>("B:0")).wrap();
        Parent<TableItem> tb = tableB.as(Parent.class, TableItem.class);

        tb.lookup(new ByTextItem<TableItem>("0")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {B:0}");
        tb.lookup(new ByTextItem<TableItem>("3")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {B:3}");
        tb.lookup(new ByTextItem<TableItem>("6")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TableItem {B:6}");
    }

    @Test
    public void tree() throws InterruptedException {
        Wrap<? extends Tree> tree = select("Tree", Tree.class);
        Parent<TreeItem> tb = tree.as(Parent.class,
                TreeItem.class);
        tb.lookup().wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {0}");
        tb.lookup(new ByTextItem<TreeItem>("1")).wrap().mouse().click(1);
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {1}");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        tb.lookup(new ByTextItem<TreeItem>("11")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {11}");
        tb.lookup(new ByTextItem<TreeItem>("0")).wrap().mouse().click(1);
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {0}");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        tb.lookup(new ByTextItem<TreeItem>("01")).wrap().mouse().click(1);
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {01}");
        if (System.getProperty("os.name").contains("Windows")) {
            tree.keyboard().pushKey(KeyboardButtons.RIGHT);
        } else {
            tree.keyboard().typeChar('+');
        }
        tb.lookup(new ByTextItem<TreeItem>("010")).wrap().mouse().click();
        shell.waitProperty(Wrap.TEXT_PROP_NAME, "TreeItem {010}");
    }

    @Test
    public void columns() throws InterruptedException {
        Parent<TableColumn> tb = select("Table", Table.class).as(Parent.class,
                TableColumn.class);
        tb.lookup().wrap().waitProperty(Wrap.TEXT_PROP_NAME, "0");
        tb.lookup(new ByTextItem<TableColumn>("1")).wait(1);
    }

    @Test
    public void dump() throws InterruptedException {
        shell.as(Parent.class, Control.class).lookup().wait(5).dump(System.out);
    }
}
