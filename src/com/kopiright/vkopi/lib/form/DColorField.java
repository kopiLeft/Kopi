/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.form;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * DColorField is a panel composed in a color field and a label behind
 */
public class DColorField extends DObjectField {

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
  public DColorField(VFieldUI model,
		     DLabel label,
		     int align,
		     int options,
                      boolean detail)
  {
    super(model, label, align, options, detail);

    SIZE = textHeight + 4; // border size
    add(inner, BorderLayout.CENTER);
    inner.setPreferredSize(new Dimension(SIZE, SIZE));
    inner.setOpaque(true);

    registerKeyboardAction(new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
	setObject(null);
	update();
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
    image = (Color)s;
    fireMouseHasChanged();
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  public void setDisplayProperties() {
  //   switch (state) {
//     case STE_SKIPPED:
//       inner.setBackground(image);
//       inner.setBorder(DObject.BRD_SKIPD);
//       break;
//     case STE_FOCUSED:
//       inner.setBackground(image);
//       inner.setBorder(DObject.BRD_SLCTD);
//       break;
//     case STE_ROLLOVER_MUSTFILL:
//       inner.setBackground(DObject.CLR_MUSTFILL);
//       inner.setBorder(DObject.BRD_ROLLOVER);
//       break;
//     case STE_ROLLOVER_VISIT:
//       inner.setBackground(DObject.CLR_OTHERS);
//       inner.setBorder(DObject.BRD_ROLLOVER);
//       break;
//     case STE_CHART:
//       inner.setBackground(image);
//       inner.setBorder(DObject.BRD_MULTI);
//       break;
//     default:
//       inner.setBackground(image);
//       inner.setBorder(DObject.BRD_MULTI);
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
//       setObject(((VColorField)getModel()).getColor(model.getBlockView().getRecordFromDisplayLine(getPosition())));
//     }

//     if (accessChanged || focusChanged) {
//       label.update(getModel());
//     }

//     if (focusChanged) {
//       fireMouseHasChanged();
//     }

    if (image != null) {
      inner.setBackground(image);
    } else {
      //      inner.setBackground(DObject.CLR_FLD_BACK);
    }

    super.update();
  }

  private void updateAlways() {
    if (image != null) {
      inner.setBackground(image);
    }
  }

  public void updateAccess() {
    label.update(getModel(), getPosition());
    updateAlways();
    super.updateAccess();
  }

  public void updateText() {
    setObject(((VColorField)getModel()).getColor(model.getBlockView().getRecordFromDisplayLine(getPosition())));
    updateAlways();
    super.updateText();
  }

  public void updateFocus() {
    label.update(getModel(), getPosition());
    fireMouseHasChanged();    
    updateAlways();
    super.updateFocus();
  }

  /**
   * set blink state
   */
  public void setBlink(boolean start) {
  }

  // NOT USED
//   /*
//    * !!! graf 990809 CHANGE !!! THIS IS ONLY A PLACEHOLDER !!!
//    */
//   public JTextComponent getTextComponent() {
//     return null;
//   }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private static int		SIZE;

  private JPanel		inner = new JPanel();
  private Color			image;

  private static final int      textHeight;

  static {
    Font                font = UIManager.getFont("KopiLayout.font");
    FontMetrics         fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

    textHeight = fm.getHeight();
  }
}
