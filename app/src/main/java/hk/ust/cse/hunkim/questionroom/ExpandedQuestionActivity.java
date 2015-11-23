package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.QuestionReply;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Iterator;

/**
 * Created by marc on 03-Nov-15.
 */
public class ExpandedQuestionActivity extends ListActivity {

    // DONE: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://intense-inferno-7677.firebaseIO.com/";

    //private static final String FIREBASE_URL = "https://vivid-inferno-237.firebaseIO.com";


    private HashMap<String, Object> thisQuestion = new HashMap<String, Object>();// = (Map<String, String>)dataSnapshot.getValue()
    private String roomName;
    private String questionKey;
    private String mainQuestionMsgString;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseRefTop;
    private ValueEventListener mConnectedListener;
    private QuestionReplyListAdapter mChatListAdapter;
    private Button echoButton;

    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.i("devDebugLog", "ExpandedQuestionActivity's onCreate.");

        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        // Set what we are viewing
        setContentView(R.layout.activity_expanded_question);


        // Get intent
        Intent intent = getIntent();
//        assert (intent != null);

        roomName = intent.getStringExtra(MainActivity.ROOM_NAME);
//        if (roomName == null || roomName.length() == 0) {
//            roomName = "all";
//        }

        questionKey = intent.getStringExtra(MainActivity.QUESTION_KEY);

        setTitle("Room name: " + roomName);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions").child(questionKey);
        mFirebaseRefTop = new Firebase(FIREBASE_URL).child(roomName).child("questions");

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.replyInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    @Override
    public void onStart() {
//        Log.i("devDebugLog", "ExpandedQuestionActivity's onStart.");
        super.onStart();


        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionReplyListAdapter(
                mFirebaseRef.child("replies").orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question_reply, roomName);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });


        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                /* This function is called for every entry of this element.
                Here is an example when calling .toString on dataSnapshot:
                Log.i("devDebugLog", dataSnapshot.toString());
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = completed, value = false }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = desc, value = and this the msg }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = echo, value = 0 }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = head, value = this is the title }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = headLastChar, value = e }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = newQuestion, value = false }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = order, value = 0 }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = timestamp, value = 1446599685052 }
                11-04 09:14:47.220  16578-16578/hk.ust.cse.hunkim.questionroom I/devDebugLog﹕ DataSnapshot { key = wholeMsg, value = this is the title and this the msg }
                */

                thisQuestion.put(dataSnapshot.getKey(), dataSnapshot.getValue());
//                Log.i("devDebugLog", "onChildAdded: " + dataSnapshot.toString());


                // TODO: This is too hacky: We know that wholeMsg will be the last element, so then we will have all elements in our thisQuestion hashmap.
                if (dataSnapshot.getKey().equals("wholeMsg")) {
                    Calendar calendar = Calendar.getInstance();
                    TimeZone obj = calendar.getTimeZone();
                    DateFormat formatter = new SimpleDateFormat("EEE MMM d, yyyy h:mm a");
                    formatter.setTimeZone(obj);
                    String time = formatter.format(thisQuestion.get("timestamp"));
                    String msgString = "<B>" + thisQuestion.get("head") + "</B><br>" + " " + thisQuestion.get("desc") + "<br>" + time + "<br>";
                    ((TextView) findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));


                    // Set the echo value
                    int echo = Integer.parseInt("" + thisQuestion.get("echo")); // Hacky....
                    echoButton = (Button) findViewById(R.id.echo);
                    echoButton.setText("" + echo);
                    echoButton.setTextColor(Color.BLUE);

                    echoButton.setTag(questionKey); // Set tag for button

                    echoButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    MainActivity m = (MainActivity) view.getContext();
                                    updateEcho(questionKey);
                                }
                            }

                    );


                    // check if we already clicked
                    boolean clickable = !dbutil.contains(questionKey);

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
                    // Make echo button red if already liked
//                    boolean clickable = !dbutil.contains(question.getKey());
//
//                    echoButton.setClickable(clickable);
//                    echoButton.setEnabled(clickable);
////        view.setClickable(clickable); // If this line is here we cannot click a question anymore to open a post to see the replies.
//
//
//                    // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
//                    // grey out our button
//                    if (clickable) {
//                        echoButton.getBackground().setColorFilter(null);
//                    } else {
//                        echoButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
//                    }
//
//
//                    view.setTag(question.getKey());  // store key in the view
                }


                // If a reply is added to this question in firebase, the key is replies. The datasnapshot contains all replies to this queston.
                // This one here will only happen for the first reply to the question, afterwards it is childChanged, not childAdded
                if (dataSnapshot.getKey().equals("replies")) {
                    handleAddedOrChangedReplies(dataSnapshot);
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.i("devDebugLog", "onChildChanged: " + dataSnapshot.toString() + " -----  " + s);
                if (dataSnapshot.getKey().equals("replies")) {
                    handleAddedOrChangedReplies(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.i("devDebugLog", "onChildRemoved: " + dataSnapshot.toString());
                if (dataSnapshot.getKey().equals("replies")) {
                    handleAddedOrChangedReplies(dataSnapshot);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.i("devDebugLog", "onChildMoved: " + dataSnapshot.toString() + " -----  " + previousChildName);
                if (dataSnapshot.getKey().equals("replies")) {
                    handleAddedOrChangedReplies(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }


    // Gets the text from the two input fields as submits a new question entry to the firebase
    private void sendMessage() {
        // Get text from the input field as String
        EditText replyInput = (EditText) findViewById(R.id.replyInput);
        String replyText = replyInput.getText().toString();

        if (!replyText.equals("")) { // Only actually submit the question if it has a title and a main text.
            // Create corresponding Question object
            QuestionReply questionReply = new QuestionReply(replyText);

            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.child("replies").push().setValue(questionReply);

            // Clear the two input fields since this question has been successfully submitted
            replyInput.setText("");

            // Increment the number of replies variable of this question
            incrementReplyCount();
        }
    }

    public void incrementReplyCount() {
        final Firebase repliesCountRef = mFirebaseRef.child("numberOfReplies");
        repliesCountRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Long repliesValue = (Long) dataSnapshot.getValue();

                        repliesCountRef.setValue(repliesValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }


    public void updateEcho(String key) {
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase echoRef = mFirebaseRefTop.child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Long echoValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + echoValue);

                        echoRef.setValue(echoValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRefTop.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);

        // Increment display by 1
        // Very unclean to do this here like this, should rather be done via two way data binding from firebase...
        int echo = Integer.parseInt("" + thisQuestion.get("echo")) + 1; // Hacky....
        echoButton.setText("" + echo);
        boolean clickable = !dbutil.contains(questionKey);
        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);

        if (clickable) {
            echoButton.getBackground().setColorFilter(null);
        } else {
            echoButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }
    }

    public void Close(View view) {
        finish();
    }


    // This method will be called when something is added or changed in the firebase data entry.
    private void handleAddedOrChangedReplies(DataSnapshot dataSnapshot) {

        Iterator it = ((Map) dataSnapshot.getValue()).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
//            Log.i("devDebugLog", "handleAddedOrChangedReplies: CURR: " + pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }


    }
}
