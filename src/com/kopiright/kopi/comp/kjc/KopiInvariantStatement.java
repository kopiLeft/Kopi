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

package com.kopiright.kopi.comp.kjc;

import java.util.ArrayList;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

public class KopiInvariantStatement extends JStatement {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   * @param	comment		the statement comment.
   */
  public KopiInvariantStatement(TokenReference where, 
                             JStatement stmt) {
    super(where, null);
    this.stmt = stmt;
  }
  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
    TokenReference      ref = getTokenReference();
    CMethod             method = context.getMethodContext().getCMethod();
    CClass              clazz = context.getClassContext().getCClass();
    CReferenceType[]        interfaces  = clazz.getInterfaces();

    CMethod             superInvariant = null;
    CMethod             outerInvariant = null;
    CMethod[]           interfaceInvariants = new CMethod[interfaces.length];

    try {
      // invariant of supeclass
      if (clazz.getSuperClass() != null) {
        superInvariant = clazz.getSuperClass().lookupMethod(context,
                                                            clazz,
                                                            null,
                                                            IDENT_INVARIANT, 
                                                            method.getParameters(),
                                                            clazz.getSuperType().getArguments()); 
      }
      // invariant of enclosing class
      if (clazz.isNested() && !clazz.isStatic() && !(clazz.descendsFrom(clazz.getOwner())) ) {
        outerInvariant = clazz.getOwner().lookupMethod(context, clazz, null, IDENT_INVARIANT, method.getParameters(), clazz.getOwner().getTypeVariables());
      }
      // find invariants in interface (Assertion-class of interfaces
      for (int i = 0; i < interfaceInvariants.length; i++) {
        CClass      ct = null;

        try {
          ct = context.lookupClass(clazz, (interfaces[i].getQualifiedName() + IDENT_CLASS_ASSERT).intern());
        } catch (UnpositionedError e) {
          ct = null;
        }

        if (ct == null) {
          continue;
        }
        interfaceInvariants[i] = ct.lookupMethod(context, 
                                                 clazz,
                                                 null, 
                                                 IDENT_INVARIANT, 
                                                 new CType[] { interfaces[i] },
                                                 CReferenceType.EMPTY); 
      }
    } catch (UnpositionedError e) {
      throw e.addPosition(ref);
    }

    ArrayList      methods = new ArrayList(); // invariant  methods calls

    if (superInvariant != null) {
      JStatement        methodcall = 
        new JExpressionStatement(ref,
                                 new KopiMethodCallExpression(ref,
                                                              new JSuperExpression(ref), 
                                                              superInvariant, 
                                                              JExpression.EMPTY), 
                                 null);
      methods.add(methodcall);
    }

    if (outerInvariant != null) {
      JStatement        methodcall = 
        new JExpressionStatement(ref,
                                 new KopiMethodCallExpression(ref,
                                                           outerInvariant,
                                                           JExpression.EMPTY), 
                                 null);
      methods.add(methodcall);
    }

    for (int i = 0; i < interfaces.length; i++) {
      if (interfaceInvariants[i] == null) {
        continue;
      }
      JStatement        methodcall = 
        new JExpressionStatement(ref,
                                 new KopiMethodCallExpression(ref,
                                                           interfaceInvariants[i],
                                                           new JExpression[] { new JThisExpression(ref) }),
                                 null);
      methods.add(methodcall);
    }
    methods.add(stmt);
    JStatement[]        statements = (JStatement[])methods.toArray(new JStatement[methods.size()]);

    impl = new JBlock(ref, statements, null);
    impl.analyse(context);
    
  }
  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    impl.accept(p);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    impl.genCode(context);
  }
  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

  private JStatement stmt;
  private JStatement impl = null;
}
