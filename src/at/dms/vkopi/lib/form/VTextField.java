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

package at.dms.vkopi.lib.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
// import java.sql.Clob;
import java.sql.Blob;
import java.sql.SQLException;

import at.dms.xkopi.lib.base.Query;
import at.dms.vkopi.lib.visual.VRuntimeException;

/**
 * This class is the Visual Kopi representation a CLOB SQL type.
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

  // MOVED TO VFIELDUI
//   /**
//    * Create a display widget for this field
//    */
//   protected DField createDisplay(DLabel label) {
//     return new DTextEditor(getUI(), label, getAlign(), 0, getHeight());
//   }

  /**
   * @return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VTextColumn(getHeader(), null, getAlign(), width, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may be raised if text is bad
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
      setString(r, new String((byte[])v));
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
    // !!! laurent 20020801 : consider clob like a blob. See
    // SapdbDbiChecker.visitClobType, see also hasBinaryLargeObject

//     Clob clob = query.getClob(column);
    Blob        blob = query.getBlob(column);

    if (blob != null) {
//       InputStream               is = clob.getAsciiStream();
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

    return c == null ? null : c.getBytes();
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
    // !!! laurent 20020801 : consider a clob like a blob :
    // see SapdDbiChecker.visitClobType, see also retrieveQuery
    //     return false;

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
