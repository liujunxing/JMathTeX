/* BigOperatorAtom.java
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
 * 表示一个 "巨算符" 的 atom (或一个元件像巨算符), 和它的 limits(极限,限制). 如积分符,求和符.
 * An atom representing a "big operator" (or an atom that acts as one) together
 * with its limits.
 */
public class BigOperatorAtom extends Atom {
    
    // limits; 在下面(或下标位置)的 limit
    private Atom under = null;
    private Atom over = null; // 上面(或上标位置)的 limit.
    
    // atom representing a big operator; 表示此算符的 atom.?? 是 CharSymbol 吗? 还是可以任意?
    // 原来是 protected 的, 我改成 private 的. 而且未发现有异常? 可能原来想法我们已不可能知道了.
    private Atom base = null;
    
    // 是否 limits-值应该计入(考虑). 否则应用缺省规则. (表示 limits 属性被显式的构造给出了?)
    // whether the "limits"-value should be taken into account
    // (otherwise the default rules will be applied)
    private boolean limitsSet = false;
    
    // 是否 limit 应该绘制在 base 的上面/下面 (或当做上下标)
    // whether limits should be drawn over and under the base (<-> as scripts)
    private boolean limits = false;
    
    /**
     * Creates a new BigOperatorAtom from the given atoms.
     * The default rules the positioning of the limits will be applied.
     *
     * @param base atom representing the big operator
     * @param under atom representing the under limit
     * @param over atom representing the over limit
     */
    public BigOperatorAtom(Atom base, Atom under, Atom over) {
        this.base = base;
        this.under = under;
        this.over = over;
        type = TeXConstants.TYPE_BIG_OPERATOR;
    }
    
    /**
     * Creates a new BigOperatorAtom from the given atoms.
     * Limits will be drawn according to the "limits"-value (同时设置了 limitsSet 标志)
     *
     * @param base atom representing the big operator
     * @param under atom representing the under limit
     * @param over atom representing the over limit
     * @param limits whether limits should be drawn over and under the base (<-> as scripts)
     */
    public BigOperatorAtom(Atom base, Atom under, Atom over, boolean limits) {
        this(base, under, over);
        this.limits = limits;
        limitsSet = true;
    }
    
    public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont(); // 得到字体管理器.
        int style = env.getStyle();    // 显式样式.
        
        Box y;
        float delta;
        
