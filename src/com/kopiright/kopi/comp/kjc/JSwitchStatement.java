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

import java.util.Vector;

import com.kopiright.bytecode.classfile.SwitchInstruction;
import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.util.base.InconsistencyException;

/**
 * JLS 14.10: Switch Statement
 */
public class JSwitchStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expr part
   * @param	groups		the different part of body.
   */
  public JSwitchStatement(TokenReference where,
			  JExpression expr,
			  JSwitchGroup[] groups,
			  JavaStyleComment[] comments)
  {
    super(where, comments);
    this.expr = expr;
    this.groups = groups;
    
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
  
      TypeFactory factory = context.getTypeFactory();
      
      expr = expr.analyse(new CExpressionContext(context, context.getEnvironment()));
      analyse(context, expr.isEnumExpression(factory) ? true : false);
  }
    
  public void analyse(CBodyContext context, boolean isEnum) throws PositionedError {
    
    TypeFactory           factory = context.getTypeFactory();
    CSwitchBodyContext    self;
    CSwitchGroupContext[] groupContexts;
    
    // !!! graf 010109:
    // The type of the Expression must be char, byte, short, or int, or a compile-time error occurs.
    // or enumeration jsr201
    if (!isEnum) {
      check(context,
            expr.getType(factory).isAssignableTo(context,
                                                 context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT)),
                                                 KjcMessages.SWITCH_BADEXPR); 
    } else {
      enumToNormalSwitch(context);
    }
    self = new CSwitchBodyContext(context, context.getEnvironment(), this);
    groupContexts = new CSwitchGroupContext[groups.length];
    
    for (int i = 0; i < groups.length; i++) {
      if (i == 0 || !groupContexts[i - 1].isReachable()) {
        groupContexts[i] = new CSwitchGroupContext(self, context.getEnvironment(), this);
      } else {
        groupContexts[i] = groupContexts[i - 1];
      }
      groups[i].analyse(groupContexts[i], isEnum);
      
      if (groupContexts[i].isReachable() && i != groupContexts.length - 1) {
        context.reportTrouble(new CWarning(getTokenReference(),
                                           KjcMessages.CASE_FALL_THROUGH));
      }
    }
    
    if (self.isBreakTarget()) {
      if (self.defaultExists()) {
        self.adopt(self.getBreakContextSummary());
      } else {
        self.merge(self.getBreakContextSummary());
      }
    }
    if (groupContexts.length > 0 && groupContexts[groupContexts.length-1].isReachable()) {
      if (self.isBreakTarget() || !self.defaultExists()){
        self.merge(groupContexts[groupContexts.length-1]);
      } else {
        self.adopt(groupContexts[groupContexts.length-1]);
      }
    }
    
    self.setReachable(groupContexts.length == 0
                      || groupContexts[groupContexts.length - 1].isReachable()
                      || self.isBreakTarget() 
                      || !self.defaultExists());
    self.close(getTokenReference());
    
    if (!self.defaultExists()) {
      context.reportTrouble(new CWarning(getTokenReference(), KjcMessages.SWITCH_NO_DEFAULT));
    }
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the type of the switch expression.
   */
  public CType getType(TypeFactory factory) {
    return expr.getType(factory);
  }


  /**
   * Return the end of this block (for break statement)
   */
  public CodeLabel getBreakLabel() {
    return endLabel;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitSwitchStatement(this, expr, groups);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    CodeLabel		defaultLabel = new CodeLabel();

    Vector		matches = new Vector();
    Vector		targets = new Vector();

    for (int i = 0; i < groups.length; i++) {
      groups[i].collectLabels(defaultLabel, matches, targets, factory);
    }

    expr.genCode(context, false);

    code.pushContext(this);
    code.plantInstruction(new SwitchInstruction(defaultLabel, matches, targets));
    for (int i = 0; i < groups.length; i++) {
      groups[i].genCode(context);
    }

    if (!defaultLabel.hasAddress()) {
      code.plantLabel(defaultLabel);
    }

    code.plantLabel(endLabel);
    code.popContext(this);

    endLabel = null;
  }

  // ----------------------------------------------------------------------
  // ENUM TOOLS
  // ----------------------------------------------------------------------

  private void enumToNormalSwitch(CBodyContext context) throws PositionedError {

    CClassContext outer;
    String enumId;
    
    // The fartest class context
    outer = context.getClassContext();
    while ( outer.getCClass().isNested() ) { 
      outer = outer.getClassContext();
    }

    enumId = expr.getType(context.getTypeFactory()).getCClass().getQualifiedName();

    innerEnumMap = outer.getEnumInnerClass(enumId);
    expr = innerEnumMap.translateEnumSwitchExpr(context, expr);
  }
  
  
  public JEnumSwitchInnerDeclaration getInnerEnumMap() {
    if (innerEnumMap == null) {
      throw new InconsistencyException("Inner Enum Class Is Not Defined Yet");
    }
    return innerEnumMap;
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private JExpression      expr;
  private JSwitchGroup[]   groups;
  private CodeLabel	       endLabel = new CodeLabel();
  private JEnumSwitchInnerDeclaration innerEnumMap = null;
}
