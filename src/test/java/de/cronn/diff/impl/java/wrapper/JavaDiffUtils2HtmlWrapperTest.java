package de.cronn.diff.impl.java.wrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlRuntimeException;

public class JavaDiffUtils2HtmlWrapperTest {

	private static final String UTF_8_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf8textfile";

	private static final String UTF_16_BE_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf16betextfile";

	private static final String UTF_16_LE_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf16letextfile";

	private static final String ISO_8859_FILE = TestBase.TEST_DATA_INPUT_DIR + "iso88591textfile";

	@Test
	public void testAppendDiffToBuilder_Exception_ISO_8859_1() {
		// on windows the default charset is windows-1252 which is close to ISO 8859
		assumeThat(SystemUtils.IS_OS_WINDOWS).isFalse();

		DiffToHtmlParameters paramWithDetectEncoding = DiffToHtmlParameters.builder()
				.withInputLeftPath(ISO_8859_FILE)
				.withInputRightPath(ISO_8859_FILE)
				.build();

		assertThatExceptionOfType(DiffToHtmlRuntimeException.class)
				.isThrownBy(() -> new JavaDiffUtils2HtmlWrapper()
						.appendDiffToBuilder(new FileDiffHtmlBuilder(paramWithDetectEncoding), paramWithDetectEncoding))
				.withCauseExactlyInstanceOf(MalformedInputException.class);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_8() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_8_FILE);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_16_BE() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_16_BE_FILE);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_16_LE() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_16_LE_FILE);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_ISO_8859_1() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(ISO_8859_FILE);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	private void assertNoExceptionReadAllLines(DiffToHtmlParameters params) throws IOException {
		FileDiffHtmlBuilder htmlBuilder = new JavaDiffUtils2HtmlWrapper()
				.appendDiffToBuilder(new FileDiffHtmlBuilder(params), params);
		assertThat(htmlBuilder.toString()).isNotNull();
	}

	private DiffToHtmlParameters createParams(String filePath) {
		return DiffToHtmlParameters.builder()
				.withDetectTextFileEncoding(true)
				.withInputLeftPath(filePath)
				.withInputRightPath(filePath)
				.build();
	}
}
