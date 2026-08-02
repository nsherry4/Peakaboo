package org.peakaboo.datalabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class DataTagsTest {

	private static final DataTag PLOT_SMOOTHED = new DataTag(DataScope.PLOT, DataLabel.SMOOTHED);
	private static final DataTag PLOT_BACKGROUND = new DataTag(DataScope.PLOT, DataLabel.BACKGROUND_REMOVED);
	private static final DataTag MAP_SMOOTHED = new DataTag(DataScope.MAP, DataLabel.SMOOTHED);
	private static final DataTag MAP_DESKEWED = new DataTag(DataScope.MAP, new DataLabel("Deskewed"));

	@Test
	public void testUniquePreservesFirstOccurrenceOrder() {
		List<DataTag> unique = DataTags.unique(List.of(
				PLOT_SMOOTHED,
				PLOT_BACKGROUND,
				new DataTag(DataScope.PLOT, new DataLabel("Smoothed")),
				PLOT_SMOOTHED
			));
		assertEquals(List.of(PLOT_SMOOTHED, PLOT_BACKGROUND), unique);
	}

	@Test
	public void testSameLabelInDifferentScopesBothSurvive() {
		//Smoothing a spectrum and smoothing a map are different things, so both are kept
		List<DataTag> unique = DataTags.unique(List.of(PLOT_SMOOTHED, MAP_SMOOTHED));
		assertEquals(List.of(PLOT_SMOOTHED, MAP_SMOOTHED), unique);
	}

	@Test
	public void testGather() {
		DataLabelProvider smoothing = provider(DataLabel.SMOOTHED);
		DataLabelProvider background = provider(DataLabel.BACKGROUND_REMOVED, DataLabel.SMOOTHED);
		DataLabelProvider unlabelled = new DataLabelProvider() {};

		List<DataTag> gathered = DataTags.gather(DataScope.PLOT, List.of(smoothing, background, unlabelled));
		assertEquals(List.of(PLOT_SMOOTHED, PLOT_BACKGROUND), gathered);
	}

	private static DataLabelProvider provider(DataLabel... labels) {
		return new DataLabelProvider() {
			@Override
			public List<DataLabel> getDataLabels() {
				return List.of(labels);
			}
		};
	}

	@Test
	public void testSummaryEmpty() {
		assertFalse(DataTags.summary(List.of(), DataScope.MAP).isPresent());
	}

	@Test
	public void testSummaryImpliedScopeIsBare() {
		Optional<String> summary = DataTags.summary(List.of(MAP_SMOOTHED, MAP_DESKEWED), DataScope.MAP);
		assertTrue(summary.isPresent());
		assertEquals("Smoothed, Deskewed", summary.get());
	}

	@Test
	public void testSummaryOtherScopeIsPrefixedEvenWhenAlone() {
		//A bare label on a map reads as map-stage, so a lone plot label still needs its prefix
		Optional<String> summary = DataTags.summary(List.of(PLOT_SMOOTHED), DataScope.MAP);
		assertEquals("Plot: Smoothed", summary.get());
	}

	@Test
	public void testSummaryGroupsWithImpliedScopeFirst() {
		Optional<String> summary = DataTags.summary(
				List.of(PLOT_BACKGROUND, PLOT_SMOOTHED, MAP_SMOOTHED, MAP_DESKEWED),
				DataScope.MAP
			);
		assertEquals("Smoothed, Deskewed; Plot: Background Removed, Smoothed", summary.get());
	}

	@Test
	public void testSummaryDedupsBeforeRendering() {
		Optional<String> summary = DataTags.summary(
				List.of(MAP_SMOOTHED, MAP_SMOOTHED, MAP_DESKEWED),
				DataScope.MAP
			);
		assertEquals("Smoothed, Deskewed", summary.get());
	}

}
