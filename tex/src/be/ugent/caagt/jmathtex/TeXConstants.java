/* TeXConstants.java
 * =========================================================================
 * This file is part of the JMathTeX Library - http://jmathtex.sourceforge.net
 * 
 * Copyright (C) 2004-2007 Universiteit Gent
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 * 
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 * 
 */

package be.ugent.caagt.jmathtex;

/**
 * The collection of constants that can be used in the methods of the classes of 
 * this package. 
 */
public class TeXConstants {

   // *******************
   // ALIGNMENT CONSTANTS
   // *******************

   /**
    * Alignment constant: extra space will be added to the right of the formula
    */
   public static final int ALIGN_LEFT = 0;

   /**
    * Alignment constant: extra space will be added to the left of the formula
    */
   public static final int ALIGN_RIGHT = 1;

   /**
    * Alignment constant: the formula will be centered in the middle. This constant 
    * can be used for both horizontal and vertical alignment.
    */
   public static final int ALIGN_CENTER = 2;

   /**
    * Alignment constant: extra space will be added under the formula
    */
   public static final int ALIGN_TOP = 3;

   /**
    * Alignment constant: extra space will be added above the formula
    */
   public static final int ALIGN_BOTTOM = 4;

   // *********************
   // SYMBOL TYPE CONSTANTS
   // *********************

   /**
    * Symbol/Atom type: ordinary(普通的,平常的) symbol, e.g. "slash"
    */
   public static final int TYPE_ORDINARY = 0;

   /**
    * Symbol/Atom type: big operator (= large operator), e.g. "sum"
    */
   public static final int TYPE_BIG_OPERATOR = 1;

   /**
    * Symbol/Atom type: binary operator, e.g. "plus"
    */
   public static final int TYPE_BINARY_OPERATOR = 2;

   /**
    * Symbol/Atom type: relation, e.g. "equals"
    */
   public static final int TYPE_RELATION = 3;

   /**
    * Symbol/Atom type: opening symbol, e.g. "lbrace"
    */
   public static final int TYPE_OPENING = 4;

   /**
    * Symbol/Atom type: closing symbol, e.g. "rbrace"
    */
   public static final int TYPE_CLOSING = 5;

   /**
    * Symbol/Atom type: punctuation symbol, e.g. "comma"
    */
   public static final int TYPE_PUNCTUATION = 6;

   /**
    * Atom type: inner atom (NOT FOR SYMBOLS!!!)
    */
   public static final int TYPE_INNER = 7;
   
   /**
    * Symbol type: accent, e.g. "hat"
    */
   public static final int TYPE_ACCENT = 10;

   // ***************************************
   // OVER AND UNDER DELIMITER TYPE CONSTANTS
   // ***************************************

   /**
    * Delimiter type constant for putting delimiters over and under formula's: brace
    */
   public static final int DELIM_BRACE = 0;

   /**
    * Delimiter type constant for putting delimiters over and under formula's: square bracket
    */
   public static final int DELIM_SQUARE_BRACKET = 1;

   /**
    * Delimiter type constant for putting delimiters over and under formula's: parenthesis
    */
   public static final int DELIM_BRACKET = 2;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * arrow with single line pointing to the left
    */
   public static final int DELIM_LEFT_ARROW = 3;

   /**
    * Delimiter type constant for putting delimiters over and under formula's: 
    * arrow with single line pointing to the right
    */
   public static final int DELIM_RIGHT_ARROW = 4;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * arrow with single line pointing to the left and to the right
    */
   public static final int DELIM_LEFT_RIGHT_ARROW = 5;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * arrow with two lines pointing to the left
    */
   public static final int DELIM_DOUBLE_LEFT_ARROW = 6;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * arrow with two lines pointing to the right
    */
   public static final int DELIM_DOUBLE_RIGHT_ARROW = 7;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * arrow with two lines pointing to the left and to the right
    */
   public static final int DELIM_DOUBLE_LEFT_RIGHT_ARROW = 8;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * underline once
    */
   public static final int DELIM_SINGLE_LINE = 9;

   /**
    * Delimiter type constant for putting delimiters over and under formula's:
    * underline twice
    */
   public static final int DELIM_DOUBLE_LINE = 10;

