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
 * $Id: TbSpoolerDbiChecker.java 33847 2009-02-11 09:51:39Z bmwael $
 */

package com.kopiright.xkopi.comp.dbi;

import java.util.ArrayList;
import java.util.List;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.sqlc.*;
import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.JdbcDateLiteral;
import com.kopiright.xkopi.comp.sqlc.JdbcDateLiteral;
import com.kopiright.xkopi.comp.sqlc.SelectStatement;
import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
import com.kopiright.xkopi.comp.sqlc.SqlChecker;
import com.kopiright.xkopi.comp.sqlc.SqlContext;
import com.kopiright.xkopi.comp.sqlc.Statement;
import com.kopiright.xkopi.comp.sqlc.TableReference;
import com.kopiright.xkopi.comp.sqlc.Type;
import com.kopiright.xkopi.comp.sqlc.TypeName;
import com.kopiright.xkopi.lib.base.DBException;
import com.kopiright.xkopi.lib.base.DriverInterface;
import com.kopiright.xkopi.lib.base.SapdbDriverInterface;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public class TbSpoolerDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new TbSpoolerDbiChecker object.
   */
  public TbSpoolerDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  public String getStatementText() {
    String      statement = getStatementText(getDriverInterface());

    return statement.length() != 0 ? "" + statement + ";" : "";
  }

  /**
   *
   */
  /*package*/ DriverInterface getDriverInterface() {
    return new SapdbDriverInterface();
  }

  /**
   * Visits DropIndexStatement
   */
  public void visitDropIndexStatement(DropIndexStatement self, String indexName)
    throws PositionedError
  {
  }

  /**
   * Visits DropTableConstraintStatement
   */
  public void visitDropTableConstraintStatement(DropTableConstraintStatement self,
                                                Expression tableName,
                                                String constraintName)
    throws PositionedError
  {
  }


  /**
   * Visits DropViewStatement
   */
  public void visitDropViewStatement(DropViewStatement self, Expression viewName)
    throws PositionedError
  {
  }

  /**
   * Visits DropTableStatement
   */
  public void visitDropTableStatement(DropTableStatement self, Expression tableName)
    throws PositionedError
  {
  }

  /**
   * Visits DropSequenceStatement
   */
  public void visitDropSequenceStatement(DropSequenceStatement self,
                                         Expression sequenceName)
    throws PositionedError
  {
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
  }

  /**
   * Visits Column
   */
  public void visitColumn(Column self,
                          String ident,
                          Type type,
                          boolean nullable,
                          Expression defaultValue,
                          String constraintName,
                          JavaStyleComment[] comment)
    throws PositionedError
  {
    if (type instanceof DateType) {
      visitDateType(ident);
    } else if (type instanceof TimestampType) {
      visitTimestampType(ident);
    } else if (type instanceof TimeType) {
      visitTimeType(ident);
    } else {
      current.append(ident);
    }
    current.append(" ");
  }
  
  public void visitUpdateStatement(UpdateStatement self,
                                   TableName table,
                                   ArrayList listIdent,
                                   SearchCondition searchCondition,
                                   boolean hasCondition)
    throws PositionedError {
    
  }
  
  /**
   * Visits SELECT
   */
  public void visitSelectStatement(SelectStatement self,
                                   String type,
                                   ArrayList columns,
                                   FromClause fromClause,
                                   WhereClause whereClause,
                                   GroupByClause groupByClause,
                                   HavingClause havingClause,
                                   SortSpec sortSpec,
                                   UpdSpec updateSpec)
    throws PositionedError {
    
  }
  
  /**
   * Visits SELECT
   */
  public void visitSelectExpression(SelectExpression self,
                                    String type,
                                    ArrayList columns,
                                    FromClause fromClause,
                                    WhereClause whereClause,
                                    GroupByClause groupByClause,
                                    HavingClause havingClause)
    throws PositionedError {
    
  }
  
  /**
   * Visits Insert statement
   * @param	ident		type ident of the table
   * @param	fields		the field name list
   * @param	soure		the source of data from clause
   */
  public void visitInsertStatement(InsertStatement self,
                                   String ident,
                                   FieldNameList fields,
                                   InsertSource source)
    throws PositionedError {
    
  }
  
  /**
   * Visits SequenceDefinition
   */
  public void visitSequenceDefinition(SequenceDefinition self,
                                      Expression sequenceName,
                                      Integer startValue)
    throws PositionedError
  {
  }

    /**
   * Visits ViewDefinition
   */
  public void visitViewDefinition(ViewDefinition self,
                                  Expression tableName,
                                  ArrayList columnNameList,
                                  TableReference reference)
    throws PositionedError
  {
  }
  
  /**
   * Visits ShortType
   */
  public void visitShortType(ShortType self, Integer min, Integer max)
    throws PositionedError
  {
  }

  /*
   * Visits JdbcDateLiteral
   */
  public void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError
  {
  }

  /**
   * Visits AlterDropDefaultColumnStatement
   */
  public void visitAlterDropDefaultColumnStatement(AlterDropDefaultColumnStatement self,
                                                   Expression tableName,
                                                   String columnName)
    throws PositionedError
  {
    
  }
  
  /**
   * Visits a AlterNotNullColumnStatement.
   */
  public void visitAlterNotNullColumnStatement(AlterNotNullColumnStatement self,
                                               Expression tableName,
                                               String columnName)
    throws PositionedError
  {
  }


  /**
   * Visits AddTableConstraintStatement
   */
  public void visitAddTableConstraintStatement(AddTableConstraintStatement self,
                                               Expression tableName,
                                               TableConstraint tableConstraint)
    throws PositionedError
  {
  }

  /**
   * Visits a AddTableColumnsStatement.
   */
  public void visitAddTableColumnsStatement(AddTableColumnsStatement self,
                                            Expression tableName,
                                            Column column)
    throws PositionedError
  {
  }


  /**
   * Visits BlobType
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    
  }

  /**
   * Visits ClobType
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    //current.append(BLOB_DEFINITION);
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    //current.append("BOOLEAN");
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    //current.append("SMALLINT");
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    //current.append("BOOLEAN");
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
  }

  /**
   * Visits CodeLongType
   */
  public void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError
  {
    
  }

  /**
   * Visits ColorType
   */
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    
  }

  private void visitDateType(String column) {
    String sql;
    
    sql = "CASE WHEN (YY  OF " + column + ") < 10 THEN '199' || TO_CHAR(YY  OF " + column + ") "
      +    "WHEN (YY  OF " + column + ") < 100 THEN '19' || TO_CHAR(YY  OF " + column + ") "
      +    "WHEN (YY  OF " + column + ") < 1000 THEN '1' || TO_CHAR(YY  OF " + column + ") ELSE TO_CHAR(YY  OF " + column + ") END "
      +    "|| '-' || "
      +    "IF (MO OF " + column + ") < 10  THEN '0' || TO_CHAR(MO OF " + column + ") ELSE TO_CHAR(MO OF " + column + ")  FI "
      +    "|| '-' || "
      +    "IF (DD OF " + column + ") < 10  THEN '0' ||  TO_CHAR(DD OF " + column + ") ELSE TO_CHAR(DD OF " + column + ")  FI";
    
    current.append(sql);
  }

  private void visitTimeType(String column) {
    String sql;
    
    sql = "IF (HH OF " + column + ") < 10 THEN '0' || TO_CHAR(HH OF " + column + ") ELSE TO_CHAR(HH OF " + column + ") FI " 
      +    "|| ':' || "
      +    "IF (MI OF " + column + ") < 10 THEN '0' || TO_CHAR(MI OF " + column + ") ELSE TO_CHAR(MI OF " + column + ") FI "
      +    "|| ':' || "
      +    "IF (SS OF " + column + ") < 10 THEN '0' || TO_CHAR(SS OF " + column + ") ELSE TO_CHAR(SS OF " + column + ") FI";
    
    current.append(sql);
  }

    private void visitTimestampType(String column) {
    String sql;
    
    sql = "CASE WHEN (YY  OF " + column + ") < 10 THEN '199' || TO_CHAR(YY  OF " + column + ") "
      +    "WHEN (YY  OF " + column + ") < 100 THEN '19' || TO_CHAR(YY  OF " + column + ") "
      +    "WHEN (YY  OF " + column + ") < 1000 THEN '1' || TO_CHAR(YY  OF " + column + ") ELSE TO_CHAR(YY  OF " + column + ") END "
      +    "|| '-' || "
      +    "IF (MO OF " + column + ") < 10  THEN '0' || TO_CHAR(MO OF " + column + ") ELSE TO_CHAR(MO OF " + column + ")  FI "
      +    "|| '-' || "
      +    "IF (DD OF " + column + ") < 10  THEN '0' ||  TO_CHAR(DD OF " + column + ") ELSE TO_CHAR(DD OF " + column + ")  FI "
      +    "|| ' ' || "
      +    "IF (HH OF " + column + ") < 10 THEN '0' || TO_CHAR(HH OF " + column + ") ELSE TO_CHAR(HH OF " + column + ") FI "
      +    "|| ':' || "
      +    "IF (MI OF " + column + ") < 10 THEN '0' || TO_CHAR(MI OF " + column + ") ELSE TO_CHAR(MI OF " + column + ") FI "
      +    "|| ':' || "
      +    "IF (SS OF " + column + ") < 10 THEN '0' || TO_CHAR(SS OF " + column + ") ELSE TO_CHAR(SS OF " + column + ") FI " 
      +    "|| '.' || "
      +    "TO_CHAR(MS OF " + column + ")";
    
    current.append(sql);
    }
  
  /**
   * Visits DateType
   */
  public void visitDateType(DateType self)
    throws PositionedError
  {
  }

  /**
   * Visits EnumType
   */
  public void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError
  {
        
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
  }

  /**
   * Visits GrantUserClassStatement
   */
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
                                           int userClass,
                                           String userName)
    throws PositionedError
  {

  }

  /**
   * Visits ImageType
   */
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {

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
  }

  /**
   * Visits IntType
   */
  public void visitIntType(IntType self, Integer min, Integer max)
    throws PositionedError
  {
  }

  /**
   * Visits Key
   */
  public void visitKey(Key self, List keyList)
    throws PositionedError
  {
  }

  /**
   * Visits MonthType
   */
  public void visitMonthType(MonthType self)
    throws PositionedError
  {
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
    boolean containsDateType = false;
    
    current.append("SPOOL INTO  /tmp/");
    tableName.accept(this);
    current.append(" SELECT  ");
    
    for (int i = 0; i < columns.size(); i++) {
      Type type = ((Column)columns.get(i)).getType();
      
      if (type instanceof TimeType ||
          type instanceof TimestampType ||
          type instanceof DateType) {
        containsDateType = true;
        break;
      }
    }
    
    if (containsDateType) {
      for (int i = 0; i < columns.size(); i++) {
        if (i != 0) {
          current.append(", ");
        }
        ((Column)columns.get(i)).accept(this);
      }
    } else {
      current.append(" * ");
    }
    current.append(" FROM ");
    tableName.accept(this);
  }

  /**
   * Visits TextType
   */
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    //current.append("VARCHAR(256)");
  }

  /**
   * Visits TimeType
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    current.append("CAST DATETIME[HH:SS]");
  }

  /**
   * Visits TimestampType
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    current.append("CAST DATETIME[YY:MS]");
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
                                              int type,
                                              FieldNameList field)
    throws PositionedError
  {
  }

  /**
   * Visits WeekType
   */
  public void visitWeekType(WeekType self)
    throws PositionedError
  {
    
  }
}
