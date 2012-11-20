package jp.takamichie.desktop.logcatviewer.table;

import java.util.ArrayList;

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
    private LogItemListener mListener;

    public interface LogItemListener {
	void logItemRecieved(LogLine oldItem, LogLine newItem);
    }

    public LogTableModel(LogItemListener listener, Object[] columnHeaders, int i) {
	super(columnHeaders, i);
	this.mListener = listener;
	this.mLogLines = new ArrayList<>();
    }

    public void addRow(LogLine logLine) {
	if (mLastLogItem != null && mLastLogItem.same(logLine)) {
	    mLastLogItem.marge(logLine);
	    int rc = getRowCount() - 1;
	    super.setValueAt(mLastLogItem.getBody(), rc, 3);
	} else {
	    mLogLines.add(logLine);
	    if (mListener != null) {
		mListener.logItemRecieved(mLastLogItem, logLine);
	    }
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
	return identifier == -1 ? null : mLogLines.get(identifier);
    }
}