/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

// Import the necessary classes
header { package org.kopi.vkopi.comp.print; }
{
  import java.awt.Color;
  import java.util.Vector;
  import java.util.ArrayList;

  import org.kopi.compiler.base.Compiler;
  import org.kopi.compiler.base.PositionedError;
  import org.kopi.compiler.tools.antlr.extra.InputBuffer;
  import org.kopi.compiler.base.TokenReference;
  import org.kopi.kopi.comp.kjc.*;
  import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;
  import org.kopi.xkopi.comp.sqlc.TableName;
  import org.kopi.xkopi.comp.sqlc.TableReference;
  import org.kopi.vkopi.comp.base.*;
  import org.kopi.vkopi.comp.trig.GKjcParser;
  import org.kopi.vkopi.comp.trig.GSqlcParser;
  import org.kopi.vkopi.lib.list.VList;
  import org.kopi.vkopi.lib.print.PBlockStyle;
  import org.kopi.vkopi.lib.print.PParagraphStyle;
  import org.kopi.xkopi.lib.type.Fixed;
  import org.kopi.xkopi.lib.type.NotNullFixed;
  import org.kopi.vkopi.comp.base.VKEnvironment;
}

// ----------------------------------------------------------------------
// THE PARSER STARTS HERE
// ----------------------------------------------------------------------

class PrintParser extends BaseParser;

options {
  k = 1;				// one token lookahead !
  importVocab = Print;			// Call its vocabulary "Print"
  exportVocab = Print;			// Call its vocabulary "Print"
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  superClass = "org.kopi.vkopi.comp.base.Parser";
  access = "private";			// Set default rule access
}
{
  public PrintParser(Compiler compiler, InputBuffer buffer, VKEnvironment environment) {
    super(compiler, new PrintScanner(compiler, buffer), MAX_LOOKAHEAD);
    this.environment = environment;
  }

  private GKjcParser buildGKjcParser() {
    return new GKjcParser(getCompiler(), getBuffer(), environment);
  }

  private GSqlcParser buildGSqlcParser() {
    return new GSqlcParser(getCompiler(), getBuffer(), environment);
  }

  private final VKEnvironment  environment;
}

public prCompilationUnit []
  returns [PRPage self]
:
  self = prPage[]
  EOF
;

public prInsert []
  returns [PRInsert self]
{
  self = new PRInsert(buildTokenReference(/*!!!0*/), environment);
}
:
  vkContextHeader[self.getCompilationUnitContext()]
  prDefinition[(PRDefinitionCollector)self.getDefinitionCollector()]
  EOF
;

