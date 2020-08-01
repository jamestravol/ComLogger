package jt.upwork.com.logger;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @author jamestravol
 */
public class ActivationPanel extends JPanel {

    private javax.swing.JButton activateButton;
    private javax.swing.JTextField instanceIdField;
    private javax.swing.JLabel instanceIdLabel;
    private javax.swing.JTextField serialField;
    private javax.swing.JLabel serialLabel;

    public ActivationPanel() {
        initComponents();
        initListeners();
    }

    private void initListeners() {
        activateButton.addActionListener(e -> activate());
    }

    private void activate() {

        try {
            final byte[] bytes = Base64.getDecoder().decode(serialField.getText());
            final String serialStr = new String(bytes);

            final long serial = Long.parseLong(serialStr);

            if (serial % Main.fl == 0) {
                final Path path = Paths.get(System.getProperty("user.dir"), "/serial");
                Files.write(path, serialField.getText().getBytes());
                JOptionPane.showMessageDialog(null, "Activated", "Activated",
                        JOptionPane.INFORMATION_MESSAGE);
                if (this.getParent() instanceof JTabbedPane) {
                    ((JTabbedPane) this.getParent()).setEnabledAt(0, true);
                    ((JTabbedPane) this.getParent()).setEnabledAt(1, true);
                }
                this.getParent().remove(this);
            } else {
                JOptionPane.showMessageDialog(null, "Wrong serial number", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during the activasion process", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void initComponents() {

        instanceIdLabel = new javax.swing.JLabel();
        instanceIdField = new javax.swing.JTextField();
        serialLabel = new javax.swing.JLabel();
        serialField = new javax.swing.JTextField();
        activateButton = new javax.swing.JButton();

        instanceIdLabel.setText("Instance ID:");

        instanceIdField.setEditable(false);

        instanceIdField.setText("" + Main.fl);

        serialLabel.setText("Serial:");

        activateButton.setText("Activate");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(instanceIdLabel)
                                                        .addComponent(serialLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(instanceIdField, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                                        .addComponent(serialField)))
                                        .addComponent(activateButton))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(instanceIdLabel)
                                        .addComponent(instanceIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(serialLabel)
                                        .addComponent(serialField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(activateButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

}
