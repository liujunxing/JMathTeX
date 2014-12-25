/* ScriptsAtom.java
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
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.CharBox;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;

/**
 * 表示一个上下标原子, 其被附加到另一个原子.
 * An atom representing scripts to be attached to another atom.
 */
public class ScriptsAtom extends Atom {

   // TeX constant: what's the use???
   private final static SpaceAtom SCRIPT_SPACE = new SpaceAtom(
         TeXConstants.UNIT_POINT, 0.5f, 0, 0);

   // base atom; 原子的核.
   private final Atom base;

   // 下标, 上标, 其附加到原子的核(如果有的话)
   // subscript and superscript to be attached to the base (if not null)
   private final Atom subscript;
   private final Atom superscript;

   /**
    * 使用指定的原子核,下标,上标构造一个 ScriptsAtom 的新实例.
    * @param base -- 原子核
    * @param sub -- 下标
    * @param sup -- 上标
    */
   public ScriptsAtom(Atom base, Atom sub, Atom sup) {
      this.base = base;
      subscript = sub;
      superscript = sup;
   }

   /*
    * 因为有上标下标的几种不同组合, 所以要分别跟踪实验. 以了解创建了什么盒子, 及其特点.
    */
   public Box createBox(TeXEnvironment env) {
      Box b = (base == null ? new StrutBox(0, 0, 0, 0) : base.createBox(env));
      if (subscript == null && superscript == null)
         return b; // 简单的 case, 没有上标也没有下标, 则就是 base 的盒子类型.
      
      else {
         TeXFont tf = env.getTeXFont();
         int style = env.getStyle();

         HorizontalBox hor = new HorizontalBox(b); // 先包装 base 以一个水平盒子.

         int lastFontId = b.getLastFontId();
         // if no last font found (whitespace box), use default "mu font"
         if (lastFontId == TeXFont.NO_FONT)
            lastFontId = tf.getMuFontId();

         TeXEnvironment subStyle = env.subStyle(); 	// 下标显示形式
         TeXEnvironment supStyle = env.supStyle();	// 上标显示形式

         // set delta and preliminary(预备性,初步) shift-up(上移) and shift-down(下移) values
         float delta = 0, shiftUp, shiftDown;

         // TODO: use polymorphism? (是否用多态, 按理应该用......)
         if (base instanceof AccentedAtom) { // special case : 
        	// 重音... 上标最好 next to 该重音...?
            // accent. This positions superscripts better next to the accent!
        	// base.base 是被加重音的那个字母(如 \hat a, 则是字母 a) 创建其一个窄化版本盒子?
            Box box = ((AccentedAtom) base).base.createBox(env.crampStyle()); // 窄化的版本...?
            shiftUp = box.getHeight() - tf.getSupDrop(supStyle.getStyle());
            shiftDown = box.getDepth() + tf.getSubDrop(subStyle.getStyle());
         } else if (base instanceof SymbolAtom
               && base.type == TeXConstants.TYPE_BIG_OPERATOR) { // single big operator symbol
        	// 单个的巨算符符号...
            Char c = tf.getChar1(((SymbolAtom) base).getName(), style);
            if (style < TeXConstants.STYLE_TEXT && tf.hasNextLarger(c)) // display style
               c = tf.getNextLarger(c, style);
            Box x = new CharBox(c);

            // 计算偏移...按理说不仔细研究是不是也可以? 只需要知道创建了何种盒子即可?
            x.setShift(-(x.getHeight() + x.getDepth()) / 2
                  - env.getTeXFont().getAxisHeight(env.getStyle()));
            hor = new HorizontalBox(x); // 创建了水平盒子.

            // include delta in width or not? 这里 delta 是指斜体调整?
            delta = c.getItalic();
            if (delta > TeXFormula.PREC && subscript == null)
               hor.add(new StrutBox(delta, 0, 0, 0));

            shiftUp = hor.getHeight() - tf.getSupDrop(supStyle.getStyle());
            shiftDown = hor.getDepth() + tf.getSubDrop(subStyle.getStyle());
         } else if (base instanceof CharSymbol) { // 普通字符.
            shiftUp = shiftDown = 0;
            CharFont cf = ((CharSymbol) base).getCharFont(tf);
            if (!((CharSymbol) base).isMarkedAsTextSymbol()
                  || !tf.hasSpace(cf.fontId))
               delta = tf.getChar2(cf, style).getItalic();
            if (delta > TeXFormula.PREC && subscript == null) {
               hor.add(new StrutBox(delta, 0, 0, 0));
               delta = 0;
            }
         } else {
            shiftUp = b.getHeight() - tf.getSupDrop(supStyle.getStyle());
            shiftDown = b.getDepth() + tf.getSubDrop(subStyle.getStyle());
         }
         // ?上面计算出了上标, 下标的偏移? 

         if (superscript == null) { // only subscript; 只有下标, 则水平盒子中添加.
            Box x = subscript.createBox(subStyle);
            // calculate and set shift amount
            x.setShift(Math.max(Math.max(shiftDown, tf.getSub1(style)), x
                  .getHeight()
                  - 4 * Math.abs(tf.getXHeight(style, lastFontId)) / 5));

            hor.add(x);
            // add scriptspace (constant value!)
            hor.add(SCRIPT_SPACE.createBox(env));
            return hor;
         } else { // 有上标. (可能也同时有下标)
            Box x = superscript.createBox(supStyle); // 上标创建为自己的盒子.
            HorizontalBox sup = new HorizontalBox(x); // 包装上标到水平盒子中.
            // add scriptspace (constant value!) ; 添加上标间距...
            sup.add(SCRIPT_SPACE.createBox(env));
            // adjust shift-up; 调整向上位移.
            float p;
            if (style == TeXConstants.STYLE_DISPLAY) // 根据不同显示形式, 选择不同的调整值...
               p = tf.getSup1(style);
            	// 在 TeXBook 中提到, crampStyle() 就是指数(上标)升高的不那么多...
            	// 如当前设置下 sup1=0.412892, sup3=0.288889
            else if (env.crampStyle().getStyle() == style)
               p = tf.getSup3(style);
            else
               p = tf.getSup2(style); // 否则 sup2=0.362892; 在 sup1,sup3 之间...
            shiftUp = Math.max(Math.max(shiftUp, p), x.getDepth()
                  + Math.abs(tf.getXHeight(style, lastFontId)) / 4);

            if (subscript == null) { // only superscript
               sup.setShift(-shiftUp);
               hor.add(sup);
            } else { // both superscript and subscript; 即有上标, 也有下标.
               Box y = subscript.createBox(subStyle);
               HorizontalBox sub = new HorizontalBox(y);
               // add scriptspace (constant value!)               
               sub.add(SCRIPT_SPACE.createBox(env));
               // adjust shift-down
               shiftDown = Math.max(shiftDown, tf.getSub2(style));
               // position both sub- and superscript
               float drt = tf.getDefaultRuleThickness(style);
               float interSpace = shiftUp - x.getDepth() + shiftDown
                     - y.getHeight(); // space between sub- en
               // superscript
               if (interSpace < 4 * drt) { // too small
                  shiftUp += 4 * drt - interSpace;
                  // set bottom superscript at least 4/5 of X-height
                  // above
                  // baseline
                  float psi = 4 * Math.abs(tf.getXHeight(style, lastFontId))
                        / 5 - (shiftUp - x.getDepth());

                  if (psi > 0) {
                     shiftUp += psi;
                     shiftDown -= psi;
                  }
               }
               // create total box; 最后?创建总的盒子... 是一个 VerticalBox...?

               VerticalBox vBox = new VerticalBox();
               sup.setShift(delta);
               vBox.add(sup);
               // recalculate interspace
               interSpace = shiftUp - x.getDepth() + shiftDown - y.getHeight();
               vBox.add(new StrutBox(0, interSpace, 0, 0));
               vBox.add(sub);
               vBox.setHeight(shiftUp + x.getHeight());
               vBox.setDepth(shiftDown + y.getDepth());
               hor.add(vBox);
            }
            return hor;
         }
      }
   }

   public int getLeftType() {
      return base.getLeftType();
   }

   public int getRightType() {
      return base.getRightType();
   }

   @Override
   public String toString() {
	   return "ScriptsAtom{base=" + base + ", subscript=" + this.subscript + 
	   		", superscript=" + this.superscript + "}";
   }
   
   public void dump() {
	   System.out.println(toString());
   }

   /**
    * XML:
    *   <ScriptsAtom>
    *     <base> ... </base>
    *     <subscript> ... </subscript>
    *     <superscript> ... </superscript>
    *   </ScriptsAtom>
    */
   public void toXml(SimpleXmlWriter sxw, Object hint) {
	   sxw.beginElement("ScriptsAtom").ln();
	   
	   if (base != null) {
		   sxw.beginElement("base");
		   base.toXml(sxw, null);
		   sxw.endElement("base").ln();
	   }
	   if (subscript != null) {
		   sxw.beginElement("subscript");
		   subscript.toXml(sxw, null);
		   sxw.endElement("subscript").ln();
	   }
	   if (superscript != null) {
		   sxw.beginElement("superscript");
		   superscript.toXml(sxw, null);
		   sxw.endElement("superscript").ln();
	   }
	   
	   sxw.endElement("ScriptsAtom").ln();
   }
}
