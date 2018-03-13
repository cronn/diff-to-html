package de.cronn.diff;

import static de.cronn.diff.util.cli.CliParser.OPT_IGNORE_LINE_ENDINGS;
import static de.cronn.diff.util.cli.CliParser.OPT_OS_DIFF;
import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainLineEndingsTest extends MainTestBase {

	private static final String TEXT_LINE_ENDINGS_LF = TEST_DATA_OUTPUT_DIR + "textWithLineEndingsLF";
	private static final String TEXT_LINE_ENDINGS_CRLF = TEST_DATA_OUTPUT_DIR + "textWithLineEndingsCRLF";

	@BeforeClass
	public static void createTestInput() throws IOException {
		createFileWithLineEndings(LF, TEXT_LINE_ENDINGS_LF);
		createFileWithLineEndings(CR + LF, TEXT_LINE_ENDINGS_CRLF);
	}

	@Test
	public void testMainJavaDiffDirsToHtmlIgnoreLineEndings_True() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { TEXT_LINE_ENDINGS_LF, TEXT_LINE_ENDINGS_CRLF, getOutHtmlFilePath(), "-" + OPT_IGNORE_LINE_ENDINGS });
	}

	@Test
	public void testMainJavaDiffDirsToHtmlIgnoreLineEndings_False() throws Exception {
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { TEXT_LINE_ENDINGS_LF, TEXT_LINE_ENDINGS_CRLF, getOutHtmlFilePath() });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreLineEndings_True() throws Exception {
		assumeFalse(SystemUtils.IS_OS_WINDOWS);
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_OK);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { TEXT_LINE_ENDINGS_LF, TEXT_LINE_ENDINGS_CRLF, getOutHtmlFilePath(), "-" + OPT_OS_DIFF,
				"-" + OPT_IGNORE_LINE_ENDINGS });
	}

	@Test
	public void testMainOSDiffDirsToHtmlIgnoreLineEndings_False() throws Exception {
		assumeFalse(SystemUtils.IS_OS_WINDOWS);
		exit.expectSystemExitWithStatus(Main.EXIT_CODE_ERROR);
		exit.checkAssertionAfterwards(new DefaultAssertion());
		Main.main(new String[] { TEXT_LINE_ENDINGS_LF, TEXT_LINE_ENDINGS_CRLF, getOutHtmlFilePath(), "-" + OPT_OS_DIFF });
	}

	protected static void createFileWithLineEndings(String lineEndings, String pathToFile) throws IOException {
		if (!Files.exists(Paths.get(pathToFile))) {
			Files.write(Paths.get(pathToFile), ("Some text. " + lineEndings + "Some more text." + lineEndings).getBytes());
		}
	}
}
