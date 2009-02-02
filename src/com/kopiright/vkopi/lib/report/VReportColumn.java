/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.report;

import com.kopiright.vkopi.lib.l10n.FieldLocalizer;
import com.kopiright.vkopi.lib.l10n.ReportLocalizer;
import com.kopiright.vkopi.lib.util.LineBreaker;

public abstract class VReportColumn {
  /**
   * Constructs a report column description
   *
   * @param	ident		The identifier of the field
   * @param	options		The column options as bitmap
   * @param	align		The column alignment
   * @param	groups		The index of the column grouped by this one or -1
   * @param	function	An (optional) function
   * @param	width		The width of a cell in characters
   * @param	height		The height of a cell in characters
   * @param	format		format of the cells
   */
  public VReportColumn(String ident,
		       int options,
		       int align,
		       int groups,
		       VCalculateColumn function,
		       int width,
		       int height,
		       VCellFormat format)
  {
    this.ident = ident;
    this.options = options;
    this.align = align;
    this.groups = groups;
    this.function = function;
    this.width = width;
    this.height = height;
    this.format = format;
    this.visible = true;
    this.folded = false;
    this.addedAtRuntime = false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the column label
   */
  public String getLabel() {
    return label == null ? "" : label;
  }

  /**
   * Returns the identifier
   */
  public String getIdent() {
    return ident;
  }
  
  public void setAddedAtRuntime(boolean addedAtRuntime) {
    this.addedAtRuntime = addedAtRuntime;
  }
  
  public boolean isAddedAtRuntime() {
    return addedAtRuntime;
  }

  /**
   * Sets the column label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Returns the column help
   */
  public String getHelp() {
    return help;
  }

  /**
   * Returns the option bitmap
   */
  public int getOptions() {
    return options;
  }

  /**
   * Returns the column alignment
   */
  public int getAlign() {
    return align;
  }

  /**
   * Returns the column grouped by this column or -1
   */
  public int getGroups() {
    return groups;
  }

  /**
   * Returns the computation function for this column
   */
  public VCalculateColumn getFunction() {
    return function;
  }

  /**
   * Returns the width of cells in this column in characters
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the width of cells in this column in characters
   */
  public double getPrintedWidth() {
    return getWidth();
  }

  /**
   * Returns the height of cells in this column in characters
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns true if this Column is hidden
   */
  public boolean isHidden() {
    return (options & Constants.CLO_HIDDEN) > 0;
  }

  public String format(Object o) {
    if (isFolded() || o == null) {
      return "";
    } else if (format != null) {
      return format.format(o);
    } else if (height == 1) {
      String		str = o.toString();
      int		strLength = str.length();

      return strLength <= width ? str : str.substring(0, width);
    } else {
      return o.toString();
    }
  }

  public String formatWithLineBreaker(Object o) {
    return LineBreaker.modelToText(format(o), width);
  }

  protected VCellFormat getFormat() {
    return format;
  }
  
  protected void setFormat(VCellFormat format) {
    this.format = format;
  }
  /**
   * Sets the visibility of the column
   */
  public void setVisible(boolean value) {
    this.visible = value;
  }

  /**
   * Returns the visibility of the column
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the visibility of the column
   */
  public void setFolded(boolean value) {
    this.folded = value;
  }

  /**
   * Returns the visibility of the column
   */
  public boolean isFolded() {
    return folded;
  }

  /**
   * Compare two objects.
   *
   * @param	o1	the first operand of the comparison
   * @param	o2	the second operand of the comparison
   * @return	-1 if the first operand is smaller than the second
   *		 1 if the second operand if smaller than the first
   *		 0 if the two operands are equal
   */
  public abstract int compareTo(Object o1, Object o2);

  public DColumnStyle[] getStyles() {
    if (styles == null) {
      DColumnStyle style = new DColumnStyle();
      style.setFont(0);
      style.setBackground(Constants.CLR_WHITE);
      style.setForeground(Constants.CLR_BLACK);
      return new DColumnStyle[] {style};
    } else {
      return styles;
    }
  }

  public void setStyles(DColumnStyle[] styles) {
    this.styles = styles;
  }

  public void formatColumn(PExport exporter, int index) {
    exporter.formatStringColumn(this, index);
  }

  public void helpOnColumn(VHelpGenerator help) {
    help.helpOnColumn(label, this.help);
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  public void localize(ReportLocalizer parent) {
    if (! isHidden() && ! ident.equals("")){
      FieldLocalizer      loc;
 
      loc = parent.getFieldLocalizer(ident);
      label = loc.getLabel();
      help = loc.getHelp();
      localize(loc);
    }
  }

    
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  protected void localize(FieldLocalizer loc) {
    // by default nothing to do
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private	String			ident;
  private	String			label;
  private	String			help;
  private	int			options;
  private	int			align;
  private	int			groups;		// reference to grouped column or -1
  private	VCalculateColumn	function;	// summation function or -1
  private	VCellFormat		format;
  private	boolean			visible;
  private	boolean			folded;
  private       boolean                 addedAtRuntime;
  private	DColumnStyle[]		styles;
  protected	int			width;
  protected	int			height;
}
