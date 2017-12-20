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
package org.jemmy.input;


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.image.AWTImage;
import org.jemmy.timing.State;
import org.jemmy.timing.Waiter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


/**
 *
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class SmoothMoveTest {

    final static Timeout TIMEOUT = new Timeout("Wait for state to be reached", 10000);
    final static Timeout DELTA_TIMEOUT = new Timeout("Delta timeout of wait for state to be reached", 1000);

    public SmoothMoveTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        File workdir = new File(System.getProperty("user.dir") + File.separator +
                "build" + File.separator +
                "test" + File.separator +
                "results");
        workdir.mkdirs();
        AWTImage.setImageRoot(workdir);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        RobotDriver.exit();
    }

    Frame frm;
    Button btn;
    Wrap<Object> area;
    private volatile boolean mousePressed;
    private volatile boolean mouseReleased;
    private volatile boolean mouseMoved;
    private volatile boolean mouseClicked;
    private volatile boolean mouseDragged;
    private volatile boolean keyPressed;
    private volatile boolean keyReleased;
    RobotDriver instance;
    Robot rb;

    @BeforeMethod
    public void setUp() throws InterruptedException, AWTException, InvocationTargetException {

        Environment.getEnvironment().setProperty(AWTRobotInputFactory.ROBOT_MOUSE_SMOOTHNESS_PROPERTY, "5");

        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                frm = new Frame("some frame");
                frm.setSize(100, 100);
                frm.setLocation(100, 100);
                btn = new Button("some button");
                MouseAdapter m = new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        System.out.println("mousePressed event triggered: " + e);
                        System.out.flush();
                        if ((e.getButton() & MouseEvent.BUTTON1) != 0 && e.isShiftDown()) {
                            mousePressed = true;
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        System.out.println("mouseReleased event triggered: " + e);
                        System.out.flush();
                        if ((e.getButton() & MouseEvent.BUTTON2) != 0 && e.isControlDown()) {
                            mouseReleased = true;
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        System.out.println("mouseMoved event triggered: " + e);
                        System.out.flush();
                        mouseMoved = true;
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("mouseClicked event triggered: " + e);
                        System.out.flush();
                        if ((e.getButton() & MouseEvent.BUTTON3) != 0 && e.isAltDown()) {
                            mouseClicked = true;
                        }
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        System.out.println("mouseDragged event triggered: " + e);
                        System.out.flush();
                        if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
                            mouseDragged = true;
                        }
                    }

                };
                btn.addMouseListener(m);
                btn.addMouseMotionListener(m);
                btn.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        System.out.println("keyPressed event triggered: " + e);
                        System.out.flush();
                        if (e.getKeyCode() == KeyEvent.VK_A && e.isShiftDown()) {
                            keyPressed = true;
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        System.out.println("keyReleased event triggered: " + e);
                        System.out.flush();
                        if (e.getKeyCode() == KeyEvent.VK_B) {
                            keyReleased = true;
                        }
                    }

                });
                frm.add(btn, BorderLayout.SOUTH);
                frm.doLayout();
                instance = new RobotDriver(Environment.getEnvironment());
                frm.setVisible(true);
                btn.requestFocusInWindow();
            }
        });

        rb = new Robot();
        rb.waitForIdle();

        RobotExecutor.get().setRunInOtherJVM(false);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                frm.setVisible(false);
            }
        });
    }

    /**
     * Test of moveMouse method in right-down direction of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMousePP() throws InterruptedException {
        System.out.println("testMoveSmoothMousePP");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y - 10);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y + btn.getHeight() + 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method in left-down direction of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMouseNP() throws InterruptedException {
        System.out.println("testMoveSmoothMouseNP");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y - 10);
        Point endPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y + btn.getHeight() + 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method in left-up direction of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMouseNN() throws InterruptedException {
        System.out.println("testMoveSmoothMouseNN");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y + btn.getHeight() + 10);
        Point endPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y - 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method in right-up direction of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMousePN() throws InterruptedException {
        System.out.println("testMoveSmoothMousePN");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y + btn.getHeight() + 10);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y - 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method along X axis of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMouseX() throws InterruptedException {
        System.out.println("testMoveSmoothMouseX");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x - 10,
                locationOnScreen.y + btn.getHeight() / 2);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth() + 10,
                locationOnScreen.y + btn.getHeight() / 2);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method along Y axis of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMouseY() throws InterruptedException {
        System.out.println("testMoveSmoothMouseY");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() / 2,
                locationOnScreen.y - 10);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth() / 2,
                locationOnScreen.y + btn.getHeight() + 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method with smooth set to false, of class RobotDriver.
     */
    @Test
    public void testMoveSmoothMouseDifferentDrivers() throws InterruptedException {
        System.out.println("testMoveSmoothMouseDifferentDrivers");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y - 10);
        Point endPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y + btn.getHeight() + 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance = new RobotDriver(Environment.getEnvironment());
        instance.moveMouse(endPoint);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true : null;
            }

        });
        assertTrue(mouseMoved);
    }

}