package de.cronn.diff.impl.os.converter;

import java.io.IOException;

import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.impl.os.converter.UnixFileDiffToHtmlConverter;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class UnixFileDiffToHtmlConverterTest extends TestBase {

	private static final String CODE1_UNIX_DIFF = "code1.unix.diff";
	private static final String CODE2_UNIX_DIFF = "code2.unix.diff";
	private static final String TEXT1_UNIX_DIFF = "text1.unix.diff";
	private static final String BINARY_DIFFER_UNIX_DIFF_OLD_VERSION = "binaryFile_differ_oldDiffVersion.unix.diff";

	UnixFileDiffToHtmlConverter converter = new UnixFileDiffToHtmlConverter();

	@Test
	public void testConvertDiffToHtmlCode1() throws Exception {
		assertDiff2HtmlConversion(CODE1_UNIX_DIFF);
	}

	@Test
	public void testConvertDiffToHtmlCode2() throws Exception {
		assertDiff2HtmlConversion(CODE2_UNIX_DIFF);
	}

	@Test
	public void testConvertDiffToHtmlText1() throws Exception {
		assertDiff2HtmlConversion(TEXT1_UNIX_DIFF);
	}

	@Test
	public void testConvertDiffToHtmlBinariesDifferOldUnixDiffVersion() throws Exception {
		assertDiff2HtmlConversion(BINARY_DIFFER_UNIX_DIFF_OLD_VERSION);
	}

	private void assertDiff2HtmlConversion(String diffFile) throws IOException {
		String diff = readInputFile(diffFile);
		DiffToHtmlParameters params = DiffToHtmlParameters.builder()
				.withDiffType(DiffType.FILES)
				.withInputLeftPath("")
				.withInputRightPath("")
				.withOutputPath(TEST_DATA_OUTPUT_DIR)
				.build();
		String html = converter.convertDiffToHtml(diff, params);
		assertHtmlResultEqualToValidation(html);
	}

}
