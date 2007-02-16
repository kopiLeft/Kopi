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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a method context during check
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CInitializerContext extends CMethodContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * CInitializerContext
   * @param	parent		the parent context
   * @param	self		the corresponding method interface
   */
  public CInitializerContext(CClassContext parent, KjcEnvironment environment, CMethod self, JFormalParameter[] parameters) {
    super(parent, environment, self, parameters);
  }

  /**
   * Verify that all checked exceptions are defined in the throw list
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void close(TokenReference ref) throws PositionedError {
    if (! getCMethod().isStatic()) {
      ((CClassContext)parent).setInitializerInfo(fieldInfo);
      // 25.09.01 throws in initializer
      //      if (getCMethod().getOwner().isAnonymous()) {
        getCMethod().setThrowables(getThrowables());
        //      }
      adoptFieldInfos((CClassContext)parent);
    } else {
      adoptFieldInfos((CClassContext)parent);
    }

    super.close(ref);
  }

  public void adoptFieldInfos(CClassContext target) {
    int		parentPosition = target.getCClass().getFieldCount();

    if (fieldInfo != null) {
      for (int i = 0; i < parentPosition; i++) {
	int	info = getFieldInfo(i);
	
	if (info != 0) {
	  target.setFieldInfo(i, info);
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // FIELD STATE
  // ----------------------------------------------------------------------

  /**
   * @param	var		the definition of a field
   * @return	all informations we have about this field
   */
  public int getFieldInfo(int index) {
    if (fieldInfo == null) {
      return parent.getFieldInfo(index);
    } else {
      return fieldInfo.getInfo(index);
    }
  }

  /**
   * @param	index		The field position in method array of local vars
   * @param	info		The information to add
   *
   * We make it a local copy of this information and at the end of this context
   * we will transfert it to the parent context according to controlFlow
   */
  public void setFieldInfo(int index, int info) {
    if (fieldInfo == null) {
      fieldInfo = (CVariableInfo)getFieldInfo().clone();
    }
    fieldInfo.setInfo(index, info);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CVariableInfo		fieldInfo;
}
