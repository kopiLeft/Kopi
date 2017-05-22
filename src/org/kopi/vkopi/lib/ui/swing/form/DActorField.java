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

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.kopi.vkopi.lib.form.UActorField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.swing.base.JActorFieldButton;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

/**
 * UI Implementation of actor field in swing environment.
 */
public class DActorField extends DField implements UActorField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public DActorField(VFieldUI model,
                     DLabel label,
                     int align,
                     int options,
                     boolean detail)
  {
    super(model, label, align, options, detail);
    button = createButton();
    if (button != null) {
      add(button, BorderLayout.CENTER);
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  @Override
  public void updateAccess() {
    super.updateAccess();
    if (button != null) {
      button.getAction().setEnabled(access >= VConstants.ACS_VISIT);
    }
  }
  
  @Override
  public void updateText() {
    if (button != null) {
      String      newModelTxt = getModel().getText(getRowController().getBlockView().getRecordFromDisplayLine(getPosition()));
      String      currentModelTxt = button.getValue();
      
      if ((newModelTxt == null && currentModelTxt != null) || !newModelTxt.equals(currentModelTxt)) {
        button.setValue(newModelTxt);
      }
    }
  }
  
  @Override
  public void updateFocus() {
    // NO FOCUS FOR ACTOR FIELDS
  }
  
  @Override
  public void forceFocus() {
    // NO FOCUS FOR ACTOR FIELDS
  }
  
  public void setBlink(boolean blink) {}

  public void updateColor() {
    // NOT SUPPORTED
  }

  public Object getObject() {
    return getText();
  }

  protected void setDisplayProperties() {
    // NOT SUPPORTED
  }

  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }

  public void setHasCriticalValue(boolean b) {}

  public void addSelectionFocusListener() {}

  public void removeSelectionFocusListener() {}

  public void setSelectionAfterUpdateDisabled(boolean disable) {}

  /**
   * Creates the field button
   * @return The created button
   */
  protected JActorFieldButton createButton() {
    DActorFieldAction           action;
    
    action = new DActorFieldAction(getModel().getLabel(), getModel().getIcon() != null ? loadImage(getModel().getIcon()) : null);
    action.putValue(Action.SHORT_DESCRIPTION, getModel().getToolTip());
    action.setEnabled(getModel().getDefaultAccess() >= VConstants.ACS_VISIT);
    
    return new JActorFieldButton(action);
  }

  /**
   * Loads an image icon from resource directory.
   * @param iconName The icon name.
   * @return The loaded image icon.
   */
  protected ImageIcon loadImage(String iconName) {
    ImageIcon   image;

    image = Utils.getImage(iconName + ".png");

    if (image == null || image == Utils.UKN_IMAGE) {
      image = Utils.getImage(iconName + ".gif");
    }

    return new ImageIcon(image.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
  }

  // --------------------------------------------------------------------
  // FILD ACTION
  // --------------------------------------------------------------------

  private class DActorFieldAction extends AbstractAction {

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    public DActorFieldAction(String name, Icon icon) {
      super(name, icon);
    }

    // ------------------------------------------------
    // MPLEMNTATION
    // ------------------------------------------------

    public void actionPerformed(ActionEvent e) {
      if (getModel().hasTrigger(VConstants.TRG_ACTION)) {
        getRowController().performAsyncAction(new KopiAction("FIELD_ACTION") {
          
          public void execute() throws VException {
            getModel().callTrigger(VConstants.TRG_ACTION);
          }
        });
      }
    }

    // -------------------------------------------------
    // DATA MEMBERS
    // -------------------------------------------------

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2304974107105391550L;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private final JActorFieldButton               button;
  /**
   * Serial version UID
   */
  private static final long                     serialVersionUID = 3697344873853787723L;
}