   /*
    * 当处理公式时, TeX 实际上有八种不同的字体, 即:
    *   0=STYLE_DISPLAY        列表字体 (用在行中单独的列表公式中)   display style
    *   2=STYLE_TEXT           文本字体 (用在嵌入文本的公式中)      text style
    *   4=STYLE_SCRIPT         标号字体 (用于公式的上下标)         script style
    *   6=STYLE_SCRIPT_SCRIPT  小标号字体                             scriptscript style
    *   
    * 以及四种其它的 `近似'(cramped) 字体, 它们与上面四种几乎一样, 只是指数升高得不那么多. 简化表示为:
    * (推测, 这里的 1=STYLE_DISPLAY+1, 3, 5, 7 表示这四种 `cramped' 字体)
    *   D, D', T, T', S, S', SS, SS'
    *   
    * TeX 还使用数学字体的三种不同大小, 分别叫做文本尺寸(text size), 标号尺寸(script size), 
    *   小标号尺寸(scriptscript size).
    * 
    * TeX 排版时公式放在 $...$ 之间, 这样得到的是文本字体(字体 T). 或者放在 $$...$$ 之间, 
    *   这样得到的是列表字体(字体 D). 公式的子公式可能使用不同的字体. 
    * 一旦知道了字体, 确定 TeX 要用的字体的大小列表如下:
    *      D, D', T, T'        文本尺寸
    *      S, S'               标号尺寸
    *      SS, SS'             小标号尺寸
    * 
    *    公式的字体      上标的字体       下标的字体
    *      D, T         S             S'
    *      D', T'       S'            S'
    *      S, SS        SS            SS'
    *      S', SS'      SS'           SS'
    * 
    * 当处理分数时, 字体D 和字体T 之间有明显的差别: (text style 下分数尺寸要小一些)
    *    公式的字体      分子的字体      分母的字体
    *      D             T            T'
    *      D'            T'           T'
    *      T             S            S'
    *      T'            S'           S'
    *      S, SS         SS           SS'
    *      S', SS'       SS'          SS'
    * 
    */
   
   // *******************
   // TEX STYLE CONSTANTS
   // *******************

   /**
    * TeX样式(=0): 显示样式 ($$ formula $$). 大操作符(如 sum, int -- 累加,积分)
    *   使用大的版本, 其 limit (缺省)放在符号的上面和下面. 符号以最大尺寸绘制.
    * 
    * 现在这些值的取值, +1 还是该类型, 只是有某些变化? 这些值除以 2 可以当做 glue[] 的数组索引.
    * 
    * TeX style: display style.
    * <p>
    * The large versions of big operators are used and limits are placed under and over 
    * these operators (default). Symbols are rendered in the largest size.
    */
   public static final int STYLE_DISPLAY = 0;

   /**
    * TeX 样式(=2): 文本样式($ formula $). 大操作符以较小方式显示, limit (缺省)放在上下标的位置.
    * ... same size...
    * TeX style: text style.
    * <p>
    * The small versions of big operators are used and limits are attached to 
    * these operators as scripts (default). The same size as in the display style
    * is used to render symbols.
    */
   public static final int STYLE_TEXT = 2;

   /**
    * 上下标(=4). 字符(符号)以较小尺寸绘制.
    * TeX style: script style.
    * <p>
    * The same as the text style, but symbols are rendered in a smaller size.
    */
   public static final int STYLE_SCRIPT = 4;

   /**
    * 小上下标(=6)(下标的下标). 
    * TeX style: script_script style.
    * <p>
    * The same as the script style, but symbols are rendered in a smaller size.
    */
   public static final int STYLE_SCRIPT_SCRIPT = 6;

   // **************
   // UNIT CONSTANTS
   // **************

   /**
    * Unit constant: em
    * <p>
    * 1 em = the width of the capital 'M' in the current font
    */
   public static final int UNIT_EM = 0;

   /**
    * Unit constant: ex
    * <p>
    * 1 ex = the height of the character 'x' in the current font
    */
   public static final int UNIT_EX = 1;

   /**
    * Unit constant: pixel
    */
   public static final int UNIT_PIXEL = 2;

   /**
    * Unit constant: point
    */
   public static final int UNIT_POINT = 3;

   /**
    * Unit constant: pica
    * <p>
    * 1 pica = 12 point
    */
   public static final int UNIT_PICA = 4;
   
   /**
    * Unit constant: math unit (mu)
    * <p>
    * 1 mu = 1/18 em (em taken from the "mufont")
    */
   public static final int UNIT_MU = 5;   
}