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

package at.dms.xkopi.comp.dbi;

import java.util.List;

import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a table definition
 */
public class Key extends Constraint {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	keyList		the list of the keys
   */
  public Key(TokenReference ref, List keyList) {
    super(ref);
    this.keyList = keyList;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * @return    the list of the keys
   */
  public List getKeys() {
    return keyList;
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * Compare 2 keys.
   *
   * @return true if the keys are equals.
   */
  public boolean compareTo(Key otherKey) {
    List	otherKeyList = otherKey.getKeys();

    if (keyList.size() == otherKeyList.size()) {
      for (int i = 0; i < keyList.size(); i++) {
	if (!keyList.get(i).equals(otherKeyList.get(i))) {
	  DbCheck.addError(new PositionedError(getTokenReference(),
                                               DbiMessages.KEY_NOT_CORRECT,
                                               keyList.get(i)));
	  return false;
	}
      }
      return true;
    } else {
      return false;
    }
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitKey(this, keyList);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private List  keyList;
}
