package org.peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class DataFiles {

	private DataFiles() {
		// Not Constructable
	}
	
	private static Map<Predicate<String>, BiFunction<String, Path, DataFile>> addressConstructors = new HashMap<>();
	static {
		registerProtocolConstructor(PathDataFile::addressValid, PathDataFile::fromAddress);
		registerProtocolConstructor(URLDataFile::addressValid, URLDataFile::fromAddress);
	}
	
	public static void registerProtocolConstructor(Predicate<String> matcher, BiFunction<String, Path, DataFile> constructor) {
		addressConstructors.put(matcher, constructor);
	}
		
	public static DataFile construct(String address, Path downloadDir) {
		for (Predicate<String> matcher : addressConstructors.keySet()) {
			if (matcher.test(address)) {
				return addressConstructors.get(matcher).apply(address, downloadDir);
			}
		}
		return null;
	}
	
	public static Path createDownloadDirectory() {
		try {
			return Files.createTempDirectory("PeakabooDataSetDownload");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
