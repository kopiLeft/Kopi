/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.lib.base;

import java.sql.SQLException;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class translates SQL queries from Kopi JDBC format to native format.
 */

/*package*/ abstract class JdbcParser {
  /**
   * Constructor
   */
  public JdbcParser(String input) {
    this.input = input;
    this.length = input.length();
    this.position = 0;
    this.parsed = null;
  }

  /**
   * Returns the query in TransBase syntax
   */
  public String getText() throws SQLException {
    if (parsed == null) {
      parsed = parse();
    }
    return parsed;
  }

  /**
   * Returns:
   *    1 ... a select statement
   *    2 ... a positioned update statement
   *    3 ... a positioned delete statement
   *    0 ... otherwise
   */
  public int getType() throws SQLException {
    String      keyword;
    int         beg;

    if (parsed == null) {
      parsed = parse();
    }

    // skip leading blanks
    for (beg = 0; Character.isSpaceChar(parsed.charAt(beg)); beg++) {
      // loop
    }

    keyword = parsed.substring(beg, parsed.indexOf(' ', beg)).toUpperCase();
    if (keyword.equals("SELECT")) {
      return 1;
    } else if (cursorName == null) {
      return 0;
    } else if (keyword.equals("UPDATE")) {
      return 2;
    } else if (keyword.equals("DELETE")) {
      return 3;
    } else {
      return 0; // !!! throw an exception
    }
  }

  /**
   * Returns the cursor name for positioned update or delete statements.
   */
  public String getCursor() {
    return cursorName;
  }


  /*
   * Returns the position of the first occurrence of {, ', " or WHERE
   */
  private int getEscape(int start) {
    int state = -1;

    while (start < length) {
      if (Character.isSpaceChar(input.charAt(start))) {
        if (state == 5) {
          return start - 5;
        } else if (state == 8) {
          return start - 3;
        } else {
          state = 0;
          start += 1;
        }
      } else {
        switch (input.charAt(start)) {
        case '{':
        case '\'':
        case '"':
          return start;
        case 'w':
        case 'W':
          state = state == 0 ? 1 : -1;
          start += 1;
          break;
        case 'h':
        case 'H':
          state = state == 1 ? 2 : -1;
          start += 1;
          break;
        case 'e':
        case 'E':
          if (state == 2) {
            state = 3;
          } else if (state == 4) {
            state = 5;
          } else {
            state = -1;
          }
          start += 1;
          break;
        case 'r':
        case 'R':
          if (state == 3) {
            state = 4;
          } else if (state == 7) {
            state = 8;
          } else {
            state = -1;
          }
          start += 1;
          break;
        case 'f':
        case 'F':
          state = state == 0 ? 6 : -1;
          start += 1;
          break;
        case 'o':
        case 'O':
          state = state == 6 ? 7 : -1;
          start += 1;
          break;
        default:
          state = -1;
          start += 1;
        }
      }
    }

    return -1;
  }


  /*
   * Top-Level
   */
  private String parse() throws SQLException {
    StringBuffer        result = new StringBuffer(length);

    while (position < length) {
      int       escape = getEscape(position);

      if (escape == -1) {
        result.append(input.substring(position, length));
        position = length;
      } else {
        result.append(input.substring(position, escape));
        position = escape;

        switch (input.charAt(position)) {
        case '{':
          result.append(parseBrace());
          break;

        case '\'':
        case '"':
          result.append(parseQuote());
          break;

        case 'w':
        case 'W':
          {
            String      sub = parseWhereCurrent();

            if (sub != null) {
              result.append(sub);
            }
          }
          break;

        case 'f':
        case 'F':
          {
            String      sub = parseForUpdate();

            if (sub != null) {
              result.append(sub);
            }
          }
          break;

        default:
          throw new InconsistencyException();
        }
      }
    }

    return result.toString();
  }

  private String parseBrace() throws SQLException {
    position += 1;      // skip opening brace

    if (input.indexOf("}", position) == -1) {
      throw new SQLException("Closing brace not found");
    }

    position = skipBlanks(position);

    if (input.indexOf("d ", position) == position) {
      return parseDate();
    } else if (input.indexOf("t ", position) == position) {
      return parseTime();
    } else if (input.indexOf("ts ", position) == position) {
      return parseTimestamp();
    } else if (input.indexOf("fn ", position) == position) {
      return parseFunction();
    } else if (input.indexOf("escape ", position) == position) {
      return parseEscape(7); // length of "escape "
    } else if (input.indexOf("oj ", position) == position) {
      return parseEscape(3); // length of  "oj "
    } else {
      throw new SQLException("bad escape syntax: " + input.substring(position));
    }
  }

  private String parseParenthesis() throws SQLException {
    StringBuffer        result = new StringBuffer(length - position);
    int start = position;

    position += 1;      // skip opening parseParenthesis

    while (position < length) {
      switch (input.charAt(position)) {
      case '{':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseBrace());
        start = position;
        break;

      case '\'':
      case '"':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseQuote());
        start = position;
        break;

      case '(':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseParenthesis());
        start = position;
        break;

      case ')':
        position += 1;
        result.append(input.substring(start, position));
        return result.toString();

      default:
        position += 1;
      }
    }

    throw new SQLException("unexpected end-of-string");
  }


  /*
   * Copies input up to matching quote/double quote
   */
  private String parseQuote() throws SQLException {
    char        quoteType = input.charAt(position);
    int start = position;

    position += 1;

loop:
    while (true) {
      if (position == length) {
        throw new SQLException("unexpected end-of-string");
      }

      if (input.charAt(position) != quoteType) {
        position += 1;
      } else {
        position += 1;  // move behind quote

        if (position == length) {
          // last character is a quote: end of quoted string found
          break loop;
        } else if (input.charAt(position) != quoteType) {
          // not a quote: end of quoted string found
          break loop;
        } else {
          // quote repeated => escaped
          position += 1;
        }
      }
    }

    return input.substring(start, position);
  }

  /**
   * Verifies whether the current input starts with "WHERE CURRENT OF".
   *
   *
   */
  protected String parseWhereCurrent() throws SQLException {
    int index = position + 6;   // WHERE + SPACE = 5 + 1

    index = skipBlanks(index);

    if (input.indexOf("CURRENT ", index) != index) {
      return null;
    } else {
      position = index + 7;
      position = skipBlanks(position);
      if (input.indexOf("OF ", position) != position) {
        throw new SQLException("Syntax error in WHERE CURRENT");
      }
      position += 3;
      position = skipBlanks(position);
      cursorName = parseIdentifier();
      position = skipBlanks(position);
      if (position != length) {
        throw new SQLException("Trailing garbage after WHERE CURRENT");
      }

      return " WHERE CURRENT";
    }
  }

  /**
   * Parses FOR UPDATE
   */
  protected String parseForUpdate() throws SQLException {
    int index = position + 4;   // FOR + SPACE = 4 + 1

    index = skipBlanks(index);
    if (input.indexOf("UPDATE", index) != index) {
      return null;
    } else {
      position = index + 6;
      position = skipBlanks(position);
      return " FOR UPDATE";
    }
  }

  private String parseDate() throws SQLException {
    position += 2;      // length("d ")
    position = skipBlanks(position);

    int yy, mo, dd;

    scanCharacter('\'');
    yy = scanInteger(4, 4, 0, 9999);
    scanCharacter('-');
    mo = scanInteger(1, 2, 1, 12);
    scanCharacter('-');
    dd = scanInteger(1, 2, 1, 31);
    scanCharacter('\'');
    position = skipBlanks(position);
    scanCharacter('}');

    return " " + convertDate(yy, mo, dd);
  }

  private String parseTime() throws SQLException {
    position += 2;      // length("t ")
    position = skipBlanks(position);

    int hh, mi, ss;

    scanCharacter('\'');
    hh = scanInteger(1, 2, 0, 23);
    scanCharacter(':');
    mi = scanInteger(1, 2, 0, 59);
    scanCharacter(':');
    ss = scanInteger(1, 2, 0, 59);
    scanCharacter('\'');
    position = skipBlanks(position);
    scanCharacter('}');

    return " " + convertTime(hh, mi, ss);
  }

  private String parseTimestamp() throws SQLException {
    position += 3;      // length("ts ")
    position = skipBlanks(position);

    int yy, mo, dd, hh, mi, ss, ns;

    scanCharacter('\'');
    yy = scanInteger(4, 4, 0, 9999);
    scanCharacter('-');
    mo = scanInteger(1, 2, 1, 12);
    scanCharacter('-');
    dd = scanInteger(1, 2, 1, 31);
    scanCharacter(' ');
    hh = scanInteger(1, 2, 0, 23);
    scanCharacter(':');
    mi = scanInteger(1, 2, 0, 59);
    scanCharacter(':');
    ss = scanInteger(1, 2, 0, 59);

    if (position < length && input.charAt(position) == '.') {
      scanCharacter('.');
      ns = scanInteger(1, 9, 0, 999999);
    } else {
      ns = 0;
    }
    scanCharacter('\'');

    position = skipBlanks(position);
    scanCharacter('}');

    return " " + convertTimestamp(yy, mo, dd, hh, mi, ss, ns);
  }

  private String parseEscape(int escape) throws SQLException {
    position += escape;
    position = skipBlanks(position);

    int start = position;

    position = input.indexOf('}', position);

    if (position == -1) {
      throw new SQLException("closing brace missing");
    }

    return " " + input.substring(start, position++);
  }

  private String parseFunction() throws SQLException {
    position += 3;      // length("fn ")
    position = skipBlanks(position);

    String      functor;
    Vector      arguments = new Vector();
    String      result;

    // parse function name
    functor = parseIdentifier();

    position = skipBlanks(position);

    if (input.charAt(position) != '(') {
      // if the function has no arguments, we found a }, else there is an error
      if (input.charAt(position) != '}') {
        throw new SQLException("opening parenthesis expected");
      }
    } else if (input.charAt(position) != '}') {
      // the function has arguments

      do {
        String  arg = parseArgument();

        if (! arg.equals("")) {
          arguments.addElement(arg);
        }
      } while (input.charAt(position) != ')');

      position += 1;    // skip closing parenthesis
      position = skipBlanks(position);
      if (position == length || input.charAt(position) != '}') {
        throw new SQLException("closing brace expected");
      }
    }

    position += 1;      // skip closing brace

    result = convertFunctionCall(functor, arguments);
    if (result != null) {
      return " " + result;
    } else if (arguments.size() == 0) {
      return " " + functor;
    } else {
      StringBuffer      buffer = new StringBuffer(" ");

      buffer.append(functor);
      buffer.append('(');
      for (int i = 0; i < arguments.size(); i++) {
        if (i != 0) {
          buffer.append(", ");
        }
        buffer.append((String)arguments.elementAt(i));
      }
      buffer.append(')');

      return buffer.toString();
    }
  }

  private String parseIdentifier() throws SQLException {
    int start = position;

    if (position == length) {
      throw new SQLException("unexpected end-of-string");
    }

    if (! (Character.isLetter(input.charAt(position)) || input.charAt(position) == '_')) {
      throw new SQLException("not an identifier");
    }
    position += 1;

    while (position < length &&
           (Character.isLetterOrDigit(input.charAt(position))
            || input.charAt(position) == '_')) {
      position += 1;
    }

    return input.substring(start, position);
  }

  private String parseArgument() throws SQLException {
    position += 1;      // skip opening parenthesis or comma

    position = skipBlanks(position);

    StringBuffer        result = new StringBuffer(length - position);
    int start = position;

    while (position < length) {
      switch (input.charAt(position)) {
      case '{':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseBrace());
        start = position;
        break;

      case '\'':
      case '"':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseQuote());
        start = position;
        break;

      case '(':
        if (start < position) {
          result.append(input.substring(start, position));
        }
        result.append(parseParenthesis());
        start = position;
        break;

      case ',':
      case ')':
        if (start < position) {
          result.append(input.substring(start, position));
          start = position;
        }
        // remove trailing blanks
        for (int i = result.length() - 1; i >= 0; i--) {
          if (! Character.isSpaceChar(result.charAt(i))) {
            break;
          }
          result.setLength(i);
        }
        return result.toString();

      default:
        position += 1;
      }
    }

    throw new SQLException("unexpected end-of-string");
  }

  /**
   * Returns first position after blanks
   */
  private int skipBlanks(int index) {
    while (index < length && Character.isSpaceChar(input.charAt(index))) {
      index += 1;
    }

    return index;
  }

  private int scanInteger(int minDigits, int maxDigits, int minValue, int maxValue)
    throws SQLException
  {
    int value = 0;
    int digits = 0;

    while (position < length
           && input.charAt(position) >= '0'
           && input.charAt(position) <= '9') {
      value = 10*value + input.charAt(position) - '0';
      digits += 1;
      position += 1;
    }

    if (digits < minDigits || digits > maxDigits) {
      throw new SQLException("");
    }
    if (value < minValue || value > maxValue) {
      throw new SQLException("");
    }

    return value;
  }

  private void scanCharacter(char ch) throws SQLException {
    if (position == length) {
      throw new SQLException("");
    }
    if (input.charAt(position) != ch) {
      throw new SQLException("expected " + ch + " but found " + input.charAt(position));
    }
    position += 1;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS WHICH MUST BE IMPLEMENTED BY SUB-CLASSES
  // ----------------------------------------------------------------------

  /**
   * Converts a date into native syntax
   * @param     yy      year
   * @param     mo      month
   * @param     dd      day
   * @return    a string representing the date in native syntax
   */
  protected String convertDate(int yy, int mo, int dd) {
    return "{d '" + format(yy, 4) + "-" + format(mo, 2) + "-" + format(dd, 2) + "'}";
  }

  /**
   * Converts a time into native syntax
   * @param     hh      hour
   * @param     mi      minute
   * @param     ss      second
   * @return    a string representing the time in native syntax
   */
  protected String convertTime(int hh, int mi, int ss) {
    return "{t '" + format(hh, 2) + ":" + format(mi, 2) + ":" + format(ss, 2) + "'}";
  }


  /**
   * Converts a date into native syntax
   * @param     yy      year
   * @param     mo      month
   * @param     dd      day
   * @param     hh      hour
   * @param     mi      minute
   * @param     ss      second
   * @param     ns      nanosecond
   * @return    a string representing the date in native syntax
   */
  protected String convertTimestamp(int yy,
                                    int mo,
                                    int dd,
                                    int hh,
                                    int mi,
                                    int ss,
                                    int ns)
  {
    return "{ts '"
      + format(yy, 4) + "-" + format(mo, 2) + "-" + format(dd, 2)
      + " "
      + format(hh, 2) + ":" + format(mi, 2) + ":" + format(ss, 2) + "." + ns
      + "'}";
  }


  /**
   * Converts a function call in JDBC syntax to native syntax
   *
   * @param     function        the name of the function
   * @param     arguments       the arguments to the function
   * @return    a string representing the function call in native syntax
   *            or null no specific translation is defined for the function
   */
  protected abstract String convertFunctionCall(String functor,
                                                Vector arguments)
    throws SQLException;

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Creates a string representation for an integer
   * with specified number of digits (using leading 0s)
   */
  public static String format(int value, int digits) {
    char[]      buffer = new char[digits];

    while (digits > 0) {
      buffer[digits - 1] = (char)('0' + (value % 10));
      value /= 10;
      digits -= 1;
    }

    return new String(buffer);
  }

  /**
   * Creates a string representation for a long
   * with specified number of digits (using leading 0s)
   */
  public static String format(long value, int digits) {
    char[]      buffer = new char[digits];

    while (digits > 0) {
      buffer[digits - 1] = (char)('0' + (value % 10));
      value /= 10;
      digits -= 1;
    }

    return new String(buffer);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String        input;          // the string to parse
  private int           length;         // its length
  private int           position;       // the current position

  private String        parsed;         // the result of parsing.
  private String        cursorName;
}
