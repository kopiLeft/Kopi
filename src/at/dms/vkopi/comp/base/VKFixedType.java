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

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.database.DatabaseColumn;
import at.dms.xkopi.comp.database.DatabaseFixedColumn;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CStdType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.JBooleanLiteral;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JIntLiteral;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.xkopi.lib.type.Fixed;

/**
 * This class represents the definition of a fixed type
 */
public class VKFixedType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new fixed type.
   *
   * @param where		the token reference of this node
   * @param width		the width in char of this field
   * @param scale		the number of digits after the period sign
   * @param fraction		should fractional parts be displayed as fraction ?
   * @param min			the min value
   * @param max			the max value
   */
  public VKFixedType(TokenReference where,
		     int width,
		     int scale,
		     boolean fraction,
		     Fixed min,
		     Fixed max)
  {
    super(where, width, 1);
    this.scale = scale;
    this.fraction = fraction;
    this.min = min;
    this.max = max;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public int getScale() {
    return scale;
  }

  /**
   * Returns the default alignment
   */
  public int getDefaultAlignment() {
    return at.dms.vkopi.lib.form.VConstants.ALG_RIGHT;
  }

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VFixedColumn;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return at.dms.vkopi.comp.trig.GStdType.FixedField;
  }


  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    if ((min != null) || (max != null)) return new DatabaseFixedColumn(true, min, max);
    return new DatabaseFixedColumn(true);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return at.dms.vkopi.comp.trig.GStdType.FixedColumn;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructor() {
    TokenReference	ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
				    getType(),
				    new JExpression[] {
				      new JIntLiteral(ref, getWidth()),
				      new JIntLiteral(ref, scale),
				      new JBooleanLiteral(ref, fraction),
				      VKUtils.toExpression(getTokenReference(), min),
				      VKUtils.toExpression(getTokenReference(), max)
				    });
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
    genComments(p);
    p.printFixedType(getWidth(), scale, fraction, min, max);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final int		scale;
  private final boolean		fraction;
  private final Fixed		min;
  private final Fixed		max;
}
