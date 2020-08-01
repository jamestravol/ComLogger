package jt.upwork.com.logger;

import javax.swing.*;

/**
 * @author jamestravol
 */
public class MainPanel extends JTabbedPane {

    public MainPanel() {
        add("Logger", new LoggerPanel());
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
}
