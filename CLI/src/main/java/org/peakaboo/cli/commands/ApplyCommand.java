package org.peakaboo.cli.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import org.peakaboo.cli.CLIBootstrap;
import org.peakaboo.cli.ExitCodes;
import org.peakaboo.cli.GlobalOptions;
import org.peakaboo.cli.HeadlessDataLoader;
import org.peakaboo.cli.HeadlessDataLoader.HeadlessLoadException;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.visualization.ExportableSurface;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceDescriptor;
import org.peakaboo.framework.cyclops.visualization.descriptor.SurfaceExporterRegistry;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.mapping.rawmap.RawMapSet;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
	name = "apply",
	description = "Apply a session's analysis (filters, fittings, calibration) to one or more datasets and export the results",
	mixinStandardHelpOptions = true
)
public class ApplyCommand implements Callable<Integer> {

	private static final Set<String> EXPORT_KINDS = Set.of("plot", "maps");

	@Mixin
	GlobalOptions globals;

	@Option(names = "--session", required = true, paramLabel = "SESSION",
			description = "The .peakaboo session file to apply")
	File sessionFile;

	@Option(names = "--export", split = ",", paramLabel = "KIND", defaultValue = "plot",
			description = "Archives to produce: plot (plot image, fittings, session), "
					+ "maps (one image and CSV per fitted element) (default: ${DEFAULT-VALUE})")
	List<String> exports;

	@Option(names = "--out", paramLabel = "DIR", defaultValue = ".",
			description = "Directory to write archives into (default: current directory)")
	File outDir;

	@Option(names = "--dimensions", paramLabel = "WxH", converter = CoordParser.class,
			description = "Map data dimensions in scans, eg 128x96; if omitted, taken from the dataset or guessed")
	Coord<Integer> dimensions;

	@Option(names = "--image-size", paramLabel = "WxH|N", converter = SizingParser.class, defaultValue = "1000",
			description = "Pixel size of exported images (default: ${DEFAULT-VALUE})")
	Sizing imageSize;

	@Option(names = "--image-format", paramLabel = "EXT", defaultValue = "png",
			description = "Image format for exported plots and maps (default: ${DEFAULT-VALUE})")
	String imageFormat;

	@Option(names = "--map-scale", paramLabel = "SCALE", defaultValue = "local",
			description = "Intensity scale for exported map images: ${COMPLETION-CANDIDATES} "
					+ "(default: ${DEFAULT-VALUE})")
	MapScale mapScale;

	@Parameters(arity = "1..*", paramLabel = "DATASETS",
			description = "Dataset files; each file is treated as a separate dataset")
	List<File> datasets;

	/** How are map intensities scaled -- relative to the local map or the global map set? */
	enum MapScale {
		local(MapScaleMode.RELATIVE),
		global(MapScaleMode.ABSOLUTE);

		private final MapScaleMode mode;

		MapScale(MapScaleMode mode) {
			this.mode = mode;
		}

		MapScaleMode mode() {
			return mode;
		}
	}

	/** Parses WxH arguments like "128x96". */
	public static class CoordParser implements CommandLine.ITypeConverter<Coord<Integer>> {
		@Override
		public Coord<Integer> convert(String value) {
			String[] parts = value.toLowerCase().split("x");
			try {
				if (parts.length != 2) throw new NumberFormatException();
				int x = Integer.parseInt(parts[0]);
				int y = Integer.parseInt(parts[1]);
				if (x < 1 || y < 1) throw new NumberFormatException();
				return new Coord<>(x, y);
			} catch (NumberFormatException e) {
				throw new CommandLine.TypeConversionException("Failed to parse: " + value);
			}
		}
	}
	
	/** Simple record for tracking user size selection */
	static record Sizing (Integer a, Integer b) {
		public Sizing(int a) {
			this(a, null);
		}
		public boolean hasTwoValues() {
			return this.b != null;
		}
		public Coord<Integer> asCoord() {
			if (!hasTwoValues()) {
				throw new IllegalArgumentException("Expected a two-valued size argument");
			}
			return new Coord<>(a, b);
		}
	}
	/** Parses WxH arguments like "128x96" or single-value arguments like "1000". */
	static class SizingParser implements CommandLine.ITypeConverter<Sizing> {

		private static final int MIN_SIZE = 250, MAX_SIZE = 10_000;

