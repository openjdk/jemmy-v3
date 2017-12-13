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


package org.jemmy.interfaces;


import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.dock.Shortcut;


/**
 * Interface representing an object which represents an integer value which
 * could be increased or decreased, such as scroll bar, slider, etc.
 * @author shura
 */
public interface CaretOwner extends ControlInterface {
    /**
     *
     * @return
     */
    @Property(Wrap.VALUE_PROP_NAME)
    public double position();
    /**
     *
     * @return
     */
    @Shortcut
    public Caret caret();

    /**
     * Utility method that invokes caret().to(Direction) with correct
     * direction.
     * TODO: Remove this method.
     * @param position
     */
    @Shortcut
    public void to(double position);

}
