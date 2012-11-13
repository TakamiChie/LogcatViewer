package jp.takamichie.desktop.logcatviewer.table.filter;

import javax.swing.RowFilter;

import jp.takamichie.desktop.logcatviewer.classes.LogLine;
import jp.takamichie.desktop.logcatviewer.table.LogTableModel;

/**
 * タグのフィルタクラス
 */
public class TagFilter extends RowFilter<LogTableModel, Integer> {

    private String mTag;

    public void setTag(String tag) {
        this.mTag = tag;
    }

    @Override
    public boolean include(
    	javax.swing.RowFilter.Entry<? extends LogTableModel, ? extends Integer> entry) {
        LogTableModel model = entry.getModel();
        LogLine item = model.getItem(entry.getIdentifier());
        return mTag == null || item.getTags().equals(mTag);
    }

}