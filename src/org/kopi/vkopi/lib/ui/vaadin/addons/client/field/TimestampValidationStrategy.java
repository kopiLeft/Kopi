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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.field;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Time stamp validation strategy
 */
public class TimestampValidationStrategy extends AllowAllValidationStrategy {

  @Override
  public void checkType(VInputTextField field, String text) throws CheckTypeException {
    if ("".equals(text)) {
      field.setText(null);
    } else {
      field.setText(toTimestamp(new Date()));
    }
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
    tmp = new StringBuffer(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date));
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
