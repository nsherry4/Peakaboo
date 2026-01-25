package org.peakaboo.framework.stratus.components.ui.header;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.junit.BeforeClass;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;
import org.peakaboo.framework.stratus.laf.theme.BrightTheme;
import org.peakaboo.framework.stratus.laf.theme.Theme;

/**
 * Base class for Stratus component tests.
 * Provides common setup for initializing Stratus in headless mode.
 */
public abstract class StratusTest {

	@BeforeClass
	public static void initializeStratus() {
		System.setProperty("java.awt.headless", "true");
		try {
			Theme theme = new BrightTheme();
			StratusLookAndFeel laf = new StratusLookAndFeel(theme);
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException("Failed to initialize Stratus for tests", e);
		}
	}
}