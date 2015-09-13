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

package com.kopiright.vkopi.comp.chart;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CParseClassContext;
import com.kopiright.kopi.comp.kjc.CParseCompilationUnitContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.CTypeVariable;
import com.kopiright.kopi.comp.kjc.JArrayAccessExpression;
import com.kopiright.kopi.comp.kjc.JAssignmentExpression;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JClassDeclaration;
import com.kopiright.kopi.comp.kjc.JClassImport;
import com.kopiright.kopi.comp.kjc.JCompilationUnit;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JFieldAccessExpression;
import com.kopiright.kopi.comp.kjc.JFieldDeclaration;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JIntLiteral;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JMethodDeclaration;
import com.kopiright.kopi.comp.kjc.JNameExpression;
import com.kopiright.kopi.comp.kjc.JNewArrayExpression;
import com.kopiright.kopi.comp.kjc.JPackageImport;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.KjcEnvironment;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.vkopi.comp.base.VKCommand;
import com.kopiright.vkopi.comp.base.VKCommandName;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKDefinitionCollector;
import com.kopiright.vkopi.comp.base.VKLocalizationWriter;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.vkopi.comp.base.VKStdType;
import com.kopiright.vkopi.comp.base.VKTrigger;
import com.kopiright.vkopi.comp.base.VKUtils;
import com.kopiright.vkopi.comp.base.VKWindow;

