package de.cronn.diff.impl.java.wrapper;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlRuntimeException;
import de.cronn.diff.util.FileHelper;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class JavaDiffUtils2HtmlWrapper {

	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	private FileDiffHtmlBuilder htmlBuilder = null;
	private int contextLinesCounter = 0;
	private int contextLinesStart;

	private int origLinesCounter = 0;
	private int origLinesStart = 0;
	private int origLinesTotal = 0;

	private int revLinesCounter = 0;
	private int revLinesStart = 0;
	private int revLinesTotal = 0;

	private int initialPostionInHtmlBuilder;
	private DiffToHtmlParameters params;



	public FileDiffHtmlBuilder appendDiffToBuilder(FileDiffHtmlBuilder htmlBuilder, DiffToHtmlParameters params)
			throws IOException {
		this.htmlBuilder = htmlBuilder;
		this.params = params;

		List<String> originalLines = readAllLinesWithCorrectEncoding(params.getInputLeftPath());
		List<String> revisedLines = readAllLinesWithCorrectEncoding(params.getInputRightPath());
		appendDiffToBuilder(originalLines, revisedLines);
		return htmlBuilder;
	}

	private void appendDiffToBuilder(List<String> originalLines, List<String> revisedLines) {
		Patch<String> diffPatches = DiffUtils.diff(originalLines, revisedLines);
		List<Delta<String>> diffPatchDeltas = new ArrayList<>(diffPatches.getDeltas());

		if (!diffPatchDeltas.isEmpty()) {
			List<Delta<String>> currentDeltas = new ArrayList<>();
			Delta<String> currentDelta = diffPatchDeltas.get(0);
			currentDeltas.add(currentDelta);

			for (int i = 1; i < diffPatchDeltas.size(); i++) {
				Delta<String> nextDelta = diffPatchDeltas.get(i);

				if (nextDeltaIsTooCloseToCurrentDelta(currentDelta, nextDelta)) {
					currentDeltas.add(nextDelta);
				} else {
					processDeltas(originalLines, currentDeltas);
					currentDeltas.clear();
					currentDeltas.add(nextDelta);
				}
				currentDelta = nextDelta;
			}
			processDeltas(originalLines, currentDeltas);
		}
	}

	private List<String> readAllLinesWithCorrectEncoding(String filePath) throws IOException {
		if (!params.isDetectTextFileEncoding()) {
			try {
				return Files.readAllLines(Paths.get(filePath), DEFAULT_CHARSET);
			} catch (CharacterCodingException e) {
				throw new DiffToHtmlRuntimeException(
						"File " + filePath + " cannot be read with default charset of this VM: " + DEFAULT_CHARSET, e);
			}
		} else {
			return FileHelper.readAllLinesWithDetectedEncoding(filePath);
		}
	}

	private boolean nextDeltaIsTooCloseToCurrentDelta(Delta<String> currentDelta, Delta<String> nextDelta) {
		int positionAfterCurrentDelta = currentDelta.getOriginal().getPosition() + currentDelta.getOriginal().size();
		int positionOfNextDelta = nextDelta.getOriginal().getPosition();
		return positionAfterCurrentDelta + params.getUnifiedContext() >= positionOfNextDelta - params.getUnifiedContext();
	}

	private void processDeltas(List<String> origLines, List<Delta<String>> deltas) {
		Delta<String> curDelta = deltas.get(0);
		resetPositionsAndCounters(curDelta);

		appendFirstContextAndDelta(origLines, curDelta);
		curDelta = appendFollowingDeltasWithLeadingContexts(origLines, deltas, curDelta);
		appendLastContext(origLines, curDelta);

		insertUnifiedDiffBlockHeaderAtStartOfHtml();
	}

	private void resetPositionsAndCounters(Delta<String> currentDelta) {
		origLinesTotal = 0;
		revLinesTotal = 0;
		contextLinesCounter = 0;
		origLinesCounter = 0;
		revLinesCounter = 0;
		initialPostionInHtmlBuilder = htmlBuilder.getCurrentPosition();

		// NOTE: +1 to overcome the 0-offset Position
		origLinesStart = currentDelta.getOriginal().getPosition() + 1 - params.getUnifiedContext();
		if (origLinesStart < 1) {
			origLinesStart = 1;
		}

		revLinesStart = currentDelta.getRevised().getPosition() + 1 - params.getUnifiedContext();
		if (revLinesStart < 1) {
			revLinesStart = 1;
		}

		contextLinesStart = currentDelta.getOriginal().getPosition() - params.getUnifiedContext();
		if (contextLinesStart < 0) {
			contextLinesStart = 0;
		}
	}

	private void appendFirstContextAndDelta(List<String> origLines, Delta<String> curDelta) {
		for (int line = contextLinesStart; line < curDelta.getOriginal().getPosition(); line++) {
			appendContextToHtmlBuilder(origLines, line);
		}
		appendDeltaTextToHtmlBuilder(curDelta);
	}

	private Delta<String> appendFollowingDeltasWithLeadingContexts(List<String> origLines, List<Delta<String>> deltas, Delta<String> curDelta) {
		int deltaIndex = 1;
		while (deltaIndex < deltas.size()) { // for each of the other Deltas
			Delta<String> nextDelta = deltas.get(deltaIndex);
			for (int line = getPositionAfter(curDelta); line < nextDelta.getOriginal().getPosition(); line++) {
				appendContextToHtmlBuilder(origLines, line);
			}
			appendDeltaTextToHtmlBuilder(nextDelta);
			curDelta = nextDelta;
			deltaIndex++;
		}
		return curDelta;
	}

	private void appendLastContext(List<String> origLines, Delta<String> curDelta) {
		contextLinesStart = getPositionAfter(curDelta);
		for (int line = contextLinesStart; (line < (contextLinesStart + params.getUnifiedContext())) & (line < origLines.size()); line++) {
			appendContextToHtmlBuilder(origLines, line);
		}
	}

	private int getPositionAfter(Delta<String> curDelta) {
		return curDelta.getOriginal().getPosition() + curDelta.getOriginal().getLines().size();
	}

	private void appendContextToHtmlBuilder(List<String> origLines, int line) {
		String unchangedLine = " " + origLines.get(line);
		htmlBuilder.appendUnchangedLine(unchangedLine, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
		origLinesTotal++;
		revLinesTotal++;
		contextLinesCounter++;
	}

	private void appendDeltaTextToHtmlBuilder(Delta<String> delta) {
		for (String line : delta.getOriginal().getLines()) {
			htmlBuilder.appendDeletionLine("-" + line, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
			origLinesCounter++;
		}
		for (String line : delta.getRevised().getLines()) {
			htmlBuilder.appendInsertionLine("+" + line, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
			revLinesCounter++;
		}
		origLinesTotal += delta.getOriginal().getLines().size();
		revLinesTotal += delta.getRevised().getLines().size();
	}

	private void insertUnifiedDiffBlockHeaderAtStartOfHtml() {
		String header = "@@ -" + origLinesStart + "," + origLinesTotal + " +" + revLinesStart + "," + revLinesTotal + " @@";
		htmlBuilder.appendInfoLineAt(initialPostionInHtmlBuilder, header);
		htmlBuilder.appendEmptyLineAt(initialPostionInHtmlBuilder);
	}

	private int getOrigLineNr(int origStart) {
		return origStart + contextLinesCounter + origLinesCounter;
	}

	private int getRevLineNr(int revStart) {
		return revStart + contextLinesCounter + revLinesCounter;
	}
}
