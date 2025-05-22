/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.comp.chart;

import java.util.Vector;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JAssignmentExpression;
import org.kopi.kopi.comp.kjc.JCastExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.vkopi.comp.base.VKCodeType;
import org.kopi.vkopi.comp.base.VKCommand;
import org.kopi.vkopi.comp.base.VKContext;
import org.kopi.vkopi.comp.base.VKFixnumType;
import org.kopi.vkopi.comp.base.VKTrigger;
import org.kopi.vkopi.comp.base.VKUtils;
import org.kopi.vkopi.comp.base.VKVisitor;

public class VCDimension extends VCField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
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
  public VCDimension(TokenReference where,
                     String ident,
                     String label,
                     String help,
                     VCFieldType type,
                     VKCommand[] commands,
                     VKTrigger[] triggers)
  {
    super(where, ident, label, help, type, commands, triggers);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------

  /**
   * @Override
   */
  protected void checkType(VKContext context) throws PositionedError {
    // nothing to do.
  }
  
  /**
   * @Override
   */
  public JExpression genConstructorCall() {
    TokenReference		ref = getTokenReference();
    Vector<JExpression>		params = new Vector<JExpression>(8);

    params.addElement(VKUtils.toExpression(ref, ident));
    params.addElement(getFormat());
    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genType());
      params.addElement(((VKCodeType)type.getDef()).genSource());
    }
    if (type.getDef() instanceof VKFixnumType) {
      // remove
      params.addElement(VKUtils.toExpression(ref, ((VKFixnumType)type.getDef()).getScale()));
      params.addElement(VKUtils.toExpression(ref, true));
    }

    if (type.getDef() instanceof VKCodeType) {
      params.addElement(((VKCodeType)type.getDef()).genIdents());
      params.addElement(((VKCodeType)type.getDef()).genValues());
    }

    return new JAssignmentExpression(ref,
				     getThis(),
				     new JUnqualifiedInstanceCreation(ref,
                                                                      type.getDef().getDimensionChartType(),
                                                                      (JExpression[])org.kopi.util.base.Utils.toArray(params, JExpression.class)));
  }

  /**
   * Generate a class for this element
   */
  public JFieldDeclaration genCode(CParseClassContext context) {
    return VKUtils.buildFieldDeclaration(getTokenReference(),
					 /*ACC_FINAL !!!*/0,
					 type.getDef().getDimensionChartType(),
					 getIdent() + "_",
					 null);
  }

  private JExpression getFormat() {
    TokenReference	ref = getTokenReference();

    for (int i = 0; i < triggers.length; i++) {
      if ((triggers[i].getEvents() & (1 << org.kopi.vkopi.lib.chart.CConstants.TRG_FORMAT)) > 0) {
	JExpression	expr;

	expr = new JMethodCallExpression(ref,
					 null,
					 "callTrigger",
					 new JExpression[]{
					   VKUtils.toExpression(ref, org.kopi.vkopi.lib.chart.CConstants.TRG_FORMAT),
					   VKUtils.toExpression(ref, getIndex() + 1)
					 });
	return new JCastExpression(ref, expr, FORMAT_TYPE);
      }
    }

    return VKUtils.toExpression(ref, (String)null);
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

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  @SuppressWarnings("deprecation")
  private static final CType	FORMAT_TYPE = CReferenceType.lookup(org.kopi.vkopi.lib.chart.VColumnFormat.class.getName().replace('.','/'));
}
