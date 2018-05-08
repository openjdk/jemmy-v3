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


import java.io.File;
import org.jemmy.env.Environment;


/**
 * This is an implementation of ImageLoader which loads images from filesystem.
 * @author mrkam
 */
public class FilesystemImageLoader implements ImageLoader {

    private File imageRoot = null;

    public static final String OUTPUT = AWTImage.class.getName() + ".OUTPUT";

    public File getImageRoot() {
        return imageRoot;
    }

    public Image load(String ID) {
        String fullPath = ID + (ID.toLowerCase().endsWith(AWTImage.PNG_FILE) ? "" :
                AWTImage.PNG_FILE);
        if (imageRoot != null) {
            fullPath = imageRoot.getAbsolutePath() + File.separator + ID;
        }
        Environment.getEnvironment().getOutput(FilesystemImageLoader.OUTPUT).println("Image loaded from " + fullPath);
        return new AWTImage(PNGDecoder.decode(fullPath));
    }

    public void setImageRoot(File imageRoot) {
        this.imageRoot = imageRoot;
    }

}
