/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import com.kopiright.compiler.tools.antlr.runtime.BitSet;
import com.kopiright.compiler.tools.antlr.runtime.SemanticException;
import com.kopiright.compiler.tools.antlr.runtime.Token;

public interface ANTLRGrammarParseBehavior {
  void abortGrammar();
  void beginAlt(boolean doAST_);
  void beginChildList();
  // Exception handling
  void beginExceptionGroup();
  void beginExceptionSpec(Token label);
  void beginSubRule(Token label, int line, boolean not);
  // Trees
  void defineRuleName(Token r, String access, String docComment) throws SemanticException;
  void defineToken(Token tokname, Token tokliteral);
  void endAlt();
  void endChildList();
  void endExceptionGroup();
  void endExceptionSpec();
  void endGrammar();
  void endOptions();
  void endRule(String r);
  void endSubRule();
  void hasError();
  void oneOrMoreSubRule();
  void optionalSubRule();
  void refAction(Token action);
  void refArgAction(Token action);
  void setUserExceptions(String thr);
  void refCharLiteral(Token lit, Token label, boolean inverted, boolean lastInRule);
  void refCharRange(Token t1, Token t2, Token label, boolean lastInRule);
  void refElementOption(Token option, Token value);
  void refTokensSpecElementOption(Token tok, Token option, Token value);
  void refExceptionHandler(Token exTypeAndName, Token action);
  void refHeaderAction(Token name,Token act);
  void refInitAction(Token action);
  void refMemberAction(Token act);
  void refPreambleAction(Token act);
  void refReturnAction(Token returnAction);
  void refRule(Token idAssign, Token r, Token label, Token arg);
  void refSemPred(Token pred);
  void refStringLiteral(Token lit, Token label, boolean lastInRule);
  void refToken(Token assignId, Token t, Token label, Token args,
		boolean inverted, boolean lastInRule);
  void refTokenRange(Token t1, Token t2, Token label, boolean lastInRule);
  void refWildcard(Token t, Token label);
  void setArgOfRuleRef(Token argaction);
  void setCharVocabulary(BitSet b);
  // Options
  void setFileOption(Token key, Token value, String filename);
  void setGrammarOption(Token key, Token value);
  void setRuleOption(Token key, Token value);
  void setSubruleOption(Token key, Token value);
  void startLexer(String file, Token name, String doc);
  // Flow control for grammars
  void startParser(String file, Token name, String doc);
  void synPred();
  void zeroOrMoreSubRule();
}
