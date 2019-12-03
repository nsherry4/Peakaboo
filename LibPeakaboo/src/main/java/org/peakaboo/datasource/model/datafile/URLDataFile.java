package org.peakaboo.datasource.model.datafile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooLog;
import org.yaml.snakeyaml.Yaml;

public class URLDataFile implements DataFile {

	private static final String URL_DATAFILE_PROTO = "urldatafile://";
	private URL url;
	private String name;
	private Path file;
	private Path downloadDir;
	
	/**
	 * Creates a new URIDataFile for a remote resource
	 * @param url the URI of the remote resource (which should be downloadable)
	 * @param name the name of file described by the uri, including the file extension
	 */
	public URLDataFile(URL url, Path downloadDir, String name) {
		this.url = url;
		this.name = name;
		this.downloadDir = downloadDir;
		this.file = null;
	}
	
	@Override
	public void close() {
		if (file != null) {
			try {
				Files.delete(file);
			} catch (IOException e) {
				PeakabooLog.get().log(Level.WARNING, "Failed to close resource for " + name, e);
			}
		}
	}

	@Override
	public String getFilename() {
		return name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}

	@Override
	public Path getAndEnsurePath() throws IOException {
		if (file == null) {
			download();
		}
		if (file == null) {
			throw new IOException("Could not retrieve file " + name);
		}
		return file;
	}
	
	@Override
	public Optional<Long> size() {
		return Optional.empty();
	}
	
	private void download() throws IOException {
		Path relative = Paths.get(name);
		Path p = downloadDir.resolve(relative);


		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				Files.delete(p);
			} catch (IOException e) {
				PeakabooLog.get().log(Level.WARNING, "Failed to delete temp file", e);
			}
		}));

		Files.copy(url.openStream(), p, StandardCopyOption.REPLACE_EXISTING);
		file = p;
	}

	@Override
	public boolean addressable() {
		return true;
	}

	@Override
	public boolean exists() {
		try {
			InputStream i = url.openStream();
			i.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		//This line isn't required, right? Java will just call the other equals method when given a URLDataFile?
		if (other instanceof URLDataFile) {
			return this.equals((URLDataFile)other);
		}
		return false;
	}
	
	public boolean equals(URLDataFile other) {
		if (other == null) { return false; }
		if (!this.url.toString().equals(other.url.toString())) { return false; }
		if (!this.name.equals(other.name)) { return false; } 
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.url.toString().hashCode() + this.name.hashCode();
	}

	@Override
	public Optional<File> localFolder() {
		return Optional.empty();
	}

	@Override
	public boolean writable() {
		return false;
	}

	@Override
	public Optional<String> address() {
		return Optional.of(serialize());
	}
	
	private String serialize() {
		Map<String, String> values = new HashMap<>();
		values.put("url", url.toString());
		values.put("name", name);
		String b64 = new String(Base64.getEncoder().encode(new Yaml().dump(values).getBytes()));
		return URL_DATAFILE_PROTO + b64;
	}
	
	private static URLDataFile deserialize(String s, Path downloadDir) {
		String b64 = s.substring(URL_DATAFILE_PROTO.length());
		String yaml = new String(Base64.getDecoder().decode(b64.getBytes()));
		Map<String, String> values = new Yaml().load(yaml);
		
		URL url;
		try {
			url = new URL(values.get("url"));
		} catch (MalformedURLException e) {
			//this should never happen because it would have been vetted before being serialized
			throw new RuntimeException(e);
		}
		String name = values.get("name");
		return new URLDataFile(url, downloadDir, name);
	}

	public static boolean addressValid(String address) {
		return address.startsWith(URL_DATAFILE_PROTO);
	}
	
	public static URLDataFile fromAddress(String address, Path tempDir) {
		return deserialize(address, tempDir);
	}

}
