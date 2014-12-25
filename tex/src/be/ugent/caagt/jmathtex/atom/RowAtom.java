/* RowAtom.java
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

import java.util.ArrayList;
import java.util.BitSet;
//import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import be.ugent.caagt.jmathtex.CharFont;
import be.ugent.caagt.jmathtex.Glue;
import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.ex.EmptyFormulaException;

/**
 * 表示其它一到多个元素的水平行, 被 glue 分隔. 也负责插入 kern 和 lig.
 *   最终创建为 HorizontalBox.
 * 
 * An atom representing a horizontal row of other atoms, to be seperated by glue.
 * It's also responsible for inserting kerns and ligatures.
 */
public class RowAtom extends Atom implements Row {
    
    /**
     * 水平排列的多个 atom 的数组(集合) 
     * atoms to be displayed horizontally next to eachother 
	 */
    protected List<Atom> elements = new ArrayList<Atom>();
    
    /**
     * previous atom (for nested Row atoms)
     */
    private Dummy previousAtom = null;
    
    /**
     * atom 类型的集合, 其使得前一个 bin 元件类型变为 ord ?
     * set of atom types that make a previous bin atom change to ord
     */
    private static BitSet binSet;
    
    // 可能需要 kern 或和前一个 atom 组合变成一个 lig.
    // set of atom types that can possibly need a kern or, together with the
    // previous atom, be replaced by a ligature
    private static BitSet ligKernSet;
    
    static {
        // fill binSet; 为什么是这些?
        binSet = new BitSet (16);
        binSet.set(TeXConstants.TYPE_BINARY_OPERATOR); 	// 二元运算符 
        binSet.set(TeXConstants.TYPE_BIG_OPERATOR); 	// 巨算符
        binSet.set(TeXConstants.TYPE_RELATION);			// 关系运算符
        binSet.set(TeXConstants.TYPE_OPENING);			// 开定界符
        binSet.set(TeXConstants.TYPE_PUNCTUATION);		// 标点
        
        // fill ligKernSet; 为什么是这些?
        ligKernSet = new BitSet (16);
        ligKernSet.set(TeXConstants.TYPE_ORDINARY);			// 一般符号
        ligKernSet.set(TeXConstants.TYPE_BIG_OPERATOR);		// 巨算符
        ligKernSet.set(TeXConstants.TYPE_BINARY_OPERATOR);	// 二元运算符
        ligKernSet.set(TeXConstants.TYPE_RELATION);			// 关系运算符
        ligKernSet.set(TeXConstants.TYPE_OPENING);			// 开定界符
        ligKernSet.set(TeXConstants.TYPE_CLOSING);			// 闭定界符
        ligKernSet.set(TeXConstants.TYPE_PUNCTUATION);		// 标点
    }
    
    protected RowAtom() {
        // empty
    }
    
    public RowAtom(Atom el) {
        if (el != null) {
            if (el instanceof RowAtom)
            	// 不用创建多层 RowAtom, 只需要复制其子元素
                // no need to make an mrow the only element of an mrow
                elements.addAll(((RowAtom) el).elements);
            else
                elements.add(el);
        }
    }
    
    /**
     * 仅当解析 MathML 的时候使用. (暂时不研究)
     * Only used while parsing MathML. An empty Mrow is not allowed, otherwise
     * it's possible to create fractions with an empty numerator or denominator.
     *
     * @param l
     *           list of objects of the type Formula
     * @throws EmptyFormulaException
     */
    public RowAtom(List<TeXFormula> l) throws EmptyFormulaException {
        for (TeXFormula f : l) {
            if (f.root != null)
                elements.add(f.root);
        }
        if (elements.isEmpty())
            throw new EmptyFormulaException();
    }
    
    /**
     * 添加一个子 atom 在列表末尾.
     * @param el
     */
    public final void add(Atom el) {
        if (el != null)
            elements.add(el);
    }
    
