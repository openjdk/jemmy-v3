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
package org.jemmy.env;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.jemmy.control.Wrap;
import org.jemmy.timing.Waiter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 * @author shura
 */
public class LoadTimeoutsTest {

    private static final String WEIRD_NAME = "weird.timeout.name";
    private static final long DELTA_VALUE = 333;
    private static final long WAIT_VALUE = 444;
    private static final long WEIRD_VALUE = 555;

    public LoadTimeoutsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        createFiles(false);
    }
    public static void createFiles(boolean relative) throws IOException {
        File timeouts = File.createTempFile("timeouts", "timeouts");
        PrintWriter out = new PrintWriter(new FileWriter(timeouts));
        out.println("default.wait.delta=" + DELTA_VALUE);
        out.println("wait.state=" + WAIT_VALUE);
        out.println(WEIRD_NAME + "=" + WEIRD_VALUE);
        out.close();
        File properties = File.createTempFile("properties", "properties");
        out = new PrintWriter(new FileWriter(properties));
        out.println("timeouts=" + (relative ? timeouts.getName() : timeouts.getAbsolutePath()));
        System.out.println("timeouts=" + (relative ? timeouts.getName() : timeouts.getAbsolutePath()));
        out.close();
//        System.setProperty("jemmy.properties", properties.getAbsolutePath());
//        System.out.println("jemmy.properties=" + properties.getAbsolutePath());
        Environment.getEnvironment().loadProperties(properties.getAbsolutePath());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test()
    public void testTimeouts() {
        assertEquals(Environment.getEnvironment().getTimeout(WEIRD_NAME).getValue(), WEIRD_VALUE);
        assertEquals(Environment.getEnvironment().getTimeout(Waiter.DEFAULT_DELTA).getValue(), DELTA_VALUE);
        assertEquals(Environment.getEnvironment().getTimeout(Wrap.WAIT_STATE_TIMEOUT).getValue(), WAIT_VALUE);
    }

}
