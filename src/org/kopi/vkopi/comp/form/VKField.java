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

import org.kopi.vkopi.comp.base.*;
import org.kopi.kopi.comp.kjc.*;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.xkjc.XNameExpression;

/**
 * This class represents an editable element of a block
 */
public class VKField
  extends VKPhylum
  implements org.kopi.vkopi.lib.form.VConstants, org.kopi.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param ident		the ident of this field
   * @param pos			the position within the block
   * @param label		the label (text on the left)
   * @param help		the help text
   * @param type		the type of this field
   * @param align		the alignement of the text
   * @param options		the options of the field
   * @param columns		the column in the database
   * @param access		the access mode
   * @param commands		the commands accessible in this field
   * @param triggers		the triggers executed by this field
   * @param alias		th e alias of this field
   */
  public VKField(TokenReference where,
		 String ident,
		 VKPosition pos,
		 String label,
		 String help,
		 VKFieldType type,
		 int align,
		 int options,
		 VKFieldColumns columns,
		 int[] access,
		 VKCommand[] commands,
		 VKTrigger[] triggers,
		 String alias) {
    super(where);
    this.ident = ident;
    this.label = label;
    this.help = help;
    this.detailedPos = pos;
    this.type = type;
    this.align = align;
    this.columns = columns;
    this.access = access;
    this.commands = commands;
    this.triggers = triggers;
    this.options = options;
    this.alias = alias;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public String getIdent() {
    return ident == null ? "ANONYMOUS!@#$%^&*()" : ident;
  }

  /**
   * Returns the index in parent array of fields
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the typoe of this field
   */
  public VKFieldType getFieldType() {
    return type;
  }

  /**
   * return table num
   */
  public VKBlockTable getTable(String name) throws PositionedError {
    return block.getTable(name);
  }

  /**
   * return table num
   */
  public int getTableNum(VKBlockTable table) {
    return block.getTableNum(table);
  }

  /**
   * Returns true iff the field is never displayed
   */
  public boolean isInternal() {
    return
      access[0] == ACS_HIDDEN &&
      access[1] == ACS_HIDDEN &&
      access[2] == ACS_HIDDEN;
  }

  /**
   * Returns true iff the field is never displayed
   */
  public boolean isSkipped() {
    return
      access[0] == ACS_SKIPPED &&
      access[1] == ACS_SKIPPED &&
      access[2] == ACS_SKIPPED;
  }

  /**
   *
   */
  public int[] getTriggerArray() {
    return commandable.getTriggerArray();
  }

  /**
   * Returns the position in the array of fields
   */
  public int getPosInArray() {
    return -1;
  }

  /**
   * Returns the position of this object within block
   */
  public VKPosition getDetailedPosition() {
    return detailedPos;
  }

  /**
   * Returns commandable
   */
  public Commandable getCommandable() {
    return commandable;
  }
  
  /**
   * Returns the field commands.
   * @return The field commands.
   */
  public VKCommand[] getCommands() {
    return commands;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context,
                        VKBlock block,
                        VKDefinitionCollector coll)
    throws PositionedError
  {
    this.block = block;

    // IDENT
    if (ident == null) {
      ident = "ANM_" + block.getNextSyntheticNumber();
    }

    index = block.getIndex(this);

    // ACCESS
    int[]		blockAccess = block.getAccess();
    for (int i = 0; i < 3; i++) {
      this.access[i] = Math.min(this.access[i], blockAccess[i]);
    }
    commandable = new Commandable(getIdent(), block.getCommandable());
    commandable.checkCode(context, coll, commands, triggers);

    // TYPE
    type.checkCode(context, this, commandable);

    // LABEL
    check(label == null || !isInternal(), FormMessages.HIDDEN_FIELD_LABEL);

    // HELP
    check(label == null || !isInternal(), FormMessages.HIDDEN_FIELD_HELP);

    // TRANSIENT MODE
    if (columns == null && (isSkipped() || isInternal())) {
      options |= FDO_TRANSIENT;
    }

/* REALLY
    check(((options & FDO_TRANSIENT) == 0) ||
	  (isSkipped() || isInternal()),
	  "vk-field-transient-cant", getIdent());
*/
    check(((options & FDO_TRANSIENT) == 0) || columns == null,
          FormMessages.TRANSIENT_DATABASE_FIELD);

    // POSITION
    if (isInternal()) {
      check(detailedPos == null, FormMessages.HIDDEN_FIELD_POSITIONED, getIdent());
      check(!hasOption(FDO_NODETAIL), FormMessages.HIDDEN_FIELD_NODETAIL);
      check(!hasOption(FDO_NOCHART), FormMessages.HIDDEN_FIELD_NOCHART);
    } else if (!block.isSingle()) {
      // only one option at a time
      check (!hasOption(FDO_NOCHART) || !hasOption(FDO_NODETAIL),
             FormMessages.FIELD_NOCHART_NODETAIL);

      // REDUNDANT NO CHART, NO DETAIL
      if (block.hasOption(BKO_NOCHART)) {
        if (hasOption(FDO_NOCHART)) {
          fail(FormMessages.BOTH_NOCHART, null);
        }
        if (hasOption(FDO_NODETAIL)) {
          fail(FormMessages.FIELD_CANT_HAVE_NODETAIL, null);
        }
      }

      if (block.hasOption(BKO_NODETAIL)) {
        if (hasOption(FDO_NOCHART)) {
          fail(FormMessages.FIELD_CANT_HAVE_NOCHART, null);
        }
        if (hasOption(FDO_NODETAIL)) {
          fail(FormMessages.BOTH_NODETAIL, null);
        }
      }

      // with NO CHART the position must be not null
      if (hasOption(FDO_NOCHART) || block.hasOption(BKO_NOCHART)) {
        check(detailedPos != null, FormMessages.FIELD_NOCHART_NOPOSITION);
      }

      if (block.hasDetailView()) {
        // A field in a block which has a detailed view must have
        // either a position or declare NO DETAIL.
        if (detailedPos == null && !hasOption(FDO_NODETAIL)) {
          fail(FormMessages.FIELD_IN_DETAILED_BLOCK, null);
        }
      }
      // with NO DETAIL the position must be null
      if (hasOption(FDO_NODETAIL) || block.hasOption(BKO_NODETAIL)) {
        check(detailedPos == null, FormMessages.FIELD_NODETAIL_POSITION, null);

        // Get a position for the chart view.
        detailedPos = block.positionField(this);
      }
      if (!(hasOption(FDO_NODETAIL)
            || block.hasOption(BKO_NODETAIL)
            || hasOption(FDO_NOCHART)
            || block.hasOption(BKO_NOCHART))) {
        block.positionField(detailedPos);
      }
      detailedPos.checkCode(context, block);
    } else {
      check(detailedPos != null, FormMessages.FIELD_MUST_HAVE_POSITION, getIdent());
      detailedPos.checkCode(context, block);
    }

    // ALIAS
    if (type instanceof VKAliasType) {
      check(isInternal() || isSkipped(), FormMessages.ALIAS_FIELD_VISIBLE, getIdent());
      System.out.println("found alias field ....");
    }

    // COLUMN
    if (columns != null) {
      columns.checkCode(context, this);
    }

    // TRIGGERS
    //check that each trigger is used only once
    int         usedTriggers = 0;

    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & usedTriggers) > 0) {
        throw new PositionedError(triggers[i].getTokenReference(), FormMessages.TRIGGER_USED_TWICE);
      }
      usedTriggers |= triggers[i].getEvents();
    }
  }

  public String getAlias() {
    return alias;
  }

  /**
   *
   */
  public VKBlock getBlock() {
    return block;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genInfo() {
    TokenReference		ref = getTokenReference();
    JExpression[]		infos = new JExpression[13];
    int				count = 0;

    // PRIMARY INFO
    infos[count++] = VKUtils.toExpression(ref, getIdent());
    infos[count++] = VKUtils.toExpression(ref, getIndex());
    infos[count++] = VKUtils.toExpression(ref, getPosInArray());
    infos[count++] = VKUtils.toExpression(ref, options);

    // ACCESS
    JExpression[]	init = new JExpression[access.length];
    for (int i = 0; i < access.length; i++) {
      init[i] = VKUtils.toExpression(ref, access[i]);
    }
    infos[count++] = VKUtils.createArray(ref, CStdType.Integer, init);

    // LIST
    infos[count++] = type.genCode();

    // COLUMNS
    if (columns == null) {
      infos[count++] = VKUtils.nullLiteral(ref);
      infos[count++] = VKUtils.zeroLiteral(ref);
      infos[count++] = VKUtils.zeroLiteral(ref);
    } else {
      JExpression[]     exprs;

      exprs = columns.genCode(this);
      for (int i = 0; i < 3; i++) {
        infos[count++] = exprs[i];
      }
    }

    // COMMANDS
    if (commands.length > 0) {
      init = new JExpression[commands.length];
      for (int i = 0; i < commands.length; i++) {
	init[i] = commands[i].genConstructorCall(commandable);
      }
      infos[count++] = VKUtils.createArray(ref, VKStdType.VCommand, init);
    } else {
      infos[count++] = VKUtils.nullLiteral(ref);
    }

    // POSITION
    if (detailedPos != null) {
      // !!! USE AN INTERNAL FIELD DESC
      infos[count++] = detailedPos.genCode();
    } else {
      infos[count++] = VKUtils.nullLiteral(ref);
    }

    infos[count++] = VKUtils.toExpression(ref, align);

    // SET ALIAS
    if (alias != null) {
      infos[count++] = XNameExpression.build(ref, alias.replace('.', '/'));
    } else {
      infos[count++] = VKUtils.nullLiteral(ref);
    }

    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      new JNameExpression(ref, getIdent()),
							      "setInfo",
							      infos),
				    null);
  }

  /**
   * Generate a class for this element
   */
  public JStatement genConstructorCall() {
    TokenReference	ref = getTokenReference();

    return VKUtils.assign(ref, getIdent(), type.getDef().genConstructor());
  }

  /**
   * Generate a class for this element
   */
  public void genCode(CParseClassContext context) {
    context.addFieldDeclaration(VKUtils.buildFieldDeclaration(getTokenReference(),
							      ACC_PUBLIC | ACC_FINAL,
							      type.getDef().getDefaultType(),
							      getIdent(),
							      null));
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
    ((VKFormPrettyPrinter)p).printField(getIdent(), label, help, detailedPos, type, align, options, columns, access, commands, triggers);
    */
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    if (!isInternal()) {
      ((VKFormLocalizationWriter)writer).genField(getIdent(), label, help);
    }
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /*package*/ boolean hasOption(int option) {
    return (options & option) == option;
  }
  
  /*package*/ int fetchColumn(VKBlockTable table) {
    if (columns != null) {
      VKFieldColumn[] cols = columns.getColumns();
      for (int i = 0; i < cols.length; i++) {
        if(cols[i].getCorr().equals(table.getCorr())) {
          return i;
        }
      }
    }
    return -1;
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKBlock		block;
  private VKPosition		detailedPos;
  private String		ident;
  private String		label;
  private String		help;
  private VKFieldType		type;
  private int			align;
  private int			options;
  private VKFieldColumns	columns;
  private int[]			access;
  private VKCommand[]		commands;
  private VKTrigger[]		triggers;
  private String		alias;

  private int			index;
  private Commandable		commandable;
}