    /**
     * 从 createBox() 中调用. 对于某些条件下, 将二元运算符(bin)当做普通(ord)符号看待.
     *   对此, cur.setType(ord) 将被调用以设置其类型.
     * 规则可能在 TeXBook 中有描述, 但是代码中可能更细致, 更严谨一些吧.
     * 
     * @param cur
     *           current atom being processed; 当前正在处理(扫描到)的 atom
     * @param prev
     *           previous atom; 前一个 atom. 
     * @param next -- 后一个 atom.
     */
    private void changeToOrd(Dummy cur, Dummy prev, Atom next) {
        int type = cur.getLeftType();
        // 语义: 如果 type 是二元运算符(如 + 加号), 以及 prev == null (前面没有符号) 或
        //  binSet 中含 prev 的类型(意思应该是: 该类型不当做二元运算符的前(左)操作数 )
        //  如 x+y 中加号当做二元运算符, +y 不作为; ++y 也不作为吧?
        if (type == TeXConstants.TYPE_BINARY_OPERATOR
                && (prev == null || binSet.get(prev.getRightType())))
            cur.setType(TeXConstants.TYPE_ORDINARY); // 此时将这些二元运算符当做普通符号(字符)看待.
        // 如果后面的元素非空, 当前是二元运算符 
        else if (next != null
                && cur.getRightType() == TeXConstants.TYPE_BINARY_OPERATOR) {
            int nextType = next.getLeftType();
            if (nextType == TeXConstants.TYPE_RELATION 			// 关系运算符
                    || nextType == TeXConstants.TYPE_CLOSING	// 闭定界符
                    || nextType == TeXConstants.TYPE_PUNCTUATION) // 标点
                cur.setType(TeXConstants.TYPE_ORDINARY); // 将当前 atom 类型设为 普通符号.
        }
        
        // 以上规则 TeXBook 哪里有解释吗??
    }

    /*
     * TeX 把 0123456789!?.|/'@" 当做普通符号看待. 当这些符号出现在字母后面或者 18 个字符后面时,
     *   不插入任何额外的间距. 但仍使用 roman 字体, 字母使用 italic(数学斜体) 字体. 
     * +, -, * 这三个字符称为 `二元运算符' (binary operator), 因为它们作用在公式的两个组成成分上.
     *   TeX 在排版时, 符号 -, * 与普通文本中所见不一样. TeX 不把 / 看做二元运算.
     *   附录F 列出更多的二元运算符, 如 \times 乘号, \cup 集合与, \land 逻辑与 等. (不少)
     * 如果二元运算符不出现在它们要操作的两个量之间, 那么就看做普通符号. 
     *   如 x=+1 的 + 在两边没有添加额外的间距.
     *   又如 K^+, 此时 + 作为上标时, 被看做是普通符号 
     * TeX 把 =, <, >, : 这四个字符看做 `关系符号' (relation operator), 因为它们表达了
     *   两个量之间的关系. 它们和二元运算的意思有差别, 排版也有所不同.
     * 逗号 `,' 和分号 `;' 这两个字符被看做公式中的标点; TeX 在后面添加额外的小间距, 但前面没有.
     * 字符在数学模式中的含义通过 \mathcode 来改变. 这里采纳的是约定俗成的 plain TeX 规约.
     * 数学家偏爱在字母上面加重音, 如 \hat a 在 a 的上面添加 ^ 形状的重音符. 还有多种别的重音.
     * \skew 控制系列, 它使得把上面的重音移到正确的位置变得相当简单. 如 \skew6\hat\Ahat (见书)
     * plain TeX 给出了两个可伸长的重音: \widehat, \widetilde. (?似乎有最大宽度尺寸限制)
     * 
     * 分数是常见数学构造, 通过命令 \over 构造(\frac 应是定义为使用 \over 的)
     * 当字母和其它符号出现在分数中时, 有时候会变得更小, 如像上下标那样. 
     *   处理公式时候八种字体, 写在 TeXConstants 那里.
     *   
     * \atop 命令类似于 \over, 但是没有分数中的横线.
     * \choose 也像 \atop, 但是把结果封装在括号中.
     *
     * \sum 表示 `求和' 符号. \int 表示 `积分' 符号. 等等这样的符号称为 `巨算符'(large operator).
     * 和其它区别在于, TeX 在 display style 模式下, 选择较大版本的 巨算符, 相对于 text style 时.
     * 对于求和符号:
     *   display style 下 limit 放在巨算符上面和下面.
     *   text style 下 limit 放在上下标的位置.
     * 积分:
     *   limit 都是放在上下标位置.
     * 使用 \limits, \nolimits 命令可以改变 limit 的位置(位于上下标, 还是上面下面)
     */
    
    /*
     * (non-Javadoc)
     * @see be.ugent.caagt.jmathtex.Atom#createBox(be.ugent.caagt.jmathtex.TeXEnvironment)
     */
    @SuppressWarnings("unchecked")
	public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont();
        HorizontalBox hBox = new HorizontalBox(env.getColor(), env.getBackground());
        env.reset();
        
