/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.util;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;

public class PixelConsumer implements ImageConsumer {

  /**
   * Constructs a new pixel consumer
   */
  PixelConsumer(Image picture) {
    picture.getSource().startProduction(this);

    for (int t = 50000; t > 0 && !complete; t -= 10) {
      try {
        Thread.sleep(10);
      } catch (Throwable e) {
	// ignore it
      }
    }
  }

  public boolean isComplete() {
    return complete; 
  }

  public void imageComplete(int status) {
    // TBR: bug as STATICIMAGEDONE is sent twice before and a break at this
    // time leads to incomplete images.
    complete = status == IMAGEERROR;		
  }

  public void setColorModel(ColorModel ignored) {
    // we currently ignore the color model
  }

  public void setDimensions(int x, int y) {
    dimension = new Dimension(y, x);
    pixelTable = new int[x][y];
    transparent = new boolean[x][y];
  }

  public void setHints(int ignored) {
    // we currently ignore any hints
  }

  public void setPixels(int x1,
			int y1,
			int w,
			int h,
                        ColorModel model,
			byte[] pixels,
			int off,
			int scansize) {
    int x, y, x2, y2, sx, sy;

    x2 = x1+w;
    y2 = y1+h;
    sy = off;

    for (y=y1; y<y2; y++) {
      sx = sy;
      for (x=x1; x<x2; x++) {
	transparent[x][y] = model.getAlpha(Math.abs(pixels[sx])) > 0;
        pixelTable[x][y] = model.getRGB(Math.abs(pixels[sx++]));
      }
      sy += scansize;
    }
  }

  public void setPixels(int x1,
			int y1,
			int w,
			int h,
                        ColorModel model,
			int[] pixels,
			int off,
			int scansize) {
    int x, y, x2, y2, sx, sy;

    x2 = x1+w;
    y2 = y1+h;
    sy = off;

    for (y=y1; y<y2; y++) {
      sx = sy;
      for (x=x1; x<x2; x++) {
	transparent[x][y] = false;//model.getAlpha(Math.abs(pixels[sx])) > 0;
        pixelTable[x][y] = model.getRGB(pixels[sx++]);
      }
      sy += scansize;
    }
  }

  public void setProperties(java.util.Hashtable ignored) {
    // we currently ignore any properties
  }

  /**
   * Returns the dimension of the image
   */
  public Dimension getDimensions() {
    return dimension;
  }

  public boolean isTransparent(int x, int y) {
    return transparent[x][y];
  }

  public int getPixelAt(int x, int y) {
    return pixelTable[x][y];
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private boolean			complete = false;
  private Dimension			dimension;
  private int[][]			pixelTable;
  private boolean[][]			transparent;
}
