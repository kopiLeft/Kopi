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

package org.kopi.vkopi.lib.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.list.VTextColumn;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.xkopi.lib.base.PostgresDriverInterface;
import org.kopi.xkopi.lib.base.Query;

/**
 * This class implements multi-line text fields.
 */
@SuppressWarnings("serial")
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
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may be raised if text is bad
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
   * @see       #isPostgresDriverInterface()
   */
  public void setObject(int r, Object v) {
    if (v instanceof byte[]) {
      try {
        setString(r, new String((byte[])v, ApplicationConfiguration.getConfiguration().isUnicodeDatabase() ? "UTF-8" : "ISO-8859-1"));
      } catch (UnsupportedEncodingException e) {
        throw new InconsistencyException(e);
      }
    } else {
      if (v != null) {
	setString(r, new String(((String)v)));
      } else {
	setString(r, null);
      }
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param	query		the query holding the tuple
   * @param	column		the index of the column in the tuple
   * 
   * @see       #isPostgresDriverInterface()
   */
  public Object retrieveQuery(Query query, int column)
    throws SQLException
  {
    if (isPostgresDriverInterface()) {
      return super.retrieveQuery(query, column);
    } else {
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
   * @see       #isPostgresDriverInterface()
   */
  public Object getObjectImpl(int r) {
    String	c = (String) super.getObjectImpl(r);

    if (c == null) {
      return null;
    } else {
      if (isPostgresDriverInterface()) {
        return c;
      } else {
        try {
          return c.getBytes(ApplicationConfiguration.getConfiguration().isUnicodeDatabase() ? "UTF-8" : "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          throw new InconsistencyException(e);
        }
      }
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * @see       #isPostgresDriverInterface()
   */
  public String getSqlImpl(int r) {
    if (isPostgresDriverInterface()) {
      return super.getSqlImpl(r);
    } else {
      return "?";
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   * @see       #isPostgresDriverInterface()
   */
  public boolean hasLargeObject(int r) {
    if (isPostgresDriverInterface()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   * @see       #isPostgresDriverInterface()
   */
  public boolean hasBinaryLargeObject(int r) {
    if (isPostgresDriverInterface()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   * @see       #isPostgresDriverInterface()
   */
  public InputStream getLargeObject(int r) {
    if (value[r] == null) {
      return null;
    } else {
      return new ByteArrayInputStream((byte[])getObjectImpl(r));
    }
  }
  
  /**
   * Checks if we are running in a postgreSQL database.
   * This check is done to treat the content of this field as String
   * and not as large objects. In fact, postgreSQL large binary objects
   * are not properly treated and we face many encoding problems.
   * The getClob or getClob methods returns always wrong content for non UTF-8
   * characters.
   * A work around for this, is to handle the content of this field as {@link String}
   * objects. This will not affect the content of the field even for non UTF-8 characters
   * since they are retrieved as strings from the database. 
   * @return {@code true} if the we are running in a postgreSQL context.
   */
  protected boolean isPostgresDriverInterface() {
    return getBlock().getDBContext().getDefaultConnection().getDriverInterface() instanceof PostgresDriverInterface;
  }
}
