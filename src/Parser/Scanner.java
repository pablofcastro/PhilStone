/* The following code was generated by JFlex 1.4.1 on 16/10/17 10:18 */

package Parser;

import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 16/10/17 10:18 from the specification file
 * <tt>Scanner.jflex</tt>
 */
public class Scanner implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = {
     0,  0,  0,  0,  0,  0,  0,  0,  0,  3,  2,  0,  3,  1,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
     9, 51,  0,  0,  0,  0, 49,  0, 38, 39,  8, 54, 29, 47, 55,  7, 
     4,  5,  5,  5,  5,  5,  5,  5,  5,  5, 30, 53, 46, 52, 48,  0, 
     0, 31, 44, 44, 44, 32, 33, 34, 44, 44, 44, 44, 44, 44, 44, 44, 
    44, 44, 44, 44, 45, 36, 44, 37, 35, 44, 44, 40,  0, 41,  0,  0, 
     0, 20, 24, 13, 43, 14, 25, 22, 43, 17, 43, 43, 23, 21, 18, 12, 
    10, 43, 11, 15, 16, 28, 19, 26, 43, 27, 43, 42, 50,  6,  0,  0
  };

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\2\2\2\3\1\4\1\5\1\6\13\7"+
    "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17"+
    "\1\20\1\21\1\22\1\23\1\24\1\25\2\1\1\26"+
    "\2\1\1\27\1\30\1\31\1\32\1\33\2\0\7\7"+
    "\1\34\4\7\3\0\1\35\1\36\1\37\3\0\1\7"+
    "\1\40\1\7\1\41\1\42\1\7\1\43\7\7\2\0"+
    "\1\44\3\0\1\45\2\7\1\46\1\47\1\50\2\7"+
    "\1\51\3\7\1\0\1\52\6\7\1\53\1\54\3\7"+
    "\1\55\1\56\2\7\1\57\1\7\1\60\1\61\1\7"+
    "\1\62";

  private static int [] zzUnpackAction() {
    int [] result = new int[122];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\70\0\160\0\70\0\70\0\250\0\70\0\340"+
    "\0\70\0\u0118\0\u0150\0\u0188\0\u01c0\0\u01f8\0\u0230\0\u0268"+
    "\0\u02a0\0\u02d8\0\u0310\0\u0348\0\70\0\70\0\70\0\70"+
    "\0\u0380\0\70\0\70\0\70\0\70\0\70\0\70\0\70"+
    "\0\70\0\70\0\u03b8\0\u03f0\0\70\0\u0428\0\u0460\0\u0498"+
    "\0\70\0\70\0\70\0\70\0\u04d0\0\u0508\0\u0540\0\u0578"+
    "\0\u05b0\0\u05e8\0\u0620\0\u0658\0\u0690\0\u01c0\0\u06c8\0\u0700"+
    "\0\u0738\0\u0770\0\u07a8\0\u07e0\0\u0818\0\70\0\70\0\70"+
    "\0\u0850\0\u0888\0\u08c0\0\u08f8\0\u01c0\0\u0930\0\u01c0\0\u01c0"+
    "\0\u0968\0\u01c0\0\u09a0\0\u09d8\0\u0a10\0\u0a48\0\u0a80\0\u0ab8"+
    "\0\u0af0\0\u0b28\0\u0b60\0\70\0\u0b98\0\u0bd0\0\u0c08\0\70"+
    "\0\u0c40\0\u0c78\0\u01c0\0\u01c0\0\u01c0\0\u0cb0\0\u0ce8\0\u01c0"+
    "\0\u0d20\0\u0d58\0\u0d90\0\u0dc8\0\70\0\u0e00\0\u0e38\0\u0e70"+
    "\0\u0ea8\0\u0ee0\0\u0f18\0\u01c0\0\70\0\u0f50\0\u0f88\0\u0fc0"+
    "\0\u01c0\0\u01c0\0\u0ff8\0\u1030\0\u01c0\0\u1068\0\u01c0\0\u01c0"+
    "\0\u10a0\0\u01c0";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[122];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\2\4\1\5\1\6\1\7\1\10\1\11"+
    "\1\4\1\12\1\13\1\14\2\15\1\16\1\15\1\17"+
    "\2\15\1\20\1\21\1\22\1\15\1\23\1\24\3\15"+
    "\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\15\1\2"+
    "\1\43\1\44\1\45\1\2\1\46\1\47\1\50\1\51"+
    "\1\52\1\53\1\54\72\0\1\4\71\0\2\6\71\0"+
    "\1\55\1\56\63\0\2\15\4\0\1\15\1\57\1\60"+
    "\20\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\22\15\1\61\2\0\7\15\5\0\3\15\16\0\2\15"+
    "\4\0\20\15\1\62\2\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\23\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\1\63\22\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\10\15\1\64\12\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\3\15\1\65"+
    "\5\15\1\66\11\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\12\15\1\67\10\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\15\15\1\70\5\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\2\15\1\71"+
    "\20\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\1\15\1\72\21\15\2\0\7\15\5\0\3\15\36\0"+
    "\1\73\56\0\1\74\133\0\1\75\71\0\1\76\70\0"+
    "\1\77\71\0\1\100\14\0\1\101\56\0\10\102\1\103"+
    "\57\102\4\0\2\15\4\0\2\15\1\104\1\15\1\105"+
    "\16\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\5\15\1\106\15\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\10\15\1\107\12\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\10\15\1\110\12\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\4\15\1\111"+
    "\16\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\6\15\1\112\1\113\1\15\1\114\11\15\2\0\7\15"+
    "\5\0\3\15\16\0\2\15\4\0\6\15\1\115\14\15"+
    "\2\0\7\15\5\0\3\15\16\0\2\15\4\0\7\15"+
    "\1\116\13\15\2\0\7\15\5\0\3\15\16\0\2\15"+
    "\4\0\2\15\1\117\20\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\2\15\1\120\20\15\2\0\7\15"+
    "\5\0\3\15\16\0\2\15\4\0\12\15\1\121\10\15"+
    "\2\0\7\15\5\0\3\15\41\0\1\122\74\0\1\123"+
    "\113\0\1\124\7\0\1\101\1\125\1\126\65\101\10\102"+
    "\1\127\57\102\7\0\1\130\1\103\63\0\2\15\4\0"+
    "\1\131\2\15\1\132\17\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\6\15\1\133\14\15\2\0\7\15"+
    "\5\0\3\15\16\0\2\15\4\0\3\15\1\134\17\15"+
    "\2\0\7\15\5\0\3\15\16\0\2\15\4\0\6\15"+
    "\1\135\14\15\2\0\7\15\5\0\3\15\16\0\2\15"+
    "\4\0\12\15\1\136\10\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\7\15\1\137\13\15\2\0\7\15"+
    "\5\0\3\15\16\0\2\15\4\0\10\15\1\140\12\15"+
    "\2\0\7\15\5\0\3\15\16\0\2\15\4\0\16\15"+
    "\1\141\4\15\2\0\7\15\5\0\3\15\16\0\2\15"+
    "\4\0\15\15\1\142\5\15\2\0\7\15\5\0\3\15"+
    "\16\0\2\15\4\0\13\15\1\143\7\15\2\0\7\15"+
    "\5\0\3\15\31\0\1\144\66\0\1\145\53\0\1\126"+
    "\3\0\1\130\67\0\1\130\61\0\7\102\1\130\1\127"+
    "\57\102\4\0\2\15\4\0\4\15\1\146\16\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\4\15\1\147"+
    "\16\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\1\15\1\150\21\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\2\15\1\151\20\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\12\15\1\152\10\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\4\15\1\153"+
    "\16\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\4\15\1\154\16\15\2\0\7\15\5\0\3\15\30\0"+
    "\1\155\55\0\2\15\4\0\1\15\1\156\21\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\5\15\1\157"+
    "\15\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\7\15\1\160\13\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\10\15\1\161\12\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\15\15\1\162\5\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\12\15\1\163"+
    "\10\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\6\15\1\164\14\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\5\15\1\165\15\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\12\15\1\166\10\15\2\0"+
    "\7\15\5\0\3\15\16\0\2\15\4\0\10\15\1\167"+
    "\12\15\2\0\7\15\5\0\3\15\16\0\2\15\4\0"+
    "\21\15\1\170\1\15\2\0\7\15\5\0\3\15\16\0"+
    "\2\15\4\0\10\15\1\171\12\15\2\0\7\15\5\0"+
    "\3\15\16\0\2\15\4\0\6\15\1\172\14\15\2\0"+
    "\7\15\5\0\3\15\12\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4312];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\1\1\2\11\1\1\1\11\1\1\1\11"+
    "\13\1\4\11\1\1\11\11\2\1\1\11\3\1\4\11"+
    "\2\0\14\1\3\0\3\11\3\0\16\1\2\0\1\11"+
    "\3\0\1\11\13\1\1\0\1\11\7\1\1\11\15\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[122];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
	
	//int lvl = 0;
	//int olvl = 0;
	
	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
	


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Scanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Scanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzPushbackPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead < 0) {
      return true;
    }
    else {
      zzEndRead+= numRead;
      return false;
    }
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 37: 
          { /* ignore */
          }
        case 51: break;
        case 22: 
          { return symbol(sym.MINUS, new String(yytext()) );
          }
        case 52: break;
        case 29: 
          { return symbol(sym.AND, new String(yytext()));
          }
        case 53: break;
        case 49: 
          { return symbol(sym.PROPERTY, new String(yytext()) );
          }
        case 54: break;
        case 30: 
          { return symbol(sym.OR, new String(yytext()));
          }
        case 55: break;
        case 6: 
          { return symbol(sym.ASTERISK, new String(yytext()) );
          }
        case 56: break;
        case 11: 
          { return symbol(sym.EXIST, new String(yytext()));
          }
        case 57: break;
        case 7: 
          { return symbol(sym.ID, new String(yytext()));
          }
        case 58: break;
        case 41: 
          { return symbol(sym.MAIN, new String(yytext()) );
          }
        case 59: break;
        case 33: 
          { return symbol(sym.RUN, new String(yytext()) );
          }
        case 60: break;
        case 42: 
          { return symbol(sym.TRUE, new String(yytext()) );
          }
        case 61: break;
        case 20: 
          { return symbol(sym.RBRACKET, new String(yytext()));
          }
        case 62: break;
        case 39: 
          { return symbol(sym.SPEC, new String(yytext()) );
          }
        case 63: break;
        case 1: 
          { System.err.println("Illegal character @"+yyline+","+yycolumn+": "+yytext());
          }
        case 64: break;
        case 35: 
          { return symbol(sym.INT, new String(yytext()) );
          }
        case 65: break;
        case 43: 
          { return symbol(sym.FRAME, new String(yytext()) );
          }
        case 66: break;
        case 17: 
          { return symbol(sym.LPARENT, new String(yytext()));
          }
        case 67: break;
        case 45: 
          { return symbol(sym.ACTION, new String(yytext()) );
          }
        case 68: break;
        case 50: 
          { return symbol(sym.INVARIANT, new String(yytext()) );
          }
        case 69: break;
        case 14: 
          { return symbol(sym.NEXT, new String(yytext()));
          }
        case 70: break;
        case 25: 
          { return symbol(sym.SEMICOLON, new String(yytext()) );
          }
        case 71: break;
        case 31: 
          { return symbol(sym.NEQ, new String(yytext()));
          }
        case 72: break;
        case 32: 
          { return symbol(sym.PRE, new String(yytext()) );
          }
        case 73: break;
        case 44: 
          { return symbol(sym.FALSE, new String(yytext()) );
          }
        case 74: break;
        case 24: 
          { return symbol(sym.EQ, new String(yytext()));
          }
        case 75: break;
        case 47: 
          { return symbol(sym.PROCESS, new String(yytext()) );
          }
        case 76: break;
        case 8: 
          { return symbol(sym.COMMA, new String(yytext()) );
          }
        case 77: break;
        case 3: 
          { return symbol(sym.INTEGER, new Integer(Integer.parseInt(yytext())));
          }
        case 78: break;
        case 19: 
          { return symbol(sym.LBRACKET, new String(yytext()));
          }
        case 79: break;
        case 13: 
          { return symbol(sym.GLOBALLY, new String(yytext()));
          }
        case 80: break;
        case 12: 
          { return symbol(sym.FUTURE, new String(yytext()));
          }
        case 81: break;
        case 26: 
          { return symbol(sym.PLUS, new String(yytext()) );
          }
        case 82: break;
        case 34: 
          { return symbol(sym.OWN, new String(yytext()) );
          }
        case 83: break;
        case 23: 
          { return symbol(sym.NEG, new String(yytext()) );
          }
        case 84: break;
        case 21: 
          { return symbol(sym.LBRACE, new String(yytext()));
          }
        case 85: break;
        case 15: 
          { return symbol(sym.UNTIL, new String(yytext()) );
          }
        case 86: break;
        case 28: 
          { return symbol(sym.AV, new String(yytext()) );
          }
        case 87: break;
        case 40: 
          { return symbol(sym.INIT, new String(yytext()) );
          }
        case 88: break;
        case 18: 
          { return symbol(sym.RPARENT, new String(yytext()));
          }
        case 89: break;
        case 16: 
          { return symbol(sym.WEAKUNTIL, new String(yytext()) );
          }
        case 90: break;
        case 27: 
          { return symbol(sym.DOT, new String(yytext()) );
          }
        case 91: break;
        case 38: 
          { return symbol(sym.POST, new String(yytext()) );
          }
        case 92: break;
        case 4: 
          { return symbol(sym.RBRACE, new String(yytext()));
          }
        case 93: break;
        case 9: 
          { return symbol(sym.COLON, new String(yytext()) );
          }
        case 94: break;
        case 48: 
          { return symbol(sym.BOOL, new String(yytext()) );
          }
        case 95: break;
        case 46: 
          { return symbol(sym.GLOBAL, new String(yytext()) );
          }
        case 96: break;
        case 36: 
          { return symbol(sym.IFF, new String(yytext()));
          }
        case 97: break;
        case 10: 
          { return symbol(sym.FORALL, new String(yytext()));
          }
        case 98: break;
        case 5: 
          { return symbol(sym.SLASH, new String(yytext()) );
          }
        case 99: break;
        case 2: 
          { /* ignore white space */
          }
        case 100: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              {     return symbol(sym.EOF);
 }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
