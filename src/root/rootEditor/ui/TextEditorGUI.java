package root.rootEditor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextAreaEditorKit;
import org.fife.ui.rtextarea.RTextScrollPane;
import root.rootEditor.interfaces.SearchAndReplaceInterface;
import root.rootEditor.interfaces.ZoomInterface;
import root.rootEditor.utils.CommandPromptUtil;

/**
 *
 * @author Ebrahim
 */
public class TextEditorGUI extends JFrame implements SearchAndReplaceInterface, ZoomInterface {

    private String language;
    private JPanel editorPanel;
    private RSyntaxTextArea editorPane;
    private RTextScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, searchMenu, runFileMenu, viewMenu;
    private JMenuItem newFileItem, openFileItem, saveFileItem, saveFileAsItem, exitAppItem;
    private JMenuItem undoChangeItem, redoChangeItem, cutTextItem, copyTextItem, pasteTextItem, deleteFileItem;
    private JMenuItem runCodeItem;
    private JMenuItem findItem, findAndReplaceItem;
    private JMenuItem zoomInItem, zoomOutItem, restoreDefaultZoomItem;
    private JToolBar toolBar;
    private JButton newFileBtn, openFileBtn, saveBtn, undoBtn, redoBtn, runBtn, zoomInBtn, zoomOutBtn, restoreDefaultBtn, findBtn, replaceBtn, cutBtn, copyBtn, pasteBtn;
    private JTabbedPane newTab;
    private NewFileDialog newFileDialog;
    private RunConsolePanel runConsole;
    private JSplitPane splitPane;
    private File file;
    private FileReader fr;
    private FileWriter fw;
    private BufferedReader br;
    private BufferedWriter bw;

    public TextEditorGUI() {
        setTitle("rootEditor");
        setIconImage(new ImageIcon(getClass().getResource("/icons/mainIconBlck.png")).getImage());
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }
        setLocationRelativeTo(null);
        createMenu();
        createToolBar();
        editorPanel = new JPanel();
        newTab = new JTabbedPane();
        runConsole = new RunConsolePanel();

