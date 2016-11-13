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

import java.util.Vector;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.*;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.*;

public class VRField
  extends VKPhylum
  implements VRConstants, org.kopi.kopi.comp.kjc.Constants
{

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param ident		the ident of this field
   * @param label		the label (text on the left)
   * @param help		the help text
   * @param type		the type of this field
   * @param align		the alignement of the text
   * @param group		the grouping column
   * @param commands		the commands accessible in this field
   * @param triggers		the triggers executed by this field
   */
  public VRField(TokenReference where,
		 String ident,
		 String label,
		 String help,
		 VRFieldType type,
		 int align,
		 int options,
		 String group,
		 VKCommand[] commands,
		 VKTrigger[] triggers)
  {
    super(where);
    this.ident = ident;
    this.label = label;
    this.help = help;
    this.type = type;
    this.align = align;
    this.options = options;
    this.group = group;
    this.commands = commands;
    this.triggers = triggers;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this field
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the index in parent array of fields
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the parent
   */
  public VRReport getReport() {
    return parent;
  }

  /**
   *
   */
  public boolean isHidden() {
    return (options & org.kopi.vkopi.lib.report.Constants.CLO_HIDDEN) > 0;
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    return getReport().getDefinitionCollector();
  }

  /**
   *
   */
  public Commandable getCommandable() {
    return commandable;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param report	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VRReport report) throws PositionedError {
    //!!! graf 990825 -> vgp : look at indentation
    //if (type.getDef().getWidth() != -1 && label != null)
      /*
      if (type.getDef().getWidth() < label.length())
      context.reportTrouble(new CWarning(getTokenReference(),
      "vk-column-size",
      label,
      new Integer(type.getDef().getWidth())));
      !!! */
    if (group != null) {
      groupID = report.getFieldIndex(group);
      check(groupID != -1, ReportMessages.GROUP_UNKNOWN_FIELD, group);
    }

    commandable = new Commandable(getIdent() + "_", report.getCommandable());

    this.index = report.getFieldIndex(getIdent());
    parent = report;
    type.checkCode(context, this, commandable);

    if (type.getDef() instanceof VKFixnumType) {
      align = org.kopi.vkopi.lib.report.Constants.ALG_RIGHT;
    }
    if (ident == null) {
      ident = "ANM_" + parent.getNextSyntheticNumber();
    }

    commandable.checkCode(context, report.getDefinitionCollector(), commands, triggers);
  }

  /**
   * Return if this event correspond to a method call or a function
   *
  public boolean wantReturn(int event) {
    switch (org.kopi.vkopi.lib.report.Constants.TRG_TYPES[event]) {
    case org.kopi.vkopi.lib.report.Constants.TRG_VOID:
      return false;
    case org.kopi.vkopi.lib.report.Constants.TRG_OBJECT:
      return true;
    default:
      throw new InconsistencyException("INTERNAL ERROR: UNEXPECTED TRG " + event);
    }
  }
*/

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public int[] getTriggerArray() {
    return commandable.getTriggerArray();
  }

  /**
   *
   */
  public VRFieldType getType() {
    return type;
  }


  /**
   * Returns an expression that design this at runtime
   */
  public JExpression getThis() {
    return new JNameExpression(getTokenReference(),
			       new JThisExpression(getTokenReference()),
			       getIdent() + "_");
  }

  /**
   * Generate a class for this element
   */
  public JFieldDeclaration genCode(CParseClassContext context) {
    return VKUtils.buildFieldDeclaration(getTokenReference(),
					 /*ACC_FINAL !!!*/0,
					 type.getDef().getReportType(),
					 getIdent() + "_",
					 null);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructorCall() {
    TokenReference	ref = getTokenReference();
    Vector		params = new Vector(14);

    params.addElement(VKUtils.toExpression(ref, ident));
    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genType());
      params.addElement(((VKCodeType)type.getDef()).genSource());
    }
    params.addElement(VKUtils.toExpression(ref, options));
    params.addElement(VKUtils.toExpression(ref, align));
    params.addElement(VKUtils.toExpression(ref, groupID));
    params.addElement(getCompute());
    params.addElement(VKUtils.toExpression(ref, type.getDef().getWidth()));
    if (type.getDef() instanceof VKStringType) {
      params.addElement(VKUtils.toExpression(ref, type.getDef().getHeight()));
    }
    if (type.getDef() instanceof VKFixnumType) {
      // remove
      params.addElement(VKUtils.toExpression(ref, ((VKFixnumType)type.getDef()).getScale()));
    }
    params.addElement(getFormat());

    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genIdents());
      params.addElement(((VKCodeType)type.getDef()).genValues());
    }

    return new JAssignmentExpression(ref,
				     getThis(),
				     new JUnqualifiedInstanceCreation(ref,
                                                                      type.getDef().getReportType(),
                                                                      (JExpression[])org.kopi.util.base.Utils.toArray(params, JExpression.class)));
  }

  private JExpression getCompute() {
    TokenReference	ref = getTokenReference();

    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & (1 << org.kopi.vkopi.lib.report.Constants.TRG_COMPUTE)) > 0) {
	JExpression	expr;

	expr = new JMethodCallExpression(ref,
                                         null,
                                         "callTrigger",
                                         new JExpression[] {
                                           VKUtils.toExpression(ref, org.kopi.vkopi.lib.report.Constants.TRG_COMPUTE),
                                           VKUtils.toExpression(ref, getIndex() + 1)
                                         });

	return new JCastExpression(ref, expr, COMPUTE_TYPE);
      }
    }

    return VKUtils.toExpression(ref, (String)null);
  }

  private JExpression getFormat() {
    TokenReference	ref = getTokenReference();

    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & (1 << org.kopi.vkopi.lib.report.Constants.TRG_FORMAT)) > 0) {
	JExpression	expr;

	expr = new JMethodCallExpression(ref,
					 null,
					 "callTrigger",
					 new JExpression[]{
					   VKUtils.toExpression(ref, org.kopi.vkopi.lib.report.Constants.TRG_FORMAT),
					   VKUtils.toExpression(ref, getIndex() + 1)
					 });
	return new JCastExpression(ref, expr, FORMAT_TYPE);
      }
    }

    return VKUtils.toExpression(ref, (String)null);
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
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!! COMMENT ME
   */
  public void genLocalization(VKLocalizationWriter writer) {
    if (! isHidden()) {
      ((VRReportLocalizationWriter)writer).genField(getIdent(), label, help);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final CType	COMPUTE_TYPE = CReferenceType.lookup(org.kopi.vkopi.lib.report.VCalculateColumn.class.getName().replace('.','/'));
  private static final CType	FORMAT_TYPE = CReferenceType.lookup(org.kopi.vkopi.lib.report.VCellFormat.class.getName().replace('.','/'));

  private String		ident;
  private String		label;
  private String		help;
  private VRFieldType		type;
  private int			align;
  private int			options;
  private String		group;
  private int			groupID = -1;
  private VKTrigger[]		triggers;
  private VKCommand[]		commands;
  private Commandable		commandable;

  private VRReport		parent;
  private int			index;
}
