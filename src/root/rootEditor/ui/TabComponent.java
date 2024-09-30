package root.rootEditor.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Ebrahim
 */
public class TabComponent extends JPanel {

    private final JTabbedPane pane;

    public TabComponent(final JTabbedPane pane, String title) {
        this.pane = pane;
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        this.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Sans", Font.BOLD, 12));
        this.add(label);
        ImageIcon image;
        JButton closeButton = null;
        try {
            image = new ImageIcon(getClass().getResource("/icons/closePage.png"));

            closeButton = new JButton(image);
            closeButton.setPreferredSize(new Dimension(17, 17));
            closeButton.setContentAreaFilled(false);
            closeButton.setBorderPainted(false);
            closeButton.setFocusPainted(false);
            closeButton.setRolloverEnabled(true);
        } catch (Exception e) {
        }

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = pane.indexOfTabComponent(TabComponent.this);
                if (index != -1) {
                    pane.remove(index);
                }
            }
        });

        this.add(closeButton);
    }
}
