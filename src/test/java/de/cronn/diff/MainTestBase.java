package de.cronn.diff;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.contrib.java.lang.system.Assertion;

import de.cronn.diff.util.FileHelper;

public class MainTestBase extends TestBase {

	static final String INPUT_DIR_1 = TEST_DATA_INPUT_DIR + "dir1_1";
	static final String INPUT_DIR_3 = TEST_DATA_INPUT_DIR + "dir1_3";
	static final String INPUT_DIR_1_DOUBLE_SLASH = TEST_DATA_INPUT_DIR + "/dir1_1";
	static final String INPUT_DIR_3_TRIPPLE_SLASH = TEST_DATA_INPUT_DIR + "//dir1_3";
	static final String INPUT_CODE_3_1 = TEST_DATA_INPUT_DIR + "code3_1.java.example";
	static final String INPUT_CODE_3_2 = TEST_DATA_INPUT_DIR + "code3_2.java.example";
	static final String INPUT_TEXT_WITH_SPACES_1 = TEST_DATA_INPUT_DIR + "textWithSpaces1";
	static final String INPUT_TEXT_WITH_SPACES_2 = TEST_DATA_INPUT_DIR + "textWithSpaces2";
	static final String INPUT_TEXT_WITHOUT_SPACES = TEST_DATA_INPUT_DIR + "textWithoutSpaces";
	static final String INPUT_DIR_WITH_SPACES_1 = TEST_DATA_INPUT_DIR + "dirWithSpaces1";
	static final String INPUT_DIR_WITH_SPACES_2 = TEST_DATA_INPUT_DIR + "dirWithSpaces2";
	static final String INPUT_DIR_WITHOUT_SPACES = TEST_DATA_INPUT_DIR + "dirWithoutSpaces";
	static final String INPUT_TEXT_1_1 = TEST_DATA_INPUT_DIR + "text1_1.example";
	static final String INPUT_TEXT_1_2 = TEST_DATA_INPUT_DIR + "text1_2.example";
	static final String SMALL_FILE = TEST_DATA_INPUT_DIR + "smallfile";
	static final String BIG_FILE = TEST_DATA_INPUT_DIR + "bigfile";
	static final String SMALL_DIR = TEST_DATA_INPUT_DIR  + "smalldir";
	static final String BIG_DIR = TEST_DATA_INPUT_DIR + "bigdir";
	static final String MEDIUM_DIR = TEST_DATA_INPUT_DIR + "mediumdir";

	private final ByteArrayOutputStream sysErr = new ByteArrayOutputStream();
	private final ByteArrayOutputStream sysOut = new ByteArrayOutputStream();

	@Override
	@Before
	public void setUp() {
		super.setUp();
		System.setErr(new PrintStream(sysErr));
		System.setOut(new PrintStream(sysOut));
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		resetOutAndErrorStream();
	}

	private void resetOutAndErrorStream() {
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}

	class DefaultAssertion implements Assertion {
		@Override
		public void checkAssertion() throws Exception {
			assertSystemMessage();
			assertOutputEqualToValidation();
		}
	}

	class SystemMessageAssertion implements Assertion {
		@Override
		public void checkAssertion() throws Exception {
			assertSystemMessage();
		}
	}

	void assertExceptionAndSystemMessage(String[] args, Class<?> clazz) throws IOException {
		try {
			Main.main(args);
		} catch (Exception e) {
			assertThat(e.getClass()).isEqualTo(clazz);
			assertSystemMessage();
		}
	}

	private void assertSystemMessage() throws IOException {
		String sysOutStr = FileHelper.normalizeLineSeparators(sysOut.toString());
		String sysErrorStr = FileHelper.normalizeLineSeparators(sysErr.toString());
		assertThat(sysOutStr).isNotNull();
		assertThat(sysErrorStr).isNotNull();
		assertSysOutErrEqualToValidation(sysErrorStr + System.lineSeparator() + sysOutStr);
	}
}
