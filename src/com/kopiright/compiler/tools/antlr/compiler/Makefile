##
## Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id: Makefile,v 1.6 2001/07/08 17:17:15 graf Exp $
##

TOPDIR =	../../../..

PACKAGE =	com/kopiright/compiler/tools/antlr/compiler

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	ANTLRGrammarParseBehavior ANTLRLexer			\
		ANTLRParser ANTLRTokdefLexer ANTLRTokdefParser		\
		ANTLRTokdefParserTokenTypes ANTLRTokenTypes		\
		ActionElement ActionLexer ActionLexerTokenTypes		\
		Alternative AlternativeBlock AlternativeElement		\
		BlockContext BlockEndElement				\
		BlockWithImpliedExitPath CharLiteralElement		\
		CharRangeElement DefineGrammarSymbols			\
		ExceptionHandler ExceptionSpec				\
		FileCopyException Grammar GrammarAnalyzer		\
		GrammarAtom GrammarDefinition GrammarElement		\
		GrammarFile GrammarSymbol				\
		Hierarchy ImportVocabTokenManager			\
		IndexedVector JavaBlockFinishingInfo			\
		JavaCharFormatter JavaCodeGenerator			\
		LLCell LLEnumeration LList LLkAnalyzer			\
		LLkGrammarAnalyzer LexerGrammar				\
		List Lookahead Main MakeGrammar				\
		OneOrMoreBlock Option ParserGrammar			\
		Preprocessor						\
		PreprocessorLexer PreprocessorTokenTypes		\
		Rule RuleBlock RuleEndElement				\
		RuleRefElement RuleSymbol SimpleTokenManager		\
		Stack StringLiteralElement StringLiteralSymbol		\
		SynPredBlock TokenManager				\
		TokenRangeElement TokenRefElement			\
		TokenSymbol Utils					\
		WildcardElement ZeroOrMoreBlock

# optgen not available at this point in the build process
ifndef TOOLS_BUILT
JAVAFILES +=	AntlrOptions
else
OPTIONS +=	AntlrOptions
endif

# msggen not available at this point in the build process
ifndef TOOLS_BUILT
JAVAFILES +=	AntlrMessages
else
MESSAGES +=	AntlrMessages
endif

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
