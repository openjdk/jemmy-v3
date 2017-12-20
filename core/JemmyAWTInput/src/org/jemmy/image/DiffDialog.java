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
package org.jemmy.image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author shura
 */
public class DiffDialog extends javax.swing.JDialog {

    private final static StrictImageComparator comparator = new StrictImageComparator();
    private double scale = 1.0;
    private int imageWidth, imageHeight, scaledWidth, scaledHeight;
    private ImagePane left = null, right = null, diff = null;
    int status = 0;

    /** Creates new form ImageDiff */
    DiffDialog() {
        super((JDialog)null, true);
        initComponents();
        leftPane.setLayout(new BorderLayout());
        leftPane.add(new JLabel("Golden"), BorderLayout.NORTH);
        rightPane.setLayout(new BorderLayout());
        rightPane.add(new JLabel("Result"), BorderLayout.NORTH);
        diffPane.setLayout(new BorderLayout());
        diffPane.add(new JLabel("Diff"), BorderLayout.NORTH);
        getContentPane().addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent e) {
                lrSplit.setDividerLocation(.5);
                dcSplit.setDividerLocation(.5);
                tbSplit.setDividerLocation(.5);
            }

            public void componentMoved(ComponentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentShown(ComponentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentHidden(ComponentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        setSize(400, 300);

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        setLocation(x, y);
    }

    void setImages(BufferedImage leftImage, BufferedImage rightImage) {
        if (leftImage != null && rightImage != null) {
            copyBtn.setEnabled(true);
            removeBtn.setEnabled(false);
            imageWidth = leftImage.getWidth();
            imageHeight = leftImage.getHeight();
        } else {
            if (leftImage == null) {
                copyBtn.setEnabled(true);
                removeBtn.setEnabled(false);
                imageWidth = rightImage.getWidth();
                imageHeight = rightImage.getHeight();
            } else if (rightImage == null) {
                copyBtn.setEnabled(false);
                removeBtn.setEnabled(true);
                imageWidth = leftImage.getWidth();
                imageHeight = leftImage.getHeight();
            }
        }
        if (left == null) {
            left = new ImagePane(leftImage);
        } else {
            left.setImage(leftImage);
        }
        leftPane.add(left, BorderLayout.CENTER);
        if (right == null) {
            right = new ImagePane(rightImage);
        } else {
            right.setImage(rightImage);
        }
        rightPane.add(right, BorderLayout.CENTER);
        if (diff == null) {
            diff = new ImagePane(subtract(leftImage, rightImage));
        } else {
            diff.setImage(subtract(leftImage, rightImage));
        }
        diffPane.add(diff, BorderLayout.CENTER);
        rescaleAll();
    }

    private BufferedImage subtract(BufferedImage left, BufferedImage right) {
        if(left != null && right != null) {
            return ImageTool.subtractImage(left, right);
        } else {
            return null;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tbSplit = new javax.swing.JSplitPane();
        lrSplit = new javax.swing.JSplitPane();
        leftPane = new javax.swing.JPanel();
        rightPane = new javax.swing.JPanel();
        dcSplit = new javax.swing.JSplitPane();
        diffPane = new javax.swing.JPanel();
        controlPane = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        copyBtn = new javax.swing.JButton();
        removeBtn = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        tbSplit.setDividerLocation(200);
        tbSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        lrSplit.setDividerLocation(250);

        javax.swing.GroupLayout leftPaneLayout = new javax.swing.GroupLayout(leftPane);
        leftPane.setLayout(leftPaneLayout);
        leftPaneLayout.setHorizontalGroup(
            leftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        leftPaneLayout.setVerticalGroup(
            leftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        lrSplit.setLeftComponent(leftPane);

        javax.swing.GroupLayout rightPaneLayout = new javax.swing.GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
        );
        rightPaneLayout.setVerticalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        lrSplit.setRightComponent(rightPane);

        tbSplit.setTopComponent(lrSplit);

        javax.swing.GroupLayout diffPaneLayout = new javax.swing.GroupLayout(diffPane);
        diffPane.setLayout(diffPaneLayout);
        diffPaneLayout.setHorizontalGroup(
            diffPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        diffPaneLayout.setVerticalGroup(
            diffPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );

        dcSplit.setLeftComponent(diffPane);

        jButton1.setMnemonic('+');
        jButton1.setText("+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setMnemonic('-');
        jButton2.setText("-");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        copyBtn.setText("Copy to golgen");
        copyBtn.setEnabled(false);
        copyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBtnActionPerformed(evt);
            }
        });

        removeBtn.setText("Remove from golden");
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtnActionPerformed(evt);
            }
        });

        jButton6.setText("Next");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Exit");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPaneLayout = new javax.swing.GroupLayout(controlPane);
        controlPane.setLayout(controlPaneLayout);
        controlPaneLayout.setHorizontalGroup(
            controlPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPaneLayout.createSequentialGroup()
                .addContainerGap(289, Short.MAX_VALUE)
                .addGroup(controlPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPaneLayout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPaneLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(copyBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(removeBtn, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        controlPaneLayout.setVerticalGroup(
            controlPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addGroup(controlPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        dcSplit.setRightComponent(controlPane);

        tbSplit.setRightComponent(dcSplit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbSplit, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbSplit, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        scale *= .9;
        rescaleAll();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        scale *= 1.1;
        rescaleAll();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void rescaleAll() {
        scaledWidth = (int) (imageWidth * scale);
        scaledHeight = (int) (imageHeight * scale);
        left.reScale();
        right.reScale();
        diff.reScale();
        getContentPane().repaint();
    }

    private void copyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBtnActionPerformed
        status = -1;
        setVisible(false);
    }//GEN-LAST:event_copyBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        status = 1;
        setVisible(false);
    }//GEN-LAST:event_removeBtnActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        status = -2;
        setVisible(false);
    }//GEN-LAST:event_jButton7ActionPerformed

    private class ImagePane extends JPanel {

        BufferedImage img;
        java.awt.Image scaled;

        public ImagePane(BufferedImage img) {
            this.img = img;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (img != null) {
                g.drawImage(scaled, 0, 0, this);
            } else {
                super.paintComponent(g);
            }
        }

        void setImage(BufferedImage img) {
            this.img = img;
            reScale();
        }

        void reScale() {
            if (img != null) {
                    scaled = img.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_DEFAULT);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPane;
    private javax.swing.JButton copyBtn;
    private javax.swing.JSplitPane dcSplit;
    private javax.swing.JPanel diffPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JPanel leftPane;
    private javax.swing.JSplitPane lrSplit;
    private javax.swing.JButton removeBtn;
    private javax.swing.JPanel rightPane;
    private javax.swing.JSplitPane tbSplit;
    // End of variables declaration//GEN-END:variables
}
