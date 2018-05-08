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


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.InputEvent;
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
import org.jemmy.interfaces.Keyboard;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifiers;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse;
import org.jemmy.interfaces.Mouse.MouseButton;
import org.jemmy.interfaces.Mouse.MouseButtons;
import org.jemmy.timing.State;
import org.jemmy.timing.Waiter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


/**
 *
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class RobotDriverTest {

    final static Timeout TIMEOUT = new Timeout("Wait for state to be reached", 10000);
    final static Timeout DELTA_TIMEOUT = new Timeout("Delta timeout of wait for state to be reached", 1000);

    public RobotDriverTest() {
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

        RobotExecutor.get().setRunInOtherJVM(true);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                frm.setVisible(false);
            }
        });
    }

//    /**
//     * Test of createScreenCapture method, of class RobotDriver.
//     */
//    @Test
//    public void testCreateScreenCaptureLocally() throws AWTException, InterruptedException {
//        System.out.println("testCreateScreenCaptureLocally");
//        Thread.sleep(3000);
//        Rectangle screenRect = new Rectangle(100, 100, 100, 100);
//        RobotExecutor.get().setRunInOtherJVM(false);
//        Image expResult = new AWTImage(new Robot().createScreenCapture(new java.awt.Rectangle(100, 100, 100, 100)));
//        Image result = RobotDriver.createScreenCapture(screenRect);
//        Image diff = expResult.compareTo(result);
//        if (diff != null) {
//            diff.save("testCreateScreenCaptureLocally.png");
//            fail();
//        }
//    }
//
//    /**
//     * Test of createScreenCapture method, of class RobotDriver.
//     */
//    @Test
//    public void testCreateScreenCaptureRemotely() throws AWTException, InterruptedException {
//        System.out.println("testCreateScreenCaptureRemotely");
//        Thread.sleep(3000);
//        Rectangle screenRect = new Rectangle(100, 100, 100, 100);
//        RobotExecutor.get().setRunInOtherJVM(true);
//        Image expResult = new AWTImage(new Robot().createScreenCapture(new java.awt.Rectangle(100, 100, 100, 100)));
//        Image result = RobotDriver.createScreenCapture(screenRect);
//        RobotDriver.createScreenCapture(screenRect);
//        Image diff = expResult.compareTo(result);
//        if (diff != null) {
//            diff.save("testCreateScreenCaptureRemotely.png");
//            fail();
//        }
//    }
//
//    /**
//     * Test of createScreenCapture method, of class RobotDriver.
//     */
//    @Test
//    public void testCreateScreenCaptureRemotely2() throws AWTException, InterruptedException {
//        System.out.println("testCreateScreenCaptureRemotely2");
//        Thread.sleep(3000);
//        Rectangle screenRect = new Rectangle(100, 100, 100, 100);
//        RobotExecutor.get().setRunInOtherJVM(true);
//        Image expResult = new AWTImage(new Robot().createScreenCapture(new java.awt.Rectangle(100, 100, 100, 100)));
//        Image result = RobotDriver.createScreenCapture(screenRect);
//        Image diff = expResult.compareTo(result);
//        if (diff != null) {
//            diff.save("testCreateScreenCaptureRemotely2.png");
//            fail();
//        }
//    }

    /**
     * Test of pressMouse method, of class RobotDriver.
     */
    @Test
    public void testPressMouse() throws InterruptedException {
        System.out.println("pressMouse");
        Thread.sleep(3000);
//        new Thread() {
//
//            @Override
//            public void run() {
                MouseButton mouseButton = MouseButtons.BUTTON1;
                Modifier modifiers[] = new Modifier[] {KeyboardModifiers.SHIFT_DOWN_MASK};
                mousePressed = false;
                java.awt.Point locationOnScreen = btn.getLocationOnScreen();
                System.out.println("Pressing mouse");
                instance.moveMouse(new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2));
                instance.pressMouse(mouseButton, modifiers);
                instance.releaseMouse(mouseButton, modifiers);

                rb.waitForIdle();
                new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

                    public Boolean reached() {
                        return mousePressed ? true: null;
                    }

                });

