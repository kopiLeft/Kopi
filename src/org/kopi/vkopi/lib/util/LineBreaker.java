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
   * @deprecated Replace with the use of textToModel(String, int, int)
   */
  public static String textToModel(String source, int col, int lin, boolean fixed) {
    // Deprecated method : the parameter [fixed] is not used.
    return textToModel(source, col, lin);
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   */
  public static String textToModel(String source, int col) {
    return textToModel(source, col, Integer.MAX_VALUE);
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param	source	the source text with carriage return
   * @param	col	the width of the text
   * @param	lin	the height of the text
   */
  public static String textToModel(String source, int col, int lin) {
    StringBuilder result = new StringBuilder();
    int length = source.length();
    int start = 0;
    int addedSpaces = 0;
    int lines = 0;

    while (start < length && lines < lin) {
      int newlineIndex = source.indexOf('\n', start); // Find the next newline or the maximum segment length
      int wrapEnd; // Determine the end of the current line segment
      int segmentEnd = newlineIndex >= start && newlineIndex <= start + col ? newlineIndex : Math.min(start + col, length);
      boolean startsWithBlank = result.length() > 0 &&
                                Character.isWhitespace(result.charAt(result.length() - 1)) &&
                                Character.isWhitespace(source.charAt(start));

      segmentEnd = startsWithBlank ? segmentEnd + 1 : segmentEnd;
      start = startsWithBlank ? start + 1 : start;
      if (segmentEnd < length && segmentEnd != newlineIndex) {
        int lastSpace = source.lastIndexOf(' ', start + col);

        wrapEnd = lastSpace >= start && lastSpace < segmentEnd ? lastSpace : segmentEnd;
      } else {
        wrapEnd = segmentEnd;
      }
      // Append the segment.
      result.append(source.substring(start, wrapEnd));

      // Handle newline replacement and padding
      if (wrapEnd == newlineIndex) {
        if (wrapEnd - start == col && wrapEnd < length - 1 && !Character.isWhitespace(source.charAt(wrapEnd + 1))) {
          // Replace newline with a space
          result.append(' ');
          addedSpaces++;
        } else {
          if (wrapEnd != length) {
            int padding = col - (wrapEnd - start);

            if (addedSpaces > 0 && padding > addedSpaces) {
              result.append(repeat(' ', padding - addedSpaces)); // Pad the line
            } else {
              result.append(repeat(' ', padding)); // Pad the line
            }
          }
        }
      } else {
        result.append(repeat(' ', col - (wrapEnd - start))); // Pad the line
      }

      // Update the start index
      start = wrapEnd == newlineIndex ? wrapEnd + 1 : wrapEnd;
      lines++;
    }

    return result.toString();
  }

  private static boolean isBlank(String source) {
    boolean isBlank = true;

    for (int i = 0; i < source.length(); i++) {
      isBlank = isBlank && Character.isWhitespace(source.charAt(i));
    }
    return isBlank;
  }

  /**
   * Repeat a character
   *
   * @param ch      The repeated character
   * @param count   The number of repeated times
   *
   * @return        A string of the character 'ch' repeated 'count' times
   */
  private static String repeat(char ch, int count) {
    if (count <= 0) return "";
    StringBuilder builder = new StringBuilder(count);

    for (int i = 0; i < count; i++) {
      builder.append(ch);
    }

    return builder.toString();
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param	source		the source text with white space
   * @param	col		the width of the text area
   */
  public static String modelToText(String source, int col/*, boolean isWhiteSpace*/) {
    return modelToText(source, col, Integer.MAX_VALUE/*, isWhiteSpace*/);
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param source		the source text with white space
   * @param col		the width of the text area
   * @param row		the width of the text area
   * @return
   */
  public static String modelToText(String source, int col, int row/*, boolean isWhiteSpace*/) {
    StringBuilder result = new StringBuilder();
    int length = source.length();
    int usedRows = 0;
    int start = 0;

    while (start < length && usedRows < row) {
      // Extract a line of the given column width
      int end = Math.min(start + col, length);
      String line = source.substring(start, end);

      // Trim trailing spaces from the line and append to the result
      result.append(line.trim());

      // Add a newline if more rows are allowed
      usedRows++;
      if (usedRows < row && end < length) {
        result.append('\n');
      }

      start = end;
    }

    return result.toString();
  }

  public static String addBreakForWidth(String source, int width) {
    BreakIterator       boundary;
    int                 start;
    int                 length;
    StringBuilder       buffer;

    if (source == null) {
      source = "";
    }
    boundary = BreakIterator.getLineInstance();
    boundary.setText(source);
    start = boundary.first();
    length = 0;
    buffer = new StringBuilder(source.length());

    for (int end = boundary.next();
         end != BreakIterator.DONE;
         start = end, end = boundary.next()) {
      length += (end - start);
      if (length > width) {
        length = (end - start);
        buffer.append("\n");
      }
      buffer.append(source, start, end);
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
