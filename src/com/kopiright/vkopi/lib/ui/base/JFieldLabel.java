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

package com.kopiright.vkopi.lib.ui.base;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class JFieldLabel extends JLabel implements FieldStates {
  /**
   * Creates a <code>JFieldLabel</code> instance with the specified text.
   * The label is for fields in a form
   *
   * @param text  The text to be displayed by the label.
   */
  public JFieldLabel(String text) {
    super(text);
  }

  public JFieldLabel() {
    super();
  }

  public Dimension getPreferredSize() {
    Dimension           dim = super.getPreferredSize();
    FontMetrics         fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());

    return new Dimension(dim.width + (((state & FieldStates.CHART) == 0) ? TXT_X_SPACE : 0), 
                         fm.getHeight() + TXT_Y_SPACE);
  }

  public String getUIClassID() {
    return "FieldLabelUI";
  }


  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public void setInfoText(String txt) {
    infoText = txt;
    repaint();
  }
  public String getInfoText() {
    return infoText;
  }

  private String        infoText;

  private int           state;
  private static int    TXT_Y_SPACE = UIManager.getInt("FieldText.y.space");
  private static int    TXT_X_SPACE = UIManager.getInt("KopiLabel.x.space");
 }
