/*
 * Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.
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

package org.jemmy.browser;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jemmy.control.Wrap;
import org.jemmy.control.Wrapper;
import org.jemmy.lookup.ControlHierarchy;
import org.jemmy.lookup.ControlList;

/**
 *
 * @author shura
 */
public class HierarchyView extends javax.swing.JFrame {
    private static final int MARKER_BORDER_GAP = 4;
    private static final int MARKER_BORDER_WIDTH = 4;
    private static final int WATCHER_LOOP_DELAY = 2000;

    public static void startApp(final String[] argv) {
        new Thread(new Runnable() {

            public void run() {
                if (argv.length > 0) {
                    try {
                        Class appClass = Class.forName(argv[0]);
                        appClass.getMethod("main", new Class[]{(new String[0]).getClass()}).invoke(null, (Object) Arrays.copyOfRange(argv, 1, argv.length));
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    DefaultTreeModel model;
    PropertiesPanel propPanel;
    InterfacesPanel intPanel;
    BrowserDescriptor descr;
    Wrap<?> root;
    ArrayList<Class> subHs;
    ComboBoxModel comboModel = new MyComboModel();
    ReflectionPanel reflectionPanel;
    private int sleepValue;
    SearchDialog sd = new SearchDialog(this);
    ImageLabel imageLabel;
    JWindow markerFrame;

    public HierarchyView(BrowserDescriptor descr) throws AWTException {
        this(descr, null);
    }

    private HierarchyView(BrowserDescriptor descr, Wrap<?> root) throws AWTException {
        this.descr = descr;
        this.root = root;
        subHs = new ArrayList<Class>();
        setTitle(descr.getTitle());
        rebuildModel();
        initComponents();
        imageLabel = new ImageLabel();
        imageScroll.setViewportView(imageLabel);
        underPropPanel.setLayout(new BorderLayout());
        propPanel = new PropertiesPanel();
        underPropPanel.add(propPanel);
        underIntPanel.setLayout(new BorderLayout());
        intPanel = new InterfacesPanel();
        underIntPanel.add(intPanel);
        underReflectionPanel.setLayout(new BorderLayout());
        reflectionPanel = new ReflectionPanel();
        underReflectionPanel.add(reflectionPanel, BorderLayout.CENTER);
        controlTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent arg0) {
                if (descr.isActiveHighlightingProvided()) {
                    try {
                        MyTreeNode selected = (MyTreeNode) arg0.getPath().getLastPathComponent();
                        propPanel.setWrap(selected.getWrap());
                        intPanel.setWrap(selected.getWrap());
                        reflectionPanel.setControl(selected.getWrap().getControl().getClass());
                        positionImage(selected.getWrap());
                        if (markerFrame == null) {
                            markerFrame = new JWindow();
                            markerFrame.setAlwaysOnTop(true);
                            markerFrame.getContentPane().setBackground(Color.red);
                            markerFrame.setVisible(highlightControl.isSelected());
                        }
                        org.jemmy.Rectangle bounds = selected.wrap.getScreenBounds();
                        markerFrame.setLocation(bounds.x - MARKER_BORDER_GAP, bounds.y - MARKER_BORDER_GAP);
                        markerFrame.setSize(bounds.width + MARKER_BORDER_GAP * 2, bounds.height + MARKER_BORDER_GAP * 2);
                        Area frame = new Area(new Rectangle(bounds.width + MARKER_BORDER_GAP * 2, bounds.height + MARKER_BORDER_GAP * 2));
                        frame.subtract(new Area(new Rectangle(MARKER_BORDER_WIDTH, MARKER_BORDER_WIDTH, bounds.width + MARKER_BORDER_GAP * 2 - MARKER_BORDER_WIDTH * 2, bounds.height + MARKER_BORDER_GAP * 2 - MARKER_BORDER_WIDTH * 2)));
                        markerFrame.setShape(frame);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        refreshingLbl.setVisible(false);
        controlTab.setMnemonicAt(0, KeyEvent.VK_1);
        controlTab.setMnemonicAt(1, KeyEvent.VK_2);
        controlTab.setMnemonicAt(2, KeyEvent.VK_3);
        controlTab.setMnemonicAt(3, KeyEvent.VK_4);
        robot = new Robot();
    }

    private void positionImage(Wrap<?> wrap) {
        org.jemmy.Rectangle bounds = wrap.getScreenBounds();
        imageLabel.setBounds(bounds);
        Point center = new Point((int)(bounds.getX() + bounds.getWidth() / 2),
                (int)(bounds.getY() + bounds.getHeight() / 2));
        center.x -= imageScroll.getViewport().getWidth() /  2;
        center.y -= imageScroll.getViewport().getHeight() /  2;
        imageScroll.getHorizontalScrollBar().setValue(center.x);
        imageScroll.getVerticalScrollBar().setValue(center.y);
    }
    public ComboBoxModel getComboModel() {
        return comboModel;
    }
    DefaultMutableTreeNode rootNode;

    private void rebuildModel() {
        rootNode = new DefaultMutableTreeNode("");
        model = new DefaultTreeModel(rootNode);
        fillAll(descr, rootNode, null);
    }

    private void fillAll(HierarchyDescriptor subDescr, DefaultMutableTreeNode parent, Wrap wrap) {
        ControlList hierarchy = subDescr.getHierarchy();
        Wrapper wrapper = subDescr.getWrapper();
        List controls = null;
        if (wrap == null) {
            controls = hierarchy.getControls();
        } else {
            if (hierarchy instanceof ControlHierarchy) {
                controls = ((ControlHierarchy) hierarchy).getChildren(wrap.getControl());
            }
        }
        MyTreeNode node;
        if (controls != null) {
            for (Object c : controls) {
                node = new MyTreeNode(c, wrapper.wrap(Object.class, c), subDescr.getNodeDescriptor());
                parent.add(node);
                fillAll(subDescr, node, node.getWrap());
            }
        }
        if (wrap != null) {
            HierarchyDescriptor hd = subDescr.getSubHierarchyDescriptor(wrap);
            if (hd != null) {
                fillAll(hd, parent, null);
            }
        }
    }

    public DefaultTreeModel getTreeModel() {
        return model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        controlTree = new javax.swing.JTree();
        refreshBtt = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        sleep = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        refreshingLbl = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        highlightControl = new javax.swing.JCheckBox();
        watchCursor = new javax.swing.JCheckBox();
        controlTab = new javax.swing.JTabbedPane();
        underPropPanel = new javax.swing.JPanel();
        underReflectionPanel = new javax.swing.JPanel();
        underIntPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        imageScroll = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jSplitPane1.setDividerLocation(400);

        controlTree.setModel(getTreeModel());
        controlTree.setRootVisible(false);
        controlTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                controlTreeKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(controlTree);

        refreshBtt.setMnemonic('r');
        refreshBtt.setText("Refresh");
        refreshBtt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBttActionPerformed(evt);
            }
        });

        jLabel1.setText("In");

        sleep.setText("0");
        sleep.setAlignmentX(1.0F);
        sleep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sleepActionPerformed(evt);
            }
        });

        jLabel2.setText("seconds");

        refreshingLbl.setText("Refreshing");

        jButton1.setMnemonic('s');
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        highlightControl.setText("Highlight control");
        highlightControl.setEnabled(descr.isActiveHighlightingProvided());
        highlightControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightControlActionPerformed(evt);
            }
        });

