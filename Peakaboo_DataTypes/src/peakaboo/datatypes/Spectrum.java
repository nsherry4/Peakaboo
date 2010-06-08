package peakaboo.datatypes;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class Spectrum
{

	private float data[];
	private int size;
	
	public Spectrum(int size)
	{
		this.data = new float[size];
		this.size = size;
	}

	public void set(int i, float value)
	{
		data[i] = value;
	}
	
	public float get(int i)
	{
		return data[i];
	}
	
	
}
