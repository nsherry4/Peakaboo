package net.sciencestudio.autodialog.view.swing;


import java.util.Arrays;

import net.sciencestudio.autodialog.model.Group;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.SelectionParameter;
import net.sciencestudio.autodialog.model.style.editors.CheckBoxStyle;
import net.sciencestudio.autodialog.model.style.editors.FileNameStyle;
import net.sciencestudio.autodialog.model.style.editors.IntegerSliderStyle;
import net.sciencestudio.autodialog.model.style.editors.IntegerSpinnerStyle;
import net.sciencestudio.autodialog.model.style.editors.ListStyle;
import net.sciencestudio.autodialog.model.style.editors.RealSliderStyle;
import net.sciencestudio.autodialog.model.style.editors.SeparatorStyle;
import net.sciencestudio.autodialog.model.style.editors.TextAreaStyle;
import net.sciencestudio.autodialog.model.style.layouts.FramedLayoutStyle;
import net.sciencestudio.autodialog.model.style.layouts.TabbedLayoutStyle;
import net.sciencestudio.autodialog.view.editors.AutoDialogButtons;
import net.sciencestudio.autodialog.view.editors.Editor.LabelStyle;
import swidget.Swidget;

public class Test {

	public static void main(String[] args)
	{
		
		Swidget.initialize(() -> {
			gui();
		});
	}
			
	public static void gui() {
		
		Group top = new Group("Demo", new TabbedLayoutStyle());
		Group g1 = new Group("First Set", new FramedLayoutStyle());
		Group g2 = new Group("Second Set");
		Group s1 = new Group("First Subset");
		Group s2 = new Group("Second Subset");
		g1.getValue().add(s1);
		g1.getValue().add(s2);
		top.getValue().add(g1);
		top.getValue().add(g2);
		
		
		s1.getValue().add(new Parameter<>("Boolean", new CheckBoxStyle(), Boolean.TRUE));
		s1.getValue().add(new Parameter<>("Boolean #2", new CheckBoxStyle(), Boolean.TRUE));
		s1.getValue().add(new Parameter<>("Integer", new IntegerSliderStyle(), 0, p -> p.getValue() < 10));
		
		s2.getValue().add(new Parameter<>("Integer #2", new IntegerSpinnerStyle(), 0));
		s2.getValue().add(new Parameter<>("Real", new RealSliderStyle(), 0f));
		
		g2.getValue().add(new Parameter<>("Dummy Separator", new SeparatorStyle(), 0));
		SelectionParameter<LabelStyle> sel = new SelectionParameter<>("List", new ListStyle<>(), LabelStyle.LABEL_HIDDEN);
		sel.setPossibleValues(Arrays.asList(LabelStyle.values()));
		g2.getValue().add(sel);
		
		g2.getValue().add(new Parameter<>("Filename", new FileNameStyle(), null));
		
		g2.getValue().add(new Parameter<>("Slider", new IntegerSliderStyle(), 1));
		g2.getValue().add(new Parameter<String>("TextArea", new TextAreaStyle(), ""));
		
		
		
		SwingAutoDialog d;
		d = new SwingAutoDialog(top, AutoDialogButtons.OK_CANCEL);
		d.setModal(true);
		d.initialize();
		
		top.visit(p -> System.out.println(p));

	}
	
	
	
	
}
