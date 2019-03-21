package cyclops.visualization;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
	void write(OutputStream out) throws IOException;
	
	default void write(Path path) throws IOException {
		OutputStream os = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		write(os);
		os.close();
	}

}
