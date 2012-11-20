package jp.takamichie.desktop.logcatviewer;

import java.awt.SystemColor;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import jp.takamichie.desktop.logcatviewer.classes.LogLine;

public class LogDetailDialog extends JPanel {
    private JTextArea mDisplay_Body;
    private JTextField mDisplay_Loglevel;
    private JTextField mDisplay_Timestamp;
    private JTextField mDisplay_Tags;
    private JTextField mDisplay_Process;

    public LogDetailDialog(LogLine log) {
	initializeComponent();
	mDisplay_Timestamp.setText(log.getTimeStamp());
	mDisplay_Loglevel.setText(log.getLevel() + "");
	mDisplay_Process.setText(log.getProcessName());
	mDisplay_Tags.setText(log.getTags());
	mDisplay_Body.setText(log.getBody());
    }

    private void initializeComponent() {
	JLabel lblNewLabel = new JLabel("タイムスタンプ");

	mDisplay_Timestamp = new JTextField();
	mDisplay_Timestamp.setEditable(false);
	mDisplay_Timestamp.setColumns(10);

	JLabel lblNewLabel_1 = new JLabel("ログレベル");

	mDisplay_Loglevel = new JTextField();
	mDisplay_Loglevel.setEditable(false);
	mDisplay_Loglevel.setColumns(10);

	JLabel lblNewLabel_2 = new JLabel("プロセス名");

	mDisplay_Process = new JTextField();
	mDisplay_Process.setEditable(false);
	mDisplay_Process.setColumns(10);

	JLabel lblNewLabel_3 = new JLabel("タグ");

	mDisplay_Tags = new JTextField();
	mDisplay_Tags.setEditable(false);
	mDisplay_Tags.setColumns(10);

	JLabel lblNewLabel_4 = new JLabel("本文");

	JScrollPane scrollPane = new JScrollPane();
	GroupLayout groupLayout = new GroupLayout(this);
	groupLayout
		.setHorizontalGroup(groupLayout
			.createParallelGroup(Alignment.LEADING)
			.addGroup(
				groupLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						groupLayout
							.createParallelGroup(
								Alignment.LEADING)
							.addComponent(
								scrollPane,
								GroupLayout.DEFAULT_SIZE,
								259,
								Short.MAX_VALUE)
							.addGroup(
								groupLayout
									.createSequentialGroup()
									.addGroup(
										groupLayout
											.createParallelGroup(
												Alignment.LEADING)
											.addComponent(
												lblNewLabel)
											.addComponent(
												lblNewLabel_1))
									.addPreferredGap(
										ComponentPlacement.RELATED)
									.addGroup(
										groupLayout
											.createParallelGroup(
												Alignment.LEADING)
											.addComponent(
												mDisplay_Loglevel,
												GroupLayout.DEFAULT_SIZE,
												194,
												Short.MAX_VALUE)
											.addComponent(
												mDisplay_Timestamp,
												GroupLayout.DEFAULT_SIZE,
												194,
												Short.MAX_VALUE)))
							.addGroup(
								groupLayout
									.createSequentialGroup()
									.addGroup(
										groupLayout
											.createParallelGroup(
												Alignment.LEADING)
											.addComponent(
												lblNewLabel_2)
											.addComponent(
												lblNewLabel_3))
									.addGap(18)
									.addGroup(
										groupLayout
											.createParallelGroup(
												Alignment.LEADING)
											.addComponent(
												mDisplay_Tags,
												GroupLayout.DEFAULT_SIZE,
												194,
												Short.MAX_VALUE)
											.addComponent(
												mDisplay_Process,
												GroupLayout.DEFAULT_SIZE,
												194,
												Short.MAX_VALUE)))
							.addComponent(
								lblNewLabel_4))
					.addContainerGap()));
	groupLayout
		.setVerticalGroup(groupLayout
			.createParallelGroup(Alignment.LEADING)
			.addGroup(
				groupLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						groupLayout
							.createParallelGroup(
								Alignment.BASELINE)
							.addComponent(
								lblNewLabel)
							.addComponent(
								mDisplay_Timestamp,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(
						ComponentPlacement.UNRELATED)
					.addGroup(
						groupLayout
							.createParallelGroup(
								Alignment.BASELINE)
							.addComponent(
								lblNewLabel_1)
							.addComponent(
								mDisplay_Loglevel,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(
						ComponentPlacement.UNRELATED)
					.addGroup(
						groupLayout
							.createParallelGroup(
								Alignment.BASELINE)
							.addComponent(
								lblNewLabel_2)
							.addComponent(
								mDisplay_Process,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(
						ComponentPlacement.UNRELATED)
					.addGroup(
						groupLayout
							.createParallelGroup(
								Alignment.BASELINE)
							.addComponent(
								lblNewLabel_3)
							.addComponent(
								mDisplay_Tags,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(
						ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel_4)
					.addPreferredGap(
						ComponentPlacement.RELATED)
					.addComponent(scrollPane,
						GroupLayout.DEFAULT_SIZE, 115,
						Short.MAX_VALUE)
					.addContainerGap()));

	mDisplay_Body = new JTextArea();
	mDisplay_Body.setBackground(SystemColor.control);
	mDisplay_Body.setEditable(false);
	scrollPane.setViewportView(mDisplay_Body);
	setLayout(groupLayout);

    }
}
