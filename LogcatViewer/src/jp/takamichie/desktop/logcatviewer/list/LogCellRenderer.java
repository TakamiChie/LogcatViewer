package jp.takamichie.desktop.logcatviewer.list;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import jp.takamichie.desktop.logcatviewer.LogPanel;
import jp.takamichie.desktop.logcatviewer.classes.LogLine;

public class LogCellRenderer extends JTextArea implements
	ListCellRenderer<LogLine> {

    public LogCellRenderer() {
	super();
//	setLineWrap(true);
	setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public Component getListCellRendererComponent(
	    JList<? extends LogLine> list, LogLine value, int index,
	    boolean isSelected, boolean cellHasFocus) {
	Color fg;
	// ログレベルに応じて文字の色を変更
	switch ((char) value.getLevel()) {
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
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	} else {
	    setBackground(list.getBackground());
	}
	setText(value.toString());
	setForeground(fg);
	return this;
    }

}
