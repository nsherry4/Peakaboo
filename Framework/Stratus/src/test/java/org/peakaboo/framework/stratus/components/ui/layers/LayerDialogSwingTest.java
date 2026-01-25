package org.peakaboo.framework.stratus.components.ui.layers;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.Test;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.test.StratusSwingTest;

/**
 * Tests for LayerDialog intended behaviour:
 * - When no buttons provided, a default "OK" button is created
 * - Any button in the dialog (default or custom) dismisses the dialog when clicked
 * - Layer mode: dialog is pushed/removed as a modal layer
 * - Window mode: dialog appears as undecorated JDialog
 */
public class LayerDialogSwingTest extends StratusSwingTest {

	private FrameFixture window;
	private LayerPanel layerPanel;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		JFrame frame = createOnEDT(() -> {
			JFrame f = new JFrame("LayerDialog Test");
			layerPanel = new LayerPanel(false);

			// Add some content to the layer panel
			JLabel content = new JLabel("Main Content");
			content.setName("mainContent");
			layerPanel.getContentLayer().add(content);

			f.setContentPane(layerPanel);
			f.setSize(600, 400);
			f.setLocationRelativeTo(null);
			return f;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
		robot().waitForIdle();
	}

	@Test
	public void testDefaultOkButtonAppearsWhenNoButtonsProvided() {
		// Create dialog with no buttons
		createOnEDT(() -> {
			LayerDialog dialog = new LayerDialog("Test Title", "Test message body");
			dialog.showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Should find a FluentButton with "OK" text (the default button)
		JButtonFixture okButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton && "OK".equals(button.getText());
			}
		});
		okButton.requireVisible();
	}

	@Test
	public void testDefaultOkButtonDismissesDialog() {
		// Create dialog with no buttons
		LayerDialog[] dialogHolder = new LayerDialog[1];
		createOnEDT(() -> {
			dialogHolder[0] = new LayerDialog("Test Title", "Test message body");
			dialogHolder[0].showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Find and click the OK button
		JButtonFixture okButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton && "OK".equals(button.getText());
			}
		});

		FluentButton button = (FluentButton) okButton.target();
		createOnEDT(() -> {
			button.doClick();
			return null;
		});
		robot().waitForIdle();

