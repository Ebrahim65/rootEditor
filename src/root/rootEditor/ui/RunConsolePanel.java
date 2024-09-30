package root.rootEditor.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ebrahim
 */
public class RunConsolePanel extends JPanel {

    private JTextArea consoleOutput;
    private JTextField consoleInput;
    private Process process; // Reference to the current running process

    public RunConsolePanel() {
        setLayout(new BorderLayout());

        // Text area for output
        consoleOutput = new JTextArea(10, 60);
        consoleOutput.setEditable(false);  // Output should not be editable
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        add(scrollPane, BorderLayout.CENTER);

        // Text field for input
        consoleInput = new JTextField();
        consoleInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendInput(consoleInput.getText());
                consoleInput.setText("");  // Clear the input field after sending input
            }
        });
        add(consoleInput, BorderLayout.SOUTH);
    }

    public void appendOutput(String output) {
        consoleOutput.append(output + "\n");
    }

    public void clearConsole() {
        consoleOutput.setText("");
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void sendInput(String input) {
        if (process != null) {
            // Use a PrintWriter to send input to the process
            PrintWriter writer = new PrintWriter(process.getOutputStream());
            writer.println(input);
            writer.flush();  // Flush the stream to ensure input is sent
        } else {
            appendOutput("No process is currently running.");
        }
    }
}
