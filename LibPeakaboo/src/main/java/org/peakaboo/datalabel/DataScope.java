package org.peakaboo.datalabel;

/**
 * The stage of processing a {@link DataLabel} was applied at. Smoothing a spectrum
 * and smoothing a map are very different things, so a label on its own isn't enough
 * to tell the user what happened to their data.
 *
 * @author NAS
 */
public enum DataScope {

	PLOT("Plot"),
	MAP("Map");

	private final String text;

	DataScope(String text) {
		this.text = text;
	}

	/** Returns the name of this scope */
	public String getText() {
		return text;
	}

}
