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

package org.kopi.vkopi.comp.chart;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.JThisExpression;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.Commandable;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKDefinitionCollector;
import org.kopi.vkopi.comp.base.VKLocalizationWriter;
import org.kopi.vkopi.comp.base.VKPhylum;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKTrigger;

public abstract class VCField extends VKPhylum implements VCConstants, org.kopi.kopi.comp.kjc.Constants {

  /**
   * This class represents the field definition of a chart
   *
   * @param where		the token reference of this node
   * @param ident		the ident of this field
   * @param label		the label (text on the left)
   * @param help		the help text
   * @param type		the type of this field
   * @param commands		the commands accessible in this field
   * @param triggers		the triggers executed by this field
   */
  public VCField(TokenReference where,
		 String ident,
		 String label,
		 String help,
		 VCFieldType type,
		 VKCommand[] commands,
		 VKTrigger[] triggers)
  {
    super(where);
    this.ident = ident;
    this.label = label;
    this.help = help;
    this.type = type;
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
  public VCChart getChart() {
    return parent;
  }

  /**
   * Returns a collector for definitiion
   */
  public VKDefinitionCollector getDefinitionCollector() {
    return getChart().getDefinitionCollector();
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
   * @param report	the actual context of analyze
   * @exception	PositionedError	Error caught as soon as possible
   */
  public void checkCode(VKContext context, VCChart chart) throws PositionedError {
    commandable = new Commandable(getIdent() + "_", chart.getCommandable());

    this.index = chart.getFieldIndex(getIdent());
    parent = chart;
    type.checkCode(context, this, commandable);
    checkType(context);
    if (ident == null) {
      ident = "ANM_" + parent.getNextSyntheticNumber();
    }

    commandable.checkCode(context, chart.getDefinitionCollector(), commands, triggers);
  }

  // ----------------------------------------------------------------------
  // ABSTRACT METHODS
  // ----------------------------------------------------------------------
  
  /**
   * Checks the type of this field.
   * For dimensions, we allow all types but for measures we allow only number types.
   * @param context The compilation context.
   * @throws PositionedError Error caught as soon as possible
   */
  protected abstract void checkType(VKContext context) throws PositionedError;
  
  /**
   * Generates the field constructor.
   */
  public abstract JExpression genConstructorCall();
  
  /**
   * Generates the field declaration.
   * @param context The parse context.
   * @return The field declaration.
   */
  public abstract JFieldDeclaration genCode(CParseClassContext context);

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the trigger array
   */
  public int[] getTriggerArray() {
    return commandable.getTriggerArray();
  }

  /**
   * Returns the field type.
   */
  public VCFieldType getType() {
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
    ((VCChartLocalizationWriter)writer).genField(getIdent(), label, help);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected String		ident;
  protected String		label;
  protected String		help;
  protected VCFieldType		type;
  protected VKTrigger[]		triggers;
  protected VKCommand[]		commands;
  protected Commandable		commandable;

  protected VCChart		parent;
  protected int			index;
}
