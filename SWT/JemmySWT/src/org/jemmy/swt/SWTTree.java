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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jemmy.TimeoutExpiredException;
import org.jemmy.action.GetAction;
import org.jemmy.control.Wrap;
import org.jemmy.env.Timeout;
import org.jemmy.input.StringTree;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Keyboard.KeyboardButton;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import org.jemmy.interfaces.TreeSelector;
import org.jemmy.lookup.LookupCriteria;
import org.jemmy.resources.StringComparePolicy;
import org.jemmy.swt.lookup.ByItemStringsLookup;
import org.jemmy.timing.State;

/**
 * @author shura, erikgreijus
 */
public class SWTTree extends StringTree<TreeItem> {

    public static final Timeout WAIT_NODE_TIMEOUT = new Timeout(SWTTree.class.getName() + ".wait.node.timeout", 1000);
    public static final Timeout WAIT_NODE_EXPANDED_TIMEOUT = new Timeout(SWTTree.class.getName() + ".wait.node.expanded.timeout", 100);
    public static final Timeout AFTER_MOVE_SLEEP = new Timeout(SWTTree.class.getName() + ".after.move.sleep.timeout", 100);
    public static final String EXPAND_BUTTON_PROP = SWTTree.class.getName() + ".expand.button";
    public static final String EXPAND_MODIFIER_PROP = SWTTree.class.getName() + ".expand.modifier";

    private static final int MAX_MOVE_TRIES = 5;

    static {
        Shells.SHELLS.getEnvironment().initTimeout(WAIT_NODE_TIMEOUT);
        Shells.SHELLS.getEnvironment().initTimeout(WAIT_NODE_EXPANDED_TIMEOUT);
        Shells.SHELLS.getEnvironment().initTimeout(AFTER_MOVE_SLEEP);
    }
    final TreeWrap<? extends Tree> owner;
    SWTTreeSelector selector = null;
    private final KeyboardButton expandButton;
    private final KeyboardModifier[] expandModifier;

    public SWTTree(TreeWrap<? extends Tree> owner) {
        super(owner.getEnvironment());
        this.owner = owner;

        KeyboardButton defaultExpandButton = System.getProperty("os.name")
                .contains("Linux") ? KeyboardButtons.ADD
                        : KeyboardButtons.RIGHT;
        KeyboardModifier[] defaultExpandModifier = new KeyboardModifier[0];

        expandButton = (KeyboardButton) owner.getEnvironment().getProperty(KeyboardButtons.class,
                EXPAND_BUTTON_PROP, defaultExpandButton);
        expandModifier = (KeyboardModifier[]) owner.getEnvironment().getProperty(KeyboardModifier[].class,
                EXPAND_MODIFIER_PROP, defaultExpandModifier);
    }

    @Override
    protected LookupCriteria<TreeItem> createCriteria(String text, StringComparePolicy policy) {
        return new ByItemStringsLookup<>(text, policy);
    }

    @Override
    public TreeSelector<TreeItem> selector() {
        if (selector == null) {
            selector = new SWTTreeSelector();
        }
        return selector;
    }

    @Override
    public Class<TreeItem> getType() {
        return TreeItem.class;
    }