public class VCChart extends VKWindow implements com.kopiright.vkopi.lib.chart.VConstants, com.kopiright.kopi.comp.kjc.Constants {

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
  @SuppressWarnings("deprecation")
  public VCChart(TokenReference where,
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
                 VCDimension[] dimensions,
                 VCMeasure[] measures,
                 String help)
  {
    super(where,
	  cunit,
	  classContext,
	  coll,
	  name,
          locale,
	  superReport == null ? CReferenceType.lookup(VCConstants.VKO_CHART) : superReport,
	  interfaces,
	  0,
	  commands,
	  triggers);

    this.pkg = cunit.getPackageName().getName();
    this.help = help;
    this.dimensions = dimensions;
    this.measures = measures;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public String getHelp() {
    return help;
  }
  
  public VCField[] getFields() {
    VCField[]		fields;
    
    fields = new VCField[dimensions.length + measures.length];
    for (int i = 0; i < dimensions.length; i++) {
      fields[i] = dimensions[i];
    }
    for (int i = 0; i < measures.length; i++) {
      fields[i + dimensions.length] = measures[i];
    }
    
    return fields;
  }
  
  public int getFieldIndex(String name) {
    int			index; 
    
    index = getDimensionIndex(name);
    if (index == -1) {
      index = dimensions.length + getMeasureIndex(name);
    }
    
    return index;
  }

  public VCField getDimension(String name) {
    // USE HASHTABLE $$$
    for (int i = 0; i < dimensions.length; i++) {
      if (dimensions[i].getIdent().equals(name)) {
	return dimensions[i];
      }
    }
    
    return null;
  }

  public int getDimensionIndex(String name) {
    for (int i = 0; i < dimensions.length; i++) {
      if (dimensions[i].getIdent().equals(name)) {
	return i;
      }
    }
    return -1;
  }

  public VCField getMeasure(String name) {
    // USE HASHTABLE $$$
    for (int i = 0; i < measures.length; i++) {
      if (measures[i].getIdent().equals(name)) {
	return measures[i];
      }
    }
    
    return null;
  }

  public int getMeasureIndex(String name) {
    for (int i = 0; i < measures.length; i++) {
      if (measures[i].getIdent().equals(name)) {
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

  /**
   * Adds the default chart commands.
   * @param where The token reference.
   * @param context The chart parse context.
   */
  public static void addDefaultChartCommands(TokenReference where, VCParseChartContext context) {
    context.addCommand(new VKCommandName(where, "Quit", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Print", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "PrintOptions", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportPNG", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportPDF", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ExportJPEG", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "ColumnView", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "BarView", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "LineView", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "AreaView", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "PieView", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
    context.addCommand(new VKCommandName(where, "Help", com.kopiright.vkopi.lib.form.VConstants.MOD_ANY));
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
    context.setTriggers(com.kopiright.vkopi.lib.chart.VConstants.TRG_TYPES);

    super.checkCode(context);
    if (dimensions == null || dimensions.length == 0) {
      throw new PositionedError(getTokenReference(), ChartMessages.NO_DIMENSION_FOUND);
    }
    for (int i = 0; i < dimensions.length; i++) {
      dimensions[i].checkCode(context, this);
    }
    for (int i = 0; i < measures.length; i++) {
      measures[i].checkCode(context, this);
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

    // ADD DIMENSIONS
    for (int i = 0; i < dimensions.length; i++) {
      JFieldDeclaration		decl = dimensions[i].genCode(clazz);

      clazz.addFieldDeclaration(decl);
      clazz.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
							      ACC_PRIVATE,
							      com.kopiright.vkopi.comp.trig.GVKAccess.translate(dimensions[i].getType().getDef().getType()),
							      dimensions[i].getIdent(),
							      null));
    }
    
    // ADD MEASURES
    for (int i = 0; i < measures.length; i++) {
      JFieldDeclaration		decl = measures[i].genCode(clazz);

      clazz.addFieldDeclaration(decl);
      clazz.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
							      ACC_PRIVATE,
							      com.kopiright.vkopi.comp.trig.GVKAccess.translate(measures[i].getType().getDef().getType()),
							      measures[i].getIdent(),
							      null));
    }

    // ADD TRIGGERS
    if (getCommandable().countTriggers(TRG_VOID1) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeVoidTrigger", TRG_VOID1));
    }
    if (getCommandable().countTriggers(TRG_OBJECT1) > 0) {
      clazz.addMethodDeclaration(getCommandable().buildTriggerHandler(null, ref, "executeObjectTrigger", TRG_OBJECT1));
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
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/util", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/form", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/print", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/chart", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/vkopi/lib/visual", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/xkopi/lib/base", null));
    cunit.addPackageImport(new JPackageImport(TokenReference.NO_REF, "com/kopiright/xkopi/lib/type", null));
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
    TokenReference		ref = getTokenReference();
    Vector<JStatement>		body = new Vector<JStatement>(10 + dimensions.length + measures.length);

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

    for (int i = 0; i < dimensions.length; i++) {
      dimensions[i].getCommandable().genCode(ref, body, false, false);
    }
    for (int i = 0; i < measures.length; i++) {
      measures[i].getCommandable().genCode(ref, body, false, false);
    }
    
    // TRIGGER ARRAY
    int[][]	triggerArray = new int[dimensions.length + measures.length + getCommands().length + 1][];

    triggerArray[0] = getCommandable().getTriggerArray();
    for (int i = 0; i < dimensions.length; i++) {
      triggerArray[i + 1] = dimensions[i].getTriggerArray();
    }
    for (int i = 0; i < measures.length; i++) {
      triggerArray[dimensions.length + i + 1] = measures[i].getTriggerArray();
    }
    for (int i = 0; i < getCommands().length; i++) {
      triggerArray[dimensions.length + measures.length + i + 1] = getCommands()[i].getBody().getCommandable().getTriggerArray();
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

    // model.dimensions = new VDimensionColumn[0]
    JExpression[] dinit1 = new JExpression[dimensions.length];
    for (int i = 0; i < dimensions.length; i++) {
      dinit1[i] = dimensions[i].genConstructorCall();
    }

    JExpression         dexpr = new JThisExpression(ref);
    dexpr = new JNameExpression(ref, dexpr, "dimensions");
    dexpr = new JAssignmentExpression(ref, dexpr, VKUtils.createArray(ref, VKStdType.VChartDimension, dinit1));
    body.addElement(new JExpressionStatement(ref, dexpr, null));

    // model.measures = new VDimensionColumn[0]
    JExpression[] minit1 = new JExpression[measures.length];
    for (int i = 0; i < measures.length; i++) {
      minit1[i] = measures[i].genConstructorCall();
    }

    JExpression         mexpr = new JThisExpression(ref);
    mexpr = new JNameExpression(ref, mexpr, "measures");
    mexpr = new JAssignmentExpression(ref, mexpr, VKUtils.createArray(ref, VKStdType.VChartMeasure, minit1));
    body.addElement(new JExpressionStatement(ref, mexpr, null));

    return new JMethodDeclaration(ref,
				  ACC_PROTECTED,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "init",
				  JFormalParameter.EMPTY,
				  VKUtils.VECT_VException,
				  new JBlock(ref,
					     body.toArray(new JStatement[body.size()]),
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
    JExpression[]	dimensionsParams = new JExpression[dimensions.length];
    JExpression[]	measuresParams = new JExpression[measures.length];
    
    for (int i = 0; i < dimensions.length; i++) {
      dimensionsParams[i] = new JFieldAccessExpression(ref, dimensions[i].getIdent());
    }
    
    for (int i = 0; i < measures.length; i++) {
      measuresParams[i] = new JFieldAccessExpression(ref, measures[i].getIdent());
    }

    JMethodCallExpression	meth = new JMethodCallExpression(ref,
								 null,
								 "addRow",
								 new JExpression[] {
								   VKUtils.createArray(ref, CStdType.Object, dimensionsParams),
								   VKUtils.createArray(ref, CStdType.Object, measuresParams)});

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
  public void genVKCode(VKPrettyPrinter p) {}

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
	baseName = baseName.substring(baseName.lastIndexOf(File.separatorChar) + 1, baseName.lastIndexOf(".vc"));
      } else {
	baseName = baseName.substring(0, baseName.lastIndexOf(".vc"));
      }

      try {
	VCChartLocalizationWriter        writer;

        writer = new VCChartLocalizationWriter();
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
    ((VCChartLocalizationWriter)writer).genChart(getTitle(),
                                                 help,
                                                 getDefinitionCollector(),
                                                 getFields());
  }

  // ----------------------------------------------------------------------
  // PRIVATE CONSTANTS
  // ----------------------------------------------------------------------

  private static final int[]	TRG_TYPES1 = com.kopiright.vkopi.lib.chart.VConstants.TRG_TYPES;
  private static final int	TRG_VOID1 = com.kopiright.vkopi.lib.form.VConstants.TRG_VOID;
  private static final int	TRG_OBJECT1 = com.kopiright.vkopi.lib.form.VConstants.TRG_OBJECT;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          			pkg;
  private final String          			help;
  //private final VCField[]       			fields;
  private final VCDimension[]				dimensions;
  private final VCMeasure[]				measures;
  private int                   			countSyntheticName;
  private final KjcEnvironment  			environment;
}
