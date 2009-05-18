/* The following code was generated by JFlex 1.4.1 on 22/02/08 07:43 */

package com.stratelia.webactiv.util.indexEngine.analysis;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Token;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 22/02/08 07:43 from the specification file
 * <tt>SilverTokenizerImpl.jflex</tt>
 */
class SilverTokenizerImpl {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\0\1\16\1\0\1\0\1\15\22\0\1\0\5\0\1\3"+
    "\1\1\4\0\1\7\1\5\1\2\1\7\12\11\6\0\1\4\32\10"+
    "\4\0\1\6\1\0\32\10\105\0\27\10\1\0\37\10\1\0\u0568\10"+
    "\12\12\206\10\12\12\u026c\10\12\12\166\10\12\12\166\10\12\12\166\10"+
    "\12\12\166\10\12\12\167\10\11\12\166\10\12\12\166\10\12\12\166\10"+
    "\12\12\340\10\12\12\166\10\12\12\u0166\10\12\12\266\10\u0100\10\u0e00\10"+
    "\u1040\0\u0150\14\140\0\20\14\u0100\0\200\14\200\0\u19c0\14\100\0\u5200\14"+
    "\u0c00\0\u2bb0\13\u2150\0\u0200\14\u0465\0\73\14\75\10\43\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\4\2\1\3\1\1\6\0\2\2\5\0"+
    "\1\4\4\5\2\6\2\0\1\7\3\5\3\7\3\5"+
    "\1\10\1\11\1\0\1\10\1\11\1\0\2\11\2\10"+
    "\2\5\1\12";

