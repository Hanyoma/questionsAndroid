package hk.ust.cse.hunkim.questionroom.question;

import java.util.ArrayList;

/**
 * Created by Nick on 11/7/2015.
 */
public class QuestionPoll extends Question
{

    private ArrayList <Poll> myPollOptions;

    private class Poll
    {
        private String myTitle;
        private int myVotes;

        private Poll(String optionTitle)
        {
            myVotes = 0;
            myTitle = optionTitle;
        }

        public String getTitle()
        {
            return myTitle;
        }

        public int getVotes()
        {
            return myVotes;
        }

        public void addVote()
        {
            myVotes++;
        }
    }

    private QuestionPoll(String title, String message, String[] pollOptions)
    {
        super(title, message);
        for (String option : pollOptions)
        {
            Poll newPoll = new Poll(option);
            myPollOptions.add(newPoll);
        }
    }

}
