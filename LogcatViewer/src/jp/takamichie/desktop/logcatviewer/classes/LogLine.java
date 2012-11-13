package jp.takamichie.desktop.logcatviewer.classes;

import java.util.regex.Matcher;

public class LogLine {

    public static final String LOGCAT_REGEX = "([\\d-]+\\s[\\d:.]+)\\s(\\w)/([^(]+)\\(\\s*(\\d*)\\):\\s*(.*)";
    public static final int MATCH_COUNT = 5;

    private String mTimeStamp;
    private char mLevel;
    private String mTags;
    private int mPID;
    private String mBody;
    private int mLineCount;

    public LogLine(Matcher m) {
	this.mTimeStamp = m.group(1);
	this.mLevel = m.group(2).charAt(0);
	this.mTags = m.group(3);
	this.mPID = Integer.parseInt(m.group(4));
	this.mBody = m.group(5);
	this.mLineCount = mBody.split("\n").length;
    }

    public String getTimeStamp() {
	return mTimeStamp;
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

    public int getLineCount() {
	return mLineCount;
    }

    /**
     * 対象となるアイテムがこのアイテムと近い(Body以外の値が等しい)かどうかを確かめます
     *
     * @param log
     *            比較対象となるログアイテム
     * @return アイテムがこのアイテムと近いかどうか
     */
    public boolean same(LogLine log) {
	return mTimeStamp.equals(log.mTimeStamp) && mLevel == log.getLevel()
		&& mPID == log.getPID() && mTags.equals(getTags());
    }

    /**
     * 対象となるアイテムのBodyをこのアイテムにマージします。
     *
     * @param log
     *            マージするログアイテム
     */
    public void marge(LogLine log) {
	mBody += "\n" + log.mBody;
	mLineCount += log.mBody.split("\n").length;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(mTimeStamp);
	builder.append('\t');
	builder.append(mLevel);
	builder.append('/');
	builder.append(mTags);
	builder.append('(');
	builder.append(mPID);
	builder.append(')');
	builder.append('\n');
	builder.append('\t');
	builder.append(mBody);
	return builder.toString();
    }
}
