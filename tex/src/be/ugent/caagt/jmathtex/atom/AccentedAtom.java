/* AccentedAtom.java
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
import be.ugent.caagt.jmathtex.TeXSymbolParser;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.CharBox;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;
import be.ugent.caagt.jmathtex.ex.InvalidSymbolTypeException;
import be.ugent.caagt.jmathtex.ex.InvalidTeXFormulaException;
import be.ugent.caagt.jmathtex.ex.SymbolNotFoundException;

/**
 * 表示在一个(任意)符号上面添加重音(accent)符号.
 * An atom representing another atom with an accent symbol above it.
 */
public class AccentedAtom extends Atom {
    
    // accent symbol (要添加的重音符号)
    private final SymbolAtom accent;
    
    // base atom (基本符号, 重音符号添加在它上面)
    protected Atom base = null;
    
    /**
     * Creates an AccentedAtom from a base atom and an accent symbol defined by its name
     *
     * @param base base atom
     * @param accentName name of the accent symbol to be put over the base atom
     * @throws InvalidSymbolTypeException if the symbol is not defined as an accent ('acc')
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     */
    public AccentedAtom(Atom base, String accentName) 
    		throws InvalidSymbolTypeException, SymbolNotFoundException { 
    	// 使用 SymbolAtom 的静态方法 get() 得到预先配置好的 accentName=>SymbolAtom 的映射
        accent = SymbolAtom.get(accentName); // 返回为一个预先从配置文件中加载的 SymbolAtom.
        
        if (accent.type == TeXConstants.TYPE_ACCENT) // 确保必须是类型 TYPE_ACCENT!
            this.base = base;
        else
            throw new InvalidSymbolTypeException("The symbol with the name '"
                    + accentName + "' is not defined as an accent ("
                    + TeXSymbolParser.TYPE_ATTR + "='acc') in '"
                    + TeXSymbolParser.RESOURCE_NAME + "'!");
    }
    
    /**
     * Creates an AccentedAtom from a base atom and an accent symbol defined as a TeXFormula.
     * This is used for parsing MathML.
     *
     * @param base base atom
     * @param acc TeXFormula representing an accent (SymbolAtom)
     * @throws InvalidTeXFormulaException if the given TeXFormula does not represent a
     * 			single SymbolAtom (type "TeXConstants.TYPE_ACCENT")
     * @throws InvalidSymbolTypeException if the symbol is not defined as an accent ('acc')
     */
    public AccentedAtom(Atom base, TeXFormula acc)
    throws InvalidTeXFormulaException, InvalidSymbolTypeException {
        if (acc == null)
            throw new InvalidTeXFormulaException(
                    "The accent TeXFormula can't be null!");
        else {
            Atom root = acc.root;
            if (root instanceof SymbolAtom) {
                accent = (SymbolAtom) root;
                if (accent.type == TeXConstants.TYPE_ACCENT)
                    this.base = base;
                else
                    throw new InvalidSymbolTypeException(
                            "The accent TeXFormula represents a single symbol with the name '"
                            + accent.getName()
                            + "', but this symbol is not defined as an accent ("
                            + TeXSymbolParser.TYPE_ATTR + "='acc') in '"
                            + TeXSymbolParser.RESOURCE_NAME + "'!");
            } else
                throw new InvalidTeXFormulaException(
                        "The accent TeXFormula does not represent a single symbol!");
        }
    }
    
    public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont(); // 得到字体(管理器,容器)
        int style = env.getStyle();    // 显示样式.
        
        // set base in cramped(狭窄的,拥挤的) style; 问: base==null 是什么意思? 或者先忽略.
        Box base_box = (base == null ? new StrutBox(0, 0, 0, 0) : 
        	base.createBox(env.crampStyle()));
        	// 先假定 base 非空, 从而 b 这里表示 base 的盒子. (狭窄的样式?)
        
        float u = base_box.getWidth();
        float s = 0;
        if (base instanceof CharSymbol) // ??skew 的语义.
            s = tf.getSkew(((CharSymbol) base).getCharFont(tf), style);
        
