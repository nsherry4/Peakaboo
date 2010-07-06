package peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ClearPanel;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.ImageButton.Layout;


public class MapTabControls extends ClearPanel{

	private JLabel title;
	private ImageButton close;
	
	public MapTabControls(final JTabbedPane tabpane, final MapViewer viewer) {
		
		setBorder(Spacing.bNone());
		
		setLayout(new BorderLayout());
		
		title = new JLabel("Map");
		title.setOpaque(false);
		title.setPreferredSize(new Dimension(100, title.getPreferredSize().height));
		title.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) { tabpane.setSelectedComponent(viewer); }
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		
		add(title, BorderLayout.CENTER);
		
		close = new ImageButton(StockIcon.WINDOW_CLOSE, "Close", "Close this Map View", Layout.IMAGE, false, IconSize.BUTTON, Spacing.iNone(), Spacing.bSmall());
		add(close, BorderLayout.LINE_END);
		close.setFocusable(false);
		
		viewer.controller.addListener(new PeakabooSimpleListener() {
			
			public void change() {
				String titlestring = viewer.getMapViewModel().mapShortTitle();
				title.setText(titlestring);
				title.setToolTipText(titlestring);
			}
		});
		
		close.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				tabpane.remove(viewer);
			}
		});

		
		
	}
	
}
