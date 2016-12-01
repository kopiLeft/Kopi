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

/**
 * Localized messages. Each message has it own unique key.
 */
public class LocalizedMessages {
  
  //---------------------------------------------------
  // LOCALIZED MESSAGES
  //---------------------------------------------------
  
  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @return The localized value of the given key.
   */
  public static String getMessage(String locale, String key) {
    return format(messages.get(locale).get(key));
  }
  
  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @param params The message parameters.
   * @return The localized value of the given key.
   */
  public static String getMessage(String locale, String key, Object... params) {
    return format(messages.get(locale).get(key), params);
  }
  
  /**
   * Properties initialization.
   */
  private static void initProperties() {
    messages.put("fr_FR", getFrenchMessages());
    messages.put("en_GB", getEnglishMessages());
    messages.put("de_AT", getGermanMessages());
    messages.put("ar_TN", getArabicMessages());
  }
  
  /**
   * Messages initialization.
   */
  private static HashMap<String, String> getFrenchMessages() {
    HashMap<String, String>     messages;
    
    messages = new HashMap<String, String>();
    messages.put("00001", "Erreur de saisie: Aucune valeur appropriée.");
    messages.put("00002", "Erreur de saisie: Valeurs multiples.");
    messages.put("00003", "Erreur de saisie: Ce n''est pas une date valide.");
    messages.put("00004", "Erreur de saisie: Ce n''est pas un entier.");
    messages.put("00005", "Erreur de saisie: Ce n''est pas un mois valide.");
    messages.put("00006", "Erreur de saisie: Ce nombre n''est pas valide.");
    messages.put("00007", "Erreur de saisie: Ce n''est pas une heure valide.");
    messages.put("00008", "Erreur de saisie: Ce n''est pas une semaine valide.");
    messages.put("00009", "Erreur de saisie: Ce nombre est trop grand (max {0}).");
    messages.put("00010", "Erreur de saisie: Ce nombre est trop grand.");
    messages.put("00011", "Erreur de saisie: Trop de chiffres après la virgule (max. {0}).");
    messages.put("00012", "Ce nombre est trop petit (min {0}).");
    messages.put("00013", "Erreur de saisie: texte trop long.");
    messages.put("00015", "Pas (plus) de données.");
    messages.put("00016", "Aucune valeur appropriée dans {0}.");
    messages.put("00017", "La valeur de l''enregistrement a changé.");
    messages.put("00018", "L''enregistrement a été éffacé.");
    messages.put("00019", "Cet enregistrement ne peut pas etre effacé.");
    messages.put("00022", "Aucune valeur appropriée trouvée.");
    messages.put("00023", "Ce champ doit être rempli.");
    messages.put("00024", "Cette page n'est pas accessible.");
    messages.put("00025", "Commande non autorisée.");
    
    return messages;
  }
  
