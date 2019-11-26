package org.peakaboo.ui.swing.plotting;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.peakaboo.common.Version;
import org.peakaboo.framework.swidget.dialogues.HeaderDialog;
import org.peakaboo.framework.swidget.icons.IconFactory;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.live.LiveFrame;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedInterface;
import org.peakaboo.framework.swidget.widgets.tabbedinterface.TabbedLayerPanel;


public class PlotFrame extends LiveFrame
{

	
	private TabbedInterface<TabbedLayerPanel> tabControl;
	private static int openWindows = 0;
	
	public PlotFrame() {
	
		openWindows++;
		//containers = new HashMap<PlotPanel, TabbedContainer>();
		
		tabControl = new TabbedInterface<TabbedLayerPanel>(this, p -> "No Data", 150) {

			@Override
			protected PlotPanel createComponent() {
				return new PlotPanel(this);
			}

			@Override
			protected void destroyComponent(TabbedLayerPanel component){}

			@Override
			protected void titleChanged(String title) {}

			@Override
			protected void titleDoubleClicked(TabbedLayerPanel component) {
				component.titleDoubleClicked();
			}
		};
		

		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e)
			{}
		
			public void windowIconified(WindowEvent e)
			{}
		
			public void windowDeiconified(WindowEvent e)
			{}
		
			public void windowDeactivated(WindowEvent e)
			{}
			
			public void windowClosing(WindowEvent e)
			{
				//Confirm close window/exit.
				
				// Used to mark if we need to prompt the user. If all tabs have no unsaved work,
				// we don't need to ask.
				List<TabbedLayerPanel> tabsToClose = new ArrayList<>();
				List<PlotPanel> unsavedWork = new ArrayList<>();
				boolean prompt = false;
				for (TabbedLayerPanel tab : tabControl.getTabs()) {
					if (tab instanceof PlotPanel) {
						PlotPanel plot = (PlotPanel) tab;
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
					openWindows--;
					PlotFrame.this.dispose();
					if (openWindows == 0) System.exit(0);
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
				details.append("Save changes to ");
				if (unsavedWork.size() > 1) {
					details.append(unsavedWork.size());
					details.append(" projects");
				} else {
					details.append("'");
					details.append(unsavedWork.get(0).getController().data().getTitle());
					details.append("'");
				}
				details.append(" before exiting?");
				details.append("</span><br/><br/>If you don't save your session(s), any unsaved work will be permanently lost.");
				details.append("</html>");
				
				
				HeaderDialog hd = new HeaderDialog(PlotFrame.this);
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
				bodyText.setVerticalAlignment(SwingConstants.TOP);
				body.add(bodyText, BorderLayout.CENTER);
				JLabel icon = new JLabel(StockIcon.BADGE_HELP.toImageIcon(IconSize.ICON));
				icon.setVerticalAlignment(SwingConstants.TOP);
				body.add(icon, BorderLayout.WEST);
				body.setBorder(Spacing.bHuge());
				hd.setBody(body);
				hd.pack();
				hd.setLocationRelativeTo(PlotFrame.this);
				hd.setModal(true);
				hd.setVisible(true);

			}
			
			public void windowClosed(WindowEvent e)
			{}
			
			public void windowActivated(WindowEvent e)
			{}
		});
		
		
		setPreferredSize(new Dimension(1000, 473));
		setIconImage(IconFactory.getImage(Version.logo));
		setTitle("Peakaboo");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		tabControl.init();
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabControl, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}
	
	


	public TabbedInterface<TabbedLayerPanel> getTabControl() {
		return tabControl;
	}

	

	
	

}