		@Override
		public Sizing convert(String value) {
			try {
				if (value.toLowerCase().contains("x")) {
					// parse as 2-arg.
					Coord<Integer> coord = new CoordParser().convert(value);
					return new Sizing(validate(coord.x), validate(coord.y));
				} else {
					// parse as 1-arg
					return new Sizing(validate(Integer.parseInt(value)));
				}
			} catch (NumberFormatException | CommandLine.TypeConversionException e) {
				throw new CommandLine.TypeConversionException("Failed to parse or value not in [" + MIN_SIZE + ", " + MAX_SIZE + "] : " + value);
			}
		}

		// Validate a single size value is in the allowed range
		private static int validate(int size) {
			if (size < MIN_SIZE || size > MAX_SIZE) {
				throw new NumberFormatException();
			}
			return size;
		}

	}
	

	@Override
	public Integer call() {
		
		// Validate the types of exports requested
		Set<String> kinds = new LinkedHashSet<>(exports);
		for (String kind : kinds) {
			if (!EXPORT_KINDS.contains(kind)) {
				System.err.println("error: unknown export kind '" + kind + "'; expected one of "
						+ String.join(", ", EXPORT_KINDS));
				return ExitCodes.USAGE;
			}
		}

		// This command is about applying a session to an explicitly given data set.
		// Therefore we do not allow session files to be given as data arguments.
		for (File dataset : datasets) {
			if (dataset.getName().toLowerCase().endsWith(".peakaboo")) {
				System.err.println("error: " + dataset + " is a session file, not a dataset; pass it with --session");
				return ExitCodes.USAGE;
			}
		}

		
		globals.bootstrap();
		CLIBootstrap.initPeakTable();

		// The surface registry only has anything in it once the bootstrap has run
		SurfaceDescriptor format = surfaceFormat();
		if (format == null) {
			System.err.println("error: unknown image format '" + imageFormat + "'; this installation supports "
					+ String.join(", ", imageFormats()));
			return ExitCodes.USAGE;
		}

		// Load the session file and validate it. Fail fast on mangled and invalid files.
		SavedSession session;
		try {
			session = Sessions.read(sessionFile);
		} catch (Sessions.SessionReadException e) {
			System.err.println("error: " + e.getMessage());
			return ExitCodes.BAD_SESSION;
		}
		Optional<String> missing = session.validate();
		if (missing.isPresent()) {
			System.err.println("error: session is missing its required '" + missing.get() + "' block");
			return ExitCodes.BAD_SESSION;
		}

		// Try to create the output directory
		if (!outDir.exists() && !outDir.mkdirs()) {
			System.err.println("error: cannot create output directory " + outDir);
			return ExitCodes.ERROR;
		}

		// Work through every dataset even if some fail, and report the first failure
		int result = ExitCodes.OK;
		int index = 0;
		for (File dataset : datasets) {
			System.err.printf("[%d/%d] %s%n", ++index, datasets.size(), dataset);
			int datasetResult = applyTo(dataset, session, kinds, format);
			if (datasetResult != ExitCodes.OK && result == ExitCodes.OK) {
				result = datasetResult;
			}
		}
		if (result != ExitCodes.OK && datasets.size() > 1) {
			System.err.println("error: some datasets failed");
		}
		return result;
	}

	private int applyTo(File dataset, SavedSession session, Set<String> kinds, SurfaceDescriptor format) {
		try (PlotController controller = new PlotController(CLIBootstrap.getConfigDir())) {

			try {
				// HeadlessDataLoader loads the data and coordinates with `controller` to store it
				new HeadlessDataLoader(controller).openDataWithSession(Datasets.adapt(List.of(dataset)), session);
			} catch (HeadlessLoadException e) {
				System.err.println("error: " + dataset + ": " + e.getMessage());
				return ExitCodes.BAD_DATA;
			}

			String basename = basename(dataset);
			try {
				if (kinds.contains("plot")) {
					// Plot defaults to a 2:1 size ratio when only one value is given
					Coord<Integer> size = imageSize.hasTwoValues()
							? imageSize.asCoord()
							: new Coord<>(imageSize.a, imageSize.a / 3);
					File file = new File(outDir, basename + "-plot.zip");
					try (OutputStream os = new FileOutputStream(file)) {
						controller.writeArchive(os, format, size);
					}
					System.err.println("    wrote " + file);
				}

				if (kinds.contains("maps")) {
					return writeMapArchive(controller, new File(outDir, basename + "-maps.zip"), format);
				}
				return ExitCodes.OK;
			} catch (IOException e) {
				System.err.println("error: " + dataset + ": " + e.getMessage());
				return ExitCodes.EXPORT_FAILED;
			}

		} catch (Exception e) {
			// PlotController.close() declares a checked Exception
			System.err.println("error: " + dataset + ": " + e.getMessage());
			return ExitCodes.ERROR;
		}
	}

