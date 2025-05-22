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

package org.kopi.vkopi.comp.base;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JBooleanLiteral;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.xkopi.comp.database.DatabaseColumn;
import org.kopi.xkopi.comp.database.DatabaseFixedColumn;
import org.kopi.xkopi.lib.type.Fixed;

/**
 * This class represents the definition of a fixed type
 */
public class VKFixnumType extends VKType {

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
  public VKFixnumType(TokenReference where,
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
   * Returns the scale of this fixnum type.
   */
  public int getScale() {
    return scale;
  }

  /**
   * Returns the default alignment
   */
  public int getDefaultAlignment() {
    return org.kopi.vkopi.lib.form.VConstants.ALG_RIGHT;
  }

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VFixnumColumn;
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getDefaultType() {
    return org.kopi.vkopi.comp.trig.GStdType.FixnumField;
  }


  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo() {
    return new DatabaseFixedColumn(true, getWidth(), scale, min, max);
  }
  
  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return org.kopi.vkopi.comp.trig.GStdType.ReportFixnumColumn;
  }
  
  /**
   * @Override
   */
  public CReferenceType getDimensionChartType() {
    return org.kopi.vkopi.comp.trig.GStdType.ChartFixnumDimension;
  }
  
  /**
   * @Override
   */
  public CReferenceType getMeasureChartType() {
    return org.kopi.vkopi.comp.trig.GStdType.ChartFixnumMeasure;
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

  // ----------------------------------------------------------------------
  // Galite CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param visitor the visitor
   */
  @Override
  public void accept(VKVisitor visitor) {}

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final int             scale;
  private final boolean         fraction;
  private final Fixed           min;
  private final Fixed           max;
}
