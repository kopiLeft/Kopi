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

import org.kopi.vkopi.lib.form.FieldHandler;
import org.kopi.vkopi.lib.form.UBlock;
import org.kopi.vkopi.lib.form.UChartLabel;
import org.kopi.vkopi.lib.form.UField;
import org.kopi.vkopi.lib.form.ULabel;
import org.kopi.vkopi.lib.form.VBlock.OrderModel;
import org.kopi.vkopi.lib.form.VBooleanField;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField;
import org.kopi.vkopi.lib.visual.VException;

/**
 * A row controller for the grid block implementation
 */
@SuppressWarnings("serial")
public class DGridBlockFieldUI extends DFieldUI {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public DGridBlockFieldUI(UBlock blockView, VField model, int index) {
    super(blockView, model, index);
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  protected UField createDisplay(ULabel label, VField model, boolean detail) {
    if (detail) {
      return super.createDisplay(label, model, detail);
    } else {
      DGridEditorField<?>         field;

      switch (model.getType()) {
      case VField.MDL_FLD_EDITOR:
      case VField.MDL_FLD_TEXT:
        if (model instanceof VBooleanField) {
          field = new DGridEditorBooleanField(this, (DGridEditorLabel)label, model.getAlign(), model.getOptions());
        } else {
          field = new DGridTextEditorField(this, (DGridEditorLabel)label, model.getAlign(), model.getOptions());
        }
        break;
      case VField.MDL_FLD_IMAGE:
        field = new DGridEditorImageField(this, (DGridEditorLabel)label, model.getAlign(), ((VImageField) model).getIconWidth(), ((VImageField) model).getIconHeight(), model.getOptions());
        break;
      case VField.MDL_FLD_ACTOR:
        field = new DGridEditorActorField(this, (DGridEditorLabel)label, model.getAlign(), model.getOptions());
        break;
      default:
        return super.createDisplay(label, model, detail);
      }

      return field;
    }
  }
  
  @Override
  protected ULabel createLabel(String text, String help, boolean detail) {
    if (detail) {
      return super.createLabel(text, help, detail);
    } else {
      return new DGridEditorLabel(text, help);
    }
  }
  
  @Override
  protected UChartLabel createChartHeaderLabel(String text,
                                               String help,
                                               int index,
                                               OrderModel model)
  {
    return new DGridEditorLabel(text, help);
  }
  
  @Override
  protected FieldHandler createFieldHandler() {
    return new DGridBlockFieldHandler(this);
  }

  @Override
  protected void fireDisplayCreated() {
    // no client side cache
  }
  
  @Override
  protected int getDisplaySize() {
    return 1;
  }
  
  @Override
  public void scrollTo(int toprec) {
    if (getModel().hasFocus()) {
      getFieldHandler().enter();
    }
  }
  
  @Override
  protected void gotoActiveRecord(boolean force) throws VException {
    getBlockView().editRecord(getBlock().getActiveRecord());
  }
  
  /**
   * Returns the grid editor display of this row controller.
   * @return The grid editor display of this row controller.
   */
  protected DGridEditorField<?> getEditorField() {
    return (DGridEditorField<?>)getDisplays()[0];
  }
  
  /**
   * Returns the grid editor field associated with this column view.
   * @return The grid editor field associated with this column view.
   */
  protected GridEditorField<?> getEditor() {
    return getEditorField().getEditor();
  }
  
  /**
   * Returns true if the column view has a chart display for this field model.
   * @return True if the column view has a chart display for this field model.
   */
  protected boolean hasDisplays() {
    return getDisplays() != null;
  }
  
  @Override
  public DGridBlock getBlockView() {
    return (DGridBlock) super.getBlockView();
  }
}
