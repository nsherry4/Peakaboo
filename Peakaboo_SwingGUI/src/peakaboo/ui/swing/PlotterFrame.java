package peakaboo.ui.swing;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fava.*;

import peakaboo.common.Env;
import peakaboo.common.Version;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.AllMapsModel;
import peakaboo.controller.plotter.ChannelCompositeMode;
import peakaboo.controller.plotter.PlotController;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.eventful.PeakabooMessageListener;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.fileio.AbstractFile;
import peakaboo.mapping.results.MapResultSet;
import peakaboo.ui.swing.fileio.SwingIO;
import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.plotting.PeakabooContainer;
import peakaboo.ui.swing.plotting.PlotCanvas;
import peakaboo.ui.swing.plotting.PlotPanel;
import peakaboo.ui.swing.plotting.filters.FiltersetViewer;
import peakaboo.ui.swing.plotting.fitting.CurveFittingView;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.ToolbarImageButton;
import peakaboo.ui.swing.widgets.ImageButton.Layout;
import peakaboo.ui.swing.widgets.pictures.SavePicture;
import peakaboo.ui.swing.widgets.tasks.TaskListView;
import peakaboo.ui.swing.widgets.toggle.ComplexToggle;



/**
 * This class is the main window for Peakaboo, the plotting window
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PlotterFrame extends JFrame implements PeakabooContainer
{

	public PlotterFrame()
	{
		
		setIconImage(IconFactory.getImage(Version.icon));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 470));
		
		setTitle(Version.title);
		
		getContentPane().add(new PlotPanel(this));
		
		// Display the window.
		pack();
		setVisible(true);

	}

	public Component getComponent()
	{
		return this;
	}

	public Window getWindow()
	{
		return this;
	}

	public Panel getPanel()
	{
		return null;
	}

	public boolean isApplet()
	{
		return false;
	}

	public void close()
	{
		System.exit(0);
	}	

}