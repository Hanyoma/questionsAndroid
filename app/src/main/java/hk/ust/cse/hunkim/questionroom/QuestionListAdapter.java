package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.client.Query;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;



import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.PollQuestion;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;

    public QuestionListAdapter(Query ref, Activity activity, int layout, String roomName) {
        super(ref, Question.class, layout, activity);

        // Must be MainActivity
        assert (activity instanceof MainActivity);

        this.activity = (MainActivity) activity;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(final View view, Question question) {
        DBUtil dbUtil = activity.getDbutil();
        DBUtil polldbUtil = activity.getPolldbutil();
        MainActivity main;

        // Map a Chat object to an entry in our listview
        int echo = question.getEcho();
        Button echoButton = (Button) view.findViewById(R.id.echo);
        echoButton.setText("" + echo);
        echoButton.setTextColor(Color.BLUE);


        echoButton.setTag(question.getKey()); // Set tag for button

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateEcho((String) view.getTag());
                    }
                }

        );

        String msgString = "";

        question.updateNewQuestion();
        if (question.isNewQuestion()) {
            msgString += "<font color=red>NEW </font>";
        }

        // Display post time
        Calendar calendar = Calendar.getInstance();
        TimeZone obj = calendar.getTimeZone();
        DateFormat formatter = new SimpleDateFormat("EEE MMM d, yyyy h:mm a");
        formatter.setTimeZone(obj);

        String time = formatter.format(question.getTimestamp());

        String repliesString;
        if (question.getNumberOfReplies() == 1)
        {
            repliesString = " reply";
        }
        else
        {
            repliesString = " replies";
        }

        msgString += "<B>" + Html.escapeHtml(question.getHead()) + "</B><br>" + " " + Html.escapeHtml(question.getDesc()) + "<br>" + time + "<br>" + question.getNumberOfReplies() +  repliesString + "<br>";

        ((TextView) view.findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));
        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity m = (MainActivity) view.getContext();
                                        m.postWasClicked((String) view.getTag());
                                    }
                                }

        );

        // check if we already clicked
        boolean clickable = !dbUtil.contains(question.getKey());

        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);
//        view.setClickable(clickable); // If this line is here we cannot click a question anymore to open a post to see the replies.


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (clickable) {
            echoButton.getBackground().setColorFilter(null);
        } else {
            echoButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }


        view.setTag(question.getKey());  // store key in the view

        LinearLayout pollLayout = (LinearLayout) view.findViewById(R.id.pollLayout);
        pollLayout.removeAllViews();
        // If the question is a pollQuestion, there is more work to do...
        if(question.getPollOptions() != null ) {
            final List<PollQuestion.Poll> pollOptions = question.getPollOptions();

            if (polldbUtil.contains(question.getKey() + "poll")) {
                for (PollQuestion.Poll option : pollOptions) {
//                    ProgressBar optionProgress = new ProgressBar(activity.getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
//                    optionProgress.setMax(question.getTotalPollVotes());
//                    optionProgress.setProgress(option.getVotes());
//                    optionProgress.setContentDescription(option.getPollString());
//                    pollLayout.addView(optionProgress);

                    pollLayout.setOrientation(LinearLayout.VERTICAL);
                    TextView optionProgress = new TextView(activity.getApplicationContext());
                    optionProgress.setText(option.getPollString() + ": " + option.getVotes());
                    optionProgress.setTextColor(Color.BLACK);
                    optionProgress.setGravity(Gravity.RIGHT);
                    pollLayout.addView(optionProgress);
                }
            } else {
                View pollDisplay = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.poll_display, null);
                final RadioGroup pollRadioGroup = (RadioGroup) pollDisplay.findViewById(R.id.pollRadioGroup);
                pollLayout.addView(pollDisplay);

                Button voteButton = (Button) pollDisplay.findViewById(R.id.voteButton);
                boolean pollClickable = !polldbUtil.contains(question.getKey() + "poll");
                voteButton.setClickable(pollClickable);

                pollRadioGroup.removeAllViews();
                for (PollQuestion.Poll option : pollOptions) {
                    RadioButton pollChoice = new RadioButton(activity.getApplicationContext());
                    pollChoice.setText(option.getPollString());
                    pollChoice.setTextColor(Color.BLACK);
                    pollChoice.setBackgroundColor(Color.argb(150, 150, 150, 150));
                    pollRadioGroup.addView(pollChoice);
                }

                voteButton.setTag(question.getKey());
                voteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View thisView) {
                        int optionSelected = pollRadioGroup.getCheckedRadioButtonId();
                        if(optionSelected != -1) {
                            View radioSelected = pollRadioGroup.findViewById(optionSelected);
                            int radioIndex = pollRadioGroup.indexOfChild(radioSelected);
//                    Log.d("RadioSize", "" + pollRadioGroup.getChildCount());
//                    Log.d("CheckedId", "" + pollRadioGroup.getCheckedRadioButtonId());
//                    Log.d("SelectedIndex", "" + radioIndex);
                            activity.onVoteClick((String) (thisView.getTag()), radioIndex, view);
                        }
                    }
                });


            }

        }
        else
        {

        }
    }

    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }
}
