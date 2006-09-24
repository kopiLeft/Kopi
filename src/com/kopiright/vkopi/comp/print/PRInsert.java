/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.print;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.util.base.NotImplementedException;
import com.kopiright.vkopi.comp.base.*;
import com.kopiright.vkopi.comp.base.VKEnvironment;

/**
 * This class represents the definition of a form
 */
public class PRInsert extends VKInsert implements com.kopiright.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param name		the name of this form
   * @param superName		the type of the form
   */
  public PRInsert(TokenReference where, VKEnvironment environment) {
    super(where, environment, null);

    this.cunit = new CParseCompilationUnitContext();
    this.clazz = new CParseClassContext();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this class
   */
  public String getIdent() {
    int		start = Math.max(0,
				 Math.max(1 + getTokenReference().getFile().lastIndexOf('/'),
					  1 + getTokenReference().getFile().lastIndexOf('\\')));

    int		dot = getTokenReference().getFile().lastIndexOf('.');

    return getTokenReference().getFile().substring(start, dot);
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    if (coll == null) {
      coll = new PRDefinitionCollector(environment.getInsertDirectories());
    }

    return coll;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    context.setMode(true);
    context.setClassContext(clazz);
    context.setFullName(cunit.getPackageName().getName() + "/" + getIdent());

    getDefinitionCollector().checkCode(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns a collector for definitiion
   */
  public CParseCompilationUnitContext getCompilationUnitContext() {
    return cunit;
  }

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompilationUnit genCode(Compiler compiler) {
    // BUILD THE CLASS
    JClassDeclaration		self = new JClassDeclaration(getTokenReference(),
							     ACC_PUBLIC,
							     getIdent(),
                                                             CTypeVariable.EMPTY,
							     null,
							     CReferenceType.EMPTY,
							     clazz.getFields(),
							     clazz.getMethods(),
							     clazz.getInnerClasses(),
							     clazz.getBody(),
							     null,
							     null);
    cunit.addTypeDeclaration(environment.getClassReader(), self);

    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/xkopi/lib/type", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    JCompilationUnit compilUnit = new JCompilationUnit(getTokenReference(),
                                                       environment,
						       cunit.getPackageName(),
						       cunit.getPackageImports(),
						       cunit.getClassImports(),
						       cunit.getTypeDeclarations());

    return compilUnit;
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CParseCompilationUnitContext		cunit;
  private CParseClassContext			clazz;
  private VKDefinitionCollector			coll;
}