prPage []
  returns [PRPage self = null]
{
  String		name;
  String		fhName;
  String		headerFooterName;
  String		footerName;
  String		inter;
  PRProlog		prolog;
  PRBlock		block;
  PRBlock		pageHeader = null;
  PRBlock		pageFooter = null;
  ArrayList		blocks = new ArrayList();
  String		ident;
  CReferenceType	superClass = null;
  ArrayList		interfaces = new ArrayList();
  CParseCompilationUnitContext	cunit = new CParseCompilationUnitContext(); // not flushed !!!
  CParseClassContext	context = new CParseClassContext(); // not flushed !!!
  PRDefinitionCollector coll = new PRDefinitionCollector(environment.getInsertDirectories());
  TokenReference	sourceRef = buildTokenReference();
  PRTrigger             headerFooter = null;
  PRTrigger             footer = null;
}
:
  "PAGE"
  (
    "IS" ident = vkQualifiedIdent[]
      { superClass = environment.getTypeFactory().createType(ident.replace('.', '/'), false); }
  )?
  (
    "IMPLEMENTS"
    inter = vkQualifiedIdent[]
      { interfaces.add(environment.getTypeFactory().createType(inter.replace('.', '/'), false)); }
    (
      COMMA inter = vkQualifiedIdent[]
        { interfaces.add(environment.getTypeFactory().createType(inter.replace('.', '/'), false)); }
    )*
  )?
  vkContextHeader[cunit]
  prDefinition[coll]
  "BEGIN"
  prolog = prProlog[]
  ( block = prBlock[true] { blocks.add(block); } )+
  (  
    fhName = vkSimpleIdent[]
    ( 
      headerFooter = prTriggerCode[fhName]
      {
        if (!headerFooter.getIdent().equals("PAGEHEADER") 
          && !headerFooter.getIdent().equals("PAGEFOOTER")) { 
             reportTrouble(new PositionedError(sourceRef, PrintMessages.WRONG_PAGE_TRIGGER_NAME,  headerFooter.getIdent()));
        }

        PRTextBlock hf = new PRTextBlock(sourceRef,
	 		                 new CParseClassContext(),
			                 "_$_" + headerFooter.getIdent(),
			                 new PRPosition(sourceRef, 0, 0, 10000, 10000, null, null, null),
			                 null,
			                 false,
			                 new PRTrigger[]{headerFooter});
        headerFooter.setIdent("SOURCE");
        if (headerFooter.equals("PAGEHEADER")) {
          pageHeader = hf;
        } else {
          pageFooter = hf;
        }

      }
    |
       headerFooterName = vkQualifiedIdent[]
      {
        if (!fhName.equals("PAGEHEADER") 
            && !fhName.equals("PAGEFOOTER")) { 
            reportTrouble(new PositionedError(sourceRef, PrintMessages.WRONG_PAGE_TRIGGER_NAME, fhName));
        }
        if (fhName.equals("PAGEHEADER")) {
          pageHeader = new PRImportedBlock(sourceRef, headerFooterName, true);
        } else {
          pageFooter = new PRImportedBlock(sourceRef, headerFooterName, true);
        }
      }
    )
    (
      ( 
        footer = prTrigger[] 
        {
          if (!footer.getIdent().equals("PAGEFOOTER") || pageFooter != null) { 
            reportTrouble(new PositionedError(sourceRef, PrintMessages.WRONG_PAGE_TRIGGER_NAME, footer.getIdent()));
          }
          pageFooter = new PRTextBlock(sourceRef,
                                       new CParseClassContext(),
                                       "_$_" + footer.getIdent(),
                                       new PRPosition(sourceRef, 0, 0, 10000, 10000, null, null, null),
                                       null,
                                       false,
                                       new PRTrigger[]{footer});
          footer.setIdent("SOURCE");
        }
      |
        fhName = vkSimpleIdent[] footerName = vkQualifiedIdent[]
        {
          if (!fhName.equals("PAGEFOOTER") || pageFooter != null) { 
            reportTrouble(new PositionedError(sourceRef, PrintMessages.WRONG_PAGE_TRIGGER_NAME, fhName));
          }
          pageFooter = new PRImportedBlock(sourceRef, footerName, true);
        }
       
      )
    )? 
  )?
  ( vkContextFooter[context] )?
  "END" "PAGE"
    {
      if (pageHeader != null) {
        blocks.add(pageHeader);
      }
      if (pageFooter != null) {
        blocks.add(pageFooter);
      }
      self = new PRPage(sourceRef,
                        environment,
	                cunit,
	                context,
			coll,
	                superClass,
                        (CReferenceType[])interfaces.toArray(new CReferenceType[interfaces.size()]),
			(PRBlock[])blocks.toArray(new PRBlock[blocks.size()]),
                        pageHeader,
                        pageFooter);
      self.setProlog(prolog);
    }
;

prDefinition[PRDefinitionCollector coll]
{ VKDefinition def; }
:
  (
      def = prStyle[] { coll.addStyleDef(def); }
    |
      prInsertDefinitions[coll]
  )*
;

prInsertDefinitions[PRDefinitionCollector coll]
{
  String			name;
}
:
  "INSERT" name = vkString[] { coll.addInsert(name, buildTokenReference().getPath()); }
;

prStyle []
  returns [PRStyle self]
:
  (
    self = prBlockStyle[]
  |
    self = prParagraphStyle[]
  |
    self = prTextStyle[]
  )
