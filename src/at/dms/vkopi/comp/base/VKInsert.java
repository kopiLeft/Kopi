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
 * $Id: VKInsert.java,v 1.2 2004/09/29 16:34:11 taoufik Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CParseClassContext;
import at.dms.kopi.comp.kjc.CParseCompilationUnitContext;
import at.dms.kopi.comp.kjc.CTypeVariable;
import at.dms.kopi.comp.kjc.JClassDeclaration;
import at.dms.kopi.comp.kjc.JClassImport;
import at.dms.kopi.comp.kjc.JCompilationUnit;
import at.dms.kopi.comp.kjc.JPackageImport;
import at.dms.util.base.NotImplementedException;

import at.dms.vkopi.comp.base.VKEnvironment;

/**
 * This class represents the definition of a form
 */
public class VKInsert
  extends VKPhylum
  implements at.dms.vkopi.lib.form.VConstants, at.dms.kopi.comp.kjc.Constants
{

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
  public VKInsert(TokenReference where,
                  VKEnvironment environment)
  {
    super(where);

    this.environment = environment;
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
      coll = new VKDefinitionCollector(environment.getInsertDirectories());
      coll.setInsert(this);
    }

    return coll;
  }

  /**
   * Returns a collector for definitiion
   */
  public CParseCompilationUnitContext getCompilationUnitContext() {
    return cunit;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public VKContext getContext(VKContext context) throws PositionedError {
    return new VKContext(context, true, clazz, cunit.getPackageName().getName() + "/" + getIdent());
  }

  /**
   * Insert all local definition to the class declaration
   */
  public void checkDefinition(VKContext context) throws PositionedError {
    getDefinitionCollector().checkDefinition(getContext(context));
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

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

    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/report", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/type", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    cunit.addTypeDeclaration(environment.getClassReader(), self);

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
    genComments(p);
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKDefinitionCollector			coll;
  private CParseCompilationUnitContext		cunit;
  private CParseClassContext			clazz;
  protected final VKEnvironment                 environment;
}
