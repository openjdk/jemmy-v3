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
package org.jemmy.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.jemmy.control.Wrap;
import org.jemmy.input.StringMenuSelectableOwner;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.MenuSelectable;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.resources.StringComparePolicy;

/**
 *
 * @author shura, erikgreijus
 */
public class SWTMenuOwner extends StringMenuSelectableOwner<MenuItem> {

    final SWTMenu menu;
    final Wrap<? extends Control> owner;

    public SWTMenuOwner(Wrap<? extends Control> owner, boolean isBar) {
        super(owner);
        this.owner = owner;
        menu = new SWTMenu(owner, isBar);
    }

    @Override
    public MenuSelectable menu() {
        owner.keyboard().pushKey(KeyboardButtons.F10);
        owner.getEnvironment().getTimeout(SWTMenu.BETWEEN_KEYS_SLEEP).sleep();
        return menu;
    }

    @Override
    public Class<MenuItem> getType() {
        return MenuItem.class;
    }

    @Override
    protected LookupCriteria<MenuItem> createCriteria(String text, StringComparePolicy policy) {
        return SWTMenu.createCriteria(text, policy);
    }

}
