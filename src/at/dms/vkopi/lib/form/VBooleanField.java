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
 * $Id: VBooleanField.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import at.dms.vkopi.lib.util.Message;

public class VBooleanField extends VBooleanCodeField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */
  /**
   * Constructor
   */
  public VBooleanField() {
    super(booleanNames, booleanCodes);
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("boolean-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Boolean");
  }

  /**
   * return the text value
   */
  public static String toText(Boolean o) {
    return booleanNames[o.booleanValue() ? 0 : 1];
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private static String[]	booleanNames = { Message.getMessage("true"), Message.getMessage("false") };
  private static Boolean[]	booleanCodes = { Boolean.TRUE, Boolean.FALSE };
}
