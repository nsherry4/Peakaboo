package org.peakaboo.dataset.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DataInputAdapters {

	private DataInputAdapters() {
		// Not Constructable
	}
	
	private static Map<Predicate<String>, BiFunction<String, Supplier<Path>, DataInputAdapter>> addressConstructors = new HashMap<>();
	static {
		registerProtocolConstructor(PathDataInputAdapter::addressValid, PathDataInputAdapter::fromAddress);
		registerProtocolConstructor(URLDataInputAdapter::addressValid, URLDataInputAdapter::fromAddress);
	}
	
	public static void registerProtocolConstructor(Predicate<String> matcher, BiFunction<String, Supplier<Path>, DataInputAdapter> constructor) {
		addressConstructors.put(matcher, constructor);
	}
	
	public static DataInputAdapter construct(String address, Supplier<Path> downloadDir) {
		for (Predicate<String> matcher : addressConstructors.keySet()) {
			if (matcher.test(address)) {
				return addressConstructors.get(matcher).apply(address, downloadDir);
			}
		}
		throw new IllegalArgumentException("No handler registered for file path: " + address);

	}
		
	public static Path createDownloadDirectory() {
		try {
			return Files.createTempDirectory("PeakabooDataSetDownload");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
}
