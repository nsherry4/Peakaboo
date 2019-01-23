package org.peakaboo.datasink.plugin;

import org.peakaboo.datasink.model.components.interaction.Interaction;
import org.peakaboo.datasink.model.components.interaction.SimpleInteraction;

public abstract class AbstractDataSink implements JavaDataSinkPlugin {

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
	public boolean pluginEnabled() {
		return true;
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
