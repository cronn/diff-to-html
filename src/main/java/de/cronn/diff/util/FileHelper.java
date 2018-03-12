package de.cronn.diff.util;

import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.LF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.UniversalDetector;

import de.cronn.diff.Main;

public class FileHelper {

	private static final int BYTE_BUFFER_SIZE_DETECT_BINARY = 16000;
	private static final int BYTE_BUFFER_SIZE_DETECT_ENCODING = 4096;
	public static final String CSS_FILE = "diffToHtml.css";

	public static void copyCssFileToDir(String dirPath) throws IOException {
		ensureDirExists(dirPath);
		if (!dirPath.endsWith("/")) {
			dirPath += "/";
		}
		String cssFileDestPath = dirPath + CSS_FILE;
		InputStream resourceAsStream = Main.class.getResourceAsStream("/" + CSS_FILE);
		FileUtils.copyInputStreamToFile(resourceAsStream, new File(cssFileDestPath));
	}

	public static void ensureDirExists(String dirPath) throws IOException {
		if(!Files.exists(Paths.get(dirPath))) {
			Files.createDirectory(Paths.get(dirPath));
		}
	}
	
	private static boolean lastIsBinaryResult;
	private static String lastIsBinaryFile = "";
	
	/**
	 * Imitates Unix diff's behavior in determining if a file is binary or text by checking the first few thousand bytes for zero values. See <a href="http://www.gnu.org/software/diffutils/manual/html_node/Binary.html">http://www.gnu.org/software/diffutils/manual/html_node/Binary.html</a>
	 * @param filePath
	 * @return true if file is binary or of size zero, false otherwise
	 * @throws IOException
	 */
	public static boolean isFileBinary(String filePath) throws IOException {
		if(!lastIsBinaryFile.equals(filePath)) { //to save processing time if same file is checked more than once
			lastIsBinaryFile = filePath;
			lastIsBinaryResult = isFileBinaryCheck(filePath);
		}
		return lastIsBinaryResult;
	}
	
	private static boolean isFileBinaryCheck(String filePath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		byte[] buffer = new byte[BYTE_BUFFER_SIZE_DETECT_BINARY];
		int bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_BINARY);
		fileInputStream.close();

		if(bytesRead == 0) {
			return true;
		}

		for(int i = 0; i < bytesRead; i++) {
			if(buffer[i] == 0) {
				return true;
			}
		}
		return false;
	}


	public static List<String> readAllLinesWithDetectedEncoding(String filePath) throws IOException {
		String encoding = getCharsetEncoding(filePath);
		if (encoding == null) {
			return readAllLines(filePath, StandardCharsets.US_ASCII);

		} else if (encoding == Constants.CHARSET_UTF_8) {
			return readAllLines(filePath, StandardCharsets.UTF_8);

		} else if (encoding == Constants.CHARSET_WINDOWS_1252) {
			return readAllLines(filePath, StandardCharsets.ISO_8859_1);

		} else if (encoding == Constants.CHARSET_UTF_16BE) {
			return readAllLines(filePath, StandardCharsets.UTF_16BE);

		} else if (encoding == Constants.CHARSET_UTF_16LE) {
			return readAllLines(filePath, StandardCharsets.UTF_16LE);

		} else {
			try {
				return readAllLines(filePath, Charset.forName(encoding));
			} catch(IllegalCharsetNameException|UnsupportedCharsetException e) {
				throw new RuntimeException("The charset encoding '" + encoding + "' of file " + filePath + " is not supported", e);
			}
		}
	}

	private static List<String> readAllLines(String filePath, Charset charset) throws IOException {
		try {
			return Files.readAllLines(Paths.get(filePath), charset);
		} catch (MalformedInputException e) {
			throw new RuntimeException("File " + filePath + " could not be read with charset " + charset.toString() , e);
		}
	}

	static String getCharsetEncoding(String filePath) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		byte[] buffer = new byte[BYTE_BUFFER_SIZE_DETECT_ENCODING];

		int bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_ENCODING);
		while(bytesRead > 0 && !detector.isDone()) {
			detector.handleData(buffer, 0, bytesRead);
			bytesRead = IOUtils.read(fileInputStream, buffer, 0, BYTE_BUFFER_SIZE_DETECT_ENCODING);
		}

		detector.dataEnd();
		return detector.getDetectedCharset();
	}
	
	public static String getWorkingDir() {
		return new File("").getAbsolutePath() + File.separator;
	}

	public static String normalizeLineSeparators(String s) {
		return s.replaceAll(CR + LF, LF);
	}
}