        /*splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, newTab, runConsole);
        splitPane.setResizeWeight(1.0);  // Editor takes 75% of the space, console 25%
        splitPane.setDividerLocation(0.95); // Initial divider location
        splitPane.setContinuousLayout(true);*/
        // Add the split pane to the main frame
        //add(splitPane);
        add(newTab);
        setVisible(true);
        updateView();
    }

    public void showNewFileDialog() {
        newFileDialog = new NewFileDialog(this);

        if (newFileDialog.isConfirmed()) {
            String fileName = newFileDialog.getFileName();
            String lang = newFileDialog.getChosenLanguage();
            createNewTab(lang, fileName);
            //updateView();
        }
    }

    public void showOpenFileDialog() {
        LoadingDialog.showLoadingDialog("Getting Directories...", 3000);
        OpenFileDialog dialog = new OpenFileDialog(this);
        if (dialog.isConfirmed()) {
            File selectedFile = dialog.getFile();
            String fileName = selectedFile.getName();

            // Determine the language based on the file extension
            String lang = "plain";
            if (fileName.endsWith(".java")) {
                lang = "Java";
            } else if (fileName.endsWith(".js")) {
                lang = "JavaScript";
            } else if (fileName.endsWith(".html")) {
                lang = "HTML";
            } else if (fileName.endsWith(".py")) {
                lang = "Python";
            } else if (fileName.endsWith(".css")) {
                lang = "CSS";
            }

            // Create a new tab and get the associated RSyntaxTextArea
            RSyntaxTextArea newEditorPane = createNewTab(lang, fileName);
            // Read the file and populate the editor pane
            readFile(selectedFile, newEditorPane);
        }
    }

    public void readFile(File file, RSyntaxTextArea textArea) {
        try {
            StringBuilder content = new StringBuilder();
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            br.close();

            // Set the text in the textArea associated with the new tab
            textArea.setText(content.toString());
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    public void saveFile() {
        // Get the selected tab index
        int selectedIndex = newTab.getSelectedIndex();

        if (selectedIndex == -1) {
            // No tab is selected
            JOptionPane.showMessageDialog(this, "No tab is selected for saving.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the tab title (which will be used as the filename)
        String fileName = newTab.getTitleAt(selectedIndex);

        // Get the RSyntaxTextArea from the selected tab
        RTextScrollPane scrollPane = (RTextScrollPane) newTab.getComponentAt(selectedIndex);
        RSyntaxTextArea editorPane = (RSyntaxTextArea) scrollPane.getViewport().getView();

        // Get the content from the editor
        String content = editorPane.getText();

        // Create the file and write the content to it
        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            // Notify the user that the file was saved
            JOptionPane.showMessageDialog(this, "File saved successfully as " + fileName, "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFileAs() {
        // Get the selected tab index
        int selectedIndex = newTab.getSelectedIndex();

        if (selectedIndex == -1) {
            // No tab is selected
            JOptionPane.showMessageDialog(this, "No tab is selected for saving.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open a JFileChooser dialog for "Save As"
        /*JFileChooser fileChooser = new JFileChooser();
        int userSelection = fileChooser.showSaveDialog(this);*/
        SaveFileDialog saveAsDialog = new SaveFileDialog(this);

        if (saveAsDialog.isConfirmed()) {
            File fileToSave = saveAsDialog.getFile();
            String fileName = fileToSave.getName();

            // Handle file extension (optional: change ".txt" to the appropriate default extension)
            if (!fileName.contains(".")) {
                fileName += ".txt";  // Default to .txt if no extension provided
                fileToSave = new File(fileToSave.getParentFile(), fileName);
            }

            // Check if the file already exists and prompt for confirmation
            if (fileToSave.exists()) {
                int overwriteConfirmation = JOptionPane.showConfirmDialog(
                        this,
                        "The file " + fileName + " already exists. Do you want to overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (overwriteConfirmation != JOptionPane.YES_OPTION) {
                    return; // Do not overwrite, exit the method
                }
            }

            // Get the RSyntaxTextArea from the selected tab
            RTextScrollPane scrollPane = (RTextScrollPane) newTab.getComponentAt(selectedIndex);
            RSyntaxTextArea editorPane = (RSyntaxTextArea) scrollPane.getViewport().getView();

            // Get the content from the editor
            String content = editorPane.getText();

            // Write the content to the file
            try {
                fw = new FileWriter(fileToSave);
                bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();

                // Update the tab name to the new file name
                newTab.setTitleAt(selectedIndex, fileName);
                updateView();

                // Notify the user that the file was saved
                JOptionPane.showMessageDialog(this, "File saved successfully as " + fileName, "Save As", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public RSyntaxTextArea createNewTab(String lang, String fileName) {
        RSyntaxTextArea editorPane = new RSyntaxTextArea(20, 60);
        editorPane.setSyntaxEditingStyle(langType(lang));
        editorPane.setCodeFoldingEnabled(true);
        editorPane.setAnimateBracketMatching(true);
        RTextScrollPane scrollPane = new RTextScrollPane(editorPane);

        newTab.addTab(fileName, scrollPane);
        int index = newTab.getTabCount() - 1;
        newTab.setTabComponentAt(index, new TabComponent(newTab, fileName));
        newTab.setSelectedIndex(index);
        LoadingDialog.showLoadingDialog("Loading File. Please wait...", 5000);
        updateView();

        return editorPane;
    }

    private RSyntaxTextArea getCurrentEditorPane() {
        int selectedIndex = newTab.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "No tab is selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        RTextScrollPane scrollPane = (RTextScrollPane) newTab.getComponentAt(selectedIndex);
        return (RSyntaxTextArea) scrollPane.getViewport().getView();
    }

    private void clearHighlighting(RSyntaxTextArea editor) {
        editor.getHighlighter().removeAllHighlights(); // Clear existing highlights
    }

    public void createMenu() {
        menuBar = new JMenuBar();

        //File menu
        fileMenu = new JMenu("File");
        newFileItem = new JMenuItem("New", new ImageIcon(getClass().getResource("/icons/newFile2.png")));
        openFileItem = new JMenuItem("Open", new ImageIcon(getClass().getResource("/icons/file-open-2.png")));
        saveFileItem = new JMenuItem("Save", new ImageIcon(getClass().getResource("/icons/saveFile.png")));
        saveFileAsItem = new JMenuItem("Save As", new ImageIcon(getClass().getResource("/icons/saveAs.png")));
        exitAppItem = new JMenuItem("Exit", new ImageIcon(getClass().getResource("/icons/exitApp.png")));

        fileMenu.add(newFileItem);
        fileMenu.add(openFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(saveFileAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitAppItem);

        newFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewFileDialog();
            }
        });
        openFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOpenFileDialog();
            }
        });
        saveFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = newTab.getSelectedIndex();
                saveFile();
            }
        });
        saveFileAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        });
        exitAppItem.addActionListener((e) -> {
            System.exit(0);
        });

        //Edit Menu
        editMenu = new JMenu("Edit");

        undoChangeItem = new JMenuItem("Undo", new ImageIcon(getClass().getResource("/icons/undo.jpg")));
        redoChangeItem = new JMenuItem("Redo", new ImageIcon(getClass().getResource("/icons/redo.jpg")));
        cutTextItem = new JMenuItem("Cut", new ImageIcon(getClass().getResource("/icons/cut.jpg")));
        copyTextItem = new JMenuItem("Copy", new ImageIcon(getClass().getResource("/icons/copy.jpg")));
        pasteTextItem = new JMenuItem("Paste", new ImageIcon(getClass().getResource("/icons/paste.jpg")));
        deleteFileItem = new JMenuItem("Delete", new ImageIcon(getClass().getResource("/icons/delete.png")));

        editMenu.add(undoChangeItem);
        editMenu.add(redoChangeItem);
        editMenu.add(cutTextItem);
        editMenu.add(copyTextItem);
        editMenu.add(pasteTextItem);
        editMenu.addSeparator();
        editMenu.add(deleteFileItem);

        undoChangeItem.addActionListener(new RTextAreaEditorKit.UndoAction());
        redoChangeItem.addActionListener(new RTextAreaEditorKit.RedoAction());
        cutTextItem.addActionListener(new RTextAreaEditorKit.CutAction());
        copyTextItem.addActionListener(new RTextAreaEditorKit.CopyAction());
        pasteTextItem.addActionListener(new RTextAreaEditorKit.PasteAction());
        deleteFileItem.addActionListener(new RTextAreaEditorKit.DeleteLineAction());

        //Run file menu
        runFileMenu = new JMenu("Run");
        runCodeItem = new JMenuItem("Run File");
        runFileMenu.add(runCodeItem);

        runCodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = newTab.getSelectedIndex();
                if (selectedIndex != -1) {
                    //String fileName = newTab.getTitleAt(selectedIndex);
                    //String language = getLanguageForFile(fileName);  // Determine language based on file name/extension

                    saveFile();
                    //runCode(language, fileName); // Call the runCode method
                    String currentDirectory = getCurrentDirectory(); // Implement this method to get the current directory
                    LoadingDialog.showLoadingDialog("Opening terminal...", 2000);
                    CommandPromptUtil.openCommandPrompt(currentDirectory);

                } else {
                    JOptionPane.showMessageDialog(TextEditorGUI.this, "No file selected to run.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Search and replace menu
        searchMenu = new JMenu("Search");

        findItem = new JMenuItem("Find");
        findAndReplaceItem = new JMenuItem("Replace");

        searchMenu.add(findItem);
        searchMenu.add(findAndReplaceItem);

        findItem.addActionListener(e -> {
            String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
            find(searchText);

        });

        findAndReplaceItem.addActionListener(e -> {

            String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
            String replaceText = JOptionPane.showInputDialog(this, "Enter text to replace with:");
            findAndReplace(searchText, replaceText);
        });

        //View menu
        viewMenu = new JMenu("View");

        zoomInItem = new JMenuItem("Zoom In");
        zoomOutItem = new JMenuItem("Zoom Out");
        restoreDefaultZoomItem = new JMenuItem("Restore Default Zoom");

        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(restoreDefaultZoomItem);

        zoomInItem.addActionListener(e -> {
            zoomIn(getCurrentEditorPane());
        });
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

        zoomOutItem.addActionListener(e -> {
            zoomOut(getCurrentEditorPane());
        });
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

        restoreDefaultZoomItem.addActionListener(e -> {
            restoreDefaultSize(getCurrentEditorPane());
        });
        restoreDefaultZoomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(viewMenu);
        menuBar.add(runFileMenu);
        this.setJMenuBar(menuBar);

    }

    public void createToolBar() {
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.white);
        //toolBar.setBorder(new BevelBorder(2));
        //Setting up buttons
        //File buttons
        createButton(newFileBtn, "/icons/add-file.png", "New File", 24).addActionListener((e) -> {
            //showNewFileDialog();
            newFileItem.doClick();
        });
        createButton(openFileBtn, "/icons/open-folder.png", "Open File", 24).addActionListener((e) -> {
            //showOpenFileDialog();
            openFileItem.doClick();
        });
        createButton(saveBtn, "/icons/save.png", "Save File", 24).addActionListener((e) -> {
            saveFileItem.doClick();
        });

        toolBar.addSeparator();
        //Edit buttons
        createButton(undoBtn, "/icons/new-undo.png", "Undo", 24).addActionListener((e) -> {
            undoChangeItem.doClick();
        });
        createButton(redoBtn, "/icons/new-redo.png", "Redo", 24).addActionListener((e) -> {
            redoChangeItem.doClick();
        });
        createButton(cutBtn, "/icons/scissors.png", "Cut", 24).addActionListener((e) -> {
            cutTextItem.doClick();
        });
        createButton(copyBtn, "/icons/copy-file.png", "Copy", 24).addActionListener((e) -> {
            copyTextItem.doClick();
        });
        createButton(pasteBtn, "/icons/newPasteIcon.png", "Paste", 24).addActionListener((e) -> {
            pasteTextItem.doClick();
        });

        toolBar.addSeparator();
        //Search buttons
        createButton(findBtn, "/icons/find-text.png", "Find text", 24).addActionListener((e) -> {
            findItem.doClick();
        });
        createButton(replaceBtn, "/icons/text.png", "Replace text", 24).addActionListener((e) -> {
            findAndReplaceItem.doClick();
        });

        toolBar.addSeparator();
        //View
        createButton(zoomInBtn, "/icons/newZoomIn.png", "Zoom In", 24).addActionListener((e) -> {
            zoomInItem.doClick();
        });
        createButton(zoomOutBtn, "/icons/newZoomOut.png", "Zoom Out", 24).addActionListener((e) -> {
            zoomOutItem.doClick();
        });
        createButton(restoreDefaultBtn, "/icons/restoreZoom.png", "Restore Zoom", 24).addActionListener((e) -> {
            restoreDefaultZoomItem.doClick();
        });

        toolBar.addSeparator();
        //Run button
        createButton(runBtn, "/icons/run.png", "Run File", 24).addActionListener((e) -> {
            runCodeItem.doClick();
        });

        this.add(toolBar, BorderLayout.NORTH);
    }

    private JButton createButton(JButton button, String pathToIcon, String buttonName, int sizeDimension) {
        button = new JButton();
        ImageIcon icon = new ImageIcon(getClass().getResource(pathToIcon));
        try {
            button = new JButton(icon);
            button.setPreferredSize(new Dimension(sizeDimension, sizeDimension));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setRolloverEnabled(true);
            button.setToolTipText(buttonName);
            button.setFocusable(false);
            toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
            toolBar.add(button);
        } catch (Exception e) {
        }
        return button;
    }

    private String getCurrentDirectory() {
        // Return the path to the current directory or the directory of the open file
        // For example, if you have an open file, you can get its directory:
        // return new File(currentFilePath).getParent();
        return System.getProperty("user.dir"); // Returns the current working directory
    }

    /*public void runCode(String language, String filePath) {
        runConsole.clearConsole();  // Clear previous output

        ProcessBuilder processBuilder = null;
        switch (language) {
            case "Java":
                processBuilder = new ProcessBuilder("javac", filePath);  // Compile Java code
                executeProcess(processBuilder);
                String javaFileName = filePath.replace(".java", "");
                processBuilder = new ProcessBuilder("java", javaFileName);  // Run Java program
                break;
            case "Python":
                processBuilder = new ProcessBuilder("python", filePath);  // Run Python script
                break;
            case "JavaScript":
                processBuilder = new ProcessBuilder("node", filePath);  // Run JS using Node.js
                break;
            default:
                runConsole.appendOutput("Unsupported language.");
                return;
        }

        if (processBuilder != null) {
            executeProcess(processBuilder);
        }
        updateView();
    }*/

 /*private void executeProcess(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            runConsole.setProcess(process);  // Pass the process to the console

            // Thread for reading the standard output (stdout)
            Thread outputThread = new Thread(() -> {
                try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        runConsole.appendOutput(line);
                    }
                } catch (IOException e) {
                    runConsole.appendOutput("Error reading output: " + e.getMessage());
                }
            });

            // Thread for reading the error output (stderr)
            Thread errorThread = new Thread(() -> {
                try ( BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        runConsole.appendOutput("ERROR: " + line);
                    }
                } catch (IOException e) {
                    runConsole.appendOutput("Error reading error stream: " + e.getMessage());
                }
            });

            // Start both threads to read output
            outputThread.start();
            errorThread.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            outputThread.join();  // Ensure output is fully read
            errorThread.join();   // Ensure errors are fully read

            runConsole.appendOutput("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            runConsole.appendOutput("Error running code: " + e.getMessage());
        }
    }*/
    @Override
    public void zoomIn(RSyntaxTextArea editorPane) {
        int increment = 2;
        Font font = editorPane.getFont();
        String fontName = font.getFamily();
        int fontSize = font.getSize() + increment;
        int fontStyle = font.getStyle();

        if (fontSize > 30) {
            return;
        }
        editorPane.setFont(new Font(fontName, fontStyle, fontSize));
    }

    @Override
    public void zoomOut(RSyntaxTextArea editorPane) {
        int decrement = 2;
        Font font = editorPane.getFont();
        String fontName = font.getFamily();
        int fontSize = font.getSize() - decrement;
        int fontStyle = font.getStyle();

        if (fontSize < 4) {
            return;
        }
        editorPane.setFont(new Font(fontName, fontStyle, fontSize));
    }

    @Override
    public void restoreDefaultSize(RSyntaxTextArea editorPane) {
        int defaultSize = 13;
        Font font = editorPane.getFont();
        String fontName = font.getFamily();
        int fontSize = defaultSize;
        int fontStyle = font.getStyle();
        editorPane.setFont(new Font(fontName, fontStyle, fontSize));
    }

    /*private String getLanguageForFile(String fileName) {
        if (fileName.endsWith(".java")) {
            return "Java";
        }
        if (fileName.endsWith(".py")) {
            return "Python";
        }
        if (fileName.endsWith(".js")) {
            return "JavaScript";
        }
        return "Unsupported";
    }*/
    public java.lang.String langType(String language) {
        String type = "text/plain";
        switch (language) {
            case "Java":
                type = "text/java";
                break;
            case "JavaScript":
                type = "text/javascript";
                break;
            case "HTML":
                type = "text/html";
                break;
            case "CSS":
                type = "text/css";
                break;
            case "Python":
                type = "text/python";
                break;
            default:
                type = "text/plain";
        }
        return type;
    }

    public void updateView() {
        newTab.revalidate();
        newTab.repaint();
        //runConsole.revalidate();
        //runConsole.repaint();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void find(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter text to find.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected tab's text area
        RSyntaxTextArea editorPane = getCurrentEditorPane();
        if (editorPane == null) {
            return;
        }

        // Clear previous highlights
        editorPane.getHighlighter().removeAllHighlights();
        String content = editorPane.getText();
        int index = 0;

        // Use a loop to find all occurrences
        while ((index = content.indexOf(searchText, index)) != -1) {
            try {
                // Highlight the found text
                editorPane.getHighlighter().addHighlight(index, index + searchText.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
                index += searchText.length(); // Move the index forward
            } catch (BadLocationException e) {
                e.printStackTrace(); // Handle the exception
            }
        }

        // Notify the user if no instances were found
        if (index == 0) {
            JOptionPane.showMessageDialog(this, "Text not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Optionally focus on the first instance
            editorPane.setCaretPosition(content.indexOf(searchText));  // Move the cursor to the first found text
            editorPane.requestFocus();  // Focus the editor
        }
        // Call this method after highlighting new text
        editorPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearHighlighting(editorPane);
            }

        });
    }

    @Override
    public void findAndReplace(String searchText, String replaceText) {
        if (searchText == null || searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter text to find.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (replaceText == null) {
            replaceText = ""; // Allow replacing with an empty string
        }

        // Get the selected tab's text area
        RSyntaxTextArea editorPane = getCurrentEditorPane();
        if (editorPane == null) {
            return;
        }

        // Clear previous highlights
        editorPane.getHighlighter().removeAllHighlights();
        String content = editorPane.getText();
        int index = 0;
        int occurrences = 0;

        // Get the document to perform replace operations
        javax.swing.text.Document doc = editorPane.getDocument();

        // Use a loop to find and replace all occurrences
        while ((index = content.indexOf(searchText, index)) != -1) {
            try {
                // Highlight the found text
                editorPane.getHighlighter().addHighlight(index, index + searchText.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));

                // Replace the found text using document replace
                doc.remove(index, searchText.length());
                doc.insertString(index, replaceText, null); // Notify the document, ensuring undoability

                index += replaceText.length(); // Move the index forward after replacement
                occurrences++;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        // Notify if no instances were found
        if (occurrences == 0) {
            JOptionPane.showMessageDialog(this, "Text not found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Optionally highlight replaced text
        content = editorPane.getText(); // Refresh content after replacements
        index = 0; // Reset index for highlighting replaced text
        while ((index = content.indexOf(replaceText, index)) != -1) {
            try {
                editorPane.getHighlighter().addHighlight(index, index + replaceText.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN)); // Different color for replaced text
                index += replaceText.length(); // Move the index forward
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        // Optionally, clear highlights after a mouse click
        editorPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearHighlighting(editorPane);
            }
        });

        JOptionPane.showMessageDialog(this, occurrences + " occurrences replaced.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
