package peakaboo.datasource;

import peakaboo.datasource.components.interaction.DataSourceInteraction;
import peakaboo.datasource.components.interaction.SimpleDataSourceInteraction;

public abstract class AbstractDataSource implements PluginDataSource
{

	private DataSourceInteraction interaction = new SimpleDataSourceInteraction();
	
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
	@Override
	public void setInteraction(DataSourceInteraction interaction) {
		this.interaction = interaction;
	}
	
	public DataSourceInteraction getInteraction() {
		return interaction;
	}

}
