package cyclops.visualization.drawing;


import cyclops.visualization.Surface;

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
	
	public Drawing()
	{
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
	public void setDrawingRequest(DrawingRequest dr)
	{
		this.dr = dr;
	}

	public abstract void draw();
	
	
	
	
	
	
	
	public static float getPenWidth(float baseSize, DrawingRequest dr)
	{
		//float width;
		//width = baseSize;
		//return width;
		return 1f;
	}


	public static float getTickSize(float baseSize, DrawingRequest dr)
	{
		return baseSize * 5;
	}


	public static float getTickFontHeight(Surface context, DrawingRequest dr)
	{
		return context.getFontHeight();
	}


	//TODO: Remove this in favour of painters
	public static float getTitleFontHeight(Surface context, DrawingRequest dr)
	{
		float height;
		context.save();
		context.setFontSize(FONTSIZE_TITLE);
		height = context.getFontHeight();
		context.restore();
		return height;
	}

}
