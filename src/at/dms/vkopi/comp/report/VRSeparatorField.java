/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: VRSeparatorField.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.comp.report;

import at.dms.compiler.base.TokenReference;
import at.dms.vkopi.comp.base.VKCommand;
import at.dms.vkopi.comp.base.VKTrigger;

public class VRSeparatorField extends VRField {

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   */
  public VRSeparatorField(TokenReference where)
  {
    super(where,
	  "S",
	  "S",
	  null,
	  new VRSeparatorFieldType(where),
	  1,
	  1,
	  null,
	  new VKCommand[0],
	  new VKTrigger[0]);
  }
}
