package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;
import android.text.Html;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 */

public class QuestionTest  extends TestCase {
    Question q;



    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("The title is this","Hello? This is very nice");
    }

    @SmallTest

    public void testChatFirstString() {
        String[] strHead = {
                "Hello? This is very nice", "Hello?",
                "This is cool! Really?", "This is cool!",
                "How.about.this? Cool", "How.about.this?"
        };

        for (int i=0; i<strHead.length; i+=2) {
            String head = q.getFirstSentence(strHead[i]);
            assertEquals("Chat.getFirstSentence", strHead[i+1], head);
        }
    }

    @SmallTest

    public void testHead() {
        assertEquals("Head", "The title is this", q.getHead());
    }

    public void testSanitation()
    {
        // Checks that new questions inputs are automatically sanitized.
        Question htmlQ = new Question("<h1> Title </h1>", "and the <p> description </p>");
        assertEquals("HTMLSanitation", htmlQ.getHead(), Html.escapeHtml("<h1> Title </h1>"));
        assertEquals("HTMLSanitationDesc", htmlQ.getDesc(), Html.escapeHtml("and the <p> description </p>"));
    }

}
