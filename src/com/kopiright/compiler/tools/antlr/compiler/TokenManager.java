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

import java.util.Enumeration;

import com.kopiright.compiler.tools.antlr.runtime.*;

/**
 * Interface that describes the set of defined tokens
 */
interface TokenManager {
  Object clone();

  /**
   * define a token symbol
   */
  void define(TokenSymbol ts);

  /**
   * Get the name of the token manager
   */
  String getName();

  /**
   * Get a token string by index
   */
  String getTokenStringAt(int idx);

  /**
   * Get the TokenSymbol for a string
   */
  TokenSymbol getTokenSymbol(String sym);
  TokenSymbol getTokenSymbolAt(int idx);

  /**
   * Get an enumerator over the symbol table
   */
  Enumeration getTokenSymbolElements();
  Enumeration getTokenSymbolKeys();

  /**
   * Get the token vocabulary (read-only).
   * @return A Vector of Strings indexed by token type */
  Vector getVocabulary();

  /**
   * Is this token manager read-only?
   */
  boolean isReadOnly();

  void mapToTokenSymbol(String name, TokenSymbol sym);

  /**
   * Get the highest token type in use
   */
  int maxTokenType();

  /**
   * Get the next unused token type
   */
  int nextTokenType();

  void setName(String n);

  void setReadOnly(boolean ro);

  /**
   * Is a token symbol defined?
   */
  boolean tokenDefined(String symbol);
}
