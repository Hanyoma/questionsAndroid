package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.Query;

import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.QuestionListAdapter;


/**
 * Created by hunkim on 7/20/15.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

    private Intent mStartIntent;
    private ImageButton mButton;
    private String theRoomName = "newAll";
    private static Activity activity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // In setUp, you can create any shared test data,
        // or set up mock components to inject
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, theRoomName);
    }


    @MediumTest
    public void testPreconditions() {

        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });


        mButton = (ImageButton) getActivity().findViewById(R.id.sendButton);
        assertNotNull(getActivity());
        assertNotNull(mButton);

        assertEquals("This is set correctly", "Room name: " + theRoomName, getActivity().getTitle());
    }


    @MediumTest
    public void testPostingMessage() {
        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        Activity activity = startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                MainActivityTest.activity = startActivity(mStartIntent, null, null);
            }
        });

        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final TextView title = (TextView) activity.findViewById(R.id.titleInput);
        final ListView lView = getActivity().getListView();

        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(title);
        assertNotNull(lView);

        // Setup that will be used to get current number of posts in the theRoomName room in order to check whether our post was successfully added
        Firebase theFirebaseRoom = super.getActivity().getFirebaseRef();
        QuestionListAdapter theChatListAdapter = new QuestionListAdapter(
                theFirebaseRoom.orderByChild("echo").limitToFirst(2000),
                super.getActivity(), R.layout.question, theRoomName);


        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                lView.performItemClick(lView, 0, lView.getItemIdAtPosition(0));
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write into title field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                title.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        title.setText("This is the title.");

        // Write into text field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        text.setText("This is the test text!");

        // Save the number of question there are in the room BEFORE submitting this one
//        Log.i("devMainActivityTest", "Number of questions before: " + theChatListAdapter.getCount());
        int numberOfQuestionBeforePosting = theChatListAdapter.getCount();

        // Click the send button
        mButton.performClick();

        // Sleep to make sure we dont check the number again before it was properly sent to firebase
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Save the number of question there are in the room AFTER submitting this one
        int numberOfQuestionAfterPosting = theChatListAdapter.getCount();

        // After posting our question there should be one additional question.
        assertEquals("Child count: ", numberOfQuestionBeforePosting + 1, numberOfQuestionAfterPosting);
    }


    @MediumTest
    public void testEmptyRoomName() {
        // use empty room name
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, "");

        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        Activity activity = startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                MainActivityTest.activity = startActivity(mStartIntent, null, null);
            }
        });

        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final TextView title = (TextView) activity.findViewById(R.id.titleInput);


        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(title);

//        mButton.performClick();
    }


    @MediumTest
    public void testEmptyRoomName2() {
        // use empty room name
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, (String) null);

        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        Activity activity = startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                MainActivityTest.activity = startActivity(mStartIntent, null, null);
            }
        });

        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final TextView title = (TextView) activity.findViewById(R.id.titleInput);


        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(title);
    }

    @MediumTest
    public void testEmptyTitle() {
        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        Activity activity = startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                MainActivityTest.activity = startActivity(mStartIntent, null, null);
            }
        });

        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final TextView title = (TextView) activity.findViewById(R.id.titleInput);
        final ListView lView = getActivity().getListView();

        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(title);
        assertNotNull(lView);

        // Setup that will be used to get current number of posts in the theRoomName room in order to check whether our post was successfully added
        Firebase theFirebaseRoom = super.getActivity().getFirebaseRef();
        QuestionListAdapter theChatListAdapter = new QuestionListAdapter(
                theFirebaseRoom.orderByChild("echo").limitToFirst(2000),
                super.getActivity(), R.layout.question, theRoomName);


        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                lView.performItemClick(lView, 0, lView.getItemIdAtPosition(0));
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write into title field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                title.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        title.setText("");

        // Write into text field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        text.setText("This is the test text!");

        // Save the number of question there are in the room BEFORE submitting this one
//        Log.i("devMainActivityTest", "Number of questions before: " + theChatListAdapter.getCount());
        int numberOfQuestionBeforePosting = theChatListAdapter.getCount();

        // Click the send button
        mButton.performClick();

        // Sleep to make sure we dont check the number again before it was properly sent to firebase
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Save the number of question there are in the room AFTER submitting this one
        int numberOfQuestionAfterPosting = theChatListAdapter.getCount();

        // Our  question should not ahve been posted,c heck that this is true
        assertEquals("Child count: ", numberOfQuestionBeforePosting, numberOfQuestionAfterPosting);
    }

    @MediumTest
    public void testEmptyText() {
        // See first answer in http://stackoverflow.com/questions/27991363/activityunittestcase-throws-runtimeexception-when-ran-with-androidjunitrunner
//        Activity activity = startActivity(mStartIntent, null, null);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                MainActivityTest.activity = startActivity(mStartIntent, null, null);
            }
        });

        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final TextView title = (TextView) activity.findViewById(R.id.titleInput);
        final ListView lView = getActivity().getListView();

        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(title);
        assertNotNull(lView);

        // Setup that will be used to get current number of posts in the theRoomName room in order to check whether our post was successfully added
        Firebase theFirebaseRoom = super.getActivity().getFirebaseRef();
        QuestionListAdapter theChatListAdapter = new QuestionListAdapter(
                theFirebaseRoom.orderByChild("echo").limitToFirst(2000),
                super.getActivity(), R.layout.question, theRoomName);


        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                lView.performItemClick(lView, 0, lView.getItemIdAtPosition(0));
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write into title field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                title.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        title.setText("This is the title");

        // Write into text field
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        text.setText("");

        // Save the number of question there are in the room BEFORE submitting this one
//        Log.i("devMainActivityTest", "Number of questions before: " + theChatListAdapter.getCount());
        int numberOfQuestionBeforePosting = theChatListAdapter.getCount();

        // Click the send button
        mButton.performClick();

        // Sleep to make sure we dont check the number again before it was properly sent to firebase
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Save the number of question there are in the room AFTER submitting this one
        int numberOfQuestionAfterPosting = theChatListAdapter.getCount();

        // Our  question should not ahve been posted,c heck that this is true
        assertEquals("Child count: ", numberOfQuestionBeforePosting, numberOfQuestionAfterPosting);
    }
}
