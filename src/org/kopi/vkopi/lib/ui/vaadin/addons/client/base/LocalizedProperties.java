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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import java.util.HashMap;

/**
 * Localized properties.
 */
public class LocalizedProperties {
  
  //---------------------------------------------------
  // LOCALIZED STRINGS
  //---------------------------------------------------
  
  /**
   * Returns the localized value of the given key.
   * @param locale The value locale.
   * @param key The value key.
   * @return The localized value of the given key.
   */
  public static String getString(String locale, String key) {
    return properties.get(locale).get(key);
  }
  
  /**
   * Properties initialization.
   */
  private static void initProperties() {
    properties.put("fr_FR", getFrenchProperties());
    properties.put("en_GB", getEnglishProperties());
    properties.put("de_AT", getGermanProperties());
    properties.put("ar_TN", getArabicProperties());
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getFrenchProperties() {
    HashMap<String, String>	properties;
    
    properties = new HashMap<String, String>();
    properties.put("OK", "Oui");
    properties.put("CLOSE", "Fermer");
    properties.put("CANCEL", "Annuler");
    properties.put("NO", "Non");
    properties.put("position-number", "Numéro de position");
    properties.put("TO", "à");
    properties.put("welcomeText", "Bienvenue");
    properties.put("windowsText", "Fenêtre");
    properties.put("informationText", "Veuillez saisir votre nom d'utilisateur et votre mot de passe.");
    properties.put("usernameLabel", "Nom d'utilisateur:");
    properties.put("passwordLabel", "Mot de passe:");
    properties.put("languageLabel", "Langue:");
    properties.put("loginText", "Connexion");
    properties.put("logoutText", "Déconnexion");
    properties.put("admin", "Admin");
    properties.put("support", "Support");
    properties.put("help", "Aide");
    properties.put("about", " A propos");
    properties.put("BROWSE", "Parcourir");
    properties.put("UPLOAD", "Télécharger");
    properties.put("UPTITLE", "Choisir un fichier");
    properties.put("UPHELP", "Cliquez sur Parcourir pour choisir un fichier de votre ordinateur");
    properties.put("Error", "Erreur");
    properties.put("Warning", "Attention");
    properties.put("Notice", "Information");
    properties.put("Question", "Question");
    // modifiers
    properties.put("alt", "Alt");
    properties.put("control", "Ctrl");
    properties.put("meta", "Méta");
    properties.put("shift", "Maj");
    // key codes
    properties.put("enter", "Entrer");
    properties.put("pgdn", "Page suivante");
    properties.put("pgup", "Page précédente");
    properties.put("home", "Origine");
    properties.put("end", "Fin");
    properties.put("escape", "Echappe");
    // actors menu tooltip
    properties.put("actorsMenuHelp", "Affiche le menu associé à ce formulaire");
    return properties;
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getEnglishProperties() {
    HashMap<String, String>	properties;

    properties = new HashMap<String, String>();
    properties.put("OK", "Yes");
    properties.put("CLOSE", "Close");
    properties.put("CANCEL", "Cancel");
    properties.put("NO", "No");
    properties.put("position-number", "Position number");
    properties.put("TO", "to");
    properties.put("welcomeText", "Welcome to");
    properties.put("windowsText", "Window");
    properties.put("informationText", "Please enter your user name and password.");
    properties.put("usernameLabel", "User Name:");
    properties.put("passwordLabel", "Password:");
    properties.put("languageLabel", "Language:");
    properties.put("loginText", "Log In");
    properties.put("logoutText", "Log Out");
    properties.put("admin", "Admin");
    properties.put("support", "Support");
    properties.put("help", "Help");
    properties.put("about", " About");
    properties.put("BROWSE", "browse");
    properties.put("UPLOAD", "Upload");
    properties.put("UPTITLE", "Choose a file");
    properties.put("UPHELP", "Click Browse to choose a file from your computer");
    properties.put("Error", "Error");
    properties.put("Warning", "Warning");
    properties.put("Notice", "Notice");
    properties.put("Question", "Question");
    // modifiers
    properties.put("alt", "Alt");
    properties.put("control", "Ctrl");
    properties.put("meta", "Meta");
    properties.put("shift", "Shift");
    // key codes
    properties.put("enter", "Enter");
    properties.put("pgdn", "Page down");
    properties.put("pgup", "Page up");
    properties.put("home", "Home");
    properties.put("end", "End");
    properties.put("escape", "Escape");
    // actors menu tooltip
    properties.put("actorsMenuHelp", "Displays the menu associated with this form");
    
    return properties;
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getGermanProperties() {
    HashMap<String, String>	properties;

    properties = new HashMap<String, String>();
    properties.put("OK", "Ja");
    properties.put("CLOSE", "Schließen");
    properties.put("CANCEL", "Abbrechen");
    properties.put("NO", "Nein");
    properties.put("position-number", "Positionszahl");
    properties.put("TO", "von");
    properties.put("welcomeText", "Willkommen");
    properties.put("windowsText", "Fenster");
    properties.put("informationText", "Bitte geben Sie Ihren Benutzer und Ihr kennwort ein.");
    properties.put("usernameLabel", "Benutzer:");
    properties.put("passwordLabel", "Kennwort:");
    properties.put("languageLabel", "Sprache:");
    properties.put("loginText", "Einloggen");
    properties.put("logoutText", "Austragen");
    properties.put("admin", "Verwaltung");
    properties.put("support", "Unterstützung");
    properties.put("help", "Hilfe");
    properties.put("about", " Über");
    properties.put("BROWSE", "Blättern");
    properties.put("UPLOAD", "Hochladen");
    properties.put("UPTITLE", "Datei auswählen");
    properties.put("UPHELP", "Klicken Sie auf Durchsuchen, um eine Datei von Ihrem Computer zu wählen");
    properties.put("Error", "Fehler");
    properties.put("Warning", "Warnung");
    properties.put("Notice", "Achtung");
    properties.put("Question", "Frage");
    // modifiers
    properties.put("alt", "Alt");
    properties.put("control", "Strg");
    properties.put("meta", "Meta");
    properties.put("shift", "Umschalt");
    // key codes
    properties.put("enter", "Eingabe");
    properties.put("pgdn", "Bild ab");
    properties.put("pgup", "Bild auf");
    properties.put("home", "Pos 1");
    properties.put("end", "Ende");
    properties.put("escape", "ESC");
    // actors menu tooltip
    properties.put("actorsMenuHelp", "Zeigt das Menü mit diesem Formular verknüpft");
    
    return properties;
  }
  
  /**
   * Properties initialization.
   */
  private static HashMap<String, String> getArabicProperties() {
    HashMap<String, String>	properties;

    properties = new HashMap<String, String>();
    properties.put("OK", "نعم");
    properties.put("CLOSE", "غلق");
    properties.put("CANCEL", "الغاء");
    properties.put("NO", "لا");
    properties.put("position-number", "رقم الموضع");
    properties.put("TO", "إلى");
    properties.put("welcomeText", "مرحباً");
    properties.put("windowsText", "نافذة");
    properties.put("informationText", "الرجاء إدخال إسم المستخدم وكلمة السر.");
    properties.put("usernameLabel", "إسم المستخدم:");
    properties.put("passwordLabel", "كلمة السر:");
    properties.put("languageLabel", "الغة:");
    properties.put("loginText", "تسجيل الدخول");
    properties.put("logoutText", "تسجيل الخروج");
    properties.put("admin", "الإدارة");
    properties.put("support", "دعم");
    properties.put("help", "مساعدة");
    properties.put("about", " حول");
    properties.put("BROWSE", "تصفح");
    properties.put("UPLOAD", "تحميل");
    properties.put("UPTITLE", " اختيار ملف");
    properties.put("UPHELP", "انقر على تصفح لاختيار ملف من جهاز الكمبيوتر الخاص بك");
    properties.put("Error", "خطأ");
    properties.put("Warning", "انتباه");
    properties.put("Notice", "معلومة");
    properties.put("Question", "سؤال");
    //!!! not translated
    // modifiers
    properties.put("alt", "Alt");
    properties.put("control", "Ctrl");
    properties.put("meta", "Meta");
    properties.put("shift", "Shift");
    // key codes
    properties.put("enter", "Enter");
    properties.put("pgdn", "Page down");
    properties.put("pgup", "Page up");
    properties.put("home", "Home");
    properties.put("end", "End");
    properties.put("escape", "Escape");
    // actors menu tooltip
    properties.put("actorsMenuHelp", "يعرض القائمة المقترنة مع هذه النافذة");
    
    return properties;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static HashMap<String, HashMap<String, String>>	properties;
  
  static {
    properties = new HashMap<String, HashMap<String,String>>();
    initProperties();
  }
}
