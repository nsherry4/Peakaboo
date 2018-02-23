package peakaboo.ui.swing.mapping.views;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.mapdisplay.MapDisplayController;
import peakaboo.controller.mapper.mapdisplay.MapScaleMode;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.mapping.results.MapResult;
import swidget.widgets.Spacing;

public class Composite extends JPanel {
	
	private MapDisplayController		controller;
	
	private JRadioButton 		relativeScale;
	private JRadioButton 		absoluteScale;
	private JCheckBox			logView;
	
	
	public Composite(MapDisplayController _controller) {
		
		this.controller = _controller;
		
		createElementsList();
		
		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{
				
				absoluteScale.setSelected(controller.getMapScaleMode() == MapScaleMode.ABSOLUTE);
				relativeScale.setSelected(controller.getMapScaleMode() == MapScaleMode.RELATIVE);			
				logView.setSelected(controller.isLogView());
			}
		});
		
	}
	
	
	private void createElementsList()
	{
				
		setLayout(new BorderLayout());
				
		//elements list
		add(createTransitionSeriesList(), BorderLayout.CENTER);
		add(createScaleOptions(), BorderLayout.SOUTH);
		
	}
	
	
	
	
	private JPanel createScaleOptions()
	{
		JPanel viewFrame = new JPanel(new BorderLayout());
		
		logView = new JCheckBox("Logarithmic Scale");
		logView.setSelected(controller.isLogView());
		logView.addActionListener(e -> {
			controller.setLogView(logView.isSelected());
		});
		viewFrame.add(logView, BorderLayout.NORTH);
		
		
		JPanel modeFrame = new JPanel();
		
		TitledBorder titleBorder = new TitledBorder("Scale Intensities by:");
		titleBorder.setBorder(Spacing.bNone());
		
		modeFrame.setBorder(titleBorder);
		modeFrame.setLayout(new BoxLayout(modeFrame, BoxLayout.Y_AXIS));
		
		
		
		
		relativeScale = new JRadioButton("Visible Fittings");
		absoluteScale = new JRadioButton("All Fittings");
		ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(relativeScale);
		scaleGroup.add(absoluteScale);
		absoluteScale.setSelected(true);
		
		relativeScale.addActionListener(e -> controller.setMapScaleMode(MapScaleMode.RELATIVE));
		absoluteScale.addActionListener(e -> controller.setMapScaleMode(MapScaleMode.ABSOLUTE));
		
		modeFrame.add(relativeScale);
		modeFrame.add(absoluteScale);
		
		viewFrame.add(modeFrame, BorderLayout.CENTER);
		
		return viewFrame;
		
	}
	

	private JScrollPane createTransitionSeriesList()
	{
		TableModel m = new TableModel() {

			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
				if (columnIndex == 0) {
					Boolean bvalue = (Boolean) value;
					TransitionSeries ts = controller.getAllTransitionSeries().get(rowIndex);
					controller.setTransitionSeriesVisibility(ts, bvalue);
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
				
				TransitionSeries ts = controller.getAllTransitionSeries().get(rowIndex);
				
				if (columnIndex == 0) {

					return controller.getTransitionSeriesVisibility(ts);

				} else {

					return ts.toElementString();
				}

			}


			public int getRowCount()
			{
				return controller.getAllTransitionSeries().size();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) return "Map";
				return "Fitting";
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
		column.setResizable(false);
		column.setPreferredWidth(45);
		column.setMaxWidth(45);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(0,0));
		

		return scroll;

	}
	
}
