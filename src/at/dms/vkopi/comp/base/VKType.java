/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.comp.base;

import java.io.PrintWriter;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.database.DatabaseColumn;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.JExpression;

/**
 * This class represents the definition of a type
 */
public abstract class VKType extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param width		the width in char of this field
   * @param height		the height in char of this field
   */
  public VKType(TokenReference where, int width, int height) {
    this(where, width, height, height);
  }

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param width		the width in char of this field
   * @param height		the height in char of this field
   * @param visibleHeight	the visible height in char of this field
   */
  public VKType(TokenReference where, int width, int height, int visibleHeight) {
    super(where);

    this.width = width;
    this.height = height;
    this.visibleHeight = visibleHeight;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Adds a list of value to this type
   */
  public void addList(VKFieldList l) {
    this.list = l;
  }

  /**
   * Get the list of value associated with this type
   */
  public VKFieldList getList() {
    return list;
  }

  /**
   * Return the width in char of this field
   */
  public int getWidth() {
    return width;
  }

  /**
   * Return the height in char of this field
   */
  public int getHeight() {
    return height;
  }

  /**
   * Return the visible height in char of this field
   */
  public int getVisibleHeight() {
    return visibleHeight;
  }
  /**
   * Return whether this type support auto fill command
   */
  public boolean hasAutofill() {
    return getList() != null;
  }

  /**
   * return whether this type support auto fill command
   */
  public boolean hasNewItem() {
    return getList() != null && getList().hasNewForm();
  }

  /**
   * return whether this type support auto fill command
   */
  public boolean hasShortcut() {
    return getList() != null && getList().hasShortcut();
  }

  /**
   * Returns the default alignment
   */
  public int getDefaultAlignment() {
    return at.dms.vkopi.lib.form.VConstants.ALG_LEFT;
  }

  /**
   * Returns the default alignment
   */
  public abstract CReferenceType getListColumnType();

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    if (list != null) {
      list.checkCode(context);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract JExpression genConstructor();

  /**
   * Returns the java equivalent type
   */
  public abstract CReferenceType getType();

  /**
   * Returns the info for the type-checking mechanism of dbi. 
   *
   * @return the info
   */
  public abstract DatabaseColumn getColumnInfo();

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract CReferenceType getReportType();

  // ----------------------------------------------------------------------
  // PROTECTED UTILITIES
  // ----------------------------------------------------------------------

  protected void genType(PrintWriter p, String name, Object par1, Object par2) {
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int			width;
  private int			height;
  private int			visibleHeight;
  private VKFieldList		list;
}
