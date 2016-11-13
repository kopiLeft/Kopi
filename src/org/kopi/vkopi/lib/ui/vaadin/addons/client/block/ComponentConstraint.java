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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.io.Serializable;

/**
 * The child component layout constraint.
 */
@SuppressWarnings("serial")
public class ComponentConstraint implements Serializable {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Default constructor
   */
  public ComponentConstraint() {}
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll  Use the whole possible width of the column ?
   */
  public ComponentConstraint(int x, int y, int width, boolean alignRight, boolean useAll) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.alignRight = alignRight;
    this.useAll = useAll;
  }
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   * @param alignRight Is it right aligned ?
   */
  public ComponentConstraint(int x, int y, int width, boolean alignRight) {
    this(x, y, width, alignRight, false);
  }
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   */
  public ComponentConstraint(int x, int y, int width) {
    this(x, y, width, false, false);
  }
  
  @Override
  public String toString() {
    return "[ X = " + x + ", Y = " + y + ", width = " + width + "]";
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * Position in x
   */
  public int				x;
  
  /**
   * Position in y
   */
  public int				y;
  
  /**
   * Number of column
   */
  public int				width;
  
  /**
   * Position in alignRight
   */
  public boolean			alignRight;
  
  /**
   * Use the whole possible width of the column
   */
  public boolean			useAll;
}
