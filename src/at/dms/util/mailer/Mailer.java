/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.util.mailer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;

public class Mailer {

  // ----------------------------------------------------------------------
  // ENTRY POINT
  // ----------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    Mailer      instance = new Mailer();

    instance.run(args);
  }

  public void run(String[] args) {
    if (!parseArguments(args)) {
      options.usage();
      System.exit(1);
    }

    List      attachments = new ArrayList();

    if (options.attachments != null && options.attachments.length() > 0) {
      StringTokenizer   tok = new StringTokenizer(options.attachments, ",");

      while (tok.hasMoreTokens()) {
        String  filename = tok.nextToken();
        try {
          attachments.add(new Attachment(new File(filename)));
        } catch (FileNotFoundException e) {
          System.err.println(filename + ": no such file");
        }
      }
    }

    sendMail(options.host,
             options.to,
             options.cc,
             options.bcc,
             options.subject,
             options.body,
             options.from,
             attachments);
  }

  /**
   * Convenience method
   *
   * @param	mailHost	the SMTP server (name or IP)
   * @param	recipient	the e-mail address (name@domain)
   * @param	bccRecipient	the e-mail address (name@domain)
   * @param	subject		the subject of the mail
   * @param	body		the body of the mail
   * @param	sender		the sender address (name@domain)
   */
  public static void sendMail(final String mailHost,
			      final String recipient,
			      final String ccRecipient,
			      final String bccRecipient,
			      final String subject,
			      final String body,
			      final String sender)
  {
    sendMail(mailHost, recipient, ccRecipient, bccRecipient, subject, body, sender, null);
  }

  /**
   * Convenience method
   *
   * @param	mailHost	the SMTP server (name or IP)
   * @param	recipient	the e-mail address (name@domain)
   * @param	ccRecipient	the e-mail address (name@domain)
   * @param	bccRecipient	the e-mail address (name@domain)
   * @param	subject		the subject of the mail
   * @param	body		the body of the mail
   * @param	sender		the sender address (name@domain)
   * @param	attachment	a file to send in attachment
   * @param	name		the name of the attachment document
   */
  public static void sendMail(final String host,
			      final String recipient,
			      final String ccRecipient,
			      final String bccRecipient,
			      final String subject,
			      final String body,
			      final String sender,
			      final List attachments)
  {
    Runnable runnable = new Runnable() {
	public void run() {
	  try {
	    Mailer      mailer = new Mailer();

	    mailer.setMailHost(host);
	    mailer.sendMessage(sender,
                               recipient,
                               ccRecipient,
                               bccRecipient,
                               subject,
                               body,
                               attachments);
	  } catch (Exception e) {
            e.printStackTrace();
	    System.out.println("*** SMTP Exception: " + e.getMessage());
	  }
	}
      };
    new Thread(runnable).start();
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Creates a new Mailer.
   */
  public Mailer() {
  }

  /**
   * Parse the argument list
   */
  public boolean parseArguments(String[] args) {
    options = new MailerOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return options.to != null
      && options.to.length() > 0
      && options.from != null
      && options.from.length() > 0;
  }

  /**
   * Sends a message with no attachments.
   *
   * @param	sender		the sender address (name@domain)
   * @param	recipient	a comma separated list of e-mail addresses (name@domain)
   * @param	ccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	bccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	subject		the subject of the mail
   * @param	message		the body of the mail
   * @param	attachment	a file to send in attachment
   * @param	name		the name of the attachment document
   */
  public void sendMessage(String sender,
			  String recipient,
			  String ccRecipient,
			  String bccRecipient,
			  String subject,
			  String message)
    throws SMTPException
  {
    sendMessage(sender, recipient, ccRecipient, bccRecipient, subject, message, new ArrayList());
  }

  /**
   * Sends a message with only one attachment.
   *
   * @param	sender		the sender address (name@domain)
   * @param	recipient	a comma separated list of e-mail addresses (name@domain)
   * @param	ccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	bccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	subject		the subject of the mail
   * @param	message		the body of the mail
   * @param	attachment	a file to send in attachment
   */
  public void sendMessage(String sender,
			  String recipient,
			  String ccRecipient,
			  String bccRecipient,
			  String subject,
			  String message,
			  Attachment attachment)
    throws SMTPException
  {
    ArrayList      attachments = new ArrayList(1);

    attachments.add(attachment);
    sendMessage(sender, recipient, ccRecipient, bccRecipient, subject, message, attachments);
  }

  /**
   * Sends a message.
   *
   * @param	sender		the sender address (name@domain)
   * @param	recipient	a comma separated list of e-mail addresses (name@domain)
   * @param	ccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	bccRecipient	a comma separated list of e-mail addresses (name@domain)
   * @param	subject		the subject of the mail
   * @param	message		the body of the mail
   * @param	attachment	a file to send in attachment
   * @param	name		the name of the attachment document
   */
  public void sendMessage(String sender,
			  String recipient,
			  String ccRecipient,
			  String bccRecipient,
			  String subject,
			  String message,
			  List attachments)
    throws SMTPException
  {
    String              separator = ",";
    List                recipients = split(recipient);
    List                ccRecipients = split(ccRecipient);
    List                bccRecipients = split(bccRecipient);

    sendMessage(sender, recipients, ccRecipients, bccRecipients, subject, message, attachments);
  }

  /**
   * Sends a message.
   *
   * @param	sender		the sender address (name@domain)
   * @param	recipients	a list of e-mail addresses (name@domain)
   * @param	ccRecipients	 a list of e-mail addresses (name@domain)
   * @param	bccRecipients	a list of e-mail addresses (name@domain)
   * @param	subject		the subject of the mail
   * @param	message		the body of the mail
   * @param	attachment	a file to send in attachment
   * @param	name		the name of the attachment document
   */
  public void sendMessage(String sender,
			  List recipients,
			  List ccRecipients,
			  List bccRecipients,
			  String subject,
			  String message,
			  List attachments)
    throws SMTPException
  {
    try {
      Properties         props = new Properties();
     
      props.put("mail.smtp.host", mailHost);
      
      Session            session = Session.getDefaultInstance(props);
      Message            msg = new MimeMessage(session);

      msg.setFrom(new InternetAddress(sender));
      msg.addRecipients(Message.RecipientType.TO,  getAddresses(recipients));  
      if (ccRecipients != null && !ccRecipients.isEmpty()) {
        msg.addRecipients(Message.RecipientType.CC,  getAddresses(ccRecipients));     
      }
      if (bccRecipients != null && !bccRecipients.isEmpty()) {
        msg.addRecipients(Message.RecipientType.BCC,  getAddresses(bccRecipients));     
      }

      msg.setSubject(subject != null ? subject : "NO SUBJECT");

      if (attachments == null || attachments.isEmpty()) {
        msg.setText(message);
      } else {
        MimeMultipart   content = new MimeMultipart();
        MimeBodyPart    text = new MimeBodyPart();
      
        text.setText(message);
        content.addBodyPart(text);

        ListIterator    iterator = attachments.listIterator();
          
        while (iterator.hasNext()) {
          Attachment    attachment = (Attachment) iterator.next();
          MimeBodyPart  bodyPart = new MimeBodyPart();

          bodyPart.setDataHandler(new DataHandler(attachment)); 
          bodyPart.setHeader("Content-Transfer-Encoding", "base64");
          bodyPart.setFileName(attachment.getName());
          content.addBodyPart(bodyPart);
        }
        msg.setContent(content);
      }

      msg.setSentDate(new Date());
      Transport.send(msg);
    } catch (MessagingException e) {
      throw new SMTPException(e);
    }
  }

  private Address[] getAddresses(List recipients) throws AddressException {
    ListIterator        iterator = recipients.listIterator();
    ArrayList           addresses = new ArrayList();

    while (iterator.hasNext()) {
      final String    tmp = (String)iterator.next();

      if (tmp.length() > 0) {
        addresses.add(new InternetAddress(tmp));
      }
    }

    return (Address[]) addresses.toArray(new  Address[addresses.size()]);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public void setMailHost(String mailHost) {
    this.mailHost = mailHost;
  }

  /**
   * Each token of the line is an element of the List.
   */
  private List split(String line) {
    List                result;

    if (line == null) {
      result = new ArrayList();
    } else {
      StringTokenizer     tok;

      tok = new StringTokenizer(line, ",");
      result = new ArrayList(tok.countTokens());

      while(tok.hasMoreTokens()) {
        result.add(tok.nextToken());
      }
    }

    return result;
  }

  // HOSTS
  private String		mailHost;

  private MailerOptions         options;
}
