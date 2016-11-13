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

import org.kopi.vkopi.lib.list.VListColumn;
import org.kopi.vkopi.lib.visual.VException;

@SuppressWarnings("serial")
public abstract class AbstractPredefinedValueHandler implements PredefinedValueHandler {

  //----------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------

  public AbstractPredefinedValueHandler(VFieldUI model, VForm form, VField field) {
    this.model = model;
    this.form = form;
    this.field = field;
  }

  public boolean selectDefaultValue() throws VException {
    return model.fillField();
  }

  public String selectFromList(VListColumn[] list,
                               Object[][] values,
                               String[] predefinedValues)
  {
    final int		selected;
    final VListDialog	listDialog;

    listDialog = new VListDialog(list, values);
    selected = listDialog.selectFromDialog(form, field);

    if (selected != -1) {
      return predefinedValues[selected];
    } else {
      return null;
    }
  }

  //----------------------------------------------------------
  // DATA MEMBERS
  //----------------------------------------------------------

  private VFieldUI      		model;
  protected VForm         		form;
  protected VField        		field;
}
