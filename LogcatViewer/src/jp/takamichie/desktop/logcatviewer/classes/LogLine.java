package jp.takamichie.desktop.logcatviewer.classes;

import java.util.regex.Matcher;

public class LogLine {

    public static final String LOGCAT_REGEX = "([\\d-]+\\s[\\d:.]+)\\s(\\w)/([^(]+)\\(\\s*(\\d*)\\):\\s*(.*)";
    public static final int MATCH_COUNT = 5;

    private String mTimeStanp;
    private char mLevel;
    private String mTags;
    private int mPID;
    private String mBody;

    public LogLine(Matcher m){
	this.mTimeStanp = m.group(1);
	this.mLevel = m.group(2).charAt(0);
	this.mTags = m.group(3);
	this.mPID = Integer.parseInt(m.group(4));
	this.mBody = m.group(5);
    }

    public String getTimeStanp() {
        return mTimeStanp;
    }

    public char getLevel() {
        return mLevel;
    }

    public String getTags() {
        return mTags;
    }

    public int getPID() {
        return mPID;
    }

    public String getBody() {
        return mBody;
    }


}
