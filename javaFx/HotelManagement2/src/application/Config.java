package application;

import java.io.*;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "src/config.properties";
    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config file", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, null);
        } catch (IOException e) {
            throw new RuntimeException("Could not write to config file", e);
        }
    }
}