        // 转换子 atom 为盒子, 然后添加到 水平盒子中. (水平盒子构造算法)
        // convert atoms to boxes and add to the horizontal box
        for (ListIterator it = elements.listIterator(); it.hasNext();) { // 遍历所有水平的元素.
            Dummy atom = new Dummy((Atom) it.next()); // 将这个元素包装为 Dummy.
            
            // if necessary, change BIN type to ORD; 如果有必要, 将 BIN 类型改变为 ORD.
            // BIN 类型是二元运算符, 如 +, * 等.
            Atom nextAtom = null;
            if (it.hasNext()) {
                nextAtom = (Atom) it.next();
                it.previous();
            }
            changeToOrd(atom, previousAtom, nextAtom); // 某些条件下 bin 看做是 ord.
            
            // 检查 lig 或 kern. check for ligatures or kerning.
            float kern = 0;
            // 如果有下一个符号, 且其类型是 ord, 是字符符号.
            if (it.hasNext() && atom.getRightType() == TeXConstants.TYPE_ORDINARY
                    && atom.isCharSymbol()) {
                Atom next = (Atom) it.next(); // 得到该字符符号.
                // 是 CharSymbol(各种字符符号), 以及 可能需要 kern/lig 的类型?
                if (next instanceof CharSymbol
                        && ligKernSet.get(next.getLeftType())) {
                    atom.markAsTextSymbol();
                    CharFont l = atom.getCharFont(tf); // 当前 atom, 作为 left
                    CharFont r = ((CharSymbol) next).getCharFont(tf); // 下一个 atom, 作为 right
                    CharFont lig = tf.getLigature(l, r); // 查找看 left/right 是否有 lig 信息.
                    if (lig == null) { // l,r 不能连排(lig); 
                        kern = tf.getKern(l, r, env.getStyle()); // 查看有没有 kern 信息. 
                        it.previous(); // iterator remains unchanged (no ligature!)
                    } 
                    else { // ligature; 这里构建了 FixedCharAtom! 处理了连排!
                        atom.changeAtom(new FixedCharAtom(lig)); // go on with the
                        // ligature
                    }
                } else
                    it.previous();// iterator remains unchanged
            }
            
            // 插入合适的粘连. 除非...
            // insert glue, unless it's the first element of the row
            // OR this element or the next is a Kern.
            if (it.previousIndex() != 0 && previousAtom != null
                    && !previousAtom.isKern() && !atom.isKern())
            	// 根据 left_type, right_type 得到合适的粘连, 并放到水平盒子中.
                hBox.add(Glue.get(previousAtom.getRightType(), atom.getLeftType(),
                        env));
            
            // insert atom's box; 
            atom.setPreviousAtom(previousAtom);
            Box b = atom.createBox(env);
            hBox.add(b);
            
            // set last used fontId (for next atom); 设置最后使用的字体标识.
            env.setLastFontId(b.getLastFontId());
            
            // insert kern; 插入间距. ??前面已经有粘连了, 还要插入 kern 吗? 两者不能合并吗?
            // 而且这个 kern 是垂直方向的?? 暂时不明白.
            if (kern > TeXFormula.PREC)
                hBox.add(new StrutBox(0, kern, 0, 0));
            
            // kerns do not interfere with the normal glue-rules without kerns
            if (!atom.isKern())
                previousAtom = atom;
        }
        // reset previousAtom
        previousAtom = null;
        
        // return resulting horizontal box
        return hBox;
    }
    
    public void setPreviousAtom(Dummy prev) {
        previousAtom = prev;
    }
    
    public int getLeftType() {
        if (elements.isEmpty())
            return TeXConstants.TYPE_ORDINARY;
        else
            return ((Atom) elements.get(0)).getLeftType();
    }
    
    public int getRightType() {
        if (elements.isEmpty())
            return TeXConstants.TYPE_ORDINARY;
        else
            return ((Atom) elements.get(elements.size() - 1)).getRightType();
    }

    public String toString() {
    	return "RowAtom{elements=" + elements + "}";
    }
    
    public void dump() {
    	System.out.println(toString());
    }

    /**
     * 输出 XML:
     *   <RowAtom>
     *     <elements>...</elements>
     *     ?? previous 要不要输出呢? 会不会出现无穷递归...?
     *   </RowAtom>
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.beginElement("RowAtom").ln();
    	
    	sxw.beginElement("elements").ln();
    	for (int i = 0 ; i < this.elements.size(); ++i) {
    		sxw.appendRaw("<element i=\"" + i +"\">").ln();
    		elements.get(i).toXml(sxw, this);
    		sxw.endElement("element").ln();
    	}
    	sxw.endElement("elements").ln();
    	
    	sxw.endElement("RowAtom").ln();
    }
}
