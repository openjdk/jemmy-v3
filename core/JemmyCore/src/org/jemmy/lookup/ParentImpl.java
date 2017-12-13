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

package org.jemmy.lookup;

import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Parent;

/**
 *
 * @param <T>
 * @author shura
 */
public class ParentImpl<T> extends AbstractParent<T> {

    private Class<T> type;
    private ControlHierarchy ch;
    private ControlList cl;
    private Environment env;
    private Wrapper wrapper;

    /**
     *
     * @param env
     * @param type
     * @param ch
     */
    public ParentImpl(Environment env, Class<T> type, ControlHierarchy ch) {
        this(env, type, ch, Wrap.getWrapper());
    }
    /**
     *
     * @param env
     * @param type
     * @param ch
     * @param wrapper
     */
    public ParentImpl(Environment env, Class<T> type, ControlHierarchy ch, Wrapper wrapper) {
        this.type = type;
        this.ch = ch;
        this.env = env;
        this.wrapper = wrapper;
    }

    /**
     *
     * @param env
     * @param type
     * @param cl
     */
    public ParentImpl(Environment env, Class<T> type, ControlList cl) {
        this(env, type, cl, Wrap.getWrapper());
    }
    /**
     *
     * @param env
     * @param type
     * @param cl
     * @param wrapper
     */
    public ParentImpl(Environment env, Class<T> type, ControlList cl, Wrapper wrapper) {
        this.type = type;
        this.cl = cl;
        this.env = env;
        this.wrapper = wrapper;
    }

    public <ST extends T> Lookup<ST> lookup(Class<ST> controlClass, LookupCriteria<ST> criteria) {
        if(ch != null) {
            return new HierarchyLookup<ST>(env, ch, wrapper, controlClass, criteria);
        } else if(cl != null) {
            return new PlainLookup<ST>(env, cl, wrapper, controlClass, criteria);
        }
        throw new IllegalStateException();
    }

    public Lookup<T> lookup(LookupCriteria<T> criteria) {
        return lookup(type, criteria);
    }

    public Class<T> getType() {
        return type;
    }

}
