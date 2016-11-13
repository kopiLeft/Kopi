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
import java.util.Vector;

import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CParseCompilationUnitContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CStdType;
import org.kopi.kopi.comp.kjc.CTypeVariable;
import org.kopi.kopi.comp.kjc.JArrayAccessExpression;
import org.kopi.kopi.comp.kjc.JAssignmentExpression;
import org.kopi.kopi.comp.kjc.JBlock;
import org.kopi.kopi.comp.kjc.JClassDeclaration;
import org.kopi.kopi.comp.kjc.JClassImport;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.JConstructorBlock;
import org.kopi.kopi.comp.kjc.JConstructorDeclaration;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JExpressionStatement;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JMethodDeclaration;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.JNewArrayExpression;
import org.kopi.kopi.comp.kjc.JPackageImport;
import org.kopi.kopi.comp.kjc.JStatement;
import org.kopi.kopi.comp.kjc.JStringLiteral;
import org.kopi.kopi.comp.kjc.JTypeNameExpression;
import org.kopi.kopi.comp.kjc.KjcEnvironment;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.Utils;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKConstants;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKStdType;
import org.kopi.vkopi.comp.base.VKTrigger;
import org.kopi.vkopi.comp.base.VKUtils;
import org.kopi.vkopi.comp.base.VKWindow;

/**
 * This class represents the definition of a form
 */
