package de.cronn.diff.impl.os.converter;

import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.impl.os.converter.UnixDirectoryDiffToHtmlConverter;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class UnixDirectoryDiffToHtmlConverterTest extends TestBase {

	private static final String INPUT_DIR_1_2 = TEST_DATA_INPUT_DIR + "dir1_2";

	private static final String INPUT_DIR_1_1 = TEST_DATA_INPUT_DIR + "dir1_1";

	private static final String DIR1_UNIX_DIFF = "dir1.diff";

	UnixDirectoryDiffToHtmlConverter converter = new UnixDirectoryDiffToHtmlConverter();

	@Test
	public void testConvertDiffToHtmlDirectory1() throws Exception {
		String diff = readInputFile(DIR1_UNIX_DIFF);
		DiffToHtmlParameters params = createMockParams();

		String html = converter.convertDiffToHtml(diff, params);
		assertHtmlResultEqualToValidation(html);
	}

	private DiffToHtmlParameters createMockParams() {
		DiffToHtmlParameters params = DiffToHtmlParameters.builder()
				.withDiffType(DiffType.DIRECTORIES)
				.withInputLeftPath(INPUT_DIR_1_1)
				.withInputRightPath(INPUT_DIR_1_2)
				.withOutputPath(TEST_DATA_OUTPUT_DIR)
				.withDiffCommandLineAsString("diff -u -r")
				.build();
		return params;
	}
}
