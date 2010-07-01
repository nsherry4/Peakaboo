package peakaboo.datatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import fava.*;

public class Spectrum implements Iterable<Float>, Serializable
{

	private float	data[];
	private int		size;
	private int		maxIndex;


	public Spectrum(int size, float initialize)
	{
		this.data = new float[size];
		this.size = size;
		maxIndex = 0;
		
		for (int i = 0; i < size; i++)
		{
			data[i] = initialize;
		}
		maxIndex = size - 1;
	}

	public Spectrum(List<Float> fromList)
	{
		this.data = new float[fromList.size()];
		this.size = fromList.size();
				
		for (int i = 0; i < size; i++)
		{
			data[i] = fromList.get(i);
		}
		maxIndex = size - 1;
		
	}

	public Spectrum(int size)
	{
		this(size, 0.0f);
	}
	
	public Spectrum(Spectrum copy)
	{
		this.data = copy.toArray();
		this.size = copy.size;
		this.maxIndex = copy.maxIndex;		
	}


	public boolean add(float value)
	{
		if (maxIndex < size - 1)
		{
			data[++maxIndex] = value;
			return true;
		}
		else
		{
			return false;
		}
	}


	public void set(int i, float value)
	{
		data[i] = value;
	}


	public float get(int i)
	{
		return data[i];
	}


	public int size()
	{
		return size;
	}


	public float[] toArray()
	{
		return data.clone();
	}

	public Spectrum subSpectrum(int start, int stop)
	{
		
		int length = stop - start + 1;
		Spectrum target = new Spectrum(length);
		System.arraycopy(data, start, target.data, 0, length);
		target.maxIndex = length-1;
		
		return target; 
	}
	
	public float[] backingArray()
	{
		return data;
	}


	public Iterator<Float> iterator()
	{
		return new Iterator<Float>() {

			int	index	= 0;

			public boolean hasNext()
			{
				return (index < size);
			}


			public Float next()
			{
				return data[index++];
			}


			public void remove()
			{
				data[index] = 0.0f;
			}
		};
	}

	public Spectrum map(FunctionMap<Float, Float> f)
	{
		
		Spectrum newSpectrum = new Spectrum(size);
		for (int i = 0; i < size; i++)
		{
			newSpectrum.set(i, f.f(data[i]));
		}
		return newSpectrum;
		
	}
	
	public Spectrum zipWith(Spectrum other, FunctionCombine<Float, Float, Float> f)
	{
		
		Spectrum newSpectrum = new Spectrum(size);
		for (int i = 0; i < size; i++)
		{
			newSpectrum.set(i, f.f(data[i], other.data[i]));
		}
		return newSpectrum;
		
	}

	public static FunctionMap<Spectrum, byte[]> getEncoder()
	{
	
		//Function to serialize a spectrum
		return  new FunctionMap<Spectrum, byte[]>()
		{
	
			public byte[] f(Spectrum s)
			{
	
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos;
				try
				{
					oos = new ObjectOutputStream(baos);
					oos.writeObject(s);
					oos.close();
					return baos.toByteArray();
				}
				catch (IOException e)
				{
					return new byte[0];
				}
	
			}
		};
	}

	public static FunctionMap<byte[], Spectrum> getDecoder(){
		
		//Function to deserialize a spectrum

		return new FunctionMap<byte[], Spectrum>()
		{

			public Spectrum f(byte[] bs)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(bs);
				ObjectInputStream ois;
				try
				{
					ois = new ObjectInputStream(bais);
					Spectrum s = (Spectrum) ois.readObject();
					ois.close();
					return s;
				}
				catch (IOException e)
				{
					return null;
				}
				catch (ClassNotFoundException e)
				{
					return null;
				}

			}
		};
		
	}
		

}
