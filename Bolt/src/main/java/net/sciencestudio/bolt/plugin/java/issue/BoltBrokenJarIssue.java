package net.sciencestudio.bolt.plugin.java.issue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenContainerIssue;

public class BoltBrokenJarIssue extends BoltBrokenContainerIssue {

	private URL url;
	private String message;
	
	public BoltBrokenJarIssue(URL url, String message) {
		this.url = url;
		this.message = message;
	}

	@Override
	public String title() {
		return "Broken Jar Container";
	}

	@Override
	public String description() {
		return "Could not read plugin jar " + shortSource() + ": " + message;
	}

	@Override
	public boolean hasFix() {
		return true;
	}
	
	@Override
	public boolean fix() {
		try {
			File f = new File(this.url.toURI());
			return f.delete();
		} catch (URISyntaxException e) {
			Bolt.logger().log(Level.WARNING, "Could not delete broken jar " + shortSource());
			return false;
		}
	}
	
	@Override
	public String fixName() {
		return "Delete";
	}
	
	@Override
	public boolean isFixDestructuve() {
		return true;
	}

	@Override
	public String shortSource() {
		try {
			return new File(this.url.toURI()).getName();
		} catch (Throwable e) {
			String[] parts = this.url.getFile().split("{/,\\}");
			return parts[parts.length-1];
		}
	}

	@Override
	public String longSource() {
		return this.url.getFile();
	}
	
}
