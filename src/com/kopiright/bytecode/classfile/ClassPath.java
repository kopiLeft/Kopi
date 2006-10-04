/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.bytecode.classfile;

import java.io.*;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipFile;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;

import com.kopiright.util.base.Utils;

/**
 * This class implements the conceptual directory structure for .class files
 */
public class ClassPath {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class path object.
   *
   * @param	path		the directory names defining the class path
   */
  public ClassPath(String workingDir, String path, String extdirs) {
    if (workingDir != null) {
      workingDirectory = new File(workingDir);
      // because it is used as parent in the constructor of File, it should 
      // be absolute (otherwise Java 1.4 has problems)
      workingDirectory = workingDirectory.getAbsoluteFile();
    }
    if (path == null) {
      // no path specified, use default
      path = System.getProperty("java.class.path");
    }
    if (path == null) {
      // last resort, use current directory
      path = ".";
    }

    this.dirs = loadClassPath(path);

    if (extdirs == null) {
      extdirs = System.getProperty("java.ext.dirs");
    }

    if (extdirs != null) {
      // According to
      // http://java.sun.com/j2se/1.4/docs/guide/extensions/spec.html,
      // extension directory property can refer to several directories

      Vector            container = new Vector();
      StringTokenizer   entries;
      ClassDirectory[]  tmp;

      entries = new StringTokenizer(extdirs, File.pathSeparator);
      while (entries.hasMoreTokens()) {
        loadExtensionDirectory(container, entries.nextToken());
      }

      tmp = new ClassDirectory[dirs.length + container.size()];
      container.copyInto(tmp);
      System.arraycopy(dirs, 0, tmp, container.size(), dirs.length);
      dirs = tmp;
    }
  }

  /**
   * returns the absoulte path as File object of the file represented as string.
   */
  private File createAbsoluteFile(String filestring) {
    // a) WORKAROUND !!! there are some problems with the names of files, if they are 
    // not absolute (not bsolute path is used as "parent", "." in the path ...)
    // b) the working directory not the System.property user.dir should 
    // be used to create the absolute path.

    File file = new File(filestring);
    
    if (file.isAbsolute()) {
      return file;
    } else {
      if (workingDirectory != null) {
        // use workingDirectory as prefix
        return new File(workingDirectory, filestring);
      } else {
        // uses system property user.dir as prefix
        return file.getAbsoluteFile();
      }
    }
  }

  /**
   * Check that specified directory is a valid directory and add all
   * zip or jar files in the container.
   *
   * @param     container       the container to put the jar files
   * @param     directory       the directory where to get the files
   */
  private void loadExtensionDirectory(Vector container, String directory) {
    File                extDirectory = createAbsoluteFile(directory);
    
    if (extDirectory.isDirectory()) {
      File[]        extFiles = extDirectory.listFiles();
      
      for (int i = 0; i < extFiles.length; i++) {
        // We consider only files in this directory
        if (extFiles[i].isFile()) {
          ClassDirectory        dir;

          dir = loadCompressedFile(extFiles[i]);
          if (dir != null) {
            container.add(dir);
          }
        }
      }
    } else {
      System.err.println(directory + "is not a valid directory");
    }
  }


  /**
   * Loads the conceptual directories defining the class path.
   *
   * @param	classPath	the directory names defining the class path
   */
  private ClassDirectory[] loadClassPath(String classPath) {
    Vector		container = new Vector();

    // load specified class directories
    StringTokenizer	entries;

    entries = new StringTokenizer(classPath, File.pathSeparator);
    while (entries.hasMoreTokens()) {
      ClassDirectory	dir;

      dir = loadClassDirectory(entries.nextToken());
      if (dir != null) {
	container.addElement(dir);
      }
    }

    // add system directories
    if (System.getProperty("sun.boot.class.path") != null) {
      // !!! graf 010508 : can there be more than one entry ?
      entries = new StringTokenizer(System.getProperty("sun.boot.class.path"),
				    File.pathSeparator);
      while (entries.hasMoreTokens()) {
	ClassDirectory	dir;

	dir = loadClassDirectory(entries.nextToken());
	if (dir != null) {
	  container.addElement(dir);
	}
      }
    } else {
      String	version = System.getProperty("java.version");

      if (version.startsWith("1.2") || version.startsWith("1.3")) {
	ClassDirectory	dir;

	dir = loadClassDirectory(System.getProperty("java.home")
				 + File.separatorChar + "lib"
				 + File.separatorChar + "rt.jar");
	if (dir != null) {
	  container.addElement(dir);
	}
      }
    }

    return (ClassDirectory[])Utils.toArray(container, ClassDirectory.class);
  }

