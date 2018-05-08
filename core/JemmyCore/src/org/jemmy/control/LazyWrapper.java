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
package org.jemmy.control;


import java.util.HashMap;
import org.jemmy.JemmyException;
import org.jemmy.env.Environment;


/**
 * This is an implementation of {@code Wrapper} which keeps a map between
 * control class names and wrap class names. In only loads the wrap classes when
 * first needed.
 * @author shura
 */
public class LazyWrapper extends AbstractWrapper {

    private final HashMap<String, String> theWrappers = new HashMap<String, String>();
    private final ClassLoader loader;
    /**
     *
     * @param env
     */
    @SuppressWarnings("unchecked")
    public LazyWrapper(ClassLoader loader, Environment env) {
        super(env);
        this.loader = loader;
    }

    /**
     *
     * @param <P>
     * @param controlClass
     * @param wrapperClass
     */
    public <P> void add(String controlClass, String wrapperClass) {
        theWrappers.put(controlClass, wrapperClass);
    }

    @Override
    protected Class<Wrap> getWrapClass(Class controlClass) {
        String wrapClassName = theWrappers.get(controlClass.getName());
        if(wrapClassName == null) {
            return null;
        }
        try {
            return (Class<Wrap>)loader.loadClass(wrapClassName);
        } catch (ClassNotFoundException ex) {
            throw new JemmyException("Unable to load wrap for " + controlClass.getName() +
                    " which is " + wrapClassName, ex);
        }
    }

}
