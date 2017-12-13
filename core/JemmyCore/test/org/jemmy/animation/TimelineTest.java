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
package org.jemmy.animation;

import org.jemmy.env.Environment;
import org.jemmy.timing.TimedCriteria;
import org.jemmy.timing.Timeline;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


/**
 *
 * @author shura
 */
public class TimelineTest {

    public TimelineTest() {
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
    public void single() {
        TestFrame one = new TestFrame(1000, 0) {};
        new Timeline(Environment.getEnvironment(), null).schedule(1000, one).start();
    }

    @Test
    public void multiple() {
        TestFrame many = new TestFrame(1000, 1000) {};
        new Timeline(Environment.getEnvironment(), null).schedule(1000, 10000, 1000, many).start();
    }

    @Test
    public void delayed() {
        TestFrame one = new TestFrame(1000, 1000) {};
        long before = System.currentTimeMillis();
        System.out.println("Before: " + before);
        new Timeline(1000, Environment.getEnvironment(), null).schedule(1000, one).start();
        assertTrue(one.actualTime >= before + 2000);
    }

    private static class TestFrame implements TimedCriteria<Long> {

        private int count = 0;
        private long start = 0;
        private long interval = 0;
        private long actualTime = 0;

        public TestFrame(long start, long interval) {
            this.start = start;
            this.interval = interval;
        }

        public boolean check(Long t, long when) {
            actualTime = System.currentTimeMillis();
            System.out.println(count + "'s executed on " + when + ", " + actualTime + " actual");
            assertTrue(when >= start + count*interval);
            assertTrue(when <= start + count*interval + 20);
            count++;
            return true;
        }
    }
}
