/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: IPPClient.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.util.ipp;

import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

public class IPPClient {

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------

  public IPPClient(String hostname, short port, String printer, String user) {
    this.hostname = hostname;
    this.port = port;
    this.printer = printer;
    this.user = user;
  }

  public void print(InputStream file, int nbCopies, String[] attributes)
    throws IOException
  {
    List        mediaAttributes = new ArrayList();
    List        optionalAttributes = new ArrayList();

    if (attributes != null) {
      for (int i = 0; i < attributes.length; ++i) {
        if (attributes[i].indexOf('=') != -1) {
          optionalAttributes.add(attributes[i]);
        } else {
          mediaAttributes.add(attributes[i]);
        }
      }
    }

    print(file, nbCopies, mediaAttributes, optionalAttributes);
  }

  /**
   *
   * optionalAttributes are attributes supported by the printer
   *  the format is "attribute=value".
   * mediaAttributes are single value corresponding to a media.
   */
  protected void print(InputStream file,
                       int nbCopies,
                       List mediaAttributes,
                       List optionalAttributes)
    throws IOException
  {
    IPPAttribute                att;
    IPP                         req = new IPP();
    ByteArrayOutputStream       baos = new ByteArrayOutputStream();
    int                         read;

    req.setRequest(1, IPPConstants.OPS_PRINT_JOB);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_CHARSET,
                           "attributes-charset" );
    att.addValue(new StringValue("iso-8859-1" ));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_LANGUAGE,
                           "attributes-natural-language" );
    att.addValue(new StringValue("en"));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_URI,
                           "printer-uri" );
    att.addValue(new StringValue("ipp://" + hostname + ":" + port +
                                 "/printers/" + printer));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_NAME,
                           "requesting-user-name");
    att.addValue(new StringValue(user));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_JOB,
                           IPPConstants.TAG_INTEGER,
                           "copies");
    // workaround LETTERHEAD lackner 13.01.2004
    // send each kopi as an own job
    // for e.g. a letterhead on every copy (milavec)
    // -> 1 instead of nbCopies
    att.addValue(new IntegerValue(1));
    req.addAttribute(att);
    // end workaround

    if (mediaAttributes != null && !mediaAttributes.isEmpty()) {
      Iterator  atts = mediaAttributes.iterator();

      att = new IPPAttribute(IPPConstants.TAG_JOB,
                             IPPConstants.TAG_KEYWORD,
                             "media");

      while (atts.hasNext()) {
        att.addValue(new StringValue((String) atts.next()));
      }
      req.addAttribute(att);
    }

    if (optionalAttributes != null) {
      Iterator  atts = optionalAttributes.iterator();

      while (atts.hasNext()) {
        String  optionalAttribute = (String) atts.next();
        String  attributeName =
          optionalAttribute.substring(0, optionalAttribute.indexOf("="));
        String  attributeValue =
          optionalAttribute.substring(optionalAttribute.indexOf("=") + 1, optionalAttribute.length());

        att = new IPPAttribute(IPPConstants.TAG_JOB,
                               IPPConstants.TAG_NAME,
                               attributeName);

        att.addValue(new StringValue(attributeValue));
        req.addAttribute(att);
      }
    }

    while ( (read = file.read()) != -1) {
      baos.write(read);
    }

    req.setData(baos.toByteArray());

    // workaround LETTERHEAD lackner 13.01.2004
    // see description above
    for (int i = 0; i < nbCopies; i++) {
      IPPHttpConnection         httpConnection;
      IPP                       resp;

      httpConnection = new IPPHttpConnection(
        new URL("http://" + hostname + ":" + port + "/printers/" + printer));

      httpConnection.sendRequest(req);

      resp = httpConnection.receiveResponse();
      resp.simpleDump();
    }
  }

  public void printPrinterAttributes()
    throws IOException
  {
    IPP         resp = getPrinterAttributes();

    resp.dump();
  }

  public List getMediaTypes()
    throws IOException
  {
    List        media = new LinkedList();
    IPP         properties = getPrinterAttributes();
    Iterator    attributes = properties.getAttributes();

    while (attributes.hasNext()) {
      IPPAttribute      attribute = (IPPAttribute) attributes.next();

      if (attribute.getName().equals("media-supported")) {
        Iterator        values = attribute.getValues();

        while (values.hasNext()) {
          IPPValue      value = (IPPValue) values.next();

          if (value instanceof StringValue) {
            media.add(((StringValue) value).getValue());
          }
        }
      }
    }

    return media;
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private IPP getPrinterAttributes()
    throws IOException
  {
    IPPHttpConnection           httpConnection;
    IPPAttribute                att;
    IPP                         req = new IPP();

    httpConnection = new IPPHttpConnection(
       new URL("http://" + hostname + ":" + port + "/printers/" + printer));

    req.setRequest(1, IPPConstants.OPS_GET_PRINTER_ATTRIBUTES);


    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_CHARSET,
                           "attributes-charset" );
    att.addValue(new StringValue("iso-8859-1" ));
    //att.addValue(new StringValue("utf-8"));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_LANGUAGE,
                           "attributes-natural-language" );
    att.addValue(new StringValue("en"));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_URI,
                           "printer-uri" );
    att.addValue(new StringValue("ipp://" + hostname + ":" + port +
                                 "/printers/" + printer));
    req.addAttribute(att);

    att = new IPPAttribute(IPPConstants.TAG_OPERATION,
                           IPPConstants.TAG_NAME,
                           "printer-name");
    att.addValue(new StringValue(printer));
    req.addAttribute(att);

    httpConnection.sendRequest(req);

    return httpConnection.receiveResponse();
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private String                hostname;
  private short                 port;
  private String                printer;
  private String                user;
}
