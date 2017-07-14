/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import java.util.Date;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * The widget par of a timestamp editor field
 */
public class VEditorTimestampField extends VEditorTextField {


  @Override
  public EditorTimestampFieldConnector getConnector() {
    return ConnectorUtils.getConnector(getConnection(), this, EditorTimestampFieldConnector.class);
  }
  
  
  @Override
  public void validate() throws InvalidEditorFieldException {
    setText(toTimestamp(new Date()));
  }
  
  @Override
  protected boolean isNumeric() {
    return true;
  }
  
  /**
   * Converts the given time stamp to its string representation.
   * @param date The date to be converted.
   * @return The string representation of the equivalent time stamp.
   */
  private String toTimestamp(Date date) {
    StringBuffer        tmp;
    int                 nanos;
    
    nanos = (int)((date.getTime()%1000) * 1000000);
    if (nanos < 0) {
      nanos = 1000000000 + nanos;
      date.setTime(((date.getTime()/1000)-1)*1000);
    }
    tmp = new StringBuffer(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").format(date));
    if (nanos >= 100) {
      tmp.append(nanos);
    } else if (nanos >= 10) {
      tmp.append("0" + nanos);
    } else {
      tmp.append("00" + nanos);
    }

    return tmp.toString();
  }
}
