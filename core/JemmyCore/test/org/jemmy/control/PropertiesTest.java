/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.control;

import java.util.Map;
import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.env.Environment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


/**
 *
 * @author shura
 */
public class PropertiesTest {

    public PropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testNoException() {
        Wrap<?> wrap = new TestWrap(false);
        Map props = wrap.getProperties();
        assertEquals(props.get("x"), 10);
        assertEquals(props.get("X"), 10);
        assertEquals(props.get("getX"), 10.);
        props = wrap.getPropertiesQiuet();
        assertEquals(props.get("x"), 10);
        assertEquals(props.get("X"), 10);
        assertEquals(props.get("getX"), 10.);
    }

    @Test
    public void testException() {
        Wrap<?> wrap = new TestWrap(true);
        try {
            wrap.getProperties();
            fail("No exception thrown");
        } catch (RuntimeException e) {
            Map props = wrap.getPropertiesQiuet();
            assertEquals(props.get("x"), 10);
            assertEquals(props.get("getX"), 10.);
        }
    }

    @MethodProperties("getX")
    @FieldProperties("x")
    class TestWrap extends Wrap<Rectangle> {

        boolean throwException;

        public TestWrap(boolean throwException) {
            super(Environment.getEnvironment(), new Rectangle(10, 20, 30, 40));
            this.throwException = throwException;
        }

        @Property("X")
        public int getX() {
            if (throwException) {
                throw new RuntimeException("Exception while getting a property");
            }
            return new GetAction<Integer>() {

                @Override
                public void run(Object... parameters) throws Exception {
                    setResult(getControl().x);
                }
            }.dispatch(getEnvironment());
        }

        @Override
        public Rectangle getScreenBounds() {
            return getControl();
        }
    }
}
