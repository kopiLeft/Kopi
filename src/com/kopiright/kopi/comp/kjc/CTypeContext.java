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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.base.PositionedError;

/**
 * Context type checking. Also used while loading binary classes.
 */
public interface CTypeContext {

  /**
   * @return the TypeFactory
   */
  TypeFactory getTypeFactory();

  /**
   * @return the Object used to read class files.
   */
  ClassReader getClassReader();


  /**
   * @return the user of the CClass, which wants to access it.
   */
  CClassContext getClassContext();

  /**
   * @param caller the user of the CClass, which wants to access it.
   * @param name the name of the class
   */
  CClass lookupClass(CClass caller, String name) throws UnpositionedError ;

  /**
   * Searches the class, interface and Method to locate declarations of TV's that are
   * accessible.
   * 
   * @param	ident		the simple name of the field
   * @return	the TV definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  CTypeVariable lookupTypeVariable(String ident) throws UnpositionedError;

  /**
   * Reports a semantic error detected during analysis.
   *
   * @param	trouble		the error to report
   */
  void reportTrouble(PositionedError trouble);

  /**
   * Returns true if warnings should be provided if 
   * something deprecated is used.
   */
  boolean showDeprecated();

  /**
   * called if somewhere something deprecated used.
   */
  void setDeprecatedUsed();
}
