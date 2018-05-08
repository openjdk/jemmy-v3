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

package org.jemmy;

/**
 * The class for easy computations.
 * @author shura
 */
public class Vector {

    private double x;
    private double y;

    /**
     *
     * @param x
     * @param y
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param from
     * @param to
     */
    public Vector(Point from, Point to) {
        x = to.x - from.x;
        y = to.y - from.y;
    }

    /**
     *
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     *
     * @return
     */
    public double lenght() {
        return Math.sqrt(x*x + y*y);
    }

    /**
     *
     * @param newLenght
     * @return self
     */
    public Vector setLenght(double newLenght) {
        double lenght = lenght();
        x = x * newLenght / lenght;
        y = y * newLenght / lenght;
        return this;
    }

    /**
     * @param multiplier
     * @return self
     */
    public Vector multiply(double multiplier) {
        x*=multiplier;
        y*=multiplier;
        return this;
    }

    /**
     *
     * @return a clone
     */
    @Override
    public Vector clone() {
        return new Vector(x, y);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Adds another vector <code>(x1 + x2, y1 + y2)</code>
     * @param v
     * @return self
     */
    public Vector add(Vector v) {
        x+=v.x;
        y+=v.y;
        return this;
    }


}
