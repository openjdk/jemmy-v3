/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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
package org.jemmy.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.jemmy.JemmyException;
import org.jemmy.control.Wrap;
import org.jemmy.env.Timeout;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.MenuSelectable;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.resources.StringComparePolicy;
import org.jemmy.swt.lookup.ByTextItem;
import org.jemmy.timing.State;

/**
 *
 * @author shura, erikgreijus
 */
public class SWTMenu implements MenuSelectable<MenuItem> {

    public static final Timeout BETWEEN_KEYS_SLEEP = new Timeout(
            SWTMenu.class.getName() + ".between.keys.timeout", 100);
    public static final Timeout SUBMENU_WAIT_TIMEOUT = new Timeout(
            SWTMenu.class.getName() + ".submenu.wait.timeout", 1000);
    public static final String SELECTION_BUTTON_PROP
            = SWTMenu.class.getName() + ".selection.button";
    public static final String ESCAPE_BUTTON_PROP
            = SWTMenu.class.getName() + ".escape.button";
    public static final String MULTI_LEVEL_DISCARD_PROP
            = SWTMenu.class.getName() + ".multi.level.discard";
    public static final String SKIPS_DISABLED_PROP
            = SWTMenu.class.getName() + ".skips.disabled";
    Wrap<? extends Control> owner;
    boolean isBar;
    private final KeyboardButton selectionButton;
    private final KeyboardButton escapeButton;
    private final boolean skipsDisabled;
    private final boolean multiLevelDiscard;

    static {
        Shells.SHELLS.getEnvironment().initTimeout(BETWEEN_KEYS_SLEEP);
        Shells.SHELLS.getEnvironment().initTimeout(SUBMENU_WAIT_TIMEOUT);
    }

    public SWTMenu(Wrap<? extends Control> owner, boolean isBar) {
        this.owner = owner;
        this.isBar = isBar;

        KeyboardButtons defaultSelectionButton = System.getProperty("os.name")
                .contains("Linux") ? KeyboardButtons.SPACE : KeyboardButtons.ENTER;
        KeyboardButtons defaultEscapeButton = KeyboardButtons.ESCAPE;
        Boolean defaultSkipsDisabled = System.getProperty("os.name").contains("Linux");
        Boolean defaultMultiLevelDiscard = System.getProperty("os.name").contains("Windows");

        selectionButton = (KeyboardButton) owner.getEnvironment().getProperty(KeyboardButton.class,
                SELECTION_BUTTON_PROP, defaultSelectionButton);
        escapeButton = (KeyboardButton) owner.getEnvironment().getProperty(KeyboardButton.class,
                ESCAPE_BUTTON_PROP, defaultEscapeButton);
        skipsDisabled = owner.getEnvironment().getProperty(Boolean.class,
                SKIPS_DISABLED_PROP, defaultSkipsDisabled);
        multiLevelDiscard = owner.getEnvironment().getProperty(Boolean.class,
                MULTI_LEVEL_DISCARD_PROP, defaultMultiLevelDiscard);
    }

    @Override
    public void push(LookupCriteria<MenuItem>... criteria) {
        select(criteria);
        owner.keyboard().pushKey(selectionButton);
        owner.getEnvironment().getTimeout(BETWEEN_KEYS_SLEEP).sleep();
    }

    @Override
    public void push(boolean desiredSelectionState, LookupCriteria<MenuItem>... criteria) {
        if (desiredSelectionState != getSelection(select(criteria))) {
            owner.keyboard().pushKey(selectionButton);
        } else {
            pushEscape((multiLevelDiscard) ? criteria.length : 1);
        }
        owner.getEnvironment().getTimeout(BETWEEN_KEYS_SLEEP).sleep();
    }

    @Override
    public boolean getState(LookupCriteria<MenuItem>... criteria) {
        boolean result = getSelection(select(criteria));
        pushEscape((multiLevelDiscard) ? criteria.length : 1);
        return result;
    }

    private void pushEscape(int times) {
        for (int i = 0; i < times; i++) {
            owner.keyboard().pushKey(escapeButton);
            owner.getEnvironment().getTimeout(BETWEEN_KEYS_SLEEP).sleep();
        }
    }

