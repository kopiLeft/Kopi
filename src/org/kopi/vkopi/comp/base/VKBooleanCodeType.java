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
import org.kopi.kopi.comp.kjc.CStdType;
import org.kopi.kopi.comp.kjc.JBooleanLiteral;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.util.base.NotImplementedException;
import org.kopi.xkopi.comp.database.DatabaseBooleanColumn;
import org.kopi.xkopi.comp.database.DatabaseColumn;
/**
 * This class represents the definition of a type
 */
public class VKBooleanCodeType extends VKCodeType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * !!!
   *
   * @param where		the token reference of this node
   * @param pack                the package name of the class defining the type
   * @param type		the identifier of the type definition
   * @param codes		a list of code pairs
   */
  public VKBooleanCodeType(TokenReference where,
                           String pack, 
                           String type,
                           VKCodeDesc[] codes)
  {
    super(where, pack, type, codes);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the column viewer
   */
  public CReferenceType getListColumnType() {
    return VKStdType.VBooleanCodeColumn;
  }

  public JExpression genValues() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[codes.length];

    for (int i = 0; i < codes.length; i++) {
      init[i] = new JBooleanLiteral(ref, codes[i].getBoolean());
    }
    return VKUtils.createArray(ref, CStdType.Boolean, init);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getDefaultType() {
    return org.kopi.vkopi.comp.trig.GStdType.BooleanCodeField;
  }

  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public DatabaseColumn getColumnInfo(){
    return new DatabaseBooleanColumn(true);
  }


  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CReferenceType getReportType() {
    return org.kopi.vkopi.comp.trig.GStdType.ReportBooleanCodeColumn;
  }
  
  /**
   * @Override
   */
  public CReferenceType getDimensionChartType() {
    return org.kopi.vkopi.comp.trig.GStdType.ChartBooleanCodeDimension;
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
    p.printCodeType("BOOL", codes);
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
}
