/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
import org.kopi.gradle.common.withExtension
import org.kopi.gradle.dsl.inPackage
import org.kopi.gradle.dsl.topDir

/** ------------ Variables declarations ------------ */

// TOPDIR DIRECTORY
topDir = "src"

inPackage("org.kopi.util.base") {
  javaFiles = listOf("ArrayLocator", "Utils", "CharArrayCache", "FormattedException", "InconsistencyException",
          "Message", "MessageDescription", "NotImplementedException", "Options", "SimpleStringBuffer", "UnicodeReader")
}

inPackage("org.kopi.compiler.base") {
  javaFiles = listOf("CWarning", "Compiler", "JavaStyleComment", "JavadocComment", "NumberParser", "Phylum",
          "PositionedError", "TabbedPrintWriter", "TokenReference", "UnpositionedError", "WarningFilter")

  if (toolsBuilt == null) {
    javaFiles = javaFiles!! + listOf("CompilerMessages")
  } else {
    messageFiles = listOf("CompilerMessages")
  }
}

inPackage("org.kopi.compiler.resource") {
  nonStandardBuild = true
  resources =        listOf("skeleton.shared")
  targetDir =        "org/kopi/compiler"
}

inPackage("org.kopi.compiler.tools.antlr.runtime") {
  javaFiles = listOf("ANTLRException", "ANTLRHashString", "ANTLRStringBuffer", "BitSet", "ByteBuffer",
          "CharBuffer", "CharFormatter", "CharQueue", "CharScanner", "CharStreamException", "CharStreamIOException",
          "CommonToken", "DefaultFileLineFormatter", "FileLineFormatter", "InputBuffer", "LLkParser", "LexerSharedInputState",
          "MismatchedCharException", "MismatchedTokenException", "NoViableAltException", "NoViableAltForCharException",
          "Parser", "ParserException", "ParserSharedInputState", "RecognitionException", "SemanticException", "Token",
          "TokenBuffer", "TokenQueue", "TokenStream", "TokenStreamException", "TokenStreamIOException",
          "TokenStreamRecognitionException", "VectorEnumerator", "Vector")
}

inPackage("org.kopi.compiler.tools.antlr.compiler") {
  javaFiles = listOf("ANTLRGrammarParseBehavior", "ANTLRLexer", "ANTLRParser", "ANTLRTokdefLexer",
          "ANTLRTokdefParser", "ANTLRTokdefParserTokenTypes", "ANTLRTokenTypes", "ActionElement", "ActionLexer",
          "ActionLexerTokenTypes", "Alternative", "AlternativeBlock", "AlternativeElement", "BlockContext",
          "BlockEndElement", "BlockWithImpliedExitPath", "CharLiteralElement", "CharRangeElement",
          "DefineGrammarSymbols", "ExceptionHandler", "ExceptionSpec", "FileCopyException", "Grammar",
          "GrammarAnalyzer", "GrammarAtom", "GrammarDefinition", "GrammarElement", "GrammarFile",
          "GrammarSymbol", "Hierarchy", "ImportVocabTokenManager", "IndexedVector", "JavaBlockFinishingInfo",
          "JavaCharFormatter", "JavaCodeGenerator", "LLCell", "LLEnumeration", "LList", "LLkAnalyzer",
          "LLkGrammarAnalyzer", "LexerGrammar", "List", "Lookahead", "Main", "MakeGrammar", "OneOrMoreBlock",
          "Option", "ParserGrammar", "Preprocessor", "PreprocessorLexer", "PreprocessorTokenTypes", "Rule",
          "RuleBlock", "RuleEndElement", "RuleRefElement", "RuleSymbol", "SimpleTokenManager", "Stack",
          "StringLiteralElement", "StringLiteralSymbol", "SynPredBlock", "TokenManager", "TokenRangeElement",
          "TokenRefElement", "TokenSymbol", "Utils", "WildcardElement", "ZeroOrMoreBlock")

  if (toolsBuilt == null) {
    javaFiles = javaFiles!! + listOf("AntlrOptions", "AntlrMessages")
  } else {
    optionFiles = listOf("AntlrOptions")
    messageFiles = listOf("AntlrMessages")
  }
}

inPackage("org.kopi.compiler.tools.antlr.extra") {
  javaFiles = listOf("CToken", "InputBuffer", "InputBufferState", "Parser", "Scanner", "TokenCache")
}

inPackage("org.kopi.compiler.tools.jperf") {
  javaFiles = listOf("Graph", "JPerf", "Keywords", "Main", "Node", "Table")
}

inPackage("org.kopi.compiler.tools.optgen") {
  javaFiles = listOf("DefinitionFile", "OptgenError", "Main", "OptionDefinition")

  if (toolsBuilt == null) {
    javaFiles = javaFiles!! + listOf("OptgenOptions", "OptgenMessages")
  } else {
    optionFiles = listOf("OptgenOptions")
    messageFiles = listOf("OptgenMessages")
  }
}

inPackage("org.kopi.compiler.tools.msggen") {
  javaFiles =   listOf("DefinitionFile", "MsggenError", "Main", "MessageDefinition")
  optionFiles = listOf("MsggenOptions")

  if (toolsBuilt == null) {
    javaFiles = javaFiles!! + listOf("MsggenMessages")
  } else {
    messageFiles = listOf("MsggenMessages")
  }
}

inPackage("org.kopi.compiler.tools.lexgen") {
  grammar =       listOf("Lexgen")
  javaFiles =     listOf("DefinitionFile", "LexgenError", "Main", "TokenDefinition")
  messageFiles =  listOf("LexgenMessages")
  optionFiles =   listOf("LexgenOptions")
}

inPackage("org.kopi.compiler.tools.include") {
  javaFiles =     listOf("IncludeError", "Main")
  messageFiles =  listOf("IncludeMessages")
  optionFiles =   listOf("IncludeOptions")
}

inPackage("org.kopi.bytecode.classfile") {
  javaFiles = listOf("AbstractInstructionAccessor", "AccessorContainer", "AccessorTransformer",
          "AsciiConstant", "Attribute", "AttributeList", "BadAccessorException", "ClassConstant", "ClassfileOptions",
          "ClassFileFormatException", "ClassInfo", "ClassPath", "ClassRefInstruction", "CodeEnv", "CodeInfo",
          "CodePosition", "ConstantPool", "ConstantValueAttribute", "Constants", "DeprecatedAttribute",
          "DoubleConstant", "ExceptionsAttribute", "FieldInfo", "FieldRefConstant", "FieldRefInstruction",
          "FloatConstant", "GenericAttribute", "HandleCreator", "HandlerInfo", "IincInstruction", "InnerClassInfo",
          "InnerClassTable", "Instruction", "InstructionAccessor", "InstructionHandle", "InstructionIO",
          "IntegerConstant", "InterfaceConstant", "InvokeinterfaceInstruction", "JumpInstruction", "LineNumberInfo",
          "LineNumberTable", "LocalVarInstruction", "LocalVariableInfo", "LocalVariableTable", "LongConstant",
          "Main", "Member", "MethodInfo", "MethodRefConstant", "MethodRefInstruction", "MultiarrayInstruction",
          "NameAndTypeConstant", "NewarrayInstruction", "NoArgInstruction", "OpcodeNames", "PooledConstant",
          "PushLiteralInstruction", "ReferenceConstant", "SkippedCodeInfo", "SourceFileAttribute", "StringConstant",
          "SwitchInstruction", "SyntheticAttribute", "UnresolvedConstant", "ConstraintsAttribute",
          "SignatureAttribute", "PreconditionAttribute", "PostconditionAttribute", "InvariantAttribute",
          "MethodDescription", "ConstantPoolOverflowException", "InstructionOverflowException",
          "LocalVariableOverflowException")
  messageFiles =  listOf("ClassfileMessages")
  optionFiles =   listOf("ClassfileOptions")
}

inPackage("org.kopi.bytecode.optimize") {
  javaFiles =     listOf("HandleCreator", "InstructionHandle", "Main", "Optimizer", "Patterns")
  messageFiles =  listOf("OptimizeMessages")
  optionFiles =   listOf("OptimizeOptions")
}

