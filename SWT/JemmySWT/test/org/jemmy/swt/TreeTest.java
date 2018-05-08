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


import java.io.FileNotFoundException;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jemmy.control.Wrap;
import org.jemmy.input.StringTree;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author shura
 */
public class TreeTest {

    public TreeTest() {
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
    String[][] trees = new String[][]{
        {"zero", "zeroone", "zeroonezero"},
        {"1", "TreeItem 10"},
        {"TreeItem 0", "zeroone", "TreeItem 010"},
        {"1", "10"},
        {"0", "00"},
        {"1", "11"},
        {"0", "01", "010"},
        {"0", "01"},
        {"one"}
    };

    @Test
    public void tree() throws InterruptedException, FileNotFoundException {
        shell.lookup(TabFolder.class).as(Selectable.class).selector().select("Tree");
        final Wrap<? extends Tree> tree = shell.lookup(Tree.class).wrap();
        for (String[] path : trees) {
            tree.as(StringTree.class, TreeItem.class).select(path);
        }
        Thread.sleep(1000);
    }

}
