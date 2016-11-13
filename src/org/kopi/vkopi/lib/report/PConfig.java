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

package org.kopi.vkopi.lib.report;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PConfig implements Serializable {

  /**
   * constructor
   */
  public PConfig() {
    papertype	= org.kopi.vkopi.lib.util.PPaperType.PPT_A4.getCode();
    paperlayout	= "Landscape";

    topmargin	= 30;
    bottommargin= 30;
    leftmargin	= 30;
    rightmargin	= 30;
    headermargin= 5;
    footermargin= 10;

    visibleRows	= true;

    order		= Constants.SUM_AT_TAIL;
    groupFormfeed	= false;

    grid_H		= 0.1;
    grid_V		= 0.1;
    border		= 1.0;
    reportScale         = MIN_REPORT_SCALE;
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

  // Cell to be displayed
  public boolean		visibleRows;	// true if only visible rows must be displayed

  // Options
  public int			order;		// order to used for printing
  public boolean		groupFormfeed;	// cut the form at each big nivel

  // Grid and border
  public double			grid_H;		// horizontal grid
  public double			grid_V;		// vertical grid
  public double			border;		// border size
  public double                 reportScale;    // report scale
  
  public static final double    MIN_REPORT_SCALE = 3;
  public static final double    MAX_REPORT_SCALE = 12;
}
