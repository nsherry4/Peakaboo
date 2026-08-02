package org.peakaboo.datalabel;

import java.util.Objects;

/**
 * A {@link DataLabel} paired with the {@link DataScope} it was applied at. Tags are
 * built application-side by the component which knows what stage it represents, so
 * plugins only ever supply the scope-free label.
 * <p>
 * Because {@link DataLabel} compares by id, two tags are the same only when both the
 * label and the scope match. That's what keeps a smoothed spectrum and a smoothed map
 * from collapsing into one entry when tags are deduplicated.
 *
 * @author NAS
 */
public record DataTag(DataScope scope, DataLabel label) {

	public DataTag {
		Objects.requireNonNull(scope, "DataTag scope must not be null");
		Objects.requireNonNull(label, "DataTag label must not be null");
	}

	public String getText() {
		return label.getText();
	}

	@Override
	public String toString() {
		return scope.getText() + ": " + label.getText();
	}

}
