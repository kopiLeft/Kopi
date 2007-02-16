/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.base;

import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents the definition of a type
 */
public abstract class VKCodeType extends VKType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * !!!
   *
   * @param where		the token reference of this node
   * @param pack                the package name of the class defining the type
   * @param type		the identifier of the type definition
   * @param codes		a list of code pair
   */
  public VKCodeType(TokenReference where,
                    String pack, 
                    String type,
                    VKCodeDesc[] codes)
  {
    super(where, 0, 0);
    this.source = pack == null ? null : pack + "/" + where.getName().substring(0, where.getName().lastIndexOf('.'));
    this.type = type;
    this.codes = codes;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Not allowed here
   */
  public void addList(VKFieldList l) {
    throw new InconsistencyException("LIST NOT ALLOWED IN CODE !!!");
  }

  /**
   * return whether this type support auto fill command
   */
  public boolean hasAutofill() {
    return true;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genConstructor() {
    TokenReference      ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
                                            getType(),
                                            new JExpression[]{
                                              VKUtils.toExpression(ref, type),
                                              VKUtils.toExpression(ref, source),
                                              genIdents(),
                                              genValues()
                                            });
  }

  /**
   * Generates the type
   */
  public JExpression genType() {
    return new JStringLiteral(getTokenReference(), type);
  }

  /**
   * Generates the source
   */
  public JExpression genSource() {
    return new JStringLiteral(getTokenReference(), source);
  }

  /**
   * Generates the names of this type
   */
  public JExpression genIdents() {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[codes.length];

    for (int i = 0; i < codes.length; i++) {
      init[i] = new JStringLiteral(ref, codes[i].getIdent());
    }
    return VKUtils.createArray(ref, CStdType.String, init);
  }

  /**
   * Generates the names of this type
   */
  public JExpression genLabels()  {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[codes.length];

    for (int i = 0; i < codes.length; i++) {
      init[i] = new JStringLiteral(ref, codes[i].getLabel());
    }
    return VKUtils.createArray(ref, CStdType.String, init);
  }

  /**
   * Generate the value of this type
   */
  public abstract JExpression genValues();

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public abstract void genVKCode(VKPrettyPrinter p);

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates localization.
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genCodeType(codes);
    super.genLocalization(writer);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String          source;
  private final String          type;
  protected VKCodeDesc[]        codes;
}
