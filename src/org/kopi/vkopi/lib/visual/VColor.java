/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.lib.visual;

import java.io.Serializable;


/**
 * The <code>VColor</code> class is used to encapsulate colors in the default
 * sRGB color space.  Every color has an implicit alpha value of 1.0 or
 * an explicit one provided in the constructor.  The alpha value
 * defines the transparency of a color and can be represented by
 * a float value in the range 0.0&nbsp;-&nbsp;1.0 or 0&nbsp;-&nbsp;255.
 * An alpha value of 1.0 or 255 means that the color is completely
 * opaque and an alpha value of 0 or 0.0 means that the color is
 * completely transparent.
 */
@SuppressWarnings("serial")
public class VColor implements Serializable {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates an opaque sRGB color with the specified red, green,
   * and blue values in the range (0 - 255).
   * The actual color used in rendering depends
   * on finding the best match given the color space
   * available for a given output device.
   * Alpha is defaulted to 255.
   * 
   * @param r the red component
   * @param g the green component
   * @param b the blue component
   * @throws IllegalArgumentException if <code>r</code>, <code>g</code>
   *        or <code>b</code> are outside of the range
   *        0 to 255, inclusive
   */
  public VColor(int r, int g, int b) {
    this(r, g, b, 255);
  }

  /**
   * Creates an sRGB color with the specified red, green, blue, and alpha
   * values in the range (0 - 255).
   * @param r the red component
   * @param g the green component
   * @param b the blue component
   * @param a the alpha component
   * @throws IllegalArgumentException if <code>r</code>, <code>g</code>,
   *        <code>b</code> or <code>a</code> are outside of the range
   *        0 to 255, inclusive
   */
  public VColor(int r, int g, int b, int a) {
    this.value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
    testColorValueRange(r,g,b,a);
  }

  /**
   * Creates an opaque sRGB color with the specified combined RGB value
   * consisting of the red component in bits 16-23, the green component
   * in bits 8-15, and the blue component in bits 0-7.  The actual color
   * used in rendering depends on finding the best match given the
   * color space available for a particular output device.  Alpha is
   * defaulted to 255.
   *
   * @param rgb the combined RGB components
   */
  public VColor(int rgb) {
    this.value = 0xff000000 | rgb;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Creates a new <code>Color</code> that is a brighter version of this
   * <code>VColor</code>.
   */
  public VColor brighter() {
    int		r = getRed();
    int		g = getGreen();
    int		b = getBlue();
    int		alpha = getAlpha();
    int 	i = (int)(1.0 / (1.0-FACTOR));

    if ( r == 0 && g == 0 && b == 0) {
      return new VColor(i, i, i, alpha);
    }
    if ( r > 0 && r < i ) {
      r = i;
    }
    if ( g > 0 && g < i ) {
      g = i;
    }
    if ( b > 0 && b < i ) {
      b = i;
    }

    return new VColor(Math.min((int)(r / FACTOR), 255),
	              Math.min((int)(g / FACTOR), 255),
	              Math.min((int)(b / FACTOR), 255),
	              alpha);
  }

  /**
   * Creates a new <code>Color</code> that is a darker version of this
   * <code>Color</code>.
   */
  public VColor darker() {
      return new VColor(Math.max((int)(getRed()  *FACTOR), 0),
                        Math.max((int)(getGreen() *FACTOR), 0),
                        Math.max((int)(getBlue() *FACTOR), 0),
                        getAlpha());
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VColor && ((VColor)obj).getRGB() == this.getRGB();
  }

  @Override
  public String toString() {
    return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * Returns the red component in the range 0-255 in the default sRGB space.
   * @return the red component.
   */
  public int getRed() {
    return (getRGB() >> 16) & 0xFF;
  }

  /**
   * Returns the green component in the range 0-255 in the default sRGB space.
   * @return the green component.
   */
  public int getGreen() {
    return (getRGB() >> 8) & 0xFF;
  }

  /**
   * Returns the blue component in the range 0-255 in the default sRGB space.
   * @return the blue component.
   */
  public int getBlue() {
    return (getRGB() >> 0) & 0xFF;
  }

  /**
   * Returns the alpha component in the range 0-255.
   * @return the alpha component.
   */
  public int getAlpha() {
    return (getRGB() >> 24) & 0xff;
  }

  /**
   * Returns the RGB value representing the color in the default sRGB space.
   * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
   * blue).
   * @return the RGB value of the color in the default sRGB space.
   */
  public int getRGB() {
    return value;
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------

  /**
   * Checks the color integer components supplied for validity.
   * Throws an {@link IllegalArgumentException} if the value is out of
   * range.
   * @param r the Red component
   * @param g the Green component
   * @param b the Blue component
   **/
  private static void testColorValueRange(int r, int g, int b, int a) {
    boolean 		rangeError = false;
    String 		badComponentString = "";

    if ( a < 0 || a > 255) {
      rangeError = true;
      badComponentString = badComponentString + " Alpha";
    }
    if ( r < 0 || r > 255) {
      rangeError = true;
      badComponentString = badComponentString + " Red";
    }
    if ( g < 0 || g > 255) {
      rangeError = true;
      badComponentString = badComponentString + " Green";
    }
    if ( b < 0 || b > 255) {
      rangeError = true;
      badComponentString = badComponentString + " Blue";
    }
    if ( rangeError == true ) {
      throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * The color value.
   * @serial
   * @see #getRGB
   */
  private final int 				value;
  
  // color constants
  private static final double 			FACTOR = 0.7;
  
  /**
   * The color white. In the default sRGB space.
   */
  public final static VColor 			WHITE = new VColor(255, 255, 255);

  /**
   * The VColor light gray. In the default sRGB space.
   */
  public final static VColor 			LIGHT_GRAY = new VColor(192, 192, 192);


  /**
   * The VColor gray. In the default sRGB space.
   */
  public final static VColor 			GRAY = new VColor(128, 128, 128);


  /**
   * The VColor dark gray. In the default sRGB space.
   */
  public final static VColor 			DARK_GRAY  = new VColor(64, 64, 64);

  /**
   * The VColor black. In the default sRGB space.
   */
  public final static VColor 			BLACK = new VColor(0, 0, 0);

  /**
   * The VColor red. In the default sRGB space.
   */
  public final static VColor 			RED = new VColor(255, 0, 0);


  /**
   * The VColor pink. In the default sRGB space.
   */
  public final static VColor	 		PINK = new VColor(255, 175, 175);

  /**
   * The VColor orange. In the default sRGB space.
   */
  public final static VColor 			ORANGE = new VColor(255, 200, 0);

  /**
   * The VColor yellow. In the default sRGB space.
   */
  public final static VColor 			YELLOW = new VColor(255, 255, 0);

  /**
   * The VColor green. In the default sRGB space.
   */
  public final static VColor 			GREEN = new VColor(0, 255, 0);

  /**
   * The VColor magenta. In the default sRGB space.
   */
  public final static VColor 			MAGENTA = new VColor(255, 0, 255);

  /**
   * The VColor cyan.  In the default sRGB space.
   */
  public final static VColor 			CYAN = new VColor(0, 255, 255);
  /**
   * The VColor blue.  In the default sRGB space.
   */
  public final static VColor 			BLUE = new VColor(0, 0, 255);
}
