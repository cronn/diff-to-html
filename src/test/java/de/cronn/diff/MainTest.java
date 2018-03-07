package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_DETECT_ENCODING;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_SPACE_CHANGE;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_UNIQUE_FILES;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_WHITESPACES;
import static de.cronn.diff.util.cli.CliParser.OPT_ONLY_REPORTS;
import static de.cronn.diff.util.cli.CliParser.OPT_OS_DIFF;
import static de.cronn.diff.util.cli.CliParser.OPT_UNIFIED_CONTEXT;

import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

public class MainTest extends MainTestBase {

	@Test
	public void testMainWrongNumberOfArgs1() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.main(new String[] {});
	}

	@Test
	public void testMainWrongNumberOfArgs2() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.main(new String[] { "onlyOneArg" });
	}

	@Test
	public void testMainInvalidArgs() throws Exception {
		assertExceptionAndSystemMessage(new String[] { "one", "two", "three", "four" }, UnrecognizedOptionException.class);
	}

	@Test
	public void testMainInvalidOptionalArg() throws Exception {
		String[] args = new String[] { "", "", "", "--invalidOptionalArg" };
		assertExceptionAndSystemMessage(args, UnrecognizedOptionException.class);
	}

	@Test
	public void testMainWrongTypeOfInputs() throws Exception {
		String[] args = new String[] { INPUT_DIR_1, INPUT_CODE_3_1, "" };
		assertExceptionAndSystemMessage(args, AmbiguousOptionException.class);
	}

	@Test
	public void testMainOSDiffFilesToHtml() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

	@Test
	public void testMainOSDiffFilesToHtmlUnifiedContext10() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_1_1, INPUT_TEXT_1_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF, "-" + OPT_UNIFIED_CONTEXT + "10" });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIdentical() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_1, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIgnoreSpaceChange_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_LINUX_EOL, INPUT_TEXT_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_LINUX_EOL, INPUT_TEXT_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_DETECT_ENCODING });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_SPACES_1, INPUT_TEXT_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_WHITESPACES });
	}
	
	@Test
	public void testMainOSDiffFilesToHtmlOnlyReports() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_ONLY_REPORTS });
	}

	@Test
	public void testMainOSDiffDirsToHtml() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_3, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreUniqueFiles() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_3, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,  });
	}

	@Test
	public void testMainOSDiffIdenticalDirs() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_1, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreSpaceChange_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_LINUX_EOL, INPUT_DIR_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_LINUX_EOL, INPUT_DIR_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_DETECT_ENCODING });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_SPACES_1, INPUT_DIR_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_WHITESPACES });
	}
	
	@Test
	public void testMainJAVADiffFilesToHtml() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlUnifiedContext16() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_1_1, INPUT_TEXT_1_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF, 
				"-" + OPT_UNIFIED_CONTEXT + "16" });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIdentical() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_1, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreSpaceChange_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_LINUX_EOL, INPUT_TEXT_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_LINUX_EOL, INPUT_TEXT_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_DETECT_ENCODING });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_SPACES_1, INPUT_TEXT_SPACES_2, getOutHtmlFilePath(), "-" + OPT_IGNORE_WHITESPACES });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlOnlyReports() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath(), "-" + OPT_ONLY_REPORTS });
	}

	@Test
	public void testMainJAVADiffDirsToHtml() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_3, getOutHtmlFilePath() });
	}
	
	@Test
	public void testMainJAVADiffDirsToHtml_outputPathOnlyFilename() throws Exception {
		String outFilename = TEST_DATA_OUTPUT_DIR + "someOutputFilename.html";
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_1, outFilename });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreUniqueFiles() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_3, getOutHtmlFilePath(), "-" + OPT_IGNORE_UNIQUE_FILES });
	}

	@Test
	public void testMainJAVADiffIdenticalDirs() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1, INPUT_DIR_1, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreSpaceChange_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_LINUX_EOL, INPUT_DIR_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_LINUX_EOL, INPUT_DIR_MIXED_EOL, getOutHtmlFilePath(), "-" + OPT_DETECT_ENCODING });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_SPACES_1, INPUT_DIR_SPACES_2, getOutHtmlFilePath(), "-" + OPT_IGNORE_WHITESPACES });
	}
}
