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

package org.kopi.vkopi.comp.base;

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
