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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import java.util.HashMap;
import java.util.Map;

/**
 * java.text.DecimalFormatSymbols hack implementation since java.util.Locale
 * has no JS translation in GWT compiler.
 */
public class DecimalFormatSymbols {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  protected DecimalFormatSymbols(String currencySymbol,
                                 String internationalCurrencySymbol,
                                 char decimalSeparator,
                                 char digit,
                                 String exponentSeparator,
                                 char groupingSeparator,
                                 String infinity,
                                 char minusSign,
                                 char monetaryDecimalSeparator,
                                 String nan,
                                 char patternSeparator,
                                 char percent,
                                 char perMill,
                                 char zeroDigit)
  {
    this.currencySymbol = currencySymbol;
    this.internationalCurrencySymbol = internationalCurrencySymbol;
    this.decimalSeparator = decimalSeparator;
    this.digit = digit;
    this.exponentSeparator = exponentSeparator;
    this.groupingSeparator = groupingSeparator;
    this.infinity = infinity;
    this.minusSign = minusSign;
    this.monetaryDecimalSeparator = monetaryDecimalSeparator;
    this.nan = nan;
    this.patternSeparator = patternSeparator;
    this.percent = percent;
    this.perMill = perMill;
    this.zeroDigit = zeroDigit;
  }
  
