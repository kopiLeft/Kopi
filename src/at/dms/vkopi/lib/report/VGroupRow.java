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
 * $Id: VGroupRow.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

public class VGroupRow extends VReportRow {

  /**
   * Constructs a description for a grouping row
   *
   * @param	report		the report
   */
  public VGroupRow(Object[] data, int level) {
    super(data);

    this.level = level;
  }

  /**
   * Sets data row
   *
   * @param	column		the index of the column
   * @param	value		the value for the column
   */
  public void setValueAt(int column, Object value) {
    data[column] = value;
  }

  /**
   * Return the level of the node in the grouping tree
   */
  public int getLevel() {
    return level;
  }

  /**
   * Returns true iff all the child nodes of the level generation are visible.
   *
   * @param	level		level to test
   */
  public boolean isUnfolded(int level) {
    for (int i = 0; i < getChildCount(); i++) {
      VReportRow child = (VReportRow)getChildAt(i);

      if (child.getLevel() > level) {
	if (((VGroupRow)child).isUnfolded(level)) {
	  return true;
	}
      } else {
	if (child.isVisible()) {
	  return true;
	}
      }
    }

    return false;
  }

  /**
   * Sets child node of the level generation to visible
   *
   * @param level level to be set visible
   */
  public void setChildNodesVisible(int level) {
    for (int i = 0; i < getChildCount(); i++) {
      VReportRow	child = (VReportRow)getChildAt(i);

      child.setVisible(true);

      if (getLevel() > level + 1) {
	((VGroupRow)child).setChildNodesVisible(level);
      }
    }
  }

  /**
   * Sets child node of the level generation to invisible
   *
   * @param level level to be set visible
   */
  public void setChildNodesInvisible(int level) {
    for (int i = 0; i < getChildCount(); i++) {
      VReportRow	child = (VReportRow)getChildAt(i);

      if (child.getLevel() > level) {
	((VGroupRow)child).setChildNodesInvisible(level);
      } else if (child instanceof VGroupRow) {
	((VGroupRow)child).setChildNodesInvisible();
      } else {
	child.setVisible(false);
      }
    }
  }

  private void setChildNodesInvisible() {
    setVisible(false);
    if (getLevel() > 0) {
      for (int i = 0; i < getChildCount(); i++) {
	VReportRow	row = (VReportRow)getChildAt(i);
	if (row instanceof VGroupRow) {
	  ((VGroupRow)row).setChildNodesInvisible();
	} else {
	  row.setVisible(false);
	}
      }
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final int		level;
}
