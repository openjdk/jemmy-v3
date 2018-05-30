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


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.jemmy.env.Environment;


/**
 * This is an implementation of the {@code Wrapper} which instantiates a wrap
 * of a class returned by {@code getWrapClass(Class)}.
 * @author shura
 */
public abstract class AbstractWrapper implements Wrapper {

    private Environment env;

    public AbstractWrapper(Environment env) {
        this.env = env;
    }

    public Environment getEnvironment() {
        return env;
    }

    protected abstract Class<Wrap> getWrapClass(Class controlClass);

    public <T> Wrap<? extends T> wrap(Class<T> controlClass, T control) {
        Class cls = control.getClass();
        Class<Wrap> wrp;
        do {
            wrp = getWrapClass(cls);
            if (wrp != null) {
                try {
                    return doWrap(control, cls, wrp);
                } catch (InstantiationException ex) {
                    throw new WrapperException(cls, wrp, ex);
                } catch (IllegalAccessException ex) {
                    throw new WrapperException(cls, wrp, ex);
                } catch (IllegalArgumentException ex) {
                    throw new WrapperException(cls, wrp, ex);
                } catch (InvocationTargetException ex) {
                    throw new WrapperException(cls, wrp, ex);
                } catch (NoSuchMethodException ex) {
                    throw new WrapperException(cls, wrp, ex);
                } catch (SecurityException ex) {
                    throw new WrapperException(cls, wrp, ex);
                }
            }
        } while ((cls = cls.getSuperclass()) != null);
        throw new WrapperException(control);
    }

    protected <T> Wrap<? extends T> doWrap(T control, Class controlClass, Class wrapperClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor cns = null;
        Class cls = controlClass;
        do {
            try {
                cns = wrapperClass.getConstructor(Environment.class, cls);
            } catch (NoSuchMethodException e) {
            }
        } while ((cls = cls.getSuperclass()) != null);
        if (cns != null) {
            return (Wrap<T>) cns.newInstance(new Environment(env), control);
        } else {
            throw new WrapperException(controlClass, wrapperClass);
        }
    }
}
