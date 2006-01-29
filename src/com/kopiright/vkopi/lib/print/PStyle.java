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

package com.kopiright.vkopi.lib.print;

public abstract class PStyle {

  /**
   * Construct a style
   */
  public PStyle(String ident) {
    this.ident = ident;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this style
   */
  public String getIdent() {
    return ident;
  }

  public void setOwner(PPage owner) {
    this.owner = owner;
  }

  public PPage getOwner() {
    return owner;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private String	ident;
  private PPage		owner;

  public static final	int	ALN_DEFAULT	= -1;
  public static final	int	ALN_LEFT	= 0;
  public static final	int	ALN_RIGHT	= 1;
  public static final	int	ALN_CENTER	= 2;
  public static final	int	ALN_JUSTIFIED	= 3;
}
