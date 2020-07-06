package jt.upwork.com.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author jamestravol
 */
public class Utils {

    public static String getStackTrace(Throwable ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

}
