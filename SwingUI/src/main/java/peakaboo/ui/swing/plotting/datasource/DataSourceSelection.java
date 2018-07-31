package peakaboo.ui.swing.plotting.datasource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import peakaboo.datasource.model.DataSource;
import swidget.widgets.HeaderBox;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.tabbedinterface.TabbedInterfacePanel;
import swidget.widgets.toggle.ItemToggleButton;
import swidget.widgets.toggle.ToggleGroup;


public class DataSourceSelection extends JPanel
{
	
	private Map<ItemToggleButton, DataSource> toggleMap;
	private DataSource selected;
	
	public DataSourceSelection() {
		super(new BorderLayout());
	}
	
	public void pickDSP(TabbedInterfacePanel parent, List<DataSource> dsps, Consumer<DataSource> onSelect) {	
		
		toggleMap = new HashMap<ItemToggleButton, DataSource>();
		
		JPanel body = new JPanel(new BorderLayout());
		
		TitlePaintedPanel title = new TitlePaintedPanel("Peakaboo can't decide what format this data is in.", true);
		title.setBackgroundPaint(Color.decode("#FFE082"));
		body.add(title, BorderLayout.NORTH);
		
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(Spacing.bHuge());
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		
		final List<ItemToggleButton> toggleButtons = new ArrayList<ItemToggleButton>();
		ItemToggleButton toggle;
		final ToggleGroup group = new ToggleGroup();
		for (DataSource dsp : dsps)
		{
			toggle = new ItemToggleButton("", dsp.getFileFormat().getFormatName(), dsp.getFileFormat().getFormatDescription());
			toggleMap.put(toggle, dsp);
			group.registerButton(toggle);	
			toggleButtons.add(toggle);
			
			optionPanel.add(toggle);
			optionPanel.add(Box.createVerticalStrut(Spacing.medium));
		}
		toggleButtons.get(0).setSelected(true);
		
		body.add(optionPanel, BorderLayout.CENTER);
		
		
		
		
		
		
		JButton ok = new ImageButton("OK").withAction(() -> {
			parent.popModalComponent();
			selected = toggleMap.get(toggleButtons.get(group.getToggledIndex()));
			onSelect.accept(selected);
		});
		
		JButton cancel = new ImageButton("Cancel").withAction(() -> {
			parent.popModalComponent();
		});
		
		HeaderBox box = new HeaderBox(cancel, "Please Select Data Format", ok);
		
		
		
		add(box, BorderLayout.NORTH);
		add(body, BorderLayout.CENTER);
		
		parent.pushModalComponent(this);
		

		
		
	}
	
	
//	public static void main(String[] args) throws UnsupportedLookAndFeelException
//	{
//		Swidget.initialize(() -> {}, "Test");
//		UIManager.setLookAndFeel(new StratusLookAndFeel());
//
//		
//	}
	
}
