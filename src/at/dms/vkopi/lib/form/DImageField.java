/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: DImageField.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import at.dms.vkopi.lib.visual.DObject;

/**
 * DImageField is a panel composed in a Image field and a label behind
 */
public class DImageField extends DObjectField {

  // ----------------------------------------------------------------------
  // CONSTRUCTION
  // ----------------------------------------------------------------------

  /**
   * Constructor
   *
   * @param	model		the model for this text field
   * @param	label		The label that describe this field
   * @param	height		the number of line
   * @param	width		the number of column
   * @param	options		The possible options (NO EDIT, NO ECHO)
   */
  public DImageField(VFieldUI model,
		     DLabel label,
		     int align,
		     int options,
		     int width,
		     int height)
  {
    super(model, label, align, options);
    this.width = width;
    this.height = height;

    icon = new JLabel();

    empty = new JPanel() {
      public Dimension getPreferredSize() {
	return new Dimension(DImageField.this.width, DImageField.this.height);
      }
    };

    //    empty.setBackground(DObject.CLR_FLD_BACK);

    add(empty, BorderLayout.CENTER);

    registerKeyboardAction(new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
	setObject(null);
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
      JComponent.WHEN_FOCUSED);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns the object associed to record r
   *
   * @param	r		the position of the record
   * @return	the displayed value at this position
   */
  public Object getObject() {
    return image;
  }

  /**
   * Sets the object associed to record r
   *
   * @param	r		the position of the record
   * @param	s		the object to set in
   */
  public void setObject(Object s) {
    if (image == null) {
      remove(empty);
    } else {
      remove(icon);
    }

    image = (byte[])s;

    if (image != null) {
      icon.setIcon(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(width, height, Image.SCALE_FAST)));
    }

    if (image == null) {
      add(empty, BorderLayout.CENTER);
    } else {
      add(icon, BorderLayout.CENTER);
    }

    setBlink(false);
    setBlink(true);
    // NOT USED ANYMORE
//     fireValueHasChanged();
//     repaint();
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  public void setDisplayProperties() {
    JComponent c;
    if (image == null) {
      c = empty;
    } else {
      c = icon;
    }

    //    c.setBackground(DObject.CLR_FLD_BACK);

//     switch (state) {
//     case STE_SKIPPED:
//       c.setBorder(DObject.BRD_SKIPD);
//       break;
//     case STE_FOCUSED:
//       c.setBorder(DObject.BRD_SLCTD);
//       break;
//     case STE_ROLLOVER_MUSTFILL:
//       c.setBorder(DObject.BRD_ROLLOVER);
//       break;
//     case STE_ROLLOVER_VISIT:
//       c.setBorder(DObject.BRD_ROLLOVER);
//       break;
//     case STE_CHART:
//       c.setBorder(DObject.BRD_MULTI);
//       break;
//     default:
//       c.setBorder(DObject.BRD_FLD);
//     }

    repaint();
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------

  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */
  public void update() {
//     if (textChanged) {
//       setObject(((VImageField)getModel()).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())));
//     }
//     if (accessChanged || focusChanged) {
//       label.update(getModel());
//     }
//     if (focusChanged) {
//       fireMouseHasChanged();
//     }
    super.update();
  }

  public void updateAccess() {
    label.update(getModel(), getPosition());
  }

  public void updateText() {
    setObject(((VImageField)getModel()).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())));
    super.updateText();
  }

  public void updateFocus() {
    label.update(getModel(), getPosition());
    fireMouseHasChanged();
    super.updateFocus();
  }

  /**
   * set blink state
   */
  public void setBlink(boolean start) {
  }


  // NOT USED
 //  /*
//    * !!! graf 990809 CHANGE !!! THIS IS ONLY A PLACEHOLDER !!!
//    */
//   public JTextComponent getTextComponent() {
//     return null;
//   }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	JLabel		icon;		// the image component
  private	JPanel		empty;		// the no image component
  private	int		width;
  private	int		height;
  private	byte[]		image;
}
