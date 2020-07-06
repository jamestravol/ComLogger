package jt.upwork.com.logger;

import javax.swing.*;

/**
 * @author jamestravol
 */
public class SettingsPanel extends JPanel {

    private javax.swing.JCheckBox appendDateCheckbox;
    private javax.swing.JLabel limisLabel;
    private javax.swing.JSpinner limitSpinner;
    private javax.swing.JTextField separatorField;
    private javax.swing.JLabel separatorLabel;
    private javax.swing.JCheckBox singleSheetCheckbox;

    /**
     * Creates new form TabsPanel
     */
    public SettingsPanel() {
        initComponents();
    }

    private void initComponents() {

        appendDateCheckbox = new javax.swing.JCheckBox();
        singleSheetCheckbox = new javax.swing.JCheckBox();
        separatorLabel = new javax.swing.JLabel();
        separatorField = new javax.swing.JTextField();
        separatorField.setText(Config.INSTANCE.getProperty("app.separator.regex"));
        separatorField.addCaretListener(e -> Config.INSTANCE.setProperty("app.separator.regex", separatorField.getText()));

        limisLabel = new javax.swing.JLabel();
        limitSpinner = new javax.swing.JSpinner();
        limitSpinner.setValue(Integer.parseInt(Config.INSTANCE.getProperty("app.column.limit")));
        limitSpinner.addChangeListener(e -> Config.INSTANCE.setProperty("app.column.limit",
                limitSpinner.getValue() != null ? "" + limitSpinner.getValue() : "1"));

        appendDateCheckbox.setText("Append date and time");
        appendDateCheckbox.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("app.append.date")));
        appendDateCheckbox.addChangeListener(e -> Config.INSTANCE.setProperty("app.append.date", "" + appendDateCheckbox.isSelected()));

        singleSheetCheckbox.setText("Use single sheet for all hosts");
        singleSheetCheckbox.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("app.single.sheet")));
        singleSheetCheckbox.addChangeListener(e -> Config.INSTANCE.setProperty("app.single.sheet", "" + singleSheetCheckbox.isSelected()));

        separatorLabel.setText("Fields separator:");

        limisLabel.setText("Fields limit:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(appendDateCheckbox)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(limisLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(limitSpinner)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(separatorLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(separatorField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(singleSheetCheckbox))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(appendDateCheckbox)
                                        .addComponent(singleSheetCheckbox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(separatorLabel)
                                        .addComponent(separatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(limisLabel)
                                        .addComponent(limitSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }


}
