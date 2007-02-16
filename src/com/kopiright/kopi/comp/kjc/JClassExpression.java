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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.bytecode.classfile.PushLiteralInstruction;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;

/**
 * A 'int.class' expression
 */
public class JClassExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JClassExpression(TokenReference where, CType type, int bounds) {
    this(where, type, null, bounds);
  }

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JClassExpression(TokenReference where, JExpression prefix, int bounds) {
    this(where, null, prefix, bounds);
  }

  private JClassExpression(TokenReference where, 
                           CType type,
                           JExpression prefix,
                           int bounds)
  {
    super(where);

    this.type = type;
    this.prefix = prefix;
    this.bounds = bounds;
  }
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return factory.createReferenceType(TypeFactory.RFT_CLASS);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    if (prefix != null) {
      CExpressionContext        exprContext = new CExpressionContext(context, context.getEnvironment());

      exprContext.setIsTypeName(true);
      prefix = prefix.analyse(exprContext);
      check(context,
	    prefix instanceof JTypeNameExpression,
	    KjcMessages.CLASS_BAD_PREFIX, prefix);
      type = prefix.getType(factory);
    }

    if (bounds > 0) {
      type = new CArrayType(type, bounds);
    }

    try {
      type = type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

  acc:
    if (!type.isPrimitive() && type.getTypeID() != TID_VOID) {
      CClass    clazz;

      if (type.isArrayType()) {
        if (((CArrayType) type).getBaseType().isPrimitive()) {
          break acc;
        }
        clazz = ((CArrayType) type).getBaseType().getCClass();
      } else {
        clazz = type.getCClass();
      }
      check(context, 
            clazz.isAccessible(context.getClassContext().getCClass()),
            KjcMessages.CLASS_NOACCESS, 
            type.getCClass());
    }

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitClassExpression(this, type, prefix, bounds);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    String		className;

    switch (type.getTypeID()) {
    case TID_NULL:
    case TID_CLASS:
    case TID_ARRAY:
      code.plantInstruction(new PushLiteralInstruction(((CReferenceType)type).getQualifiedName().replace('/', '.')));
      code.plantMethodRefInstruction(opc_invokestatic,
				     "java/lang/Class",
				     "forName",
				     "(Ljava/lang/String;)Ljava/lang/Class;");
      className = null;
      break;
    case TID_BOOLEAN:
      className = "java/lang/Boolean";
      break;
    case TID_BYTE:
      className = "java/lang/Byte";
      break;
    case TID_CHAR:
      className = "java/lang/Character";
      break;
    case TID_DOUBLE:
      className = "java/lang/Double";
      break;
    case TID_FLOAT:
      className = "java/lang/Float";
      break;
    case TID_INT:
      className = "java/lang/Integer";
      break;
    case TID_LONG:
      className = "java/lang/Long";
      break;
    case TID_SHORT:
      className = "java/lang/Short";
      break;
    case TID_VOID:
      className = "java/lang/Void";
      break;
    default:
      throw new InconsistencyException();
    }
    if (className != null) {
      code.plantFieldRefInstruction(opc_getstatic,
                                    className,
                                    "TYPE",
                                    factory.createReferenceType(TypeFactory.RFT_CLASS).getSignature());
    }

    if (discardValue) {
      code.plantPopInstruction(type);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CType			type;
  private JExpression		prefix;
  private int			bounds;
}
