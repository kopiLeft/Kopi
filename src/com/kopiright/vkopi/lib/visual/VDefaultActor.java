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

package com.kopiright.vkopi.lib.visual;

@SuppressWarnings("serial")
public class VDefaultActor extends VActor {

  public VDefaultActor(int code,
		       String menuIdent,
                       String menuSource,
		       String actorIdent,
		       String actorSource,
		       String iconName,
		       int acceleratorKey,
		       int acceleratorModifier)
  {
    super(menuIdent, menuSource, actorIdent, actorSource, iconName, acceleratorKey, acceleratorModifier);
    this.code = code;
  }

  /**
   * Return the code of this default command
   */
  public int getCode() {
    return code;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private int						code;
}
