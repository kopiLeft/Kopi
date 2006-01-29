/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.compiler.tools.antlr.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

import com.kopiright.util.base.UnicodeReader;

/**
 * Handles the input of compiler source code to the scanner.
 */
public class InputBuffer extends Reader {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	input		the inputststream to read
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, Reader input) {
    this(name, input, true);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	input		the inputststream to read
   * @param	encoding	the name of a supported character encoding
   * @param     unicode         does the input contains unicode sequence ?
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, Reader input, boolean unicode) {
    Reader      underlying;

    this.file = name;
    this.line = 1;
    this.scanner = null;

    if (unicode) {
      underlying = new UnicodeReader(new BufferedReader(input));
    } else {
      underlying = new BufferedReader(input);
    }

    this.reader = new PushbackReader(underlying);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	input		the inputststream to read
   * @param	encoding	the name of a supported character encoding
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, InputStream input, String encoding)
    throws IOException
  {
    this(name, input, encoding, true);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	input		the inputststream to read
   * @param	encoding	the name of a supported character encoding
   * @param     unicode         does the input contains unicode sequence ?
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, InputStream input, String encoding, boolean unicode)
    throws IOException
  {
    this(name, encoding == null
         ? new InputStreamReader(input)
         :new InputStreamReader(input, encoding),
         unicode);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	file		the file to read
   * @param	encoding	the name of a supported character encoding
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, File file, String encoding)
    throws IOException
  {
    this(name, file, encoding, true);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	name		the name of the file
   * @param	file		the file to read
   * @param	encoding	the name of a supported character encoding
   * @param     unicode         does the input contains unicode sequence ?
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(String name, File file, String encoding, boolean unicode)
    throws IOException
  {
    this(name, new FileInputStream(file), encoding, unicode);
    this.path = file;
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	file		the file to read
   * @param	encoding	the name of a supported character encoding
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(File file, String encoding) throws IOException {
    this(file, encoding, true);
  }

  /**
   * Constructs a new input buffer that uses the specified
   * character encoding, or the default character encoding
   * if the specified encoding is null.
   *
   * @param	file		the file to read
   * @param	encoding	the name of a supported character encoding
   * @param     unicode         does the input contains unicode sequence ?
   * @exception	IOException	an I/O exception occurred
   */
  public InputBuffer(File file, String encoding, boolean unicode) throws IOException {
    this(file.getPath(), file, encoding, unicode);
  }

  /**
   * Closes the input buffer.
   */
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
    }
  }

  /**
   * Reads characters into a portion of an array. This method will block
   * until some input is available, an I/O error occurs, or the end of
   * the stream is reached.
   *
   * JLS 3.5 :
   * As a special concession for compatibility with certain operating systems,
   * the ASCII SUB character (\ u 0 0 1 a, or control-Z) is ignored if it is the
   * last character in the escaped input.
   *
   * @param	cbuf		destination buffer
   * @param	off		offset at which to start storing characters
   * @param	len		maximum number of characters to read
   * @return	The number of characters read, or -1 if the end of the stream has been reached
   * @exception	IOException - If an I/O error occurs
   */
  public int read(char[] cbuf, int off, int len) throws IOException {
    int         read;

    read = reader.read(cbuf, off, len);
    if (read == -1) {
      // normal end of file
      return -1;
    } else if (cbuf[off + read - 1] != '\u001a') {
      // no control-Z at end of destination buffer
      return read;
    } else {
      // control-Z at end of destination buffer
      int       next;

      next = reader.read();
      if (next == -1) {
        // control-Z was last character before end of file : remove it
        return read - 1;
      } else {
        // end of file not reached, push back
        reader.unread(next);
        return read;
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the name of the file being read.
   */
  public void setFile(String file) {
    this.file = file;
  }

  /**
   * Returns the name of the file being read.
   */
  public String getFile() {
    return file;
  }
  /**
   * Returns the full path of the file being read.
   */
  public File getPath() {
    return path;
  }

  /**
   * Returns the current line number in the source code.
   */
  public final int getLine() {
    return line;
  }

  /**
   * Sets the current line number in the source code.
   */
  public final void setLine(int line) {
    this.line = line;
  }

  /**
   * Sets the current line number in the source code.
   */
  public final void incrementLine() {
    this.line += 1;
  }

  // --------------------------------------------------------------------
  // COMMUNICATION BETWEEN JFLEX BASED SCANNERS
  // --------------------------------------------------------------------

  /**
   * Exports the buffer state.
   */
  public InputBufferState getBufferState(Scanner scanner) {
    InputBufferState	state;

    if (this.scanner == null) {
      state = null;
    } else {
      state = this.scanner.getBufferState();
    }
    this.scanner = scanner;

    return state;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String		file;
  private File                  path;
  private int			line;

  private Scanner		scanner;

  private final PushbackReader	reader;
}
