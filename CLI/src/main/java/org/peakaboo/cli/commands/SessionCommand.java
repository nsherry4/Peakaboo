package org.peakaboo.cli.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.peakaboo.cli.CLIBootstrap;
import org.peakaboo.cli.ExitCodes;
import org.peakaboo.cli.GlobalOptions;
import org.peakaboo.cli.commands.Sessions.SessionReadException;
import org.peakaboo.controller.session.v2.SavedFitting;
import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

@Command(
	name = "session",
	description = "Inspect and validate Peakaboo session files",
	mixinStandardHelpOptions = true,
	subcommands = { SessionCommand.Validate.class }
)
public class SessionCommand {

	@Command(name = "validate",
			description = "Check that a session file will load cleanly",
			mixinStandardHelpOptions = true)
	static class Validate implements Callable<Integer> {

		@Mixin
		GlobalOptions globals;

		@Parameters(arity = "1..*", paramLabel = "SESSIONS", description = "One or more .peakaboo session files")
		List<File> sessionFiles;

		@Override
		public Integer call() {
			globals.bootstrap();
			// We need the peak table to look up fitted shellements
			CLIBootstrap.initPeakTable();

			boolean hasErrors = false;
			for (File sessionFile : sessionFiles) {
				List<String> errors = new ArrayList<>();
				List<String> warnings = new ArrayList<>();
				validate(sessionFile, errors, warnings);

				String status = errors.isEmpty() ? (warnings.isEmpty() ? "OK" : "OK (with warnings)") : "INVALID";
				System.out.println(sessionFile + ": " + status);
				for (String error : errors) {
					System.out.println("    error: " + error);
				}
				for (String warning : warnings) {
					System.out.println("    warning: " + warning);
				}
				hasErrors |= !errors.isEmpty();
			}
			return hasErrors ? ExitCodes.BAD_SESSION : ExitCodes.OK;
		}

		/*
		 * Check if a session is loadable:
		 *
		 * - Can we parse this at all?
		 * - Does the session file have all of the required sections?
		 * - Do we have the plugins it references?
		 * - Do we have the fittings it references?
		 * - Do the referenced data files exist on disk?
		 *
		 * Issues that would derail a real session load should be reported as
		 * errors, otherwise they should be warnings.
		 */
		private void validate(File sessionFile, List<String> errors, List<String> warnings) {
			SavedSession session;
			
			// Can we parse this at all?
			try {
				session = Sessions.read(sessionFile);
			} catch (SessionReadException e) {
				errors.add(e.getMessage());
				return;
			}

			// Does the session file have all of the required sections?
			session.validate().ifPresent(missing -> errors.add("missing required '" + missing + "' block"));
			if (session.data == null || session.fittings == null) {
				// The rest of this method inspects data that we don't have
				return;
			}

			// Do we have the plugins it references?
			checkPlugin(errors, "data source", session.data.datasource, DataSourceRegistry.system());
			checkPlugin(errors, "fitting solver", session.fittings.solver, FittingSolverRegistry.system());
			checkPlugin(errors, "curve fitter", session.fittings.fitter, CurveFitterRegistry.system());
			checkPlugin(errors, "peak model", session.fittings.model, FittingFunctionRegistry.system());
			if (session.filters != null) {
				for (SavedPlugin filter : session.filters) {
					checkPlugin(errors, "filter", filter, FilterRegistry.system());
				}
			}
			
			
			// Do we have the fittings it references?
			if (session.fittings.fittings != null) {
				for (SavedFitting fitting : session.fittings.fittings) {
					if (PeakTable.SYSTEM.get(fitting.shellement) == null) {
						errors.add("unknown fitting '" + fitting.shellement + "' (not in the peak table)");
					}
				}
			}

			// Do the referenced data files exist on disk?
			if (session.data.files != null) {
				for (String filename : session.data.files) {
					if (!dataFileExists(filename)) {
						warnings.add("data file not found: " + filename
								+ " (apply can still retarget this session to other data)");
					}
				}
			}
		}

		private void checkPlugin(
				List<String> errors,
				String pluginType,
				SavedPlugin saved,
				PluginRegistry<? extends BoltPlugin> registry)
		{
			if (saved == null) {
				errors.add("no " + pluginType + " specified");
				return;
			}
			if (registry.fromSaved(saved).isEmpty()) {
				errors.add(pluginType + " '" + saved.name + "' (uuid " + saved.uuid + ") is not installed");
			}
		}

		private boolean dataFileExists(String filename) {
			return DataInputAdapter.fromFilenames(List.of(filename)).get(0).exists();
		}

	}

}
