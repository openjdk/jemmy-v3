/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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

package org.jemmy.browser;

import org.jemmy.Point;
import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.swing.JFrame;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;

import static org.testng.Assert.fail;


/**
 *
 * @author shura
 */
public class PropPanelTest {

    public PropPanelTest() {
    }

    static Wrap<? extends JFrame> frm;
    static PropertiesPanel panel;
    static TableModel table;

    @BeforeClass
    public static void setUpClass() throws Exception {
        JFrame frame = new JFrame("test frame");
        frame.setVisible(true);
        frame.setSize(400, 600);
        frame.getContentPane().setLayout(new BorderLayout());
        panel = new PropertiesPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frm = new TestWrap(Environment.getEnvironment(), frame);
        panel.setWrap(frm);
        table = panel.model;
    }
    @DataProvider(name = "testcases")
    public Object[][] testcases() {
        return new Object[][] {
                {"bounds", Rectangle.class, null},
                {"control", JFrame.class, frm.getControl().toString()},
                {"clickPoint", Point.class, null},
                {"wrapper.class", Class.class, TestWrap.class.toString()},
                {"getTitle", String.class, "test frame"},
                {"control.class", Class.class, JFrame.class.toString()},
                {"isShowing", Boolean.class, "true"}
        };
    }

    @Test(dataProvider = "testcases")
    public void checkTable(String name, Class cls, String expectedValue) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if(table.getValueAt(i, 0).equals(name)) {
                if(!table.getValueAt(i, 1).equals(cls.getName())) {
                    fail("Wrong class for " + name + ": " + table.getValueAt(i, 1) + " (expected " + cls.getName() + ")");
                }
                if(expectedValue != null) {
                    if(!table.getValueAt(i, 2).equals(expectedValue)) {
                        fail("Wrong value for " + name + ": " + table.getValueAt(i, 2) + " (expected " + expectedValue + ")");
                    }
                }
                return;
            }
        }
        fail(name + " not found!");
    }

    public static class TestWrap<T extends JFrame> extends Wrap<T> {

        public TestWrap(Environment env, T frame) {
            super(env, frame);
        }

        @Override
        public Rectangle getScreenBounds() {
            return new Rectangle(
                    getControl().getX(),
                    getControl().getY(),
                    getControl().getWidth(),
                    getControl().getHeight());
        }
        @Property("getTitle")
        public String getTitle() {
            return new GetAction<String>() {

                @Override
                public void run(Object... parameters) throws Exception {
                    setResult(getControl().getTitle());
                }
            }.dispatch(getEnvironment());
        }
        @Property("isShowing")
        public boolean isShowing() {
            return new GetAction<Boolean>() {

                @Override
                public void run(Object... parameters) throws Exception {
                    setResult(getControl().isShowing());
                }
            }.dispatch(getEnvironment());
        }
    }
}
