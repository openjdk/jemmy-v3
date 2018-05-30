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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import org.jemmy.action.GetAction;
import org.jemmy.control.ControlType;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.env.Environment;
import org.jemmy.input.SelectionText;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.Focusable;

/**
 *
 * @author shura
 * @author erikgreijus
 */
@ControlType(Text.class)
public class TextWrap<T extends Text> extends ControlWrap<T> implements Focusable {

    class FocusableSelectionText extends SelectionText implements Focusable {
        protected ClickFocus focuser;
        private final TextWrap textWrap;

        public FocusableSelectionText(TextWrap textWrap) {
            super(textWrap);
            this.textWrap = textWrap;
        }

        public double position() {
            return textWrap.position();
        }

        public String text() {
            return textWrap.text();
        }

        public String tooltip() {
            return textWrap.tooltip();
        }

        public double anchor() {
            return textWrap.anchor();
        }

        public ClickFocus focuser() {
            if (focuser == null) {
                focuser = new ClickFocus();
            }
            return focuser;
        }
    }

    FocusableSelectionText text = new FocusableSelectionText(this) ;

    public TextWrap(Environment env, T node) {
        super(env, node);
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

    @Property(Wrap.TOOLTIP_PROP_NAME)
    public String tooltip() {
        GetAction<String> action = new GetAction<String>() {

            @Override
            public void run(Object... parameters) {
                setResult(getControl().getToolTipText());
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
                setResult(getControl().getCaretPosition());
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
                setResult((selection.x == getControl().getCaretPosition()) ?
                    selection.y : selection.x);
            }
        };
        getEnvironment().getExecutor().execute(getEnvironment(), true, action);
        return action.getResult();
    }

    @Override
    public <INTERFACE extends ControlInterface> boolean is(Class<INTERFACE> interfaceClass) {
        if(interfaceClass.isAssignableFrom(SelectionText.class)) {
            return true;
        }
        if(interfaceClass.equals(Focusable.class)) {
            return true;
        }
        return super.is(interfaceClass);
    }

    @Override
    public <INTERFACE extends ControlInterface> INTERFACE as(Class<INTERFACE> interfaceClass) {
        if(interfaceClass.isAssignableFrom(SelectionText.class)) {
            return (INTERFACE) text;
        }
        if(interfaceClass.isAssignableFrom(Focusable.class)) {
            return (INTERFACE) text;
        }
        return super.as(interfaceClass);
    }


}
