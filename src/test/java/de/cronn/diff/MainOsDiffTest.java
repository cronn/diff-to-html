package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_SPACE_CHANGE;
import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_WHITESPACES;
import static de.cronn.diff.util.cli.CliParser.OPT_ONLY_REPORTS;
import static de.cronn.diff.util.cli.CliParser.OPT_OS_DIFF;
import static de.cronn.diff.util.cli.CliParser.OPT_UNIFIED_CONTEXT;
import static org.junit.Assume.assumeFalse;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;

public class MainOsDiffTest extends MainTestBase {

	@Before
	public void assertNotOnWindows() {
		assumeFalse(SystemUtils.IS_OS_WINDOWS);
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
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}
	
	@Test
	public void testMainOSDiffFilesToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_WHITESPACES });
	}

	@Test
	public void testMainOSDiffFilesToHtmlIgnoreWhiteSpaces_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_TEXT_WITH_SPACES_1, INPUT_TEXT_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_OS_DIFF  });
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
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_SPACE_CHANGE });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreSpaceChange_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITH_SPACES_2, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}
	
	@Test
	public void testMainOSDiffDirsToHtmlIgnoreWhiteSpaces_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_WHITESPACES });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreWhiteSpaces_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { INPUT_DIR_WITH_SPACES_1, INPUT_DIR_WITHOUT_SPACES, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

}
