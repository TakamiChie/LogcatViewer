package jp.takamichie.desktop.logcatviewer.classes;

import java.awt.Component;
import java.awt.FlowLayout;

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
	JComboBox<String> box = showDialogCore(parentComponent, title, message, editable, items);
	String result = null;
	if (box != null) {
	    result = box.getSelectedItem().toString();
	}
	return result;
    }

    public static int showDialogGetIndex(Component parentComponent, String title,
	    String message, boolean editable, String... items) {
	JComboBox<String> box = showDialogCore(parentComponent, title, message, editable, items);
	int result = -1;
	if (box != null) {
	    result = box.getSelectedIndex();
	}
	return result;
    }

    private static JComboBox<String> showDialogCore(Component parentComponent, String title,
	    String message, boolean editable, String... items) {
	JPanel panel = new JPanel();
	JComboBox<String> editor = new JComboBox<>(items);
	JLabel label = new JLabel(message);
	label.setHorizontalAlignment(JLabel.LEFT);
	JComboBox<String> result = null;
	editor.setEditable(editable);
	panel.setLayout(new FlowLayout(FlowLayout.LEADING));
	panel.add(label);
	panel.add(editor);

	CustomOptionPane pane = new CustomOptionPane(panel,
		JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	JDialog dialog = pane.createDialog(parentComponent, title);
	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	dialog.setVisible(true);
	if (new Integer(JOptionPane.OK_OPTION).equals(pane.getValue())) {
	    result = editor;
	}
	return result;
    }
}
