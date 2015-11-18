package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.question.PollQuestion;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by Nick on 11/17/2015.
 */
public class PollListAdapter extends ArrayAdapter
{
    public PollListAdapter(Context context, int resource, List<String> items)
    {
        super(context, resource, items);

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View cView = convertView;

        if(cView == null)
        {
            cView = LayoutInflater.from(getContext()).inflate(R.layout.poll_entry, null);
        }

        String pollString = getItem(position).toString();

        if(pollString != null)
        {
            EditText pollEditText = (EditText) cView.findViewById(R.id.pollString);
        }

        return cView;
    }

}
