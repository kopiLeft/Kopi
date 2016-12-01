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

package org.kopi.compiler.tools.antlr.compiler;

import org.kopi.compiler.tools.antlr.runtime.Token;

class WildcardElement extends GrammarAtom {
  protected String label1;

  public WildcardElement(Grammar g, Token t) {
    super(g, t);
    line = t.getLine();
  }
  public void generate(JavaCodeGenerator generator) {
    generator.gen(this);
  }
  public String getLabel() {
    return label1;
  }
  public Lookahead look(int k) {
    return grammar.theLLkAnalyzer.look(k, this);
  }
  public void setLabel(String label_) {
    label1 = label_;
  }
  public String toString() {
    String s = " ";
    if (label1!=null) {
      s += label1+":";
    }
    return s + ".";
  }
}
