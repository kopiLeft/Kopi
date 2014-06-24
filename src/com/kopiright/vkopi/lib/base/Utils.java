/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.kopiright.util.base.InconsistencyException;

/**
 * loading of image
 * usage:
 * To load image first in Application directory, then in default directory:
 *  Utils.getImage("name");
 * To load default images (that appear in com.kopiright.vkopi.lib.util/resources)
 *  Utils.getDefaultImage("name");
 * To load Application images (that appear in resources)
 *  Utils.getApplicationImage("name");
 *
 */
public class Utils extends com.kopiright.util.base.Utils {

  /**
   * Compress a file in a byte[]
   */
  public static byte[] compress(File file) throws IOException {
    ByteArrayOutputStream       baos = new ByteArrayOutputStream();
    OutputStream                output = new GZIPOutputStream(baos);
    FileInputStream             input = new FileInputStream(file);
    byte[]                      buffer = new byte[10*1024];
    int                         length;

    while ((length = input.read(buffer)) != -1) {
      output.write(buffer, 0, length);
    }
    output.close();

    return baos.toByteArray();
  }

  /**
   * Decompress a byte array
   */
  public static InputStream decompress(byte[] b) throws IOException {
    return new GZIPInputStream(new ByteArrayInputStream(b));
  }

  /**
   * <p>Creates a temporary file in the default temporary directory.
   * The filename will look like this : prefixXXXX.extension</p>
   * <p>Please note that this file will be deleted at the shutdown of
   * the program. </p>
   *
   * @param prefix the prefix of the temp file
   * @param extension the extension of the temp file (can be null. in
   * this case default is "tmp")
   * @return an empty temp file on the local machine
   */
  public static File getTempFile(String prefix,
                                 String extension)
    throws IOException
  {
    return getTempFile(prefix, extension, true);
  }

  /**
   * Creates a temporary file in the default temporary directory. The
   * filename will look like this : prefixXXXX.extension
   *
   * @param prefix the prefix of the temp file
   * @param extension the extension of the temp file (can be null. in
   * this case default is "tmp")
   * @param deleteOnExit if the file has to be deleted at the end of the program.
   * @return an empty temp file on the local machine
   */
  public static File getTempFile(String prefix,
                                 String extension,
                                 boolean deleteOnExit)
    throws IOException
  {
    File	file;

    if (extension == null) {
      extension = "tmp" ;
    }
    file = File.createTempFile(prefix, "." + extension);
    if (deleteOnExit) {
      file.deleteOnExit();
    }

    return file;
  }

  /**
   * return file from classpath or jar file
   * @param file must be an fully qualified file from resource directory
   * path separator is "/"
   * @return a File or null if not found
   */
  public static InputStream getFile(String file) {
    InputStream is = getDefaultFile(file);

    if (is == null) {
      is = getApplicationFile(file);
    }

    if (is == null) {
      System.err.println("Utils ==> cant load: " + file);
    }

    return is;
  }

  /**
   * return file from classpath or jar file
   * @param img must be an file from resource directory
   * path separator is "/"
   * @return an fileIcon or null if not found
   */
  public static InputStream getDefaultFile(String img) {
    return getFileFromResource(img, RESOURCE_DIR);
  }

  /**
   * return image from classpath or jar file
   * @param img must be an image from resource directory
   * path separator is "/"
   * @return an imageIcon or null if not found
   */
  public static InputStream getApplicationFile(String img) {
    return getFileFromResource(img, APPLICATION_DIR);
  }

  /**
   * return an URL from the default resource directory
   */
  public static URL getURLFromResource(String name) {
    return getURLFromResource(name, RESOURCE_DIR);
  }

  /**
   * return an URL from the resources
   */
  public static URL getURLFromResource(String name, String directory) {
    if (directory == null) {
      return null;
    } else {
      // taoufik 2005-07-20: Java Web Start needs to get the class loader based on
      //                     the current class.
      return Utils.class.getClassLoader().getResource(directory + "/" + name);
    }
  }

  /**
   * return file from resources or null if not found
   */
  public static InputStream getFileFromResource(String name, String directory) {
    if (directory == null) {
      return null;
    } else {
      // taoufik 2005-09-12: Java Web Start needs to get the class loader based on
      //                     the current class.
      return Utils.class.getClassLoader().getResourceAsStream(directory + "/" + name);
    }
  }

  public static void log(String	mod, String text) {
    final String		filename;

    System.err.println(mod + "\t" + text);

    // Utils.getTempFile creates a new file kopiXXX.log but we want
    // to use always the same file.
    filename = System.getProperty("java.io.tmpdir") + File.separator + "kopi.log";

    try {
      final PrintWriter         writer;

      writer = new PrintWriter(new FileWriter(filename, true));
      writer.println();
      writer.println();
      writer.println(new Date() + "\t" + mod + "\t" + text + "   ");

      if (writer.checkError()) {
	writer.close();
        throw new IOException("error while writing");
      }
      writer.close();
    } catch (IOException e) {
      // can't write error:
      System.err.println("Can't write in file: " + filename);
      System.err.println(": " + e.getMessage());
    }
  }

  public static byte[] convertUTF(String str) {
    try {
      return str.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new InconsistencyException(e);
    }
  }

  public static String convertUTF(byte[] bytes) {
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new InconsistencyException(e);
    }
  }
  
  /**
   * Returns the version of this build
   */
  @SuppressWarnings("deprecation")
  public static String[] getVersion() {
    try {
      DataInputStream   	in;
      ArrayList<String>         list = new ArrayList<String>();

      in = new DataInputStream(Utils.class.getClassLoader().getResourceAsStream(APPLICATION_DIR + "/version"));

      while (in.available() != 0) {
        list.add(in.readLine());
      }
      in.close();

      return (String[])list.toArray(new String[list.size()]);
    } catch (Exception e) {
      System.err.println("Error while reading version informations.\n" + e);
    }

    return DEFAULT_VERSION;
  }

  /**
   * 2003.08.14; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * Ab jdk 1.4.2 gibt es auch die option -XX:MinHeapFreeRatio=0
   */
  public static void freeMemory() {
    System.gc();
  }
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  public static final String    APPLICATION_DIR = "resources";
  public static final String	RESOURCE_DIR	= "com/kopiright/vkopi/lib/resource";
  private static final String[] DEFAULT_VERSION = new String[] {
    "No version information available.",
    "Copyright 1990-2014 kopiRight Managed Solutions GmbH. All rights reserved."
  };
}