    private boolean getExpanded(final TreeItem item) {
        return new GetAction<Boolean>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(item.getExpanded());
            }
        }.dispatch(owner.getEnvironment());
    }

    private class SWTTreeSelector implements TreeSelector<TreeItem> {

        private List<LookupCriteria<TreeItem>> criteriaList;

        @Override
        public Wrap<? extends TreeItem> select(LookupCriteria<TreeItem>... criteria) {
            criteriaList = Arrays.asList(criteria);
            owner.as(Focusable.class).focuser().focus();
            TreeItem[] items = new GetAction<TreeItem[]>() {

                @Override
                public void run(Object... parameters) throws Exception {
                    setResult(owner.getControl().getItems());
                }
            }.dispatch(owner.getEnvironment());
            return new ItemWrap<>(owner, waitAndExpand(Arrays.asList(items), Arrays.asList(criteria)));
        }

        protected TreeItem waitAndExpand(final List<TreeItem> items, final List<LookupCriteria<TreeItem>> criteria) {
            final TreeItem next = owner.getEnvironment().getWaiter(WAIT_NODE_TIMEOUT).ensureState(new State<TreeItem>() {

                @Override
                public TreeItem reached() {
                    for (TreeItem ti : items) {
                        if (criteria.get(0).check(ti)) {
                            return ti;
                        }
                    }
                    return null;
                }

                @Override
                public String toString() {
                    return getReadableCriteriaList();
                }
            });
            int numberOfTries = 0;
            // determine if we really need to walk the tree to this non-leaf node (and expand it) in order to be able to find the next node
            boolean isExpanded = criteria.size() > 1 && getExpanded(next);
            while (!next.equals(owner.getSelectedItem()) && !isExpanded) {
                int from = owner.getItems().indexOf(owner.getSelectedItem());
                int to = owner.getItems().indexOf(next);
                if (numberOfTries > 0) {
                    if (from == -1) {
                        /* in case we were unable to select the desired item,
                         * focus (but not selection) is likely on the last or the
                         * only item in the tree so pushing the down or up key won't select the item.
                         */
                        owner.keyboard().pushKey(KeyboardButtons.SPACE);
                        owner.getEnvironment().getTimeout(AFTER_MOVE_SLEEP).sleep();
                    } else if (numberOfTries > 1) {
                        // we could be stuck on an editable field so keyboard navigation won't work until we escape out
                        // of that. Only do this if the first retry round fails as pushing escape may close a crucial dialog
                        owner.keyboard().pushKey(KeyboardButtons.ESCAPE);
                        owner.getEnvironment().getTimeout(AFTER_MOVE_SLEEP).sleep();
                    }
                    from = owner.getItems().indexOf(owner.getSelectedItem());
                }

                KeyboardButton btt = (to > from) ? KeyboardButtons.DOWN : KeyboardButtons.UP;
                for (int i = 0; i < Math.abs(to - from); i++) {
                    owner.keyboard().pushKey(btt);
                    owner.getEnvironment().getTimeout(AFTER_MOVE_SLEEP).sleep();
                }
                numberOfTries++;
                if (numberOfTries >= MAX_MOVE_TRIES) {
                    if (!next.equals(owner.getSelectedItem())) {
                        throw new TimeoutExpiredException("Unable to select the tree item with the following path " + getReadableCriteriaList());
                    } else {
                        break;
                    }
                }
            }

            if (criteria.size() > 1) {
                if (!getExpanded(next)) {
                    owner.keyboard().pushKey(expandButton, expandModifier);
                    owner.getEnvironment().getTimeout(AFTER_MOVE_SLEEP).sleep();
                    owner.keyboard().pushKey(KeyboardButtons.DOWN);
                    owner.getEnvironment().getTimeout(AFTER_MOVE_SLEEP).sleep();
                }
                owner.getEnvironment().getWaiter(WAIT_NODE_EXPANDED_TIMEOUT).
                        ensureValue(true, new State<Boolean>() {

                            @Override
                            public Boolean reached() {
                                return getExpanded(next);
                            }
                        });
                return waitAndExpand(getItems(next), criteria.subList(1, criteria.size()));
            } else {
                return next;
            }
        }

        protected List<TreeItem> getItems(final TreeItem next) {
            return new GetAction<List<TreeItem>>() {

                @Override
                public void run(Object... parameters) throws Exception {
                    setResult(Arrays.asList(next.getItems()));
                }
            }.dispatch(owner.getEnvironment());
        }

        private String getReadableCriteriaList() {
            List<String> result = new ArrayList<>();
            criteriaList.stream().forEach((lookupCriteria) -> {
                result.add(lookupCriteria.toString());
            });
            return result.toString();
        }
    }

    private LookupCriteria<TreeItem> thisCriteria(final TreeItem item) {
        return new LookupCriteria<TreeItem>() {

            @Override
            public boolean check(TreeItem control) {
                return control == item;
            }
        };
    }
}
