/* PhantomAtom.java
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

import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.StrutBox;

/**
 * 表示应绘制一个不可见元素的元素. (?但是占用空间?)
 *   phantom -- 幽灵，阴影，幻觉，影象 (用幽灵的引申意很合适)
 * 因为内部使用 RowAtom, 所以必须实现 RowAtom 自己实现的 Row, getLeftType() 等各种接口, 函数等.
 * 
 * An atom representing another atom that should be drawn invisibly.
 */
public class PhantomAtom extends Atom implements Row {

   // RowAtom to be drawn invisibly; 要绘制的不可见的 RowAtom 元素
   private RowAtom elements;

   // dimensions to be taken into account; 哪个尺寸被计入考虑的标志.
   private boolean w = true, h = true, d = true;

   public PhantomAtom(Atom el) {
      if (el == null)
         elements = new RowAtom(); // 一定要包装为一个 RowAtom 吗?
      else
         elements = new RowAtom(el);
   }

   /**
    * 构造新实例, 分别可设置哪些尺寸被计入.
    * @param el
    * @param width
    * @param height
    * @param depth
    */
   public PhantomAtom(Atom el, boolean width, boolean height, boolean depth) {
      this(el);
      w = width;
      h = height;
      d = depth;
   }

   public Box createBox(TeXEnvironment env) {
	  // 创建内部元素, 但是用 StrutBox 替代它(使用相同的尺寸)
      Box res = elements.createBox(env);
      return new StrutBox((w ? res.getWidth() : 0), (h ? res.getHeight() : 0),
            (d ? res.getDepth() : 0), res.getShift());
   }

   public int getLeftType() {
      return elements.getLeftType();
   }

   public int getRightType() {
      return elements.getRightType();
   }

   public void setPreviousAtom(Dummy prev) {
      elements.setPreviousAtom(prev);
   }

   public String toString() {
	   return "PhantomAtom{elements = " + elements + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * <PhantomAtom w=bool h=bool d=bool>
    *   <elements> ... </elements>
    * <PhantomAtom>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.appendRaw("<PhantomAtom ").attribute("w", w)
	   		.blank().attribute("h", h)
	   		.blank().attribute("d", d)
	   		.appendRaw(">").ln();
	   
	   super.superToXml(sxw);
	   
	   sxw.beginElement("elements").ln();
	   elements.toXml(sxw, null);
	   sxw.endElement("elements").ln();
	   
	   sxw.endElement("PhantomAtom").ln();
   }
}
