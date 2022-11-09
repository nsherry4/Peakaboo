package org.peakaboo.framework.swidget.widgets.options;

public enum OptionSize {
	SMALL, 
	MEDIUM, 
	LARGE
	
	;

	int getTitleSize() {
		return switch(this) {
			case SMALL -> 10;
			case MEDIUM -> 12;
			case LARGE -> 14;
		};
	}
	
	int getDescriptionSize() {
		return switch(this) {
			case SMALL -> 8;
			case MEDIUM -> 10;
			case LARGE -> 11;
		};
	}
	
	int getPaddingSize() {
		return switch(this) {
			case SMALL -> 6;
			case MEDIUM -> 8;
			case LARGE -> 10;
		};
	}
	
}