  /**
   * Messages initialization.
   */
  private static HashMap<String, String> getEnglishMessages() {
    HashMap<String, String>     messages;

    messages = new HashMap<String, String>();
    messages.put("00001", "Data entry error: No matching value.");
    messages.put("00002", "Data entry error: More than one matching value.");
    messages.put("00003", "Data entry error: This is not a valid date value.");
    messages.put("00004", "Data entry error: This is not an integer.");
    messages.put("00005", "Data entry error: This is not a valid month value.");
    messages.put("00006", "Data entry error: This in not a number.");
    messages.put("00007", "Data entry error: This is not a valid time value.");
    messages.put("00008", "Data entry error: This is not a valid week value.");
    messages.put("00009", "Data entry error: This number is to big (max {0}).");
    messages.put("00010", "Data entry error: This number is to big");
    messages.put("00011", "Data entry error: Too many digit after comma (max. {0}).");
    messages.put("00012", "Data entry error: This number is to small (min. {0}).");
    messages.put("00013", "Data entry error: This text is too long.");
    messages.put("00015", "No (more) data.");
    messages.put("00016", "No matching value in {0}");
    messages.put("00017", "The value of the record has changed.");
    messages.put("00018", "The record was deleted.");
    messages.put("00019", "This record can''t be deleted.");
    messages.put("00022", "No matching value found.");
    messages.put("00023", "This field must be filled.");
    messages.put("00024", "This page is not accessible.");
    messages.put("00025", "Command not accessible.");
    
    return messages;
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getGermanMessages() {
    HashMap<String, String>     messages;

    messages = new HashMap<String, String>();
    messages.put("00001", "Eingabefehler: kein entsprechender Wert.");
    messages.put("00002", "Eingabefehler: Eingabe nicht eindeutig.");
    messages.put("00003", "Eingabefehler: Eingabe ist kein gültiges Datum.");
    messages.put("00004", "Eingabefehler: Eingabe ist nicht ganzzahlig.");
    messages.put("00005", "Eingabefehler: Eingabe ist kein gültiges Monat.");
    messages.put("00006", "Eingabefehler: Formatfehler bei Eingabe.");
    messages.put("00007", "Eingabefehler: Eingabe ist keine gültige Uhrzeit.");
    messages.put("00008", "Eingabefehler: Eingabe ist keine gültige Woche.");
    messages.put("00009", "Eingabefehler: Wert ist zu groß (max {0}).");
    messages.put("00010", "Eingabefehler: Wert ist zu groß");
    messages.put("00011", "Eingabefehler: zu viele Nachkommastellen (max {0}).");
    messages.put("00012", "Eingabefehler: Wert ist zu klein (min {0}).");
    messages.put("00013", "Eingabefehler: Text ist zu lang.");
    messages.put("00015", "Keine (weiteren) Datensätze.");
    messages.put("00016", "Kein entsprechender Eintrag in {0}");
    messages.put("00017", "Datensatz wurde in der Zwischenzeit verändert.");
    messages.put("00018", "Datensatz wurde in der Zwischenzeit gelöscht.");
    messages.put("00019", "Dieser Datensatz kann nicht gelöscht werden.");
    messages.put("00022", "Kein entsprechender Eintrag gefunden.");
    messages.put("00023", "Dieses Feld muß gefüllt werden.");
    messages.put("00024", "Auf diese Seite kann nicht zugegriffen werden.");
    messages.put("00025", "Kommando nicht erlaubt.");
    
    return messages;
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getArabicMessages() {
    HashMap<String, String>     messages;

    messages = new HashMap<String, String>();
    messages.put("00001", "خطأ تسجيل : لا توجد أية قيمة مناسبة");
    messages.put("00002", "خطأ تسجيل : القيم متعددة");
    messages.put("00003", "خطأ تسجيل : هذا التاريخ غير صحيح");
    messages.put("00004", "خطأ تسجيل : انه ليس بعدد صحيح");
    messages.put("00005", "خطأ تسجيل : هذا الشهر غير صحيح");
    messages.put("00006", "خطأ تسجيل :هذا العددغير صحيح");
    messages.put("00007", "خطأ تسجيل :هذه الساعة غير صحيحة");
    messages.put("00008", "خطأ تسجبل : هذا الاأسبوع غير صحيح");
    messages.put("00009", "خطأ تسجيل : هذا  الرقم كبير جدا (الأقصى{0})");
    messages.put("00010", "خطأ تسجيل : هذا االرقم كبير جدا.");
    messages.put("00011", "خطأ تسجيل :الأرقام كثيرة بعد الفاصلة(الأقصى{0})");
    messages.put("00012", "هذا العدد صغير جدا(الأدنى {0})");
    messages.put("00013", "خطأ تسجيل : النص طويل جدا");
    messages.put("00015", "لا يوجد (أكثر) بيانات");
    messages.put("00016", "لا توجد القيمة مناسبة في {0}.");
    messages.put("00017", "قيمة التسجيل تغيرت");
    messages.put("00018", "وقع شطب  التسجيل.");
    messages.put("00019", "لا يمكن  شطب  هذا التسجيل");
    messages.put("00022", "لا توجد أية قيمة مناسبة");
    messages.put("00023", "يجب ملء هذا الموقع.");
    messages.put("00024", "لا يمكن الدخول الى هذه الصفحة");
    messages.put("00025", "الطلبية  غير مسموح بها.");
    
    return messages;
  }
  
  /**
   * A replacement for java.text.MessageFormat.format().
   * @param format The text to be formatted.
   * @param args The format parameters.
   * @return The formatted string.
   */
  private static String format(final String format, final Object... args) {
    StringBuilder       sb = new StringBuilder();
    int                 cur = 0;
    int                 len = format.length();
    
    while (cur < len) {
      int       fi = format.indexOf('{', cur);
      
      if (fi != -1) {
        sb.append(format.substring(cur, fi));
        int     si = format.indexOf('}', fi);
        
        if (si != -1) {
          String        nStr = format.substring(fi + 1, si);
          int           i = Integer.parseInt(nStr);
          
          sb.append(args[i]);
          cur = si + 1;
        } else {
          sb.append(format.substring(fi));
          break;
        }
      } else {
        sb.append(format.substring(cur, len));
        break;
      }
    }
    
    return sb.toString();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static HashMap<String, HashMap<String, String>>       messages;
  
  static {
    messages = new HashMap<String, HashMap<String,String>>();
    initProperties();
  }
}
