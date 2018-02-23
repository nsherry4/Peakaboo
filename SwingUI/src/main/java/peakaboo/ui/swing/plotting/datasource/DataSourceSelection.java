package peakaboo.ui.swing.plotting.datasource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import peakaboo.datasource.model.DataSource;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitlePaintedPanel;
import swidget.widgets.toggle.ItemToggleButton;
import swidget.widgets.toggle.ToggleGroup;
import swidget.widgets.toggle.ItemToggleButton;


public class DataSourceSelection extends JDialog
{
	
	private Map<ItemToggleButton, DataSource> toggleMap;
	private DataSource selected;
	
	public DataSourceSelection()
	{
		
	}
	
	public DataSource pickDSP(Container parent, List<DataSource> dsps)
	{	
		
		toggleMap = new HashMap<ItemToggleButton, DataSource>();
		
		setTitle("Please Select Data Format");
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		setResizable(false);
		setModal(true);
		
		TitlePaintedPanel title = new TitlePaintedPanel("Peakaboo can't decide what format this data is in.", true);
		title.setBackgroundPaint(Color.decode("#FFE082"));
		c.add(title, BorderLayout.NORTH);
	
		
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
		
		add(optionPanel, BorderLayout.CENTER);
		
		
		
		ButtonBox box = new ButtonBox();
		ImageButton ok = new ImageButton(StockIcon.CHOOSE_OK, "OK");
		ImageButton cancel = new ImageButton(StockIcon.CHOOSE_CANCEL, "Cancel");
		
		box.addRight(cancel);
		box.addRight(ok);
		add(box, BorderLayout.SOUTH);
		
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				selected = toggleMap.get(toggleButtons.get(group.getToggledIndex()));
				setVisible(false);
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				selected = null;
				setVisible(false);
			}
		});
		
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
		
		return selected;		
		
	}
	
	
//	public static void main(String[] args)
//	{
//		Swidget.initialize();
//		DataSourceSelection dss = new DataSourceSelection();
//		AbstractDataSource dsp = dss.pickDSP(null, new ArrayList<AbstractDataSource>(new ScienceStudio(), new CDFMLSax()));
//		System.out.println(dsp.getDataFormat());
//	}
	
}
