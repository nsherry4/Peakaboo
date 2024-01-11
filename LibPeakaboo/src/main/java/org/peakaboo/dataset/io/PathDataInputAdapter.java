package org.peakaboo.dataset.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.function.Supplier;

public class PathDataInputAdapter implements DataInputAdapter {
	
	private Path path;
	
	public PathDataInputAdapter(String filename) {
		this(new File(filename));
	}
	
	public PathDataInputAdapter(File file) {
		this.path = file.toPath();
	}
	
	public PathDataInputAdapter(Path path) {
		this.path = path;
	}

	@Override
	public String getFilename() {
		return path.getFileName().toString();
	}
	
	@Override
	public String getFullyQualifiedFilename() {
		return path.toString();
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
	public void close() {
		//NOOP
	}

	@Override
	public Optional<Long> size() {
		try {
			return Optional.of(Files.size(path));
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean exists() {
		return Files.exists(path);
	}
	
	@Override
	public boolean equals(Object other) {
		//This line isn't needed, right? Java will just call the other method?
		if (other instanceof PathDataInputAdapter) {
			return equals((PathDataInputAdapter) other);
		}
		return false;
	}
	
	public boolean equals(PathDataInputAdapter other) {
		if (other == null) { return false; }
		return this.path.equals(other.path);
	}
	
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public Optional<File> localFolder() {
		return Optional.of(path.getParent().toFile());
	}

	@Override
	public boolean writable() {
		return true;
	}

	@Override
	public Optional<String> address() {
		try {
			return Optional.of("file://" + path.toFile().toString());
		} catch (UnsupportedOperationException e) {
			return Optional.empty();
		}
	}

	public static boolean addressValid(String address) {
		return address.startsWith("/") || address.startsWith("file://");
	}
	
	public static PathDataInputAdapter fromAddress(String address, Supplier<Path> tempDir) {
		if (address.startsWith("file://")) {
			address = address.substring(7);
		}
		return new PathDataInputAdapter(address);
	}
	

}
