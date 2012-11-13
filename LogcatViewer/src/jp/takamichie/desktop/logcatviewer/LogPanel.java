package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import jp.takamichie.desktop.logcatviewer.classes.Device;
import jp.takamichie.desktop.logcatviewer.classes.LogLine;
import jp.takamichie.desktop.logcatviewer.table.LogLevelFilter;
import jp.takamichie.desktop.logcatviewer.table.LogTable;
import jp.takamichie.desktop.logcatviewer.table.LogTableModel;
import jp.takamichie.desktop.logcatviewer.table.MultilineStringRenderer;
import jp.takamichie.desktop.logcatviewer.table.VerticalTopRenderer;

public class LogPanel extends javax.swing.JPanel implements Runnable {
    private static final Object[] COLUMN_HEADERS = new Object[] { "", "時間",
	    "タグ", "" };
    public static final char LOGLEVEL_VERBOSE = 'V';
    public static final char LOGLEVEL_DEBUG = 'D';
    public static final char LOGLEVEL_INFO = 'I';
    public static final char LOGLEVEL_WARN = 'W';
    public static final char LOGLEVEL_ERROR = 'E';
    private Main mOwner;
    private JTable mListLog;
    private Thread mLogcatThread;
    private Process mProccess;
    private LogTableModel mTableModel;
    private LogLevelFilter mLogLevelFilter;
    private TableRowSorter<LogTableModel> mLogSorter;
    private Set<RowFilter<LogTableModel, Integer>> mFilters;
    private boolean mChaseItem;

    public LogPanel(Main main) {
	mOwner = main;
	initializeComponent();
	mLogSorter = new TableRowSorter<LogTableModel>(mTableModel);
	mLogLevelFilter = new LogLevelFilter();
	mFilters = new HashSet<>();
	mFilters.add(mLogLevelFilter);

	mLogSorter.setRowFilter(RowFilter.andFilter(mFilters));
	mListLog.setRowSorter(mLogSorter);
    }

    @Override
    protected void finalize() throws Throwable {
	if (mProccess != null) {
	    mProccess.destroy();
	}
	super.finalize();
    }

    private void initializeComponent() {
	setLayout(new BorderLayout(0, 0));
	JScrollPane scrollPane = new JScrollPane();
	scrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	add(scrollPane, BorderLayout.CENTER);

	mTableModel = new LogTableModel(COLUMN_HEADERS, 0);
	mListLog = new LogTable(mTableModel);

	mListLog.setShowGrid(false);
	mListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	mListLog.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	mTableModel.setOwner(mListLog);

	TableColumnModel columnModel = mListLog.getColumnModel();
	columnModel.getColumn(0).setMaxWidth(10);
	columnModel.getColumn(0).setCellRenderer(new VerticalTopRenderer());
	columnModel.getColumn(1).setCellRenderer(new VerticalTopRenderer());
	columnModel.getColumn(2).setCellRenderer(new VerticalTopRenderer());
	columnModel.getColumn(3).setCellRenderer(new MultilineStringRenderer());

	scrollPane.setColumnHeaderView(mListLog.getTableHeader());
	scrollPane.setViewportView(mListLog);

    }

    /**
     * ロギング対象となるデバイスを指定します。
     *
     * @param device
     *            ロギングを行うデバイスオブジェクト
     * @throws IOException
     *             プロセスのロギングで入出力エラーが発生した
     */
    public void setDevice(Device device) throws IOException {
	if (mProccess != null) {
	    mProccess.destroy();
	}
	mProccess = new ProcessBuilder("adb", "logcat", "-v", "time")
		.redirectErrorStream(true).start();
	mLogcatThread = new Thread(this);
	mLogcatThread.start();
    }

    /**
     * リストをフィルタリングするPIDを指定します。
     *
     * @param pid
     *            PID。
     */
    public void setFilteredPID(int pid) {

    }

    /**
     * リストをフィルタリングするログレベルを指定します。
     *
     * @param loglevel
     *            ログレベル
     */
    public void setLogLevel(char loglevel) {
	mLogLevelFilter.setLogLevel(loglevel);
	mLogSorter.allRowsChanged();
    }

    /**
     * 行を追加します。
     *
     * @param logLine
     *            追加する行を示す{@link LogLine}オブジェクト
     */
    private void addlog(final LogLine logLine) {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		mTableModel.addRow(logLine);
		if(mChaseItem){
		    selectLastItem();
		}
	    }
	});
    }

    /**
     * 最後のアイテムを選択します
     */
    private void selectLastItem() {
	int rc = mTableModel.getRowCount();
	mListLog.scrollRectToVisible(mListLog.getCellRect(
		rc, 0, true));
	mListLog.changeSelection(rc, 0, true, false);
    }

    /// getter & setter
    /**
     * 最後のアイテムを常に追尾するかどうかを設定します
     * @param state 最後のアイテムを常に追尾するかどうか
     */
    public void setChaseItem(boolean state){
	this.mChaseItem = state;
	if(state){
	    selectLastItem();
	}
    }

    /**
     * 最後のアイテムを常に追尾するかどうかを取得します
     * @return 最後のアイテムを常に追尾するかどうかを示す値
     */
    public boolean isChaseItem() {
	return mChaseItem;
    }

    @Override
    public void run() {
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(
		mProccess.getInputStream()))) {
	    String line;
	    Pattern pattern = Pattern.compile(LogLine.LOGCAT_REGEX,
		    Pattern.DOTALL);
	    while ((line = reader.readLine()) != null) {
		Matcher m = pattern.matcher(line);
		if (m.matches()) {
		    addlog(new LogLine(m));
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
