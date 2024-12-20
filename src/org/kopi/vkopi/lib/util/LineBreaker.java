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
   * @param     source  the source text with carriage return
   * @param     col     the width of the text
   */
  public static String textToModel(String source, int col) {
    return textToModel(source, col, Integer.MAX_VALUE);
  }

  /**
   * Replaces new-lines by blanks
   *
   * @param     source  the source text with carriage return
   * @param     col     the width of the text
   * @param     lin     the height of the text
   */
  public static String textToModel(String source, int col, int lin) {
    StringBuilder result = new StringBuilder();
    int length = source.length();
    int start = 0;
    int addedSpaces = 0;
    int lines = 0;

    while (start < length && lines < lin) {
      String line;
      int newlineIndex = source.indexOf('\n', start); // Find the next newline or the maximum segment length
      int wrapEnd; // Determine the end of the current line segment
      // Calculate the end of the current segment : can be one of 3 values : [new line index] or [start + col] or [length]
      int segmentEnd = newlineIndex >= start && newlineIndex <= start + col ? newlineIndex : Math.min(start + col, length);
      // Check to remove exceeding whitespaces if found
      boolean startsWithBlank = result.length() > 0 &&
                                Character.isWhitespace(result.charAt(result.length() - 1)) &&
                                Character.isWhitespace(source.charAt(start));

      // If the current segment starts with a blank, remove the said blank form the segment
      segmentEnd = (startsWithBlank && segmentEnd < length) ? segmentEnd + 1 : segmentEnd;
      start = startsWithBlank ? start + 1 : start;

      // Calculate the wrap index : index of the last space to prevent word cutting
      if (segmentEnd < length && segmentEnd != newlineIndex) {
        int lastSpace = source.lastIndexOf(' ', start + col);

        wrapEnd = lastSpace >= start && lastSpace < segmentEnd ? lastSpace : segmentEnd;
      } else {
        wrapEnd = segmentEnd;
      }
      line = source.substring(start, wrapEnd);
      // Append the segment.
      result.append(line);
      if (isBlank(line)) {
        // Reset [addedSpaces] : if a blank line exists, there is no need to carry blank spaces
        addedSpaces = 0;
      }
      // Handle newline replacement and padding
      if (wrapEnd == newlineIndex) {
        if (wrapEnd - start == col && wrapEnd < length - 1 && !Character.isWhitespace(source.charAt(wrapEnd + 1))) {
          // If the segment ends with a new line, and the next segment does not end with a white space, add the missing space
          result.append(' ');
          addedSpaces++;
        } else {
          // If the segment ends with a new line, pad segment with spaces to reach length = [col]
          int padding = col - (wrapEnd - start);

          if (!isBlank(line) && addedSpaces > 0 && padding > addedSpaces) {
            result.append(repeat(' ', padding - addedSpaces)); // Pad the line
          } else {
            result.append(repeat(' ', padding)); // Pad the line
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

  /**
   * Repeat a character
   *
   * @param     ch      The repeated character
   * @param     count   The number of repeated times
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
   * @param     source  the source text with white space
   * @param     col     the width of the text area
   */
  public static String modelToText(String source, int col) {
    return modelToText(source, col, Integer.MAX_VALUE);
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param     source  the source text with white space
   * @param     col     the width of the text area
   * @param     row     the width of the text area
   *
   * @return            formatted text
   */
  public static String modelToText(String source, int col, int row) {
    StringBuilder result = new StringBuilder();
    int length = source.length();
    int usedRows = 0;
    int start = 0;

    while (start < length && usedRows < row) {
      // Extract a line of the given column width
      int end = Math.min(start + col, length);
      String line = source.substring(start, end);

      // Trim leading and trailing spaces from the line and append to the result
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
   * Check if a string is blank
   */
  private static boolean isBlank(String source) {
    boolean isBlank = true;

    for (int i = 0; i < source.length(); i++) {
      isBlank = isBlank && Character.isWhitespace(source.charAt(i));
    }
    return isBlank;
  }

  /**
   * Splits specified string into an array of strings where each element
   * represents a single line fitting the specified width (except if a
   * single word is longer than the specified width).
   */
  public static String[] splitForWidth(String source, int width) {
    return addBreakForWidth(source, width).split("\n");
  }
}
