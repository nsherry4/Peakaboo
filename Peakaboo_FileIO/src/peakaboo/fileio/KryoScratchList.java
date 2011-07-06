package peakaboo.fileio;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;

import fava.functionable.FList;

import scratch.ScratchList;

public class KryoScratchList<T extends Serializable> extends ScratchList<T>{

	private Kryo kryo;
	private ObjectBuffer kryoBuffer;
	Class<T> mainclass;
	
	
	public static <T extends Serializable> List<T> create(String name, Class<T> c)
	{
		try
		{
			return new KryoScratchList<T>(name, c);
		}
		catch (IOException e)
		{
			//FList can also behave sparsely
			e.printStackTrace();
			return new FList<T>();
		}
	}
	
	
	
	/**
	 * Create a new ScratchList
	 * @param name the name prefix to give the temporary file
	 * @throws IOException
	 */
	public KryoScratchList(String name, Class<T> c) throws IOException
	{
		super(name);
		mainclass = c;
		register(c);
	}
	
	public KryoScratchList(File file, Class<T> c) throws IOException
	{
		super(file);
		mainclass = c;
		register(c);
	}

	
	protected KryoScratchList(Class<T> c)
	{
		mainclass = c;
		register(c);
	}
	
	
	@Override
	protected byte[] encodeObject(T element) throws IOException
	{
		ObjectBuffer buffer = getKryoBuffer();
		return buffer.writeObject(element);
	}
	
	@Override
	protected T decodeObject(byte[] byteArray) throws IOException
	{
		ObjectBuffer buffer = getKryoBuffer();
		return buffer.readObject(byteArray, mainclass);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		ScratchList<T> sublist = new KryoScratchList<T>(mainclass);
		makeSublist(sublist, fromIndex, toIndex);
		return sublist;
	}
	
	
	public void register(Class<?> c)
	{
		getKryo().register(c);
	}
	
	public void register(Class<?> c, Serializer s)
	{
		getKryo().register(c, s);
	}
	
	public Kryo getKryo()
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
