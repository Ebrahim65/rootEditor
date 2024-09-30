package root.rootEditor.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Ebrahim
 */
public class NewFileDialog extends JDialog {

    private final String[] langArray = {"Java", "JavaScript",
        "HTML", "CSS", "Python", "PlainText"};
    private String chosenLanguage, extension;
    private boolean confirmed;
    private String fileName;
    private JComboBox languages;
    private JTextField fileNameField;
    private JLabel enterNameHere;
    private JButton doneButton;

    public NewFileDialog(JFrame frame) {
        super(frame);
        this.setTitle("New File");
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/newFile2.png")).getImage());
        this.setLayout(new FlowLayout());
        this.setSize(300, 150); // Adjusted size to be more compact
        this.setModal(true);
        this.setLocationRelativeTo(frame);

        languages = new JComboBox<>(langArray);
        fileNameField = new JTextField(20);
        enterNameHere = new JLabel("Enter file name:");
        doneButton = new JButton("Create");

        this.add(enterNameHere);
        this.add(fileNameField);
        this.add(new JLabel("Choose language:"));
        this.add(languages);
        this.add(doneButton);

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                chosenLanguage = languages.getSelectedItem().toString();
                switch (chosenLanguage) {
                    case "Java":
                        extension = ".java";
                        break;
                    case "JavaScript":
                        extension = ".js";
                        break;
                    case "HTML":
                        extension = ".html";
                        break;
                    case "CSS":
                        extension = ".css";
                        break;
                    case "Python":
                        extension = ".py";
                        break;
                    default:
                        extension = ".txt";
                }
                fileName = fileNameField.getText().trim() + extension;

                if (fileName.isEmpty()) {
                    JOptionPane.showMessageDialog(NewFileDialog.this,
                            "File name cannot be empty!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    if (new File(fileName).exists()) {
                        JOptionPane.showMessageDialog(NewFileDialog.this,
                                "File with this name already exists!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        confirmed = true;
                        NewFileDialog.this.dispose();
                    }

                }

            }
        });

        this.setVisible(true);
    }

    public String getChosenLanguage() {
        return chosenLanguage;
    }

    public void setChosenLanguage(String chosenLanguage) {
        this.chosenLanguage = chosenLanguage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

}
