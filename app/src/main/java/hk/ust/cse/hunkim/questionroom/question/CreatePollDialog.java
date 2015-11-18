package hk.ust.cse.hunkim.questionroom.question;

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
    public List<String> pollOptions;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_poll_dialog, null);
        userInput = (ListView) dialogView.findViewById(R.id.pollListView);

        pollOptions = new ArrayList<String>();
        final PollListAdapter pollAdapter =
                new PollListAdapter(getActivity().getApplicationContext(), R.layout.poll_entry, pollOptions );

        userInput.setAdapter(pollAdapter);

        // Create 2 blank lines for users to add poll options
        pollOptions.add("");
        pollOptions.add("");
        pollAdapter.notifyDataSetChanged();

        // Button that adds a row to the list view when pressed
        Button addRow = (Button) dialogView.findViewById(R.id.pollAddRowButton);
        addRow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pollOptions.add("");
                userInput.requestFocus();
                pollAdapter.notifyDataSetChanged();
            }

        });


        // The dialog window, either create poll from input or cancel
        builder.setView(dialogView)
                .setPositiveButton("Create Poll", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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
