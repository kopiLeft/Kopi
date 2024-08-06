/*
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH
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

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.list.VColorColumn;
import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.xkopi.lib.base.Query;

@SuppressWarnings("serial")
public class VColorField extends VField {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public VColorField(int width, int height) {
    super(1, 1);
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
    value = new Color[2 * block.getBufferSize()];
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return VlibProperties.getString("color-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Color");
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
    return new VColorColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(int rec, Object o) {
  }

  public int getType() {
    return MDL_FLD_COLOR;
  }
  // ---------------------------------------------------------------------
  // PROTECTED UTILS
  // ---------------------------------------------------------------------

  // MOVED TO VFIELDUI
//   /**
//    * Create a display widget for this field
//    */
//   protected DField createDisplay(DLabel label) {
//     return new DColorField(getUI(), label, getAlign(), 0);
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
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setColor(r, null);
  }

  /**
   * Sets the field value of given record to a date value.
   */
  public void setColor(int r, Color v) {
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
    if (v == null) {
      setColor(r, Color.BLACK);
    } else if (v instanceof byte[]) {
      byte[]  b = (byte[])v;
      setColor(r, new Color(reformat(b[0]), reformat(b[1]), reformat(b[2])));
    } else {
      setColor(r, new Color((int) v));
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
    if (query.isNull(column)) {
      return null;
    } else {
      return query.getInt(column);
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
  public Color getColor(int r) {
    return (Color) getObject(r);
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
  }

  @Override
  public String toText(Object o) {
    throw new InconsistencyException("UNEXPECTD GET TEXT");
  }

  @Override
  public Object toObject(String s) {
    throw new InconsistencyException("UNEXPECTD GET TEXT");
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
  public String getSqlImpl(int r) {return value[r] == null ? "NULL" :
    org.kopi.xkopi.lib.base.KopiUtils.toSql(value[r].getRGB());
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    Color               oldValue;

    oldValue = value[t];
    value[t] = value[f];
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < getBlock().getBufferSize()
        && ((oldValue != null && value[t] == null)
            || (oldValue == null && value[t] != null)
            || (oldValue != null && !oldValue.equals(value[t]))))
    {
      fireValueChanged(t);
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasLargeObject(int r) {
    return false;
  }

  /**
   * Warning:	This method will become inaccessible to kopi users in next release
   * @kopi	inaccessible
   */
  public boolean hasBinaryLargeObject(int r) {
    return false;
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
      return new ByteArrayInputStream(getByteArrayFromColor((Color)getObjectImpl(r)));
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  public Class getDataType() {
    return Color.class;
  }

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

//   /**
//    * autofill
//    * @exception	org.kopi.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     Color f = JColorChooser.showDialog(getForm().getFrame(), Message.getMessage("color-chooser"), getColor(getBlock().getActiveRecord()));

//     setColor(f);
//     if (gotoNextField) {
//       getBlock().gotoNextField();
//     }
//   }
  /**
   * autofill
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may occur in gotoNextField
   */
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (handler != null) {
      setColor(block.getActiveRecord(),
          handler.selectColor(getColor(getBlock().getActiveRecord())));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Reformat a unsigned int from a byte
   */
  private int reformat(byte b) {
    return b < 0 ? b + 256 : b;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private Color[]		value;

  /*
   * ----------------------------------------------------------------------
   * STATIC MEMBERS
   * ----------------------------------------------------------------------
   */

  /**
   * Get byteArray from Color
   */
  public static byte[] getByteArrayFromColor(Color color) {
    int rgb = color.getRGB();
    int red = (rgb >> 16) & 0xFF;
    int green = (rgb >> 8) & 0xFF;
    int blue = rgb & 0xFF;

    return new byte[]{(byte) red, (byte) green, (byte) blue};
  }
}
