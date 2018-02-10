# Coincidental Correctness Propagation

## Info on files:
1. InstrumentSource_classes.bat: instrument source classes targetted, output to ./instrumented/classes
2. InstrumentSource_jar.bat: instrument source jar file targetted, output to ./instrumented/jar
3. InstrumentTest_classes.bat: instrument test classes to generate a profile, , output to ./instrumented/classes
4. InstrumentTest_jar.bat: instrument test jar to generate a profile, , output to ./instrumented/classes
5. build.bat: builds the profiler and instrumenter code
6. Check.bat: Print the bytecode of the targetted class



## Set up
1. Edit the path to the jar file to profile in the issta_jar or the path to the class file in run.bat
2. Run run.bat
3. Replace the original file with the generated files located in instrumented directory
4. Do the necessary changes in test.bat 
5. Run test.bat
6. Check instrumented/profile directory for the generated csv files


## Set up with JUnit 4 test cases
```
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.Rule;

import com.issta.Profiler;

public class TestExample1 {
	

	@Rule public TestName name = new TestName();


	@Before
	public void setUp() throws Exception {
		System.out.println("Before " + name.getMethodName() + " executed");
		Profiler.beforeTest(name.getMethodName());
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("After " + name.getMethodName() + " executed");
		Profiler.afterTest();
	}

	@Test
	public final void testFoo() {
		Example1.foo();
		assert...
	}

	@Test
	public final void test...

}
```
OR use run instrumentedTest_XXX.bat after changing the path to the test cases



