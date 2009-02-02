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

package com.kopiright.vkopi.lib.util;

import java.io.IOException;
import java.net.ConnectException;
import java.util.StringTokenizer;
import java.util.Vector;

import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.HylaFAXClientProtocol;
import gnu.inet.ftp.ServerResponseException;

public class HylaFAXUtils {

  // ----------------------------------------------------------------------
  // CONVENIENCE METHODS TO SEND FAX, GET QUEUE STATUS, ...
  // ----------------------------------------------------------------------

  /*
   * ----------------------------------------------------------------------
   * READ THE SEND QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  public static Vector readSendQueue(String host, int port, String user, String password) throws FaxException {
    return readQueue(host, port, user, password, "sendq");
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  public static Vector readDoneQueue(String host, int port, String user, String password) throws FaxException {
    return readQueue(host, port, user, password, "doneq");
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  public static Vector readRecQueue(String host, int port, String user, String password) throws FaxException {
    return readQueue(host, port, user, password, "recvq");
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */

  public static Vector readServerStatus(String host, int port, String user, String password) throws FaxException {
    Vector	status;

    try{
      status = getQueue(host, port, user, password, "status");
      Utils.log("Fax", "READ STATE : host " + host + " / user " + user);
    } catch (ConnectException e) {
      throw new FaxException("NO FAX SERVER");
    } catch (IOException e) {
      throw new FaxException("Trying read server state: " + e.getMessage(), e);
    } catch (ServerResponseException e) {
      throw new FaxException("Trying read server state: " + e.getMessage(), e);
    }

    return status;
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */


  /**
   * Convenience method
   */
  public static void killJob(String host,
                             int port,
			     String user,
                             String password,
			     long job)
    throws FaxException
  {
    try {
      HylaFAXClient       faxClient;
      
      faxClient =  new HylaFAXClient();
      faxClient.open(host);

      if(faxClient.user(user)){
        // need password
        faxClient.pass(password);
      }
      
      faxClient.jkill(job);
      Utils.log("Fax", "Kill 1: " + job);
      faxClient.quit();
    } catch (IOException ioe) {
      throw new FaxException(ioe);
    } catch (ServerResponseException sre) {
      throw new FaxException(sre);
    }
  }

  /**
   * Convenience method
   */
  public static void deleteJob(String host,
                               int port,
                               String user,
                               String password,
                               long job)
    throws FaxException
  {
    try {
      HylaFAXClient       faxClient;
    
      faxClient =  new HylaFAXClient();
      faxClient.open(host);
      
      if(faxClient.user(user)){
        // need password
        faxClient.pass(password);
      }

      faxClient.jdele(job);
      Utils.log("Fax", "Delete 1: " + job);
      faxClient.quit();
    } catch (IOException ioe) {
      throw new FaxException(ioe);
    } catch (ServerResponseException sre) {
      throw new FaxException(sre);
    }
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE QUEUES --- ALL QUEUES ARE HANDLED BY THAT METHOD
   * ----------------------------------------------------------------------
   */
  private static Vector getQueue(String host, int port, String user, String password, String qname)
    throws IOException, ServerResponseException
  {
    HylaFAXClient       faxClient;
    Vector              entries;

    faxClient =  new HylaFAXClient();
    faxClient.open(host);

    if(faxClient.user(user)){
      // need password
      faxClient.pass(password);
    }

    faxClient.tzone(HylaFAXClientProtocol.TZONE_LOCAL);
    faxClient.rcvfmt("%f| %t| %s| %p| %h| %e");
    faxClient.jobfmt("%j| %J| %o| %e| %a| %P| %D| %.25s");
    faxClient.mdmfmt("Modem %m (%n): %s");

    entries = faxClient.getList(qname); 

    faxClient.quit();

    return entries;
  }


  /*
   * ----------------------------------------------------------------------
   * READS ANY QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  private static Vector readQueue(String host, int port, String user, String password, String qname) throws FaxException  {
    Vector	queue = new Vector();

    try {
      Vector		result = getQueue(host, port, user, password, qname);

      Utils.log("Fax", "READ " + qname + " : host " + host + " / user " + user);

      for (int i=0; i < result.size(); i++) {
        try {
          String                str = (String) result.elementAt(i);
	  StringTokenizer       prozess = new StringTokenizer(str, "|");

	  if (!qname.equals("recvq")) {
            queue.addElement(new FaxStatus(prozess.nextToken().trim(),	// ID
                                           prozess.nextToken().trim(),	// TAG
                                           prozess.nextToken().trim(),	// USER
                                           prozess.nextToken().trim(),	// DIALNO
                                           prozess.nextToken().trim(),	// STATE (CODE)
                                           prozess.nextToken().trim(),	// PAGES
                                           prozess.nextToken().trim(),	// DIALS
                                           prozess.nextToken().trim()));	// STATE (TEXT)
	  } else {
            queue.addElement(new FaxStatus(prozess.nextToken().trim(),	// FILENAME %f
                                           prozess.nextToken().trim(),	// TIME IN %t
                                           prozess.nextToken().trim(),	// SENDER %s
                                           prozess.nextToken().trim(),	// PAGES %p
                                           prozess.nextToken().trim(),	// DURATION %h
                                           prozess.nextToken().trim()));	// ERRORTEXT %e
	  }
	} catch (Exception e) {
          throw new FaxException(e.getMessage(), e);	  
	}
      }
    } catch (ConnectException e) {
      Utils.log("Fax", "NO FAX SERVER");
      throw new FaxException("NO FAX SERVER");
    } catch (IOException e) {
      throw new FaxException(e);
    } catch (ServerResponseException e) {
      throw new FaxException(e);
    }

    return queue;
  }


  public static int             HFX_DEFAULT_PORT = 4559;
  public static String          HFX_DEFAULT_USER = "KOPI";
}
