/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.io.*;

/*
 * ----------------------------------------------------------------------
 * FAX STATUS
 * A CLASS FOR HANDLING THE STATUS OF HYLAFAX ENTRIES RETURNED BY VARIOUS
 * QUEUES
 * ----------------------------------------------------------------------
 */
public class FaxStatus {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR OUTGOING FAXES
  // ----------------------------------------------------------------------

  public FaxStatus(String id,
		   String tag,
                   String user,
		   String dialNo,
		   String state,
		   String pages,
		   String dials,
		   String text) {
    this.id = (id.compareTo("") == 0) ? null : id;
    this.tag = (tag.compareTo("")  == 0) ? null : tag;
    this.user = (user.compareTo("") == 0) ? null : user;
    this.dialNo = (dialNo.compareTo("") == 0) ? null : dialNo;
    this.state = (state.compareTo("") == 0) ? null : state;
    this.pages = (pages.compareTo("") == 0) ? null : pages;
    this.text = (text.compareTo("") == 0) ? null : text;
    this.dials = (dials.compareTo("") == 0) ? null : dials;

    this.sendtime = null;
    if (isSent()) {		// TRY TO GATHER THE SEND TIME
      this.sendtime = Fax.readSendtime(id);
    }
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR FOR INCOMING FAXES
  // ----------------------------------------------------------------------

  public FaxStatus(String filename,
		   String incomingtime,
                   String sender,
		   String pages,
		   String duration,
		   String text) {
    this.filename = (filename.compareTo("") == 0) ? null : filename;
    this.incomingtime = (incomingtime.compareTo("") == 0) ? null : incomingtime;
    this.sender = (sender.compareTo("") == 0) ? null : sender;
    this.pages = (pages.compareTo("") == 0) ? null : pages;
    this.duration = (duration.compareTo("") == 0) ? null : duration;
    this.text = (text.compareTo("") == 0) ? null : text;
  }

  // ----------------------------------------------------------------------
  // RETURNS THE ID (DATABASE ID) INSIDE THE TAG
  // THE ID IS A NUMBER SO STRIP THEREFORE ANY OTHER LEADING CHARACTERS
  // IF NO ID IS FOUND RETURN -1
  // ----------------------------------------------------------------------

  public int getTagId() {
    int startpos = 0;

    if (this.tag == null) {
      return -1;
    }

    for (int i = 0; i < this.tag.length(); i ++) {
      char b = this.tag.charAt(i);
      if ((b >= '0') && (b <= '9')) {
        startpos = i;
	break;
      }
    }

    int id = -1;

    try {
      id = Integer.valueOf(this.tag.substring(startpos)).intValue();
    } catch (Exception e) {
      return -1;
    }

    return id;
  }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF TAG STARTS WITH TAGSTR
  // ----------------------------------------------------------------------

  public boolean isTagged(String tagstr) {
    if (this.tag == null) {
      return false;
    }
    return (this.tag.startsWith(tagstr));
  }

  // ----------------------------------------------------------------------
  // RETURNS TRUE IF HAS BEEN SENT
  // ----------------------------------------------------------------------

  public boolean isSent() {
    if (this.state.compareTo("D") == 0) {
      return (this.text == null || this.text.compareTo("") == 0);
    } else {
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // SIMPLE ACCESSORS
  // ----------------------------------------------------------------------

  public String getId() {
    return this.id;
  }

  public String getTag() {
    return this.tag;
  }

  public String getUser() {
    return this.user;
  }

  public String getDialNo() {
    return this.dialNo;
  }

  public String getDials() {
    return this.dials;
  }

  public String getState() {
    return this.state;
  }

  public String getPages() {
    return this.pages;
  }

  public String getText() {
    return this.text;
  }

  public String getFilename() {
    return this.filename;
  }

  public String getIncomingtime() {
    return this.incomingtime;
  }

  public String getSender() {
    return this.sender;
  }

  public String getDuration() {
    return this.duration;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR OUTGOING FAXES
  // ----------------------------------------------------------------------

  private String id;
  private String tag;
  private String user;
  private String dialNo;
  private String dials;
  private String state;
  private String sendtime;

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR INCOMMING FAXES
  // ----------------------------------------------------------------------

  private String filename;
  private String incomingtime;
  private String sender;
  private String duration;

  // ----------------------------------------------------------------------
  // DATA MEMBERS FOR BOTH IN/OUT FAXES
  // ----------------------------------------------------------------------

  private String pages;
  private String text;
}
