package org.peakaboo.datasource.model.internal;

import org.peakaboo.datasource.model.DataSource;


/**
 * Represents a DataSource which is derived from another by sampling some of the points within it
 * @author NAS
 *
 */
public interface SubsetDataSource extends DataSource {

	
	/**
	 * Given an index in the subset data source, return the index of the same point in the original data source.
	 */
	int getBackingIndex(int index);
	
	
	/**
	 * Given an index in the original data source, return the index of the same point in the subset data source.
	 * @return updated index, or -1 if the index does not appear in the subset data source
	 */
	int getUpdatedIndex(int originalIndex);

	/**
	 * Part of the initialization of a SubsetDataSource is reanalyzing the
	 * constrained data. This method reanalyzes all indexes in the datasource
	 */
	void reanalyze();
	
	/**
	 * Part of the initialization of a SubsetDataSource is reanalyzing the
	 * constrained data. This method reanalyzes one specific index in the datasource
	 */
	void reanalyze(int i);
	
	
}
