/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.form;

import java.io.File;
import java.io.IOException;

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CParseCompilationUnitContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JClassDeclaration;
import org.kopi.kopi.comp.kjc.JClassImport;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.JPackageImport;
import org.kopi.kopi.comp.kjc.KjcEnvironment;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKConstants;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKTrigger;
import org.kopi.vkopi.comp.base.VKVisitor;
import org.kopi.vkopi.comp.base.VKWindow;

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
		       VKFormElement[] blocks,
                       String locale)
  {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  "BlockInsert",
          locale,
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
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/report", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/type", null));
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

  public VKBlock getBlock() {
    return block;
  }

  // ----------------------------------------------------------------------
  // Galite CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param visitor the visitor
   */
  @Override
  public void accept(VKVisitor visitor) {}

  /**
   * Generates Galite code for this VK element.
   *
   * @param destination the target path for code generation
   * @param factory     the factory used during generation
   */
  @Override
  public void genGaliteCode(String destination, TypeFactory factory) {}

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX : comment + move file creation to upper level (VKPhylum?)
   */
  public void genLocalization(String destination) {
    if (getLocale() != null) {
      String        baseName;

      baseName = getTokenReference().getFile();

      baseName = getTokenReference().getFile();
      if (baseName.lastIndexOf(File.separatorChar) != -1) {
	baseName = baseName.substring(baseName.lastIndexOf(File.separatorChar) + 1, baseName.lastIndexOf(".vf"));
      } else {
	baseName = baseName.substring(0, baseName.lastIndexOf(".vf"));
      }

      try {
        VKFormLocalizationWriter        writer;

        writer = new VKFormLocalizationWriter();
        genLocalization(writer);
        writer.write(destination, baseName, getLocale());
      } catch (IOException ioe) {
        ioe.printStackTrace();
        System.err.println("cannot write : " + baseName);
      }
    }
  }

  /**
   * !!!FIX
   */
  public void genLocalization(VKLocalizationWriter writer) {
    ((VKFormLocalizationWriter)writer).genBlockInsert(getDefinitionCollector(),
                                                      block);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKBlock			block;
  private final KjcEnvironment          environment;
}
