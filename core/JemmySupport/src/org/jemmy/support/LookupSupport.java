/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.support;

import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author shura
 */
public class LookupSupport {
    private final List<SupportParameter> params = new LinkedList<SupportParameter>();
    private final ExecutableElement method;
    private final String description;
    private final TypeElement cls;

    /**
     *
     * @param cls
     * @param method
     * @param description
     */
    public LookupSupport(TypeElement cls, ExecutableElement method, String description) {
        this.cls = cls;
        this.method = method;
        this.description = description;
    }

    /**
     *
     * @return
     */
    public TypeElement getDeclaringType() {
        return cls;
    }

    /**
     *
     * @return
     */
    public List<SupportParameter> getParams() {
        return params;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return
     */
    public ExecutableElement getMethod() {
        return method;
    }

    /**
     *
     * @param another
     * @return
     */
    public boolean equalInTypes(LookupSupport another) {
        if(another.getParams().size() == getParams().size()) {
            for (int i = 0; i < getParams().size(); i++) {
                if(!another.getParams().get(i).getType().toString()
                        .equals(getParams().get(i).getType().toString())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