		// Verify the dialog layer was removed by checking the OK button is no longer visible
		// The button should not be findable anymore after dialog is dismissed
		boolean buttonStillExists = createOnEDT(() -> {
			return button.isShowing();
		});
		assertThat(buttonStillExists).isFalse();
	}

	@Test
	public void testCustomButtonAppearsInDialog() {
		// Create dialog with custom button
		createOnEDT(() -> {
			LayerDialog dialog = new LayerDialog("Test Title", "Test message body");
			FluentButton customButton = new FluentButton("Custom Action");
			customButton.setName("customButton");
			dialog.addRight(customButton);
			dialog.showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Should find the custom button
		JButtonFixture customButton = window.button("customButton");
		customButton.requireVisible();
		customButton.requireText("Custom Action");
	}

	@Test
	public void testCustomButtonDismissesDialog() {
		// Create dialog with custom button
		boolean[] actionCalled = {false};
		createOnEDT(() -> {
			LayerDialog dialog = new LayerDialog("Test Title", "Test message body");
			FluentButton customButton = new FluentButton("Confirm")
				.withAction(() -> actionCalled[0] = true);
			customButton.setName("confirmButton");
			dialog.addRight(customButton);
			dialog.showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Find and click the custom button
		JButtonFixture confirmButton = window.button("confirmButton");
		JButton button = confirmButton.target();
		createOnEDT(() -> {
			button.doClick();
			return null;
		});
		robot().waitForIdle();

		// Both the custom action and auto-hide should have occurred
		assertThat(actionCalled[0]).isTrue();
		boolean buttonStillExists = createOnEDT(() -> button.isShowing());
		assertThat(buttonStillExists).isFalse();
	}

	@Test
	public void testLeftAndRightButtonsBothAppear() {
		// Create dialog with buttons on both sides
		createOnEDT(() -> {
			LayerDialog dialog = new LayerDialog("Test Title", "Test message body");
			FluentButton leftButton = new FluentButton("Cancel");
			leftButton.setName("cancelButton");
			FluentButton rightButton = new FluentButton("Save");
			rightButton.setName("saveButton");
			dialog.addLeft(leftButton);
			dialog.addRight(rightButton);
			dialog.showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Both buttons should be visible
		window.button("cancelButton").requireVisible();
		window.button("saveButton").requireVisible();
	}

	@Test
	public void testHideMethodRemovesDialogLayer() {
		// Create dialog and keep reference
		LayerDialog[] dialogHolder = new LayerDialog[1];
		FluentButton[] buttonHolder = new FluentButton[1];
		createOnEDT(() -> {
			dialogHolder[0] = new LayerDialog("Test Title", "Test message body");
			buttonHolder[0] = new FluentButton("Action");
			buttonHolder[0].setName("actionButton");
			dialogHolder[0].addRight(buttonHolder[0]);
			dialogHolder[0].showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Verify dialog is showing
		window.button("actionButton").requireVisible();

		// Call hide() directly
		createOnEDT(() -> {
			dialogHolder[0].hide();
			return null;
		});
		robot().waitForIdle();

		// Dialog should be removed
		boolean buttonStillExists = createOnEDT(() -> buttonHolder[0].isShowing());
		assertThat(buttonStillExists).isFalse();
	}

	@Test
	public void testWindowModeShowsDialog() {
		// Test window mode (showInWindow)
		// Modal dialogs block the calling thread, so we launch from EDT via invokeLater
		// and use a timer to auto-dismiss after a short delay
		JFrame[] ownerHolder = new JFrame[1];
		boolean[] dialogShown = {false};
		boolean[] dialogClosed = {false};

		createOnEDT(() -> {
			ownerHolder[0] = new JFrame("Owner");
			ownerHolder[0].setSize(300, 200);
			ownerHolder[0].setLocationRelativeTo(null);
			ownerHolder[0].setVisible(true);
			return null;
		});
		robot().waitForIdle();

		// Create and show dialog on EDT via invokeLater (non-blocking call)
		// Use a timer to click OK after the dialog appears
		createOnEDT(() -> {
			LayerDialog dialog = new LayerDialog("Window Dialog", "Window mode test");

			// Add a custom button that tracks when clicked
			FluentButton closeButton = new FluentButton("Close")
				.withAction(() -> dialogClosed[0] = true);
			dialog.addRight(closeButton);

			// Schedule auto-dismiss via timer (will run after dialog is visible)
			javax.swing.Timer timer = new javax.swing.Timer(200, e -> {
				dialogShown[0] = true;
				dialog.hide();
			});
			timer.setRepeats(false);
			timer.start();

			// This blocks until hide() is called
			dialog.showInWindow(ownerHolder[0]);
			return null;
		});

		// Wait for the dialog to complete its lifecycle
		robot().waitForIdle();

		// Verify the dialog was shown and then hidden
		assertThat(dialogShown[0]).isTrue();

		// Clean up owner frame
		createOnEDT(() -> {
			ownerHolder[0].dispose();
			return null;
		});
	}

	@Test
	public void testDialogWithComponentBody() {
		// Test dialog with custom JComponent body instead of string
		createOnEDT(() -> {
			JLabel customBody = new JLabel("Custom Component");
			customBody.setName("customBody");
			LayerDialog dialog = new LayerDialog("Component Body", customBody);
			dialog.showIn(layerPanel);
			return null;
		});
		robot().waitForIdle();

		// Custom body component should be visible
		window.label("customBody").requireVisible();
		window.label("customBody").requireText("Custom Component");
	}

	@Override
	protected void onTearDown() throws Exception {
		if (window != null) {
			window.cleanUp();
		}
		super.onTearDown();
	}
}
