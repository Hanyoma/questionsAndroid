package hk.ust.cse.hunkim.questionroom;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.PollQuestion;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class MainActivity extends ListActivity {

    // DONE: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://intense-inferno-7677.firebaseIO.com/";

    //private static final String FIREBASE_URL = "https://vivid-inferno-237.firebaseIO.com";


    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;
    private PollListAdapter mPollListAdapter;

    private DBUtil dbutil;
    private DBUtil polldbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }
    public DBUtil getPolldbutil() { return polldbutil; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "TEMP";
        }

        setTitle("Room name: " + roomName);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child(roomName).child("questions");

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
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
        findViewById(R.id.pollButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createPoll();
//                Toast.makeText(MainActivity.this, "Yay poll button works", Toast.LENGTH_SHORT).show();
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
        DBHelper mPollDbHelper = new DBHelper(this);
        polldbutil = new DBUtil(mPollDbHelper);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("echo").limitToFirst(200),
                this, R.layout.question, roomName);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();

                // MARC: I decided to take out the toast message concerning the firebase connection. It causes more confusion than it actually helps since at every start it shows disconnected and then connected.
//                if (connected) {
//                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    // Gets the text from the two input fields as submits a new question entry to the firebase
    private void sendMessage() {
        // Get text from the two inputs field as Strings
        EditText titleText = (EditText) findViewById(R.id.titleInput);
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String title = titleText.getText().toString();
        String input = inputText.getText().toString();

        if (!title.equals("") && !input.equals("")) { // Only actually submit the question if it has a title and a main text.
            // Create corresponding Question object
            Question question = new Question(title,input);

            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);

            // Clear the two input fields since this question has been successfully submitted
            titleText.setText("");
            inputText.setText("");
        }
    }

    public void updateEcho(String key) {
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase echoRef = mFirebaseRef.child(key).child("echo");
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

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
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
    }

    public void Close(View view) {
        finish();
    }

    public Firebase getFirebaseRef() {return mFirebaseRef;}

    public void createPoll()
    {
        final DialogFragment pollDialog = new CreatePollDialog() {
            @Override
            public void onPositiveButtonClick() {
                //If the user input is valid, create a new PollQuestion and push it to Firebase
                if(pollTitle != null && pollTitle.length() > 0 && pollOptions.size() >= 2)
                    mFirebaseRef.push().setValue(new PollQuestion(pollTitle," ", pollOptions));
            }
        };
        pollDialog.show(getFragmentManager(), "PollDialogFrag");

    }

    public void onVoteClick(final String key, final int pollSelectIndex, final View questionView)
    {
        if (polldbutil.contains(key+"poll")) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase pollRef = mFirebaseRef.child(key).child("pollOptions");
        pollRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> pollChildren = dataSnapshot.getChildren().iterator();
                        List<DataSnapshot> optionRefs = new ArrayList<DataSnapshot>();
                        List<Long> optionVotes = new ArrayList<Long>();
                        Log.e("Poll update:", "" + pollRef);

                        // Iterate through and save all pollOption "vote" snapshots
                        while( pollChildren.hasNext())
                        {
                            optionRefs.add(pollChildren.next());
                        }
                        // Get and increment the selected pollOption's vote counter.
                        DataSnapshot choiceRefSS = optionRefs.get(pollSelectIndex).child("votes");
                        Firebase choiceRef =  choiceRefSS.getRef();
                        Long choiceVotes = (Long)choiceRefSS.getValue();
                        choiceRef.setValue(choiceVotes + 1);
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        // Add key to the database to prevent duplicate voting.
        polldbutil.put(key+"poll");

    }

//    public void updatePollDisplay(List<DataSnapshot> optionRefs, List<Long> optionVotes, View questionView)
//    {
//        LinearLayout pollDisplayArea = (LinearLayout) questionView.findViewById(R.id.pollLayout);
//        ((ViewGroup) pollDisplayArea.getParent()).removeView(pollDisplayArea);
//
//        int totalVotes = 0;
//        for(Long optionVote : optionVotes)
//        {
//            totalVotes += optionVote.intValue();
//        }
//        pollDisplayArea.removeAllViews();
//
//        Log.d("REMOVE DISPLAY", pollDisplayArea.getChildCount() + "");
//        for(DataSnapshot optionRef : optionRefs) {
//            Log.d("SNAP ITERATOR", "IM IN THE LOOP");
//            ProgressBar optionProgress = new ProgressBar(this);
//            optionProgress.setMax();
//            optionProgress.setProgress((int) (long) optionRef.child("votes").getValue());
//            optionProgress.setContentDescription((String) optionRef.child("pollString").getValue());
//            pollDisplayArea.addView(optionProgress);
//        }
//        Log.d("FINISHED PROGREEBARS", pollDisplayArea.getChildCount()+"");
//    }

}
