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

package org.kopi.xkopi.comp.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import org.kopi.bytecode.classfile.ClassFileFormatException;
import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.BytecodeOptimizer;
import org.kopi.kopi.comp.kjc.CBinaryTypeContext;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CSourceClass;
import org.kopi.kopi.comp.kjc.CTypeVariable;
import org.kopi.kopi.comp.kjc.JBlock;
import org.kopi.kopi.comp.kjc.JClassBlock;
import org.kopi.kopi.comp.kjc.JClassDeclaration;
import org.kopi.kopi.comp.kjc.JClassFieldDeclarator;
import org.kopi.kopi.comp.kjc.JClassImport;
import org.kopi.kopi.comp.kjc.JCompilationUnit;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JExpressionStatement;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JMethodDeclaration;
import org.kopi.kopi.comp.kjc.JPackageImport;
import org.kopi.kopi.comp.kjc.JPackageName;
import org.kopi.kopi.comp.kjc.JStatement;
import org.kopi.kopi.comp.kjc.JTypeDeclaration;
import org.kopi.kopi.comp.kjc.JVariableDefinition;
import org.kopi.kopi.comp.kjc.KjcClassReader;
import org.kopi.kopi.comp.kjc.KjcEnvironment;
import org.kopi.kopi.comp.kjc.KjcOptions;
import org.kopi.kopi.comp.kjc.KjcSignatureParser;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.xkopi.comp.database.DatabaseColumn;
import org.kopi.xkopi.comp.database.DatabaseMember;
import org.kopi.xkopi.comp.database.DatabaseTable;
import org.kopi.xkopi.comp.dbi.Column;
import org.kopi.xkopi.comp.dbi.DbiType;
import org.kopi.xkopi.comp.dbi.IntType;
import org.kopi.xkopi.comp.dbi.SCompilationUnit;
import org.kopi.xkopi.comp.dbi.SequenceDefinition;
import org.kopi.xkopi.comp.dbi.TableDefinition;
import org.kopi.xkopi.comp.dbi.ViewColumn;
import org.kopi.xkopi.comp.dbi.ViewDefinition;
import org.kopi.xkopi.comp.sqlc.Constants;
import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;
import org.kopi.xkopi.comp.sqlc.Statement;
import org.kopi.xkopi.comp.sqlc.Type;
import org.kopi.xkopi.comp.xkjc.XKjcTypeFactory;

/**
 * This class generates an interface of the database in a .class file
 */
public class DBInterface implements org.kopi.kopi.comp.kjc.Constants {

  public static void generateDatabase(Compiler compiler,
                                      SCompilationUnit[] cunit,
                                      String packageName,
                                      String destination,
                                      String className,
                                      boolean toUpperCase)
  {
    final KjcClassReader        reader =
      new KjcClassReader(null, // working directory (uses system property user.dir
                         System.getProperty("java.class.path"),
                         System.getProperty("java.ext.dirs"),
                         new KjcSignatureParser());
    final KjcEnvironment        env = new KjcEnvironment(reader,
                                                         new XKjcTypeFactory(compiler, reader, false),
                                                         new KjcOptions());
    init(compiler, env);

    final String                filename = className + ".k";
    JFieldDeclaration[]         fields = getFieldsDeclarations(cunit, toUpperCase);
    JClassDeclaration           clazz;
    JStatement[]                initializers;
    ArrayList                   initializerList = new ArrayList();
    int                         lengthDone = 0;
    JMethodDeclaration[]        methods;
    ArrayList                   methodList = new ArrayList();

    while (lengthDone < fields.length) {
      for (int i = 0; i < 1000 && lengthDone < fields.length; i++, lengthDone++) {
        initializerList.add(new JClassFieldDeclarator(TokenReference.NO_REF, fields[lengthDone]));
      }
        // put all statements into a static method, and call
        // this static method instead of all intializers -> less byte code instructions
        JMethodDeclaration      method;
        JStatement[]            body;
        String                  name;

        name = "staticInit$"+methodList.size();
        body = (JStatement[]) initializerList.toArray(new JStatement[initializerList.size()]);
        method = new JMethodDeclaration(TokenReference.build(filename, new File(filename), 0),
                                        ACC_PRIVATE | ACC_STATIC,
                                        CTypeVariable.EMPTY,
                                        env.getTypeFactory().getVoidType(),
                                        name,
                                        JFormalParameter.EMPTY,
                                        CReferenceType.EMPTY,
                                        new JBlock(TokenReference.NO_REF,
                                                   body,
                                                   null),
                                        null,
                                        null);
        methodList.add(method);
        // remove all initializer (work is done in the method)
        initializerList.clear();
        // add static method with initializer
        initializerList.add(new JExpressionStatement(TokenReference.NO_REF,
                                                     new JMethodCallExpression(TokenReference.NO_REF,
                                                                               null, //prefix
                                                                               name,
                                                                               JExpression.EMPTY),
                                                     null));
    }

    methods = (JMethodDeclaration[]) methodList.toArray(new JMethodDeclaration[methodList.size()]);
    initializers = (JStatement[]) initializerList.toArray(new JStatement[initializerList.size()]);
    initializers = new JStatement[] { new JClassBlock(TokenReference.NO_REF, true, initializers, null)};
    clazz =  new JClassDeclaration(TokenReference.build(filename, new File(filename), 0),
                                   ACC_PUBLIC,
                                   className,
                                   CTypeVariable.EMPTY,
                                   env.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT),
                                   CReferenceType.EMPTY,
                                   fields,
                                   methods,
                                   new JTypeDeclaration[]{},
                                   initializers,
                                   null,
                                   null);
    clazz.generateInterface(env.getClassReader(), null, packageName.replace('.', '/') + '/');


