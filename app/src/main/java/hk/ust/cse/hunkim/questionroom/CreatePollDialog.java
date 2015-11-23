package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import hk.ust.cse.hunkim.questionroom.JoinActivity;
import hk.ust.cse.hunkim.questionroom.MainActivity;
import hk.ust.cse.hunkim.questionroom.PollListAdapter;
import hk.ust.cse.hunkim.questionroom.R;

/**
 * Created by Nick on 11/7/2015.
 */
public abstract class CreatePollDialog extends DialogFragment {

    private ListView userInput;
    private LayoutInflater inflater;
    private View dialogView;
    private PollListAdapter pollAdapter;
    public List<String> pollOptions;
    public List<EditText> pollEditTexts;
    public String pollTitle;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.create_poll_dialog, null);
        userInput = (ListView) dialogView.findViewById(R.id.pollListView);
        pollOptions = new ArrayList<String>();
        pollEditTexts = new ArrayList<EditText>();
        pollAdapter =
                new PollListAdapter(getActivity().getApplicationContext(),
                        R.layout.poll_entry, pollEditTexts);
        userInput.setAdapter(pollAdapter);

        // Create 2 blank lines for users to add poll options
        final EditText userOption1 = new EditText(getActivity().getApplicationContext());
        final EditText userOption2 = new EditText(getActivity().getApplicationContext());
        pollEditTexts.add(userOption1);
        pollEditTexts.add(userOption2);
        pollAdapter.notifyDataSetChanged();

        // Button that adds a row to the list view when pressed
        final Button addRow = (Button) dialogView.findViewById(R.id.pollAddRowButton);
        addRow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText userOptionExtra = new EditText(getActivity().getApplicationContext());
                pollEditTexts.add(userOptionExtra);
                pollAdapter.notifyDataSetChanged();
            }

        });


        // The dialog window, either create poll from input or cancel
        builder.setView(dialogView)
                .setPositiveButton("Create Poll", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Get the pollTitle for later use.
                        EditText editTitle = (EditText) dialogView.findViewById(R.id.pollTitle);
                        pollTitle = editTitle.getText().toString();

                        // Add each user input option to the pollOptions list.
                        for (int i = 0; i < userInput.getChildCount(); i++) {
                            View v = userInput.getChildAt(i);
                            EditText pollEditInput = (EditText) v.findViewById(R.id.pollString);
                            // Only accept user input if its valid (not empty).
                            if (pollEditInput != null && !pollEditInput.getText().toString().equals(""))
                            {
                                pollOptions.add(pollEditInput.getText().toString());
                            }
                        }
                        onPositiveButtonClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public abstract void onPositiveButtonClick();
}

