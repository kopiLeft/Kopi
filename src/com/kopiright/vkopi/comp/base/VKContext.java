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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CParseClassContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.JTypeNameExpression;

public class VKContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VKContext(VKContext parent,
		   boolean isInsertFile,
		   CParseClassContext clazz,
		   String ident) {
    this.compiler = parent.compiler;
    this.topLevel = parent.topLevel;
    this.isInsertFile = isInsertFile;
    this.clazz = clazz;
    this.fullName = ident.intern();
    this.allowSQLInTriggers = parent.allowSQLInTriggers;
  }

  /**
   * Constructor
   */
  public VKContext(Compiler compiler, VKTopLevel topLevel, boolean allowSQLInTriggers) {
    this.compiler = compiler;
    this.topLevel = topLevel;
    this.allowSQLInTriggers = allowSQLInTriggers;
  }

  public void setMode(boolean isInsertFile) {
    this.isInsertFile = isInsertFile;
  }

  public void setClassContext(CParseClassContext clazz) {
    this.clazz = clazz;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName.intern();
  }

  public void setTriggers(int[] TRG_TYPES) {
    this.TRG_TYPES = TRG_TYPES;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public VKTopLevel getTopLevel() {
    return topLevel;
  }

  public CParseClassContext getClassContext() {
    return clazz;
  }

  public boolean isInsertFile() {
    return isInsertFile;
  }

  public JExpression getAccess(TokenReference ref, boolean isStatic) {
    JExpression		result;

    result = new JTypeNameExpression(ref, CReferenceType.lookup(fullName));
    if (!isStatic) {
      result = new JThisExpression(ref, result);
    }
    return result;
  }

  public String getFullName() {
    return fullName;
  }

  public int[] getTriggers() {
    return TRG_TYPES;
  }

  // ----------------------------------------------------------------------
  // OPTION ACCESS
  // ----------------------------------------------------------------------

  public boolean allowSQLInTriggers() {
    return allowSQLInTriggers;
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Add an error into the list and eat it
   * This method should be called after a try catch block after catching exception
   * or directly without exception thrown
   * @param	error		the error
   */
  public void reportTrouble(PositionedError trouble) {
    compiler.reportTrouble(trouble);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Compiler		compiler;
  private final VKTopLevel		topLevel;

  private boolean			isInsertFile;
  private CParseClassContext		clazz;
  private String			fullName;
  private boolean                       allowSQLInTriggers;
  private int[]				TRG_TYPES = com.kopiright.vkopi.lib.form.VConstants.TRG_TYPES;
}