        // retrieve best Char from the accent symbol; 计算 最好的, 最合适的该符号版本. 这里按照宽度选择.
        Char ch = tf.getChar1(accent.getName(), style);
        while (tf.hasNextLarger(ch)) { // 如果有更大版本...
            Char larger = tf.getNextLarger(ch, style); // 试图获得更大的那个版本.
            if (larger.getWidth() <= u) // 如果大版本的宽度比 u 小(表示更合适一些)
                ch = larger; // 则选用这个较大版本
            else
                break;
        } // 这里的选择结果信息, 可能导致在转换为 HTML 的时候不太好弄...
        
        
        // calculate delta; 计算 delta 值(??) 下面创建 kern 盒子用.
        float delta = Math.min(base_box.getHeight(), tf.getXHeight(style, ch.getFontCode()));
        
        // create vertical box; 创建一个垂直的盒子.
        VerticalBox vBox = new VerticalBox();
        
        // accent; 重音的盒子.
        Box y;
        float italic = ch.getItalic();
        if (italic > TeXFormula.PREC) { // 大于0则创建支柱盒子.
        	CharBox ch_box = new CharBox(ch);
            y = new HorizontalBox(ch_box); // 水平盒子包括: CharBox, StrutBox.
            y.add(new StrutBox(italic, 0, 0, 0)); // 添加一个 支柱盒子, 宽为 italic.
        } else
            y = new CharBox(ch); // 否则就用 CharBox 即可.
        
        // u 表示 base 盒子 b 的宽度. 如果 base 盒子和 y 盒子宽度不同, 则需要将少的那个居中.
        // if diff > 0, center accent, otherwise center base
        float diff = (u - y.getWidth()) / 2;
        y.setShift(s + (diff > 0 ? diff : 0)); // 如果 u宽(重音符窄), 则设置一个重音符的位移
        if (diff < 0) // 表示 base 比较窄. 则用水平盒子, 将其居中?
            base_box = new HorizontalBox(base_box, y.getWidth(), TeXConstants.ALIGN_CENTER); // 产生居中的盒子. 内含三个盒子.
        
        // y 是重音符号的盒子.
        vBox.add(y);
        
        // kern (字距) ?? delta 的含义是什么?
        vBox.add(new StrutBox(0, -delta, 0, 0));
        // base
        vBox.add(base_box);
        
        // 现在 VerticalBox 里面有 accent(重音的盒子) + kern(字距盒子) + base_box(基本符号盒子).
        
        // set height and depth vertical box; 设置这个垂直盒子的高度, 深度.
        // 高度在 VerticalBox.add() 的时候已经进行了更新 (注意是增加 depth)
        float total = vBox.getHeight() + vBox.getDepth(); // 总的高度.
        float d = base_box.getDepth(); // 只使用 base_box 的深度作为合成之后的深度.
        vBox.setDepth(d); // 设置实际深度.
        vBox.setHeight(total - d); // 设置实际高度.
        return vBox;
    }

    @Override
    public String toString() {
    	return "AccentedAtom{accent=" + accent + ", base=" + base + "}";
    }
    
    public void dump() {
    	System.out.println(toString());
    }

    /**
     * 输出为 xml. 此 atom 的格式为 
     *   <AccentedAtom>
     *     <accent>要添加的重音符号 atom</accent>   如果非空的话.
     *     <base>基本符号</base>                   如果非空的话.
     *   </AccentedAtom>
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.beginElement("AccentedAtom").ln();
    	if (this.accent != null) {
    		sxw.beginElement("accent").ln();
    		this.accent.toXml(sxw, null);
    		sxw.endElement("accent").ln();
    	}
    	if (this.base != null) {
    		sxw.beginElement("base").ln();
    		this.base.toXml(sxw, null);
    		sxw.endElement("base").ln();
    	}
    	sxw.endElement("AccentedAtom").ln();
    }
}
