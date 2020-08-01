package jt.upwork.com.logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * @author jamestravol
 */
public final class Application {

    private static final String APPLICATION_NAME = "WSI Com port logging manager";

    public static final Application INSTANCE = new Application();

    private final JFrame frame;
    private final TrayIcon trayIcon;

    private volatile File selectedFile;

    public Application() {

        System.out.println(Config.INSTANCE);

        frame = new JFrame(APPLICATION_NAME);
        frame.setIconImage(Images.INSTANCE.getLogo());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(MainPanel.INSTANCE);
        frame.pack();
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Config.INSTANCE.save();
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {
                frame.setVisible(false);
            }
        });

        PopupMenu trayMenu = new PopupMenu();

        MenuItem showItem = new MenuItem("Show/Hide");
        showItem.addActionListener(e -> frame.setVisible(!frame.isVisible()));
        trayMenu.add(showItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            Config.INSTANCE.save();
            System.exit(0);
        });
        trayMenu.add(exitItem);

        trayIcon = new TrayIcon(Images.INSTANCE.getTrayInactiveIcon(), APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    frame.setVisible(!frame.isVisible());
                    if (frame.isVisible()) {
                        frame.setExtendedState(Frame.NORMAL);
                    }
                }
            }
        });

        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void show() {
        frame.setVisible(true);
    }

    public void showActiveStatus() {
        trayIcon.setImage(Images.INSTANCE.getTrayActiveIcon());
    }

    public void showInactiveStatus() {
        trayIcon.setImage(Images.INSTANCE.getTrayInactiveIcon());
    }

    public void showWarnStatus() {
        trayIcon.setImage(Images.INSTANCE.getTrayWarnIcon());
    }

    public void showErrorStatus() {
        trayIcon.setImage(Images.INSTANCE.getTrayErrorIcon());
    }

}
