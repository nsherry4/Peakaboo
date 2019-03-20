package net.sciencestudio.bolt.plugin.java.issue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import net.sciencestudio.bolt.Bolt;
import net.sciencestudio.bolt.plugin.core.issue.BoltBrokenContainerIssue;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltBrokenJarIssue extends BoltBrokenContainerIssue {

	private BoltJarContainer<?> container;
	private String message;
	
	public BoltBrokenJarIssue(BoltJarContainer<?> container, String message) {
		this.container = container;
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
		return container.isDeletable();
	}
	
	@Override
	public boolean fix() {
		return container.delete();
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
		return container.getSourceName();
	}

	@Override
	public String longSource() {
		return container.getSourcePath();
	}
	
}
