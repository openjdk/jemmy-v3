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

import org.jemmy.env.Environment;

/**
 * This allows to reuse another {@code Wrapper} with a different environment
 * @author shura
 */
public class WrapperDelegate implements Wrapper {
    private final Wrapper real;
    private final Environment env;

    public WrapperDelegate(Wrapper real, Environment env) {
        this.real = real;
        this.env = env;
    }

    public <T> Wrap<? extends T> wrap(Class<T> controlClass, T control) {
        Wrap<? extends T> res = real.wrap(controlClass, control);
        res.setEnvironment(new Environment(env));
        return res;
    }
}
