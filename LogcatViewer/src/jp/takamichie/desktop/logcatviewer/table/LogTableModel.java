package jp.takamichie.desktop.logcatviewer.table;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.takamichie.desktop.logcatviewer.classes.LogLine;

/**
 * ログリストのテーブルモデル
 *
 * @author 知英
 */
public class LogTableModel extends DefaultTableModel {

    private ArrayList<LogLine> mLogLines;
    private LogLine mLastLogItem;
    private JTable mOwner;

    public LogTableModel(Object[] columnHeaders, int i) {
	super(columnHeaders, i);
	mLogLines = new ArrayList<>();
    }

    public void setOwner(JTable owner) {
	mOwner = owner;
    }

    public void addRow(LogLine logLine) {
	if (mLastLogItem != null && mLastLogItem.same(logLine)) {
	    mLastLogItem.marge(logLine);
	    int rc = getRowCount() - 1;
	    super.setValueAt(mLastLogItem.getBody(), rc, 3);
	    mOwner.setRowHeight(rc,
		    mOwner.getRowHeight() * mLastLogItem.getLineCount());
	} else {
	    mLogLines.add(logLine);
	    mLastLogItem = logLine;
	    super.addRow(new Object[] { logLine.getLevel(),
		    logLine.getTimeStamp(), logLine.getTags(),
		    logLine.getBody() });
	}
    }

    @Override
    public boolean isCellEditable(int row, int column) {
	return false;
    }

    public LogLine getItem(Integer identifier) {
	return mLogLines.get(identifier);
    }
}