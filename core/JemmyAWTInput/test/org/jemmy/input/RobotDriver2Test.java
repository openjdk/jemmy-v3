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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jemmy.Point;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.image.AWTImage;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse.MouseButton;
import org.jemmy.interfaces.Mouse.MouseButtons;
import org.jemmy.timing.State;
import org.jemmy.timing.Waiter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 *
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class RobotDriver2Test {

    final static Timeout TIMEOUT = new Timeout("Wait for state to be reached", 10000);
    final static Timeout DELTA_TIMEOUT = new Timeout("Delta timeout of wait for state to be reached", 1000);

    public RobotDriver2Test() {
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
    RobotDriver instance;
    Robot rb;
    Queue<InputEvent> queue = new ConcurrentLinkedQueue<InputEvent>();
    static Random r = new Random();

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
                        queue.add(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        System.out.println("mouseReleased event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        System.out.println("mouseMoved event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("mouseClicked event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        System.out.println("mouseDragged event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
                    }

                };
                btn.addMouseListener(m);
                btn.addMouseMotionListener(m);
                btn.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        System.out.println("keyPressed event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        System.out.println("keyReleased event triggered: " + e);
                        System.out.flush();
                        queue.add(e);
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
     * Test of all RobotDriver methods invoked multiple times
     * @throws InterruptedException
     */
//    @Test
    public void testAll() throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            testPressMouse();
        }
    }

    public void test() throws InterruptedException {
//        testPressMouse();
        testPressKey();
    }

    @Test
    public void test0() throws InterruptedException {
        test();
    }

    @Test
    public void test1() throws InterruptedException {
        test();
    }

    @Test
    public void test2() throws InterruptedException {
        test();
    }

    @Test
    public void test3() throws InterruptedException {
        test();
    }

    @Test
    public void test4() throws InterruptedException {
        test();
    }

    @Test
    public void test5() throws InterruptedException {
        test();
    }

    @Test
    public void test6() throws InterruptedException {
        test();
    }

    @Test
    public void test7() throws InterruptedException {
        test();
    }

    @Test
    public void test8() throws InterruptedException {
        test();
    }

    @Test
    public void test9() throws InterruptedException {
        test();
    }

    protected static final int[] MODIFIERS = new int[] {
            InputEvent.SHIFT_DOWN_MASK,
            InputEvent.CTRL_DOWN_MASK,
            InputEvent.ALT_DOWN_MASK,
            InputEvent.SHIFT_MASK,
            InputEvent.CTRL_MASK,
            InputEvent.ALT_MASK,
//            MouseEvent.ALT_GRAPH_DOWN_MASK,
//            MouseEvent.META_DOWN_MASK
        };

    public static int getModifiers() {
        int modifiersMask = r.nextInt(1 << MODIFIERS.length/2);
        int m = 0;
        System.out.print("Modifiers:");
        for (int i = 0; i < MODIFIERS.length/2; i++) {
            if ((modifiersMask & (1 << i)) != 0 ) {
                m |= MODIFIERS[i];
                System.out.print(" " + i);
            }
        }
        System.out.println("");
        return m;
    }

    protected static HashMap<Integer, Integer> normalizeMap =  new HashMap<Integer, Integer>();
    static {
        normalizeMap.put(InputEvent.SHIFT_MASK,InputEvent.SHIFT_DOWN_MASK);
        normalizeMap.put(InputEvent.CTRL_MASK,InputEvent.CTRL_DOWN_MASK);
        normalizeMap.put(InputEvent.ALT_MASK,InputEvent.ALT_DOWN_MASK);
        normalizeMap.put(InputEvent.META_MASK,InputEvent.META_DOWN_MASK);
    }

    protected static HashSet<Integer> normalize(HashSet<Integer> modifiers) {
        HashSet<Integer> normalized = new HashSet<Integer>();
        for (Integer mod : modifiers) {
            Integer n = normalizeMap.get(mod);
            if (n != null) {
                normalized.add(n);
            } else {
                normalized.add(mod);
            }
        }
        return normalized;
    }

    protected static HashSet<Integer> modifiers2Set(int mods) {
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < MODIFIERS.length; i++) {
            if ((mods & MODIFIERS[i]) > 0) {
                set.add(MODIFIERS[i]);
            }
        }
        return set;
   }

    protected static boolean compareModifiers(int m1, int m2) {
        return normalize(modifiers2Set(m1)).equals(normalize(modifiers2Set(m2)));
    }
    /**
     * Test of pressMouse method, of class RobotDriver.
     */
    public void testPressMouse() throws InterruptedException {
        System.out.println("pressMouse");
        Thread.sleep(3000);
        final int[] MOUSE_BUTTONS_1 = new int[] {
            MouseEvent.BUTTON1_MASK,
            MouseEvent.BUTTON2_MASK,
            MouseEvent.BUTTON3_MASK
        };
        final int[] MOUSE_BUTTONS_2 = new int[] {
            MouseEvent.BUTTON1,
            MouseEvent.BUTTON2,
            MouseEvent.BUTTON3
        };
        int bIndex = r.nextInt(MOUSE_BUTTONS_1.length);
        final int mouseButton1 = MOUSE_BUTTONS_1[bIndex];
        final int mouseButton2 = MOUSE_BUTTONS_2[bIndex];
        System.out.print("Button: " + bIndex + " Modifier:");
        final int modifiers = getModifiers();
        queue.clear();
        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        System.out.println("Pressing mouse");
        instance.moveMouse(new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2));
        AWTMap map = new AWTMap();
        MouseButton button = map.convertMouseButton(mouseButton1);
        Modifier[] converted_modifiers = map.convertModifiers(modifiers);
        instance.pressMouse(button, converted_modifiers);
        instance.releaseMouse(button, converted_modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                while(true) {
                    InputEvent e = queue.poll();
                    if (e != null) {
                        if (e instanceof MouseEvent) {
                            MouseEvent me = (MouseEvent) e;
                            if (me.getID() == MouseEvent.MOUSE_PRESSED && me.getButton() == mouseButton2 && (compareModifiers(me.getModifiers(), modifiers))) {
                                return true;
                            }
                            if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                                System.out.println("Wrong combination of button and modifiers triggered:");
                                System.out.println("me.getModifiers() = " + Integer.toString(me.getModifiers(), 2)  + ", modifiers = " + Integer.toString(modifiers, 2));
                                System.out.println("expected: " + new MouseEvent(
                                        me.getComponent(), MouseEvent.MOUSE_PRESSED,
                                        me.getWhen(), modifiers, me.getX(), me.getY(), me.getClickCount(), me.isPopupTrigger(), mouseButton2));
                                System.out.println("     got: " + me);
                            }
                        }
                    } else {
                        break;
                    }
                }
                return null;
            }

        });

        System.out.println("PASSED");

    }

