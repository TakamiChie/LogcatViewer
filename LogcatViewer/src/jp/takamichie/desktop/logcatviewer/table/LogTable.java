package jp.takamichie.desktop.logcatviewer.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import jp.takamichie.desktop.logcatviewer.LogPanel;

/**
 * ログ表示機能を付与した{@link JTable}
 *
 * @author 知英
 */
public class LogTable extends JTable {

    public LogTable(DefaultTableModel tableModel) {
	super(tableModel);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row,
	    int column) {
	Component c = super.prepareRenderer(renderer, row, column);
	Color fg;
	// ログレベルに応じて文字の色を変更
	switch ((char) getValueAt(row, 0)) {
	case LogPanel.LOGLEVEL_ERROR:
	    fg = Color.RED;
	    break;
	case LogPanel.LOGLEVEL_WARN:
	    fg = new Color(255, 128, 0);
	    break;
	case LogPanel.LOGLEVEL_INFO:
	    fg = new Color(0, 128, 0);
	    break;
	case LogPanel.LOGLEVEL_DEBUG:
	    fg = Color.BLUE;
	    break;
	default:
	    fg = Color.BLACK;
	    break;
	}
	c.setBackground(isRowSelected(row) ? getSelectionBackground()
		: getBackground());
	c.setForeground(fg);
	return c;
    }
}