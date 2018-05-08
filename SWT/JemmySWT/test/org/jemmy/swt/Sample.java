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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author shura
 */
public class Sample {

    public static void main(String[] args) {
        //Creates a new display object for the example to go into
        Display display = Display.getDefault();
        //Creates a new shell object
        final Shell shell = new Shell(display);
        //Sets the layout for the shell
        shell.setLayout(new RowLayout());
        final Text lbl = new Text(shell, SWT.DEFAULT);
        lbl.setText("click the button, please.");
        lbl.setBounds(0, 0, 100, 30);
        lbl.setEditable(true);
        Button btn = new Button(shell, SWT.DEFAULT);
        btn.setText("Click me!");
        btn.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                lbl.setText("Now type some new text");
                System.out.println(shell.getLocation());
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
                //System.out.println(arg0);
            }
        });
        Combo combo = new Combo(shell, SWT.DEFAULT);
        combo.add("one");
        combo.add("two");
        combo.add("three");
        combo.add("four");
        combo.select(0);
        List list = new List(shell, SWT.DEFAULT);
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
        list.select(0);
        //Creates the control example - see import statement for location.
        shell.setText("Control Example");
        shell.setBounds(100, 100, 300, 200);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
