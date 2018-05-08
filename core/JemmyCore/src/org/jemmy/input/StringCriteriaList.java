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
package org.jemmy.input;

import org.jemmy.env.Environment;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.resources.StringComparePolicy;

/**
 * Defines logic of converting a list of strings (aka titles, texts) into a list of
 * LookupCriteria. It is to be extended for hierarchical data support
 * where string identification is applicable (such as trees, menus).
 * @author shura
 */
public abstract class StringCriteriaList<T> {

    private StringComparePolicy policy;

    public final static String STRING_COMPARE_POLICY_PROP_NAME =
            StringCriteriaList.class.getName() + ".string.compare.policy";

    protected StringCriteriaList(Environment env) {
        policy = (StringComparePolicy) env.getProperty(STRING_COMPARE_POLICY_PROP_NAME, StringComparePolicy.EXACT);
    }

    protected StringCriteriaList(StringComparePolicy policy) {
        this.policy = policy;
    }

    public StringComparePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(StringComparePolicy policy) {
        this.policy = policy;
    }

    protected LookupCriteria<T>[] createCriteriaList(String[] texts) {
        LookupCriteria[] criteria = new LookupCriteria[texts.length];
        for(int i = 0; i < texts.length; i++) {
            criteria[i] = createCriteria(texts[i], policy);
        }
        return criteria;
    }

    protected abstract LookupCriteria<T> createCriteria(String text, StringComparePolicy policy);
}
