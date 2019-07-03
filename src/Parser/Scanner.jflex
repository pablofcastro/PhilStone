package Parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;
%%
%cup
%line
%column
%class Scanner
%{
	
	//int lvl = 0;
	//int olvl = 0;
	
	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
	
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
IntLiteral = 0 | [1-9][0-9]*

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "// " {InputCharacter}* {LineTerminator}

%public
%eofval{
    return symbol(sym.EOF);
%eofval}

/* %state MINDENT */

%%



<YYINITIAL>	"process" 			{ return symbol(sym.PROCESS, new String(yytext()) ); }
<YYINITIAL>	"pre" 				{ return symbol(sym.PRE, new String(yytext()) ); }
<YYINITIAL>	"post" 				{ return symbol(sym.POST, new String(yytext()) ); } 
<YYINITIAL>	"invariant" 		{ return symbol(sym.INVARIANT, new String(yytext()) ); }
<YYINITIAL>	"init" 				{ return symbol(sym.INIT, new String(yytext()) ); }
<YYINITIAL>	"main" 				{ return symbol(sym.MAIN, new String(yytext()) ); }
<YYINITIAL>	"global" 			{ return symbol(sym.GLOBAL, new String(yytext()) ); }
<YYINITIAL>	"spec" 				{ return symbol(sym.SPEC, new String(yytext()) ); }
<YYINITIAL>	"int" 				{ return symbol(sym.INT, new String(yytext()) ); }
<YYINITIAL>	"boolean" 			{ return symbol(sym.BOOL, new String(yytext()) ); }
<YYINITIAL>	"lock" 				{ return symbol(sym.LOCK, new String(yytext()) ); }
<YYINITIAL> "action"			{ return symbol(sym.ACTION, new String(yytext()) ); } 
<YYINITIAL> "frame"				{ return symbol(sym.FRAME, new String(yytext()) ); }  
<YYINITIAL> "av"				{ return symbol(sym.AV, new String(yytext()) ); }       
<YYINITIAL> "own"				{ return symbol(sym.OWN, new String(yytext()) ); } 
<YYINITIAL> "owns"				{ return symbol(sym.OWNS, new String(yytext()) ); }       
<YYINITIAL> "property"			{ return symbol(sym.PROPERTY, new String(yytext()) ); }  
<YYINITIAL> "LTLproperty"		{ return symbol(sym.LTLPROPERTY, new String(yytext()) ); }  
<YYINITIAL> "run"				{ return symbol(sym.RUN, new String(yytext()) ); }
<YYINITIAL> "prim_boolean"		{ return symbol(sym.PRIMBOOL, new String(yytext()) ); }           
<YYINITIAL> "prim_int"			{ return symbol(sym.PRIMINT, new String(yytext()) ); } 
<YYINITIAL> "enum"				{ return symbol(sym.ENUM, new String(yytext()) ); } 
<YYINITIAL> "primenum"			{ return symbol(sym.PRIMENUM, new String(yytext()) ); } 
<YYINITIAL> "inc"				{ return symbol(sym.INC, new String(yytext()) ); }  
<YYINITIAL> "dec"				{ return symbol(sym.DEC, new String(yytext()) ); }                               



<YYINITIAL> {

	"," 			{ return symbol(sym.COMMA, new String(yytext()) ); }
	":" 			{ return symbol(sym.COLON, new String(yytext()) ); }
	
	
	"A" 				{ return symbol(sym.FORALL, new String(yytext())); }
	"E" 				{ return symbol(sym.EXIST, new String(yytext())); }
	"F" 				{ return symbol(sym.FUTURE, new String(yytext())); }
	"G" 				{ return symbol(sym.GLOBALLY, new String(yytext())); }
	"X" 				{ return symbol(sym.NEXT, new String(yytext())); }
	"U" 				{ return symbol(sym.UNTIL, new String(yytext()) ); }
	"W" 				{ return symbol(sym.WEAKUNTIL, new String(yytext()) ); }
	
	"(" 				{ return symbol(sym.LPARENT, new String(yytext())); }
	")" 				{ return symbol(sym.RPARENT, new String(yytext())); }
	"["   				{ return symbol(sym.LBRACKET, new String(yytext()));}
	"]"   				{ return symbol(sym.RBRACKET, new String(yytext()));}
	
	"{" 				{ return symbol(sym.LBRACE, new String(yytext())); }
	"}" 				{ return symbol(sym.RBRACE, new String(yytext())); }
	
	[a-z][a-zA-Z0-9]* { return symbol(sym.ID, new String(yytext())); }
	/* literals */
	{IntLiteral} 		{ return symbol(sym.INTEGER, new Integer(Integer.parseInt(yytext()))); }	
	"True" 				{ return symbol(sym.TRUE, new String(yytext()) ); }
	"False" 			{ return symbol(sym.FALSE, new String(yytext()) ); }
/*	"->" 				{ return symbol(sym.IMPLIES, new String(yytext())); } */
	"<->" 				{ return symbol(sym.IFF, new String(yytext())); }
	"&&" 				{ return symbol(sym.AND, new String(yytext())); }
	"||" 				{ return symbol(sym.OR, new String(yytext())); }
	"!=" 				{ return symbol(sym.NEQ, new String(yytext())); }
	"=" 				{ return symbol(sym.EQ, new String(yytext())); }
	"!" 				{ return symbol(sym.NEG, new String(yytext()) ); }
	";" 				{ return symbol(sym.SEMICOLON, new String(yytext()) ); }
	"+" 				{ return symbol(sym.PLUS, new String(yytext()) ); }
	"-" 				{ return symbol(sym.MINUS, new String(yytext()) ); }
	"*" 				{ return symbol(sym.ASTERISK, new String(yytext()) ); }
	"/" 				{ return symbol(sym.SLASH, new String(yytext()) ); }
	"." 				{ return symbol(sym.DOT, new String(yytext()) ); }
	
	
	{WhiteSpace}		{ /* ignore white space */ }
 /*	[\r\n] 			{ olvl = lvl; lvl = 0; yybegin(MINDENT); } */
}



{Comment}           { /* ignore */ }
[^] 					{ System.err.println("Illegal character @"+yyline+","+yycolumn+": "+yytext()); }

