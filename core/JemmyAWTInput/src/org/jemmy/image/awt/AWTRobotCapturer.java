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


import org.jemmy.Rectangle;
import org.jemmy.control.Wrap;
import org.jemmy.image.Image;
import org.jemmy.image.ImageCapturer;
import org.jemmy.input.awt.RobotDriver;


/**
 * Uses java.awt.Robot to capture the images
 * @author mrkam, shura
 */
public class AWTRobotCapturer implements ImageCapturer {
    static {
        try {
            Class.forName(AWTImage.class.getName());
        } catch (ClassNotFoundException ex) {
        }
    }

    public Image capture(Wrap<?> control, Rectangle area) {
        Rectangle rect = new Rectangle();
        Rectangle bounds = control.getScreenBounds();
        rect.x = bounds.x + area.x;
        rect.y = bounds.y + area.y;
        rect.width = area.width;
        rect.height = area.height;
        return RobotDriver.createScreenCapture(rect);
    }

}
