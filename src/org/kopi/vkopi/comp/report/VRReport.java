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

package org.kopi.vkopi.comp.report;

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
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JExpressionStatement;
import org.kopi.kopi.comp.kjc.JFieldAccessExpression;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JMethodDeclaration;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.JNewArrayExpression;
import org.kopi.kopi.comp.kjc.JPackageImport;
import org.kopi.kopi.comp.kjc.JStatement;
import org.kopi.kopi.comp.kjc.KjcEnvironment;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKCommandName;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKStdType;
import org.kopi.vkopi.comp.base.VKTrigger;
import org.kopi.vkopi.comp.base.VKUtils;
import org.kopi.vkopi.comp.base.VKWindow;

class VRReport
  extends VKWindow
  implements org.kopi.vkopi.lib.report.Constants,
             org.kopi.kopi.comp.kjc.Constants
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
                  String locale,
                  CReferenceType superReport,
                  CReferenceType[] interfaces,
		  VKCommand[] commands,
		  VKTrigger[] triggers,
		  VRField[] fields,
                  String help)
  {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  name,
          locale,
	  superReport == null ? CReferenceType.lookup(VRConstants.VKO_REPORT) : superReport,
	  interfaces,
	  0,
	  commands,
	  triggers);

    this.pkg = cunit.getPackageName().getName();
    this.help = help;
    this.fields = fields;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public String getHelp() {
    return help;
  }

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
    context.addCommand(new VKCommandName(where, "Quit", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Print", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "PrintOptions", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportCSV", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportPDF", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportXLSX", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Fold", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Unfold", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Sort", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "AddConfiguration", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "LoadConfiguration", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "RemoveConfiguration", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ColumnInfo", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Help", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "EditColumn", org.kopi.vkopi.lib.form.VConstants.MOD_ANY));
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
    context.setTriggers(org.kopi.vkopi.lib.report.Constants.TRG_TYPES);

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
							      org.kopi.vkopi.comp.trig.GVKAccess.translate(fields[i].getType().getDef().getDefaultType()),
							      fields[i].getIdent(),
							      null));
    }

    // ADD TRIGGERS
    if (getCommandable().countTriggers(TRG_VOID1) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeVoidTrigger", TRG_VOID1));
    }
    if (getCommandable().countTriggers(TRG_OBJECT1) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeObjectTrigger", TRG_OBJECT1));
    }
    if (getCommandable().countTriggers(TRG_BOOLEAN1) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeBooleanTrigger", TRG_BOOLEAN1));
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
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "org/kopi/vkopi/lib/print", null));
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

  /**
   * Returns the qualified source file name where this object is defined.
   */
  private String getSource() {
    String      basename;

    basename = getTokenReference().getName().substring(0, getTokenReference().getName().lastIndexOf('.'));
    return pkg == null ? basename : pkg + "/" + basename;
  }

  /**
   * Check expression and evaluate and alter context
   * @@exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration buildInit() {
    TokenReference	ref = getTokenReference();
    Vector		body = new Vector(10 + fields.length);

    super.genInit(body);

    // SET SOURCE
    body.addElement(new JExpressionStatement(ref,
					     new JMethodCallExpression(ref,
								       null,
								       "setSource",
								       new JExpression[] {
									 VKUtils.toExpression(ref, getSource())
								       }),
					     null));
    /**
     * taoufik 200060929 we get the title and the help from localization files
    // SET TITLE
    body.addElement(new JExpressionStatement(ref,
					     new JMethodCallExpression(ref,
								       null,
								       "setPageTitle",
								       new JExpression[] {
									 VKUtils.toExpression(ref, getTitle())
								       }),
					     null));
    body.addElement(new JExpressionStatement(ref,
					     new JMethodCallExpression(ref,
								       null,
								       "setHelp",
								       new JExpression[] {
									 VKUtils.toExpression(ref, getHelp())
								       }),
					     null));
    */

    for (int i = 0; i < fields.length; i++) {
      fields[i].getCommandable().genCode(ref, body, false, false);
    }
    // TRIGGER ARRAY
    int[][]	triggerArray = new int[fields.length + getCommands().length + 1][];

    triggerArray[0] = getCommandable().getTriggerArray();
    for (int i = 0; i < fields.length; i++) {
      triggerArray[i + 1] = fields[i].getTriggerArray();
    }
    for (int i = 0; i < getCommands().length; i++) {
      triggerArray[fields.length + i + 1] = getCommands()[i].getBody().getCommandable().getTriggerArray();
    }

    // TRIGGERS
    body.addElement(VKUtils.assign(ref,
				   CMP_BLOCK_ARRAY,
				   new JNewArrayExpression(ref,
							   CStdType.Integer,
							   new JExpression[] {
							     new JIntLiteral(ref, triggerArray.length),
							     new JIntLiteral(ref, TRG_TYPES1.length)
							   },
							   null)));
    for (int i = 0; i < triggerArray.length; i++) {
      for (int j = 0; j < TRG_TYPES1.length; j++) {
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
					     (JStatement[])org.kopi.util.base.Utils.toArray(body, JStatement.class),
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
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX : comment move file creation to upper level (VKPhylum?)
   */
  public void genLocalization(String destination) {
    if (getLocale() != null) {
      String        baseName;

      baseName = getTokenReference().getFile();

      baseName = getTokenReference().getFile();
      if (baseName.lastIndexOf(File.separatorChar) != -1) {
	baseName = baseName.substring(baseName.lastIndexOf(File.separatorChar) + 1, baseName.lastIndexOf(".vr"));
      } else {
	baseName = baseName.substring(0, baseName.lastIndexOf(".vr"));
      }

      try {
        VRReportLocalizationWriter        writer;

        writer = new VRReportLocalizationWriter();
        genLocalization(writer);
        writer.write(destination, baseName, getLocale());
      } catch (IOException ioe) {
        ioe.printStackTrace();
        System.err.println("cannot write : " + baseName);
      }
    }
  }

  /**
   * !!! COMMENT ME
   */
  public void genLocalization(VKLocalizationWriter writer) {
    ((VRReportLocalizationWriter)writer).genReport(getTitle(),
                                                   help,
                                                   getDefinitionCollector(),
                                                   fields);
  }

  // ----------------------------------------------------------------------
  // PRIVATE CONSTANTS
  // ----------------------------------------------------------------------

  private static final int[]	TRG_TYPES1 = org.kopi.vkopi.lib.report.Constants.TRG_TYPES;
  private static final int	TRG_VOID1 = org.kopi.vkopi.lib.form.VConstants.TRG_VOID;
  private static final int	TRG_OBJECT1 = org.kopi.vkopi.lib.form.VConstants.TRG_OBJECT;
  private static final int      TRG_BOOLEAN1 = org.kopi.vkopi.lib.form.VConstants.TRG_BOOLEAN;

  private static final Vector	TRIGGER_ARRAY_DIM = new Vector(2);

  static {
    TRIGGER_ARRAY_DIM.addElement(null);
    TRIGGER_ARRAY_DIM.addElement(null);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          pkg;
  private final String          help;
  private final VRField[]       fields;
  private int                   countSyntheticName;
  private final KjcEnvironment  environment;
}