    private boolean getSelection(Wrap<MenuItem> menuItem) {
        final boolean[] result = new boolean[]{false};
        Display.getDefault().syncExec(() -> {
            result[0] = menuItem.getControl().getSelection();
        });
        return result[0];
    }

    @Override
    public Wrap<MenuItem> select(LookupCriteria<MenuItem>... criteria) {
        if (criteria.length == 0) {
            throw new IllegalArgumentException("Menu path length should be greater than 0");
        }
        final org.eclipse.swt.widgets.Menu[] start = new org.eclipse.swt.widgets.Menu[1];
        if (isBar) {
            if (!(owner.getControl() instanceof Shell)) {
                throw new JemmyException("Menu bars are in shells");
            }
            Display.getDefault().syncExec(() -> {
                start[0] = ((Shell) owner.getControl()).getMenuBar();
            });
        } else {
            Display.getDefault().syncExec(() -> {
                start[0] = owner.getControl().getMenu();
            });
        }
        return select(start[0], Arrays.asList(criteria), true);
    }

    //selects hierarchically
    //assumes first item in the menu is selected
    private Wrap<MenuItem> select(final org.eclipse.swt.widgets.Menu menu, final List<LookupCriteria<MenuItem>> criteria, boolean entry) {
        waitVisible(menu);
        final int[] moveTimes = new int[]{0};
        final MenuItem[] current = new MenuItem[]{null};
        //find the one we're looking for
        Display.getDefault().syncExec(() -> {
            for (MenuItem item : menu.getItems()) {
                if (criteria.get(0).check(item)) {
                    current[0] = item;
                    break;
                }
                if ((item.isEnabled() || !skipsDisabled) && !item.toString().contains("{|}")) {
                    moveTimes[0]++;
                }
            }
        });

        if (current[0] == null) {
            throw new JemmyException("Unable to find menu item conforming criteria "
                    + criteria.get(0).toString(), menu);
        }

        boolean horizontal = entry ? isBar : false;
        move(moveTimes[0], horizontal);

        if (criteria.size() > 1) {
            final org.eclipse.swt.widgets.Menu[] nextMenu = new org.eclipse.swt.widgets.Menu[1];
            List<LookupCriteria<MenuItem>> nextCriteria = new ArrayList<>();
            nextCriteria.addAll(criteria);
            nextCriteria.remove(0);
            KeyboardButton key = horizontal ? KeyboardButtons.DOWN : KeyboardButtons.RIGHT;
            owner.keyboard().pushKey(key);
            owner.getEnvironment().getTimeout(BETWEEN_KEYS_SLEEP).sleep();
            Display.getDefault().syncExec(() -> {
                nextMenu[0] = current[0].getMenu();
            });
            if (nextMenu[0] == null) {
                throw new JemmyException("No submenu while criteria list length is still " + criteria.size(),
                        criteria.get(0));
            }
            return select(nextMenu[0], nextCriteria, false);
        }
        return new ItemWrap<>(owner, current[0]);
    }

    void waitVisible(final org.eclipse.swt.widgets.Menu menu) {
        owner.getEnvironment().getWaiter(SUBMENU_WAIT_TIMEOUT).waitValue(true,
                new State<Boolean>() {

                    boolean state = false;

                    @Override
                    public Boolean reached() {
                        Display.getDefault().syncExec(() -> {
                            state = menu.isVisible();
                        });
                        return state;
                    }
                });
    }

    private void move(int moveTimes, boolean horizontal) {
        KeyboardButton key = horizontal ? KeyboardButtons.RIGHT : KeyboardButtons.DOWN;
        for (int i = 0; i < moveTimes; i++) {
            owner.keyboard().pushKey(key);
            owner.getEnvironment().getTimeout(BETWEEN_KEYS_SLEEP).sleep();
        }
    }

    static LookupCriteria<MenuItem> createCriteria(String text, StringComparePolicy policy) {
        return new ByTextItem<>(text, policy);
    }

}
