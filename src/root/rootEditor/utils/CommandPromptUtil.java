package root.rootEditor.utils;

import java.io.IOException;

/**
 *
 * @author Ebrahim
 */

import java.io.IOException;

public class CommandPromptUtil {

    // Method to open command prompt
    public static void openCommandPrompt(String directory) {
        try {
            // Use ProcessBuilder to open cmd in a new window
            String command = "cmd.exe /K cd " + directory;
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", "cmd.exe", "/K", "cd", directory});
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error opening command prompt: " + e.getMessage());
        }
    }
}


