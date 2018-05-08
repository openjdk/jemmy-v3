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
package org.jemmy.image;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jemmy.Rectangle;
import org.jemmy.TimeoutExpiredException;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.operators.AWTScreen;
import org.jemmy.operators.Screen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 *
 * @author shura
 */
public class ScreenAreaImageTest {

    public ScreenAreaImageTest() {
    }

    static ImageLoader loader;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Screen.setSCREEN(new AWTScreen());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    File tmpDump = null;
    File tmpDumpPart = null;
    Wrap<Object> area;
    Frame frm;
    Rectangle part = new Rectangle(10, 10, 80, 80);

    @BeforeMethod
    public void setUp() throws IOException, InterruptedException {
        Environment.getEnvironment().setImageCapturer(new AWTRobotCapturer());
        loader = new FilesystemImageLoader();
        frm = new Frame("some frame");
        frm.setVisible(true);
        frm.setSize(100, 100);
        frm.setLocation(100, 100);
        area = new Wrap<Object>(Environment.getEnvironment(), "screen area") {

            @Override
            public Rectangle getScreenBounds() {
                return new Rectangle(100, 100, 100, 100);
            }
        };
        // Added timeout to prevent failures due to Windows Vista Visual Effects
        Thread.sleep(1000);
        tmpDump = File.createTempFile("screen", ".png");
        tmpDumpPart = File.createTempFile("screenPart", ".png");
        area.getScreenImage().save(tmpDump.getAbsolutePath());
        area.getScreenImage(part).save(tmpDumpPart.getAbsolutePath());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScreenImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterMethod
    public void tearDown() {
        frm.setVisible(false);
    }

    @Test
    public void compareFull() {
        try {
            area.waitImage(loader.load(tmpDump.getAbsolutePath()), null, null);
        } catch(TimeoutExpiredException e) {
            fail(e.getLocalizedMessage());
            throw e;
        }
    }

    @Test
    public void comparePart() {
        try {
            area.waitImage(loader.load(tmpDumpPart.getAbsolutePath()), part, null, null);
        } catch(TimeoutExpiredException e) {
            fail(e.getLocalizedMessage());
            throw e;
        }
    }

    @Test
    public void compareNegative() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                frm.setVisible(false);
            }
        });
        Thread.sleep(100);
        try {
            area.waitImage(loader.load(tmpDump.getAbsolutePath()), null, null);
            // TODO: Test unstable. Sometimes passes
            fail();
        } catch(TimeoutExpiredException e) {
        }
    }
}
