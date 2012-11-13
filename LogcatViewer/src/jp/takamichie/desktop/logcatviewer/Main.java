package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

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


public class Main extends JFrame implements ActionListener {
    private static final String COMMAND_LOGLEVEL_VERBOSE = "logverbose";
    private static final String COMMAND_LOGLEVEL_DEBUG = "logdebug";
    private static final String COMMAND_LOGLEVEL_INFO = "loginfo";
    private static final String COMMAND_LOGLEVEL_WARN = "logwarn";
    private static final String COMMAND_LOGLEVEL_ERROR = "logerror";
    private static final String COMMAND_FILTER_PROCESS = "procfilter";
    private static final String COMMAND_FILTER_CHASEITEM = "chaseitem";
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
    private JMenuItem mMenuItemProcess;
    private JCheckBoxMenuItem mMenuItemChaseItem;

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

	mMenuItemProcess = new JMenuItem("プロセス...");
	mMenuItemProcess.setMnemonic('P');
	mMenuItemProcess.setAccelerator(KeyStroke
		.getKeyStroke(KeyEvent.VK_P, 0));
	mMenuItemProcess.setActionCommand(COMMAND_FILTER_PROCESS);
	mMenuItemProcess.addActionListener(this);
	mMenuFilters.add(mMenuItemProcess);

	mMenuItemChaseItem = new JCheckBoxMenuItem("ログを追尾(C)");
	mMenuItemChaseItem.setMnemonic('C');
	mMenuItemChaseItem.setAccelerator(KeyStroke
		.getKeyStroke(KeyEvent.VK_C, 0));
	mMenuItemChaseItem.setActionCommand(COMMAND_FILTER_CHASEITEM);
	mMenuItemChaseItem.addActionListener(this);
	mMenuFilters.add(mMenuItemChaseItem);
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
	case COMMAND_FILTER_CHASEITEM:
	    boolean state = !mLogPanel.isChaseItem();
	    mLogPanel.setChaseItem(state);
	    mMenuItemChaseItem.setSelected(state);
	    break;
	default:
	    break;
	}
    }

}
