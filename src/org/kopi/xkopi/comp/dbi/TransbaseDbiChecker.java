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
import java.util.ListIterator;

import org.kopi.compiler.base.PositionedError;
import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.comp.sqlc.CastPrimary;
import org.kopi.xkopi.comp.sqlc.Expression;
import org.kopi.xkopi.comp.sqlc.FieldNameList;
import org.kopi.xkopi.comp.sqlc.JdbcDateLiteral;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.Type;
import org.kopi.xkopi.lib.base.DriverInterface;
import org.kopi.xkopi.lib.base.TbxDriverInterface;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;

/**
 *
 */
public class TransbaseDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new TransbaseDbiChecker object.
   */
  public TransbaseDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  public String getStatementText() {
    // we use the tbx driver because it expands functions and other escapes
    return getStatementText(getDriverInterface()) + ";";
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return new TbxDriverInterface();
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append("DATETIME(");
    current.append(value.getYear());
    current.append("-");
    if (value.getMonth() < 10) {
      current.append("0");
    }
    current.append(value.getMonth());
    current.append("-");
    if (value.getDay() < 10) {
      current.append("0");
    }
    current.append(value.getDay());
    current.append(")");
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
  
  /*
   * Visits CastPrimary
   */
  public void visitCastPrimary(CastPrimary self, Expression left, Type right)
    throws PositionedError
  {
    current.append("CAST (");
    left.accept(this);
    current.append(" AS ");
    right.accept(this);
    current.append(")");
  }
  
  /**
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append("BLOB");
  }

  /**
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append("BLOB");
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    current.append("BOOL");
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("TINYINT");
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("BOOL");
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
    // !!! graf 990814: scale, precision missing
    current.append("NUMERIC");
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
    current.append("BLOB");
  }

  /**
   * Visits DateType
   */
  public void visitDateType(DateType self)
    throws PositionedError
  {
    current.append("DATETIME[YY:DD]");
  }

  /**
   * Visits EnumType
   */
  public void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError
  {
    current.append("CHAR(*)");
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
    current.append("NUMERIC(");
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
    current.append("BLOB");
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
    //!!! coco 050201 : for sapdb there is no type after : it's all the time TYP_NONE
    switch(type) {
    case IndexDefinition.TYP_NONE:
      break;
    case IndexDefinition.TYP_PRIMARY:
      current.append(" WITH PRIMARY");
      break;
    case IndexDefinition.TYP_DISALLOW:
      current.append(" WITH DISALLOW NULL");
      break;
    case IndexDefinition.TYP_IGNORE:
      current.append(" WITH IGNORE NULL");
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
    ListIterator        iterator = keyList.listIterator();

    current.append("KEY IS ");
    current.append(iterator.next());
    while (iterator.hasNext()) {
      current.append(", ");
      current.append(iterator.next());
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
    case ReferentialConstraintDefinition.TYP_RESTRICT:
      break;
    case ReferentialConstraintDefinition.TYP_CASCADE:
      current.append(" ON DELETE CASCADE");
      break;
    case ReferentialConstraintDefinition.TYP_NULL:
      current.append(" ON DELETE SET DEFAULT");
      break;
    case ReferentialConstraintDefinition.TYP_DEFAULT:
      current.append(" ON DELETE SET DEFAULT");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
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
    if (fixed) {
      current.append("CHAR(");
      current.append(width * height);
      current.append(")");
    } else {
      current.append("CHAR(*)");
    }
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
    current.append(")");
    if (key != null) {
      current.append(" ");
      key.accept(this);
    }
  }

  /**
   * Visits TextType
   */
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    current.append("BLOB");
  }

  /**
   * Visits TimeType
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    current.append("DATETIME[HH:SS]");
  }

  /**
   * Visits TimestampType
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    current.append("DATETIME[YY:MS]");
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
    case UniqueConstraintDefinition.TYP_PRIMARY:
      current.append("PRIMARY KEY (");
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
}
