/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.input;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jemmy.JemmyException;
import org.jemmy.Rectangle;
import org.jemmy.env.Environment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


/**
 * TODO: this test is unstable
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class RobotExecutorTest {

//    public RobotExecutorTest() {
//    }
//
//    static File props;
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        props = File.createTempFile("jemmy", "properties");
//        final FileWriter fileWriter = new FileWriter(props);
//        fileWriter.write(AWTRobotInputFactory.OTHER_VM_CONNECTION_TIMEOUT_PROPERTY + "=" + CONNECTION_TIMEOUT);
//        fileWriter.flush();
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//        props.delete();
//    }
//
//    @BeforeMethod
//    public void setUp() {
//    }
//
//    @AfterMethod
//    public void tearDown() {
//    }

//    /**
//     * Test of get method, of class RobotExecutor.
//     */
//    @Test
//    public void testGet() {
//        System.out.println("get");
//        RobotExecutor expResult = null;
//        RobotExecutor result = RobotExecutor.get();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of createScreenCapture method, of class RobotExecutor.
//     */
//    @Test
//    public void testCreateScreenCapture() {
//        System.out.println("createScreenCapture");
//        Rectangle screenRect = null;
//        RobotExecutor instance = new RobotExecutor();
//        Image expResult = null;
//        Image result = instance.createScreenCapture(screenRect);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of makeAnOperation method, of class RobotExecutor.
//     */
//    @Test
//    public void testMakeAnOperation() {
//        System.out.println("makeAnOperation");
//        String method = "";
//        Object[] params = null;
//        Class[] paramClasses = null;
//        RobotExecutor instance = new RobotExecutor();
//        Object expResult = null;
//        Object result = instance.makeAnOperation(method, params, paramClasses);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    final static int CONNECTION_TIMEOUT = 30000;
//    final static int DESTROY_TIMEOUT = 8000;
//
//    /**
//     *
//     * @throws IOException
//     */
//    @Test
//    public void testOtherVMConnectionTimout() throws IOException, InterruptedException {
//        System.out.println("testOtherVMConnectionTimout");
//        String ROBOT_TIMEOUT = "123000";
//        Object prevValue = Environment.getEnvironment().setProperty(AWTRobotInputFactory.OTHER_VM_CONNECTION_TIMEOUT_PROPERTY, ROBOT_TIMEOUT);
//        RobotExecutor re = RobotExecutor.get();
//        re.setRunInOtherJVM(true);
//        String timeout = (String)re.getProperty(AWTRobotInputFactory.OTHER_VM_CONNECTION_TIMEOUT_PROPERTY);
//        re.exit();
//        Thread.sleep(DESTROY_TIMEOUT);
//        Environment.getEnvironment().setProperty(AWTRobotInputFactory.OTHER_VM_CONNECTION_TIMEOUT_PROPERTY, prevValue);
//        assertEquals(ROBOT_TIMEOUT, timeout);
//    }
//
//    /**
//     *
//     * @throws IOException
//     */
//    @Test
//    public void testOtherVMConnectionPort() throws IOException, InterruptedException {
//        System.out.println("testOtherVMJemmyProperties");
//        String PORT = "12300";
//        Object prevValue = Environment.getEnvironment().setProperty(AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY, PORT);
//        RobotExecutor re = RobotExecutor.get();
//        re.setRunInOtherJVM(true);
//        String port = (String)re.getProperty(AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY);
//        re.exit();
//        Thread.sleep(DESTROY_TIMEOUT);
//        Environment.getEnvironment().setProperty(AWTRobotInputFactory.OTHER_VM_PORT_PROPERTY, prevValue);
//        assertEquals(PORT, port);
//    }
//
//    /**
//     * Test of exit method, of class RobotExecutor.
//     */
//    @Test
//    public void testExit() {
//        System.out.println("exit");
//        Process pp = null;
//        try {
//            ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), "-D" + Environment.JEMMY_PROPERTIES_FILE_PROPERTY + "=" + props.getCanonicalPath(), RobotExecutor.class.getName());
//            pb.redirectErrorStream(true);
//            final Process p = pb.start();
//            pp = p;
//            new Thread() {
//
//                @Override
//                public void run() {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    while (true) {
//                        try {
//                            String line = br.readLine();
//                            if (line == null) {
//                                break;
//                            }
//                            System.out.println("SERVER: " + line);
//                        } catch (IOException ex) {
//                            throw new JemmyException("Exception during other JVM output processing", ex);
//                        }
//                    }
//                }
//            }.start();
//            RobotExecutor re = RobotExecutor.get();
//            re.setRunInOtherJVM(true);
//            re.exit();
//            final boolean [] result = new boolean[] { false };
//            synchronized (result) {
//                new Thread() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            p.waitFor();
//                            synchronized (result) {
//                                result[0] = true;
//                                result.notify();
//                            }
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(RobotExecutorTest.class.getName()).log(Level.SEVERE, null, ex);
//                            synchronized (result) {
//                                result[0] = false;
//                                result.notify();
//                            }
//                        }
//                    }
//
//                }.start();
//                try {
//                    result.wait(DESTROY_TIMEOUT * 2);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(RobotExecutorTest.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                assertTrue("Server process doesn't finish", result[0]);
//            }
//        } catch (IOException ex) {
//            throw new JemmyException("Failed to start other JVM", ex);
//        } finally {
//            if (pp != null) {
//                pp.destroy();
//                try {
//                    Thread.sleep(DESTROY_TIMEOUT);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(RobotExecutorTest.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//
//    /**
//     * Test of main method, of class RobotExecutor.
//     */
//    @Test
//    public void testMain() throws InterruptedException {
//        System.out.println("main");
//        try {
//            final boolean [] result = new boolean[] { false };
//            long start = System.currentTimeMillis();
//            ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), "-D" + Environment.JEMMY_PROPERTIES_FILE_PROPERTY + "=" + props.getCanonicalPath(), RobotExecutor.class.getName());
//            pb.redirectErrorStream(true);
//            final Process p = pb.start();
//            synchronized(result) {
//                new Thread() {
//
//                    @Override
//                    public void run() {
//                        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                        while (true) {
//                            try {
//                                String line = br.readLine();
//                                if (line == null) {
//                                    break;
//                                }
//                                System.out.println("SERVER: " + line);
//                                if (line.startsWith("Exiting server as there is no connection for")) {
//                                    synchronized (result) {
//                                        result[0] = true;
//                                        result.notify();
//                                    }
//                                }
//                            } catch (IOException ex) {
//                                throw new JemmyException("Exception during other JVM output processing", ex);
//                            }
//                        }
//                    }
//                }.start();
//                result.wait((int)(CONNECTION_TIMEOUT * 1.1));
//                long end = System.currentTimeMillis();
//                long time = end - start;
//                p.destroy();
//                try {
//                    Thread.sleep(DESTROY_TIMEOUT);
//                } catch (InterruptedException interruptedException) {
//                    Logger.getLogger(RobotExecutorTest.class.getName()).log(Level.SEVERE, null, interruptedException);
//                }
//                if (Math.abs(time - CONNECTION_TIMEOUT) > CONNECTION_TIMEOUT * 0.3) {
//                    fail("Application finished with time (" + time + ") more than 30% different from timeout (" + CONNECTION_TIMEOUT + ")");
//                }
//            }
//        } catch (IOException ex) {
//            throw new JemmyException("Failed to start other JVM", ex);
//        }
//    }
//
//    /**
//     * Test of main method, of class RobotExecutor.
//     */
//    @Test
//    public void testConnectToAlreadyRunningServer() throws InterruptedException {
//        System.out.println("ConnectToAlreadyRunningServer");
//        Process pp = null;
//        try {
//            ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), "-D" + Environment.JEMMY_PROPERTIES_FILE_PROPERTY + "=" + props.getCanonicalPath(), RobotExecutor.class.getName());
//            pb.redirectErrorStream(true);
//            final Process p = pb.start();
//            pp = p;
//            new Thread() {
//
//                @Override
//                public void run() {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    while (true) {
//                        try {
//                            String line = br.readLine();
//                            if (line == null) {
//                                break;
//                            }
//                            System.out.println("SERVER: " + line);
//                        } catch (IOException ex) {
//                            throw new JemmyException("Exception during other JVM output processing", ex);
//                        }
//                    }
//                }
//            }.start();
//            RobotExecutor re = RobotExecutor.get();
//            re.setRunInOtherJVM(true);
//            re.createScreenCapture(new Rectangle(0, 0, 10, 10));
//            re.exit();
//        } catch (IOException ex) {
//            throw new JemmyException("Failed to start other JVM", ex);
//        } finally {
//            if (pp != null) {
//                pp.destroy();
//                Thread.sleep(DESTROY_TIMEOUT);
//            }
//        }
//    }
//
//    /**
//     * Test of synchronizeRobot method, of class RobotExecutor.
//     */
//    @Test
//    public void testSynchronizeRobot() {
//        System.out.println("synchronizeRobot");
//        RobotExecutor instance = new RobotExecutor();
//        instance.synchronizeRobot();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setAutoDelay method, of class RobotExecutor.
//     */
//    @Test
//    public void testSetAutoDelay() {
//        System.out.println("setAutoDelay");
//        Timeout autoDelay = null;
//        RobotExecutor instance = new RobotExecutor();
//        instance.setAutoDelay(autoDelay);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isRunInOtherJVM method, of class RobotExecutor.
//     */
//    @Test
//    public void testIsRunInSeparateJVM() {
//        System.out.println("isRunInOtherJVM");
//        RobotExecutor instance = new RobotExecutor();
//        boolean expResult = false;
//        boolean result = instance.isRunInOtherJVM();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRunInOtherJVM method, of class RobotExecutor.
//     */
//    @Test
//    public void testSetRunInSeparateJVM() {
//        System.out.println("setRunInOtherJVM");
//        boolean runInSeparateJVM = false;
//        RobotExecutor instance = new RobotExecutor();
//        instance.setRunInOtherJVM(runInSeparateJVM);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
