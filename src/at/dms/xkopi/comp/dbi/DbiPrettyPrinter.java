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
 * $Id$
 */

package at.dms.xkopi.comp.dbi;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.PositionedError;
import at.dms.xkopi.comp.sqlc.Expression;
import at.dms.xkopi.comp.sqlc.FieldNameList;
import at.dms.xkopi.comp.sqlc.IntegerLiteral;
import at.dms.xkopi.comp.sqlc.SelectStatement;
import at.dms.xkopi.comp.sqlc.SqlcPrettyPrinter;
import at.dms.xkopi.comp.sqlc.Statement;
import at.dms.xkopi.comp.sqlc.TableReference;
import at.dms.xkopi.comp.sqlc.Type;
import at.dms.util.base.InconsistencyException;
import at.dms.xkopi.lib.type.Fixed;

/**
 * This class implements a Java pretty printer
 */
public class DbiPrettyPrinter extends SqlcPrettyPrinter implements DbiVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public DbiPrettyPrinter(String fileName) throws IOException {
    super(fileName, 20);
  }

  // ----------------------------------------------------------------------
  // STATEMENT
  // ----------------------------------------------------------------------

  /**
   * Visits SequenceDefinition
   */
  public void visitSequenceDefinition(SequenceDefinition self,
                                      Expression sequenceName,
                                      Integer startValue)
    throws PositionedError
  {
    print("CREATE SEQUENCE ");
    sequenceName.accept(this);
    if (startValue != null) {
      print(" START WITH ");
      print(startValue);
    }
  }

  /**
   * prints a table definition
   */
  public void visitTableDefinition(TableDefinition self,
				   Expression tableName,
				   ArrayList columns,
				   Key key)
    throws PositionedError
  {
    print("CREATE TABLE ");
    tableName.accept(this);
    println();
    println("(");
    for (int i = 0; i < columns.size(); i++) {
      if (i != 0) {
	println(", ");
      }
      ((Column)columns.get(i)).accept(this);
    }
    println();
    print(")");
    if (key != null) {
      println();
      key.accept(this);
    }
  }

  /**
   * prints a key constraint
   */
  public void visitKey(Key self, List keyList) {
    print("KEY IS ");
    for (int i = 0; i < keyList.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      print(keyList.get(i));
    }
  }

  /**
   * prints a column
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
    pos += TAB_SIZE;

    // print the comment
    if (comment != null) {
      if (comment.length > 1) {
	println();
      }
      for (int j = 0; j < comment.length; j++) {
	printComment(comment[j]);
      }
      if (comment.length > 1) {
	println();
      }
    }

    print(ident);
    pos += TAB_SIZE;
    type.accept(this);
    if (!nullable) {
      print(" ");
      pos += TAB_SIZE;
      print("NOT NULL");
      pos -= TAB_SIZE;
    }
    if (defaultValue != null) {
      print(" DEFAULT ");
      defaultValue.accept(this);
    }
    if (constraintName != null) {
      print(" ");
      pos += TAB_SIZE;
      print("CONSTRAINT ");
      print(constraintName);
      pos -= TAB_SIZE;
    }
    pos -= TAB_SIZE;
    pos -= TAB_SIZE;
  }

  /**
   * prints a view column
   */
  public void visitViewColumn(ViewColumn self,
			      String ident,
			      Type type,
			      boolean nullable,
			      JavaStyleComment[] comment)
    throws PositionedError
  {
    pos += TAB_SIZE;

    // print the comment
    if (comment != null) {
      if (comment.length > 1) {
	println();
      }
      for (int j = 0; j < comment.length; j++) {
	printComment(comment[j]);
      }
      if (comment.length > 1) {
	println();
      }
    }

    print(ident);
    pos += TAB_SIZE;
    type.accept(this);
    if (!nullable) {
      print(" ");
      pos += TAB_SIZE;
      print("NOT NULL");
      pos -= TAB_SIZE;
    }
    pos -= TAB_SIZE;
    pos -= TAB_SIZE;
  }


  /**
   * Visits DropViewStatement
   */
  public void visitDropViewStatement(DropViewStatement self, Expression viewName)
    throws PositionedError
  {
    print("DROP VIEW ");
    viewName.accept(this);
  }

  /**
   * Visits DropSequenceStatement
   */
  public void visitDropSequenceStatement(DropSequenceStatement self, Expression sequenceName)
    throws PositionedError
  {
    print("DROP SEQUENCE ");
    sequenceName.accept(this);
  }

 /**
   * prints a drop table statement
   */
  public void visitDropTableStatement(DropTableStatement self, Expression tableName)
    throws PositionedError
  {
    print("DROP TABLE ");
    tableName.accept(this);
  }

  /**
   * prints a drop index statement
   */
  public void visitDropIndexStatement(DropIndexStatement self, String indexName)
    throws PositionedError
  {
    print("DROP INDEX ");
    print(indexName);
  }

  /**
   * prints a drop index element
   */
  public void visitIndexElem(IndexElem self, String name, boolean isDesc)
    throws PositionedError
  {
    print(name);
    if (isDesc) {
      print(" DESC");
    } //else {
    //print(" ASC");
    //}
  }

  /**
   * prints a drop index definition
   */
  public void visitIndexDefinition(IndexDefinition self,
				   boolean hasUnique,
				   String indexName,
				   Expression tableName,
				   ArrayList indexElemList,
				   int type)
    throws PositionedError
  {

    print("CREATE ");
    if (hasUnique) {
      print("UNIQUE ");
    }
    print("INDEX ");
    print(indexName);
    print(" ON ");
    tableName.accept(this);
    print(" (");
    for (int i = 0; i < indexElemList.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((IndexElem)indexElemList.get(i)).accept(this);
    }
    print(")");
    switch(type) {
    case IndexDefinition.TYP_NONE:
      break;
    case IndexDefinition.TYP_PRIMARY:
      print(" WITH PRIMARY");
      break;
    case IndexDefinition.TYP_DISALLOW:
      print(" WITH DISALLOW NULL");
      break;
    case IndexDefinition.TYP_IGNORE:
      print(" WITH IGNORE NULL");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
  }

  /**
   * prints a ReferencedTableAndColumns
   */
  public void visitReferencedTableAndColumns(ReferencedTableAndColumns self,
					     Expression tableName,
					     FieldNameList field)
    throws PositionedError
  {
    print(tableName);
    print(" (");
    field.accept(this);
    print(")");
  }

  /**
   * prints a ReferentialConstraintDefinition
   */
  public void visitReferentialConstraintDefinition(ReferentialConstraintDefinition self,
						   String name,
						   FieldNameList field,
						   ReferencedTableAndColumns reference,
						   int type)
    throws PositionedError
  {
    print(name);
    print(" FOREIGN KEY (");
    field.accept(this);
    print(") REFERENCES ");
    reference.accept(this);
    switch(type) {
    case ReferentialConstraintDefinition.TYP_RESTRICT:
      print(" ON DELETE RESTRICT");
      break;
    case ReferentialConstraintDefinition.TYP_CASCADE:
      print(" ON DELETE CASCADE");
      break;
    case ReferentialConstraintDefinition.TYP_NULL:
      print(" ON DELETE SET NULL");
      break;
    case ReferentialConstraintDefinition.TYP_DEFAULT:
      print(" ON DELETE SET DEFAULT");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
  }

  /**
   * prints a UniqueConstraintDefinition
   */
  public void visitUniqueConstraintDefinition(UniqueConstraintDefinition self,
					      int type,
					      FieldNameList field)
    throws PositionedError
  {
    switch(type) {
    case UniqueConstraintDefinition.TYP_UNIQUE:
      print("UNIQUE ");
      break;
    case UniqueConstraintDefinition.TYP_PRIMARY:
      print("PRIMARY KEY ");
      break;
    default:
      throw new InconsistencyException("Unexpected type");
    }
    field.accept(this);
    print(")");
  }

  /**
   * prints a drop constraint statement
   */
  public void visitDropTableConstraintStatement(DropTableConstraintStatement self,
						Expression tableName,
						String constraintName)
    throws PositionedError
  {
    print("ALTER TABLE ");
    tableName.accept(this);
    print(" DROP CONSTRAINT ");
    print(constraintName);
  }

  /**
   * prints a add constraint statement
   */
  public void visitAddTableConstraintStatement(AddTableConstraintStatement self,
					       Expression tableName,
					       TableConstraint tableConstraint)
    throws PositionedError
  {
    print("ALTER TABLE ");
    tableName.accept(this);
    print("ADD CONSTRAINT ");
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
    print("ALTER TABLE ");
    tableName.accept(this);
    print("ADD COLUMN ");
    column.accept(this);
  }

  /**
   * Visits AlterDropDeafultColumnStatement
   */
  public void visitAlterDropDefaultColumnStatement(AlterDropDefaultColumnStatement self,
						   Expression tableName,
						   String columnName)
    throws PositionedError
  {
    print("ALTER TABLE ");
    tableName.accept(this);
    print(" ALTER ");
    print(columnName);
    print(" DROP DEFAULT");
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
    print("ALTER TABLE ");
    tableName.accept(this);
    print(" ALTER ");
    print(columnName);
    print(" SET DEFAULT ");
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
    print("ALTER TABLE ");
    tableName.accept(this);
    print(" ALTER ");
    print(columnName);
    print(" NOT NULL ");
  }


  /**
   * prints a view definition
   */
  public void visitViewDefinition(ViewDefinition self,
				  Expression tableName,
				  ArrayList columnNameList,
				  TableReference reference)
    throws PositionedError
  {
    print("CREATE ");
    print("VIEW ");
    tableName.accept(this);
    println();
    println("(");
    if (columnNameList != null) {
      for (int i = 0; i < columnNameList.size(); i++) {
	if (i != 0) {
	  println(", ");
	}
	((ViewColumn)columnNameList.get(i)).accept(this);
      }
    }
    println();
    println(")");
    println("AS");
    println("(");
    pos += TAB_SIZE;
    if (reference != null) {
      reference.accept(this);
    }
    pos -= TAB_SIZE;
    print(")");
  }

  /**
   * prints a grant statement
   */
  public void visitGrantPrivilegeStatement(GrantPrivilegeStatement self,
					   Expression tableName,
					   ArrayList privileges,
					   ArrayList userList,
					   boolean grantOption)
    throws PositionedError
  {
    print("GRANT ");
    for (int i = 0; i < privileges.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      ((TablePrivilege)privileges.get(i)).accept(this);
    }
    print(" ON ");
    tableName.accept(this);
    print(" TO ");
    for (int i = 0; i < userList.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      print(userList.get(i));
    }
  }

  /**
   * prints a table privilege
   */
  public void visitTablePrivilege(TablePrivilege self,
				  int privilege,
				  FieldNameList fieldName)
    throws PositionedError
  {
    switch(privilege) {
    case TablePrivilege.TYP_ALL:
      print("ALL");
      break;
    case TablePrivilege.TYP_SELECT:
      print("SELECT");
      break;
    case TablePrivilege.TYP_INSERT:
      print("INSERT");
      break;
    case TablePrivilege.TYP_DELETE:
      print("DELETE");
      break;
    case TablePrivilege.TYP_UPDATE:
      print("UPDATE");
      if (fieldName != null) {
	print(" ");
	fieldName.accept(this);
      }
      break;
    default:
      throw new InconsistencyException("Unexpected privilege");
    }
  }

  /**
   * prints a table privilege
   */
  public void visitGrantUserClassStatement(GrantUserClassStatement self,
					   int userClass,
					   String userName)
    throws PositionedError
  {
    print("GRANT ");
    switch(userClass) {
    case GrantUserClassStatement.TYP_ACCESS:
      print("ACCESS ");
      break;
    case GrantUserClassStatement.TYP_RESOURCE:
      print("RESOURCE ");
      break;
    case GrantUserClassStatement.TYP_DBA:
      print("DBA ");
      break;
    default:
      throw new InconsistencyException("Unexpected privilege");
    }
    print(userName);
  }

  /**
   * prints a null spec
   */
  public void visitNullSpec(NullSpec self, String literal)
    throws PositionedError
  {
    print("NULL IS ");
    print(literal);
  }

  /**
   * prints a delim spec
   */
  public void visitDelimSpec(DelimSpec self, String literal)
    throws PositionedError
  {
    print("DELIM IS ");
    print(literal);
  }

  /**
   * prints a null delimiter spec
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
	print(" ");
      }
      delimSpec.accept(this);
    }
  }

  /**
   * prints a spool file statement
   */
  public void visitSpoolFileStatement(SpoolFileStatement self,
				      String fileName,
				      NullDelimiterSpec spec,
				      SelectStatement select)
    throws PositionedError
  {
    print("SPOOL INTO ");
    print(fileName);
    print(" ");
    spec.accept(this);
    print(" ");
    select.accept(this);
  }

  /**
   * prints a spool tabel statement
   */
  public void visitSpoolTableStatement(SpoolTableStatement self,
				       Expression tableName,
				       boolean hasSorted,
				       String fileName,
				       boolean hasLocal,
				       NullDelimiterSpec spec)
    throws PositionedError
  {
    print("SPOOL ");
    tableName.accept(this);
    print(" FROM ");
    if (hasSorted) {
      print("SORTED ");
    }
    print(fileName);
    if (hasLocal) {
      print(" LOCAL");
    }
    print(" ");
    spec.accept(this);
  }

  /**
   * prints a boolean type
   */
  public void visitBooleanType(BooleanType self)
    throws PositionedError
  {
    print("BOOL");
  }

  /**
   * prints a color type
   */
  public void visitColorType(ColorType self)
    throws PositionedError
  {
    print("COLOR");
  }

  /**
   * prints a month type
   */
  public void visitMonthType(MonthType self)
    throws PositionedError
  {
    print("MONTH");
  }

  /**
   * prints a time type
   */
  public void visitTimeType(TimeType self)
    throws PositionedError
  {
    print("TIME");
  }

  /**
   * prints a time stamp type
   */
  public void visitTimestampType(TimestampType self)
    throws PositionedError
  {
    print("TIMESTAMP");
  }

  /**
   * prints a int type
   */
  public void visitIntType(IntType self,
			   Integer min,
			   Integer max)
    throws PositionedError
  {
    print("INT");
    if (min != null) {
      print(" MIN " + min.intValue());
    }
    if (max != null) {
      print(" MAX " + max.intValue());
    }
  }

  /**
   * Visits CompilationUnit (root of the syntax abstract tree)
   */
  public void visitSCompilationUnit(SCompilationUnit self,
				    String packageName,
				    int version,
				    ArrayList elems)
    throws PositionedError
  {
    printCUnit(elems);
  }


  /**
   * prints a short type
   */
  public void visitShortType(ShortType self, Integer min, Integer max) {
    print("SHORT");
    if (min != null) {
      print(" MIN " + min.intValue());
    }
    if (max != null) {
      print(" MAX " + max.intValue());
    }
  }

  /**
   * prints a byte type
   */
  public void visitByteType(ByteType self, Integer min, Integer max)
    throws PositionedError
  {
    print("BYTE");
    if (min != null) {
      print(" MIN " + min.intValue());
    }
    if (max != null) {
      print(" MAX " + max.intValue());
    }
  }

  /**
   * prints a image type
   */
  public void visitImageType(ImageType self, int width, int height)
    throws PositionedError
  {
    print("IMAGE(");
    print("" + width);
    print(", " + height);
    print(")");
  }

  /**
   * prints a blob type
   */
  public void visitBlobType(BlobType self)
    throws PositionedError
  {
    print("BLOB");
  }

  /**
   * prints a clob type
   */
  public void visitClobType(ClobType self)
    throws PositionedError
  {
    print("CLOB");
  }

  /**
   * prints a string field type
   */
  public void visitStringType(StringType self,
			      boolean fixed,
			      int width,
			      int height,
			      int convert)
    throws PositionedError
  {
    if (fixed) {
      print("CHAR");
      if (width != 1) {
	print("(");
	print("" + width);
	if (height != 1) {
	  print(", " + height);
	}
	print(")");
      }
    } else {
      print("STRING");
      if (width != -1) {
	print("(");
	print("" + width);
	if (height != 1) {
	  print(", " + height);
	}
 	print(")");
      }
    }
    switch(convert) {
    case StringType.TYP_NONE:
      break;
    case StringType.TYP_UPPER:
      print(" CONVERT UPPER");
      break;
    case StringType.TYP_LOWER:
      print(" CONVERT LOWER");
      break;
    case StringType.TYP_NAME:
      print(" CONVERT NAME");
      break;
    default:
      throw new InconsistencyException("Unexpected String Type");
    }
  }

  /**
   * prints a text type
   */
  public void visitTextType(TextType self, int width, int height)
    throws PositionedError
  {
    print("STRING(");
    print("" + width);
    print(", " + height);
    print(")");
  }

  /**
   * prints a fixed type
   */
  public void visitFixedType(FixedType self,
			     int precision,
			     int scale,
			     Fixed min,
			     Fixed max)
    throws PositionedError
  {
    print("FIXED(");
    print("" + precision);
    print(", " + scale);
    print(")");
    if (min != null) {
      print(" MINVAL " + min);
    }
    if (max != null) {
      print(" MAXVAL " + max);
    }
  }

  /**
   * prints a enum type
   */
  public void visitEnumType(EnumType self, ArrayList list)
    throws PositionedError
  {
    print("ENUM(");
    for (int i = 0; i < list.size(); i++) {
      if (i != 0) {
	print(", ");
      }
      print(list.get(i));
    }
    print(")");
  }

  /**
   * prints a date type
   */
  public void visitDateType(DateType self)
    throws PositionedError
  {
    print("DATE");
  }


  /**
   * prints a  Code Desc Fixed
   */
  public void visitCodeFixedDesc(CodeFixedDesc self, String name, Fixed value)
    throws PositionedError
  {
    print('"' + name + '"');
    print("= ");
    print(value.toString());
  }

  /**
   * prints a Code Desc Long
   */
  public void visitCodeLongDesc(CodeLongDesc self, String name, Integer value)
    throws PositionedError
  {
    print('"' + name + '"');
    print("= ");
    print(value.toString());
  }

  /**
   * prints a  Code Desc Bool
   */
  public void visitCodeBoolDesc(CodeBoolDesc self, String name, Boolean value)
    throws PositionedError
  {
    print('"' + name + '"');
    print("= ");
    print(((Boolean)value).booleanValue() ? "TRUE" : "FALSE");
  }

  /**
   * prints a CODE BOOL type
   */
  public void visitCodeBoolType(CodeBoolType self, ArrayList list)
    throws PositionedError
  {
    print("CODE ");
    print("BOOL");
  }

  /**
   * prints a CODE FIXED type
   */
  public void visitCodeFixedType(CodeFixedType self, int precision, int scale, ArrayList list)
    throws PositionedError
  {
    print("CODE ");
    print("FIXED (" + precision + ", " + scale + ")");
  }

  /**
   * prints a CODE LONG type
   */
  public void visitCodeLongType(CodeLongType self, ArrayList list)
    throws PositionedError
  {
    print("CODE ");
    print("LONG");
  }

  /**
   * prints a week type
   */
  public void visitWeekType(WeekType self)
    throws PositionedError
  {
    print("WEEK");
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  public void printComment(JavaStyleComment comment) {
    StringTokenizer	tok = new StringTokenizer(comment.getText(), "\n");

    if (tok.countTokens() == 1) {
      print("// " + tok.nextToken().trim());
      println();
    } else if (tok.countTokens() <= 3) {
      println();
      while (tok.hasMoreTokens()) {
	print("//");
	String  comm = tok.nextToken().trim();
	if (comm.startsWith("*")) {
	  comm = comm.substring(1).trim();
	}
	if (comm.length() == 0) {
	  continue;
	}
	print(" " + comm);
	println();
      }
    } else {
      if (line > 0) {
	println();
      }
      print("/*");
      while (tok.hasMoreTokens()) {
	String comm = tok.nextToken().trim();
	if (comm.startsWith("*")) {
	  comm = comm.substring(1).trim();
	}
	if (comm.length() == 0) {
	  continue;
	}
	println();
	print(" * " + comm);
      }
      println();
      print(" */");
      println();
    }
  }
}
