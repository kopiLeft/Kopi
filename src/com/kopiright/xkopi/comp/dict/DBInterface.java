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

package com.kopiright.xkopi.comp.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import com.kopiright.bytecode.classfile.ClassFileFormatException;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.BytecodeOptimizer;
import com.kopiright.kopi.comp.kjc.CBinaryTypeContext;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CSourceClass;
import com.kopiright.kopi.comp.kjc.CTypeVariable;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JClassBlock;
import com.kopiright.kopi.comp.kjc.JClassDeclaration;
import com.kopiright.kopi.comp.kjc.JClassFieldDeclarator;
import com.kopiright.kopi.comp.kjc.JClassImport;
import com.kopiright.kopi.comp.kjc.JCompilationUnit;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JExpressionStatement;
import com.kopiright.kopi.comp.kjc.JFieldDeclaration;
import com.kopiright.kopi.comp.kjc.JFormalParameter;
import com.kopiright.kopi.comp.kjc.JMethodCallExpression;
import com.kopiright.kopi.comp.kjc.JMethodDeclaration;
import com.kopiright.kopi.comp.kjc.JPackageImport;
import com.kopiright.kopi.comp.kjc.JPackageName;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.kopi.comp.kjc.JTypeDeclaration;
import com.kopiright.kopi.comp.kjc.JVariableDefinition;
import com.kopiright.kopi.comp.kjc.KjcClassReader;
import com.kopiright.kopi.comp.kjc.KjcEnvironment;
import com.kopiright.kopi.comp.kjc.KjcOptions;
import com.kopiright.kopi.comp.kjc.KjcSignatureParser;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.xkopi.comp.database.DatabaseColumn;
import com.kopiright.xkopi.comp.database.DatabaseMember;
import com.kopiright.xkopi.comp.database.DatabaseTable;
import com.kopiright.xkopi.comp.dbi.Column;
import com.kopiright.xkopi.comp.dbi.DbiType;
import com.kopiright.xkopi.comp.dbi.SCompilationUnit;
import com.kopiright.xkopi.comp.dbi.TableDefinition;
import com.kopiright.xkopi.comp.dbi.ViewColumn;
import com.kopiright.xkopi.comp.dbi.ViewDefinition;
import com.kopiright.xkopi.comp.sqlc.SimpleIdentExpression;
import com.kopiright.xkopi.comp.sqlc.Statement;
import com.kopiright.xkopi.comp.sqlc.Type;
import com.kopiright.xkopi.comp.xkjc.XKjcTypeFactory;

/**
 * This class generates an interface of the database in a .class file
 */
public class DBInterface implements com.kopiright.kopi.comp.kjc.Constants {

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
    com.kopiright.kopi.comp.kjc.CStdType.init(compiler, env);
    com.kopiright.xkopi.comp.xkjc.XStdType.init(compiler, new CBinaryTypeContext(env.getClassReader(),
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

          fieldDeclarations.add(genColumnFieldDeclaration(tableName + "__" + columnName,
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

          fieldDeclarations.add(genColumnFieldDeclaration(tableName + "__" + columnName,
                                                          column.getType(),
                                                          column.isNullable()));
          names[j] = columnName;
        }

        fieldDeclarations.add(genTableFieldDeclaration(tableName, names));
      }
    }
  }
}
