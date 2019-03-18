package de.cronn.diff.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.head;
import static j2html.TagCreator.pre;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import de.cronn.diff.util.DiffToHtmlParameters;
import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

@SuppressWarnings("rawtypes")
public class DirectoryDiffHtmlBuilder extends HtmlBuilder {

	private static final String MSG_IDENTICAL_DIRS = "Directories are identical!";

	private static final String JS_SCRIPTS_URI = "diffToHtmlJSScripts.js";

	private static final String JS_FUNCTION_SHOW_ELEMENT = "showElement";

	private static final String JS_FUNCTION_SHOW_ELEMENTS = "showElements";

	private static final String ID_PREFIX_DIFF_TEXT = "diffText";

	private static final String ID_PREFIX_DIFF_BINARY = "diffBinary";

	private static final String ID_PREFIX_UNIQUE_FILE_LEFT = "uniqueFileLeft";

	private static final String ID_PREFIX_UNIQUE_FILE_RIGHT = "uniqueFileRight";

	private static final String HEADER_UNIQUE_FILES = "Unique files, only in ";

	private static final String HEADER_CHANGED_BINARY_FILES = "Changed binary files:";

	private static final String HEADER_CHANGED_TEXT_FILES = "Changed text files:";

	private static final String MESSAGE_EXPAND_COLLAPSE_ALL = "[expand/collapse all]";

	private static final String CSS_STYLE_DISPLAY_NONE = "display: none";

	private static final String CSS_CLASS_EXPAND_ALL_LINK = "expand-all-link";

	private static final String CSS_ID_WRAPPER2 = "wrapper2";

	private static final String CSS_CLASS_ONE_LINE_HEADER = "one-line-header";

	private String title;

	private DiffToHtmlParameters params;

	private List<Tag> changedTextFiles = new ArrayList<>();

	private List<Tag> changedBinaryFiles = new ArrayList<>();

	private List<Tag> uniqueFilesLeft = new ArrayList<>();

	private List<Tag> uniqueFilesRight = new ArrayList<>();

	private int changedTextDivCnt = 0;
	private List<String> changedTextDivIds = new ArrayList<>();

	private int changedBinaryDivCnt = 0;
	private List<String> changedBinaryDivIds = new ArrayList<>();

	private int uniqueFileLeftDivCnt = 0;
	private List<String> uniqueFileLeftDivIds = new ArrayList<>();

	private int uniqueFileRightDivCnt = 0;
	private List<String> uniqueFileRightDivIds = new ArrayList<>();

	public DirectoryDiffHtmlBuilder(DiffToHtmlParameters params) {
		super(params);
		this.params = params;
		this.title = "Diff directories " + params.getInputLeftPath() + " and " + params.getInputRightPath();
	}

	public void appendChangedTextFile(String filePath, Tag diffTable) {
		String divIdStr = ID_PREFIX_DIFF_TEXT + changedTextDivCnt++;
		changedTextDivIds.add(divIdStr);
		changedTextFiles.addAll(createDiffTableEntry(filePath, diffTable, divIdStr));
	}

	public void appendChangedBinaryFile(String filePath, Tag diffTable) {
		String divIdStr = ID_PREFIX_DIFF_BINARY + changedBinaryDivCnt++;
		changedBinaryDivIds.add(divIdStr);
		changedBinaryFiles.addAll(createDiffTableEntry(filePath, diffTable, divIdStr));
	}

	public void appendUniqueFileLeft(String filePath, Tag diffTable) {
		String divIdStr = ID_PREFIX_UNIQUE_FILE_LEFT + uniqueFileLeftDivCnt++;
		uniqueFileLeftDivIds.add(divIdStr);
		uniqueFilesLeft.addAll(createDiffTableEntry(filePath, diffTable, divIdStr));
	}

	public void appendUniqueFileRight(String filePath, Tag diffTable) {
		String divIdStr = ID_PREFIX_UNIQUE_FILE_RIGHT + uniqueFileRightDivCnt++;
		uniqueFileRightDivIds.add(divIdStr);
		uniqueFilesRight.addAll(createDiffTableEntry(filePath, diffTable, divIdStr));
	}

