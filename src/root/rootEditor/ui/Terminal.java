package root.rootEditor.ui;

/**
 *
 * @author Ebrahim
 */
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.File;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import root.rootEditor.ui.TextEditorGUI;

public class Terminal extends JPanel {

    private JTextArea outputArea;   // Area to display terminal output
    private JTextField inputField;   // Field to accept user input
    private PrintStream printStream; // To redirect System output
    private Process process;          // To manage the process for executing commands
    private TextEditorGUI teu;

    public Terminal() {
        setLayout(new BorderLayout());

        // Text area for output
        outputArea = new JTextArea(10, 60);
        outputArea.setEditable(false);  // Output should not be editable
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Text field for input
        inputField = new JTextField();
        add(inputField, BorderLayout.SOUTH);
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
        System.setOut(printStream);
        System.setErr(printStream);
        setupInputField();
    }

    // Setup input field to accept commands
    private void setupInputField() {
        inputField.addActionListener(e -> {
            String command = acceptInput();
            executeCommand(command);
        });
    }

    // Method to accept input from the user
    public String acceptInput() {
        String input = inputField.getText();
        inputField.setText(""); // Clear input field
        return input; // Return the user input
    }

    // Method to execute a command in a new process
    private void executeCommand(String command) {
        try {
            process = Runtime.getRuntime().exec(command);
            new Thread(() -> readOutput(process)).start();
            new Thread(() -> readError(process)).start();
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    public void executeCommand(String language, String filePath) {
        String[] command = null;

        switch (language.toLowerCase()) {
            case "java":
                // Compile the Java file first
                command = new String[]{"javac", filePath}; // Compile Java file
                executeProcess(command);

                // Extract class name and then run it
                String className = getClassName(filePath);
                command = new String[]{"java", className}; // Run the compiled Java class
                break;

            case "python":
                command = new String[]{"python", filePath}; // Executes Python script
                break;

            case "javascript":
                command = new String[]{"node", filePath}; // Executes JavaScript file using Node.js
                break;

            default:
                displayError("Unsupported language: " + language);
                return;
        }

        executeProcess(command);
    }

// Method to execute the process
    private void executeProcess(String[] command) {
        try {
            process = new ProcessBuilder(command).start(); // Use ProcessBuilder to handle the command
            new Thread(() -> readOutput(process)).start();
            new Thread(() -> readError(process)).start();
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

// Helper method to get the class name from the Java file path
    private String getClassName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.')); // Removes .java extension to get the class name
    }

    // Read output from the process
    private void readOutput(Process process) {
        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                displayOutput(line);
            }
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    // Read errors from the process
    private void readError(Process process) {
        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                displayError(line);
            }
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    // Method to display output in the terminal
    public void displayOutput(String output) {
        outputArea.append(output + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength()); // Scroll to the bottom
    }

    // Method to display errors/exceptions
    public void displayError(String error) {
        outputArea.append("ERROR: " + error + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength()); // Scroll to the bottom
    }

    private String getFilePathFromTab(RSyntaxTextArea editorPane) {
        return editorPane.getName();
    }

    // Custom OutputStream to redirect System.out and System.err
    private class CustomOutputStream extends OutputStream {

        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf((char) b));
                textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
            });
        }
    }

    // Additional method to clear the terminal output
    public void clearOutput() {
        outputArea.setText("");
    }
}
