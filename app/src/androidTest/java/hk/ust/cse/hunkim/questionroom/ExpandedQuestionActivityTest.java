package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class ExpandedQuestionActivityTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    JoinActivity activity;
    EditText roomNameEditText;
    Button joinButton;

    private static final int TIMEOUT_IN_MS = 5000;

    public ExpandedQuestionActivityTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

        roomNameEditText =
                (EditText) activity.findViewById(R.id.room_name);

        joinButton =
                (Button) activity.findViewById(R.id.join_button);

    }




    public void testCreatingActivity() {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        Instrumentation.ActivityMonitor receiverExpandedQuestionActivityMonitor = getInstrumentation()
                .addMonitor(ExpandedQuestionActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        getInstrumentation().sendStringSync("newAll");
        getInstrumentation().waitForIdleSync();


        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        final MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        /*
        //Read the message received by ReceiverActivity
        final TextView receivedMessage = (TextView) mainActivity
                .findViewById(R.id.received_message_text_view);
        //Verify that received message is correct
        assertNotNull(receivedMessage);
        assertEquals("Wrong received message", TEST_MESSAGE, receivedMessage.getText().toString());
        */

        Intent intent = mainActivity.getIntent();
        assertNotNull("Intent should be set", intent);

        assertEquals("newAll", intent.getStringExtra(JoinActivity.ROOM_NAME));

        assertEquals("This is set correctly", "Room name: newAll", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);


        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final ListView lView = mainActivity.getListView();

        // Upvote one posts
        final String[] theKey = new String[1];
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("devMainActivityTest", "First: " + lView.getFirstVisiblePosition() + " last: " + lView.getLastVisiblePosition());

                lView.getChildAt(0).findViewById(R.id.echo).performClick();
                // Call updateecho a second time on the same question
                // Cannot do it by click anymore since click was dissable after one upvote
                mainActivity.updateEcho((String) lView.getChildAt(0).getTag());
            }
        });


        // Now click on one post to open it:
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lView.getChildAt(0).performClick();
            }
        });


        // get reference to the new expanded question activity
        getInstrumentation().waitForIdleSync();
        final ExpandedQuestionActivity expandedQuestionActivity = (ExpandedQuestionActivity) receiverExpandedQuestionActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("expandedQuestionActivity is null", expandedQuestionActivity);

        getInstrumentation().removeMonitor(receiverExpandedQuestionActivityMonitor);

        // Now write something in the field and send a reply:
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((EditText) expandedQuestionActivity.findViewById(R.id.replyInput)).requestFocus();
            }
        });
        getInstrumentation().sendStringSync("This is a reply from testing!");


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expandedQuestionActivity.findViewById(R.id.sendButton).performClick();
            }
        });


        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testEmptyRoomName()
    {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        Instrumentation.ActivityMonitor receiverExpandedQuestionActivityMonitor = getInstrumentation()
                .addMonitor(ExpandedQuestionActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        getInstrumentation().sendStringSync("newAll");
        getInstrumentation().waitForIdleSync();


        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        final MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        /*
        //Read the message received by ReceiverActivity
        final TextView receivedMessage = (TextView) mainActivity
                .findViewById(R.id.received_message_text_view);
        //Verify that received message is correct
        assertNotNull(receivedMessage);
        assertEquals("Wrong received message", TEST_MESSAGE, receivedMessage.getText().toString());
        */

        Intent intent = mainActivity.getIntent();
        assertNotNull("Intent should be set", intent);

        assertEquals("newAll", intent.getStringExtra(JoinActivity.ROOM_NAME));

        assertEquals("This is set correctly", "Room name: newAll", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);


        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final ListView lView = mainActivity.getListView();

        // Upvote one posts
        final String[] theKey = new String[1];
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("devMainActivityTest", "First: " + lView.getFirstVisiblePosition() + " last: " + lView.getLastVisiblePosition());

                lView.getChildAt(0).findViewById(R.id.echo).performClick();
                // Call updateecho a second time on the same question
                // Cannot do it by click anymore since click was dissable after one upvote
                mainActivity.updateEcho((String) lView.getChildAt(0).getTag());
            }
        });


        // Set roomname to empty
        intent.putExtra(MainActivity.ROOM_NAME, (String) null);


        // Now click on one post to open it:
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lView.getChildAt(0).performClick();
            }
        });


        // get reference to the new expanded question activity
        getInstrumentation().waitForIdleSync();
        final ExpandedQuestionActivity expandedQuestionActivity = (ExpandedQuestionActivity) receiverExpandedQuestionActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("expandedQuestionActivity is null", expandedQuestionActivity);

        getInstrumentation().removeMonitor(receiverExpandedQuestionActivityMonitor);
    }


    public void testEmptyRoomName2()
    {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        Instrumentation.ActivityMonitor receiverExpandedQuestionActivityMonitor = getInstrumentation()
                .addMonitor(ExpandedQuestionActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        getInstrumentation().sendStringSync("newAll");
        getInstrumentation().waitForIdleSync();


        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        final MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        /*
        //Read the message received by ReceiverActivity
        final TextView receivedMessage = (TextView) mainActivity
                .findViewById(R.id.received_message_text_view);
        //Verify that received message is correct
        assertNotNull(receivedMessage);
        assertEquals("Wrong received message", TEST_MESSAGE, receivedMessage.getText().toString());
        */

        Intent intent = mainActivity.getIntent();
        assertNotNull("Intent should be set", intent);

        assertEquals("newAll", intent.getStringExtra(JoinActivity.ROOM_NAME));

        assertEquals("This is set correctly", "Room name: newAll", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);


        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final ListView lView = mainActivity.getListView();

        // Upvote one posts
        final String[] theKey = new String[1];
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("devMainActivityTest", "First: " + lView.getFirstVisiblePosition() + " last: " + lView.getLastVisiblePosition());

                lView.getChildAt(0).findViewById(R.id.echo).performClick();
                // Call updateecho a second time on the same question
                // Cannot do it by click anymore since click was dissable after one upvote
                mainActivity.updateEcho((String) lView.getChildAt(0).getTag());
            }
        });


        // Set roomname to empty
        intent.putExtra(MainActivity.ROOM_NAME, "");


        // Now click on one post to open it:
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lView.getChildAt(0).performClick();
            }
        });


        // get reference to the new expanded question activity
        getInstrumentation().waitForIdleSync();
        final ExpandedQuestionActivity expandedQuestionActivity = (ExpandedQuestionActivity) receiverExpandedQuestionActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);

        assertNotNull("expandedQuestionActivity is null", expandedQuestionActivity);

        getInstrumentation().removeMonitor(receiverExpandedQuestionActivityMonitor);
    }


}
