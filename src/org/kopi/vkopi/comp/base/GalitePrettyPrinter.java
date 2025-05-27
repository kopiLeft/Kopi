/*
 * Copyright (c) 1990-2025 kopiRight Managed Solutions GmbH
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
 */

package org.kopi.vkopi.comp.base;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.kopi.compiler.base.TabbedPrintWriter;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.vkopi.comp.form.VKBlock;
import org.kopi.vkopi.comp.form.VKField;
import org.kopi.vkopi.comp.form.VKForm;
import org.kopi.vkopi.comp.form.VKPage;

/**
 * This class implements a Pretty Printer for Galite.
 * It formats and outputs various components of a Galite program.
 */
public class GalitePrettyPrinter implements VKVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a GalitePrettyPrinter object that writes the output to a file.
   *
   * @param fileName the name of the file to write the formatted code to
   * @param factory the TypeFactory to manage types during printing
   * @throws IOException if an I/O error occurs while creating the file writer
   */
  public GalitePrettyPrinter(String fileName, TypeFactory factory) throws IOException {
    // Initializes with a TabbedPrintWriter to handle file output
    this(new TabbedPrintWriter(new BufferedWriter(new FileWriter(fileName))), factory);
  }

  /**
   * Constructs a GalitePrettyPrinter with an existing TabbedPrintWriter.
   *
   * @param p the TabbedPrintWriter to be used for output
   * @param factory the TypeFactory to manage types during printing
   */
  public  GalitePrettyPrinter(TabbedPrintWriter p, TypeFactory factory) {
    // Assigns the writer and type factory
    this.p = p;
    this.factory = factory;
    // Initializes the position to 0 (used for indentation or formatting)
    this.pos = 0;
  }

  /**
   * Closes the underlying writer (file output stream).
   */
  public void close() {
    p.close();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Visits a VKForm element.
   *
   * @param vkForm the VKForm element to visit
   */
  @Override
  public void visitVKForm(VKForm vkForm) {
    printCopyright();
  }

  /**
   * Visits a VKCommand element.
   *
   * @param vkCommand the VKCommand element to visit
   */
  @Override
  public void visitVKCommand(VKCommand vkCommand) {}

  /**
   * Visits a VKTrigger element.
   *
   * @param vkTrigger the VKTrigger element to visit
   */
  @Override
  public void visitVKTrigger(VKTrigger vkTrigger) {}

  /**
   * Visits a VKPage element.
   *
   * @param vkPage the VKPage element to visit
   */
  @Override
  public void visitVKPage(VKPage vkPage) {}

  /**
   * Visits a VKBlock element.
   *
   * @param vkBlock the VKBlock element to visit
   */
  @Override
  public void visitVKBlock(VKBlock vkBlock) {}

  /**
   * Visits a VKField element.
   *
   * @param vkField the VKField element to visit
   */
  @Override
  public void visitVKField(VKField vkField) {}

  // ----------------------------------------------------------------------
  //  METHODS FOR PRINTING
  // ----------------------------------------------------------------------

  /**
   * Prints the copyright header for the generated file.
   */
  protected void printCopyright() {
    print("// ----------------------------------------------------------------------");
    newLine();
    print("// Copyright (c) 2013-2025 kopiLeft Services SARL, Tunisie");
    newLine();
    print("// Copyright (c) 2018-2025 ProGmag SAS, France");
    newLine();
    print("// ----------------------------------------------------------------------");
    newLine();
    print("// All rights reserved - tous droits réservés.");
    newLine();
    print("// ----------------------------------------------------------------------");
    newLine();
  }

  /**
   * Prints an integer as a string.
   *
   * @param i the integer to print
   */
  protected void print(int i) {
    p.print("" + i);
  }

  /**
   * Prints a string.
   *
   * @param str the string to print
   */
  protected void print(String str) {
    p.print(str);
  }

  /**
   * Prints a new line with proper indentation.
   */
  protected void newLine() {
    p.println();
  }

  /**
   * Generates a string of spaces of a given length.
   *
   * @param i the number of spaces to generate
   * @return a string of spaces
   */
  protected String space(int i) {
    return new String(new char[i]).replace((char)0, ' ');
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * The current position used for formatting or indentation.
   */
  protected int pos;

  /**
   * The TabbedPrintWriter used for outputting the formatted code.
   */
  private TabbedPrintWriter p;

  /**
   * The TypeFactory used for type management during printing.
   */
  protected TypeFactory factory;
}