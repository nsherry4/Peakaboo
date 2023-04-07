package org.peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PathOutputFile implements OutputFile {

	private Path path;
	
	public PathOutputFile(Path path) {
		this.path = path;
	}
	
	@Override
	public void close() {
		// NOOP
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(path, 
				StandardOpenOption.WRITE, 
				StandardOpenOption.CREATE, 
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.SYNC //We do this so that each plugin is not required to flush/close the outputstream
			);
	}

	@Override
	public Path getFallback() {
		return path;
	}

	@Override
	public void finishFallback() {
		// NOOP
	}

}
