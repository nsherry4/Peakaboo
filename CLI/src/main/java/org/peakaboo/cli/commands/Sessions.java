package org.peakaboo.cli.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.peakaboo.controller.session.v2.SavedSession;
import org.peakaboo.framework.accent.Mutable;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

/** Shared helpers for commands that read session files. */
public final class Sessions {

	private Sessions() {}

	/** Something went wrong reading a session file. */
	public static class SessionReadException extends Exception {
		public SessionReadException(String message) {
			super(message);
		}
	}

	/**
	 * Reads and deserializes a v2 session file.
	 */
	public static SavedSession read(File file) throws SessionReadException {
		String contents;
		try {
			contents = Files.readString(file.toPath(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new SessionReadException("Cannot read session file: " + e.getMessage());
		}

		// Reject any document without a valid format declaration
		if (!DruthersSerializer.hasFormat(contents)) {
			throw new SessionReadException(
					"Not a valid session file. If this is an old session, open and re-save it in the Peakaboo GUI first.");
		}

		Mutable<SavedSession> box = new Mutable<>();
		try {
			DruthersSerializer.deserialize(contents, false,
					new DruthersSerializer.FormatLoader<>(
							SavedSession.SESSION_FORMAT,
							SavedSession.class,
							box::set
						)
				);
		} catch (DruthersLoadException e) {
			throw new SessionReadException("Failed to parse session file: " + e.getMessage());
		}
		// The file had a format identifier, but not one we recognize
		if (box.get() == null) {
			throw new SessionReadException("Not a Peakaboo session file (unrecognized format identifier)");
		}
		return box.get();
	}

}
