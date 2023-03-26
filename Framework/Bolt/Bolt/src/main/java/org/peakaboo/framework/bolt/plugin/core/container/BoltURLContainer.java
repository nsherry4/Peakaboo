package org.peakaboo.framework.bolt.plugin.core.container;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;

public abstract class BoltURLContainer<T extends BoltPlugin> implements BoltContainer<T>  {

	protected URL url;
	protected boolean deletable;
	
	public BoltURLContainer(URL url, boolean deletable) {
		this.url = url;
		this.deletable = deletable;
	}

	@Override
	public String getSourcePath() {
		try {
			File f = new File(url.toURI());
			return f.getAbsolutePath();
		} catch (URISyntaxException e) {
			return url.getPath();
		}
	}

	@Override
	public String getSourceName() {
		try {
			File f = new File(url.toURI());
			return f.getName();
		} catch (URISyntaxException e) {
			String[] parts = url.getPath().split("{/,\\}");
			return parts[parts.length - 1];
		}
	}


	@Override
	public boolean delete() {
		if (!isDeletable()) {
			throw new RuntimeException("Plugin is not deletable");
		}
		try {
			File f = new File(url.toURI());
			f.delete();
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	@Override
	public boolean isDeletable() {
		return deletable;
	}
	
	
	public URL getURL() {
		return url;
	}
}
