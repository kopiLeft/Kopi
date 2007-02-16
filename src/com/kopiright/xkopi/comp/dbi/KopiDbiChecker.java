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
import java.util.List;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.JdbcDateLiteral;
import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
import com.kopiright.xkopi.comp.sqlc.SqlContext;
import com.kopiright.xkopi.comp.sqlc.Type;
import com.kopiright.xkopi.lib.base.DriverInterface;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public class KopiDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new KopiDbiChecker object.
   */
  public KopiDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  public String getStatementText() {
    return this.toString();
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return null; // laurent : do a KopiDriverInterface ??
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
    current.append(value.toSql());
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
    current.append("CLOB");
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
    current.append("BYTE");
    if (min != null) {
      current.append(" MIN " + min.intValue());
    }
    if (max != null) {
      current.append(" MAX " + max.intValue());
    }
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("CODE ");
    current.append("BOOL");
  }

  /**
   * Visits CodeFixedType
   */
  public void visitCodeFixedType(CodeFixedType self, int precision, int scale, ArrayList list)
    throws PositionedError
  {
    current.append("CODE ");
    current.append("FIXED (" + precision + ", " + scale + ")");
  }

  /**
   * Visits CodeLongType
   */
  public void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError
  {
    current.append("CODE ");
    current.append("LONG");
  }

  /**
   * Visits ColorType
   */
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    current.append("COLOR");
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
    current.append("ENUM(");
    for (int i = 0; i < list.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      current.append(list.get(i));
    }
    current.append(")");
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
    current.append("CODE ");
    current.append("FIXED(");
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
    current.append("IMAGE(");
    current.append("" + width);
    current.append(", " + height);
    current.append(")");
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
    current.append("INT");
    if (min != null) {
      current.append(" MIN " + min.intValue());
    }
    if (max != null) {
      current.append(" MAX " + max.intValue());
    }
  }

  /**
   * Visits Key
   */
  public void visitKey(Key self, List keyList)
    throws PositionedError
  {
    current.append("KEY IS ");
    for (int i = 0; i < keyList.size(); i++) {
      if (i != 0) {
	current.append(", ");
      }
      current.append(keyList.get(i));
    }
    // test if the keys are correct
    if (getContext() instanceof TableDefinition) {
      TableDefinition def = (TableDefinition)getContext();
      for (int i = 0; i < keyList.size(); i++) {
	if (keyList.get(i) == null) {
	  System.err.println("The number of keys in the table " + ((SimpleIdentExpression)def.getTableName()).getIdent() + " is not correct.");
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
    current.append("MONTH");
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
  public void visitStringType(StringType self, boolean fixed, int width, int height, int convert)
    throws PositionedError
  {
    if (fixed) {
      current.append("CHAR");
      if (width != 1) {
	current.append("(");
	current.append("" + width);
	if (height != 1) {
	  current.append(", " + height);
	}
	current.append(")");
      }
    } else {
      current.append("STRING");
      if (width != -1) {
	current.append("(");
	current.append("" + width);
	if (height != 1) {
	  current.append(", " + height);
	}
 	current.append(")");
      }
    }
    switch(convert) {
    case StringType.TYP_NONE:
      break;
    case StringType.TYP_UPPER:
      current.append(" CONVERT UPPER");
      break;
    case StringType.TYP_LOWER:
      current.append(" CONVERT LOWER");
      break;
    case StringType.TYP_NAME:
      current.append(" CONVERT NAME");
      break;
    default:
      throw new InconsistencyException("Unexpected String Type");
    }
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
    current.append("STRING(");
    current.append("" + width);
    current.append(", " + height);
    current.append(")");
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
    current.append("WEEK");
  }
}
