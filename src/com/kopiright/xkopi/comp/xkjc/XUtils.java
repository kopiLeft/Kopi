/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.lang.reflect.Field;
import java.util.StringTokenizer;
import java.util.Vector;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.kopi.comp.kjc.CBinaryTypeContext;
import com.kopiright.kopi.comp.kjc.CExpressionContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JArrayInitializer;
import com.kopiright.kopi.comp.kjc.JCastExpression;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JNewArrayExpression;
import com.kopiright.kopi.comp.kjc.JNullLiteral;
import com.kopiright.kopi.comp.kjc.JTypeNameExpression;
import com.kopiright.kopi.comp.kjc.KjcEnvironment;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.util.base.Utils;
import com.kopiright.xkopi.comp.sqlc.SqlcMessages;

public class XUtils extends com.kopiright.util.base.Utils {

  // ----------------------------------------------------------------------
  // FIXED LITERAL
  // ----------------------------------------------------------------------

  /**
   * Sets database
   *//*
  public static void setDatabase(CClass database) {
    if (XUtils.database == null) {
      XUtils.database = new Vector();
    }
    XUtils.database.addElement(database);
    }*/

  /**
   * Sets database
   */
  public static void setDatabase(String database) {
    if (database == null) {
      return;
    }
    //    XUtils.database = null;
    XUtils.database = new Vector();
    StringTokenizer	token = new StringTokenizer(database, ": ");
    while (token.hasMoreTokens()) {
      String	current = token.nextToken();
        try {
          Class clazz = Class.forName(current);
          XUtils.database.addElement(clazz);
        } catch (ClassNotFoundException e) {
          System.err.println("WARNING: "+current+" (Database.k-class) not found. ");
        } 
    }
    com.kopiright.xkopi.comp.sqlc.XUtils.setChecker(new com.kopiright.xkopi.comp.sqlc.DBChecker() {
	/**
	 * Checks if table exists
	 */
	public boolean tableExists(String table) {
	  return XUtils.tableExists(table);
	}

	/**
	 * Checks if a column exists in a table
	 */
	public boolean columnExists(String table, String column) {
	  return XUtils.columnExists(table, column);
	}
      });
  }

  // ----------------------------------------------------------------------
  // OVERLOADING CHECK
  // ----------------------------------------------------------------------

  /**
   * Returns a method that overload an operator or null if there is no method
   *
   * @param	operator	the id of the operator
   * @param	context		the context to look at
   * @param	left		the left expression
   * @param	right		the right expression
   * @param	ref		the location in source code
   * @param	typeError	the type error between the two operands
   * @exception	PositionedError the typeError is rethrown if there is no method
   */
  public static JExpression fetchBinaryOverloadedOperator(int operator,
							  CExpressionContext context,
							  JExpression left,
							  JExpression right,
							  TokenReference ref,
							  PositionedError typeError)
    throws PositionedError
  {
    if (typeError.hasDescription(com.kopiright.kopi.comp.kjc.KjcMessages.TYPE_UNKNOWN)
	|| typeError.hasDescription(com.kopiright.kopi.comp.kjc.KjcMessages.VAR_UNKNOWN)) {
      throw typeError;
    }

    JExpression ret = fetchOverloadedOperator(context, buildBinary(ref, operator, left, right));

    if (ret == null) {
      throw typeError;
    }

    return ret;
  }

  /**
   * Returns a method that overload an operator or null if there is no method
   *
   * @param	operator	the id of the operator
   * @param	context		the context to look at
   * @param	expr		the left expression
   * @param	ref		the location in source code
   * @param	typeError	the type error between the two operands
   * @exception	PositionedError the typeError is rethrow if there is
   *		no method
   */
  public static JExpression fetchUnaryOverloadedOperator(int operator,
							 CExpressionContext context,
							 JExpression expr,
							 TokenReference ref,
							 PositionedError typeError)
    throws PositionedError
  {
    JExpression ret = fetchOverloadedOperator(context,  buildUnary(ref, operator, expr));
    if (ret == null) {
      throw typeError;
    } else {
      return ret;
    }
  }

