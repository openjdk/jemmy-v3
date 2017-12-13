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
package org.jemmy.control;


import java.util.HashMap;
import org.jemmy.env.Environment;


/**
 * This is an implementation of {@code Wrapper} which keeps a map between
 * control classes and wrap classes.
 * @author shura
 */
public class WrapperImpl extends AbstractWrapper {

    private HashMap<Class, Class<Wrap>> theWrappers;

    /**
     *
     * @param env
     */
    @SuppressWarnings("unchecked")
    public WrapperImpl(Environment env) {
        super(env);
        theWrappers = new HashMap<Class, Class<Wrap>>();
    }

    /**
     *
     * @param <P>
     * @param controlClass
     * @param wrapperClass
     */
    public <P> void add(Class controlClass, Class<Wrap> wrapperClass) {
        theWrappers.put(controlClass, wrapperClass);
        // TODO: Improve output
//        getEnvironment().getOutput().println("Added \"" + wrapperClass.getName() + "\"" +
//                " wrapper for \"" + controlClass.getName() + "\" control type");
    }

    @Override
    protected Class<Wrap> getWrapClass(Class controlClass) {
        return theWrappers.get(controlClass);
    }

}
