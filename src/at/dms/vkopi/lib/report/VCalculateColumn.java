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
 * $Id: VCalculateColumn.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

public abstract class VCalculateColumn {

  /**
   * Intialisation of the calculation
   */
  public abstract void init();

  /**
   * Evaluates nodes
   */
  public Object evalNode(VReportRow row, int column) {
    return null;
  }

  /**
   * Add calculated data into the report row
   */
  public abstract void calculate(VGroupRow tree, int column);
}
