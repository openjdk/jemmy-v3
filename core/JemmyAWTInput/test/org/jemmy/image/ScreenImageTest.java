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
import org.jemmy.TimeoutExpiredException;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.env.Timeout;
import org.jemmy.operators.AWTScreen;
import org.jemmy.operators.Screen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;

/**
 *
 * @author shura
 */
public class ScreenImageTest {
    public static final String GOLDEN = "golden.png";

    public ScreenImageTest() {
    }

    static FilesystemImageLoader loader;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Environment.getEnvironment().setImageCapturer(new AWTRobotCapturer());
        File workdir = new File(System.getProperty("user.dir") + File.separator +
                "build" + File.separator +
                "test" + File.separator +
                "results");
        workdir.mkdirs();
        loader = new FilesystemImageLoader();
        loader.setImageRoot(workdir);
        AWTImage.setImageRoot(workdir);
        Screen.setSCREEN(new AWTScreen());
        Screen.SCREEN.getEnvironment().setTimeout(new Timeout(Wrap.WAIT_STATE_TIMEOUT.getName(), 10000));
        System.out.println("Saving data to " + AWTImage.getImageRoot().getAbsolutePath());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() throws IOException {
        Screen.SCREEN.getScreenImage().save(GOLDEN);
    }

    @AfterMethod
    public void tearDown() {
    }

    /**
     * This test usually fails because it compares the whole screen where
     * changes usually happen between screenshots.
     */
    @Test
    public void compareFull() {
        try {
            Screen.SCREEN.waitImage(loader.load(GOLDEN), "positive.png", "positive_diff.png");
        } catch(TimeoutExpiredException e) {
            e.printStackTrace();
            fail("compareFull failed, see positive.png and positive_diff.png for details");
        }
    }

    @Test
    public void compareNegative() throws InterruptedException, InvocationTargetException {
        final Frame frm = new Frame("some frame");
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                frm.setSize(100, 100);
                frm.setVisible(true);
            }
        });
        //note - if you will be running test from netbeans you would also get a difference
        //from JUnit execution progress. Which is fine :)
        try {
            Screen.SCREEN.waitImage(loader.load(GOLDEN), "negative.png", "negative_diff.png");
            fail("compareFull failed, see negative.png and negative_diff.png for details");
        } catch(TimeoutExpiredException e) {
        }
        frm.setVisible(false);
        assertTrue(new File(AWTImage.getImageRoot(), "negative_diff.png").exists());
        AWTImage res = (AWTImage) loader.load("negative.png");
        AWTImage diff = (AWTImage) loader.load("negative_diff.png");
        assertEquals(res.getTheImage().getWidth(), diff.getTheImage().getWidth());
        assertEquals(res.getTheImage().getHeight(), diff.getTheImage().getHeight());
    }

}
