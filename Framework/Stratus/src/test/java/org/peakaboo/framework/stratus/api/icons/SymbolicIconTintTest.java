package org.peakaboo.framework.stratus.api.icons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.junit.Test;
import org.peakaboo.framework.stratus.laf.StratusLookAndFeel;
import org.peakaboo.framework.stratus.laf.theme.BrightTheme;
import org.peakaboo.framework.stratus.laf.theme.DuskTheme;
import org.peakaboo.framework.stratus.laf.theme.Theme;

/**
 * Symbolic icons are a single flat tone carried by their alpha channel, and are
 * meant to be tinted by whoever draws them. Untinted they render in whatever grey
 * the asset was baked at -- 0x212121 for some, 0x555555 for others -- which doesn't
 * even agree with itself in the light theme and is close to invisible in the dark
 * one. {@link IconFactory} tints them with the theme's control text by default.
 * <p>
 * The risk is the other half of the icon set: mime types, folders, badges, and the
 * shaded document-save artwork are real pictures, and recolouring flattens every
 * pixel to one value, which would destroy them. This test pins both halves.
 *
 * @author NAS
 */
public class SymbolicIconTintTest {

	/** Icons that must follow the theme. */
	private static final String[] SYMBOLIC = {
		"edit-copy", "window-close", "edit-undo", "edit-redo", "app-help", "app-about",
		"document-open-symbolic", "document-save-symbolic", "find", "go-up", "zoom-in",
		"menu-main", "selection-all"
	};

	/** Icons that must survive untouched. */
	private static final String[] PICTORIAL = {
		"badge-error", "badge-info", "badge-warning", "choose-ok", "choose-cancel",
		"mime-pdf", "mime-text", "place-folder", "document-open", "document-export",
		"document-save", "document-save-as", "process-completed"
	};

	private static Theme[] themes() {
		return new Theme[] { new BrightTheme(), new DuskTheme() };
	}

	/**
	 * The distinct fully-opaque colours an icon paints. Partially transparent pixels
	 * are skipped: reading them back through an ARGB image round-trips them via
	 * premultiplied alpha and shifts them by a unit or two, which says nothing about
	 * the icon's real colour.
	 */
	private static List<Integer> opaqueColours(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		bi.createGraphics().drawImage(icon.getImage(), 0, 0, null);
		List<Integer> found = new ArrayList<>();
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				int argb = bi.getRGB(x, y);
				if (((argb >> 24) & 0xff) != 255) { continue; }
				int rgb = argb & 0xffffff;
				if (!found.contains(rgb)) { found.add(rgb); }
			}
		}
		return found;
	}

	private static ImageIcon untinted(String name) {
		return new ImageIcon(IconFactory.getImageIconURL(StockIcon.PATH, name, IconSize.BUTTON));
	}

	@Test
	public void symbolicIconsFollowTheTheme() throws Exception {
		for (Theme theme : themes()) {
			UIManager.setLookAndFeel(new StratusLookAndFeel(theme));
			int expected = theme.getControlText().getRGB() & 0xffffff;
			for (String name : SYMBOLIC) {
				List<Integer> painted = opaqueColours(IconFactory.getImageIcon(StockIcon.PATH, name, IconSize.BUTTON));
				assertEquals(name + " should paint exactly one colour", 1, painted.size());
				assertEquals(name + " should be tinted to the control text", expected, (int) painted.get(0));
			}
		}
	}

	@Test
	public void pictorialIconsAreLeftAlone() throws Exception {
		for (Theme theme : themes()) {
			UIManager.setLookAndFeel(new StratusLookAndFeel(theme));
			for (String name : PICTORIAL) {
				assertEquals(name + " should be untouched by the default tint",
						opaqueColours(untinted(name)),
						opaqueColours(IconFactory.getImageIcon(StockIcon.PATH, name, IconSize.BUTTON)));
			}
		}
	}

	@Test
	public void anExplicitColourAlwaysWins() throws Exception {
		UIManager.setLookAndFeel(new StratusLookAndFeel(new DuskTheme()));
		for (String name : SYMBOLIC) {
			List<Integer> painted = opaqueColours(
					IconFactory.getImageIcon(StockIcon.PATH, name, IconSize.BUTTON, Color.RED));
			assertEquals(name + " should honour an explicit colour", List.of(0xff0000), painted);
		}
	}

	@Test
	public void recolouringKeepsFullyOpaquePixelsOpaque() {
		//(alpha * 255) >> 8 leaves an opaque pixel at 254, which makes exact comparison
		//of a recoloured icon impossible and very slightly washes it out
		ImageIcon recoloured = IconFactory.recolour(untinted("edit-copy"), Color.RED);
		BufferedImage bi = new BufferedImage(recoloured.getIconWidth(), recoloured.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		bi.createGraphics().drawImage(recoloured.getImage(), 0, 0, null);

		int maxAlpha = 0;
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				maxAlpha = Math.max(maxAlpha, (bi.getRGB(x, y) >> 24) & 0xff);
			}
		}
		assertEquals("an opaque pixel should stay fully opaque through a recolour", 255, maxAlpha);
	}

	@Test
	public void theReportedIconsAreLegibleInBothThemes() throws Exception {
		for (Theme theme : themes()) {
			UIManager.setLookAndFeel(new StratusLookAndFeel(theme));
			for (String name : new String[] { "edit-copy", "window-close" }) {
				int rgb = opaqueColours(IconFactory.getImageIcon(StockIcon.PATH, name, IconSize.BUTTON)).get(0);
				assertTrue(name + " should reach 4.5:1 against the control colour",
						contrast(new Color(rgb), theme.getControl()) >= 4.5);
			}
		}
	}

	private static double luminance(Color c) {
		int[] channels = { c.getRed(), c.getGreen(), c.getBlue() };
		double[] linear = new double[3];
		for (int i = 0; i < 3; i++) {
			double x = channels[i] / 255d;
			linear[i] = x <= 0.03928 ? x / 12.92 : Math.pow((x + 0.055) / 1.055, 2.4);
		}
		return 0.2126 * linear[0] + 0.7152 * linear[1] + 0.0722 * linear[2];
	}

	private static double contrast(Color a, Color b) {
		double la = luminance(a);
		double lb = luminance(b);
		return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
	}
}
