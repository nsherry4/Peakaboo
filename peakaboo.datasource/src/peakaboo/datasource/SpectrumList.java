package peakaboo.datasource;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;

import scitypes.ISpectrum;
import scitypes.SparsedList;
import scitypes.Spectrum;
import scratch.ScratchList;

/**
 * SpectrumList is an implementation of the List interface which writes 
 * out values to a temporary file, rather than storing elements in memory.
 * This is useful for lists which are sufficiently large to cause memory
 * concerns in a typical JVM.
 * <br /><br /> 
 * Storing elements on disk means that get operations will return copies 
 * of the objects in the list rather than the originally stored objects. 
 * If an element is retrieved from the list, modified, and then 
 * retrieved a second time, the second copy retrieved will lack the 
 * modifications made to the first copy.
 * 
 * To create a new SpectrumList, call {@link SpectrumList#create(String)}.
 * If the SpectrumList cannot be created for whatever reason, a memory-based
 * list will be created instead.
 * <br/><br/>
 * Note that this class depends on the specific implementation of Spectrum
 * being {@link ISpectrum} 
 * @author Nathaniel Sherry, 2011-2012
 *
 */

public final class SpectrumList extends ScratchList<Spectrum>{

	private Kryo kryo;
	private ObjectBuffer kryoBuffer;
	
	
	@SuppressWarnings("unchecked")
	public static List<Spectrum> create(String name)
	{
		try
		{
			return new SpectrumList(name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//We need to use a list implementation here which can be sparse, 
			//since SpectrumList is, and we're looking to provide the same 
			//functionality in fallback mode
			return new SparsedList<>(new ArrayList<>());
		}
	}
	
	/**
	 * Create a new ScratchList
	 * @param name the name prefix to give the temporary file
	 * @throws IOException
	 */
	private SpectrumList(String name) throws IOException
	{
		super(name);
		register(Spectrum.class);
		register(float[].class);
	}


	
	private SpectrumList()
	{
		register(Spectrum.class);
		register(float[].class);
	}
	
	
	@Override
	protected byte[] encodeObject(Spectrum element) throws IOException
	{
		ObjectBuffer buffer = getKryoBuffer();
		return buffer.writeObject(element);
	}
	
	@Override
	protected Spectrum decodeObject(byte[] byteArray) throws IOException
	{
		ObjectBuffer buffer = getKryoBuffer();
		return buffer.readObject(byteArray, ISpectrum.class);
	}

	@Override
	public List<Spectrum> subList(int fromIndex, int toIndex) {
		SpectrumList sublist = new SpectrumList();
		makeSublist(sublist, fromIndex, toIndex);
		return sublist;
	}
	
	private void register(Class<?> c)
	{
		getKryo().register(c);
	}
	
	private Kryo getKryo()
	{
		
		if (kryo != null) return kryo;
		
		kryo = new Kryo();
		kryo.setRegistrationOptional(true);
		
		kryo.register(Color.class, new SimpleSerializer<Color>() {
	        public void write (ByteBuffer buffer, Color color) {
	                buffer.putInt(color.getRGB());
	        }
	        public Color read (ByteBuffer buffer) {
	                return new Color(buffer.getInt());
	        }
		});
		
		return kryo;
	}
	
	
	private ObjectBuffer getKryoBuffer()
	{
		if (kryoBuffer != null) return kryoBuffer;
		kryoBuffer = new ObjectBuffer(getKryo(), 1000 << 16);
		return kryoBuffer;
	}

	
}
