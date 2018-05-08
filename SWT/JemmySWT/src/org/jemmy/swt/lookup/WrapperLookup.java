/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.swt.lookup;

import org.eclipse.swt.widgets.Widget;
import org.jemmy.env.Environment;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.swt.Shells;


/**
 *
 * @author shura
 */
public class WrapperLookup<T extends Widget> extends QueueLookup<T> {

    private final LookupCriteria<T> subCriteria;

    public WrapperLookup(LookupCriteria<T> subCriteria) {
        this(Shells.SHELLS.getEnvironment(), subCriteria);
    }

    public WrapperLookup(Environment env, LookupCriteria<T> subCriteria) {
        super(env);
        this.subCriteria = subCriteria;
    }

    @Override
    protected boolean doCheck(T control) {
        return subCriteria.check(control);
    }

}
