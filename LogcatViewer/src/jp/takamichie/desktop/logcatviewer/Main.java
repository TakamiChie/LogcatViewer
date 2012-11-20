package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jp.takamichie.desktop.logcatviewer.classes.CustomOptionPane;
import jp.takamichie.desktop.logcatviewer.classes.Device;
import jp.takamichie.desktop.logcatviewer.classes.ProcessInfo;
import jp.takamichie.desktop.logcatviewer.table.filter.PIDFilter;

public class Main extends JFrame implements ActionListener, WindowListener {
    private static final String COMMAND_LOGLEVEL_VERBOSE = "logverbose";
    private static final String COMMAND_LOGLEVEL_DEBUG = "logdebug";
    private static final String COMMAND_LOGLEVEL_INFO = "loginfo";
    private static final String COMMAND_LOGLEVEL_WARN = "logwarn";
    private static final String COMMAND_LOGLEVEL_ERROR = "logerror";
    private static final String COMMAND_FILTER_PROCESS = "procfilter";
    private static final String COMMAND_FILTER_CHASEITEM = "chaseitem";
    private static final String COMMAND_FILTER_TAGS = "tagfilter";
    public static final String COMMAND_FILTER_THISTAG = "thistagfilter";
    public static final String COMMAND_FILTER_THISPROC = "thisprocfilter";
    public static final String COMMAND_FILTER_WATCHPROC = "watchproc";
    public static final String COMMAND_FILTER_COPY = "copy";
    public static final String COMMAND_FILTER_COPYBODY = "copybody";
    private static final String COMMAND_FILTER_ERASE = "erasefilter";
    private static final String COMMAND_DEVICE = "device";
    public static final String COMMAND_LOG_DETAILS = "logdetails";
    private static final String PROPFILE_PATH = "LogcatViewer.properties";
    private static final String PROPKEY_WINDOW_BOUNDS = "windowBounds";
    private static final String PROPKEY_RECENT_TAGS = "recentTags";
    private static final String PROPKEY_CHASELOG = "chaseLog";
    private static final String PROPVALUE_YES = "yes";
    private static final String PROPVALUE_NO = "no";
    private static final long AUTOUPDATE_TIMER = 5000;
    private LogPanel mLogPanel;
    private JMenu mMenuFilters;
    private JMenu mMenuLogLevels;
    private JRadioButtonMenuItem mMenuItemLogLevelInfo;
    private JMenuItem mMenuItemLogLevelDebug;
    private JRadioButtonMenuItem mMenuItemLogLevelVerbose;
    private JRadioButtonMenuItem mMenuItemLogLevelWarn;
    private JRadioButtonMenuItem mMenuItemLogLevelError;
    private final ButtonGroup mButtonGroupLogLevels = new ButtonGroup();
    private JMenuBar mMenuBar;
    private JMenuItem mMenuItemFilterdProcess;
    private JCheckBoxMenuItem mMenuItemChaseItem;
    private JMenuItem mMenuItemFilterdTags;
    private JMenu mMenuFilterSelection;
    private JMenuItem mMenuItemFilterdThisTag;
    private JMenuItem mMenuItemWatchProcess;
    private JMenuItem mMenuItemCopyToClipboard;
    private JMenuItem mMenuItemCopyToClipboardBodyOnly;
    private JMenuItem mMenuItemFilterdThisProcess;
    private JMenuItem mMenuItemEraseFilter;
    private ArrayList<String> mRecentTagList;
    private JMenuItem mMenuItemShowDetails;
    private ScheduledExecutorService mScheduler;
    private boolean mIsAutoUpdate;
    private ArrayList<Device> mDeviceList;
    private JMenu mMenuDevices;

