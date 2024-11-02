package su.kartushin.busAPI.utils;

import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {
    public static void logError(Logger logger, Exception e){
        logger.error(e.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        logger.trace(stackTrace);
    }
}
