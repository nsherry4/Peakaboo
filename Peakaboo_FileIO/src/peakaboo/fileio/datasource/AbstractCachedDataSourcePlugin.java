package peakaboo.fileio.datasource;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import peakaboo.common.Version;
import peakaboo.fileio.KryoScratchList;

import com.esotericsoftware.kryo.serialize.ArraySerializer;

import fava.functionable.FList;

import scitypes.Spectrum;
import scratch.ScratchList;

public abstract class AbstractCachedDataSourcePlugin extends AbstractDataSourcePlugin
{

	private List<Spectrum> cache;
	
	
	public AbstractCachedDataSourcePlugin()
	{
	
		KryoScratchList<Spectrum> newlist;
		try {
			newlist = new KryoScratchList<Spectrum>(Version.program_name + " - Corrected Spectrum", Spectrum.class);
			newlist.register(float[].class, new ArraySerializer(newlist.getKryo()));
			cache = newlist;
		} catch (IOException e) {
			try
			{
				cache = new ScratchList<Spectrum>("Peakaboo - Cache for DataSource: ");
			}
			catch (IOException e1)
			{
				cache = new FList<Spectrum>();
			}
		}
		
	}
	
	
	@Override
	public Spectrum getScanAtIndex(int index) throws IndexOutOfBoundsException
	{

		if (index >= getScanCount()) throw new IndexOutOfBoundsException();
		if (index < 0) throw new IndexOutOfBoundsException();
		
		Spectrum s;
		
		if (cache.size() <= index || cache.get(index) == null)
		{
			setCache(index);
		}
		return cache.get(index);
		
	}
	
	private void setCache(int index)
	{
		Spectrum s;
		s = loadScanAtIndex(index);
		cache.set(index, s);
	}
	
	
	public abstract Spectrum loadScanAtIndex(int index);

		
	protected void cache(int index, Spectrum spectrum) throws IndexOutOfBoundsException
	{
		if (index >= getScanCount()) throw new IndexOutOfBoundsException();
		if (index < 0) throw new IndexOutOfBoundsException();
		
		if (spectrum == null) return;
		
		cache.set(index, spectrum);
		
	}
	
}