    /**
     * エントリポイント
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Main window = new Main();
		    window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    window.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public Main() {
	initializeComponent();
	mIsAutoUpdate = true;
	mLogPanel.setDevice(null);

	mRecentTagList = new ArrayList<String>();
	addWindowListener(this);
	loadProperties();

	// 5秒ごとに実行
	mScheduler = Executors.newSingleThreadScheduledExecutor();
	mScheduler.scheduleWithFixedDelay(new Runnable() {

	    public void run() {
		if (mIsAutoUpdate) {
		    updateDeviceList();
		}
	    }

	}, 0, AUTOUPDATE_TIMER, TimeUnit.MILLISECONDS);
    }

    /**
     * デバイスのリストを更新し、変更があればメニュー項目を再構築します
     */
    private void updateDeviceList() {
	try {
	    final Process process = new ProcessBuilder("adb", "devices")
		    .redirectErrorStream(true).start();
	    Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
		    ArrayList<Device> tempList = new ArrayList<>();
		    try (BufferedReader reader = new BufferedReader(
			    new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
			    String[] dataline = line.split("\t");
			    if (dataline.length > 1
				    && dataline[1].equals("device")) {
				tempList.add(new Device(dataline[0]));
			    }
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		    // リスト変更チェック
		    boolean changes = false;
		    if (mDeviceList == null) {
			changes = true;
		    } else if (mDeviceList.size() != tempList.size()) {
			changes = true;
		    } else {
			for (int i = 0; i < mDeviceList.size(); i++) {
			    if (mDeviceList.get(i).equals(tempList.get(i))) {
				changes = true;
				break;
			    }
			}
		    }

		    // メニュー項目更新
		    if (changes) {
			mDeviceList = tempList;

			SwingUtilities.invokeLater(new Runnable() {

			    @Override
			    public void run() {
				mMenuDevices.removeAll();
				int i = 0;
				ButtonGroup group = new ButtonGroup();
				for (Device d : mDeviceList) {
				    JRadioButtonMenuItem item = new JRadioButtonMenuItem(
					    ++i + ":" + d.toString());
				    item.addActionListener(Main.this);
				    item.setActionCommand(COMMAND_DEVICE);
				    group.add(item);
				    mMenuDevices.add(item);
				}
				if (mDeviceList.size() >= 1) {
				    mMenuDevices.getItem(0).doClick();
				}
			    }
			});
		    }
		}
	    });
	    t.start();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    /**
     * 外部に保存したプロパティをロードします
     */
    private void loadProperties() {
	Properties prop = new Properties();
	try (InputStream stream = new FileInputStream(PROPFILE_PATH)) {
	    prop.loadFromXML(stream);
	} catch (IOException e) {
	    // ignore this
	}
	String[] bounds = prop.getProperty(PROPKEY_WINDOW_BOUNDS, "")
		.split(",");
	if (bounds.length == 4 && Integer.parseInt(bounds[0]) >= 0
		&& Integer.parseInt(bounds[1]) >= 0
		&& Integer.parseInt(bounds[2]) >= 0
		&& Integer.parseInt(bounds[3]) >= 0) {
	    setBounds(Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1]),
		    Integer.parseInt(bounds[2]), Integer.parseInt(bounds[3]));
	} else {
	    this.setSize(400, 300);
	}
	if (prop.containsKey(PROPKEY_RECENT_TAGS)) {
	    String[] slist = prop.getProperty(PROPKEY_RECENT_TAGS).split(",");
	    for (String s : slist) {
		mRecentTagList.add(s);
	    }
	}
	if (prop.getProperty(PROPKEY_CHASELOG, PROPVALUE_NO).equals(
		PROPVALUE_YES)) {
	    mLogPanel.setChaseItem(true);
	    mMenuItemChaseItem.setSelected(true);
	}
	mLogPanel.loadProperties(prop);
    }

    /**
     * 外部にプロパティをセーブします
     */
    private void saveProperties() {
	Properties prop = new Properties();
	try (InputStream stream = new FileInputStream(PROPFILE_PATH)) {
	    prop.loadFromXML(stream);
	} catch (IOException ex) {
	    // ignore this
	}
	prop.setProperty(PROPKEY_WINDOW_BOUNDS, String.format("%d,%d,%d,%d",
		getX(), getY(), getWidth(), getHeight()));
	if (mRecentTagList.size() > 0) {
	    StringBuilder tags = new StringBuilder();
	    for (String tag : mRecentTagList) {
		tags.append(tag + ",");
	    }
	    prop.setProperty(PROPKEY_RECENT_TAGS,
		    tags.substring(0, tags.length() - 1));
	}
	prop.setProperty(PROPKEY_CHASELOG,
		mMenuItemChaseItem.isSelected() ? PROPVALUE_YES : PROPVALUE_NO);
	mLogPanel.saveProperties(prop);
	try (OutputStream out = new FileOutputStream(PROPFILE_PATH)) {
	    prop.storeToXML(out, null);
	    out.flush();
	} catch (IOException ex) {
	    showStandardErrorDialog(ex);
	    ex.printStackTrace();
	}
    }

    private void initializeComponent() {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException | InstantiationException
		| IllegalAccessException | UnsupportedLookAndFeelException e) {
	    e.printStackTrace();
	}

	setTitle("LogcatViewer");

	mLogPanel = new LogPanel(this);
	getContentPane().add(mLogPanel, BorderLayout.CENTER);

	mMenuBar = new JMenuBar();
	setJMenuBar(mMenuBar);

	mMenuDevices = new JMenu("デバイス(D)");
	mMenuDevices.setMnemonic('D');
	mMenuBar.add(mMenuDevices);

	mMenuFilters = new JMenu("フィルタ(F)");
	mMenuFilters.setMnemonic('F');
	mMenuBar.add(mMenuFilters);

	mMenuLogLevels = new JMenu("ログレベル(L)");
	mMenuLogLevels.setMnemonic('L');
	mMenuFilters.add(mMenuLogLevels);

	mMenuItemLogLevelVerbose = new JRadioButtonMenuItem("VERBOSE");
	mButtonGroupLogLevels.add(mMenuItemLogLevelVerbose);
	mMenuItemLogLevelVerbose.setSelected(true);
	mMenuItemLogLevelVerbose.setMnemonic('V');
	mMenuItemLogLevelVerbose.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_5, 0));
	mMenuItemLogLevelVerbose.setActionCommand(COMMAND_LOGLEVEL_VERBOSE);
	mMenuItemLogLevelVerbose.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelVerbose);

	mMenuItemLogLevelDebug = new JRadioButtonMenuItem("DEBUG");
	mButtonGroupLogLevels.add(mMenuItemLogLevelDebug);
	mMenuItemLogLevelDebug.setMnemonic('D');
	mMenuItemLogLevelDebug.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_4, 0));
	mMenuItemLogLevelDebug.setActionCommand(COMMAND_LOGLEVEL_DEBUG);
	mMenuItemLogLevelDebug.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelDebug);

	mMenuItemLogLevelInfo = new JRadioButtonMenuItem("INFO");
	mButtonGroupLogLevels.add(mMenuItemLogLevelInfo);
	mMenuItemLogLevelInfo.setMnemonic('I');
	mMenuItemLogLevelInfo.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_3, 0));
	mMenuItemLogLevelInfo.setActionCommand(COMMAND_LOGLEVEL_INFO);
	mMenuItemLogLevelInfo.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelInfo);

	mMenuItemLogLevelWarn = new JRadioButtonMenuItem("WARN");
	mButtonGroupLogLevels.add(mMenuItemLogLevelWarn);
	mMenuItemLogLevelWarn.setMnemonic('I');
	mMenuItemLogLevelWarn.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_2, 0));
	mMenuItemLogLevelWarn.setActionCommand(COMMAND_LOGLEVEL_WARN);
	mMenuItemLogLevelWarn.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelWarn);

	mMenuItemLogLevelError = new JRadioButtonMenuItem("ERROR");
	mButtonGroupLogLevels.add(mMenuItemLogLevelError);
	mMenuItemLogLevelError.setMnemonic('E');
	mMenuItemLogLevelError.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_1, 0));
	mMenuItemLogLevelError.setActionCommand(COMMAND_LOGLEVEL_ERROR);
	mMenuItemLogLevelError.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelError);

	mMenuItemFilterdProcess = new JMenuItem("プロセス...");
	mMenuItemFilterdProcess.setMnemonic('P');
	mMenuItemFilterdProcess.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_P, 0));
	mMenuItemFilterdProcess.setActionCommand(COMMAND_FILTER_PROCESS);
	mMenuItemFilterdProcess.addActionListener(this);
	mMenuFilters.add(mMenuItemFilterdProcess);

	mMenuItemFilterdTags = new JMenuItem("タグ...");
	mMenuItemFilterdTags.setMnemonic('T');
	mMenuItemFilterdTags.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_T, 0));
	mMenuItemFilterdTags.setActionCommand(COMMAND_FILTER_TAGS);
	mMenuItemFilterdTags.addActionListener(this);
	mMenuFilters.add(mMenuItemFilterdTags);

	mMenuItemEraseFilter = new JMenuItem("フィルタをすべて解除(E)");
	mMenuItemEraseFilter.setMnemonic('E');
	mMenuItemEraseFilter.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_ESCAPE, 0));
	mMenuItemEraseFilter.setActionCommand(COMMAND_FILTER_ERASE);
	mMenuItemEraseFilter.addActionListener(this);
	mMenuFilters.add(mMenuItemEraseFilter);

	mMenuItemChaseItem = new JCheckBoxMenuItem("ログを追尾(C)");
	mMenuItemChaseItem.setMnemonic('C');
	mMenuItemChaseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		0));
	mMenuItemChaseItem.setActionCommand(COMMAND_FILTER_CHASEITEM);
	mMenuItemChaseItem.addActionListener(this);
	mMenuFilters.add(mMenuItemChaseItem);

	mMenuFilterSelection = new JMenu("選択項目(T)");
	mMenuFilterSelection.setMnemonic('T');
	mMenuBar.add(mMenuFilterSelection);

	mMenuItemFilterdThisTag = new JMenuItem("このタグでフィルタリング(T)");
	mMenuItemFilterdThisTag.setActionCommand(COMMAND_FILTER_THISTAG);
	mMenuItemFilterdThisTag.setMnemonic('T');
	mMenuItemFilterdThisTag.addActionListener(this);
	mMenuItemFilterdThisTag.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_F2, 0));
	mMenuFilterSelection.add(mMenuItemFilterdThisTag);

	mMenuItemFilterdThisProcess = new JMenuItem("このプロセスでフィルタリング(P)");
	mMenuItemFilterdThisProcess.setActionCommand(COMMAND_FILTER_THISPROC);
	mMenuItemFilterdThisProcess.setMnemonic('P');
	mMenuItemFilterdThisProcess.addActionListener(this);
	mMenuItemFilterdThisProcess.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_F3, 0));
	mMenuFilterSelection.add(mMenuItemFilterdThisProcess);

	mMenuItemWatchProcess = new JMenuItem("このプロセスを監視(W)");
	mMenuItemWatchProcess.setActionCommand(COMMAND_FILTER_WATCHPROC);
	mMenuItemWatchProcess.setMnemonic('W');
	mMenuItemWatchProcess.addActionListener(this);
	mMenuItemWatchProcess.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_F4, 0));
	mMenuFilterSelection.add(mMenuItemWatchProcess);

	mMenuItemCopyToClipboard = new JMenuItem("ログをクリップボードにコピー(C)");
	mMenuItemCopyToClipboard.setActionCommand(COMMAND_FILTER_COPY);
	mMenuItemCopyToClipboard.setMnemonic('C');
	mMenuItemCopyToClipboard.addActionListener(this);
	mMenuItemCopyToClipboard.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_C, InputEvent.CTRL_MASK));
	mMenuFilterSelection.add(mMenuItemCopyToClipboard);

	mMenuItemCopyToClipboardBodyOnly = new JMenuItem("ログの本文をクリップボードのコピー(P)");
	mMenuItemCopyToClipboardBodyOnly
		.setActionCommand(COMMAND_FILTER_COPYBODY);
	mMenuItemCopyToClipboardBodyOnly.setMnemonic('P');
	mMenuItemCopyToClipboardBodyOnly.addActionListener(this);
	mMenuItemCopyToClipboardBodyOnly.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
	mMenuFilterSelection.add(mMenuItemCopyToClipboardBodyOnly);

	mMenuItemShowDetails = new JMenuItem("ログの詳細(D)");
	mMenuItemShowDetails.setActionCommand(Main.COMMAND_LOG_DETAILS);
	mMenuItemShowDetails.setMnemonic('L');
	mMenuItemShowDetails.addActionListener(this);
	mMenuItemShowDetails.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_D, 0));
	mMenuFilterSelection.add(mMenuItemShowDetails);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case COMMAND_DEVICE:
	    String text = ((JMenuItem) e.getSource()).getText();
	    int i = Integer.parseInt(text.split(":")[0]) - 1;
	    mLogPanel.setDevice(mDeviceList.get(i));
	    break;
	case COMMAND_LOGLEVEL_VERBOSE:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_VERBOSE);
	    break;
	case COMMAND_LOGLEVEL_DEBUG:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_DEBUG);
	    break;
	case COMMAND_LOGLEVEL_INFO:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_INFO);
	    break;
	case COMMAND_LOGLEVEL_WARN:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_WARN);
	    break;
	case COMMAND_LOGLEVEL_ERROR:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_ERROR);
	    break;
	case COMMAND_FILTER_PROCESS:
	    ArrayList<Integer> ids = new ArrayList<>();
	    ArrayList<String> names = new ArrayList<>();
	    for (ProcessInfo info : mLogPanel.getProcessList().values()) {
		if (info.getName().split("\\.").length >= 3) {
		    // アプリプロセスのみ抽出
		    ids.add(info.getPID());
		    names.add(info.getName());
		}
	    }
	    int proc = CustomOptionPane.showDialogGetIndex(this, "プロセス",
		    "プロセスを選択", false, names.toArray(new String[names.size()]));
	    if (proc != -1) {
		mLogPanel.setFilteredPID(ids.get(proc));
	    }
	    break;
	case COMMAND_FILTER_TAGS:
	    String tags = CustomOptionPane.showDialog(this, "タグ", "タグを選択",
		    true,
		    mRecentTagList.toArray(new String[mRecentTagList.size()]));
	    if (tags != null) {
		mRecentTagList.remove(tags);
		mRecentTagList.add(tags);
		mLogPanel.setFilteredTag(tags);
	    }
	    break;
	case COMMAND_FILTER_ERASE:
	    mMenuItemLogLevelVerbose.doClick();
	    mLogPanel.setFilteredTag(null);
	    mLogPanel.setFilteredPID(PIDFilter.NOSELECT);
	    break;
	case COMMAND_FILTER_CHASEITEM:
	    boolean state = !mLogPanel.isChaseItem();
	    mLogPanel.setChaseItem(state);
	    mMenuItemChaseItem.setSelected(state);
	    break;
	case COMMAND_FILTER_THISTAG:
	case COMMAND_FILTER_THISPROC:
	case COMMAND_FILTER_WATCHPROC:
	case COMMAND_FILTER_COPY:
	case COMMAND_FILTER_COPYBODY:
	case COMMAND_LOG_DETAILS:
	    mLogPanel.actionPerformed(e);
	    break;
	default:
	    break;
	}
    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
	saveProperties();
	System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    /**
     * エラーダイアログを表示します。
     *
     * @param e
     *            例外オブジェクト
     */
    private void showStandardErrorDialog(Exception e) {
	showStandardErrorDialog(e.getMessage());
    }

    /**
     * エラーダイアログを表示します。
     *
     * @param e
     *            エラーを説明した文字列
     */
    private void showStandardErrorDialog(String error) {
	JOptionPane.showMessageDialog(this, error, "エラー",
		JOptionPane.ERROR_MESSAGE);
    }
}
