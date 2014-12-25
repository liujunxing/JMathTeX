/* CharFont.java
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
 * 表示在一个特指的字体中的特指的字符(由它的字体 ID 标识). 相当于 pair<char, fontid>
 * 看起来称为 CharFontid 更准确? 或者直接用 (Fontid*65536+Char) 表示也可?
 * Represents a specific character in a specific font (identified by its font ID).
 */
public class CharFont {
   /** 字符, 是在 fontId 这种字体中的位置. */
   public char c;

   /** 字体标识, 索引. 索引到 TeXFont.fontInfo[] 数组.
    * ?? 别处有不这么用的方式吗? 
    */
   public int fontId;

   public CharFont(char ch, int f) {
      c = ch;
      fontId = f;
   }
   
   @Override
   public String toString() {
	  return "CharFont{`" + c + "', " + fontId + "}"; 
   }
}
