package de.cronn.diff;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlParameters.DiffType;

public class CronnDiffToHtmlTest extends TestBase {

	private static final String INPUT_CODE_1_1 = TEST_DATA_INPUT_DIR + "code1_1.java.example";
	private static final String INPUT_CODE_1_2 = TEST_DATA_INPUT_DIR + "code1_2.java.example";
	private static final String INPUT_CODE_2_1 = TEST_DATA_INPUT_DIR + "code2_1.java.example";
	private static final String INPUT_CODE_2_2 = TEST_DATA_INPUT_DIR + "code2_2.java.example";
	private static final String INPUT_TEXT_1_1 = TEST_DATA_INPUT_DIR + "text1_1.example";
	private static final String INPUT_TEXT_1_2 = TEST_DATA_INPUT_DIR + "text1_2.example";
	private static final String INPUT_BINARY_1_1 = TEST_DATA_INPUT_DIR + "binaryFile1_1";
	private static final String INPUT_BINARY_1_2 = TEST_DATA_INPUT_DIR + "binaryFile1_2";

	CronnDiffToHtml diffToHtml = null;

	@Override
	@Before
	public void setUp() throws IOException {
		super.setUp();
		diffToHtml = new CronnDiffToHtml();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedCode1() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_CODE_1_1, INPUT_CODE_1_2));
		assertOutputEqualToValidation();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedCode2() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_CODE_2_1, INPUT_CODE_2_2));
		assertOutputEqualToValidation();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedText1() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_TEXT_1_1, INPUT_TEXT_1_2));
		assertOutputEqualToValidation();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedBinary1() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_BINARY_1_1, INPUT_BINARY_1_2));
		assertOutputEqualToValidation();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedBinary2Identical() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_BINARY_1_2, INPUT_BINARY_1_2));
		assertOutputEqualToValidation();
	}

	@Test
	public void testGenerateDiffToHtmlReportJavaBasedCodeIdentical() throws Exception {
		diffToHtml.generateDiffToHtmlReport(createParameters(INPUT_CODE_1_1, INPUT_CODE_1_1));
		assertOutputEqualToValidation();
	}

	private DiffToHtmlParameters createParameters(String inputFileLeft, String inputFileRight) {
		return DiffToHtmlParameters.builder()
				.withDiffType(DiffType.FILES)
				.withInputLeftPath(inputFileLeft)
				.withInputRightPath(inputFileRight)
				.withOutputPath(getOutHtmlFilePath())
				.build();
	}
}
