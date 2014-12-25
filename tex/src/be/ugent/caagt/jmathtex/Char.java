/* Char.java
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

import java.awt.Font;

/**
 * 表示字符, 以及它的字体, 字体标识, 字符的尺寸信息.
 * 
 * 这个对象, 实际包含了字符的位置信息(c), 物理字体信息(font), 尺寸信息(metrics), 
 *   字体的索引(fontCode, 也即fontId). 可以认为是 model + view 的信息都在这里面.
 * 
 * 那么这个对象叫做 Char 实在是不能清晰地表达这个类的含义. 改叫别的如 CharView??
 * 
 * Represents a character together with its font, font ID and metric information.
 */
public class Char {

   private final char c; // 代表的字符(实际是位置)

   private final Font font; // 系统字体(物理字体)

   private final Metrics m; // 该字符的尺寸信息.

   /** 
    * 为 CharFont.fontId, 表示在 TeX 中字体的索引; 在 DefaultTeXFont.getChar() 函数中给出.
    * 例如取值=0 表示 cmr10, 1=cmmi10, 2=cmsy10, 3=cmex10. 
    * 索引到 DefaultTeXFont.fontInfo[] 数组. 按理应该与 TeXFont 的具体实现无关...可是...?
    */
   private final int fontCode;

   /**
    * 使用指定参数构造 Char 的新实例.
    * @param c - 字符(实际是在 font 中的位置)
    * @param f - 系统字体(物理字体)
    * @param fc - fontId, 字体索引, 索引到 fontInfo[]
    * @param m - metrics, 该字符的尺寸信息.
    */
   public Char(char c, Font f, int fc, Metrics m) {
      font = f;
      fontCode = fc;
      this.c = c;
      this.m = m;
   }

   public CharFont getCharFont() {
      return new CharFont(c, fontCode);
   }

   public char getChar() {
      return c;
   }

   public Font getFont() {
      return font;
   }

   /**
    * 即对应于 CharFont 中的 fontId 值.
    * @return
    */
   public int getFontCode() {
      return fontCode;
   }

   public float getWidth() {
      return m.getWidth();
   }

   public float getItalic() {
      return m.getItalic();
   }

   public float getHeight() {
      return m.getHeight();
   }

   public float getDepth() {
      return m.getDepth();
   }

   public Metrics getMetrics() {
      return m;
   }
}
