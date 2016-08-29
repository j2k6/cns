package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({NickCommandTest.class, HelpCommandTest.class, CountUsersCommandTest.class, ChatCommandProcessorTest.class})
public class AllTests {

}
