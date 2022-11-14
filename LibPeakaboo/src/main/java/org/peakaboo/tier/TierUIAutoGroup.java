package org.peakaboo.tier;

import org.peakaboo.framework.autodialog.model.Group;

public class TierUIAutoGroup<C> {

	private Group group;
	private String iconPath;
	
	public TierUIAutoGroup(Group group, String iconPath) {
		this.group = group;
		this.iconPath = iconPath;
	}
	
	public Group getValue() {
		return group;
	}
	
	public String getIconPath() {
		return iconPath;
	}

}
