package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;



import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.QuestionReply;


public class QuestionReplyListAdapter extends FirebaseListAdapter<QuestionReply> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    ExpandedQuestionActivity activity;

    public QuestionReplyListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, QuestionReply.class, layout, activity);

        // Must be ExpandedQuestionActivity
        assert (activity instanceof ExpandedQuestionActivity);

        this.activity = (ExpandedQuestionActivity) activity;
    }


    protected void populateView(View view, QuestionReply questionReply) {
        DBUtil dbUtil = activity.getDbutil();

        // Display post time
        Calendar calendar = Calendar.getInstance();
        TimeZone obj = calendar.getTimeZone();
        DateFormat formatter = new SimpleDateFormat("EEE MMM d, yyyy h:mm a");
        formatter.setTimeZone(obj);

        String time = formatter.format(questionReply.getTimestamp());

        String msgString = questionReply.getWholeMsg() + "<br>" + time;

        ((TextView) view.findViewById(R.id.question_reply_text)).setText(Html.fromHtml(msgString));
        view.setClickable(true); // Replies are never clickable.
    }

    @Override
    protected void sortModels(List<QuestionReply> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, QuestionReply model) {
        model.setKey(key);
    }
}
