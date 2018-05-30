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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.jemmy.control.Wrap;

/**
 * This should be used on classes - inheritors of <code>Wrap</code> class to give
 * annotation processor some information.
 * @see Wrap
 * @author shura
 */
@Target(ElementType.TYPE)
@Documented
public @interface DockInfo {
    /**
     * Desired name of the dock class, should one be generated.
     * Usually empty ("", as nulls are not allowed) in which case the calculated value
     * is taken whatever logic annotation processor decides to use.
     * @return todo document
     */
    String name() default "";

    /**
     * Should there be extra constructors which take another lookup criteria - a class
     * of a desired control? That class must be a subtype of the one wrapped by the wrap
     * class annotated with this annotation.
     * @return todo document
     */
    boolean generateSubtypeLookups() default false;

    /**
     * Should generated <code>wrap()</code> method return this class or
     * <code>Wrap&lt;? extends ...&gt;</code> and also should there be a constructor with
     * one parameter - the wrap.
     * @return todo document
     */
    boolean anonymous() default false;

    /**
     * Should the lookup constructors have <code>LookupCriteria&lt;Type&gt;...</code>
     * parameter or the <code>LookupCriteria&lt;Type&gt;</code> parameter.
     * @return todo document
     */
    boolean multipleCriteria() default true;
}
