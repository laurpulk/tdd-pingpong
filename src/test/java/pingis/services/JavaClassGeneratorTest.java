
package pingis.services;

import pingis.utils.JavaClassGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pingis.entities.Challenge;
import pingis.entities.Task;
import pingis.entities.User;

public class JavaClassGeneratorTest {
    
    private Challenge challenge1;
    private Task task1;
    private Task task2;
    private Task task3;
    private User authorUser;
    private static final int TMC_USER_LEVEL = 100;
    
    private JavaClassGenerator JCGenerator;
    private final int Challenge1Level = 3;
    
    public JavaClassGeneratorTest() {
        challenge1 = new Challenge("Immutable Calculator", authorUser, "Amazing immutable calculator.",
                "src/ImmutableCalculator.java",
                "test/ImmutableCalculatorTest.java");
        JCGenerator = new JavaClassGenerator();
        User testUser = new User(new Random().nextLong(), "Test_userfirst", Challenge1Level);
        
        task1 = new Task(0, testUser, "testAddition",
                "test addition of two integers, return single value", 
                "@Test\npublic void testAddition() {\n\t//TODO: implement this\n\n}", 
                Challenge1Level, 0);
        
        task2 = new Task(1, testUser, "testSubstraction",
                "test substraction of two integers, return single value", 
                "@Test\npublic void testSubstraction() {\n\t//TODO: implement this\n\n}", 
                Challenge1Level, 0);
        
        task3 = new Task(2, testUser, "testMultiplication",
                "test multiplication of two integers, return single value", 
                "@Test\npublic void testMultiplication() {\n\t//TODO: implement this\n\n}", 
                Challenge1Level, 0);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testParseChallengeWithOneTask() {
        String parsedChallenge1Task1 = "\npublic class ImmutableCalculatorTest {\n"
                + "\n"
                + "	@Test\n"
                + "	public void testAddition() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "\n"
                + "}";
        
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);
        
        assertEquals(parsedChallenge1Task1, JCGenerator.generateChallenge(challenge1, tasks));
    }
    
    @Test
    public void testParseChallengeWithTwoTasks() {
        String parsedChallenge1TwoTasks = "\npublic class ImmutableCalculatorTest {\n"
                + "\n"
                + "	@Test\n"
                + "	public void testAddition() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "	@Test\n"
                + "	public void testSubstraction() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "\n"
                + "}";
        
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);
        tasks.add(task2);
 
        assertEquals(parsedChallenge1TwoTasks, JCGenerator.generateChallenge(challenge1, tasks));
    }
    
    @Test
    public void testParseChallengeWithThreeTasks() {
        String parsedChallenge1TwoTasks = "\npublic class ImmutableCalculatorTest {\n"
                + "\n"
                + "	@Test\n"
                + "	public void testAddition() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "	@Test\n"
                + "	public void testSubstraction() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "	@Test\n"
                + "	public void testMultiplication() {\n"
                + "		//TODO: implement this\n"
                + "	\n"
                + "	}\n"
                + "\n"
                + "\n"
                + "}";
        
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        assertEquals(parsedChallenge1TwoTasks, JCGenerator.generateChallenge(challenge1, tasks));
    }
    
}
