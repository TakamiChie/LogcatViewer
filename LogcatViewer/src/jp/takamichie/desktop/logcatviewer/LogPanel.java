package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import jp.takamichie.desktop.logcatviewer.classes.Device;
import jp.takamichie.desktop.logcatviewer.classes.LogLine;
import jp.takamichie.desktop.logcatviewer.list.LogCellRenderer;
import jp.takamichie.desktop.logcatviewer.list.LogListModel;

public class LogPanel extends javax.swing.JPanel implements Runnable {
    public static final char LOGLEVEL_VERBOSE = 'V';
    public static final char LOGLEVEL_DEBUG = 'D';
    public static final char LOGLEVEL_INFO = 'I';
    public static final char LOGLEVEL_WARN = 'W';
    public static final char LOGLEVEL_ERROR = 'E';
    private Main mOwner;
    private JList<LogLine> mListLog;
    private Thread mLogcatThread;
    private Process mProccess;
    private boolean mChaseItem;
    private LogLine mLastLogItem;

    public LogPanel(Main main) {
	mOwner = main;
	initializeComponent();
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

	mListLog = new JList<>();
	mListLog.setModel(new LogListModel());
	mListLog.setCellRenderer(new LogCellRenderer());
	mListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
    }

    /**
     * 行を追加します。
     *
     * @param logLine
     *            追加する行を示す{@link LogLine}オブジェクト
     */
    private void addlog(LogLine logLine) {
	if(mLastLogItem != null && mLastLogItem.same(logLine)){
	    mLastLogItem.marge(logLine);
	    mListLog.invalidate();
	}else{
	    ((LogListModel)mListLog.getModel()).addElement(logLine);
	    mLastLogItem = logLine;
	}
	if(mChaseItem){
	    selectLastItem();
	}
    }

    /**
     * 最後のアイテムを選択します
     */
    private void selectLastItem() {
	mListLog.ensureIndexIsVisible(mListLog.getModel().getSize() - 1);
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
