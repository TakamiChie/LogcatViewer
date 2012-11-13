package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import jp.takamichie.desktop.logcatviewer.classes.Device;
import jp.takamichie.desktop.logcatviewer.classes.LogLine;

public class LogPanel extends javax.swing.JPanel implements Runnable {
    private static final Object[] COLUMN_HEADERS = new Object[] { "", "時間",
	    "タグ", "" };
    public static final char LOGLEVEL_VERBOSE = 'V';
    public static final char LOGLEVEL_DEBUG = 'D';
    public static final char LOGLEVEL_INFO = 'I';
    public static final char LOGLEVEL_WARN = 'W';
    public static final char LOGLEVEL_ERROR = 'E';
    private JTable mListLog;
    private Thread mLogcatThread;
    private Process mProccess;
    private LogTableModel mTableModel;
    private LogLevelFilter mLogLevelFilter;
    private TableRowSorter<LogTableModel> mLogSorter;
    private Set<RowFilter<LogTableModel, Integer>> mFilters;

    public LogPanel() {
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
	mTableModel = new LogTableModel(COLUMN_HEADERS, 0);
	JScrollPane scrollPane = new JScrollPane();
	scrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	add(scrollPane, BorderLayout.CENTER);

	mListLog = new LogTable(mTableModel);

	mListLog.setShowGrid(false);
	mListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	mListLog.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

	TableColumnModel columnModel = mListLog.getColumnModel();
	columnModel.getColumn(0).setMaxWidth(10);

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
    private void addlog(LogLine logLine) {
	mTableModel.addRow(logLine);
	mListLog.scrollRectToVisible(mListLog.getCellRect(
		mTableModel.getRowCount(), 0, true));
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

    /**
     * ログ表示機能を付与した{@link JTable}
     */
    class LogTable extends JTable {

	public LogTable(DefaultTableModel tableModel) {
	    super(tableModel);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
		int column) {
	    Component c = super.prepareRenderer(renderer, row, column);
	    Color fg;
	    // ログレベルに応じて文字の色を変更
	    switch ((char) mTableModel.getValueAt(row, 0)) {
	    case 'E':
		fg = Color.RED;
		break;
	    case 'W':
		fg = new Color(255, 128, 0);
		break;
	    case 'I':
		fg = new Color(0, 128, 0);
		break;
	    case 'D':
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

    /**
     * ログリストのテーブルモデル
     */
    class LogTableModel extends DefaultTableModel {

	private ArrayList<LogLine> mLogLines;

	public LogTableModel(Object[] columnHeaders, int i) {
	    super(columnHeaders, i);
	    mLogLines = new ArrayList<>();
	}

	public void addRow(LogLine logLine) {
	    mLogLines.add(logLine);
	    mTableModel.addRow(new Object[] { logLine.getLevel(),
		    logLine.getTimeStanp(), logLine.getTags(),
		    logLine.getBody() });
	}

	@Override
	public boolean isCellEditable(int row, int column) {
	    return false;
	}

	public LogLine getItem(Integer identifier) {
	    return mLogLines.get(identifier);
	}
    }

    /**
     * 複数行表示に対応したレンダラー
     */
    class MultilineStringRenderer extends JTextArea implements
	    TableCellRenderer {

	public MultilineStringRenderer() {
	    setLineWrap(true);
	    setWrapStyleWord(true);
	    setMargin(new Insets(0, 0, 0, 0));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table,
		Object value, boolean isSelected, boolean hasFocus, int row,
		int column) {
	    if (isSelected) {
		setForeground(table.getSelectionForeground());
		setBackground(table.getSelectionBackground());
	    } else {
		setForeground(table.getForeground());
		setBackground(table.getBackground());
	    }
	    setText((value == null) ? "" : value.toString());

	    return this;
	}
    }

    /**
     * ログレベルでのフィルタクラス
     */
    class LogLevelFilter extends RowFilter<LogTableModel, Integer> {

	private int mLogLevel;

	public LogLevelFilter() {
	    this.mLogLevel = logLevelToInt(LOGLEVEL_VERBOSE);
	}

	private int logLevelToInt(char logLevel) {
	    int level;
	    switch (logLevel) {
	    case LOGLEVEL_VERBOSE:
		level = 5;
		break;
	    case LOGLEVEL_INFO:
		level = 4;
		break;
	    case LOGLEVEL_DEBUG:
		level = 3;
		break;
	    case LOGLEVEL_WARN:
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
}
