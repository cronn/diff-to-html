package de.cronn.diff;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainNoOutputTest extends MainTestBase {

	private String workingDirBkp;

	@Before
	public void backupMainWorkingDir() {
		workingDirBkp = Main.workingDir;
	}
	
	@After 
	public void resetMainWorkingDir() {
		Main.workingDir = workingDirBkp;
	}
	
	@Test
	public void testMainJAVADiffDirsToHtml_noOutputProvided() throws Exception {		
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.workingDir = TestBase.TEST_DATA_OUTPUT_DIR;
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_1 });
	}
	
}
