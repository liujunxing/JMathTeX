/* OverlinedAtom.java
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
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.OverBar;
import be.ugent.caagt.jmathtex.box.StrutBox;

/**
 * 表示一个 atom, 在另一个 atom 上面有一条水平线.
 * An atom representing another atom with a horizontal line above it
 */
public class OverlinedAtom extends Atom {

   // base atom to be overlined; 上面要添加线的 atom.
   private final Atom base;

   public OverlinedAtom(Atom f) {
      base = f;
      type = TeXConstants.TYPE_ORDINARY; 
   }

   public Box createBox(TeXEnvironment env) {
	   // 得到缺省(水平)线宽度. (表示水平线, 分数线缺省宽度)
      float drt = env.getTeXFont().getDefaultRuleThickness(env.getStyle());

      // 用窄化的 style 创建 base 盒子. 然后创建 垂直盒子.
      // cramp the style of the formula to be overlined and create vertical box
      Box b = (base == null ? new StrutBox(0, 0, 0, 0) : base.createBox(env
            .crampStyle()));
      OverBar ob = new OverBar(b, 3 * drt, drt); // 公式和线之间有 '3倍线宽' 的间距.

      // baseline vertical box = baseline box b; 正确设置最后盒子的基线.
      ob.setDepth(b.getDepth());
      ob.setHeight(b.getHeight() + 5 * drt);

      return ob;
   }

   @Override
   public String toString() {
	   return "OverlinedAtom{base=" + base + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * 输出 <OverlinedAtom>
    *       <base>...</base>
    *     </OverlinedAtom>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.beginElement("OverlinedAtom").ln();
	   
	   super.superToXml(sxw);
	   
	   if (base != null) {
		   sxw.beginElement("base").ln();
		   base.toXml(sxw, null);
		   sxw.endElement("base").ln();
	   }
	   
	   sxw.endElement("OverlinedAtom").ln();
   }
}
