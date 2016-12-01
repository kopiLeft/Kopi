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
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.xkopi.comp.database.DatabaseColumn;
import org.kopi.xkopi.comp.database.DatabaseStringColumn;

/**
 * This class represents the definition of a type
 */
public class VKStringType extends VKType implements VConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param width		the width in char of this field
   * @param height		the height in char of this field
   */
  public VKStringType(TokenReference where,
                      int width,
                      int height,
                      int visibleHeight,
                      int convert)
  {
    super(where, width, height, visibleHeight);
    this.convert = convert;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    super.checkCode(context);
    if (getHeight() == 1 && (convert & FDO_DYNAMIC_NL) > 0) {
      throw new PositionedError(getTokenReference(), BaseMessages.BAD_DYNAMIC_NEW_LINE);
    }
    if (getHeight() == 1 && (convert & FDO_FIX_NL) > 0) {
      throw new PositionedError(getTokenReference(), BaseMessages.BAD_FIXED_NEW_LINE);
    }
    if ((convert & (FDO_FIX_NL | FDO_DYNAMIC_NL)) == (FDO_FIX_NL | FDO_DYNAMIC_NL)) {
      throw new PositionedError(getTokenReference(), BaseMessages.BAD_DYNAMIC_AND_FIXED);
    }
    if (getHeight() > 1 && (convert & (FDO_FIX_NL | FDO_DYNAMIC_NL)) == 0) {
      throw new PositionedError(getTokenReference(), BaseMessages.UNSET_DYNAMIC_AND_FIXED);
    }
  }

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VStringColumn;
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

    JExpression[]	exprs;

    if (getVisibleHeight() != getHeight()) {
      exprs = new JExpression[] {
	new JIntLiteral(ref, getWidth()),
	new JIntLiteral(ref, getHeight()),
	new JIntLiteral(ref, getVisibleHeight()),
	new JIntLiteral(ref, convert & (FDO_CONVERT_MASK | FDO_DYNAMIC_NL))};
    } else {
      exprs = new JExpression[] {
	new JIntLiteral(ref, getWidth()),
	new JIntLiteral(ref, getHeight()),
	new JIntLiteral(ref, convert & FDO_CONVERT_MASK | FDO_DYNAMIC_NL)};
    }

    return new JUnqualifiedInstanceCreation(ref, getType(), exprs);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getDefaultType() {
    return org.kopi.vkopi.comp.trig.GStdType.StringField;
  }

  /**
   * Returns the type for the type-checking mechanism of dbi. The type of 
   * the field in the Database.k class must be "implicitly castable" to this type. 
   *
   * @return the type
   */
  public CType getStandardType() {
    return org.kopi.kopi.comp.kjc.CStdType.String;
  }

  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    return new DatabaseStringColumn(true, getWidth(), getHeight());
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return org.kopi.vkopi.comp.trig.GStdType.ReportStringColumn;
  }
  
  /**
   * @Override
   */
  public CReferenceType getDimensionChartType() {
    return org.kopi.vkopi.comp.trig.GStdType.ChartStringDimension;
  }
  
  /**
   * @Override
   */
  public CReferenceType getMeasureChartType() {
    throw new NotImplementedException();
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
    p.printStringType(getWidth(), getHeight(), convert);
    // !!!
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int			convert;
}
