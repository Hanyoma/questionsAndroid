package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;
import android.text.Html;
import android.util.Log;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;
import java.lang.reflect.Field;
import java.util.Date;


/**
 * Created by hunkim on 7/15/15.
 */

public class QuestionTest extends TestCase {
    Question q;


    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("The title is this", "Hello? This is very nice");
    }

    @SmallTest
    public void testChatFirstString() {
        String[] strHead = {
                "Hello? This is very nice", "Hello?",
                "This is cool! Really?", "This is cool!",
                "How.about.this? Cool", "How.about.this?",
                "This no terminal symbol","This no terminal symbol",
                "This. is? some. sentence! ", "This."
        };

        for (int i = 0; i < strHead.length; i += 2) {
            String head = q.getFirstSentence(strHead[i]);
            assertEquals("Chat.getFirstSentence", strHead[i + 1], head);
        }
    }

    @SmallTest
    public void testHead() {
        assertEquals("Head", "The title is this", q.getHead());
    }

<<<<<<< HEAD
    @SmallTest
    public void testSanitation() {
=======
    public void testSanitation()
    {
        String inputTitle = "<h1> Title </h1>";
        String inputDesc = "and the <p> description </p>";
>>>>>>> master
        // Checks that new questions inputs are automatically sanitized.
        Question htmlQ = new Question(inputTitle, inputDesc);
        assertEquals("HTMLSanitationTitle", htmlQ.getHead(), Html.escapeHtml(inputTitle));
        assertEquals("HTMLSanitationDesc", htmlQ.getDesc(), Html.escapeHtml(inputDesc));
    }


    @SmallTest
    public void testComparisonBetweenQuestions(){
        Question q1 = new Question("AAA", "AAA");
        Question q2 = new Question("AAA", "AAA");

        Class<?> secretClass1 = q1.getClass();
        Class<?> secretClass2 = q2.getClass();

        // Make sure timestampts are the same for beginning:
//        Log.i("dev","\n1: " + q1.getTimestamp() + "\nq2: " + q2.getTimestamp());


        try {
            Field field1 = secretClass1.getDeclaredField("timestamp");
            field1.setAccessible(true);
            Field field2 = secretClass2.getDeclaredField("timestamp");
            field2.setAccessible(true);
            try {
                field1.set(q1, field2.get(q2));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

//        Log.i("dev","\n1: " + q1.getTimestamp() + "\nq2: " + q2.getTimestamp());

        // Now compare them
        assertEquals("Comparison1", 0, q1.compareTo(q2));

        // Now change timestamp of one:
        try {
            Field field1 = secretClass1.getDeclaredField("timestamp");
            field1.setAccessible(true);
            try {
//                Object a = field1.get(q1);
//                long currentTimestamp = Long.valueOf(String.valueOf(a));
                field1.set(q1, new Date().getTime() - 1000);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // q1 is now older than q2
        assertEquals("Comparison2", -1, q1.compareTo(q2));
        assertEquals("Comparison3", 1, q2.compareTo(q1));

        //Make one into new question and other not by changing timestamp of one to be old
        try {
            Field field1 = secretClass1.getDeclaredField("timestamp");
            field1.setAccessible(true);
            try {
                field1.set(q1, 2000);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        assertEquals("Comparison4", -1, q1.compareTo(q2));
        assertEquals("Comparison5", 1, q2.compareTo(q1));
    }


    @SmallTest
    public void testComparisonBetweenQuestions2(){
        Question q1 = new Question("AAA", "AAA");
        Question q2 = new Question("AAA", "AAA");

        Class<?> secretClass1 = q1.getClass();
        Class<?> secretClass2 = q2.getClass();


        // Now change echo of one:
        try {
            Field field1 = secretClass1.getDeclaredField("echo");
            field1.setAccessible(true);
            try {
                field1.set(q1, 100);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        // q1 has echo 100, q2 has echo 0
        assertEquals("Comparison1", 100, q1.compareTo(q2));
        assertEquals("Comparison2", -100, q2.compareTo(q1));
    }


    @SmallTest
    public void testHashCode()
    {
//        Class<?> secretClass = q.getClass();
        String theRealKey = "a_value";
//        try {
//            Field field = secretClass.getDeclaredField("key");
//            field.setAccessible(true);
//            try {
//                field.set(q, theRealKey);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        q.setKey(theRealKey);
//        Log.i("dev", String.valueOf(q.hashCode()) + " real: " + theRealKey);
        assertEquals("hashCode: ", theRealKey, q.hashCode());

//
//        // Try it with null as key value
//        try {
//            Field field = secretClass.getDeclaredField("key");
//            field.setAccessible(true);
//            try {
//                field.set(q, null);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        q.setKey(null);
        assertEquals("hashCode: ", 0, q.hashCode());
    }

    @SmallTest
    public void testEquals()
    {
        assertFalse(q.equals("this is not a Question"));
    }

}
