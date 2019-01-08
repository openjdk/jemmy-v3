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

import com.company.test.CollectionDock;
import com.company.test.CollectionRoot;
import com.company.test.ListDock;
import com.company.test.ObjectDock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static com.company.test.CollectionRoot.PARENT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DockTest {
    @BeforeClass
    public static void init() {
        CollectionRoot.COLLECTIONS.add(List.of(0, 1, 2));
        CollectionRoot.COLLECTIONS.add(List.of(3, 4, 5, 6));
        CollectionRoot.COLLECTIONS.add(List.of());
        CollectionRoot.COLLECTIONS.add(Set.of(0, 1, 2, 3));
        CollectionRoot.COLLECTIONS.add(Set.of("0", "1", "2"));
    }
    @Test
    public void testCollectionDock() {
        new CollectionDock(PARENT);
        new CollectionDock(PARENT, 3);
        assertTrue(new CollectionDock(PARENT, List.class).control() instanceof List);
        assertTrue(new CollectionDock(PARENT, Set.class, 1).control() instanceof Set);
        assertEquals(new CollectionDock(PARENT, List.class, collection -> collection.size() == 4)
                .getSize(), 4);
        assertTrue(new CollectionDock(PARENT, List.class, collection -> collection.size() == 0)
                .isEmpty());
        assertTrue(new CollectionDock(PARENT, c -> c.size() > 0 && c.iterator().next() instanceof String)
                .control() instanceof Set);
    }
    @Test
    public void testListDock() {
        assertEquals(new ListDock(PARENT, collection -> collection.size() == 4)
                .getSize(), 4);
        assertEquals(new ListDock(PARENT, collection -> collection.size() == 4)
                .getIterator().next(), 3);
    }
    @Test
    public void testObjectDock() {
        ListDock cd = new ListDock(PARENT, c -> c.size() > 3);
        ObjectDock od = new ObjectDock(cd.asParent(), i -> (Integer)i > 5);
        assertEquals(od.control(), 6);
    }
}
