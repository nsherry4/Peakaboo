package org.peakaboo.framework.stratus.components.ui.options;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;

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