    JCompilationUnit compilUnit = new JCompilationUnit(TokenReference.NO_REF,
                                                       env,
                                                       new JPackageName(TokenReference.build(filename, new File(filename), 0),
                                                                        packageName,
                                                                        null),
                                                       new JPackageImport[]{},
                                                       new JClassImport[]{},
                                                       new JTypeDeclaration[] {clazz});


    try {
      Vector    classes = new Vector();

      compilUnit.join(compiler);
      compilUnit.checkInterface(compiler);
      compilUnit.prepareInitializers(compiler, classes);
      compilUnit.checkInitializers(compiler, classes);
      compilUnit.checkBody(compiler, classes);
      ((CSourceClass)classes.elementAt(0)).genCode(new BytecodeOptimizer(0),
                                                   destination,
                                                   env.getTypeFactory());
    } catch (PositionedError e) {
      e.printStackTrace();
      //!!! graf 20070210: no error reported! why?
      compiler.reportTrouble(e);
    } catch (java.io.IOException e) {
      e.printStackTrace();
    } catch (ClassFileFormatException e) {
      e.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  private static void init(Compiler compiler, KjcEnvironment env) {
    org.kopi.kopi.comp.kjc.CStdType.init(compiler, env);
    org.kopi.xkopi.comp.xkjc.XStdType.init(compiler, new CBinaryTypeContext(env.getClassReader(),
                                                               env.getTypeFactory()));
  }

  private static JFieldDeclaration genTableFieldDeclaration(String name, String[] names) {
    return new JFieldDeclaration(TokenReference.NO_REF,
                                 new JVariableDefinition(TokenReference.NO_REF,
                                                         ACC_PUBLIC + ACC_STATIC,
                                                         DatabaseMember.TYPE,
                                                         name,
                                                         DatabaseTable.getCreationExpression(name, names)),
                                 null,
                                 null);
  }

  private static JFieldDeclaration genColumnFieldDeclaration(String name,
                                                             Type type,
                                                             boolean isNullable)
  {
    JExpression         initializer;

    if (type instanceof DbiType) {
      initializer = ((DbiType)type).getColumnInfo(isNullable).getCreationExpression();
    } else {
      initializer = null;
    }

    return new JFieldDeclaration(TokenReference.NO_REF,
                                 new JVariableDefinition(TokenReference.NO_REF,
                                                         ACC_PUBLIC + ACC_STATIC,
                                                         DatabaseColumn.TYPE,
                                                         name,
                                                         initializer),
                                 null,
                                 null);
  }

  private static JFieldDeclaration[] getFieldsDeclarations(SCompilationUnit[] cunit,
                                                           boolean toUpperCase)
  {
    ArrayList   fieldDeclarations = new ArrayList();

    for (int c = 0; c < cunit.length; c++) {
      addFieldDeclarations(fieldDeclarations, cunit[c], toUpperCase);
    }

    return (JFieldDeclaration[])fieldDeclarations.toArray(new JFieldDeclaration[fieldDeclarations.size()]);
  }

  private static void addFieldDeclarations(ArrayList fieldDeclarations,
                                           SCompilationUnit cunit,
                                           boolean toUpperCase)
  {
    ListIterator      iterator = cunit.getElems().listIterator();

    while (iterator.hasNext()) {
      Statement stmt = (Statement)iterator.next();

      if (stmt instanceof TableDefinition) {
        TableDefinition def = (TableDefinition)stmt;
        ArrayList       columns = def.getColumns();
        String          tableName = ((SimpleIdentExpression)def.getTableName()).getIdent();
        String[]        names = new String[columns.size()];

        if (toUpperCase) {
          tableName = tableName.toUpperCase();
        }

        for (int j = 0; j < columns.size(); j++) {
          Column        column = (Column)columns.get(j);
          String        columnName = column.getIdent();

          if (toUpperCase) {
            columnName = columnName.toUpperCase();
          }

          fieldDeclarations.add(genColumnFieldDeclaration(tableName + Constants.DICT_SEPARATOR + columnName,
                                                          column.getType(),
                                                          column.isNullable()));
          names[j] = columnName;
        }

        fieldDeclarations.add(genTableFieldDeclaration(tableName, names));
      } else if (stmt instanceof ViewDefinition) {
        ViewDefinition  def = (ViewDefinition)stmt;
        ArrayList       columns = def.getColumns();
        String          tableName = ((SimpleIdentExpression)def.getTableName()).getIdent();
        String[]        names = new String[columns.size()];

        if (toUpperCase) {
          tableName = tableName.toUpperCase();
        }

        for (int j = 0; j < columns.size(); j++) {
          ViewColumn    column = (ViewColumn)columns.get(j);
          String        columnName = column.getIdent();

          if (toUpperCase) {
            columnName = columnName.toUpperCase();
          }

          fieldDeclarations.add(genColumnFieldDeclaration(tableName + Constants.DICT_SEPARATOR + columnName,
                                                          column.getType(),
                                                          column.isNullable()));
          names[j] = columnName;
        }

        fieldDeclarations.add(genTableFieldDeclaration(tableName, names));
      } else if (stmt instanceof SequenceDefinition) {
        SequenceDefinition def = (SequenceDefinition)stmt;
        String sequenceName = ((SimpleIdentExpression)def.getSequenceName()).getIdent();
        
        if (toUpperCase) {
          sequenceName = sequenceName.toUpperCase();
        }
        
        fieldDeclarations.add(genColumnFieldDeclaration(Constants.KOPI_SEQUENCES_TABLE + Constants.DICT_SEPARATOR + sequenceName,
                                                        SEQUENCE_TYPE,
                                                        false));
      }
    }
  }
  private static final IntType SEQUENCE_TYPE  = new IntType(null, new Integer(1), new Integer(Integer.MAX_VALUE));
}
