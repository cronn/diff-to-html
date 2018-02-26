package de.cronn.diff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TestName;

import de.cronn.diff.html.HtmlBuilder;
import de.cronn.diff.util.FileHelper;

public class TestBase {

	protected enum OS {UNIX, WINDOWS, SUN}

	private static final String LINUX_LINE_SEPARATOR = "\n";

	private static final String WINDOWS_LINE_SEPARATOR = "\r\n";

	private static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();

	public static final String TEST_DATA_VALIDATION_DIR = "src/test/resources/validation/";

	public static final String TEST_DATA_INPUT_DIR = "src/test/resources/input/";

	public static final String TEST_DATA_OUTPUT_DIR = "src/test/resources/output/";

	public static final String HTML_SUFFIX = ".html";

	private static final String SYS_OUT_SUFFIX = ".sysouterr";

	private boolean outputDirChecked = false;

	@Rule
	public TestName testName = new TestName();

	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	OS osOrig;

	@Before
	public void setUp() throws IOException {
		createOutputDirIfNotExists();
		HtmlBuilder.useSimpleFormatOnHtmls = true;
	}

	@After
	public void tearDown() {
		HtmlBuilder.useSimpleFormatOnHtmls = false;
	}

	protected void assertHtmlResultEqualToValidation(String actualHtml) throws IOException {
		writeToDisk(actualHtml, getOutHtmlFilePath()); // for external comparison by developer
		String expectedHtml = readFileToString(getValidationHtmlFilePath());
		assertEquals(expectedHtml, actualHtml);
	}

	protected void assertStringResultEqualToValidation(String actualString) throws IOException {
		actualString = normalizeWorkingDir(actualString);
		writeToDisk(actualString, getOutFilePath()); // for external comparison by developer
		String expectedString = readFileToString(getValidationFilePath());
		actualString = normalizeLineSeparators(actualString);
		expectedString = normalizeLineSeparators(expectedString);
		assertEquals(expectedString, actualString);
	}

	protected void assertOutputEqualToValidation() throws IOException {
		String expected = readFileToString(getValidationHtmlFilePath());
		String actual = readFileToString(getOutHtmlFilePath());
		actual = normalizeTimestamps(actual);
		writeToDisk(actual, getOutHtmlFilePath());
		assertEquals(expected, actual);
	}

	protected void assertSysOutErrEqualToValidation(String actual) throws IOException {
		actual = normalizeWorkingDir(actual);
		writeToDisk(actual, getOutSysOutFilePath()); // for external comparison by developer
		String expected = readFileToString(getValidationSysOutFilePath());

		String expectedNorm = normalizeLineSeparators(expected);
		String actualNorm = normalizeLineSeparators(actual);
		assertEquals(expectedNorm, actualNorm);
	}

	protected String getOutHtmlFilePath() {
		return TEST_DATA_OUTPUT_DIR + getTestMethodName() + HTML_SUFFIX;
	}

	protected String readInputFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(TEST_DATA_INPUT_DIR + filePath)));
	}

	protected String getTestMethodName() {
		return testName.getMethodName();
	}

	private String normalizeLineSeparators(String s) {
		if (SystemUtils.IS_OS_WINDOWS) {
			return s.replace(WINDOWS_LINE_SEPARATOR, SYSTEM_LINE_SEPARATOR);
		} else if (SystemUtils.IS_OS_UNIX) {
			return s.replaceAll(LINUX_LINE_SEPARATOR, SYSTEM_LINE_SEPARATOR);
		} else {
			return s;
		}
	}

	private String normalizeTimestamps(String s) {
		s = s.replaceAll("([0-9]{4}-[0-9]{2}-[0-9]{2})(.*?)([0-9]{2}:[0-9]{2}:[0-9]{2}[\\.]*[0-9]*)", "[DATE]$2[TIME]");
		s = s.replaceAll("(\\[TIME\\] )(\\+[0-9]{4})", "$1 [ZONE]");
		return s;
	}
	
	private String normalizeWorkingDir(String s) {
		String workingDir = FileHelper.getWorkingDir();
		return s.replaceAll(workingDir, "[current/working/directory/]");
	}

	private String readFileToString(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

	private void createOutputDirIfNotExists() throws IOException {
		if(!outputDirChecked) {
			Path outputDirPath = Paths.get(TEST_DATA_OUTPUT_DIR);
			if(Files.notExists(outputDirPath)){
				Files.createDirectory(outputDirPath);
			}
			outputDirChecked = true;
		}
	}

	private void writeToDisk(String str, String destPath) throws IOException {
		Files.write(Paths.get(destPath), str.getBytes());
	}

	private String getValidationFilePath() {
		return TEST_DATA_VALIDATION_DIR + getTestMethodName();
	}

	private String getValidationHtmlFilePath() {
		return TEST_DATA_VALIDATION_DIR + getTestMethodName() + HTML_SUFFIX;
	}

	private String getOutFilePath() {
		return TEST_DATA_OUTPUT_DIR + getTestMethodName();
	}

	private String getOutSysOutFilePath() {
		return TEST_DATA_OUTPUT_DIR + getTestMethodName() + SYS_OUT_SUFFIX;
	}

	private String getValidationSysOutFilePath() {
		return TEST_DATA_VALIDATION_DIR + getTestMethodName() + SYS_OUT_SUFFIX;
	}

	protected void backupOrigOS() {
		osOrig = SystemUtils.IS_OS_UNIX ? OS.UNIX : SystemUtils.IS_OS_WINDOWS ? 
				OS.WINDOWS : SystemUtils.IS_OS_SUN_OS ? OS.SUN : null;
	}

	protected void resetOS() throws Exception {
		setOS(osOrig);
	}

	protected void setOS(OS os) throws Exception {
		setFinalStatic(SystemUtils.class.getField("IS_OS_UNIX"), os == OS.UNIX ? true : false);
		setFinalStatic(SystemUtils.class.getField("IS_OS_WINDOWS"), os == OS.WINDOWS ? true : false);
		setFinalStatic(SystemUtils.class.getField("IS_OS_SUN_OS"), os == OS.SUN ? true : false);
	}

	private void setFinalStatic(Field field, Object newValue) throws Exception {
	    field.setAccessible(true);
	    Field modifiersField = Field.class.getDeclaredField("modifiers");
	    modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    field.set(null, newValue);
	}
}