  //---------------------------------------------------
  // STATIC ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the decimal symbols instance for a given locale.
   * @param locale The locale string value.
   * @return The deciaml format symbols instance.
   */
  public static DecimalFormatSymbols get(String locale) {
    if (cache.containsKey(locale)) {
      return cache.get(locale);
    } else {
      return DEFAULT;
    }
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof DecimalFormatSymbols)) {
      return false;
    }
    
    DecimalFormatSymbols        dfs = (DecimalFormatSymbols) other;
    
    return nullEquals(currencySymbol, dfs.currencySymbol)
      && nullEquals(internationalCurrencySymbol, dfs.internationalCurrencySymbol)
      && decimalSeparator == dfs.decimalSeparator
      && digit == dfs.digit
      && nullEquals(exponentSeparator, dfs.exponentSeparator)
      && groupingSeparator == dfs.groupingSeparator
      && nullEquals(infinity, dfs.infinity)
      && minusSign == dfs.minusSign
      && monetaryDecimalSeparator == dfs.monetaryDecimalSeparator
      && nullEquals(nan, dfs.nan)
      && patternSeparator == dfs.patternSeparator
      && percent == dfs.percent
      && perMill == dfs.perMill
      && zeroDigit == dfs.zeroDigit;
  }
  
  private static boolean nullEquals(Object obj1, Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

  private static int nullHash(Object obj) {
    return obj == null ? 0 : obj.hashCode();
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * @return the currencySymbol
   */
  public String getCurrencySymbol() {
    return currencySymbol;
  }

  /**
   * @return the decimalSeparator
   */
  public char getDecimalSeparator() {
    return decimalSeparator;
  }

  /**
   * @return the digit
   */
  public char getDigit() {
    return digit;
  }

  /**
   * @return the exponentSeparator
   */
  public String getExponentSeparator() {
    return exponentSeparator;
  }

  /**
   * @return the groupingSeparator
   */
  public char getGroupingSeparator() {
    return groupingSeparator;
  }

  /**
   * @return the infinity
   */
  public String getInfinity() {
    return infinity;
  }

  /**
   * @return the internationalCurrencySymbol
   */
  public String getInternationalCurrencySymbol() {
    return internationalCurrencySymbol;
  }

  /**
   * @return the minusSign
   */
  public char getMinusSign() {
    return minusSign;
  }

  /**
   * @return the monetaryDecimalSeparator
   */
  public char getMonetaryDecimalSeparator() {
    return monetaryDecimalSeparator;
  }

  /**
   * @return the naN
   */
  public String getNaN() {
    return nan;
  }

  /**
   * @return the patternSeparator
   */
  public char getPatternSeparator() {
    return patternSeparator;
  }

  /**
   * @return the percent
   */
  public char getPercent() {
    return percent;
  }

  /**
   * @return the perMill
   */
  public char getPerMill() {
    return perMill;
  }

  /**
   * @return the zeroDigit
   */
  public char getZeroDigit() {
    return zeroDigit;
  }

  @Override
  public int hashCode() {
    return nullHash(currencySymbol) * 3
        + nullHash(internationalCurrencySymbol) * 5
        + decimalSeparator * 7
        + digit * 11
        + nullHash(exponentSeparator) * 13
        + groupingSeparator * 17
        + nullHash(infinity) * 19
        + minusSign * 23
        + monetaryDecimalSeparator * 29
        + nullHash(nan) * 31
        + patternSeparator * 37
        + percent * 41
        + perMill * 43
        + zeroDigit * 53;
  }

  /**
   * @param currencySymbol the currencySymbol to set
   */
  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  /**
   * @param decimalSeparator the decimalSeparator to set
   */
  public void setDecimalSeparator(char decimalSeparator) {
    this.decimalSeparator = decimalSeparator;
  }

  /**
   * @param digit the digit to set
   */
  public void setDigit(char digit) {
    this.digit = digit;
  }

  /**
   * @param exponentSeparator the exponentSeparator to set
   */
  public void setExponentSeparator(String exponentSeparator) {
    this.exponentSeparator = exponentSeparator;
  }

  /**
   * @param groupingSeparator the groupingSeparator to set
   */
  public void setGroupingSeparator(char groupingSeparator) {
    this.groupingSeparator = groupingSeparator;
  }

  /**
   * @param infinity the infinity to set
   */
  public void setInfinity(String infinity) {
    this.infinity = infinity;
  }

  /**
   * @param internationalCurrencySymbol the internationalCurrencySymbol to set
   */
  public void setInternationalCurrencySymbol(String internationalCurrencySymbol) {
    this.internationalCurrencySymbol = internationalCurrencySymbol;
  }

  /**
   * @param minusSign the minusSign to set
   */
  public void setMinusSign(char minusSign) {
    this.minusSign = minusSign;
  }

  /**
   * @param monetaryDecimalSeparator the monetaryDecimalSeparator to set
   */
  public void setMonetaryDecimalSeparator(char monetaryDecimalSeparator) {
    this.monetaryDecimalSeparator = monetaryDecimalSeparator;
  }

  /**
   * @param naN the naN to set
   */
  public void setNaN(String nan) {
    this.nan = nan;
  }

  /**
   * @param patternSeparator the patternSeparator to set
   */
  public void setPatternSeparator(char patternSeparator) {
    this.patternSeparator = patternSeparator;
  }

  /**
   * @param percent the percent to set
   */
  public void setPercent(char percent) {
    this.percent = percent;
  }

  /**
   * @param perMill the perMill to set
   */
  public void setPerMill(char perMill) {
    this.perMill = perMill;
  }

  /**
   * @param zeroDigit the zeroDigit to set
   */
  public void setZeroDigit(char zeroDigit) {
    this.zeroDigit = zeroDigit;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private String                                        currencySymbol;
  private String                                        internationalCurrencySymbol;
  private char                                          decimalSeparator;
  private char                                          digit;
  private String                                        exponentSeparator;
  private char                                          groupingSeparator;
  private String                                        infinity;
  private char                                          minusSign;
  private char                                          monetaryDecimalSeparator;
  private String                                        nan;
  private char                                          patternSeparator;
  private char                                          percent;
  private char                                          perMill;
  private char                                          zeroDigit;
  
  private static final DecimalFormatSymbols             DEFAULT;
  public static final DecimalFormatSymbols              FR;
  public static final DecimalFormatSymbols              AT;
  public static final DecimalFormatSymbols              TN;
  public static final DecimalFormatSymbols              GB;
  private static Map<String, DecimalFormatSymbols>      cache;
  
  static {
    cache = new HashMap<String, DecimalFormatSymbols>();
    FR = new DecimalFormatSymbols("€",
                                  "EUR",
                                  ',',
                                  '#',
                                  "E",
                                  ' ',
                                  "∞",
                                  '-',
                                  ',',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0');
    AT = new DecimalFormatSymbols("€",
                                  "EUR",
                                  ',',
                                  '#',
                                  "E",
                                  '.',
                                  "∞",
                                  '-',
                                  ',',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0');
    GB = new DecimalFormatSymbols("£",
                                  "GBP",
                                  '.',
                                  '#',
                                  "E",
                                  ',',
                                  "∞",
                                  '-',
                                  '.',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0');
    TN = new DecimalFormatSymbols("د.ت.",
                                  "TND",
                                  '.',
                                  '#',
                                  "E",
                                  ',',
                                  "∞",
                                  '-',
                                  '.',
                                  "�",
                                  ';',
                                  '%',
                                  '‰',
                                  '0');
    DEFAULT = AT;
    cache.put("de_AT", AT);
    cache.put("fr_FR", FR);
    cache.put("en_GB", GB);
    cache.put("ar_TN", TN);
  }
}
