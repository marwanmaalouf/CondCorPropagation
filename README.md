# Coincidental Correctness Propagation

## Set up
1. Edit the path to the jar file to profile in the issta_jar or the path to the class file in issta.bat
2. Run issta.bat
3. Output generated in instrumented directory


## Set up with JUnit 4 test cases
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.Rule;

import com.issta.*;

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
	}

	@Test
	public final void test...

}



