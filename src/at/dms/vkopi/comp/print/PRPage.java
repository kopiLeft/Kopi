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
 * $Id: PRPage.java,v 1.2 2004/09/28 10:34:01 graf Exp $
 */

package at.dms.vkopi.comp.print;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.NotImplementedException;
import at.dms.util.base.Utils;
import at.dms.vkopi.comp.base.*;

/**
 * This class represents the definition of a page
 */
public class PRPage
  extends VKPhylum
  implements VKCompilationUnit, at.dms.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRPage(TokenReference where,
                KjcEnvironment environment,
		CParseCompilationUnitContext cunit,
		CParseClassContext context,
		PRDefinitionCollector coll,
		CReferenceType type,
		PRBlock[] blocks)
  {
    super(where);
    this.environment = environment;
    this.cunit = cunit;
    this.clazz = context;
    this.coll = coll;
    this.type = type;
    this.blocks = blocks;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the style withe name:
   * @param	name the name of the style
   * @return	the style
   */
  public PRStyle getStyle(String name) {
    PRStyle	ret = (PRStyle)usedStyles.get(name);
    if (ret == null) {
      ret = ((PRDefinitionCollector)getDefinitionCollector()).getStyleDef(name);
      if (ret != null) {
	try {
	  ret.checkCode(context, this);
	} catch (PositionedError e) {
	  e.printStackTrace();
	}
      } else {
	// !!! should use an error here
	System.err.println("UNDEFINED: " + name); // !!!
      }
      if (ret != null) {
	usedStyles.put(name, ret);
      }
    }
    return ret;
  }

  /**
   * return a block with a name
   */
  public PRBlock getBlock(String name) throws PositionedError {
    if (!blocks_H.containsKey(name)) {
      // an unpositioned error would be better
      throw new CLineError(getTokenReference(), PrintMessages.UNDEFINED_BLOCK, name);
    }

    return (PRBlock)blocks_H.get(name);
  }

  /**
   * Sets the prolog for this page
   */
  public void setProlog(PRProlog prolog) {
    this.prolog = prolog;
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    return coll;
  }

  /**
   * Returns the ident
   */
  public String getIdent() {
    String	file = getTokenReference().getName();
    return file.substring(0, file.indexOf('.'));
  }

  /**
   * Returns the ident
   */
  public String genUniqueIdent() {
    return "ANONYMOUSBLOCK_" + countAnonymousBlock++;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void addBlock(PRBlock block) {
    allBlocks.addElement(block);
  }

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    TokenReference	ref = getTokenReference();

    this.context = context;
    context.setMode(true);
    context.setClassContext(clazz);
    context.setFullName(cunit.getPackageName().getName() + "/" + getIdent());

    prolog.checkCode(context);

    try {
      getDefinitionCollector().checkInsert(context.getTopLevel());
    } catch (at.dms.compiler.base.UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
    getDefinitionCollector().checkCode(context);

    // Blocks
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].checkCode(context, this);
    }

    blocks = (PRBlock[])Utils.toArray(allBlocks, PRBlock.class);
    context = null;
  }


  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------


  /**
   * Returns the class declaration
   */
  public JCompilationUnit genCode(Compiler compiler) {
    TokenReference	ref = getTokenReference();
    JExpression		expr;

    // Init
    expr = new JMethodCallExpression(ref,
				     new JSuperExpression(ref),
				     "internalInitLoadDefinition",
				     JExpression.EMPTY);
    initLoadDefinition.addElement(new JExpressionStatement(ref, expr, null));
    if (prolog != null) {
      initLoadDefinition.addElement(prolog.genCode());
    }

    // Blocks vars
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i].getIdent() != null) {
	JFieldDeclaration		decl = blocks[i].genFieldDeclaration();

	if (decl != null) {
	  clazz.addFieldDeclaration(decl);
	}
      }
    }

    // Show if
    Vector	switchBody = new Vector();
    for (int i = 0; i < blocks.length; i++) {
      PRBlock block = blocks[i];
      JSwitchLabel[]	  labels = new JSwitchLabel[] {
	new JSwitchLabel(getTokenReference(), new JIntLiteral(getTokenReference(), i))
      };
      JStatement[]	  stmts = new JStatement[1];
      JSwitchGroup  group = new JSwitchGroup(getTokenReference(), labels, stmts);
      if (block.getPosition() != null) {
	stmts[0] = new JReturnStatement(getTokenReference(), block.getPosition().genExpression() , null);
      } else {
	// imported block, see parent showIf
	// !!! translate pos >>> temp
	stmts[0] = (new JReturnStatement(getTokenReference(), new JBooleanLiteral(ref, true), null));
      }
      switchBody.addElement(group);
    }
    // Add a default
    JSwitchLabel[]	labels = new JSwitchLabel[] {new JSwitchLabel(getTokenReference(), null)};
    JStatement[]	stmts = new JStatement[] {new JReturnStatement(getTokenReference(), new JBooleanLiteral(ref, true), null)};
    JSwitchGroup	group = new JSwitchGroup(getTokenReference(), labels, stmts);
    switchBody.addElement(group);

    showIf.addElement(new JSwitchStatement(ref, new JNameExpression(ref, "block"),
					   (JSwitchGroup[])Utils.toArray(switchBody, JSwitchGroup.class),
					   null));
    //showIf.addElement(new JReturnStatement(ref, new JBooleanLiteral(ref, true), null));

    // ADD LoadDefinition
    clazz.addMethodDeclaration(buildLoadDefinition());

    // ADD LoadBlock
    clazz.addMethodDeclaration(buildLoadBlock());

    // ADD ShowBlock
    clazz.addMethodDeclaration(buildShowBlock());

    // ADD FIELDS
    for (int i = 0; i < blocks.length; i++) {
      JClassDeclaration		decl = blocks[i].genCode(environment.getTypeFactory());

      if (decl != null) {
	clazz.addInnerDeclaration(decl);
      }
    }

    // BUILD THE CLASS
    JClassDeclaration		self = new JClassDeclaration(ref,
							     ACC_PUBLIC,
							     getIdent(),
                                                             CTypeVariable.EMPTY,
							     type == null ? TYPE : type,
							     CReferenceType.EMPTY,
							     clazz.getFields(),
							     clazz.getMethods(),
							     clazz.getInnerClasses(),
							     clazz.getBody(),
							     null,
							     null);

    // BUILD THE COMPILATION UNIT
    cunit.addTypeDeclaration(environment.getClassReader(), self);
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "java/lang", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/xkopi/lib/type", null));
    cunit.addClassImport(new JClassImport(TokenReference.NO_REF, "java/sql/SQLException", null));

    JCompilationUnit compilUnit = new JCompilationUnit(ref,
                                                       environment,
						       cunit.getPackageName(),
						       cunit.getPackageImports(),
						       cunit.getClassImports(),
						       cunit.getTypeDeclarations());

    return compilUnit;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildLoadDefinition() {
    TokenReference	ref = getTokenReference();

    
    for (Enumeration elems = usedStyles.elements(); elems.hasMoreElements(); ) {
      PRStyle style = (PRStyle)elems.nextElement();
      initLoadDefinition.addElement(style.genInit());
    }

    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i] instanceof PRRectangleBlock || blocks[i] instanceof PRPostscriptBlock) {
	initLoadDefinition.addElement(new JExpressionStatement(ref, blocks[i].genConstructorCall(), null));
      }
    }

    // INIT LOAD DEFINITION
    //!!!XXX!!!
    return new JMethodDeclaration(ref,
				  ACC_PROTECTED,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "internalInitLoadDefinition",
 				  JFormalParameter.EMPTY,
				  CReferenceType.EMPTY,
				  new JBlock(ref,
                                             (JStatement[])Utils.toArray(initLoadDefinition, JStatement.class),
                                             null),
				  null,
				  null);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildLoadBlock() {
    TokenReference	ref = getTokenReference();

    // BLOCKS
    JStatement[] elems = new JStatement[blocks.length];
    for (int i = 0; i < blocks.length; i++) {
      JExpression	expr;

      if (blocks[i] instanceof PRRectangleBlock || blocks[i] instanceof PRPostscriptBlock) {
	expr = new JNameExpression(ref, blocks[i].getIdent());
      } else {
	expr = blocks[i].genConstructorCall();
      }
      expr = new JMethodCallExpression(ref,
				       null,
				       "addBlock",
				       new JExpression[] {expr});

      elems[i] = new JExpressionStatement(ref, expr, null);
    }
    JBlock	block = new JBlock(ref, elems, null);

    return new JMethodDeclaration(ref,
				  ACC_PROTECTED,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "internalInitLoadBlock",
 				  JFormalParameter.EMPTY,
				  CReferenceType.EMPTY,
				  block,
				  null,
				  null);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildShowBlock() {
    TokenReference	ref = getTokenReference();

    JBlock blk = new JBlock(ref,
			    (JStatement[])Utils.toArray(showIf, JStatement.class),
			    null);
    return  new JMethodDeclaration(ref,
				   ACC_PUBLIC,
                                   CTypeVariable.EMPTY,
				   CStdType.Boolean,
				   "showBlock",
				   new JFormalParameter[]{
				     new JFormalParameter(ref,
							  JLocalVariable.DES_PARAMETER,
							  CStdType.Integer,
							  "block",
							  true)},
				   CReferenceType.EMPTY,
				   blk,
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

      pp = new VKPrettyPrinter(fileName, factory);
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
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static CReferenceType	TYPE		= CReferenceType.lookup(at.dms.vkopi.lib.print.PPage.class.getName().replace('.','/'));
  public static CReferenceType	TYPE_PROTECTED	= CReferenceType.lookup(at.dms.vkopi.lib.print.PProtectedPage.class.getName().replace('.','/'));
  public static CReferenceType	BLOCK_TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PBlock.class.getName().replace('.','/'));
  public static CReferenceType	STYLE_TYPE	= CReferenceType.lookup(at.dms.vkopi.lib.print.PBlock.class.getName().replace('.','/')); // !!! TO REMOVE

  protected	String		name;
  private	VKDefinitionCollector coll;
  private	CParseClassContext    clazz;
  private	CParseCompilationUnitContext cunit;
  private	CReferenceType	type;
  private	Hashtable	blocks_H = new Hashtable();
  private	PRBlock[]	blocks;
  private	Hashtable	usedStyles = new Hashtable();
  private	Vector		initLoadDefinition = new Vector();
  private	Vector		showIf = new Vector();
  private	Vector		allBlocks = new Vector(); // !!!
  private	PRProlog	prolog;
  private	int		countAnonymousBlock;
  private       VKContext	context;
  private final KjcEnvironment  environment;
}
