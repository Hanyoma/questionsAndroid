package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

import android.text.Html;
import android.util.Log;

/**
 * Created by marc on 03-Nov-15.
 */
public class QuestionReply implements Comparable<QuestionReply> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */
    private String key;
    private String wholeMsg;
    private long timestamp;
    private int echo;
    private int order;


    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private QuestionReply() {
    }

    /**
     * Set question from a String message
     * @param message string message
     */
    public QuestionReply(String theText) {
        // "Sanitize" the input, escaping HTML sequences.
        theText = Html.escapeHtml(theText);
        this.wholeMsg = theText;
        this.echo = 0;

        this.timestamp = new Date().getTime();
    }


    public int getEcho() {
        return echo;
    }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrder() {
        return order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(QuestionReply other) {
        // Dummy. We won't be using this.
        return 0;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
