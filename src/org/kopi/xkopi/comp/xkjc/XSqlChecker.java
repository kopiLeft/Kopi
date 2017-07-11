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

package org.kopi.xkopi.comp.xkjc;


import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.kopi.comp.kjc.CBodyContext;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JAddExpression;
import org.kopi.kopi.comp.kjc.JCheckedExpression;
import org.kopi.kopi.comp.kjc.JConditionalExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JLocalVariable;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JNullLiteral;
import org.kopi.kopi.comp.kjc.JParenthesedExpression;
import org.kopi.kopi.comp.kjc.JStringLiteral;
import org.kopi.kopi.comp.kjc.JTypeNameExpression;
import org.kopi.kopi.comp.kjc.KjcMessages;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.xkopi.comp.sqlc.FieldReference;
import org.kopi.xkopi.comp.sqlc.SearchCondition;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.SqlPhylum;
import org.kopi.xkopi.comp.sqlc.SqlcMessages;

/**
 * This class implements a Java pretty printer
 */
public class XSqlChecker extends org.kopi.xkopi.comp.sqlc.SqlChecker implements XSqlVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new visitor
   */
  public XSqlChecker(SqlContext context) {
    super(context);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the current string
   */
  public JExpression toJavaExpression(TokenReference ref) {
    String	text = toString();

    if (expr == null) {
      // only string
      return new JStringLiteral(ref, text);
    } else if (text.length() > 0) {
      // pending sql, add to left expression
      return new JAddExpression(ref, expr, new JStringLiteral(ref, text));
    } else {
      return expr;
    }
  }

  // ----------------------------------------------------------------------
  // VISITORS
  // ----------------------------------------------------------------------

  /**
   * Generates code.
   * @param	context		the sql context that holds the kjc Context
   * @param	left		the SQL expression to be built
   * @param	current		the current string literal to be added to left
   */
  public void visitTypedSelectElem(XTypedSelectElem self,
				   String ident,
				   JLocalVariable var,
				   SqlPhylum sqlExpr,
				   boolean hasAs)
    throws PositionedError
  {
    if (!hasAs) {
      String	column;
      String	alias;
      int	index = ident.indexOf('.');

      if (index != -1) {
	column = ident.substring(index + 1, ident.length());
	alias = ident.substring(0, index);
      } else {
	column = ident;
	alias = null;
      }

      String table = FieldReference.checkColumnExists(sqlExpr.getTokenReference(), 
                                                      getContext(), 
                                                      alias, 
                                                      column);
      if (table != null) {
        XDatabaseColumn         dc = XUtils.getDatabaseColumn(table, column);

        if (dc == null) {
          if (!XUtils.tableExists(table)) {
            getContext().reportTrouble(new CWarning(self.getTokenReference(), 
                                               SqlcMessages.TABLE_NOT_FOUND, 
                                               table));
          } else {
            getContext().reportTrouble(new CWarning(self.getTokenReference(), 
                                               SqlcMessages.COLUMN_NOT_IN_TABLE, 
                                               column, 
                                               table));
          }
        } else {
          CType         expected = var.getType();

          if (! dc.getStandardType().isAssignableTo(getContext().getTypeContext(), expected)) {
            getContext().reportTrouble(new CWarning(self.getTokenReference(), 
                                               SqlcMessages.BAD_DATABASE_TYPE, 
                                               dc + " " + table + "." + column, 
                                               expected));           
          }
        }
      }
    }
    sqlExpr.accept(this);
    if (hasAs) {
      //current.append(" AS ");
      //current.append(ident);
      if (ident.indexOf('.') != -1) {
        getContext().reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.BAD_ALIAS_FORMAT,
                                   ident));
      }
      if (sqlExpr instanceof FieldReference
          && ((FieldReference)sqlExpr).getQualifiedFieldName().equals(ident)) {
        getContext().reportTrouble(new CWarning(self.getTokenReference(),
                                   SqlcMessages.NOT_USEFUL_ALIAS,
                                   ident + " AS " + ident));
      }
    }
  }

  /**
   * Generates java code.
   *
   * @exception	PositionedError		if the check fails
   */
  public void visitIfCondition(XIfCondition self,
			       String oper,
			       SearchCondition left,
			       JExpression cond,
			       XSqlExpr thenClause,
			       XSqlExpr elseClause)
    throws PositionedError
  {
    TokenReference	ref = left.getTokenReference();
    JavaSqlContext	sqlStatement = getJavaContext(getContext());
    CBodyContext	context = sqlStatement.getBodyContext();
    TypeFactory         factory = context.getTypeFactory();

    cond = cond.analyse(new CExpressionContext(context, context.getEnvironment()));
    if (cond.getType(factory) != factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN)) {
      throw new PositionedError(left.getTokenReference(), KjcMessages.IF_COND_NOTBOOLEAN, cond.getType(factory));
    }

    left.accept(this);

    JExpression elsePart = null;
    JExpression	thenPart = thenClause.analyse(this.getContext());

    thenPart = new JAddExpression(ref,
			      new JStringLiteral(ref, " " + oper + " "),
			      new JCheckedExpression(ref, thenPart));

    if (elseClause != null) {
      elsePart = elseClause.analyse(this.getContext());
      elsePart = new JAddExpression(ref,
				    new JStringLiteral(ref, " " + oper + " "),
				    new JCheckedExpression(ref, elsePart));
    } else {
      elsePart = new JStringLiteral(ref, "");
    }

    if (current.length() > 0) {
      // pending sql, add to left expression
      if (expr == null) {
	expr = new JStringLiteral(ref, current.toString());
      } else {
	expr = new JAddExpression(ref, expr, new JStringLiteral(ref, current.toString()));
      }
    }
    current.setLength(0);

    expr = new JAddExpression(ref,
			      expr,
			      new JParenthesedExpression(ref,
							 new JConditionalExpression(ref,
										    new JParenthesedExpression(ref, new JCheckedExpression(ref, cond)),
										    thenPart,
										    elsePart)));
  }

  /**
   * Generates java code.
   *
   * @exception	PositionedError		if the check fails
   */
  public void visitSqlExprJava(SqlExprJava self, JExpression expr, boolean asText)
    throws PositionedError
  {
    JavaSqlContext	sqlStatement = getJavaContext(getContext());
    CBodyContext	context = sqlStatement.getBodyContext();
    TokenReference	ref = expr.getTokenReference();
    TypeFactory         factory = context.getTypeFactory();

    expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));

    if (current.length() > 0) {
      // pending sql, add to left expression
      if (this.expr == null) {
	this.expr = new JStringLiteral(ref, current.toString() + " ");
      } else {
	this.expr = new JAddExpression(ref, this.expr, new JStringLiteral(ref, current.toString() + " "));
      }
    }
    current.setLength(0);

    if (expr instanceof JNullLiteral) {
      if (!asText) {
	this.expr = new JAddExpression(ref, this.expr, new JStringLiteral(ref, "NULL"));
      } else {
        // append null as text -> nothing to do
      }
      return;
    }

    if (!XStdType.isSqlPrimitiveType(expr.getType(factory))) {
      sqlStatement.addBlob(expr);
    }

    CReferenceType    utilType = (CReferenceType) context.getTypeFactory().createType(org.kopi.xkopi.lib.base.KopiUtils.class.getName().replace('.','/'), true);

    try {
      utilType = (CReferenceType) utilType.checkType(context);
    } catch (UnpositionedError ue) {
      throw ue.addPosition(ref);
    }

    if (asText) {
      this.expr = new JAddExpression(ref, this.expr, expr);
    } else {
      this.expr = new JAddExpression(ref,
                                     this.expr,
                                     new JMethodCallExpression(ref, 
                                                               new JTypeNameExpression(ref, utilType), 
                                                               "toSql", 
                                                               new JExpression[] {new JCheckedExpression(ref, expr)}));
    }
  }

  // ----------------------------------------------------------------------
  // STATIC UTILITY
  // ----------------------------------------------------------------------

  public static JavaSqlContext getJavaContext(SqlContext ctxt) {
    if (ctxt instanceof JavaSqlContext) {
      return (JavaSqlContext)ctxt;
    }
    while (ctxt != null) {
      ctxt = ctxt.getParentContext();
      if (ctxt instanceof JavaSqlContext) {
	return (JavaSqlContext)ctxt;
      }
    }
    return null;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression			expr;
}
