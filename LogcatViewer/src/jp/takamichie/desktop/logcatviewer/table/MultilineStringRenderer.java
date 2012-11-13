package jp.takamichie.desktop.logcatviewer.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * 複数行表示に対応したレンダラー
 *
 * @author 知英
 *
 */
public class MultilineStringRenderer extends JTextArea implements
	TableCellRenderer {
    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	setForeground(adaptee.getForeground());
	setBackground(adaptee.getBackground());
	setBorder(adaptee.getBorder());
	setFont(adaptee.getFont());
	setText((value == null) ? "" : value.toString());

	return this;
    }
}
