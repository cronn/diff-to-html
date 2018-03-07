package de.cronn.diff;

import org.junit.Test;

import de.cronn.diff.util.OS;

public class MainWindowsTest extends MainTestBase {

	@Test
	public void testMainNoArgsOnWindows_noOsDiffOption() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.os = OS.WINDOWS;
		Main.main(new String[] {});
	}
}
