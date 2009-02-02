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

package com.kopiright.vkopi.lib.form;

import com.kopiright.vkopi.lib.visual.VException;

/**
 * This class implements predefined triggers
 */

public class Triggers implements VConstants {
  /*
   * ----------------------------------------------------------------------
   * FORM-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Returns true iff form is changed.
   */
  public static boolean isChanged(VForm f) {
    return f.isChanged();
  }

  /*
   * ----------------------------------------------------------------------
   * BLOCK-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Returns always false (= unchanged).
   */
  public static boolean ignoreChanges(VBlock b) {
    return false;
  }

  /**
   * Returns true iff first block of form is not in query mode
   */
  public static boolean mainBlockInsertable(VBlock b) {
    return b.getForm().getBlock(0).getMode() != MOD_QUERY;
  }

  /*
   * ----------------------------------------------------------------------
   * FIELD-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Fetches fields of lookup table with key current field.
   * This trigger is normally called as POSTCHG
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may occur if
   *	next record was deleted by tierce
   */
  public static void fetchLookup(VField f) throws VException {
    f.getBlock().fetchLookup(f);
  }
}