inPackage("org.kopi.bytecode.ssa") {
  javaFiles = listOf("BasicBlock", "CFGEdge", "CodeGenerator", "CodeGeneratorBasicBlock", "CodeGeneratorMethod",
          "ColorComputer", "ControlFlowGraph", "CopyPropagation", "DominatorComputer", "DominatorTreeNode", "Edge",
          "EdgeLabel", "ExceptionHandler", "GenerateQVar", "Graph", "InterferenceGraph", "LivenessComputer", "Main",
          "MethodOptimizer", "Node", "NodeVisitor", "Propagator", "QANewArray", "QAbstractJumpInst", "QArrayLength",
          "QAssignment", "QBinaryOperation", "QCallReturn", "QCallVoid", "QCheckCast", "QConditionalJump", "QConstant",
          "QDeclareInitialised", "QExpression", "QGetArray", "QGetField", "QInst", "QInstArray", "QInstArrayAccessor",
          "QInstanceOf", "QJsr", "QJump", "QMethodReturn", "QMonitor", "QMultiArray", "QNew", "QNewArray",
          "QNewInitialized", "QNop", "QOperand", "QOperandBox", "QPhi", "QPhiCatch", "QPhiJoin", "QPhiNop",
          "QPhiReturn", "QPutArray", "QPutField", "QRet", "QReturn", "QSSAVar", "QSimpleExpression", "QSwitch",
          "QThrow", "QUnaryOperation", "QVar", "QVoidMethodCall", "QuadrupleGenerator", "SSAConstructor",
          "SSAConstructorInfo", "SSADestructor", "SSAMessages", "SSAOptions", "SSAVar", "SubRoutine", "UnusedComputer", "Optimizer")
  messageFiles =  listOf("SSAMessages")
  optionFiles =   listOf("SSAOptions")

}

inPackage("org.kopi.bytecode.ksm") {
  grammar =       listOf("Ksm")
  javaFiles =     listOf("Assembler", "KsmError", "LabelReference", "Main", "MethodBody", "UnresolvableLabelException", "Utils")
  messageFiles =  listOf("KsmMessages")
  optionFiles =   listOf("KsmOptions")
}

inPackage("org.kopi.bytecode.dis") {
  javaFiles =     listOf("Constants", "Disassembler", "HandleCreator", "InstructionHandle", "IndentingWriter", "Main",
          "OpcodeNames")
  messageFiles =  listOf("DisMessages")
  optionFiles =   listOf("DisOptions")
}

inPackage("org.kopi.bytecode.memcnt") {
  javaFiles =     listOf("Instrumenter", "Main", "Registry")
  messageFiles =  listOf("MemcntMessages")
  optionFiles =   listOf("MemcntOptions")
}

inPackage("org.kopi.kopi.comp.kjc") {
  grm1voc =           listOf("Kjc")
  scanner1 =          listOf("Kjc")
  messageFiles =      listOf("KjcMessages")
  optionFiles =       listOf("KjcOptions")
  compilerClasses =   listOf("BytecodeOptimizer", "CArrayType", "CBadClass", "CBinaryClass", "CBinaryField",
          "CBinaryMethod", "CBlockContext", "CBlockError", "CBodyContext", "CBooleanType", "CByteType",
          "CCatchContext", "CCharType", "CClass", "CClassContext", "CClassNameType", "CReferenceType",
          "CCompilationUnit", "CCompilationUnitContext", "CConstructorContext", "CContext",
          "CClassOrInterfaceType", "CDoubleType", "CExpressionContext", "CExpressionError", "CField",
          "CFloatType", "CInitializerContext", "CIntType", "CInterfaceContext", "CLabeledContext",
          "CLineError", "CLongType", "CLoopContext", "CMember", "CMethod", "CMethodContext",
          "CMethodNotFoundError", "CModifier", "CNullType", "CNumericType", "CParseClassContext",
          "CParseEnumContext", "CParseCompilationUnitContext", "CPrimitiveType", "CShortType",
          "CSimpleBodyContext", "CSourceClass", "CSourceField", "CSourceMethod", "CStdType",
          "CSwitchBodyContext", "CSwitchGroupContext", "CThrowableInfo", "CTryContext", "CTryFinallyContext",
          "CType", "CVariableInfo", "CVoidType", "CodeLabel", "CodeSequence", "Constants", "DefaultFilter",
          "JCheckedExpression", "KjcPrettyPrinter", "KjcScanner", "KjcVisitor", "Main", "KjcEnvironment",
          "TypeFactory", "KjcTypeFactory", "ClassReader", "KjcClassReader", "CTypeContext", "CBinaryTypeContext",
          "SignatureParser", "CTypeVariable", "KjcSignatureParser", "CBinaryType", "CTypeVariableAlias",
          "CErasedReferenceType", "GenerationContext")
  syntaxTreeClasses = listOf("JAddExpression", "JArrayAccessExpression", "JArrayInitializer", "JArrayLengthExpression",
          "JAssignmentExpression", "JBinaryArithmeticExpression", "JBinaryExpression", "JBitwiseComplementExpression",
          "JBitwiseExpression", "JBlock", "JBooleanLiteral", "JBreakStatement", "JByteLiteral", "JCastExpression", "JCatchClause",
          "JCharLiteral", "JClassBlock", "JClassDeclaration", "JEnumDeclaration", "JClassExpression", "JClassFieldDeclarator",
          "JClassImport", "JCompilationUnit", "JCompoundAssignmentExpression", "JCompoundStatement", "JConditionalAndExpression",
          "JConditionalExpression", "JConditionalOrExpression", "JConstructorBlock", "JConstructorCall", "JConstructorDeclaration",
          "JContinueStatement", "JDivideExpression", "JDoStatement", "JDoubleLiteral", "JEmptyStatement",
          "JEqualityExpression", "JEnhancedForStatement", "JExpression", "JExpressionListStatement", "JExpressionStatement",
          "JFieldAccessExpression", "JFieldDeclaration", "JFloatLiteral", "JForStatement", "JEnhancedForStatement",
          "JFormalParameter", "JGeneratedLocalVariable", "JIfStatement", "JInitializerDeclaration", "JInstanceofExpression",
          "JIntLiteral", "JInterfaceDeclaration", "JLabeledStatement", "JLiteral", "JLocalVariable", "JLocalVariableExpression",
          "JLogicalComplementExpression", "JLongLiteral", "JLoopStatement", "JMemberDeclaration", "JMethodCallExpression",
          "JEnumMethodValueOf", "JEnumMethodValues", "JMethodDeclaration", "JMinusExpression", "JModuloExpression",
          "JMultExpression", "JNameExpression", "JNewArrayExpression", "JNullLiteral", "JOuterLocalVariableExpression",
          "JPackageImport", "JPackageName", "JParenthesedExpression", "JPhylum", "JPostfixExpression", "JPrefixExpression",
          "JQualifiedAnonymousCreation", "JQualifiedInstanceCreation", "JRelationalExpression", "JReturnStatement",
          "JShiftExpression", "JShortLiteral", "JStatement", "JOwnerExpression", "JStringLiteral", "JSuperExpression",
          "JSwitchGroup", "JEnumSwitchInnerDeclaration", "JSwitchLabel", "JSwitchStatement", "JSynchronizedStatement",
          "JThisExpression", "JThrowStatement", "JTryCatchStatement", "JTryFinallyStatement", "JTypeDeclaration", "JTypeDeclarationStatement",
          "JTypeNameExpression", "JUnaryExpression", "JUnaryMinusExpression", "JUnaryPlusExpression", "JUnaryPromote",
          "JUnqualifiedAnonymousCreation", "JUnqualifiedInstanceCreation", "JVariableDeclarationStatement", "JVariableDefinition",
          "JWhileStatement", "JAccessorMethod", "LanguageExtensions", "KopiInvariantDeclaration", "KopiInvariantStatement",
          "KopiConstraintStatement", "KopiPostconditionDeclaration", "KopiPreconditionDeclaration", "KopiPreconditionStatement",
          "KopiMethodCallExpression", "KopiMethodDeclaration", "KopiMethodPreconditionDeclaration", "KopiPostconditionStatement",
          "KopiReturnStatement", "KopiMethodPostconditionDeclaration", "KopiImplicitReturnBlock", "KopiAssertionClassDeclaration",
          "KopiConstructorDeclaration", "KopiConstructorBlock", "KopiReturnValueExpression", "KopiStoreClassDeclaration",
          "KopiOldValueExpression", "KopiStoreFieldAccessExpression", "KopiOldValueStoreCreation",
          "KopiAssertStatement", "KopiFailStatement")

  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
}

inPackage("org.kopi.kopi.lib.assertion") {
  javaFiles = listOf("AssertionError", "AssertionRuntime", "InvariantError", "PostconditionError", "PreconditionError")
}

inPackage("org.kopi.xkopi.lib.type") {
  javaFiles = listOf("Date", "Fixed", "Month", "NotNullDate", "NotNullFixed", "NotNullMonth", "NotNullTime",
          "NotNullTimestamp", "NotNullWeek", "Time", "Timestamp", "Type", "Week")
}

