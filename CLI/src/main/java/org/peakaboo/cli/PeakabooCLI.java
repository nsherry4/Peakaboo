package org.peakaboo.cli;

import org.peakaboo.app.Version;
import org.peakaboo.cli.commands.ApplyCommand;
import org.peakaboo.cli.commands.SessionCommand;
import org.peakaboo.tier.Tier;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
	name = "peakaboo-cli",
	description = "Peakaboo XRF command line interface (Experimental)",
	mixinStandardHelpOptions = true,
	versionProvider = PeakabooCLI.CLIVersionProvider.class,
	subcommands = {
		ApplyCommand.class,
		SessionCommand.class,
	}
)
public class PeakabooCLI {

	static class CLIVersionProvider implements CommandLine.IVersionProvider {
		@Override
		public String[] getVersion() {
			return new String[] {
				Tier.provider().appName() + " " + Version.LONG_VERSION + " (built " + Version.buildDate + ")"
			};
		}
	}

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		CommandLine cli = new CommandLine(new PeakabooCLI());
		if (args.length == 0) {
			cli.usage(System.out);
			System.exit(ExitCodes.USAGE);
		}
		System.exit(cli.execute(args));
	}

}
