/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: PSize.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.print;

/**
 * A position inside a page or a block
 */
public class PSize {

  public PSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public PSize(int width, String height) {
    this.width = width;
    this.heightStr = height;
  }

  public PSize(String width, int height) {
    this.widthStr = width;
    this.height = height;
  }

  public PSize(String width, String height) {
    this.widthStr = width;
    this.heightStr = height;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  public void preparePrint(PBlock block) {
    if (widthStr != null) {
      if (widthStr.equals("PAGE_WIDTH")) {
	width = block.getPage().getWidth() - 2 * block.getPage().getBorder();
      } else if (widthStr.equals("MAX")) {
	width = 1000; // !!!
      }
    }
    if (heightStr != null) {
      if (heightStr.equals("PAGE_HEIGHT")) {
	height = block.getPage().getHeight() - 2 * block.getPage().getBorder();
      } else if (heightStr.equals("MAX")) {
	height = 1000;
      }
    }
  }

  public void set(float width, float height) {
    this.width = width;
    this.height = height;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	widthStr;
  private String	heightStr;

  private float width;
  private float height;
}
