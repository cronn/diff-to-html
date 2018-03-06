package de.cronn.diff.html;

import static j2html.TagCreator.html;
import static j2html.TagCreator.link;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.FileHelper;
import j2html.tags.EmptyTag;
import j2html.tags.Tag;

public abstract class HtmlBuilder {

	protected static final String EMPTY_LINE_HEIGHT = "16";

	protected static final String CSS_CLASS_LINE_NUMBER = "line-number";

	protected static final String CSS_ID_WRAPPER = "wrapper";

	protected static final String CSS_CLASS_DELETE = "delete";

	protected static final String CSS_CLASS_INSERT = "insert";

	protected static final String CSS_CLASS_CONTEXT = "context";

	protected static final String CSS_CLASS_INFO = "info";

	protected static final String CSS_CLASS_ATTENTION = "attention";

	public static boolean useSimpleFormatOnHtmls = false;

	protected String outputDirForRelativePaths = null;

	public abstract Tag createHead();

	public abstract Tag createBody();
	
	public static final String preferedEncoding = StandardCharsets.UTF_8.toString();

	public HtmlBuilder(DiffToHtmlParameters params) {
		this.outputDirForRelativePaths = FilenameUtils.getFullPath(params.getOutputPath());
	}

	@Override
	public String toString() {
		String renderedHtml = html().with(
				createHead(),
				createBody())
				.render();
		if (useSimpleFormatOnHtmls) {
			return newlineAllHtmlTags(renderedHtml);
		} else {
			return renderedHtml;
		}
	}

	protected String getRelativePath(String absolutePath) {
		if(absolutePath.isEmpty()) {
			return "";
		}
		if (outputDirForRelativePaths != null) {
			return Paths.get(outputDirForRelativePaths).relativize(Paths.get(absolutePath)).toString();
		}
		return absolutePath;
	}

	protected String newlineAllHtmlTags(String html) {
		return html.replaceAll("(<.*?>)", System.lineSeparator() + "$1")
				.replaceAll(System.lineSeparator() + "(</.*?>)", "$1");
	}

	protected EmptyTag createStyleTag() {
		return link().withRel("stylesheet").withType("text/css").withHref(FileHelper.CSS_FILE);
	}


}
