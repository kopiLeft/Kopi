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
 * $Id: VCCDepthFirstCircuitN.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

public abstract class VCCDepthFirstCircuitN extends VCalculateColumn {

  /**
   * Intialisation of the calculation
   */
  public void init() {
  }

  /**
   * Evaluates nodes
   */
  public Object evalNode(VReportRow row, int column) {
    return null;
  }

  /**
   * Evaluates leafs
   */
  public Object evalLeaf(VReportRow row, int column) {
    return null;
  }

  /**
   * Add calculated data into the report row
   */
  public void calculate(VGroupRow tree, int column) {
    if (tree.getLevel() > 1) {
      int	childCount = tree.getChildCount();

      for (int i = 0; i < childCount; i++) {
	calculate((VGroupRow)tree.getChildAt(i), column);
      }
    } else {
      int	childCount = tree.getChildCount();

      for (int i = 0; i < childCount; i++) {
	Object evaluatedObject = evalLeaf((VBaseRow)tree.getChildAt(i),
					  column);
	if (evaluatedObject != null) {
	  ((VBaseRow)tree.getChildAt(i)).setValueAt(column, evaluatedObject);
	}
      }
    }

    tree.setValueAt(column, evalNode(tree, column));
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
}