inPackage("org.kopi.xkopi.lib.base") {
  javaFiles = listOf("Connection", "Cursor", "DBConstraintException", "DBContext", "DBContextHandler",
          "DBCursorException", "DBDeadLockException", "DBException", "DBInterruptionException", "DBInvalidDataException",
          "DBNoRowException", "DBRuntimeException", "DBTooManyRowsException", "DBUnspecifiedException",
          "DBDuplicateIndexException", "DBForeignKeyException", "DBUtils", "DefaultDBContextHandler",
          "DriverInterface", "JdbcParser", "KopiSerializable", "KopiUtils", "Query", "XInterruptProtectedException",
          "OpenEdgeDriverInterface", "As400DriverInterface", "OracleDriverInterface", "PostgresDriverInterface",
          "SapdbDriverInterface")
  optionFiles = listOf("ConnectionOptions")
}

inPackage("org.kopi.xkopi.comp.sqlc") {
  grm1voc =           listOf("Sqlc")
  scanner1 =          listOf("Sqlc")
  compilerClasses =   listOf("DefaultSqlVisitor", "Main", "SqlcScanner", "SqlContext", "SqlcPrettyPrinter",
          "SqlVisitor", "DBChecker", "SCompilationUnitContext", "Constants", "SqlChecker")
  syntaxTreeClasses = listOf("AndCondition", "ArrayPrecision", "Assignment", "BetweenPredicate",
          "BinaryExpression", "BinarySearchCondition", "BlobType", "BoolType", "BooleanLiteral", "CastPrimary",
          "CharType", "ClobType", "ComparisonPredicate", "CorrespondingSpec", "CountExpression", "DbpipeExpression",
          "DefaultValuesInsertSource", "DeleteStatement", "DoubleType", "ExistsPredicate", "Expression", "ExpressionList",
          "ExpressionPredicate", "ExtendedPredicate", "FieldNameList", "FieldReference", "FloatType", "FromClause",
          "GroupByClause", "HavingClause", "InsertSource", "InsertStatement", "IntegerLiteral", "IntegerType",
          "IntersectSpec", "IntersectTableReference", "IntersectTableTerm", "IsPredicate", "JdbcDateLiteral",
          "JdbcEscape", "JdbcFunction", "JdbcTimeLiteral", "JdbcTimeStampLiteral", "JoinPred", "LikePredicate",
          "Literal", "MatchesPredicate", "MinusExpression", "NotCondition", "NullLiteral", "NumericLiteral",
          "NumericType", "OnJoinPred", "OrCondition", "OuterJoin", "ParenthesedTableReference", "PlusExpression",
          "Predicate", "QuantifiedPredicate", "RealType", "SCompilationUnit", "SearchCondition",
          "SearchedCaseExpression", "SearchedWhenClause", "SelectElem", "SelectElemExpression",
          "SelectElemStar", "SelectExpression", "SelectStatement", "SelectTableReference", "SetFunction",
          "SimpleCaseExpression", "SimpleIdentExpression", "SimpleSearchCondition", "SimpleSubTableReference",
          "SimpleTableReference", "SimpleWhenClause", "SlashExpression", "SmallIntType", "SortElem",
          "SortSpec", "SqlPhylum", "StarExpression", "Statement", "StringLiteral", "StringType",
          "SubTable", "SubTableExpression", "SubTableReference", "TableAlias", "TableExpressionInsertSource",
          "TableInsertSource", "TableName", "TableReference", "TinyIntType", "TupleAssignment",
          "TupleInPredicate", "Type", "TypeName", "UnaryExpression", "UnaryMinusExpression",
          "UnaryPlusExpression", "UnidiffSpec", "UnidiffTableReference", "UnidiffTableTerm",
          "UpdSpec", "UpdateStatement", "UsingJoinPred", "ValueInPredicate", "ValueList",
          "ValueListInsertSource", "WhereClause", "FieldNameListTableReference", "XUtils", "SubOuterJoin",
          "JdbcOuterJoin")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty() + listOf("DbSchemaVisitor")
  messageFiles =      listOf("SqlcMessages")
  optionFiles =       listOf("SqlcOptions")
}

inPackage("org.kopi.xkopi.comp.xkjc") {
  grm1voc =       listOf("XKjc")
  grm1dep =       listOf("../../../kopi/comp/kjc/Kjc")
  scanner1 =      listOf("XKjc")
  grm2voc =       listOf("XSqlc")
  grm2dep =       listOf("../../../xkopi/comp/sqlc/Sqlc")
  scanner2 =      listOf("XSqlc")
  compilerClasses = listOf("JavaSqlContext", "XKjcScanner", "XSqlcScanner", "Main", "XConstants", "XUtils",
          "XKjcPrettyPrinter", "XProtectedContext", "XUnprotectedContext", "XPrimitiveClassType", "XDateType",
          "XTimeType", "XMonthType", "XFixedType", "XWeekType", "XTimestampType", "XSqlChecker",
          "XSqlVisitor", "XSqlcPrettyPrinter", "XKjcSignatureParser", "XKjcTypeFactory", "XTypeFactory")
  syntaxTreeClasses = listOf("SqlExprJava", "XAddExpression", "XAssignmentExpression", "XBitwiseComplementExpression",
          "XBitwiseExpression", "XCastExpression", "XCompoundAssignmentExpression", "XCursor", "XContextCursorDeclaration",
          "XCursorDeclaration", "XCursorField", "XCursorFieldDeclaration", "XCursorFieldExpression", "XDivideExpression",
          "XEqualityExpression", "XExecSqlExpression", "XExecSqlStatement", "XExpression", "XFixedLiteral", "XFormalParameter",
          "XIfCondition", "XLocalCursorDeclaration", "XLogicalComplementExpression", "XMethodCallExpression", "XMinusExpression",
          "XModuloExpression", "XMultExpression", "XMutableType", "XNameExpression", "XOperatorDeclaration",
          "XOverloadedMethodCallExpression", "XPostfixExpression", "XPrefixExpression", "XProtectedStatement",
          "XRelationalExpression", "XReturnStatement", "XSelectStatement", "XShiftExpression", "XSqlExpr",
          "XStatement", "XStdType", "XTypedSelectElem", "XTypedSqlExpr", "XUnaryMinusExpression", "XUnaryPlusExpression",
          "XUnprotectedStatement", "XVariableDefinition", "XDatabaseColumn", "XDatabaseMember", "XDatabaseTable",
          "XKjcEnvironment", "XProtectedExceptionHandler", "XTypeFactory", "XKjcTypeFactory", "XNewArrayExpression",
          "XGotoStatement", "XParseSqlExpression", "XExecInsertExpression")
  javaFiles =     compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =  listOf("XKjcMessages", "XSqlcMessages")
  optionFiles =   listOf("XKjcOptions")
}

inPackage("org.kopi.xkopi.lib.oper") {
  xFiles = listOf("XBoolean", "XByte", "XCharacter", "XDate", "XDouble", "XFixed", "XFloat", "XInteger",
          "XMonth", "XShort", "XString", "XTime", "XTimestamp", "XWeek")
  jFiles = listOf("NullValueInComparison", "NullValueInCastOperator", "NullValueInOperation")
  nonStandardCompiler = true
}

inPackage("org.kopi.xkopi.comp.database") {
  javaFiles = listOf("DatabaseMember", "DatabaseTable", "DatabaseColumn", "DatabaseClobColumn",
          "DatabaseIntegerColumn", "DatabaseStringColumn", "DatabaseTimestampColumn", "DatabaseEnumColumn",
          "DatabaseImageColumn", "DatabaseDateColumn", "DatabaseMonthColumn", "DatabaseFixedColumn",
          "DatabaseTextColumn", "DatabaseWeekColumn", "DatabaseBooleanColumn", "DatabaseColorColumn",
          "DatabaseTimeColumn", "DatabaseShortColumn", "DatabaseByteColumn", "DatabaseBlobColumn",
          "DatabaseCardinalColumn")
}

