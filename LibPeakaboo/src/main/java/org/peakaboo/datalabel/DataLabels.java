package org.peakaboo.datalabel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * Static utility methods for collecting, deduplicating and summarizing
 * {@link DataLabel}s.
 */
public final class DataLabels {

	private DataLabels() {}

	/**
	 * Returns the given labels deduplicated by label identity, preserving
	 * first-occurrence order.
	 */
	public static List<DataLabel> unique(List<DataLabel> labels) {
		return List.copyOf(new LinkedHashSet<>(labels));
	}

	/**
	 * Collects the labels from the given providers in order, deduplicated by
	 * label identity.
	 */
	public static List<DataLabel> gather(List<? extends DataLabelProvider> providers) {
		List<DataLabel> labels = new ArrayList<>();
		for (DataLabelProvider provider : providers) {
			labels.addAll(provider.getDataLabels());
		}
		return unique(labels);
	}

	/**
	 * Renders the given labels as a single human-readable string like
	 * "Smoothed, Background Removed", deduplicated and in first-occurrence
	 * order. Returns an empty Optional when there are no labels.
	 */
	public static Optional<String> summary(List<DataLabel> labels) {
		return unique(labels).stream()
				.map(DataLabel::getText)
				.reduce((a, b) -> a + ", " + b);
	}

}
