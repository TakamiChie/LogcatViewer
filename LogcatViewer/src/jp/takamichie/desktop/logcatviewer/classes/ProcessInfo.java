package jp.takamichie.desktop.logcatviewer.classes;

import java.util.regex.Matcher;

public class ProcessInfo {

    public static final String PS_REGEX= "(\\w+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(.+)";
    private String mUserName;
    private int mPID;
    private int mPPID;
    private int mVSize;
    private int mRss;
    private long mWChan;
    private long mPC;
    private String mStatus;
    private String mName;

    public ProcessInfo(Matcher m){
	this.mUserName = m.group(1);
	this.mPID = Integer.parseInt(m.group(2));
	this.mPPID = Integer.parseInt(m.group(3));
	this.mVSize = Integer.parseInt(m.group(4));
	this.mRss = Integer.parseInt(m.group(5));
	this.mWChan = Long.parseLong(m.group(6).toUpperCase(), 16);
	this.mPC = Long.parseLong(m.group(7).toUpperCase(), 16);
	this.mStatus = m.group(8);
	this.mName = m.group(9);
    }

    public String getUserName() {
        return mUserName;
    }

    public int getPID() {
        return mPID;
    }

    public int getPPID() {
        return mPPID;
    }

    public int getVSize() {
        return mVSize;
    }

    public int getRss() {
        return mRss;
    }

    public long getWChan() {
        return mWChan;
    }

    public long getPC() {
        return mPC;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getName() {
        return mName;
    }


}
