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

import java.sql.SQLException;

import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.list.VWeekColumn;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.base.Query;
import org.kopi.xkopi.lib.type.NotNullWeek;
import org.kopi.xkopi.lib.type.Week;

@SuppressWarnings("serial")
public class VWeekField extends VField {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VWeekField() {
    super(7, 1);
  }

  /**
   * just after loading, construct record
   */
  public void build() {
    super.build();
    value = new Week[2 * block.getBufferSize()];
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return true;
  }
  
  public boolean isNumeric() {
    return true;
  }

  /**
   * return the name of this field
   */
  public String getTypeInformation() {
    return VlibProperties.getString("week-type-field");
  }

  /**
   * return the name of this field
   */
  public String getTypeName() {
    return VlibProperties.getString("Week");
  }

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  protected VListColumn getListColumn() {
    return new VWeekColumn(getHeader(), null, getPriority() >= 0);
  }

  /**
   * verify that text is valid (during typing)
   */
  public boolean checkText(String s) {
    if (s.length() > 10) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (!isWeekChar(s.charAt(i))) {
	return false;
      }
    }
    return true;
  }

  /**
   * verify that value is valid (on exit)
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception is raised if text is bad
   */
  public void checkType(int rec, Object o) throws VException {
    String	s = (String)o;

    if (s.equals("")) {
      setNull(rec);
    } else {
      parseWeek(rec, s);
    }
  }

  private void parseWeek(int rec, String s) throws VFieldException {
    int	week = 0;
    int	year = -1;
    int	bp = 0;
    int	state;
    String	buffer = s + '\0';

    for (state = 1; state > 0; bp += 1) {
      switch (state) {
      case 1: /* The first week's digit */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  week = buffer.charAt(bp) - '0';
	  state = 2;
	} else {
	  state = -1;
	}
	break;

      case 2: /* The second week's digit  */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  week = 10*week + (buffer.charAt(bp) - '0');
	  state = 3;
	  } else if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
	    state = 4;
	  } else if (buffer.charAt(bp) == '\0') {
	    state = 0;
	  } else {
	    state = -1;
	  }
	break;

      case 3: /* The first point : between week and year */
	if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
	  state = 4;
	} else if (buffer.charAt(bp) == '\0') {
	  state = 0;
	} else {
	  state = -1;
	}
	break;

      case 4: /* The first year's digit */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  year = buffer.charAt(bp) - '0';
	  state = 5;
	} else if (buffer.charAt(bp) == '\0') {
	  state = 0;
	} else {
	  state = -1;
	}
	break;

      case 5: /* The second year's digit */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  year = 10*year + (buffer.charAt(bp) - '0');
	  state = 6;
	} else {
	  state = -1;
	}
	break;

      case 6: /* The third year's digit */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  year = 10*year + (buffer.charAt(bp) - '0');
	  state = 7;
	} else if (buffer.charAt(bp) == '\0') {
	  state = 0;
	} else {
	  state = -1;
	}
	break;

      case 7:	/* The fourth year's digit */
	if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
	  year = 10*year + (buffer.charAt(bp) - '0');
	  state = 8;
	} else {
	  state = -1;
	}
	break;

      case 8:	/* The end */
	if (buffer.charAt(bp)  == '\0') {
	  state = 0;
	} else {
	  state = -1;
	}
	break;
      default:
	throw new VFieldException(this, MessageCode.getMessage("VIS-00008"));
      }
    }
    if (state == -1) {
      throw new VFieldException(this, MessageCode.getMessage("VIS-00008"));
    }

    if (year == -1) {
      NotNullWeek	now = Week.now();

      year  = now.getYear();
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    }

    setWeek(rec, new NotNullWeek(year, week));
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  public void setNull(int r) {
    setWeek(r, null);
  }

  /**
   * Sets the field value of given record to a week value.
   */
  public void setWeek(int r, Week v) {
    if (isChangedUI() 
        || (value[r] == null && v != null)
        || (value[r] != null && !value[r].equals(v))) {
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
    setWeek(r, (Week)v);
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
      return query.getWeek(column);
    }
  }

  /**
   * Is the field value of given record null ?
   */
  public boolean isNullImpl(int r) {
    return value[r] == null;
  }

  /**
   * Returns the field value of given record as a week value.
   */
  public Week getWeek(int r) {
    return (Week) getObject();
  }

  /**
   * Returns the field value of the current record as an object
   */
  public Object getObjectImpl(int r) {
    return value[r];
  }
  
  @Override
  public String toText(Object o) {
    return o == null ? "" : toText((Week)o);
  }
  
  @Override
  public Object toObject(String s) throws VException {
    if (s.equals("")) {
      return null;
    }
    
    int week = 0;
    int year = -1;
    int bp = 0;
    int state;
    String      buffer = s + '\0';

    for (state = 1; state > 0; bp += 1) {
      switch (state) {
      case 1: /* The first week's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          week = buffer.charAt(bp) - '0';
          state = 2;
        } else {
          state = -1;
        }
        break;

      case 2: /* The second week's digit  */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          week = 10*week + (buffer.charAt(bp) - '0');
          state = 3;
          } else if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
            state = 4;
          } else if (buffer.charAt(bp) == '\0') {
            state = 0;
          } else {
            state = -1;
          }
        break;

      case 3: /* The first point : between week and year */
        if (buffer.charAt(bp) == '.' || buffer.charAt(bp) == '/') {
          state = 4;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 4: /* The first year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = buffer.charAt(bp) - '0';
          state = 5;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 5: /* The second year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 6;
        } else {
          state = -1;
        }
        break;

      case 6: /* The third year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 7;
        } else if (buffer.charAt(bp) == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;

      case 7:   /* The fourth year's digit */
        if (buffer.charAt(bp) >= '0' && buffer.charAt(bp) <= '9') {
          year = 10*year + (buffer.charAt(bp) - '0');
          state = 8;
        } else {
          state = -1;
        }
        break;

      case 8:   /* The end */
        if (buffer.charAt(bp)  == '\0') {
          state = 0;
        } else {
          state = -1;
        }
        break;
      default:
        throw new VFieldException(this, MessageCode.getMessage("VIS-00008"));
      }
    }
    if (state == -1) {
      throw new VFieldException(this, MessageCode.getMessage("VIS-00008"));
    }

    if (year == -1) {
      NotNullWeek       now = Week.now();

      year  = now.getYear();
    } else if (year < 50) {
      year += 2000;
    } else if (year < 100) {
      year += 1900;
    }

    return new NotNullWeek(year, week);
  }

  /**
   * Returns the display representation of field value of given record.
   */
  public String getTextImpl(int r) {
    if (value[r] == null) {
      return "";
    } else {
      return toText(value[r]);
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  public String getSqlImpl(int r) {
    return value[r] == null ? "NULL" : org.kopi.xkopi.lib.base.KopiUtils.toSql(value[r]);
  }

  /**
   * Copies the value of a record to another
   */
  public void copyRecord(int f, int t) {
    Week                oldValue;
    
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
   * Returns the data type handled by this field.
   */
  public Class getDataType() {
    return Week.class;
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a week value wrt the field type.
   */
  protected String formatWeek(Week value) {
    return toText(value);
  }

  /**
   *
   */
  public static String toText(Week value) {
    return value.toString();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private boolean isWeekChar(char c) {
    return ((c >= '0') && (c <= '9')) || (c == '.') || (c == '/');
  }

//   /**
//    * autofill
//    * @exception	org.kopi.vkopi.lib.visual.VException	an exception may occur in gotoNextField
//    */
//   public void autofill(boolean showDialog, boolean gotoNextField) throws VException {
//     if (list != null) {
//       super.autofill(showDialog, gotoNextField);
//     } else {
//       boolean force = false;

//       try {
// 	String	text = (String)getDisplayedValue();
// 	checkType(text);
// 	force = text == null || getText() == null || getText().equals("") || (!text.equals(getText()));
//       } catch (Exception e) {
// 	force = true;
//       }
//       if (!showDialog || force) {
// 	setWeek(Week.now());
//       } else {
// 	setWeek(new NotNullWeek(DateChooser.getDate(getForm().getDisplay(), getDisplay(), getWeek().getFirstDay())));
//       }
//       if (gotoNextField) {
// 	getBlock().gotoNextField();
//       }
//     }
//   }
  /**
   * autofill
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may occur in gotoNextField
   */
  public boolean fillField(PredefinedValueHandler handler) throws VException {
    if (list != null) {
      return super.fillField(handler);
    } else {
      boolean   force = false;

      try {
	String	oldText;
        String  newText;

        oldText = (String)getDisplayedValue(true);
	checkType(oldText);
        newText = getText(block.getActiveRecord());
	force = oldText == null || newText == null || newText.equals("") || !oldText.equals(newText);
      } catch (Exception e) {
	force = true;
      }
      if (handler == null || force) {
	setWeek(block.getActiveRecord(), Week.now());
      } else {
	setWeek(block.getActiveRecord(),
                new NotNullWeek(handler.selectDate(getWeek(block.getActiveRecord()).getFirstDay())));
      }
      return true;
    }
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  public boolean hasNextPreviousEntry() {
    return true;
  }

  /**
   * Checks that field value exists in list
   */
  protected void enumerateValue(boolean desc) throws VException {
    int         record = block.getActiveRecord();

    if (list != null) {
      super.enumerateValue(desc);
    } else if (isNull(record)) {
      autofill();
    } else {
      // try to read week
      try {
	checkType(getText(record));
      } catch (VException e) {
	// not valid, get now
	setWeek(record, Week.now());
      }

      setWeek(record, getWeek(record).add(desc ? -1 : 1));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Week[]			value;
}
