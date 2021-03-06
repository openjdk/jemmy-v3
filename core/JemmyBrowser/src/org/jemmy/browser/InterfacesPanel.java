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

package org.jemmy.browser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import org.jemmy.control.ControlInterfaces;
import org.jemmy.control.Property;
import org.jemmy.control.Wrap;
import org.jemmy.interfaces.ControlInterface;
import org.jemmy.interfaces.InterfaceException;

/**
 *
 * @author shura
 */
public class InterfacesPanel extends javax.swing.JPanel {

    private static final String OFFSET = "    ";
    Wrap<?> wrap;
    DefaultTableModel tableModel;

    /** Creates new form InterfacesPanel */
    public InterfacesPanel() {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Interface/Property");
        tableModel.addColumn("Class/Value");
        initComponents();
    }

    public void setWrap(Wrap<?> wrap) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.wrap = wrap;
        buildModel();
        revalidate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        interfacesTable = new javax.swing.JTable();

        interfacesTable.setModel(getTableModel());
        jScrollPane1.setViewportView(interfacesTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable interfacesTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    private void buildModel() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        while (getTableModel().getRowCount() > 0) {
            getTableModel().removeRow(0);
        }
        Class cls = wrap.getClass();
        ArrayList<Class> interfaces =
                new ArrayList<Class>();
        do {
            ControlInterfaces cis = ((Class<? extends Wrap>) cls).getAnnotation(ControlInterfaces.class);
            for (Class c : cis.value()) {
                if (!interfaces.contains(c)) {
                    interfaces.add(c);
                }
            }
        } while (Wrap.class.isAssignableFrom((cls = cls.getSuperclass())));
        Collections.sort(interfaces, new Comparator() {
            public int compare(Object t, Object t1) {
                return t.toString().compareTo(t1.toString());
            }
        });
        for (Class<? extends ControlInterface> c : interfaces) {
            String iName = c.getName();
            try {
                Object iInstance = wrap.as(c);
                getTableModel().addRow(new Object[]{iName, iInstance.getClass().getName()});
                for (Method m : c.getMethods()) {
                    if (m.isAnnotationPresent(Property.class)) {
                        String name = m.getAnnotation(Property.class).value();
                        getTableModel().addRow(new Object[]{OFFSET + name, wrap.getProperty(name, c)});
                    }
                }
            } catch (InterfaceException e) {
                //do nothing
            }
        }
    }
}
