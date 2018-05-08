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
 * Replacement for java.awt.Rectangle
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class Rectangle implements Serializable {

    /**
     * The X coordinate of the upper-left corner of the <code>Rectangle</code>.
     *
     * @serial
     * @see #setLocation(int, int)
     * @see #getLocation()
     */
    public int x;

    /**
     * The Y coordinate of the upper-left corner of the <code>Rectangle</code>.
     *
     * @serial
     * @see #setLocation(int, int)
     * @see #getLocation()
     */
    public int y;

    /**
     * The width of the <code>Rectangle</code>.
     * @serial
     * @see #setSize(int, int)
     * @see #getSize()
     */
    public int width;

    /**
     * The height of the <code>Rectangle</code>.
     *
     * @serial
     * @see #setSize(int, int)
     * @see #getSize()
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -4345857070255674764L;


    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner
     * is at (0,&nbsp;0) in the coordinate space, and whose width and
     * height are both zero.
     */
    public Rectangle() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs a new <code>Rectangle</code>, initialized to match
     * the values of the specified <code>Rectangle</code>.
     * @param r  the <code>Rectangle</code> from which to copy initial values
     *           to a newly constructed <code>Rectangle</code>
     */
    public Rectangle(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner is
     * specified as
     * {@code (x,y)} and whose width and height
     * are specified by the arguments of the same name.
     * @param     x the specified X coordinate
     * @param     y the specified Y coordinate
     * @param     width    the width of the <code>Rectangle</code>
     * @param     height   the height of the <code>Rectangle</code>
     */
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner
     * is at (0,&nbsp;0) in the coordinate space, and whose width and
     * height are specified by the arguments of the same name.
     * @param width the width of the <code>Rectangle</code>
     * @param height the height of the <code>Rectangle</code>
     */
    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner is
     * specified by the {@link Point} argument, and
     * whose width and height are specified by the
     * {@link Dimension} argument.
     * @param p a <code>Point</code> that is the upper-left corner of
     * the <code>Rectangle</code>
     * @param d a <code>Dimension</code>, representing the
     * width and height of the <code>Rectangle</code>
     */
    public Rectangle(Point p, Dimension d) {
        this(p.x, p.y, d.width, d.height);
    }

    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner is the
     * specified <code>Point</code>, and whose width and height are both zero.
     * @param p a <code>Point</code> that is the top left corner
     * of the <code>Rectangle</code>
     */
    public Rectangle(Point p) {
        this(p.x, p.y, 0, 0);
    }

    /**
     * Constructs a new <code>Rectangle</code> whose top left corner is
     * (0,&nbsp;0) and whose width and height are specified
     * by the <code>Dimension</code> argument.
     * @param d a <code>Dimension</code>, specifying width and height
     */
    public Rectangle(Dimension d) {
        this(0, 0, d.width, d.height);
    }

    /**
     * Constructs a new <code>Rectangle</code> whose upper-left corner is
     * specified as {@code (x,y)} and whose width and height
     * are specified by the arguments of the same name. All {@code double}
     * values are rounded and stored as {@code int} values.
     * @param     x the specified X coordinate
     * @param     y the specified Y coordinate
     * @param     width    the width of the <code>Rectangle</code>
     * @param     height   the height of the <code>Rectangle</code>
     */
    public Rectangle(double x, double y, double width, double height) {
        this((int) Math.round(x), (int) Math.round(y),
                (int) Math.round(width), (int) Math.round(height));
    }

    /**
     * Returns the X coordinate of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the X coordinate of the bounding <code>Rectangle</code>.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y coordinate of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the Y coordinate of the bounding <code>Rectangle</code>.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the width of the bounding <code>Rectangle</code>.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the bounding <code>Rectangle</code> in
     * <code>double</code> precision.
     * @return the height of the bounding <code>Rectangle</code>.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the bounding <code>Rectangle</code> of this <code>Rectangle</code>.
     * <p>
     * @return    a new <code>Rectangle</code>, equal to the
     * bounding <code>Rectangle</code> for this <code>Rectangle</code>.
     * @see       #setBounds(Rectangle)
     * @see       #setBounds(int, int, int, int)
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Sets the bounding <code>Rectangle</code> of this <code>Rectangle</code>
     * to match the specified <code>Rectangle</code>.
     * <p>
     * @param r the specified <code>Rectangle</code>
     * @see       #getBounds
     */
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Sets the bounding <code>Rectangle</code> of this
     * <code>Rectangle</code> to the specified
     * <code>x</code>, <code>y</code>, <code>width</code>,
     * and <code>height</code>.
     * <p>
     * @param x the new X coordinate for the upper-left
     *                    corner of this <code>Rectangle</code>
     * @param y the new Y coordinate for the upper-left
     *                    corner of this <code>Rectangle</code>
     * @param width the new width for this <code>Rectangle</code>
     * @param height the new height for this <code>Rectangle</code>
     * @see       #getBounds
     */
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the bounds of this {@code Rectangle} to the integer bounds
     * which encompass the specified {@code x}, {@code y}, {@code width},
     * and {@code height}.
     * If the parameters specify a {@code Rectangle} that exceeds the
     * maximum range of integers, the result will be the best
     * representation of the specified {@code Rectangle} intersected
     * with the maximum integer bounds.
     * @param x the X coordinate of the upper-left corner of
     *                  the specified rectangle
     * @param y the Y coordinate of the upper-left corner of
     *                  the specified rectangle
     * @param width the width of the specified rectangle
     * @param height the new height of the specified rectangle
     */
    public void setRect(double x, double y, double width, double height) {
        int newx, newy, neww, newh;

        if (x > 2.0 * Integer.MAX_VALUE) {
            // Too far in positive X direction to represent...
            // We cannot even reach the left side of the specified
            // rectangle even with both x & width set to MAX_VALUE.
            // The intersection with the "maximal integer rectangle"
            // is non-existant so we should use a width < 0.
            // REMIND: Should we try to determine a more "meaningful"
            // adjusted value for neww than just "-1"?
            newx = Integer.MAX_VALUE;
            neww = -1;
        } else {
            newx = clip(x, false);
            if (width >= 0) width += x-newx;
            neww = clip(width, width >= 0);
        }

        if (y > 2.0 * Integer.MAX_VALUE) {
            // Too far in positive Y direction to represent...
            newy = Integer.MAX_VALUE;
            newh = -1;
        } else {
            newy = clip(y, false);
            if (height >= 0) height += y-newy;
            newh = clip(height, height >= 0);
        }

        setBounds(newx, newy, neww, newh);
    }
    // Return best integer representation for v, clipped to integer
    // range and floor-ed or ceiling-ed, depending on the boolean.
    private static int clip(double v, boolean doceil) {
        if (v <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (v >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) (doceil ? Math.ceil(v) : Math.floor(v));
    }

    /**
     * Returns the location of this <code>Rectangle</code>.
     * <p>
     * @return the <code>Point</code> that is the upper-left corner of
     * this <code>Rectangle</code>.
     * @see       #setLocation(Point)
     * @see       #setLocation(int, int)
     */
    public Point getLocation() {
        return new Point(x, y);
    }

    /**
     * Moves this <code>Rectangle</code> to the specified location.
     * <p>
     * @param p the <code>Point</code> specifying the new location
     *                for this <code>Rectangle</code>
     * @see       #getLocation
     */
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    /**
     * Moves this <code>Rectangle</code> to the specified location.
     * <p>
     * @param x the X coordinate of the new location
     * @param y the Y coordinate of the new location
     * @see       #getLocation
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Translates this <code>Rectangle</code> the indicated distance,
     * to the right along the X coordinate axis, and
     * downward along the Y coordinate axis.
     * @param dx the distance to move this <code>Rectangle</code>
     *                 along the X axis
     * @param dy the distance to move this <code>Rectangle</code>
     *                 along the Y axis
     * @see       #setLocation(int, int)
     * @see       #setLocation(org.jemmy.Point)
     */
    public void translate(int dx, int dy) {
        int oldv = this.x;
        int newv = oldv + dx;
        if (dx < 0) {
            // moving leftward
            if (newv > oldv) {
                // negative overflow
                // Only adjust width if it was valid (>= 0).
                if (width >= 0) {
                    // The right edge is now conceptually at
                    // newv+width, but we may move newv to prevent
                    // overflow.  But we want the right edge to
                    // remain at its new location in spite of the
                    // clipping.  Think of the following adjustment
                    // conceptually the same as:
                    // width += newv; newv = MIN_VALUE; width -= newv;
                    width += newv - Integer.MIN_VALUE;
                    // width may go negative if the right edge went past
                    // MIN_VALUE, but it cannot overflow since it cannot
                    // have moved more than MIN_VALUE and any non-negative
                    // number + MIN_VALUE does not overflow.
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving rightward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (width >= 0) {
                    // Conceptually the same as:
                    // width += newv; newv = MAX_VALUE; width -= newv;
                    width += newv - Integer.MAX_VALUE;
                    // With large widths and large displacements
                    // we may overflow so we need to check it.
                    if (width < 0) width = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.x = newv;

        oldv = this.y;
        newv = oldv + dy;
        if (dy < 0) {
            // moving upward
            if (newv > oldv) {
                // negative overflow
                if (height >= 0) {
                    height += newv - Integer.MIN_VALUE;
                    // See above comment about no overflow in this case
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving downward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (height >= 0) {
                    height += newv - Integer.MAX_VALUE;
                    if (height < 0) height = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.y = newv;
    }

    /**
     * Gets the size of this <code>Rectangle</code>, represented by
     * the returned <code>Dimension</code>.
     * <p>
     * @return a <code>Dimension</code>, representing the size of
     *            this <code>Rectangle</code>.
     * @see       #setSize(Dimension)
     * @see       #setSize(int, int)
     */
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Sets the size of this <code>Rectangle</code> to match the
     * specified <code>Dimension</code>.
     * <p>
     * @param d the new size for the <code>Dimension</code> object
     * @see       #getSize
     */
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    /**
     * Sets the size of this <code>Rectangle</code> to the specified
     * width and height.
     * <p>
     * @param width the new width for this <code>Rectangle</code>
     * @param height the new height for this <code>Rectangle</code>
     * @see       #getSize
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Checks whether or not this <code>Rectangle</code> contains the
     * specified <code>Point</code>.
     * @param p the <code>Point</code> to test
     * @return    <code>true</code> if the specified <code>Point</code>
     *            is inside this <code>Rectangle</code>;
     *            <code>false</code> otherwise.
     */
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    /**
     * Checks whether or not this <code>Rectangle</code> contains the
     * point at the specified location {@code (x,y)}.
     *
     * @param  x the specified X coordinate
     * @param  y the specified Y coordinate
     * @return    <code>true</code> if the point
     * {@code (x,y)} is inside this
     * <code>Rectangle</code>;
     * <code>false</code> otherwise.
     */
    public boolean contains(int x, int y) {
        return contains(x, y, 1, 1);
    }

    /**
     * Checks whether or not this <code>Rectangle</code> entirely contains
     * the specified <code>Rectangle</code>.
     *
     * @param     r   the specified <code>Rectangle</code>
     * @return    <code>true</code> if the <code>Rectangle</code>
     *            is contained entirely inside this <code>Rectangle</code>;
     *            <code>false</code> otherwise
     */
    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }

    /**
     * Checks whether this <code>Rectangle</code> entirely contains
     * the <code>Rectangle</code>
     * at the specified location {@code (X,Y)} with the
     * specified dimensions {@code (W,H)}.
     * @param     X the specified X coordinate
     * @param     Y the specified Y coordinate
     * @param     W   the width of the <code>Rectangle</code>
     * @param     H   the height of the <code>Rectangle</code>
     * @return    <code>true</code> if the <code>Rectangle</code> specified by
     *            {@code (X, Y, W, H)}
     *            is entirely enclosed inside this <code>Rectangle</code>;
     *            <code>false</code> otherwise.
     */
    public boolean contains(int X, int Y, int W, int H) {
        int w = this.width;
        int h = this.height;
        if ((w | h | W | H) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        int xx = this.x;
        int yy = this.y;
        if (X < xx || Y < yy) {
            return false;
        }
        w += xx;
        W += X;
        if (W <= X) {
            // X+W overflowed or W was zero, return false if...
            // either original w or W was zero or
            // x+w did not overflow or
            // the overflowed x+w is smaller than the overflowed X+W
            if (w >= xx || W > w) return false;
        } else {
            // X+W did not overflow and W was not zero, return false if...
            // original w was zero or
            // x+w did not overflow and x+w is smaller than X+W
            if (w >= xx && W > w) return false;
        }
        h += yy;
        H += Y;
        if (H <= Y) {
            if (h >= yy || H > h) return false;
        } else {
            if (h >= yy && H > h) return false;
        }
        return true;
    }

    /**
     * Determines whether or not this <code>Rectangle</code> and the specified
     * <code>Rectangle</code> intersect. Two rectangles intersect if
     * their intersection is nonempty.
     *
     * @param r the specified <code>Rectangle</code>
     * @return    <code>true</code> if the specified <code>Rectangle</code>
     *            and this <code>Rectangle</code> intersect;
     *            <code>false</code> otherwise.
     */
    public boolean intersects(Rectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
            (rh < ry || rh > ty) &&
            (tw < tx || tw > rx) &&
            (th < ty || th > ry));
    }

    /**
     * Computes the intersection of this <code>Rectangle</code> with the
     * specified <code>Rectangle</code>. Returns a new <code>Rectangle</code>
     * that represents the intersection of the two rectangles.
     * If the two rectangles do not intersect, the result will be
     * an empty rectangle.
     *
     * @param     r   the specified <code>Rectangle</code>
     * @return    the largest <code>Rectangle</code> contained in both the
     *            specified <code>Rectangle</code> and in
     * this <code>Rectangle</code>; or if the rectangles
     * do not intersect, an empty rectangle.
     */
    public Rectangle intersection(Rectangle r) {
        int tx1 = this.x;
        int ty1 = this.y;
        int rx1 = r.x;
        int ry1 = r.y;
        long tx2 = tx1; tx2 += this.width;
        long ty2 = ty1; ty2 += this.height;
        long rx2 = rx1; rx2 += r.width;
        long ry2 = ry1; ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    }

    /**
     * Computes the union of this <code>Rectangle</code> with the
     * specified <code>Rectangle</code>. Returns a new
     * <code>Rectangle</code> that
     * represents the union of the two rectangles.
     * <p>
     * If either {@code Rectangle} has any dimension less than zero
     * the rules for <a href=#NonExistant>non-existant</a> rectangles
     * apply.
     * If only one has a dimension less than zero, then the result
     * will be a copy of the other {@code Rectangle}.
     * If both have dimension less than zero, then the result will
     * have at least one dimension less than zero.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension
     * too large to be expressed as an {@code int}, the result
     * will have a dimension of {@code Integer.MAX_VALUE} along
     * that dimension.
     * @param r the specified <code>Rectangle</code>
     * @return    the smallest <code>Rectangle</code> containing both
     * the specified <code>Rectangle</code> and this
     * <code>Rectangle</code>.
     */
    public Rectangle union(Rectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) {
            // This rectangle has negative dimensions...
            // If r has non-negative dimensions then it is the answer.
            // If r is non-existant (has a negative dimension), then both
            // are non-existant and we can return any non-existant rectangle
            // as an answer.  Thus, returning r meets that criterion.
            // Either way, r is our answer.
            return new Rectangle(r);
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) {
            return new Rectangle(this);
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) tx1 = rx1;
        if (ty1 > ry1) ty1 = ry1;
        if (tx2 < rx2) tx2 = rx2;
        if (ty2 < ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty
        // they might overflow, though...
        if (tx2 > Integer.MAX_VALUE) tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE) ty2 = Integer.MAX_VALUE;
        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    }

    /**
     * Adds a point, specified by the integer arguments {@code newx,newy}
     * to the bounds of this {@code Rectangle}.
     * <p>
     * If this {@code Rectangle} has any dimension less than zero,
     * the rules for <a href=#NonExistant>non-existant</a>
     * rectangles apply.
     * In that case, the new bounds of this {@code Rectangle} will
     * have a location equal to the specified coordinates and
     * width and height equal to zero.
     * <p>
     * After adding a point, a call to <code>contains</code> with the
     * added point as an argument does not necessarily return
     * <code>true</code>. The <code>contains</code> method does not
     * return <code>true</code> for points on the right or bottom
     * edges of a <code>Rectangle</code>. Therefore, if the added point
     * falls on the right or bottom edge of the enlarged
     * <code>Rectangle</code>, <code>contains</code> returns
     * <code>false</code> for that point.
     * If the specified point must be contained within the new
     * {@code Rectangle}, a 1x1 rectangle should be added instead:
     * <pre>
     *     r.add(newx, newy, 1, 1);
     * </pre>
     * @param newx the X coordinate of the new point
     * @param newy the Y coordinate of the new point
     */
    public void add(int newx, int newy) {
        if ((width | height) < 0) {
            this.x = newx;
            this.y = newy;
            this.width = this.height = 0;
            return;
        }
        int x1 = this.x;
        int y1 = this.y;
        long x2 = this.width;
        long y2 = this.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx) x1 = newx;
        if (y1 > newy) y1 = newy;
        if (x2 < newx) x2 = newx;
        if (y2 < newy) y2 = newy;
        x2 -= x1;
        y2 -= y1;
        if (x2 > Integer.MAX_VALUE) x2 = Integer.MAX_VALUE;
        if (y2 > Integer.MAX_VALUE) y2 = Integer.MAX_VALUE;
        setBounds(x1, y1, (int) x2, (int) y2);
    }

    /**
     * Adds the specified {@code Point} to the bounds of this
     * {@code Rectangle}.
     * <p>
     * If this {@code Rectangle} has any dimension less than zero,
     * the rules for <a href=#NonExistant>non-existant</a>
     * rectangles apply.
     * In that case, the new bounds of this {@code Rectangle} will
     * have a location equal to the coordinates of the specified
     * {@code Point} and width and height equal to zero.
     * <p>
     * After adding a <code>Point</code>, a call to <code>contains</code>
     * with the added <code>Point</code> as an argument does not
     * necessarily return <code>true</code>. The <code>contains</code>
     * method does not return <code>true</code> for points on the right
     * or bottom edges of a <code>Rectangle</code>. Therefore if the added
     * <code>Point</code> falls on the right or bottom edge of the
     * enlarged <code>Rectangle</code>, <code>contains</code> returns
     * <code>false</code> for that <code>Point</code>.
     * If the specified point must be contained within the new
     * {@code Rectangle}, a 1x1 rectangle should be added instead:
     * <pre>
     *     r.add(pt.x, pt.y, 1, 1);
     * </pre>
     * @param pt the new <code>Point</code> to add to this
     *           <code>Rectangle</code>
     */
    public void add(Point pt) {
        add(pt.x, pt.y);
    }

    /**
     * Adds a <code>Rectangle</code> to this <code>Rectangle</code>.
     * The resulting <code>Rectangle</code> is the union of the two
     * rectangles.
     * <p>
     * If either {@code Rectangle} has any dimension less than 0, the
     * result will have the dimensions of the other {@code Rectangle}.
     * If both {@code Rectangle}s have at least one dimension less
     * than 0, the result will have at least one dimension less than 0.
     * <p>
     * If either {@code Rectangle} has one or both dimensions equal
     * to 0, the result along those axes with 0 dimensions will be
     * equivalent to the results obtained by adding the corresponding
     * origin coordinate to the result rectangle along that axis,
     * similar to the operation of the {@link #add(Point)} method,
     * but contribute no further dimension beyond that.
     * <p>
     * If the resulting {@code Rectangle} would have a dimension
     * too large to be expressed as an {@code int}, the result
     * will have a dimension of {@code Integer.MAX_VALUE} along
     * that dimension.
     * @param  r the specified <code>Rectangle</code>
     */
    public void add(Rectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) {
            setBounds(r.x, r.y, r.width, r.height);
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) {
            return;
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) tx1 = rx1;
        if (ty1 > ry1) ty1 = ry1;
        if (tx2 < rx2) tx2 = rx2;
        if (ty2 < ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original
        // rectangles were non-empty
        // they might overflow, though...
        if (tx2 > Integer.MAX_VALUE) tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE) ty2 = Integer.MAX_VALUE;
        setBounds(tx1, ty1, (int) tx2, (int) ty2);
    }

    /**
     * Resizes the <code>Rectangle</code> both horizontally and vertically.
     * <p>
     * This method modifies the <code>Rectangle</code> so that it is
     * <code>h</code> units larger on both the left and right side,
     * and <code>v</code> units larger at both the top and bottom.
     * <p>
     * The new <code>Rectangle</code> has {@code (x - h, y - v)}
     * as its upper-left corner,
     * width of {@code (width + 2h)},
     * and a height of {@code (height + 2v)}.
     * <p>
     * If negative values are supplied for <code>h</code> and
     * <code>v</code>, the size of the <code>Rectangle</code>
     * decreases accordingly.
     * The {@code grow} method will check for integer overflow
     * and underflow, but does not check whether the resulting
     * values of {@code width} and {@code height} grow
     * from negative to non-negative or shrink from non-negative
     * to negative.
     * @param h the horizontal expansion
     * @param v the vertical expansion
     */
    public void grow(int h, int v) {
        long x0 = this.x;
        long y0 = this.y;
        long x1 = this.width;
        long y1 = this.height;
        x1 += x0;
        y1 += y0;

        x0 -= h;
        y0 -= v;
        x1 += h;
        y1 += v;

        if (x1 < x0) {
            // Non-existant in X direction
            // Final width must remain negative so subtract x0 before
            // it is clipped so that we avoid the risk that the clipping
            // of x0 will reverse the ordering of x0 and x1.
            x1 -= x0;
            if (x1 < Integer.MIN_VALUE) x1 = Integer.MIN_VALUE;
            if (x0 < Integer.MIN_VALUE) x0 = Integer.MIN_VALUE;
            else if (x0 > Integer.MAX_VALUE) x0 = Integer.MAX_VALUE;
        } else { // (x1 >= x0)
            // Clip x0 before we subtract it from x1 in case the clipping
            // affects the representable area of the rectangle.
            if (x0 < Integer.MIN_VALUE) x0 = Integer.MIN_VALUE;
            else if (x0 > Integer.MAX_VALUE) x0 = Integer.MAX_VALUE;
            x1 -= x0;
            // The only way x1 can be negative now is if we clipped
            // x0 against MIN and x1 is less than MIN - in which case
            // we want to leave the width negative since the result
            // did not intersect the representable area.
            if (x1 < Integer.MIN_VALUE) x1 = Integer.MIN_VALUE;
            else if (x1 > Integer.MAX_VALUE) x1 = Integer.MAX_VALUE;
        }

        if (y1 < y0) {
            // Non-existant in Y direction
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE) y1 = Integer.MIN_VALUE;
            if (y0 < Integer.MIN_VALUE) y0 = Integer.MIN_VALUE;
            else if (y0 > Integer.MAX_VALUE) y0 = Integer.MAX_VALUE;
        } else { // (y1 >= y0)
            if (y0 < Integer.MIN_VALUE) y0 = Integer.MIN_VALUE;
            else if (y0 > Integer.MAX_VALUE) y0 = Integer.MAX_VALUE;
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE) y1 = Integer.MIN_VALUE;
            else if (y1 > Integer.MAX_VALUE) y1 = Integer.MAX_VALUE;
        }

        setBounds((int) x0, (int) y0, (int) x1, (int) y1);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean isEmpty() {
        return (width <= 0) || (height <= 0);
    }

    /**
     * Checks whether two rectangles are equal.
     * <p>
     * The result is <code>true</code> if and only if the argument is not
     * <code>null</code> and is a <code>Rectangle</code> object that has the
     * same upper-left corner, width, and height as
     * this <code>Rectangle</code>.
     * @param obj the <code>Object</code> to compare with
     *                this <code>Rectangle</code>
     * @return    <code>true</code> if the objects are equal;
     *            <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle)obj;
            return ((x == r.x) &&
                (y == r.y) &&
                (width == r.width) &&
                (height == r.height));
        }
        return super.equals(obj);
    }

    /**
     * {@inheritDoc }
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.y;
        hash = 29 * hash + this.width;
        hash = 29 * hash + this.height;
        return hash;
    }

    /**
     * Returns a <code>String</code> representing this
     * <code>Rectangle</code> and its values.
     * @return a <code>String</code> representing this
     *               <code>Rectangle</code> object's coordinate and size values.
     */
    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }

    /**
     * Parses given string to restore Rectangle instance from the string.
     * String assumed to be previously created using
     * {@linkplain #toString() Rectangle.toString()} method.
     * @param str String to parse.
     * @return Recreated Rectangle instance.
     */
    public static Rectangle parseRectangle(String str) {
        if (str != null && str.startsWith(Rectangle.class.getName())) {
            try {
                String[] t =
                        str.substring(Rectangle.class.getName().length() + 1)
                        .split("\\,|\\]");
                Rectangle res = new Rectangle();
                for(String pair : t) {
                    String[] p = pair.split("\\=");
                    String var = p[0];
                    int value = Integer.parseInt(p[1]);
                    res.getClass().getDeclaredField(var).setInt(res, value);
                }
                return res;
            } catch (Exception ex) {
                throw new JemmyException(
                        "Failed to parse Rectangle '" + str + "'", ex);
            }
        }
        return null;
    }
}
