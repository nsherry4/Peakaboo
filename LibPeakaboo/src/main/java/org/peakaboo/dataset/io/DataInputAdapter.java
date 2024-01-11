package org.peakaboo.dataset.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.eventful.cache.EventfulCache;
import org.peakaboo.framework.eventful.cache.EventfulNullableCache;

/**
 * A {@link DataInputAdapter} is a representation of data that does not
 * necessarily exist on disk in an accessible location. The data can either be
 * accessed through an {@link InputStream}, or through a {@link Path}. The
 * InputStream should be preferred, as the file referenced by the {@link Path}
 * may be created on demand as a temporary file copied from the InputStream.
 * 
 * This is helpful for working in environments like Android which do not allow
 * direct access to the filesystem, or generally don't choose/share files that
 * way.
 * 
 * @author NAS
 *
 */
public interface DataInputAdapter extends AutoCloseable {

	
	/**
	 * Returns an {@link InputStream} for this DataFile
	 * 
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Ensures that the data is available as a file on disk (or some filesystem
	 * accessible by the java.nio system), and returns a {@link Path} to that file.
	 * <br/>
	 * <br/>
	 * Note that for some input sources, the data may not originate from a file on
	 * disk, and this step may incur extra overhead compared to
	 * {@link #getInputStream()}. This is useful for {@link DataSource}s which wrap
	 * native libraries, and which cannot make use of Java constructs like
	 * {@link InputStream}s. To minimize overhead and resource consumotion,
	 * DataSources are encouraged to call {@link #close()} on a
	 * {@link DataInputAdapter} after it has been read to clean up any temporary
	 * files created in the process.
	 * 
	 * @throws IOException
	 */
	Path getAndEnsurePath() throws IOException;

	/**
	 * Returns the size of the file or stream if available
	 */
	Optional<Long> size();
	
	/**
	 * Gets a relative filename for this DataFile.
	 */
	String getFilename();
	
	String getFullyQualifiedFilename();
	
	/**
	 * Provide the address of this resource, if it is addressable
	 * @return an Optional<String> representation of the address if it is addressable, empty otherwise
	 */
	Optional<String> address();
	

	/**
	 * Tests if the resource is currently accessable.
	 * @return true if the resource is accessible, false otherwise
	 */
	boolean exists();

	/**
	 * Reports if the resource can be written to as well as read 
	 * @return true if the source is writable, false otherwise
	 */
	boolean writable();
	
	/**
	 * If applicable, returns the local folder which contains the datafile
	 * @return a Optional<File> object representing the file's parent directory if applicable, an empty Optional otherwise
	 */
	Optional<File> localFolder();
	
	
	
	
	/**
	 * Gets the filename without the extension
	 */
	default String getBasename() {
		return FilenameUtils.getBaseName(getFilename());
	}
	
	
	default List<String> toLines() throws IOException {
		return this.toLines(null);
	}
	
	default List<String> toLines(Charset charset) throws IOException {
		//Read lines
		var filescanner = (charset == null) ? new Scanner(this.getInputStream()) : new Scanner(this.getInputStream(), charset);
		List<String> lines = new ArrayList<>();
		while (filescanner.hasNextLine()) {
			lines.add(filescanner.nextLine());
		}
		filescanner.close();
		return lines;
	}

	/**
	 * Indicates if this resource can be represented as a String and re-accessed
	 * later. Some formats may rely on having a "magic" object passed to the
	 * constructor which cannot be serialized and re-accessed.
	 * 
	 * @return true if is it addressable (re-accessable), false otherwise
	 */
	default boolean addressable() {
		return address().isPresent();
	}
	

	

	static String getSharedBasename(List<DataInputAdapter> datafiles) {
		if (datafiles.isEmpty()) return "";
		
		var shared = datafiles.get(0).getBasename();
		for (var datafile : datafiles) {
			var othername = datafile.getBasename();
			var length = Math.min(shared.length(), othername.length());
			for (int i = 0; i < length; i++) {
				if (shared.charAt(i) != othername.charAt(i)) {
					shared = shared.substring(0, i);
					break; //out of inner loop over shared chars
				}
			}
		}
		return shared;
	}
	
	static String getTitle(List<DataInputAdapter> datafiles) {
		if (datafiles.size() == 0) return "";
		var shared = getSharedBasename(datafiles);
		if (shared.length() > 2) return shared;
		return datafiles.get(0).getBasename();
	}

	static List<DataInputAdapter> fromFilenames(List<String> filenames) {
		EventfulCache<Path> lazyDownload = new EventfulNullableCache<>(DataInputAdapters::createDownloadDirectory);
		return filenames.stream()
				.map(f -> DataInputAdapters.construct(f, lazyDownload::getValue))
				.collect(Collectors.toList());
	}
	
	
}
