package org.peakaboo.framework.swidget.widgets.layerpanel.widgets;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.layerpanel.HeaderLayer;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.options.OptionChooserPanel;
import org.peakaboo.framework.swidget.widgets.options.OptionRadioButton;

public class OptionChooserLayer<T> extends HeaderLayer {
	
	public OptionChooserLayer(LayerPanel owner, String title, List<T> items, Consumer<T> onAccept, Function<T, OptionRadioButton> widget) {
		super(owner, false);
		Consumer<T> realOnAccept = t -> {
			remove();
			onAccept.accept(t);
		};
		
		OptionChooserPanel<T> reflist = new OptionChooserPanel<>(items, widget);
		
		FluentButton accept = new FluentButton("OK").withStateDefault().withAction(() -> realOnAccept.accept(reflist.getSelected()));
		FluentButton reject = new FluentButton("Cancel").withAction(this::remove);
		
		getHeader().setComponents(reject, title, accept);
		setBody(reflist);
		
	}
	
}


