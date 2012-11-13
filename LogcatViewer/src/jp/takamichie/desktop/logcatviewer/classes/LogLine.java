package jp.takamichie.desktop.logcatviewer.classes;

import java.util.regex.Matcher;

public class LogLine {

    /**
     * Logcatの解析用正規表現です
     */
    public static final String LOGCAT_REGEX = "([\\d-]+\\s[\\d:.]+)\\s(\\w)/([^(]+)\\(\\s*(\\d*)\\):\\s*(.*)";

    /**
     * Logcatの解析用正規表現から、ミリ秒の指定を除いたものです
     */
    public static final String LOGCAT_REGEX_MSEC_IGNORED = "([\\d-]+\\s[\\d:]+)\\.\\d+\\s(\\w)/([^(]+)\\(\\s*(\\d*)\\):\\s*(.*)";

    private String mTimeStamp;
    private char mLevel;
    private String mTags;
    private int mPID;
    private String mBody;

    public LogLine(Matcher m) {
	this.mTimeStamp = m.group(1);
	this.mLevel = m.group(2).charAt(0);
	this.mTags = m.group(3);
	this.mPID = Integer.parseInt(m.group(4));
	this.mBody = m.group(5);
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
	builder.append(mBody);
	return builder.toString();
    }
}
