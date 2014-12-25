/* FencedAtom.java
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

import be.ugent.caagt.jmathtex.DelimiterFactory;
import be.ugent.caagt.jmathtex.Glue;
import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;

/**
 * 表示一个 base atom 被定界符包围着, 定界符根据 base 的高度自动调整.
 * fence(篱笆,栅栏,保护)
 * 
 * An atom representing a base atom surrounded with delimiters that change their size
 * according to the height of the base.
 */
public class FencedAtom extends Atom {

   // parameters used in the TeX algorithm; (TeX 算法中使用的神奇数字, 我也不知道什么意思)
   private static final int DELIMITER_FACTOR = 901;

   private static final float DELIMITER_SHORTFALL = 0.5f; // shortfall -- 短缺值??

   // base atom
   private final Atom base;

   // delimiters
   private final SymbolAtom left; 
   private final SymbolAtom right;

   /**
    * Creates a new FencedAtom from the given base and delimiters
    * 
    * @param base the base to be surrounded with delimiters
    * @param l the left delimiter
    * @param r the right delimiter
    */
   public FencedAtom(Atom base, SymbolAtom l, SymbolAtom r) {
      if (base == null)
         this.base = new RowAtom(); // empty base
      else
         this.base = base;
      left = l;
      right = r;
   }

   public int getLeftType() {
      return TeXConstants.TYPE_OPENING;
   }

   public int getRightType() {
      return TeXConstants.TYPE_CLOSING;
   }

   /**
    * Centers the given box with resprect to the given axis, by setting an appropriate
    * shift value.
    * 
    * @param box
    *           box to be vertically centered with respect to the axis
    */
   private static void center(Box box, float axis) {
      float h = box.getHeight(), total = h + box.getDepth();
      box.setShift(-(total / 2 - h) - axis);
   }

   public Box createBox(TeXEnvironment env) {
      TeXFont tf = env.getTeXFont();

      Box content = base.createBox(env); // 创建基元件的盒子.
      // 以后研究 getAxisHeight()
      float axis = tf.getAxisHeight(env.getStyle());
      // 计算 delta, 可能是为使内容垂直居中.
      float delta = Math.max(content.getHeight() - axis, content.getDepth() + axis);
      // 估计都是 TeX 的算法. 也可以暂时不用懂??
      float minHeight = Math.max((delta / 500) * DELIMITER_FACTOR, 
    		  2 * delta - DELIMITER_SHORTFALL);

      // construct box; 最终结果是一个水平盒子.
      HorizontalBox hBox = new HorizontalBox();

      // left delimiter; 处理左边的定界符.
      if (left != null) {
    	 // 创建左边定界符的盒子(一般是 CharBox, 或 VerticalBox)
         Box b = DelimiterFactory.create(left.getName(), env, minHeight);
         center(b, axis); // 按照指定水平轴居中(数学公式在水平线上按照垂直轴线对齐)
         hBox.add(b);
      }

      // 在左定界符和内容之间的间距(glue), 如果没有空格的话.
      // glue between left delimiter and content (if not whitespace)
      if (!(base instanceof SpaceAtom)) // 如果 base 不是 SpaceAtom, 则添加 Glue.
         hBox.add(Glue.get(TeXConstants.TYPE_OPENING, base.getLeftType(), env));

      // add content
      hBox.add(content);

      // glue between right delimiter and content (if not whitespace)
      if (!(base instanceof SpaceAtom))
         hBox.add(Glue.get(base.getRightType(), TeXConstants.TYPE_CLOSING, env));

      // right delimiter; 右定界符.
      if (right != null) {
         Box b = DelimiterFactory.create(right.getName(), env, minHeight);
         center(b, axis);
         hBox.add(b);
      }
      return hBox;
   }

   public String toString() {
	   return "FencedAtom{base=" + base + ", left=" + left + ", right=" + right + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * 输出为 xml, 格式:
    *   <FencedAtom>
    *     <base>...</base>
    *     <left>...</left>
    *     </right>...</right>
    *   </FencedAtom>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.beginElement("FencedAtom").ln();
	   
	   super.superToXml(sxw);
	   
	   if (base != null) {
		   sxw.beginElement("base").ln();
		   base.toXml(sxw, null);
		   sxw.endElement("base").ln();
	   }
	   if (left != null) {
		   sxw.beginElement("left").ln();
		   left.toXml(sxw, null);
		   sxw.endElement("left").ln();
	   }
	   if (right != null) {
		   sxw.beginElement("right").ln();
		   right.toXml(sxw, null);
		   sxw.endElement("right").ln();
	   }
	   sxw.endElement("FencedAtom").ln();
   }
}
