package org.peakaboo.framework.scratch.list;

import java.util.AbstractList;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.single.Compressed;

/**
 * ScratchList extends the standard Java List interface to support compressed storage
 * and out-of-order data loading patterns.
 * 
 * <p><strong>IMPORTANT:</strong> This implementation intentionally breaks with the standard 
 * List contract by supporting null-filled pseudo-sparse lists. This allows for efficient 
 * out-of-order loading of data when the final size is indeterminate. Elements can be set 
 * at arbitrary indices, with the list automatically expanding and filling gaps with null 
 * values. This design choice prioritizes performance for specific use cases over strict 
 * List interface compliance.</p>
 * 
 * <p>All concrete implementations must handle this sparse behavior consistently.</p>
 */
public abstract class ScratchList<T> extends AbstractList<T> implements AutoCloseable {
	
	public void addCompressed(Compressed<T> compressed) {
		addCompressed(size(), compressed);
	}
		
	public abstract void addCompressed(int index, Compressed<T> compressed);
	public abstract void setCompressed(int index, Compressed<T> compressed);
	
	public abstract ScratchEncoder<T> getEncoder();
	
}
