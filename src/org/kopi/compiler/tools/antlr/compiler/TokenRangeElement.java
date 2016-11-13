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

import org.kopi.compiler.tools.antlr.runtime.*;

class TokenRangeElement extends AlternativeElement {
  String label;
  protected int begin=Token.INVALID_TYPE;
  protected int end  =Token.INVALID_TYPE;
  protected String beginText;
  protected String endText;


  public TokenRangeElement(Grammar g, Token t1, Token t2) {
    super(g);
    begin = grammar.tokenManager.getTokenSymbol(t1.getText()).getTokenType();
    beginText = t1.getText();
    end = grammar.tokenManager.getTokenSymbol(t2.getText()).getTokenType();
    endText = t2.getText();
    line = t1.getLine();
  }
  public void generate(JavaCodeGenerator generator) {
    generator.gen(this);
  }
  public String getLabel() {
    return label;
  }
  public Lookahead look(int k) {
    return grammar.theLLkAnalyzer.look(k, this);
  }
  public void setLabel(String label_) {
    label = label_;
  }
  public String toString() {
    if ( label!=null ) {
      return " "+label+":"+beginText+".."+endText;
    } else {
      return " "+beginText+".."+endText;
    }
  }
}
