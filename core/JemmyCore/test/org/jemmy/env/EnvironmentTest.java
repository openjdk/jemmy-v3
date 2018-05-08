/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.jemmy.env;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 *
 * @author shura
 */
public class EnvironmentTest {

    /**
     *
     */
    public EnvironmentTest() {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    Environment local;

    /**
     *
     */
    @BeforeMethod
    public void setUp() {
        Environment.getEnvironment().setTimeout(new Timeout("timeout", 1));
        local = new Environment(Environment.getEnvironment());
    }

    /**
     *
     */
    @AfterMethod
    public void tearDown() {
    }

    /**
     * Test of getEnvironment method, of class Environment.
     */
    @Test
    public void testGetEnvironment() {
        assertNotNull(Environment.getEnvironment());
        assertEquals(Environment.getEnvironment().getTimeout("timeout").getValue(), (long)1);
    }

    /**
     * Test of setEnvironmentIfNotSet method, of class Environment.
     */
    @Test
    public void testSetEnvironmentIfNotSet() {
        Environment parent = new Environment();
        parent.setPropertyIfNotSet(Boolean.class, false);
        assertEquals((boolean)parent.getProperty(Boolean.class), false);

        parent.setPropertyIfNotSet(Boolean.class, true);
        assertEquals((boolean)parent.getProperty(Boolean.class), false);

        Environment env = new Environment(parent);
        env.setPropertyIfNotSet(Boolean.class, true);
        assertEquals((boolean)env.getProperty(Boolean.class), false);
    }

    /**
     * Test of getParentEnvironment method, of class Environment.
     */
    @Test
    public void testGetParentEnvironment() {
        assertEquals((long)1, local.getTimeout("timeout").getValue());
    }

    /**
     *
     */
    @Test
    public void testPropByString() {
        Environment.getEnvironment().setProperty("some.property", this);
        assertEquals(local.getProperty("some.property"), this);
    }

    /**
     * Tests Environment.PropertyKey equals() and hashCode() functions.
     */
    @Test
    public void testKeyEqualsAndHashCode() {
        Object value = "value";
        Environment.getEnvironment().setProperty("testKeyEqualsAndHashCode", value);
        assertEquals(Environment.getEnvironment().setProperty("testKeyEqualsAndHashCode", this), value);
        assertEquals(Environment.getEnvironment().getProperty("testKeyEqualsAndHashCode"), this);
    }

    /**
     * Tests {@linkplain Environment#setTimeout(java.lang.String, long)} and
     * {@linkplain Environment#setTimeout(org.jemmy.env.Timeout, long) methods.
     */
    @Test
    public void testSetTimeout() {
        Timeout timeout = new Timeout("testSetTimeout", 100);
        Environment.getEnvironment().setTimeout(timeout.getName(), 200);
        assertEquals(Environment.getEnvironment().getTimeout(timeout.getName()).getValue(), 200l);
        assertEquals(Environment.getEnvironment().setTimeout(timeout, 300).getValue(), 200l);
        assertEquals(Environment.getEnvironment().getTimeout(timeout).getValue(), 300l);
    }

    // TODO: More tests on Environment

/*
    @Test
    public void testSetParentEnvironment() {
        System.out.println("setParentEnvironment");
        Environment parent = null;
        Environment instance = new Environment();
        instance.setParentEnvironment(parent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGet() {
        System.out.println("get");
        Class cls = null;
        Environment instance = new Environment();
        List<T> expResult = null;
        List<T> result = instance.get(cls);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetLookupDriver() {
        System.out.println("setLookupDriver");
        LookupParent driver = null;
        Environment instance = new Environment();
        LookupParent expResult = null;
        LookupParent result = instance.setLookupDriver(driver);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetLookupDriver() {
        System.out.println("getLookupDriver");
        Environment instance = new Environment();
        LookupParent expResult = null;
        LookupParent result = instance.getLookupDriver();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetOutput() {
        System.out.println("setOutput");
        TestOut out = null;
        Environment instance = new Environment();
        TestOut expResult = null;
        TestOut result = instance.setOutput(out);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetOutput() {
        System.out.println("getOutput");
        Environment instance = new Environment();
        TestOut expResult = null;
        TestOut result = instance.getOutput();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetWaiter() {
        System.out.println("getWaiter");
        String timeoutName = "";
        Environment instance = new Environment();
        Waiter expResult = null;
        Waiter result = instance.getWaiter(timeoutName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetTimeout() {
        System.out.println("getTimeout");
        String name = "";
        Environment instance = new Environment();
        Timeout expResult = null;
        Timeout result = instance.getTimeout(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetTimeout() {
        System.out.println("setTimeout");
        Timeout t = null;
        Environment instance = new Environment();
        Timeout expResult = null;
        Timeout result = instance.setTimeout(t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
}

