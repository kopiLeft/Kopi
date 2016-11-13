/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.util;

import java.text.BreakIterator;

public class LineBreaker extends org.kopi.util.base.Utils {

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   * @param	fixed	is it a fixed text ?
   */
  public static String textToModel(String source, int col, int lin) {
    return textToModel(source, col, lin, false);
  }
  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   * @param	fixed	is it a fixed text ?
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
   * Replaces blanks by new-lines
   *
   * @param	source		the source text with white space
   * @param	col		the width of the text area
   */
  public static String modelToText(String source, int col) {
    if (source != null) {
      StringBuffer      target = new StringBuffer();
      int               length = source.length();

      for (int start = 0; start < length; start += col) {
        String  line = source.substring(start, Math.min(start + col, length));
        int     last = -1;

        for (int i = line.length() - 1; last == -1 && i >= 0; --i) {
          if (! Character.isWhitespace(line.charAt(i))) {
            last = i;
          }
        }

        if (start != 0) {
          target.append('\n');
        }
        if (last != -1) {
          target.append(line.substring(0, last + 1));
        }
      }

      return target.toString();
    } else {
      return "";
    }
  }

  public static String addBreakForWidth(String source, int width) {
    BreakIterator       boundary;
    int                 start;
    int                 length;
    StringBuffer        buffer;

    if (source == null) {
      source = "";
    }
    boundary = BreakIterator.getLineInstance();
    boundary.setText(source);
    start = boundary.first();
    length = 0;
    buffer = new StringBuffer(source.length());
    
    for (int end = boundary.next();
         end != BreakIterator.DONE;
         start = end, end = boundary.next()) {
      length += (end - start);
      if (length > width) {
        length = (end - start);
        buffer.append("\n");
      }
      buffer.append(source.substring(start, end));
      if (source.substring(start, end).endsWith("\n")) {
        length = 0;
      }
    }

    return buffer.toString();
  }

  /**
   * Splits specified string into an array of strings where each element
   * represents a single line fitting the specified width (except if a
   * single word is longer than the specified width.
   */
  public static String[] splitForWidth(String source, int width) {
    return addBreakForWidth(source, width).split("\n");
  }
}
