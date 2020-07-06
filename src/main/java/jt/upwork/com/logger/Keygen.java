package jt.upwork.com.logger;

import com.jtattoo.plaf.fast.FastLookAndFeel;

import javax.swing.*;

/**
 * @author jamestravol
 */
public class Keygen {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        FastLookAndFeel.setTheme("Giant-Font");
        UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");

        JFrame frame = new JFrame("Keygen");
        frame.setIconImage(Images.INSTANCE.getLogo());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new KeygenPanel());
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
