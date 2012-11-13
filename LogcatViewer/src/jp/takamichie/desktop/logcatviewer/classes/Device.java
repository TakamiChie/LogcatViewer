package jp.takamichie.desktop.logcatviewer.classes;

public class Device {
    private String mDeviceID;
    private String mDeviceName;
    public Device(String deviceID){
	this.mDeviceID = deviceID;
    }
    public String getDeviceID() {
        return mDeviceID;
    }
    public String getDeviceName() {
        return mDeviceName;
    }

}
