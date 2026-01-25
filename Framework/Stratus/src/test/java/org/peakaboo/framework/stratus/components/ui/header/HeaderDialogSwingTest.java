package org.peakaboo.framework.stratus.components.ui.header;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.Test;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.test.StratusSwingTest;

/**
 * Tests for HeaderDialog intended behaviour:
 * - Close button triggers dialog.close()
 * - dialog.close() hides the dialog AND runs the onClose callback
 */
public class HeaderDialogSwingTest extends StratusSwingTest {

	private FrameFixture ownerWindow;
	private HeaderDialog dialog;
	private boolean onCloseCalled;

	@Override
	protected void onSetUp() {
		super.onSetUp();
		onCloseCalled = false;

		// Create owner frame (dialogs need a parent)
		JFrame owner = createOnEDT(() -> {
			JFrame f = new JFrame("Owner");
			f.setSize(400, 300);
			f.setLocationRelativeTo(null);
			return f;
		});

		ownerWindow = new FrameFixture(robot(), owner);
		ownerWindow.show();
		robot().waitForIdle();

		// Create the dialog with an onClose callback
		createOnEDT(() -> {
			dialog = new HeaderDialog(owner, () -> onCloseCalled = true);
			dialog.getRootPanel().getHeader().setCentre("Test Dialog");
			dialog.setSize(300, 200);
			dialog.setLocationRelativeTo(owner);
			return null;
		});
		robot().waitForIdle();
	}

	@Test
	public void testDialogHasCloseButtonByDefault() {
		// Show dialog
		createOnEDT(() -> {
			dialog.setVisible(true);
			return null;
		});
		robot().waitForIdle();

		DialogFixture dialogFixture = new DialogFixture(robot(), dialog);

		// Close button should be visible by default (per HeaderWindow.createHeaderPanel design)
		JButtonFixture closeButton = dialogFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});
		closeButton.requireVisible();

		// Clean up
		createOnEDT(() -> {
			dialog.close();
			return null;
		});
	}

	@Test
	public void testCloseButtonHidesDialog() {
		// Show dialog
		createOnEDT(() -> {
			dialog.setVisible(true);
			return null;
		});
		robot().waitForIdle();

		assertThat(dialog.isVisible()).isTrue();

		// Find and click close button
		DialogFixture dialogFixture = new DialogFixture(robot(), dialog);
		JButtonFixture closeButton = dialogFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});

		// Trigger programmatically (Robot clicks can fail with bounds issues)
		FluentButton button = (FluentButton) closeButton.target();
		createOnEDT(() -> {
			button.doClick();
			return null;
		});
		robot().waitForIdle();

		// Dialog should be hidden
		assertThat(dialog.isVisible()).isFalse();
	}

	@Test
	public void testCloseButtonTriggersOnCloseCallback() {
		// Show dialog
		createOnEDT(() -> {
			dialog.setVisible(true);
			return null;
		});
		robot().waitForIdle();

		assertThat(onCloseCalled).isFalse();

		// Find and click close button
		DialogFixture dialogFixture = new DialogFixture(robot(), dialog);
		JButtonFixture closeButton = dialogFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});

		FluentButton button = (FluentButton) closeButton.target();
		createOnEDT(() -> {
			button.doClick();
			return null;
		});
		robot().waitForIdle();

		// onClose callback should have been called
		assertThat(onCloseCalled).isTrue();
	}

	@Test
	public void testCloseMethodHidesDialogAndRunsCallback() {
		// Show dialog
		createOnEDT(() -> {
			dialog.setVisible(true);
			return null;
		});
		robot().waitForIdle();

		assertThat(dialog.isVisible()).isTrue();
		assertThat(onCloseCalled).isFalse();

		// Call close() directly
		createOnEDT(() -> {
			dialog.close();
			return null;
		});
		robot().waitForIdle();

		// Both should happen
		assertThat(dialog.isVisible()).isFalse();
		assertThat(onCloseCalled).isTrue();
	}

	@Test
	public void testDialogProvidesAccessToHeaderPanel() {
		HeaderPanel panel = dialog.getRootPanel();
		assertThat(panel).isNotNull();
		assertThat(panel.getHeader()).isNotNull();
	}

	@Override
	protected void onTearDown() throws Exception {
		// Ensure dialog is closed
		if (dialog != null && dialog.isVisible()) {
			createOnEDT(() -> {
				dialog.close();
				return null;
			});
		}
		if (ownerWindow != null) {
			ownerWindow.cleanUp();
		}
		super.onTearDown();
	}
}
