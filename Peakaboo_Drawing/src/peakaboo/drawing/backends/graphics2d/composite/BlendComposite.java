package peakaboo.drawing.backends.graphics2d.composite;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/*
 * $Id: BlendComposite.java,v 1.9 2007/02/28 01:21:29 gfx Exp $
 *
 * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
 *
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * Copyright (c) 2006 Romain Guy <romain.guy@mac.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/**
 * <p>A blend composite defines the rule according to which a drawing primitive
 * (known as the source) is mixed with existing graphics (know as the
 * destination.)</p>
 * <p><code>BlendComposite</code> is an implementation of the
 * {@link java.awt.Composite} interface and must therefore be set as a state on
 * a {@link java.awt.Graphics2D} surface.</p>
 * <p>Please refer to {@link java.awt.Graphics2D#setComposite(java.awt.Composite)}
 * for more information on how to use this class with a graphics surface.</p>
 * <h2>Blending Modes</h2>
 * <p>This class offers a certain number of blending modes, or compositing
 * rules. These rules are inspired from graphics editing software packages,
 * like <em>Adobe Photoshop</em> or <em>The GIMP</em>.</p>
 * <p>Given the wide variety of implemented blending modes and the difficulty
 * to describe them with words, please refer to those tools to visually see
 * the result of these blending modes.</p>
 * <h2>Opacity</h2>
 * <p>Each blending mode has an associated opacity, defined as a float value
 * between 0.0 and 1.0. Changing the opacity controls the force with which the
 * compositing operation is applied. For instance, a composite with an opacity
 * of 0.0 will not draw the source onto the destination. With an opacity of
 * 1.0, the source will be fully drawn onto the destination, according to the
 * selected blending mode rule.</p>
 * <p>The opacity, or alpha value, is used by the composite instance to mutiply
 * the alpha value of each pixel of the source when being composited over the
 * destination.</p>
 * <h2>Creating a Blend Composite</h2>
 * <p>Blend composites can be created in various manners:</p>
 * <ul>
 *   <li>Use one of the pre-defined instance. Example:
 *     <code>BlendComposite.Average</code>.</li>
 *   <li>Derive one of the pre-defined instances by calling
 *     {@link #derive(float)} or {@link #derive(BlendingMode)}. Deriving allows
 *     you to change either the opacity or the blending mode. Example:
 *     <code>BlendComposite.Average.derive(0.5f)</code>.</li>
 *   <li>Use a factory method: {@link #getInstance(BlendingMode)} or
 *     {@link #getInstance(BlendingMode, float)}.</li>
 * </ul>
 * <h2>Implementation Caveat</h2>
 * <p>TThe blending mode <em>SoftLight</em> has not been implemented yet.</p>
 *
 * @see java.awt.Graphics2D
 * @see java.awt.Composite
 * @see java.awt.AlphaComposite
 * @author Romain Guy <romain.guy@mac.com>
 */
 public class BlendComposite implements Composite {
    /**
     * <p>A blending mode defines the compositing rule of a
     * {@link BlendComposite}.</p>
     *
     * @author Romain Guy <romain.guy@mac.com>
     */
    public enum BlendingMode {
        AVERAGE,
        MULTIPLY,
        SCREEN,
        DARKEN,
        LIGHTEN,
        OVERLAY,
        HARD_LIGHT,
        SOFT_LIGHT,
        DIFFERENCE,
        NEGATION,
        EXCLUSION,
        COLOR_DODGE,
        INVERSE_COLOR_DODGE,
        SOFT_DODGE,
        COLOR_BURN,
        INVERSE_COLOR_BURN,
        SOFT_BURN,
        REFLECT,
        GLOW,
        FREEZE,
        HEAT,
        ADD,
        SUBTRACT,
        STAMP,
        RED,
        GREEN,
        BLUE,
        HUE,
        SATURATION,
        COLOR,
        LUMINOSITY
    }

    public static final BlendComposite Average = new BlendComposite(BlendingMode.AVERAGE);
    public static final BlendComposite Multiply = new BlendComposite(BlendingMode.MULTIPLY);
    public static final BlendComposite Screen = new BlendComposite(BlendingMode.SCREEN);
    public static final BlendComposite Darken = new BlendComposite(BlendingMode.DARKEN);
    public static final BlendComposite Lighten = new BlendComposite(BlendingMode.LIGHTEN);
    public static final BlendComposite Overlay = new BlendComposite(BlendingMode.OVERLAY);
    public static final BlendComposite HardLight = new BlendComposite(BlendingMode.HARD_LIGHT);
    public static final BlendComposite SoftLight = new BlendComposite(BlendingMode.SOFT_LIGHT);
    public static final BlendComposite Difference = new BlendComposite(BlendingMode.DIFFERENCE);
    public static final BlendComposite Negation = new BlendComposite(BlendingMode.NEGATION);
    public static final BlendComposite Exclusion = new BlendComposite(BlendingMode.EXCLUSION);
    public static final BlendComposite ColorDodge = new BlendComposite(BlendingMode.COLOR_DODGE);
    public static final BlendComposite InverseColorDodge = new BlendComposite(BlendingMode.INVERSE_COLOR_DODGE);
    public static final BlendComposite SoftDodge = new BlendComposite(BlendingMode.SOFT_DODGE);
    public static final BlendComposite ColorBurn = new BlendComposite(BlendingMode.COLOR_BURN);
    public static final BlendComposite InverseColorBurn = new BlendComposite(BlendingMode.INVERSE_COLOR_BURN);
    public static final BlendComposite SoftBurn = new BlendComposite(BlendingMode.SOFT_BURN);
    public static final BlendComposite Reflect = new BlendComposite(BlendingMode.REFLECT);
    public static final BlendComposite Glow = new BlendComposite(BlendingMode.GLOW);
    public static final BlendComposite Freeze = new BlendComposite(BlendingMode.FREEZE);
    public static final BlendComposite Heat = new BlendComposite(BlendingMode.HEAT);
    public static final BlendComposite Add = new BlendComposite(BlendingMode.ADD);
    public static final BlendComposite Subtract = new BlendComposite(BlendingMode.SUBTRACT);
    public static final BlendComposite Stamp = new BlendComposite(BlendingMode.STAMP);
    public static final BlendComposite Red = new BlendComposite(BlendingMode.RED);
    public static final BlendComposite Green = new BlendComposite(BlendingMode.GREEN);
    public static final BlendComposite Blue = new BlendComposite(BlendingMode.BLUE);
    public static final BlendComposite Hue = new BlendComposite(BlendingMode.HUE);
    public static final BlendComposite Saturation = new BlendComposite(BlendingMode.SATURATION);
    public static final BlendComposite Color = new BlendComposite(BlendingMode.COLOR);
    public static final BlendComposite Luminosity = new BlendComposite(BlendingMode.LUMINOSITY);

    private final float alpha;
    private final BlendingMode mode;

    private BlendComposite(BlendingMode mode) {
        this(mode, 1.0f);
    }

    private BlendComposite(BlendingMode mode, float alpha) {
        this.mode = mode;

        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException(
                    "alpha must be comprised between 0.0f and 1.0f");
        }
        this.alpha = alpha;
    }

    /**
     * <p>Creates a new composite based on the blending mode passed
     * as a parameter. A default opacity of 1.0 is applied.</p>
     *
     * @param mode the blending mode defining the compositing rule
     * @return a new <code>BlendComposite</code> based on the selected blending
     *   mode, with an opacity of 1.0
     */
    public static BlendComposite getInstance(BlendingMode mode) {
        return new BlendComposite(mode);
    }

    /**
     * <p>Creates a new composite based on the blending mode and opacity passed
     * as parameters. The opacity must be a value between 0.0 and 1.0.</p>
     *
     * @param mode the blending mode defining the compositing rule
     * @param alpha the constant alpha to be multiplied with the alpha of the
     *   source. <code>alpha</code> must be a floating point between 0.0 and 1.0.
     * @throws IllegalArgumentException if the opacity is less than 0.0 or
     *   greater than 1.0
     * @return a new <code>BlendComposite</code> based on the selected blending
     *   mode and opacity
     */
    public static BlendComposite getInstance(BlendingMode mode, float alpha) {
        return new BlendComposite(mode, alpha);
    }

    /**
     * <p>Returns a <code>BlendComposite</code> object that uses the specified
     * blending mode and this object's alpha value. If the newly specified
     * blending mode is the same as this object's, this object is returned.</p>
     *
     * @param mode the blending mode defining the compositing rule
     * @return a <code>BlendComposite</code> object derived from this object,
     *   that uses the specified blending mode
     */
    public BlendComposite derive(BlendingMode mode) {
        return this.mode == mode ? this : new BlendComposite(mode, getAlpha());
    }

    /**
     * <p>Returns a <code>BlendComposite</code> object that uses the specified
     * opacity, or alpha, and this object's blending mode. If the newly specified
     * opacity is the same as this object's, this object is returned.</p>
     *
     * @param alpha the constant alpha to be multiplied with the alpha of the
     *   source. <code>alpha</code> must be a floating point between 0.0 and 1.0.
     * @throws IllegalArgumentException if the opacity is less than 0.0 or
     *   greater than 1.0
     * @return a <code>BlendComposite</code> object derived from this object,
     *   that uses the specified blending mode
     */
    public BlendComposite derive(float alpha) {
        return this.alpha == alpha ? this : new BlendComposite(getMode(), alpha);
    }

    /**
     * <p>Returns the opacity of this composite. If no opacity has been defined,
     * 1.0 is returned.</p>
     *
     * @return the alpha value, or opacity, of this object
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * <p>Returns the blending mode of this composite.</p>
     *
     * @return the blending mode used by this object
     */
    public BlendingMode getMode() {
        return mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(alpha) * 31 + mode.ordinal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlendComposite)) {
            return false;
        }

        BlendComposite bc = (BlendComposite) obj;
        return mode == bc.mode && alpha == bc.alpha;
    }

    private static boolean checkComponentsOrder(ColorModel cm) {
        if (cm instanceof DirectColorModel &&
                cm.getTransferType() == DataBuffer.TYPE_INT) {
            DirectColorModel directCM = (DirectColorModel) cm;
            
            return directCM.getRedMask() == 0x00FF0000 &&
                   directCM.getGreenMask() == 0x0000FF00 &&
                   directCM.getBlueMask() == 0x000000FF &&
                   (directCM.getNumComponents() != 4 ||
                    directCM.getAlphaMask() == 0xFF000000);
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public CompositeContext createContext(ColorModel srcColorModel,
                                          ColorModel dstColorModel,
                                          RenderingHints hints) {
        if (!checkComponentsOrder(srcColorModel) ||
                !checkComponentsOrder(dstColorModel)) {
            throw new RasterFormatException("Incompatible color models");
        }
        
        return new BlendingContext(this);
    }

    private static final class BlendingContext implements CompositeContext {
        private final Blender blender;
        private final BlendComposite composite;

        private BlendingContext(BlendComposite composite) {
            this.composite = composite;
            this.blender = Blender.getBlenderFor(composite);
        }

        public void dispose() {
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());

            float alpha = composite.getAlpha();

            int[] result = new int[4];
            int[] srcPixel = new int[4];
            int[] dstPixel = new int[4];
            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y++) {
                src.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);
                for (int x = 0; x < width; x++) {
                    // pixels are stored as INT_ARGB
                    // our arrays are [R, G, B, A]
                    int pixel = srcPixels[x];
                    srcPixel[0] = (pixel >> 16) & 0xFF;
                    srcPixel[1] = (pixel >>  8) & 0xFF;
                    srcPixel[2] = (pixel      ) & 0xFF;
                    srcPixel[3] = (pixel >> 24) & 0xFF;

                    pixel = dstPixels[x];
                    dstPixel[0] = (pixel >> 16) & 0xFF;
                    dstPixel[1] = (pixel >>  8) & 0xFF;
                    dstPixel[2] = (pixel      ) & 0xFF;
                    dstPixel[3] = (pixel >> 24) & 0xFF;

                    blender.blend(srcPixel, dstPixel, result);

                    // mixes the result with the opacity
                    dstPixels[x] = ((int) (dstPixel[3] + (result[3] - dstPixel[3]) * alpha) & 0xFF) << 24 |
                                   ((int) (dstPixel[0] + (result[0] - dstPixel[0]) * alpha) & 0xFF) << 16 |
                                   ((int) (dstPixel[1] + (result[1] - dstPixel[1]) * alpha) & 0xFF) <<  8 |
                                    (int) (dstPixel[2] + (result[2] - dstPixel[2]) * alpha) & 0xFF;
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }

    private static abstract class Blender {
        public abstract void blend(int[] src, int[] dst, int[] result);

        public static Blender getBlenderFor(BlendComposite composite) {
            switch (composite.getMode()) {
                case ADD:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.min(255, src[0] + dst[0]);
                            result[1] = Math.min(255, src[1] + dst[1]);
                            result[2] = Math.min(255, src[2] + dst[2]);
                            result[3] = Math.min(255, src[3] + dst[3]);
                            
                        }
                    };
                case AVERAGE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] + dst[0]) >> 1;
                            result[1] = (src[1] + dst[1]) >> 1;
                            result[2] = (src[2] + dst[2]) >> 1;
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case BLUE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0];
                            result[1] = src[1];
                            result[2] = dst[2];
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case COLOR:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtilities.HSLtoRGB(srcHSL[0], srcHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case COLOR_BURN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - dst[0]) << 8) / src[0]));
                            result[1] = src[1] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - dst[1]) << 8) / src[1]));
                            result[2] = src[2] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - dst[2]) << 8) / src[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case COLOR_DODGE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0] == 255 ? 255 :
                                Math.min((dst[0] << 8) / (255 - src[0]), 255);
                            result[1] = src[1] == 255 ? 255 :
                                Math.min((dst[1] << 8) / (255 - src[1]), 255);
                            result[2] = src[2] == 255 ? 255 :
                                Math.min((dst[2] << 8) / (255 - src[2]), 255);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case DARKEN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.min(src[0], dst[0]);
                            result[1] = Math.min(src[1], dst[1]);
                            result[2] = Math.min(src[2], dst[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case DIFFERENCE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.abs(dst[0] - src[0]);
                            result[1] = Math.abs(dst[1] - src[1]);
                            result[2] = Math.abs(dst[2] - src[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case EXCLUSION:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] + src[0] - (dst[0] * src[0] >> 7);
                            result[1] = dst[1] + src[1] - (dst[1] * src[1] >> 7);
                            result[2] = dst[2] + src[2] - (dst[2] * src[2] >> 7);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case FREEZE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0] == 0 ? 0 :
                                Math.max(0, 255 - (255 - dst[0]) * (255 - dst[0]) / src[0]);
                            result[1] = src[1] == 0 ? 0 :
                                Math.max(0, 255 - (255 - dst[1]) * (255 - dst[1]) / src[1]);
                            result[2] = src[2] == 0 ? 0 :
                                Math.max(0, 255 - (255 - dst[2]) * (255 - dst[2]) / src[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case GLOW:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] == 255 ? 255 :
                                Math.min(255, src[0] * src[0] / (255 - dst[0]));
                            result[1] = dst[1] == 255 ? 255 :
                                Math.min(255, src[1] * src[1] / (255 - dst[1]));
                            result[2] = dst[2] == 255 ? 255 :
                                Math.min(255, src[2] * src[2] / (255 - dst[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case GREEN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0];
                            result[1] = dst[1];
                            result[2] = src[2];
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case HARD_LIGHT:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0] < 128 ? dst[0] * src[0] >> 7 :
                                255 - ((255 - src[0]) * (255 - dst[0]) >> 7);
                            result[1] = src[1] < 128 ? dst[1] * src[1] >> 7 :
                                255 - ((255 - src[1]) * (255 - dst[1]) >> 7);
                            result[2] = src[2] < 128 ? dst[2] * src[2] >> 7 :
                                255 - ((255 - src[2]) * (255 - dst[2]) >> 7);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case HEAT:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] == 0 ? 0 :
                                Math.max(0, 255 - (255 - src[0]) * (255 - src[0]) / dst[0]);
                            result[1] = dst[1] == 0 ? 0 :
                                Math.max(0, 255 - (255 - src[1]) * (255 - src[1]) / dst[1]);
                            result[2] = dst[2] == 0 ? 0 :
                                Math.max(0, 255 - (255 - src[2]) * (255 - src[2]) / dst[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case HUE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtilities.HSLtoRGB(srcHSL[0], dstHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case INVERSE_COLOR_BURN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - src[0]) << 8) / dst[0]));
                            result[1] = dst[1] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - src[1]) << 8) / dst[1]));
                            result[2] = dst[2] == 0 ? 0 :
                                Math.max(0, 255 - (((255 - src[2]) << 8) / dst[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case INVERSE_COLOR_DODGE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] == 255 ? 255 :
                                Math.min((src[0] << 8) / (255 - dst[0]), 255);
                            result[1] = dst[1] == 255 ? 255 :
                                Math.min((src[1] << 8) / (255 - dst[1]), 255);
                            result[2] = dst[2] == 255 ? 255 :
                                Math.min((src[2] << 8) / (255 - dst[2]), 255);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case LIGHTEN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.max(src[0], dst[0]);
                            result[1] = Math.max(src[1], dst[1]);
                            result[2] = Math.max(src[2], dst[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case LUMINOSITY:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtilities.HSLtoRGB(dstHSL[0], dstHSL[1], srcHSL[2], result);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case MULTIPLY:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = (src[0] * dst[0]) >> 8;
                            result[1] = (src[1] * dst[1]) >> 8;
                            result[2] = (src[2] * dst[2]) >> 8;
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case NEGATION:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = 255 - Math.abs(255 - dst[0] - src[0]);
                            result[1] = 255 - Math.abs(255 - dst[1] - src[1]);
                            result[2] = 255 - Math.abs(255 - dst[2] - src[2]);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case OVERLAY:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] < 128 ? dst[0] * src[0] >> 7 :
                                255 - ((255 - dst[0]) * (255 - src[0]) >> 7);
                            result[1] = dst[1] < 128 ? dst[1] * src[1] >> 7 :
                                255 - ((255 - dst[1]) * (255 - src[1]) >> 7);
                            result[2] = dst[2] < 128 ? dst[2] * src[2] >> 7 :
                                255 - ((255 - dst[2]) * (255 - src[2]) >> 7);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case RED:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0];
                            result[1] = dst[1];
                            result[2] = dst[2];
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case REFLECT:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = src[0] == 255 ? 255 :
                                Math.min(255, dst[0] * dst[0] / (255 - src[0]));
                            result[1] = src[1] == 255 ? 255 :
                                Math.min(255, dst[1] * dst[1] / (255 - src[1]));
                            result[2] = src[2] == 255 ? 255 :
                                Math.min(255, dst[2] * dst[2] / (255 - src[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SATURATION:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            float[] srcHSL = new float[3];
                            ColorUtilities.RGBtoHSL(src[0], src[1], src[2], srcHSL);
                            float[] dstHSL = new float[3];
                            ColorUtilities.RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

                            ColorUtilities.HSLtoRGB(dstHSL[0], srcHSL[1], dstHSL[2], result);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SCREEN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = 255 - ((255 - src[0]) * (255 - dst[0]) >> 8);
                            result[1] = 255 - ((255 - src[1]) * (255 - dst[1]) >> 8);
                            result[2] = 255 - ((255 - src[2]) * (255 - dst[2]) >> 8);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SOFT_BURN:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] + src[0] < 256 ?
                                (dst[0] == 255 ? 255 :
                                 Math.min(255, (src[0] << 7) / (255 - dst[0]))) :
                                                                                Math.max(0, 255 - (((255 - dst[0]) << 7) / src[0]));
                            result[1] = dst[1] + src[1] < 256 ?
                                (dst[1] == 255 ? 255 :
                                 Math.min(255, (src[1] << 7) / (255 - dst[1]))) :
                                                                                Math.max(0, 255 - (((255 - dst[1]) << 7) / src[1]));
                            result[2] = dst[2] + src[2] < 256 ?
                                (dst[2] == 255 ? 255 :
                                 Math.min(255, (src[2] << 7) / (255 - dst[2]))) :
                                                                                Math.max(0, 255 - (((255 - dst[2]) << 7) / src[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SOFT_DODGE:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = dst[0] + src[0] < 256 ?
                                (src[0] == 255 ? 255 :
                                 Math.min(255, (dst[0] << 7) / (255 - src[0]))) :
                                    Math.max(0, 255 - (((255 - src[0]) << 7) / dst[0]));
                            result[1] = dst[1] + src[1] < 256 ?
                                (src[1] == 255 ? 255 :
                                 Math.min(255, (dst[1] << 7) / (255 - src[1]))) :
                                    Math.max(0, 255 - (((255 - src[1]) << 7) / dst[1]));
                            result[2] = dst[2] + src[2] < 256 ?
                                (src[2] == 255 ? 255 :
                                 Math.min(255, (dst[2] << 7) / (255 - src[2]))) :
                                    Math.max(0, 255 - (((255 - src[2]) << 7) / dst[2]));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SOFT_LIGHT:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            int mRed = src[0] * dst[0] / 255;
                            int mGreen = src[1] * dst[1] / 255;
                            int mBlue = src[2] * dst[2] / 255;
                            result[0] = mRed + src[0] * (255 - ((255 - src[0]) * (255 - dst[0]) / 255) - mRed) / 255;
                            result[1] = mGreen + src[1] * (255 - ((255 - src[1]) * (255 - dst[1]) / 255) - mGreen) / 255;
                            result[2] = mBlue + src[2] * (255 - ((255 - src[2]) * (255 - dst[2]) / 255) - mBlue) / 255;
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case STAMP:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.max(0, Math.min(255, dst[0] + 2 * src[0] - 256));
                            result[1] = Math.max(0, Math.min(255, dst[1] + 2 * src[1] - 256));
                            result[2] = Math.max(0, Math.min(255, dst[2] + 2 * src[2] - 256));
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
                case SUBTRACT:
                    return new Blender() {
                        @Override
                        public void blend(int[] src, int[] dst, int[] result) {
                            result[0] = Math.max(0, src[0] + dst[0] - 256);
                            result[1] = Math.max(0, src[1] + dst[1] - 256);
                            result[2] = Math.max(0, src[2] + dst[2] - 256);
                            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
                        }
                    };
            }
            throw new IllegalArgumentException("Blender not implemented for " +
                                               composite.getMode().name());
        }
   }
}
 
 
 /*
  * $Id: GraphicsUtilities.java,v 1.1 2006/12/15 13:53:13 gfx Exp $
  *
  * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
  *
  * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
  * Santa Clara, California 95054, U.S.A. All rights reserved.
  *
  * Copyright (c) 2006 Romain Guy <romain.guy@mac.com>
  * All rights reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions
  * are met:
  * 1. Redistributions of source code must retain the above copyright
  *    notice, this list of conditions and the following disclaimer.
  * 2. Redistributions in binary form must reproduce the above copyright
  *    notice, this list of conditions and the following disclaimer in the
  *    documentation and/or other materials provided with the distribution.
  * 3. The name of the author may not be used to endorse or promote products
  *    derived from this software without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  */



 /**
  * <p><code>GraphicsUtilities</code> contains a set of tools to perform
  * common graphics operations easily. These operations are divided into
  * several themes, listed below.</p>
  * <h2>Compatible Images</h2>
  * <p>Compatible images can, and should, be used to increase drawing
  * performance. This class provides a number of methods to load compatible
  * images directly from files or to convert existing images to compatibles
  * images.</p>
  * <h2>Creating Thumbnails</h2>
  * <p>This class provides a number of methods to easily scale down images.
  * Some of these methods offer a trade-off between speed and result quality and
  * shouuld be used all the time. They also offer the advantage of producing
  * compatible images, thus automatically resulting into better runtime
  * performance.</p>
  * <p>All these methodes are both faster than
  * {@link java.awt.Image#getScaledInstance(int, int, int)} and produce
  * better-looking results than the various <code>drawImage()</code> methods
  * in {@link java.awt.Graphics}, which can be used for image scaling.</p>
  * <h2>Image Manipulation</h2>
  * <p>This class provides two methods to get and set pixels in a buffered image.
  * These methods try to avoid unmanaging the image in order to keep good
  * performance.</p>
  *
  * @author Romain Guy <romain.guy@mac.com>
  */
  class GraphicsUtilities {
     private static final GraphicsConfiguration CONFIGURATION =
             GraphicsEnvironment.getLocalGraphicsEnvironment().
                     getDefaultScreenDevice().getDefaultConfiguration();

     private GraphicsUtilities() {
     }

     /**
      * <p>Returns a new <code>BufferedImage</code> using the same color model
      * as the image passed as a parameter. The returned image is only compatible
      * with the image passed as a parameter. This does not mean the returned
      * image is compatible with the hardware.</p>
      *
      * @param image the reference image from which the color model of the new
      *   image is obtained
      * @return a new <code>BufferedImage</code>, compatible with the color model
      *   of <code>image</code>
      */
     public static BufferedImage createColorModelCompatibleImage(BufferedImage image) {
         ColorModel cm = image.getColorModel();
         return new BufferedImage(cm,
             cm.createCompatibleWritableRaster(image.getWidth(),
                                               image.getHeight()),
             cm.isAlphaPremultiplied(), null);
     }

     /**
      * <p>Returns a new compatible image with the same width, height and
      * transparency as the image specified as a parameter.</p>
      *
      * @see java.awt.Transparency
      * @see #createCompatibleImage(int, int)
      * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
      * @see #createTranslucentCompatibleImage(int, int)
      * @see #loadCompatibleImage(java.net.URL)
      * @see #toCompatibleImage(java.awt.image.BufferedImage)
      * @param image the reference image from which the dimension and the
      *   transparency of the new image are obtained
      * @return a new compatible <code>BufferedImage</code> with the same
      *   dimension and transparency as <code>image</code>
      */
     public static BufferedImage createCompatibleImage(BufferedImage image) {
         return createCompatibleImage(image, image.getWidth(), image.getHeight());
     }

     /**
      * <p>Returns a new compatible image of the specified width and height, and
      * the same transparency setting as the image specified as a parameter.</p>
      *
      * @see java.awt.Transparency
      * @see #createCompatibleImage(java.awt.image.BufferedImage)
      * @see #createCompatibleImage(int, int)
      * @see #createTranslucentCompatibleImage(int, int)
      * @see #loadCompatibleImage(java.net.URL)
      * @see #toCompatibleImage(java.awt.image.BufferedImage)
      * @param width the width of the new image
      * @param height the height of the new image
      * @param image the reference image from which the transparency of the new
      *   image is obtained
      * @return a new compatible <code>BufferedImage</code> with the same
      *   transparency as <code>image</code> and the specified dimension
      */
     public static BufferedImage createCompatibleImage(BufferedImage image,
                                                       int width, int height) {
         return CONFIGURATION.createCompatibleImage(width, height,
                                                    image.getTransparency());
     }

     /**
      * <p>Returns a new opaque compatible image of the specified width and
      * height.</p>
      *
      * @see #createCompatibleImage(java.awt.image.BufferedImage)
      * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
      * @see #createTranslucentCompatibleImage(int, int)
      * @see #loadCompatibleImage(java.net.URL)
      * @see #toCompatibleImage(java.awt.image.BufferedImage)
      * @param width the width of the new image
      * @param height the height of the new image
      * @return a new opaque compatible <code>BufferedImage</code> of the
      *   specified width and height
      */
     public static BufferedImage createCompatibleImage(int width, int height) {
         return CONFIGURATION.createCompatibleImage(width, height);
     }

     /**
      * <p>Returns a new translucent compatible image of the specified width
      * and height.</p>
      *
      * @see #createCompatibleImage(java.awt.image.BufferedImage)
      * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
      * @see #createCompatibleImage(int, int)
      * @see #loadCompatibleImage(java.net.URL)
      * @see #toCompatibleImage(java.awt.image.BufferedImage)
      * @param width the width of the new image
      * @param height the height of the new image
      * @return a new translucent compatible <code>BufferedImage</code> of the
      *   specified width and height
      */
     public static BufferedImage createTranslucentCompatibleImage(int width,
                                                                  int height) {
         return CONFIGURATION.createCompatibleImage(width, height,
                                                    Transparency.TRANSLUCENT);
     }

     /**
      * <p>Returns a new compatible image from a URL. The image is loaded from the
      * specified location and then turned, if necessary into a compatible
      * image.</p>
      *
      * @see #createCompatibleImage(java.awt.image.BufferedImage)
      * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
      * @see #createCompatibleImage(int, int)
      * @see #createTranslucentCompatibleImage(int, int)
      * @see #toCompatibleImage(java.awt.image.BufferedImage)
      * @param resource the URL of the picture to load as a compatible image
      * @return a new translucent compatible <code>BufferedImage</code> of the
      *   specified width and height
      * @throws java.io.IOException if the image cannot be read or loaded
      */
     public static BufferedImage loadCompatibleImage(URL resource)
             throws IOException {
         BufferedImage image = ImageIO.read(resource);
         return toCompatibleImage(image);
     }

     /**
      * <p>Return a new compatible image that contains a copy of the specified
      * image. This method ensures an image is compatible with the hardware,
      * and therefore optimized for fast blitting operations.</p>
      *
      * @see #createCompatibleImage(java.awt.image.BufferedImage)
      * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
      * @see #createCompatibleImage(int, int)
      * @see #createTranslucentCompatibleImage(int, int)
      * @see #loadCompatibleImage(java.net.URL)
      * @param image the image to copy into a new compatible image
      * @return a new compatible copy, with the
      *   same width and height and transparency and content, of <code>image</code>
      */
     public static BufferedImage toCompatibleImage(BufferedImage image) {
         if (image.getColorModel().equals(CONFIGURATION.getColorModel())) {
             return image;
         }

         BufferedImage compatibleImage = CONFIGURATION.createCompatibleImage(
                 image.getWidth(), image.getHeight(), image.getTransparency());
         Graphics g = compatibleImage.getGraphics();
         g.drawImage(image, 0, 0, null);
         g.dispose();

         return compatibleImage;
     }

     /**
      * <p>Returns a thumbnail of a source image. <code>newSize</code> defines
      * the length of the longest dimension of the thumbnail. The other
      * dimension is then computed according to the dimensions ratio of the
      * original picture.</p>
      * <p>This method favors speed over quality. When the new size is less than
      * half the longest dimension of the source image,
      * {@link #createThumbnail(BufferedImage, int)} or
      * {@link #createThumbnail(BufferedImage, int, int)} should be used instead
      * to ensure the quality of the result without sacrificing too much
      * performance.</p>
      *
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int, int)
      * @param image the source image
      * @param newSize the length of the largest dimension of the thumbnail
      * @return a new compatible <code>BufferedImage</code> containing a
      *   thumbnail of <code>image</code>
      * @throws IllegalArgumentException if <code>newSize</code> is larger than
      *   the largest dimension of <code>image</code> or &lt;= 0
      */
     public static BufferedImage createThumbnailFast(BufferedImage image,
                                                     int newSize) {
         float ratio;
         int width = image.getWidth();
         int height = image.getHeight();

         if (width > height) {
             if (newSize >= width) {
                 throw new IllegalArgumentException("newSize must be lower than" +
                                                    " the image width");
             } else if (newSize <= 0) {
                  throw new IllegalArgumentException("newSize must" +
                                                     " be greater than 0");
             }

             ratio = (float) width / (float) height;
             width = newSize;
             height = (int) (newSize / ratio);
         } else {
             if (newSize >= height) {
                 throw new IllegalArgumentException("newSize must be lower than" +
                                                    " the image height");
             } else if (newSize <= 0) {
                  throw new IllegalArgumentException("newSize must" +
                                                     " be greater than 0");
             }

             ratio = (float) height / (float) width;
             height = newSize;
             width = (int) (newSize / ratio);
         }

         BufferedImage temp = createCompatibleImage(image, width, height);
         Graphics2D g2 = temp.createGraphics();
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         g2.drawImage(image, 0, 0, temp.getWidth(), temp.getHeight(), null);
         g2.dispose();

         return temp;
     }

     /**
      * <p>Returns a thumbnail of a source image.</p>
      * <p>This method favors speed over quality. When the new size is less than
      * half the longest dimension of the source image,
      * {@link #createThumbnail(BufferedImage, int)} or
      * {@link #createThumbnail(BufferedImage, int, int)} should be used instead
      * to ensure the quality of the result without sacrificing too much
      * performance.</p>
      *
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int, int)
      * @param image the source image
      * @param newWidth the width of the thumbnail
      * @param newHeight the height of the thumbnail
      * @return a new compatible <code>BufferedImage</code> containing a
      *   thumbnail of <code>image</code>
      * @throws IllegalArgumentException if <code>newWidth</code> is larger than
      *   the width of <code>image</code> or if code>newHeight</code> is larger
      *   than the height of <code>image</code> or if one of the dimensions
      *   is &lt;= 0
      */
     public static BufferedImage createThumbnailFast(BufferedImage image,
                                                     int newWidth, int newHeight) {
         if (newWidth >= image.getWidth() ||
             newHeight >= image.getHeight()) {
             throw new IllegalArgumentException("newWidth and newHeight cannot" +
                                                " be greater than the image" +
                                                " dimensions");
         } else if (newWidth <= 0 || newHeight <= 0) {
             throw new IllegalArgumentException("newWidth and newHeight must" +
                                                " be greater than 0");
         }

         BufferedImage temp = createCompatibleImage(image, newWidth, newHeight);
         Graphics2D g2 = temp.createGraphics();
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         g2.drawImage(image, 0, 0, temp.getWidth(), temp.getHeight(), null);
         g2.dispose();

         return temp;
     }

     /**
      * <p>Returns a thumbnail of a source image. <code>newSize</code> defines
      * the length of the longest dimension of the thumbnail. The other
      * dimension is then computed according to the dimensions ratio of the
      * original picture.</p>
      * <p>This method offers a good trade-off between speed and quality.
      * The result looks better than
      * {@link #createThumbnailFast(java.awt.image.BufferedImage, int)} when
      * the new size is less than half the longest dimension of the source
      * image, yet the rendering speed is almost similar.</p>
      *
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int, int)
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int, int)
      * @param image the source image
      * @param newSize the length of the largest dimension of the thumbnail
      * @return a new compatible <code>BufferedImage</code> containing a
      *   thumbnail of <code>image</code>
      * @throws IllegalArgumentException if <code>newSize</code> is larger than
      *   the largest dimension of <code>image</code> or &lt;= 0
      */
     public static BufferedImage createThumbnail(BufferedImage image,
                                                 int newSize) {
         int width = image.getWidth();
         int height = image.getHeight();

         boolean isWidthGreater = width > height;

         if (isWidthGreater) {
             if (newSize >= width) {
                 throw new IllegalArgumentException("newSize must be lower than" +
                                                    " the image width");
             }
         } else if (newSize >= height) {
             throw new IllegalArgumentException("newSize must be lower than" +
                                                " the image height");
         }

         if (newSize <= 0) {
             throw new IllegalArgumentException("newSize must" +
                                                " be greater than 0");
         }

         float ratioWH = (float) width / (float) height;
         float ratioHW = (float) height / (float) width;

         BufferedImage thumb = image;

         do {
             if (isWidthGreater) {
                 width /= 2;
                 if (width < newSize) {
                     width = newSize;
                 }
                 height = (int) (width / ratioWH);
             } else {
                 height /= 2;
                 if (height < newSize) {
                     height = newSize;
                 }
                 width = (int) (height / ratioHW);
             }


             BufferedImage temp = createCompatibleImage(image, width, height);
             Graphics2D g2 = temp.createGraphics();
             g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
             g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
             g2.dispose();

             thumb = temp;
         } while (newSize != (isWidthGreater ? width : height));

         return thumb;
     }

     /**
      * <p>Returns a thumbnail of a source image.</p>
      * <p>This method offers a good trade-off between speed and quality.
      * The result looks better than
      * {@link #createThumbnailFast(java.awt.image.BufferedImage, int)} when
      * the new size is less than half the longest dimension of the source
      * image, yet the rendering speed is almost similar.</p>
      *
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int)
      * @see #createThumbnailFast(java.awt.image.BufferedImage, int, int)
      * @see #createThumbnail(java.awt.image.BufferedImage, int)
      * @param image the source image
      * @param newWidth the width of the thumbnail
      * @param newHeight the height of the thumbnail
      * @return a new compatible <code>BufferedImage</code> containing a
      *   thumbnail of <code>image</code>
      * @throws IllegalArgumentException if <code>newWidth</code> is larger than
      *   the width of <code>image</code> or if code>newHeight</code> is larger
      *   than the height of <code>image or if one the dimensions is not &gt; 0</code>
      */
     public static BufferedImage createThumbnail(BufferedImage image,
                                                 int newWidth, int newHeight) {
         int width = image.getWidth();
         int height = image.getHeight();

         if (newWidth >= width || newHeight >= height) {
             throw new IllegalArgumentException("newWidth and newHeight cannot" +
                                                " be greater than the image" +
                                                " dimensions");
         } else if (newWidth <= 0 || newHeight <= 0) {
             throw new IllegalArgumentException("newWidth and newHeight must" +
                                                " be greater than 0");
         }

         BufferedImage thumb = image;

         do {
             if (width > newWidth) {
                 width /= 2;
                 if (width < newWidth) {
                     width = newWidth;
                 }
             }

             if (height > newHeight) {
                 height /= 2;
                 if (height < newHeight) {
                     height = newHeight;
                 }
             }

             BufferedImage temp = createCompatibleImage(image, width, height);
             Graphics2D g2 = temp.createGraphics();
             g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
             g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
             g2.dispose();

             thumb = temp;
         } while (width != newWidth || height != newHeight);

         return thumb;
     }

     /**
      * <p>Returns an array of pixels, stored as integers, from a
      * <code>BufferedImage</code>. The pixels are grabbed from a rectangular
      * area defined by a location and two dimensions. Calling this method on
      * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
      * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
      *
      * @param img the source image
      * @param x the x location at which to start grabbing pixels
      * @param y the y location at which to start grabbing pixels
      * @param w the width of the rectangle of pixels to grab
      * @param h the height of the rectangle of pixels to grab
      * @param pixels a pre-allocated array of pixels of size w*h; can be null
      * @return <code>pixels</code> if non-null, a new array of integers
      *   otherwise
      * @throws IllegalArgumentException is <code>pixels</code> is non-null and
      *   of length &lt; w*h
      */
     public static int[] getPixels(BufferedImage img,
                                   int x, int y, int w, int h, int[] pixels) {
         if (w == 0 || h == 0) {
             return new int[0];
         }

         if (pixels == null) {
             pixels = new int[w * h];
         } else if (pixels.length < w * h) {
             throw new IllegalArgumentException("pixels array must have a length" +
                                                " >= w*h");
         }

         int imageType = img.getType();
         if (imageType == BufferedImage.TYPE_INT_ARGB ||
             imageType == BufferedImage.TYPE_INT_RGB) {
             Raster raster = img.getRaster();
             return (int[]) raster.getDataElements(x, y, w, h, pixels);
         }

         // Unmanages the image
         return img.getRGB(x, y, w, h, pixels, 0, w);
     }

     /**
      * <p>Writes a rectangular area of pixels in the destination
      * <code>BufferedImage</code>. Calling this method on
      * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
      * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
      *
      * @param img the destination image
      * @param x the x location at which to start storing pixels
      * @param y the y location at which to start storing pixels
      * @param w the width of the rectangle of pixels to store
      * @param h the height of the rectangle of pixels to store
      * @param pixels an array of pixels, stored as integers
      * @throws IllegalArgumentException is <code>pixels</code> is non-null and
      *   of length &lt; w*h
      */
     public static void setPixels(BufferedImage img,
                                  int x, int y, int w, int h, int[] pixels) {
         if (pixels == null || w == 0 || h == 0) {
             return;
         } else if (pixels.length < w * h) {
             throw new IllegalArgumentException("pixels array must have a length" +
                                                " >= w*h");
         }

         int imageType = img.getType();
         if (imageType == BufferedImage.TYPE_INT_ARGB ||
             imageType == BufferedImage.TYPE_INT_RGB) {
             WritableRaster raster = img.getRaster();
             raster.setDataElements(x, y, w, h, pixels);
         } else {
             // Unmanages the image
             img.setRGB(x, y, w, h, pixels, 0, w);
         }
     }
 }
  /*
   * $Id: ColorUtilities.java,v 1.1 2006/12/15 13:53:13 gfx Exp $
   *
   * Dual-licensed under LGPL (Sun and Romain Guy) and BSD (Romain Guy).
   *
   * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
   * Santa Clara, California 95054, U.S.A. All rights reserved.
   *
   * Copyright (c) 2006 Romain Guy <romain.guy@mac.com>
   * All rights reserved.
   *
   * Redistribution and use in source and binary forms, with or without
   * modification, are permitted provided that the following conditions
   * are met:
   * 1. Redistributions of source code must retain the above copyright
   *    notice, this list of conditions and the following disclaimer.
   * 2. Redistributions in binary form must reproduce the above copyright
   *    notice, this list of conditions and the following disclaimer in the
   *    documentation and/or other materials provided with the distribution.
   * 3. The name of the author may not be used to endorse or promote products
   *    derived from this software without specific prior written permission.
   *
   * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
   * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
   * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
   * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
   * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
   * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
   * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
   * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
   * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
   * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
   */



  /**
   * <p><code>ColorUtilities</code> contains a set of tools to perform
   * common color operations easily.</p>
   *
   * @author Romain Guy <romain.guy@mac.com>
   */
   class ColorUtilities {
      private ColorUtilities() {
      }

      /**
       * <p>Returns the HSL (Hue/Saturation/Luminance) equivalent of a given
       * RGB color. All three HSL components are between 0.0 and 1.0.</p>
       *
       * @param color the RGB color to convert
       * @return a new array of 3 floats corresponding to the HSL components
       */
      public static float[] RGBtoHSL(Color color) {
          return RGBtoHSL(color.getRed(), color.getGreen(), color.getBlue(), null);
      }

      /**
       * <p>Returns the HSL (Hue/Saturation/Luminance) equivalent of a given
       * RGB color. All three HSL components are between 0.0 and 1.0.</p>
       *
       * @param color the RGB color to convert
       * @param hsl a pre-allocated array of floats; can be null
       * @return <code>hsl</code> if non-null, a new array of 3 floats otherwise
       * @throws IllegalArgumentException if <code>hsl</code> has a length lower
       *   than 3
       */
      public static float[] RGBtoHSL(Color color, float[] hsl) {
          return RGBtoHSL(color.getRed(), color.getGreen(), color.getBlue(), hsl);
      }

      /**
       * <p>Returns the HSL (Hue/Saturation/Luminance) equivalent of a given
       * RGB color. All three HSL components are between 0.0 and 1.0.</p>
       *
       * @param r the red component, between 0 and 255
       * @param g the green component, between 0 and 255
       * @param b the blue component, between 0 and 255
       * @return a new array of 3 floats corresponding to the HSL components
       */
      public static float[] RGBtoHSL(int r, int g, int b) {
          return RGBtoHSL(r, g, b, null);
      }

      /**
       * <p>Returns the HSL (Hue/Saturation/Luminance) equivalent of a given
       * RGB color. All three HSL components are floats between 0.0 and 1.0.</p>
       *
       * @param r the red component, between 0 and 255
       * @param g the green component, between 0 and 255
       * @param b the blue component, between 0 and 255
       * @param hsl a pre-allocated array of floats; can be null
       * @return <code>hsl</code> if non-null, a new array of 3 floats otherwise
       * @throws IllegalArgumentException if <code>hsl</code> has a length lower
       *   than 3
       */
      public static float[] RGBtoHSL(int r, int g, int b, float[] hsl) {
          if (hsl == null) {
              hsl = new float[3];
          } else if (hsl.length < 3) {
              throw new IllegalArgumentException("hsl array must have a length of" +
                                                 " at least 3");
          }

          if (r < 0) r = 0;
          else if (r > 255) r = 255;
          if (g < 0) g = 0;
          else if (g > 255) g = 255;
          if (b < 0) b = 0;
          else if (b > 255) b = 255;

          float var_R = (r / 255f);
          float var_G = (g / 255f);
          float var_B = (b / 255f);

          float var_Min;
          float var_Max;
          float del_Max;

          if (var_R > var_G) {
              var_Min = var_G;
              var_Max = var_R;
          } else {
              var_Min = var_R;
              var_Max = var_G;
          }
          if (var_B > var_Max) {
              var_Max = var_B;
          }
          if (var_B < var_Min) {
              var_Min = var_B;
          }

          del_Max = var_Max - var_Min;

          float H, S, L;
          L = (var_Max + var_Min) / 2f;

          if (del_Max - 0.01f <= 0.0f) {
              H = 0;
              S = 0;
          } else {
              if (L < 0.5f) {
                  S = del_Max / (var_Max + var_Min);
              } else {
                  S = del_Max / (2 - var_Max - var_Min);
              }

              float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
              float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
              float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

              if (var_R == var_Max) {
                  H = del_B - del_G;
              } else if (var_G == var_Max) {
                  H = (1 / 3f) + del_R - del_B;
              } else {
                  H = (2 / 3f) + del_G - del_R;
              }
              if (H < 0) {
                  H += 1;
              }
              if (H > 1) {
                  H -= 1;
              }
          }

          hsl[0] = H;
          hsl[1] = S;
          hsl[2] = L;

          return hsl;
      }

      /**
       * <p>Returns the RGB equivalent of a given HSL (Hue/Saturation/Luminance)
       * color.</p>
       *
       * @param h the hue component, between 0.0 and 1.0
       * @param s the saturation component, between 0.0 and 1.0
       * @param l the luminance component, between 0.0 and 1.0
       * @return a new <code>Color</code> object equivalent to the HSL components
       */
      public static Color HSLtoRGB(float h, float s, float l) {
          int[] rgb = HSLtoRGB(h, s, l, null);
          return new Color(rgb[0], rgb[1], rgb[2]);
      }

      /**
       * <p>Returns the RGB equivalent of a given HSL (Hue/Saturation/Luminance)
       * color. All three RGB components are integers between 0 and 255.</p>
       *
       * @param h the hue component, between 0.0 and 1.0
       * @param s the saturation component, between 0.0 and 1.0
       * @param l the luminance component, between 0.0 and 1.0
       * @param rgb a pre-allocated array of ints; can be null
       * @return <code>rgb</code> if non-null, a new array of 3 ints otherwise
       * @throws IllegalArgumentException if <code>rgb</code> has a length lower
       *   than 3
       */
      public static int[] HSLtoRGB(float h, float s, float l, int[] rgb) {
          if (rgb == null) {
              rgb = new int[3];
          } else if (rgb.length < 3) {
              throw new IllegalArgumentException("rgb array must have a length of" +
                                                 " at least 3");
          }

          if (h < 0) h = 0.0f;
          else if (h > 1.0f) h = 1.0f;
          if (s < 0) s = 0.0f;
          else if (s > 1.0f) s = 1.0f;
          if (l < 0) l = 0.0f;
          else if (l > 1.0f) l = 1.0f;

          int R, G, B;

          if (s - 0.01f <= 0.0f) {
              R = (int) (l * 255.0f);
              G = (int) (l * 255.0f);
              B = (int) (l * 255.0f);
          } else {
              float var_1, var_2;
              if (l < 0.5f) {
                  var_2 = l * (1 + s);
              } else {
                  var_2 = (l + s) - (s * l);
              }
              var_1 = 2 * l - var_2;

              R = (int) (255.0f * hue2RGB(var_1, var_2, h + (1.0f / 3.0f)));
              G = (int) (255.0f * hue2RGB(var_1, var_2, h));
              B = (int) (255.0f * hue2RGB(var_1, var_2, h - (1.0f / 3.0f)));
          }

          rgb[0] = R;
          rgb[1] = G;
          rgb[2] = B;

          return rgb;
      }

      private static float hue2RGB(float v1, float v2, float vH) {
          if (vH < 0.0f) {
              vH += 1.0f;
          }
          if (vH > 1.0f) {
              vH -= 1.0f;
          }
          if ((6.0f * vH) < 1.0f) {
              return (v1 + (v2 - v1) * 6.0f * vH);
          }
          if ((2.0f * vH) < 1.0f) {
              return (v2);
          }
          if ((3.0f * vH) < 2.0f) {
              return (v1 + (v2 - v1) * ((2.0f / 3.0f) - vH) * 6.0f);
          }
          return (v1);
      }
  }