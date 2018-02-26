package de.cronn.diff.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import de.cronn.diff.TestBase;

public class FileHelperTest extends TestBase {

	@Test
	public void testCopyCssFileToDir() throws Exception {
		String dirPath = TEST_DATA_OUTPUT_DIR + getTestMethodName();
		String cssFilePath = dirPath + File.separator + FileHelper.CSS_FILE;

		Files.deleteIfExists(Paths.get(cssFilePath));
		Files.deleteIfExists(Paths.get(dirPath));
		FileHelper.copyCssFileToDir(dirPath);
		assertTrue("Expected " + dirPath + " does not exist", Files.exists(Paths.get(cssFilePath)));
	}

	@Test
	public void testIsFileBinary_isBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "binaryFile1_1");
		assertTrue(isBinary);
	}

	@Test
	public void testIsFileBinary_isNotBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "text1_1.example");
		assertFalse(isBinary);
	}

	@Test
	public void testIsFileBinary_emptyFileIsBinary() throws Exception {
		boolean isBinary = FileHelper.isFileBinary(TEST_DATA_INPUT_DIR + "emptyFile");
		assertTrue(isBinary);
	}
}
