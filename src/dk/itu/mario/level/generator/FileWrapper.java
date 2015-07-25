package dk.itu.mario.level.generator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class analyzes the statistics from the player ratings .arff file.
 * Created by sd on 7/25/15.
 */
public class FileWrapper {
    private File file;
    private String filename;

    public FileWrapper(String filename) {
        this.filename = filename;
        this.file = new File(filename);
    }

    //Counts the number of lines in the arff data file.
    public int countDataLines() {
        try(BufferedInputStream input = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = input.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count-15;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Returns true if the file exists in the current directory.
    public boolean exists() {
        return !file.isDirectory() && file.exists();
    }
}
