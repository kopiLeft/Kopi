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
public class CBinaryTypeContext implements CTypeContext{

  public CBinaryTypeContext(ClassReader classReader, TypeFactory typeFactory) {
    this(classReader, typeFactory, null, null, null, false);
  }

  public CBinaryTypeContext(ClassReader classReader, TypeFactory typeFactory, CTypeContext parent, CMember owner) {
    this(classReader, typeFactory, parent, owner, null, !owner.isStatic());
  }

  public CBinaryTypeContext(ClassReader classReader, 
                            TypeFactory typeFactory, 
                            CTypeContext parent, 
                            CTypeVariable[] typeVariables, 
                            boolean parentLookup) {

    this(classReader, typeFactory, parent, null, typeVariables, parentLookup);
  }

  private CBinaryTypeContext(ClassReader classReader, 
                             TypeFactory typeFactory, 
                             CTypeContext parent, 
                             CMember owner, 
                             CTypeVariable[] typeVariables, 
                             boolean parentLookup) {
    this.classReader = classReader;
    this.typeFactory = typeFactory;
    this.parent = parent;
    this.owner = owner;
    this.typeVariables = typeVariables;
    this.parentLookup = parentLookup;
  }

  /**
   * @return the TypeFactory
   */
  public TypeFactory getTypeFactory() {
    return typeFactory;
  }

  /**
   * @return the Object used to read class files.
   */
  public ClassReader getClassReader() {
    return classReader;
  }

  /**
   * @return the user of the CClass, which wants to access it.
   */
  public CClassContext getClassContext(){
    if (parent == null) {
      return null;
    } else {
      return parent.getClassContext();
    }
  }

  /**
   * @param caller the user of the CClass, which wants to access it.
   * @param name the name of the class
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
    if (parent == null) {
      return null;
    } else {
      return parent.lookupClass(caller, name);
    }
  }


  /**
   * Returns true if warnings should be provided if 
   * something deprecated is used.
   */
  public boolean showDeprecated() {
    if (parent == null) {
      return false;
    } else {
      return parent.showDeprecated();
    }
  }

  /**
   * called if somewhere something deprecated used.
   */
  public void setDeprecatedUsed() {
    if (parent != null) {
      parent.showDeprecated();
    }
    // else:
    // if a binary class use some binary please
    // show no message
  }

  /**
   * Reports a semantic error detected during analysis.
   *
   * @param	trouble		the error to report
   */
  public void reportTrouble(PositionedError trouble) {
    typeFactory.reportTrouble(trouble);
  }

  /**
   * Searches the class, interface and Method to locate declarations of TV's that are
   * accessible.
   * 
   * @param	ident		the simple name of the field
   * @return	the TV definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CTypeVariable lookupTypeVariable(String ident)
    throws UnpositionedError
  {
    if (owner != null) {
      return owner.lookupTypeVariable(ident);
    } else if (typeVariables != null) {
      for (int i = 0; i < typeVariables.length; i++) {
        if (typeVariables[i].getIdent() == ident) {
          return typeVariables[i];
        }
      }
    }
    if (parentLookup && (parent != null)) {
      return parent.lookupTypeVariable(ident);
    } else {
      return null;
    }
  }

  public SignatureParser getSignatureParser() {
    return classReader.getSignatureParser();
  }

  private TypeFactory           typeFactory;
  private ClassReader           classReader; 
  private CMember               owner;
  private CTypeContext          parent;
  private CTypeVariable[]       typeVariables;
  private boolean               parentLookup;
}
