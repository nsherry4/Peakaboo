package peakaboo.ui.swing.console;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import stratus.StratusLookAndFeel;
import swidget.Swidget;
import swidget.dialogues.HeaderFrame;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;

public class DebugConsole extends TabbedLayerPanel {

	private JConsole console;
	private Interpreter interpreter;
	private Thread thread;
	
	public DebugConsole(TabbedInterface<TabbedLayerPanel> tabbed) {
		super(tabbed);
		setLayout(new BorderLayout());
		
		this.console = new JConsole();
		this.interpreter = new Interpreter(this.console);
		this.thread = new Thread(interpreter);
		this.thread.start();
		
		for (Package p : this.getClass().getClassLoader().getDefinedPackages()) {
			try {

				if (p.getName().startsWith("peakaboo.")) {
					String importCmd = "import " + p.getName() + ".*;";
					System.out.println(importCmd);
					this.interpreter.eval(importCmd);
				}
			} catch (EvalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		


		this.console.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setBackground(Color.WHITE);
		this.add(this.console, BorderLayout.CENTER);
	}

	@Override
	public String getTabTitle() {
		return "Debug Console";
	}
	
	
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		Swidget.initialize("JTextPane Test");
		UIManager.setLookAndFeel(new StratusLookAndFeel());
		
		HeaderFrame frame = new HeaderFrame();
		frame.getHeader().setCentre("JTextPane Test");
		
		JTextPane text = new JTextPane();
		text.setBorder(new EmptyBorder(0, 0, 0, 0));
		frame.setBody(text);
		
		frame.pack();
		frame.setVisible(true);
	}
}