//            }
//
//        }.start();
    }

    /**
     * Test of releaseMouse method, of class RobotDriver.
     */
    @Test
    public void testReleaseMouse() throws InterruptedException {
        System.out.println("releaseMouse");
        Thread.sleep(3000);
        MouseButton mouseButton = MouseButtons.BUTTON2;
        Modifier modifiers[] = new Modifier[] {KeyboardModifiers.CTRL_DOWN_MASK};
        mouseReleased = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        instance.moveMouse(new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2));
        System.out.println("Pressing mouse");
        instance.pressMouse(mouseButton, modifiers);
        System.out.println("Releasing mouse");
        instance.releaseMouse(mouseButton, modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseReleased ? true: null;
            }

        });
        assertTrue(mouseReleased);
    }

    /**
     * Test of moveMouse method, of class RobotDriver.
     */
    @Test
    public void testMoveMouse() throws InterruptedException {
        System.out.println("moveMouse");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x, locationOnScreen.y);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth(), locationOnScreen.y + btn.getHeight());
        double steps = 5; //Math.max(btn.getWidth(), btn.getHeight());
        double dx = (endPoint.x - startPoint.x) / steps;
        double dy = (endPoint.y - startPoint.y) / steps;
        for(int i = 0; i < steps; i++) {
            Point point = new Point(startPoint.x + dx * i, startPoint.y + dy * i);
            instance.moveMouse(point);
            Thread.sleep(100);
        }

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseMoved ? true: null;
            }

        });
        assertTrue(mouseMoved);
    }

    /**
     * Test of moveMouse method with smoothness set Integer.MAX_VALUE
     * of class RobotDriver.
     */
    @Test
    public void testMoveNonSmoothMouse() throws InterruptedException {
        System.out.println("testMoveNonSmoothMouse");
        Thread.sleep(3000);
        mouseMoved = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Moving mouse");
        Point startPoint = new Point(locationOnScreen.x - 10, locationOnScreen.y - 10);
        Point endPoint = new Point(locationOnScreen.x + btn.getWidth() + 10, locationOnScreen.y + btn.getHeight() + 10);
        instance.moveMouse(startPoint);
        Thread.sleep(100);
        instance.moveMouse(endPoint);
        Thread.sleep(2000);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return !mouseMoved ? true : null;
            }

        });
        assertFalse(mouseMoved);
    }

    /**
     * Test of clickMouse method, of class RobotDriver.
     */
    @Test
    public void testClickMouse() throws InterruptedException {
        System.out.println("clickMouse");
        Thread.sleep(3000);
        mouseClicked = false;
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        Point point = new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2);
        int clickCount = 1;
        MouseButton mouseButton = MouseButtons.BUTTON3;
        Modifier modifiers[] = new Modifier[] {KeyboardModifiers.ALT_DOWN_MASK};
        Timeout mouseClick = new Timeout("mouseClick", 100);
        System.out.println("Clicking mouse");
        instance.clickMouse(point, clickCount, mouseButton, mouseClick, modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseClicked ? true: null;
            }

        });
        assertTrue(mouseClicked);
    }

    /**
     * Test of dragNDrop method, of class RobotDriver.
     */
    @Test
    public void testDragNDrop() throws InterruptedException {
        System.out.println("dragNDrop");
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2);
        Point endPoint = new Point(frm.getLocationOnScreen().x + frm.getWidth() / 2, frm.getLocationOnScreen().y + frm.getHeight() / 2);
        MouseButton mouseButton = MouseButtons.BUTTON2;
        Modifier modifiers[] = new Modifier[] {};
        Timeout before = new Timeout("before", 500);
        Timeout after = new Timeout("after", 500);
        mouseDragged = false;
        instance.dragNDrop(startPoint, endPoint, mouseButton, modifiers, before, after);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return mouseDragged ? true: null;
            }

        });
        assertTrue(mouseDragged);
    }

    /**
     * Test of pressKey method, of class RobotDriver.
     */
    @Test
    public void testPressKey() throws InterruptedException {
        System.out.println("pressKey");
        KeyboardButton kbdButton = KeyboardButtons.A;
        Modifier modifiers[] = new Modifier[] {KeyboardModifiers.SHIFT_DOWN_MASK};
        keyPressed = false;
        instance.pressKey(kbdButton, modifiers);
        instance.releaseKey(kbdButton, modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return keyPressed ? true: null;
            }

        });
        assertTrue(keyPressed);
    }

    /**
     * Test of releaseKey method, of class RobotDriver.
     */
    @Test
    public void testReleaseKey() throws InterruptedException {
        System.out.println("releaseKey");
        KeyboardButton kbdButton = KeyboardButtons.B;
        Modifier modifiers[] = new Modifier[] {};
        keyReleased = false;
        instance.pressKey(kbdButton, modifiers);
        instance.releaseKey(kbdButton, modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                return keyReleased ? true: null;
            }

        });
        assertTrue(keyReleased);
    }

}
