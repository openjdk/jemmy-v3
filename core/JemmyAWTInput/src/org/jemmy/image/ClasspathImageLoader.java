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


import org.jemmy.env.Environment;


/**
 * ImageLoader implementation which is able to load images through
 * a given classloader.
 * @author mrkam, shura
 */
public class ClasspathImageLoader implements ImageLoader {

    private String packagePrefix = "";
    private ClassLoader classLoader = getClassLoader();

    public static final String OUTPUT = AWTImage.class.getName() + ".OUTPUT";

    /**
     * Get the value of classLoader which is used to load images.
     *
     * @return the value of classLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * {@inheritDoc}
     */
    public Image load(String ID) {
        String fullId = ((packagePrefix != null) ? packagePrefix : "") + ID;
        Environment.getEnvironment().getOutput(ClasspathImageLoader.OUTPUT).println("Image loaded from " + fullId + " by " + classLoader);
        return new AWTImage(PNGDecoder.decode(classLoader, fullId));
    }

    /**
     * Set the value of classLoader
     *
     * @param classLoader new value of classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * TODO: Add JavaDoc
     * @param rootPackage
     */
    public void setRootPackage(Package rootPackage) {
        if (rootPackage != null) {
            this.packagePrefix = rootPackage.getName().replace('.', '/') + "/";
        } else {
            this.packagePrefix = null;
        }
    }

}
