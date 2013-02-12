package com.clusterclient.gui;

import com.clusterclient.EnvironmentRepository;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ClusterRequestDialog extends JDialog {

    private final EnvironmentRepository repository;
    private String environmentSelected = "";
    private JComboBox options;

    public ClusterRequestDialog(Frame parent,
            EnvironmentRepository repository) {
        super(parent);
        this.repository = repository;
        setTitle("Please select");
        setModal(true);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(makeOptionsPanel(), BorderLayout.CENTER);
        content.add(makeControls(), BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }

    private JPanel makeOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        panel.add(new JLabel("Cluster:"));
        options = new JComboBox(repository.findAllEnvironments().toArray());
        panel.add(options);
        return panel;
    }

    private JPanel makeControls() {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                environmentSelected = (String) options.getSelectedItem();
                ClusterRequestDialog.this.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClusterRequestDialog.this.dispose();
            }
        });

        JPanel panel = new JPanel();
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    public String getEnvironment() {
        return environmentSelected;
    }
}
