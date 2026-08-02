package org.peakaboo.datalabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class DataLabelTest {

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

}
