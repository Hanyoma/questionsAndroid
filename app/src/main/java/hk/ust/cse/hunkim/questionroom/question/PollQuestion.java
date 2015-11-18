package hk.ust.cse.hunkim.questionroom.question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 11/7/2015.
 */
public class PollQuestion extends Question
{
    public static class Poll
    {
        private String pollString;
        private int votes;

        private Poll()
        {
            super();
        }
        private Poll(String optionTitle)
        {
            votes = 0;
            pollString = optionTitle;
        }

        public String getPollString()
        {
            return pollString;
        }

        public int getVotes()
        {
            return votes;
        }

        public void addVote()
        {
            votes++;
        }
    }

    public PollQuestion(String title, String message, List<String> pollStrings)
    {
        super(title, message);
        pollOptions = new ArrayList<Poll>();
        for (String option : pollStrings)
        {
            Poll newPoll = new Poll(option);
            pollOptions.add(newPoll);
        }

    }
}
