package org.peakaboo.framework.stratus.test;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;
import org.peakaboo.framework.stratus.laf.theme.BrightTheme;
import org.peakaboo.framework.stratus.laf.theme.Theme;

/**
 * Base class for AssertJ Swing UI tests.
 * Handles Robot lifecycle and Look and Feel initialisation.
 *
 * <p>Note: AssertJ Swing tests require a display (either real or virtual via Xvfb).
 * They cannot run in true headless mode (java.awt.headless=true).
 */
public abstract class StratusSwingTest extends AssertJSwingJUnitTestCase {

	@Override
	protected void onSetUp() {
		// Initialise Stratus Look and Feel
		initialiseStratusLookAndFeel();

		// Increase timeouts for async operations
		robot().settings().timeoutToBeVisible(3000);
		robot().settings().timeoutToFindPopup(3000);
	}

	private static void initialiseStratusLookAndFeel() {
		try {
			Theme theme = new BrightTheme();
			StratusLookAndFeel laf = new StratusLookAndFeel(theme);
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException("Failed to initialise Stratus for tests", e);
		}
	}

	/**
	 * Safely create components on the Event Dispatch Thread.
	 */
	protected <T> T createOnEDT(java.util.concurrent.Callable<T> callable) {
		return GuiActionRunner.execute(() -> {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
