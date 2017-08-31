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

package org.kopi.xkopi.lib.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.kopi.util.base.InconsistencyException;

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
    ArrayList   arguments = new ArrayList();
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
          arguments.add(arg);
        }
      } while (input.charAt(position) != ')');

      position += 1;    // skip closing parenthesis
      position = skipBlanks(position);
      if (position == length || input.charAt(position) != '}') {
        throw new SQLException("closing brace expected");
      }
    }

    position += 1;      // skip closing brace

    return " " + translateFunctionCall(functor, arguments);
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

  /**
   * Translates a function call in JDBC syntax to native syntax
   *
   * @param     function        the name of the function
   * @param     arguments       the arguments to the function
   * @return    a string representing the function call in native syntax
   *            or null no specific translation is defined for the function
   */
  private String translateFunctionCall(String functor, ArrayList arguments)
    throws SQLException
  {
    Integer     function = (Integer)functions.get(functor.toUpperCase() + "/" + arguments.size());

    if (function == null) {
      if (arguments.size() == 0) {
        return functor;
      } else {
        StringBuffer    buffer = new StringBuffer();

        buffer.append(functor);
        buffer.append('(');
        for (int i = 0; i < arguments.size(); i++) {
          if (i != 0) {
            buffer.append(", ");
          }
          buffer.append((String)arguments.get(i));
        }
        buffer.append(')');

        return buffer.toString();
      }
    } else {
      switch (function.intValue()) {
      case 1:   // TOMONTH/1
        return translateTomonth((String)arguments.get(0));
      case 2:   // ADD_DAYS/2
        return translateAddDays((String)arguments.get(0), (String)arguments.get(1));
      case 3:   // ADD_YEARS/2
        return translateAddYears((String)arguments.get(0), (String)arguments.get(1));
      case 4:   // MONTH/2
        return translateMonth((String)arguments.get(0), (String)arguments.get(1));
      case 5:   // EXTRACT/2
        return translateExtract((String)arguments.get(0), (String)arguments.get(1));
      case 6:   // STRPOS/2
        return translateStrpos((String)arguments.get(0), (String)arguments.get(1));
      case 7:   // POSITION/2
        return translatePosition((String)arguments.get(0), (String)arguments.get(1));
      case 8:   // SUBSTRING/2
        return translateSubstring((String)arguments.get(0), (String)arguments.get(1));
      case 9:   // SUBSTRING/3
        return translateSubstring((String)arguments.get(0), (String)arguments.get(1), (String)arguments.get(2));
      case 11:  // UPPER/1
        return translateUpper((String)arguments.get(0));
      case 12:  // LOWER/1
        return translateLower((String)arguments.get(0));
      case 13:  // LTRIM/1
        return translateLtrim((String)arguments.get(0));
      case 14:  // RTRIM/1
        return translateRtrim((String)arguments.get(0));
      case 15:  // REPLACE/3
        return translateReplace((String)arguments.get(0), (String)arguments.get(1), (String)arguments.get(2));
      case 16:  // REPEAT/2
        return translateRepeat((String)arguments.get(0), (String)arguments.get(1));
      case 17:  // CURRENTDATE/0
        return translateCurrentdate();
      case 18:  // USER/0
        return translateUser();
      case 19:  // SPACE/1
        return translateSpace((String)arguments.get(0));
      case 20:  // RIGHT/2
        return translateRight((String)arguments.get(0), (String)arguments.get(1));
      case 21:  // LEFT/2
        return translateLeft((String)arguments.get(0), (String)arguments.get(1));
      case 22:  // CONCAT/2
        return translateConcat((String)arguments.get(0), (String)arguments.get(1));
      case 23:  // LOCATE/2
        return translateLocate((String)arguments.get(0), (String)arguments.get(1));
      case 24:  // TRUE/0
        return translateTrue();
      case 25:  // FALSE/0
        return translateFalse();
      case 26:  // LENGTH/1
        return translateLength((String)arguments.get(0));
      case 27:  // CURRENTTIME/0
        return translateCurrenttime();
      case 28:  // CURRENTTIMESTAMP/0
        return translateCurrenttimestamp();
      case 29:  // WEEK/2
        return translateWeek((String)arguments.get(0), (String)arguments.get(1));
      case 30:  // DATEDIFF/2
        return translateDatediff((String)arguments.get(0), (String)arguments.get(1));
      case 31:  // COALESCE/2
        return translateCoalesce((String)arguments.get(0), (String)arguments.get(1));
      case 32:  // ROWNO/0
        return translateRowno();
      case 33:  // STRING2INT/1
        return translateString2int((String)arguments.get(0));
      case 34:  // MOD/2
        return translateMod((String)arguments.get(0), (String)arguments.get(1));
      case 35:  // LAST_DAY/1
        return translateLastDay((String)arguments.get(0));
      case 36:  // CAST/2
        return translateCast((String)arguments.get(0), (String)arguments.get(1));
      case 37:  // NEXTVAL/1
        return translateNextval((String)arguments.get(0));
      case 38:  // UPPER/1
        return translateUpper((String)arguments.get(0));
      case 39:  // ROWID/0
        return translateRowid();
      case 40:  // ROWID/1
        return translateRowid((String)arguments.get(0));
      case 41:  // TRUNC_DATE/1
        return translateTruncDate((String)arguments.get(0));
      case 42:  // TO_CHAR/1
        return translateToChar((String)arguments.get(0));
      case 43:  // TO_CHAR/2
        return translateToChar((String)arguments.get(0), (String)arguments.get(1));
      case 44:  // TO_DATE/1
        return translateToDate((String)arguments.get(0));
      case 45:  // TO_DATE/2
        return translateToDate((String)arguments.get(0), (String)arguments.get(1));
      case 46:  // LPAD/2
        return translateLpad((String)arguments.get(0), (String)arguments.get(1));
      case 47:  // RPAD/2
        return translateRpad((String)arguments.get(0), (String)arguments.get(1));
      case 48:  // LPAD/3
        return translateLpad((String)arguments.get(0), (String)arguments.get(1), (String)arguments.get(2));
      case 49:  // RPAD/3
        return translateRpad((String)arguments.get(0), (String)arguments.get(1), (String)arguments.get(2));
      case 51:  // INSTR/2
        return translateInstr((String)arguments.get(0), (String)arguments.get(1));
      case 53:  // TO_NUMBER/1
        return translateToNumber((String)arguments.get(0));
      case 54:  // ROWIDEXT/0
        return translateRowidext();
      case 55:  // ROWIDEXT/1
        return translateRowidext((String)arguments.get(0));
      case 56:  // TRUNC/2
        return translateTrunc((String)arguments.get(0), (String)arguments.get(1));
      case 57:  // INT2STRING/1
        return translateInt2string((String)arguments.get(0));
      case 58:  // GREATEST/2
        return translateGreatest((String)arguments.get(0), (String)arguments.get(1));
      case 59:  // ROUND/2
        return translateRound((String)arguments.get(0), (String)arguments.get(1));
      default:
        throw new InconsistencyException("INTERNAL ERROR: UNDEFINED CONVERSION FOR "
                                         + functor.toUpperCase() + "/" + arguments.size());
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Checks whether an SQL function with specified name and arity is known
   * by the translator.
   */
  public static boolean functionKnown(String name, int arity) {
    return functions.get(name.toUpperCase() + "/" + arity) != null;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS WHICH MAY BE IMPLEMENTED BY SUB-CLASSES
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

  // ----------------------------------------------------------------------
  // METHODS WHICH MUST BE IMPLEMENTED BY SUB-CLASSES
  // ----------------------------------------------------------------------

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_DAYS/2: Adds a specified number of days to a given valid character
   * string representation of a date.
   */
  protected abstract String translateAddDays(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ADD_YEARS/2:Adds a specified number of years to a given valid character
   * string representation of a date.
   */
  protected abstract String translateAddYears(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CAST/2: Converts a value from one data type to a given target type.
   */
  protected abstract String translateCast(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * COALESCE/2: Returns the first non-null expression of the two given
   * expression
   */
  protected abstract String translateCoalesce(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CONCAT/2: Returns a string that is the result of concatenating two string
   * values.
   */
  protected abstract String translateConcat(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTDATE/0: Returns the current date in the connected user time zone.
   */
  protected abstract String translateCurrentdate() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIME/0: Returns the current database system time as a datetime
   * value.
   */
  protected abstract String translateCurrenttime() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * CURRENTTIMESTAMP/0: Returns the current database system timestamp as a
   * datetime value.
   */
  protected abstract String translateCurrenttimestamp() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * DATEDIFF/2: Calculates the difference between two given dates in days.
   */
  protected abstract String translateDatediff(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * EXTRACT/2: Extracts and returns the value of a specified datetime field
   * from a datetime.
   */
  protected abstract String translateExtract(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * FALSE/0: Returns the boolean false value.
   */
  protected abstract String translateFalse() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * GREATEST/2: Returns the greatest of the given list of two expressions.
   */
  protected abstract String translateGreatest(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INSTR/2: Returns the position of the first occurrence of a substring in a
   * host string.
   */
  protected abstract String translateInstr(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * INT2STRING/1: Converts a given number to a value of string datatype.
   */
  protected abstract String translateInt2string(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LAST_DAY/1: Returns the date of the last day of the month that contains
   * the given date.
   */
  protected abstract String translateLastDay(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LENGTH/1: Returns the length of a given string.
   */
  protected abstract String translateLength(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LEFT/2: Returns the left part of a character string with the specified
   * number of characters.
   */
  protected abstract String translateLeft(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOCATE/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected abstract String translateLocate(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LOWER/1: Converts all characters in the specified string to lowercase.
   */
  protected abstract String translateLower(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/2: Returns an expression, left-padded to a given length with blanks
   * or, when the expression to be padded is longer than the length specified
   * after padding, only that portion of the expression that fits into the
   * specified length.
   */
  protected abstract String translateLpad(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LPAD/3: Returns an expression, left-padded to a given length with a given
   * string or, when the expression to be padded is longer than the length
   * specified after padding, only that portion of the expression that fits
   * into the specified length.
   */
  protected abstract String translateLpad(String arg1, String arg2, String arg3) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * LTRIM/1: Removes all blanks from the left end of a given string.
   */
  protected abstract String translateLtrim(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MOD/2: Returns the remainder of the second argument divided by the first
   * argument.
   */
  protected abstract String translateMod(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * MONTH/2: Converts the given year and month (YYYY and MM respectively) into
   * an integer representing the combined month in the form YYYYMM.
   */
  protected abstract String translateMonth(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * NEXTVAL/1: Increments the given sequence and returns its new current
   * value.
   */
  protected abstract String translateNextval(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * POSITION/2: Returns the position of the first occurrence of a substring in
   * a host string.
   */
  protected abstract String translatePosition(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPEAT/2: Returns a string by repeating a given count times a given
   * expression.
   */
  protected abstract String translateRepeat(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * REPLACE/3: Returns a string with every occurrence of a given searched
   * string replaced with given replacement string
   */
  protected abstract String translateReplace(String arg1, String arg2, String arg3) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RIGHT/2: Returns the right part of a character string with the specified
   * number of characters.
   */
  protected abstract String translateRight(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROUND/2: Returns the number given as first argument rounded to the number of digits given
   * as second argument.
   */
  protected abstract String translateRound(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/0: Returns the address or the ID of the row.
   */
  protected abstract String translateRowid() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWID/1: Returns the address or the ID of the row in a given table.
   */
  protected abstract String translateRowid(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/0: Returns the address or the ID of the row.
   */
  protected abstract String translateRowidext() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWIDEXT/1: Returns the address or the ID of the row of a given external
   * table.
   */
  protected abstract String translateRowidext(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * ROWNO/0: Returns the sequential number of a row within a partition of a
   * result set, starting at 1 for the first row in each partition.
   */
  protected abstract String translateRowno() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RPAD/2: Returns an expression, right-padded to a given length with blanks
   * or, when the expression to be padded is longer than the length specified
   * after padding, only that portion of the expression that fits into the
   * specified length.
   */
  protected abstract String translateRpad(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RPAD/3: Returns an expression, right-padded to a given length with a given
   * string or, when the expression to be padded is longer than the length
   * specified after padding, only that portion of the expression that fits
   * into the specified length.
   */
  protected abstract String translateRpad(String arg1, String arg2, String arg3) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * RTRIM/1: Removes all blanks from the right end of a given string.
   */
  protected abstract String translateRtrim(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SPACE/1: Returns a given number of spaces.
   */
  protected abstract String translateSpace(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRING2INT/1: Converts the given string number into an integer.
   */
  protected abstract String translateString2int(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * STRPOS/2: Returns the position of the first occurrence of a substring in a
   *  host string.
   */
  protected abstract String translateStrpos(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SUBSTRING/2: Returns a portion of a given string beginning at a given
   * position
   */
  protected abstract String translateSubstring(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * SUBSTRING/3: Returns a portion of a given string beginning at a given
   * position having the given length (third parameter).
   */
  protected abstract String translateSubstring(String arg1, String arg2, String arg3) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/1: Converts a given number to a value of string datatype.
   */
  protected abstract String translateToChar(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_CHAR/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected abstract String translateToChar(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/1: Converts a given datetime into a date format.
   */
  protected abstract String translateToDate(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_DATE/2: Converts a given valid character string representation of a
   * date into a given format and returns a date in database date format.
   */
  protected abstract String translateToDate(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TOMONTH/1: Returns the month (as integer) of a date in the form YYYYMM,
   * the argument must be a valid character string representation of a date in
   * the form YYYY-MM-DD.
   */
  protected abstract String translateTomonth(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TO_NUMBER/1: Converts a given expression to a value of Integer type.
   */
  protected abstract String translateToNumber(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC/2: Returns the first given number truncated to second number decimal
   * places.
   */
  protected abstract String translateTrunc(String arg1, String arg2) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUNC_DATE/1: returns date with the time portion of the day truncated.
   * The value returned is always of database date type.
   */
  protected abstract String translateTruncDate(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * TRUE/0: Returns the boolean true value.
   */
  protected abstract String translateTrue() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * UPPER/1: Converts all characters in the specified string to uppercase.
   */
  protected abstract String translateUpper(String arg1) throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * USER/0:Returns the name of the current user.
   */
  protected abstract String translateUser() throws SQLException;

  /**
   * Translates the following SQL function to the dialect of this DBMS:
   * WEEK/2: Returns the week number (from 1 to 53) for a given date.
   */
  protected abstract String translateWeek(String arg1, String arg2) throws SQLException;

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

  private static Hashtable              functions;

  // ----------------------------------------------------------------------
  // STATIC INITIALIZER
  // ----------------------------------------------------------------------

  static {
    functions = new Hashtable();

    functions.put("TOMONTH/1", new Integer(1));
    functions.put("ADD_DAYS/2", new Integer(2));
    functions.put("ADD_YEARS/2", new Integer(3));
    functions.put("MONTH/2", new Integer(4));
    functions.put("EXTRACT/2", new Integer(5));
    functions.put("STRPOS/2", new Integer(6));
    functions.put("POSITION/2", new Integer(7));
    functions.put("SUBSTRING/2", new Integer(8));
    functions.put("SUBSTRING/3", new Integer(9));
    functions.put("UPPER/1", new Integer(11));
    functions.put("LOWER/1", new Integer(12));
    functions.put("LTRIM/1", new Integer(13));
    functions.put("RTRIM/1", new Integer(14));
    functions.put("REPLACE/3", new Integer(15));
    functions.put("REPEAT/2", new Integer(16));
    functions.put("CURRENTDATE/0", new Integer(17));
    functions.put("USER/0", new Integer(18));
    functions.put("SPACE/1", new Integer(19));
    functions.put("RIGHT/2", new Integer(20));
    functions.put("LEFT/2", new Integer(21));
    functions.put("CONCAT/2", new Integer(22));
    functions.put("LOCATE/2", new Integer(23));
    functions.put("TRUE/0", new Integer(24));
    functions.put("FALSE/0", new Integer(25));
    functions.put("LENGTH/1", new Integer(26));
    functions.put("CURRENTTIME/0", new Integer(27));
    functions.put("CURRENTTIMESTAMP/0", new Integer(28));
    functions.put("WEEK/2", new Integer(29));
    functions.put("DATEDIFF/2", new Integer(30));
    functions.put("COALESCE/2", new Integer(31));
    functions.put("ROWNO/0", new Integer(32));
    functions.put("STRING2INT/1", new Integer(33));
    functions.put("MOD/2", new Integer(34));
    functions.put("LAST_DAY/1", new Integer(35));
    functions.put("CAST/2", new Integer(36));
    functions.put("NEXTVAL/1", new Integer(37));
    functions.put("UPPER/1", new Integer(38));
    functions.put("ROWID/0", new Integer(39));
    functions.put("ROWID/1", new Integer(40));
    functions.put("TRUNC_DATE/1", new Integer(41));
    functions.put("TO_CHAR/1", new Integer(42));
    functions.put("TO_CHAR/2", new Integer(43));
    functions.put("TO_DATE/1", new Integer(44));
    functions.put("TO_DATE/2", new Integer(45));
    functions.put("LPAD/2", new Integer(46));
    functions.put("RPAD/2", new Integer(47));
    functions.put("LPAD/3", new Integer(48));
    functions.put("RPAD/3", new Integer(49));
    functions.put("INSTR/2", new Integer(51));
    functions.put("TO_NUMBER/1", new Integer(53));
    functions.put("ROWIDEXT/0", new Integer(54));
    functions.put("ROWIDEXT/1", new Integer(55));
    functions.put("TRUNC/2", new Integer(56));
    functions.put("INT2STRING/1", new Integer(57));
    functions.put("GREATEST/2", new Integer(58));
    functions.put("ROUND/2", new Integer(59));
  }

  private String        input;          // the string to parse
  private int           length;         // its length
  private int           position;       // the current position

  private String        parsed;         // the result of parsing.
  private String        cursorName;
}
