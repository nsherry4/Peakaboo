package org.peakaboo.framework.cyclops;


import java.util.List;
import java.util.function.BiConsumer;




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


	public int size() {
		return width * height;
	}
	
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
		if (!boundsCheck(x, y)) return fallback;

		T value = list.get(index);
		if (value == null) return fallback;

		return value;
	}

	public Float get(ReadOnlySpectrum spec, int x, int y)
	{
		int index = y * width + x;

		if (index >= spec.size()) return 0.0f;
		if (!boundsCheck(x, y)) return 0f;

		float value = spec.get(index);

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
		if (!boundsCheck(x, y)) return;

		list.set(index, value);
	}
	
	public void set(Spectrum list, int x, int y, float value)
	{
		int index = getIndexFromXY(x, y);

		if (index >= list.size()) return;
		if (!boundsCheck(x, y)) return;

		list.set(index, value);
	}

	
	public interface GridVisitor<S> {
		void visit(int index, int x, int y, S value);
	}
	public void visit(ReadOnlySpectrum list, GridVisitor<Float> visitor) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				visitor.visit(this.getIndexFromXY(x, y), x, y, get(list, x, y));
			}
		}
	}
	public void visit(List<T> list, GridVisitor<T> visitor) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				visitor.visit(this.getIndexFromXY(x, y), x, y, get(list, x, y));
			}
		}
	}
	
	public void visitRow(int row, ReadOnlySpectrum list, GridVisitor<Float> visitor) {
		for (int x = 0; x < width; x++) {
			visitor.visit(this.getIndexFromXY(x, row), x, row, get(list, x, row));
		}
	}
	public void visitRow(int row, List<T> list, GridVisitor<T> visitor) {
		for (int x = 0; x < width; x++) {
			visitor.visit(this.getIndexFromXY(x, row), x, row, get(list, x, row));
		}
	}
	
	public void visitColumn(int col, ReadOnlySpectrum list, GridVisitor<Float> visitor) {
		for (int y = 0; y < height; y++) {
			visitor.visit(this.getIndexFromXY(col, y), col, y, get(list, col, y));
		}
	}
	public void visitColumn(int col, List<T> list, GridVisitor<T> visitor) {
		for (int y = 0; y < height; y++) {
			visitor.visit(this.getIndexFromXY(col, y), col, y, get(list, col, y));
		}
	}
	
	
	public IntPair getXYFromIndex(int index)
	{
		int x = index % width;
		int y = ((index - x) / width);
		return new IntPair(x, y);
		
	}
	
	public int getIndexFromXY(int x, int y)
	{
		if (!boundsCheck(x, y)) return -1;
		return y * width + x;
	}
	
	public int getIndexFromXY(Coord<Integer> coord) {
		return getIndexFromXY(coord.x, coord.y);
	}
	
	public int getIndexFromXY(IntPair pair) {
		return getIndexFromXY(pair.first, pair.second);
	}
	
	@Override
	public GridPerspective<T> clone()
	{
	
		GridPerspective<T> g = new GridPerspective<T>(width, height, fallback);
		
		return g;
		
	}
	
	public boolean boundsCheck(int x, int y) {
		if (x < 0) return false;
		if (y < 0) return false;
		if (x >= this.width)  return false;
		if (y >= this.height) return false;
		return true;
	}
	
	public int north(int index) {
		
		IntPair coord = getXYFromIndex(index);
		int x = coord.first - 1;
		int y = coord.second;
		
		if (!boundsCheck(x, y)) return -1;
		return getIndexFromXY(x, y);
				
	}
	
	public int south(int index) {
		
		IntPair coord = getXYFromIndex(index);
		int x = coord.first + 1;
		int y = coord.second;
		
		if (!boundsCheck(x, y)) return -1;
		return getIndexFromXY(x, y);
				
	}
	
	public int east(int index) {
		
		IntPair coord = getXYFromIndex(index);
		int x = coord.first;
		int y = coord.second+1;
		
		if (!boundsCheck(x, y)) return -1;
		return getIndexFromXY(x, y);
				
	}
	
	public int west(int index) {
		
		IntPair coord = getXYFromIndex(index);
		int x = coord.first;
		int y = coord.second-1;
		
		if (!boundsCheck(x, y)) return -1;
		return getIndexFromXY(x, y);
				
	}
	
}
