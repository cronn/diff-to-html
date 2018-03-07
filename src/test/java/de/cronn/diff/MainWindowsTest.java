package de.cronn.diff;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cronn.diff.util.OS;

public class MainWindowsTest extends MainTestBase {

	OS osBackup;
	
	@Override
	@Before
	public void setUp() throws IOException {
		super.setUp();
		osBackup = Main.os;
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		Main.os = osBackup;
	}
	
	@Test
	public void testMainNoArgsOnWindows_noOsDiffOption() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.os = OS.WINDOWS;
		Main.main(new String[] {});
	}
}
