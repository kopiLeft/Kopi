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
 * $Id: VKInsertParser.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import java.io.File;

//!!! graf 990821 WHAT A KLUDGE !!!
public interface VKInsertParser {

  /**
   * Parses the given file.
   * side effect: increment error number
   * @param	file		the name of the file (assert exists)
   * @return	the compilation unit defined by this file
   */
  VKInsert parseInsert(File file, VKEnvironment environment);
}
