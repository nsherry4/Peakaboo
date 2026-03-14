package org.peakaboo.framework.bolt.plugin.core.container;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;
import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

public class BoltURLContainerTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private static class TestURLContainer extends BoltURLContainer<BoltPlugin> {
		TestURLContainer(URL url, boolean deletable) { super(url, deletable); }
		public List<PluginDescriptor<BoltPlugin>> getPlugins() { return List.of(); }
		public List<BoltIssue<BoltPlugin>> getIssues() { return List.of(); }
		public PluginRegistry<BoltPlugin> getManager() { return null; }
	}

	@Test
	public void getSourcePath_fileUrl_returnsAbsolutePath() throws Exception {
		File f = tempFolder.newFile("test-plugin.jar");
		URL url = f.toURI().toURL();
		TestURLContainer container = new TestURLContainer(url, false);
		assertEquals(f.getAbsolutePath(), container.getSourcePath());
	}

	/** Regression: jar:file: URLs previously threw IllegalArgumentException (only URISyntaxException was caught). */
	@Test
	public void getSourcePath_jarUrl_returnsFallbackPath() throws Exception {
		URL url = new URL("jar:file:/opt/plugins/myplugin.jar!/plugin.yaml");
		TestURLContainer container = new TestURLContainer(url, false);
		String path = container.getSourcePath();
		assertNotNull(path);
		assertTrue("Expected path to contain 'plugin.yaml', got: " + path, path.contains("plugin.yaml"));
	}

	/** Regression: jar:file: URLs previously threw IllegalArgumentException in getSourceName(). */
	@Test
	public void getSourceName_jarUrl_returnsFallbackName() throws Exception {
		URL url = new URL("jar:file:/opt/plugins/myplugin.jar!/plugin.yaml");
		TestURLContainer container = new TestURLContainer(url, false);
		assertEquals("plugin.yaml", container.getSourceName());
	}

	@Test
	public void delete_fileUrl_deletesFile() throws Exception {
		File f = tempFolder.newFile("deleteme.jar");
		assertTrue(f.exists());
		URL url = f.toURI().toURL();
		TestURLContainer container = new TestURLContainer(url, true);
		assertTrue(container.delete());
		assertFalse(f.exists());
	}

	/**
	 * Bug: delete() only catches URISyntaxException, not IllegalArgumentException.
	 * A jar:file: URL causes new File(url.toURI()) to throw IllegalArgumentException
	 * which should be caught and return false, like getSourcePath/getSourceName do.
	 */
	@Test
	public void delete_jarUrl_returnsFalse() throws Exception {
		URL url = new URL("jar:file:/opt/plugins/myplugin.jar!/plugin.yaml");
		TestURLContainer container = new TestURLContainer(url, true);
		assertFalse("delete() should return false for non-file URLs", container.delete());
	}

}
