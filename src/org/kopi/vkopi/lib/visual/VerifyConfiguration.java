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

package org.kopi.vkopi.lib.visual;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.kopi.util.mailer.Mailer;

public class VerifyConfiguration {

  private VerifyConfiguration() {
  }

  public void verifyConfiguration(String smtpServer, String failureRecipient, String applicationName) {
    StringWriter	buffer = new StringWriter();
    PrintWriter         writer = new PrintWriter(buffer);
    boolean             configurationError = false;
    String              hostname;
    // get Hostname
    try {
      InetAddress     inetAdress = InetAddress.getLocalHost();

      hostname = inetAdress.getCanonicalHostName();
      writer.println(formatMessage("Getting hostname ", false));
    } catch (UnknownHostException e) {
      hostname = "unkown";
      writer.println(formatMessage("Getting hostname ", true));
      e.printStackTrace(writer);
      writer.println();
      configurationError = true;
    }

    // check that -ea is on 
    boolean     isAssertOn = false;

    assert(isAssertOn = true);
    writer.println(formatMessage("java called with option -ea", !isAssertOn));
    configurationError = configurationError || !isAssertOn;

    // check tmp-directory (exists, writable)
    // !!! todo

    // check print-server (exits)
    // !!! todo

    // check fax-server (exits)
    // !!! todo

    // check mail-server
    // NOT useful (how to mail this error?)

    // check gs-server (works)
    // !!! todo

    // check aspell-server (works)
    // !!! todo

    if (configurationError) {
      Mailer.sendMail(smtpServer,
                      failureRecipient,        // recipient
		      null,                             // cc
		      null,                             // bcc
                      "[KOPI CONFIGURATION] " 
                      + applicationName 
                      + " " 
                      + System.getProperty("user.name", "")
                      + "@"
                      + hostname,
                      buffer.toString(),
                      "kopi@kopiright.com");
    }
  } 


  private static String formatMessage(String message, boolean fail) {
    String      result;

    if (message.length() >= 70) {
      result = message.substring(0, 70);
    } else {
      result = message + STR_BASIC.substring(0, 70 - message.length());
    }

    if (fail) {
      return result + STR_FAILED;
    } else {
      return result + STR_OK;
    }
  }

  public static VerifyConfiguration getVerifyConfiguration() {
    return configurationChecker;
  }
  
  private static  VerifyConfiguration   configurationChecker = new VerifyConfiguration();

  private static final String           STR_BASIC
    = "   ........................................................................";
  private static final String           STR_OK = " [OK]";
  private static final String           STR_FAILED = " [FAILED]";
}


