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
import org.kopi.xkopi.comp.sqlc.*;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Fixed;

/**
 * This interface defines Dbi phylum visitors.
 */
public interface DbiVisitor extends SqlVisitor {

  /**
   * Visits JdbcDateLiteral
   * @param     value   the JDBC date
   */
  void visitJdbcDateLiteral(JdbcDateLiteral self, Date value)
    throws PositionedError;

  /**
   * Visits a AlterDropDefaultColumnStatement.
   */
  void visitAlterDropDefaultColumnStatement(AlterDropDefaultColumnStatement self,
                                            Expression tableName,
                                            String columnName)
    throws PositionedError;

  /**
   * Visits a AlterSetDefaultColumnStatement.
   */
  void visitAlterSetDefaultColumnStatement(AlterSetDefaultColumnStatement self,
                                           Expression tableName,
                                           String columnName,
                                           Expression defaultValue)
    throws PositionedError;

  /**
   * Visits a AlterDropDefaultColumnStatement.
   */
  void visitAlterNotNullColumnStatement(AlterNotNullColumnStatement self,
                                        Expression tableName,
                                        String columnName)
    throws PositionedError;

  /**
   * Visits a AddTableConstraintStatement.
   */
  void visitAddTableConstraintStatement(AddTableConstraintStatement self,
                                        Expression tableName,
                                        TableConstraint tableConstraint)
    throws PositionedError;

  /**
   * Visits a AddTableColumnsStatement.
   */
  void visitAddTableColumnsStatement(AddTableColumnsStatement self,
                                     Expression tableName,
                                     Column column)
    throws PositionedError;

  /**
   * Visits a BlobType.
   */
  void visitBlobType(BlobType self)
    throws PositionedError;

  /**
   * Visits a ClobType.
   */
  void visitClobType(ClobType self)
    throws PositionedError;

  /**
   * Visits a BooleanType.
   */
  void visitBooleanType(BooleanType self)
    throws PositionedError;

  /**
   * Visits a ByteType.
   */
  void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError;

  /**
   * Visits a CodeBoolType.
   */
  void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError;

  /**
   * Visits a CodeBoolDesc.
   */
  void visitCodeBoolDesc(CodeBoolDesc self, String name, Boolean value)
    throws PositionedError;

  /**
   * Visits a CodeLongDesc.
   */
  void visitCodeLongDesc(CodeLongDesc self, String name, Integer value)
    throws PositionedError;

  /**
   * Visits a CodeFixedDesc.
   */
  void visitCodeFixedDesc(CodeFixedDesc self, String name, Fixed value)
    throws PositionedError;

  /**
   * Visits a CodeFixedType.
   */
  void visitCodeFixedType(CodeFixedType self, int precision, int scale, ArrayList list)
    throws PositionedError;

  /**
   * Visits a CodeLongType.
   */
  void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError;

  /**
   * Visits a ColorType.
   */
  void visitColorType(ColorType self)
    throws PositionedError;

  /**
   * Visits a Column.
   */
  void visitColumn(Column self,
                   String ident,
                   Type type,
                   boolean nullable,
                   Expression defaultValue,
                   String constraintName,
                   JavaStyleComment[] comment)
    throws PositionedError;

  /**
   * Visits a DateType.
   */
  void visitDateType(DateType self)
    throws PositionedError;

  /**
   * Visits a DelimSpec.
   */
  void visitDelimSpec(DelimSpec self, String literal)
    throws PositionedError;

  /**
   * Visits a DropIndexStatement.
   */
  void visitDropIndexStatement(DropIndexStatement self, String indexName)
    throws PositionedError;

  /**
   * Visits DropSequenceStatement
   */
  void visitDropSequenceStatement(DropSequenceStatement self, Expression sequenceName)
    throws PositionedError;

  /**
   * Visits a DropTableConstraintStatement.
   */
  void visitDropTableConstraintStatement(DropTableConstraintStatement self,
                                         Expression tableName,
                                         String constraintName)
    throws PositionedError;

  /**
   * Visits a DropViewStatement.
   */
  void visitDropViewStatement(DropViewStatement self,
                              Expression tableName)
    throws PositionedError;

  /**
   * Visits a DropTableStatement.
   */
  void visitDropTableStatement(DropTableStatement self,
                               Expression tableName)
    throws PositionedError;

  /**
   * Visits a EnumType.
   */
  void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError;

  /**
   * Visits a FixedType.
   */
  void visitFixedType(FixedType self,
                      int precision,
                      int scale,
                      Fixed min,
                      Fixed max)
    throws PositionedError;

  /**
   * Visits a GrantPrivilegeStatement.
   */
  void visitGrantPrivilegeStatement(GrantPrivilegeStatement self,
                                    Expression tableName,
                                    ArrayList privileges,
                                    ArrayList userList,
                                    int type,
                                    boolean grantOption)
    throws PositionedError;

