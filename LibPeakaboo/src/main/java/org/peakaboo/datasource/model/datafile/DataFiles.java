package org.peakaboo.datasource.model.datafile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DataFiles {

	private static Map<Function<String, Boolean>, BiFunction<String, Path, DataFile>> addressConstructors = new HashMap<>();
	static {
		registerProtocolConstructor(PathDataFile::addressValid, PathDataFile::fromAddress);
		registerProtocolConstructor(URLDataFile::addressValid, URLDataFile::fromAddress);
	}
	
	public static void registerProtocolConstructor(Function<String, Boolean> matcher, BiFunction<String, Path, DataFile> constructor) {
		addressConstructors.put(matcher, constructor);
	}
		
	public static DataFile construct(String address, Path downloadDir) {
		for (Function<String, Boolean> matcher : addressConstructors.keySet()) {
			if (matcher.apply(address)) {
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
