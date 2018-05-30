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


import java.io.Serializable;


/**
 * Replacement for java.awt.Dimension
 * @author mrkam
 */
public class Dimension implements Serializable {

    /**
     * The width dimension; negative values can be used.
     *
     * @serial
     * @see #getSize
     * @see #setSize
     */
    public int width;

    /**
     * The height dimension; negative values can be used.
     *
     * @serial
     * @see #getSize
     * @see #setSize
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 4723952579491349524L;

    /**
     * Creates an instance of <code>Dimension</code> with a width
     * of zero and a height of zero.
     */
    public Dimension() {
        this(0, 0);
    }

    /**
     * Creates an instance of <code>Dimension</code> whose width
     * and height are the same as for the specified dimension.
     *
     * @param    d   the specified dimension for the
     *               <code>width</code> and
     *               <code>height</code> values
     */
    public Dimension(Dimension d) {
        this(d.width, d.height);
    }

    /**
     * Constructs a <code>Dimension</code> and initializes
     * it to the specified width and specified height.
     *
     * @param width the specified width
     * @param height the specified height
     */
    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a <code>Dimension</code> and initializes
     * it to the specified width and specified height. All {@code double}
     * values are rounded and stored as {@code int} values.
     *
     * @param width the specified width
     * @param height the specified height
     */
    public Dimension(double width, double height) {
        this.width = (int) Math.round(width);
        this.height = (int) Math.round(height);
    }

    /**
     * {@inheritDoc}
     */
    public double getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the size of this <code>Dimension</code> object to
     * the specified width and height in double precision.
     * Note that if <code>width</code> or <code>height</code>
     * are larger than <code>Integer.MAX_VALUE</code>, they will
     * be reset to <code>Integer.MAX_VALUE</code>.
     *
     * @param width  the new width for the <code>Dimension</code> object
     * @param height the new height for the <code>Dimension</code> object
     */
    public void setSize(double width, double height) {
        this.width = (int) Math.ceil(width);
        this.height = (int) Math.ceil(height);
    }

    /**
     * Gets the size of this <code>Dimension</code> object.
     * @return   the size of this dimension, a new instance of
     *           <code>Dimension</code> with the same width and height
     * @see      #setSize
     */
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Sets the size of this <code>Dimension</code> object to the specified size.
     * @param    d  the new size for this <code>Dimension</code> object
     * @see      Dimension#getSize
     */
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    /**
     * Sets the size of this <code>Dimension</code> object
     * to the specified width and height.
     * @param    width   the new width for this <code>Dimension</code> object
     * @param    height  the new height for this <code>Dimension</code> object
     * @see      Dimension#getSize
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dimension) {
            Dimension d = (Dimension)obj;
            return (width == d.width) && (height == d.height);
        }
        return false;
    }

    /**
     * Returns the hash code for this <code>Dimension</code>.
     *
     * @return    a hash code for this <code>Dimension</code>
     */
    @Override
    public int hashCode() {
        int sum = width + height;
        return sum * (sum + 1)/2 + width;
    }

    /**
     * Returns a string representation of the values of this
     * <code>Dimension</code> object's <code>height</code> and
     * <code>width</code> fields. This method is intended to be used only
     * for debugging purposes, and the content and format of the returned
     * string may vary between implementations. The returned string may be
     * empty but may not be <code>null</code>.
     *
     * @return  a string representation of this <code>Dimension</code>
     *          object
     */
    @Override
    public String toString() {
        return getClass().getName() + "[width=" + width + ",height=" + height + "]";
    }
}
