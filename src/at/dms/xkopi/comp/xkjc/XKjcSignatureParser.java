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
 * $Id: XKjcSignatureParser.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.CType; 
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.KjcSignatureParser;
import at.dms.kopi.comp.kjc.TypeFactory;

public class XKjcSignatureParser extends KjcSignatureParser {
	// USE THE OPTIMIZED WAYU !!! $$$
	/**
	 * Parse a java type signature
	 *  Description : Attempts to parse the provided string as if it started with
	 *    the Java VM-standard signature for a type.
	 */
  public CType parseSignature(TypeFactory factory, String signature, int from, int to) {
    CType	type = super.parseSignature(factory, signature, from, to);

    if (type instanceof CReferenceType) {
      if (((CReferenceType)type).getQualifiedName() == at.dms.xkopi.lib.type.NotNullDate.class.getName().replace('.','/')) {
        return XStdType.PDate;
      } else if (((CReferenceType)type).getQualifiedName() == at.dms.xkopi.lib.type.NotNullFixed.class.getName().replace('.','/')) {
        return XStdType.PFixed;
      } else if (((CReferenceType)type).getQualifiedName() == at.dms.xkopi.lib.type.NotNullMonth.class.getName().replace('.','/')) {
        return XStdType.PMonth;
      } else if (((CReferenceType)type).getQualifiedName() == at.dms.xkopi.lib.type.NotNullTime.class.getName().replace('.','/')) {
        return XStdType.PTime;
      } else if (((CReferenceType)type).getQualifiedName() == at.dms.xkopi.lib.type.NotNullWeek.class.getName().replace('.','/')) {
        return XStdType.PWeek;
      }
    }
    
    return type;
  }
}
