package jt.upwork.com.logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jamestravol
 */
public class LoggerPanel extends JPanel {

    private final DateTimeFormatter dateFormatter;

    private javax.swing.ButtonGroup fileModeRadioGroup;
    private javax.swing.JRadioButton appendRadio;
    private javax.swing.JRadioButton overwriteRadio;
    private javax.swing.JButton connectButton;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JButton fileButton;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JButton addHostButton;
    private javax.swing.JPanel connectionPanels;
    private javax.swing.JButton openButton;
    private javax.swing.JLabel baudLabel;
    private javax.swing.JComboBox<Integer> baudComboBox;
    private javax.swing.JButton logButton;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;

    private final AtomicInteger running = new AtomicInteger();

    private volatile Timer timer;

    private void connectionEvent(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case "deleted":
                delete((ConnectionPanel) evt.getSource());
                break;
        }

    }

    private void delete(final ConnectionPanel source) {
        SwingUtilities.invokeLater(() -> {
            connectionPanels.remove(source);
            Config.INSTANCE.setProperty("app.coms.count", "" + connectionPanels.getComponentCount());
            connectionPanels.updateUI();
            final Container topLevelAncestor = this.getTopLevelAncestor();
            if (topLevelAncestor instanceof JFrame) {
                ((JFrame) topLevelAncestor).pack();
            }
            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                component.setIndex(i);
                component.save();
            }

            if (connectionPanels.getComponentCount() < Integer.parseInt(Config.INSTANCE.getProperty("app.coms.max"))) {
                addHostButton.setEnabled(true);
            }

        });
    }

    private void updateExcel() {

        try {
            HashMap<String, List<String[]>> result = new HashMap<>();

            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                component.getLock().lock();
                String name = component.getNameOrPort();
                if (!name.isEmpty()) {
                    result.put(name, component.getDataList());
                }
            }

            ExcelProcessor.update(Application.INSTANCE.getSelectedFile(), result);

            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                component.clearDataList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                component.getLock().unlock();
            }
        }

    }

    public LoggerPanel() {
        dateFormatter = DateTimeFormatter.ofPattern(Config.INSTANCE.getProperty("app.log.datetime.format", "dd/MM/yyyy-HH:mm:ss"));
        initComponents();
        afterInit();
        initListeners();
    }

    private void afterInit() {

        baudComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{1200, 9600, 19200, 38400, 57600, 115200}));

        baudComboBox.setSelectedItem(Integer.parseInt(Config.INSTANCE.getProperty("app.baud.rate")));

        baudComboBox.addItemListener(e -> Config.INSTANCE.setProperty("app.baud.rate", e.getItem().toString()));

        final int hostCount = Integer.parseInt(Config.INSTANCE.getProperty("app.coms.count"));

        for (int i = 0; i < hostCount; i++) {
            final ConnectionPanel additionalPanel = new ConnectionPanel(i);
            additionalPanel.addPropertyChangeListener(this::connectionEvent);
            additionalPanel.load();
            this.connectionPanels.add(additionalPanel);
        }

        if (hostCount >= Integer.parseInt(Config.INSTANCE.getProperty("app.coms.max"))) {
            addHostButton.setEnabled(false);
        }

    }

    private void initListeners() {

        addHostButton.addActionListener(e -> {
            final int count = connectionPanels.getComponentCount();
            final ConnectionPanel comp = new ConnectionPanel(count);
            comp.addPropertyChangeListener(this::connectionEvent);
            comp.save();
            connectionPanels.add(comp);
            connectionPanels.updateUI();
            final Container topLevelAncestor = this.getTopLevelAncestor();
            if (topLevelAncestor instanceof JFrame) {
                ((JFrame) topLevelAncestor).pack();
            }
            Config.INSTANCE.setProperty("app.coms.count", "" + (count + 1));
            if (count + 1 >= Integer.parseInt(Config.INSTANCE.getProperty("app.coms.max"))) {
                addHostButton.setEnabled(false);
            }
        });

        fileButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel files", "xls", "xlsx");
            chooser.addChoosableFileFilter(filter);

            final int returnValue = chooser.showDialog(Application.INSTANCE.getFrame(), "Choose");

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                Application.INSTANCE.setSelectedFile(chooser.getSelectedFile());
                fileLabel.setText(chooser.getSelectedFile().getName());
                connectButton.setEnabled(true);
                openButton.setEnabled(true);
            }
        });

        connectButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (Application.INSTANCE.getSelectedFile().exists()) {

                    if (overwriteRadio.isSelected()) {
                        final int result = JOptionPane.showConfirmDialog(Application.INSTANCE.getFrame(),
                                String.format("Do you really want to overwrite the file %s?",
                                        Application.INSTANCE.getSelectedFile().getName()),
                                "Overwrite?",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            cleanUpFile(Application.INSTANCE.getSelectedFile());
                            execute();
                        }

                    } else {
                        execute();
                    }

                } else {
                    final int result = JOptionPane.showConfirmDialog(Application.INSTANCE.getFrame(),
                            String.format("Create a new file %s?",
                                    Application.INSTANCE.getSelectedFile().getName()),
                            "Create new?",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        createEmpty(Application.INSTANCE.getSelectedFile());
                        execute();
                    }
                }
            }
        });

        disconnectButton.addActionListener(e -> {
            disconnectButton.setEnabled(false);

            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                component.cancel();
            }
        });

        openButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Application.INSTANCE.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);

        logButton.addActionListener((event) -> {
            if (logScrollPane.isVisible()) {
                logScrollPane.setVisible(false);
                logButton.setText(">");
                Application.INSTANCE.getFrame().pack();
            } else {
                logScrollPane.setVisible(true);
                logButton.setText("<");
                Application.INSTANCE.getFrame().pack();
            }
        });
    }

    private void createEmpty(File selectedFile) {
        ExcelProcessor.createEmpty(selectedFile);
    }

    private void cleanUpFile(File selectedFile) {
        ExcelProcessor.createEmpty(selectedFile);
    }

    private void execute() {
        connectButton.setEnabled(false);
        fileButton.setEnabled(false);

        running.set(connectionPanels.getComponentCount());

        for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
            ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
            component.execute((Integer) baudComboBox.getSelectedItem(), () -> onStopped(component));
        }
        disconnectButton.setEnabled(true);
        final int flushIntervalMills = Integer.parseInt(Config.INSTANCE.getProperty("app.flush.interval.mills"));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateExcel();
            }
        }, flushIntervalMills, flushIntervalMills);
        Application.INSTANCE.showActiveStatus();
    }

    private void onStopped(ConnectionPanel connectionPanel) {
        System.out.println(String.format("Stopping process index %d. Name %s", connectionPanel.getIndex(), connectionPanel.getNameOrPort()));
        if (running.decrementAndGet() == 0) {
            System.out.println("Stopping all");
            fileButton.setEnabled(true);
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            Application.INSTANCE.showInactiveStatus();
            timer.cancel();

            updateExcel();

            for (int i = 0; i < connectionPanels.getComponentCount(); i++) {
                final ConnectionPanel component = (ConnectionPanel) connectionPanels.getComponent(i);
                SwingUtilities.invokeLater(component::setIdle);
            }
        } else {
            Application.INSTANCE.showWarnStatus();
        }
    }

    public void log(String source, String text) {

        if (logTextArea.getLineCount() >= 1000) {
            try {
                logTextArea.replaceRange("", 0, logTextArea.getLineEndOffset(0));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        logTextArea.append(dateFormatter.format(LocalDateTime.now()) + " " + source + ": " + text + "\n");
    }

    private void initComponents() {

        fileModeRadioGroup = new javax.swing.ButtonGroup();
        connectionPanels = new javax.swing.JPanel();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        fileButton = new javax.swing.JButton();
        fileLabel = new javax.swing.JLabel();
        overwriteRadio = new javax.swing.JRadioButton();
        appendRadio = new javax.swing.JRadioButton();
        addHostButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        baudLabel = new javax.swing.JLabel();
        baudComboBox = new javax.swing.JComboBox<>();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        logButton = new javax.swing.JButton();

        connectionPanels.setLayout(new BoxLayout(connectionPanels, BoxLayout.Y_AXIS));

        connectButton.setText("Start");
        connectButton.setEnabled(false);

        disconnectButton.setText("Stop");
        disconnectButton.setEnabled(false);

        fileButton.setText("Browse");

        fileLabel.setText("Select an excel file");

        fileModeRadioGroup.add(overwriteRadio);
        overwriteRadio.setSelected(true);
        overwriteRadio.setText("Override");

        fileModeRadioGroup.add(appendRadio);
        appendRadio.setText("Append");

        addHostButton.setText("Add port");

        openButton.setEnabled(false);
        openButton.setText("Open");

        baudLabel.setText("Baud:");

        logTextArea.setEditable(false);
        logTextArea.setRows(3);
        logScrollPane.setViewportView(logTextArea);
        logScrollPane.setVisible(false);

        logButton.setText(">");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(connectionPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(openButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(fileButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(fileLabel))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(overwriteRadio)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(appendRadio))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(connectButton)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(disconnectButton)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(logButton))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(baudLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(baudComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(addHostButton))))
                                .addGap(10, 10, 10)
                                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(logScrollPane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(connectionPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(baudLabel)
                                                        .addComponent(baudComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addHostButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(fileButton)
                                                        .addComponent(fileLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(openButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(overwriteRadio)
                                                        .addComponent(appendRadio))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(connectButton)
                                                        .addComponent(disconnectButton)
                                                        .addComponent(logButton))))
                                .addContainerGap())
        );
    }


}
