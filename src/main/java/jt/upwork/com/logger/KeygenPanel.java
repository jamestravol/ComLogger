package jt.upwork.com.logger;

import javax.swing.*;
import java.util.Base64;

/**
 * @author jamestravol
 */
public class KeygenPanel extends JPanel {

    private JButton activateButton;
    private JTextField instanceIdField;
    private JLabel instanceIdLabel;
    private JTextField serialField;
    private JLabel serialLabel;

    public KeygenPanel() {
        initComponents();
        initListeners();
    }

    private void initListeners() {
        activateButton.addActionListener(e -> generate());
    }

    private void generate() {

        try {

            final String instance = instanceIdField.getText();

            final long instanceLong = Long.parseLong(instance);

            long multiplier = (long) (Math.random() * 1000d);

            String serialNumber = "" + (instanceLong * multiplier * 17);

            serialField.setText(Base64.getEncoder().encodeToString(serialNumber.getBytes()));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wrong instance number", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void initComponents() {

        instanceIdLabel = new JLabel();
        instanceIdField = new JTextField();
        serialLabel = new JLabel();
        serialField = new JTextField();
        activateButton = new JButton();

        instanceIdLabel.setText("Instance ID:");

        instanceIdField.setText("");

        serialLabel.setText("Serial:");

        serialField.setEditable(false);

        activateButton.setText("Generate");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(instanceIdLabel)
                                                        .addComponent(serialLabel))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(instanceIdField, GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                                        .addComponent(serialField)))
                                        .addComponent(activateButton))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(instanceIdLabel)
                                        .addComponent(instanceIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(serialLabel)
                                        .addComponent(serialField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(activateButton)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

}
