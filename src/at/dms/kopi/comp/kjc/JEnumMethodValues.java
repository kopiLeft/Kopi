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

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.CTypeVariable;
import at.dms.kopi.comp.kjc.JBlock;
import at.dms.kopi.comp.kjc.JFormalParameter;

/**
 * This class represents a Java method declaration in the syntax tree.
 */
public class JEnumMethodValues extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a method declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param enumType    the enum class where this method is declared
   * @param valuesField the name of the values field in this class
   * */
  public JEnumMethodValues(TokenReference where,
 			               CType enumType,
                           String valuesField)
  {
    super(where,
          ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
          CTypeVariable.EMPTY,
          new CArrayType(enumType, 1),
          "values",
          JFormalParameter.EMPTY,
          CReferenceType.EMPTY,
          null,
          null, 
          JavaStyleComment.EMPTY);

    super.body = createBody(where, valuesField, enumType);

  }

  private JBlock createBody(TokenReference where, String valuesField, CType enumType) {
      JFieldAccessExpression   accessField;
      JMethodCallExpression    clone;
      JCastExpression          cast;
      JReturnStatement         returnStatment;
    
      accessField = new JFieldAccessExpression(where, valuesField);
      clone = new JMethodCallExpression(where, accessField, "clone", JExpression.EMPTY);
      cast = new JCastExpression(where, clone, new CArrayType(enumType, 1));
      returnStatment = new JReturnStatement(where, cast, JavaStyleComment.EMPTY);
      
      return new JBlock(where, 
                        new JStatement[] {returnStatment},
                        JavaStyleComment.EMPTY);
      }
}
