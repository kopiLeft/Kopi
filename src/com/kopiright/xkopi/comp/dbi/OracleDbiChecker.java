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

package com.kopiright.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.xkopi.comp.sqlc.*;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.base.DriverInterface;
import com.kopiright.xkopi.lib.base.OracleDriverInterface;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public class OracleDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new OracleDbiChecker object.
   */
  public OracleDbiChecker(SqlContext sql) {
    super(sql);
  }


  /**
   * Visits GrantUserClassStatement
   */
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
					   int userClass,
					   String userName)
    throws PositionedError
  {
    switch(userClass) {
    case GrantUserClassStatement.TYP_ACCESS:
      current.append("GRANT CONNECT TO ");
      current.append(userName);
      current.append(" IDENTIFIED BY ");
      current.append("\"" + userName + "\"");
      break;
    case GrantUserClassStatement.TYP_RESOURCE:
      current.append("GRANT CONNECT, RESOURCE TO ");
      current.append(userName);
      current.append(" IDENTIFIED BY ");
      current.append("\"" + userName + "\"");
      break;
    case GrantUserClassStatement.TYP_DBA:
      current.append("GRANT CONNECT, RESOURCE, CREATE USER, DROP USER, CREATE PUBLIC SYNONYM, DROP PUBLIC SYNONYM TO ");
      current.append(userName);
      current.append(" IDENTIFIED BY ");
      current.append("\"" + userName + "\"");
      current.append(" WITH ADMIN OPTION");
      break;
    default:
      throw new InconsistencyException("Unexpected privilege");
    }
  }


  public void visitGrantPrivilegeStatement(GrantPrivilegeStatement self,
					   Expression tableName,
					   ArrayList privileges,
					   ArrayList userList,
					   boolean grantOption)
    throws PositionedError
  {
    current.append("GRANT ");
    for (int i = 0; i < privileges.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((TablePrivilege)privileges.get(i)).accept(this);
    }
    current.append(" ON ");
    tableName.accept(this);
    current.append(" TO ");
    for (int i = 0; i < userList.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      current.append(userList.get(i));
    }

    // if it's a GRANT ALL ... TO PUBLIC.
    // a CREATE PUBLIC SYNONYM ... FOR ... is added for Oracle.
    if (privileges.size() == 1 &&
        ((TablePrivilege) privileges.get(0)).privilege == TablePrivilege.TYP_ALL &&
        userList.size() == 1 &&
        userList.get(0).equals("PUBLIC")) {
      current.append("; \n");
      current.append("CREATE PUBLIC SYNONYM ");
      tableName.accept(this);
      current.append(" FOR ");
      tableName.accept(this);
    }

  }

  /**
   *
   */
  public String getStatementText() {
    return getStatementText(getDriverInterface()) + ";";
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return new OracleDriverInterface();
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append("TO_DATE('");
    current.append(value.getYear());
    current.append("-");
    current.append(value.getMonth());
    current.append("-");
    current.append(value.getDay());
    current.append(", 'YYYY-MM-DD')");
  }

  /**
   * Visits AddTableConstraintStatement
   */
  public void visitAddTableConstraintStatement(AddTableConstraintStatement self,
					       Expression tableName,
					       TableConstraint tableConstraint)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append(" ADD CONSTRAINT ");
    tableConstraint.accept(this);
  }

  /**
   * Visits a AddTableColumnsStatement.
   */
  public void visitAddTableColumnsStatement(AddTableColumnsStatement self,
                                            Expression tableName,
                                            Column column)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append(" ADD (");
    column.accept(this);
    current.append(") ");
  }

  /**
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append("LONG RAW");
  }

  /**
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append("LONG");
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    // Using Numeric for boolean (no Boolean type in Oracle7).
    current.append("NUMBER(1)");
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("NUMBER(3)");
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("NUMBER(1)");
  }


  /**
   * Visits CodeBoolDesc
   */
  public void visitCodeBoolDesc(CodeBoolDesc self, String name, Boolean value)
    throws PositionedError
  {
    current.append('"' + name + '"');
    current.append("= ");
    current.append(value.booleanValue() ? "1" : "0");
  }


  /**
   * Visits CodeFixedType
   */
  public void visitCodeFixedType(CodeFixedType self, int precision, int scale, ArrayList list)
    throws PositionedError
  {
    current.append("NUMBER(");
    current.append("" + precision);
    current.append("," + scale);
    current.append(")");
  }

  /**
   * Visits CodeLongType
   */
  public void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError
  {
    current.append("NUMBER(30)");
  }

  /**
   * Visits ColorType
   */
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    current.append("RAW");
  }

  /**
   * Visits DateType
   */
  public void visitDateType(DateType self)
    throws PositionedError
  {
    current.append("DATE");
  }

  /**
   * Visits EnumType
   */
  public void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError
  {
    current.append("VARCHAR2(" + DEFAULT_STRING_SIZE + ")");
  }


  /**
   * Visits FixedType
   */
  public void visitFixedType(FixedType self,
			     int precision,
			     int scale,
			     Fixed min,
			     Fixed max)
    throws PositionedError
  {
    current.append("NUMBER(");
    current.append("" + precision);
    current.append(", " + scale);
    current.append(")");
  }

  /**
   * Visits ImageType
   */
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {
    current.append("LONG RAW");
  }

  /**
   * Visits IndexDefinition
   */
  public void visitIndexDefinition(IndexDefinition self,
				   boolean hasUnique,
				   String indexName,
				   Expression tableName,
				   ArrayList indexElemList,
				   int type)
    throws PositionedError
  {
    current.append("CREATE ");
    if (hasUnique) {
      current.append("UNIQUE ");
    }
    current.append("INDEX ");
    current.append(indexName);
    current.append(" ON ");
    tableName.accept(this);
    current.append(" (");
    for (int i = 0; i < indexElemList.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((IndexElem)indexElemList.get(i)).accept(this);
    }
    current.append(")");
    switch(type) {
    case IndexDefinition.TYP_NONE:
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
  }

  /**
   * Visits IntType
   */
  public void visitIntType(IntType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("NUMBER(15)");
  }

  /**
   * Visits Key
   */
  public void visitKey(Key self, List keyList)
    throws PositionedError
  {
    ListIterator        iterator = keyList.listIterator();

    current.append("PRIMARY KEY (");
    current.append(iterator.next());
    while (iterator.hasNext()) {
      current.append(", ");
      current.append(iterator.next());
    }
    current.append(")");
  }

  /**
   * Visits MonthType
   */
  public void visitMonthType(MonthType self)
    throws PositionedError
  {
    current.append("NUMBER(15)");
  }

  /**
   * Visits ReferentialConstraintDefinition
   */
  public void visitReferentialConstraintDefinition(ReferentialConstraintDefinition self,
						   String name,
						   FieldNameList field,
						   ReferencedTableAndColumns reference,
						   int type)
    throws PositionedError
  {
    current.append(name);
    current.append(" FOREIGN KEY (");
    field.accept(this);
    current.append(") REFERENCES ");
    reference.accept(this);
    switch(type) {
    case ReferentialConstraintDefinition.TYP_CASCADE:
      current.append(" ON DELETE CASCADE");
      break;
    case ReferentialConstraintDefinition.TYP_NULL:
      break;
    case ReferentialConstraintDefinition.TYP_DEFAULT:
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
  }

  /**
   * Visits StringType
   */
  public void visitStringType(StringType self, boolean fixed, int width, int height, int convert)
    throws PositionedError
  {
    int		size;

    if (width == -1) {
      size = DEFAULT_STRING_SIZE;	// arbitrary : we don't know the size
    } else {
      size = width * height;
    }

    current.append(fixed ? "CHAR(" : "VARCHAR2(");
    current.append(size);
    current.append(")");
  }

  /**
   * Visits TableDefinition
   */
  public void visitTableDefinition(TableDefinition self,
				   Expression tableName,
				   ArrayList columns,
				   Key key)
    throws PositionedError
  {
    current.append("CREATE TABLE ");
    tableName.accept(this);
    current.append(" (");
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      ((Column)columns.get(i)).accept(this);
    }
    if (key != null) {
      current.append(", ");
      key.accept(this);
    }
    current.append(")");
  }

  /**
   * Visits TextType
   */
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    current.append("LONG");
  }

  /**
   * Visits TimeType
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    current.append("DATE");
  }

  /**
   * Visits TimestampType
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    current.append("DATE");
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self, int type, FieldNameList field)
    throws PositionedError
  {
    switch (type) {
    case UniqueConstraintDefinition.TYP_UNIQUE:
      current.append("UNIQUE ");
      break;
    case UniqueConstraintDefinition.TYP_PRIMARY:
      current.append("PRIMARY KEY ");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
    field.accept(this);
    current.append(")");
  }

  /**
   * Visits WeekType
   */
  public void visitWeekType(WeekType self)
    throws PositionedError
  {
    current.append("NUMBER(15)");
  }

  // ----------------------------------------------------------------------
  // DATA CONSTANTS
  // ----------------------------------------------------------------------

  public static final int               DEFAULT_STRING_SIZE = 256;

}