	private List<Tag> createDiffTableEntry(String filePath, Tag diffTable, String divIdStr) {
		Tag link = a().with(pre().withText(filePath)).withHref("javascript:" + JS_FUNCTION_SHOW_ELEMENT + "('" + divIdStr + "')");
		Tag div = div().attr(Attr.ID, divIdStr).attr(Attr.STYLE, CSS_STYLE_DISPLAY_NONE).with(diffTable, br(), br());
		return Arrays.asList(link, div);
	}

	@Override
	protected Tag createHead() {
		return head()
				.withCharset(PREFERRED_ENCODING)
				.with(title(title), createStyleTag());
	}

	@Override
	protected Tag createBody() {
		List<String> combinedLists = new ArrayList<>();
		combinedLists.addAll(changedTextDivIds);
		combinedLists.addAll(changedBinaryDivIds);
		combinedLists.addAll(uniqueFileLeftDivIds);
		combinedLists.addAll(uniqueFileRightDivIds);
		if(combinedLists.isEmpty()) {
			return body().withId(CSS_ID_WRAPPER2).with(h1(title), h2(MSG_IDENTICAL_DIRS));
		}
		return body().withId(CSS_ID_WRAPPER2)
				.with(
						div().with(
								h1(title),
								createExpandAllLink(combinedLists),
								br(), br()),
						createTablesWithChangedFiles())
				.with(createJSScripts());
	}

	private ContainerTag createTablesWithChangedFiles() {
		ContainerTag tablesDiv = div();
		if(!changedTextFiles.isEmpty()) {
			tablesDiv = tablesDiv.with(createTableBody(HEADER_CHANGED_TEXT_FILES, changedTextDivIds, changedTextFiles));
		}
		if (!changedBinaryFiles.isEmpty()) {
			tablesDiv = tablesDiv.with(createTableBody(HEADER_CHANGED_BINARY_FILES, changedBinaryDivIds, changedBinaryFiles));
		}
		if(!uniqueFilesLeft.isEmpty()){
			tablesDiv = tablesDiv
					.with(createTableBody(createUniqueFilesHeader(params.getInputLeftPath()), uniqueFileLeftDivIds, uniqueFilesLeft));
		}
		if(!uniqueFilesRight.isEmpty()){
			tablesDiv = tablesDiv
					.with(createTableBody(createUniqueFilesHeader(params.getInputRightPath()), uniqueFileRightDivIds, uniqueFilesRight));
		}
		return tablesDiv;
	}

	private Tag createTableBody(String tableTitle, List<String> tableDivIds, List<Tag> rows) {
		return div()
				.with(div().with(
						h2(tableTitle).withClass(CSS_CLASS_ONE_LINE_HEADER),
						createExpandAllLink(tableDivIds)))
				.with(rows)
				.with(br());
	}

	private String createUniqueFilesHeader(String path) {
		return HEADER_UNIQUE_FILES + FilenameUtils.getBaseName(FilenameUtils.normalizeNoEndSeparator(path)) + ":";
	}

	private ContainerTag createExpandAllLink(List<String> tableDivIds) {
		return a(MESSAGE_EXPAND_COLLAPSE_ALL)
				.withClass(CSS_CLASS_EXPAND_ALL_LINK)
				.withHref("javascript:" + JS_FUNCTION_SHOW_ELEMENTS + "(" + getIdStrListForJS(tableDivIds) + ")");
	}

	private String getIdStrListForJS(List<String> tableDivIds) {
		if(tableDivIds.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int lastIndex = tableDivIds.size() - 1;
		for (int i = 0; i < lastIndex; i++) {
			sb.append("'");
			sb.append(tableDivIds.get(i));
			sb.append("', ");
		}
		sb.append("'");
		sb.append(tableDivIds.get(lastIndex));
		sb.append("']");
		return sb.toString();
	}

	private Tag createJSScripts() {
		String script;
		try {
			InputStream resourceAsStream = getClass().getResourceAsStream("/" + JS_SCRIPTS_URI);
			script = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
		} catch (NullPointerException | IOException e) {
			throw new RuntimeException("The resource " + JS_SCRIPTS_URI + " could not be loaded.", e);
		}
		return script().attr(Attr.TYPE, "text/javascript").with(rawHtml(script));
	}
}
