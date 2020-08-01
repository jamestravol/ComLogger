package jt.upwork.com.logger;

import javax.swing.*;

/**
 * @author jamestravol
 */
public class MainPanel extends JTabbedPane {

    public static final MainPanel INSTANCE = new MainPanel();
    private final LoggerPanel loggerPanel;

    private MainPanel() {
        loggerPanel = new LoggerPanel();
        add("Logger", loggerPanel);
        add("Settings", new SettingsPanel());
        add("Help", new HelpPanel());
        if (!Main.licensed) {
            add("Activation", new ActivationPanel());
        }

        if (Main.block) {
            this.setEnabledAt(0, false);
            this.setEnabledAt(1, false);
            this.setSelectedIndex(2);
        }

    }

    public LoggerPanel getLoggerPanel() {
        return loggerPanel;
    }

}
