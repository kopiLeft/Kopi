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

package com.kopiright.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.InsertSource;
import com.kopiright.xkopi.comp.sqlc.InsertStatement;
import com.kopiright.xkopi.comp.sqlc.IntegerLiteral;
import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
import com.kopiright.xkopi.comp.sqlc.Statement;
import com.kopiright.xkopi.comp.sqlc.StringLiteral;
import com.kopiright.xkopi.comp.sqlc.ValueListInsertSource;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class DbCheck {

  public boolean check(SCompilationUnit db1,
                       String name1,
                       SCompilationUnit db2,
                       String name2)
  {
    // the CREATE TABLE
    List	elems1 = db1.getElems();
    List	elems2 = db2.getElems();

    Hashtable	table1 = fetchTableDefinition(elems1);
    Hashtable	table2 = fetchTableDefinition(elems2);

    compareTableDefinition(table1.elements(), table2);
    getBadTables(table2.elements());

    // the INSERT INTO MODULE
    List insertsModule1 = fetchInsertStatement(elems1, "MODULE");
    List insertsModule2 = fetchInsertStatement(elems2, "MODULE");

    sortModule(insertsModule2);
    if (!compareInsertStatement(insertsModule1, insertsModule2)) {
      return false;
    }

    // the INSERT INTO REFERENZEN
    List insertsRef1 = fetchInsertStatement(elems1, "REFERENZEN");
    List insertsRef2 = fetchInsertStatement(elems2, "REFERENZEN");

    sortReferenzen(insertsRef2);
    if (!compareInsertStatement(insertsRef1, insertsRef2)) {
      return false;
    }

    // the views
    List views1 = fetchViews(elems1);
    List views2 = fetchViews(elems2);
    if (!compareViews(views1, views2)) {
      return false;
    }

    return true;
  }

  /**
   * fetchTableDefinition
   */
  public Hashtable fetchTableDefinition(List v) {
    Hashtable	table = new Hashtable();

    for (int i = 0; i < v.size(); i++) {
      Statement	stmt = (Statement)v.get(i);
      if (stmt instanceof TableDefinition) {
	String	TableName = ((SimpleIdentExpression)((TableDefinition)stmt).getTableName()).getIdent();
	table.put(TableName, stmt);
      }
    }
    return table;
  }

  /**
   * fetchViews
   */
  public List fetchViews(List v) {
    List views = new ArrayList();

    for (int i = 0; i < v.size(); i++) {
      Statement	stmt = (Statement)v.get(i);
      if (stmt instanceof ViewDefinition) {
	views.add((ViewDefinition)stmt);
      }
    }
    return views;
  }

  /**
   * fetchInsertStatement
   */
  public List fetchInsertStatement(List v, String tableName) {
    List inserts = new ArrayList();

    for (int i = 0; i < v.size(); i++) {
      Statement	stmt = (Statement)v.get(i);
      if (stmt instanceof InsertStatement) {
	if ((((InsertStatement)stmt).getIdent()).equals(tableName)) {
	  inserts.add((InsertStatement)stmt);
	}
      }
    }
    return inserts;
  }

  /**
   * compareViews
   */
  public boolean compareViews(List v1, List v2) {
    for (int i = 0; i < v1.size(); i++) {
      if (!((ViewDefinition)v1.get(i)).compareTo((ViewDefinition)v2.get(i))) {
	addError(new PositionedError(NO_REF, DbiMessages.VIEWS_DIFFERENT, v1.get(i), v2.get(i)));
	return false;
      }
    }
    return true;
  }

  /**
   * compareInsertStatement
   */
  public boolean compareInsertStatement(List v1, List v2) {
    for (int i = 0; i < v1.size(); i++) {
      if (!((InsertStatement)v1.get(i)).compareTo((InsertStatement)v2.get(i))) {
	addError(new PositionedError(NO_REF, DbiMessages.INSERTS_DIFFERENT, v1.get(i), v2.get(i)));
	return false;
      }
    }
    return true;
  }

  /**
   * compareTableDefinition
   */
  public void compareTableDefinition(Enumeration elems, Hashtable hashTable) {
    while (elems.hasMoreElements()) {
      TableDefinition table = (TableDefinition)elems.nextElement();
      String	tableName = ((SimpleIdentExpression)table.getTableName()).getIdent();

      if (hashTable.containsKey(tableName)) {
	if ( !table.compareTo((TableDefinition)hashTable.remove(tableName))) {
	  addError(new PositionedError(NO_REF, DbiMessages.TABLE_NOT_CORRECT, tableName));
	}
      } else {
	addError(new PositionedError(NO_REF, DbiMessages.TABLE_NOT_EXIST_IN_FILE, tableName));
      }
    }
  }

  /**
   * getBadTables
   */
  public void  getBadTables(Enumeration badTables) {
    while (badTables.hasMoreElements()) {
      TableDefinition table = (TableDefinition)badTables.nextElement();
      String	tableName = ((SimpleIdentExpression)table.getTableName()).getIdent();
      addError(new PositionedError(NO_REF, DbiMessages.TABLE_NOT_EXIST_IN_FILE, tableName));
    }
  }

  /**
   * Bubble sort for the insert into referenzen
   */
  private static final void sortReferenzen(List data) {
    for (int i = data.size(); --i >= 0;) {
      for (int j = 0; j < i; j++) {
	InsertSource source1 = ((InsertStatement)data.get(j)).getSource();
	InsertSource source2 = ((InsertStatement)data.get(j +1)).getSource();
	if (source1 instanceof ValueListInsertSource && source2 instanceof ValueListInsertSource) {
	  Expression expr1 = ((((ValueListInsertSource)source1).getValue()).getList()).getElem(0);
	  Expression expr11 = ((((ValueListInsertSource)source1).getValue()).getList()).getElem(1);
	  Expression expr2 = ((((ValueListInsertSource)source2).getValue()).getList()).getElem(0);
	  Expression expr22 = ((((ValueListInsertSource)source2).getValue()).getList()).getElem(1);
	  if (expr1 instanceof StringLiteral && expr2 instanceof StringLiteral &&
	      expr11 instanceof StringLiteral && expr22 instanceof StringLiteral) {
	    if (((String)(((StringLiteral)expr1).getValue())).compareTo((String)(((StringLiteral)expr2).getValue())) > 0) {
	      Object tmp = data.get(j);
	      data.set(j, data.get(j + 1));
	      data.set(j+1, tmp);
	    } else if (((String)(((StringLiteral)expr1).getValue())).compareTo((String)(((StringLiteral)expr2).getValue())) == 0) {
	      if (((String)(((StringLiteral)expr11).getValue())).compareTo((String)(((StringLiteral)expr22).getValue())) > 0) {
		Object tmp = data.get(j);
		data.set(j, data.get(j + 1));
		data.set(j + 1, tmp);
	      }
	    }
	  } else {
	    addError(new PositionedError(NO_REF, DbiMessages.BAD_INSERT_NOT_STR_LIT));
	  }
	} else {
	  addError(new PositionedError(NO_REF, DbiMessages.BAD_INSERT_REFERENZEN, source1, source2));
	}
      }
    }
  }

  /**
   * Bubble sort for insert into module
   */
  private static final void sortModule(List data) {
    for (int i = data.size(); --i >= 0;) {
      for (int j = 0; j < i; j++) {
	InsertSource source1 = ((InsertStatement)data.get(j)).getSource();
	InsertSource source2 = ((InsertStatement)data.get(j +1)).getSource();
	if (source1 instanceof ValueListInsertSource && source2 instanceof ValueListInsertSource) {
	  Expression expr1 = ((((ValueListInsertSource)source1).getValue()).getList()).getElem(0);
	  Expression expr2 = ((((ValueListInsertSource)source2).getValue()).getList()).getElem(0);
	  if (expr1 instanceof IntegerLiteral && expr2 instanceof IntegerLiteral) {
	    if (((Integer)(((IntegerLiteral)expr1).getValue())).intValue() > ((Integer)(((IntegerLiteral)expr2).getValue())).intValue()) {
	      Object tmp = data.get(j);
	      data.set(j, data.get(j + 1));
	      data.set(j + 1, tmp);
	    }
	  } else {
	    addError(new PositionedError(NO_REF, DbiMessages.BAD_INSERT_NOT_INT_LIT));
	  }
	} else {
	  addError(new PositionedError(NO_REF, DbiMessages.BAD_INSERT_MODULE, source1, source2));
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  public static void addError(PositionedError error) {
    System.err.println(error.getMessage());
  }

  // ----------------------------------------------------------------------
  // PRIVATE MEMBERS
  // ----------------------------------------------------------------------

  private static TokenReference		NO_REF = TokenReference.NO_REF;
}
