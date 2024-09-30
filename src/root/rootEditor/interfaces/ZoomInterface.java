/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package root.rootEditor.interfaces;

import java.awt.TextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 *
 * @author Ebrahim
 */
public interface ZoomInterface {
    public void zoomIn(RSyntaxTextArea textArea);
    public void zoomOut(RSyntaxTextArea textArea);
    public void restoreDefaultSize(RSyntaxTextArea textArea);
}
