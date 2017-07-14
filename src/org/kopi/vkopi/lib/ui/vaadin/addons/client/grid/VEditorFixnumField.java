/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.DecimalFormatSymbols;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;

/**
 * A decimal field for grid editor 
 */
public class VEditorFixnumField extends VEditorTextField {
  
  @Override
  public EditorFixnumFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorFixnumFieldConnector.class);
  }
  
  @Override
  public void onKeyPress(KeyPressEvent event) {
    super.onKeyPress(event);
    if (event.getCharCode() == '.') {
      dotPressed = true;
    }
  }
  
  @Override
  public void onKeyUp(KeyUpEvent event) {
    super.onKeyUp(event);
    // if period press is detected in key press event
    // we try to replace the decimal separator.
    if (dotPressed) {
      // look if the decimal separator must be changed.
      maybeReplaceDecimalSeparator();
      dotPressed = false;
    }
  }
  
  @Override
  protected boolean check(String text) {
    for (int i = 0; i < text.length(); i++) {
      char      c = text.charAt(i);
      
      if (!((c >= '0' && c <= '9') || c == '.' || c == '-' || c == ' ' || c == ',' || c == '/')) {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    BigDecimal          value;

    try {
      value = toBigDecimal(getValue());
    } catch (NumberFormatException e) {
      throw new InvalidEditorFieldException(this, "00006");
    }

    if (value != null) {
      if (value.scale() > scale) {
        throw new InvalidEditorFieldException(this, "00011", maxScale);
      }
      
      if (minValue != null && value.compareTo(new BigDecimal(minValue)) < 0) {
        throw new InvalidEditorFieldException(this, "00012", minValue);
      }
      if (maxValue != null && value.compareTo(new BigDecimal(maxValue)) > 0) {
        throw new InvalidEditorFieldException(this, "00009", maxValue);
      }
      if (toText(value.setScale(maxScale)).length() > getMaxLength()) {
        throw new InvalidEditorFieldException(this, "00010");
      }
    }
    setText(toText(value.setScale(scale)));
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }

  /**
   * @param minValue the minValue to set
   */
  public void setMinValue(Double minValue) {
    this.minValue = minValue;
  }

  /**
   * @param maxValue the maxValue to set
   */
  public void setMaxValue(Double maxValue) {
    this.maxValue = maxValue;
  }
  
  /**
   * @param scale the scale to set
   */
  public void setScale(int scale) {
    this.scale = scale;
  }

  /**
   * @param maxScale the maxScale to set
   */
  public void setMaxScale(int maxScale) {
    this.maxScale = maxScale;
  }

  /**
   * @param fraction the fraction to set
   */
  public void setFraction(boolean fraction) {
    this.fraction = fraction;
  }
  
  /**
   * Checks if the decimal separator must be changed.
   */
  protected void maybeReplaceDecimalSeparator() {
    if (getText().contains(".")) {
      DecimalFormatSymbols      dfs = DecimalFormatSymbols.get(VMainWindow.getLocale());
      
      if (dfs.getDecimalSeparator() != '.') {
        super.setText(getText().replace('.', dfs.getDecimalSeparator()));
      }
    }
  }

  /**
   * Parses the string argument as a big decimal number in human-readable format.
   * @param str The string to be scanned.
   */
  private static BigDecimal toBigDecimal(String str) {
    boolean     negative = false;
    int         state = 0;
    int         scale = 0;
    long        value = 0;
    long        num = 0;
    long        den = 0;

    if (str.equals("")) {
      return null;
    }

    for (int i = 0; i < str.length(); i++) {
      // skip dots
      if (str.charAt(i) == '.') {
        continue;
      }

      switch (state) {
      case 0:         // start state
        if (str.charAt(i) == ' ') {
          state = 0;
        } else if (str.charAt(i) == '+') {
          state = 1;
        } else if (str.charAt(i) == '-') {
          negative = true;
          state = 1;
        } else if (str.charAt(i) == ',') {
          state = 3;
        } else if (Character.isDigit(str.charAt(i))) {
          value = Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 1:         // after initial sign
        if (str.charAt(i) == ' ') {
          state = 1;
        } else if (str.charAt(i) == ',') {
          state = 3;
        } else if (Character.isDigit(str.charAt(i))) {
          value = Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 2:         // after digit before comma
        if (str.charAt(i) == ',') {
          state = 3;
        } else if (str.charAt(i) == ' ') {
          state = 4;
        } else if (str.charAt(i) == '/') {
          num = value;
          value = 0;
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          value = 10 * value + Character.digit(str.charAt(i), 10);
          state = 2;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 3:         // after comma
        if (Character.isDigit(str.charAt(i))) {
          value = 10 * value + Character.digit(str.charAt(i), 10);
          scale += 1;
          state = 3;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 4:         // before numerator of fractional part
        if (str.charAt(i) == ' ') {
          state = 4;
        } else if (Character.isDigit(str.charAt(i))) {
          num = Character.digit(str.charAt(i), 10);
          state = 5;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 5:         // in numerator of fractional part
        if (str.charAt(i) == '/') {
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          num = 10 * num + Character.digit(str.charAt(i), 10);
          state = 5;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 6:         // before denominator of fractional part
        if (str.charAt(i) == '0') {
          state = 6;
        } else if (Character.isDigit(str.charAt(i))) {
          den = Character.digit(str.charAt(i), 10);
          state = 7;
        } else {
          throw new NumberFormatException();
        }
        break;

      case 7:         // in denominator of fractional part
        if (Character.isDigit(str.charAt(i))) {
          den = 10 * den + Character.digit(str.charAt(i), 10);
          state = 7;
        } else {
          throw new NumberFormatException();
        }
        break;

      default:
        throw new RuntimeException();
      }
    }

    switch (state) {
    case 0:         // start state
      return null;

    case 2:         // after digit before comma
      break;

    case 3:         // after comma
      // remove trailing zeroes after comma
      while (scale > 0 && value % 10 == 0) {
        value /= 10;
        scale -= 1;
      }
      break;

    case 7:         // in denominator of fractional part
      if (num > den || num % 2 == 0 || den > 64) {
        throw new NumberFormatException();
      }

      switch ((int)den) {
      case 2:
        value = 10 * value + 5 * num;
        scale = 1;
        break;

      case 4:
        value = 100 * value + 25 * num;
        scale = 2;
        break;

      case 8:
        value = 1000 * value + 125 * num;
        scale = 3;
        break;

      case 16:
        value = 10000 * value + 625 * num;
        scale = 4;
        break;

      case 32:
        value = 100000 * value + 3125 * num;
        scale = 5;
        break;

      case 64:
        value = 1000000 * value + 15625 * num;
        scale = 6;
        break;

      default:
        throw new NumberFormatException();
      }
      break;

    default:
      throw new NumberFormatException();
    }

    if (value == 0) {
      return BigDecimal.ZERO;
    } else {
      if (negative) {
        value = -value;
      }
      return new BigDecimal(BigInteger.valueOf(value), scale);
    }
  }

  /**
   * Returns the
   * @param value
   * @return
   */
  private String toString(BigDecimal value) {
    String              str = value.toString();
    StringBuffer        buf = new StringBuffer();
    int                 pos = 0;
    int                 dot;

    // has minus sign ?
    if (str.charAt(0) == '-') {
      buf.append('-');
      pos = 1;
    }

    // get number of digits in front of the dot
    if ((dot = str.indexOf('.')) == -1) {
      if ((dot = str.indexOf(' ')) == -1) {
        dot = str.length();
      }
    }

    if (dot - pos <= 3) {
      buf.append(str.substring(pos, dot));
      pos = dot;
    } else {
      switch ((dot - pos) % 3) {
      case 1:
        buf.append(str.substring(pos, pos + 1));
        pos += 1;
        break;
      case 2:
        buf.append(str.substring(pos, pos + 2));
        pos += 2;
        break;
      case 0:
        buf.append(str.substring(pos, pos + 3));
        pos += 3;
        break;
      }

      do {
        buf.append(".").append(str.substring(pos, pos + 3));
        pos += 3;
      }
      while (dot - pos > 0);
    }

    if (str.length() > pos) {
      buf.append(",").append(str.substring(pos + 1));
    }

    return buf.toString();
  }

  /**
   * Returns the string representation in human-readable format.
   * @return The string representation in human-readable format.
   */
  public String toText(BigDecimal v) {
    if (!fraction) {
      return toString(v);
    } else {
      return toFraction(toString(v));
    }
  }

  /**
   * Returns the fraction representation of the given string.
   * @param str The string to calculate its fraction.
   * @return The calculated fraction.
   */
  private String toFraction(String str) {
    int         dot;

    if ((dot = str.indexOf(',')) == -1) {
      return str;
    }
    String precomma = str.substring(0, dot);
    int fract = Integer.valueOf(str.substring(dot + 1, str.length())).intValue();

    if (fract * 64 % 1000000 != 0) {
      return str;
    } else if (fract == 0) {
      return precomma;
    } else {
      int num, den;

      den = 64;
      num = (fract * den) / 1000000;
      while (num % 2 == 0) {
        num /= 2;
        den /= 2;
      }

      if (precomma.equals("0")) {
        return "" + num + "/" + den;
      } else if (precomma.equals("-0")) {
        return "-" + num + "/" + den;
      } else {
        return precomma + " " + num + "/" + den;
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private Double                        minValue;
  private Double                        maxValue;
  private int                           scale;
  private int                           maxScale;
  private boolean                       fraction;
  private boolean                       dotPressed;
}
