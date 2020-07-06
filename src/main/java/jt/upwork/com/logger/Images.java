package jt.upwork.com.logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author jamestravol
 */
public class Images {

    public final static Images INSTANCE = new Images();

    private final Image trayInactiveIcon;
    private final Image trayActiveIcon;
    private final Image trayWarnIcon;
    private final Image trayErrorIcon;
    private final ImageIcon hostConnected;
    private final ImageIcon hostDisConnected;
    private final ImageIcon hostIdle;
    private final ImageIcon deleted;
    private final Image logo;

    public Images() {
        trayInactiveIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/tray_inactive.png"));
        trayActiveIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/tray_active.png"));
        trayWarnIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/tray_warn.png"));
        trayErrorIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/tray_error.png"));
        hostConnected = new ImageIcon(Main.class.getResource("/images/host_connected.png"));
        hostDisConnected = new ImageIcon(Main.class.getResource("/images/host_disconnected.png"));
        hostIdle = new ImageIcon(Main.class.getResource("/images/host_idle.png"));
        deleted = new ImageIcon(Main.class.getResource("/images/delete.png"));
        logo = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/logo.jpg"));
    }

    public Image getTrayInactiveIcon() {
        return trayInactiveIcon;
    }

    public Image getTrayActiveIcon() {
        return trayActiveIcon;
    }

    public Image getTrayWarnIcon() {
        return trayWarnIcon;
    }

    public Image getTrayErrorIcon() {
        return trayErrorIcon;
    }

    public ImageIcon getHostConnected() {
        return hostConnected;
    }

    public ImageIcon getHostDisConnected() {
        return hostDisConnected;
    }

    public ImageIcon getHostIdle() {
        return hostIdle;
    }

    public ImageIcon getDeleted() {
        return deleted;
    }

    public Image getLogo() {
        return logo;
    }
}
