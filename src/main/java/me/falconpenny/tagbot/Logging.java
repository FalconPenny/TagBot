package me.falconpenny.tagbot;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.logging.Level;

public class Logging {
    public static Logging log = new Logging();

    public void log(Level loglevel, Object... objects) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(objects).map(this::str).forEach(it -> builder.append(it).append(' '));
        String message = builder.toString();
        System.out.println(new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ISO_LOCAL_TIME) + " [" + loglevel.getName() + "] : " + message);
    }

    public void info(Object... message) {
        log(Level.INFO, message);
    }

    public void warn(Object... message) {
        log(Level.WARNING, message);
    }

    public void severe(Object... message) {
        log(Level.SEVERE, message);
    }

    private String str(Object object) {
        return object == null ? "null" : object.toString();
    }

    private Logging() {
    }
}
