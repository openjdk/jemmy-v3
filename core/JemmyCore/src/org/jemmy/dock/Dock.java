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
package org.jemmy.dock;

import org.jemmy.Rectangle;
import org.jemmy.action.GetAction;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Drag;
import org.jemmy.interfaces.Keyboard;
import org.jemmy.interfaces.Mouse;
import org.jemmy.interfaces.Parent;
import org.jemmy.lookup.Lookup;
import org.jemmy.lookup.LookupCriteria;

/**
 * Superclass for all "docks" - classes which simple provide API for lookup, interfaces
 * and properties.
 * @author shura
 */
public class Dock {

    /**
     * Default suffix to construct result image name.
     */
    public static final String DEFAULT_RESULT_IMAGE_SUFFIX = "default.result.image.suffix";
    /**
     * Default suffix to construct diff image name.
     */
    public static final String DEFAULT_DIFF_IMAGE_SUFFIX = "default.diff.image.suffix";

    static {
        Environment.getEnvironment().setPropertyIfNotSet(DEFAULT_DIFF_IMAGE_SUFFIX, "-diff");
        Environment.getEnvironment().setPropertyIfNotSet(DEFAULT_RESULT_IMAGE_SUFFIX, "-result");
    }

    private Wrap<?> wrap;

    protected Dock(Wrap<?> wrap) {
        this.wrap = wrap;
    }

    /**
     * Method which at the end actually get called from all dock lookup
     * constructors.
     * @param <T> todo document
     * @param parent todo document
     * @param controlType todo document
     * @param index todo document
     * @param criteria todo document
     * @return todo document
     */
    protected static <T> Wrap<? extends T> lookup(Parent<? super T> parent, Class<T> controlType, int index, LookupCriteria<T>... criteria) {
        Lookup<T> lookup;
        if (criteria.length > 0) {
            lookup = parent.lookup(controlType, criteria[0]);
            for (int i = 1; i < criteria.length; i++) {
                lookup = lookup.lookup(controlType, criteria[i]);
            }
        } else {
            lookup = parent.lookup(controlType);
        }
        return lookup.wrap(index);
    }

    /**
     *
     * @return Wrap instance obtainer through lookup
     */
    public Wrap<?> wrap() {
        return wrap;
    }

    /**
     * @return Wrap instance obtainer through lookup
     */
    public Object control() {
        return wrap.getControl();
    }

    /**
     * @return Shortcut to <code>wrap().mouse()</code>
     */
    public Mouse mouse() {
        return wrap.mouse();
    }

    /**
     * @return Shortcut to <code>wrap().keyboard()</code>
     */
    public Keyboard keyboard() {
        return wrap.keyboard();
    }

    /**
     * @return Shortcut to <code>wrap().drag()</code>
     */
    public Drag drag() {
        return wrap.drag();
    }

    /**
     * @return Shortcut to <code>wrap().getScreenBounds()</code>
     */
    public Rectangle bounds() {
        return wrap.getScreenBounds();
    }

    protected <P> P getProperty(GetAction<P> action) {
        action.execute();
        return action.getResult();
    }

    /**
     * @return <code>wrap().getEnvironment()</code>.
     */
    public Environment environment() {
        return wrap.getEnvironment();
    }

    /**
     * Loads image with <code>goldenId</code> id waits for the control to match it.
     * @see Wrap#waitImage(org.jemmy.image.Image, org.jemmy.Rectangle, java.lang.String, java.lang.String)
     * @param goldenId todo document
     * @param rect todo document
     * @param resID todo document
     * @param diffID todo document
     */
    public void waitImage(String goldenId, Rectangle rect, String resID, String diffID) {
        wrap.waitImage(environment().getImageLoader().load(goldenId), rect, resID, diffID);
    }

    /**
     * Constructs names for diff and result images and waits for the control to match it.
     * Diff and result names
     * constructed by adding suffixes. Suffixes are obtained from environment with
     * default values being &quot;-diff&quot; and &quot;-result&quot;
     * @see #waitImage(java.lang.String, org.jemmy.Rectangle, java.lang.String, java.lang.String)
     * @see #DEFAULT_DIFF_IMAGE_SUFFIX
     * @see #DEFAULT_RESULT_IMAGE_SUFFIX
     * @param goldenId todo document
     * @param rect todo document
     */
    public void waitImage(String goldenId, Rectangle rect) {
        waitImage(goldenId,
                rect,
                goldenId + environment().getProperty(DEFAULT_RESULT_IMAGE_SUFFIX),
                goldenId + environment().getProperty(DEFAULT_DIFF_IMAGE_SUFFIX));
    }

    /**
     * Loads image with <code>goldenId</code> id waits for the control to match it.
     * @see Wrap#waitImage(org.jemmy.image.Image, java.lang.String, java.lang.String)
     * @param goldenId todo document
     * @param resID todo document
     * @param diffID todo document
     */
    public void waitImage(String goldenId, String resID, String diffID) {
        wrap.waitImage(environment().getImageLoader().load(goldenId), resID, diffID);
    }

    /**
     * Constructs names for diff and result images and waits for the control to match it.
     * Diff and result names
     * constructed by adding suffixes. Suffixes are obtained from environment with
     * default values being &quot;-diff&quot; and &quot;-result&quot;
     * @see #waitImage(java.lang.String, java.lang.String, java.lang.String)
     * @see #DEFAULT_DIFF_IMAGE_SUFFIX
     * @see #DEFAULT_RESULT_IMAGE_SUFFIX
     * @param goldenId todo document
     */
    public void waitImage(String goldenId) {
        waitImage(goldenId,
                goldenId + environment().getProperty(DEFAULT_RESULT_IMAGE_SUFFIX),
                goldenId + environment().getProperty(DEFAULT_DIFF_IMAGE_SUFFIX));
    }
 }
