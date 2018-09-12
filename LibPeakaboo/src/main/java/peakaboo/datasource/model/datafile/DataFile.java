package peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import peakaboo.datasource.model.DataSource;

public interface DataFile extends AutoCloseable {

	/**
	 * Gets a relative filename for this DataFile.
	 */
	public String getFilename();

	/**
	 * Returns an {@link InputStream} for this DataFile
	 * 
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Ensures that the data is available as a file on disk, and returns a
	 * {@link Path} to that file. <br/>
	 * <br/>
	 * Note that for some input sources, the data may not originate from a file on
	 * disk, and this step may incur extra overhead compared to
	 * {@link #getInputStream()}. This is useful for {@link DataSource}s which wrap
	 * native libraries, and which cannot make use of Java constructs like
	 * {@link InputStream}s. To minimize overhead and resource consumotion,
	 * DataSources are encouraged to call {@link #close()} on a DataFile after it
	 * has been read to clean up any temporary files created in the process.
	 * 
	 * @throws IOException
	 */
	public Path getAndEnsurePath() throws IOException;

}
