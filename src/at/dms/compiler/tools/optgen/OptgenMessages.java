
/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: OptgenMessages.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

// Generated by msggen from OptgenMessages.msg
package at.dms.compiler.tools.optgen;

import at.dms.util.base.MessageDescription;

public interface OptgenMessages extends at.dms.compiler.base.CompilerMessages {
  MessageDescription	DUPLICATE_DEFINITION = new MessageDescription("Option \"{0}\" redefined in \"{1}\": previous definition in \"{2}\"", null, 0);
  MessageDescription	DUPLICATE_SHORTCUT = new MessageDescription("Shortcut \"{0}\" redefined in \"{1}\": previous definition in \"{2}\"", null, 0);
}
