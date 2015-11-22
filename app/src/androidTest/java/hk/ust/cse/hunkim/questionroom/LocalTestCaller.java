package hk.ust.cse.hunkim.questionroom;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * Created by M on 19-Nov-15.
 */
public class LocalTestCaller {

    @Test
    public void testLocal() throws Exception {
        Class<?> test = Class.forName("hk.ust.cse.hunkim.questionroom.QuestionTest");
        JUnitCore junit = new JUnitCore();
        junit.run(test);

//        Class<?> test2 = Class.forName("hk.ust.cse.hunkim.questionroom.NewTest");
//        JUnitCore junit2 = new JUnitCore();
//        junit2.run(test2);

    }
}