package org.peakaboo.framework.stratus.laf.painters;

import org.peakaboo.framework.stratus.laf.theme.Theme;

public abstract class SimpleThemed implements Themed{

	private Theme theme;
	
	public SimpleThemed(Theme theme) {
		this.theme = theme;
	}
	
	@Override
	public Theme getTheme() {
		return theme;
	}

	
	
	
	


}
