package jp.takamichie.desktop.logcatviewer.table.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * 常に文字を上詰め表示するレンダラー
 *
 * @author 知英
 *
 */
public class VerticalTopRenderer extends JLabel implements TableCellRenderer {
    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	setForeground(adaptee.getForeground());
	setBackground(adaptee.getBackground());
	setOpaque(true);
	setBorder(adaptee.getBorder());
	setFont(adaptee.getFont());
	setText((value == null) ? "" : value.toString());
	setVerticalAlignment(JLabel.TOP);

	return this;
    }

}
