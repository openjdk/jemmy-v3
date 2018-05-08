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


import java.util.Enumeration;
import java.util.Hashtable;
import org.jemmy.env.Environment;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import static org.jemmy.interfaces.Keyboard.KeyboardButtons.*;
import static org.jemmy.interfaces.Keyboard.KeyboardModifiers.*;



/**
 *
 * Default implementation of CharBindingMap interface.
 * Provides a mapping for the following symbols:<BR>
 * @see org.jemmy.CharBindingMap
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public class DefaultCharBindingMap implements CharBindingMap<KeyboardButton, KeyboardModifier> {

    private Hashtable<Character, CharKey> chars;

    /**
     * Constructor.
     */
    public DefaultCharBindingMap() {
        initMap();
    }

    /**
     * Returns the code of the primary key used to type a symbol.
     * @param c Character.
     * @return a KeyboardButton.
     * @see CharBindingMap#getCharKey(char)
     */
    public KeyboardButton getCharKey(char c) {
        CharKey charKey = (CharKey)chars.get(new Character(c));
        if (charKey != null) {
            return charKey.key;
        } else {
            return null;
        }
    }

    /**
     * Returns the modifiers that should be pressed to type a symbol.
     * @param c Character.
     * @return an array of KeyboardModifier constants.
     * @see CharBindingMap#getCharModifiers(char)
     * @see KeyboardModifier
     */
    public KeyboardModifier[] getCharModifiers(char c) {
        CharKey charKey = (CharKey)chars.get(new Character(c));
        if(charKey != null) {
            return charKey.modifiers;
        } else {
            return null;
        }
    }

    /**
     * Returns an array of all supported chars.
     * @return an array of chars representing the supported chars values.
     */
    public char[] getSupportedChars() {
        char[] charArray = new char[chars.size()];
        Enumeration keys = chars.keys();
        int index = 0;
        while(keys.hasMoreElements()) {
            charArray[index] = ((Character)keys.nextElement()).charValue();
        }
        return(charArray);
    }

    /**
     * Removes a char from supported.
     * @param c Symbol code.
     */
    public void removeChar(char c) {
        chars.remove(new Character(c));
    }

    /**
     * Adds a char to supported.
     * @param c Symbol code.
     * @param key key code.
     * @param modifiers a combination of InputEvent MASK fields.
     */
    public void addChar(char c, KeyboardButton key, KeyboardModifier... modifiers) {
        chars.put(new Character(c), new CharKey(key, modifiers));
    }

    private void initMap() {
        chars = new Hashtable<Character, CharKey>();
        //first add letters and digits represented by . fields
        KeyboardButtons[] buttons = KeyboardButtons.values();
        for(int i = 0; i < buttons.length; i++) {
            String name = buttons[i].name();
            String letter;
            if (name.length() == 1 && Character.isLetter(name.charAt(0))) {
                letter = name;
            } else if (name.length() == 2 && name.startsWith("D") && Character.isDigit(name.charAt(1))) {
                letter = name.substring(1);
            } else {
                continue;
            }
            addChar(letter.toLowerCase().charAt(0), buttons[i]);
            if(!letter.toUpperCase().equals(letter.toLowerCase())) {
                addChar(letter.toUpperCase().charAt(0), buttons[i], SHIFT_DOWN_MASK);
            }
        }
        //add special simbols
        addChar('\t', TAB);
        addChar(' ', SPACE);
        addChar('!', D1 , SHIFT_DOWN_MASK);
        addChar('"', QUOTE , SHIFT_DOWN_MASK);
        addChar('#', D3 , SHIFT_DOWN_MASK);
        addChar('$', D4 , SHIFT_DOWN_MASK);
        addChar('%', D5 , SHIFT_DOWN_MASK);
        addChar('&', D7 , SHIFT_DOWN_MASK);
        addChar('\'', QUOTE);
        addChar('(', D9 , SHIFT_DOWN_MASK);
        addChar(')', D0 , SHIFT_DOWN_MASK);
        addChar('*', D8 , SHIFT_DOWN_MASK);
        addChar(',', COMMA);
        addChar('-', MINUS);
        addChar('.', PERIOD);
        addChar('/', SLASH);
        addChar(';', SEMICOLON);
        addChar('<', COMMA , SHIFT_DOWN_MASK);
        addChar('>', PERIOD , SHIFT_DOWN_MASK);
        addChar('?', SLASH , SHIFT_DOWN_MASK);
        addChar('@', D2 , SHIFT_DOWN_MASK);
        addChar('[', OPEN_BRACKET);
        addChar(']', CLOSE_BRACKET);
        addChar('^', D6 , SHIFT_DOWN_MASK);
        addChar('_', MINUS , SHIFT_DOWN_MASK);
        addChar('`', BACK_QUOTE);
        addChar('{', OPEN_BRACKET , SHIFT_DOWN_MASK);
        addChar('|', BACK_SLASH , SHIFT_DOWN_MASK);
        addChar('}', CLOSE_BRACKET, SHIFT_DOWN_MASK);
        addChar('\n', ENTER);

       if ("sv".equals(Environment.getEnvironment().getProperty("LANG"))) {
           addChar('+', PLUS);
           addChar(':', PERIOD, SHIFT_DOWN_MASK);
           addChar('\\', PLUS, CTRL_DOWN_MASK, ALT_DOWN_MASK);
           addChar('~', DEAD_DIAERESIS, CTRL_DOWN_MASK, ALT_DOWN_MASK);
           addChar('=', D0, SHIFT_DOWN_MASK);
       } else {
           addChar('+', EQUALS , SHIFT_DOWN_MASK);
           addChar(':', SEMICOLON , SHIFT_DOWN_MASK);
           addChar('\\', BACK_SLASH);
           addChar('~', BACK_QUOTE , SHIFT_DOWN_MASK);
           addChar('=', EQUALS);
       }
    }

    private static class CharKey {
        public KeyboardButton key;
        public KeyboardModifier modifiers[];
        public CharKey(KeyboardButton key, KeyboardModifier... modifiers) {
            this.key = key;
            this.modifiers = modifiers;
        }
    }

}
