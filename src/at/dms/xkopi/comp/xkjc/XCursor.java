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
 * $Id: XCursor.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import java.util.Enumeration;
import java.util.Hashtable;

import at.dms.bytecode.classfile.FieldInfo;
import at.dms.compiler.base.Compiler;
import at.dms.kopi.comp.kjc.*;

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
