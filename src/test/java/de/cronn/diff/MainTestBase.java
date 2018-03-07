package de.cronn.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.contrib.java.lang.system.Assertion;

import de.cronn.diff.util.OS;

public class MainTestBase extends TestBase {

	protected static final String INPUT_DIR_1 = TEST_DATA_INPUT_DIR + "dir1_1";
	protected static final String INPUT_DIR_3 = TEST_DATA_INPUT_DIR + "dir1_3";
	protected static final String INPUT_CODE_3_1 = TEST_DATA_INPUT_DIR + "code3_1.java.example";
	protected static final String INPUT_CODE_3_2 = TEST_DATA_INPUT_DIR + "code3_2.java.example";
	protected static final String INPUT_TEXT_LINUX_EOL = TEST_DATA_INPUT_DIR + "textWith.linuxEOL";
	protected static final String INPUT_TEXT_MIXED_EOL = TEST_DATA_INPUT_DIR + "textWith.mixedEOL";
	protected static final String INPUT_DIR_LINUX_EOL = TEST_DATA_INPUT_DIR + "dirLinuxEOL";
	protected static final String INPUT_DIR_MIXED_EOL = TEST_DATA_INPUT_DIR + "dirMixedEOL";
	protected static final String INPUT_TEXT_SPACES_1 = TEST_DATA_INPUT_DIR + "textWithSpaces1";
	protected static final String INPUT_TEXT_SPACES_2 = TEST_DATA_INPUT_DIR + "textWithSpaces2";
	protected static final String INPUT_DIR_SPACES_1 = TEST_DATA_INPUT_DIR + "dirWithSpaces1";
	protected static final String INPUT_DIR_SPACES_2 = TEST_DATA_INPUT_DIR + "dirWithSpaces2";
	protected static final String INPUT_TEXT_1_1 = TEST_DATA_INPUT_DIR + "text1_1.example";
	protected static final String INPUT_TEXT_1_2 = TEST_DATA_INPUT_DIR + "text1_2.example";
	
	private final ByteArrayOutputStream sysErr = new ByteArrayOutputStream();
	private final ByteArrayOutputStream sysOut = new ByteArrayOutputStream();
	
	private OS osBackup;

	@Override
	@Before
	public void setUp() throws IOException {
		super.setUp();
		System.setErr(new PrintStream(sysErr));
		System.setOut(new PrintStream(sysOut));
		osBackup = Main.os;
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		resetOutAndErrorStream();
		Main.os = osBackup;
	}
	
	private void resetOutAndErrorStream() {
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
	
	protected class DefaultAssertion implements Assertion {
		@Override
		public void checkAssertion() throws Exception {
			assertSystemMessage();
			assertOutputEqualToValidation();
		}
	}
	
	protected class SystemMessageAssertion implements Assertion {
		@Override
		public void checkAssertion() throws Exception {
			assertSystemMessage();
		}
	}
	
	protected void assertExceptionAndSystemMessage(String[] args, Class<?> clazz) throws IOException {
		try {
			Main.main(args);
		} catch (Exception e) {
			assertEquals(clazz, e.getClass());
			assertSystemMessage();
		}
	}

	private void assertSystemMessage() throws IOException {
		String sysOutStr = normalizeLineSeparators(sysOut.toString());
		String sysErrorStr = normalizeLineSeparators(sysErr.toString());
		assertNotNull(sysOutStr);
		assertNotNull(sysErrorStr);
		assertSysOutErrEqualToValidation(sysErrorStr + System.lineSeparator() + sysOutStr);
	}
}
