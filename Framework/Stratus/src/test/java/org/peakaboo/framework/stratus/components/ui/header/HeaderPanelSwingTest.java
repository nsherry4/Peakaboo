package org.peakaboo.framework.stratus.components.ui.header;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.junit.Test;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.test.StratusSwingTest;

public class HeaderPanelSwingTest extends StratusSwingTest {

	private FrameFixture window;
	private HeaderPanel headerPanel;

	@Override
	protected void onSetUp() {
		super.onSetUp();

		JFrame frame = createOnEDT(() -> {
			JFrame f = new JFrame("HeaderPanel Test");
			headerPanel = new HeaderPanel();
			headerPanel.getHeader().setCentre("Test Title");
			headerPanel.getHeader().setShowClose(true);

			JLabel bodyContent = new JLabel("Body Content");
			bodyContent.setName("bodyLabel");
			headerPanel.setBody(bodyContent);

			f.add(headerPanel);
			f.setSize(400, 200);
			f.setLocationRelativeTo(null);
			return f;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
		robot().waitForIdle();
	}

	@Test
	public void testHeaderPanelIsVisible() {
		assertThat(headerPanel.isVisible()).isTrue();
	}

	@Test
	public void testHeaderIsAccessible() {
		assertThat(headerPanel.getHeader()).isNotNull();
		assertThat(headerPanel.getHeader()).isInstanceOf(HeaderBox.class);
	}

	@Test
	public void testBodyIsAccessible() {
		assertThat(headerPanel.getBody()).isNotNull();
		assertThat(headerPanel.getBody()).isInstanceOf(JLabel.class);
	}

	@Test
	public void testBodyLabelIsVisible() {
		JLabelFixture bodyLabel = window.label("bodyLabel");
		bodyLabel.requireVisible();
		bodyLabel.requireText("Body Content");
	}

	@Test
	public void testCloseButtonVisible() {
		// Find the FluentButton (close button in header)
		JButtonFixture closeButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
			@Override
			protected boolean isMatching(JButton button) {
				return button instanceof FluentButton;
			}
		});
		closeButton.requireVisible();
	}

	@Test
	public void testBodyCanBeChanged() {
		// Create and set a new body on the EDT
		boolean[] bodyChanged = {false};
		createOnEDT(() -> {
			JLabel newBody = new JLabel("New Body");
			headerPanel.setBody(newBody);
			bodyChanged[0] = headerPanel.getBody() == newBody;
			return null;
		});
		robot().waitForIdle();

		// Verify the body reference was updated
		assertThat(bodyChanged[0]).isTrue();
	}

	@Override
	protected void onTearDown() throws Exception {
		if (window != null) {
			window.cleanUp();
		}
		super.onTearDown();
	}
}
