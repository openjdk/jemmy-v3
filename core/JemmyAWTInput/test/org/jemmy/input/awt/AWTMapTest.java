/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.input.awt;


import java.util.Arrays;
import java.util.HashSet;
import org.jemmy.JemmyException;
import org.jemmy.input.awt.AWTMap;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Modifier;
import org.jemmy.interfaces.Mouse.MouseButton;
import static org.jemmy.interfaces.Keyboard.KeyboardButtons.*;
import org.jemmy.interfaces.Keyboard.KeyboardModifiers;
import org.jemmy.interfaces.Mouse.MouseButtons;
import org.jemmy.interfaces.Mouse.MouseModifiers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.awt.event.KeyEvent.*;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;


/**
 *
 * @author Alexander Kouznetsov <mrkam@mail.ru>
 */
public class AWTMapTest {

    public AWTMapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    private final KeyboardButton [] jemmyKeyboardButtons = new KeyboardButton [] { A, ADD, D5, F5, NUMPAD5, OPEN_BRACKET };
    private final int [] awtKeyboardButtons = new int [] { VK_A, VK_ADD, VK_5, VK_F5, VK_NUMPAD5, VK_OPEN_BRACKET };
    /**
     * Test of convert method, of class AWTMap.
     */
    @Test
    public void testConvert_KeyboardKeyboardButton() {
        System.out.println("convert");
        for(int i = 0; i < jemmyKeyboardButtons.length; i++) {
            int result = new AWTMap().convert(jemmyKeyboardButtons[i]);
            assertEquals("Failed check for " + jemmyKeyboardButtons[i], awtKeyboardButtons[i], result);
        }
    }

    private final Modifier[][] jemmyModifierCombinations = new Modifier [][] {
            { KeyboardModifiers.SHIFT_DOWN_MASK },
            { KeyboardModifiers.CTRL_DOWN_MASK, KeyboardModifiers.SHIFT_DOWN_MASK },
            { KeyboardModifiers.CTRL_DOWN_MASK, KeyboardModifiers.ALT_DOWN_MASK, KeyboardModifiers.SHIFT_DOWN_MASK },
            { MouseModifiers.BUTTON1_DOWN_MASK },
            { MouseModifiers.BUTTON1_DOWN_MASK, KeyboardModifiers.SHIFT_DOWN_MASK },
    };
    private final int[] awtModifierCombinations = new int [] {
            SHIFT_DOWN_MASK,
            CTRL_DOWN_MASK | SHIFT_DOWN_MASK,
            CTRL_DOWN_MASK | ALT_DOWN_MASK | SHIFT_DOWN_MASK,
            BUTTON1_DOWN_MASK,
            BUTTON1_DOWN_MASK | SHIFT_DOWN_MASK
    };

    /**
     * Test of convert method, of class AWTMap.
     */
    @Test
    public void testConvert_ModifierArr() {
        System.out.println("convert");
        for(int i = 0; i < jemmyModifierCombinations.length; i++) {
            Modifier[] modifiers = jemmyModifierCombinations[i];
            int expResult = awtModifierCombinations[i];
            int result = new AWTMap().convert(modifiers);
            assertEquals("Failed check for " + Arrays.toString(modifiers), expResult, result);
        }
    }

    private final MouseButton [] jemmyMouseButtons = new MouseButton [] { MouseButtons.BUTTON1 };
    private final int [] awtMouseButtons = new int [] { BUTTON1_MASK };

    /**
     * Test of convert method, of class AWTMap.
     */
    @Test
    public void testConvert_MouseMouseButton() {
        System.out.println("convert");
        for(int i = 0; i < jemmyMouseButtons.length; i++) {
            MouseButton button = jemmyMouseButtons[i];
            int expResult = awtMouseButtons[i];
            int result = new AWTMap().convert(button);
            assertEquals("Failed check for " + button, expResult, result);
        }
    }

    /**
     * Test of convertKeyboardButton method, of class AWTMap.
     */
    @Test
    public void testConvertKeyboardButton() {
        System.out.println("convertKeyboardButton");
        for (int i = 0; i < awtKeyboardButtons.length; i++) {
            int key = awtKeyboardButtons[i];
            KeyboardButton expResult = jemmyKeyboardButtons[i];
            KeyboardButton result = new AWTMap().convertKeyboardButton(key);
            assertEquals("Failed check for " + expResult, expResult, result);
        }
    }

    /**
     * Test of convertModifiers method, of class AWTMap.
     */
    @Test
    public void testConvertModifiers() {
        System.out.println("convertModifiers");
        for (int i = 0; i < awtModifierCombinations.length; i ++) {
            int modifiers = awtModifierCombinations[i];
            Modifier[] expResult = jemmyModifierCombinations[i];
            Modifier[] result = new AWTMap().convertModifiers(modifiers);
            assertEquals("Failed check with " + Arrays.toString(expResult), new HashSet<Modifier>(Arrays.asList(expResult)), new HashSet<Modifier>(Arrays.asList(result)));
        }
    }

    /**
     * Test of convertMouseButton method, of class AWTMap.
     */
    @Test
    public void testConvertMouseButton() {
        System.out.println("convertMouseButton");
        for (int i = 0; i < awtMouseButtons.length; i++) {
            int button = awtMouseButtons[i];
            MouseButton expResult = jemmyMouseButtons[i];
            MouseButton result = new AWTMap().convertMouseButton(button);
            assertEquals("Check failed with " + expResult, expResult, result);
        }
    }

    @Test
    public void testGetException() {
        try {
            new AWTMap().convert(new KeyboardButton() {});
            fail("No JemmyException");
        } catch(JemmyException e) {
        } catch(Exception e) {
            fail("Not a JemmyException");
        }
        try {
            new AWTMap().convert(new MouseButton() {});
            fail("No JemmyException");
        } catch(JemmyException e) {
        } catch(Exception e) {
            fail("Not a JemmyException");
        }
        try {
            new AWTMap().convert(new Modifier() {}, new Modifier() {});
            fail("No JemmyException");
        } catch(JemmyException e) {
        } catch(Exception e) {
            fail("Not a JemmyException");
        }
        try {
            new AWTMap().convertKeyboardButton(-1);
            fail("No JemmyException");
        } catch(JemmyException e) {
        } catch(Exception e) {
            fail("Not a JemmyException");
        }
        try {
            new AWTMap().convertMouseButton(-1);
            fail("No JemmyException");
        } catch(JemmyException e) {
        } catch(Exception e) {
            fail("Not a JemmyException");
        }
    }

}
