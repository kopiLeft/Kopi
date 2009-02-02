/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

class StringLiteralElement extends GrammarAtom {
  // atomText with quotes stripped and escape codes processed
  protected String processedAtomText;


  public StringLiteralElement(Grammar g, Token t) {
    super(g, t);
    if (!(g instanceof LexerGrammar)) {
      // lexer does not have token types for string literals
      TokenSymbol ts = grammar.tokenManager.getTokenSymbol(atomText);
      if (ts == null) {
	g.tool.error("Undefined literal: " + atomText, grammar.getFilename(), t.getLine());
      } else {
	tokenType = ts.getTokenType();
      }
    }
    line = t.getLine();

    // process the string literal text by removing quotes and escaping chars
    // If a lexical grammar, add the characters to the char vocabulary
    processedAtomText = new String();
    for (int i = 1; i < atomText.length()-1; i++) {
      char c = atomText.charAt(i);
      if (c == '\\') {
	if (i+1 < atomText.length()-1) {
	  i++;
	  c = atomText.charAt(i);
	  switch (c) {
	  case 'n' : c = '\n'; break;
	  case 'r' : c = '\r'; break;
	  case 't' : c = '\t'; break;
	  }
	}
      }
      if (g instanceof LexerGrammar) {
	((LexerGrammar)g).charVocabulary.add(c);
      }
      processedAtomText += c;
    }
  }
  public void generate(JavaCodeGenerator generator) {
    generator.gen(this);
  }
  public Lookahead look(int k) {
    return grammar.theLLkAnalyzer.look(k, this);
  }
}
