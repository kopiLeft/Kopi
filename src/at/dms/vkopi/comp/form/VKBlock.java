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
 * $Id: VKBlock.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import java.awt.Point;
import java.util.Vector;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.vkopi.comp.base.*;
import at.dms.vkopi.lib.form.VConstants;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.Utils;

/**
 * A block on a form
 * A block contains fields and reference to database
 */
public class VKBlock
  extends VKFormElement
  implements at.dms.vkopi.lib.form.VConstants, at.dms.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param	where		the token reference of this node
   * @param     context         the parser context
   * @param     interfaces      the interfaces implemented by this block
   * @param	buffer		the buffer size of this block
   * @param	visible		the number of visible elements
   * @param	name		the name of this form
   * @param	title		the title of the block
   * @param	border		the border of the block
   * @param	align		the type of alignment in form
   * @param	help		the help
   * @param	options		the options
   * @param	tables		the tables accessed on the database
   * @param	indices		the indices for database
   * @param	access		the accessmode
   * @param	commands	the commands associated with this block
   * @param	triggers	the triggers executed by this form
   * @param	fields		the objects that populate the block
   */
  public VKBlock(TokenReference where,
		 CParseClassContext context,
		 CReferenceType[] interfaces,
		 int buffer,
		 int visible,
		 String name,
                 CReferenceType superBlock,
		 String title,
		 int border,
		 VKBlockAlign align,
		 String help,
		 int options,
		 VKBlockTable[] tables,
		 String[] indices,
		 int[] access,
		 VKCommand[] commands,
		 VKTrigger[] triggers,
		 VKField[] fields)
    {
      super(where, name);

      this.context = context;
      this.interfaces = interfaces;
      this.superBlock = superBlock == null ? 
        CReferenceType.lookup(VKConstants.VKO_BLOCK) : superBlock;
      this.visible = visible;
      this.title = title;
      this.align = align;
      this.border = border;
      this.help = help;
      this.options = options;
      this.buffer = buffer;
      this.tables = tables;
      this.indices = indices;
      this.access = access;
      this.commands = commands;
      this.triggers = triggers;
      this.fields = fields;
      alignment = ALG_CENTER;
    }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return an unique number in block
   */
  public int getNextSyntheticNumber() {
    return countSyntheticName++;
  }

  /**
   *
   */
  public VKWindow getWindow() {
    return window;
  }

  /**
   *
   */
  boolean isInner() {
    return !(window instanceof VKBlockInsert);
  }

  /**
   *
   */
  public int getMaxDisplayWidth() {
      return Math.max(displayedFields, maxColumnPos);
  }

  /**
   * return table num
   */
  public VKBlockTable getTable(String corr) throws PositionedError {
    if (tables != null) {
      for (int i = 0; i < tables.length; i++) {
	if (corr.equals(tables[i].getCorr())) {
	  return tables[i];
	}
      }
    }
    throw new CLineError(getTokenReference(), FormMessages.UNDEFINED_TABLENUM, corr);
  }

  /**
   * return table num $$$
   */
  public int getTableNum(VKBlockTable table) {
    for (int i = 0; i < tables.length; i++) {
      if (table == tables[i]) {
	return i;
      }
    }
    throw new InconsistencyException();
  }

  /**
   * return table num $$$
   */
  public int getIndex(VKField field) {
    for (int i = 0; i < fields.length; i++) {
      if (field == fields[i]) {
	return i;
      }
    }
    throw new InconsistencyException();
  }

  /**
   * Returns the number of visible fields (size of chart)
   */
  public int getNbDisplay() {
    return isSingle() ? 1 : visible;
  }

  /**
   * Returns
   */
  public int getVisible() {
    return visible;
  }

  /**
   * Returns the size of the buffer
   */
  public int getBufferSize() {
    return buffer;
  }

  /**
   * Returns the size of the buffer
   */
  public boolean isSingle() {
    return buffer == 1;
  }

  /**
   * Returns the access mask of the block
   */
  public int[] getAccess() {
    return access;
  }

  /**
   * Returns the alignment
   */
  public int getAlignment() {
    return ALG_LEFT;
  }

  /**
   * Returns if there is an index with given position
   */
  public boolean hasIndex(int index) {
    verify(index >= 0);

    if (indices == null || index >= indices.length) {
      return false;
    } else {
      indicesUsed |= (1 << index);
      return true;
    }
  }

  /**
   * Returns a field from an ident
   */
  public VKField getField(String ident) {
    int		i = ident.indexOf(".");
    if (i == -1) {
      for (int j = 0; j < fields.length; j++) {
	if (fields[j].getIdent().equals(ident)) {
	  return fields[j];
	}
      }

      return null;
    }
    if (isInner()) {
      VKFormElement	block = ((VKForm)window).getFormElement(ident.substring(0, i));
      return ((VKBlock)block).getField(ident.substring(i + 1));
    }

    return null;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good this pass is just
   * a tuning pass in order to create informations about exported
   * elements such as Classes, Interfaces, Methods, Constructors and
   * Fields
   * @param window	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKWindow window) throws PositionedError {
    context = new VKContext(context,
                            false,
                            this.context,
                            context.getFullName() + "/" + getIdent());
    this.window = window;

    check(isSingle() || visible <= buffer, FormMessages.MORE_VISIBLE_THAN_BUFFERED);
    check(getShortcut() != null, FormMessages.BLOCK_NO_SHORTCUT, getIdent());

    if (isSingle()) {
      // for a single block : the {PRE|VAL|POST}REC triggers are not executed
      for (int i = 0; i < triggers.length; i++) {
        checkRECTrigger(context, triggers[i], TRG_PREREC);
        checkRECTrigger(context, triggers[i], TRG_VALREC);
        checkRECTrigger(context, triggers[i], TRG_POSTREC);
      }

      check(!hasOption(VConstants.BKO_NOCHART), FormMessages.SINGLE_NOCHART);
      check(!hasOption(VConstants.BKO_NODETAIL), FormMessages.SINGLE_NODETAIL);
    } else {
      if (hasOption(BKO_NOCHART)) {
        check(getNbDisplay() == 1, FormMessages.MULTI_MULTI_DISPLAY_NOCHART);
      }

      if (getNbDisplay() > 1 && !hasOption(VConstants.BKO_NODETAIL)) {
        // We are in a chart block which has a detailed view : either
        // you specify NO DETAIL in the block options, either you
        // position some fields.

        boolean allUnpositioned = true;
        boolean allHidden = true;

        for (int i = 0; i < fields.length; i++) {
          if (!fields[i].isInternal()) {
            allHidden = false;
            allUnpositioned &= (fields[i].getDetailedPosition() == null && fields[i].hasOption(FDO_NODETAIL));
          }
        }

        check(!allUnpositioned || allHidden,
              FormMessages.MULTI_NODETAIL_ALLUNPOSITIONED,
              getIdent());
      }

    }

    if (align != null) {
      align.checkCode(context, this);
    }

    commandable.checkCode(context, window.getDefinitionCollector(), commands, triggers);

    Point		bottomRight = new Point(0, 0);

    for (int i = 0; i < fields.length; i++) {
      fields[i].checkCode(context, this, window.getDefinitionCollector());
      if (fields[i].getDetailedPosition() != null) {
	fields[i].getDetailedPosition().checkBR(bottomRight, fields[i]);
      }
    }

    // checkPositions
    String[][]             freePositions = new String[bottomRight.x+1][bottomRight.y+1];

    // all positions are free
    for (int i = 0; i < freePositions.length; i++) {
      for ( int k = 0; k < freePositions[i].length; k++) {
        freePositions[i][k] = null;
      }
    }

    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getDetailedPosition() != null) {
	fields[i].getDetailedPosition().checkPlace(context, fields[i], freePositions);
      }
    }

    maxRowPos = bottomRight.y;
    maxColumnPos = bottomRight.x;

    for (int i = 0; i < indices.length; i++) {
      if ((indicesUsed & (1 << i)) == 0) {
	context.reportTrouble(new CWarning(getTokenReference(),
                                           FormMessages.INDEX_NOT_USED,
                                           new Integer(i)));
      }
    }
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    return commandable.getDefinitionCollector();
  }

  /**
   * Returns a collector for definitiion
   */
  public Commandable getCommandable() {
    return commandable;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Ask to place a field on block
   */
  /*package*/ VKPosition positionField(VKField field) {
    return new VKCoordinatePosition(field.getTokenReference(), ++displayedFields);
  }
  /*package*/ void positionField(VKCoordinatePosition pos) {
    pos.setChartPosition (++displayedFields);
  }

  /**
   * Generate a class for this element
   */
  public JClassDeclaration genCode(boolean innerClass, TypeFactory factory) {
    TokenReference		ref = getTokenReference();

    // ADD FIELDS
    for (int i = 0; i < fields.length; i++) {
      fields[i].genCode(context);
    }

    if (!innerClass) {
      context.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
								ACC_PUBLIC,
								getType(),
								getIdent(),
								new JThisExpression(ref)));
      context.addFieldDeclaration(VKUtils.buildFieldDeclaration(ref,
								ACC_PUBLIC,
								getType(),
								getShortcut(),
								new JThisExpression(ref)));
    }

    // ADD CONSTRUCTOR
    context.addMethodDeclaration(buildConstructor(factory));
    context.addMethodDeclaration(buildSetInfo(factory));
    context.addMethodDeclaration(buildDisplay());

    // ADD TRIGGERS
    if (commandable.countTriggers(TRG_VOID) > 0) {
      context.addMethodDeclaration(commandable.buildTriggerHandler(factory, ref, "executeVoidTrigger", TRG_VOID));
    }
    if (commandable.countTriggers(TRG_PRTCD) > 0) {
      context.addMethodDeclaration(commandable.buildTriggerHandler(factory, ref, "executeProtectedVoidTrigger", TRG_PRTCD));
    }
    if (commandable.countTriggers(TRG_OBJECT) > 0) {
      context.addMethodDeclaration(commandable.buildTriggerHandler(factory, ref, "executeObjectTrigger", TRG_OBJECT));
    }
    if (commandable.countTriggers(TRG_BOOLEAN) > 0) {
      context.addMethodDeclaration(commandable.buildTriggerHandler(factory, ref, "executeBooleanTrigger", TRG_BOOLEAN));
    }
    if (commandable.countTriggers(TRG_INT) > 0) {
      context.addMethodDeclaration(commandable.buildTriggerHandler(factory, ref, "executeIntegerTrigger", TRG_INT));
    }

    JClassDeclaration decl = new JClassDeclaration(ref,
						   ACC_PUBLIC,
						   innerClass ? CMP_BLOCK_TYP + getIdent() : getIdent(),
                                                   CTypeVariable.EMPTY,
						   innerClass ? superBlock : CReferenceType.lookup(VKConstants.VKO_IMPORTEDBLOCK),
						   interfaces,
						   context.getFields(),
						   context.getMethods(),
						   context.getInnerClasses(),
						   context.getBody(),
						   null,
						   null);

    return decl;
  }

  /**
   * Gen new object
   */
  public JExpression genConstructorCall() {
    if (isInner()) {
      return new JUnqualifiedInstanceCreation(getTokenReference(),
				      getType(),
				      JExpression.EMPTY);
    } else {
      return new JUnqualifiedInstanceCreation(getTokenReference(),
				      getType(),
				      new JExpression[]{ new JThisExpression(getTokenReference()) });
    }
  }

  /**
   * Returns the java type of this block
   */
  public CReferenceType getType() {
    return CReferenceType.lookup(isInner() ? CMP_BLOCK_TYP + getIdent() : getIdent());
  }

  private JConstructorDeclaration buildConstructor(TypeFactory factory) {
    TokenReference	ref = getTokenReference();
    Vector		body = new Vector(20 + fields.length);

    body.addElement(VKUtils.assign(ref, "name", VKUtils.toExpression(ref, getIdent())));
    body.addElement(VKUtils.assign(ref, "shortcut", VKUtils.toExpression(ref, getShortcut())));
    body.addElement(VKUtils.assign(ref, "bufferSize", VKUtils.toExpression(ref, buffer)));
    body.addElement(VKUtils.assign(ref, "displaySize", VKUtils.toExpression(ref, visible)));
    body.addElement(VKUtils.assign(ref, "title", VKUtils.toExpression(ref, title)));
    body.addElement(VKUtils.assign(ref, "help", VKUtils.toExpression(ref, help)));
    body.addElement(VKUtils.assign(ref, "page", VKUtils.toExpression(ref, getPageNumber())));
    body.addElement(VKUtils.assign(ref, "options", VKUtils.toExpression(ref, options)));

    // TRIGGER HANDLING
    commandable.genCode(ref, body, !isInner(), true);
    for (int i = 0; i < fields.length; i++) {
      fields[i].getCommandable().genCode(ref, body, false, false);
    }
    // TRIGGER ARRAY
    int[][]	triggerArray = new int[fields.length + 1][];

    triggerArray[0] = commandable.getTriggerArray();
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

    // TABLES
    if (tables.length > 0) {
      JExpression[] init = new JExpression[tables.length];
      for (int i = 0; i < tables.length; i++) {
	init[i] = tables[i].genCode();
      }
      body.addElement(VKUtils.assign(ref, "tables", VKUtils.createArray(ref, CStdType.String, init)));
    }

    // INDICES
    if (indices.length > 0) {
      JExpression[] init = new JExpression[indices.length];
      for (int i = 0; i < indices.length; i++) {
	init[i] = new JStringLiteral(ref, indices[i]);
      }
      body.addElement(VKUtils.assign(ref, "indices", VKUtils.createArray(ref, CStdType.String, init)));
    }

    // ACCESS
    if (access.length > 0) {
      JExpression[] init = new JExpression[access.length];
      for (int i = 0; i < access.length; i++) {
	init[i] = new JIntLiteral(ref, access[i]);
      }
      body.addElement(VKUtils.assign(ref, "access", VKUtils.createArray(ref, CStdType.Integer, init)));
    }

    if (align != null) {
      align.genCode(body); // !!!
    }

    // FIELDS
    for (int i = 0; i < fields.length; i++) {
      body.addElement(fields[i].genConstructorCall());
    }

    JExpression[]	exprs = new JExpression[fields.length];
    for (int i = 0; i < fields.length; i++) {
      exprs[i] = new JFieldAccessExpression(ref, new JThisExpression(ref), fields[i].getIdent());
    }
    body.addElement(VKUtils.assign(ref, "fields", VKUtils.createArray(ref, VKStdType.VField, exprs)));

    return new JConstructorDeclaration(ref,
				       ACC_PUBLIC,
				       isInner() ? CMP_BLOCK_TYP + getIdent() : getIdent(),
				       isInner() ?
				       JFormalParameter.EMPTY :
				       new JFormalParameter[] {
					 new JFormalParameter(ref,
							      JLocalVariable.DES_PARAMETER,
							      at.dms.vkopi.comp.trig.GStdType.Form,
							      "form",
							      true)
				       },
				       CReferenceType.EMPTY,
				       new JConstructorBlock(ref,
                                                             genConstructorCall(ref),
                                                             (JStatement[])Utils.toArray(body, JStatement.class)),
				       null,
				       null,
                                       factory);
  }

  private JMethodDeclaration buildSetInfo(TypeFactory factory) {
    TokenReference	ref = getTokenReference();
    Vector		body = new Vector(fields.length);

    // FIELDS
    for (int i = 0; i < fields.length; i++) {
      body.addElement(fields[i].genInfo());
    }

    return new JMethodDeclaration(ref,
                                  ACC_PUBLIC,
                                  CTypeVariable.EMPTY,
                                  CStdType.Void,
                                  "setInfo",
                                  JFormalParameter.EMPTY,
                                  CReferenceType.EMPTY,
                                  new JBlock(ref,
                                             (JStatement[])Utils.toArray(body, JStatement.class),
                                             null),
                                  null,
                                  null);
  }

  private JConstructorCall genConstructorCall(TokenReference ref) {
    JExpression		expr;

    if (isInner()) {
      expr = new JThisExpression(ref, JNameExpression.build(ref, window.getFullName().replace('.', '/')));
    } else {
      expr = new JNameExpression(ref, "form");
    }

    return new JConstructorCall(ref, false, new JExpression[] { expr });
  }

  private JMethodDeclaration buildDisplay() {
    TokenReference	ref = getTokenReference();
    CReferenceType	type = VKStdType.VBlockUIProperties;
    JUnqualifiedInstanceCreation display;
    JStatement          stmt;

    display = new JUnqualifiedInstanceCreation(ref,
                                               type,
                                               new JExpression[] {
                                                 VKUtils.toExpression(ref, border),
                                                 VKUtils.toExpression(ref, title),
                                                 VKUtils.toExpression(ref, alignment),
                                                 VKUtils.toExpression(ref, maxRowPos),
                                                 VKUtils.toExpression(ref, maxColumnPos),
                                                 VKUtils.toExpression(ref, displayedFields)});
    stmt = new JExpressionStatement(ref,
                                    new JMethodCallExpression(ref,
                                                              null,
                                                              "setUIProperties",
                                                              new JExpression[] {display}),
                                    null);
    return new JMethodDeclaration(ref,
				  ACC_PUBLIC,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  "buildDisplay",
				  JFormalParameter.EMPTY,
				  CReferenceType.EMPTY,
				  new JBlock(ref, new JStatement[] {stmt}, null),
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
  public void genVKCode(VKPrettyPrinter p) {
    /*
    genComments(p);
    ((VKFormPrettyPrinter)p).printBlock(getIdent(), getShortcut(), visible, buffer, title,
					, border, alignment, align, help, tables, indices, access,
					options, commands, triggers, objects, page, getDeclaration());
					*/
  }

  // ----------------------------------------------------------------------
  //
  // ----------------------------------------------------------------------

  /*package*/ boolean hasDetailView() {
    return !isSingle() && getNbDisplay() > 1 && !hasOption(VConstants.BKO_NODETAIL);
  }

  /*package*/ boolean hasChartView() {
    return !isSingle() && getNbDisplay() > 1 && !hasOption(VConstants.BKO_NOCHART);
  }

  // ----------------------------------------------------------------------
  //
  // ----------------------------------------------------------------------

  private void checkRECTrigger(VKContext context, VKTrigger trigger, int type) {
    if ((trigger.getEvents() & (1 << type)) != 0) {
      String      name = null;

      switch(type) {
      case TRG_PREREC:
        name = "PRE";
        break;
      case TRG_VALREC:
        name = "VAL";
        break;
      case TRG_POSTREC:
        name = "POST";
        break;
      }

      context.reportTrouble(new CWarning(trigger.getTokenReference(),
                                         FormMessages.NO_REC_TRG_FOR_SINGLE_BLOCK,
                                         getIdent(),
                                         name));
    }
  }

  /*package*/ boolean hasOption(int option) {
    return (this.options & option) == option;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKWindow		window;

  private String		title;
  private int			border;
  private VKBlockAlign		align;
  private String		help;
  private int			options;
  private int			visible;
  private int			buffer;
  private VKBlockTable[]	tables;   // <VKBlockTable>
  private String[]		indices;  // <String>
  private int[]			access;
  private VKCommand[]		commands; // <VKCommand>
  private VKTrigger[]		triggers; // <VKTrigger>
  private VKField[]		fields;   // <VKObject>

  private int			indicesUsed;
  private int			countSyntheticName;

  private Commandable		commandable = new Commandable(null);


  private CParseClassContext	context;
  private CReferenceType[]	interfaces;
  private CReferenceType	superBlock;

  private int			maxRowPos;
  private int			maxColumnPos;
  private int			displayedFields ;
  private int			alignment; //left, right, center
}
