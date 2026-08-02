package org.peakaboo.datalabel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Static utility methods for collecting, deduplicating and summarizing {@link DataTag}s.
 *
 * @author NAS
 */
public final class DataTags {
	
	private DataTags() {}
	
	/**
	 * Tags the given labels with a scope, deduplicated by tag identity and preserving
	 * first-occurrence order.
	 */
	public static List<DataTag> of(DataScope scope, List<DataLabel> labels) {
		return unique(labels.stream().map(label -> new DataTag(scope, label)).toList());
	}
	
	/**
	 * Collects the labels from the given providers in order and tags them all with the
	 * given scope, as the providers only give the labels.
	 */
	public static List<DataTag> gather(DataScope scope, List<? extends DataLabelProvider> providers) {
		List<DataLabel> labels = new ArrayList<>();
		for (DataLabelProvider provider : providers) {
			labels.addAll(provider.getDataLabels());
		}
		return of(scope, labels);
	}
	
	/**
	 * Returns the given tags deduplicated by tag identity, preserving first-occurrence
	 * order.
	 */
	public static List<DataTag> unique(List<DataTag> tags) {
		return List.copyOf(new LinkedHashSet<>(tags));
	}
	
	/**
	 * Renders the given tags as a single human-readable string, grouped by scope. The
	 * scope {@code implied} is the one the UI implies, so its tags are written first
	 * and without qualification. Other scopes are prefixed with the scope name.
	 */
	public static Optional<String> summary(List<DataTag> tags, DataScope implied) {
		
		List<DataTag> unique = unique(tags);
		if (unique.isEmpty()) {
			return Optional.empty();
		}
		
		Map<DataScope, List<DataTag>> groups = new LinkedHashMap<>();
		for (DataTag tag : unique) {
			groups.computeIfAbsent(tag.scope(), scope -> new ArrayList<>()).add(tag);
		}
		
		List<String> parts = new ArrayList<>();
		List<DataTag> impliedTags = groups.remove(implied);
		if (impliedTags != null) {
			parts.add(join(impliedTags));
		}
		for (var group : groups.entrySet()) {
			parts.add(group.getKey().getText() + ": " + join(group.getValue()));
		}
		
		return Optional.of(String.join("; ", parts));
		
	}
	
	private static String join(List<DataTag> tags) {
		return tags.stream().map(DataTag::getText).collect(Collectors.joining(", "));
	}
	
}
