/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import java.util.Hashtable;

import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents a compilation unit
 */
public class CCompilationUnit {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a compilation unit context.
   */
  public CCompilationUnit(KjcEnvironment environment,
                          String packageName,
			  JClassImport[] importedClasses,
			  JPackageImport[] importedPackages,
			  Hashtable loadedClasses) {
    this.packageName = packageName;
    this.importedClasses = importedClasses;
    this.importedPackages = importedPackages;
    this.loadedClasses = loadedClasses;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (LOOKUP)
  // ----------------------------------------------------------------------

  /**
   * @param	caller		the class of the caller
   * @return	a class according to imports or null if error occur
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
      ClassReader classReader = environment.getClassReader();
      TypeFactory typeFactory = environment.getTypeFactory();
    // $$$ USE A STRING BUFFER FOR IMPORT
    if (name.lastIndexOf('/') == -1) {
      // 6.5.4.1 Simple Type Names

      CClass            cl;

      // First look for a type declared by a single-type-import of by a type declaration
      if ((cl = (CClass)loadedClasses.get(name)) != null) {
	// If type is declared by a single-type-import, mark it as used (max. 1)
	for (int i = 0; i < importedClasses.length; i++) {
	  if (name == importedClasses[i].getSimpleName()) {
	    importedClasses[i].setUsed();
	    break;
	  }
	}

	return cl;
      }

      // Otherwise, look for a type declared in another compilation unit of this package
      if (packageName.length() == 0) {
	cl = classReader.hasClassFile(name) ? classReader.loadClass(typeFactory, name) : null;
      } else {
	String		temp = packageName + '/' + name;

	cl = classReader.hasClassFile(temp) ?  classReader.loadClass(typeFactory, temp) : null;
      }

      if (cl != null) {
	loadedClasses.put(name, cl);
      } else {
	// Otherwise, look for a type declared by EXACTLY ONE import-on-demand declaration
	for (int i = 0; i < importedPackages.length; i++) {
	  String	qualifiedName;
          
          if (classReader.hasClassFile(importedPackages[i].getName())) {
            // import on demand of enclosed classes!
            qualifiedName = (importedPackages[i].getName() + '$' + name).intern();
          } else {
            qualifiedName = (importedPackages[i].getName() + '/' + name).intern();
          }
	  if (classReader.hasClassFile(qualifiedName)) {
	    CClass	lastClass = (CClass)loadedClasses.get(name);

            // 7.5.2 Type-Import-on-Demand Declaration
            // A type-import-on-demand declaration allows all accessible (�6.6) types 
            // declared in the type or package named by a canonical name to be imported 
            // as needed
            if (lastClass!= null && !lastClass.isAccessible(caller)) {
              lastClass = null; // not accessible -> not imported
            }

	    if (lastClass != null && !lastClass.getQualifiedName().equals(qualifiedName)) {
	      // Oops, the name is ambiguous (declared by more than one import-on-demand declaration)
	      throw new UnpositionedError(KjcMessages.CUNIT_RENAME2, name);
	    }
	    loadedClasses.put(name, classReader.loadClass(typeFactory, qualifiedName));
	    importedPackages[i].setClassUsed(name);
	  }
	}
      }

      // now the name must be unique and found
      if ((cl = (CClass)loadedClasses.get(name)) == null) {
	throw new UnpositionedError(KjcMessages.CLASS_UNKNOWN, name);
      }

      return cl.getCClass();
    } else {
      // 6.5.4.2 Qualified Type Names: look directly at top
      if (!environment.getClassReader().hasClassFile(name)) {
	throw new UnpositionedError(KjcMessages.CLASS_UNKNOWN, name);
      }

      return classReader.loadClass(typeFactory, name); 
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String			packageName;

  private final JClassImport[]		importedClasses;
  private final JPackageImport[]	importedPackages;

  private final Hashtable		loadedClasses;
  private final KjcEnvironment          environment;
}
