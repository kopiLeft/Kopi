/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: PostgresDbiChecker.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.List;

import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.xkopi.comp.sqlc.*;
import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.base.DriverInterface;
import at.dms.xkopi.lib.base.PostgresDriverInterface;
import at.dms.xkopi.lib.type.Date;
import at.dms.xkopi.lib.type.Fixed;

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
  public String getStatementText() {
    return getStatementText(getDriverInterface()) + ";";
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return new PostgresDriverInterface();
  }

  /*
   * Visits JdbcDateLiteral
   * !!! coco 310202 : verify the syntax
   */
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
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    current.append("OID");
  }

  /**
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    current.append("???");
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    current.append("BOOLEAN");
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("INT2");
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    current.append("BOOLEAN");
  }

  /**
   * Visits CodeFixedType
   */
  public void visitCodeFixedType(CodeFixedType self, int precision, int scale, ArrayList list)
    throws PositionedError
  {
    // !!! graf 990814: scale, precision missing
    // !!! coco : put the scale and the precision
    current.append("NUMERIC(");
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
    current.append("OID");
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
    current.append("VARCHAR(256)");
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
    current.append("," + scale);
    current.append(")");
  }

  /**
   * Visits ImageType
   */
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {
    current.append("OID");
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
     current.append(")");
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
    current.append(" FOREIGN KEY ");
    current.append(name);
    current.append(" (");
    field.accept(this);
    current.append(") REFERENCES ");
    reference.accept(this);
  }

  /**
   * Visits StringType
   */
  public void visitStringType(StringType self, boolean fixed, int width, int height, int convert)
    throws PositionedError
  {
    int		size;

    if (width == -1) {
      size = 256;	// arbitrary : we don't know the size
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
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self, int type, FieldNameList field)
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
}
