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

package com.kopiright.compiler.tools.antlr.compiler;

/**
 * A GrammarElement is a generic node in our
 * data structure that holds a grammar in memory.
 * This data structure can be used for static
 * analysis or for dynamic analysis (during parsing).
 * Every node must know which grammar owns it, how
 * to generate code, and how to do analysis.
 */
abstract class GrammarElement {
  /*
   * Note that Java does static argument type matching to
   * determine which function to execute on the receiver.
   * Here, that implies that we cannot simply say
   * generator.gen(this) in GrammarElement or
   * only JavaCodeGenerator.gen(GrammarElement ge) would
   * ever be called.
   */
  protected Grammar grammar;
  protected int line;


  public GrammarElement(Grammar g) {
    grammar = g;
  }
  public void generate(JavaCodeGenerator generator) {
  }
  public int getLine() {
    return line;
  }
  public Lookahead look(int k) { return null; }
  public abstract String toString();
}
