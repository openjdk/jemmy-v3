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
 *
 * Parent of all Jemmy exceptions.
 * Exception can be thrown from inside jemmy methods,
 * if some exception occurs from code invoked from jemmy.
 *
 * @author Alexandre Iline (alexandre.iline@oracle.com), Alexander Kouznetsov (Alexander.Kouznetsov@oracle.com)
 */

public class JemmyException extends RuntimeException {

    private Object object = null;

    /**
     * Constructor.
     * @param description An exception description.
     */
    public JemmyException(String description) {
        super(description);
    }

    /**
     * Constructor.
     * @param description An exception description.
     * @param innerException Exception from code invoked from jemmy.
     */
    public JemmyException(String description, Throwable innerException) {
        super(description, innerException);
    }

    /**
     * Constructor.
     * @param description An exception description.
     * @param object Object regarding which exception is thrown.
     */
    public JemmyException(String description, Object object) {
        this(description, null, object);
    }

    /**
     * Constructor.
     * @param description An exception description.
     * @param innerException Exception from code invoked from jemmy.
     * @param object Object regarding which exception is thrown.
     */
    public JemmyException(String description, Throwable innerException, Object object) {
        this(description + " (Object: " + object + ")", innerException);
        this.object = object;
    }

    /**
     * Returns "object" constructor parameter.
     * @return the Object value associated with the exception.
     */
    public Object getObject() {
        return(object);
    }
}
