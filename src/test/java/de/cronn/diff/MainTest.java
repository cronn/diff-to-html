package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_SPACE_CHANGE;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_UNIQUE_FILES;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_WHITESPACES;
import static de.cronn.diff.util.cli.CliParser.OPT_ONLY_REPORTS;
import static de.cronn.diff.util.cli.CliParser.OPT_UNIFIED_CONTEXT;

import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

public class MainTest extends MainTestBase {

	@Test
	public void testMainWrongNumberOfArgs1() throws Exception {
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		assertExceptionAndSystemMessage(new String[] {}, MissingArgumentException.class);
	}

	@Test
	public void testMainWrongNumberOfArgs2() throws Exception {
		exit.checkAssertionAfterwards(new SystemMessageAssertion());
		assertExceptionAndSystemMessage(new String[] { "onlyOneArg" }, MissingArgumentException.class);
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
	public void testMainJAVADiffFilesToHtml() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlUnifiedContext16() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_1_1, INPUT_TEXT_1_2, getOutHtmlFilePath(),
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
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITH_SPACES_2, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_IGNORE_WHITESPACES });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlIgnoreWhiteSpaces_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITHOUT_SPACES, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffFilesToHtmlOnlyReports() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath(), "-" + OPT_ONLY_REPORTS });
	}
	
	@Test
	public void testMainJAVADiffFilesToHtml_pathWithMultipleSlashes() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_CODE_3_1, INPUT_CODE_3_2, getOutHtmlFilePath() });
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
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITH_SPACES_2, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_IGNORE_WHITESPACES });
	}

	@Test
	public void testMainJAVADiffDirsToHtmlIgnoreWhiteSpaces_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITHOUT_SPACES, getOutHtmlFilePath() });
	}

	@Test
	public void testMainJAVADiffDirsToHtml_pathWithMultipleSlashes() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_1_DOUBLE_SLASH, INPUT_DIR_3_TRIPPLE_SLASH, getOutHtmlFilePath() });
	}

}
