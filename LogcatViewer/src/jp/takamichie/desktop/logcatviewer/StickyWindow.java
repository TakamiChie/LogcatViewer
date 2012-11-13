package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

/**
 * 付箋紙状に表示されるウィンドウです。
 *
 * @author 知英
 */
public class StickyWindow extends JFrame implements MouseListener,
	MouseMotionListener {
    private JLabel mDisplayTitle;
    private JTextArea mDisplayMessage;
    private JButton mOperateSave;
    private Point mLoc;
    private MouseEvent mStart;

    public StickyWindow(String title, String message) {
	initializeComponent();
	setUndecorated(true);
	mDisplayTitle.setText(title);
	mDisplayMessage.setText(message);
	setSize(400, 300);
    }

    private void initializeComponent() {
	JToolBar toolBox = new JToolBar();
	getContentPane().add(toolBox, BorderLayout.NORTH);
	toolBox.setFloatable(false);
	toolBox.addMouseListener(this);
	toolBox.addMouseMotionListener(this);

	mDisplayTitle = new JLabel("New label");
	toolBox.add(mDisplayTitle);

	mOperateSave = new JButton("保存(S)");
	mOperateSave.setMnemonic('S');
	toolBox.add(mOperateSave);

	JScrollPane scrollPane = new JScrollPane();
	getContentPane().add(scrollPane, BorderLayout.CENTER);

	mDisplayMessage = new JTextArea();
	scrollPane.setViewportView(mDisplayMessage);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	mLoc = getLocation(mLoc);
	int x = (int) (mLoc.x - mStart.getX() + e.getX());
	int y = (int) (mLoc.y - mStart.getY() + e.getY());
	setLocation(x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
	mStart = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }
}
