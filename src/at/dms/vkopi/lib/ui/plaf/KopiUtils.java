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

package at.dms.vkopi.lib.ui.plaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.border.*;
import at.dms.vkopi.lib.ui.base.*;

public class KopiUtils {

  public static void drawBackground(Graphics g, JTextComponent c, Rectangle clipRect) {
    if (c.getDocument() instanceof Stateful) {
      if (((Stateful) c.getDocument()).isAlert()) {
        g.setColor(color_alert);
      } else {
        if (c.hasFocus()) {
          if ((((Stateful) c.getDocument()).getState() & FieldStates.NOEDIT) != 0) {
            g.setColor(color_noEdit);
          } else {
            g.setColor(c.getBackground());
          }
        } else {
          g.setColor(getBackColor(g, (JComponent) c));
        }
      }
    } else {
      g.setColor(color_back);
    }

    g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
  }

  protected static Color getBackColor(Graphics g, JComponent c) {
    Color       background;

    if ((((JTextComponent)c).getDocument()) instanceof Stateful) {
      Stateful        fl = (Stateful) ((JTextComponent)c).getDocument();

      if ((fl.getState() & FieldStates.FLD_MASK) == FieldStates.SKIPPED || (fl.getState() & FieldStates.NOEDIT) > 0) {
        background = color_back_skipped;
      } else {
        if ((fl.getState() & FieldStates.ROLLOVER) != 0) {
          switch (fl.getState() & FieldStates.FLD_MASK) {
          case FieldStates.MUSTFILL:
            background = color_back_mustfill;
            break;
          case FieldStates.VISIT:
            background = color_back_visit;
            break;
          default:
            background = color_back;
          }
        } else {
          background = color_back;
        }
      }
    } else {
      background = color_back;
    }
    return background;
  }

  public static void drawKopiBorder(Graphics g, Component c, Rectangle clipRect) {
    /* Line at the bottom */
    if ((((JTextComponent) c).getDocument()) instanceof Stateful) {
      Stateful         fl = (Stateful) ((JTextComponent)c).getDocument();

      if (clipRect.height > 0) {
        if (fl.getAutofill()
            && clipRect.width > 20
            && (fl.getState() & FieldStates.FLD_MASK) > FieldStates.SKIPPED) {
          int           h = Math.max(clipRect.height, 20);
          int           w = clipRect.width;
          int[]         xPoints = new int[] {w-6, w-2, w-10};
          int[]         yPoints = new int[] {2, 7, 7};

          g.setColor(color_index_sign);
          g.fillPolygon(xPoints, yPoints, 3);
        }

        if ((fl.getState() & FieldStates.CHART) > 0) {
          if ((fl.getState() & FieldStates.ACTIVE) > 0) {
            g.setColor(color_border_chart_active);
          } else {
            g.setColor(color_border_chart);
          }
        } else {
          switch (fl.getState() & FieldStates.FLD_MASK) {
          case FieldStates.SKIPPED:
            g.setColor(color_underline_skipped);
            break;
          case FieldStates.MUSTFILL:
            g.setColor(color_underline_mustfill);
            break;
          case FieldStates.VISIT:
            g.setColor(color_underline_visit);
            break;
          default:
            g.setColor(color_underline);
          }
        }

        if ((fl.getState() & FieldStates.NOBORDER) == 0) {
          g.drawRect(clipRect.x, clipRect.y, clipRect.width-1, clipRect.height-1);
          g.fillRect(clipRect.x, clipRect.y, clipRect.width-1, underline_width);
        }
      }
    }
  }

