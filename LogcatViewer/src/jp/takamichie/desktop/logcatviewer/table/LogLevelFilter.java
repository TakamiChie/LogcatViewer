package jp.takamichie.desktop.logcatviewer.table;

import javax.swing.RowFilter;

import jp.takamichie.desktop.logcatviewer.LogPanel;
import jp.takamichie.desktop.logcatviewer.classes.LogLine;

/**
 * ログレベルでのフィルタクラス
 */
public class LogLevelFilter extends RowFilter<LogTableModel, Integer> {

    private int mLogLevel;

    public LogLevelFilter() {
        this.mLogLevel = logLevelToInt(LogPanel.LOGLEVEL_VERBOSE);
    }

    private int logLevelToInt(char logLevel) {
        int level;
        switch (logLevel) {
        case LogPanel.LOGLEVEL_VERBOSE:
    	level = 5;
    	break;
        case LogPanel.LOGLEVEL_INFO:
    	level = 4;
    	break;
        case LogPanel.LOGLEVEL_DEBUG:
    	level = 3;
    	break;
        case LogPanel.LOGLEVEL_WARN:
    	level = 2;
    	break;
        default:
    	level = 1;
    	break;
        }
        return level;
    }

    public void setLogLevel(char level) {
        this.mLogLevel = logLevelToInt(level);
    }

    @Override
    public boolean include(
    	javax.swing.RowFilter.Entry<? extends LogTableModel, ? extends Integer> entry) {
        LogTableModel model = entry.getModel();
        LogLine item = model.getItem(entry.getIdentifier());
        return logLevelToInt(item.getLevel()) <= mLogLevel;
    }

}