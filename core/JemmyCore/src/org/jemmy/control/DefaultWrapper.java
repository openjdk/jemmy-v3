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

import org.jemmy.env.Environment;

/**
 *
 * @author shura
 */
public class DefaultWrapper extends WrapperImpl {

    /**
     *
     * @param env
     * @param wrapList
     */
    @SuppressWarnings("unchecked")
    public DefaultWrapper(Environment env, Class<? extends Wrap>... wrapList) {
        super(env);
        addAnnotated(wrapList);
    }

    /**
     *
     * @param env
     */
    @SuppressWarnings("unchecked")
    public DefaultWrapper(Environment env) {
        super(env);
    }

    /**
     *
     * @param list
     */
    @SuppressWarnings("unchecked")
    public final void addAnnotated(Class<? extends Wrap>... list) {
        for (Class cn : list) {
            if (!cn.isAnnotationPresent(ControlType.class)) {
                throw new IllegalStateException("\"" + cn.getName() + "\"" +
                        " must be annotated with @Control");
            }
            if (cn.isAnnotationPresent(ControlType.class)) {
                ControlType cc = (ControlType) cn.getAnnotation(ControlType.class);
                for(Class ccv : cc.value()) {
                    add(ccv, cn);
                }
            }
        }
    }


}
