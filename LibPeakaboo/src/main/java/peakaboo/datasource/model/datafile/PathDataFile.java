package peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PathDataFile implements DataFile {
	
	private Path path;
	
	public PathDataFile(Path path) {
		this.path = path;
	}

	@Override
	public String getFilename() {
		return path.getFileName().toString();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return Files.newInputStream(path, StandardOpenOption.READ);
	}

	@Override
	public Path getAndEnsurePath() throws IOException {
		return path;
	}

	@Override
	public void close() throws Exception {
		//NOOP
	}

}
