package jt.upwork.com.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author jamestravol
 */
public class Config extends Properties {

    public static final String DEFAULT_APP_PROPERTIES = "/config/app.properties";
    public static final String APP_PROPERTIES = "/app.properties";

    public final static Config INSTANCE = new Config();

    public Config() {
        try (final FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir"), APP_PROPERTIES))) {
            load(fis);
        } catch (IOException e) {
            try {
                load(Config.class.getResourceAsStream(DEFAULT_APP_PROPERTIES));
            } catch (IOException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save() {
        try (final FileOutputStream stream = new FileOutputStream(new File(System.getProperty("user.dir"), APP_PROPERTIES))) {
            store(stream, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
