package net.sciencestudio.bolt.plugin.java.issue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.issue.BoltEmptyContainerIssue;

public class BoltEmptyJarIssue extends BoltEmptyContainerIssue {

	private URL url;
	
	public BoltEmptyJarIssue(URL url) {
		this.url = url;
	}
	
	@Override
	public String title() {
		return "Empty Jar Container";
	}

	@Override
	public String description() {
		return "The Jar Container " + shortSource() + " does not seem to contain any plugins";
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
