package jt.upwork.com.logger;

import com.jtattoo.plaf.fast.FastLookAndFeel;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @author jamestravol
 */
public class Main {

    public static final String ROOT = "SOFTWARE\\ComLogger";
    public static final String FL = "fl";
    public static final String LL = "ll";

    private static final long EXPIRE_MILLS = 2592000000L;

    public static volatile long fl = 0L;
    public static volatile boolean licensed = false;
    private static String message;
    private static boolean forceExit = false;
    public static boolean block = false;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException,
            InstantiationException, IllegalAccessException {

        JWindow splash = new JWindow();
        ImageIcon image = new ImageIcon(Main.class.getResource("/images/splash.png"));
        splash.getContentPane().add(new JLabel("", image, SwingConstants.CENTER));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        splash.setBounds((int) ((screenSize.getWidth() - image.getIconWidth()) / 2),
                (int) ((screenSize.getHeight() - image.getIconHeight()) / 2),
                image.getIconWidth(), image.getIconHeight());

        splash.setAlwaysOnTop(true);
        splash.setVisible(true);

        FastLookAndFeel.setTheme("Giant-Font");
        UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");

        try {
            checkAppState();
        } catch (Exception e) {
            message = "ComLogger unable to start";
            forceExit = true;
        }

        final Application application = Application.INSTANCE;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.getStackTrace();
        }

        splash.setVisible(false);

        application.show();
        application.getFrame().setAlwaysOnTop(true);
        application.getFrame().toFront();
        application.getFrame().requestFocus();
        application.getFrame().setAlwaysOnTop(false);
        if (message != null) {
            JOptionPane.showMessageDialog(application.getFrame(), message, "Info", block ? JOptionPane.WARNING_MESSAGE :
                    forceExit ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }
        if (forceExit) {
            SwingUtilities.invokeLater(() -> System.exit(1));
        }

    }


    private static void checkAppState() {

        long current = System.currentTimeMillis();

        if (Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, ROOT)) {

            fl = Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, FL);

            if (checkSerial(fl)) {
                licensed = true;
                Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, LL, current);
                return;
            }

            long ll = Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, LL);

            long days = (EXPIRE_MILLS + fl - current) / 86400000L + 1L;

            if (checkTrial(fl, ll, current)) {
                Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, LL, current);
                message = "You are running a trial version of ComLogger! There are " + days + " days left";
                return;
            }

            message = "ComLogger has expired!";
            block = true;

        } else {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, ROOT);
            fl = current;
            Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, FL, current);
            Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, LL, current);

            if (checkSerial(fl)) {
                licensed = true;
                Advapi32Util.registrySetLongValue(WinReg.HKEY_CURRENT_USER, ROOT, LL, current);
                return;
            }

            long days = EXPIRE_MILLS / 86400000L;

            message = "You are running a trial version of ComLogger! There are " + days + " days left";

        }
    }

    private static boolean checkTrial(long fl, long ll, long current) {

        if (current > fl + EXPIRE_MILLS) {
            return false;
        }

        if (current <= fl) {
            return false;
        }

        if (current < ll) {
            return false;
        }

        return true;
    }

    private static boolean checkSerial(long fl) {

        final Path path = Paths.get(System.getProperty("user.dir"), "/serial");

        if (path.toFile().exists()) {

            try {

                final String serialStr = new String(Base64.getDecoder().decode(Files.readAllBytes(path)));

                final long serial = Long.parseLong(serialStr);

                return serial % Main.fl == 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;

    }

}
