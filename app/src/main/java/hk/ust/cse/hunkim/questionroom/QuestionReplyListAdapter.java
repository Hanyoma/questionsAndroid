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

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
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

        // Map a Chat object to an entry in our listview
//        int echo = question.getEcho();
//        Button echoButton = (Button) view.findViewById(R.id.echo);
//        echoButton.setText("" + echo);
//        echoButton.setTextColor(Color.BLUE);


//        echoButton.setTag(question.getKey()); // Set tag for button

//        echoButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        MainActivity m = (MainActivity) view.getContext();
//                        m.updateEcho((String) view.getTag());
//                    }
//                }
//
//        );
//
//        String msgString = "";
//
//        question.updateNewQuestion();
//        if (question.isNewQuestion()) {
//            msgString += "<font color=red>NEW </font>";
//        }

        // Display post time
        Calendar calendar = Calendar.getInstance();
        TimeZone obj = calendar.getTimeZone();
        DateFormat formatter = new SimpleDateFormat("EEE MMM d, yyyy h:mm a");
        formatter.setTimeZone(obj);

        String time = formatter.format(questionReply.getTimestamp());

        String msgString = questionReply.getWholeMsg() + "<br>" + time;

        ((TextView) view.findViewById(R.id.question_reply_text)).setText(Html.fromHtml(msgString));
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
