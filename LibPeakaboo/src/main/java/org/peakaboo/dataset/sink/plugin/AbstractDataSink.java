package org.peakaboo.dataset.sink.plugin;

import org.peakaboo.dataset.sink.model.components.interaction.Interaction;
import org.peakaboo.dataset.sink.model.components.interaction.SimpleInteraction;

public abstract class AbstractDataSink implements DataSinkPlugin {

	private Interaction interaction = new SimpleInteraction();
	
	@Override
	public Interaction getInteraction() {
		return interaction;
	}

	@Override
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}

	@Override
	public String pluginName() {
		return getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFormatDescription();
	}

}
