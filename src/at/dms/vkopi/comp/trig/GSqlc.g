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
 * $Id: GSqlc.g,v 1.9 2003/10/24 15:42:45 laurent Exp $
 */

// Import the necessary classes
header { package at.dms.vkopi.comp.trig; }
{
  import java.util.Vector;
  import java.util.ArrayList;

  import at.dms.compiler.base.Compiler;
  import at.dms.compiler.base.CWarning;
  import at.dms.compiler.tools.antlr.extra.InputBuffer;
  import at.dms.compiler.base.PositionedError;
  import at.dms.compiler.base.TokenReference;
  import at.dms.kopi.comp.kjc.*;
  import at.dms.xkopi.comp.sqlc.*;
  import at.dms.xkopi.comp.xkjc.*;
  import at.dms.util.base.Utils;
  import at.dms.xkopi.lib.type.Fixed;
  import at.dms.xkopi.lib.type.NotNullFixed;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class GSqlcParser extends XSqlcParser;

options {
  k = 2;				// two token lookahead
  importVocab = GSqlc;			// Call its vocabulary "GSqlc"
  exportVocab = GSqlc;			// Call its vocabulary "GSqlc"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "at.dms.compiler.tools.antlr.extra.Parser";
  access = "private";			// Set default rule access
}
{
  public GSqlcParser(Compiler compiler, InputBuffer buffer, KjcEnvironment environment) {
    super(compiler, new GSqlcScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.environment = environment;
  }

  private GKjcParser buildXKjcParser() {
    return new GKjcParser(getCompiler(), getBuffer(), environment);
  }

  private final KjcEnvironment  environment;
}

// Accessed from Base
public gSimpleTableReference []
  returns [TableReference self]
:
  self = sSimpleTableReference[]
  RCURLY
;

jPrimitiveType []
  returns [CType self = null]
{
  boolean	nullable = false;
  TypeFactory   tf = environment.getTypeFactory();
}
:
  "void" { self = tf.getVoidType(); }
|
  ( "nullable" { nullable = true; } )?
  (
    "boolean" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_BOOLEAN) : tf.getPrimitiveType(TypeFactory.PRM_BOOLEAN); }
  |
    "byte" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_BYTE) : tf.getPrimitiveType(TypeFactory.PRM_BYTE); }
  |
    "char" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_CHARACTER) : tf.getPrimitiveType(TypeFactory.PRM_CHAR); }
  |
    "short" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_SHORT) : tf.getPrimitiveType(TypeFactory.PRM_SHORT); }
  |
    "int" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_INTEGER) : tf.getPrimitiveType(TypeFactory.PRM_INT); }
  |
    "long" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_LONG) : tf.getPrimitiveType(TypeFactory.PRM_LONG); }
  |
    "float" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_FLOAT) : tf.getPrimitiveType(TypeFactory.PRM_FLOAT); }
  |
    "double" { self = nullable ? (CType)tf.createReferenceType(GTypeFactory.RFT_DOUBLE) : tf.getPrimitiveType(TypeFactory.PRM_DOUBLE); }
  |
    "fixed" { self = nullable ? (CType)XStdType.Fixed : XStdType.PFixed; }
  |
    "date" { self = nullable ? (CType)XStdType.Date : XStdType.PDate; }
  |
    "time" { self = nullable ? (CType)XStdType.Time : XStdType.PTime; }
  |
    "timestamp" { self = nullable ? (CType)XStdType.Timestamp : XStdType.PTimestamp; }
  |
    "month" { self = nullable ? (CType)XStdType.Month : XStdType.PMonth; }
  |
    "week" { self = nullable ? (CType)XStdType.Week : XStdType.PWeek; }
  )
;
