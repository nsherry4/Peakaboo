package stratus.painters;

import stratus.theme.Theme;

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
