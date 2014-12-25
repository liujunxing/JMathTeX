/* OverUnderDelimiter.java
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
import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.OverUnderBox;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;
import be.ugent.caagt.jmathtex.ex.InvalidUnitException;

/**
 * 表示另一个 atom 其有定界符和上下标在上面或下面, 其上下标和定界符通过间距隔开.
 * A box representing another atom with a delimiter and a script above or under it, 
 * with script and delimiter seperated by a kern.
 */
public class OverUnderDelimiter extends Atom {

   // base and script atom
   private final Atom base;
   private final Atom script;

   // delimiter symbol
   private final SymbolAtom symbol;

   // kern between delimiter and script
   private final SpaceAtom kern;

   // whether the delimiter should be positioned above or under the base
   private final boolean over;

   public OverUnderDelimiter(Atom base, Atom script, SymbolAtom s, int kernUnit,
         float kern, boolean over) throws InvalidUnitException {
      type = TeXConstants.TYPE_INNER;
      this.base = base; // 基本元件
      this.script = script; // 上标
      symbol = s; // 定界符
      this.kern = new SpaceAtom(kernUnit, 0, kern, 0); // 间距
      this.over = over;
   }

   /*
    * 
    */
   public Box createBox(TeXEnvironment env) {
	  // 构造 base 的盒子
      Box b = (base == null ? new StrutBox(0, 0, 0, 0) : base.createBox(env));
      // 构造 symbol(定界符) 的盒子.
      Box del = DelimiterFactory.create(symbol.getName(), env, b.getWidth());

      Box scriptBox = null; // 上标盒子. 根据 over 判断是上标, or 下标.
      if (script != null)
         scriptBox = script.createBox((over ? env.supStyle() : env.subStyle()));

      // 创建居中的水平盒子, 如果小于最大宽度.
      // create centered horizontal box if smaller dan maximum width
      float max = getMaxWidth(b, del, scriptBox);
      if (Math.abs(max - b.getWidth()) > TeXFormula.PREC)
         b = new HorizontalBox(b, max, TeXConstants.ALIGN_CENTER);
      if (Math.abs(max - del.getHeight() - del.getDepth()) > TeXFormula.PREC)
         del = new VerticalBox(del, max, TeXConstants.ALIGN_CENTER);
      if (scriptBox != null
            && Math.abs(max - scriptBox.getWidth()) > TeXFormula.PREC)
         scriptBox = new HorizontalBox(scriptBox, max,
               TeXConstants.ALIGN_CENTER);

      // 创建 OverUnderBox(上面下面排列的盒子)
      return new OverUnderBox(b, del, scriptBox, kern.createBox(env)
            .getHeight(), over);
   }

   private static float getMaxWidth(Box b, Box del, Box script) {
      float max = Math.max(b.getWidth(), del.getHeight() + del.getDepth());
      if (script != null)
         max = Math.max(max, script.getWidth());
      return max;
   }

   @Override
   public String toString() {
	   return "OverUnderDelimiter{base=" + base + ", script=" + script + ", symbol=" + symbol +
	   		", kern=" + kern + ", over=" + over + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * 输出: <OverUnderDelimieter over=bool>
    *        <base> ... </base>
    *        <script> ... </script>
    *        <symbol> ... </symbol>
    *        <kern> ... </kern>
    *      </OverUnderDelimieter>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.appendRaw("<OverUnderDelimiter ").attribute("over", over)
	   	  .appendRaw(">").ln();
	   
	   super.superToXml(sxw);
	   
	   if (base != null) {
		   sxw.beginElement("base");
		   base.toXml(sxw, null);
		   sxw.endElement("base").ln();
	   }
	   if (script != null) {
		   sxw.beginElement("script");
		   script.toXml(sxw, null);
		   sxw.endElement("script").ln();
	   }
	   if (symbol != null) {
		   sxw.beginElement("symbol");
		   symbol.toXml(sxw, null);
		   sxw.endElement("symbol").ln();
	   }
	   if (kern != null) {
		   sxw.beginElement("kern");
		   kern.toXml(sxw, null);
		   sxw.endElement("kern").ln();
	   }
	   
	   sxw.endElement("OverUnderDelimiter").ln();
   }
}
