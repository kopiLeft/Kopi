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
 * $Id: DefaultFilter.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.CWarning;

/**
 * This is the default warning filter
 * public class MyWarningFilter implements at.dms.kopi.comp.kjc.DefaultFilter {
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
public class DefaultFilter implements at.dms.compiler.base.WarningFilter {

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
