package root.rootEditor.ui;

//import java.awt.FileDialog;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Ebrahim
 */
public class OpenFileDialog extends JFileChooser {

    private File file;
    private boolean confirm;

    public OpenFileDialog(JFrame frame) {

        if (this.showOpenDialog(frame) == APPROVE_OPTION) {
            file = this.getSelectedFile();
            confirm = true;
        } else {
            confirm = false;
        }
    }

    public File getFile() {
        return file;
    }

    public boolean isConfirmed() {
        return confirm;
    }

}
