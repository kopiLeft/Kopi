/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.vkopi.comp.print;

import java.util.Vector;
import java.awt.Color;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.vkopi.comp.base.VKUtils;

/**
 * Some utilities to generate java code
 */
public class PRUtils extends VKUtils {

  /**
   * Sets style info
   */
  public static JExpressionStatement setStyle(TokenReference ref, String ident, String action, JExpression value) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      new JNameExpression(ref, "StyleConstants"),
							      action,
							      new JExpression[] {
								getStyle(ref, ident)
							      }),
				    null);
  }

  /**
   * Gets a style
   */
  public static JExpression getStyle(TokenReference ref, String ident) {
    return new JMethodCallExpression(ref,
				     new JNameExpression(ref, "styles"),
				     "getStyle",
				     new JExpression[]{
				       new JStringLiteral(ref, ident)
				     });
  }

  /**
   * New Color
   */
  public static JExpression toExpression(TokenReference ref, Color c) {
    if (c == null) {
      return new JNullLiteral(ref);
    } else {
      return new JUnqualifiedInstanceCreation(ref,
				      COLOR_TYPE,
				      new JExpression[]{
					VKUtils.toExpression(ref, c.getRed()),
					VKUtils.toExpression(ref, c.getGreen()),
					VKUtils.toExpression(ref, c.getBlue())
				      });
    }
  }

  /**
   * Create a new style
   */
  public static JExpressionStatement addText(TokenReference ref, String text) {
    return addExpression(ref, new JStringLiteral(ref, text));
  }

  /**
   * Create a new style
   */
  public static JExpression format(TokenReference ref, JExpression exp) {
    return new JMethodCallExpression(ref, null, "format", new JExpression[]{ exp });
  }

  /**
   * Create a new style
   */
  public static JExpressionStatement addExpression(TokenReference ref, JExpression exp) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "addExpression",
							      new JExpression[]{ exp }),
				    null);
  }

  /**
   * Sets the style of the block
   */
  public static JExpressionStatement genInit(TokenReference ref) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      new JNameExpression(ref, "block"),
							      "init",
							      new JExpression[] {
								new JNameExpression(ref, "styles")
							      }),
				    null);
  }

  /**
   * Gets a style
   */
  public static JExpression getBlockStyle(TokenReference ref, String ident) {
    return new JStringLiteral(ref, ident);
  }

  /**
   * Sets the style of the block
   */
  public static JExpressionStatement setStyle(TokenReference ref, String style, boolean hasBang) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "setStyle",
							      new JExpression[] {
								getBlockStyle(ref, style),
								toExpression(ref, hasBang)
							      }),
				    null);
  }

  /**
   * Sets the style of the block
   */
  public static JExpressionStatement setStyles(TokenReference ref, Vector styles) {
    JExpression[]	init = new JExpression[styles.size()];
    for (int i = 0; i < styles.size(); i++) {
      init[i] = getBlockStyle(ref, (String)styles.elementAt(i));
    }
    JExpression		expr;

    expr = new JCastExpression(ref, new JNameExpression(ref, "block"), LIST_TYPE);
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      new JParenthesedExpression(ref, expr),
							      "setStyles",
							      new JExpression[]{
								VKUtils.createArray(ref, BLOCK_STYLE_TYPE, init)
							      }),
				    null);
  }

  /**
   * Sets the style of the block
   */
  public static JExpressionStatement setTab(TokenReference ref, String style) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "setTab",
							      new JExpression[] {
								toExpression(ref, style)
							      }),
				    null);
  }

  /**
   * Sets the style of the block
   */
  public static JExpressionStatement addBlock(TokenReference ref, JExpression block) {
    return new JExpressionStatement(ref,
				    new JMethodCallExpression(ref,
							      null,
							      "addBlock",
							      new JExpression[]{ block }),
				    null);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMEBERS
  // ----------------------------------------------------------------------

  private static CReferenceType	COLOR_TYPE	= CReferenceType.lookup("java/awt/Color");
  private static CReferenceType	LIST_TYPE	= CReferenceType.lookup(com.kopiright.vkopi.lib.print.PListBlock.class.getName().replace('.','/'));
  private static CReferenceType	BLOCK_STYLE_TYPE= CReferenceType.lookup(com.kopiright.vkopi.lib.print.PBlockStyle.class.getName().replace('.','/'));
}
