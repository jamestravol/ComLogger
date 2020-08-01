package jt.upwork.com.logger;

import jssc.SerialPortList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jamestravol
 */
public class ConnectionPanel extends JPanel {

    private JComboBox<String> portComboBox;
    private JLabel ipLabel;
    private JButton removeButton;
    private JTextField nameField;
    private JLabel nameLabel;
    private JLabel statusLabel;
    private volatile int index;

    private volatile TelnetReaderWorker telnetReaderWorker;

    public ConnectionPanel(int index) {
        this.index = index;
        initComponents();
        initListeners();
    }
    
    public void load() {
        final String port = Config.INSTANCE.getProperty("app.com" + (index + 1) + ".port");
        final String name = Config.INSTANCE.getProperty("app.com" + (index + 1) + ".name");

        if (port != null) {
            portComboBox.setSelectedItem(port);
        }

        if (name != null) {
            nameField.setText(name);
        }
    }

    public void save() {
        Config.INSTANCE.setProperty("app.com" + (index + 1) + ".port", portComboBox.getSelectedItem().toString());
        Config.INSTANCE.setProperty("app.com" + (index + 1) + ".name", nameField.getText());
    }

    private void initListeners() {
        removeButton.addActionListener(e -> {
            firePropertyChange("deleted", false, true);
        });

        portComboBox.addActionListener(e -> save());
        nameField.addCaretListener(e -> save());

    }

    private void initComponents() {
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        ipLabel = new javax.swing.JLabel();
        portComboBox = new javax.swing.JComboBox<>();
        removeButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();

        nameLabel.setText("Name:");

        ipLabel.setText("Port:");

        portComboBox.setModel(new DefaultComboBoxModel<>(SerialPortList.getPortNames()));

        statusLabel.setIcon(Images.INSTANCE.getHostIdle());

        removeButton.setIcon(Images.INSTANCE.getDeleted());
        removeButton.setBorder(new EmptyBorder(0, 0, 0, 0));

        this.setBorder(new EmptyBorder(3, 3, 3, 3));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
//                                .addContainerGap()
                                        .addComponent(ipLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(portComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nameLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
//                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
//                                .addContainerGap(2, 2)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(ipLabel)
                                                        .addComponent(portComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(nameLabel)
                                                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(statusLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
//                                .addContainerGap(2, 2)
                        )
        );
    }

    public TelnetReaderWorker getTelnetReaderWorker() {
        return telnetReaderWorker;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setConnected() {
        statusLabel.setIcon(Images.INSTANCE.getHostConnected());
    }

    public void setDisconnected() {
        statusLabel.setIcon(Images.INSTANCE.getHostDisConnected());
    }

    public void setIdle() {
        statusLabel.setIcon(Images.INSTANCE.getHostIdle());
    }

    public void execute(int baudRate, Runnable completeCallback) {
        telnetReaderWorker = new TelnetReaderWorker(portComboBox.getSelectedItem().toString(),
                baudRate,
                this,
                completeCallback);
        telnetReaderWorker.execute();
    }

    public void cancel() {
        telnetReaderWorker.cancel();
    }

    public List<String[]> getDataList() {
        return telnetReaderWorker.getDataList();
    }

    public void clearDataList() {
        telnetReaderWorker.clearDataList();
    }

    public String getNameOrPort() {
        return nameField.getText().trim().isEmpty() ? portComboBox.getSelectedItem().toString().trim() : nameField.getText().trim();
    }

    public ReentrantLock getLock() {
        return telnetReaderWorker.getLock();
    }

    public void log(String text) {
        MainPanel.INSTANCE.getLoggerPanel().log(getNameOrPort(), text);
    }

}
