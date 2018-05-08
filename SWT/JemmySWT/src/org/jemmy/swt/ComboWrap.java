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

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.input.KeyboardSelectable;
import org.jemmy.input.KeyboardSelector;
import org.jemmy.input.SelectionText;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Focusable;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jemmy.interfaces.Keyboard.KeyboardModifier;
import org.jemmy.interfaces.Selector;

/**
 *
 * @author shura, erikgreijus
 * @param <T>
 */
@ControlType(Combo.class)
public class ComboWrap<T extends Combo> extends ControlWrap<T> implements
        KeyboardSelectable<String>, Focusable {

    public static final String DISMISSAL_BUTTON_PROP = ComboWrap.class.getName() + ".dismissal.button";
    public static final String DISMISSAL_MODIFIER_PROP = ComboWrap.class.getName() + ".dismissal.modifier";
    private final KeyboardButtons dismissalButton;
    private final KeyboardModifier[] dismissalModifier;

    class ComboKeyboardSelector<T> extends KeyboardSelector<T> {

        private final Wrap<?> comboWrap;

        public ComboKeyboardSelector(Wrap<?> wrap, KeyboardSelectable<T> control) {
            super(wrap, control);
            comboWrap = wrap;
        }

        @Override
        public void select(T state) {
            super.select(state);
            comboWrap.keyboard().pushKey(dismissalButton, dismissalModifier);
        }
    }

    class FocusableSelectionText extends SelectionText implements Focusable {

        protected ClickFocus focuser;
        private final ComboWrap<T> comboWrap;

        public FocusableSelectionText(ComboWrap<T> textWrap) {
            super(textWrap);
            this.comboWrap = textWrap;
        }

        @Override
        public double position() {
            return comboWrap.position();
        }

        @Override
        public String text() {
            return comboWrap.text();
        }

        @Override
        public double anchor() {
            return comboWrap.anchor();
        }

        @Override
        public ClickFocus focuser() {
            if (focuser == null) {
                focuser = new ClickFocus();
            }
            return focuser;
        }
    }

    FocusableSelectionText text = new FocusableSelectionText(this);
    private ComboKeyboardSelector<String> selector = null;

    public ComboWrap(Environment env, T node) {
        super(env, node);
        KeyboardButtons defaultDismissalButton = System.getProperty("os.name").toLowerCase().contains("windows") ? KeyboardButtons.ESCAPE : KeyboardButtons.SPACE;
        KeyboardModifier[] defaultDismissalModifiers = new KeyboardModifier[0];
        dismissalButton = env.getProperty(KeyboardButtons.class,
                DISMISSAL_BUTTON_PROP, defaultDismissalButton);
        dismissalModifier = (KeyboardModifier[]) env.getProperty(KeyboardModifier[].class,
                DISMISSAL_MODIFIER_PROP, defaultDismissalModifiers);
    }

    @Override
    public java.util.List<String> getStates() {
        return new GetAction<java.util.List<String>>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(Arrays.asList(getControl().getItems()));
            }
        }.dispatch(getEnvironment());
    }

    @Override
    public String getState() {
        return new GetAction<String>() {

            @Override
            public void run(Object... parameters) throws Exception {
                if (getControl().getSelectionIndex() >= 0) {
                    setResult(getControl().getItem(getControl().getSelectionIndex()));
                } else {
                    setResult(null);
                }
            }
        }.dispatch(getEnvironment());
    }

    @Override
    public int selection() {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().getSelectionIndex());
            }
        }.dispatch(getEnvironment());
    }

    @Override
    public Selector<String> selector() {
        if (selector == null) {
            selector = new ComboKeyboardSelector<>(this, this);
        }
        return selector;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    @Override
    public int index(String item) {
        return new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) throws Exception {
                setResult(getControl().indexOf(item));
            }
        }.dispatch(getEnvironment());
    }

    @Override
    public ClickFocus focuser() {
        if (focuser == null) {
            super.focuser().clickCount = 2;
        }
        return focuser;
    }

    @Override
    public boolean hasFocus() {
        GetAction<Boolean> action;
        action = new GetAction<Boolean>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().isFocusControl() || getControl().getListVisible());
            }

            @Override
            public String toString() {
                return "Getting focus state for " + getControl();
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Property(Wrap.POSITION_PROP_NAME)
    public Integer position() {
        GetAction<Integer> action = new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getSelection().x);
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Property(SelectionText.SELECTION_ANCHOR_PROP_NAME)
    public Integer anchor() {
        GetAction<Integer> action = new GetAction<Integer>() {

            @Override
            public void run(Object... parameters) {
                Point selection = getControl().getSelection();
                setResult((selection.x == getControl().getCaretPosition())
                        ? selection.y : selection.x);
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Property(Wrap.TEXT_PROP_NAME)
    @Override
    public String text() {
        GetAction<String> action = new GetAction<String>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getText());
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if (interfaceClass.isAssignableFrom(SelectionText.class)) {
            return true;
        }
        if (interfaceClass.equals(Focusable.class)) {
            return true;
        }
        return super.is(interfaceClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if (interfaceClass.isAssignableFrom(SelectionText.class)) {
            return (INTERFACE) text;
        }
        if (interfaceClass.isAssignableFrom(Focusable.class)) {
            return (INTERFACE) text;
        }
        return super.as(interfaceClass);
    }
}
