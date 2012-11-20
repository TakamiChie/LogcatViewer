package jp.takamichie.desktop.logcatviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import jp.takamichie.desktop.logcatviewer.classes.Device;

public class ServiceViewer extends JPanel {
    private static final long AUTOUPDATE_TIMER = 5000;
    private JList<Device> mDeviceList;
    private ArrayList<Device> mDevices;
    private ScheduledExecutorService mScheduler;
    private boolean mIsAutoUpdate;

    public ServiceViewer() {
	initializeComponent();
	mIsAutoUpdate = true;

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
		    mDevices = tempList;
		    SwingUtilities.invokeLater(new Runnable() {

		        @Override
		        public void run() {
		            mDeviceList.removeAll();
		            mDeviceList.setListData(mDevices.toArray(new Device[mDevices.size()]));
		        }
		    });
		}
	    });
	    t.start();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    private void initializeComponent() {
	setLayout(new BorderLayout(0, 0));

	mDeviceList = new JList<Device>();
	mDeviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	mDeviceList.setForeground(Color.WHITE);
	mDeviceList.setBackground(Color.BLACK);
	mDeviceList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
		null, null));
	add(mDeviceList, BorderLayout.CENTER);
    }

}
