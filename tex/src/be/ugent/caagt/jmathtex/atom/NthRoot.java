/* NthRoot.java
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
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.OverBar;
import be.ugent.caagt.jmathtex.box.StrutBox;

/**
 * 表示构造根号(n 次根号)的构造元素(atom).
 * An atom representing an nth-root construction.
 */
public class NthRoot extends Atom {

   /* 求根号 */
   private static final String sqrtSymbol = "sqrt";

   private static final float FACTOR = 0.55f;

   // base atom to be put under the root sign; 放在根号下的基本 atom
   private final Atom base;

   // 根次 atom, 放在根号符号的左上位置. 可以没有(没有表示 2 次根式)
   // root atom to be put in the upper left corner above the root sign
   private final Atom root;

   public NthRoot(Atom base, Atom root) {
      this.base = base;
      this.root = root;
   }

   /**
    * 创建此根式的盒子(结果为一个 HorizontalBox).
    */
   public Box createBox(TeXEnvironment env) {
      // first create a simple square root construction; 首先创建简单的二次根式?

      TeXFont tf = env.getTeXFont();
      int style = env.getStyle();
      // calculate minimum clearance clr; 计算最小的净空(余隙)
      float clr;
      float drt = tf.getDefaultRuleThickness(style); // 得到缺省配置的尺子线的粗细(乘以适当的比例根据 style)
      	// 推测 drt: DefaultRuleThickness 首字母的缩写.
      
      // 如果是 display style, 则 clr=... ??语义是什么.
      if (style < TeXConstants.STYLE_TEXT) {
    	 // tf.getChar(...) 返回 sqrtSymbol 对应的 Char 对象.
    	 // 当前配置为: <SymbolMapping name="sqrt" fontId="2" ch="112"/>
    	 //  也即这里 .getFontCode() 应该返回 2(对应 cmex10.ttf), ch=112 是根号(左边部分),113,114,115 是其大的版本.
    	 // 该字体(cmex10.ttf) 配置的 XHeight 值为 0.430555.
         clr = tf.getXHeight(style, tf.getChar1(sqrtSymbol, style).getFontCode());
      }
      else
         clr = drt;
      clr = drt + Math.abs(clr) / 4; 	// ?净空

      // 根号下的公式使用狭窄的样式.(为什么?)
      // cramped style for the formula under the root sign
      Box b = base.createBox(env.crampStyle());

      // create root sign; 创建根号符号, 其高度要大于等于 base 的盒子高度.
      float totalH = b.getHeight() + b.getDepth();
      Box rootSign = DelimiterFactory.create(sqrtSymbol, env, 
    		  totalH + clr + drt);

      // add half the excess to clr;
      float delta = rootSign.getDepth() - (totalH + clr);
      clr += delta / 2;

      // create total box; 将 base 上面加上根号线, => OverBar
      rootSign.setShift(-(b.getHeight() + clr));
      OverBar ob = new OverBar(b, clr, rootSign.getHeight());
      ob.setShift(-(b.getHeight() + clr + drt));
      // 然后将左边根号符号+加上线的base公式 => HorizontalBox
      HorizontalBox squareRoot = new HorizontalBox(rootSign);
      squareRoot.add(ob);

      if (root == null) {
         // simple square root; 简单根号, 没有次数.
         return squareRoot;
      }
      else { // nthRoot, not a simple square root

         // create box from root; 根次 创建盒子.
         Box r = root.createBox(env.rootStyle());

         // shift root up; 添加向上位移.
         float bottomShift = FACTOR
               * (squareRoot.getHeight() + squareRoot.getDepth());
         r.setShift(squareRoot.getDepth() - r.getDepth() - bottomShift);

         // negative kern; 添加一个负的间距(kern)
         Box negativeKern = new SpaceAtom(TeXConstants.UNIT_MU, -10f, 0, 0)
               .createBox(env);

         // 将 kern+根式 水平排列在一起, 构成一个 HorizontalBox() 
         // arrange both boxes together with the negative kern
         Box res = new HorizontalBox();
         float pos = r.getWidth() + negativeKern.getWidth();
         if (pos < 0)
            res.add(new StrutBox(-pos, 0, 0, 0));

         res.add(r);            // 根次
         res.add(negativeKern); // 间距调整
         res.add(squareRoot);   // 根式(不带根次的), 其本身也是一个 HorizontalBox.
         return res;
      } 
   }

   @Override
   public String toString() {
	   return "NthRoot{base=" + base + ", root=" + root + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * 转换为 XML. 格式为:
    *   <NthRoot>
    *     <base>...</base>
    *     <root>...</root>
    *   </NthRoot>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.beginElement("NthRoot").ln();
	   
	   super.superToXml(sxw);
	   
	   if (base != null) {
		   sxw.beginElement("base").ln();
		   base.toXml(sxw, null);
		   sxw.endElement("base").ln();
	   }
	   if (root != null) {
		   sxw.beginElement("root").ln();
		   root.toXml(sxw, null);
		   sxw.endElement("root").ln();
	   }
	   
	   sxw.endElement("NthRoot").ln();
   }
}
