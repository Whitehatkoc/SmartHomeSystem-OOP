import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileRW {
    /**
     * This method is used to read the contents of a file and store each line as an element in a string array.
     *
     * @param path The file path to read from.
     * @return A string array containing the lines read from the file.
     */
    public static String[] readFromFile(String path) {
        try {
            int i = 0;
            int length = Files.readAllLines(Paths.get(path)).size();
            String[] results = new String[length];
            for (String line : Files.readAllLines(Paths.get(path))) {
                results[i++] = line;
            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is used to write content to a file with options to specify whether to append or create a new line.
     *
     * @param path The file path to write to.
     * @param content The content to be written to the file.
     * @param append If true, the content will be appended to the end of the file. If false, the file will be overwritten.
     * @param newLine If true, a new line will be added after the content. If false, no new line will be added.
     */
    public static void writeToFile(String path, String content, boolean append, boolean newLine) {
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(path, append));
            ps.print(content + (newLine ? "\n" : ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { //Flushes all the content and closes the stream if it has been successfully created.
                ps.flush();
                ps.close();
            }
        }
    }
}
