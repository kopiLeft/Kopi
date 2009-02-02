/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.Utils;

/**
 * JLS 6.5.6 Expression Names.
 */
public class JNameExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JNameExpression(TokenReference where, JExpression prefix, String ident) {
    super(where);

    this.prefix = prefix;
    this.ident = ident.intern();
    verify(ident.indexOf('.') == -1); // $$$
    verify(ident.indexOf('/') == -1);
  }

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JNameExpression(TokenReference where, String ident) {
    this(where, null, ident);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (NO ACCESS) // it is a temporary node
  // ----------------------------------------------------------------------

  /**
   * Constructs a sequence of name expressions for a qualified name.
   *
   * @param	where		the position of this node in the parsing tree
   * @param	name		the fully qualified name
   */
  public static JNameExpression build(TokenReference where, String name) {
    verify(name.indexOf('.') == -1);

    String[]		splitted;
    JExpression		prefix;

    splitted = Utils.splitQualifiedName(name, '/');
    if (splitted[0] == "") {
      prefix = null;
    } else {
      prefix = JNameExpression.build(where, splitted[0]);
    }
    return new JNameExpression(where, prefix, splitted[1]);
  }
  
  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return null;
  }

  /**
   * @return the name of this name expression
   */
  public String getName() {
    return ident;
  }

  /**
   * @return the prefix of this name expression
   */
  public JExpression getPrefix() {
    return prefix;
  }

  /**
   * Returns the longest name available
   */
  public String getQualifiedName() {
    String str = getName();

    if (prefix == null) {
      return str;
    } else if (prefix instanceof JNameExpression) {
      return ((JNameExpression)prefix).getQualifiedName() + "." + str;
    } else if (prefix instanceof JTypeNameExpression) {
      return ((JNameExpression)prefix) + "." + str;
    } else {
      return str;
    }
  }

  /**
   * Returns a string representation of this object.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    if (prefix != null) {
      buffer.append(prefix.toString());
      buffer.append(".");
    }
    buffer.append(ident);
    return buffer.toString();
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

    try {
      // 6.5.2 Reclassification of Contextually Ambiguous Names
      // If the AmbiguousName is a simple name, consisting of a single Identifier:
      if (prefix == null) {
        if (!context.isTypeName()) {
          // If the Identifier appears within the scope of a local variable declaration or parameter
          JLocalVariable	var = context.lookupLocalVariable(ident);

          if (var != null) {
            return new JLocalVariableExpression(getTokenReference(), var).analyse(context);
          }

          // Otherwise, consider the class or interface C within whose declaration the Identifier occurs
          if (context.lookupField(context.getClassContext().getCClass(), null, ident) != null) {
            return createClassField(getTokenReference(), ident).analyse(context);
          }

          // If the Identifier appears within (an outer) scope of a local variable declaration or parameter
          JExpression outer = context.getBlockContext().lookupOuterLocalVariable(getTokenReference(), ident);
          if (outer != null) {
            return outer.analyse(context);
          }

          // Otherwise, outers
          if (context.getClassContext().lookupOuterField(context.getClassContext().getCClass(), null, ident) != null) {
            return createClassField(getTokenReference(), ident).analyse(context);
          }
        }

	try {
	  // Otherwise: TypeName
	  CReferenceType	type = context.getTypeFactory().createType(getTokenReference(), ident, false);

	  type = (CReferenceType)type.checkType(context);
	  return new JTypeNameExpression(getTokenReference(), type).analyse(context);
	} catch (UnpositionedError cue) {
	  // Otherwise: PackageName
	  throw new CLineError(getTokenReference(), KjcMessages.VAR_UNKNOWN, ident);
	}
      }

      verify(prefix != null);

      try {
	prefix = prefix.analyse(new CExpressionContext(context, context.getEnvironment(), false, false));
      } catch (CLineError cue) {
	if (cue.hasDescription(KjcMessages.VAR_UNKNOWN)) {
	  return convertToPackageName(cue, context).analyse(context);
	} else {
	  throw cue;
	}
      }

      // If the name to the left of the "." is reclassified as a TypeName
      if (prefix instanceof JTypeNameExpression) {
	if (((JTypeNameExpression)prefix).getClassType().getCClass().lookupField(context.getClassContext().getCClass(), prefix.getType(factory).getCClass(), ident) != null) {
	  // there is a field
	  return createClassField(getTokenReference(),  prefix, ident, factory).analyse(context);
	} else {
	  // no field => should be a type name
	  return new JTypeNameExpression(getTokenReference(),
					 context.getTypeFactory().createType(getTokenReference(), ((JTypeNameExpression)prefix).getQualifiedName() + "/" + ident, false)).analyse(context);
	}
      }

      // If the name to the left of the "." is reclassified as an ExpressionName

      if (ident == Constants.JAV_LENGTH) {
	// is it an array access ?
	if (prefix.getType(factory).isArrayType()) {
	  return new JArrayLengthExpression(getTokenReference(), new JCheckedExpression(getTokenReference(),  prefix)).analyse(context);
	}
      }
      return createClassField(getTokenReference(), prefix, ident, factory).analyse(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }
  }

  /**
   * Try to convert to a package name
   */
  private JExpression convertToPackageName(CLineError cue, CExpressionContext context) throws PositionedError {
    try {
      if (prefix instanceof JNameExpression) {
	CReferenceType	type = context.getTypeFactory().createType(getTokenReference(), ((JNameExpression)prefix).getName() + '/' + ident, false);

	type = (CReferenceType)type.checkType(context);
	return new JTypeNameExpression(getTokenReference(), type);
      } else if (!(prefix instanceof JTypeNameExpression)) {
	throw new CLineError(getTokenReference(), KjcMessages.VAR_UNKNOWN, ident);
      } else {
        return prefix;
      }
    } catch (UnpositionedError cue2) {
      // Otherwise: PackageName
      ident = ((JNameExpression)prefix).getName() + '/' +  ident;
      throw new CLineError(getTokenReference(), KjcMessages.VAR_UNKNOWN, ident);
    }
  }

  /**
   * Since class field may be overloaded in sub compiler, this method allow
   * you to specifie the type of class field without needed to touch
   * the huge method above !
   */
  protected JFieldAccessExpression createClassField(TokenReference ref,
                                                    JExpression prefix,
                                                    String ident,
                                                    TypeFactory factory)
  {
    return new JFieldAccessExpression(ref, new JCheckedExpression(getTokenReference(), prefix), ident);
  }

  /**
   * Since class field may be overloaded in sub compiler, this method allow
   * you to specifie the type of class field without needed to touch
   * the huge method above !
   */
  protected JFieldAccessExpression createClassField(TokenReference ref,
						   String ident)
  {
    return new JFieldAccessExpression(ref, ident);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitNameExpression(this, prefix, ident);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    throw new InconsistencyException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression		prefix;
  private String		ident;
}
