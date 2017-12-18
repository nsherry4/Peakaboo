package peakaboo.datasource;

import peakaboo.datasource.components.interaction.Interaction;
import peakaboo.datasource.components.interaction.SimpleInteraction;

public abstract class AbstractDataSource implements JavaPluginDataSource
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
