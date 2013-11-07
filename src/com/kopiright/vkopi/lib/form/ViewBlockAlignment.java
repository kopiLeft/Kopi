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

package com.kopiright.vkopi.lib.form;

/**
 * A class to specify alignment in KopiLayout
 */
public class ViewBlockAlignment {

  //------------------------------------------------------------
  // CONSTRUCTOR
  //------------------------------------------------------------

  public ViewBlockAlignment(UForm formView, BlockAlignment align) {
    this.align = align;
    this.formView = formView;
  }

  //------------------------------------------------------------
  // ACCESSORS
  //------------------------------------------------------------

  public boolean isChart() {
    return align.isChart();
  }

  public boolean isAligned(int x) {
    return align.isAligned(x);
  }

  public int getMinStart(int x) {
    int         	target;
    UBlock      	view;

    x--; // we want to align middle
    target = align.getTargetAt(x);
    view = formView.getBlockView(align.getBlock());

//     if (x >= 0 && x < targets.length && targets[x] != -1) {
    if (target != -1) {
      if (view == null) {
	return 0;
      }
      int        pos;

      pos = isChart() ? target : target * 2 + 1;
      return view.getColumnPos(pos);
    }
    return 0;
  }

  public int getLabelMinStart(int x) {
    int         	target;
    UBlock      	view;

    x--; // we want to align middle
    target = align.getTargetAt(x);
    view = formView.getBlockView(align.getBlock());

//     if (x >= 0 && x < targets.length && targets[x] != -1) {
    if (target != -1) {
      if (view == null) {
	return 0;
      }
      return view.getColumnPos(target*2);
    }
    return 0;
  }

  //------------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------------

  /*package*/ UForm                 		formView;
  /*package*/ BlockAlignment        		align;
}
