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

import java.util.HashSet;
import java.util.Hashtable;

import org.kopi.bytecode.classfile.ClassInfo;
import org.kopi.bytecode.classfile.ClassPath;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.util.base.InconsistencyException;

/**
 * This class implements the conceptual directory structure for .class files
 */
public class KjcClassReader extends org.kopi.util.base.Utils implements ClassReader{

  public KjcClassReader(String workingdir, String classp, String extdirs, SignatureParser signatureParser) {
    classpath = new ClassPath(workingdir, classp, extdirs);
    this.signatureParser = signatureParser;
  }
  // ----------------------------------------------------------------------
  // LOAD CLASS
  // ----------------------------------------------------------------------

  /**
   * Loads class definition from .class file
   */
  public CClass loadClass(TypeFactory typeFactory, String name) {
    CClass		cl = (CClass)allLoadedClasses.get(name);

    if (cl != null) {
      // look in cache
      return cl != CClass.CLS_UNDEFINED ? cl : null;
    } else {
      ClassInfo		file = classpath.loadClass(name, true);

      cl = file == null ? CClass.CLS_UNDEFINED : new CBinaryClass(signatureParser, this, typeFactory, file);
      allLoadedClasses.put(name, cl);
      if (cl instanceof CBinaryClass) {
        try {
          ((CBinaryClass)cl).checkTypes(new CBinaryTypeContext(this, typeFactory, null, cl));
        } catch (UnpositionedError e) {
          e.addPosition(org.kopi.compiler.base.TokenReference.NO_REF);
          e.printStackTrace();
          throw new InconsistencyException("Error while reading class");
        }
      }

      return cl;
    }
  }

  /**
   * @return  false if name exists for source class as source class
   *          in an other file
   * @param CClass a class to add (must be a CSourceClass)
   */
  public boolean addSourceClass(CSourceClass cl) {
    CClass	last = (CClass)allLoadedClasses.put(cl.getQualifiedName(), cl);
    
    allLoadedPackages.add(cl.getPackage());
    return (last == null)
      || (cl.getOwner() != null)
      || !(last instanceof CSourceClass)
      || last.getSourceFile() == cl.getSourceFile();
  }

  /**
   * @return a class file that contain the class named name
   * @param name the name of the class file
   */
  public boolean hasClassFile(String name) {
    CClass		cl = (CClass)allLoadedClasses.get(name);
    return (cl != null && cl != CClass.CLS_UNDEFINED) || (classpath.loadClass(name, true) != null);
  }

  public SignatureParser getSignatureParser() {
    return signatureParser;
  }

  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public boolean packageExists(String name) {
    if (allLoadedPackages.contains(name)) {
      return true;
    } else {
      return classpath.packageExists(name);
    }
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Hashtable	allLoadedClasses = new Hashtable(2000);
  private HashSet	allLoadedPackages = new HashSet(2000);
 //  private TypeFactory   typeFactory;
  private ClassPath     classpath;
  private final SignatureParser signatureParser;
}
