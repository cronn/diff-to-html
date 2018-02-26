package de.cronn.diff.impl.java.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

import org.junit.Test;

import de.cronn.diff.TestBase;
import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;

public class JavaDiffUtils2HtmlWrapperTest {

	private static final String UTF_8_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf8textfile";

	private static final String UTF_16_BE_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf16betextfile";

	private static final String UTF_16_LE_FILE = TestBase.TEST_DATA_INPUT_DIR + "utf16letextfile";

	private static final String ISO_8859_FILE = TestBase.TEST_DATA_INPUT_DIR + "iso88591textfile";

	@Test
	public void testAppendDiffToBuilder_Exception_ISO_8859_1() throws Exception {
		try {
			DiffToHtmlParameters paramWithDetectEncoding = DiffToHtmlParameters.builder()
					.withInputLeftPath(ISO_8859_FILE)
					.withInputRightPath(ISO_8859_FILE)
					.build();
			new JavaDiffUtils2HtmlWrapper().appendDiffToBuilder(new FileDiffHtmlBuilder(paramWithDetectEncoding), paramWithDetectEncoding);
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getCause().getClass(), MalformedInputException.class);
		}
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_8() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_8_FILE, true);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_16_BE() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_16_BE_FILE, true);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_UTF_16_LE() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(UTF_16_LE_FILE, true);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	@Test
	public void testAppendDiffToBuilder_detectEncoding_OK_ISO_8859_1() throws Exception {
		DiffToHtmlParameters paramWithDetectEncoding = createParams(ISO_8859_FILE, true);
		assertNoExceptionReadAllLines(paramWithDetectEncoding);
	}

	private void assertNoExceptionReadAllLines(DiffToHtmlParameters params) throws IOException {
		FileDiffHtmlBuilder htmlBuilder = new JavaDiffUtils2HtmlWrapper().appendDiffToBuilder(new FileDiffHtmlBuilder(params), params);
		assertNotNull(htmlBuilder.toString());
	}

	private DiffToHtmlParameters createParams(String filePath, boolean detectEncoding) {
		DiffToHtmlParameters paramWithDetectEncoding = DiffToHtmlParameters.builder()
				.withDetectTextFileEncoding(detectEncoding)
				.withInputLeftPath(filePath)
				.withInputRightPath(filePath)
				.build();
		return paramWithDetectEncoding;
	}
}
