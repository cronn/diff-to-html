package de.cronn.diff;

import org.apache.commons.cli.AmbiguousOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static de.cronn.diff.util.cli.CliParser.*;

public class MainTest extends MainTestBase {

	private static final String EMPTYDIR = TEST_DATA_INPUT_DIR + "emptyDir";

	@Before
	public void setup() throws IOException{
		Files.createDirectory(Paths.get(EMPTYDIR));
	}

	@After
	public void teardown() throws IOException {
		Files.deleteIfExists(Paths.get(EMPTYDIR));
	}

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
	public void testMainJAVADiffFilesToHtml_maxFileSizeDifferenceTooBig_true() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { SMALL_FILE, BIG_FILE, getOutHtmlFilePath(), "-" + OPT_MAX_ALLOWED_FILESIZE_DIFFERENCE, "5" });
	}

	@Test
	public void testMainJAVADiffFilesToHtml_maxFileSizeDifferenceTooBig_false() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { SMALL_FILE, BIG_FILE, getOutHtmlFilePath(), "-" + OPT_MAX_ALLOWED_FILESIZE_DIFFERENCE, "101"});
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

	@Test
	public void testMainJAVADiffDirsToHtml_emptyLeftDir() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] {EMPTYDIR, INPUT_DIR_1, getOutHtmlFilePath()});
	}

	@Test
	public void testMainJAVADiffDirsToHtml_emptyRightDir() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] {INPUT_DIR_1, EMPTYDIR, getOutHtmlFilePath()});
	}

}
