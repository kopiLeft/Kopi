/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.TextFieldState.ConvertType;

/**
 * Validation strategy for string fields.
 */
public class StringValidationStrategy extends AllowAllValidationStrategy {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new string validation strategy.
   * @param width The field width.
   * @param height The field height.
   * @param fixedNewLine Use fixed lines or dynamic lines ?
   * @param convertType The convert type to be used.
   */
  public StringValidationStrategy(int width, int height, boolean fixedNewLine, ConvertType convertType) {
    this.width = width;
    this.height = height;
    this.fixedNewLine = fixedNewLine;
    this.convertType = convertType;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void checkType(VInputTextField field, String text) throws CheckTypeException {
    if (text == null || "".equals(text)) {
      field.setText(null);
    } else {
      switch (convertType) {
      case NONE:
        break;
      case UPPER:
        text = text.toUpperCase();
        break;
      case LOWER:
        text = text.toLowerCase();
        break;
      case NAME:
        text = convertName(text);
        break;
      default:
        throw new RuntimeException();
      }
      
      if (! checkText(text)) {
        throw new CheckTypeException(field, "00013");
      }
      
      field.setText(text);
    }
  }
  
  /**
   * Returns the convert type of this validation strategy.
   * @return The convert type of this validation strategy.
   */
  public ConvertType getConvertType() {
    return convertType;
  }
  
  /**
   * Convert the first letter in each word in the source text into upper case.
   *
   * @param     source          the source text.
   */
  private String convertName(String source) {
    char[]      chars = source.toLowerCase().toCharArray();
    boolean     found = false;

    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (isWhitespace(chars[i])) {
        found = false;
      }
    }
    
    return String.valueOf(chars);
  }
  
  /**
   * Checks the given text against field constraints.
   * @param text The text to be checked.
   * @return {@code true} if the text is valid.
   */
  private boolean checkText(String text) {
    int         end   = 0;

    end = textToModel(text, width, Integer.MAX_VALUE, fixedNewLine).length();
    return end <= width * height;
  }
  
  /**
   * Replaces new-lines by blanks
   *
   * @param     source  the source text with carriage return
   * @param     col     the width of the text
   * @param     fixed   is it a fixed text ?
   */
  public static String textToModel(String source, int col, int lin, boolean fixed) {
    StringBuffer        target = new StringBuffer();
    int                 length = source.length();
    int                 start = 0;
    int                 lines = 0;
    
    while (start < length && lines < lin) {
      int       index;
      
      index = source.indexOf('\n', start);
      if (index == -1) {
        target.append(source.substring(start, length));
        start = length;
      } else {
        target.append(source.substring(start, index));
        if (fixed) {
          for (int i = (index - start); i < col; i++) {
            target.append(' ');
          }
        } else {
          for (int i = (index - start)%col; i != 0 && i < col; i++) {
            target.append(' ');
          }
        }
        start = index + 1;
        lines++;
      }
    }
    
    return target.toString();
  }
  
  /**
   * Checks if the given character is a white space.
   * The implementation is picked from java implementation
   * since GWT does not contains the implementation of {@link Character#isWhitespace(char)}
   * @param c The concerned character.
   * @return {@code true} if the character is whitespace.
   */
  public static boolean isWhitespace(char c) {
    return c == ' ' 
      || c == '\u00A0' // SPACE_SEPARATOR
      || c == '\u2007'   // LINE_SEPARATOR
      || c == '\u202F'   // PARAGRAPH_SEPARATOR
      || c == '\u0009'   // HORIZONTAL TABULATION.
      || c == '\n'       // LINE FEED.
      || c == '\u000B'   // VERTICAL TABULATION.
      || c == '\u000C'   // FORM FEED.
      || c == '\r'       // CARRIAGE RETURN.
      || c == '\u001C'   // FILE SEPARATOR.
      || c == '\u001D'   // GROUP SEPARATOR.
      || c == '\u001E'   // RECORD SEPARATOR.
      || c == '\u001F';  // UNIT SEPARATOR.
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final int                     width;
  private final int                     height;
  private final boolean                 fixedNewLine;
  private final ConvertType             convertType;
}