  private static int [] zzUnpackAction() {
    int [] result = new int[53];
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
    "\0\0\0\17\0\36\0\55\0\74\0\113\0\17\0\132"+
    "\0\151\0\170\0\207\0\226\0\245\0\264\0\303\0\322"+
    "\0\341\0\360\0\377\0\u010e\0\u011d\0\u012c\0\u013b\0\u014a"+
    "\0\u0159\0\u0168\0\207\0\u0177\0\u0186\0\u0195\0\u01a4\0\u01b3"+
    "\0\u01c2\0\u01d1\0\u01e0\0\u01ef\0\u01fe\0\u020d\0\u021c\0\u022b"+
    "\0\u023a\0\u0249\0\u0258\0\u0267\0\u0276\0\u0285\0\u011d\0\341"+
    "\0\170\0\u010e\0\u0294\0\u02a3\0\u02b2";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[53];
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
    "\10\2\1\3\1\4\1\5\1\6\1\7\1\10\1\2"+
    "\20\0\1\11\1\12\1\13\1\14\2\15\1\16\1\17"+
    "\1\4\1\20\1\6\5\0\1\21\1\0\1\22\2\15"+
    "\1\23\3\4\1\6\4\0\1\11\1\24\1\13\1\14"+
    "\2\15\1\23\1\20\1\4\1\20\1\6\5\0\1\25"+
    "\1\0\1\22\2\15\1\16\4\6\21\0\1\2\10\0"+
    "\1\26\1\0\1\26\14\0\1\27\1\30\1\31\1\32"+
    "\13\0\1\33\1\0\1\33\14\0\1\34\1\35\1\34"+
    "\1\35\13\0\4\36\13\0\1\16\2\37\5\0\1\11"+
    "\1\25\1\13\1\14\2\15\1\16\1\17\1\4\1\20"+
    "\1\6\4\0\1\11\1\21\1\13\1\14\2\15\1\23"+
    "\1\20\1\4\1\20\1\6\13\0\1\40\2\41\1\42"+
    "\13\0\4\35\13\0\1\43\2\44\1\45\13\0\1\46"+
    "\1\41\1\47\1\42\13\0\1\50\2\30\1\32\4\0"+
    "\1\11\6\0\1\26\1\0\1\26\6\0\1\51\1\0"+
    "\1\22\2\15\1\0\1\50\2\30\1\32\5\0\1\52"+
    "\1\0\1\22\2\15\1\53\3\30\1\32\5\0\1\54"+
    "\1\0\1\22\2\15\1\53\3\30\1\32\5\0\1\55"+
    "\1\0\1\22\2\15\1\0\4\32\5\0\1\56\2\0"+
    "\1\56\2\0\1\34\1\35\1\34\1\35\5\0\1\56"+
    "\2\0\1\56\2\0\4\35\5\0\1\15\1\0\1\22"+
    "\2\15\1\0\4\36\5\0\1\53\4\0\1\53\3\37"+
    "\6\0\1\57\1\0\1\22\2\15\1\16\1\40\2\41"+
    "\1\42\5\0\1\60\1\0\1\22\2\15\1\23\3\41"+
    "\1\42\5\0\1\57\1\0\1\22\2\15\1\16\4\42"+
    "\5\0\1\16\4\0\1\16\1\43\2\44\1\45\5\0"+
    "\1\23\4\0\1\23\3\44\1\45\5\0\1\16\4\0"+
    "\1\16\4\45\5\0\1\61\1\0\1\22\2\15\1\16"+
    "\1\40\2\41\1\42\5\0\1\62\1\0\1\22\2\15"+
    "\1\23\3\41\1\42\5\0\1\55\1\0\1\22\2\15"+
    "\1\0\1\50\2\30\1\32\13\0\1\63\1\32\1\63"+
    "\1\32\13\0\4\42\13\0\4\45\13\0\1\64\1\42"+
    "\1\64\1\42\13\0\4\32\13\0\4\65\5\0\1\51"+
    "\1\0\1\22\2\15\1\0\4\32\5\0\1\61\1\0"+
    "\1\22\2\15\1\16\4\42\5\0\1\56\2\0\1\56"+
    "\2\0\4\65\3\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[705];
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
    "\1\0\1\11\4\1\1\11\1\1\6\0\2\1\5\0"+
    "\7\1\2\0\14\1\1\0\2\1\1\0\7\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[53];
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

  /* user code: */

public static final int ALPHANUM          = 0;
public static final int APOSTROPHE        = 1;
public static final int ACRONYM           = 2;
public static final int COMPANY           = 3;
public static final int EMAIL             = 4;
public static final int HOST              = 5;
public static final int NUM               = 6;
public static final int CJ                = 7;
/**
 * @deprecated this solves a bug where HOSTs that end with '.' are identified
 *             as ACRONYMs. It is deprecated and will be removed in the next
 *             release.
 */
public static final int ACRONYM_DEP       = 8;

public static final String [] TOKEN_TYPES = new String [] {
    "<ALPHANUM>",
    "<APOSTROPHE>",
    "<ACRONYM>",
    "<COMPANY>",
    "<EMAIL>",
    "<HOST>",
    "<NUM>",
    "<CJ>",
    "<ACRONYM_DEP>"
};

public final int yychar()
{
    return yychar;
}

/**
 * Fills Lucene token with the current token text.
 */
final void getText(Token t) {
  t.setTermBuffer(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
}


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  SilverTokenizerImpl(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  SilverTokenizerImpl(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 156) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
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
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int getNextToken() throws java.io.IOException {
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

      yychar+= zzMarkedPosL-zzStartRead;

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
        case 5: 
          { return HOST;
          }
        case 11: break;
        case 9: 
          { return ACRONYM_DEP;
          }
        case 12: break;
        case 8: 
          { return ACRONYM;
          }
        case 13: break;
        case 1: 
          { /* ignore */
          }
        case 14: break;
        case 7: 
          { return NUM;
          }
        case 15: break;
        case 3: 
          { return CJ;
          }
        case 16: break;
        case 2: 
          { return ALPHANUM;
          }
        case 17: break;
        case 6: 
          { return COMPANY;
          }
        case 18: break;
        case 4: 
          { return APOSTROPHE;
          }
        case 19: break;
        case 10: 
          { return EMAIL;
          }
        case 20: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return YYEOF;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
