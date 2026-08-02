package org.peakaboo.filter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.peakaboo.datalabel.DataLabel;
import org.peakaboo.datalabel.DataScope;
import org.peakaboo.datalabel.DataTag;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public class FilterSetDataTagTest {

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
	public void testNoFiltersNoTags() {
		assertTrue(new FilterSet().getDataTags().isEmpty());
	}

	@Test
	public void testTagsFromDescriptors() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		filters.add(new StubFilter(FilterDescriptor.BACKGROUND));
		assertEquals(List.of(plot(DataLabel.SMOOTHED), plot(DataLabel.BACKGROUND_REMOVED)), filters.getDataTags());
	}

	@Test
	public void testDuplicateDescriptorsDedup() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING));
		assertEquals(List.of(plot(DataLabel.SMOOTHED)), filters.getDataTags());
	}

	@Test
	public void testDisabledFiltersExcluded() {
		FilterSet filters = new FilterSet();
		Filter smoothing = new StubFilter(FilterDescriptor.SMOOTHING);
		smoothing.setEnabled(false);
		filters.add(smoothing);
		assertTrue(filters.getDataTags().isEmpty());
	}

	@Test
	public void testPreviewOnlyFiltersExcluded() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(FilterDescriptor.SMOOTHING, true));
		assertTrue(filters.getDataTags().isEmpty());
	}

	@Test
	public void testCustomActionStringBecomesCustomLabel() {
		FilterSet filters = new FilterSet();
		filters.add(new StubFilter(new FilterDescriptor(FilterType.OTHER, "Despeckled")));
		assertEquals(List.of(plot(new DataLabel("Despeckled"))), filters.getDataTags());
	}

	//A spectrum FilterSet scopes everything it gathers to the plot stage, including
	//labels a plugin made up itself
	private static DataTag plot(DataLabel label) {
		return new DataTag(DataScope.PLOT, label);
	}

}
