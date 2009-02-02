/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.compiler.base;

/**
 * This interface filters warning
 */
public interface WarningFilter {

  /**
   * Rejects the warning, it will not be displayed
   */
  int	FLT_REJECT	= 0;
  /**
   * Forces the warning to be displayed
   */
  int	FLT_FORCE	= 1;
  /**
   * Does not decide, the warning will be displayed depending on the user
   * options (warning level, langage specification)
   */
  int	FLT_ACCEPT	= 2;

  // ----------------------------------------------------------------------
  // FILTER
  // ----------------------------------------------------------------------

  /**
   * Filters a warning
   * @param	warning		a warning to be filtred
   * @return	FLT_REJECT, FLT_FORCE, FLT_ACCEPT
   */
  int filter(CWarning warning);
}
