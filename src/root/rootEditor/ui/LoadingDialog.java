package root.rootEditor.ui;

/**
 *
 * @author Ebrahim
 */
import javax.swing.*;
import java.awt.*;

public class LoadingDialog {

    private static JDialog dialog;
    private static JProgressBar progressBar;

    public static void showLoadingDialog(String message, int durationInMillis) {
        // Create a new dialog
        dialog = new JDialog();
        dialog.setUndecorated(true); // Remove window decorations
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Prevent closing
        dialog.setModal(true); // Modal dialog
        dialog.setLayout(new BorderLayout());

        // Create a loading label
        JLabel loadingLabel = new JLabel(message, JLabel.CENTER);
        loadingLabel.setFont(new Font("Sans", Font.BOLD, 12));
        dialog.add(loadingLabel, BorderLayout.CENTER);

        // Create and configure the progress bar
        progressBar = new JProgressBar(0, durationInMillis);
        progressBar.setIndeterminate(true); // Set to indeterminate mode for animation
        dialog.add(progressBar, BorderLayout.SOUTH);

        // Set dialog size and location
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(null); // Center on screen

        // Timer to close the dialog after the specified duration
        new Timer(durationInMillis, e -> closeDialog()).start();

        dialog.setVisible(true); // Show dialog
    }

    private static void closeDialog() {
        dialog.dispose(); // Dispose of the dialog
    }
}
