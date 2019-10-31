package org.peakaboo.framework.cyclops.visualization;

import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

/**
 * A Surface is something which can be drawn to. This includes things such as raster images, SVG documents, sections of
 * a computer screen buffer, etc
 * 
 * @author Nathaniel Sherry, 2009
 */

public interface Surface
{

	public enum CompositeModes{
		OVER,
		OUT,
		IN,
		ATOP,
		SOURCE,
		XOR,
		ADD
	}
	
	public enum LineJoin
	{
		ROUND, BEVEL, MITER
	}
	
	public enum EndCap
	{
		BUTT, ROUND, SQUARE
	}
	
	/**
	 * Appends a line -- from the current pen position to the specified x,y coordinate -- to the buffer
	 * 
	 * @param x
	 *            the x position to draw to
	 * @param y
	 *            the y position to draw to
	 */
	void lineTo(float x, float y);


	/**
	 * Moves the pen to the specified x,y coordinate without drawing a line
	 * 
	 * @param x
	 *            the x position to move to
	 * @param y
	 *            the y position to move to
	 */
	void moveTo(float x, float y);

	
	void rectAt(float x, float y, float width, float height);
	
	void roundRectAt(float x, float y, float width, float height, float xradius, float yradius);
	
	
	/**
	 * Outlines the shape in the buffer, clears the buffer
	 */
	void stroke();


	/**
	 * Outlines the shape in the buffer, does not clear the buffer
	 */
	void strokePreserve();


	/**
	 * Fills the shape in the buffer, clears the buffer
	 */
	void fill();


	/**
	 * Fills the shape in the buffer, does not clear the buffer
	 */
	void fillPreserve();


	/**
	 * Writes text to the surface. This does not append the text shape to the shape buffer, or use the existing pen
	 * location. Text is written immediately.
	 * 
	 * @param text
	 *            the text to write
	 * @param x
	 *            the x position for the baseline
	 * @param y
	 *            the y position for the baseline
	 */
	void writeText(String text, float x, float y);


	/**
	 * Sets the drawing colour. The colour is fully opaque
	 * 
	 * @param red
	 *            the red channel of the colour -- 0-255
	 * @param green
	 *            the green channel of the colour -- 0-255
	 * @param blue
	 *            the blue channel of the colour -- 0-255
	 */
	void setSource(int red, int green, int blue);


	/**
	 * Sets the drawing colour
	 * 
	 * @param red
	 *            the red channel of the colour -- 0-255
	 * @param green
	 *            the green channel of the colour -- 0-255
	 * @param blue
	 *            the blue channel of the colour -- 0-255
	 * @param alpha
	 *            the alpha channel of the colour -- 0-255
	 */
	void setSource(int red, int green, int blue, int alpha);
	
	
	/**
	 * Sets the drawing colour
	 * 
	 * @param argb
	 *            the colour in ARGB int32 format
	 */
	default void setSource(int arbg) {
		setSource(new PaletteColour(arbg));
	}


	/**
	 * Sets the drawing colour. The colour is fully opaque
	 * 
	 * @param red
	 *            the red channel of the colour -- 0.0 to 1.0
	 * @param green
	 *            the green channel of the colour -- 0.0 to 1.0
	 * @param blue
	 *            the blue channel of the colour -- 0.0 to 1.0
	 */
	void setSource(float red, float green, float blue);


	/**
	 * Sets the drawing colour
	 * 
	 * @param red
	 *            the red channel of the colour -- 0.0 to 1.0
	 * @param green
	 *            the green channel of the colour -- 0.0 to 1.0
	 * @param blue
	 *            the blue channel of the colour -- 0.0 to 1.0
	 * @param alpha
	 *            the alpha channel of the colour -- 0.0 to 1.0
	 */
	void setSource(float red, float green, float blue, float alpha);


	/**
	 * Sets the drawing colour
	 * 
	 * @param c
	 *            the Colour object to retrieve the colour from
	 */
	void setSource(PaletteColour c);


	/**
	 * Sets the stroke or fill source to a gradient rather than a solid colour
	 * 
	 * @param x1
	 *            the x position for the first colour
	 * @param y1
	 *            the y position for the first colour
	 * @param colour1
	 *            the first colour
	 * @param x2
	 *            the x position for the second colour
	 * @param y2
	 *            the y position for the second colour
	 * @param colour2
	 *            the second colour
	 */
	void setSourceGradient(float x1, float y1, PaletteColour colour1, float x2, float y2, PaletteColour colour2);


