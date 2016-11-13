/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.chart;

import org.kopi.vkopi.lib.l10n.FieldLocalizer;
import org.kopi.vkopi.lib.l10n.TypeLocalizer;

@SuppressWarnings("serial")
public abstract class VCodeDimension extends VDimension {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  /**
   * Creates a new chart code column.
   * @param ident The column identifier.
   * @param The dimension format.
   * @param type The column type.
   * @param source The column localization source.
   * @param idents The columns displayed labels.
   */
  public VCodeDimension(String ident,
      		        VColumnFormat format,
                        String type,
                        String source,
                        String[] idents)
  {
    super(ident, format);
    this.source = source;
    this.type = type;
    this.idents = idents;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * @Override
   */
  public String toString(Object value) {
    return names != null ? names[getIndex(value)] : idents[getIndex(value)];
  }

  // ----------------------------------------------------------------------
  // ABSTRACT METHODS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the index.of given object
   * @return The index.of given object
   */
  protected abstract int getIndex(Object value);

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * @Override
   */
  protected void localize(FieldLocalizer parent) {
    TypeLocalizer       loc;
    
    loc = parent.getManager().getTypeLocalizer(source, type);
    names = new String[idents.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = loc.getCodeLabel(idents[i]);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private String 					type;
  private String 					source;
  private String[] 					idents;
  protected String[]            			names;  // array of external representations
}
