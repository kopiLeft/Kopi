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
 * $Id: VKBlockInsert.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import java.io.File;
import java.io.IOException;

import at.dms.vkopi.comp.base.VKCommand;
import at.dms.vkopi.comp.base.VKConstants;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKDefinitionCollector;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.vkopi.comp.base.VKTrigger;
import at.dms.vkopi.comp.base.VKWindow;
import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents the definition of a form
 */
class VKBlockInsert extends VKWindow {

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
  public VKBlockInsert(TokenReference where,
                       KjcEnvironment environment,
		       CParseCompilationUnitContext cunit,
		       CParseClassContext classContext,
		       VKDefinitionCollector coll,
		       VKFormElement[] blocks) {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  "BlockInsert",
	  CReferenceType.lookup(VKConstants.VKO_BLOCK),
	  CReferenceType.EMPTY,
	  0,
	  new VKCommand[0],
	  new VKTrigger[0]);

    this.environment = environment;
    this.block = (VKBlock)blocks[0];
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
    super.checkCode(context);

    block.checkCode(context, this);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompilationUnit genCode(Compiler compiler) {
    JClassDeclaration                   decl;
    CParseCompilationUnitContext	cunit;

    decl =  block.genCode(false, environment.getTypeFactory());
    cunit = getCompilationUnitContext();

    cunit.addTypeDeclaration(environment.getClassReader(), decl);
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/report", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/type", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    return new JCompilationUnit(getTokenReference(),
                                environment,
				cunit.getPackageName(),
				cunit.getPackageImports(),
				cunit.getClassImports(),
				cunit.getTypeDeclarations());
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(String destination, TypeFactory factory) {
    final String                fileName;

    if (destination == null || destination.equals("")) {
      fileName = getTokenReference().getFile();
    } else {
      fileName = destination + File.separatorChar + getTokenReference().getFile();
    }

    try {
    final VKPrettyPrinter       pp;

    pp = new VKFormPrettyPrinter(fileName, factory);
    genVKCode(pp);
    pp.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.err.println("cannot write : " + fileName);
    }
  }

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKBlock			block;
  private final KjcEnvironment          environment;
}