	/**
	 * Sets the width of a stroke.
	 * 
	 * @param width
	 *            the width to set the stroke to
	 */
	void setLineWidth(float width);

	
	void setLineJoin(LineJoin join);
	void setLineEnd(EndCap cap);
	void setLineStyle(float width, EndCap cap, LineJoin join);

	/**
	 * Calculates the width of the given text with the current font settings
	 * 
	 * @param text
	 *            the text to calculate the width for
	 * @return the width of the text
	 */
	float getTextWidth(String text);


	/**
	 * Calculates the height of a line of text for the current font settings. This is the same as getFontLeading() +
	 * getFontAscent() + getFontDescent()
	 * 
	 * @return the height of a line of text for the current font settings.
	 */
	float getFontHeight();


	/**
	 * This is the spacing between two rows of text for the current font settings
	 * 
	 * @return the font leading
	 */
	float getFontLeading();


	/**
	 * This is the distance from the baseline to the top of a line of text for the current font settings
	 * 
	 * @return the font ascent
	 */
	float getFontAscent();


	/**
	 * Descenders are portions of text which go below the baseline, such as lower case g, y, j, etc...
	 * 
	 * @return the fond descent
	 */
	float getFontDescent();


	/**
	 * Saves the current state of the drawing context, for later restoration. This can be used to make temporary changes
	 * to things like font, source colour, etc, without having to worry about rolling them back manually afterwards
	 */
	void save();


	/**
	 * Invokes {@link Surface#save}, and returns an index representing the just-saved data's position on the save stack.
	 */
	int saveWithMarker();


	/**
	 * Accepts an index for the save stack, and restores the drawing context from that location on the save stack.
	 * Behaviour is undefined for indices larger than the stack
	 */
	void restoreFromMarker(int marker);


	/**
	 * Restores a previous state that was the result of a call to save()
	 * 
	 * @see #save()
	 */
	void restore();


	/**
	 * Limits the drawable region to the path outlined in the buffer.
	 */
	void clip();


	/**
	 * Shifts the origin point.
	 * 
	 * @param x
	 *            the distance in x to shift the origin point
	 * @param y
	 *            the distance in y to shift the origin point
	 */
	void translate(float x, float y);


	/**
	 * Scales the coordinate system.
	 * 
	 * @param x
	 *            the proportion in x to scale the coordinate system
	 * @param y
	 *            the proportion in y to scale the coordinate system
	 */
	void scale(float x, float y);


	/**
	 * Rotates the coordinate system
	 * 
	 * @param radians
	 *            amount in radians to rotate the coordinate system
	 */
	void rotate(float radians);


	/**
	 * Sets the font size
	 * 
	 * @param size
	 */
	void setFontSize(float size);


	/**
	 * Retrieves the current font size
	 * 
	 * @return the current font size
	 */
	float getFontSize();


	/**
	 * Specifies that text should be written in a monospaced font
	 */
	void useMonoFont();


	/**
	 * Specifies that text should be written in a sans-serif font
	 */
	void useSansFont();

	
	/**
	 * Specifies that a custom font should be used
	 * @param f the font to be used
	 */
	void setFont(String name);
	
	void setFontBold(boolean bold);
	
	/**
	 * Indicates if this surface is backed by a vector image
	 * @return true if the surface is vector based, false if the surface is raster based
	 */
	boolean isVectorSurface();

	/**
	 * Creates a raster Buffer for intermediate drawing. This buffer can later be drawn onto the Surface by calling
	 * compose()
	 * 
	 * @param width
	 *            the width of the buffer in pixels
	 * @param height
	 *            the height of the buffer in pixels
	 * @return a new image buffer
	 * @see Buffer
	 * @see #compose(Buffer, int, int, float)
	 */
	Buffer getImageBuffer(int width, int height);


	/**
	 * Draws a given Buffer to the Surface
	 * 
	 * @param buffer
	 *            the Buffer to draw onto the Surface
	 * @param x
	 *            the x position at which to position the buffer
	 * @param y
	 *            the y position at which to position the buffer
	 * @param scale
	 *            the proportion by which the buffer should be scaled when being drawn
	 * @see Buffer
	 * @see #getImageBuffer(int, int)
	 */
	void compose(Buffer buffer, int x, int y, float scale);


	void setAntialias(boolean antialias);


	void setCompositeMode(CompositeModes mode);
	CompositeModes getCompositeMode();
	
	
	Surface getNewContextForSurface();

	SurfaceType getSurfaceType();
	
}
