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

public interface LLkGrammarAnalyzer extends GrammarAnalyzer {

  boolean deterministic(AlternativeBlock blk);
  boolean deterministic(OneOrMoreBlock blk);
  boolean deterministic(ZeroOrMoreBlock blk);
  Lookahead FOLLOW(int k, RuleEndElement end);
  Lookahead look(int k, ActionElement action);
  Lookahead look(int k, AlternativeBlock blk);
  Lookahead look(int k, BlockEndElement end);
  Lookahead look(int k, CharLiteralElement atom);
  Lookahead look(int k, CharRangeElement end);
  Lookahead look(int k, GrammarAtom atom);
  Lookahead look(int k, OneOrMoreBlock blk);
  Lookahead look(int k, RuleBlock blk);
  Lookahead look(int k, RuleEndElement end);
  Lookahead look(int k, RuleRefElement rr);
  Lookahead look(int k, StringLiteralElement atom);
  Lookahead look(int k, SynPredBlock blk);
  Lookahead look(int k, TokenRangeElement end);
  Lookahead look(int k, WildcardElement wc);
  Lookahead look(int k, ZeroOrMoreBlock blk);
  Lookahead look(int k, String rule);
  void setGrammar(Grammar g);
  boolean subruleCanBeInverted(AlternativeBlock blk, boolean forLexer);
}
