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
 * $Id: ClassReader.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.kopi.comp.kjc;

//import at.dms.compiler.base.UnpositionedError;

/**
 * Interface for Java-Classfile readers.
 */
public interface ClassReader {

  /**
   * Loads class definition from .class file
   */
  CClass loadClass(TypeFactory typeFactory, String name);

  /**
   * @return  false if name exists for source class as source class
   *          in an other file
   * @param CClass a class to add (must be a CSourceClass)
   */
  boolean addSourceClass(CSourceClass cl);

  /**
   * @return a class file that contain the class named name
   * @param name the name of the class file
   */
  boolean hasClassFile(String name);

  SignatureParser getSignatureParser();
 
  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  boolean packageExists(String name);
}
