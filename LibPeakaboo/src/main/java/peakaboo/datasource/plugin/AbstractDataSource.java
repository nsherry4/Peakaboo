package peakaboo.datasource.plugin;

import peakaboo.datasource.model.components.interaction.Interaction;
import peakaboo.datasource.model.components.interaction.SimpleInteraction;

public abstract class AbstractDataSource implements JavaDataSourcePlugin
{

	private Interaction interaction = new SimpleInteraction();
	
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
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
