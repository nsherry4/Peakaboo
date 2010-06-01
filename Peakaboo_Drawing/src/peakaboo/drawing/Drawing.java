package peakaboo.drawing;


import peakaboo.drawing.backends.Surface;

/**
 * 
 * This abstract class contains some common methods for drawing which can be used by any drawing logic which
 * inherits from this class.
 * 
 * @author Nathaniel Sherry, 2009
 **/

public abstract class Drawing
{

	public static final int	FONTSIZE_TICK	= 10;
	public static final int	FONTSIZE_TITLE	= 24;

	protected DrawingRequest dr;
	
	protected Surface				context;
	
	
	
	public Drawing(DrawingRequest dr)
	{
		this.dr = dr;
	}
	
	public void setContext(Surface context) {
		this.context = context;
	}

	/**
	 * Gets the {@link DrawingRequest} that defines how this plot should be drawn
	 * @return {@link DrawingRequest}
	 */
	public DrawingRequest getDR()
	{
		return dr;
	}
	public void setDR(DrawingRequest dr)
	{
		this.dr = dr;
	}

	public abstract void draw();
	
	
	
	
	
	
	
	public static double getPenWidth(double baseSize, DrawingRequest dr)
	{
		double width;
		width = baseSize;
		return width;
	}


	public static double getTickSize(double baseSize, DrawingRequest dr)
	{
		return baseSize * 5;
	}


	public static double getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}


	//TODO: Remove this in favour of painters
	public static double getTitleFontHeight(Surface context, DrawingRequest dr)
	{
		double height;
		context.save();
		context.setFontSize(FONTSIZE_TITLE);
		height = context.getFontHeight();
		context.restore();
		return height;
	}

}
