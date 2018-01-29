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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 *
 * @author shura
 */
public class Items {

    static Shell shell;

    public static void main(String[] args) {
        Display d = Display.getDefault();
        shell = new Shell(d);
        shell.setText("lala");
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);

        final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);

        //toolbar
        TabItem tab = new TabItem(tabFolder, SWT.NONE);
        tab.setText("Toolbar");
        Composite itemsPanel = new Composite(tabFolder, SWT.BORDER);
        ToolBar bar = new ToolBar(itemsPanel, SWT.BORDER);
        for (int i = 0; i < 3; i++) {
            final ToolItem item = new ToolItem(bar, SWT.PUSH);
            item.setText("" + i);
            item.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    shell.setText("ToolItem " + item.getText());
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                    System.out.println("default selected " + arg0);
                }
            });
        }
        bar.pack();
        tab.setControl(itemsPanel);

        Menu popup = new Menu(bar);
        createMenu(popup);
        bar.setMenu(popup);

        //table
        TabItem tableTab = new TabItem(tabFolder, SWT.NONE);
        tableTab.setText("Table");
        Composite tablePanel = new Composite(tabFolder, SWT.BORDER);
        tablePanel.setLayout(new GridLayout());
        setupTable(tablePanel, "A");
        setupTable(tablePanel, "B");
        tableTab.setControl(tablePanel);

        //tree
        TabItem treeTab = new TabItem(tabFolder, SWT.NONE);
        treeTab.setText("Tree");
        Tree tree = new Tree(tabFolder, SWT.MULTI | SWT.BORDER);
        tree.setHeaderVisible(true);
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText("Name");
        column1.setWidth(200);
        TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
        column2.setText("Description");
        column2.setWidth(200);
        TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
        column3.setText("Value");
        column3.setWidth(200);
        TreeItem item0 = new TreeItem(tree, SWT.NULL);
        item0.setText(0, "0");
        item0.setText(1, "TreeItem 0");
        item0.setText(2, "zero");
        TreeItem item00 = new TreeItem(item0, SWT.NULL);
        item00.setText(0, "00");
        item00.setText(1, "TreeItem 00");
        item00.setText(2, "zerozero");
        TreeItem item01 = new TreeItem(item0, SWT.NULL);
        item01.setText(0, "01");
        item01.setText(1, "TreeItem 01");
        item01.setText(2, "zeroone");
        TreeItem item010 = new TreeItem(item01, SWT.NULL);
        item010.setText(0, "010");
        item010.setText(1, "TreeItem 010");
        item010.setText(2, "zeroonezero");
        TreeItem item1 = new TreeItem(tree, SWT.NULL);
        item1.setText(0, "1");
        item1.setText(1, "TreeItem 1");
        item1.setText(2, "one");
        TreeItem item10 = new TreeItem(item1, SWT.NULL);
        item10.setText(0, "10");
        item10.setText(1, "TreeItem 10");
        item10.setText(2, "onezero");
        TreeItem item11 = new TreeItem(item1, SWT.NULL);
        item11.setText(0, "11");
        item11.setText(1, "TreeItem 11");
        item11.setText(2, "oneone");
        tree.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent se) {
                shell.setText("" + se.item);
            }

            public void widgetDefaultSelected(SelectionEvent se) {
            }
        });
        treeTab.setControl(tree);

        //buttons
        TabItem buttonsTab = new TabItem(tabFolder, SWT.NONE);
        Composite buttonsParent = new Composite(tabFolder, SWT.NONE);
        buttonsParent.setData("name", "buttonsParent");
        buttonsParent.setLayout(new RowLayout());
        final Text lbl = new Text(buttonsParent, SWT.DEFAULT);
        lbl.setText("click the button, please.");
        lbl.setBounds(0, 0, 100, 30);
        lbl.setEditable(true);
        Button btn = new Button(buttonsParent, SWT.DEFAULT);
        btn.setText("Click me!");
        btn.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                lbl.setText("Now type some new text");
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
                //System.out.println(arg0);
            }
        });
        Combo combo = new Combo(buttonsParent, SWT.DEFAULT);
        combo.add("one");
        combo.add("two");
        combo.add("three");
        combo.add("four");
        combo.select(0);
        List list = new List(buttonsParent, SWT.DEFAULT);
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
        list.select(0);
        buttonsTab.setText("Buttons");
        buttonsTab.setControl(buttonsParent);

        tabFolder.pack();

        Menu menuBar = new Menu(shell, SWT.BAR);
        createMenu(menuBar);

        shell.setMenuBar(menuBar);
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!d.readAndDispatch()) {
                d.sleep();
            }
        }
        d.dispose();
    }

    private static void setupTable(Composite tablePanel, final String tableId) {
        Table table = new Table(tablePanel, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        table.setLayoutData(data);
        String[] titles = {"0", "1", "2"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
        int count = 3;
        for (int i = 0; i < count; i++) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, "" + tableId + ":" + (count * i + 0));
            item.setText(1, "" + tableId + ":" + (count * i + 1));
            item.setText(2, "" + tableId + ":" + (count * i + 2));
        }
        for (int i = 0; i < titles.length; i++) {
            table.getColumn(i).pack();
        }
        table.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                shell.setText("" + event.item);
            }
        });
    }

    private static void createMenu(Menu menu) {
        MenuItem menu0Item = new MenuItem(menu, SWT.CASCADE);
        menu0Item.setText("menu0");
        Menu menu0 = new Menu(menu);
        menu0Item.setMenu(menu0);
        MenuItem item00 = new MenuItem(menu0, SWT.DEFAULT);
        item00.setText("item00");
        new MenuSelectionListener(item00);
        MenuItem itemDisabled = new MenuItem(menu0, SWT.DEFAULT);
        itemDisabled.setText("disabled");
        itemDisabled.setEnabled(false);
        MenuItem item01 = new MenuItem(menu0, SWT.DEFAULT);
        item01.setText("item01");
        new MenuSelectionListener(item01);
        MenuItem menu00Item = new MenuItem(menu0, SWT.CASCADE);
        menu00Item.setText("menu00");
        Menu menu00 = new Menu(menu);
        menu00Item.setMenu(menu00);
        MenuItem item000 = new MenuItem(menu00, SWT.DEFAULT);
        item000.setText("item000");
        new MenuSelectionListener(item000);

        MenuItem menu1Item = new MenuItem(menu, SWT.CASCADE);
        menu1Item.setText("menu1");
        Menu menu1 = new Menu(menu);
        menu1Item.setMenu(menu1);
        MenuItem item10 = new MenuItem(menu1, SWT.DEFAULT);
        item10.setText("item10");
        new MenuSelectionListener(item10);
        MenuItem item11 = new MenuItem(menu1, SWT.DEFAULT);
        item11.setText("item11");
        new MenuSelectionListener(item11);
        MenuItem item12 = new MenuItem(menu1, SWT.DEFAULT);
        item12.setText("item12");
        new MenuSelectionListener(item12);
    }

    static class MenuSelectionListener implements SelectionListener {

        public MenuSelectionListener(MenuItem item) {
            this.item = item;
            item.addSelectionListener(this);
        }
        MenuItem item;

        public void widgetSelected(SelectionEvent se) {
            shell.setText(item.getText() + " selected");
        }

        public void widgetDefaultSelected(SelectionEvent se) {
        }
    }

    static class TreeSelectionListener implements Listener {

        public TreeSelectionListener(TreeItem item) {
            this.item = item;
            item.addListener(SWT.Selection, this);
        }
        TreeItem item;

        public void handleEvent(Event event) {
            shell.setText(item.getText() + " selected");
        }
    }
}
