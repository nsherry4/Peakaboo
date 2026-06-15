package org.peakaboo.datalabel;

import java.util.List;

/**
 * Capability interface for components which mark the data they process with
 * {@link DataLabel}s. Implementors describe what they do to data; collection
 * and presentation of labels is handled by {@link DataLabels} and the
 * relevant controllers.
 */
public interface DataLabelProvider {

	/**
	 * Returns the labels this component applies to data it processes. The default
	 * implementation returns no labels.
	 */
	default List<DataLabel> getDataLabels() {
		return List.of();
	}

}
