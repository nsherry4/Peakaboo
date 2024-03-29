package org.peakaboo.framework.cyclops.visualization.palette;



import java.util.ArrayList;
import java.util.List;




/**
 * This class provides methods for generating Color spectrums for displaying data.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Spectrums
{

	public static int	DEFAULT_STEPS	= 1000;


	
	/* spectum is encoded {{r, g, b, p}, ...}
	 * where p is the percent distance from the last colour stop expressed in the range 0..255
	 */
	
	final static int[] ratioThermal = {
			0xff1485CC, 0,
			0xff000000, 127,
			0xffff0000, 128
	};
	
	final static int[] ratioMonochrome = { 
			0xff000000, 0,
			0xffffffff, 255
	};
	
	
	
	
	
	
	
	public static List<PaletteColour> getScale(Palette p)
	{
		return generateSpectrum(DEFAULT_STEPS, p.getPaletteData(), 1.0f, 1.0f);
	}
	
	public static List<PaletteColour> getScale(Palette p, int steps)
	{
		return generateSpectrum(steps, p.getPaletteData(), 1.0f, 1.0f);
	}
		
	public static List<PaletteColour> getScale(Palette p, float brightness, float centreIntensity)
	{
		return generateSpectrum(DEFAULT_STEPS, p.getPaletteData(), brightness, centreIntensity);
	}
	
	public static List<PaletteColour> getScale(Palette p, int steps, float brightness, float centreIntensity)
	{
		return generateSpectrum(steps, p.getPaletteData(), brightness, centreIntensity);
	}
	

	
	
	
	

	public static List<PaletteColour> ThermalScale()
	{
		return ThermalScale(DEFAULT_STEPS);
	}
	
	public static List<PaletteColour> RatioThermalScale()
	{
		return RatioThermalScale(DEFAULT_STEPS);
	}
	
	public static List<PaletteColour> MonochromeScale()
	{
		return MonochromeScale(DEFAULT_STEPS);
	}

	public static List<PaletteColour> MonochromeScale(PaletteColour c)
	{
		return MonochromeScale(DEFAULT_STEPS, c);
	}

	public static List<PaletteColour> RatioMonochromeScale()
	{
		return RatioMonochromeScale(DEFAULT_STEPS);
	}
	
	
	
	
	
	/**
	 * Creates a thermal scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrumBright
	 * @return a thermal scale spectrum
	 */
	public static List<PaletteColour> ThermalScale(int steps)
	{
		return ThermalScale(steps, 1.0f, 1.0f);
	}


	/**
	 * Creates a thermal ratio scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<PaletteColour> RatioThermalScale(int steps)
	{
		return RatioThermalScale(steps, 1.1f, 1.1f);
	}


	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<PaletteColour> MonochromeScale(int steps)
	{
		return MonochromeScale(steps, 1.0f, 1.0f);
	}
	
	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @param c
	 *            the colour of the monochrome scale
	 */
	public static List<PaletteColour> MonochromeScale(int steps, PaletteColour c)
	{
		return MonochromeScale(steps, 1.0f, 1.0f, c);
	}


	/**
	 * Creates a monochrome ratio scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<PaletteColour> RatioMonochromeScale(int steps)
	{
		return RatioMonochromeScale(steps, 1.0f, 1.0f);
	}

	
	


	
	
	
	
	/**
	 * Creates a thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<PaletteColour> ThermalScale(int _steps, float brightness, float centreIntensity)
	{
		return generateSpectrum(_steps, Palette.THERMAL.getPaletteData(), brightness, centreIntensity);
	}

	

	/**
	 * Creates a ratio(red/blue) thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<PaletteColour> RatioThermalScale(int _steps, float brightness, float centreIntensity)
	{
		return generateSpectrum(_steps, ratioThermal, brightness, centreIntensity);
	}


	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a monochrome scale spectrum
	 */
	public static List<PaletteColour> MonochromeScale(int _steps, float brightness, float centreIntensity)
	{
		return generateSpectrum(_steps, Palette.MONOCHROME.getPaletteData(), brightness, centreIntensity);
	}
	
	
	/**
	 * Creates a monochrome scale spectrum ranging from black to the given colour 
	 * 
	 * @param c
	 *            the colour of the monochrome scale
	 * @return a monochrome scale spectrum
	 */
	public static List<PaletteColour> MonochromeScale(int _steps, float brightness, float centreIntensity, PaletteColour c)
	{

		int[] monochrome = {
				0xff000000,	0,
				c.getARGB(), 255
		};

		return generateSpectrum(_steps, monochrome, brightness, centreIntensity);

	}


	/**
	 * Creates a ratio(red/blue) thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<PaletteColour> RatioMonochromeScale(int _steps, float brightness, float centreIntensity)
	{
		return generateSpectrum(_steps, ratioMonochrome, brightness, centreIntensity);
	}


	
	
	


	public static List<PaletteColour> generateSpectrum(float totalSteps, int[] values, float brightness, float centreIntensity)
	{

		int stage_count = values.length / 2 - 1;
		
		//output list
		List<PaletteColour> spectrum = new ArrayList<>();
		
		
		/* 
		 * Number of steps between each colour stop
		 * stepcount[0] is the number of steps between
		 * values[0] and values[1]
		 */
		int stepcount[] = new int[stage_count];
		
		
		/* 
		 * Fill the stepcount array with the number of steps 
		 * between each colour stop. To eliminate rounding 
		 * error, we count the number of steps taken so far, 
		 * and adjust the final segment in order to make the 
		 * real total equal the desired number of steps
		 */
		int realSteps = 0;
		for (int i = 0; i < stage_count - 1; i++)
		{
			stepcount[i] = (int)(double)Math.round(values[(i+1)*2+1] / 255f * totalSteps);
			realSteps += stepcount[i]; 
		}
		stepcount[stage_count - 1] += (totalSteps - realSteps);


		
		/*
		 * For each entry in the stepcount array, we create the 
		 * required number of intermediate colours 
		 */
		int steps;
		double percent;
		for (int stage = 0; stage < stepcount.length; stage++)
		{

			PaletteColour previous, next;
			previous = new PaletteColour(values[stage*2]);
			next = new PaletteColour(values[stage*2+2]);
			
			steps = stepcount[stage];

			//create 'steps' intermediate colours
			for (int step = 0; step < steps; step++)
			{
				//how far along from the start colour to the end colour are we?
				percent = (double) step / (double) steps;
				PaletteColour blend = previous.blend(next, percent);
				spectrum.add(blend); 
			}

		}
		
		
		/*
		 * Now we get into the more tricky part of the algorithm. Now we have our
		 * complete colour spectrum generated, and we're going to adjust the colours
		 * in it based on two parameters.
		 * * brightness: Overall brightness. 1.0 is normal 
		 * * centre intensity: Parabolic brightness adjustment with 0 impact on edges
		 */

		
		
		/*
		 * We convert a Color object into an HSV triplet, and make adjustments
		 * to the value parameter before converting it back into an RGB-based
		 * Color object
		 */
		float hsv[] = new float[3];
		PaletteColour rgbColor;
		float adjust, x;
		
		//for each Color object
		for (int i = 0; i < spectrum.size(); i++) {
			
			//get the hsv values of this Color object
			rgbColor = spectrum.get(i);
			RGBtoHSB(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), hsv);
						
			/*
			 * This is a parabolic curve to brighten the inner part of a spectrum while leaving the outside dark.
			 * x represents how far along the spectrum this point is as a value between -1..1
			 * The resulting porabola will have roots at 0 and spectrum.size() - 1, and will reach
			 * a maximum of (centreIntensity-1f) at (spectrum.size() - 1)/2
			 */
			x = i / (float)spectrum.size() * 2f - 1f;
			adjust = (float)(    1d + (  -(x*x) + 1d  ) * (centreIntensity-1f)    );
	
	
			//apply the adjustments and bound the values to prevent overflow upon conversion to rgb resulting in
			//incorrect hues/sats
			hsv[2] *= (brightness * adjust);
			hsv[2] = Math.min(hsv[2], 1f);
			hsv[2] = Math.max(hsv[2], 0f);
			
			//replace with updated value
			spectrum.set(i, new PaletteColour(  HSBtoRGB(hsv[0], hsv[1], hsv[2]) | (0xFF<<24) ));
		}
		

		return spectrum;
	
	}
	
	
	//Copied from java.awt.Color
    /**
     * Converts the components of a color, as specified by the default RGB
     * model, to an equivalent set of values for hue, saturation, and
     * brightness that are the three components of the HSB model.
     * <p>
     * If the <code>hsbvals</code> argument is <code>null</code>, then a
     * new array is allocated to return the result. Otherwise, the method
     * returns the array <code>hsbvals</code>, with the values put into
     * that array.
     * @param     r   the red component of the color
     * @param     g   the green component of the color
     * @param     b   the blue component of the color
     * @param     hsbvals  the array used to return the
     *                     three HSB values, or <code>null</code>
     * @return    an array of three elements containing the hue, saturation,
     *                     and brightness (in that order), of the color with
     *                     the indicated red, green, and blue components.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @see       java.awt.image.ColorModel#getRGBdefault()
     * @since     JDK1.0
     */
    private static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }
    
    //Copied from java.awt.Color
    /**
     * Converts the components of a color, as specified by the HSB
     * model, to an equivalent set of values for the default RGB model.
     * <p>
     * The <code>saturation</code> and <code>brightness</code> components
     * should be floating-point values between zero and one
     * (numbers in the range 0.0-1.0).  The <code>hue</code> component
     * can be any floating-point number.  The floor of this number is
     * subtracted from it to create a fraction between 0 and 1.  This
     * fractional number is then multiplied by 360 to produce the hue
     * angle in the HSB color model.
     * <p>
     * The integer that is returned by <code>HSBtoRGB</code> encodes the
     * value of a color in bits 0-23 of an integer value that is the same
     * format used by the method {@link #getRGB() getRGB}.
     * This integer can be supplied as an argument to the
     * <code>Color</code> constructor that takes a single integer argument.
     * @param     hue   the hue component of the color
     * @param     saturation   the saturation of the color
     * @param     brightness   the brightness of the color
     * @return    the RGB value of the color with the indicated hue,
     *                            saturation, and brightness.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @see       java.awt.image.ColorModel#getRGBdefault()
     * @since     JDK1.0
     */
    private static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

	
}