inPackage("org.kopi.xkopi.comp.dbi") {
  grm1voc =     listOf("Dbi")
  grm1dep =     listOf("../../../xkopi/comp/sqlc/Sqlc")
  scanner1 =    listOf("Dbi")
  compilerClasses = listOf("DefaultDbiVisitor", "Main", "DBAccess", "DbiScanner", "DbiVisitor",
          "DbiPrettyPrinter", "Constants", "DbiChecker", "DbiPhylum")
  syntaxTreeClasses = listOf("AddTableConstraintStatement", "AddTableColumnsStatement", "AlterColumnStatement",
          "AlterSetDefaultColumnStatement", "AlterDropDefaultColumnStatement", "AlterNotNullColumnStatement", "As400DbiChecker",
          "BlobType", "BooleanType", "ByteType", "ClobType", "CodeBoolDesc", "CodeBoolType", "CodeDesc", "CodeFixedDesc",
          "CodeFixedType", "CodeLongDesc", "CodeLongType", "ColorType", "Column", "Console", "Constraint", "DateType",
          "DbCheck", "DbiStatement", "DbiType", "DelimSpec", "DoubleType", "DropIndexStatement", "DropTableConstraintStatement",
          "DropTableStatement", "DropViewStatement", "EnumType", "FixedType", "FloatType", "GrantPrivilegeStatement",
          "GrantUserClassStatement", "ImageType", "IndexDefinition", "IndexElem", "IntType", "Key", "KopiDbiChecker",
          "MonthType", "NullDelimiterSpec", "NullSpec", "ReferencedTableAndColumns", "OracleDbiChecker",
          "PostgresDbiChecker", "Pragma", "ReferentialConstraintDefinition", "SapdbDbiChecker", "SCompilationUnit",
          "ScriptHeadStatement", "SequenceDefinition", "DropSequenceStatement", "ShortType", "SpoolFileStatement",
          "SpoolTableStatement", "StringType", "TableConstraint", "TableDefinition", "TablePrivilege", "TextType",
          "TimeType", "TimestampType", "UniqueConstraintDefinition", "ViewColumn", "ViewDefinition", "WeekType",
          "OraSpoolerDbiChecker", "OpenEdgeDbiChecker")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  optionFiles =       listOf("DbiOptions", "ConsoleOptions")
  messageFiles =      listOf("DbiMessages")
}

inPackage("org.kopi.xkopi.comp.dict") {
  javaFiles = listOf("Main", "DBInterface")
}

inPackage("org.kopi.util.ipp") {
  javaFiles =   listOf("BooleanValue", "DateValue", "IntegerValue", "IPPAttribute", "IPPConstants", "IPPHeader",
          "IPPHttpConnection", "IPPHttpHeader", "IPPHttp", "IPPInputStream", "IPP", "IPPOutputStream", "IPPClient",
          "IPPValue", "LangValue", "RangeValue", "ResolutionValue", "StringValue", "Main")
  optionFiles = listOf("IPPOptions")
}

inPackage("org.kopi.util.lpr") {
  javaFiles =   listOf("LpdClient", "LpQ", "LpR", "LpRm", "LpdException", "LpdOptions", "LpQOptions", "LpRmOptions", "LpROptions")
  optionFiles = listOf("LpdOptions", "LpQOptions", "LpRmOptions", "LpROptions")
}

inPackage("org.kopi.util.html") {
  javaFiles = listOf("HtmlDocument", "HtmlTableCell", "HtmlTable", "HtmlTableRow")
}

inPackage("org.kopi.util.mailer") {
  javaFiles =   listOf("Attachment", "Mailer", "SMTPException")
  optionFiles = listOf("MailerOptions")
}

inPackage("org.kopi.vkopi.lib.base") {
  javaFiles = listOf("ExtendedChoiceFormat", "ExtendedMessageFormat", "UComponent", "Image", "Utils")
}

inPackage("org.kopi.vkopi.lib.l10n") {
  javaFiles =   listOf("ActorLocalizer", "BlockLocalizer", "FieldLocalizer", "FormLocalizer", "ListLocalizer",
          "LocalizationChecker", "LocalizationManager", "Localizer", "MenuLocalizer", "MessageLocalizer", "ModuleLocalizer",
          "ReportLocalizer", "TypeLocalizer", "Utils", "PropertyLocalizer", "ChartLocalizer")
  optionFiles = listOf("LocalizationCheckerOptions")
}

inPackage("org.kopi.vkopi.lib.util") {
  javaFiles = listOf("AWTToPS", "AbstractPrinter", "CachePrinter", "Charset437", "FPrinter",
          "Fax", "FaxException", "FaxPrinter", "FaxStatus", "Filter", "GroupHandler", "HylaFAXPrinter",
          "IPPPrinter", "LPrinter", "LpRPrinter", "PPaperType", "PixelConsumer", "PlatformFileWriter",
          "PreviewPrinter", "PrintException", "PrintInformation", "PrintJob", "Printer", "RPrinter",
          "Rexec", "To437", "ToLatin1", "HylaFAXUtils", "LineBreaker")
}

inPackage("org.kopi.vkopi.lib.visual") {
  javaFiles = listOf("ActionHandler", "Application", "ApplicationContext", "Constants", "FileHandler",
          "KopiAction", "KopiExecutable", "MessageCode", "Module", "Item", "DPositionPanelListener", "Registry",
          "WaitDialogListener", "ProgressDialogListener", "VActor", "VDefaultActor", "VActionListener", "VCommand",
          "VException", "PreviewRunner", "VColor", "VExecFailedException", "VHelpGenerator", "VInterruptException",
          "VMenuTree", "VItemTree", "VRuntimeException", "VTrigger", "VWindow", "VDatabaseUtils", "WindowController",
          "WindowBuilder", "WaitInfoListener", "MessageListener", "VerifyConfiguration", "ApplicationConfiguration",
          "PrinterManager", "PropertyException", "ModelCloseListener", "UserConfiguration", "Message", "VlibProperties",
          "UWindow", "UMenuTree", "ImageHandler", "VModel", "UIFactory", "UActor", "FileProductionListener",
          "LogoutModule", "RootMenu", "RootItem", "UItemTree", "ItemTreeManager")
}

inPackage("org.kopi.vkopi.lib.list") {
  javaFiles = listOf("VBooleanCodeColumn", "VBooleanColumn", "VCodeColumn", "VColorColumn", "VColumn",
          "VConstants", "VDateColumn", "VFixnumCodeColumn", "VFixnumColumn", "VImageColumn", "VIntegerCodeColumn",
          "VIntegerColumn", "VList", "VListColumn", "VMonthColumn", "VStringCodeColumn", "VStringColumn", "VTextColumn",
          "VTimeColumn", "VWeekColumn", "VTimestampColumn", "ObjectFormater")
}

inPackage("org.kopi.vkopi.lib.form") {
  javaFiles =   listOf("AbstractFieldHandler", "BlockAlignment", "Commands", "FieldHandler", "KopiAlignment",
          "LatexPrintWriter", "MultiFieldAlignment", "Triggers", "VBlock", "VBooleanCodeField", "VBooleanField",
          "VCodeField", "VColorField", "VConstants", "VDateField", "VDictionaryForm", "VDocGenerator", "VDictionary",
          "VField", "VFieldCommand", "VFieldException", "VFieldUI", "VFixnumCodeField", "VFixnumField", "VForm",
          "VHelpGenerator", "VImageField", "VImportedBlock", "VIntegerCodeField", "VIntegerField", "VListDialog",
          "VMonthField", "VPosition", "BlockRecordListener", "VQueryNoRowException", "VQueryOverflowException",
          "VSkipRecordException", "VStringCodeField", "VStringField", "VTextField", "VTimeField", "VTimestampField",
          "VWeekField", "KopiLayoutManager", "FieldListener", "FieldChangeListener", "BlockListener", "ViewBlockAlignment",
          "FormListener", "PredefinedValueHandler", "AbstractPredefinedValueHandler", "Accessor", "ModelTransformer",
          "UBlock", "UChartLabel", "UField", "UForm", "ULabel", "UMultiBlock", "UTextField", "UListDialog",
          "VBlockDefaultOuterJoin", "VBlockOracleOuterJoin", "UActorField", "VActorField")
}

inPackage("org.kopi.vkopi.lib.preview") {
  javaFiles = listOf("VPreviewWindow", "PreviewListener")
}

inPackage("org.kopi.vkopi.lib.print") {
  javaFiles = listOf("PPage", "PBlock", "PPosition", "PSize", "PRectangleBlock", "PStyle", "PBlockStyle",
          "PTextStyle", "PTabStop", "PTextBlock", "PLayoutEngine", "Metrics", "PParagraphStyle", "PBodyStyle",
          "PRecursiveBlock", "PListBlock", "PProtectedPage", "PHorizontalBlock", "PreviewPrinter", "Printable",
          "PrintManager", "DefaultPrintManager", "LabelPrinter", "PSPrintException", "PdfPrintJob", "Laserjet",
          "EpsonLQ", "MailPrinter")
}

