package org.peakaboo.datalabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class DataLabelsTest {

	@Test
	public void testCustomLabelDerivesId() {
		DataLabel label = new DataLabel("  Smoothed ");
		assertEquals("smoothed", label.getId());
		assertEquals("  Smoothed ", label.getText());
	}

	@Test
	public void testEqualityOnIdOnly() {
		assertEquals(DataLabel.SMOOTHED, new DataLabel("Smoothed"));
		assertEquals(DataLabel.SMOOTHED, new DataLabel("smoothed", "Smoothed Out"));
		assertNotEquals(DataLabel.SMOOTHED, DataLabel.SHARPENED);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTextRejected() {
		new DataLabel(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlankTextRejected() {
		new DataLabel("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlankIdRejected() {
		new DataLabel("", "Some Text");
	}

	@Test
	public void testUniquePreservesFirstOccurrenceOrder() {
		List<DataLabel> unique = DataLabels.unique(List.of(
				DataLabel.SMOOTHED,
				DataLabel.BACKGROUND_REMOVED,
				new DataLabel("Smoothed"),
				DataLabel.SMOOTHED
			));
		assertEquals(List.of(DataLabel.SMOOTHED, DataLabel.BACKGROUND_REMOVED), unique);
	}

	@Test
	public void testGather() {
		DataLabelProvider smoothing = provider(DataLabel.SMOOTHED);
		DataLabelProvider background = provider(DataLabel.BACKGROUND_REMOVED, DataLabel.SMOOTHED);
		DataLabelProvider unlabelled = new DataLabelProvider() {};

		List<DataLabel> gathered = DataLabels.gather(List.of(smoothing, background, unlabelled));
		assertEquals(List.of(DataLabel.SMOOTHED, DataLabel.BACKGROUND_REMOVED), gathered);
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
		assertFalse(DataLabels.summary(List.of()).isPresent());
	}

	@Test
	public void testSummary() {
		Optional<String> summary = DataLabels.summary(List.of(
				DataLabel.SMOOTHED,
				DataLabel.BACKGROUND_REMOVED,
				new DataLabel("Smoothed")
			));
		assertTrue(summary.isPresent());
		assertEquals("Smoothed, Background Removed", summary.get());
	}

}
