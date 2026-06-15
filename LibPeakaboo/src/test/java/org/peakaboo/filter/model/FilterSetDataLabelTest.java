package org.peakaboo.filter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.peakaboo.datalabel.DataLabel;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class FilterSetDataLabelTest {

	private static class StubFilter extends AbstractFilter {

		private final FilterDescriptor descriptor;
		private final boolean previewOnly;

		StubFilter(FilterDescriptor descriptor) {
			this(descriptor, false);
		}

		StubFilter(FilterDescriptor descriptor, boolean previewOnly) {
			this.descriptor = descriptor;
			this.previewOnly = previewOnly;
		}

		@Override
		public String getFilterName() {
			return "Stub";
		}

		@Override
		public String getFilterDescription() {
			return "Stub filter for testing";
		}

		@Override
		public FilterDescriptor getFilterDescriptor() {
			return descriptor;
		}

		@Override
		public String getFilterUUID() {
			return "stub-filter-uuid";
		}

		@Override
		public String pluginVersion() {
			return "1.0";
		}

		@Override
		public void initialize() {}

		@Override
		public boolean canFilterSubset() {
			return true;
		}

		@Override
		public boolean isPreviewOnly() {
			return previewOnly;
		}

		@Override
		protected SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
			return data;
		}

	}

	@Test
	public void testNoFiltersNoLabels() {
		assertTrue(new FilterSet().getDataLabels().isEmpty());
	}

	@Test
	public void testLabelsFromDescriptors() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		filters.add(new StubFilter(FilterDescriptor.BACKGROUND));
		assertEquals(List.of(DataLabel.SMOOTHED, DataLabel.BACKGROUND_REMOVED), filters.getDataLabels());
	}

	@Test
	public void testDuplicateDescriptorsDedup() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		assertEquals(List.of(DataLabel.SMOOTHED), filters.getDataLabels());
	}

	@Test
	public void testDisabledFiltersExcluded() {
		FilterSet filters = new FilterSet();
		Filter smoothing = new StubFilter(FilterDescriptor.SMOOTHING);
		smoothing.setEnabled(false);
		filters.add(smoothing);
		assertTrue(filters.getDataLabels().isEmpty());
	}

	@Test
	public void testPreviewOnlyFiltersExcluded() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING, true));
		assertTrue(filters.getDataLabels().isEmpty());
	}

	@Test
	public void testCustomActionStringBecomesCustomLabel() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(new FilterDescriptor(FilterType.OTHER, "Despeckled")));
		assertEquals(List.of(new DataLabel("Despeckled")), filters.getDataLabels());
	}

}
