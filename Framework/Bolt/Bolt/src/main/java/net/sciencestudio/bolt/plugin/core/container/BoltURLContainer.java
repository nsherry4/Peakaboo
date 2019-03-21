package net.sciencestudio.bolt.plugin.core.container;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;

public abstract class BoltURLContainer<T extends BoltPlugin> implements BoltContainer<T>  {

	protected URL url;
	
	public BoltURLContainer(URL url) {
		this.url = url;
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
		return true;
	}
	
	
	public URL getURL() {
		return url;
	}
}