        if ((limitsSet && !limits) // limitsSet==true && limits==false: limits 被设置为 false
        		|| (!limitsSet && style >= TeXConstants.STYLE_TEXT)) // 未设置 limits, 显示样式>=2(较小字体?)
        	// 如果显式设置为不显示为 limit(此时作为上下标), 或未设置并且 style 是文本行模式
        	//  (非 display 模式), 则将 over,under 当做普通的(一般的)上下标处理.
            // if explicitly set to not display as limits or if not set and style
            // is not display, then attach over and under as regular sub- en
            // superscript
        	// 实际临时创建一个 ScriptsAtom, 然后用其 createBox()
        	// 如果是这样, 可否考虑 BigOperatorAtom, ScriptsAtom 使用某个共同的东西?? 
            return new ScriptsAtom(base, under, over).createBox(env);
        else {
            if (base instanceof SymbolAtom // base 是一个符号, 而且类型也是 BIG_OP~
                    && base.type == TeXConstants.TYPE_BIG_OPERATOR) { // single
                // bigop
                // symbol; 得到 base 的字符显示信息(在显示模式 style 下)
                Char c = tf.getChar1(((SymbolAtom) base).getName(), style);
                if (style < TeXConstants.STYLE_TEXT && tf.hasNextLarger(c)) // display
                    // style
                    c = tf.getNextLarger(c, style); // 如果有较大版本, 则使用较大版本. (?含义是什么)
                		// 推断: 正常符号大小是给 STYLE_TEXT 使用的. 在 DISPLAY 模式下, 要使用该符号的较大版本.(如果有的话)
                
                Box x = new CharBox(c); // 创建 base 的盒子为 CharBox.
                x.setShift(-(x.getHeight() + x.getDepth()) / 2 /* x 高度的一半 */
                        - env.getTeXFont().getAxisHeight(env.getStyle())); // 设置位移: 沿垂直中心线对齐 x
                y = new HorizontalBox(x); // 放到一个水平盒子中.
                
                // include delta in width; 在宽度中包含 delta. (斜体校正...?)
                delta = c.getItalic();
                if (delta > TeXFormula.PREC)
                    y.add(new StrutBox(delta, 0, 0, 0));
            } else { // formula (公式)
                delta = 0;
                y = new HorizontalBox(base == null 
                		? new StrutBox(0, 0, 0, 0)
                		: base.createBox(env));
            }
            
            // limits (处理 limits 部分); x 表示 over 的盒子, z 表示 under 的盒子.
            Box x = null, z = null;
            if (over != null)
                x = over.createBox(env.supStyle());
            if (under != null)
                z = under.createBox(env.subStyle());
            
            // make boxes equally wide; 使得盒子等宽. 因为到这里是将 over,under 放在上面和下面
            // 所有在水平方向上要宽度相同. (上下标前面走另一个分支了)
            float maxWidth = Math.max(Math.max(x == null ? 0 : x.getWidth(), y
                    .getWidth()), z == null ? 0 : z.getWidth());
            x = changeWidth(x, maxWidth); // 根据情况包装 HorizontalBox, 使其宽度为 maxWidth.
            y = changeWidth(y, maxWidth);
            z = changeWidth(z, maxWidth);
            
            // build vertical box; 经过上面处理, x,y,z 盒子现在已经等宽(=maxWidth)
            VerticalBox vBox = new VerticalBox();
            // 为什么是 bigop5?? 呢
            float bigop5 = tf.getBigOpSpacing5(style), kern = 0;
            
            @SuppressWarnings("unused") /* 减少点警告 */
			float xh = 0; // TODO: check why this is not used(检查为何没用) // NOPMD(什么意思, 表示未实现) NOt imPleMenteD??
            
            // over (处理上面的 limit)
            if (over != null) {
                vBox.add(new StrutBox(0, bigop5, 0, 0));
                x.setShift(delta / 2); // 为斜体校正发生一些位移? 暂时不明白.
                vBox.add(x);
                kern = Math.max(tf.getBigOpSpacing1(style), 		// 计算 kern; 具体含义不清.
                		tf.getBigOpSpacing3(style) - x.getDepth());
                vBox.add(new StrutBox(0, kern, 0, 0));
                xh = vBox.getHeight() + vBox.getDepth(); // xh = x部分(over)的总高度.
            }
            
            // base
            vBox.add(y);
            
            // under
            if (under != null) {
                float k = Math.max(tf.getBigOpSpacing2(style), 		// 计算 kern; 具体含义不清.
                		tf.getBigOpSpacing4(style) - z.getHeight());
                vBox.add(new StrutBox(0, k, 0, 0));
                z.setShift(-delta / 2);
                vBox.add(z);
                vBox.add(new StrutBox(0, bigop5, 0, 0));
            }
            
            // set height and depth vertical box and return it; 设置构造好的 VerticalBox 的高度,深度.
            // 需要看明白, height,depth 是以谁为基线的. (应该是以 base 符号的基线为基线).
            float h = y.getHeight(), total = vBox.getHeight() + vBox.getDepth();
            if (x != null)
                h += bigop5 + kern + x.getHeight() + x.getDepth();
            vBox.setHeight(h);
            vBox.setDepth(total - h);
            return vBox;
        }
    }
    
   /*
    * Centers the given box in a new box that has the given width
    */
    private static Box changeWidth(Box b, float maxWidth) {
        if (b != null && Math.abs(maxWidth - b.getWidth()) > TeXFormula.PREC)
            return new HorizontalBox(b, maxWidth, TeXConstants.ALIGN_CENTER);
        else
            return b;
    }

    @Override
    public String toString() {
    	return "BigOperatorAtom{under=" + under + ", over=" + over + ", base=" + base + 
    		", limitsSet=" + limitsSet + ", limits=" + limits + "}";
    }
    
    public void dump() {
    	System.out.println(toString());
    }

    /**
     * 输出 xml. 结构为:
     *   <BigOperatorAtom limitsSet=bool limits=bool>
     *     <base>...</base>
     *     <under>...</under>
     *     <over>...</over>
     *   </BigOperatorAtom>
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.appendRaw("<BigOperatorAtom ").attribute("limitsSet", limitsSet)
    		.blank().attribute("limits", limits).appendRaw(">").ln();
    	
    	if (base != null) {
	    	sxw.beginElement("base").ln();
	    	base.toXml(sxw, null);
	    	sxw.endElement("base").ln();
    	}
    	if (under != null) {
	    	sxw.beginElement("under").ln();
	    	under.toXml(sxw, null);
	    	sxw.endElement("under").ln();
    	}
    	if (over != null) {
	    	sxw.beginElement("over").ln();
	    	over.toXml(sxw, null);
	    	sxw.endElement("over").ln();
    	}
    	
    	sxw.endElement("</BigOperatorAtom>").ln();
    }
 }
