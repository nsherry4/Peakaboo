package org.peakaboo.framework.bolt.plugin.core;

import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public interface PluginRegistry <P extends BoltPlugin> extends BoltPluginCollection<P> {

	List<BoltIssue<? extends P>> getIssues();

	List<BoltPluginPrototype<? extends P>> getPlugins();

	String getAssetPath();

	String getName();

	void load();

	void clear();

	void reload();

}
