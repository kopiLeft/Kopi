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

package at.dms.vkopi.lib.ui.base;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class ListDialogCellRenderer implements TableCellRenderer {

  public interface ObjectFormater {
    /**
     * Returns a representation of value
     */
    Object formatObject(Object value) ;
    /**
     * Returns the column alignment
     */
    int getAlign();
  }

  public ListDialogCellRenderer(ObjectFormater[] columns) {
    this.columns = columns;
  }


  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    int	col = table.convertColumnIndexToModel(column);
    Object	obj = columns[col].formatObject(value);
    
    if (obj instanceof String) {
      renderer.set(columns[column].getAlign(), (String)obj, isSelected);
      
      return renderer;
    } else {
      return new JLabel((javax.swing.ImageIcon)obj);
    }
  }

  private static class CellRenderer extends JComponent {
    
    public void paint(Graphics g) {
      Dimension   size = getSize();
      
      g.setColor(isSelected ? color_back_sel : color_back);
      g.fillRect(0, 0, size.width, size.height);
      
      if (str != null && str != "" && str.length() != 0) {
        g.setColor(Color.black);
        g.setColor(isSelected ? color_fore_sel : color_fore);
        g.setFont(font_dialog);

        int             left;
        FontMetrics     fm = g.getFontMetrics();
        int             stringWidth = fm.stringWidth(str);

        if (align == JLabel.RIGHT) {
          left = size.width - stringWidth - 2;
          //} else if (align == VConstants.ALG_CENTER) {
            // left = (size.width - DObject.FNT_FIXED_WIDTH * str.length()) / 2 - 2;
        } else {
          left = 2;
        }
        g.drawString(str, left, fm.getHeight() - 2);
      }
    }
    
    public void set(int align, String value, boolean isSelected) {
      this.align = align;
      this.str = value;
      this.isSelected = isSelected;
    }

    public Dimension getPreferredSize() {
      Dimension         dim = super.getPreferredSize();

      return new Dimension(dim.width+2, dim.height+2);
    }

    private	int		align;
    private	boolean		isSelected;
    private	String		str;
  }
  
  private ObjectFormater[]		columns;
  private static final CellRenderer     renderer = new CellRenderer();

  private static final Color    color_back     = UIManager.getColor("ListDialog.background");
  private static final Color    color_back_sel = UIManager.getColor("ListDialog.background.selected");
  private static final Color    color_fore     = UIManager.getColor("ListDialog.foreground");
  private static final Color    color_fore_sel = UIManager.getColor("ListDialog.foreground.selected");

  private static final Font     font_dialog    = UIManager.getFont("ListDialog.font");
}
