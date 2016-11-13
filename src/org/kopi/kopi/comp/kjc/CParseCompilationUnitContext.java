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

package org.kopi.kopi.comp.kjc;

import java.util.ArrayList;


public class CParseCompilationUnitContext {

  public void release() {
    release(this);
  }

  public static void release(CParseCompilationUnitContext context) {
    context.clear();
  }

  private void clear() {
    packageImports.clear();
    classImports.clear();
    typeDeclarations.clear();
    pack = null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  public void setPackage(JPackageName pack) {
    packageName = pack == JPackageName.UNNAMED ? "" : pack.getName() + '/';
    this.pack = pack;
  }

  public void addPackageImport(JPackageImport pack) {
    packageImports.add(pack);
  }

  public void addClassImport(JClassImport clazz) {
    classImports.add(clazz);
  }

  public void addTypeDeclaration(ClassReader classReader, JTypeDeclaration decl) {
    typeDeclarations.add(decl);
    decl.generateInterface(classReader, null, packageName);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public JPackageImport[] getPackageImports() {
    return (JPackageImport[])packageImports.toArray(new JPackageImport[packageImports.size()]);
  }

  public JClassImport[] getClassImports() {
    return (JClassImport[])classImports.toArray(new JClassImport[classImports.size()]);
  }

  public JTypeDeclaration[] getTypeDeclarations() {
    return (JTypeDeclaration[])typeDeclarations.toArray(new JTypeDeclaration[typeDeclarations.size()]);
  }

  public JPackageName getPackageName() {
    return pack;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JPackageName		pack;
  private String		packageName;
  private ArrayList		packageImports = new ArrayList();
  private ArrayList		classImports = new ArrayList();
  private ArrayList		typeDeclarations = new ArrayList();
}
