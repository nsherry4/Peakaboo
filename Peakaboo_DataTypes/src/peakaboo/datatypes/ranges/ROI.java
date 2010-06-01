package peakaboo.datatypes.ranges;

import peakaboo.datatypes.peaktable.Transition;

/**
 * 
 *  This class defines a Region Of Interest -- a band of selected channels over an XRF plot
 * @author Nathaniel Sherry, 2009
 */


public class ROI {

	private int start;
	private int stop;
	
	/**
	 * The title of this Region of Interest is a {@link Transition}. All ROIs should ideally be matched against a particular Transition
	 */
	public Transition title;
	
	/**
	 * A position in Y for the title position -- allows titles to be moved up and down over a plot
	 */
	public double titlePosition;
	
	private int maxChannel;

	public ROI()
	{
		start = 0;
		stop = 1;
		title = null;
		titlePosition = 0.075;
		maxChannel = 2047;
	}
	
	/**
	 * Sets the bounds of this ROI. Problems such as reversed start/stop values will be corrected.
	 * @param start start of ROI
	 * @param stop end of ROI
	 */
	public void setBounds(int start, int stop)
	{
		if (start > maxChannel) start = maxChannel;
		if (stop > maxChannel) stop = maxChannel;

		if (start < 0) start = 0;
		if (stop < 0) stop = 0;
		
		if (start < stop){
			this.start = start;
			this.stop = stop;
		} else {
			this.stop = start;
			this.start = stop;
		}
	}
	
	/**
	 * Gets the starting value of this ROI
	 * @return start of this ROI
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Determines if this ROI overlaps with another ROI
	 * @param otherRegion the other ROI
	 * @return true if there is an overlap, false otherwise. Regions which have a single shared boundary value are considered to be overlapping
	 */
	public boolean overlaps(ROI otherRegion){
		
		if (otherRegion.getStart() >= start && otherRegion.getStart() <= stop) return true;
		if (otherRegion.getStop() >= start && otherRegion.getStop() <= stop) return true;
		if (otherRegion.getStart() < start && otherRegion.getStop() > stop) return true;
		if (otherRegion.getStart() > start && otherRegion.getStop() < stop) return true;
		return false;
		
	}
	
	/**
	 * Merges this ROI with the given ROI. The given ROI can be discarded if it is no longer needed.
	 * @param region the region to merge into this one
	 */
	public void mergeWith(ROI region)
	{
		if (region.getStart() < start) start = region.getStart();
		if (region.getStop() > stop) stop = region.getStop();
	}

	/**
	 * Sets the starting value of this ROI. Problems such as reversed start/stop values will be corrected.
	 * @param value the new start value
	 */
	public void setStart(int value) {
		
		if (value > maxChannel) value = maxChannel;
		if (value < 0) value = 0;
		
		if (value > this.stop){
			//flip them around if new start is gt stop
			this.start = this.stop;
			this.stop = value;
			
		}
		else {
			this.start = value;
		}
	}

	/**
	 * Get the stop value for this ROI
	 * @return the stop value
	 */
	public int getStop() {
		return stop;
	}

	/**
	 * Sets the stopping value of this ROI. Problems such as reversed start/stop values will be corrected.
	 * @param value the new stop value
	 */
	public void setStop(int value) {
		
		if (value > maxChannel) value = maxChannel;
		if (value < 0) value = 0;
		
		if (value < this.start)
		{
			//flip them around if new stop is lt start
			this.stop = this.start;
			this.start = value;
		} else {
			this.stop = value;
		}
	}
		
	
}