  /**
   * Loads a conceptual class directory.
   *
   * @param	name		the name of the directory
   */
  private ClassDirectory loadClassDirectory(String name) {
    return loadClassDirectory(createAbsoluteFile(name));
  }

  /**
   * Loads a conceptual class directory.
   *
   * @param	file		the directory
   */
  private ClassDirectory loadClassDirectory(File file) {
    if (! file.isAbsolute()) {
      file = createAbsoluteFile(file.getAbsolutePath());
    }
    try {
      if (file.isDirectory()) {
	return new DirClassDirectory(file);
      } else if (file.isFile()) {
        return loadCompressedFile(file);
      } else {
	// wrong file type, ignore it
	return null;
      }
    } catch (SecurityException e) {
      // unreadable file, ignore it
      return null;
    }
  }

  /**
   * Loads a zip/jar file.
   *
   * @param	file		the compressed file
   */
  private static ClassDirectory loadCompressedFile(File file) {
    // check if file is zipped (.zip or .jar)
    final String    filename = file.getName();
        
    if (filename.endsWith(".zip") || filename.endsWith(".jar")) {
      try {
        return new ZipClassDirectory(new ZipFile(file));
      } catch (ZipException e) {
        // it was not a zip file, ignore it
        return null;
      } catch (IOException e) {
        // ignore it
        return null;
      }
    } else {
      // wrong suffix, ignore it
      return null;
    }
  }

  // ----------------------------------------------------------------------
  // CLASS LOADING
  // ----------------------------------------------------------------------

  /**
   * Loads the class with specified name.
   *
   * @param	name		the qualified name of the class
   * @param	interfaceOnly	do not load method code ?
   * @return	the class info for the specified class,
   *		or null if the class cannot be found
   */
  public ClassInfo loadClass(String name, boolean interfaceOnly) {
    for (int i = 0; i < dirs.length; i++) {
      ClassInfo		info;

      info = dirs[i].loadClass(name, interfaceOnly);
      if (info != null) {
        if (name.equals(info.getName())) {
          return info;
        } else {
          // we might throw an exception here
        }
      }
    }

    return null;
  }


  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public boolean packageExists(String name) {
    for (int i = 0; i < dirs.length; i++) {
      if (dirs[i].packageExists(name)) {
        return true;
      }
    }
    return false;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ClassDirectory[]	dirs;	// list of directories in class path
  private File                  workingDirectory = null;
}

/**
 * This class represents a conceptual directory which may hold
 * Java class files. Since Java can use archived class files found in
 * a compressed ("zip") file, this entity may or may not correspond to
 * an actual directory on disk.
 */
abstract class ClassDirectory {

  /**
   * Loads the class with specified name from this directory.
   *
   * @param	name		the qualified name of the class
   * @param	interfaceOnly	do not load method code ?
   * @return	the class info for the specified class,
   *		or null if the class cannot be found in this directory
   */
  public abstract ClassInfo loadClass(String name, boolean interfaceOnly);
  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public abstract boolean packageExists(String name);
}

class DirClassDirectory extends ClassDirectory {
  /**
   * Constructs a class directory representing a real directory
   */
  public DirClassDirectory(File dir) {
    this.dir = dir;
  }

