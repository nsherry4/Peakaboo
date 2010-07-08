package peakaboo.ui.swing.plotting.fitting.fitted;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import peakaboo.controller.plotter.FittingController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.ui.swing.plotting.fitting.Changeable;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.plotting.fitting.MutableTableModel;
import swidget.widgets.ClearPanel;
import swidget.widgets.listcontrols.ListControls;



public class FittingPanel extends ClearPanel implements Changeable
{

	protected ListControls		controls;
	protected JTable			fitTable;

	protected MutableTableModel	tm;

	CurveFittingView			owner;
	FittingController			controller;


	public FittingPanel(final FittingController controller, final CurveFittingView owner)
	{

		this.owner = owner;
		this.controller = controller;
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());

		JScrollPane fitted = createFittedTable();
		this.add(fitted, BorderLayout.CENTER);

		JPopupMenu addMenu = createAddMenu();

		String tooltips[] = {
				"Add elemental fittings",
				"Remove the selected fitting",
				"Clear all fittings",
				"Move the selected fitting up",
				"Move the selected fitting down" };
		controls = new ListControls(tooltips, addMenu) {

			@Override
			public void up()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.moveTransitionSeriesUp(ts);
				owner.changed();
				row = controller.getFittedTransitionSeries().indexOf(ts);
				fitTable.addRowSelectionInterval(row, row);
			}


			@Override
			public void remove()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.removeTransitionSeries(ts);
				owner.changed();
			}


			@Override
			public void down()
			{
				int row = fitTable.getSelectedRow();
				if (row == -1) return;
				TransitionSeries ts = controller.getFittedTransitionSeries().get(row);
				controller.moveTransitionSeriesDown(ts);
				row = controller.getFittedTransitionSeries().indexOf(ts);
				fitTable.addRowSelectionInterval(row, row);
			}


			@Override
			public void clear()
			{
				controller.clearTransitionSeries();
				owner.changed();
			}


			@Override
			public void add()
			{
				owner.elementalAdd();

			}

		};

		this.add(controls, BorderLayout.NORTH);

	}


	public void changed()
	{
		tm.fireChangeEvent();
		
		int elements = controller.getFittedTransitionSeries().size();
		if (elements == 0)
		{
			controls.setElementCount(ListControls.ElementCount.NONE);
		}
		else if (elements == 1)
		{
			controls.setElementCount(ListControls.ElementCount.ONE);
		}
		else
		{
			controls.setElementCount(ListControls.ElementCount.MANY);
		}
		
	}
	
	
	private JPopupMenu createAddMenu()
	{
		JPopupMenu menu = new JPopupMenu();

		JMenuItem elementalAddItem = new JMenuItem("Add Elemental Fitting");
		elementalAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.elementalAdd();
			}
		});

		JMenuItem summationAddItem = new JMenuItem("Add Summation Fitting");
		summationAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.summationAdd();
			}
		});
		
		JMenuItem smartAddItem = new JMenuItem("Guided Fitting");
		smartAddItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				owner.smartAdd();
			}
		});

		menu.add(elementalAddItem);
		menu.add(summationAddItem);
		menu.add(smartAddItem);


		return menu;
	}


	private JScrollPane createFittedTable()
	{

		fitTable = new JTable();

		tm = new MutableTableModel() {

			List<TableModelListener>	listeners;


			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{

				if (columnIndex == 0)
				{
					Boolean visible = (Boolean) aValue;
					controller.setTransitionSeriesVisibility(
							controller.getFittedTransitionSeries().get(rowIndex),
							visible);
				}

			}


			public void removeTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TableModelListener> list();
				listeners.remove(l);
			}


			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return columnIndex == 0;
			}


			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0)
				{
					return controller.getFittedTransitionSeries().get(rowIndex).visible;
				}
				else
				{
					return controller.getFittedTransitionSeries().get(rowIndex);
				}
			}


			public int getRowCount()
			{
				return controller.getFittedTransitionSeries().size();
			}


			public String getColumnName(int columnIndex)
			{
				if (columnIndex == 0) return "Fit";
				return "Transition Series";

			}


			public int getColumnCount()
			{
				return 2;
			}


			public Class<?> getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 0:
						return Boolean.class;
					case 1:
						return TransitionSeries.class;
					default:
						return Object.class;
				}
			}


			public void addTableModelListener(TableModelListener l)
			{
				if (listeners == null) listeners = DataTypeFactory.<TableModelListener> list();
				listeners.add(l);
			}


			public void fireChangeEvent()
			{
				for (TableModelListener l : listeners)
				{
					l.tableChanged(new TableModelEvent(this));
				}
			}
		};


		fitTable.setModel(tm);


		FittingRenderer renderer = new FittingRenderer(controller);
		fitTable.getColumnModel().getColumn(1).setCellRenderer(new FittingRenderer(controller));

		fitTable.setShowVerticalLines(false);
		fitTable.setShowHorizontalLines(false);

		fitTable.setRowHeight(renderer.getPreferredSize().height);

		TableColumn column = fitTable.getColumnModel().getColumn(0);
		column.setMinWidth(40);
		column.setPreferredWidth(40);
		column.setMaxWidth(100);

		fitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scroll = new JScrollPane(fitTable);
		// scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(200, 0));

		return scroll;
	}

}
