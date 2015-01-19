/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import org.kopi.vaadin.fields.AbstractField;
import org.kopi.vaadin.fields.CodeField;
import org.kopi.vaadin.fields.DateField;
import org.kopi.vaadin.fields.FixNumField;
import org.kopi.vaadin.fields.IntegerField;
import org.kopi.vaadin.fields.MonthField;
import org.kopi.vaadin.fields.NoEditableField;
import org.kopi.vaadin.fields.NoEditableTextArea;
import org.kopi.vaadin.fields.PasswordField;
import org.kopi.vaadin.fields.StringField;
import org.kopi.vaadin.fields.TimeField;
import org.kopi.vaadin.fields.TimestampField;
import org.kopi.vaadin.fields.WeekField;
import org.kopi.vaadin.fields.TextArea;

import com.kopiright.vkopi.lib.form.VCodeField;
import com.kopiright.vkopi.lib.form.VDateField;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFixnumField;
import com.kopiright.vkopi.lib.form.VIntegerField;
import com.kopiright.vkopi.lib.form.VMonthField;
import com.kopiright.vkopi.lib.form.VStringField;
import com.kopiright.vkopi.lib.form.VTimeField;
import com.kopiright.vkopi.lib.form.VTimestampField;
import com.kopiright.vkopi.lib.form.VWeekField;
import com.vaadin.server.Sizeable.Unit;

/**
 * The <code>FieldFactory</code> generates the field
 * view instance according to its model. The field views
 * are special fields implemented as vaadin addons located
 * in the <code>org.kopi.vaadin.fields</code> package.
 */
public class FieldFactory {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the UI field representation according to its model.
   * @param model The field model.
   * @param noedit Is it a no edit field ?
   * @param noEcho Is it a no echo field ?
   * @param scanner Is it a scanner field ?
   * @return The UI field representation according to its model.
   */
  public static AbstractField createField(VField model,
                                          boolean noedit,
                                          boolean noEcho,
                                          boolean scanner)
  {
    AbstractField		field = null;
	  
    if (noEcho && model.getHeight() == 1) {
      // password field.
      field = new PasswordField(model.getWidth());
    } else {
      if (model instanceof VStringField) {
	if (model.getHeight() == 1) {
	  if (noedit) {
	    // no edit field
	    field = new NoEditableField(model.getWidth());
	  } else {
	    // normal string field.
	    field = new StringField(model.getWidth());
	  }
	} else {
	  int		col;
		  
	  if (scanner) {
	    col = 40;
	  } else {
	    col = model.getWidth();
	  }
	  if (noedit) {
	    // no edit text area
	    field = new NoEditableTextArea(col,
		                           model.getHeight(),
		                           ((VStringField)model).getVisibleHeight() - 1);
	  } else {
	    // normal text area
	    field = new TextArea(col,
		                 model.getHeight(),
		                 ((VStringField)model).getVisibleHeight() - 1);
	  }
	}
      } else if (model instanceof VIntegerField) {
	// integer field
    	field = new IntegerField(model.getWidth());
      } else if (model instanceof VMonthField) {
	// month field
    	field = new MonthField();
      } else if (model instanceof VDateField) {
	// date field
    	field = new DateField();
      } else if (model instanceof VWeekField) {
	// week field
    	field = new WeekField();
      } else if (model instanceof VTimeField) {
	// time field
    	field = new TimeField();
      } else if (model instanceof VCodeField) {
	// code field
    	field = new CodeField(model.getWidth(), ((VCodeField)model).getLabels());
      } else if (model instanceof VFixnumField) {
	// fixnum field
    	field = new FixNumField(model.getWidth());
      } else if (model instanceof VTimestampField) {
	// timestamp field
    	field = new TimestampField();
      } else {
	throw new IllegalArgumentException("unknown field model : " + model.getClass().getName());
      }
    }
	    
    field.addStyleName(KopiTheme.MONOSPACE);
    field.setWidth((field.getWidth() * 7) + 5, Unit.PIXELS);
    return field;
  }
}