  /**
   * Loads the class with specified name from this directory.
   *
   * @param	name		the qualified name of the class
   * @param	interfaceOnly	do not load method code ?
   * @return	the class info for the specified class,
   *		or null if the class cannot be found in this directory
   */
  public ClassInfo loadClass(String name, boolean interfaceOnly) {
    File		file;

    file = new File(dir.getAbsolutePath(),
		    name.replace('/', File.separatorChar) + ".class");
    if (!file.canRead()) {
      return null;
    } else {
      try {
	Data		data = new Data(new FileInputStream(file));

	try {
	  return new ClassInfo(data.getDataInput(), interfaceOnly);
	} catch (ClassFileFormatException e) {
	  e.printStackTrace();
	  return null;
	} catch (IOException e) {
	  e.printStackTrace();
	  return null;
	} finally {
	  data.release();
	}
      } catch (FileNotFoundException e) {
	return null; // really bad : file exists but is not accessible
      }
    }
  }

  /**
   * Returns ture iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public boolean packageExists(String name) {
    File		file;

    file = new File(dir.getPath(),
		    name.replace('/', File.separatorChar));

    return file.isDirectory();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private File		dir;		// non null iff is a real directory
}

class ZipClassDirectory extends ClassDirectory {
  /**
   * Constructs a class directory representing a zipped file
   */
  public ZipClassDirectory(ZipFile zip) {
    this.zip = zip;
  }

  /**
   * Loads the class with specified name from this directory.
   *
   * @param	name		the qualified name of the class
   * @param	interfaceOnly	do not load method code ?
   * @return	the class info for the specified class,
   *		or null if the class cannot be found in this directory
   */
  public ClassInfo loadClass(String name, boolean interfaceOnly) {
    ZipEntry		entry;

    entry = zip.getEntry(name + ".class");
    if (entry == null) {
      return null;
    } else {
      try {
	Data		data = new Data(zip.getInputStream(entry));

	try {
	  return new ClassInfo(data.getDataInput(), interfaceOnly);
	} catch (ClassFileFormatException e) {
	  e.printStackTrace();
	  return null;
	} catch (IOException e) {
	  e.printStackTrace();
	  return null;
	} finally {
	  data.release();
	}
      } catch (IOException e) {
	return null; // really bad : file exists but is not accessible
      }
    }
  }

  /**
   * Returns true iff the specified package exists in the classpath
   *
   * @param	name		the name of the package
   */
  public boolean packageExists(String name) {
    ZipEntry		entry;

    entry = zip.getEntry(name);
    if (entry != null) {
      // !!! FIXME lackner 25.11.01 .isDirectory() returns false for packages ?!?
      // return entry.isDirectory();
      return true;
    } else {
      // in rt.jar of JRE 1.4, no directories are stored : 
      // look if a class exists in the specified package.
      for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
        entry = (ZipEntry)e.nextElement();

        if (entry.getName().startsWith(name + "/")
            && entry.getName().endsWith(".class")) {
          return true;
        }
      }
      return false;
    }
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ZipFile	zip;		// non null iff is a zip or jar file
}

// optimization
class Data {
  public Data(InputStream is) {
    this.is = is;
  }

  public DataInput getDataInput() throws IOException {
    ba = getByteArray();
    int		n = 0;
    int		available;
    while (true) {
      int count = is.read(ba, n, ba.length - n);
      if (count < 0) {
	break;
      }
      available = is.available();
      n += count;
      if (n + available > ba.length) {
	byte[] temp = new byte[ba.length * 2];
	System.arraycopy(ba, 0, temp, 0, ba.length);
	ba = temp;
      } else if (available == 0) {
	break;
      }
    }

    is.close();
    is = null;

    return new DataInputStream(new ByteArrayInputStream(ba, 0, n));
  }

  public void release() {
    release(ba);
  }

  private static byte[] getByteArray() {
    if (!Constants.ENV_USE_CACHE || stack.empty()) {
      return new byte[10000];
    }
    return (byte[])stack.pop();
  }

  private static void release(byte[] arr) {
    if (Constants.ENV_USE_CACHE) {
      stack.push(arr);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private InputStream           is;
  private byte[]                ba;
  private static Stack          stack = new Stack();
}