  public static void drawActiveButtonBorder(Graphics g,
                                            Component c,
                                            Rectangle clipRect,
                                            Color active1,
                                            Color active2,
                                            Color border) {
    drawActiveButtonBorder(g, c, clipRect, active1, active2, border, 3, 7);
  }
  public static void drawActiveButtonBorder(Graphics g,
                                            Component c,
                                            Rectangle clipRect,
                                            Color active1,
                                            Color active2,
                                            Color border,
                                            int width,
                                            int arc) {
    GradientPaint       gp;
    Graphics2D          g2d = (Graphics2D) g;

    gp = new GradientPaint(clipRect.x,
                           clipRect.y,
                           active2,
                           clipRect.x,
                           clipRect.y + clipRect.height,
                           active1);
    g2d.setPaint(gp);
    g.fillRoundRect(clipRect.x, clipRect.y, clipRect.width, width, arc , arc);
    g.fillRoundRect(clipRect.x, clipRect.y, width, clipRect.height, arc , arc);
    g.fillRoundRect(clipRect.x+clipRect.width-width, clipRect.y, width, clipRect.height, arc , arc);
    g.fillRoundRect(clipRect.x, clipRect.y+clipRect.height-width, clipRect.width, width, arc , arc);
    g.setColor(border);
    g.drawRoundRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height, arc , arc);
  }

  static class KopiButtonBorder implements javax.swing.border.Border {
    /**
     *
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      getBorder(c).paintBorder(c, g, x, y, width, height);
    }

    /**
     *
     */
    public Insets getBorderInsets(Component c) {
      return getBorder(c).getBorderInsets(c);
    }

    /**
     *
     */
    public boolean isBorderOpaque() {
      return true;
    }

    private javax.swing.border.Border getBorder(Component c) {
      AbstractButton    ab = (AbstractButton) c;

      if (ab.getModel().isEnabled()) {
 	if (ab.getModel().isPressed()) {
          return BRD_BTN_PRESSED;
 	} else if (ab.getModel().isRollover()) {
           return BRD_BTN_ROLL;
 	}
       }
       return BRD_BTN_EMPTY;
     }
  }

  static class ButtonRollBorder extends LineBorder {

    public ButtonRollBorder(Color color)  {
      super(color, 3, true);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      int i;

      g.setColor(lineColor);
      g.drawRoundRect(x, y, width-1, height-1, border_arc, border_arc);
      g.setColor(oldColor);
    }
  }
  static class ButtonLineBorder extends LineBorder {

    public ButtonLineBorder(Color color)  {
      super(color, 3, true);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      int i;

      g.setColor(lineColor);
      g.drawRoundRect(x, y, width-1, height-1, border_arc, border_arc);
      g.setColor(oldColor);
    }
  }

  static class KopiFieldBorder extends LineBorder {
    public KopiFieldBorder(JTextComponent textfield)  {
      super(Color.white, 2, true);
      this.textfield = textfield;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      drawKopiBorder(g, textfield, new Rectangle(x,y,width, height));
    }

    private JTextComponent      textfield;
  }

  private static final int      border_arc = UIManager.getInt("MenuButton.border.arc");

  private static final Color    color_border = UIManager.getColor("MenuButton.border");
  private static final Color    color_border_b = UIManager.getColor("MenuButton.border.highlight");
  private static final Color    color_border_d = UIManager.getColor("MenuButton.border.darkshadow");

  private static Border         BRD_BTN_ROLL = new ButtonRollBorder(color_border);
  private static Border         BRD_BTN_PRESSED = new ButtonLineBorder(color_border);
  private static Border         BRD_BTN_EMPTY = new EmptyBorder(3, 3, 3, 3);

  private static final Color    color_underline  = UIManager.getColor("KopiField.ul.color");
  private static final Color    color_underline_visit  = UIManager.getColor("KopiField.ul.visit.color");
  private static final Color    color_underline_skipped = UIManager.getColor("KopiField.ul.skipped.color");
  private static final Color    color_underline_mustfill = UIManager.getColor("KopiField.ul.mustfill.color");
  private static final Color    color_border_chart        = UIManager.getColor("KopiField.ul.chart");
  private static final Color    color_border_chart_active = UIManager.getColor("KopiField.ul.chart.active");
  private static final Color    color_index_sign          = UIManager.getColor("KopiField.index");

  private static final int      underline_width     = UIManager.getInt("KopiField.ul.width");
  private static Color          color_alert       = UIManager.getColor("KopiField.alert");
  private static Color          color_noEdit      = UIManager.getColor("KopiField.noedit");

  protected static final Color  color_focused;
  protected static final Color  color_skipped;
  protected static final Color  color_mustfill;
  protected static final Color  color_visit;

  protected static final Color  color_back;
  protected static final Color  color_back_mustfill;
  protected static final Color  color_back_visit;
  protected static final Color  color_back_skipped;

  static {
    color_focused       = UIManager.getColor("KopiField.focused.color");
    color_skipped       = UIManager.getColor("KopiField.skipped.color");
    color_mustfill      = UIManager.getColor("KopiField.mustfill.color");
    color_visit         = UIManager.getColor("KopiField.visit.color");

    color_back          = UIManager.getColor("KopiField.background.color");
    color_back_skipped  = UIManager.getColor("KopiField.background.skipped.color");
    color_back_visit    = UIManager.getColor("KopiField.background.visit.color");
    color_back_mustfill = UIManager.getColor("KopiField.background.mustfill.color");
  }

  private static final int      TXT_Y_SPACE = UIManager.getInt("FieldText.y.space");
  private static final int      TXT_X_SPACE = UIManager.getInt("FieldText.x.space");

}
