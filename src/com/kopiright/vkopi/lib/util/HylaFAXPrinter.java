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

import java.awt.Dimension;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gnu.hylafax.Job;
import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.HylaFAXClientProtocol;
import gnu.inet.ftp.FtpClientProtocol;
import gnu.inet.ftp.ServerResponseException;

import com.kopiright.util.base.InconsistencyException;

/**
 * Fax printer
 */
@SuppressWarnings("deprecation")
public class HylaFAXPrinter extends AbstractPrinter implements CachePrinter {

  /**
   * Constructs a fax printer
   */
  public HylaFAXPrinter(final String faxHost,
                        final String number,
                        final String user,
                        final List<?> attachments)
  {
    super("FaxPrinter "+number);
    this.faxHost = faxHost;
    this.number = number;
    this.user = user;
    this.attachments = attachments;
  }

  /**
   * Gets the phone number
   */
  public String getNumber() {
    return number;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  public String print(PrintJob printdata) throws PrintException {
    // get down to business, send the FAX already

    // List with names of temporary files on the server side
    ArrayList<Object>         	documents = new ArrayList<Object>();
    // fax client
    HylaFAXClient     		faxClient = new HylaFAXClient();

    try{
      faxClient.setDebug(false);      // no debug messages
      faxClient.open(faxHost);        // name of host
      faxClient.user(user);           // hyla fax user
      // necessary for pdf documents to keep the correct file size
      faxClient.type(FtpClientProtocol.TYPE_IMAGE);
      faxClient.noop();
      faxClient.tzone(HylaFAXClientProtocol.TZONE_LOCAL);

      // add fax document
      documents.add(faxClient.putTemporary(printdata.getInputStream()));

      // put attachments to server
      if (attachments != null) {
        Iterator<?>      attachmentInterator = attachments.iterator();

        while (attachmentInterator.hasNext()) {
          InputStream dataSource = (InputStream) attachmentInterator.next();

          // put data to the hylafax server
          documents.add(faxClient.putTemporary(dataSource));
        }
      }
      // all file to send are at the server
      // create a job to send them
      Job             job = faxClient.createJob();

      // set job properties
      job.setFromUser(user);
      job.setNotifyAddress(user);
      job.setKilltime("000259");
      job.setMaximumDials(3);
      job.setMaximumTries(3);
      job.setPriority(Job.PRIORITY_NORMAL);
      job.setDialstring(number);
      job.setVerticalResolution(Job.RESOLUTION_MEDIUM);
      job.setPageDimension((Dimension) Job.pagesizes.get("a4"));
      job.setNotifyType(HylaFAXClientProtocol.NOTIFY_NONE);
      job.setChopThreshold(3);

      // add documents to the job
      Iterator<?>        docIterator = documents.iterator();

      while (docIterator.hasNext()) {
        job.addDocument((String)docIterator.next());
      }

      faxClient.submit(job); // submit the job to the scheduler
    } catch (ServerResponseException e) {
      throw new InconsistencyException("Can't send fax job", e);
    } catch (IOException e) {
      throw new InconsistencyException("Can't send fax job", e);
    } finally {
      // disconnect from the server
      try {
        faxClient.quit();
      } catch (IOException e) {
        throw new InconsistencyException("Can't disconnect from server", e);
      } catch (ServerResponseException e) {
        throw new InconsistencyException("Can't disconnect from server", e);
      }
    }
    return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String				faxHost;
  private final String				number;
  private final String				user;
  private final List<?>       			attachments;
}
