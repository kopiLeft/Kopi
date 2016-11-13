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

/**
 * A point representing a location in {@code (x,y)} coordinate space,
 * specified in integer precision.
 */
@SuppressWarnings("serial")
public class Point implements Serializable {

  /**
   * Constructs and initializes a point at the origin 
   * (0,&nbsp;0) of the coordinate space. 
   * @since       1.1
   */
  public Point() {
    this(0, 0);
  }

  /**
   * Constructs and initializes a point with the same location as
   * the specified <code>Point</code> object.
   * @param       p a point
   * @since       1.1
   */
  public Point(Point p) {
    this(p.x, p.y);
  }

  /**
   * Constructs and initializes a point at the specified 
   * {@code (x,y)} location in the coordinate space. 
   * @param x the X coordinate of the newly constructed <code>Point</code>
   * @param y the Y coordinate of the newly constructed <code>Point</code>
   * @since 1.0
   */
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Determines whether or not two points are equal. Two instances of
   * <code>Point2D</code> are equal if the values of their 
   * <code>x</code> and <code>y</code> member fields, representing
   * their position in the coordinate space, are the same.
   * @param obj an object to be compared with this <code>Point2D</code>
   * @return <code>true</code> if the object to be compared is
   *         an instance of <code>Point2D</code> and has
   *         the same values; <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point pt = (Point)obj;
      return (x == pt.x) && (y == pt.y);
    }
    return super.equals(obj);
  }

  /**
   * Returns a string representation of this point and its location 
   * in the {@code (x,y)} coordinate space. This method is 
   * intended to be used only for debugging purposes, and the content 
   * and format of the returned string may vary between implementations. 
   * The returned string may be empty but may not be <code>null</code>.
   * 
   * @return  a string representation of this point
   */
  public String toString() {
    return getClass().getName() + "[x=" + x + ",y=" + y + "]";
  }
  
  //-------------------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------------------
  
  /**
   * The X coordinate of this <code>Point</code>.
   * If no X coordinate is set it will default to 0.
   */
  public int 				x;

  /**
   * The Y coordinate of this <code>Point</code>. 
   * If no Y coordinate is set it will default to 0.
   */
  public int 				y;
}
