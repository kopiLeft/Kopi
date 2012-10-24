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

package com.kopiright.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.List;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.JdbcDateLiteral;
import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
import com.kopiright.xkopi.comp.sqlc.SqlContext;
import com.kopiright.xkopi.lib.base.DriverInterface;
import com.kopiright.xkopi.lib.base.IngresDriverInterface;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public class IngresDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new IngresDbiChecker object.
   */
  public IngresDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  public String getStatementText() {
    String      statement = getStatementText(getDriverInterface());
    
    return statement.length() != 0 ? statement + "\n \\g" : "";
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return new IngresDriverInterface();
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append("'");
    if (value.getDay() < 10) {
      current.append("0");
    }
    current.append(value.getDay());
    current.append("-");
    if (value.getMonth() < 10) {
      current.append("0");
    }
    current.append(value.getMonth());
    current.append("-");
    current.append(value.getYear());
    current.append("'");
  }

  /**
   * Visits AlterDropDefaultColumnStatement
   */
  public void visitAlterDropDefaultColumnStatement(AlterDropDefaultColumnStatement self,
                                                   Expression tableName,
                                                   String columnName)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append("DROP COLUMN ");
    current.append(columnName);
  }

  /**
   * Visits a AlterNotNullColumnStatement.
   */
  public void visitAlterNotNullColumnStatement(AlterNotNullColumnStatement self,
                                               Expression tableName,
                                               String columnName)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append("ALTER COLUMN ");
    current.append(columnName);
    current.append(" NOT NULL ");
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
    if ( !(tableConstraint instanceof ReferentialConstraintDefinition) ) {
      current.append(" ADD CONSTRAINT ");
    }
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
    current.append(" ADD COLUMN ");
    column.accept(this);
    current.append(") ");
  }

  
  /**
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append(BLOB_DEFINITION);
  }

  /**
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append(BLOB_DEFINITION);
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    current.append("TINYINT");
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("SMALLINT");
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("TINYINT");
  }

  /**
   * Visits CodeFixedType
   */
  public void visitCodeFixedType(CodeFixedType self,
                                 int precision,
                                 int scale,
                                 ArrayList list)
    throws PositionedError
  {
    current.append("DECIMAL(");
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
    current.append("INTEGER");
  }

  /**
   * Visits ColorType
   */
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    current.append(BLOB_DEFINITION);
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
    current.append("VARCHAR(" + DEFAULT_STRING_SIZE + ")");
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
    current.append("DECIMAL(");
    current.append("" + precision);
    current.append("," + scale);
    current.append(")");
  }

  /**
   * Visits GrantUserClassStatement
   */
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
                                           int userClass,
                                           String userName)
    throws PositionedError
  {
    // laurent 20020725 : sapdb does not use the user class. TO VERIFY !
  }

  /**
   * Visits GrantPrivilegeStatement
   */
  public void visitGrantPrivilegeStatement(GrantPrivilegeStatement self,
                                           Expression tableName,
                                           ArrayList privileges,
                                           ArrayList userList,
                                           int type,
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
    if (type == GrantPrivilegeStatement.TABLE_TYPE) {
      current.append("TABLE ");
    } else if (type == GrantPrivilegeStatement.SEQUENCE_TYPE) {
      current.append("SEQUENCE ");
    }
    tableName.accept(this);
    current.append(" TO ");
    for (int i = 0; i < userList.size(); i++) {
      // !!! coco 050201 : on sapdb there is only one element on the user list
      if (i != 0) {
        current.append(", ");
      }
      current.append(userList.get(i));
    }
  }

  /**
   * Visits ImageType
   */
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {
    current.append(BLOB_DEFINITION);
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
    current.append("INTEGER");
  }

  /**
   * Visits Key
   */
  public void visitKey(Key self, List keyList)
    throws PositionedError
  {
    current.append("PRIMARY KEY (");
    for (int i = 0; i < keyList.size(); i++) {
      if (i != 0) {
        current.append(", ");
      }
      current.append(keyList.get(i));
    }
    current.append(") WITH STRUCTURE = BTREE");
    // test if the keys are correct
    if (getContext() instanceof TableDefinition) {
      TableDefinition def = (TableDefinition)getContext();
      for (int i = 0; i < keyList.size(); i++) {
        if (keyList.get(i) == null) {
          System.err.println("The number of keys in the table "
                             + ((SimpleIdentExpression)def.getTableName()).getIdent()
                             + " is not correct.");
        }
      }
    }
  }

  /**
   * Visits MonthType
   */
  public void visitMonthType(MonthType self)
    throws PositionedError
  {
    current.append("INTEGER");
  }

  /**
   * Visits ReferentialConstraintDefinition
   */
  // coco 010329 : look at the documentation to see what produce
  public void visitReferentialConstraintDefinition(ReferentialConstraintDefinition self,
                                                   String name,
                                                   FieldNameList field,
                                                   ReferencedTableAndColumns reference,
                                                   int type)
    throws PositionedError
  {
    current.append(" ADD CONSTRAINT ");
    current.append(name);
    current.append(" FOREIGN KEY (");
    field.accept(this);
    current.append(") REFERENCES ");
    reference.accept(this);
  }

  /**
   * Visits StringType
   */
  public void visitStringType(StringType self,
                              boolean fixed,
                              int width,
                              int height,
                              int convert)
    throws PositionedError
  {
    int size;

    if (width == -1) {
      size = DEFAULT_STRING_SIZE;       // arbitrary : we don't know the size
    } else {
      size = width * height;
    }

    current.append(fixed ? "CHAR(" : "VARCHAR(");
    current.append(size);
    current.append(")");
  }

  /**
   * Visits TableDefinition
   */
  public void visitTableDefinition(TableDefinition self,
                                   Expression tableName,
                                   ArrayList columns,
                                   Key key,
                                   Pragma pragma)
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
  // !!! coco 050201 : verify if it's correct or not
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    current.append("VARCHAR(256)");
  }

  /**
   * Visits TimeType
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    current.append("TIME");
  }

  /**
   * Visits TimestampType
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    current.append("TIMESTAMP");
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
                                              int type,
                                              FieldNameList field)
    throws PositionedError
  {
    switch (type) {
    case UniqueConstraintDefinition.TYP_UNIQUE:
      current.append("UNIQUE ");
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
    current.append("INTEGER");
  }

  // ----------------------------------------------------------------------
  // DATA CONSTANTS
  // ----------------------------------------------------------------------
  public static final int               DEFAULT_STRING_SIZE = 256;
  private static final String           BLOB_DEFINITION = "BLOB";
}
