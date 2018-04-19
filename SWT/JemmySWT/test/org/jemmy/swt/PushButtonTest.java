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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.jemmy.control.Wrap;
import org.jemmy.input.SelectionText;
import org.jemmy.interfaces.Parent;
import org.jemmy.interfaces.Selectable;
import org.jemmy.swt.lookup.ByName;
import org.jemmy.swt.lookup.CoordinateLookup;
import org.jemmy.swt.lookup.ByTextTextLookup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/**
 *
 * @author shura
 */
public class PushButtonTest {

    private static Parent<Control> shell;
    private static Parent<Control> buttonsParent;

    public PushButtonTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        new Thread(new Runnable() {

            public void run() {
                Items.main(null);
            }
        }).start();
        Thread.sleep(2000);

        shell = Shells.SHELLS.lookup().as(Parent.class, Control.class);
        shell.lookup(TabFolder.class).as(Selectable.class).selector().select("Buttons");
        buttonsParent = shell.lookup(Composite.class, new ByName<Composite>("buttonsParent")).as(Parent.class, Control.class);
   }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() throws InterruptedException {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void push() throws InterruptedException {
        Wrap<? extends Text> field = buttonsParent.
                lookup(Text.class, new ByTextTextLookup<Text>("please")).
                wrap();
        buttonsParent.lookup().
                lookup(Button.class).
                wrap().mouse().click();
        field.getEnvironment().setTimeout(Wrap.WAIT_STATE_TIMEOUT, 20000);
        field.waitProperty(Wrap.TEXT_PROP_NAME, "Now type some new text, please");
    }

    @Test
    public void type() throws InterruptedException {
        Wrap<? extends Text> field = buttonsParent.
                lookup(Text.class, new ByTextTextLookup<Text>("please")).
                wrap();
        SelectionText text = field.as(SelectionText.class);
        text.clear();
        text.type("Now press the field");
        text.select("field");
        text.type("button again, please");
        assertTrue(field.as(org.jemmy.interfaces.Text.class).text().
                contains("again"));
    }

    @Test
    public void list() throws InterruptedException {
        Wrap<? extends List> list = buttonsParent.
                lookup(List.class).wrap();
        assertEquals(list.as(Selectable.class).getStates().size(), 4);
        list.as(Selectable.class).selector().select("three");
        assertEquals(list.as(Selectable.class).getState(), "three");
    }

    @Test
    public void combo() throws InterruptedException {
        Wrap<? extends List> list = buttonsParent.
                lookup(List.class).wrap();
        assertEquals(list.as(Selectable.class).getStates().size(), 4);
        list.as(Selectable.class).selector().select("three");
        assertEquals(list.as(Selectable.class).getState(), "three");
    }

    @Test
    public void coordinatesLookup() throws InterruptedException {
        Wrap<? extends Button> btt = buttonsParent.lookup(Button.class).wrap();
        //find text field to the left
        buttonsParent.lookup(Text.class, new CoordinateLookup<Text>(btt, true, -1, 0)).wait(1);
        //find combo to the right
        buttonsParent.lookup(Combo.class, new CoordinateLookup<Combo>(btt, false , 1, 0)).wait(1);
        //find list
        buttonsParent.lookup(List.class, new CoordinateLookup<List>(btt, true, 2, 0)).wait(1);
    }
}

