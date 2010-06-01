package peakaboo.drawing.backends;



import java.awt.Color;



/**
 * A Surface is something which can be drawn to. This includes things such as raster images, SVG documents, sections of
 * a computer screen buffer, etc
 * 
 * @author Nathaniel Sherry, 2009
 */

public interface Surface
{

	/**
	 * Appends a line -- from the current pen position to the specified x,y coordinate -- to the buffer
	 * 
	 * @param x
	 *            the x position to draw to
	 * @param y
	 *            the y position to draw to
	 */
	public void lineTo(double x, double y);


	/**
	 * Moves the pen to the specified x,y coordinate without drawing a line
	 * 
	 * @param x
	 *            the x position to move to
	 * @param y
	 *            the y position to move to
	 */
	public void moveTo(double x, double y);


	/**
	 * Outlines the shape in the buffer, clears the buffer
	 */
	public void stroke();


	/**
	 * Outlines the shape in the buffer, does not clear the buffer
	 */
	public void strokePreserve();


	/**
	 * Fills the shape in the buffer, clears the buffer
	 */
	public void fill();


	/**
	 * Fills the shape in the buffer, does not clear the buffer
	 */
	public void fillPreserve();


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
	public void writeText(String text, double x, double y);


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
	public void setSource(int red, int green, int blue);


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
	public void setSource(int red, int green, int blue, int alpha);


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
	public void setSource(double red, double green, double blue);


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
	public void setSource(double red, double green, double blue, double alpha);


	/**
	 * Sets the drawing colour
	 * 
	 * @param c
	 *            the Colour object to retrieve the colour from
	 */
	public void setSource(Color c);


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
	public void setSourceGradient(double x1, double y1, Color colour1, double x2, double y2, Color colour2);


	/**
	 * Sets the width of a stroke.
	 * 
	 * @param width
	 *            the width to set the stroke to
	 */
	public void setLineWidth(double width);


	/**
	 * Calculates the width of the given text with the current font settings
	 * 
	 * @param text
	 *            the text to calculate the width for
	 * @return the width of the text
	 */
	public double getTextWidth(String text);


	/**
	 * Calculates the height of a line of text for the current font settings. This is the same as getFontLeading() +
	 * getFontAscent() + getFontDescent()
	 * 
	 * @return the height of a line of text for the current font settings.
	 */
	public double getFontHeight();


	/**
	 * This is the spacing between two rows of text for the current font settings
	 * 
	 * @return the font leading
	 */
	public double getFontLeading();


	/**
	 * This is the distance from the baseline to the top of a line of text for the current font settings
	 * 
	 * @return the font ascent
	 */
	public double getFontAscent();


	/**
	 * Descenders are portions of text which go below the baseline, such as lower case g, y, j, etc...
	 * 
	 * @return the fond descent
	 */
	public double getFontDescent();


	/**
	 * Saves the current state of the drawing context, for later restoration. This can be used to make temporary changes
	 * to things like font, source colour, etc, without having to worry about rolling them back manually afterwards
	 */
	public void save();


	/**
	 * Invokes {@link Surface#save}, and returns an index representing the just-saved data's position on the save stack.
	 */
	public int saveWithMarker();


	/**
	 * Accepts an index for the save stack, and restores the drawing context from that location on the save stack.
	 * Behaviour is undefined for indices larger than the stack
	 */
	public void restoreFromMarker(int marker);


	/**
	 * Restores a previous state that was the result of a call to save()
	 * 
	 * @see #save()
	 */
	public void restore();


	/**
	 * Limits the drawable region to the path outlined in the buffer.
	 */
	public void clip();


	/**
	 * Draws a rectangle
	 * 
	 * @param x
	 *            starting x position
	 * @param y
	 *            starting y position
	 * @param width
	 *            width of the rectangle
	 * @param height
	 *            height of the rectangle
	 */
	public void rectangle(double x, double y, double width, double height);


	/**
	 * Shifts the origin point.
	 * 
	 * @param x
	 *            the distance in x to shift the origin point
	 * @param y
	 *            the distance in y to shift the origin point
	 */
	public void translate(double x, double y);


	/**
	 * Scales the coordinate system.
	 * 
	 * @param x
	 *            the proportion in x to scale the coordinate system
	 * @param y
	 *            the proportion in y to scale the coordinate system
	 */
	public void scale(double x, double y);


	/**
	 * Rotates the coordinate system
	 * 
	 * @param radians
	 *            amount in radians to rotate the coordinate system
	 */
	public void rotate(double radians);


	/**
	 * Sets the font size
	 * 
	 * @param size
	 */
	public void setFontSize(double size);


	/**
	 * Retrieves the current font size
	 * 
	 * @return the current font size
	 */
	public double getFontSize();


	/**
	 * Specifies that text should be written in a monospaced font
	 */
	public void useMonoFont();


	/**
	 * Specifies that text should be written in a sans-serif font
	 */
	public void useSansFont();


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
	 * @see #compose(Buffer, int, int, double)
	 */
	public Buffer getImageBuffer(int width, int height);


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
	public void compose(Buffer buffer, int x, int y, double scale);


	public void setAntialias(boolean antialias);


	public Surface getNewContextForSurface();

}
