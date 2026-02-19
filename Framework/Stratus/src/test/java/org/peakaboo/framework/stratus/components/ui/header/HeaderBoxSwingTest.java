package org.peakaboo.framework.stratus.components.ui.header;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.Test;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.test.StratusSwingTest;

public class HeaderBoxSwingTest extends StratusSwingTest {

	private FrameFixture window;
	private HeaderBox headerBox;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		JFrame frame = createOnEDT(() -> {
			JFrame f = new JFrame("HeaderBox Test");
			headerBox = new HeaderBox();
			headerBox.setShowClose(true);
			f.add(headerBox);
			f.setSize(400, 100);
			f.setLocationRelativeTo(null);  // Centre on screen
			return f;
		});

		window = new FrameFixture(robot(), frame);
		window.show();

		// Wait for EDT to process the rebuild scheduled by setShowClose
		robot().waitForIdle();
	}

	@Test
	public void testCloseButtonVisible() {
		// Find the FluentButton (there should only be one - the close button)
		JButtonFixture closeButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});
		closeButton.requireVisible();
	}

	@Test
	public void testCloseButtonAction() {
		boolean[] closeCalled = {false};
		headerBox.setOnClose(() -> closeCalled[0] = true);

		// Find the close button
		JButtonFixture closeButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});

		// Trigger action programmatically instead of clicking
		// (clicking can fail with "out of bounds" errors in some test environments)
		FluentButton fluentButton = (FluentButton) closeButton.target();
		createOnEDT(() -> {
			fluentButton.doClick();
			return null;
		});

		robot().waitForIdle();
		assertThat(closeCalled[0]).isTrue();
	}

	@Override
	protected void onTearDown() throws Exception {
		if (window != null) {
			window.cleanUp();
		}
		super.onTearDown();
	}
}
