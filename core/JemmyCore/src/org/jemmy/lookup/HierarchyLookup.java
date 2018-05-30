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
package org.jemmy.lookup;

import java.io.PrintStream;
import java.util.List;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.env.Environment;

/**
 * @author shura
 */
public class HierarchyLookup<CONTROL> extends AbstractLookup<CONTROL> {

    ControlHierarchy hierarchy;

    public HierarchyLookup(Environment env, ControlHierarchy hierarchy, Wrapper wrapper, Class<CONTROL> controlClass, LookupCriteria<CONTROL> criteria) {
        super(env, controlClass, criteria, wrapper);
        this.hierarchy = hierarchy;
    }

    public HierarchyLookup(Environment env, ControlHierarchy hierarchy, Class<CONTROL> controlClass, LookupCriteria<CONTROL> criteria) {
        this(env, hierarchy, Wrap.getWrapper(), controlClass, criteria);
    }

    @Override
    List getChildren(Object subParent) {
        if(subParent != null) {
            return hierarchy.getChildren(subParent);
        } else {
            return hierarchy.getControls();
        }
    }

    @Override
    protected void dump(PrintStream out, Lookup<? extends CONTROL> lookup) {
        int size = lookup.size();
        for (int i = 0; i < size; i++) {
            CONTROL object = lookup.get(i);
            dumpOne(out, object, calcPrefix(object));
        }
    }

    private String calcPrefix(CONTROL child) {
        String res = "";
        Object sp = child;
        while((sp = hierarchy.getParent(sp)) != null) {
            res += PREFIX_DELTA;
        }
        return res;
    }
}
