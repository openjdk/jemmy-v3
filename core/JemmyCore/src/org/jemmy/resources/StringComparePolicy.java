/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.resources;

/**
 *
 * @author shura
 */
public interface StringComparePolicy {

    public static final StringComparePolicy EXACT = new ComparePolicy(true, true);
    public static final StringComparePolicy SUBSTRING = new ComparePolicy(false, true);

    public boolean compare(String golden, String value);

    static class ComparePolicy implements StringComparePolicy {

        boolean ce;
        boolean cc;

        /**
         * @param ce if true then entire strings are compared, if false golden
         * could be a substring of a value
         * @param cc case sensitive comparison policy
         */
        public ComparePolicy(boolean ce, boolean cc) {
            this.cc = cc;
            this.ce = ce;
        }

        public boolean compare(String golden, String value) {
            if (value == null) {
                return golden == null;
            } else if (golden == null) {
                return !ce;
            }
            String g, v;
            if (cc) {
                g = golden;
                v = value;
            } else {
                g = golden.toUpperCase();
                v = value.toUpperCase();
            }
            if (ce) {
                return v.equals(g);
            } else {
                return v.contains(g);
            }
        }

        @Override
        public String toString() {
            return "case " + (!cc ? "in" : "") + "sensitive" + (!ce ? " as substring" : "");
        }
    }
}
