package de.cronn.diff.impl.java.wrapper;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.github.difflib.text.DiffRowGenerator.Builder;

import de.cronn.diff.html.FileDiffHtmlBuilder;
import de.cronn.diff.util.DiffToHtmlParameters;
import de.cronn.diff.util.DiffToHtmlRuntimeException;
import de.cronn.diff.util.FileHelper;

public class JavaDiffUtils2HtmlWrapper {

	public static final String DELETION_OPEN_TAG = "###DELETION_OPEN_383dc8aa341f41289278324023e4a030###";
	public static final String DELETION_CLOSE_TAG = "###DELETION_CLOSE_a6af1ef424e74b97a3a2853df441be2a###";
	public static final String INSERTION_OPEN_TAG = "###INSERTION_OPEN_b20a6ec4c51d45499a527bbe19628281###";
	public static final String INSERTION_CLOSE_TAG = "###INSERTION_CLOSE_594bd9ee828743ef95eb721da47ad406###";
	
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
		Patch<String> diffPatches;
		diffPatches = DiffUtils.diff(originalLines, revisedLines);
		
		List<AbstractDelta<String>> diffPatchDeltas = new ArrayList<>(diffPatches.getDeltas());

		if (!diffPatchDeltas.isEmpty()) {
			List<AbstractDelta<String>> currentDeltas = new ArrayList<>();
			AbstractDelta<String> currentDelta = diffPatchDeltas.get(0);
			currentDeltas.add(currentDelta);

			for (int i = 1; i < diffPatchDeltas.size(); i++) {
				AbstractDelta<String> nextDelta = diffPatchDeltas.get(i);

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

	private boolean nextDeltaIsTooCloseToCurrentDelta(AbstractDelta<String> currentDelta, AbstractDelta<String> nextDelta) {
		int positionAfterCurrentDelta = currentDelta.getSource().getPosition() + currentDelta.getSource().size();
		int positionOfNextDelta = nextDelta.getSource().getPosition();
		return positionAfterCurrentDelta + params.getUnifiedContext() >= positionOfNextDelta - params.getUnifiedContext();
	}

	private void processDeltas(List<String> origLines, List<AbstractDelta<String>> deltas) {
		AbstractDelta<String> curDelta = deltas.get(0);
		resetPositionsAndCounters(curDelta);

		appendFirstContextAndDelta(origLines, curDelta);
		curDelta = appendFollowingDeltasWithLeadingContexts(origLines, deltas, curDelta);
		appendLastContext(origLines, curDelta);

		insertUnifiedDiffBlockHeaderAtStartOfHtml();
	}

	private void resetPositionsAndCounters(AbstractDelta<String> currentDelta) {
		origLinesTotal = 0;
		revLinesTotal = 0;
		contextLinesCounter = 0;
		origLinesCounter = 0;
		revLinesCounter = 0;
		initialPostionInHtmlBuilder = htmlBuilder.getCurrentPosition();

		// NOTE: +1 to overcome the 0-offset Position
		origLinesStart = currentDelta.getSource().getPosition() + 1 - params.getUnifiedContext();
		if (origLinesStart < 1) {
			origLinesStart = 1;
		}

		revLinesStart = currentDelta.getTarget().getPosition() + 1 - params.getUnifiedContext();
		if (revLinesStart < 1) {
			revLinesStart = 1;
		}

		contextLinesStart = currentDelta.getSource().getPosition() - params.getUnifiedContext();
		if (contextLinesStart < 0) {
			contextLinesStart = 0;
		}
	}

	private void appendFirstContextAndDelta(List<String> origLines, AbstractDelta<String> curDelta) {
		for (int line = contextLinesStart; line < curDelta.getSource().getPosition(); line++) {
			appendContextToHtmlBuilder(origLines, line);
		}
		appendDeltaTextToHtmlBuilder(curDelta);
	}

	private AbstractDelta<String> appendFollowingDeltasWithLeadingContexts(List<String> origLines,
			List<AbstractDelta<String>> deltas, AbstractDelta<String> curDelta) {
		int deltaIndex = 1;
		while (deltaIndex < deltas.size()) { // for each of the other Deltas
			AbstractDelta<String> nextDelta = deltas.get(deltaIndex);
			for (int line = getPositionAfter(curDelta); line < nextDelta.getSource().getPosition(); line++) {
				appendContextToHtmlBuilder(origLines, line);
			}
			appendDeltaTextToHtmlBuilder(nextDelta);
			curDelta = nextDelta;
			deltaIndex++;
		}
		return curDelta;
	}

	private void appendLastContext(List<String> origLines, AbstractDelta<String> curDelta) {
		contextLinesStart = getPositionAfter(curDelta);
		for (int line = contextLinesStart; (line < (contextLinesStart + params.getUnifiedContext())) & (line < origLines.size()); line++) {
			appendContextToHtmlBuilder(origLines, line);
		}
	}

	private int getPositionAfter(AbstractDelta<String> curDelta) {
		return curDelta.getSource().getPosition() + curDelta.getSource().getLines().size();
	}

	private void appendContextToHtmlBuilder(List<String> origLines, int line) {
		String unchangedLine = " " + origLines.get(line);
		htmlBuilder.appendUnchangedLine(unchangedLine, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
		origLinesTotal++;
		revLinesTotal++;
		contextLinesCounter++;
	}

	private void appendDeltaTextToHtmlBuilder(AbstractDelta<String> delta) {
		List<String> sourceLines = delta.getSource().getLines();
		List<String> targetLines = delta.getTarget().getLines();
		
		switch(delta.getType()) {
		case CHANGE:
			Builder diffRowGeneratorBuilder = DiffRowGenerator.create();
			diffRowGeneratorBuilder = disableNormalization(diffRowGeneratorBuilder);
			diffRowGeneratorBuilder = setInlineDiff(diffRowGeneratorBuilder, !params.isLinewiseDiff());
			DiffRowGenerator diffGen = diffRowGeneratorBuilder.build();
			
			List<DiffRow> diffRows = diffGen.generateDiffRows(sourceLines, targetLines);

			int origLinesCounterBefore = origLinesCounter;

			for (int i = 0; i < sourceLines.size() && i < diffRows.size(); i++) {
				htmlBuilder.appendDeletionLine("-" + diffRows.get(i).getOldLine(), getOrigLineNr(origLinesStart),
						getRevLineNr(revLinesStart));
				origLinesCounter++;
			}

			for (int j = 0; j < targetLines.size() && j < diffRows.size(); j++) {
				htmlBuilder.appendInsertionLine("+" + diffRows.get(j).getNewLine(),
						origLinesStart + contextLinesCounter + origLinesCounterBefore, getRevLineNr(revLinesStart));
				revLinesCounter++;
			}

			break;
		case DELETE:
			for (String line : sourceLines) {
				htmlBuilder.appendDeletionLine("-" + line, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
				origLinesCounter++;
			}
			break;
		case INSERT:
			for (String line : targetLines) {
				htmlBuilder.appendInsertionLine("+" + line, getOrigLineNr(origLinesStart), getRevLineNr(revLinesStart));
				revLinesCounter++;
			}
			break;
		case EQUAL:
		default:
			break;
		
		}
		origLinesTotal += sourceLines.size();
		revLinesTotal += targetLines.size();
	}
	
	private Builder setInlineDiff(Builder builder, boolean isInlineDiff) {
		return isInlineDiff ? 
				builder.showInlineDiffs(true)
				.oldTag(f -> f ? DELETION_OPEN_TAG : DELETION_CLOSE_TAG)
				.newTag(f -> f ? INSERTION_OPEN_TAG : INSERTION_CLOSE_TAG) 
				: builder;
	}
	
	private Builder disableNormalization(Builder builder) {
		return builder.lineNormalizer(s -> s);
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