        watchCursor.setText("Watch cursor");
        watchCursor.setEnabled(descr.getElementRetriever() != null);
        watchCursor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watchCursorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(refreshingLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sleep, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshBtt)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(highlightControl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(watchCursor)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshBtt)
                    .addComponent(jLabel2)
                    .addComponent(sleep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(refreshingLbl)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(highlightControl)
                    .addComponent(watchCursor))
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout underPropPanelLayout = new javax.swing.GroupLayout(underPropPanel);
        underPropPanel.setLayout(underPropPanelLayout);
        underPropPanelLayout.setHorizontalGroup(
            underPropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        underPropPanelLayout.setVerticalGroup(
            underPropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
        );

        controlTab.addTab("1. Properties", underPropPanel);

        javax.swing.GroupLayout underReflectionPanelLayout = new javax.swing.GroupLayout(underReflectionPanel);
        underReflectionPanel.setLayout(underReflectionPanelLayout);
        underReflectionPanelLayout.setHorizontalGroup(
            underReflectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        underReflectionPanelLayout.setVerticalGroup(
            underReflectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
        );

        controlTab.addTab("2. Reflection", underReflectionPanel);

        javax.swing.GroupLayout underIntPanelLayout = new javax.swing.GroupLayout(underIntPanel);
        underIntPanel.setLayout(underIntPanelLayout);
        underIntPanelLayout.setHorizontalGroup(
            underIntPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        underIntPanelLayout.setVerticalGroup(
            underIntPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
        );

        controlTab.addTab("3. Interfaces", underIntPanel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        controlTab.addTab("4. Image", jPanel2);

        jSplitPane1.setRightComponent(controlTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Robot robot;
    private BufferedImage shot = null;
    private Rectangle fullScreen = new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());;
    private void takeShot() {
        shot = robot.createScreenCapture(fullScreen);
        imageLabel.setIcon(new ImageIcon(shot));
    }
    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
    }//GEN-LAST:event_formKeyReleased

    private void highlightControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightControlActionPerformed
        if (markerFrame != null) {
            markerFrame.setVisible(highlightControl.isSelected());
        }
    }//GEN-LAST:event_highlightControlActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sd.setLocation(getLocation().x, getLocation().y + getHeight() - sd.getHeight());
        sd.setVisible(true);
        if (sd.isConfirmed()) {
            MyTreeNode res = search(rootNode, sd.getClassNameFilter(), sd.getPropertyFilter());
            if (res != null) {
                controlTree.setSelectionPath(new TreePath(res.getPath()));
                controlTree.requestFocus();
            } else {
                String msg = "Class name: \"" + sd.getClassNameFilter() + "\""+
                ((sd.getPropertyFilter() != null) ? (", property: \"" + sd.getPropertyFilter() + "\"") : "");
                JOptionPane.showMessageDialog(null, msg, "None found", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void sleepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sleepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sleepActionPerformed

    private void refreshBttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBttActionPerformed
        sleep.setEditable(false);
        refreshBtt.setVisible(false);
        refreshingLbl.setVisible(true);
        new Thread(new Runnable() {

            public void run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        public void run() {
                            sleepValue = Integer.parseInt(sleep.getText());
                        }
                    });
                    int restSleep = sleepValue;
                    while (restSleep > 0) {
                        final String newText = Integer.toString(restSleep);
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                sleep.setText(newText);
                            }
                        });
                        Thread.sleep(1000);
                        restSleep--;
                    }
                    rebuildModel();
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException e) {
                    Logger.getLogger(HierarchyView.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            refreshingLbl.setVisible(false);
                            refreshBtt.setVisible(true);
                            sleep.setEditable(true);
                            sleep.setText(Integer.toString(sleepValue));
                            controlTree.setModel(model);
                            controlTree.revalidate();
                            controlTree.requestFocus();
                            takeShot();
                        }
                    });
                }
            }
        }).start();
    }//GEN-LAST:event_refreshBttActionPerformed

    private void controlTreeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_controlTreeKeyReleased

    }//GEN-LAST:event_controlTreeKeyReleased

    private void watchCursorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watchCursorActionPerformed
        if (watchCursor.isSelected()) {
            watcherRunning = true;
            ElementRetriever retriever = descr.getElementRetriever();
            if (retriever != null) {
                Thread watcher = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (watcherRunning) {
                            Point pt = MouseInfo.getPointerInfo().getLocation();
                            try {
                                new Robot().waitForIdle();
                                for (int i = 0; i < 10; i++) {
                                    if (!watcherRunning) {
                                        return;
                                    }
                                    Thread.sleep(WATCHER_LOOP_DELAY/10);
                                    Point new_pt = MouseInfo.getPointerInfo().getLocation();
                                    if (!new_pt.equals(pt)) {
                                        pt = new_pt;
                                        i = 0;
                                    }
                                }
                            } catch (AWTException ex) {
                            } catch (InterruptedException ex) {
                            }
                            Object element = retriever.fromPoint(pt);
                            // extremely slow for MSAA proxy
                            MyTreeNode watched = search(rootNode, element);
                            // does not work due to the bugs in MS's walker implementation
                            /*final List<Integer> path = retriever.getPath(element);
                            MyTreeNode watched = search(rootNode, path);*/
                            if (watched != null && !watched.equals(latestWatched)) {
                                TreePath treePath = new TreePath(watched.getPath());
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        controlTree.setSelectionPath(treePath);
                                        controlTree.scrollPathToVisible(treePath);
                                        controlTree.requestFocus();
                                        latestWatched = watched;
                                    }
                                });
                            }
                        }
                    }
                });
                watcher.start();
            }
        } else {
            watcherRunning = false;
        }
    }//GEN-LAST:event_watchCursorActionPerformed

    private MyTreeNode search(DefaultMutableTreeNode node, List<Integer> path) {
        for (Integer index : path) {
            node = (MyTreeNode)node.getChildAt(index);
        }
        return (MyTreeNode)node;
    }

    private MyTreeNode search(DefaultMutableTreeNode node, Object control) {
        if (node instanceof MyTreeNode) {
            MyTreeNode mtnode = (MyTreeNode) node;
            Object element = mtnode.getWrap().getControl();
            if (element.equals(control)) {
                return mtnode;
            }
        }
        MyTreeNode res;
        for (int i = 0; i < node.getChildCount(); i++) {
            res = search((DefaultMutableTreeNode) node.getChildAt(i), control);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    private MyTreeNode search(DefaultMutableTreeNode node, String className, String property) {
        if (node instanceof MyTreeNode && ((MyTreeNode) node).getWrap().getControl().getClass().getName().indexOf(className) > 1) {
            MyTreeNode mtnode = (MyTreeNode) node;
            if (property != null) {
                HashMap<String, Object> props = mtnode.getWrap().getProperties();
                for (String prop : props.keySet()) {
                    if (props.get(prop) != null) {
                        if (props.get(prop).toString() != null) {
                            if (props.get(prop).toString().contains(property)) {
                                return mtnode;
                            }
                        }
                    }
                }
            } else {
                return mtnode;
            }
        }
        MyTreeNode res;
        for (int i = 0; i < node.getChildCount(); i++) {
            res = search((DefaultMutableTreeNode) node.getChildAt(i), className, property);
            if (res != null) {
                return res;
            }
        }
        return null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane controlTab;
    private javax.swing.JTree controlTree;
    private javax.swing.JCheckBox highlightControl;
    private javax.swing.JScrollPane imageScroll;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton refreshBtt;
    private javax.swing.JLabel refreshingLbl;
    private javax.swing.JTextField sleep;
    private javax.swing.JPanel underIntPanel;
    private javax.swing.JPanel underPropPanel;
    private javax.swing.JPanel underReflectionPanel;
    private javax.swing.JCheckBox watchCursor;
    // End of variables declaration//GEN-END:variables

    // End of variables declaration
    private volatile boolean watcherRunning;
    private MyTreeNode latestWatched;

    private class MyTreeNode extends DefaultMutableTreeNode {

        Wrap wrap = null;
        Object control;
        NodeDescriptor descriptor;

        public MyTreeNode(Object control, Wrap wrap, NodeDescriptor descriptor) {
            super(control);
            this.control = control;
            this.wrap = wrap;
            this.descriptor = descriptor;
        }

        public Wrap<?> getWrap() {
            return wrap;
        }

        @Override
        public String toString() {
            return descriptor.toString(control);
        }
    }

    private class MyComboModel implements ComboBoxModel {

        Class<?> selected;

        public void setSelectedItem(Object arg0) {
            System.out.println(arg0 + " selected");
            selected = (Class<?>) arg0;
        }

        public Object getSelectedItem() {
            System.out.println("selected = " + selected);
            return selected;
        }

        public int getSize() {
            return subHs.size();
        }

        public Object getElementAt(int arg0) {
            return subHs.get(arg0);
        }

        public void addListDataListener(ListDataListener arg0) {
        }

        public void removeListDataListener(ListDataListener arg0) {
        }
    }
}
