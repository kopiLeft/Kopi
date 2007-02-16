/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
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
 * $Id$
 */

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.CWarning;

/**
 * This is the default warning filter
 * public class MyWarningFilter implements com.kopiright.kopi.comp.kjc.DefaultFilter {
 *
 *  // ----------------------------------------------------------------------
 *  // FILTER
 *  // ----------------------------------------------------------------------
 *
 *
 *  * Filters a warning
 *  * @param	warning		a warning to be filtred
 *  * @return	FLT_REJECT, FLT_FORCE, FLT_ACCEPT
 *  *
 *  * This filter accepts unused catch parameters if they are prefixed with an underscore
 *  *
 * public int filter(CWarning warning) {
 *    if (warning.hasDescription(KjcMessages.UNUSED_CATCH_PARAMETER)) {
 *      if (!warning.getParams()[0].toString().startsWith("_")) {
 *	// catch (Exception unusedParam) {...
 *	return FLT_FORCE;
 *      } else {
 *	// catch (Exception _unusedParam) {...
 *	return FLT_REJECT;
 *      }
 *    }
 *
 *    return FLT_ACCEPT;
 *  }
 */
public class DefaultFilter implements com.kopiright.compiler.base.WarningFilter {

  // ----------------------------------------------------------------------
  // FILTER
  // ----------------------------------------------------------------------

  /**
   * Filters a warning
   * @param	warning		a warning to be filtred
   * @return	FLT_REJECT, FLT_FORCE, FLT_ACCEPT
   */
  public int filter(CWarning warning) {
    if (warning.hasDescription(KjcMessages.UNUSED_PARAMETER)
	|| warning.hasDescription(KjcMessages.CONSTANT_VARIABLE_NOT_FINAL)
	|| warning.hasDescription(KjcMessages.UNUSED_CATCH_PARAMETER)) {
      return FLT_REJECT;
    } else {
      return FLT_ACCEPT;
    }
  }
}
