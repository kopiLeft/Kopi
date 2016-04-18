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

package com.kopiright.vkopi.comp.trig;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CExpressionContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JArrayAccessExpression;
import com.kopiright.kopi.comp.kjc.JCheckedExpression;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JLogicalComplementExpression;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.xkjc.XAddExpression;
import com.kopiright.xkopi.comp.xkjc.XBitwiseExpression;
import com.kopiright.xkopi.comp.xkjc.XCastExpression;
import com.kopiright.xkopi.comp.xkjc.XDivideExpression;
import com.kopiright.xkopi.comp.xkjc.XExpression;
import com.kopiright.xkopi.comp.xkjc.XKjcPrettyPrinter;
import com.kopiright.xkopi.comp.xkjc.XMethodCallExpression;
import com.kopiright.xkopi.comp.xkjc.XMinusExpression;
import com.kopiright.xkopi.comp.xkjc.XModuloExpression;
import com.kopiright.xkopi.comp.xkjc.XMultExpression;
import com.kopiright.xkopi.comp.xkjc.XNameExpression;
import com.kopiright.xkopi.comp.xkjc.XShiftExpression;
import com.kopiright.xkopi.comp.xkjc.XStdType;

/**
 * This class represents unary expressions.
 */
public class GVKAccess extends XExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	oper		the operator
   * @param	expr		the operand
   */
  public GVKAccess(TokenReference where,
		   String name,
		   JExpression field,
		   JExpression record,
		   boolean hasBang,
		   boolean hasQuestion)
  {
    super(where);
    this.name = name;
    if (record == null) {
      this.record = field;
      this.field = null;
    } else {
      this.record = record;
      this.field = field;
    }
    this.mode = hasQuestion ? MOD_ISNOTNULL : hasBang ? MOD_FORCE : MOD_NULLABLE;
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
    TokenReference	ref = getTokenReference();
    JExpression[]	params = JExpression.EMPTY;
    JExpression		expr = checkAccessExpression(context);

    if (record != null) {
      record = record.analyse(context);
      check(context, record.isAssignableTo(context, context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT)), GKjcMessages.BAD_RECORD_ACCESS);
      params = new JExpression[]{ new JCheckedExpression(ref, record) };
    }
    switch (mode) {
    case MOD_NULLABLE:
      return new JMethodCallExpression(ref,
				       expr,
				       TYPE_OPERATORS[getType(expr.getType(factory))][OPE_READ],
				       params).analyse(context);
    case MOD_FORCE:
      return new XCastExpression(expr.getTokenReference(),
				 new JMethodCallExpression(ref,
							   expr,
							   TYPE_OPERATORS[getType(expr.getType(factory))][OPE_READ_FORCE],
							   params),
				 translateToPrimitive(expr.getType(factory)),
                                 true).analyse(context);
    case MOD_ISNOTNULL:
      return new JLogicalComplementExpression(ref,
					      new JMethodCallExpression(ref,
									expr,
									"isNull",
									params)
					      ).analyse(context);
    default:
      throw new InconsistencyException();
    }
  }

  /**
   * Check access
   */
  private JExpression checkAccessExpression(CExpressionContext context) throws PositionedError {
    JExpression		expr;
    TypeFactory         factory = context.getTypeFactory();

    expr = XNameExpression.build(getTokenReference(), name.replace('.', '/'));
    if (field != null) {
      expr = new JArrayAccessExpression(getTokenReference(), expr, field);
    }
    expr = expr.analyse(context);
    if (!expr.getType(factory).getCClass().descendsFrom(GStdType.Field.getCClass())) {
      // may be its an array access
      check(context, expr.getType(factory).isArrayType() && field == null, GKjcMessages.ACCESS_NOT_FIELD);
      field = record;
      record = null;
      check(context, field != null, GKjcMessages.ACCESS_NOT_FIELD);
      expr = new JArrayAccessExpression(getTokenReference(),
					new JCheckedExpression(getTokenReference(),
							       expr),
					field);
      expr = expr.analyse(context);
    }
    return expr;
  }

  // ----------------------------------------------------------------------
  // ASSIGNMENT METHODS
  // ----------------------------------------------------------------------

  /**
   * Generate the code to assign a value to this field if possible
   */
  public JExpression generateAssignment(JExpression right,
					int oper,
					CExpressionContext context)
    throws PositionedError
  {
    return generateAssignment(this, right, oper, context);
  }

  /**
   * Generate the code to assign a value to this field if possible
   */
  public static JExpression generateAssignment(JExpression left,
					       JExpression right,
					       int oper,
					       CExpressionContext context)
    throws PositionedError
  {
    TypeFactory         factory = context.getTypeFactory();
    TokenReference	ref = left.getTokenReference();
    JExpression		self = ((GVKAccess)left).checkAccessExpression(context);
    JExpression		record = ((GVKAccess)left).record;
    left = left.analyse(context);
    right = right.analyse(context);
    if (record != null) {
      record = record.analyse(context);
      if (!record.isAssignableTo(context, context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT))) {
	// !!! ADD TO ERRORS
	throw new PositionedError(left.getTokenReference(), GKjcMessages.BAD_RECORD_ACCESS);
      }
    }

    right = convert(right, getType(self.getType(factory)), factory);
    JExpression	expr = null;

    switch (oper) {
    case OPE_SIMPLE:
      expr = right;
      break;
    case OPE_STAR:
      expr = new XMultExpression(ref, left, right);
      break;
    case OPE_SLASH:
      expr = new XDivideExpression(ref, left, right);
      break;
    case OPE_PERCENT:
      expr = new XModuloExpression(ref, left, right);
      break;
    case OPE_PLUS:
      expr = new XAddExpression(ref, left, right);
      break;
    case OPE_MINUS:
      expr = new XMinusExpression(ref, left, right);
      break;
    case OPE_SL:
    case OPE_SR:
    case OPE_BSR:
      expr = new XShiftExpression(ref, oper, left, right);
      break;
    case OPE_BAND:
    case OPE_BXOR:
    case OPE_BOR:
      expr = new XBitwiseExpression(ref, oper, left, right);
      break;
    default:
      throw new InconsistencyException("UNDEFINED OPERATOR:" + oper);
    }

    return new XMethodCallExpression(ref,
				     self,
				     TYPE_OPERATORS[getType(self.getType(factory))][OPE_WRITE],
				     record != null ?
				     new JExpression[]{ new JCheckedExpression(ref, record), expr } :
				     new JExpression[]{ expr }
				     ).analyse(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param p the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
    p.visitNameExpression(null, null, "@" + name);
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Returns which VField it is
   */
  private static int getType(CType type) {
    if (type.equals(GStdType.BooleanField)) {
      return TYP_BOOLEAN;
    } else if (type.equals(GStdType.IntegerField)) {
      return TYP_INTEGER;
    } else if (type.equals(GStdType.FixnumField)) {
      return TYP_FIXED;
    } else if (type.equals(GStdType.StringField)) {
      return TYP_STRING;
    } else if (type.equals(GStdType.TextField)) {
      return TYP_TEXT;
    } else if (type.equals(GStdType.DateField)) {
      return TYP_DATE;
    } else if (type.equals(GStdType.MonthField)) {
      return TYP_MONTH;
    } else if (type.equals(GStdType.TimeField)) {
      return TYP_TIME;
    } else if (type.equals(GStdType.TimestampField)) {
      return TYP_TIMESTAMP;
    } else if (type.equals(GStdType.WeekField)) {
      return TYP_WEEK;
    } else if (type.equals(GStdType.ColorField)) {
      return TYP_COLOR;
    } else if (type.equals(GStdType.ImageField)) {
      return TYP_IMAGE;
    } else if (type.equals(GStdType.BooleanCodeField)) {
      return TYP_BOOLEAN_CODE;
    } else if (type.equals(GStdType.FixnumCodeField)) {
      return TYP_FIXED_CODE;
    } else if (type.equals(GStdType.IntegerCodeField)) {
      return TYP_INTEGER_CODE;
    } else if (type.equals(GStdType.TextField)) {
      return TYP_TEXT;
    } else if (type.equals(GStdType.StringCodeField)) {
      return TYP_ENUM;
    } else {
      throw new InconsistencyException(">>>>>>>>>> " + type);
    }
  }

  /**
   * Returns the type of value accepted by a field
   */
  public static CReferenceType translate(CType type) {
    switch (getType(type)) {
    case TYP_FIXED:
    case TYP_FIXED_CODE:
      return XStdType.Fixed;
    case TYP_COLOR:
      return GStdType.Color;
    case TYP_DATE:
      return XStdType.Date;
    case TYP_IMAGE:
      return GStdType.Image;
    case TYP_MONTH:
      return XStdType.Month;
    case TYP_WEEK:
      return XStdType.Week;
    case TYP_STRING:
    case TYP_TEXT:
    case TYP_ENUM:
      return CStdType.String;
    case TYP_TIME:
      return XStdType.Time;
    case TYP_TIMESTAMP:
      return XStdType.Timestamp;
    case TYP_BOOLEAN:
    case TYP_BOOLEAN_CODE:
      return XStdType.Boolean;
    case TYP_INTEGER:
    case TYP_INTEGER_CODE:
      return XStdType.Int;
    default:
      throw new InconsistencyException("INTERNAL ERROR:" + type);
    }
  }

  /**
   * Returns the type of value accepted by a vrfield (primitive type)
   */
  public static CType translateToPrimitive(CType type) {
    switch (getType(type)) {
    case TYP_FIXED:
    case TYP_FIXED_CODE:
      return XStdType.PFixed;
    case TYP_COLOR:
      return GStdType.Color;
    case TYP_DATE:
      return XStdType.PDate;
    case TYP_IMAGE:
      return GStdType.Image;
    case TYP_MONTH:
      return XStdType.PMonth;
    case TYP_WEEK:
      return XStdType.PWeek;
    case TYP_STRING:
    case TYP_TEXT:
    case TYP_ENUM:
      return CStdType.String;
    case TYP_TIME:
      return XStdType.PTime;
    case TYP_TIMESTAMP:
      return XStdType.PTimestamp;
    case TYP_BOOLEAN:
    case TYP_BOOLEAN_CODE:
      return CStdType.Boolean;
    case TYP_INTEGER:
    case TYP_INTEGER_CODE:
      return CStdType.Integer;
    default:
      throw new InconsistencyException("INTERNAL ERROR:" + type);
    }
  }

  private static JExpression convert(JExpression expr, int type, TypeFactory factory) {
    CReferenceType ctype = null;

    if (expr.getType(factory).isReference()) {
      return expr;
    }

    switch (type) {
    case TYP_FIXED:
    case TYP_FIXED_CODE:
      return new XCastExpression(expr.getTokenReference(),
				 expr,
				 XStdType.Fixed,
                                 true);

    case TYP_COLOR:
    case TYP_DATE:
    case TYP_IMAGE:
    case TYP_MONTH:
    case TYP_WEEK:
    case TYP_STRING:
    case TYP_TIME:
    case TYP_FIELD:
      return expr;

    case TYP_INTEGER:
    case TYP_INTEGER_CODE:
      return new XCastExpression(expr.getTokenReference(),
				 expr,
				 XStdType.Int,
                                 true);

    case TYP_BOOLEAN:
    case TYP_BOOLEAN_CODE:
      ctype = XStdType.Boolean;
      break;

    default:
      throw new InconsistencyException(">>>>>>>>>>" + type);
    }
    return new JUnqualifiedInstanceCreation(expr.getTokenReference(),
				    ctype,
				    new JExpression[] {expr});
  }

  // ----------------------------------------------------------------------
  // PRIVATE CONSTANTS
  // ----------------------------------------------------------------------

  private static final int      OPE_READ        = 0;
  private static final int      OPE_READ_FORCE  = 1;
  private static final int      OPE_WRITE       = 2;

  private static final String[][] TYPE_OPERATORS = new String[][] {
    new String[]{ "getFixed", "getFixed", "setFixed" },
    new String[]{ "getBoolean", "getBoolean", "setBoolean" },
    new String[]{ "getBoolean", "getBoolean", "setBoolean" },
    new String[]{ "getColor", "getColor", "setColor" },
    new String[]{ "getDate", "getDate", "setDate" },
    new String[]{ "getFixed", "getFixed", "setFixed" },
    new String[]{ "getImage", "getImage", "setImage" },
    new String[]{ "getInt", "getInt", "setInt" },
    new String[]{ "getInt", "getInt", "setInt" },
    new String[]{ "getMonth", "getMonth", "setMonth" },
    new String[]{ "getString", "getString", "setString" },
    new String[]{ "getTime", "getTime", "setTime" },
    new String[]{ "getString", "getString", "setString" },
    new String[]{ "getObject", "getObject", "setObject" },
    new String[]{ "getWeek", "getWeek", "setWeek" },
    new String[]{ "getString", "getString", "setString" },
    new String[]{ "getTimestamp", "getTimestamp", "setTimestamp" },
  };

  private static final int TYP_FIXED		= 0;
  private static final int TYP_BOOLEAN		= 1;
  private static final int TYP_BOOLEAN_CODE	= 2;
  private static final int TYP_COLOR		= 3;
  private static final int TYP_DATE		= 4;
  private static final int TYP_FIXED_CODE	= 5;
  private static final int TYP_IMAGE		= 6;
  private static final int TYP_INTEGER		= 7;
  private static final int TYP_INTEGER_CODE	= 8;
  private static final int TYP_MONTH		= 9;
  private static final int TYP_STRING		= 10;
  private static final int TYP_TIME		= 11;
  private static final int TYP_TEXT		= 12;
  private static final int TYP_FIELD		= 13;
  private static final int TYP_WEEK		= 14;
  private static final int TYP_ENUM		= 15;
  private static final int TYP_TIMESTAMP	= 16;

  private static final int MOD_NULLABLE		= 0;
  private static final int MOD_FORCE		= 1;
  private static final int MOD_ISNOTNULL	= 2;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	name;
  private int		mode;
  private JExpression	field;
  private JExpression	record;
}