public class VKForm extends VKWindow implements org.kopi.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param     where		the token reference of this node
   * @param     title		the title of this form
   * @param     superName       the type of the form
   */
  public VKForm(TokenReference where,
                KjcEnvironment environment,
		CParseCompilationUnitContext cunit,
		CParseClassContext classContext,
		VKDefinitionCollector coll,
		String title,
		String locale,
		CReferenceType superForm,
		CReferenceType[] interfaces,
		VKCommand[] commands,
		VKTrigger[] triggers,
		VKFormElement[] blocks,
		VKPage[] pages)
  {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  title,
          locale,
	  superForm == null ? CReferenceType.lookup(VKConstants.VKO_FORM) : superForm,
	  interfaces,
	  0,  // forms have no options
	  commands,
	  triggers);

    this.pkg = cunit.getPackageName().getName();
    this.blocks = blocks;
    this.pages = pages;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get block
   */
  public VKFormElement getFormElement(String ident) {
    for (int j = 0; j < blocks.length; j++) {
      if (blocks[j].getIdent().equals(ident) || blocks[j].getShortcut().equals(ident)) {
	return blocks[j];
      }
    }
    return null;
  }

  /**
   * Return true if this field is multipage
   */
  public boolean isMultiPage() {
    return pages.length > 0;
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

    for (int i = 0; i < blocks.length; i++) {
      blocks[i].checkCode(context, this);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public JCompilationUnit genCode(Compiler compiler) {
    CParseCompilationUnitContext	cunit = getCompilationUnitContext();
    CParseClassContext			clazz = getClassContext();
    TokenReference			ref = getTokenReference();
    TypeFactory                         factory = environment.getTypeFactory();

    // ADD INIT
    clazz.addMethodDeclaration(buildInit());

    // ADD FIELDS
    clazz.addFieldDeclaration(buildTimestamp());

    // ADD BLOCKS
    for (int i = 0; i < blocks.length; i++) {
      JClassDeclaration		decl = blocks[i].genCode(true,factory);

      if (decl != null) {
	clazz.addInnerDeclaration(decl);
      }
      clazz.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
							      ACC_PROTECTED,
							      blocks[i].getType(),
							      blocks[i].getIdent(),
							      null));
      clazz.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
							      ACC_PROTECTED,
							      blocks[i].getType(),
							      blocks[i].getShortcut(),
							      null));
    }

    // BUILD CONSTRUCTOR
    JConstructorDeclaration cstr = new JConstructorDeclaration(ref,
							       ACC_PUBLIC,
							       getIdent(),
							       JFormalParameter.EMPTY,
							       VKUtils.VECT_VException,
							       new JConstructorBlock(ref,
                                                                                     null,
                                                                                     new JStatement[0]),
							       null,
							       null,
                                                               factory);
    clazz.addMethodDeclaration(cstr);

    // ADD TRIGGERS
    if (getCommandable().countTriggers(TRG_VOID) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(factory, ref, "executeVoidTrigger", TRG_VOID));
    }
    if (getCommandable().countTriggers(TRG_PRTCD) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(factory, ref, "executeProtectedVoidTrigger", TRG_PRTCD));
    }
    if (getCommandable().countTriggers(TRG_OBJECT) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(factory, ref, "executeObjectTrigger", TRG_OBJECT));
    }
    if (getCommandable().countTriggers(TRG_BOOLEAN) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(factory, ref, "executeBooleanTrigger", TRG_BOOLEAN));
    }
    if (getCommandable().countTriggers(TRG_INT) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(factory, ref, "executeIntegerTrigger", TRG_INT));
    }

    // BUILD THE CLASS
    JClassDeclaration		self = new JClassDeclaration(ref,
							     ACC_PUBLIC,
							     getIdent(),
                                                             CTypeVariable.EMPTY,
							     getSuperWindow(),
							     getInterfaces(),
							     clazz.getFields(),
							     clazz.getMethods(),
							     clazz.getInnerClasses(),
							     clazz.getBody(),
							     null,
							     null);

    // BUILD THE COMPILATION UNIT
    cunit.addTypeDeclaration(environment.getClassReader(), self);
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/cross", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/chart", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/report", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/xkopi/lib/type", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    JCompilationUnit compilUnit = new JCompilationUnit(ref,
                                                       environment,
						       cunit.getPackageName(),
						       cunit.getPackageImports(),
						       cunit.getClassImports(),
						       cunit.getTypeDeclarations());

    return compilUnit;
  }

  private JFieldDeclaration buildTimestamp() {
    TokenReference	ref = getTokenReference();
    JExpression		expr;

    expr = new JMethodCallExpression(ref,
				     new JTypeNameExpression(ref, CReferenceType.lookup("java/lang/System")),
				     "currentTimeMillis",
				     JExpression.EMPTY);
    return VKUtils.buildFieldDeclaration(ref,
					 ACC_PUBLIC | ACC_FINAL | ACC_STATIC,
					 CStdType.Long,
					 "timestamp",
					 expr);
  }

  /**
   * Returns the qualified source file name where this object is defined.
   */
  private String getSource() {
    String      basename;

    basename = getTokenReference().getName().substring(0, getTokenReference().getName().lastIndexOf('.'));
    return pkg == null ? basename : pkg + "/" + basename;
  }


  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildInit() {
    TokenReference	ref = getTokenReference();
    Vector		body = new Vector(10 + blocks.length);

    super.genInit(body);

    // SOURCE
    body.addElement(VKUtils.assign(ref, "source", VKUtils.toExpression(ref, getSource())));

    // PAGES
    JExpression[]	init1 = new JExpression[pages.length];
    for (int i = 0; i < pages.length; i++) {
      init1[i] = new JStringLiteral(ref, pages[i].getIdent());
    }
    body.addElement(VKUtils.assign(ref, "pages", VKUtils.createArray(ref, CStdType.String, init1)));

    // blocks = new Block[0]
    init1 = new JExpression[blocks.length];
    for (int i = 0; i < blocks.length; i++) {
      VKFormElement	block = blocks[i];
      JExpression	left = JNameExpression.build(ref, block.getIdent());
      JExpression	right = block.genConstructorCall();

      init1[i] = new JAssignmentExpression(ref, left, right);
      left = JNameExpression.build(ref, block.getShortcut());
      init1[i] = new JAssignmentExpression(ref, left, init1[i]);
    }
    body.addElement(VKUtils.assign(ref, "blocks", VKUtils.createArray(ref, VKStdType.VBlock, init1)));

    // TRIGGERS
    int[][]	triggerArray = new int[getCommands().length + 1][];
    
    body.addElement(VKUtils.assign(ref,
				   CMP_BLOCK_ARRAY,
				   new JNewArrayExpression(ref,
							   CStdType.Integer,
							   new JExpression[] {
				                             new JIntLiteral(ref, triggerArray.length),
							     new JIntLiteral(ref, TRG_TYPES.length)
							   },
							   null)));
    
    triggerArray[0] = getCommandable().getTriggerArray();
    for (int i = 0; i < getCommands().length; i++) {
      triggerArray[i + 1] = getCommands()[i].getBody().getCommandable().getTriggerArray();
    }
    for (int i = 0; i < triggerArray.length; i++) {
      for (int j = 0; j < TRG_TYPES.length; j++) {
	if (triggerArray[i][j] != 0) {
	  JExpression	expr = new JIntLiteral(ref, triggerArray[i][j]);
	  JExpression	left = new JArrayAccessExpression(ref,
	                                                  new JNameExpression(ref, CMP_BLOCK_ARRAY),
	                                                  new JIntLiteral(ref, i));
	  left = new JArrayAccessExpression(ref,
		                            left,
		                            new JIntLiteral(ref, j));
	  JExpression	assign = new JAssignmentExpression(ref, left, expr);

	  body.addElement(new JExpressionStatement(ref, assign, null));
	}
      }
    }

    for (int i = 0; i < blocks.length; i++) {
      VKFormElement	block = blocks[i];

      JExpression	left;
      left = new JMethodCallExpression(ref,
				       JNameExpression.build(ref, block.getIdent()),
				       "setInfo",
				       new JExpression[] {
                                         VKUtils.toExpression(ref, block.getPageNumber())
                                       });
      body.addElement(new JExpressionStatement(ref, left, null));
    }

    return new JMethodDeclaration(ref,
				  ACC_PROTECTED | ACC_FINAL,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "init",
				  JFormalParameter.EMPTY,
				  CReferenceType.EMPTY,
				  new JBlock(ref,
					     (JStatement[])Utils.toArray(body, JStatement.class),
					     null),
				  null,
				  null);
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
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX : comment move file creation to upper level (VKPhylum?)
   */
  public void genLocalization(String destination) {
    if (getLocale() != null) {
      String        baseName;

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
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    ((VKFormLocalizationWriter)writer).genForm(getTitle(),
                                               getDefinitionCollector(),
                                               pages,
                                               blocks);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String                  pkg;
  private VKFormElement[]		blocks;
  private VKPage[]			pages;
  private final KjcEnvironment          environment;
}
