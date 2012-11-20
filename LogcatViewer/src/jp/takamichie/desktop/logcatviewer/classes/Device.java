package jp.takamichie.desktop.logcatviewer.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Device {
    private String mDeviceID;
    private String mDeviceName;

    public Device(String deviceID) {
	this.mDeviceID = deviceID;
	// デバイス名取得
	try {
	    Process process = new ProcessBuilder("adb", "-s", deviceID, "shell",
		    "getprop", "ro.product.model").redirectErrorStream(true)
		    .start();
	    try (BufferedReader reader = new BufferedReader(
		    new InputStreamReader(process.getInputStream()))) {
		this.mDeviceName = reader.readLine();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public String getDeviceID() {
	return mDeviceID;
    }

    public String getDeviceName() {
	return mDeviceName;
    }

    @Override
    public String toString() {
	return String.format("%s(%s)", mDeviceID, mDeviceName);
    }
}
