/* CharAtom.java
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

package be.ugent.caagt.jmathtex.atom;

import be.ugent.caagt.jmathtex.Char;
import be.ugent.caagt.jmathtex.CharFont;
import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.CharBox;

/**
 * 表示一个(且一个)字母或数字字符, 以及绘制它应使用的文本样式(字体?)
 * An atom representing exactly one alphanumeric character and the text style in which 
 * it should be drawn. 
 */
public class CharAtom extends CharSymbol {

   // alphanumeric character(此实例的文字/数字的字符)
   private final char c;

   // text style (null means the default text style) (文本样式), ??取值什么呢?
   private final String textStyle;

   /**
    * 使用指定字符, 字体样式 构造一个 CharAtom 新实例. 如果 textStyle 为 null 则表示使用缺省字体样式.
    * Creates a CharAtom that will represent the given character in the given text style.
    * Null for the text style means the default text style.
    * 
    * @param c the alphanumeric character
    * @param textStyle the text style in which the character should be drawn
    */
   public CharAtom(char c, String textStyle) {
      this.c = c;
      this.textStyle = textStyle;
   }

   public Box createBox(TeXEnvironment env) {
	   TeXFont tf = env.getTeXFont();
	   Char ch = getChar(tf, env.getStyle());
	   return new CharBox(ch);
   }

   /*
    * Get the Char-object representing this character ("c") in the right text style
    */
   private Char getChar(TeXFont tf, int style) {
      if (textStyle == null) // default text style
         return tf.getDefaultChar(c, style);
      else
         return tf.getChar3(c, textStyle, style);
   }

   // 实现 CharSymbol.getCharFont() 接口.
   public CharFont getCharFont(TeXFont tf) {
      // style doesn't matter here 
      return getChar(tf, TeXConstants.STYLE_DISPLAY).getCharFont();
   }

   @Override
   public String toString() {
	   return "CharAtom{c='" + c + "(" + (int)c + ")', textString=" + textStyle + "}";
   }
   
   public void dump() {
	   System.out.println(this.toString());
   }

   /**
    * 以 XML 格式输出. 如下:
    *   <CharAtom ccode='97' c='a' textStyle="..." />
    * 其中 c='a' 是可选的, 只有 32-126 之间的字符显示出来.
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.appendRaw("<CharAtom").blank()
	   		.attribute("ccode", (int)c).blank()
	   		.attribute("textStyle", textStyle);
	   if (isPrintable(c)) {
		   sxw.blank().attribute("c", Character.toString(c));
	   }
	   sxw.appendRaw(">").ln();
	   
	   super.superToXml(sxw);
	   
	   sxw.endElement("CharAtom").ln();
   }
   
   public static boolean isPrintable(char c) {
	   if (c > 32 && c < 127) return true;
	   return false;
   }
}
