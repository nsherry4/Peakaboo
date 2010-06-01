package peakaboo.datatypes.ranges;


import java.util.List;
import java.util.Iterator;
import java.util.Stack;

import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * This class is a collection of Regions of Interest. An internal solution, rather than a simple list is
 * preferable because there is enough logic associated with addition of new ROIs (eg overlap, boundary issues,
 * etc) that it would be overly complicated to place that logic somewhere (or perhaps several somewheres)
 * else.
 * 
 * ROISet is Iterable over a list of ROIs to allow for easy external access/search/etc.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class ROISet implements Iterable<ROI>
{

	private List<ROI>	regions;
	private Stack<ROI>	selectedRegions;

	/**
	 * A marker to indicate that someone has begun creating a new {@link ROI}, and that this is one of its bounds
	 */
	public int			partialRegionMarker;


	public ROISet()
	{

		regions = DataTypeFactory.<ROI> list();
		selectedRegions = new Stack<ROI>();

		partialRegionMarker = -1;
	}


	/**
	 * Add a new {@link ROI} to the set. If this region overlaps with an existing region, it will be merged into it.
	 * @param sourceRegion region to add
	 */
	public void addRegion(ROI sourceRegion)
	{

		List<ROI> duds = DataTypeFactory.<ROI> list();

		for (ROI region : regions) {

			// this region overlaps with an existing region
			// we want to keep the information in the original region
			if (region.overlaps(sourceRegion)) {

				region.mergeWith(sourceRegion);
				// if this is the 2nd or more region to overlap, sourceRegion will be in the roi list already,
				// and it needs to go
				duds.add(sourceRegion);
				sourceRegion = region;

			}
		}

		regions.removeAll(duds);

		// if newregion isn't already in the set of regions (like it would be if there were overlap)
		// add it to the list of regions
		if (regions.indexOf(sourceRegion) == -1) regions.add(sourceRegion);
		// also add it to the selected regions regardless of newness
		selectRegion(sourceRegion);

	}


	/** 
	 * Adds a new {@link ROI} from just the starting and stopping values. This is a convenience method for {@link #addRegion(ROI)}
	 * @param start staring value of the new ROI
	 * @param stop stopping value of the new ROI
	 */
	public void addRegion(int start, int stop)
	{
		ROI region = new ROI();
		region.setBounds(start, stop);
		addRegion(region);
	}



	/**
	 * Selects the {@link ROI} under the given channel value
	 * @param channel the channel value to select at.
	 */
	public void selectRegionAtChannel(int channel)
	{
		ROI region = getROIAtChannel(channel);
		if (selectedRegions.indexOf(region) == -1) {
			// region was not on selected stack
			selectRegion(region);
		} else {
			// region was on selected stack, take it out and re-add it
			// to make sure it is on the top now
			selectRegion(region);
		}
	}


	/**
	 * Selects the given region. If this ROISet does not contain the specified {@link ROI}, the request will be ignored.
	 * @param region {@link ROI} to select
	 */
	private void selectRegion(ROI region)
	{
		if (! regions.contains(region)) return;
		
		selectedRegions.remove(region);
		selectedRegions.add(region);
		regions.remove(region);
		regions.add(region);
	}


	/**
	 * Deselects the {@link ROI} at a given channel.
	 * @param channel channel to deselect at
	 */
	public void deselectRegionAtChannel(int channel)
	{
		ROI region = getROIAtChannel(channel);
		if (selectedRegions.indexOf(region) != -1) {
			// region was on selected stack, take it out
			deselectRegion(region);
		}
	}


	/**
	 * Deselects the given {@link ROI}. If the region is not selected, or is not part of the set, the request is ignored.
	 * @param region
	 */
	private void deselectRegion(ROI region)
	{
		selectedRegions.remove(region);
	}


	/**
	 * Determines the selection status of the given {@link ROI}
	 * @param region region to check the selection of
	 * @return true if the region is selected, false otherwise
	 */
	public boolean isRegionSelected(ROI region)
	{
		if (selectedRegions.size() == 0) return false;
		return (selectedRegions.indexOf(region) != -1);
	}

	/**
	 * Check to see if this {@link ROI} is the most active selected region.
	 * @param region the region to check on
	 * @return true if this {@link ROI} is the active selected region, false otherwise.
	 */
	public boolean isRegionTopSelected(ROI region)
	{
		if (selectedRegions.size() == 0) return false;
		return (region == selectedRegions.peek());
	}

	/**
	 * Gets the nubmer of selected {@link ROI}s
	 * @return number of selected {@link ROI}ss
	 */
	public int selectionCount()
	{
		return selectedRegions.size();
	}


	/**
	 * Deselect all {@link ROI}s
	 */
	public void deselectAll()
	{
		selectedRegions.clear();
	}


	/**
	 * Delete all {@link ROI}s
	 */
	public void deleteAll()
	{
		deselectAll();
		regions.clear();
	}


	/**
	 * Delete all selected {@link ROI}s
	 */
	public void deleteSelected()
	{
		regions.removeAll(selectedRegions);
		deselectAll();
	}


	/**
	 * Shanges the title/caption height for the {@link ROI} under the channel in question
	 * @param height new height
	 * @param channel channel to look for an {@link ROI} in
	 */
	public void setTitleHeightForRegionAtChannel(double height, int channel)
	{
		ROI region = getROIAtChannel(channel);
		if (region == null) return;
		region.titlePosition = height;
	}


	/**
	 * Returns the {@link ROI} at the given channel, if one exists
	 * @param channel the channel to look at
	 * @return an {@link ROI} from the given channel
	 */
	public ROI getROIAtChannel(int channel)
	{

		Iterator<ROI> i = regions.iterator();
		ROI roi;

		while (i.hasNext()) {

			roi = i.next();

			if (channel >= roi.getStart() && channel <= roi.getStop()) {
				return roi;
			}
		}
		return null;
	}


	/**
	 * Returns an iterator over the set of {@link ROI}s
	 */
	public Iterator<ROI> iterator()
	{
		return regions.iterator();
	}


	/**
	 * Returns a list of all selected {@link ROI}s
	 * @return a list of selected {@link ROI}s
	 */
	public List<ROI> getSelectedRegions()
	{

		List<ROI> sel = DataTypeFactory.<ROI> list();

		for (ROI region : selectedRegions) {
			sel.add(region);
		}

		return sel;

	}

	/**
	 * Returns the active selected {@link ROI}
	 * @return the active selected {@link ROI}
	 */
	public ROI getTopROI()
	{
		if (selectedRegions.size() == 0) return null;
		return selectedRegions.peek();
	}
}
