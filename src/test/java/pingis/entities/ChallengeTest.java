package pingis.entities;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ChallengeTest {

  Challenge calculatorChallenge;
  Challenge protectedChallenge;
  private User authorUser;
  private static final int TMC_USER_LEVEL = 100;

  @Before
  public void setUp() {
    authorUser = new User(1, "ModelUser", TMC_USER_LEVEL);
    calculatorChallenge = new Challenge("Calculator", authorUser, "Calculator description");
    protectedChallenge = new Challenge();
  }

  @Test
  public void testChallengeToString() {
    assertEquals("Calculator: Calculator description", calculatorChallenge.toString());
  }

}
