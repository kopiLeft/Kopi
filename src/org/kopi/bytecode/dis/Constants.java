/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.bytecode.dis;

/**
 * Defines disassembler constants
 */
public interface Constants {

  // ----------------------------------------------------------------------
  // OPTIONS
  // ----------------------------------------------------------------------

  int OPT_SORT_MEMBERS		= (1 << 0);
  int OPT_NO_CODE		= (1 << 1);
  int OPT_SHOW_STACK		= (1 << 2);
  int OPT_STDOUT		= (1 << 3);
}
