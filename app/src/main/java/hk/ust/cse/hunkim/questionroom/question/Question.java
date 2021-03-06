package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;
import java.util.List;

import android.text.Html;
import android.util.Log;

/**
 * Created by hunkim on 7/16/15.
 */


public class Question implements Comparable<Question> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */
    private String key;
    private String wholeMsg;
    private String head;
    private String headLastChar;
    private String desc;
    private String linkedDesc;
    private boolean completed;
    private long timestamp;
    private String tags;
    private int echo;
    private int order;
    private boolean newQuestion;
    private Object replies; // This is needed for the "replies" subfolder of every question.
    private int numberOfReplies;

    public int getNumberOfReplies() { return numberOfReplies; }

    public Object getReplies() {
        return replies;
    }
    private int totalPollVotes;
    protected List<PollQuestion.Poll> pollOptions;

    public String getDateString() {
        return dateString;
    }

    private String dateString;

    public String getTrustedDesc() {
        return trustedDesc;
    }

    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    protected Question() {
    }
    /**
     * Set question from a String message
     * @param message string message
     */
    public Question(String title, String message) {
        // "Sanitize" the input, escaping HTML sequences.
        title = Html.escapeHtml(title);
        message = Html.escapeHtml(message);
        this.wholeMsg = title + " " + message;
        this.echo = 0;
        this.head = title;
        this.desc = message;
        this.numberOfReplies = 0;

        // get the last char
        this.headLastChar = head.substring(head.length() - 1);

        this.timestamp = new Date().getTime();

        this.pollOptions = null;
        this.totalPollVotes = 0;

    }

    /**
     * Get first sentence from a message
     * @param message
     * @return
     */
    public static String getFirstSentence(String message) {
        String[] tokens = {". ", "? ", "! "};

        int index = -1;

        for (String token : tokens) {
            int i = message.indexOf(token);
            if (i == -1) {
                continue;
            }

            if (index == -1) {
                index = i;
            } else {
                index = Math.min(i, index);
            }
        }

        if (index == -1) {
            return message;
        }

        return message.substring(0, index+1);
    }

    /* -------------------- Getters ------------------- */
    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public int getEcho() {
        return echo;
    }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public String getHeadLastChar() {
        return headLastChar;
    }

    public String getLinkedDesc() {
        return linkedDesc;
    }

    public List<PollQuestion.Poll> getPollOptions() {return pollOptions; }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTags() {
        return tags;
    }

    public int getOrder() {
        return order;
    }

    public int getTotalPollVotes() {
        int total = 0;
        if(pollOptions != null) {
            for (PollQuestion.Poll pollOption : pollOptions) {
                total += pollOption.getVotes();
            }
            totalPollVotes = total;
            return total;
        }
        return -1;
    }

    public boolean isNewQuestion() {
        return newQuestion;
    }

    public void updateNewQuestion() {
        newQuestion = this.timestamp > new Date().getTime() - 180000;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * New one/high echo goes bottom
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        // Push new on top
        other.updateNewQuestion(); // update NEW button
        this.updateNewQuestion();

        if (this.newQuestion != other.newQuestion) {
            return this.newQuestion ? 1 : -1; // this is the winner
        }


        if (this.echo == other.echo) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.echo - other.echo;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        if (key == null)
        {
            return 0;
        }
        //else.
        return key.hashCode();
    }
}
