package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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
import jp.takamichie.desktop.logcatviewer.classes.ProcessInfo;
import jp.takamichie.desktop.logcatviewer.table.LogTable;
import jp.takamichie.desktop.logcatviewer.table.LogTableModel;
import jp.takamichie.desktop.logcatviewer.table.LogTableModel.LogItemListener;
import jp.takamichie.desktop.logcatviewer.table.filter.LogLevelFilter;
import jp.takamichie.desktop.logcatviewer.table.filter.PIDFilter;
import jp.takamichie.desktop.logcatviewer.table.filter.TagFilter;
import jp.takamichie.desktop.logcatviewer.table.renderer.MultilineStringRenderer;
import jp.takamichie.desktop.logcatviewer.table.renderer.VerticalTopRenderer;

public class LogPanel extends javax.swing.JPanel implements Runnable,
	ActionListener, LogItemListener {
    private static final Object[] COLUMN_HEADERS = new Object[] { "", "時間",
	    "タグ", "" };
    public static final char LOGLEVEL_VERBOSE = 'V';
    public static final char LOGLEVEL_DEBUG = 'D';
    public static final char LOGLEVEL_INFO = 'I';
    public static final char LOGLEVEL_WARN = 'W';
    public static final char LOGLEVEL_ERROR = 'E';
    private static final long AUTOUPDATE_TIMER = 5000;
    private static final String PROPKEY_COLUMN_SIZE = "columnsize";
    @SuppressWarnings("unused")
    private Main mOwner;
    private JTable mListLog;
    private Thread mLogcatThread;
    private Process mProccess;
    private LogTableModel mTableModel;
    private LogLevelFilter mLogLevelFilter;
    private TagFilter mTagFilter;
    private PIDFilter mPIDFilter;
    private TableRowSorter<LogTableModel> mLogSorter;
    private Set<RowFilter<LogTableModel, Integer>> mFilters;
    private boolean mChaseItem;
    private HashMap<Integer, ProcessInfo> mProcessList;
    private JPopupMenu mLogListPopupMenu;
    private JMenuItem mMenuItemFilterdThisProcess;
    private JMenuItem mMenuItemFilterdThisTag;
    private JMenuItem mMenuItemCopyToClipboard;
    private JMenuItem mMenuItemCopyToClipboardBodyOnly;
    private JMenuItem mMenuItemWatchProcess;
    @SuppressWarnings("unused")
    private int mMonitoringPID;
    private ScheduledExecutorService mScheduler;
    private JMenuItem mMenuItemShowDetails;
    private Device mDevice;

    public LogPanel(Main main) {
	mOwner = main;
	initializeComponent();
	mLogSorter = new TableRowSorter<LogTableModel>(mTableModel);
	mLogLevelFilter = new LogLevelFilter();
	mTagFilter = new TagFilter();
	mPIDFilter = new PIDFilter();
	mFilters = new HashSet<>();
	mFilters.add(mLogLevelFilter);
	mFilters.add(mPIDFilter);
	mFilters.add(mTagFilter);
	mMonitoringPID = PIDFilter.NOSELECT;

	mLogSorter.setRowFilter(RowFilter.andFilter(mFilters));
	mListLog.setRowSorter(mLogSorter);
	addPopup(mListLog, mLogListPopupMenu);

	// 5秒ごとに実行
	mScheduler = Executors.newSingleThreadScheduledExecutor();
	mScheduler.scheduleWithFixedDelay(new Runnable() {
	    public void run() {
		try {
		    updateProcessList();
		} catch (IOException e) {
		    // do nothing
		}
	    }
	}, 0, AUTOUPDATE_TIMER, TimeUnit.MILLISECONDS);
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

	mTableModel = new LogTableModel(this, COLUMN_HEADERS, 0);
	mListLog = new LogTable(mTableModel);

	mListLog.setShowGrid(false);
	mListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	mListLog.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

	mLogListPopupMenu = new JPopupMenu();

	mMenuItemFilterdThisTag = new JMenuItem("このタグでフィルタリング(T)");
	mMenuItemFilterdThisTag.setActionCommand(Main.COMMAND_FILTER_THISTAG);
	mMenuItemFilterdThisTag.setMnemonic('T');
	mMenuItemFilterdThisTag.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemFilterdThisTag);

	mMenuItemFilterdThisProcess = new JMenuItem("このプロセスでフィルタリング(P)");
	mMenuItemFilterdThisProcess
		.setActionCommand(Main.COMMAND_FILTER_THISPROC);
	mMenuItemFilterdThisProcess.setMnemonic('P');
	mMenuItemFilterdThisProcess.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemFilterdThisProcess);

	mMenuItemWatchProcess = new JMenuItem("このプロセスを監視(W)");
	mMenuItemWatchProcess.setActionCommand(Main.COMMAND_FILTER_WATCHPROC);
	mMenuItemWatchProcess.setMnemonic('W');
	mMenuItemWatchProcess.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemWatchProcess);

	mMenuItemCopyToClipboard = new JMenuItem("ログをクリップボードにコピー(C)");
	mMenuItemCopyToClipboard.setActionCommand(Main.COMMAND_FILTER_COPY);
	mMenuItemCopyToClipboard.setMnemonic('C');
	mMenuItemCopyToClipboard.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemCopyToClipboard);

	mMenuItemCopyToClipboardBodyOnly = new JMenuItem("ログの本文をクリップボードのコピー(P)");
	mMenuItemCopyToClipboardBodyOnly
		.setActionCommand(Main.COMMAND_FILTER_COPYBODY);
	mMenuItemCopyToClipboardBodyOnly.setMnemonic('P');
	mMenuItemCopyToClipboardBodyOnly.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemCopyToClipboardBodyOnly);

	mMenuItemShowDetails = new JMenuItem("ログの詳細(D)");
	mMenuItemShowDetails.setActionCommand(Main.COMMAND_LOG_DETAILS);
	mMenuItemShowDetails.setMnemonic('L');
	mMenuItemShowDetails.addActionListener(this);
	mLogListPopupMenu.add(mMenuItemShowDetails);

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
     * 外部に保存したプロパティをロードします
     * @param prop プロパティを保存するオブジェクト
     */
    public void loadProperties(Properties prop) {
	String[] columns = prop.getProperty(PROPKEY_COLUMN_SIZE, "").split(",");
	if (columns.length == 3 && Integer.parseInt(columns[0]) >= 0
		&& Integer.parseInt(columns[1]) >= 0
		&& Integer.parseInt(columns[2]) >= 0) {
	    TableColumnModel column = mListLog.getColumnModel();
	    column.getColumn(1).setPreferredWidth(Integer.parseInt(columns[0]));
	    column.getColumn(2).setPreferredWidth(Integer.parseInt(columns[1]));
	    column.getColumn(3).setPreferredWidth(Integer.parseInt(columns[2]));
	} else {
	    this.setSize(400, 300);
	}
    }

    /**
     * 外部にプロパティをセーブします
     * @param prop プロパティを保存するオブジェクト
     */
    public void saveProperties(Properties prop) {
	TableColumnModel column = mListLog.getColumnModel();
	prop.setProperty(PROPKEY_COLUMN_SIZE, String.format("%d,%d,%d", column
		.getColumn(1).getWidth(), column.getColumn(2).getWidth(),
		column.getColumn(3).getWidth()));
    }

    public void updateProcessList() throws IOException {
	final Process proccess = new ProcessBuilder("adb", "-s",
		mDevice.getDeviceID(), "shell", "ps").redirectErrorStream(true)
		.start();
	Thread t = new Thread(new Runnable() {

	    @Override
	    public void run() {
		HashMap<Integer, ProcessInfo> tempList = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(proccess.getInputStream()))) {
		    String line;
		    Pattern pattern = Pattern.compile(ProcessInfo.PS_REGEX,
			    Pattern.DOTALL);
		    while ((line = reader.readLine()) != null) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
			    ProcessInfo info = new ProcessInfo(m);
			    tempList.put(info.getPID(), info);
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		mProcessList = tempList;

	    }
	});
	t.start();
    }

    // / 外部向けgetter & setter

    public HashMap<Integer, ProcessInfo> getProcessList() {
	return mProcessList;
    }

    /**
     * ロギング対象となるデバイスを指定します。
     *
     * @param device
     *            ロギングを行うデバイスオブジェクト
     */
    public void setDevice(Device device) {
	((LogTableModel)mListLog.getModel()).clearItems();
	if (mProccess != null) {
	    mProccess.destroy();
	}
	this.mDevice = device;
	if (device != null) {
	    try {
		updateProcessList();
		mProccess = new ProcessBuilder("adb", "-s",
			device.getDeviceID(), "logcat", "-v", "time")
			.redirectErrorStream(true).start();
		mLogcatThread = new Thread(this);
		mLogcatThread.start();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * リストをフィルタリングするPIDを指定します。
     *
     * @param pid
     *            PID。
     */
    public void setFilteredPID(int pid) {
	mPIDFilter.setPID(pid);
	selectLastItemIsSetting();
	mLogSorter.allRowsChanged();
    }

    /**
     * リストをフィルタリングするタグを指定します。
     *
     * @param タグ文字列
     *            タグ文字列。
     */
    public void setFilteredTag(String tag) {
	mTagFilter.setTag(tag);
	selectLastItemIsSetting();
	mLogSorter.allRowsChanged();
    }

    /**
     * リストをフィルタリングするログレベルを指定します。
     *
     * @param loglevel
     *            ログレベル
     */
    public void setLogLevel(char loglevel) {
	mLogLevelFilter.setLogLevel(loglevel);
	selectLastItemIsSetting();
	mLogSorter.allRowsChanged();
    }

    // ユーティリティメソッド類

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
		selectLastItemIsSetting();
	    }
	});
    }

    /**
     * 「ログを追跡」オプションが有効の時に、最後のアイテムを選択します。
     */
    public void selectLastItemIsSetting() {
	if (mChaseItem) {
	    selectLastItem();
	}
    }

    /**
     * 最後のアイテムを選択します
     */
    private void selectLastItem() {
	int rc = mTableModel.getRowCount() - 1;
	mListLog.scrollRectToVisible(mListLog.getCellRect(rc, 0, true));
	mListLog.changeSelection(rc, 0, true, false);
    }

    /**
     * クリップボードに文字列をコピーします
     *
     * @param string
     *            コピーする文字列
     */
    private void copyToClipboard(String string) {
	Toolkit kit = Toolkit.getDefaultToolkit();
	Clipboard clip = kit.getSystemClipboard();
	clip.setContents(new StringSelection(string), null);
    }

    // / getter & setter

    /**
     * 最後のアイテムを常に追尾するかどうかを設定します
     *
     * @param state
     *            最後のアイテムを常に追尾するかどうか
     */
    public void setChaseItem(boolean state) {
	this.mChaseItem = state;
	if (state) {
	    selectLastItem();
	}
    }

    /**
     * 最後のアイテムを常に追尾するかどうかを取得します
     *
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
	    Pattern pattern = Pattern.compile(
		    LogLine.LOGCAT_REGEX_MSEC_IGNORED, Pattern.DOTALL);
	    while ((line = reader.readLine()) != null) {
		Matcher m = pattern.matcher(line);
		if (m.matches()) {
		    addlog(new LogLine(m, getProcessList()));
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void addPopup(Component component, final JPopupMenu popup) {
	component.addMouseListener(new MouseAdapter() {
	    public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
		    JTable source = (JTable) e.getSource();
		    int row = source.rowAtPoint(e.getPoint());
		    int column = source.columnAtPoint(e.getPoint());

		    if (!source.isRowSelected(row)) {
			source.changeSelection(row, column, false, false);
		    }
		    showMenu(e);
		}
	    }

	    private void showMenu(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	LogLine selected = ((LogTableModel) mListLog.getModel())
		.getItem(mListLog.getSelectedRow());
	if (selected != null) {
	    switch (e.getActionCommand()) {
	    case Main.COMMAND_FILTER_THISTAG:
		setFilteredTag(selected.getTags());
		break;
	    case Main.COMMAND_FILTER_THISPROC:
		setFilteredPID(selected.getPID());
		break;
	    case Main.COMMAND_FILTER_WATCHPROC:
		mMonitoringPID = selected.getPID();
		break;
	    case Main.COMMAND_FILTER_COPY:
		copyToClipboard(selected.toString());
		break;
	    case Main.COMMAND_FILTER_COPYBODY:
		copyToClipboard(selected.getBody());
		break;
	    case Main.COMMAND_LOG_DETAILS:
		LogDetailDialog dialog = new LogDetailDialog(selected);
		JOptionPane.showMessageDialog(this, dialog, "ログの詳細",
			JOptionPane.PLAIN_MESSAGE);
	    default:
		break;
	    }
	}
    }

    @Override
    public void logItemRecieved(LogLine oldItem, LogLine newItem) {
    }
}