//    /**
//     * Test of releaseMouse method, of class RobotDriver.
//     */
//    @Test
//    public void testReleaseMouse() throws InterruptedException {
//        System.out.println("releaseMouse");
//        Thread.sleep(3000);
//        int mouseButton = MouseEvent.BUTTON2_MASK;
//        int modifiers = MouseEvent.CTRL_DOWN_MASK;
//        mouseReleased = false;
//        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
//        instance.moveMouse(new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2));
//        System.out.println("Pressing mouse");
//        instance.pressMouse(mouseButton, modifiers);
//        System.out.println("Releasing mouse");
//        instance.releaseMouse(mouseButton, modifiers);
//
//        rb.waitForIdle();
//        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){
//
//            public Boolean reached() {
//                return mouseReleased ? true: null;
//            }
//
//        });
//        assertTrue(mouseReleased);
//    }
//
//    /**
//     * Test of moveMouse method, of class RobotDriver.
//     */
//    @Test
//    public void testMoveMouse() throws InterruptedException {
//        System.out.println("moveMouse");
//        Thread.sleep(3000);
//        mouseMoved = false;
//        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
//        System.out.println("Moving mouse");
//        Point startPoint = new Point(locationOnScreen.x, locationOnScreen.y);
//        Point endPoint = new Point(locationOnScreen.x + btn.getWidth(), locationOnScreen.y + btn.getHeight());
//        double steps = 5; //Math.max(btn.getWidth(), btn.getHeight());
//        double dx = (endPoint.x - startPoint.x) / steps;
//        double dy = (endPoint.y - startPoint.y) / steps;
//        for(int i = 0; i < steps; i++) {
//            Point point = new Point(startPoint.x + dx * i, startPoint.y + dy * i);
//            instance.moveMouse(point);
//            Thread.sleep(100);
//        }
//
//        rb.waitForIdle();
//        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){
//
//            public Boolean reached() {
//                return mouseMoved ? true: null;
//            }
//
//        });
//        assertTrue(mouseMoved);
//    }
//
//    /**
//     * Test of clickMouse method, of class RobotDriver.
//     */
//    @Test
//    public void testClickMouse() throws InterruptedException {
//        System.out.println("clickMouse");
//        Thread.sleep(3000);
//        mouseClicked = false;
//        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
//        Point point = new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2);
//        int clickCount = 1;
//        int mouseButton = MouseEvent.BUTTON3_MASK;
//        int modifiers = InputEvent.ALT_DOWN_MASK;
//        Timeout mouseClick = new Timeout("mouseClick", 100);
//        System.out.println("Clicking mouse");
//        instance.clickMouse(point, clickCount, mouseButton, modifiers, mouseClick);
//
//        rb.waitForIdle();
//        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){
//
//            public Boolean reached() {
//                return mouseClicked ? true: null;
//            }
//
//        });
//        assertTrue(mouseClicked);
//    }
//
//    /**
//     * Test of dragNDrop method, of class RobotDriver.
//     */
//    @Test
//    public void testDragNDrop() throws InterruptedException {
//        System.out.println("dragNDrop");
//        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
//        Point startPoint = new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2);
//        Point endPoint = new Point(frm.getLocationOnScreen().x + frm.getWidth() / 2, frm.getLocationOnScreen().y + frm.getHeight() / 2);
//        int mouseButton = MouseEvent.BUTTON2_MASK;
//        int modifiers = 0;
//        Timeout before = new Timeout("before", 500);
//        Timeout after = new Timeout("after", 500);
//        mouseDragged = false;
//        instance.dragNDrop(startPoint, endPoint, mouseButton, modifiers, before, after);
//
//        rb.waitForIdle();
//        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){
//
//            public Boolean reached() {
//                return mouseDragged ? true: null;
//            }
//
//        });
//        assertTrue(mouseDragged);
//    }

    /**
     * Test of pressKey method, of class RobotDriver.
     */
    public void testPressKey() throws InterruptedException {
        System.out.println("pressKey");
        Thread.sleep(3000);

        final int keyCode = KeyEvent.VK_A;
        final int modifiers = getModifiers();
        AWTMap map = new AWTMap();
        KeyboardButton button = map.convertKeyboardButton(keyCode);
        Modifier[] converted_modifiers = map.convertModifiers(modifiers);

        queue.clear();

        java.awt.Point locationOnScreen = btn.getLocationOnScreen();
        instance.clickMouse(new Point(locationOnScreen.x + btn.getWidth() / 2, locationOnScreen.y + btn.getHeight() / 2), 1,
                MouseButtons.BUTTON1, DELTA_TIMEOUT);

        rb.waitForIdle();

        instance.pressKey(button, converted_modifiers);
        instance.releaseKey(button, converted_modifiers);

        rb.waitForIdle();
        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){

            public Boolean reached() {
                while(true) {
                    InputEvent e = queue.poll();
                    if (e != null) {
                        if (e instanceof KeyEvent) {
                            KeyEvent ke = (KeyEvent) e;
                            if (ke.getID() == KeyEvent.KEY_PRESSED && ke.getKeyCode() == keyCode && compareModifiers(ke.getModifiers(), modifiers)) {
                                return true;
                            }
                            if (ke.getID() == KeyEvent.KEY_PRESSED) {
                                System.out.println("Wrong combination of button and modifiers triggered:");
                                System.out.println("ke.getModifiers() = " + Integer.toString(ke.getModifiers(), 2)  + ", modifiers = " + Integer.toString(modifiers, 2));
                                System.out.println("expected: " + new KeyEvent(ke.getComponent(), KeyEvent.KEY_PRESSED, ke.getWhen(), modifiers, keyCode, KeyEvent.CHAR_UNDEFINED));
                                System.out.println("     got: " + ke);
                            }
                        }
                    } else {
                        break;
                    }
                }
                return null;
            }

        });

        System.out.println("PASSED");
    }

//    /**
//     * Test of releaseKey method, of class RobotDriver.
//     */
//    @Test
//    public void testReleaseKey() throws InterruptedException {
//        System.out.println("releaseKey");
//        int keyCode = KeyEvent.VK_B;
//        int modifiers = 0;
//        keyReleased = false;
//        instance.pressKey(keyCode, modifiers);
//        instance.releaseKey(keyCode, modifiers);
//
//        rb.waitForIdle();
//        new Waiter(TIMEOUT, DELTA_TIMEOUT).ensureState(new State<Boolean>(){
//
//            public Boolean reached() {
//                return keyReleased ? true: null;
//            }
//
//        });
//        assertTrue(keyReleased);
//    }

}
