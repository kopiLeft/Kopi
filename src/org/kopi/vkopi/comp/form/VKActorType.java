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

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JIntLiteral;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;
import org.kopi.vkopi.comp.base.VKType;
import org.kopi.xkopi.comp.database.DatabaseColumn;

public class VKActorType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  
  public VKActorType(TokenReference where) {
    super(where, 1, 1);
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * @Override
   */
  public CReferenceType getListColumnType() {
    return null;
  }

  /**
   * @Override
   */
  public JExpression genConstructor() {
    TokenReference      ref = getTokenReference();
    
    return new JUnqualifiedInstanceCreation(ref,
                                            getType(),
                                            new JExpression[] {
                                              new JIntLiteral(ref, getWidth()),
                                              new JIntLiteral(ref, getHeight())
                                            });
  }

  /**
   * @Override
   */
  public CReferenceType getDefaultType() {
    return org.kopi.vkopi.comp.trig.GStdType.ActorField;
  }

  /**
   * @Override
   */
  public DatabaseColumn getColumnInfo() {
    throw new NotImplementedException();
  }

  /**
   * @Override
   */
  public CReferenceType getReportType() {
    throw new NotImplementedException();
  }

  /**
   * @Override
   */
  public CReferenceType getDimensionChartType() {
    throw new NotImplementedException();
  }

  /**
   * @Override
   */
  public CReferenceType getMeasureChartType() {
    throw new NotImplementedException();
  }

  /**
   * @Override
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
  }
}