	/**
	 * Fits the maps for this dataset and hands them to the same archive writer the
	 * GUI's map export uses.
	 */
	private int writeMapArchive(PlotController controller, File file, SurfaceDescriptor format) throws IOException {
		System.err.println("    fitting maps...");
		StreamExecutor<RawMapSet> mapTask = controller.getMapTask();
		Optional<RawMapSet> rawMaps = mapTask.run();
		if (rawMaps.isEmpty()) {
			System.err.println("error: map generation failed");
			return ExitCodes.EXPORT_FAILED;
		}

		// Create the map controller from the results and data from the plot controller
		RawDataController rawData = new RawDataController();
		rawData.setMapData(
				rawMaps.get(),
				controller.data().getDataSet(),
				controller.data().getTitle(),
				controller.data().getDiscards().list(),
				controller.calibration().getDetectorProfile()
			);
		MappingController mapController = new MappingController(rawData, controller);
		// Set before we size or draw anything, since the scale mode is part of what gets rendered
		mapController.getFitting().setMapScaleMode(mapScale.mode());

		int sizeResult = applyMapDimensions(controller, mapController);
		if (sizeResult != ExitCodes.OK) {
			return sizeResult;
		}
		
		
		// Determine map image size
		Coord<Integer> size = imageSize.hasTwoValues()
				? imageSize.asCoord()
				: mapController.naturalImageSize(format, imageSize.a);
		System.err.println("    image size: " + size.x + "x" + size.y);
		// Write the maps using the derived sizes
		try (OutputStream os = new FileOutputStream(file)) {
			StreamExecutor<Void> archiver = mapController.writeArchive(os, format, size.x, size.y,
					() -> (ExportableSurface) format.create(size));
			archiver.run();
		}
		System.err.println("    wrote " + file);
		return ExitCodes.OK;
	}

	/**
	 * Try to set the plot controller's data dimensions from the parameter, the
	 **/
	private int applyMapDimensions(PlotController controller, MappingController mapController) {
		
		// First source: CLI parameter
		if (dimensions != null) {
			// Set the controller's dimensions
			mapController.getUserDimensions().setUserDataWidth(dimensions.x);
			mapController.getUserDimensions().setUserDataHeight(dimensions.y);
			// Check that the controller accepted these dimensions
			Coord<Integer> accepted = mapController.getUserDimensions().getDimensions();
			if (accepted.x != dimensions.x.intValue() || accepted.y != dimensions.y.intValue()) {
				System.err.println("error: --dimensions " + dimensions.x + "x" + dimensions.y
						+ " does not fit this dataset's " + mapController.rawDataController.getMapSize() + " scans");
				return ExitCodes.BAD_DATA;
			}
			return ExitCodes.OK;
		}

		
		// Second source: from the dataset itself -- except if one dimension is 1 (likely wrong).
		Coord<Integer> declared = mapController.getUserDimensions().getDimensions();
		if (declared.x > 1 && declared.y > 1) {
			return ExitCodes.OK;
		}

		// Try to guess the dimensions from the data
		System.err.println("    dataset does not declare its dimensions; guessing (use --dimensions WxH to override)...");
		Optional<Coord<Integer>> guess = mapController.getUserDimensions().guessDataDimensions().run();
		if (guess.isEmpty() || guess.get() == null) {
			System.err.println("error: could not guess map dimensions; specify them with --dimensions WxH");
			return ExitCodes.BAD_DATA;
		}
		System.err.println("    guessed dimensions: " + guess.get().x + "x" + guess.get().y);
		mapController.getUserDimensions().setUserDataWidth(guess.get().x);
		mapController.getUserDimensions().setUserDataHeight(guess.get().y);
		return ExitCodes.OK;
	}

	/** Return the first compatible export SurfaceDescriptor for imageFormat, or null */
	private SurfaceDescriptor surfaceFormat() {
		return SurfaceExporterRegistry.exporters().stream()
				.filter(descriptor -> descriptor.extension().equalsIgnoreCase(imageFormat))
				.findFirst()
				.orElse(null);
	}

	/** Return a list of supported image-export file extensions */
	private static List<String> imageFormats() {
		return SurfaceExporterRegistry.exporters().stream().map(SurfaceDescriptor::extension).toList();
	}

	/** Returns the name of the given file without an extension */
	private static String basename(File file) {
		String name = file.getName();
		int dot = name.lastIndexOf('.');
		return dot > 0 ? name.substring(0, dot) : name;
	}

}