inPackage("org.kopi.vkopi.lib.chart") {
  javaFiles = listOf("ChartTypeFactory", "UChart", "UChartType", "VBooleanCodeDimension", "VBooleanDimension",
          "VChart", "VChartCommand", "VChartType", "VCodeDimension", "VCodeMeasure", "VColumn", "VColumnFormat",
          "CConstants", "VDataSeries", "VDateDimension", "VDefaultChartActor", "VDimension", "VFixnumCodeDimension",
          "VFixnumCodeMeasure", "VFixnumDimension", "VFixnumMeasure", "VHelpGenerator", "VIntegerCodeDimension",
          "VIntegerCodeMeasure", "VIntegerDimension", "VIntegerMeasure", "VMeasure", "VMonthDimension", "VNoChartRowException",
          "VPrintOptions", "VRow", "VStringCodeDimension", "VStringDimension", "VTimeDimension", "VTimestampDimension",
          "VWeekDimension", "VCodeMeasure", "VDimensionData")
}

inPackage("org.kopi.vkopi.lib.report") {
  javaFiles = listOf("Constants", "MReport", "PConfig", "ReportListener", "Triggers", "VBaseRow",
          "VBooleanCodeColumn", "VBooleanColumn", "VCCDepthFirstCircuitN", "VCalculateColumn", "VCellFormat",
          "VCodeColumn", "VDateColumn", "VFixnumCodeColumn", "VFixnumColumn", "VGroupRow", "VIntegerCodeColumn",
          "VIntegerColumn", "VMonthColumn", "VReport", "VReportColumn", "VReportRow", "VStringCodeColumn",
          "VStringColumn", "VTimeColumn", "VTimestampColumn", "VWeekColumn", "VNoRowException", "VSeparatorColumn",
          "VHelpGenerator", "Point", "PExport", "PExport2XLS", "PExport2PDF", "PExport2CSV", "UReport",
          "VDefaultReportActor", "VReportCommand", "ColumnStyle", "Parameters", "CellStyleCacheManager",
          "PExport2XLSX", "PExport2Excel")
}

inPackage("org.kopi.vkopi.lib.cross") {
  javaFiles = listOf("VReportSelectionForm", "VPrintSelectionForm", "VMultiPrintSelectionForm", "VDynamicReport",
          "VChartSelectionForm")
}

inPackage("org.kopi.vkopi.lib.doc") {
  javaFiles = listOf("Main")
}

inPackage("org.kopi.vkopi.lib.ui.swing.base") {
  javaFiles = listOf("JFieldLabel", "FieldStates", "JMenuButton", "KopiTitledBorder", "JButtonPanel",
          "ListDialogCellRenderer", "JFieldButton", "JDisablePanel", "TextSelecter", "Stateful", "Utils", "MultiLineToolTip",
          "MultiLineToolTipUI", "KnownBugs", "JActorFieldButton", "JHtmlTextArea")
}

inPackage("org.kopi.vkopi.lib.ui.swing.visual") {
  javaFiles =   listOf("DActor", "DFootPanel", "DInfoPanel", "DMenuBar", "DMenuItem", "DMenuTree", "DItemTree",
          "DObject", "DPreferences", "DStatePanel", "DWaitPanel", "DWindow", "ProgressWindow", "JFileHandler", "DHelpViewer",
          "JBookmarkPanel", "Utils", "DPositionPanel", "SplashScreen", "SwingThreadHandler", "JApplicationContext", "JApplication",
          "JImageHandler", "JUIFactory", "JWindowController", "MenuItemRenderer", "MenuItemRenderer", "WaitWindow")
  optionFiles = listOf("ApplicationOptions")
}

inPackage("org.kopi.vkopi.lib.ui.swing.plaf") {
  javaFiles = listOf("KopiLabelUI", "KopiFieldLabelUI", "KopiLookAndFeel", "KopiTheme", "KopiTextFieldUI",
          "KopiTabbedPaneUI", "KopiScrollBarUI", "KopiButtonUI", "KopiTextAreaUI", "KopiUtils", "KopiPasswordFieldUI",
          "KopiProgressBarUI", "KopiUserColors", "KopiToggleButtonUI", "KopiTextPaneUI")
}

inPackage("org.kopi.vkopi.lib.ui.swing.preview") {
  javaFiles = listOf("DPreviewWindow", "StandAlone")
}

inPackage("org.kopi.vkopi.lib.ui.swing.form") {
  javaFiles = listOf("DateChooser", "DBlock", "DBlockDropTargetHandler", "DChartBlock", "DChartHeaderLabel",
          "DColorField", "DField", "DFieldUI", "DForm", "DImage", "DImageField", "DLabel", "DListDialog", "DMultiBlock",
          "DObjectField", "DPage", "DTextEditor", "DTextField", "Environment", "ImageFileChooser", "JFieldHandler",
          "JPredefinedValueHandler", "KeyNavigator", "KopiFieldDocument", "KopiMultiBlockLayout", "KopiScanDocument",
          "KopiSimpleBlockLayout", "DActorField", "KopiStyledDocument", "KopiDocument")
}

inPackage("org.kopi.vkopi.lib.ui.swing.chart") {
  javaFiles = listOf("DAbstractChartType", "DAreaChart", "DBarChart", "DChart", "DColumnChart",
          "DLineChart", "DPieChart", "JChartTypeFactory")
}

inPackage("org.kopi.vkopi.lib.ui.swing.report") {
  javaFiles = listOf("CellRenderer", "DTable", "DReport", "VTable")
}

