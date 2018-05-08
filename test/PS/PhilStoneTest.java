package PS;

import static org.junit.Assert.*;

import org.junit.Test;

public class PhilStoneTest {

	@Test
	public void testMutex() {
		String filename = "src/tests/mutex/mutex2.spec";
		String [] args = {"-scope=6", "-pdf", filename};
		PhilStone.main(args);
	}
	
	@Test
	public void testBackery() {
		String filename = "src/tests/bakery/bakery.spec";
		String [] args = {"-scope=6", "-pdf", filename};
		PhilStone.main(args);
	}
	
	@Test
	public void testPeterson() {
		String filename = "src/tests/peterson/peterson.spec";
		String [] args = {"-scope=6", "-pdf", filename};
		PhilStone.main(args);
	}

	
	@Test
	public void testReaderWriter() {
		String filename = "src/tests/readerwriter/reader2writer3.spec";
		String [] args = {"-scope=14", "-pdf", filename};
		PhilStone.main(args);
	}
	@Test
	public void testPhilLiveness() {
		String filename = "src/tests/phils/phil-liveness.spec";
		String [] args = {"-scope=14", "-pdf", filename};
		PhilStone.main(args);
	}
}
