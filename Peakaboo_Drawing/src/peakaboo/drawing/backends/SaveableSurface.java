package peakaboo.drawing.backends;


import java.io.IOException;
import java.io.OutputStream;

/**
 * A SaveableSurface is a kind of Surface which can be written to an OutputStream.
 * 
 * @author Nathaniel Sherry, 2009
 * @see Surface
 * @see OutputStream
 * 
 */

public interface SaveableSurface extends Surface
{

	/**
	 * Writes the contents of this SaveableSurface to a given OutputStream
	 * @param out the {@link OutputStream} to write to
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException;

}
