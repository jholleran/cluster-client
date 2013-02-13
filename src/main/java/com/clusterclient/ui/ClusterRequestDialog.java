/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.clusterclient.ui;

import com.clusterclient.EnvironmentRepository;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author HolleranJ
 */
public class ClusterRequestDialog extends javax.swing.JDialog {

    private final EnvironmentRepository environmentRepository;
    private String environmentSelected = "";
    
    /**
     * Creates new form ClusterRequestDialog
     */ 
    public ClusterRequestDialog(java.awt.Frame parent, EnvironmentRepository environmentRepository) {
        super(parent, true);
        this.environmentRepository = environmentRepository;
        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public String getEnvironment() {
        return environmentSelected;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        envComboBox = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Server Cluster");
        setModal(true);

        label.setText("Please select Cluster:");

        envComboBox.setModel(new DefaultComboBoxModel(environmentRepository.findAllEnvironments().toArray()));

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonPressed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(envComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(98, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addGap(68, 68, 68))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(envComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonPressed
        environmentSelected = (String) envComboBox.getSelectedItem();
        dispose();
    }//GEN-LAST:event_okButtonPressed

    private void cancelButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonPressed
        dispose();
    }//GEN-LAST:event_cancelButtonPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox envComboBox;
    private javax.swing.JLabel label;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
