package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jp.takamichie.desktop.logcatviewer.classes.CustomOptionPane;
import jp.takamichie.desktop.logcatviewer.table.filter.PIDFilter;

public class Main extends JFrame implements ActionListener {
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

    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Main window = new Main();
		    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    window.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public Main() {

	initializeComponent();
	try {
	    mLogPanel.setDevice(null);
	    // TODO: 直前のウィンドウ位置の復元
	    this.setSize(400, 300);
	    mRecentTagList = new ArrayList<String>();
	} catch (IOException e) {
	    e.printStackTrace();
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
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
	    mLogPanel.actionPerformed(e);
	    break;
	default:
	    break;
	}
    }

}
