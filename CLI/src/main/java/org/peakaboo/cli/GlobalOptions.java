package org.peakaboo.cli;

import java.io.File;

import picocli.CommandLine.Option;

/**
 * Options every leaf command mixes in. Commands call {@link #bootstrap()} at
 * the top of their {@code call()}.
 */
public class GlobalOptions {

	@Option(names = "--verbose", description = "Verbose logging to the console")
	boolean verbose;

	@Option(names = "--plugins-dir", paramLabel = "DIR",
			description = "Load plugin jars from this directory instead of the application's plugin directory")
	File pluginsDir;

	public void bootstrap() {
		CLIBootstrap.init(pluginsDir, verbose);
	}

}