  /**
   * Visits a GrantUserClassStatement.
   */
  void visitGrantUserClassStatement(GrantUserClassStatement self,
                                    int userClass,
                                    String userName)
    throws PositionedError;

  /**
   * Visits a ImageType.
   */
  void visitImageType(ImageType self, int width, int height)
    throws PositionedError;

  /**
   * Visits a IndexDefinition.
   */
  void visitIndexDefinition(IndexDefinition self,
                            boolean hasUnique,
                            String indexName,
                            Expression tableName,
                            ArrayList indexElemList,
                            int type)
    throws PositionedError;

  /**
   * Visits a IndexElem.
   */
  void visitIndexElem(IndexElem self, String name, boolean isDesc)
    throws PositionedError;

  /**
   * Visits a IntType.
   */
  void visitIntType(IntType self, Integer min, Integer max)
    throws PositionedError;

  /**
   * Visits a Key.
   */
  void visitKey(Key self, List keyList)
    throws PositionedError;

  /**
   * Visits a MonthType.
   */
  void visitMonthType(MonthType self)
    throws PositionedError;

  /**
   * Visits a NullDelimiterSpec.
   */
  void visitNullDelimiterSpec(NullDelimiterSpec self,
                              NullSpec nullSpec,
                              DelimSpec delimSpec)
    throws PositionedError;

  /**
   * Visits a NullSpec.
   */
  void visitNullSpec(NullSpec self, String literal) throws PositionedError;

  /**
   * Visits a ReferencedTableAndColumns.
   */
  void visitReferencedTableAndColumns(ReferencedTableAndColumns self,
                                      Expression tableName,
                                      FieldNameList field)
    throws PositionedError;

  /**
   * Visits a ReferentialConstraintDefinition.
   */
  void visitReferentialConstraintDefinition(ReferentialConstraintDefinition self,
                                            String name,
                                            FieldNameList field,
                                            ReferencedTableAndColumns reference,
                                            int type)
    throws PositionedError;

  /**
   * Visits CompilationUnit (root of the syntax abstract tree)
   */
  void visitSCompilationUnit(SCompilationUnit self,
                             String packageName,
                             int version,
                             ArrayList elems)
    throws PositionedError;

  /**
   * Visits a ShortType.
   */
  void visitShortType(ShortType self, Integer min, Integer max)
    throws PositionedError;

  /**
   * Visits a SpoolFileStatement.
   */
  void visitSpoolFileStatement(SpoolFileStatement self,
                               String fileName,
                               NullDelimiterSpec spec,
                               SelectStatement select)
    throws PositionedError;

  /**
   * Visits a SpoolTableStatement.
   */
  void visitSpoolTableStatement(SpoolTableStatement self,
                                Expression tableName,
                                boolean hasSorted,
                                String fileName,
                                boolean hasLocal,
                                NullDelimiterSpec spec)
    throws PositionedError;

  /**
   * Visits a StringType.
   */
  void visitStringType(StringType self,
                       boolean fixed,
                       int width,
                       int height,
                       int convert)
    throws PositionedError;
  
  /**
   * Visits a Sequence.
   */
  void visitSequenceDefinition(SequenceDefinition self,
                               Expression sequenceName,
                               Integer startValue)
    throws PositionedError;

  /**
   * Visits a TableDefinition.
   */
  void visitTableDefinition(TableDefinition self,
                            Expression tableName,
                            ArrayList columns,
                            Key key,
                            Pragma pragma)
    throws PositionedError;

  /**
   * Visits a TablePrivilege.
   */
  void visitTablePrivilege(TablePrivilege self,
                           int privilege,
                           FieldNameList fieldName)
    throws PositionedError;

  /**
   * Visits a TextType.
   */
  void visitTextType(TextType self, int width, int height)
    throws PositionedError;

  /**
   * Visits a TimeType.
   */
  void visitTimeType(TimeType self) throws PositionedError;

  /**
   * Visits a TimestampType.
   */
  void visitTimestampType(TimestampType self) throws PositionedError;

  /**
   * Visits a UniqueConstraintDefinition.
   */
  void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
                                       int type,
                                       FieldNameList field)
    throws PositionedError;

  /**
   * Visits a ViewColumn.
   */
  void visitViewColumn(ViewColumn self,
                       String ident,
                       Type type,
                       boolean nullable,
                       JavaStyleComment[] comment)
    throws PositionedError;

  /**
   * Visits a ViewDefinition.
   */
  void visitViewDefinition(ViewDefinition self,
                           Expression tableName,
                           ArrayList columnNameList,
                           TableReference reference)
    throws PositionedError;

  /**
   * Visits a WeekType.
   */
  void visitWeekType(WeekType self) throws PositionedError;
}
