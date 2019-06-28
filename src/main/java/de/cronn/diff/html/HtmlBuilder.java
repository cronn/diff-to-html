package de.cronn.diff.html;

import static de.cronn.diff.impl.java.wrapper.JavaDiffUtils2HtmlWrapper.DELETION_CLOSE_TAG;
import static de.cronn.diff.impl.java.wrapper.JavaDiffUtils2HtmlWrapper.DELETION_OPEN_TAG;
import static de.cronn.diff.impl.java.wrapper.JavaDiffUtils2HtmlWrapper.INSERTION_CLOSE_TAG;
import static de.cronn.diff.impl.java.wrapper.JavaDiffUtils2HtmlWrapper.INSERTION_OPEN_TAG;
import static j2html.TagCreator.html;
import static j2html.TagCreator.style;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import de.cronn.diff.Main;
import de.cronn.diff.util.DiffToHtmlParameters;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

@SuppressWarnings("rawtypes")
public abstract class HtmlBuilder {

	private static final String CSS_FILE = "diffToHtml.css";

	protected static final String EMPTY_LINE_HEIGHT = "16";

	protected static final String CSS_CLASS_LINE_NUMBER = "line-number";

	protected static final String CSS_ID_WRAPPER = "wrapper";

	protected static final String CSS_CLASS_DELETE = "delete";

	protected static final String CSS_CLASS_INSERT = "insert";

	protected static final String CSS_CLASS_CONTEXT = "context";

	protected static final String CSS_CLASS_INFO = "info";

	protected static final String CSS_CLASS_ATTENTION = "attention";
	
	private static final String CSS_CLASS_INSERT_CHAR = "insertChar";
	
	private static final String CSS_CLASS_DELETE_CHAR = "deleteChar";

	protected static final String PREFERRED_ENCODING = StandardCharsets.UTF_8.toString();
	
	protected static boolean useSimpleFormatOnHtmls = false;

	protected String outputDirForRelativePaths = null;

	protected abstract Tag createHead();

	protected abstract Tag createBody();

	public HtmlBuilder(DiffToHtmlParameters params) {
		String outPath = params.getOutputPath();
		if(outPath != null) {
			outputDirForRelativePaths = new File(FilenameUtils.getFullPath(outPath)).getAbsolutePath();
		}
	}

	@Override
	public String toString() {
		String renderedHtml = html().with(
				createHead(),
				createBody())
				.render();
		if (useSimpleFormatOnHtmls) {
			renderedHtml = newlineAllHtmlTags(renderedHtml);
		} 
		return renderedHtml.replaceAll(DELETION_OPEN_TAG, "<u class=\"" + CSS_CLASS_DELETE_CHAR + "\">")
				.replaceAll(DELETION_CLOSE_TAG, "</u>")
				.replaceAll(INSERTION_OPEN_TAG, "<u class=\""+CSS_CLASS_INSERT_CHAR+"\">")
				.replaceAll(INSERTION_CLOSE_TAG, "</u>");
	}

	protected String getRelativePath(String path) {
		if(path.isEmpty()) {
			return "";
		}
		
		String absolutePath = new File(path).getAbsolutePath();
		if (outputDirForRelativePaths != null) {
			String relativePath = Paths.get(outputDirForRelativePaths).relativize(Paths.get(absolutePath)).toString();
			return normalizeFileSeparators(relativePath);
		}

		return path;
	}

	/*
	 * always use forward slash as file separator, because in HTML files both work,
	 * but windows style file separators, i.e. backward slashes, will break tests
	 */
	private String normalizeFileSeparators(String path) {
		return FilenameUtils.separatorsToUnix(path);
	}

	private String newlineAllHtmlTags(String html) {
		return html.replaceAll("(<[^/].*?>)", System.lineSeparator() + "$1");
	}

	ContainerTag createStyleTag() {
		String styleSheet;
		try(InputStream cssInputStream =  Main.class.getResourceAsStream("/" + CSS_FILE)) {
			styleSheet = IOUtils.toString(cssInputStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			styleSheet = "<!-- stylesheet " + CSS_FILE + " could not be loaded -->";
		}
		return style(styleSheet);
	}

	public static void setUseSimpleFormatOnHtmls(boolean useSimpleFormatOnHtmls) {
		HtmlBuilder.useSimpleFormatOnHtmls = useSimpleFormatOnHtmls;
	}
}
