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

package org.jemmy.properties;

import org.jemmy.Rectangle;
import org.jemmy.control.FieldProperties;
import org.jemmy.control.MethodProperties;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;

/**
 *
 * @author shura
 */
@MethodProperties({"m1", "m2"})
@FieldProperties({"f1", "f2"})
public class TestWrap<T extends TestObject> extends Wrap<T>{
    public static final String M1_PROPERTY = "m1";
    public static final String F1_PROPERTY = "f1";

    public TestWrap(T node) {
        super(Environment.getEnvironment(), node);
    }

    @Override
    public Rectangle getScreenBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Property(M1_PROPERTY)
    public int m1() {
        //something different from what method returns
        return getControl().m1() + 1;
    }
    @Property(F1_PROPERTY)
    public int f1() {
        //something different from what method returns
        return getControl().f1 + 1;
    }

}
