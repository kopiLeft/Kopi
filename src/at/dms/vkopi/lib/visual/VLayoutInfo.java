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
 * $Id: VLayoutInfo.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

public class VLayoutInfo {
  public VLayoutInfo(int center, int width, int height, int column, int columnEnd) {
  this.center = center;
  this.width = width;
  this.height = height;
  this.column = column;
  this.columnEnd = columnEnd;
  }

  public int center;
  public int width;
  public int height;
  public int column;
  public int columnEnd;
}
