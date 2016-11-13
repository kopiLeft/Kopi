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

import org.kopi.vkopi.lib.visual.VlibProperties;

@SuppressWarnings("serial")
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
    super("boolean", GENERAL_LOCALIZATION_RESOURCE, booleanNames, booleanCodes);
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return VlibProperties.getString("boolean-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Boolean");
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
  private static final String   GENERAL_LOCALIZATION_RESOURCE = "org/kopi/vkopi/lib/resource/General";
  private static String[]	booleanNames = { "true", "false" };
  private static Boolean[]	booleanCodes = { Boolean.TRUE, Boolean.FALSE };
}
