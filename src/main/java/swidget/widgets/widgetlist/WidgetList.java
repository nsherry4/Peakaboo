package swidget.widgets.widgetlist;


import javax.swing.JComboBox;
import javax.swing.JFrame;

import swidget.Swidget;
import swidget.widgets.Spacing;

public class WidgetList {

	public static void main(String[] args) {
		
		
		Swidget.initialize(() -> {
			
			WidgetListPanel<LabeledComponent<JComboBox<?>>> texts = new WidgetListPanel<>(new SimpleComponentListController<LabeledComponent<JComboBox<?>>>("ComboBox") {
				
				@Override
				public LabeledComponent<JComboBox<?>> generateComponent() {
					return new LabeledComponent<JComboBox<?>>(new JComboBox<>(new String[]{"a", "b", "c"})){

						@Override
						public String getStringDescription(JComboBox<?> component) {
							return component.getSelectedItem().toString();
						}};
				}
			});
			

			
			texts.setBorder(Spacing.bMedium());
			
			JFrame frame = new JFrame();
			frame.add(texts);
			frame.pack();
			frame.setVisible(true);
			
		}, "WidgetList Test");
		

	}
	
}
