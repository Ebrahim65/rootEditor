package root.rootEditor.ui;

//import java.awt.FileDialog;
import java.io.File;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import javax.swing.JFrame;

/**
 *
 * @author Ebrahim
 */
public class SaveFileDialog extends JFileChooser {

    private File file;
    private boolean confirm;

    public SaveFileDialog(JFrame frame) {

        if (this.showSaveDialog(frame) == APPROVE_OPTION) {
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