  // ----------------------------------------------------------------------
  // DATABASE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Returns the database type of a Table/column
   */
  public static XDatabaseColumn getDatabaseColumn(String table, String column) {
    XDatabaseMember dm = getDatabaseMember(table + "_" + column);

    if ((dm != null) && (dm.isColumn())) {
      return (XDatabaseColumn) dm;
    } else {
      return null;
    }
  }

  public static XDatabaseTable getDatabaseTable(String table) {
    XDatabaseMember dm = getDatabaseMember(table);

    if ((dm != null) && (dm.isTable())) {
      return (XDatabaseTable) dm;
    } else {
      return null;
    }
  }

  private static XDatabaseMember getDatabaseMember(String member) {
    if (! checkDatabase()) {
      return null;
    } else {
      if (sqlToUpper) {
        member = member.toUpperCase();
      }

      for (int i = 0; i < database.size(); i++) {
        try {
          Class         clazz = (Class)database.elementAt(i);
          Field         field = clazz.getField(member);

          if (field != null) {
            return (XDatabaseMember)field.get(null);
          }
        } catch (NoSuchFieldException e) {
          // continue;
        } catch (IllegalAccessException e) {
          // continue;
        }
      }
      return null;
    }
  }

  /**
   * Returns the database type of a Table/column
   */
  public static boolean tableExists(String table) {
    if (!checkDatabase()) {
      return true;    
    } else {
      if (sqlToUpper) {
        table = table.toUpperCase();
      }
      for (int i = 0; i < database.size(); i++) {
	try {
          Field                 field;
          XDatabaseMember       dm;

          field = ((Class)database.elementAt(i)).getField(table);
          dm = (XDatabaseMember) field.get(null);

          if (dm != null) {
            return dm.isTable();
          } 
	  return false;
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
      }
      return  false;
    }
  }

  /**
   * Returns the database type of a Table/column
   */
  public static boolean columnExists(String table, String column) {
    if (checkDatabase()) {
      if (sqlToUpper) {
        table = table.toUpperCase();
        column = column.toUpperCase();
      }
      XDatabaseMember   dm = getDatabaseColumn(table, column);

      return ((dm != null) && dm.isColumn());
    } else {
      return true;
    }
  }


  /**
   * Returns the database type of a Table/column
   */
  public static void checkDatabaseType(XDatabaseColumn expected, 
                                       String table,
                                       String column)
    throws UnpositionedError
  {
    checkDatabaseType(expected, table, column, 0);
  }
 
  /**
   * Returns the database type of a Table/column
   */
  public static void checkDatabaseType(XDatabaseColumn expected, 
                                       String table, 
                                       String column, 
                                       int check)
    throws UnpositionedError
  {
    if (checkDatabase()) {
      XDatabaseColumn   dc = getDatabaseColumn(table, column);    

      if (dc != null) {
        if (!dc.isEquivalentTo(expected,check)) {              
 	  throw new UnpositionedError(SqlcMessages.BAD_DATABASE_TYPE,
                                      dc + " " + table + "." + column,
                                      expected);
        }
      } else { //else failure 
        if (!tableExists(table)) {
          throw new UnpositionedError(SqlcMessages.TABLE_NOT_FOUND, table);
        } else {
          throw new UnpositionedError(SqlcMessages.COLUMN_NOT_IN_TABLE, column, table);
        }
      }
    }
  }

  /**
   * Returns the database type of a Table/column
   */
  public static void checkColumnExists(String table, String column)
    throws UnpositionedError
  {
    if (checkDatabase() && getDatabaseColumn(table, column) == null) {
      if (!tableExists(table)) {
	throw new UnpositionedError(SqlcMessages.TABLE_NOT_FOUND, table);
      } else {
	throw new UnpositionedError(SqlcMessages.COLUMN_NOT_IN_TABLE, column, table);
      }
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  
  /**
   *
   */
  private static boolean checkDatabase() {
    return database != null;
  }

  // ----------------------------------------------------------------------
  // INITIALIZATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public static void initialize(KjcEnvironment env,
                                String xkjcPath,
                                boolean overloadEqual,
                                String database)
  {
    XUtils.overloadEqual = overloadEqual;

    if (xkjcPath == null) {
      // thomas 20020628 : we can't use
      // com.kopiright.xkopi.lib.oper.XFixed.class.getName() because the XFixed.x
      // needs xkjc to compile.
      initialize(env, new String[] {
	"com/kopiright/xkopi/lib/oper/XFixed",
        "com/kopiright/xkopi/lib/oper/XByte",
        "com/kopiright/xkopi/lib/oper/XShort",
        "com/kopiright/xkopi/lib/oper/XInteger",
        "com/kopiright/xkopi/lib/oper/XFloat",
        "com/kopiright/xkopi/lib/oper/XDouble",
        "com/kopiright/xkopi/lib/oper/XBoolean",
        "com/kopiright/xkopi/lib/oper/XDate",
        "com/kopiright/xkopi/lib/oper/XMonth",
        "com/kopiright/xkopi/lib/oper/XTime",
        "com/kopiright/xkopi/lib/oper/XWeek",
        "com/kopiright/xkopi/lib/oper/XCharacter",
        "com/kopiright/xkopi/lib/oper/XString"
	  });
    } else {
      // !!! USE USER PATH
      prefixes = new JExpression[0];
    }

    setDatabase(database);
  }

  /**
   *
   */
  private static void initialize(KjcEnvironment env, String[] xkjcPath) {
    Vector		container = new Vector();
    TypeFactory factory = env.getTypeFactory();
    
    CBinaryTypeContext  context = new CBinaryTypeContext(env.getClassReader(),
                                                         factory);

    try {
      for (int i = 0; i < xkjcPath.length; i++) {
	JTypeNameExpression     expr;
        CReferenceType          type;

        type = factory.createType(xkjcPath[i], false);
        expr = new JTypeNameExpression(TokenReference.NO_REF, type);

	container.addElement(expr.analyse(context));
      }
    } catch (PositionedError e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }

    prefixes = (JExpression[])Utils.toArray(container, JExpression.class);
  }

  /**
   * Returns a method that overload an operator or null if there is no method
   *
   * @param	context		the context to look at
   * @param	overload	the method call
   */
  public static JExpression fetchOverloadedOperator(CExpressionContext context,
						    XOverloadedMethodCallExpression overload)
  {
    JExpression	ret = null;
    boolean	found = true;

    try {
      overload.preCheckExpression(context);
    } catch (PositionedError error) {
      return null; // !!!
    }

    try {
      ret = overload.analyse(context);
    } catch (PositionedError error) {
      found = false;
    }

    for (int i = 0; !found && i < prefixes.length; i++) {
      try {
	found = true;
	overload.setPrefix(prefixes[i]);
	ret = overload.analyse(context);
      } catch (PositionedError error) {
	found = false;
      }
    }

    return found ? ret : null;
  }

  /**
   * Returns a method that overload an operator or null if there is no method
   *
   * @param	context		the context to look at
   * @param	overload	the method call
   */
  public static XOverloadedMethodCallExpression fetchOverloadedNewArray(TypeFactory factory, JExpression[] dims, CType type) {
    JExpression         param;
    JExpression         arrayParam;

    param = new JCastExpression(TokenReference.NO_REF,
                                new JNullLiteral(TokenReference.NO_REF),
                                type);

    arrayParam = new JNewArrayExpression(TokenReference.NO_REF,
                                                     factory.getPrimitiveType(TypeFactory.PRM_INT),
                                                     new JExpression[] {null},
                                                     new JArrayInitializer(TokenReference.NO_REF, dims));

    return new XOverloadedMethodCallExpression(TokenReference.NO_REF,
                                               null,
                                               "operator$array".intern(),
                                               new JExpression[] {param, arrayParam});

  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * buildUnary
   */
  private static XOverloadedMethodCallExpression buildUnary(TokenReference ref,
							    int operator,
							    JExpression expr)
  {
    return new XOverloadedMethodCallExpression(ref,
					       null,
					       ("operator$" + operator).intern(),
					       new JExpression[]{ expr });
  }

  /**
   * buildBinary
   */
  private static XOverloadedMethodCallExpression buildBinary(TokenReference ref,
							     int operator,
							     JExpression left,
							     JExpression right)
  {
    return new XOverloadedMethodCallExpression(ref,
					       null,
					       ("operator$" + operator).intern(),
					       new JExpression[]{ left, right });
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static JExpression[]	prefixes;
  private static Vector		database;
  public static boolean         overloadEqual;
  public static boolean         sqlToUpper;
}
