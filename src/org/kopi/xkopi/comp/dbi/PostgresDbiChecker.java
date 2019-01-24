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

package org.kopi.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.List;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.comp.sqlc.Expression;
import org.kopi.xkopi.comp.sqlc.FieldNameList;
import org.kopi.xkopi.comp.sqlc.JdbcDateLiteral;
import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.Type;
import org.kopi.xkopi.lib.base.DriverInterface;
import org.kopi.xkopi.lib.base.PostgresDriverInterface;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;

/**
 *
 */
public class PostgresDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new PostgresDbiChecker object.
   */
  public PostgresDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  @Override
  public String getStatementText() {
    String stmt = getStatementText(getDriverInterface());
    //System.out.println(stmt);
    return stmt.length() != 0 ? stmt + ";": "";
  }

  /**
   *
   */
  @Override
  /*package*/ DriverInterface getDriverInterface() {
    return new PostgresDriverInterface();
  }

  /*
   * Visits JdbcDateLiteral
   * !!! coco 310202 : verify the syntax
   */
  @Override
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append("'");
    current.append(value.getYear());
    current.append("-");
    current.append(value.getMonth());
    current.append("-");
    current.append(value.getDay());
    current.append("'");
  }

  /**
   * Visits AddTableConstraintStatement
   */
  @Override
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
   * Visits DropSequenceStatement
   */
  @Override
  public void visitDropSequenceStatement(DropSequenceStatement self,
                                         Expression sequenceName)
    throws PositionedError
  {
    current.append("DROP SEQUENCE ");
    sequenceName.accept(this);
  }


  @Override
  public void visitSequenceDefinition(SequenceDefinition self,
                                      Expression sequenceName,
                                      Integer startValue)
    throws PositionedError
  {
    current.append("CREATE SEQUENCE ");
    sequenceName.accept(this);
    current.append(" START WITH " + startValue );

  }


  /**
   * Visits BlobType
   */
  @Override
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append("bytea");
  }

  /**
   * Visits ClobType
   */
  @Override
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append("TEXT");
  }

  /**
   * Visits BooleanType
   */
  @Override
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    current.append("BOOLEAN");
  }

  /**
   * Visits ByteType
   */
  @Override
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("SMALLINT");
  }

  /**
   * Visits CodeBoolType
   */
  @Override
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("BOOLEAN");
  }

  /**
   * Visits CodeFixedType
   */
  @Override
  public void visitCodeFixedType(CodeFixedType self,
                                 int precision,
                                 int scale,
                                 ArrayList list)
    throws PositionedError
  {
    current.append("NUMERIC(");
    current.append("" + precision);
    current.append("," + scale);
    current.append(")");
  }

  /**
   * Visits FloatType
   */
  @Override
  public void visitFloatType(FloatType self)
    throws PositionedError
  {
    current.append("FLOAT");
  }

  /**
   * Visits DoubleType
   */
  @Override
  public void visitDoubleType(DoubleType self)
    throws PositionedError
  {
    current.append("DOUBLE");
  }

  /**
   * Visits CodeLongType
   */
  @Override
  public void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError
  {
    current.append("INTEGER");
  }

  /**
   * Visits ColorType
   */
  @Override
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    current.append("bytea");
  }

  /**
   * Visits Column
   */
  public void visitColumn(Column self,
                          String ident,
                          Type type,
                          boolean nullable,
                          String constraintName,
                          JavaStyleComment[] comment)
    throws PositionedError
  {

    current.append(ident);
    current.append(" ");
    type.accept(this);
    if (!nullable) {
      current.append(" NOT NULL");
    }
    if (constraintName != null) {
      current.append(" CONSTRAINT ");
      current.append(constraintName);
    }
  }

  /**
   * Visits DateType
   */
  @Override
  public void visitDateType(DateType self)
    throws PositionedError
  {
    current.append("DATE");
  }

  /**
   * Visits EnumType
   */
  @Override
  public void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError
  {
    current.append("VARCHAR(256)");
  }

  /**
   * Visits FixedType
   */
  @Override
  public void visitFixedType(FixedType self,
                             int precision,
                             int scale,
                             Fixed min,
                             Fixed max)
    throws PositionedError
  {
    current.append("NUMERIC(");
    current.append("" + precision);
    current.append("," + scale);
    current.append(")");
  }

  /**
   * Visits ImageType
   */
  @Override
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {
    current.append("bytea");
  }

  /**
   * Visits GrantUserClassStatement
   */
  @Override
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
                                           int userClass,
                                           String userName)
    throws PositionedError
  {
    // current.append("GRANT ");
    // switch(userClass) {
    // case GrantUserClassStatement.TYP_ACCESS:
    //   current.append("ACCESS TO ");
    //   break;
    // case GrantUserClassStatement.TYP_RESOURCE:
    //   current.append("RESOURCE TO ");
    //   break;
    // case GrantUserClassStatement.TYP_DBA:
    //   current.append("DBA TO ");
    //   break;
    // default:
    //   throw new InconsistencyException("Unexpected privilege");
    // }
    // current.append(userName);
  }

  /**
   * Visits IndexDefinition
   */
  @Override
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
  @Override
  public void visitIntType(IntType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("INTEGER");
  }

  /**
   * Visits Key
   */
  @Override
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
    current.append(")");
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
  @Override
  public void visitMonthType(MonthType self)
    throws PositionedError
  {
    current.append("INTEGER");
  }

  /**
   * Visits ReferentialConstraintDefinition
   */
  // coco 010329 : look at the documentation to see what produce
  @Override
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
    switch (type) {
    case ReferentialConstraintDefinition.TYP_RESTRICT:
      current.append(" ON DELETE RESTRICT");
      break;
    case ReferentialConstraintDefinition.TYP_CASCADE:
      current.append(" ON DELETE CASCADE");
      break;
    case ReferentialConstraintDefinition.TYP_NULL:
      current.append(" ON DELETE SET NULL");
      break;
    case ReferentialConstraintDefinition.TYP_DEFAULT:
      current.append(" ON DELETE SET DEFAULT");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
    // set constraint deferrable but set it to immediate as a default behavior
    current.append(" DEFERRABLE INITIALLY IMMEDIATE");
  }

  /**
   * Visits StringType
   */
  @Override
  public void visitStringType(StringType self,
                              boolean fixed,
                              int width,
                              int height,
                              int convert)
    throws PositionedError
  {
    int size;

    if (width == -1) {
      size = 256;       // arbitrary : we don't know the size
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
  @Override
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
  @Override
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    current.append("TEXT");
  }

  /**
   * Visits TimeType
   */
  @Override
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    current.append("TIME");
  }

  /**
   * Visits TimestampType
   */
  @Override
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    current.append("TIMESTAMP");
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  @Override
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
  @Override
  public void visitWeekType(WeekType self)
    throws PositionedError
  {
    current.append("INTEGER");
  }
}
