package dk.itu.mario.level.generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This file processes the CONFIG.txt file.
 * Created by sd on 7/26/15.
 */
public class ConfigFileWrapper extends FileWrapper {
    private HashMap<String, String> preferences = new HashMap<>();

    public ConfigFileWrapper(String filename) {
        super(filename);
    }

    public void process() {
        try (BufferedReader input = new BufferedReader(new FileReader(getFilename()))) {
            String s = null;
            while ((s = input.readLine()) != null) {
                processExpression(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getPreferences() {
        return this.preferences;
    }

    public void processExpression(String expression) {
        String[] parts = expression.split("=");
        preferences.put(parts[0].trim(), parts[1].trim());
    }
}
