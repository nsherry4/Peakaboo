/*
 * not mine - got it from 
 * 
 * https://code.google.com/p/aephyr/source/browse/trunk/src/test/aephyr/swing/NimbusThemeCreator.java?r=46
 */

package org.peakaboo.framework.stratus.laf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultRowSorter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Painter;
import javax.swing.RowFilter;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;

import org.peakaboo.framework.stratus.laf.theme.BrightTheme;
import org.peakaboo.framework.stratus.laf.theme.LightTheme;

import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;


//import com.sun.java.swing.Painter;

public class StratusThemeCreator
		implements ActionListener, ChangeListener, ItemListener, PropertyChangeListener, TableModelListener {
	private static final int TABLE_WIDTH = 450;
	private static final int VALUE_WIDTH = 100;
	private static final int DEFAULT_WIDTH = 50;

	public static void main(String[] args) {


		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setStratusLookAndFeel();
				//setNimbusLookAndFeel();
				
				// loadRandom();
				JFrame frame = new JFrame(StratusThemeCreator.class.getSimpleName());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				StratusThemeCreator creator = new StratusThemeCreator();
				frame.add(creator.createBody(), BorderLayout.CENTER);
				frame.getRootPane().setDefaultButton(creator.defaultButton);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private static void setStratusLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new StratusLookAndFeel(new BrightTheme()));
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void setNimbusLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadRandom() {
		Random rdm = new Random();
		for (Map.Entry<Object, Object> entry : UIManager.getLookAndFeelDefaults().entrySet()) {
			Type type = Type.getType(entry.getValue());
			switch (type) {
			case Color:
				UIManager.put(entry.getKey(), new Color(rdm.nextInt(256), rdm.nextInt(256), rdm.nextInt(256)));
				break;
			case Insets:
				UIManager.put(entry.getKey(),
						new Insets(rdm.nextInt(10), rdm.nextInt(10), rdm.nextInt(10), rdm.nextInt(10)));
				break;
			case Font:
				Font font = (Font) entry.getValue();
				UIManager.put(entry.getKey(), font.deriveFont(font.getSize2D() + (rdm.nextBoolean() ? 1 : -1)));
				break;
			case Boolean:
				UIManager.put(entry.getKey(), Boolean.FALSE.equals(entry.getValue()));
				break;
			case Integer:
				Integer i = (Integer) entry.getValue();
				UIManager.put(entry.getKey(), i + rdm.nextInt(5));
			default:
			}
		}
	}

	private JButton update;
	private JCheckBox autoUpdate;
	private JTable primaryTable;
	private JTable secondaryTable;
	private JTable otherTable;
	private JComboBox keyFilter;
	private JComboBox keyFilterMethod;
	private JComboBox typeFilter;
	private JButton defaultButton;

	private static String[] painterKeys;

	private StratusThemeCreator() {
		List<String> primary = new ArrayList<String>();
		List<String> secondary = new ArrayList<String>();
		List<String> other = new ArrayList<String>();
		Set<String> filters = new HashSet<String>();
		List<String> painters = new ArrayList<String>();
		for (Map.Entry<Object, Object> entry : UIManager.getLookAndFeelDefaults().entrySet()) {
			if (!(entry.getKey() instanceof String))
				continue;
			String str = (String) entry.getKey();
			if (Character.isLowerCase(str.charAt(0))) {
				if (entry.getValue() instanceof Color) {
					if (entry.getValue() instanceof ColorUIResource) {
						primary.add(str);
					} else {
						secondary.add(str);
					}
				} else {
					other.add(str);
				}
			} else {
				if (str.endsWith("Painter"))
					painters.add(str);
				int i = str.indexOf('.');
				if (i < 0)
					continue;
				other.add(str);
				if (Character.isLetter(str.charAt(0))) {
					int j = str.indexOf('[');
					if (j >= 0 && j < i)
						i = j;
					j = str.indexOf(':');
					if (j >= 0 && j < i)
						i = j;
					filters.add(str.substring(0, i));
				}
			}
		}
		painterKeys = painters.toArray(new String[painters.size()]);
		Arrays.sort(painterKeys);
		primaryTable = createUITable(false, 0, Type.Color, primary);
		primaryTable.getModel().addTableModelListener(this);
		secondaryTable = createUITable(false, 0, Type.Color, secondary);
		otherTable = createUITable(true, 75, null, other);
		otherTable.setAutoCreateRowSorter(true);
		DefaultRowSorter<?, ?> sorter = (DefaultRowSorter<?, ?>) otherTable.getRowSorter();
		sorter.setSortable(2, false);

		String[] filterArray = filters.toArray(new String[filters.size() + 1]);
		filterArray[filterArray.length - 1] = "";
		Arrays.sort(filterArray);
		keyFilter = new JComboBox(filterArray);
		keyFilter.setToolTipText("Filter Key Column");
		keyFilter.setEditable(true);
		keyFilter.addActionListener(this);
		keyFilterMethod = new JComboBox(new Object[] { "Starts With", "Ends With", "Contains", "Regex" });
		keyFilterMethod.addActionListener(this);
		Object[] types = Type.values();
		Object[] typeArray = new Object[types.length + 1];
		System.arraycopy(types, 0, typeArray, 1, types.length);
		typeArray[0] = "";
		typeFilter = new JComboBox(typeArray);
		typeFilter.setToolTipText("Filter Type Column");
		typeFilter.addActionListener(this);

		update = new JButton("Update UI");
		update.addActionListener(this);
		autoUpdate = new JCheckBox("Auto Update", false);
		autoUpdate.addItemListener(this);

		defaultButton = new JButton("Default");
		defaultButton.setDefaultCapable(true);

	}

	JComponent createBody() {
		JScrollPane primary = titled(new JScrollPane(primaryTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), "Primary");
		JScrollPane secondary = titled(new JScrollPane(secondaryTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), "Secondary");

		JPanel colors = new JPanel(new StackedTableLayout(3, 10, true));
		colors.add(primary);
		colors.add(secondary);
		Dimension size = new Dimension(TABLE_WIDTH, primaryTable.getRowHeight() * 20);
		otherTable.setPreferredScrollableViewportSize(size);

		JScrollPane other = new JScrollPane(otherTable);
		other.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JPanel otherPanel = new JPanel(null);

		JPanel filters = new JPanel(new FiltersLayout());
		filters.add(keyFilter);
		filters.add(keyFilterMethod);
		filters.add(typeFilter);
		otherTable.getColumnModel().getColumn(0).addPropertyChangeListener(this);

		GroupLayout layout = new GroupLayout(otherPanel);
		otherPanel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup().addGap(2)
				.addGroup(layout.createParallelGroup().addComponent(filters).addComponent(other)));
		final int prf = GroupLayout.PREFERRED_SIZE;
		layout.setVerticalGroup(
				layout.createSequentialGroup().addGap(2).addComponent(other).addComponent(filters, prf, prf, prf));

		JTabbedPane options = new JTabbedPane();
		options.addTab("UI Base", colors);
		options.addTab("UI Controls", otherPanel);
		JComponent preview = createPreview();

		JButton imp = new JButton("Import");
		imp.addActionListener(this);
		JButton exp = new JButton("Export");
		exp.addActionListener(this);

		JPanel body = new JPanel(null);
		layout = new GroupLayout(body);
		body.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout
				.createParallelGroup(Alignment.LEADING, false).addComponent(options)
				.addGroup(layout.createSequentialGroup().addGap(4).addComponent(imp).addComponent(exp)
						.addGap(0, 100, Short.MAX_VALUE).addComponent(autoUpdate).addGap(5).addComponent(update)))
				.addComponent(preview));
		layout.setVerticalGroup(
				layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(options)
								.addGroup(layout.createBaselineGroup(false, true).addComponent(imp).addComponent(exp)
										.addComponent(update).addComponent(autoUpdate))
								.addGap(4))
						.addComponent(preview));
		return body;
	}

	private static String getPrototypeString(int chars) {
		char[] c = new char[chars];
		Arrays.fill(c, 'w');
		return new String(c);
	}

	private void createCollections() {
		JList list = new JList(painterKeys);
		list.setPrototypeCellValue(getPrototypeString(50));
		JTree tree = new JTree();
		for (int row = 0; row < tree.getRowCount(); row++)
			tree.expandRow(row);
		TableColumnModel columns = new DefaultTableColumnModel();
		TableColumn nameColumn = new TableColumn(0, 300);
		nameColumn.setHeaderValue("Name");
		columns.addColumn(nameColumn);
		TableColumn typeColumn = new TableColumn(1, 100);
		typeColumn.setHeaderValue("Type");
		columns.addColumn(typeColumn);
		JTable table = new JTable(otherTable.getModel(), columns);
		table.setPreferredScrollableViewportSize(new Dimension(400, table.getRowHeight() * 15));
		JSplitPane hor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, titled(new JScrollPane(tree), "JTree"),
				titled(new JScrollPane(list), "JList"));
		collections.setTopComponent(hor);
		collections.setBottomComponent(titled(new JScrollPane(table), JTable.class.getSimpleName()));
		collections.validate();
		collections.setDividerLocation(0.55);
		hor.setDividerLocation(0.35);
	}

	private void createTexts() {
		JTextArea area = new JTextArea(10, 40);
		Exception ex = new Exception("Little something for the Text Components");
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		pw.flush();
		pw.close();
		String str = writer.toString();
		area.setText(str);
		area.select(0, 0);
		final JEditorPane editor = new JEditorPane();
		editor.setText(str);
		texts.setTopComponent(titled(new JScrollPane(area), JTextArea.class.getSimpleName()));
		texts.setBottomComponent(titled(new JScrollPane(editor), JEditorPane.class.getSimpleName()));
		texts.setDividerLocation(0.5);
	}

	private void createOptions() {
		options.add(createOptionPane("Plain Message", JOptionPane.PLAIN_MESSAGE));
		options.add(createOptionPane("Error Message", JOptionPane.ERROR_MESSAGE));
		options.add(createOptionPane("Information Message", JOptionPane.INFORMATION_MESSAGE));
		options.add(createOptionPane("Warning Message", JOptionPane.WARNING_MESSAGE));
		options.add(createOptionPane("Want to do something?", JOptionPane.QUESTION_MESSAGE));
		JComboBox choiceCombo = new JComboBox(Type.values());
		options.add(titled(new JOptionPane(choiceCombo, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION),
				"Question Message"));

	}

	private void createDesktop() {
		final JDesktopPane desktop = new JDesktopPane();
		JPopupMenu popup = new JPopupMenu();
		ActionListener al = e -> {
			JInternalFrame frame = new JInternalFrame(JInternalFrame.class.getSimpleName(), true, true, true, true);
			frame.setVisible(true);
			frame.setBounds(50, 100, 600, 500);
			desktop.add(frame);
			desktop.moveToFront(frame);
			desktop.setSelectedFrame(frame);
		};
		al.actionPerformed(null);
		popup.add("New Internal Frame").addActionListener(al);
		desktop.setComponentPopupMenu(popup);
		this.desktop.add(desktop, BorderLayout.CENTER);
	}

	private void createFileChooser() {
		fileChooser.add(titled(new JFileChooser(), JFileChooser.class.getSimpleName()));
	}

	private void createColorChooser() {
		colorChooser.add((titled(new JColorChooser(), JColorChooser.class.getSimpleName())));
	}

	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabs = (JTabbedPane) e.getSource();
		int idx = tabs.getSelectedIndex();
		if (idx >= 0 && !created[idx]) {
			created[idx] = true;
			switch (idx) {
			case 1:
				createCollections();
				break;
			case 2:
				createOptions();
				break;
			case 3:
				createTexts();
				break;
			case 4:
				createFileChooser();
				break;
			case 5:
				createColorChooser();
				break;
			case 6:
				createDesktop();
				break;
			}

		}
	}

	private boolean[] created;
	private JSplitPane collections;
	private JPanel options;
	private JSplitPane texts;
	private JPanel fileChooser;
	private JPanel colorChooser;
	private JPanel desktop;

	private JComponent createPreview() {
		JLabel label1 = new JLabel("Hover Here for Tooltip");
		label1.setToolTipText("Tooltip");
		JLabel label2 = disabled(new JLabel("Disabled"));
		JButton button1 = new JButton("Button");
		JButton button2 = disabled(new JButton("Disabled"));
		JToggleButton toggle1 = new JToggleButton("Toggle", true);
		JToggleButton toggle2 = new JToggleButton("Toggle", false);
		JToggleButton toggle3 = disabled(new JToggleButton("Disabled", true));
		JToggleButton toggle4 = disabled(new JToggleButton("Disabled", false));
		JRadioButton radio1 = new JRadioButton("Radio", true);
		JRadioButton radio2 = new JRadioButton("Radio", false);
		JRadioButton radio3 = disabled(new JRadioButton("Disabled", true));
		JRadioButton radio4 = disabled(new JRadioButton("Disabled", false));
		JCheckBox check1 = new JCheckBox("Check", true);
		JCheckBox check2 = new JCheckBox("Check", false);
		JCheckBox check3 = disabled(new JCheckBox("Disabled", true));
		JCheckBox check4 = disabled(new JCheckBox("Disabled", false));
		JPopupMenu popup = new JPopupMenu();
		JMenu menu = new JMenu("Menu");
		menu.add("Item");
		popup.add(menu);
		popup.add(new JMenuItem("Item"));
		JMenuItem item1 = new JMenuItem("Accelerator");
		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		popup.add(item1);
		popup.add(disabled(new JMenuItem("Disabled")));
		popup.addSeparator();
		popup.add(new JRadioButtonMenuItem("Radio", true));
		popup.add(new JRadioButtonMenuItem("Radio", false));
		popup.add(disabled(new JRadioButtonMenuItem("Disabled", false)));
		popup.addSeparator();
		popup.add(new JCheckBoxMenuItem("Check", true));
		popup.add(new JCheckBoxMenuItem("Check", false));
		popup.add(disabled(new JCheckBoxMenuItem("Disabled", false)));
		JTextField text1 = new JTextField("Click Here for Popup");
		text1.setComponentPopupMenu(popup);
		JTextField text2 = disabled(new JTextField("Disabled"));
		JSlider slider1 = new JSlider();
		JSlider slider2 = disabled(new JSlider());
		JSlider slider3 = tickedSlider(false);
		JSlider slider4 = disabled(tickedSlider(false));
		JSlider slider5 = tickedSlider(true);
		JSlider slider6 = disabled(tickedSlider(true));
		JSpinner spinner1 = new JSpinner(new SpinnerNumberModel(100, 0, Short.MAX_VALUE, 100));
		JSpinner spinner2 = disabled(new JSpinner(new SpinnerNumberModel(100, 0, Short.MAX_VALUE, 100)));
		JSpinner spinner3 = new JSpinner(new SpinnerDateModel());
		JSpinner spinner4 = disabled(new JSpinner(new SpinnerDateModel()));
		Type[] values = Type.values();
		JSpinner spinner5 = new JSpinner(new SpinnerListModel(values));
		JSpinner spinner6 = disabled(new JSpinner(new SpinnerListModel(values)));
		JComboBox combo1 = new JComboBox(values);
		JComboBox combo2 = disabled(new JComboBox(values));
		JComboBox combo3 = new JComboBox(values);
		combo3.setEditable(true);
		JComboBox combo4 = disabled(new JComboBox(values));
		combo4.setEditable(true);
		JProgressBar prog1 = progress(0, false);
		JProgressBar prog2 = progress(50, false);
		JProgressBar prog3 = progress(100, false);
		JProgressBar progA = progress(0, true);
		JProgressBar progB = progress(50, true);
		JProgressBar progC = progress(100, true);
		final JProgressBar indeterminate = new JProgressBar();
		indeterminate.setIndeterminate(true);
		JCheckBox hide = new JCheckBox("Hide Indeterminate Progress Bar:", false);
		hide.setHorizontalAlignment(SwingConstants.RIGHT);
		hide.addItemListener(evt -> indeterminate.setVisible(evt.getStateChange() != ItemEvent.SELECTED));
		JPanel other = new JPanel(null);
		GroupLayout layout = new GroupLayout(other);
		other.setLayout(layout);
		final int prf = GroupLayout.PREFERRED_SIZE;

		JPanel toggles = createPanel(JToggleButton.class, 2, 0, toggle1, toggle2, toggle3, toggle4);
		JPanel buttons = createPanel(JButton.class, 1, 0, defaultButton, button1, button2);
		JPanel combos = createPanel(JComboBox.class, 0, 2, combo1, combo2, combo3, combo4);
		JPanel spinners = createPanel(JSpinner.class, 0, 2, spinner1, spinner2, spinner3, spinner4, spinner5, spinner6);
		JPanel checks = createPanel(JCheckBox.class, 2, 0, check1, check2, check3, check4);
		JPanel radios = createPanel(JRadioButton.class, 2, 0, radio1, radio2, radio3, radio4);
		JPanel progs = createPanel(JProgressBar.class, 0, 2, prog1, progA, prog2, progB, prog3, progC, hide,
				indeterminate);
		JPanel texts = createPanel(JTextField.class, 0, 1, text1, text2);
		JPanel labels = createPanel(JLabel.class, 1, 0, label1, label2);
		JPanel sliders = createPanel(JSlider.class, 0, 2, slider1, slider2, slider3, slider4, slider5, slider6);
		layout.linkSize(SwingConstants.HORIZONTAL, combos, spinners);
		layout.setHorizontalGroup(layout.createParallelGroup().addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup().addComponent(buttons, prf, prf, prf)
						.addComponent(toggles, prf, prf, prf)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup().addComponent(radios).addComponent(checks))
								.addGap(0, 0, 20)))
				.addGroup(layout.createParallelGroup().addComponent(texts).addComponent(combos, prf, prf, prf)
						.addComponent(spinners, prf, prf, prf)))
				.addComponent(labels).addComponent(sliders).addComponent(progs));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup().addComponent(buttons, prf, prf, prf)
								.addGap(0, 0, Short.MAX_VALUE).addComponent(toggles, prf, prf, prf)
								.addGap(0, 0, Short.MAX_VALUE).addComponent(radios, prf, prf, prf)
								.addGap(0, 0, Short.MAX_VALUE).addComponent(checks, prf, prf, prf))
						.addGroup(layout.createSequentialGroup().addComponent(texts, prf, prf, prf)
								.addGap(0, 0, Short.MAX_VALUE).addComponent(combos, prf, prf, prf)
								.addGap(0, 0, Short.MAX_VALUE).addComponent(spinners, prf, prf, prf)))
				.addGap(0, 0, Short.MAX_VALUE).addComponent(labels, prf, prf, prf).addGap(0, 0, Short.MAX_VALUE)
				.addComponent(sliders, prf, prf, prf).addGap(0, 0, Short.MAX_VALUE).addComponent(progs, prf, prf, prf));

		this.texts = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		collections = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		options = new JPanel(new GridLayout(0, 2));
		desktop = new JPanel(new BorderLayout());
		fileChooser = new JPanel(new GridBagLayout());
		colorChooser = new JPanel(new GridBagLayout());

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Controls", other);
		tabs.addTab("Collections", collections);
		tabs.addTab("Options", centered(options));
		tabs.addTab("Texts", this.texts);
		tabs.addTab("File Chooser", fileChooser);
		tabs.addTab("Color Chooser", colorChooser);
		tabs.addTab("Desktop Pane", desktop);
		created = new boolean[tabs.getTabCount()];
		created[0] = true;
		tabs.addChangeListener(this);
		return tabs;
	}

	private static JPanel centered(JComponent c) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(c);
		return panel;
	}

	private static JOptionPane createOptionPane(String message, int type) {
		JOptionPane pane = new JOptionPane(message, type);
		String title = message;
		if (type == JOptionPane.QUESTION_MESSAGE) {
			title = "Question Message";
			pane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
		}
		return titled(pane, title);
	}

	private static JProgressBar progress(int value, boolean paint) {
		JProgressBar bar = new JProgressBar();
		bar.setValue(value);
		bar.setStringPainted(paint);
		return bar;
	}

	private static JSlider tickedSlider(boolean paintLabels) {
		JSlider s = new JSlider(0, 100);
		s.setMajorTickSpacing(25);
		s.setMinorTickSpacing(5);
		s.setPaintTicks(true);
		s.setPaintLabels(paintLabels);
		return s;
	}

	private static <T extends JComponent> T disabled(T c) {
		c.setEnabled(false);
		return c;
	}

	private static <T extends JComponent> T titled(T c, String title) {
		c.setBorder(BorderFactory.createTitledBorder(title));
		return c;
	}

	private static JPanel createPanel(Class<?> cls, int rows, int cols, Component... components) {
		JPanel panel = new JPanel(new GridLayout(rows, cols, 5, 0));
		for (Component c : components)
			panel.add(c);
		return titled(panel, cls.getSimpleName());
	}

	private class StackedTableLayout implements LayoutManager {

		StackedTableLayout() {
			this(5, 15, true);
		}

		StackedTableLayout(int minRows, int prefRows, boolean fillHeight) {
			this.minRows = minRows;
			this.prefRows = prefRows;
			this.fillHeight = fillHeight;
		}

		int minRows;
		int prefRows;
		boolean fillHeight;

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		private JScrollPane[] scrollers(Container parent) {
			synchronized (parent.getTreeLock()) {
				int n = parent.getComponentCount();
				if (n == 0)
					return null;
				JScrollPane[] scrollers = new JScrollPane[n];
				while (--n >= 0)
					scrollers[n] = (JScrollPane) parent.getComponent(n);
				return scrollers;
			}
		}

		@Override
		public void layoutContainer(Container parent) {
			JScrollPane[] scrollers = scrollers(parent);
			if (scrollers == null)
				return;
			int[] max = new int[scrollers.length];
			int[] rowHeights = new int[scrollers.length];
			int[] yInsets = new int[scrollers.length];
			int maxTot = 0;
			Insets insets = parent.getInsets();
			int y = insets.top;
			int x = insets.left;
			int height = parent.getHeight() - y - insets.bottom;
			int width = parent.getWidth() - x - insets.right;
			for (int i = scrollers.length; --i >= 0;) {
				JTable table = (JTable) scrollers[i].getViewport().getView();
				Dimension size = scrollers[i].getPreferredSize();
				int h = size.height;
				size = table.getPreferredScrollableViewportSize();
				yInsets[i] = h - size.height;
				rowHeights[i] = table.getRowHeight();
				max[i] = table.getRowHeight() * table.getRowCount();
				maxTot += max[i] + yInsets[i];
			}
			if (maxTot <= height) {
				for (int i = 0; i < scrollers.length; i++) {
					int h = max[i] + yInsets[i];
					scrollers[i].setBounds(x, y, width, h);
					y += h;
				}
			} else {
				int count = max.length;
				int availableHeight = height;
				while (count > 1) {
					int min = Integer.MAX_VALUE;
					int minIdx = -1;
					for (int i = max.length; --i >= 0;) {
						if (max[i] >= 0 && max[i] + yInsets[i] < min) {
							min = max[i] + yInsets[i];
							minIdx = i;
						}
					}
					if (min > availableHeight / count)
						break;
					availableHeight -= min;
					max[minIdx] = -min;
					count--;
				}
				int rem = availableHeight % count;
				availableHeight /= count;
				for (int i = scrollers.length; --i >= 0;) {
					int h = max[i];
					if (h < 0)
						continue;
					if (h + yInsets[i] > availableHeight) {
						h = availableHeight;
						int r = (h - yInsets[i]) % rowHeights[i];
						h -= r;
						rem += r;
						max[i] = h;
					} else {
						max[i] = -h - yInsets[i];
					}
				}
				for (int i = 0; i < scrollers.length; i++) {
					int h = max[i];
					if (h < 0) {
						h = -h;
					} else {
						if (rem > rowHeights[i]) {
							h += rowHeights[i];
							rem -= rowHeights[i];
						}
					}
					scrollers[i].setBounds(x, y, width, h);
					y += h;
				}
			}
			if (fillHeight) {
				JScrollPane s = scrollers[scrollers.length - 1];
				s.setSize(width, s.getHeight() + height - y);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return size(parent, true);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return size(parent, false);
		}

		private Dimension size(Container parent, boolean min) {
			JScrollPane[] scrollers = scrollers(parent);
			if (scrollers == null)
				return new Dimension(0, 0);
			Insets insets = parent.getInsets();
			int height = insets.top + insets.bottom;
			int xInsets = insets.left + insets.right;
			int maxWidth = 0;
			int rows = min ? minRows : prefRows;
			for (int i = scrollers.length; --i >= 0;) {
				JTable table = (JTable) scrollers[i].getViewport().getView();
				Dimension size = scrollers[i].getPreferredSize();
				int w = size.width;
				int h = size.height;
				size = table.getPreferredScrollableViewportSize();
				height += h - size.height + Math.min(rows, table.getRowCount()) * table.getRowHeight();
				w -= size.width;
				size = min ? table.getMinimumSize() : table.getPreferredSize();
				w += size.width;
				if (w > maxWidth)
					maxWidth = w;
			}
			return new Dimension(maxWidth + xInsets, height);
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

	}

	private class FiltersLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			TableColumnModel mdl = otherTable.getColumnModel();
			int cw = mdl.getColumn(0).getWidth();
			Dimension size = keyFilterMethod.getPreferredSize();
			int kfmw = size.width;
			int kfw = cw - kfmw - 10;
			keyFilter.setBounds(0, 0, kfw, size.height);
			keyFilterMethod.setBounds(kfw, 0, kfmw, size.height);
			size = typeFilter.getPreferredSize();
			typeFilter.setBounds(cw, 0, size.width, size.height);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return size(300);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return size(TABLE_WIDTH);
		}

		private Dimension size(int width) {
			Dimension size = keyFilter.getPreferredSize();
			size.width = width;
			return size;
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

	}

	private static class Table extends JTable implements ComponentListener {
		Table(TableModel mdl, TableColumnModel clm) {
			super(mdl, clm);
		}

		@Override
		public void addNotify() {
			super.addNotify();
			Container parent = getParent();
			if (parent instanceof JViewport) {
				parent.addComponentListener(this);
			}
		}

		@Override
		public void removeNotify() {
			super.removeNotify();
			Container parent = getParent();
			if (parent instanceof JViewport) {
				parent.removeComponentListener(this);
			}
		}

		/**
		 * Overridden to supply hasFocus as false to the renderers but still allow the
		 * table to be focusable.
		 */
		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Object value = getValueAt(row, column);
			boolean isSelected = false;
			// Only indicate the selection and focused cell if not printing
			if (!isPaintingForPrint()) {
				isSelected = isCellSelected(row, column);
			}
			return renderer.getTableCellRendererComponent(this, value, isSelected, false, row, column);
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			JViewport port = (JViewport) e.getSource();
			TableColumnModel columns = getColumnModel();
			int width = port.getWidth();
			Insets in = port.getInsets();
			width -= in.left + in.right;
			for (int i = columns.getColumnCount(); --i > 0;)
				width -= columns.getColumn(i).getWidth();
			if (width < 210)
				width = 210;
			TableColumn col = columns.getColumn(0);
			if (width != col.getPreferredWidth()) {
				col.setMinWidth(width);
				col.setPreferredWidth(width);
			}
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

	}

	private static JTable createUITable(boolean keyColumnResizable, int typeWidth, Type type, List<String> lst) {
		String[] keys = lst.toArray(new String[lst.size()]);
		Arrays.sort(keys);
		TableModel mdl = type == null ? new UITableModel(keys) : new UITypeTableModel(keys, type, true);
		JTable table = new Table(mdl, null);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(25);
		TableColumnModel columns = table.getColumnModel();
		int keyWidth = TABLE_WIDTH - typeWidth - VALUE_WIDTH - DEFAULT_WIDTH;
		columns.getColumn(0).setMinWidth(keyWidth);
		columns.getColumn(0).setPreferredWidth(keyWidth);
		columns.getColumn(0).setResizable(keyColumnResizable);
		setWidth(columns.getColumn(1), typeWidth);
		TableColumn column = columns.getColumn(2);
		setWidth(column, VALUE_WIDTH);
		column.setCellRenderer(new UIDefaultsRenderer());
		column.setCellEditor(new UIDefaultsEditor());
		setWidth(columns.getColumn(3), DEFAULT_WIDTH);
		return table;
	}

	private static void setWidth(TableColumn column, int width) {
		column.setPreferredWidth(width);
		column.setResizable(false);
		column.setMinWidth(width);
		column.setMaxWidth(width);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getSource() == primaryTable.getModel()) {
			UITableModel mdl = (UITableModel) secondaryTable.getModel();
			mdl.fireTableRowsUpdated(0, mdl.getRowCount() - 1);
		}
		if (autoUpdate.isSelected() && updater == null) {
			updater = new Runnable() {
				public void run() {
					updater = null;
					updateUI();
				}
			};
			SwingUtilities.invokeLater(updater);
		}
	}

	private Runnable updater;

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("width".equals(e.getPropertyName())) {
			JComponent c = (JComponent) keyFilter.getParent();
			if (c != null) {
				c.revalidate();
				c.repaint();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean b = autoUpdate.isSelected();
		update.setEnabled(!b);
		if (b) {
			secondaryTable.getModel().addTableModelListener(this);
			otherTable.getModel().addTableModelListener(this);
		} else {
			secondaryTable.getModel().removeTableModelListener(this);
			otherTable.getModel().removeTableModelListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == update) {
			updateUI();
		} else if (e.getSource() == keyFilter || e.getSource() == keyFilterMethod || e.getSource() == typeFilter) {
			updateFilter();
		} else if (e.getActionCommand() == "Import") {
			if (importer == null)
				importer = new Importer((JFrame) SwingUtilities.getWindowAncestor(defaultButton));
			importer.showDialog();
		} else if (e.getActionCommand() == "Export") {
			if (exporter == null)
				exporter = new Exporter((JFrame) SwingUtilities.getWindowAncestor(defaultButton));
			exporter.showDialog();
		}
	}

	private Importer importer;
	private Exporter exporter;
	private JFileChooser browse;

	private JFileChooser getFileChooser() {
		if (browse == null)
			browse = new JFileChooser();
		return browse;
	}

	private JComponent createContentPane(JTabbedPane tabs, JButton ok, JButton cancel) {
		JPanel content = new JPanel(null);
		GroupLayout layout = new GroupLayout(content);
		content.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup().addComponent(tabs).addGroup(layout.createSequentialGroup()
						.addGap(0, 200, Short.MAX_VALUE).addComponent(ok).addGap(3).addComponent(cancel).addGap(5)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(tabs, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createBaselineGroup(false, true).addComponent(ok).addComponent(cancel)).addGap(5));
		layout.linkSize(SwingConstants.HORIZONTAL, ok, cancel);
		return content;
	}

	private class Importer implements ActionListener {

		Importer(JFrame frame) {

			location = new JTextField(25);
			JButton browse = new JButton("Browse...");
			browse.addActionListener(this);
			JPanel file = new JPanel(new BorderLayout());
			file.add(titled(createLocation(location, browse), "File Location"), BorderLayout.SOUTH);

			text = new JTextArea(10, 20);

			tabs = new JTabbedPane();
			tabs.addTab("Import from File", file);
			tabs.addTab("Import from Text", new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

			ok = new JButton("OK");
			ok.addActionListener(this);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(this);

			dialog = new JDialog(frame, true);
			dialog.setContentPane(createContentPane(tabs, ok, cancel));
			dialog.pack();
			dialog.setLocationRelativeTo(null);
		}

		JDialog dialog;
		JTabbedPane tabs;
		JTextArea text;
		JTextField location;
		JButton ok;
		File browseFile;

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "Browse...") {
				JFileChooser browse = getFileChooser();
				browse.setFileSelectionMode(JFileChooser.FILES_ONLY);
				browse.setMultiSelectionEnabled(false);
				if (browseFile != null)
					browse.setSelectedFile(browseFile);
				if (JFileChooser.APPROVE_OPTION == browse.showOpenDialog(null)) {
					browseFile = browse.getSelectedFile();
					location.setText(browseFile.getPath());
				}
			} else if (e.getActionCommand() == "OK") {
				if (tabs.getSelectedIndex() == 0) {
					try {
						File file = new File(location.getText());
						if (!file.isFile()) {
							error("Invalid File:\n\t" + file.getCanonicalPath());
							return;
						}
						// TODO
						error("Not Implemented.");
					} catch (IOException x) {
						error("IOException: " + x.getMessage());
						return;
					}
				} else if (tabs.getSelectedIndex() == 1) {
					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
					if (compiler == null) {
						error("No compiler available.");
						return;
					}
					String statements = text.getText();
					StringBuilder s = new StringBuilder(statements.length() + 200);
					String cls = "NimbusTheme";
					s.append("import javax.swing.*;\n");
					s.append("import javax.swing.plaf.*;\n");
					s.append("import java.awt.*;\n");
					s.append("public class ").append(cls).append(" {\n");
					s.append("\tpublic static void loadTheme() {\n");
					s.append(statements);
					s.append("\t}\n}");

					CompilationTask task = compiler.getTask(null, null, null, null, null,
							Arrays.asList(new MemoryFileObject(cls, s.toString())));
					boolean success = task.call();
					if (!success) {
						error("Unable to compile code.");
						return;
					} else {
						try {
							Class.forName(cls).getDeclaredMethod("loadTheme", (Class[]) null).invoke(null,
									(Object[]) null);
							File file = new File(".", cls.replace('.', File.separatorChar).concat(".class"));
							if (file.exists())
								file.delete();
						} catch (Exception x) {
							error(x.getClass().getSimpleName() + ": " + x.getMessage());
							return;
						}
					}
				}
				dialog.dispose();
			} else if (e.getActionCommand() == "Cancel") {
				dialog.dispose();
			}
		}

		void showDialog() {
			ok.getRootPane().setDefaultButton(ok);
			dialog.setVisible(true);
		}

	}

	private class MemoryFileObject extends SimpleJavaFileObject {
		final String code;

		MemoryFileObject(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

	private JComponent createLocation(JTextField location, JButton browse) {
		JPanel panel = new JPanel(null);
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(location).addComponent(browse));
		int prf = GroupLayout.PREFERRED_SIZE;
		layout.setVerticalGroup(layout.createBaselineGroup(false, true).addComponent(location, prf, prf, prf)
				.addComponent(browse, prf, prf, prf));
		return panel;
	}

	private void error(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private boolean confirm(String msg) {
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, msg, "Confirm",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	private class Exporter implements ActionListener, ChangeListener {
		Exporter(JFrame frame) {

			JLabel pkgLabel = new JLabel("Package Name:");
			JLabel clsLabel = new JLabel("Class Name:");
			JLabel mtdLabel = new JLabel("Method Name:");
			packageField = new JTextField();
			classField = new JTextField("NimbusTheme");
			methodField = new JTextField("loadTheme");
			location = new JTextField(25);
			JButton browse = new JButton("Browse...");
			browse.addActionListener(this);

			JPanel options = titled(new JPanel(null), "Naming Options");
			GroupLayout layout = new GroupLayout(options);
			options.setLayout(layout);
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addComponent(pkgLabel)
							.addComponent(clsLabel).addComponent(mtdLabel))
					.addGap(5).addGroup(layout.createParallelGroup().addComponent(packageField).addComponent(classField)
							.addComponent(methodField)));
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(layout.createBaselineGroup(false, true).addComponent(pkgLabel).addComponent(packageField))
					.addGroup(layout.createBaselineGroup(false, true).addComponent(clsLabel).addComponent(classField))
					.addGroup(
							layout.createBaselineGroup(false, true).addComponent(mtdLabel).addComponent(methodField)));
			JComponent save = titled(createLocation(location, browse), "Save Location");
			JPanel file = new JPanel(null);
			layout = new GroupLayout(file);
			file.setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup().addComponent(options).addComponent(save));
			final int prf = GroupLayout.PREFERRED_SIZE;
			layout.setVerticalGroup(layout.createSequentialGroup().addComponent(options, prf, prf, prf)
					.addComponent(save, prf, prf, prf));

			text = new JTextArea();
			text.setEditable(false);

			tabs = new JTabbedPane();
			tabs.addChangeListener(this);
			tabs.addTab("Export to File", file);
			tabs.addTab("Export to Text", new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

			ok = new JButton("OK");
			ok.addActionListener(this);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(this);

			dialog = new JDialog(frame, true);
			dialog.setContentPane(createContentPane(tabs, ok, cancel));
			dialog.pack();
			dialog.setLocationRelativeTo(null);
		}

		private JDialog dialog;
		private JTabbedPane tabs;
		private JTextField packageField;
		private JTextField classField;
		private JTextField methodField;
		private JTextField location;
		private JButton ok;
		private JTextArea text;
		private boolean validTextArea;
		private File browseDirectory;

		public void stateChanged(ChangeEvent e) {
			if (!validTextArea && tabs.getSelectedIndex() == 1) {
				validTextArea = true;
				StringWriter writer = new StringWriter();
				try {
					export(writer, null);
					text.setText(writer.toString());
				} catch (IOException x) {
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "Browse...") {
				JFileChooser browse = getFileChooser();
				browse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				browse.setMultiSelectionEnabled(false);
				if (browseDirectory != null)
					browse.setSelectedFile(browseDirectory);
				if (JFileChooser.APPROVE_OPTION == browse.showSaveDialog(null)) {
					browseDirectory = browse.getSelectedFile();
					location.setText(browseDirectory.getPath());
				}
			} else if (e.getActionCommand() == "OK") {
				if (tabs.getSelectedIndex() == 0) {
					String pkg = packageField.getText();
					String cls = classField.getText();
					String mtd = methodField.getText();
					File dir = new File(location.getText());
					try {
						if (!dir.isDirectory()) {
							if (dir.isFile()) {
								error("Invalid location:\n\t" + dir.getCanonicalPath()
										+ "\nLocation must be a directory.");
								return;
							}
							if (!confirm("Directory does not exist:\n\t" + dir.getCanonicalPath() + "\nCreate?"))
								return;
							dir.mkdirs();
							if (!dir.isDirectory()) {
								error("Unable to create directory:\n\t" + dir.getCanonicalPath());
								return;
							}
						}
						File file = new File(dir, cls.concat(".java"));
						if (file.exists()) {
							if (!confirm("File already exists:\n\t" + file.getCanonicalPath() + "\nOverwrite?"))
								return;
						}
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						if (pkg != null && !pkg.isEmpty()) {
							writer.write(pkg);
							writer.newLine();
							writer.newLine();
						}
						writer.write("import javax.swing.*;");
						writer.newLine();
						writer.write("import javax.swing.plaf.*;");
						writer.newLine();
						writer.write("import java.awt.*;");
						writer.newLine();
						writer.newLine();
						writer.write("public class ");
						writer.write(cls);
						writer.write(" {");
						writer.newLine();
						writer.write("\tpublic static void ");
						writer.write(mtd);
						writer.write(" {");
						writer.newLine();
						export(writer, "\t\t");
						writer.write("\t}");
						writer.newLine();
						writer.write("}");
						writer.flush();
						writer.close();
					} catch (IOException x) {
						error("IOException: " + x.getMessage());
						return;
					}
				}
				dialog.dispose();
			} else if (e.getActionCommand() == "Cancel") {
				dialog.dispose();
			}
		}

		void showDialog() {
			ok.getRootPane().setDefaultButton(ok);
			validTextArea = false;
			tabs.setSelectedIndex(0);
			dialog.setVisible(true);

		}

		private void export(Writer writer, String prefix) throws IOException {
			BufferedWriter buf = writer instanceof BufferedWriter ? (BufferedWriter) writer : null;
			UIDefaults def = UIManager.getDefaults();
			for (Map.Entry<Object, Object> entry : def.entrySet()) {
				if (def.containsKey(entry.getKey())) {
					if (prefix != null)
						writer.write(prefix);
					writer.write("UIManager.put(\"");
					writer.write(entry.getKey().toString());
					Object obj = entry.getValue();
					Type type = Type.getType(obj);
					switch (type) {
					case Color:
						Color color = (Color) obj;
						writer.write("\", new ColorUIResource(0x");
						writer.write(Integer.toHexString(color.getRGB() & 0xffffff));
						writer.write("));");
						break;
					case Painter:
						break;
					case Insets:
						Insets insets = (Insets) obj;
						writer.write("\", new InsetsUIResource(");
						writer.write(Integer.toString(insets.top));
						writer.write(", ");
						writer.write(Integer.toString(insets.left));
						writer.write(", ");
						writer.write(Integer.toString(insets.bottom));
						writer.write(", ");
						writer.write(Integer.toString(insets.right));
						writer.write("));");
						break;
					case Font:
						Font font = (Font) obj;
						writer.write("\", new FontUIResource(\"");
						writer.write(font.getFamily());
						writer.write("\", ");
						String style = font.isBold() ? "Font.BOLD" : null;
						style = font.isItalic() ? style == null ? "Font.ITALIC" : style + " | " + "Font.ITALIC"
								: "Font.PLAIN";
						writer.write(style);
						writer.write(", ");
						writer.write(font.getSize());
						writer.write("));");
						break;
					case Boolean:
						writer.write("\", Boolean.");
						writer.write(obj == Boolean.TRUE ? "TRUE" : "FALSE");
						writer.write(");");
						break;
					case Integer:
						writer.write("\", new Integer(");
						writer.write(obj.toString());
						writer.write("));");
						break;
					case String:
						writer.write("\", \"");
						writer.write(obj.toString());
						writer.write('"');
						writer.write(");");
						break;
					case Icon:
						break;
					case Dimension:
						Dimension size = (Dimension) obj;
						writer.write("\", new DimensionUIResource(");
						writer.write(Integer.toString(size.width));
						writer.write(", ");
						writer.write(Integer.toString(size.height));
						writer.write("));");
						break;
					case Object:
						break;
					}
					if (buf != null) {
						buf.newLine();
					} else {
						writer.write('\n');
					}
				}
			}
		}

	}

	private void updateUI() {
		for (Window window : Window.getWindows()) {
			SwingUtilities.updateComponentTreeUI(window);
		}
		defaultButton.getRootPane().setDefaultButton(defaultButton);
	}

	private void updateFilter() {
		DefaultRowSorter<TableModel, Object> sorter = (DefaultRowSorter<TableModel, Object>) otherTable.getRowSorter();
		String key = keyFilter.getSelectedItem().toString();
		RowFilter<TableModel, Object> filter = null;
		if (!key.isEmpty()) {
			Object method = keyFilterMethod.getSelectedItem();
			if (method == "Starts With") {
				filter = RowFilter.regexFilter('^' + Pattern.quote(key), 0);
			} else if (method == "Ends With") {
				filter = RowFilter.regexFilter(Pattern.quote(key) + '$', 0);
			} else if (method == "Contains") {
				filter = RowFilter.regexFilter(Pattern.quote(key), 0);
			} else {
				filter = RowFilter.regexFilter(key, 0);
			}
		}
		String type = typeFilter.getSelectedItem().toString();
		if (!type.isEmpty()) {
			RowFilter<TableModel, Object> typeFilter = RowFilter.regexFilter('^' + type + '$', 1);
			filter = filter == null ? typeFilter
					: RowFilter.<TableModel, Object>andFilter(Arrays.asList(filter, typeFilter));
		}
		sorter.setRowFilter(filter);
	}

	private enum Type {
		Color(ColorChooser.class), Painter(null), Insets(InsetsChooser.class), Font(FontChooser.class), Boolean(
				BooleanChooser.class), Integer(IntegerChooser.class), String(
						StringChooser.class), Icon(null), Dimension(DimensionChooser.class), Object(null);

		private Type(Class<? extends ValueChooser> cls) {
			chooserClass = cls;
		}

		ValueChooser chooser;
		Class<? extends ValueChooser> chooserClass;

		ValueChooser getValueChooser() {
			if (chooser == null) {
				if (chooserClass == null)
					return null;
				try {
					chooser = chooserClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					chooserClass = null;
					return null;
				}
			}
			return chooser;
		}

		static Type getType(Object obj) {
			if (obj instanceof Color) {
				return Color;
			} else if (obj instanceof Painter<?>) {
				return Painter;
			} else if (obj instanceof Insets) {
				return Insets;
			} else if (obj instanceof Font) {
				return Font;
			} else if (obj instanceof Boolean) {
				return Boolean;
			} else if (obj instanceof Integer) {
				return Integer;
			} else if (obj instanceof Icon) {
				return Icon;
			} else if (obj instanceof String) {
				return String;
			} else if (obj instanceof Dimension) {
				return Dimension;
			} else {
				return Object;
			}

		}

	}

	private static class UITypeTableModel extends UITableModel {
		UITypeTableModel(String[] keys, Type typ, boolean edt) {
			super(keys, null);
			type = typ;
			editable = edt;
		}

		private Type type;
		private boolean editable;

		@Override
		Type getType(int row) {
			return type;
		}

		@Override
		public String getColumnName(int col) {
			return col == 2 ? type.name() : super.getColumnName(col);
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return editable ? super.isCellEditable(row, col) : false;
		}

	}

	private static class UITableModel extends AbstractTableModel {
		UITableModel(String[] kys) {
			this(kys, new Type[kys.length]);
		}

		UITableModel(String[] kys, Type[] tys) {
			keys = kys;
			types = tys;
		}

		private String[] keys;
		private Type[] types;

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Key";
			case 1:
				return "Type";
			case 2:
				return "Value";
			case 3:
				return "Default";
			}
			throw new IllegalArgumentException();
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch (col) {
			case 2:
				return UIDefaults.class;
			case 3:
				return Boolean.class;
			}
			return Object.class;
		}

		@Override
		public int getRowCount() {
			return keys.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return keys[row];
			case 1:
				return getType(row);
			case 2:
				return UIManager.get(keys[row]);
			case 3:
				return !UIManager.getDefaults().containsKey(keys[row]);
			}
			throw new IllegalArgumentException();
		}

		Type getType(int row) {
			if (types[row] == null)
				types[row] = Type.getType(UIManager.get(keys[row]));
			return types[row];
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col == 2 || col == 3;
		}

		// Font.equals() is too sensitive, so use this.
		private boolean fontEquals(Font a, Font b) {
			return a.getSize2D() == b.getSize2D() && a.getStyle() == b.getStyle()
					&& a.getFamily().equals(b.getFamily());
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			switch (col) {
			case 2:
				Object def = UIManager.getLookAndFeel().getDefaults().get(keys[row]);
				if ((getType(row) == Type.Font && fontEquals((Font) aValue, (Font) def)) || aValue.equals(def)) {
					UIManager.put(keys[row], null);
				} else {
					UIManager.put(keys[row], aValue);
				}
				fireTableCellUpdated(row, 3);
				break;
			case 3:
				if (aValue == Boolean.TRUE) {
					UIManager.put(keys[row], null);
					fireTableCellUpdated(row, 2);
				}
				break;
			}
		}

	}

	private static class UIDefaultsRenderer extends JComponent implements TableCellRenderer {
		private static final Font BOOLEAN_FONT = Font.decode("sansserif-bold");

		Object value;
		Type type;
		int row = -1;
		boolean selected = false;

		@Override
		public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected, boolean hasFocus,
				int row, int column) {
			UITableModel mdl = (UITableModel) tbl.getModel();
			value = val;
			type = mdl.getType(tbl.convertRowIndexToModel(row));
			this.row = row;
			selected = isSelected;
			return this;
		}

		protected void paintComponent(Graphics g) {
			if (selected) {
				g.setColor(UIManager.getColor("Table[Enabled+Selected].textBackground"));
				g.fillRect(0, 0, getWidth(), getHeight());
			} else if (row % 2 == 0) {
				g.setColor(UIManager.getColor("Table.alternateRowColor"));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			switch (type) {
			case Color: {
				Color col = (Color) value;
				g.setColor(col);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
			}
				break;
			case Painter: {
				Painter<JComponent> painter = (Painter<JComponent>) value;
				g.translate((getWidth() - getHeight()) / 2, 0);
				painter.paint((Graphics2D) g, this, getHeight(), getHeight());
			}
				break;
			case Insets: {
				Insets in = (Insets) value;
				g.setColor(Color.BLACK);
				g.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
				g.setColor(Color.GRAY);
				g.drawRect(3 + in.left, 3 + in.top, getWidth() - 6 - in.right - in.left,
						getHeight() - 6 - in.bottom - in.top);
			}
				break;
			case Font: {
				Font font = (Font) value;
				drawString(g, font.getFamily(), font);
			}
				break;
			case Boolean:
				drawString(g, value.toString(), BOOLEAN_FONT);
				break;
			case Integer:
			case String:
				drawString(g, value.toString(), getFont());
				break;
			case Icon: {
				Icon icn = (Icon) value;
				int x = (getWidth() - icn.getIconWidth()) / 2;
				int y = (getHeight() - icn.getIconHeight()) / 2;
				icn.paintIcon(this, g, x, y);
			}
				break;
			case Dimension: {
				Dimension d = (Dimension) value;
				if (d.width < getWidth() - 2 && d.height < getHeight() - 2) {
					g.setColor(Color.GRAY);
					g.drawRect((getWidth() - d.width) / 2, (getHeight() - d.height) / 2, d.width, d.height);
				} else {
					drawString(g, d.width + " x " + d.height, getFont());
				}
			}
				break;
			case Object: {
				System.out.println(value.getClass());
			}
				break;
			}
		}

		private void drawString(Graphics g, String str, Font font) {
			g.setColor(selected ? UIManager.getColor("Table[Enabled+Selected].textForeground")
					: UIManager.getColor("Table.textForeground"));
			g.setFont(font);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics metrics = g.getFontMetrics();
			int w = metrics.stringWidth(str);
			int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
			int x;
			int cw = getWidth();
			if (w > cw) {
				w = metrics.charWidth('.') * 3;
				int i = 0;
				while (w < cw)
					w += metrics.charWidth(str.charAt(i++));
				str = str.substring(0, i - 1).concat("...");
				x = 0;
			} else {
				x = (cw - w) / 2;
			}
			g.drawString(str, x, y);
		}
	}

	private static class UIDefaultsEditor extends AbstractCellEditor
			implements TableCellEditor, ActionListener, MouseListener {
		private static final String OK = "OK";
		private static final String CANCEL = "Cancel";

		public UIDefaultsEditor() {
			renderer = new UIDefaultsRenderer();
			renderer.addMouseListener(this);
			popup = new JPopupMenu();
			popup.setLayout(new BorderLayout());
			JButton ok = new JButton(OK);
			ok.addActionListener(this);
			JButton cancel = new JButton(CANCEL);
			cancel.addActionListener(this);
			JPanel buttons = new JPanel(null);
			GroupLayout layout = new GroupLayout(buttons);
			buttons.setLayout(layout);
			layout.setHorizontalGroup(layout
					.createParallelGroup(Alignment.CENTER, true).addGroup(layout.createSequentialGroup().addGap(8)
							.addComponent(ok).addGap(5).addComponent(cancel).addGap(8))
					.addGap(100, 100, Short.MAX_VALUE));
			layout.setVerticalGroup(layout.createBaselineGroup(false, true).addComponent(ok).addComponent(cancel));
			layout.linkSize(SwingUtilities.HORIZONTAL, ok, cancel);

			popup.add(buttons, BorderLayout.SOUTH);
		}

		UIDefaultsRenderer renderer;
		JPopupMenu popup;
		ValueChooser currentChooser;

		@Override
		public Object getCellEditorValue() {
			return renderer.value;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return renderer.getTableCellRendererComponent(table, value, true, false, row, column);
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			if (e instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) e;
				return (me.getModifiersEx() & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
						| InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) == 0;
			}
			return super.isCellEditable(e);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == OK) {
				Object value = currentChooser.getValue();
				if (!value.equals(renderer.value))
					renderer.value = value;
				currentChooser = null;
				popup.setVisible(false);
				fireEditingStopped();
			} else if (e.getActionCommand() == CANCEL) {
				currentChooser = null;
				popup.setVisible(false);
				fireEditingCanceled();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent evt) {
			currentChooser = renderer.type.getValueChooser();
			if (currentChooser == null)
				return;
			currentChooser.setValue(renderer.value);
			BorderLayout layout = (BorderLayout) popup.getLayout();
			Component cur = layout.getLayoutComponent(BorderLayout.CENTER);
			if (cur != currentChooser.getComponent()) {
				if (cur != null)
					popup.remove(cur);
				popup.add(currentChooser.getComponent(), BorderLayout.CENTER);
			}
			popup.show(renderer, 0, renderer.getHeight());
		}

	}

	private static abstract class ValueChooser {

		abstract JComponent getComponent();

		abstract void setValue(Object value);

		abstract Object getValue();

	}

	private static class BooleanChooser extends ValueChooser {
		@SuppressWarnings("unused")
		BooleanChooser() {
			tru = new JRadioButton(Boolean.TRUE.toString());
			tru.setFont(UIDefaultsRenderer.BOOLEAN_FONT);
			fal = new JRadioButton(Boolean.FALSE.toString());
			fal.setFont(UIDefaultsRenderer.BOOLEAN_FONT);
			ButtonGroup group = new ButtonGroup();
			group.add(tru);
			group.add(fal);
			pane = new JPanel(null);
			GroupLayout layout = new GroupLayout(pane);
			pane.setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(Alignment.CENTER, true).addGap(100, 100, Short.MAX_VALUE)
							.addGroup(layout.createSequentialGroup().addGap(8).addGroup(layout
									.createParallelGroup(Alignment.LEADING, false).addComponent(tru).addComponent(fal))
									.addGap(8)));
			layout.setVerticalGroup(
					layout.createSequentialGroup().addGap(2).addComponent(tru).addComponent(fal).addGap(4));
		}

		JComponent pane;
		JRadioButton tru;
		JRadioButton fal;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			if (Boolean.TRUE.equals(value)) {
				tru.setSelected(true);
			} else {
				fal.setSelected(true);
			}
		}

		@Override
		Object getValue() {
			return Boolean.valueOf(tru.isSelected());
		}

	}

	private static class StringChooser extends ValueChooser {

		@SuppressWarnings("unused")
		StringChooser() {
			pane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
			text = new JTextField(40);
			pane.add(text);
		}

		JComponent pane;
		JTextField text;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			text.setText(value.toString());
		}

		@Override
		Object getValue() {
			return text.getText();
		}

	}

	private static class ColorChooser extends ValueChooser {

		@SuppressWarnings("unused")
		ColorChooser() {
			chooser = new JColorChooser();
		}

		JColorChooser chooser;

		@Override
		JComponent getComponent() {
			return chooser;
		}

		@Override
		void setValue(Object value) {
			chooser.setColor((Color) value);
		}

		@Override
		Object getValue() {
			return chooser.getColor();
		}

	}

	private static class IntegerChooser extends ValueChooser {
		@SuppressWarnings("unused")
		IntegerChooser() {
			chooser = new NumberChooser(null, -10, 100);
			pane = NumberChooser.createComponent(null, -1, -1, -1, -1, chooser);
		}

		JComponent pane;
		NumberChooser chooser;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			chooser.setValue((Integer) value);
		}

		@Override
		Object getValue() {
			return chooser.getValue();
		}

	}

	private static class DimensionChooser extends ValueChooser implements ChangeListener {
		@SuppressWarnings("unused")
		DimensionChooser() {
			width = new NumberChooser("Width:", 0, 2000);
			height = new NumberChooser("Height:", 0, 2000);
			renderer = new UIDefaultsRenderer();
			renderer.type = Type.Dimension;
			width.addChangeListener(this);
			height.addChangeListener(this);
			pane = NumberChooser.createComponent(renderer, 200, Short.MAX_VALUE, 200, 200, width, height);
		}

		JComponent pane;
		NumberChooser width;
		NumberChooser height;
		UIDefaultsRenderer renderer;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			Dimension d = (Dimension) value;
			renderer.value = null;
			width.setValue(d.width);
			height.setValue(d.height);
			renderer.value = (Dimension) d.clone();
		}

		@Override
		Object getValue() {
			return renderer.value;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (renderer.value != null) {
				Dimension d = (Dimension) renderer.value;
				d.width = width.getValue();
				d.height = height.getValue();
				renderer.repaint();
			}
		}

	}

	private static class InsetsChooser extends ValueChooser implements ChangeListener {

		@SuppressWarnings("unused")
		InsetsChooser() {
			top = new NumberChooser("Top:", 0, 20);
			left = new NumberChooser("Left:", 0, 20);
			bottom = new NumberChooser("Bottom:", 0, 20);
			right = new NumberChooser("Right:", 0, 20);
			renderer = new UIDefaultsRenderer();
			renderer.type = Type.Insets;
			top.addChangeListener(this);
			left.addChangeListener(this);
			bottom.addChangeListener(this);
			right.addChangeListener(this);

			pane = NumberChooser.createComponent(renderer, 120, 120, 50, 50, top, left, bottom, right);
		}

		JComponent pane;
		NumberChooser top;
		NumberChooser left;
		NumberChooser bottom;
		NumberChooser right;
		UIDefaultsRenderer renderer;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			Insets i = (Insets) value;
			renderer.value = null;
			top.setValue(i.top);
			left.setValue(i.left);
			bottom.setValue(i.bottom);
			right.setValue(i.right);
			renderer.value = (Insets) i.clone();
			renderer.repaint();
		}

		@Override
		Object getValue() {
			return renderer.value;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (renderer.value != null) {
				Insets in = (Insets) renderer.value;
				in.top = top.getValue();
				in.left = left.getValue();
				in.bottom = bottom.getValue();
				in.right = right.getValue();
				renderer.repaint();
			}
		}

	}

	private static class NumberChooser implements ChangeListener {
		NumberChooser(String nam, int min, int max) {
			name = nam;
			spin = new SpinnerNumberModel(min, min, max, 1);
			spin.addChangeListener(this);
			slide = new JSlider(min, max);
			slide.setMinorTickSpacing((max - min) / 10);
			slide.setMajorTickSpacing((max - min) / 2);
			slide.setPaintTicks(true);
			slide.addChangeListener(this);
		}

		String name;
		SpinnerNumberModel spin;
		JSlider slide;

		@Override
		public void stateChanged(ChangeEvent e) {
			if (spin.getNumber().intValue() != slide.getValue()) {
				if (e.getSource() == slide) {
					spin.setValue(slide.getValue());
				} else {
					slide.setValue(spin.getNumber().intValue());
				}
			}
		}

		int getValue() {
			return slide.getValue();
		}

		void setValue(int value) {
			slide.setValue(value);
		}

		void addChangeListener(ChangeListener l) {
			slide.addChangeListener(l);
		}

		static JComponent createComponent(JComponent preview, int prefW, int maxW, int prefH, int maxH,
				NumberChooser... choosers) {
			JComponent pane = new JPanel(null);
			GroupLayout layout = new GroupLayout(pane);
			pane.setLayout(layout);
			GroupLayout.ParallelGroup labelX = null;
			GroupLayout.ParallelGroup spinX = layout.createParallelGroup(Alignment.LEADING, false);
			GroupLayout.ParallelGroup slideX = layout.createParallelGroup(Alignment.LEADING, false);
			GroupLayout.SequentialGroup y = layout.createSequentialGroup().addGap(2);
			for (NumberChooser chooser : choosers) {
				JLabel label = chooser.name == null ? null : new JLabel(chooser.name);
				JSpinner spin = new JSpinner(chooser.spin);
				GroupLayout.ParallelGroup baseline = layout.createBaselineGroup(false, true);
				y.addGroup(baseline);
				if (label != null) {
					if (labelX == null)
						labelX = layout.createParallelGroup(Alignment.TRAILING, false);
					labelX.addComponent(label);
					baseline.addComponent(label);
				}
				spinX.addComponent(spin);
				slideX.addComponent(chooser.slide);
				baseline.addComponent(spin).addComponent(chooser.slide);
			}
			GroupLayout.Group x = layout.createSequentialGroup().addGap(8);
			if (labelX != null)
				x.addGroup(labelX).addGap(2);
			x.addGroup(spinX).addGap(2).addGroup(slideX).addGap(8);
			y.addGap(4);
			if (preview != null) {
				y.addComponent(preview, prefH, prefH, maxH);
				y.addGap(4);
				x = layout.createParallelGroup(Alignment.CENTER, false).addGroup(x).addComponent(preview, prefW, prefW,
						maxW);
			}
			layout.setHorizontalGroup(x);
			layout.setVerticalGroup(y);
			return pane;
		}
	}

	private static class FontChooser extends ValueChooser implements ChangeListener {
		@SuppressWarnings("unused")
		FontChooser() {
			family = new SpinnerListModel(
					new Object[] { Font.DIALOG, Font.DIALOG_INPUT, Font.MONOSPACED, Font.SANS_SERIF, Font.SERIF });
			family.addChangeListener(this);
			JSpinner familySpin = new JSpinner(family);
			size = new SpinnerNumberModel(32f, 8f, 32f, 2f);
			size.addChangeListener(this);
			JSpinner sizeSpin = new JSpinner(size);
			bold = new JToggleButton("b");
			bold.addChangeListener(this);
			bold.setFont(bold.getFont().deriveFont(Font.BOLD));
			italic = new JToggleButton("i");
			italic.addChangeListener(this);
			italic.setFont(italic.getFont().deriveFont(Font.ITALIC));
			renderer = new UIDefaultsRenderer();
			renderer.type = Type.Font;
			pane = new JPanel(null);
			GroupLayout layout = new GroupLayout(pane);
			pane.setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
					.addGroup(layout.createSequentialGroup().addGap(8).addComponent(familySpin, 150, 150, 150).addGap(2)
							.addComponent(sizeSpin).addGap(2).addComponent(bold).addGap(2).addComponent(italic)
							.addGap(12))
					.addComponent(renderer));
			layout.setVerticalGroup(layout
					.createSequentialGroup().addGap(2).addGroup(layout.createBaselineGroup(false, true)
							.addComponent(familySpin).addComponent(sizeSpin).addComponent(bold).addComponent(italic))
					.addComponent(renderer, 50, 50, 50));
		}

		JComponent pane;
		SpinnerListModel family;
		SpinnerNumberModel size;
		JToggleButton bold;
		JToggleButton italic;
		UIDefaultsRenderer renderer;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			Font font = (Font) value;
			renderer.value = null;
			family.setValue(font.getFamily());
			size.setValue(font.getSize2D());
			bold.setSelected(font.isBold());
			italic.setSelected(font.isItalic());
			renderer.value = value;
		}

		@Override
		Object getValue() {
			return renderer.value;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			Font font = (Font) renderer.value;
			if (font != null) {
				if (e.getSource() == size) {
					renderer.value = font.deriveFont(size.getNumber().floatValue());
				} else if (e.getSource() == bold) {
					renderer.value = font
							.deriveFont(bold.isSelected() ? font.getStyle() | Font.BOLD : font.getStyle() & ~Font.BOLD);
				} else if (e.getSource() == italic) {
					renderer.value = font.deriveFont(
							italic.isSelected() ? font.getStyle() | Font.ITALIC : font.getStyle() & ~Font.ITALIC);
				} else if (e.getSource() == family) {
					font = Font.decode(family.getValue().toString() + ' ' + size.getNumber().intValue());
					int style = 0;
					if (bold.isSelected())
						style |= Font.BOLD;
					if (italic.isSelected())
						style |= Font.ITALIC;
					if (style != 0)
						font = font.deriveFont(style);
					renderer.value = font;
				}
				renderer.repaint();
			}
		}

	}

	// TODO
	private static class PainterChooser extends ValueChooser
			implements ActionListener, ListSelectionListener, ChangeListener {
		@SuppressWarnings("unused")
		PainterChooser() {
			tablePane = new JScrollPane();
			renderer = new UIDefaultsRenderer();
			renderer.type = Type.Painter;
			editor = new JTextArea(20, 80);
			editor.setText("Not Implemented");
			editor.setEnabled(false);
			editor.setFont(Font.decode(Font.MONOSPACED + ' ' + 12));
			JScrollPane scroller = new JScrollPane(editor);
			JButton update = new JButton("Update");
			update.addActionListener(this);
			update.setEnabled(false);
			JButton toDialog = new JButton("Switch to Dialog");
			toDialog.addActionListener(this);
			JPanel custom = new JPanel(null);
			GroupLayout layout = new GroupLayout(custom);
			custom.setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup().addComponent(scroller).addGroup(layout.createSequentialGroup()
							.addComponent(update).addGap(10, 100, Short.MAX_VALUE).addComponent(toDialog)));
			layout.setVerticalGroup(layout.createSequentialGroup().addComponent(scroller)
					.addGroup(layout.createBaselineGroup(false, true).addComponent(update).addComponent(toDialog)));

			tabs = new JTabbedPane();
			pane = new JPanel(null);
			layout = new GroupLayout(pane);
			pane.setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup().addComponent(tabs).addComponent(renderer,
					Alignment.CENTER, 100, 100, Short.MAX_VALUE));
			layout.setVerticalGroup(
					layout.createSequentialGroup().addComponent(tabs).addComponent(renderer, 25, 25, 25));
			tabs.add("Custom", custom);
			tabs.add("Nimbus Painters", tablePane);
			tabs.addChangeListener(this);
		}

		JComponent pane;
		JTabbedPane tabs;
		JScrollPane tablePane;
		JTable table;
		JTextArea editor;
		UIDefaultsRenderer renderer;
		Object value;
		JDialog dialog;

		@Override
		JComponent getComponent() {
			return pane;
		}

		@Override
		void setValue(Object value) {
			this.value = null;
			if (table != null)
				table.changeSelection(-1, -1, false, false);
			this.value = value;
			renderer.value = value;
		}

		@Override
		Object getValue() {
			return value;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (value == null || e.getValueIsAdjusting())
				return;
			int row = table.getSelectedRow();
			renderer.value = row < 0 ? value : UIManager.getLookAndFeelDefaults().get(table.getValueAt(row, 0));
			renderer.repaint();
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (tabs.getSelectedComponent() == tablePane && table == null) {
				DefaultTableColumnModel columns = new DefaultTableColumnModel();
				TableColumn column = new TableColumn(0, 400);
				column.setHeaderValue("Key");
				columns.addColumn(column);
				column = new TableColumn(2, 50, new UIDefaultsRenderer(), null);
				columns.addColumn(column);
				column.setHeaderValue(Type.Painter.name());
				table = new Table(new UITypeTableModel(painterKeys, Type.Painter, false), columns);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setRowHeight(25);
				table.setPreferredScrollableViewportSize(new Dimension(500, table.getRowHeight() * 10));
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.getSelectionModel().addListSelectionListener(this);
				tablePane.getViewport().setView(table);
				tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "Update") {

			} else if (e.getActionCommand() == "Switch to Dialog") {
				if (dialog == null) {
					dialog = new JDialog((JFrame) null, true);
				}
				dialog.add(pane, BorderLayout.CENTER);
				dialog.pack();
				dialog.setVisible(true);
			}
		}

	}

}
