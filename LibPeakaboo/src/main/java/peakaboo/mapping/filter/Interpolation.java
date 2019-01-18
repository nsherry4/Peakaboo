package peakaboo.mapping.filter;

import java.util.ArrayList;
import java.util.List;

import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.Spectrum;

/**
 * 
 *  This class holds algorithms designed to interpolate a given data set.
 *
 * @author Nathaniel Sherry, 2009
 *
 */

public class Interpolation {

	/**
	 * Linear interpolation of a grid of values by averaging neighbouring values. Grid size expands from x,y to x*2-1, y*2-1
	 * @param grid the {@link GridPerspective} defining the dimensions for the data
	 * @param list the {@link Spectrum} of data
	 * @return an interpolated grid
	 */
	public static Pair<GridPerspective<Float>, Spectrum> interpolateGridLinear(GridPerspective<Float> grid, Spectrum list){
		
		GridPerspective<Float> upGrid = new GridPerspective<Float>(grid.width * 2 - 1, grid.height * 2 - 1, 0.0f);
		Spectrum upList = new ISpectrum(upGrid.width * upGrid.height, 0.0f);
		
		
		//copy over the original data into a spaced out pattern
		for (int x =0; x < grid.width; x++){
			for (int y=0; y < grid.height; y++){
				upGrid.set(upList, x*2, y*2, grid.get(list, x, y));
			}
		}
		
		//interpolate the missing data
		for (int x=0; x < grid.width; x++){
			for (int y=0; y < grid.height; y++){
				
				float average;
								
				if (x == grid.width-1 && y == grid.height-1){
					//nothing to do here, at the bottom right corner of the grid
				} else if (x == grid.width-1){
					//along the right edge of the grid
					average = ( grid.get(list, x, y) + grid.get(list, x, y+1) ) / 2.0f;
					upGrid.set(upList, x*2, y*2+1, average);
				} else if (y == grid.height-1){
					//along the bottom edge of the grid
					average = ( grid.get(list, x, y) + grid.get(list, x+1, y) ) / 2.0f;
					upGrid.set(upList, x*2+1, y*2, average);
				} else {
					//somewhere in the middle of the grid

					average = ( grid.get(list, x, y) + grid.get(list, x, y+1) ) / 2.0f;
					upGrid.set(upList, x*2, y*2+1, average);
					
					average = ( grid.get(list, x, y) + grid.get(list, x+1, y) ) / 2.0f;
					upGrid.set(upList, x*2+1, y*2, average);
					
					average = ( grid.get(list, x, y) + grid.get(list, x+1, y) + grid.get(list, x, y+1) + grid.get(list, x+1, y+1) ) / 4.0f;
					upGrid.set(upList, x*2+1, y*2+1, average);
				}
				
			}
		}
		
		return new Pair<GridPerspective<Float>, Spectrum>(upGrid, upList);
		
	}
	
	
	/**
	 * Given a set of data, a definition of its 2D dimensions, and a list of bad indices, interpolate the data
	 * to replace the bad indices with approximations based off of neighbouring values. In place.
	 * @param grid the {@link GridPerspective} defining the dimensions for the data
	 * @param list the {@link Spectrum} of data
	 * @param badPoints the list of indices of bad points
	 */
	public static void interpolateBadPoints(GridPerspective<Float> grid, Spectrum list, List<Integer> badPoints)
	{
		
		Pair<Integer, Integer> coords;
		int x, y;
		float newval;
		List<Integer> newBadPoints = new ArrayList<Integer>();
		boolean repeat = false;
		
		for (int i : badPoints){
		
			coords = grid.getXYFromIndex(i);
			x = coords.first;
			y = coords.second;
			
			newval = interpolateBadPoint(grid, list, badPoints, x, y);
			if (newval == -1) 
			{
				repeat = true;
				newBadPoints.add(i);
			}
			list.set(i, newval);
			
		}
		
		if (repeat) interpolateBadPoints(grid, list, newBadPoints);
		
		
	}
	
	
	/**
	 * interpolate a single bad point
	 * @param grid dimensions of data
	 * @param list Spectrum containing data
	 * @param badPoints list of bad points
	 * @param x x coordinate value to replace with interpolation
	 * @param y y coordinate value to replace with interpolation
	 * @return a new value for the given point
	 */
	private static float interpolateBadPoint(GridPerspective<Float> grid, Spectrum list, List<Integer> badPoints, int x, int y)
	{
		
		float total = 0;
		int count = 0;
		
		if (x >= 1 && !badPoints.contains(grid.getIndexFromXY(x-1, y)))
			{ total += grid.get(list, x-1, y); count += 1; }
		
		if (y >= 1 && !badPoints.contains(grid.getIndexFromXY(x, y-1))) 
			{ total += grid.get(list, x, y-1); count += 1; }
		
		
		if (x <= grid.width - 2 && !badPoints.contains(grid.getIndexFromXY(x+1, y))) 
			{ total += grid.get(list, x+1, y); count += 1; }
		
		if (y <= grid.height- 2 && !badPoints.contains(grid.getIndexFromXY(x, y+1))) 
			{ total += grid.get(list, x, y+1); count += 1; }
				
		if (count > 0)
			return total / count;
		else
			return -1;
		
	}
	
}
