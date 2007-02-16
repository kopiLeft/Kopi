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

package com.kopiright.xkopi.comp.xkjc;

import com.kopiright.kopi.comp.kjc.CClass;
import com.kopiright.kopi.comp.kjc.CReferenceType;

/**
 * This class represents the exported members of a class (inner classes, methods and fields)
 */
public class XCursor extends CClass {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class export from file
   */
  public XCursor(CClass owner,
		 String sourceFile,
		 int modifiers,
		 String ident,
		 String qualifiedName,
		 CReferenceType superClass,
		 boolean deprecated)
  {
    super(owner, sourceFile, modifiers, ident, qualifiedName, superClass, deprecated, false);
  }
}
