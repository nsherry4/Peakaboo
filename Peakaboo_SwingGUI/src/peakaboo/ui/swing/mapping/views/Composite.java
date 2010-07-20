package peakaboo.ui.swing.mapping.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import eventful.EventfulTypeListener;

import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.MapScaleMode;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.mapping.results.MapResult;
import swidget.widgets.Spacing;

public class Composite extends JPanel {
	
	private MapController		controller;
	
	private JRadioButton 		relativeScale;
	private JRadioButton 		absoluteScale;
	
	
	public Composite(MapController _controller) {
		
		this.controller = _controller;
			
		setLayout(new GridBagLayout());
		
		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iNone();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;
		

		/*maingbc.weightx = 1.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		add(createScaleOptions(), maingbc);*/

		maingbc.gridx = 0;
		maingbc.gridy = 0;		
		maingbc.weightx = 1.0;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		//maingbc.gridy+=1;
		add(createElementsList(), maingbc);
		
		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				
				absoluteScale.setSelected(controller.getMapScaleMode() == MapScaleMode.ABSOLUTE);
				relativeScale.setSelected(controller.getMapScaleMode() == MapScaleMode.RELATIVE);			

			}
		});
		
	}
	
	
	private JPanel createElementsList()
	{
				
		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BorderLayout());
				
		//elements list
		elementsPanel.add(createTransitionSeriesList(), BorderLayout.CENTER);
		elementsPanel.add(createScaleOptions(), BorderLayout.SOUTH);
		
		
		return elementsPanel;
	}
	
	
	private JPanel createScaleOptions()
	{
		
		JPanel modeFrame = new JPanel();
		
		TitledBorder titleBorder = new TitledBorder("Scale Intensities by:");
		titleBorder.setBorder(Spacing.bNone());
		
		modeFrame.setBorder(titleBorder);
		modeFrame.setLayout(new BoxLayout(modeFrame, BoxLayout.Y_AXIS));
		
		relativeScale = new JRadioButton("Visible Elements");
		absoluteScale = new JRadioButton("All Elements");
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeScale);
		scaleGroup.add(absoluteScale);
		absoluteScale.setSelected(true);
		
		relativeScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.RELATIVE);
			}
		});
		absoluteScale.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.setMapScaleMode(MapScaleMode.ABSOLUTE);
			}
		});
		
		
		modeFrame.add(relativeScale);
		modeFrame.add(absoluteScale);
		
		return modeFrame;
		
	}
	

	private JScrollPane createTransitionSeriesList()
	{
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {
					Boolean bvalue = (Boolean) value;
					TransitionSeries ts = controller.getActiveTabModel().getAllTransitionSeries().get(rowIndex);
					controller.getActiveTabModel().visible.put(ts, bvalue);
					//controller.getActiveTabModel().mapResults.getMap(rowIndex).visible = bvalue;
					controller.invalidateInterpolation();
				}
			}


			public void removeTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub

			}


			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) return true;
				return false;
			}


			public Object getValueAt(int rowIndex, int columnIndex)
			{
				
				TransitionSeries ts = controller.getActiveTabModel().getAllTransitionSeries().get(rowIndex);
				
				if (columnIndex == 0) {

					return controller.getActiveTabModel().visible.get(ts);

				} else {

					return ts.toElementString();
				}

			}


			public int getRowCount()
			{
				return controller.getActiveTabModel().getAllTransitionSeries().size();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) return "Map";
				return "Element";
			}


			public int getColumnCount()
			{
				return 2;
			}


			public Class<?> getColumnClass(int columnIndex)
			{
				if (columnIndex == 0) return Boolean.class;
				return MapResult.class;
			}


			public void addTableModelListener(TableModelListener l)
			{
				// TODO Auto-generated method stub

			}
		};

		JTable table = new JTable(m);

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		JScrollPane scroll = new JScrollPane(table);

		scroll.setPreferredSize(new Dimension(200, scroll.getPreferredSize().height));

		return scroll;

	}
	
}
