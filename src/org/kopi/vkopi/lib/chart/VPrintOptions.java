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

package org.kopi.vkopi.lib.chart;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VPrintOptions implements Serializable {

  /**
   * constructor
   */
  public VPrintOptions() {
    papertype	= org.kopi.vkopi.lib.util.PPaperType.PPT_A4.getCode();
    paperlayout	= "Landscape";

    topmargin	= 30;
    bottommargin= 30;
    leftmargin	= 30;
    rightmargin	= 30;
    headermargin= 5;
    footermargin= 10;
    imageWidth = 900;
    imageHeight = 500;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  // Paper
  public int			papertype;	// differents formats of paper
  public String			paperlayout;	// the layout of the paper (Portrait or Landscape)

  // Margins
  public int			topmargin;	// top sheet margin in points
  public int			bottommargin;	// bottom sheet margin in points
  public int			leftmargin;	// left sheet margin in points
  public int			rightmargin;	// right sheet margin in points
  public int			headermargin;	// header sheet margin in points
  public int			footermargin;	// footer sheet margin in points
  
  public int			imageWidth;	// The image width to be used when exporting as image format.
  public int			imageHeight;	// The image height to be used when exporting as image format.
}
