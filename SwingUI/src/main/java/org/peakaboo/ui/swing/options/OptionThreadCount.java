package org.peakaboo.ui.swing.options;

import java.awt.FlowLayout;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.framework.plural.Plural;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.options.OptionBlock;
import org.peakaboo.framework.stratus.components.ui.options.OptionCustomComponent;

public class OptionThreadCount extends OptionCustomComponent {


	
	public OptionThreadCount(OptionBlock block, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(block, new ThreadCountComponent(setter, getter), false);
	}
	

	
	private static class ThreadCountComponent extends JComponent {
		
		public JSpinner spinner;
		public FluentButton reset;
		
		private Consumer<Integer> setter;
		private Supplier<Integer> getter;
		
		public ThreadCountComponent(Consumer<Integer> setter, Supplier<Integer> getter) {
			this.setter = setter;
			this.getter = getter;
			
			spinner = new JSpinner(new SpinnerNumberModel((int)getter.get(), 1, (int)Math.max(Plural.cores() * 2, 24), 1));
			spinner.setMaximumSize(spinner.getPreferredSize());
			
			//Listeners for the size textbox
			spinner.addChangeListener(change -> {
				set((int)spinner.getValue());
			});
			
			reset = new FluentButton()
				.withIcon(StockIcon.EDIT_UNDO, IconSize.BUTTON)
				.withBordered(BorderStyle.ACTIVE)
				.withTooltip("Reset to default")
				.withButtonSize(FluentButtonSize.COMPACT)
				.withAction(() -> {
					int defaultCores = Plural.cores();
					setter.accept(defaultCores);
					spinner.getModel().setValue(defaultCores);
				});
			
			this.setLayout(new FlowLayout());
			this.add(reset);
			this.add(spinner);
			
		}
		
		void set(int value) {
			try {
				if (value != getter.get()) {
					setter.accept(value);
				}
			} catch (NumberFormatException e) {
				
			}
		}
		
	}
	
	
}
