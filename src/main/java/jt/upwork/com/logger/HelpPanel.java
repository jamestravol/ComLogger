package jt.upwork.com.logger;

import javax.swing.*;

/**
 * @author jamestravol
 */
public class HelpPanel extends JPanel {

    private javax.swing.JLabel infoLabel;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel supportLabel;

    public HelpPanel() {
        initComponents();
    }

    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        supportLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();

        logoLabel.setIcon(new javax.swing.ImageIcon(Images.INSTANCE.getLogo()));

//        infoLabel.set
        infoLabel.setText("<html>All rights reserved<br>Weighing Solution and Instrumentation<br>New Delhi, India</html>");

        supportLabel.setText("support email:");

        emailField.setEditable(false);
        emailField.setFont(UIManager.getFont("Label.font"));
        emailField.setForeground(UIManager.getColor("Label.foreground"));
        emailField.setBackground(UIManager.getColor("Label.background"));
        emailField.setText("Sales@wsi-scales.com");
        emailField.setBorder(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(logoLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(infoLabel)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(supportLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(logoLabel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(infoLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(supportLabel)
                                                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

}
