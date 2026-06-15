package org.peakaboo.datalabel;

import java.util.Objects;

/**
 * A disclaimer-style label marking data as having been altered or derived in
 * some way (e.g. smoothed, background removed, speculative). Components which
 * process data expose their labels through {@link DataLabelProvider} so that
 * displays can show the user how the data they are looking at was produced.
 * <p>
 * A curated set of predefined labels is provided here and should be preferred
 * where one fits; custom labels can be created with the public constructors
 * when none does. Labels are deduplicated by {@link #getId()}, so two labels
 * with the same id are considered the same label regardless of origin.
 */
public final class DataLabel {

	// Plot-filter labels
	public static final DataLabel SMOOTHED           = new DataLabel("smoothed", "Smoothed");
	public static final DataLabel BACKGROUND_REMOVED = new DataLabel("background-removed", "Background Removed");
	public static final DataLabel OTHER_FILTERING    = new DataLabel("other-filtering", "Other Filtering");

	// Map-filter labels (SMOOTHED is shared across both filter systems)
	public static final DataLabel SIZED       = new DataLabel("sized", "Sized");
	public static final DataLabel CLIPPED     = new DataLabel("clipped", "Clipped");
	public static final DataLabel FILTERED    = new DataLabel("filtered", "Filtered");
	public static final DataLabel TRANSFORMED = new DataLabel("transformed", "Transformed");
	public static final DataLabel SHARPENED   = new DataLabel("sharpened", "Sharpened");

	// Solver labels
	public static final DataLabel SPECULATIVE = new DataLabel("speculative", "Speculative");

	private final String id;
	private final String text;

	/**
	 * Creates a custom label, deriving its id from the display text. Labels with
	 * text differing only in case or surrounding whitespace will share an id and
	 * deduplicate as one.
	 */
	public DataLabel(String text) {
		this(deriveId(text), text);
	}

	/**
	 * Creates a custom label with an explicit id.
	 * @param id stable identity used for deduplication
	 * @param text human-readable display text
	 */
	public DataLabel(String id, String text) {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("DataLabel id must not be null or blank");
		}
		if (text == null || text.isBlank()) {
			throw new IllegalArgumentException("DataLabel text must not be null or blank");
		}
		this.id = id;
		this.text = text;
	}

	private static String deriveId(String text) {
		if (text == null) {
			throw new IllegalArgumentException("DataLabel text must not be null or blank");
		}
		return text.trim().toLowerCase();
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof DataLabel label && id.equals(label.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return text;
	}

}
