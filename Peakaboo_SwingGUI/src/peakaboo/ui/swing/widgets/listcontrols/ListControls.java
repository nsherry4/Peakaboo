package peakaboo.ui.swing.widgets.listcontrols;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton.Layout;


public abstract class ListControls extends JPanel
{

	private JButton add, remove, clear, up, down;
	
	public enum ElementCount{NONE, ONE, MANY}
	
	public ListControls(){
				
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add = new ImageButton("add", "Add", Layout.IMAGE);
		add.setMargin(Spacing.iTiny());
		add.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				add();
			}
		});
		
		remove = new ImageButton("remove", "Remove", Layout.IMAGE);
		remove.setMargin(Spacing.iTiny());
		remove.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		
		clear = new ImageButton("clear", "Clear", Layout.IMAGE);
		clear.setMargin(Spacing.iTiny());
		clear.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		
		
		
		up = new ImageButton("go-up", "Up", Layout.IMAGE);
		up.setMargin(Spacing.iTiny());
		down = new ImageButton("go-down", "Down", Layout.IMAGE);
		down.setMargin(Spacing.iTiny());
		
		
		up.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				up();
				
			}
		});
		
		down.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});
		
		
		
		add(  add  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  remove  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  clear  );
		add(  Box.createRigidArea(new Dimension(10,0))  );
		add(  Box.createHorizontalGlue()  );
		add(  up  );
		add(  Box.createRigidArea(new Dimension(5,0))  );
		add(  down  );
		
		setBorder(Spacing.bSmall());

	}
	
	public void setElementCount(ElementCount ec){
		
		if ( ec == ElementCount.NONE ){
			
			add.setEnabled(true);
			remove.setEnabled(false);
			clear.setEnabled(false);
			up.setEnabled(false);
			down.setEnabled(false);
			
		} else if ( ec == ElementCount.ONE ){
		
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			up.setEnabled(false);
			down.setEnabled(false);
			
		} else {
		
			add.setEnabled(true);
			remove.setEnabled(true);
			clear.setEnabled(true);
			up.setEnabled(true);
			down.setEnabled(true);
			
		}
		
	}
	
	protected abstract void add();
	protected abstract void remove();
	protected abstract void clear();
	protected abstract void up();
	protected abstract void down();
	
	
}
