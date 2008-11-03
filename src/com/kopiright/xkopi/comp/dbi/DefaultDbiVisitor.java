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
import com.kopiright.xkopi.comp.sqlc.DefaultSqlVisitor;
import com.kopiright.xkopi.comp.sqlc.Expression;
import com.kopiright.xkopi.comp.sqlc.FieldNameList;
import com.kopiright.xkopi.comp.sqlc.SelectStatement;
import com.kopiright.xkopi.comp.sqlc.Statement;
import com.kopiright.xkopi.comp.sqlc.TableReference;
import com.kopiright.xkopi.comp.sqlc.Type;
import com.kopiright.xkopi.comp.sqlc.TypeName;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 *
 */
public class DefaultDbiVisitor extends DefaultSqlVisitor implements DbiVisitor {

  /**
   * Visits AlterColumnStatement
   */
  public void visitAlterDropDefaultColumnStatement(AlterDropDefaultColumnStatement self,
                                                   Expression tableName,
                                                   String columnName)
    throws PositionedError
  {
    tableName.accept(this);
  }

  /**
   * Visits AlterColumnStatement
   */
  public void visitAlterSetDefaultColumnStatement(AlterSetDefaultColumnStatement self,
                                                  Expression tableName,
                                                  String columnName,
                                                  Expression defaultValue)
    throws PositionedError
  {
    tableName.accept(this);
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
    tableName.accept(this);
  }

  /**
   * Visits AddTableConstraintStatement
   */
  public void visitAddTableConstraintStatement(AddTableConstraintStatement self,
                                               Expression tableName,
                                               TableConstraint tableConstraint)
    throws PositionedError
  {
    tableName.accept(this);
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
    tableName.accept(this);
    column.accept(this);
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
  }

  /**
   * Visits BooleanType
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
  }

  /**
   * Visits ByteType
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
  }

  /**
   * Visits CodeBoolType
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
  }

  /**
   * Visits CodeFixedDesc
   */
  public void visitCodeFixedDesc(CodeFixedDesc self, String name, Fixed value)
    throws PositionedError
  {
  }

  /**
   * Visits CodeLongDesc
   */
  public void visitCodeLongDesc(CodeLongDesc self, String name, Integer value)
    throws PositionedError
  {
  }

  /**
   * Visits CodeBoolDesc
   */
  public void visitCodeBoolDesc(CodeBoolDesc self, String name, Boolean value)
    throws PositionedError
  {
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
    type.accept(this);
  }

  /**
   * Visits DateType
   */
  public void visitDateType(DateType self)
    throws PositionedError
  {
  }

  /**
   * Visits DelimSpec
   */
  public void visitDelimSpec(DelimSpec self, String literal)
    throws PositionedError
  {
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
    tableName.accept(this);
  }

  /**
   * Visits DropViewStatement
   */
  public void visitDropViewStatement(DropViewStatement self, Expression viewName)
    throws PositionedError
  {
    viewName.accept(this);
  }

  /**
   * Visits DropSequenceStatement
   */
  public void visitDropSequenceStatement(DropSequenceStatement self,
                                         Expression sequenceName)
    throws PositionedError
  {
    sequenceName.accept(this);
  }

  /**
   * Visits DropTableStatement
   */
  public void visitDropTableStatement(DropTableStatement self, Expression tableName)
    throws PositionedError
  {
    tableName.accept(this);
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
    for (int i = 0; i < privileges.size(); i++) {
      ((TablePrivilege)privileges.get(i)).accept(this);
    }
    tableName.accept(this);
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
    tableName.accept(this);
    for (int i = 0; i < indexElemList.size(); i++) {
      ((IndexElem)indexElemList.get(i)).accept(this);
    }
  }

  /**
   * Visits IndexElem
   */
  public void visitIndexElem(IndexElem self, String name, boolean isDesc)
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
      delimSpec.accept(this);
    }
  }

  /**
   * Visits NullSpec
   */
  public void visitNullSpec(NullSpec self, String literal)
    throws PositionedError
  {
  }

  /**
   * Visits ReferencedTableAndColumns
   */
  public void visitReferencedTableAndColumns(ReferencedTableAndColumns self,
                                             Expression tableName,
                                             FieldNameList field)
    throws PositionedError
  {
    field.accept(this);
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
    field.accept(this);
    reference.accept(this);
  }

  /**
   * Visits ScriptHeadStatement
   */
  public void visitScriptHeadStatement(ScriptHeadStatement self)
    throws PositionedError
  {
  }

  /**
   * Visits CompilationUnit (root of the syntax abstract tree)
   */
  public void visitSCompilationUnit(SCompilationUnit self,
                                    String packageName,
                                    int version,
                                    ArrayList elems)
    throws PositionedError {
    for(int i = 0; i < elems.size(); i++) {
      ((Statement)elems.get(i)).accept(this);
      // cast based on the addStmt method in SCompilationUnit
    }
  }

  /**
   * Visits ShortType
   */
  public void visitShortType(ShortType self, Integer min, Integer max)
    throws PositionedError
  {
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
    spec.accept(this);
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
    tableName.accept(this);
    spec.accept(this);
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
   * Visits SequenceDefinition
   */
  public void visitSequenceDefinition(SequenceDefinition self,
                                      Expression sequenceName,
                                      Integer startValue)
    throws PositionedError
  {
    sequenceName.accept(this);
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
    tableName.accept(this);
    for (int i = 0; i < columns.size(); i++) {
      ((Column)columns.get(i)).accept(this);
    }
    if (key != null) {
      key.accept(this);
    }
  }

  /**
   * Visits TablePrivilege
   */
  public void visitTablePrivilege(TablePrivilege self,
                                  int privilege,
                                  FieldNameList fieldName)
    throws PositionedError
  {
    switch (privilege) {
    case TablePrivilege.TYP_ALL:
      break;
    case TablePrivilege.TYP_SELECT:
      break;
    case TablePrivilege.TYP_INSERT:
      break;
    case TablePrivilege.TYP_DELETE:
      break;
    case TablePrivilege.TYP_UPDATE:
      if (fieldName != null) {
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
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
  }

  /**
   * Visits TimeType
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
  }

  /**
   * Visits TimestampType
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
  }

  /**
   * Visits TypeName
   */
  public void visitTypeName(TypeName self, String ident)
    throws PositionedError
  {
  }

  /**
   * Visits UniqueConstraintDefinition
   */
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
                                              int type,
                                              FieldNameList field)
    throws PositionedError
  {
    field.accept(this);
  }

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
    tableName.accept(this);
    for (int i = 0; i < columnNameList.size(); i++) {
      ((ViewColumn)columnNameList.get(i)).accept(this);
    }
    reference.accept(this);
  }

  /**
   * Visits WeekType
   */
  public void visitWeekType(WeekType self)
    throws PositionedError
  {
  }
}
