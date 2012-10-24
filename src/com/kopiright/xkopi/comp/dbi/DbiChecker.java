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

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.JdbcDateLiteral;
import com.kopiright.xkopi.comp.sqlc.SelectStatement;
import com.kopiright.xkopi.comp.sqlc.SqlChecker;
import com.kopiright.xkopi.comp.sqlc.SqlContext;
import com.kopiright.xkopi.comp.sqlc.Statement;
import com.kopiright.xkopi.comp.sqlc.TableReference;
import com.kopiright.xkopi.comp.sqlc.Type;
import com.kopiright.xkopi.comp.sqlc.TypeName;
import com.kopiright.xkopi.lib.base.DBException;
import com.kopiright.xkopi.lib.base.DriverInterface;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public abstract class DbiChecker extends SqlChecker implements DbiVisitor {

  /**
   * Constructs a new DbiChecker object.
   */
  public DbiChecker(SqlContext sql) {
    super(sql);
  }

  /**
   * Constructs a new DbiChecker object for the specified syntax.
   */
  public static DbiChecker create(String syntax) {
    return create(syntax, null);
  }

  /**
   * Constructs a new DbiChecker object for the specified syntax.
   */
  public static DbiChecker create(String syntax, SqlContext context) {
    if (syntax.equals("sap")) {
      return new SapdbDbiChecker(context);
    } else if (syntax.equals("tbx")) {
      return new TransbaseDbiChecker(context);
    } else if (syntax.equals("pg")) {
      return new PostgresDbiChecker(context);
    } else if (syntax.equals("ora")) {
      return new OracleDbiChecker(context);
    } else if (syntax.equals("mysql")) {
      return new MysqlDbiChecker(context);
    } else if (syntax.equals("ingres")) {
      return new IngresDbiChecker(context);
    } else if (syntax.equals("as400")) {
      return new As400DbiChecker(context);
    } else if (syntax.equals("tbspool")) {
      return new TbSpoolerDbiChecker(context);
    } else if (syntax.equals("oraspool")) {
      return new OraSpoolerDbiChecker(context);
    } else {
      System.err.println("syntax " + syntax + " not found, using default syntax." );
      return new KopiDbiChecker(context);
    }
  }

  /**
   *
   */
  public abstract String getStatementText();

  /**
   *
   */
  /*package*/ abstract DriverInterface getDriverInterface();

  /**
   *
   */
  public String getStatementText(DriverInterface driver) {
    try {
      final String text = driver.convertSql(current.toString());

      current = new StringBuffer();

      return text;
    } catch (DBException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Visits CompilationUnit (root of the syntax abstract tree)
   */
  public final void visitSCompilationUnit(SCompilationUnit self,
                                          String packageName,
                                          int version,
                                          ArrayList elems)
    throws PositionedError
  {
    for(int i = 0; i < elems.size(); i++) {
      ((Statement)elems.get(i)).accept(this);
      // cast based on the addStmt method in SCompilationUnit
    }
  }


  /**
   * Visits JdbcDateLiteral
   * @param     value   the JDBC date
   */
  public abstract void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError;


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
    current.append(" ALTER ");
    current.append(columnName);
    current.append(" DROP DEFAULT");
  }

  /**
   * Visits AlterSetDefaultColumnStatement
   */
  public void visitAlterSetDefaultColumnStatement(AlterSetDefaultColumnStatement self,
                                                  Expression tableName,
                                                  String columnName,
                                                  Expression defaultValue)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append(" ALTER ");
    current.append(columnName);
    current.append(" SET DEFAULT ");
    defaultValue.accept(this);
  }

  /**
   * Visits a AlterNotNullColumnStatement.
   */
  public void visitAlterNotNullColumnStatement(AlterNotNullColumnStatement self,
                                               Expression tableName,
                                               String columnName)
    throws PositionedError
  {
    throw new RuntimeException("NOT YET IMPLEMENTED FOR THIS DbiChecker.");
  }

  /**
   * Visits AddTableConstraintStatement
   */
  public abstract void visitAddTableConstraintStatement(AddTableConstraintStatement self,
                                                        Expression tableName,
                                                        TableConstraint tableConstraint)
    throws PositionedError;

  /**
   * Visits a AddTableColumnsStatement.
   */
  public void visitAddTableColumnsStatement(AddTableColumnsStatement self,
                                            Expression tableName,
                                            Column column)
    throws PositionedError
  {
    throw new RuntimeException("NOT YET IMPLEMENTED FOR THIS DbiChecker.");
  }

  /**
   * Visits BlobType
   */
  public abstract void visitBlobType(BlobType self) throws PositionedError;

  /**
   * Visits BooleanType
   */
  public abstract void visitBooleanType(BooleanType self) throws PositionedError;

  /**
   * Visits ByteType
   */
  public abstract void visitByteType(ByteType self,
                                     Integer min,
                                     Integer max)
    throws PositionedError;

  /**
   * Visits CodeBoolType
   */
  public abstract void visitCodeBoolType(CodeBoolType self,
                                         ArrayList list)
    throws PositionedError;

  /**
   * Visits CodeFixedDesc
   */
  public void visitCodeFixedDesc(CodeFixedDesc self, String name, Fixed value)
    throws PositionedError
  {
    current.append('"' + name + '"');
    current.append("= ");
    current.append(value.toString());
  }

  /**
   * Visits CodeLongDesc
   */
  public void visitCodeLongDesc(CodeLongDesc self, String name, Integer value)
    throws PositionedError
  {
    current.append('"' + name + '"');
    current.append("= ");
    current.append(value.toString());
  }

  /**
   * Visits CodeBoolDesc
   */
  public void visitCodeBoolDesc(CodeBoolDesc self, String name, Boolean value)
    throws PositionedError
  {
    current.append('"' + name + '"');
    current.append("= ");
    current.append(value.booleanValue() ? "TRUE" : "FALSE");
  }

  /**
   * Visits CodeFixedType
   */
  public abstract void visitCodeFixedType(CodeFixedType self,
                                          int precision,
                                          int scale,
                                          ArrayList list)
    throws PositionedError;

  /**
   * Visits CodeLongType
   */
  public abstract void visitCodeLongType(CodeLongType self,
                                         ArrayList list)
    throws PositionedError;

  /**
   * Visits ColorType
   */
  public abstract void visitColorType(ColorType self) throws PositionedError;

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
    current.append(ident);
    current.append(" ");
    type.accept(this);
    if (!nullable) {
      current.append(" NOT NULL");
    }
    if (defaultValue != null) {
      current.append(" DEFAULT ");
      defaultValue.accept(this);
    }
    if (constraintName != null) {
      current.append(" CONSTRAINT ");
      current.append(constraintName);
    }
  }

  /**
   * Visits DateType
   */
  public abstract void visitDateType(DateType self) throws PositionedError;

  /**
   * Visits DelimSpec
   */
  public void visitDelimSpec(DelimSpec self, String literal)
    throws PositionedError
  {
    current.append("NULL IS ");
    current.append(literal);
  }

  /**
   * Visits DropIndexStatement
   */
  public void visitDropIndexStatement(DropIndexStatement self, String indexName)
    throws PositionedError
  {
    current.append("DROP INDEX ");
    current.append(indexName);
  }

  /**
   * Visits DropTableConstraintStatement
   */
  public void visitDropTableConstraintStatement(DropTableConstraintStatement self,
                                                Expression tableName,
                                                String constraintName)
    throws PositionedError
  {
    current.append("ALTER TABLE ");
    tableName.accept(this);
    current.append(" DROP CONSTRAINT ");
    current.append(constraintName);
  }


  /**
   * Visits DropViewStatement
   */
  public void visitDropViewStatement(DropViewStatement self, Expression viewName)
    throws PositionedError
  {
    current.append("DROP VIEW ");
    viewName.accept(this);
  }

  /**
   * Visits DropTableStatement
   */
  public void visitDropTableStatement(DropTableStatement self, Expression tableName)
    throws PositionedError
  {
    current.append("DROP TABLE ");
    tableName.accept(this);
  }

  /**
   * Visits DropSequenceStatement
   */
  public void visitDropSequenceStatement(DropSequenceStatement self,
                                         Expression sequenceName)
    throws PositionedError
  {
    current.append("DROP SEQUENCE ");
    sequenceName.accept(this);
  }

  /**
   * Visits EnumType
   */
  public abstract void visitEnumType(EnumType self,
                                     ArrayList list)
    throws PositionedError;

  /**
   * Visits FixedType
   */
  public abstract void visitFixedType(FixedType self,
                                      int precision,
                                      int scale,
                                      Fixed min,
                                      Fixed max) throws PositionedError;

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
   * Visits GrantUserClassStatement
   */
  // !!! coco 050201 : verify what it is an user class
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
                                           int userClass,
                                           String userName)
    throws PositionedError
  {
    current.append("GRANT ");
    switch(userClass) {
    case GrantUserClassStatement.TYP_ACCESS:
      current.append("ACCESS TO ");
      break;
    case GrantUserClassStatement.TYP_RESOURCE:
      current.append("RESOURCE TO ");
      break;
    case GrantUserClassStatement.TYP_DBA:
      current.append("DBA TO ");
      break;
    default:
      throw new InconsistencyException("Unexpected privilege");
    }
    current.append(userName);
  }

  /**
   * Visits ImageType
   */
  public abstract void visitImageType(ImageType self,
                                      int width,
                                      int height)
    throws PositionedError;

  /**
   * Visits IndexDefinition
   */
  public abstract void visitIndexDefinition(IndexDefinition self,
                                            boolean hasUnique,
                                            String indexName,
                                            Expression tableName,
                                            ArrayList indexElemList,
                                            int type) throws PositionedError;

  /**
   * Visits IndexElem
   */
  public void visitIndexElem(IndexElem self, String name, boolean isDesc)
    throws PositionedError
  {
    current.append(name);
    if (isDesc) {
      current.append(" DESC");
    }// else {
    //current.append(" ASC");
    //}
  }

  /**
   * Visits IntType
   */
  public abstract void visitIntType(IntType self,
                                    Integer min,
                                    Integer max)
    throws PositionedError;

  /**
   * Visits Key
   */
  public abstract void visitKey(Key self, List keyList) throws PositionedError;

  /**
   * Visits MonthType
   */
  public abstract void visitMonthType(MonthType self) throws PositionedError;

  /**
   * Visits NullDelimiterSpec
   */
  public void visitNullDelimiterSpec(NullDelimiterSpec self,
                                     NullSpec nullSpec,
                                     DelimSpec delimSpec)
    throws PositionedError
  {
    if (nullSpec != null) {
      nullSpec.accept(this);
    }
    if (delimSpec != null) {
      if (nullSpec != null) {
        current.append(" ");
      }
      delimSpec.accept(this);
    }
  }

  /**
   * Visits NullSpec
   */
  public void visitNullSpec(NullSpec self, String literal)
    throws PositionedError
  {
    current.append("NULL IS ");
    current.append(literal);
  }

  /**
   * Visits ReferencedTableAndColumns
   */
  public void visitReferencedTableAndColumns(ReferencedTableAndColumns self,
                                             Expression tableName,
                                             FieldNameList field)
    throws PositionedError
  {
    tableName.accept(this);
    current.append("(");
    field.accept(this);
    current.append(")");
  }

  /**
   * Visits ReferentialConstraintDefinition
   */
  public abstract void visitReferentialConstraintDefinition(ReferentialConstraintDefinition self,
                                                            String name,
                                                            FieldNameList field,
                                                            ReferencedTableAndColumns reference,
                                                            int type)
    throws PositionedError;

  /**
   * Visits ScriptHeadStatement
   */
  public void visitScriptHeadStatement(ScriptHeadStatement self)
    throws PositionedError
  {
  }

  /**
   * Visits ShortType
   */
  public void visitShortType(ShortType self, Integer min, Integer max)
    throws PositionedError
  {
    current.append("SMALLINT");
  }

  /**
   * Visits SpoolFileStatement
   */
  public void visitSpoolFileStatement(SpoolFileStatement self,
                                      String fileName,
                                      NullDelimiterSpec spec,
                                      SelectStatement select)
    throws PositionedError
  {
    current.append("SPOOL INTO");
    current.append(" ");
    current.append(fileName);
    current.append(" ");
    spec.accept(this);
    current.append(" ");
    select.accept(this);
  }

  /**
   * Visits SpoolTableStatement
   */
  public void visitSpoolTableStatement(SpoolTableStatement self,
                                       Expression tableName,
                                       boolean hasSorted,
                                       String fileName,
                                       boolean hasLocal,
                                       NullDelimiterSpec spec)
    throws PositionedError
  {
    current.append("SPOOL ");
    tableName.accept(this);
    current.append(" FROM ");
    if (hasSorted) {
      current.append("SORTED ");
    }
    current.append(fileName);
    if (hasLocal) {
      current.append(" LOCAL");
    }
    current.append(" ");
    spec.accept(this);
  }

  /**
   * Visits StringType
   */
  public abstract void visitStringType(StringType self,
                                       boolean fixed,
                                       int width,
                                       int height,
                                       int convert)
    throws PositionedError;


  /**
   * Visits SequenceDefinition
   */
  public void visitSequenceDefinition(SequenceDefinition self,
                                      Expression sequenceName,
                                      Integer startValue)
    throws PositionedError
  {
    current.append("CREATE SEQUENCE ");
    sequenceName.accept(this);
    if (startValue != null) {
      current.append(" START WITH ");
      current.append(startValue);
    }
  }

  /**
   * Visits TableDefinition
   */
  public abstract void visitTableDefinition(TableDefinition self,
                                            Expression tableName,
                                            ArrayList columns,
                                            Key key,
                                            Pragma pragma)
    throws PositionedError;

  /**
   * Visits TablePrivilege
   */
  public void visitTablePrivilege(TablePrivilege self,
                                  int privilege,
                                  FieldNameList fieldName)
    throws PositionedError
  {
    switch(privilege) {
    case TablePrivilege.TYP_ALL:
      current.append("ALL");
      break;
    case TablePrivilege.TYP_SELECT:
      current.append("SELECT");
      break;
    case TablePrivilege.TYP_INSERT:
      current.append("INSERT");
      break;
    case TablePrivilege.TYP_DELETE:
      current.append("DELETE");
      break;
    case TablePrivilege.TYP_UPDATE:
      current.append("UPDATE");
      if (fieldName != null) {
        current.append(" ");
        fieldName.accept(this);
      }
      break;
    default:
      throw new InconsistencyException("Unexpected privilege");
    }
  }

  /**
   * Visits TextType
   */
  public abstract void visitTextType(TextType self,
                                     int width,
                                     int height)
    throws PositionedError;

  /**
   * Visits TimeType
   */
  public abstract void visitTimeType(TimeType self) throws PositionedError;

  /**
   * Visits TimestampType
   */
  public abstract void visitTimestampType(TimestampType self) throws PositionedError;

  /**
   * Visits TypeName
   */
  public void visitTypeName(TypeName self, String ident)
    throws PositionedError
  {
    current.append(ident);
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  public abstract void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
                                                       int type,
                                                       FieldNameList field)
    throws PositionedError;

  /**
   * Visits ViewColumn
   */
  public void visitViewColumn(ViewColumn self,
                              String ident,
                              Type type,
                              boolean nullable,
                              JavaStyleComment[] comment)
    throws PositionedError
  {
    current.append(ident);
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
    current.append("CREATE ");
    current.append("VIEW ");
    tableName.accept(this);
    current.append(" (");
    for (int i = 0; i < columnNameList.size(); i++) {
      if (i != 0) {
        current.append(", ");
      }
      ((ViewColumn)columnNameList.get(i)).accept(this);
    }
    current.append(") AS (");
    reference.accept(this);
    current.append(")");
  }

  /**
   * Visits WeekType
   */
  public abstract void visitWeekType(WeekType self) throws PositionedError;
}
