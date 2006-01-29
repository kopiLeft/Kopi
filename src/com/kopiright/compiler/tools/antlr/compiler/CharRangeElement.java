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

import com.kopiright.compiler.tools.antlr.runtime.*;

class CharRangeElement extends AlternativeElement {
  String label;
  protected char begin=0;
  protected char end  =0;
  protected String beginText;
  protected String endText;


  public CharRangeElement(LexerGrammar g, Token t1, Token t2) {
    super(g);
    begin = (char)ANTLRLexer.tokenTypeForCharLiteral(t1.getText());
    beginText = t1.getText();
    end   = (char)ANTLRLexer.tokenTypeForCharLiteral(t2.getText());
    endText = t2.getText();
    line = t1.getLine();
    // track which characters are referenced in the grammar
    for (int i=begin; i<=end; i++) {
      g.charVocabulary.add(i);
    }
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
