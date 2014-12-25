/* ColorAtom.java
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

import java.awt.Color;

import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.box.Box;

/**
 * 表示其它 atom 的前景/背景色的 元件(atom).
 * An atom representing the foreground and background color of an other atom. 
 */
public class ColorAtom extends Atom implements Row {

   // background color; 背景色.
   private final Color background;

   // foreground color; 前景色.
   private final Color color;

   // RowAtom for which the colorsettings apply; 此颜色设置应用给的 RowAtom.
   private final RowAtom elements;

   /**
    * 构造一个新的 ColorAtom, 使用指定的背景色,前景色, 应用给指定的 atom. 
    *   如果颜色为 null 表示没有特定的颜色指定给该 atom.
    * Creates a new ColorAtom that sets the given colors for the given atom.
    * Null for a color means: no specific color set for this atom.
    * 
    * @param atom the atom for which the given colors have to be set 
    * @param bg the background color
    * @param c the foreground color
    */
   public ColorAtom(Atom atom, Color bg, Color c) {
      elements = new RowAtom(atom); // 这里为什么一定要用 RowAtom 包装 atom 呢?
      background = bg;
      color = c;
   }

   /**
    * Creates a ColorAtom that overrides the colors of the given ColorAtom if the given
    * colors are not null. If they're null, the old values are used.
    * 
    * @param bg the background color
    * @param c the foreground color
    * @param old the ColorAtom for which the colorsettings should be overriden with the
    * 			given colors.
    */
   public ColorAtom(Color bg, Color c, ColorAtom old) {
      elements = new RowAtom(old.elements);
      background = (bg == null ? old.background : bg);
      color = (c == null ? old.color : c);
   }

   public Box createBox(TeXEnvironment env) {
      TeXEnvironment copy = env.copy();
      if (background != null)
         copy.setBackground(background);
      if (color != null)
         copy.setColor(color);
      return elements.createBox(copy);
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

   @Override 
   public String toString() {
	   return "ColorAtom{bg=" + background + ", c=" + color + ", e[]=" + elements.toString() + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
	   if (elements != null) elements.dump();
   }

   /**
    * 输出 XML:
    *   <ColorAtom background="..." color="...">
    *     <elements>...RowAtom...</elements>
    *   </ColorAtom>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   throw new java.lang.UnsupportedOperationException();
   }
}
