package de.cronn.diff.html;

import static j2html.TagCreator.a;
import static j2html.TagCreator.b;
import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.head;
import static j2html.TagCreator.pre;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.text;
import static j2html.TagCreator.th;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.SimpleFileInfo;
import j2html.attributes.Attr;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class FileDiffHtmlBuilder extends HtmlBuilder {

	public FileDiffHtmlBuilder(DiffToHtmlParameters params) {
		super(params);
	}

	private List<Tag> diffLines = new ArrayList<>();

	private SimpleFileInfo fileLeftInfo = new SimpleFileInfo();

	private SimpleFileInfo fileRightInfo = new SimpleFileInfo();

	public void setFileLeftInfo(SimpleFileInfo fileLeftInfo) {
		this.fileLeftInfo = fileLeftInfo;
	}

	public void setFileRightInfo(SimpleFileInfo fileRightInfo) {
		this.fileRightInfo = fileRightInfo;
	}

	public void setFileInfoPair(SimpleFileInfo[] fileInfoPair) {
		if (fileInfoPair.length == 2) {
			fileLeftInfo = fileInfoPair[0];
			fileRightInfo = fileInfoPair[1];
		}
	}

	public int getCurrentPosition() {
		return diffLines.size();
	}

	public void appendInfoLine(String line) {
		appendLine(line, "--", "--", CSS_CLASS_INFO);
	}

	public void appendInfoLineAt(int position, String line) {
		appendLineAt(position, line, "--", "--", CSS_CLASS_INFO);
	}

	public void appendAttentionLine(String line) {
		appendLine(line, "--", "--", CSS_CLASS_ATTENTION);
	}

	public void appendUnchangedLine(String line, Integer fileLeftLineNr, Integer fileRightLineNr) {
		appendLine(line, fileLeftLineNr.toString(), fileRightLineNr.toString(), CSS_CLASS_CONTEXT);
	}

	public void appendInsertionLine(String line, Integer fileLeftLineNr, Integer fileRightLineNr) {
		appendLine(line, fileLeftLineNr.toString(), fileRightLineNr.toString(), CSS_CLASS_INSERT);
	}

	public void appendDeletionLine(String line, Integer fileLeftLineNr, Integer fileRightLineNr) {
		appendLine(line, fileLeftLineNr.toString(), fileRightLineNr.toString(), CSS_CLASS_DELETE);
	}

	public void appendEmptyLine() {
		diffLines.add(createEmptyLine());
	}

	public void appendEmptyLineAt(int position) {
		diffLines.add(position, createEmptyLine());
	}

	public void appendTextFile(String textFile) {
		String[] textFileLines = textFile.split(System.lineSeparator());
		for(int i = 0; i < textFileLines.length; i++) {
			int line = i+1;
			appendLine(textFileLines[i], Integer.toString(line), " ", CSS_CLASS_CONTEXT);
		}
	}

	private void appendLine(String line, String fileLeftLineNr, String fileRightLineNr, String lineClass) {
		diffLines.add(createDiffLine(line, fileLeftLineNr, fileRightLineNr, lineClass));
	}

	private void appendLineAt(int position, String line, String fileLeftLineNr, String fileRightLineNr, String lineClass) {
		diffLines.add(position, createDiffLine(line, fileLeftLineNr, fileRightLineNr, lineClass));
	}

	private ContainerTag createDiffLine(String line, String fileLeftLineNr, String fileRightLineNr, String lineClass) {
		return tr()
				.with(
						td(fileLeftLineNr).withClass(CSS_CLASS_LINE_NUMBER),
						td(fileRightLineNr).withClass(CSS_CLASS_LINE_NUMBER),
						td()
								.withClass(lineClass)
								.with(pre().withText(line)));
	}

	private ContainerTag createEmptyLine() {
		return tr().attr(Attr.HEIGHT, EMPTY_LINE_HEIGHT).with(td(""), td(""), td(""));
	}

	@Override
	protected Tag createHead() {
		String fileLeftSimpleName = StringUtils.substringAfterLast(fileLeftInfo.getPath(), File.separator);
		String fileRightSimpleName = StringUtils.substringAfterLast(fileRightInfo.getPath(), File.separator);
		String title = "diff " + fileLeftSimpleName + " " + fileRightSimpleName;

		return head()
				.withCharset(PREFERRED_ENCODING)
				.with(title(title), createStyleTag());
	}

	@Override
	protected Tag createBody() {
		return body().with(div()
				.withId(CSS_ID_WRAPPER)
				.with(createDiffTable()));
	}

	public Tag createDiffTable() {
		return table()
				.with(createDiffTableTitle())
				.with(diffLines);
	}

	private Tag createDiffTableTitle() {
		String fileLeftPath = fileLeftInfo.getPath();
		String fileRightPath = fileRightInfo.getPath();

		String fileLeftRelativePath = getRelativePath(fileLeftPath);
		String fileRightRelativePath = getRelativePath(fileRightPath);
		return tr().with(th()
				.attr(Attr.COLSPAN, "3")
				.with(
						createBoldLink(fileLeftRelativePath), text(" " + fileLeftInfo.getLastModified()),
						br(),
						createBoldLink(fileRightRelativePath), text(" " + fileRightInfo.getLastModified())));
	}

	private Tag createBoldLink(String filePath) {
		return a().withHref(filePath).with(b(filePath));
	}
}
