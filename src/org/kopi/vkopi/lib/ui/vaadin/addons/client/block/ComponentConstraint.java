package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.io.Serializable;

/**
 * The child component layout constraint.
 */
@SuppressWarnings("serial")
public class ComponentConstraint implements Serializable {
  
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Default constructor
   */
  public ComponentConstraint() {}
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll  Use the whole possible width of the column ?
   */
  public ComponentConstraint(int x, int y, int width, int height, boolean alignRight, boolean useAll) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.alignRight = alignRight;
    this.useAll = useAll;
  }
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   * @param alignRight Is it right aligned ?
   */
  public ComponentConstraint(int x, int y, int width, int height, boolean alignRight) {
    this(x, y, width, height, alignRight, false);
  }
  
  /**
   * Creates a new <code>ComponentConstraint</code> instance.
   * @param x The position in x axis.
   * @param y The position in y axis.
   * @param width The column span width.
   */
  public ComponentConstraint(int x, int y, int width, int height) {
    this(x, y, width, height, false, false);
  }
  
  @Override
  public String toString() {
    return "[ X = " + x + ", Y = " + y + ", width = " + width + ", height = " + height + "]";
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * Position in x
   */
  public int				x;
  
  /**
   * Position in y
   */
  public int				y;
  
  /**
   * Number of column
   */
  public int				width;
  
  /**
   * Number of line
   */
  public int                            height;
  
  /**
   * Position in alignRight
   */
  public boolean			alignRight;
  
  /**
   * Use the whole possible width of the column
   */
  public boolean			useAll;
}
