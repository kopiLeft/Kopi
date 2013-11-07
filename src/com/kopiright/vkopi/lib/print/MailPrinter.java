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

package com.kopiright.vkopi.lib.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kopiright.util.mailer.Attachment;
import com.kopiright.util.mailer.Mailer;
import com.kopiright.vkopi.lib.util.AbstractPrinter;
import com.kopiright.vkopi.lib.util.CachePrinter;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.visual.MessageCode;

/**
 * Mail printer
 */
@SuppressWarnings("deprecation")
public class MailPrinter extends AbstractPrinter implements CachePrinter {

  /**
   * Constructs a mail printer
   */
  public MailPrinter(final String command,
		     final String mailHost,
		     final String recipient,
		     final String ccRecipient,
		     final String bccRecipient,
		     final String subject,
		     final String body,
		     final String sender,
		     final boolean sendPdf,
		     final List<?> attachments)
  {
    super("MailPrinter");
    this.command = command;
    this.mailHost = mailHost;
    this.recipient = recipient;
    this.ccRecipient = ccRecipient;
    this.bccRecipient = bccRecipient;
    this.subject = subject;
    this.body = body;
    this.sender = sender;
    this.sendPdf = sendPdf;
    this.attachments = attachments;
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  @SuppressWarnings("unchecked")
  public String print(PrintJob printdata) throws IOException, PrintException {
    try {
      PrintJob          gsJob;
      File              dest;

      if (printdata.getDataType() == PrintJob.DAT_PS) {
        // convert if postcript
        gsJob = convertToGhostscript(printdata);
      } else {
        gsJob = printdata;
      }
      dest = gsJob.getDataFile();

      if (sendPdf && printdata.getDataType() == PrintJob.DAT_PS) {
        Process       process;

        // convert if postcript
        dest = Utils.getTempFile("MAIL", "PDF");
        process = Runtime.getRuntime().exec(command + " -q -sOutputFile=" + dest + " -sDEVICE=pdfwrite " +
                                            "-g" + (int)(gsJob.getWidth() * 10) + "x" + (int)(gsJob.getHeight() * 10) +
                                            " -dNOPAUSE " + gsJob.getDataFile() + " -c quit ");
        process.waitFor();
      }

      // ALLE ATTACHMENTS IN EINEM VEKTOR
      List<Attachment> allattachments = new ArrayList<Attachment>();

      allattachments.add(new Attachment(gsJob.getTitle() + (sendPdf ? ".pdf" : ".ps"),
                                        sendPdf ? "application/pdf" : "application/postscript",
                                        new FileInputStream(dest)));

      allattachments.addAll(attachments);

      Mailer          mailer = new Mailer();

      mailer.setMailHost(mailHost);
      mailer.sendMessage(sender,
                         recipient,
                         ccRecipient,
                         bccRecipient,
                         subject,
                         (body == null) ? "" : body,
                         allattachments);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PrintException(MessageCode.getMessage("VIS-00056"), e, PrintException.EXC_UNKNOWN);
    }
    return "NYI";
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String		command;
  private final String		mailHost;
  private final String		recipient;
  private final String		ccRecipient;
  private final String		bccRecipient;
  private final String		subject;
  private final String		body;
  private final String		sender;
  private final boolean	        sendPdf;

  @SuppressWarnings("rawtypes")
  private List			attachments;
}
