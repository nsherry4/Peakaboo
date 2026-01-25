package org.peakaboo.framework.stratus.components.ui.fluentcontrols.button;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.Test;
import org.peakaboo.framework.stratus.test.StratusSwingTest;

public class FluentButtonSwingTest extends StratusSwingTest {

	private FrameFixture window;
	private FluentButton button;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		JFrame frame = createOnEDT(() -> {
			JFrame f = new JFrame("FluentButton Test");
			JPanel panel = new JPanel();
			button = new FluentButton("Test Button");
			panel.add(button);
			f.add(panel);
			f.setSize(300, 100);
			f.setLocationRelativeTo(null);
			return f;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
		robot().waitForIdle();
	}

	@Test
	public void testButtonWithTextIsVisible() {
		JButtonFixture buttonFixture = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton b) {
				return b instanceof FluentButton && "Test Button".equals(b.getText());
			}
		});
		buttonFixture.requireVisible();
		buttonFixture.requireEnabled();
	}

	@Test
	public void testButtonActionTriggered() {
		boolean[] actionCalled = {false};

		createOnEDT(() -> {
			button.withAction(() -> actionCalled[0] = true);
			return null;
		});
		robot().waitForIdle();

		// Trigger action programmatically
		createOnEDT(() -> {
			button.doClick();
			return null;
		});
		robot().waitForIdle();

		assertThat(actionCalled[0]).isTrue();
	}

	@Test
	public void testButtonWithTooltip() {
		createOnEDT(() -> {
			button.withTooltip("Test Tooltip");
			return null;
		});
		robot().waitForIdle();

		// Stratus wraps tooltips in HTML for styling
		assertThat(button.getToolTipText()).contains("Test Tooltip");
	}

	@Test
	public void testButtonCanBeDisabled() {
		createOnEDT(() -> {
			button.setEnabled(false);
			return null;
		});
		robot().waitForIdle();

		JButtonFixture buttonFixture = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton b) {
				return b instanceof FluentButton;
			}
		});
		buttonFixture.requireDisabled();
	}

	@Test
	public void testButtonTextCanBeChanged() {
		createOnEDT(() -> {
			button.withText("New Text");
			return null;
		});
		robot().waitForIdle();

		assertThat(button.getText()).isEqualTo("New Text");
	}

	@Override
	protected void onTearDown() throws Exception {
		if (window != null) {
			window.cleanUp();
		}
		super.onTearDown();
	}
}