;

prBlockStyle []
  returns [PRStyle self]
{
  String	ident = null;
  String	superId		= null;
  int		border		= 0;
  Color		background	= null;
  int		r, g, b;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "BLOCK" "STYLE" ident = vkSimpleIdent[] ( "IS" superId = vkSimpleIdent[] )?
     (
      "BORDER"   border = vkInteger[]
    |
      "BACKGROUND" r = vkInteger[] g = vkInteger[] b = vkInteger[] { background = new Color(r, g, b); }
  )*
    { self = new PRBlockStyle(sourceRef, ident, superId, border, background); }
  "END" "STYLE"
;

prParagraphStyle[]
  returns [PRStyle self]
{
  String		ident = null;
  String		superId = null;
  int			align = -1;
  int			indentLeft = -1;
  int			indentRight = -1;
  int			firstLineIndent = -1;
  Fixed			lineSpacing = null;
  int			spaceAbove = -1;
  int			spaceBelow = -1;
  int			borderMode = 0;
  int			border = 0;
  int			marginLeft = 0;
  int			marginRight = 0;
  Color			background = null;
  boolean		noBackground = false;
  int			r, g, b;
  PRTabStop[]		tabSet = PRTabStop.EMPTY;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "PARAGRAPH" "STYLE" ident = vkSimpleIdent[] ( "IS" superId = vkSimpleIdent[] )?
  (
      "ALIGN"  (
		 "LEFT"      { align = PParagraphStyle.ALN_LEFT; }
	       |
		 "RIGHT"     { align = PParagraphStyle.ALN_RIGHT; }
	       |
		 "CENTER"    { align = PParagraphStyle.ALN_CENTER; }
	       |
		 "JUSTIFIED" { align = PParagraphStyle.ALN_LEFT; }
	       )
    |
      "INDENT" ( "LEFT" indentLeft = prPos[] | "RIGHT" indentRight = prPos[] )
    |
      "FIRST" "LINE" "IDENT" firstLineIndent = prPos[]
    |
      "LINE" "SPACING" lineSpacing = vkFixed[]
    |
      "ORIENTATION"
    |
      "SPACE" ( "ABOVE" spaceAbove = prPos[] | "BELOW" spaceBelow = prPos[] )
    |
      "MARGIN" ( "LEFT" marginLeft = prPos[] | "RIGHT" marginRight = prPos[] )
    |
      "BORDER" border = vkInteger[] (
                                      { borderMode = PParagraphStyle.BRD_ALL; }
                                    |
                                      (
                                          "TOP"   { borderMode |= PParagraphStyle.BRD_TOP; }
                                        |
                                          "BOTTOM"   { borderMode |= PParagraphStyle.BRD_BOTTOM; }
                                        |
                                          "LEFT"   { borderMode |= PParagraphStyle.BRD_LEFT; }
                                        |
                                          "RIGHT"   { borderMode |= PParagraphStyle.BRD_RIGHT; }
                                      )+
                                    )
    |
      "BACKGROUND" r = vkInteger[] g = vkInteger[] b = vkInteger[] { background = new Color(r, g, b); }
    |
      "NO" "BACKGROUND" { noBackground = true; }
    |
      "TABSET" tabSet = prTabset[]
  )*
    {
      self = new PRParagraphStyle(sourceRef,
				  ident,
				  superId,
				  align,
				  indentLeft,
				  borderMode,
				  border,
                  marginLeft,
                  marginRight,
				  background,
				  noBackground,
				  tabSet);
    }
  "END" "STYLE"
;

prTabset []
  returns [PRTabStop[] self]
{
  String		ident = null;
  int			pos;
  int			align;
  Vector		vect = new Vector();
}
:
  (
    ("TAB")? ident = vkSimpleIdent[] "AT" pos = prPos[] "ALIGN"
    (
      (
        "LEFT" { align = PBlockStyle.ALN_LEFT; }
      |
        "CENTER" { align = PBlockStyle.ALN_CENTER; }
      |
        "RIGHT" { align = PBlockStyle.ALN_RIGHT; }
      /*
      |
	"DECIMAL" { align = PBlockStyle.ALN_DECIMAL; }
      */
      )
        {
	  vect.addElement(new PRTabStop(buildTokenReference(),
					ident,
					pos,
					align));
	}
    )
  )+
  {
    self = (PRTabStop[])org.kopi.util.base.Utils.toArray(vect, PRTabStop.class);
  }
;

prTextStyle []
  returns [PRStyle self]
{
  String	ident = null;
  String	superId		= null;
  Color		background	= null;
  Color		foreground	= null;
  int		face		= 0;
  String	font		= null;
  int		size		= -1;
  int		r, g, b;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "TEXT" "STYLE" ident = vkSimpleIdent[] ( "IS" superId = vkSimpleIdent[] )?
  (
      "BACKGROUND" r = vkInteger[] g = vkInteger[] b = vkInteger[] { background = new Color(r, g, b); }
    |
      "FOREGROUND" r = vkInteger[] g = vkInteger[] b = vkInteger[] { foreground = new Color(r, g, b); }
    |
      "BOLD"		{ face |= PRTextStyle.FCE_BOLD; }
    |
      "ITALIC"		{ face |= PRTextStyle.FCE_ITALIC; }
    |
      "SUBSCRIPT"	{ face |= PRTextStyle.FCE_SUBSCRIPT; }
    |
      "SUPERSCRIPT"	{ face |= PRTextStyle.FCE_SUPERSCRIPT; }
    |
      "UNDERLINE"	{ face |= PRTextStyle.FCE_UNDERLINE; }
    |
      "FONT" font = vkString[]
    |
      "SIZE" size = vkInteger[]
    |
      "STRIKETHROUGH" { face |= PRTextStyle.FCE_STRIKETHROUGHT; }
  )*
  "END" "STYLE"
    { self = new PRTextStyle(sourceRef, ident, superId, background, foreground,
			     face, font, size); }
;

prBlock [boolean wantPos]
  returns [PRBlock self]
:
    self = prImportedBlock[]
  |
    self = prTextBlock[wantPos]
  |
    self = prRecursiveBlock[wantPos]
  |
    self = prHorizontalBlock[wantPos]
  |
    self = prListBlock[wantPos]
  |
    self = prRectangleBlock[wantPos]
;

prProlog []
  returns [PRProlog self = null]
{
  boolean		portrait = false;
  String		format = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    (
      "LANDSCAPE" { portrait = false; }
    |
      "PORTRAIT"  { portrait = true; }
    )
    ( format = vkString[] )?
    { self = new PRProlog(sourceRef, portrait, format); }
  )?
;

prImportedBlock []
  returns [PRBlock self]
{
  String		name;
  TokenReference	sourceRef = buildTokenReference();
  boolean		mode = false;
}
:
  "INSERT"
  (
    ("RECT" | "VERTICAL" "BLOCK" | "HORIZONTAL" "BLOCK")
  |
    ("TEXT" | "LIST" ) { mode = true; }
  )

   name = vkQualifiedIdent[]
    { self = new PRImportedBlock(sourceRef, name, mode); }
;

prRectangleBlock [boolean wantPos]
  returns [PRBlock self]
{
  String		ident = null;
  PRPosition		pos;
  String		blockStyle = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "RECT" ( ident = vkSimpleIdent[] )?
  pos = prBlockPos[wantPos]
  "STYLE" blockStyle = vkSimpleIdent[]
  "END" "BLOCK"
    { self = new PRRectangleBlock(sourceRef, ident, pos, blockStyle); }
;

prTextBlock [boolean wantPos]
  returns [PRTextBlock self = null]
{
  String		ident = null;
  boolean	        recurrent = false;
  String		style;
  String		blockStyle = null;
  //Vector		interfaces = new Vector();
  String		inter = null;
  PRTrigger[]		trigs = null;
  PRPosition		pos;
  CParseClassContext	context = new CParseClassContext(); // not flushed !!!
  TokenReference	sourceRef = buildTokenReference();
}
:
  ("REC" {recurrent = true; })? "TEXT" ( ident = vkSimpleIdent[] )?
  //( "IS" inter = vkSimpleIdent[] { interfaces.addElement(inter.replace('.', '/')); } // $$$ TESTED => EXTENDS ????
  //           ( COMMA inter = vkSimpleIdent[] { interfaces.addElement(inter.replace('.', '/')); })*
  //  )?
  pos = prBlockPos[wantPos]
  ( "STYLE" blockStyle = vkSimpleIdent[] )?
  trigs = prTriggers[]
  ( "BODY" vkContextFooter[context] )?
  "END" "BLOCK"
    { self = new PRTextBlock(sourceRef,
			     context,
			     ident,
			     pos,
			     blockStyle,
			     recurrent,
			     trigs);
   }
;

prRecursiveBlock [boolean wantPos]
  returns [PRRecursiveBlock self]
{
  String		ident = null;
  PRBlock		block = null;
  String		blockStyle = null;
  PRPosition		pos;
  Vector		blocks = new Vector();
  TokenReference	sourceRef = buildTokenReference();
}
:
  "VERTICAL" "BLOCK" ( ident = vkSimpleIdent[] )?
  pos = prBlockPos[wantPos]
  ( "STYLE" blockStyle = vkSimpleIdent[] )?
  (block = prBlock[false] {blocks.addElement(block); } )+
  "END" "BLOCK"
  {
    self = new PRRecursiveBlock(sourceRef,
				ident,
				pos,
				blockStyle,
				(PRBlock[])org.kopi.util.base.Utils.toArray(blocks, PRBlock.class));
  }
;

prHorizontalBlock [boolean wantPos]
  returns [PRHorizontalBlock self]
{
  String		ident = null;
  PRBlock		block = null;
  Vector		blocks = new Vector();
  String		blockStyle = null;
  PRPosition		pos;
  TokenReference	sourceRef = buildTokenReference();
}
:
  "HORIZONTAL" "BLOCK" ( ident = vkSimpleIdent[] )?
  pos = prBlockPos[wantPos]
  ( "STYLE" blockStyle = vkSimpleIdent[] )?
  (block = prBlock[true] { blocks.addElement(block); } )+
  "END" "BLOCK"
    {
      self = new PRHorizontalBlock(sourceRef,
				   ident,
				   pos,
				   blockStyle,
				   (PRBlock[])org.kopi.util.base.Utils.toArray(blocks, PRBlock.class));
    }
;

prListBlock [boolean wantPos]
  returns [PRTextBlock self = null]
{
  String		ident = null;
  String		blockStyle = null;
  Vector		styles = new Vector();
  //  Vector		interfaces = new Vector();
  String		inter = null;
  PRPosition		pos;
  PRTrigger[]		trigs = null;
  CParseClassContext	context = new CParseClassContext(); // not flushed !!!
  TokenReference	sourceRef = buildTokenReference();
}
:
  "LIST" ( ident = vkSimpleIdent[])?
  //    ( "IS" inter = vkSimpleIdent[] { interfaces.addElement(inter.replace('.', '/')); }
  //           ( COMMA inter = vkSimpleIdent[] { interfaces.addElement(inter.replace('.', '/')); })*
  //  )?
  pos = prBlockPos[wantPos]
  ( "STYLE" blockStyle = vkSimpleIdent[] )?
    // { styles.addElement(blockStyle); }
  trigs = prTriggers[]
  ( "BODY" vkContextFooter[context] )?
  "END" "BLOCK"
    {
      self = new PRListBlock(sourceRef,
			     context,
			     ident,
			     pos,
			     blockStyle,
			     (PRStyle[])org.kopi.util.base.Utils.toArray(styles, PRStyle.class),
			     trigs);
   }
;

prTriggers []
  returns [PRTrigger[] self]
{
  PRTrigger		trig;
  Vector		trigs = new Vector();
}
:
  ( trig = prTrigger[] { trigs.addElement(trig); } )+
    {
      self = (PRTrigger[])org.kopi.util.base.Utils.toArray(trigs, PRTrigger.class);
    }
;

prTrigger []
  returns [PRTrigger self]
{
  String		ident = null;
  PRSourceElement[]		elems;
  JFormalParameter[]	params = JFormalParameter.EMPTY;
  boolean	        recurrent = false;
  String		style;
  String		blockStyle = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ident = vkSimpleIdent[]
  self = prTriggerCode[ident]
;

prTriggerCode [String ident]
  returns [PRTrigger self]
{
  PRSourceElement[]		elems;
  JFormalParameter[]	params = JFormalParameter.EMPTY;
  boolean	        recurrent = false;
  String		style;
  String		blockStyle = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
    LPAREN
    {
      params = buildGKjcParser().gFormalParameter(JLocalVariable.DES_PARAMETER);
    }
    // RPAREN
  )?
  LCURLY elems = prSource[] RCURLY
    { self = new PRTrigger(sourceRef, ident, params, elems); }
;


prSource []
  returns [PRSourceElement[] self]
{
  Vector		vect = new Vector();
  String		line;
  String		style;
  JExpression		expr;
  boolean		hasTab = false;
  boolean		hasBang = false;
  PRSourceElement[]	sources = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  (
     line = vkString[] { vect.addElement(new PRText(sourceRef, line)); }
   |
     LT ("TAB" { hasTab = true; } | BANG { hasBang = true; } | ) style = vkSimpleIdent[] GT
       {
         if (hasTab) {
	   vect.addElement(new PRTabRef(sourceRef, style));
	 } else {
	   vect.addElement(new PRStyleRef(sourceRef, style, hasBang));
	 }
         hasTab = false;
         hasBang = false;
       }
   |
     "PAGE_COUNT" { vect.addElement(new PRPageNumber(sourceRef)); }
   |
     LPAREN expr = prJavaExpressionInParenthesis[] //RPAREN
       { vect.addElement(new PRJavaExpression(sourceRef, expr)); }
   |
     ( LCURLY LPAREN expr = prJavaExpressionInParenthesis[] ASSIGN GT //RPAREN
       (sources = prSource[])+
       RCURLY
	 { vect.addElement(new PRConditionalSource(sourceRef, expr, sources)); }
     )
  )+
  {
    self = (PRSourceElement[])org.kopi.util.base.Utils.toArray(vect, PRSourceElement.class);
  }
;


prBlockPos [boolean wantPos]
  returns [PRPosition self]
{
  int			x = 0;
  int			y = 0;
  int			width = 10000;
  int			height = 1000;
  String		widthStr = null;
  String		heightStr = null;
  JExpression		expr;
  PRJavaExpression	prexpr = null;
  TokenReference	sourceRef = buildTokenReference();
}
:
  ("POS" x = prPos[] (y = prPos[] | "BELOW" { y = -1; } ))?
  (
    "SIZE" ( width = prPos[] | widthStr = vkSimpleIdent[] )
           ( height = prPos[] | heightStr = vkSimpleIdent[] )
  )?
  ( "SHOW" "IF" expr = prJavaExpression[]
    { prexpr = new PRJavaExpression(sourceRef, expr); }
  )?
    { self = new PRPosition(sourceRef, x, y, width, height, widthStr, heightStr, prexpr); }
;

prPos []
  returns [int self]
:
  self = vkInteger[] ( "mm" { self = self * 612 / 210; } )?
;

prJavaExpression []
  returns [JExpression self]
:
  {
    self = buildGKjcParser().gJavaExpressionStatement();
  }
  // SEMI
;

prJavaExpressionInParenthesis []
  returns [JExpression self]
:
  {
    self = buildGKjcParser().gJavaExpressionInParenethesis();
  }
  // SEMI
;

prDummy []
:
  "AUTO" "LOOP"
;
