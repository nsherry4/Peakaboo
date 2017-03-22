package peakaboo.ui.swing.plotting.datasource;

import java.awt.BorderLayout;
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

import fava.functionable.FList;
import peakaboo.datasource.plugin.AbstractDSP;
import peakaboo.datasource.plugin.plugins.CDFMLSaxDSP;
import peakaboo.datasource.plugin.plugins.ScienceStudioDSP;
import swidget.Swidget;
import swidget.icons.StockIcon;
import swidget.widgets.ButtonBox;
import swidget.widgets.ImageButton;
import swidget.widgets.Spacing;
import swidget.widgets.gradientpanel.TitleGradientPanel;
import swidget.widgets.toggle.ComplexToggle;
import swidget.widgets.toggle.ComplexToggleGroup;


public class DataSourceSelection extends JDialog
{
	
	private Map<ComplexToggle, AbstractDSP> toggleMap;
	private AbstractDSP selected;
	
	public DataSourceSelection()
	{
		
	}
	
	public AbstractDSP pickDSP(Container parent, List<AbstractDSP> dsps)
	{	
		
		toggleMap = new HashMap<ComplexToggle, AbstractDSP>();
		
		setTitle("Please Select Data Format");
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		setResizable(false);
		setModal(true);
		
		TitleGradientPanel title = new TitleGradientPanel("Peakaboo can't decide what format this data is in.", true);
		title.setBorder(Spacing.bMedium());
		//JLabel title = new JLabel("<html><b>Peakaboo can't decide what format this data is</b></html>", JLabel.CENTER);
		//title.setFont(title.getFont().deriveFont(title.getFont().getSize() * 1.5f));
		//title.setBorder(Spacing.bMedium());
		c.add(title, BorderLayout.NORTH);
	
		
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(Spacing.bHuge());
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		
		final List<ComplexToggle> toggleButtons = new ArrayList<ComplexToggle>();
		ComplexToggle toggle;
		final ComplexToggleGroup group = new ComplexToggleGroup();
		for (AbstractDSP dsp : dsps)
		{
			toggle = new ComplexToggle("", dsp.getDataFormat(), dsp.getDataFormatDescription());
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
	
	
	public static void main(String[] args)
	{
		Swidget.initialize();
		DataSourceSelection dss = new DataSourceSelection();
		AbstractDSP dsp = dss.pickDSP(null, new FList<AbstractDSP>(new ScienceStudioDSP(), new CDFMLSaxDSP()));
		System.out.println(dsp.getDataFormat());
	}
	
}
