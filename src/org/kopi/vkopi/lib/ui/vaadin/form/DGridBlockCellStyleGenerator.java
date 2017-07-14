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

package org.kopi.vkopi.lib.ui.vaadin.form;

import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.ui.vaadin.base.StylesInjector;
import org.kopi.vkopi.lib.ui.vaadin.visual.VApplication;
import org.kopi.vkopi.lib.visual.ApplicationContext;

import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;

/**
 * Cell style generator for grid block.
 */
@SuppressWarnings("serial")
public class DGridBlockCellStyleGenerator implements CellStyleGenerator {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridBlockCellStyleGenerator(VBlock model) {
    this.model = model;
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  public String getStyle(CellReference cell) {
    if (cell.getPropertyId() == null || !isRecordFilled(cell)) {
      return null;
    } else {
      VField                    field;
      StylesInjector            injector;

      field = getField(cell);
      injector = ((VApplication)ApplicationContext.getApplicationContext().getApplication()).getStylesInjector();
      
      return injector.createAndInjectStyle(field.getAlign(),
                                           field.getForeground((Integer)cell.getItemId()),
                                           field.getBackground((Integer)cell.getItemId()));
    }
  }
  
  /**
   * Returns the field model of a given cell reference.
   * @param cell The cell reference.
   * @return The field model.
   */
  protected VField getField(CellReference cell) {
    return model.getFields()[(Integer)cell.getPropertyId()];
  }
  
  /**
   * Returns if the given record is filled. The check will be in two stages :
   * 1- Check if the block tells that record is filled or not (not really reliable cause records are to set always to changed)
   * 2- Check if the cell is filled for the given cell reference.
   *
   * @param cell The cell reference.
   * @return true if the record is considered as filled for the given cell reference.
   */
  protected boolean isRecordFilled(CellReference cell) {
    return model.isRecordFilled((Integer)cell.getItemId())
     || getField(cell).getObject((Integer)cell.getItemId()) != null;
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  private final VBlock                  model;
}
