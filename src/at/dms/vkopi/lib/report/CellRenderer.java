/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.report;

import javax.swing.JComponent;
import java.awt.*;

/**
 * @version 1.0 11/09/98
 */
public class CellRenderer extends JComponent {

  protected CellRenderer(int state) {
    this.state = state;
  }

  protected CellRenderer(int state,
			 int align,
			 Color back,
			 Color fore,
			 Font font) {
    this.state = state;
    this.align = align;
    this.back = back;
    this.fore = fore;
    this.font = font;
    this.metrics = Toolkit.getDefaultToolkit().getFontMetrics(font); // $$$
  }

  public void paint(Graphics g) {
    Dimension   size = getSize();
    Color	back;
    switch (state) {
    case Constants.STA_FOLDED:
      back = Color.lightGray;
      break;
    case Constants.STA_SEPARATOR:
      back = Color.red;
      break;
    default:
      back = selected ? Color.black : this.back == Color.white ? level : this.back;
    }
    g.setColor(back);
    g.fillRect(0, 0, size.width, size.height);

    switch (state) {
    case Constants.STA_FOLDED:
    case Constants.STA_SEPARATOR:
      break;
    default:
      if (state != Constants.STA_SEPARATOR && str != null) {
	Color		fore = selected ? level : this.fore;
	int		left;
	int		index = 0, oldIndex = 0;
	int		line = 0;

	g.setColor(fore);
	g.setFont(font);

	while ((index = str.indexOf('\n', oldIndex)) != -1) {
	  if (align == Constants.ALG_RIGHT) {
	    left = size.width - metrics.stringWidth(str.substring(oldIndex, index)) - 2;
	    //} else if (align == Constants.ALG_CENTER){
	    //left = (size.width - metrics.stringWidth(str)) / 2 - 2;
	  } else {
	    left = 2;
	  }
	  g.drawString(str.substring(oldIndex, index), left, (line++ + 1) * metrics.getHeight() - 2);
	  oldIndex = index + 1;
	}
	if (align == Constants.ALG_RIGHT) {
	  left = size.width - metrics.stringWidth(str.substring(oldIndex)) - 2; // $$$
	  //} else if (align == Constants.ALG_CENTER) {
	  //left = (size.width - metrics.stringWidth(str)) / 2 - 2;
	} else {
	  left = 2;
	}
	g.drawString(str.substring(oldIndex), left, (line++ + 1) * metrics.getHeight() - 2);
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public int getState() {
    return state;
  }

  public void set(String value, boolean selected, Color color) {
    str = value;
    level = color;
    this.selected = selected;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	int		state;
  private	int		align;
  private	Color		back;
  private	Color		fore;
  private	Font		font;
  private	FontMetrics	metrics;
  private	Color		level;
  private	boolean		selected;
  private	String		str;
}
