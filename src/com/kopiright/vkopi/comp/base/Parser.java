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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.tools.antlr.extra.Scanner;

public abstract class Parser extends com.kopiright.compiler.tools.antlr.extra.Parser {

  /**
   * Constructs a new parser instance.
   *
   * @param	compiler	the invoking compiler.
   * @param	scanner		the token stream generator
   * @param	lookahead	lookahead
   */
  protected Parser(Compiler compiler, Scanner scanner, int lookahead) {
    super(compiler, scanner, lookahead);
  }

  /**
   * Sets the locale specified for this compilation unit.
   */
  protected void setLocale(String locale) {
    this.locale = locale;
  }


  /**
   * Returns the locale specified for this compilation unit.
   */
  protected String getLocale() {
    return locale;
  }

  /**
   * Verifies that a locale is specified for this compilation unit.
   * @param     phylum          a description of the translated syntax element.
   */
  protected void checkLocaleIsSpecified(String phylum) {
    if (locale == null) {
      reportTrouble(new PositionedError(buildTokenReference(),
                                        BaseMessages.TRANSLATION_WITHOUT_LOCALE,
                                        new String[]{ phylum }));
    }
  }

  /**
   * Verifies that no locale is specified for this compilation unit.
   * @param     phylum          a description of the un-translated syntax element.
   */
  protected void checkNoLocaleSpecified(String phylum) {
    if (locale != null) {
      reportTrouble(new PositionedError(buildTokenReference(),
                                        BaseMessages.LOCALE_WITHOUT_TRANSLATION,
                                        new String[]{ locale, phylum }));
    }
  }

  /**
   * Verifies that a locale is specified for this compilation unit if and only
   * iff the specified condition is verified.
   *
   * @param     phylum          a description of the translated syntax element.
   */
  protected void checkLocaleIsSpecifiedIff(boolean condition, String phylum) {
    if (condition) {
      checkLocaleIsSpecified(phylum);
    } else {
      checkNoLocaleSpecified(phylum);
    }
  }

  private String                locale;
}
