package org.peakaboo.dataset.source.plugin;

import org.peakaboo.dataset.source.model.components.interaction.Interaction;
import org.peakaboo.dataset.source.model.components.interaction.SimpleInteraction;

public abstract class AbstractDataSource implements DataSourcePlugin {

	private Interaction interaction = new SimpleInteraction();

	
	@Override
	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}
	
	public Interaction getInteraction() {
		return interaction;
	}
	
	
	
	
	@Override
	public String pluginName() {
		return getFileFormat().getFormatName();
	}

	@Override
	public String pluginDescription() {
		return getFileFormat().getFormatDescription();
	}




}
