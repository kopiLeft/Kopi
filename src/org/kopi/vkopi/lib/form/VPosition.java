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

@SuppressWarnings("serial")
public class VPosition implements VConstants, Serializable {

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------

  /**
   * Constructs a list column.
   */
  public VPosition(int line, int lineEnd, int column, int columnEnd) {
    this(line, lineEnd, column, columnEnd, -1);
  }

  /**
   * Constructs a list column.
   */
  public VPosition(int line, int lineEnd, int column) {
    this(line, lineEnd, column, -1, -1);
  }
    public VPosition(int line, int lineEnd, int column, int columnEnd, int chartPos) {
    this.line = line;
    this.lineEnd = lineEnd;
    this.column = column;
    this.columnEnd = columnEnd;
    this.chartPos = chartPos;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public final int line;
  public final int lineEnd;
  public final int column;
  public final int columnEnd;
  public final int chartPos;
}
