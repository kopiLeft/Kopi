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

package org.kopi.vkopi.lib.form;

import java.io.Serializable;

/**
 * A class to specifie alignment in KopiLayout
 */
@SuppressWarnings("serial")
public class BlockAlignment implements Serializable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>BlockAlignment</code> instance.
   * @param ori The original block to be aligned with.
   * @param targets The alignment targets.
   */
  public BlockAlignment(VBlock ori, int[] targets) {
    this.targets = targets;
    this.ori = ori;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @SuppressWarnings("deprecation")
  public boolean isChart() {
    return (ori != null) && ori.isChart();
  }

  public boolean isAligned(int x) {
    x--;
    if (x >= 0 && x < targets.length && targets[x] != -1) {
      return true;
    }
    return false;
  }

  public int getTargetAt(int x) {
    if (x < 0 || x >= targets.length) {
      return -1;
    } else {
      return targets[x];
    }
  }

  /**
   * Returns the original block to be aligned with. 
   * @return The original block to be aligned with. 
   */
  public VBlock getBlock() {
    return ori;
  }
  
  /**
   * Returns the alignment targets.
   * @return The alignment targets.
   */
  public int[] getTartgets() {
    return targets;
  }

  // MOVED TO DISPLAY
//   public int getMinStart(int x) {
//     x--; // we want to align middle
//     if (x >= 0 && x < targets.length && targets[x] != -1) {
//       if (ori == null || ori.getDisplay() == null) {
// 	return 0;
//       }
//       int        pos;

//       pos = isChart() ? targets[x] : targets[x] * 2 + 1;
//       return ((DBlock) ori.getDisplay()).getColumnPos(pos);
//     }
//     return 0;
//   }

//   public int getLabelMinStart(int x) {
//     x--; // we want to align middle
//     if (x >= 0 && x < targets.length && targets[x] != -1) {
//       if (ori == null || ori.getDisplay() == null) {
// 	return 0;
//       }
//       return ((DBlock) ori.getDisplay()).getColumnPos(targets[x]*2);
//     }
//     return 0;
//   }

  // ----------------------------------------------------------------------
  // DATA MEMBER
  // ----------------------------------------------------------------------

  public static final boolean	ALG_LEFT	= false;
  public static final boolean	ALG_RIGHT	= true;

  /* package */ int[]		targets;        //!!!FINAL 020729
  /* package */ VBlock		ori;            //!!!FINAL 020729
}
