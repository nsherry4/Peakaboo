package peakaboo.ui.swing.widgets;

import java.awt.Insets;

import javax.swing.border.EmptyBorder;


public class Spacing
{

	public final static int none 	= 0;
	public final static int tiny 	= 1;
	public final static int small 	= 3;
	public final static int medium 	= 6;
	public final static int large 	= 8;
	public final static int huge 	= 12;
	
	public final static int button 	= 2;
	
	public static EmptyBorder bNone(){
		return new EmptyBorder(none, none, none, none);
	}

	public static EmptyBorder bTiny(){
		return new EmptyBorder(tiny, tiny, tiny, tiny);
	}
	
	public static EmptyBorder bSmall(){
		return new EmptyBorder(small, small, small, small);
	}

	public static EmptyBorder bMedium(){
		return new EmptyBorder(medium, medium, medium, medium);
	}
	
	public static EmptyBorder bLarge(){
		return new EmptyBorder(large, large, large, large);
	}
	
	public static EmptyBorder bHuge(){
		return new EmptyBorder(huge, huge, huge, huge);
	}
	
	
	public static Insets iNone(){
		return new Insets(none, none, none, none);
	}

	public static Insets iTiny(){
		return new Insets(tiny, tiny, tiny, tiny);
	}
	
	public static Insets iButton(){
		return new Insets(button, button, button, button);
	}
	
	public static Insets iSmall(){
		return new Insets(small, small, small, small);
	}

	public static Insets iMedium(){
		return new Insets(medium, medium, medium, medium);
	}
	
	public static Insets iLarge(){
		return new Insets(large, large, large, large);
	}
	
	public static Insets iHuge(){
		return new Insets(huge, huge, huge, huge);
	}
	
	
	public static EmptyBorder menuPadding(){
		return new EmptyBorder(none, small, none, small);	
	}
	
}
