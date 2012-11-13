package jp.takamichie.desktop.logcatviewer.classes;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CustomOptionPane extends JOptionPane {

    private CustomOptionPane(Object message, int messageType, int optionType) {
	super(message, messageType, optionType);
    }

    @Override
    public void selectInitialValue() {
	// superを呼ばない
    }

    public static String showDialog(Component parentComponent, String title,
	    String message, boolean editable, String... items) {
	JPanel panel = new JPanel();
	JComboBox<String> editor = new JComboBox<>(items);
	String result = null;
	editor.setEditable(editable);
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.add(new JLabel(message));
	panel.add(editor);

	CustomOptionPane pane = new CustomOptionPane(panel,
		JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	JDialog dialog = pane.createDialog(parentComponent, title);
	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	dialog.setVisible(true);
	if (new Integer(JOptionPane.OK_OPTION).equals(pane.getValue())) {
	    result = editor.getSelectedItem().toString();
	}
	return result;
    }
}
