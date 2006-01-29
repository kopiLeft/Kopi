/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.form;

/**
 * A class to specifie alignment in KopiLayout
 */
public class KopiAlignment {

  public boolean		ALG_LEFT	= false;
  public boolean		ALG_RIGHT	= true;

  public KopiAlignment(int x, int y, int width, boolean alignRight) {
    this(x, y, width, alignRight, false);
  }

  public KopiAlignment(int x, int y, int width, boolean alignRight, boolean useAll) {
    this.x = x;
    this.y = y;
    this.alignRight = alignRight;
    this.width = width;
    this.useAll = useAll;
  }

  /* package */ int		x;		// position in x
  /* package */ int		y;		// position in y
  /* package */ int		width;		// number of column
  /* package */ boolean		alignRight;	// position in alignRight
  /* package */ boolean		useAll;         // use the whole possible width of the column
}
