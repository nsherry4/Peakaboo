package org.peakaboo.dataset.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PathDataOutputAdapter implements DataOutputAdapter {

	private Path path;
	
	public PathDataOutputAdapter(Path path) {
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
				StandardOpenOption.TRUNCATE_EXISTING
			);
	}

	@Override
	public Path getFallback() {
		return path;
	}


}
