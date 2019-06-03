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
package org.jemmy.image.awt;

import java.io.*;
import org.jemmy.Rectangle;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.image.awt.AWTImage;
import org.jemmy.image.awt.AWTRobotCapturer;
import org.jemmy.image.awt.PNGImageLoader;
import org.jemmy.image.pixel.PNGSaver;
import org.jemmy.operators.ScreenRectangle;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;

/**
 *
 * @author shura
 */
public class SaveLoadTest {

    public SaveLoadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeClass
    public void setUp() {
    }

    @AfterClass
    public void tearDown() {
    }

    @Test
    public void hello() throws FileNotFoundException, IOException {
        Wrap<?> wrap = new ScreenRectangle(Environment.getEnvironment(), new Rectangle(0, 0, 220, 220));
        AWTImage img = (AWTImage) new AWTRobotCapturer().capture(wrap, new Rectangle(0, 0, 200, 200));
        File imgFile = new File(System.getProperty("user.dir") + File.separator + "out.png");
        new PNGSaver(new FileOutputStream(imgFile), PNGSaver.COLOR_MODE).encode(img);
        AWTImage loaded = new AWTImage(new PNGImageLoader().load(new FileInputStream(imgFile)));
        new PNGSaver(new FileOutputStream(new File(System.getProperty("user.dir") + File.separator + "loaded.png")), PNGSaver.COLOR_MODE).encode(loaded);
        AWTImage diff = (AWTImage) img.compareTo(loaded);
        if(diff != null) {
            new PNGSaver(new FileOutputStream(new File(System.getProperty("user.dir") + File.separator + "diff.png")), PNGSaver.COLOR_MODE).encode(diff);
        }
        assertNull(diff);
    }
}