inPackage("org.kopi.vkopi.lib.ui.swing.spellchecker") {
  javaFiles = listOf("AspellProcess", "SpellChecker", "Suggestions", "SpellException", "SpellcheckerDialog")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.base") {
  javaFiles = listOf("ChartProperty", "Colors", "PlotlyChart", "PlotlyChartFactory", "PlotlyChartState", "Util")
  jsFiles =   listOf("connector", "plotly-latest.min")
  copyFiles = jsFiles.withExtension("js")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.configuration") {
  javaFiles = listOf("DataConfiguration", "LayoutConfiguration")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data.dataseries") {
  javaFiles = listOf("AbstractDataSeries", "CoupleOfData", "DataSeries", "DataSeriesType", "RangeOfData", "SingleData")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data.features") {
  javaFiles = listOf("DataMode", "HoverInfo", "Orientation")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data.line") {
  javaFiles = listOf("Line", "LineShape", "LineStyle")

}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data.marker") {
  javaFiles = listOf("AbstractMarker", "BarMarker", "BoxPlotMarker", "BubbleMarker", "Marker", "PieMarker", "ScatterMarker")

}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data.types") {
  javaFiles = listOf("AbstractData", "AreaData", "AreaRangeData", "AreaSplineData", "AreaSplineRangeData", "BarData",
          "BoxPlotData",
          "BubbleData", "ColumnData", "ChartData", "DataType", "DonutData", "LineData", "PieData", "ScatterData",
          "SplineData")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.data") {
  javaFiles = listOf()
  jsFiles =   listOf()
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.exceptions") {
  javaFiles = listOf("DataMismatchException", "PlotlyException", "TypeMismatchException")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.plotly.layout") {
  javaFiles = listOf("Annotation", "Axis", "Layout", "LayoutWithAxis", "LayoutWithoutAxis")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.actor") {
  javaFiles =   listOf("ActorConnector", "ActorServerRpc", "ActorState", "VActor", "VActorMenuItem",
          "VActorsMenu", "VActorsNavigationPanel", "VActorNavigationItem")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.base") {
  javaFiles =   listOf("AcceleratorKeyCombination", "AcceleratorKeyHandler", "Icons", "LocalizedProperties",
          "ResourcesUtil", "ShortcutActionHandler", "ShortcutAction", "Styles", "VAnchorPanel", "VHiddenSeparator",
          "VInputButton", "VInputLabel", "VInputPassword", "VInputText", "VParagraphPanel", "VPopup", "VScrollablePanel",
          "VSeparator", "VSingleRowTable", "VSpanPanel", "VULPanel", "WidgetUtils", "VConstants", "ConnectorUtils",
          "LocalizedMessages", "DecimalFormatSymbols")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.suggestion") {
  javaFiles =   listOf("AutocompleteSuggestion", "DefaultSuggestion", "DefaultSuggestionDisplay", "DefaultSuggestOracle",
          "QueryListener", "SuggestionCallback", "SuggestionDisplay", "SuggestionHandler", "SuggestionMenu", "SuggestionMenuItem",
          "SuggestionTable", "Response")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.block") {
  javaFiles =   listOf("AbstractBlockLayoutConnector", "AbstractBlockLayoutState", "BlockAlignment",
          "BlockClientRpc", "BlockConnector", "BlockLayout", "BlockServerRpc", "BlockState", "ChartBlockLayoutConnector",
          "ChartBlockLayoutState", "ComponentConstraint", "VBlock", "MultiBlockLayoutConnector", "MultiBlockLayoutState",
          "SimpleBlockLayoutConnector", "SimpleBlockLayoutState", "VAbstractBlockLayout", "VAlignPanel", "VSimpleBlockLayout",
          "VChartBlockLayout", "VMultiBlockLayout", "VScrollBar", "VLayoutManager", "VChartBlockScrollBar", "ColumnView")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.checkbox") {
  javaFiles =   listOf("TristateChangeListener", "TristateCheckBoxClientRpc", "TristateCheckBoxConnector",
          "TristateCheckBoxServerRpc", "TristateCheckBoxState", "VTristateCheckBox")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.common") {
  javaFiles =   listOf("VAnchor", "VBromine", "VButton", "VCaption", "VClearPanel", "VCompanyLogo",
          "VContent", "VGlobalLinks", "VH4", "VHeader", "VImage", "VLine", "VLinkGroup", "VLink", "VLogo",
          "VMain", "VPopupWindow", "VSpan", "VStrong", "VTabSheet", "VIcon", "VSmall")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.date") {
  javaFiles =   listOf("DateChooserConnector", "DateChooserServerRpc", "DateChooserState", "DayClickHandler",
          "Day", "FocusChangeListener", "VEventButton", "FocusOutListener", "SubmitListener", "VCalendarPanel",
          "VDateChooser", "FocusedDate")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.event") {
  javaFiles =   listOf("ActionListener", "CloseListener", "DateChooserListener", "DimensionCellListener", "DropActionListener",
          "FieldListener", "FormListener", "LoginWindowListener", "MainWindowListener", "NotificationListener",
          "PositionPanelListener")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.grid") {
  javaFiles =   listOf("EditorDateFieldConnector", "EditorEnumFieldConnector", "EditorEnumFieldState", "EditorField",
          "EditorFieldClickServerRpc", "EditorFieldClientRpc", "EditorFieldConnector", "EditorFieldNavigationServerRpc",
          "EditorFieldState", "EditorFixnumFieldConnector", "EditorFixnumFieldState", "EditorHandlingExtensionConnector",
          "EditorHandlingExtensionServerRpc", "EditorIntegerFieldConnector", "EditorIntegerFieldState",
          "EditorLabelClickServerRpc", "EditorLabelConnector", "EditorLabelState", "EditorMonthFieldConnector",
          "EditorTextAreaFieldConnector", "EditorTextAreaFieldState", "EditorTextChangeServerRpc",
          "EditorTextFieldConnector", "EditorTextFieldState", "EditorTimeFieldConnector",
          "EditorTimestampFieldConnector", "EditorWeekFieldConnector", "InvalidEditorFieldException",
          "VEditorDateField", "VEditorEnumField", "VEditorFixnumField", "VEditorIntegerField", "VEditorLabel",
          "VEditorMonthField", "VEditorTextAreaField", "VEditorTextField", "VEditorTimeField", "VEditorTimestampField",
          "VEditorWeekField", "EditorActorFieldConnector", "EditorActorFieldState", "VEditorActorField",
          "EditorImageFieldConnector", "EditorImageFieldState", "VEditorImageField", "BooleanRendererConnector",
          "BooleanRendererState", "EditorBooleanFieldConnector", "EditorBooleanFieldState",
          "EditorBooleanFieldValueChangeServerRpc", "VBooleanRenderer", "VEditorBooleanField",
          "VFocusableEditorField", "EditorSuggestionsQueryClientRpc", "EditorSuggestionsQueryServerRpc",
          "EditorBooleanFieldClientRpc", "BooleanRendererValueChangeServerRpc", "ActorRendererConnector",
          "ActorRendererSate", "VActorRenderer")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.field") {
  javaFiles =   listOf("AllowAllValidationStrategy", "DateValidationStrategy", "EnumValidationStrategy", "FieldConnector",
          "FieldServerRpc", "FieldState", "FixnumValidationStrategy", "ImageFieldConnector", "ImageFieldServerRpc",
          "ImageFieldState", "VField", "VImageField", "IntegerValidationStrategy", "KeyNavigator", "VTextField",
          "MonthValidationStrategy", "NoeditValidationStrategy", "ObjectFieldConnector", "ObjectFieldServerRpc",
          "TextChangeServerRpc", "TextFieldClientRpc", "TextFieldNavigationHandler", "TextFieldServerRpc",
          "TextValidationStrategy", "TimeValidationStrategy", "VInputPasswordField", "VInputTextArea", "VInputTextField",
          "VObjectField", "WeekValidationStrategy", "TextFieldState", "TextFieldConnector", "StringValidationStrategy",
          "TimestampValidationStrategy", "CheckTypeException", "ActorFieldConnector", "ActorFieldServerRpc", "ActorFieldState",
          "VActorField", "BooleanFieldClientRpc", "BooleanFieldConnector", "BooleanFieldServerRpc", "BooleanFieldState",
          "VBooleanField", "RichTextFieldConnector", "RichTextFieldNavigationServerRpc", "RichTextFieldState",
          "VRichTextField", "VInputButtonField")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.form") {
  javaFiles =   listOf("BlockComponentData", "FormClientRpc", "FormConnector", "FormServerRpc",
          "FormState", "VForm", "VPage", "VPositionPanel", "VPosRequester")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.label") {
  javaFiles =   listOf("LabelConnector", "LabelServerRpc", "LabelState", "SortableLabelConnector",
          "SortableLabelListener", "SortableLabelServerRpc", "VLabel", "VSortableLabel")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.list") {
  javaFiles =   listOf("ListDialogConnector", "ListDialogServerRpc", "ListDialogState", "Sortable",
          "SortListener", "TableCell", "TableColumn", "TableHeader", "Table", "TableModel", "VListDialog",
          "GridListDialogCloseServerRpc", "GridListDialogConnector", "GridListDialogSelectionServerRpc",
          "GridListDialogState", "VGridListDialog", "GridListDialogSearchServerRpc")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.login") {
  javaFiles =   listOf("VEmptyModuleList", "VLoginBox", "VLoginView", "VLoginWindow", "VWelcomeView",
          "WelcomeViewClientRpc", "WelcomeViewConnector", "WelcomeViewServerRpc", "WelcomeViewState", "FontMetrics")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.main") {
  javaFiles =   listOf("MainWindowConnector", "MainWindowServerRpc", "VWelcome", "VLocalizedLink", "VMainWindow",
          "MainWindowState", "VWindowContainer", "VWindows", "VWindowsDisplay", "VWindowsMenu", "VWindowsMenuItem")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.menu") {
  javaFiles =   listOf("ModuleItemConnector", "ModuleItemState", "ModuleListConnector", "ModuleListServerRpc",
          "ModuleListState", "VMenuPopup", "VModuleItem", "VModuleList", "VModuleListMenu", "ModuleListClientRpc",
          "VNavigationColumn", "VNavigationItem", "VNavigationPanel", "VNavigationMenu", "VHeaderNavigationItem",
          "VClickableNavigationItem", "ContextMenuConnector")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.notification") {
  javaFiles =   listOf("AbstractNotificationConnector", "AbstractNotificationState", "ConfirmNotificationConnector",
          "ErrorNotificationConnector", "InformationNotificationConnector", "NotificationServerRpc",
          "VAbstractNotification", "VConfirmNotification", "VErrorMessagePopup", "VErrorNotification",
          "VInformationNotification", "VWarningNotification", "WarningNotificationConnector", "NotificationUtils")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.progress") {
  javaFiles =   listOf("ProgressDialogClientRpc", "ProgressDialogConnector", "ProgressDialogState",
          "VProgressBar", "VProgressDialog", "ProgressDialogServerRpc")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.upload") {
  javaFiles =   listOf("UploadClientRpc", "UploadConnector", "UploadIFrameOnloadStrategy", "UploadServerRpc",
          "UploadState", "VFileUpload", "VUpload", "VUploadForm", "VUploadProgress", "VUploadTextInput",
          "UploadProgressClientRpc", "UploadProgressConnector")
  copySources = true
}
inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar") {
  javaFiles =   listOf("Direction", "HorizontalScrollbarBundle", "JsniWorkaround", "ScrollbarBundle",
          "Scroller", "ScrollEvent", "ScrollEventFirer", "ScrollHandler", "VerticalScrollBar", "VerticalScrollbarBundle",
          "VisibilityChangeEvent", "VisibilityHandler")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.wait") {
  javaFiles =   listOf("VWaitDialog", "VWaitWindow", "WaitDialogConnector", "WaitDialogState", "WaitWindowConnector")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons.client.window") {
  javaFiles =   listOf("PopupWindowConnector", "VMoreActorsItem", "VMoreActorsPopup", "VWindowView",
          "PopupWindowState", "VMoreActors", "VPopupWindow", "WindowConnector", "VActorPanel",
          "VMoreActorsMenu", "VWindow", "WindowState", "VActorsRootMenu", "VActorsRootNavigationItem")
  copySources = true
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.addons") {
  javaFiles =   listOf("AbstractBlockLayout", "AbstractNotification", "ActionEvent", "ActionListener",
          "Actor", "Block", "BlockLayout", "BlockListener", "ChartBlockLayout", "CloseListener", "ConfirmNotification",
          "ContextMenu", "DateChooser", "DateChooserListener", "ErrorNotification", "Field", "FieldListener", "Form",
          "FormListener", "ImageField", "ImageFieldListener", "InformationNotification", "Label", "LabelEvent",
          "LabelListener", "ListDialog", "ListDialogListener", "MainWindow", "MainWindowListener", "ModuleItem",
          "ModuleList", "ModuleListEvent", "ModuleListListener", "MultiBlockLayout", "NoInputStreamEvent",
          "NoOutputStreamEvent", "NotificationListener", "ObjectField", "ObjectFieldListener", "PopupWindow",
          "ProgressDialog", "SimpleBlockLayout", "SortableLabel", "SortableLabelListener", "TextField",
          "TextFieldListener", "TextValueChangeListener", "TristateChangeListener", "TristateCheckBox",
          "Upload", "UploadChangeEvent", "UploadChangeListener", "UploadFailedEvent", "UploadFailedListener",
          "UploadFinishedEvent", "UploadFinishedListener", "UploadProgressListener", "UploadReceiver",
          "UploadStartedEvent", "UploadStartedListener", "UploadSucceededEvent", "UploadSucceededListener",
          "WaitDialog", "WaitWindow", "WarningNotification", "WelcomeView", "WelcomeViewEvent",
          "WelcomeViewListener", "Window", "GridEditorDateField", "GridEditorEnumField", "GridEditorField",
          "GridEditorFixnumField", "GridEditorHandlingExtension", "GridEditorIntegerField",
          "GridEditorLabel", "GridEditorMonthField", "GridEditorTextAreaField", "GridEditorTextField",
          "GridEditorTimeField", "GridEditorTimestampField", "GridEditorWeekField", "SingleComponentBlockLayout",
          "ActorField", "GridEditorActorField", "GridListDialog", "GridEditorImageField", "BooleanField",
          "BooleanRenderer", "GridEditorBooleanField", "UploadProgress", "RichTextField", "ActorRenderer")
  widgetSets =  listOf("KopiAddonsWidgetset.gwt")
  copyFiles =   widgetSets.withExtension("xml")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.base") {
  javaFiles = listOf("AbstractUIExecutor", "BackgroundThreadHandler", "CommunicationStatistics", "CurrentUIExecutor",
          "DynamicPollingManualPushUIExecutor", "ExportResource", "FileUploader", "Image", "KopiServlet",
          "StrategyUIExecutor", "Tree", "UIExecutor", "UIRunnable", "UIRunnableFuture", "FontMetrics", "Utils",
          "StylesInjector")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.list") {
  javaFiles = listOf("ListContainer", "ListConverter", "ListFilter", "ListItem", "ListProperty", "ListStyleGenerator",
          "ListTable")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.form") {
  javaFiles = listOf("DateChooser", "DBlock", "DBlockDropHandler", "DChartBlock", "DChartHeaderLabel", "DField",
          "DFieldHandler", "DFieldUI", "DForm", "DImageField", "DLabel", "DListDialog", "DMultiBlock",
          "DObjectField", "DTextEditor", "DTextField", "KeyNavigator", "VPredefinedValueHandler", "DGridBlock",
          "DGridMultiBlock", "DGridBlockCellStyleGenerator", "DGridBlockContainer", "DGridBlockFieldHandler",
          "DGridBlockFieldUI", "DGridEditorField", "DGridEditorLabel", "DGridTextEditorField", "DActorField",
          "DGridEditorActorField", "DGridEditorImageField", "DBooleanField", "DGridEditorBooleanField",
          "DGridBlockItemSorter", "DRichTextEditor", "DGridBlockFilter")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.visual") {
  javaFiles = listOf("DActor", "DMenuTree", "DWindow", "VFileHandler", "DHelpViewer", "VApplicationContext",
          "VApplication", "VUIFactory", "VImageHandler", "VWindowController", "DAdminMenu", "DBookmarkMenu", "DMainMenu",
          "DMenu", "DUserMenu", "DItemTree", "Tree")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.chart") {
  javaFiles = listOf("DAbstractChartType", "DAreaChart", "DBarChart", "DChart", "DColumnChart",
          "DLineChart", "DPieChart", "VChartTypeFactory", "ChartKeyedValues")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.report") {
  javaFiles = listOf("VTable", "StyleGenerator", "ReportCellStyleGenerator", "DTable", "DReport")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.preview") {
  javaFiles = listOf("DPreviewWindow")
}

