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

package com.kopiright.xkopi.comp.xkjc;

import java.util.ArrayList;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CArrayType;
import com.kopiright.kopi.comp.kjc.CExpressionContext;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JArrayInitializer;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JNewArrayExpression;
import com.kopiright.kopi.comp.kjc.JStringLiteral;
import com.kopiright.kopi.comp.kjc.JThisExpression;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.xkopi.comp.sqlc.SqlcMessages;

/**
 * This class represents cursor declaration in code part
 */
public class XExecInsertExpression extends XExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	conn		a pointer to a valid connection to a database
   * @param	table		the table name
   * @param	column		the column names
   * @param	exprs		the column values
   */
  public XExecInsertExpression(TokenReference where,
			       JExpression conn,
			       JExpression table,
			       String[] columns,
			       JExpression[] exprs)
  {
    super(where);
    this.conn = conn;
    this.table = table;
    this.columns = columns;
    this.exprs = exprs;
    verify(exprs.length == columns.length);
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
    // if table is a constant verify this statement with the table in database
    String              tableName; // null -> not constant

    table = table.analyse(context);
    check(context,
          table.getType(factory).equals(CStdType.String),
          XKjcMessages.EXEC_KOPI_INSERT_TABLE_BAD_TYPE); // !!!
    tableName = table.isConstant() ? table.stringValue() : null;

    JExpression[]	columnExprs = new JExpression[exprs.length];
    ArrayList           blobs = new ArrayList();

    for (int i = 0; i < exprs.length; i++) {
      JExpression	expr1 = exprs[i].analyse(context);

      // verify with database (if table is a constant)
      if (tableName != null) {
        XDatabaseColumn         dc = XUtils.getDatabaseColumn(tableName, columns[i]);

        if (dc == null) {
          if (!XUtils.tableExists(tableName)) {
            context.reportTrouble(new CWarning(getTokenReference(),
                                               SqlcMessages.TABLE_NOT_FOUND,
                                               tableName));
          } else {
            context.reportTrouble(new CWarning(getTokenReference(),
                                               SqlcMessages.COLUMN_NOT_IN_TABLE,
                                               columns[i],
                                               tableName));
          }
        } else {
          CType         expected = dc.getStandardType();
          XMutableType  mutable = new XMutableType(expr1.getType(factory));

          if ((! expr1.isAssignableTo(context, expected)) && (! mutable.isAssignableTo(context, expected))) {
            context.reportTrouble(new CWarning(getTokenReference(),
                                               SqlcMessages.BAD_DATABASE_TYPE,
                                               dc + " " + tableName + "." + columns[i],
                                               expr1.getType(factory)));
          }
        }
      }
      if (expr1.getType(factory).getTypeID() == TID_NULL) {
	exprs[i] = new JStringLiteral(ref, "NULL");
      } else if (expr1.getType(factory).isArrayType() && ((CArrayType)expr1.getType(factory)).getBaseType().getTypeID() == TID_BYTE) {
        // blobs
        blobs.add(expr1);
        exprs[i] = new JStringLiteral(ref, "?");
      } else {
	exprs[i] = new JMethodCallExpression(ref,
					     XNameExpression.build(ref, com.kopiright.xkopi.lib.base.KopiUtils.class.getName().replace('.','/')),
					     "toSql",
					     new JExpression[]{ expr1 });
      }
      columnExprs[i] = new JStringLiteral(ref, columns[i]);
    }

    if (tableName != null) {
      // all necessary fields ?
      XDatabaseTable            tbl =  XUtils.getDatabaseTable(tableName);

      if (tbl != null) {
        XDatabaseColumn[]       dbColumns = tbl.getColumns();
        String[]                names = tbl.getNames();

        for (int i = 0; i < dbColumns.length; i++) {
          if (! names[i].equals("ID") && ! names[i].equals("TS")) {
            if (! dbColumns[i].isNullable()) {
              String      colName = names[i];
              boolean     notFound = true;

              for (int k = 0; k < columns.length && notFound; k++) {
                if (XUtils.sqlToUpper) {
                  if (colName.toUpperCase().equals(columns[k].toUpperCase())) {
                    notFound = false;
                  }
                } else {
                  if (colName.equals(columns[k])) {
                    notFound = false;
                  }
                }
              }
              if (notFound) {
                context.reportTrouble(new CWarning(getTokenReference(), XKjcMessages.MISSING_NOTNULL_COLUMN, colName));
              }
            }
          }
        }
      }
    }

    if (conn == null) {
      check(context,
            context.getClassContext().getCClass().descendsFrom(XStdType.DBContextHandler.getCClass()),
	    XKjcMessages.BAD_DEFAULT_CONTEXT,
            context.getClassContext().getCClass().getQualifiedName());
    } else {
      conn = conn.analyse(context);
      if (!conn.isAssignableTo(context, XStdType.Connection)) {
	check(context, conn.isAssignableTo(context, XStdType.DBContextHandler),
	      XKjcMessages.BAD_CONNECTION, conn.getType(factory).getCClass().getQualifiedName());
	// CONSTRUCT THE CONNECTION ACCESS
	conn = XProtectedStatement.buildConnection(getTokenReference(), conn).analyse(context);
      }
    }

    // check uniquness of columns !!!

    if (conn == null) {
     conn = new JMethodCallExpression(ref,
                                       new JMethodCallExpression(ref,
                                                                 new JThisExpression(ref),
                                                                 "getDBContext",
                                                                 JExpression.EMPTY),
                                       "getDefaultConnection",
                                       JExpression.EMPTY);
    }

    // ARRAY 1
    JExpression expr1 = new JNewArrayExpression(ref,
						CStdType.String,
						new JExpression[]{ null },
						new JArrayInitializer(ref, columnExprs));

    // ARRAY 2
    JExpression expr2 = new JNewArrayExpression(ref,
						CStdType.String,
						new JExpression[]{ null },
						new JArrayInitializer(ref, exprs));
    // BLOBARRAY
    JExpression expr3 = new JNewArrayExpression(ref,
						CStdType.Object,
						new JExpression[]{ null },
						new JArrayInitializer(ref,(JExpression[]) blobs.toArray(new JExpression[blobs.size()])));
    return new JMethodCallExpression(ref,
				     XNameExpression.build(ref, com.kopiright.xkopi.lib.base.KopiUtils.class.getName().replace('.','/')),
				     "executeInsert",
				     new JExpression[] {
                                         conn,
                                         table,
                                         expr1,
                                         expr2,
                                         expr3
                                     }).analyse(context);
  }

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
//     ((KjcPrettyPrinter)p).visitInsertStatement(conn,
//                                                 table,
//                                                 columns,
//                                                 exprs);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression                   conn;
  private JExpression                   table;
  private final String[]		columns;
  private final JExpression[]		exprs;
}
