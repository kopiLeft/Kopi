/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.comp.report;

import java.util.Vector;
import at.dms.vkopi.comp.base.*;
import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

class VRReport
  extends VKWindow
  implements at.dms.vkopi.lib.report.Constants, at.dms.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @@param where		the token reference of this node
   * @@param name		the name of this form
   * @@param superName		the type of the form
   */
  public VRReport(TokenReference where,
                  KjcEnvironment environment,
		  CParseCompilationUnitContext cunit,
		  CParseClassContext classContext,
		  VKDefinitionCollector coll,
		  String name,
		  VKCommand[] commands,
		  VKTrigger[] triggers,
		  VRField[] fields) {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  name,
	  CReferenceType.lookup(VRConstants.VKO_REPORT),
	  new CReferenceType[0],
	  0,
	  commands,
	  triggers);

    this.fields = fields;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public VRField getField(String name) {
    // USE HASHTABLE $$$
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getIdent().equals(name)) {
	return fields[i];
      }
    }
    return null;
  }

  public int getFieldIndex(String name) {
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getIdent().equals(name)) {
	return i;
      }
    }
    return -1;
  }

  /**
   * Returns an unique number in block
   */
  public int getNextSyntheticNumber() {
    return countSyntheticName++;
  }

  public static void addDefaultReportCommands(TokenReference where, VRParseReportContext context) {
    context.addCommand(new VKCommandName(where, "Quit", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Print", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "PrintOptions", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Export", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Fold", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Unfold", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Sort", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "AddConfiguration", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "LoadConfiguration", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "RemoveConfiguration", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ColumnInfo", at.dms.vkopi.lib.form.VConstants.MOD_ANY));
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @@param block	the actual context of analyse
   * @@exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    context.setTriggers(at.dms.vkopi.lib.report.Constants.TRG_TYPES);

    super.checkCode(context);

    for (int i = 0; i < fields.length; i++) {
      fields[i].checkCode(context, this);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the class declaration
   */
  public JCompilationUnit genCode(Compiler compiler) {
    CParseCompilationUnitContext	cunit = getCompilationUnitContext();
    CParseClassContext			clazz = getClassContext();
    TokenReference			ref = getTokenReference();

    // ADD INIT
    clazz.addMethodDeclaration(buildInit());

    // ADD ADD()
    clazz.addMethodDeclaration(buildAdd());

    // ADD FIELDS
    for (int i = 0; i < fields.length; i++) {
      JFieldDeclaration		decl = fields[i].genCode(clazz);

      clazz.addFieldDeclaration(decl);
      clazz.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
							      ACC_PRIVATE,
							      at.dms.vkopi.comp.trig.GVKAccess.translate(fields[i].getType().getDef().getType()),
							      fields[i].getIdent(),
							      null));
    }

    // ADD TRIGGERS
    if (getCommandable().countTriggers(TRG_VOID) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeVoidTrigger", TRG_VOID));
    }
    if (getCommandable().countTriggers(TRG_OBJECT) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeObjectTrigger", TRG_OBJECT));
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
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "at/dms/vkopi/lib/report", null));
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
   * @@exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildInit() {
    TokenReference	ref = getTokenReference();
    Vector		body = new Vector(10 + fields.length);

    super.genInit(body);

    // SET TITLE
    body.addElement(new JExpressionStatement(ref,
					     new JMethodCallExpression(ref,
								       null,
								       "setPageTitle",
								       new JExpression[] {
									 VKUtils.toExpression(ref, getName())
								       }),
					     null));

    for (int i = 0; i < fields.length; i++) {
      fields[i].getCommandable().genCode(ref, body, false, false);
    }
    // TRIGGER ARRAY
    int[][]	triggerArray = new int[fields.length + 1][];

    triggerArray[0] = getCommandable().getTriggerArray();
    for (int i = 0; i < fields.length; i++) {
      triggerArray[i + 1] = fields[i].getTriggerArray();
    }

    // TRIGGERS
    body.addElement(VKUtils.assign(ref,
				   CMP_BLOCK_ARRAY,
				   new JNewArrayExpression(ref,
							   CStdType.Integer,
							   new JExpression[] {
							     new JIntLiteral(ref, triggerArray.length),
							     new JIntLiteral(ref, TRG_TYPES.length)
							   },
							   null)));
    for (int i = 0; i < triggerArray.length; i++) {
      for (int j = 0; j < TRG_TYPES.length; j++) {
	if (triggerArray[i][j] != 0) {
	  JExpression   expr = new JIntLiteral(ref, triggerArray[i][j]);
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

    // model.columns = new VReportColumn[0]
    JExpression[] init1 = new JExpression[fields.length];
    for (int i = 0; i < fields.length; i++) {
      init1[i] = fields[i].genConstructorCall();
    }

    JExpression         expr = new JNameExpression(ref, "model");
    expr = new JNameExpression(ref, expr, "columns");
    expr = new JAssignmentExpression(ref, expr, VKUtils.createArray(ref, VKStdType.VReportColumn, init1));
    body.addElement(new JExpressionStatement(ref, expr, null));

    return new JMethodDeclaration(ref,
				  ACC_PROTECTED,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "init",
				  JFormalParameter.EMPTY,
				  VKUtils.VECT_VException,
				  new JBlock(ref,
					     (JStatement[])at.dms.util.base.Utils.toArray(body, JStatement.class),
					     null),
				  null,
				  null);
  }

  /**
   * Check expression and evaluate and alter context
   * @@exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildAdd() {
    TokenReference	ref = getTokenReference();

    JExpression[]	params = new JExpression[fields.length];
    for (int i = 0; i < fields.length; i++) {
      params[i] = new JFieldAccessExpression(ref, fields[i].getIdent());
    }

    JMethodCallExpression	meth = new JMethodCallExpression(ref,
								 new JNameExpression(ref, "model"),
								 "addLine",
								 new JExpression[] {
								   VKUtils.createArray(ref, CStdType.Object, params)});

    return new JMethodDeclaration(ref,
				  ACC_PUBLIC | ACC_FINAL,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "add",
				  JFormalParameter.EMPTY,
				  CReferenceType.EMPTY,
				  new JBlock(ref,
					     new JStatement[] {
					       new JExpressionStatement(ref, meth, null)
					     },
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
   * @@param p		the printwriter into the code is generated
   */
  public void genVKCode(String destination, TypeFactory factory) {
/*
    VKPrettyPrinter pp;
    if (destination.equals("")) {
      genVKCode(pp = new VKPrettyPrinter(getTokenReference().getFile()));
    } else {
      genVKCode(pp = new VKPrettyPrinter(destination + File.separatorChar + getTokenReference().getFile()));
    }
    pp.close();
*/
  }

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @@param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
  }

  // ----------------------------------------------------------------------
  // PRIVATE CONSTANTS
  // ----------------------------------------------------------------------

  private static final int[]	TRG_TYPES = at.dms.vkopi.lib.report.Constants.TRG_TYPES;
  private static final int	TRG_VOID = at.dms.vkopi.lib.form.VConstants.TRG_VOID;
  private static final int	TRG_OBJECT = at.dms.vkopi.lib.form.VConstants.TRG_OBJECT;

  private static final Vector	TRIGGER_ARRAY_DIM = new Vector(2);

  static {
    TRIGGER_ARRAY_DIM.addElement(null);
    TRIGGER_ARRAY_DIM.addElement(null);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final VRField[]	fields;
  private int			countSyntheticName;
  private final KjcEnvironment          environment;
}
