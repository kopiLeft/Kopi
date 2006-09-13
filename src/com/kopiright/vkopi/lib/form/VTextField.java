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

package com.kopiright.vkopi.lib.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;

import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.list.VTextColumn;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.VRuntimeException;

/**
 * This class implements multi-line text fields.
 */
public class VTextField extends VStringField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VTextField(int width, int height, int visibleHeight, int convert) {
    super(width, height, visibleHeight, convert);
  }

  public int getType() {
    return MDL_FLD_EDITOR;
  }
  // ----------------------------------------------------------------------
  // INTERFACE DISPLAY
  // ----------------------------------------------------------------------

  /**
   * @return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VTextColumn(getHeader(), null, getAlign(), width, getPriority() >= 0);
  }

  /**
   * verify that value is valid (on exit)
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised if text is bad
   */
  public void checkType(Object o) {
    setString(block.getActiveRecord(), (String)o);
  }

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  public int getSearchType() {
    return VConstants.STY_NO_COND;
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    if (v instanceof byte[]) {
      try {
        setString(r, new String((byte[])v, ApplicationConfiguration.getConfiguration().isUnicodeDatabase() ? "UTF-8" : "ISO-8859-1"));
      } catch (UnsupportedEncodingException e) {
        throw new InconsistencyException(e);
      }
    } else {
      setString(r, (String)v);
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    Blob        blob = query.getBlob(column);

    if (blob != null) {
      InputStream               is = blob.getBinaryStream();
      ByteArrayOutputStream     out = new ByteArrayOutputStream();
      byte[]                    buf = new byte[2048];
      int                       nread;

      try {
        while ((nread = is.read(buf)) != -1) {
          out.write(buf, 0, nread);
        }
        return out.toByteArray();
      } catch (IOException e) {
        throw new VRuntimeException(e);
      }
    } else {
      return null;
    }
  }

  /**
   * Returns the field value of given record as a string value.
   */
  public String getString(int r) {
    // lackner 2005.04027
    // !!! this does not work for alias fields
    return (String) super.getObjectImpl(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    String	c = (String) super.getObjectImpl(r);

    if (c == null) {
      return null;
    } else {
      try {
        return c.getBytes(ApplicationConfiguration.getConfiguration().isUnicodeDatabase() ? "UTF-8" : "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
        throw new InconsistencyException(e);
      }
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return "?";
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasLargeObject(int r) {
    return true;
  }

  /**
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasBinaryLargeObject(int r) {
    return true;
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public InputStream getLargeObject(int r) {
    if (value[r] == null) {
      return null;
    } else {
      return new ByteArrayInputStream((byte[])getObjectImpl(r));
    }
  }
}
