package org.peakaboo.controller.session.v2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.peakaboo.controller.plotter.view.SessionViewModel;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.druthers.DruthersStorable;


public class SavedSession implements DruthersStorable {
	
	public static final String SESSION_FORMAT = "org.peakaboo.session/v2";
	
	public String format;
	public SavedData data;
	public List<SavedPlugin> filters;
	public SavedFittings fittings;
	public SessionViewModel view;
	public SavedAppData app;
	//Optional block for schema extension, defaults to empty rather than null
	public Map<String, Object> extended = new LinkedHashMap<>();
	
	public SavedSession() {}
	
	public SavedSession(
			SavedData data, 
			List<SavedPlugin> filters, 
			SavedFittings fittings, 
			SessionViewModel view,
			SavedAppData app, 
			LinkedHashMap<String, Object> extended
		) {
		this.format = SESSION_FORMAT;
		this.data = data;
		this.filters = filters;
		this.fittings = fittings;
		this.view = view;
		this.app = app;
		this.extended = new LinkedHashMap<>(extended);

	}

	/**
	 * Sessions are deserialized non-strict (missing keys leave fields null rather
	 * than erroring), so a hand-edited, truncated, or partially-corrupted
	 * {@code .peakaboo} file can be missing required blocks. This validates that
	 * the blocks the load path dereferences are present, so loading can fail
	 * cleanly up front instead of NPE-ing partway through and leaving the session
	 * half-loaded.
	 * <p>
	 * {@code extended} is intentionally not checked: it is optional and downstream
	 * consumers tolerate its absence.
	 *
	 * @return the name of the first missing required block, or empty if the session
	 *         is structurally complete enough to load.
	 */
	public Optional<String> validate() {
		if (data == null)                 return Optional.of("data");
		if (data.files == null)           return Optional.of("data.files");
		if (data.discards == null)        return Optional.of("data.discards");
		if (filters == null)              return Optional.of("filters");
		if (fittings == null)             return Optional.of("fittings");
		if (fittings.fittings == null)    return Optional.of("fittings.fittings");
		if (fittings.annotations == null) return Optional.of("fittings.annotations");
		if (view == null)                 return Optional.of("view");
		if (app == null)                  return Optional.of("app");
		if (app.version == null)          return Optional.of("app.version");
		return Optional.empty();
	}

}
