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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VExecFailedException;
import at.dms.vkopi.lib.visual.VRuntimeException;
import at.dms.xkopi.lib.base.Query;

public class VImageField extends VField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public VImageField(int width, int height) {
    super(1, 1);
    this.iconWidth = width;
    this.iconHeight = height;
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }

 /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new byte[2 * block.getBufferSize()][];
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return Message.getMessage("image-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return Message.getMessage("Image");
  }

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VImageColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(Object o) {
  }

  public int getType() {
    return MDL_FLD_IMAGE;
  }
  // ---------------------------------------------------------------------
  // PROTECTED UTILS
  // ---------------------------------------------------------------------
  // MOVED TO VFIELDUI
//   /**
//    * Create a display widget for this field
//    */
//   protected DField createDisplay(DLabel label) {
//     return new DImageField(getUI(), label, getAlign(), 0, iconWidth, iconHeight);
//   }

  // ---------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ---------------------------------------------------------------------

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  public int getSearchType() {
    return VConstants.STY_NO_COND;
  }

  /**
   * Returns the search conditions for this field.
   */
  public String getSearchCondition() {
    return null;
  }

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setImage(r, null);
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setImage(int r, byte[] v) {
    if (isChangedUI() || value[r] != v) {
      // trails (backup) the record if necessary
      trail(r);
      // set value in the defined row
      value[r] = v;
      // inform that value has changed
      setChanged(r);
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public void setObject(int r, Object v) {
    setImage(r, (byte[])v);
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
      } catch (IOException e) {
        throw new VRuntimeException(e);
      }
      return out.toByteArray();
    } else {
      return null;
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a date value.
   */
  public byte[] getImage(int r) {
    return value[r];
  }


  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return getImage(r);
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    throw new InconsistencyException("UNEXPECTD GET TEXT");
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == null ? "NULL" : "?";
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    value[t] = value[f];
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasLargeObject(int r) {
    return  value[r] != null;
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
      return new ByteArrayInputStream(value[r]);
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  protected String formatImage(Object value) {
    return "image";
  }

//   /**
//    * autofill
//    * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     File f = ImageFileChooser.chooseFile(getForm().getFrame());

//     if (f == null) {
//       return;
//     }

//     try {
//       FileInputStream	is = new FileInputStream(f);
//       byte[]	b = new byte[is.available()];
//       is.read(b);

//       setImage(getBlock().getActiveRecord(), b);
//     } catch (Exception e) {
//       throw new VExecFailedException("bad-file", e);
//     }
//     if (gotoNextField) {
//       getBlock().gotoNextField();
//     }
//   }
  /**
   * autofill
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in gotoNextField
   */
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (handler != null) {
      byte[]	b = handler.selectImage();

      if (b != null) {
        setImage(b);
        return true;
      }
    }
    return false;
  }

  public int getIconHeight() {
    return iconHeight;
  }
  public int getIconWidth() {
    return iconWidth;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private byte[][]		value;
  private int			iconWidth;
  private int			iconHeight;
}
