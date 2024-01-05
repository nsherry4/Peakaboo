package org.peakaboo.ui.swing.plotting;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.app.Version;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderDialog;
import org.peakaboo.framework.stratus.components.ui.live.LiveFrame;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterfaceTitle;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.widgets.PeakabooTabTitle;


public class PlotFrame extends LiveFrame
{

	
	private TabbedInterface<TabbedLayerPanel> tabControl;
	private String title;
	
	public PlotFrame() {
	
		tabControl = createTabControl();
		title = Tier.provider().appName();
		
		addWindowListener(new TabWindowListener(this, tabControl));
		
		setPreferredSize(new Dimension(1200, 569));
		setIconImage(IconFactory.getImage(Tier.provider().iconPath(), Version.logo));
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		tabControl.init();
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabControl, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}
	
	


	private TabbedInterface<TabbedLayerPanel> createTabControl() {
		return new TabbedInterface<TabbedLayerPanel>(this, p -> "No Data", 180) {

			@Override
			protected PlotPanel createComponent() {
				return new PlotPanel(this);
			}

			@Override
			protected void destroyComponent(TabbedLayerPanel component){
				//NOOP
			}

			@Override
			protected void titleChanged(String title) {
				setTitle(PlotFrame.this.title + " — " + title);
				
			}

			@Override
			protected void titleDoubleClicked(TabbedLayerPanel component) {
				component.titleDoubleClicked();
			}
			
			@Override
			public TabbedInterfaceTitle provideTitleComponent() {
				return new PeakabooTabTitle(this, super.tabWidth);
				
			}
		};
	}




	public TabbedInterface<TabbedLayerPanel> getTabControl() {
		return tabControl;
	}
	

}

class TabWindowListener extends WindowAdapter {
	
	private TabbedInterface<TabbedLayerPanel> tabControl;
	private PlotFrame parent;
	
	//one window will already be open(ing) when this is created
	private static int openWindows = 1; 
	
	public TabWindowListener(PlotFrame parent, TabbedInterface<TabbedLayerPanel> tabControl) {
		this.tabControl = tabControl;
		this.parent = parent;
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		//Confirm close window/exit.
		
		// Used to mark if we need to prompt the user. If all tabs have no unsaved work,
		// we don't need to ask.
		List<TabbedLayerPanel> tabsToClose = new ArrayList<>();
		List<PlotPanel> unsavedWork = new ArrayList<>();
		boolean prompt = false;
		for (TabbedLayerPanel tab : tabControl.getTabs()) {
			if (tab instanceof PlotPanel plot) {
				if (!plot.hasUnsavedWork()) {
					tabsToClose.add(tab);
				} else {
					unsavedWork.add(plot);
					prompt = true;
				}
			} else {
				tabsToClose.add(tab);
			}
		}
		
		Runnable closeAction = () -> {
			synchronized(TabWindowListener.class) {
				openWindows--;
				parent.dispose();
				if (openWindows == 0) {
					System.exit(0);
				}
			}
		};
		
		//if all the tabs are ready to close, just run the close action
		if (!prompt) {
			closeAction.run();
			return;
		}
		
		//close the ones without unsaved work right away
		for (TabbedLayerPanel tab : tabsToClose) {
			tabControl.closeTab(tab);
		}

		StringBuilder details = new StringBuilder();
		details.append("<html><span style='font-size: 145%; font-weight: bold;'>");
		details.append("Save changes before exiting?");
		details.append("</span><br/><br/>");
		
		
		details.append("Save changes to ");
		if (unsavedWork.size() > 1) {
			details.append(unsavedWork.size());
			details.append(" projects?");
		} else {
			details.append("'");
			details.append(unsavedWork.get(0).getController().data().getTitle());
			details.append("'?");
		}

		details.append(" Any unsaved work will be lost.");
		details.append("</html>");
		
		
		HeaderDialog hd = new HeaderDialog(parent);
		hd.getHeader().setCentre("Confirm Exit");
		hd.getHeader().setShowClose(false);
		hd.setPreferredSize(new Dimension(500, 160));
		
		FluentButton close = new FluentButton("Exit")
				.withStateCritical()
				.withAction(closeAction);
		FluentButton stay = new FluentButton("Cancel")
				.withAction(() -> hd.setVisible(false));
		hd.getHeader().setRight(close);
		hd.getHeader().setLeft(stay);
		
		JPanel body = new JPanel(new BorderLayout(Spacing.huge, Spacing.huge));
		JLabel bodyText = new JLabel(details.toString());
		bodyText.setBorder(new EmptyBorder(Spacing.medium, 0, 0, 0));
		bodyText.setVerticalAlignment(SwingConstants.TOP);
		body.add(bodyText, BorderLayout.CENTER);
		JLabel icon = new JLabel(StockIcon.BADGE_QUESTION.toImageIcon(IconSize.ICON));
		icon.setVerticalAlignment(SwingConstants.TOP);
		body.add(icon, BorderLayout.WEST);
		body.setBorder(Spacing.bHuge());
		hd.setBody(body);
		hd.pack();
		hd.setLocationRelativeTo(parent);
		hd.setModal(true);
		hd.setVisible(true);

	}
	
}
