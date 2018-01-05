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
package org.kopi.vkopi.lib.base;

import java.text.ChoiceFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * An extended implementation of the message format that allows to
 * handle a specific syntax of choice format :
 *
 * {argument-number <b>?</b>, choice, 0#<b>null case</b> | 1#<b>not null case</b>}
 *
 * The question marks says if the argument should be tested on null value. If yes
 * the {@link ExtendedChoiceFormat} implementation guarantees to return 1 when the
 * object is not null and 0 if the object is null.
 *
 * The parse strategy was copied from the org.apache.commons.text.ExtendedMessageFormat.
 * The implementation was adapted to our needs.
 *
 * It is safer to call new {@link #formatMessage(String, Object[])} method instead of calling
 * {@link #format(Object)} method. behavior is not ensured.
 */
public final class ExtendedMessageFormat extends MessageFormat {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  public ExtendedMessageFormat(String pattern) {
    super(pattern);
  }

  public ExtendedMessageFormat(String pattern, Locale locale) {
    super(pattern, locale);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Creates a MessageFormat with the given pattern and uses it
   * to format the given arguments. This is equivalent to
   * <blockquote>
   *     <code>(new {@link #MessageFormat(String) MessageFormat}(pattern)).{@link #format(java.lang.Object[], java.lang.StringBuffer, java.text.FieldPosition) format}(arguments, new StringBuffer(), null).toString()</code>
   * </blockquote>
   *
   * @param pattern   the pattern string
   * @param arguments object(s) to format
   * @return the formatted string
   * @exception IllegalArgumentException if the pattern is invalid,
   *            or if an argument in the <code>arguments</code> array
   *            is not of the type expected by the format element(s)
   *            that use it.
   */
  public static String formatMessage(String pattern, Object[] arguments) {
    ExtendedMessageFormat temp = new ExtendedMessageFormat(pattern);

    return temp.formatMessage(arguments);
  }


  /**
   * Formats an object to produce a string. This is equivalent to
   * <blockquote>
   * {@link #format(Object, StringBuffer, FieldPosition) format}<code>(obj,
   *         new StringBuffer(), new FieldPosition(0)).toString();</code>
   * </blockquote>
   *
   * @param obj    The object to format
   * @return       Formatted string.
   * @exception IllegalArgumentException if the Format cannot format the given
   *            object
   */
  public final String formatMessage(Object[] obj) {
    return format(toNullRepresentation(obj)).toString().replaceAll("null", "");
  }

  /**
   * Transforms the internal elements of the given array to their
   * null representation if the element is null.
   * @param obj The source object array.
   * @return The transformed object array or null if the source object
   *         is already null.
   */
  protected Object[] toNullRepresentation(Object[] obj) {
    Object[]            newObjects;

    if (obj == null) {
      newObjects = null;
    } else {
      newObjects = new Object[obj.length];

      // replace null by null representation object to allow
      // format execution since this behavior cannot be hacked
      for (int i = 0; i < newObjects.length; i++) {
        if (obj[i] == null) {
          newObjects[i] = NULL_REPRESENTATION;
        } else {
          newObjects[i] = obj[i];
        }
      }
    }

    return newObjects;
  }

  /*
   * (non-Javadoc)
   * @see java.text.MessageFormat#applyPattern(java.lang.String)
   */
  public final void applyPattern(String pattern) {
    final List<FormatDescription>       descriptions;
    final StringBuilder                 customPattern;
    final ParsePosition                 position;
    final char[]                        patternChars;
    int                                 fmtCount;

    descriptions = new ArrayList<FormatDescription>();
    customPattern = new StringBuilder(pattern.length());
    position = new ParsePosition(0);
    patternChars = pattern.toCharArray();
    fmtCount = 0;

    while (position.getIndex() < pattern.length()) {
      switch (patternChars[position.getIndex()]) {
      case QUOTE:
        appendQuotedString(pattern, position, customPattern);
        break;
      case START_FE:
        final ArgumentInfo      argumentInfo;
        final int               start;
        final int               index;
        final String            formatDescription;

        fmtCount++;
        seekNonWs(pattern, position);
        start = position.getIndex();
        argumentInfo = readArgumentInfo(pattern, next(position));
        index = argumentInfo.index;
        customPattern.append(START_FE).append(index);
        seekNonWs(pattern, position);
        if (patternChars[position.getIndex()] == START_FMT) {
          formatDescription = parseFormatDescription(pattern, next(position));
          customPattern.append(START_FMT).append(formatDescription);
        } else {
          formatDescription = null;
        }
        descriptions.add(new FormatDescription(formatDescription, argumentInfo));
        if (descriptions.size() != fmtCount) {
          throw new IllegalArgumentException("The validated expression is false");
        }
        if (patternChars[position.getIndex()] != END_FE) {
          throw new IllegalArgumentException("Unreadable format element at position " + start);
        }
        //$FALL-THROUGH$
      default:
        customPattern.append(patternChars[position.getIndex()]);
        next(position);
      }
    }
    super.applyPattern(customPattern.toString().replaceAll("''", "'"));
    // use extended choice format to handle specific use
    useExtendedChoiceFormat(descriptions);
  }

  /**
   * Forces to use extended choice format to handle null test in a standard
   * choice format pattern.
   */
  protected void useExtendedChoiceFormat(List<FormatDescription> formatDescriptions) {
    final Format[]      newFormats = new Format[getFormats().length];

    for (int i = 0; i < getFormats().length; i++) {
      if (getFormats()[i] instanceof ChoiceFormat) {
        String                  pattern;
        FormatDescription       description;

        pattern = ((ChoiceFormat)getFormats()[i]).toPattern();
        description = getChoiceFormatDescription(pattern, formatDescriptions);
        // use extended choice format instead
        newFormats[i] = new ExtendedChoiceFormat(pattern, description != null ? description.info.hasNotNullMarker : false);
      } else {
        newFormats[i] = getFormats()[i];
      }
    }
    setFormats(newFormats);
  }

  /**
   * Read the argument index from the current format element.
   *
   * @param pattern pattern to parse
   * @param pos current parse position
   * @return argument index
   */
  private ArgumentInfo readArgumentInfo(final String pattern, final ParsePosition pos) {
    final int           start;
    final StringBuilder result;
    boolean             error;
    boolean             hasNotNullMarker;

    start = pos.getIndex();
    seekNonWs(pattern, pos);
    result = new StringBuilder();
    error = false;
    hasNotNullMarker = false;
    for (; !error && pos.getIndex() < pattern.length(); next(pos)) {
      char c = pattern.charAt(pos.getIndex());
      if (Character.isWhitespace(c)) {
        seekNonWs(pattern, pos);
        c = pattern.charAt(pos.getIndex());
        if (c == NOT_NULL_MARKER) {
          hasNotNullMarker = true;
          continue;
        }
        if (c != START_FMT && c != END_FE) {
          error = true;
          continue;
        }
      }
      if (c == NOT_NULL_MARKER) {
        hasNotNullMarker = true;
        continue;
      }
      if ((c == START_FMT || c == END_FE) && result.length() > 0) {
        try {
          return new ArgumentInfo(Integer.parseInt(result.toString()), hasNotNullMarker);
        } catch (final NumberFormatException e) { // NOPMD
          // we've already ensured only digits, so unless something
          // outlandishly large was specified we should be okay.
        }
      }
      error = !Character.isDigit(c);
      result.append(c);
    }
    if (error) {
      throw new IllegalArgumentException("Invalid format argument index at position " + start + ": "
        + pattern.substring(start, pos.getIndex()));
    }
    throw new IllegalArgumentException("Unterminated format element at position " + start);
  }

  /**
   * Parse the format component of a format element.
   *
   * @param pattern string to parse
   * @param pos current parse position
   * @return Format description String
   */
  private String parseFormatDescription(final String pattern, final ParsePosition pos) {
    final int           start;
    final int           text;
    int                 depth;

    start = pos.getIndex();
    seekNonWs(pattern, pos);
    text = pos.getIndex();
    depth = 1;
    while (pos.getIndex() < pattern.length()) {
      switch (pattern.charAt(pos.getIndex())) {
      case START_FE:
        depth++;
        next(pos);
        break;
      case END_FE:
        depth--;
        if (depth == 0) {
          return pattern.substring(text, pos.getIndex());
        }
        next(pos);
        break;
      case QUOTE:
        getQuotedString(pattern, pos);
        break;
      default:
        next(pos);
        break;
      }
    }
    throw new IllegalArgumentException("Unterminated format element at position " + start);
  }

  /**
   * Consume whitespace from the current parse position.
   *
   * @param pattern String to read
   * @param pos current position
   */
  private void seekNonWs(final String pattern, final ParsePosition pos) {
    int         len = 0;
    final       char[] buffer = pattern.toCharArray();

    do {
      len = isWsMatch(buffer, pos.getIndex());
      pos.setIndex(pos.getIndex() + len);
    } while (len > 0 && pos.getIndex() < pattern.length());
  }

  /**
   * Convenience method to advance parse position by 1.
   *
   * @param pos ParsePosition
   * @return <code>pos</code>
   */
  private ParsePosition next(final ParsePosition pos) {
    pos.setIndex(pos.getIndex() + 1);
    return pos;
  }

  /**
   * Consume a quoted string, adding it to <code>appendTo</code> if
   * specified.
   *
   * @param pattern pattern to parse
   * @param pos current parse position
   * @param appendTo optional StringBuilder to append
   * @return <code>appendTo</code>
   */
  private StringBuilder appendQuotedString(final String pattern,
                                           final ParsePosition pos,
                                           final StringBuilder appendTo)
  {
    assert pattern.toCharArray()[pos.getIndex()] == QUOTE : "Quoted string must start with quote character";

    final int           start;
    final char[]        c;
    final int           lastHold;

    // handle quote character at the beginning of the string
    if (appendTo != null) {
      appendTo.append(QUOTE);
    }
    next(pos);

    start = pos.getIndex();
    c = pattern.toCharArray();
    lastHold = start;
    for (int i = pos.getIndex(); i < pattern.length(); i++) {
      switch (c[pos.getIndex()]) {
      case QUOTE:
        next(pos);
        return appendTo == null ? null : appendTo.append(c, lastHold, pos.getIndex() - lastHold);
      default:
        next(pos);
      }
    }
    throw new IllegalArgumentException("Unterminated quoted string at position " + start);
  }

  /**
   * Consume quoted string only.
   *
   * @param pattern pattern to parse
   * @param pos current parse position
   */
  private void getQuotedString(final String pattern, final ParsePosition pos) {
    appendQuotedString(pattern, pos, null);
  }

  /**
   * Returns whether or not the given character matches with whitespace.
   *
   * @param buffer  the text content to match against, do not change
   * @param pos  the starting position for the match, valid for buffer
   * @return the number of matching characters, zero for no match
   */
  private int isWsMatch(final char[] buffer, final int pos) {
    char[]              chars;

    chars = " \t\n\r\f".toCharArray();
    Arrays.sort(chars);

    return Arrays.binarySearch(chars, buffer[pos]) >= 0 ? 1 : 0;
  }


  /**
   * Returns the format description of the given description.
   * @param descrption The searched format description.
   * @param descriptions The list of available format descriptions.
   * @return The format description if found or null if not.
   */
  private FormatDescription getChoiceFormatDescription(String descrption, List<FormatDescription> descriptions) {
    for (FormatDescription fdescription : descriptions) {
      if (fdescription.description == null || !fdescription.description.contains("choice")) {
        continue;
      }

      if (toChoicePattern(fdescription).equals(descrption)) {
        return fdescription;
      }
    }

    return null;
  }

  /**
   * Converts the given choice format description to a choice pattern.
   * @param fdescription The choice format description.
   * @return The choice pattern.
   */
  private String toChoicePattern(FormatDescription fdescription) {
    return ((ChoiceFormat)new MessageFormat("{" + fdescription.info.index + "," +  fdescription.description.replaceAll("''", "'") + "}").getFormats()[0]).toPattern();
  }

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  /*package*/ static class ArgumentInfo {

    public ArgumentInfo(int index, boolean hasNotNullMarker) {
      this.index = index;
      this.hasNotNullMarker = hasNotNullMarker;
    }

    private final int           index;
    private final boolean       hasNotNullMarker;
  }

  /*package*/ static class FormatDescription {

    public FormatDescription(String description, ArgumentInfo info) {
      this.description = description;
      this.info = info;
    }

    private final String                description;
    private final ArgumentInfo          info;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * A comma.
   */
  private static final char             START_FMT = ',';

  /**
   * Not null marker represented as a question marker.
   */
  private static final char             NOT_NULL_MARKER = '?';

  /**
   * A right side squigly brace.
   */
  private static final char             END_FE = '}';

  /**
   * A left side squigly brace.
   */
  private static final char             START_FE = '{';

  /**
   * A properly escaped character representing a single quote.
   */
  private static final char             QUOTE = '\'';
  // null object representation to allow format execution since
  // default behavior can't be hacked.
  /*package*/ final static Object       NULL_REPRESENTATION = new Object();

  /**
   * Generated serial ID.
   */
  private static final long             serialVersionUID = -5290553560430519118L;
}
