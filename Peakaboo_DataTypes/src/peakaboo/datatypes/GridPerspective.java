package peakaboo.datatypes;


import java.util.List;

/**
 * 
 * This class provides a way to treat any List as a 2D grid of data without needing to keep the data in a
 * 2D-centric format and without the need to constantly make sure that two pointers/references are to the data
 * are synced up.
 * 
 * @author Nathaniel Sherry, 2009
 * @param <T>
 *            The type of data to be accessed using this GridPerspective
 */

public class GridPerspective<T> implements Cloneable
{

	public int	width;
	public int	height;
	private T	fallback;


	
	public T getFallback()
	{
		return fallback;
	}

	
	public void setFallback(T fallback)
	{
		this.fallback = fallback;
	}

	/**
	 * Creates a new grid perspective.
	 * @param width width of the grid
	 * @param height height of the grid
	 * @param fallbackValue value to be returned when a non-existent value is requested
	 */
	public GridPerspective(int width, int height, T fallbackValue)
	{
		this.width = width;
		this.height = height;
		this.fallback = fallbackValue;
	}
	
	/**
	 * Gets the value from the given list at the given coordinates, using the previously provided width and
	 * height values. If the position is outside the bounds of the list, or is outside the bounds of the
	 * specified height and width for the grid, the fallback value will be returned.
	 * 
	 * @param list
	 *            the list to retrieve the data from
	 * @param x
	 *            the x position in the grid to look in
	 * @param y
	 *            the y position in the grid to look in
	 * @return a value of type T from the desired position in the list
	 */
	public T get(List<T> list, int x, int y)
	{
		int index = y * width + x;

		if (index >= list.size()) return fallback;
		if (x < 0 || y < 0) return fallback;
		if (x >= this.width || y >= this.height) return fallback;

		T value = list.get(index);
		if (value == null) return fallback;

		return value;
	}


	/**
	 * Sets the value in the given list at the given coordinates, using the previously provided width and
	 * height values. If the position is outside the bounds of the list, or is outside the bounds of the
	 * specified height and width for the grid, the request will be ignored.
	 * @param list
	 * @param x
	 * @param y
	 * @param value
	 */
	public void set(List<T> list, int x, int y, T value)
	{
		int index = getIndexFromXY(x, y);

		if (index >= list.size()) return;
		if (x < 0 || y < 0) return;
		if (x >= this.width || y >= this.height) return;

		list.set(index, value);
	}

	public Pair<Integer, Integer> getXYFromIndex(int index)
	{
		int x = index % width;
		int y = ((index - x) / width);
		return new Pair<Integer, Integer>(x, y);
		
	}
	
	public int getIndexFromXY(int x, int y)
	{
		if (x < 0 || x >= width) return -1;
		if (y < 0 || y >= height) return -1;
		return y * width + x;
	}
	
	@Override
	public GridPerspective<T> clone()
	{
	
		GridPerspective<T> g = new GridPerspective<T>(width, height, fallback);
		
		return g;
		
	}
	
}
