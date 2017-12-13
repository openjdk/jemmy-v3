/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.jemmy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author shura
 */
public class Version {
    /**
     *
     */
    public static final Version VERSION = new Version();

    private int major;
    private int minor;
    private int mini;
    private String build;

    /**
     *
     */
    public Version() {
        this(Version.class.getPackage().getName());
    }

    /**
     *
     * @param pkg
     */
    public Version(String pkg) {
        try {
            Properties props = new Properties();
            String fileName = pkg.replace(".", "/") + "/jemmy.properties";
            InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
            if(in == null) {
                throw new JemmyException("Can not get version - no " + fileName + " file");
            }
            props.load(in);
            major = Integer.parseInt(props.getProperty("version.major"));
            minor = Integer.parseInt(props.getProperty("version.minor"));
            mini = Integer.parseInt(props.getProperty("version.mini"));
            build = props.getProperty("build");
        } catch (IOException ex) {
            throw new JemmyException("Can not get version.", ex);
        }
    }

    /**
     *
     * @return
     */
    public int getMajor() {
        return major;
    }

    /**
     *
     * @return
     */
    public int getMini() {
        return mini;
    }

    /**
     *
     * @return
     */
    public int getMinor() {
        return minor;
    }

    /**
     *
     * @return
     */
    public String getVersion() {
        return major + "." + minor + "." + mini;
    }

    /**
     *
     * @return
     */
    public String getBuild() {
        return build;
    }

    /**
     *
     * @param old
     * @return
     */
    public boolean newer(String old) {
        StringTokenizer tn = new StringTokenizer(old, ".");
        if(major >= Integer.parseInt(tn.nextToken())) {
            if(minor >= Integer.parseInt(tn.nextToken())) {
                if(mini >= Integer.parseInt(tn.nextToken())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("JemmyCore version: " + VERSION.getVersion() + "." + VERSION.build);
    }
}
