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

package at.dms.vkopi.lib.form;

import at.dms.vkopi.lib.visual.SActor;

public abstract class VImportedBlock extends VBlock {

  // ----------------------------------------------------------------------
  // Constructor / build
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VImportedBlock(VForm form) {
    super(form);
    // !!! remove
  }

  // ----------------------------------------------------------------------
  // ACTOR HANDLING
  // ----------------------------------------------------------------------

  /**
   *
   */
  public SActor getActor(int i) {
    return actors[i];
  }

  /**
   *
   */
  protected void setActors(SActor[] actors) {
    this.actors = actors;
  }

  /**
   *
   */
  public SActor[] getActors() {
    return actors;
  }
}
