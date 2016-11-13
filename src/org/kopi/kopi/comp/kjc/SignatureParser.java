/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.kopi.comp.kjc;

public interface SignatureParser {

  MethodSignature parseMethodSignature(TypeFactory factory, String signature);

  CType parseSignature(TypeFactory factory, String signature);

  ClassSignature parseClassSignature(TypeFactory factory, String signature);

  class ClassSignature {
    public ClassSignature(CReferenceType st, CReferenceType[] ifes, CTypeVariable[] tv) {
      superType = st;
      interfaces = ifes;
      typeVariables = tv;
    }

    public final CReferenceType             superType;
    public final CReferenceType[]           interfaces;
    public final CTypeVariable[]        typeVariables;
  }

  class MethodSignature {
    public MethodSignature(CType retType, CType[] params, CReferenceType[] exceptns, CTypeVariable[] tv) {
      returnType = retType;
      parameterTypes = params;
      exceptions = exceptns;
      typeVariables = tv;
    }

    public final CType                  returnType;
    public final CType[]                parameterTypes;
    public final CReferenceType[]           exceptions;
    public final CTypeVariable[]        typeVariables;
  }
}
