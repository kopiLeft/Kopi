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
import org.kopi.xkopi.comp.sqlc.FromClause;
import org.kopi.xkopi.comp.sqlc.GroupByClause;
import org.kopi.xkopi.comp.sqlc.HavingClause;
import org.kopi.xkopi.comp.sqlc.InsertSource;
import org.kopi.xkopi.comp.sqlc.InsertStatement;
import org.kopi.xkopi.comp.sqlc.JdbcDateLiteral;
import org.kopi.xkopi.comp.sqlc.SearchCondition;
import org.kopi.xkopi.comp.sqlc.SelectExpression;
import org.kopi.xkopi.comp.sqlc.SelectStatement;
import org.kopi.xkopi.comp.sqlc.SortSpec;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.TableName;
import org.kopi.xkopi.comp.sqlc.TableReference;
import org.kopi.xkopi.comp.sqlc.Type;
import org.kopi.xkopi.comp.sqlc.UpdSpec;
import org.kopi.xkopi.comp.sqlc.UpdateStatement;
import org.kopi.xkopi.comp.sqlc.WhereClause;
import org.kopi.xkopi.lib.base.DriverInterface;
import org.kopi.xkopi.lib.base.SapdbDriverInterface;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;

/**
 *
 */
public class OraSpoolerDbiChecker extends DbiChecker implements DbiVisitor {

  /**
   * Constructs a new SpoolerDbiChecker object.
   */
  public OraSpoolerDbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   *
   */
  public String getStatementText() {
    String      statement = getStatementText(getDriverInterface());

    return statement.length() != 0 ? "" + statement + ";" : "";
  }
  
  private void writeHeader(int width) {
    current.append("SET ECHO OFF;");
    current.append("\n");
    current.append("SET TERMOUT OFF;");
    current.append("\n");
    current.append("SET TRIMOUT OFF;");
    current.append("\n");
    current.append("SET TAB OFF;");
    current.append("\n");
    current.append("SET HEADING OFF;");
    current.append("\n");
    current.append("SET FEEDBACK OFF;");
    current.append("\n");
    current.append("SET VERIFY OFF;");
    current.append("\n");
    current.append("SET NEWPAGE 0;");
    current.append("\n");
    current.append("SET LINE " + width + ";");
    current.append("\n");
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
    if (ident.contains("#") ||
        ident.contains("-")) {
      ident = "\"" + ident + "\"";
    }
    
    if (type instanceof TimeType) {
      visitTimeColumn(ident, nullable);
    } else if (type instanceof TimestampType) {
      visitTimestampColumn(ident, nullable);
    } else if (type instanceof DateType) {
      visitDateColumn(ident, nullable);
    } else if (type instanceof StringType) {
      visitStringColumn(ident, nullable);
    } else if (type instanceof TextType) {
      visitStringColumn(ident, nullable);
    } else if (type instanceof IntType) {
      visitNumericColumn(ident, nullable);
    } else if (type instanceof ShortType) {
      visitNumericColumn(ident, nullable);
    } else if (type instanceof ByteType) {
      visitNumericColumn(ident, nullable);
    } else if (type instanceof FixedType) {
      visitNumericColumn(ident, nullable);
    } else if (type instanceof BooleanType) {
      visitBooleanColumn(ident, nullable);
    } else if (type instanceof BlobType) {
      visitBlobColumn(ident, nullable);
    } else {
      throw new InconsistencyException("--------- UNHANDLED TYPE  "  + type   + "-----------");
    }
  }
  
  private void visitBooleanColumn(String column, boolean nullable) {
    current.append("DECODE(" +  column + ", 0, 'FALSE', 'TRUE')");
  }
  

  private void visitBlobColumn(String column, boolean nullable) {
      current.append("'?'");
  }
  
  private void visitStringColumn(String column, boolean nullable) {
    if (!nullable) {
      current.append(column);
    } else {
      current.append("NVL(" + column + ", '?')");
    }
  }
  
  private void visitNumericColumn(String column, boolean nullable) {
    if (!nullable) {
      current.append(column);
    } else {
      current.append("NVL(TO_CHAR(" + column + "), '?')");
    }
  }
  
  private void visitDateColumn(String column, boolean nullable) {
    if (!nullable) {
      current.append("TO_CHAR(" + column + ", 'YYYY-MM-DD')");
    } else {
      current.append("NVL(TO_CHAR(" + column +", 'YYYY-MM-DD'), '?')");
    }
  }
  
  private void visitTimeColumn(String column, boolean nullable) {
    if (!nullable) {
      current.append("TO_CHAR(" + column + ", 'HH24:MI:SS')");
    } else {
      current.append("NVL(TO_CHAR(" + column + ", 'HH24:MI:SS'), '?')");
    }
  }
  
  private void visitTimestampColumn(String column, boolean nullable) {
    if (!nullable) {
      current.append("TO_CHAR(" + column + ", 'YYYY-MM-DD HH24:MI:SS')");
    } else {
      current.append("NVL(TO_CHAR(" + column + ", 'YYYY-MM-DD HH24:MI:SS'), '?')");
    }
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
    int width = 0;
    
    for (int i = 0; i < columns.size(); i++) {
      Type type = ((Column)columns.get(i)).getType();
      
      width += 1;
      if (type instanceof TimeType) {
        width += 8;
      } else if (type instanceof TimestampType) {
        width += 19;
      } else if (type instanceof DateType) {
        width += 10;
      } else if (type instanceof StringType) {
        width += ((StringType)type).getWidth() * ((StringType)type).getHeight();
      } else if (type instanceof TextType) {
        width += ((TextType)type).getWidth() * ((TextType)type).getHeight();
      } else if (type instanceof IntType) {
        width += 12;
      } else if (type instanceof ShortType) {
        width += 12;
      } else if (type instanceof ByteType) {
        width += 1;
      } else if (type instanceof FixedType) {
        width += ((FixedType)type).getPrecision() + ((FixedType)type).getScale() + 1;
      } else if (type instanceof BooleanType) {
        width += 5;
      } else if (type instanceof BlobType) {
        width += 1;
      } else {
        throw new InconsistencyException("--------- UNHANDLED TYPE  "  + type   + "-----------");
      }
    }
    
    writeHeader(width);
      
    current.append("SPOOL ");
    tableName.accept(this);
    current.append(".dat;");
    current.append("\n");
    current.append("SELECT  ");
    
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
        current.append(" || '\t' || ");
      }
      ((Column)columns.get(i)).accept(this);
    }
    current.append(" FROM ");

    tableName.accept(this);
    current.append(";");
    current.append("\n");
    current.append("SPOOL OFF");
    current.append("\n");
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
