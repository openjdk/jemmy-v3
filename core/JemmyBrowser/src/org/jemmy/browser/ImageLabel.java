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

package org.jemmy.browser;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import org.jemmy.Rectangle;

/**
 *
 * @author shura
 */
public class ImageLabel extends JLabel {

    private org.jemmy.Rectangle bounds = new org.jemmy.Rectangle(100, 100, 100, 100);

    public ImageLabel() {
        super();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
        repaint();
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs);
        grphcs.setColor(Color.red);
        if(bounds != null) {
            grphcs.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);
        }
    }

}