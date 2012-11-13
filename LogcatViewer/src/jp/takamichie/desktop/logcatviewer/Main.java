package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends JFrame implements ActionListener {
    private static final String COMMAND_LOGLEVEL_VERBOSE = "logverbose";
    private static final String COMMAND_LOGLEVEL_DEBUG = "logdebug";
    private static final String COMMAND_LOGLEVEL_INFO = "loginfo";
    private static final String COMMAND_LOGLEVEL_WARN = "logwarn";
    private static final String COMMAND_LOGLEVEL_ERROR = "logerror";
    private LogPanel mLogPanel;
    private JMenu mMenuFilters;
    private JMenu mMenuLogLevels;
    private JRadioButtonMenuItem mMenuItemLogLevelInfo;
    private JMenuItem mMenuItemLogLevelDebug;
    private JRadioButtonMenuItem mMenuItemLogLevelVerbose;
    private JRadioButtonMenuItem mMenuItemLogLevelWarn;
    private JRadioButtonMenuItem mMenuItemLogLevelError;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JMenuBar mMenuBar;

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
	    ButtonGroup logLevelGroup = new ButtonGroup();
	    for(MenuElement item : mMenuLogLevels.getSubElements()){
		if(item instanceof JRadioButtonMenuItem){
		    logLevelGroup.add((JRadioButtonMenuItem) item);
		}
	    }
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
	mLogPanel = new LogPanel();
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
	buttonGroup.add(mMenuItemLogLevelVerbose);
	mMenuItemLogLevelVerbose.setMnemonic('V');
	mMenuItemLogLevelVerbose.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_5, 0));
	mMenuItemLogLevelVerbose.setActionCommand(COMMAND_LOGLEVEL_VERBOSE);
	mMenuItemLogLevelVerbose.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelVerbose);

	mMenuItemLogLevelDebug = new JRadioButtonMenuItem("DEBUG");
	buttonGroup.add(mMenuItemLogLevelDebug);
	mMenuItemLogLevelDebug.setMnemonic('D');
	mMenuItemLogLevelDebug.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_4, 0));
	mMenuItemLogLevelDebug.setActionCommand(COMMAND_LOGLEVEL_DEBUG);
	mMenuItemLogLevelDebug.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelDebug);

	mMenuItemLogLevelInfo = new JRadioButtonMenuItem("INFO");
	buttonGroup.add(mMenuItemLogLevelInfo);
	mMenuItemLogLevelInfo.setMnemonic('I');
	mMenuItemLogLevelInfo.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_3, 0));
	mMenuItemLogLevelInfo.setActionCommand(COMMAND_LOGLEVEL_INFO);
	mMenuItemLogLevelInfo.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelInfo);

	mMenuItemLogLevelWarn = new JRadioButtonMenuItem("WARN");
	buttonGroup.add(mMenuItemLogLevelWarn);
	mMenuItemLogLevelWarn.setMnemonic('I');
	mMenuItemLogLevelWarn.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_2, 0));
	mMenuItemLogLevelWarn.setActionCommand(COMMAND_LOGLEVEL_WARN);
	mMenuItemLogLevelWarn.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelWarn);

	mMenuItemLogLevelError = new JRadioButtonMenuItem("ERROR");
	buttonGroup.add(mMenuItemLogLevelError);
	mMenuItemLogLevelError.setMnemonic('E');
	mMenuItemLogLevelError.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_1, 0));
	mMenuItemLogLevelError.setActionCommand(COMMAND_LOGLEVEL_ERROR);
	mMenuItemLogLevelError.addActionListener(this);
	mMenuLogLevels.add(mMenuItemLogLevelError);

	JMenuItem mMenuItemProcess = new JMenuItem("プロセス...");
	mMenuItemProcess.setMnemonic('P');
	mMenuItemProcess.setAccelerator(KeyStroke
		.getKeyStroke(KeyEvent.VK_P, 0));
	mMenuFilters.add(mMenuItemProcess);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case COMMAND_LOGLEVEL_VERBOSE:
	    mLogPanel.setLogLevel(LogPanel.LOGLEVEL_VERBOSE);
	    break;

	default:
	    break;
	}
    }

}
