package org.peakaboo.framework.swidget.widgets.buttons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;

public class FileChooserImageButton extends JButton implements ImageButtonFluentAPI<FileChooserImageButton> {

	private ImageButtonConfigurator configurator;
	
	private File file;
	private String overrideText = null;
	
	private JPanel panelContents;
	private JLabel lblText;
	private JLabel lblIcon;
	
	public FileChooserImageButton(File file) {
		super();
		
		panelContents = new ClearPanel(new BorderLayout());
		lblText = new JLabel();
		lblText.setBorder(new EmptyBorder(0, Spacing.small, 0, Spacing.medium));
		lblIcon = new JLabel();
		lblIcon.setIcon(StockIcon.DOCUMENT_OPEN.toImageIcon(IconSize.BUTTON));
		panelContents.add(lblText, BorderLayout.CENTER);
		panelContents.add(lblIcon, BorderLayout.EAST);
		this.add(panelContents);

		
		configurator = new ImageButtonConfigurator(this, this, new ImageButtonConfig()) {
			protected Dimension getPreferredSize(Dimension superPreferred) {

				int prefHeight = 0, prefWidth = 160;
				Dimension preferred = new Dimension((int)Math.max(superPreferred.getWidth(), prefWidth), (int)Math.max(superPreferred.getHeight(), prefHeight));

				return preferred;
				
			}
		};
		
		getConfigurator().init(() -> getConfigurator().setButtonBorder(false));

		
		setFile(file);
	}
	
	
	@Override
	public ImageButtonConfig getImageButtonConfig() {
		return getConfigurator().getConfiguration();
	}

	@Override
	public void makeButton() {
		getConfigurator().makeButton();		
	}

	@Override
	public FileChooserImageButton getSelf() {
		return this;
	}

	@Override
	public ImageButtonConfigurator getConfigurator() {
		return configurator;
	}

	@Override
	public Dimension getPreferredSize() {
		
		if (super.isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		
		return getConfigurator().getPreferredSize(super.getPreferredSize());
		
	}
	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		
		makeButton();
		postMake();
	}
	
	
	
	public String getOverrideText() {
		return overrideText;
	}


	public void setOverrideText(String overrideText) {
		this.overrideText = overrideText;
		makeButton();
		postMake();
	}

	private void postMake() {
		if (file == null) {
			lblText.setText("(None)");
			setToolTipText("No file selected");
		} else {
			if (overrideText != null) {
				lblText.setText(overrideText);
				setToolTipText(overrideText + " (" + file.getName() + ")");
			} else {
				lblText.setText(file.getName());
				setToolTipText(file.getName());
			}
		}
		
		lblText.setPreferredSize(new Dimension(getPreferredSize().width - 30, 0));
		
	}

	public FileChooserImageButton withFile(File file) {
		setFile(file);
		return this;
	}
	
	

}
