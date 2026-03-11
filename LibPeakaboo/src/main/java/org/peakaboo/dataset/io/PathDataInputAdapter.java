package org.peakaboo.dataset.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
			return Optional.of(path.toUri().toString());
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	public static boolean addressValid(String address) {
		// Standard file: URI (file:/// on all platforms)
		if (address.startsWith("file:")) { return true; }
		// Legacy: bare Unix absolute path
		if (address.startsWith("/")) { return true; }
		// Legacy: bare Windows absolute path (e.g. C:\path or C:/path)
		if (address.length() >= 3
				&& Character.isLetter(address.charAt(0))
				&& address.charAt(1) == ':'
				&& (address.charAt(2) == '\\' || address.charAt(2) == '/')) { return true; }
		return false;
	}

	public static PathDataInputAdapter fromAddress(String address, Supplier<Path> tempDir) {
		if (address.startsWith("file:")) {
			return new PathDataInputAdapter(Path.of(URI.create(address)));
		}
		// Legacy fallback: bare Unix path (/path/to/file) or bare Windows path (C:\path\to\file)
		return new PathDataInputAdapter(address);
	}
	

}
