package jp.takamichie.desktop.logcatviewer.table.filter;

import javax.swing.RowFilter;

import jp.takamichie.desktop.logcatviewer.classes.LogLine;
import jp.takamichie.desktop.logcatviewer.table.LogTableModel;

/**
 * PIDのフィルタクラス
 */
public class PIDFilter extends RowFilter<LogTableModel, Integer> {

    /**
     * PIDが未指定であることを示す値です
     */
    public static final int NOSELECT = -1;

    private int mPID;

    public PIDFilter() {
	mPID = NOSELECT;
    }

    public void setPID(int pid) {
        this.mPID = pid;
    }

    @Override
    public boolean include(
    	javax.swing.RowFilter.Entry<? extends LogTableModel, ? extends Integer> entry) {
        LogTableModel model = entry.getModel();
        LogLine item = model.getItem(entry.getIdentifier());
        return mPID == NOSELECT || item.getPID() == mPID;
    }

}