inPackage("org.kopi.vkopi.lib.ui.vaadin.resource") {
  nonStandardBuild =  true
  val freeGifs =      listOf("list", "loading", "otherTab", "topRibbon", "calendar")
  val oldGifs =       listOf<String>()
  val pngFiles =      listOf("all", "bg_header", "block", "border", "bottomRibbon", "bread_crumb_separator", "break",
          "calendar", "collapsed_a", "collapsed_p", "collapsed", "copy", "delete", "desk", "detail_view", "detail",
          "down", "duke", "edit", "expanded_a", "expanded_p", "expanded", "exportCsv", "exportPdf", "exportXls",
          "flag-de", "flag-fr", "flag-gb", "flag-tn", "fold", "foldColumn", "footer_icon", "form_a", "form_p",
          "form_selected", "forms", "formula", "help", "home", "insert", "insertline", "list", "login_img", "logo_kopi",
          "logo_srd_excellence_operationelle", "logo_srd", "mail", "menuquery", "note", "nothing", "open", "options",
          "preview", "print", "quit", "ribbon", "save", "search", "searchop", "separator", "serialquery", "serviceoff",
          "serviceon", "slogan", "splash_image", "store", "suggest", "timeStamp", "topRibbon", "tri", "unfold",
          "unfoldColumn", "up", "exportXlsx", "item", "node", "checked", "unchecked", "default")
  val pngNew =        listOf("")
  val psFiles =       listOf("")
  val localizations = listOf<String>()
  val otherFiles =    listOf("")
  gifFiles =          freeGifs + oldGifs
  resources =         pngFiles.withExtension("png") + pngNew.withExtension("png") + gifFiles.withExtension("gif") +
          psFiles.withExtension("ps") + localizations.map { it + "-de_AT.xml" } +
          localizations.map { it + "-fr_FR.xml" } + localizations.map { it + "-en_GB.xml" } +
          localizations.map { it + "-ar_TN.xml" } + otherFiles
}

inPackage("org.kopi.vkopi.lib.resource") {
  val freeGifs =  listOf<String>()
  val oldGifs =   listOf("all", "apply", "arrowleft", "arrowright", "article", "ask", "bkup", "bkup3", "block",
          "block2", "board", "bomb", "bookmark", "boxarrow", "break", "bw", "calculate", "cfolder", "clip", "collapsed",
          "collapsed_f", "collapsed_s", "collapsed_t", "collapsedb", "combo", "config", "convert", "copy", "delete",
          "deleteline", "desk", "detail", "done", "duke", "edit", "error", "expanded", "expanded_f", "expanded_s",
          "expanded_t", "expandedb", "export", "fax", "fold", "foldColumn", "form", "form_f", "form_s", "form_t",
          "forms", "forms_f", "forms_s", "forms_t", "fw", "gifIcon", "green", "guide", "help", "home", "index",
          "info", "info2", "insert", "insertline", "interrupt", "jpgIcon", "launch", "list", "lock", "login",
          "mail", "menuquery", "money", "moneycheck", "noIcon", "note", "nothing", "notice", "ofolder", "open",
          "options", "pageLeft", "pageRight", "phone", "preview", "print", "printoptions", "project", "quit",
          "red", "refresh", "reload", "report", "save", "search", "search2", "searchop", "sec", "selected",
          "send", "serialquery", "serviceoff", "serviceon", "sort", "split", "standard", "stick", "stop",
          "store", "suggest", "todo", "top", "unfold", "unfoldColumn", "unstick", "users", "utils", "validate",
          "wait", "warning", "window", "yellow", "arrowfirst", "arrowlast")
  val pngFiles = listOf("add", "all", "apply", "article", "ask", "bkup", "bkup3", "block", "block2",
          "board", "bomb", "bookmark", "boxarrow", "break", "bw", "clip", "config", "convert", "copy",
          "delete", "deleteline", "desk", "detail", "done", "duke", "edit", "error", "export", "fold",
          "foldColumn", "detail_view", "chart_view", "fw", "gifIcon", "help", "home", "calculate", "insert",
          "insertline", "jpgIcon", "launch", "list", "lock", "login", "mail", "menuquery", "money", "note",
          "nothing", "notice", "open", "options", "pageLeft", "pageRight", "pageFirst", "pageLast", "phone",
          "preview", "print", "printoptions", "project", "quit", "refresh", "reload", "report", "save", "search",
          "search2", "searchop", "sec", "send", "serialquery", "serviceoff", "serviceon", "sort", "split", "standard",
          "stop", "store", "suggest", "todo", "top", "unfold", "unfoldColumn", "update", "users", "utils", "validate",
          "wait", "warning", "forms", "checkbox", "checked", "unchecked", "default", "checked_s", "unchecked_s",
          "default_s", "radio_checked", "radio_checked_s")
  val pngNew =  listOf("align_center", "align_justify", "align_left", "align_right", "bold", "cut", "ident",
          "italic", "paste", "redo", "unident", "underline", "undo", "border", "password", "zoomminus", "zoomwidth",
          "zoomheight", "zoomoptimal", "zoomplus", "formula", "area_chart", "column_chart", "pie_chart", "bar_chart", "line_chart")
  val psFiles =       listOf("StoneGerman", "german", "report")
  val localizations = listOf("General", "HelpViewer", "Preview", "Menu", "Window", "Messages", "VKMessages", "VlibProperties", "RootMenu")
  val otherFiles =    listOf("general.tex", "macros.tex", "splash.jpg", "titlepage.sty", "top.tex")
  gifFiles =          freeGifs + oldGifs
  resources =         pngFiles.withExtension("png") + pngNew.withExtension("png") + gifFiles.withExtension("gif") +
          psFiles.withExtension("ps") + localizations.map { it + "-de_AT.xml" } + localizations.map { it + "-fr_FR.xml" } +
          localizations.map { it + "-en_GB.xml" } + localizations.map { it + "-ar_TN.xml" } + otherFiles
  nonStandardBuild =  true
}

