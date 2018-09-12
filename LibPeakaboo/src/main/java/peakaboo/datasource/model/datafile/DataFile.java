package peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface DataFile {

	/**
	 * Gets a relative filename for this DataFile.
	 */
	public String getFilename();

	/**
	 * Returns an {@link InputStream} for this DataFile
	 * @throws IOException 
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Ensures that the data is available as a file on disk, and returns a
	 * {@link Path} to that file. Note that for some input sources, the file may not
	 * be available on disk, and this step may incur extra overhead compared to
	 * {@link #getInputStream()}
	 * @throws IOException 
	 */
	public Path getAndEnsurePath() throws IOException;

}
