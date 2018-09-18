package peakaboo.datasource.model;

import java.util.ArrayList;
import java.util.List;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.CompoundEncoder;
import net.sciencestudio.scratch.encoders.compressors.Compressors;
import net.sciencestudio.scratch.encoders.serializers.Serializers;
import net.sciencestudio.scratch.list.ScratchLists;
import peakaboo.common.Configuration;
import scitypes.ISpectrum;
import scitypes.Spectrum;

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
 * To create a new SpectrumList, call {@link PeakabooLists#create(String)}.
 * If the SpectrumList cannot be created for whatever reason, a memory-based
 * list will be created instead.
 * <br/><br/>
 * Note that this class depends on the specific implementation of Spectrum
 * being {@link ISpectrum} 
 * @author Nathaniel Sherry, 2011-2012
 *
 */

public final class PeakabooLists {

	
	@SuppressWarnings("unchecked")
	public static List<Spectrum> create() {
		return create(Serializers.fstUnsafe(ISpectrum.class));
	}
	
	
	public static <T> List<T> create(ScratchEncoder<T> serializer) {
		if (Configuration.diskstore == false && Configuration.compression == false) {
			return new ArrayList<>();
		}
		
		//Config for compression
		ScratchEncoder<T> encoder = serializer;
		if (Configuration.compression) {
			encoder = new CompoundEncoder<>(encoder, Compressors.lz4fast());
		}
		
		//Config for disk-backed
		if (Configuration.diskstore) {
			return ScratchLists.tryDiskBacked(encoder);
		} else {
			return ScratchLists.memoryBacked(encoder);
		}
	}
	
	
}