inPackage("org.kopi.vkopi.comp.trig") {
  grm1voc =   listOf("GKjc")
  grm1dep =   listOf("../../../kopi/comp/kjc/Kjc", "../../../xkopi/comp/xkjc/XKjc")
  scanner1 =  listOf("GKjc")
  grm2voc =   listOf("GSqlc")
  grm2dep =   listOf("../../../xkopi/comp/sqlc/Sqlc", "../../../xkopi/comp/xkjc/XSqlc")
  scanner2 =  listOf("GSqlc")
  javaFiles = listOf("Main", "GVKAccess", "GStdType", "GTypeFactory", "GCompoundAssignmentExpression",
          "GKopiInsertExpression", "GAssignmentExpression", "GProtectionStatementWrapper", "GKjcPrettyPrinter",
          "GExecSqlStatement", "GKjcScanner", "GKjcTypeFactory", "GSqlcScanner")
  messageFiles = listOf("GKjcMessages")
}

inPackage("org.kopi.vkopi.comp.base") {
  grm1voc =         listOf("Base")
  grm1dep =         listOf()
  scanner1 =        listOf("Base")
  optionFiles =     listOf("VKOptions")
  compilerClasses = listOf("BaseScanner", "Parser", "VKParseContext", "VKParseCommandContext", "VKParseVKWindowContext")
  syntaxTreeClasses = listOf("Commandable", "VKAction", "VKActor", "VKBlockAction", "VKBooleanCodeType",
          "VKBooleanType", "VKCodeDesc", "VKCodeType", "VKColorType", "VKCommand", "VKCommandBody", "VKCommandDefinition",
          "VKCommandName", "VKCompilationUnit", "VKConstants", "VKContext", "VKFieldListAction", "VKDateType",
          "VKDefaultCommand", "VKDefinition", "VKDefinitionCollector", "VKStringCodeType", "VKEnvironment",
          "VKExternAction", "VKFieldList", "VKFixnumCodeType", "VKFixnumType", "VKHelpUtils", "VKImageType",
          "VKInsert", "VKInsertParser", "VKIntegerCodeType", "VKIntegerType", "VKInternAction", "VKKjcTypeFactory",
          "VKLatexPrintWriter", "VKListDesc", "VKMenuDefinition", "VKMethodAction", "VKMonthType", "VKPhylum",
          "VKPrettyPrinter", "VKStdType", "VKStringType", "VKTextType", "VKTimeType", "VKTimestampType", "VKTopLevel",
          "VKTrigger", "VKType", "VKTypeFactory", "VKTypeDefinition", "VKUtils", "VKWeekType", "VKWindow",
          "VKLocalizationWriter", "VKMessageDefinition")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =      listOf("BaseMessages")
}

inPackage("org.kopi.vkopi.comp.form") {
  grm1voc =           listOf("Form")
  grm1dep =           listOf("../base/Base")
  scanner1 =          listOf("Form")
  compilerClasses =   listOf("FormScanner", "VKFormLocalizationWriter", "VKParseFormContext", "VKParseBlockContext",
          "VKParseFieldContext")
  syntaxTreeClasses = listOf("VKBlock", "VKBlockAlign", "VKBlockIndex", "VKBlockTable", "VKField", "VKFieldColumn",
          "VKFieldColumns", "VKFieldTypeName", "VKForm", "VKFieldType", "VKDescriptionPosition",
          "VKCoordinatePosition", "VKPage", "VKPosition", "VKDefinitionType", "VKAliasType", "VKBlockInsert",
          "VKImportedBlock", "VKFormElement", "VKFormPrettyPrinter", "VKMultiField", "VKMultiFieldPosition",
          "VKActorField", "VKActorType")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =      listOf("FormMessages")
}

inPackage("org.kopi.vkopi.comp.chart") {
  grm1voc =         listOf("Chart")
  grm1dep =         listOf("../base/Base")
  scanner1 =        listOf("Chart")
  compilerClasses = listOf("ChartScanner", "VCParseChartContext", "VCParseFieldContext")
  syntaxTreeClasses = listOf("VCChart", "VCChartLocalizationWriter", "VCConstants", "VCDefinitionType",
          "VCField", "VCFieldType", "VCFieldTypeName", "VCDimension", "VCMeasure", "VCMultiMeasure",
          "VCMultiDimension")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =      listOf("ChartMessages")
}

inPackage("org.kopi.vkopi.comp.report") {
  grm1voc =           listOf("Report")
  grm1dep =           listOf("../base/Base")
  scanner1 =          listOf("Report")
  compilerClasses =   listOf("ReportScanner", "VRParseReportContext", "VRParseFieldContext")
  syntaxTreeClasses = listOf("VRConstants", "VRReport", "VRField", "VRFieldType", "VRDefinitionType", "VRFieldTypeName",
          "VRSeparatorField", "VRSeparatorFieldType", "VRMultiField", "VRReportLocalizationWriter")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =      listOf("ReportMessages")
}

inPackage("org.kopi.vkopi.comp.print") {
  grm1voc =           listOf("Print")
  grm1dep =           listOf("../base/Base")
  scanner1 =          listOf("Print")
  compilerClasses =   listOf("PrintScanner")
  syntaxTreeClasses = listOf("PRParagraphStyle", "PRStyle", "PRPosition", "PRStyleRef", "PRTabStop", "PRBlock",
          "PRText", "PRBlockStyle", "PRTextBlock", "PRTextStyle", "PRJavaExpression", "PRProlog", "PRRectangleBlock",
          "PRPage", "PRSourceElement", "PRDefinitionCollector", "PRUtils", "PRInsert", "PRListBlock", "PRTrigger",
          "PRTabRef", "PRRecursiveBlock", "PRConditionalSource", "PRHorizontalBlock", "PRPageNumber", "PRImportedBlock")
  javaFiles =         compilerClasses.orEmpty() + syntaxTreeClasses.orEmpty()
  messageFiles =      listOf("PrintMessages")
}

inPackage("org.kopi.vkopi.comp.main") {
  javaFiles = listOf("Main")
}

inPackage("org.kopi.drivers.ikjc") {
  javaFiles =   listOf("Client", "Constants", "Server")
  genFiles =    listOf("ikjc")
  optionFiles = listOf("ClientOptions", "ServerOptions")
}

inPackage("org.kopi.drivers.kopi") {
  javaFiles = listOf("Main")
}

inPackage("org.kopi") {
  kjcFiles = listOf("README", "Makefile", "Make.Defs", "util", "compiler", "classfile", "optimize", "backend", "dis",
          "ksm", "kjc", "ikjc", "jperf", "lexgen", "kopi", "optgen", "msggen")
}

// Main resources to copied to classroot.
inPackage("resources") {
  nonStandardBuild = true
  resources = listOf(".")
  targetDir = "."
}
