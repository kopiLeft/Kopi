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
 * $Id: VKStringType.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.database.DatabaseColumn;
import at.dms.xkopi.comp.database.DatabaseStringColumn;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CStdType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JIntLiteral;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;

/**
 * This class represents the definition of a type
 */
public class VKStringType extends VKType {

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
	new JIntLiteral(ref, convert)};
    } else {
      exprs = new JExpression[] {
	new JIntLiteral(ref, getWidth()),
	new JIntLiteral(ref, getHeight()),
	new JIntLiteral(ref, convert)};
    }

    return new JUnqualifiedInstanceCreation(ref, getType(), exprs);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getType() {
    return at.dms.vkopi.comp.trig.GStdType.StringField;
  }

  /**
   * Returns the type for the type-checking mechanism of dbi. The type of 
   * the field in the Database.k class must be "implicitly castable" to this type. 
   *
   * @return the type
   */
  public CType getStandardType() {
    return at.dms.kopi.comp.kjc.CStdType.String;
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
    return at.dms.vkopi.comp.trig.GStdType.StringColumn;
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
