/* DelimiterFactory.java
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

package be.ugent.caagt.jmathtex; // NOPMD

import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.CharBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;

/**
 * 定界符工厂. 职责是创建一个盒子其包含定界符符号, 符号有着不同的尺寸.
 * 
 * 也许应该在 DefaultTeXFont 中研究一下, 哪些字符有扩展信息. (推测那些定界符有, 但怎么联系起来的呢??) 
 * 
 * Responsible for creating a box containing a delimiter symbol that exists
 * in different sizes.
 */
public class DelimiterFactory {
    
	/*
	 * 数学公式可能变得很大. TeX 使用特定的方式制作(make)更大的符号. 例如多层的根号时候, 最外层的
	 *   根号可能变得很大. (见书) 类似情况如括号, 等其它定界符(delimiters).(见书)
	 * 书上例子中, 最大的三对(定界符)是通过可重复的(repeatable)扩展(extensions)而得到的, 因而
	 *   可以达到所需任意大小.
	 * 
	 * \bigl, \bigr 用于得到稍微大一点的开定界符和闭定界符. (可使得公式更可读)
	 *   类似的有 \Bigl, \Bigr, \biggl, \biggr 等...
	 * \left, \right 可以自己选择合适的大小的定界符.
	 * 
	 * 巨圆括号和大括号关于基线附近有一条看不见的水平线对称; 当分界符变大时, 高度和深度增加同样的量.
	 *   这个水平线称为公式的 轴(axis); 在每个分数中, 横线都在轴上, 不管分子或分母的大小.
	 * 
	 */
	
    /**
     * 得到最少是指定 minHeight 高度的定界符. (一般为 VerticalBox 盒子)
     * @param symbol the name of the delimiter symbol (该定界符的名字, 如 "langle", "lbrace" 等)
     * @param env the TeXEnvironment in which to create the delimiter box
     * @param minHeight the minimum required total height of the box (height + depth).
     *        对该定界符, 所需的最小尺寸.
     * @return the box representing the delimiter variant that fits best according to
     * 			the required minimum size.
     */
    public static Box create(String symbol, TeXEnvironment env, float minHeight) {
        TeXFont tf = env.getTeXFont();
        int style = env.getStyle();
        Char c = tf.getChar1(symbol, style); // 得到该符号的 char, fontid, font, 尺寸等全部信息.
        
        // start with smallest character; 从最小的尺寸开始.
        Metrics m = c.getMetrics();
        float total = m.getHeight() + m.getDepth(); // 该符号的大小(高度). 公式是水平排的, 所以...
        
        // try larger versions of the same character until minHeight has been
        // reached; 尝试较大版本, 直到能达到 minHeight (或者没有了?)
        while (total < minHeight && tf.hasNextLarger(c)) { // 高度不够, 有下一个版本, 尝试它.
            c = tf.getNextLarger(c, style);
            m = c.getMetrics();
            total = m.getHeight() + m.getDepth();
        }
        
        // 如果有足够高度的, 则返回它.
        if (total >= minHeight) { // tall enough character found
            return new CharBox(c);
        } else if (tf.isExtensionChar(c)) {
            // construct tall enough vertical box; 构造一个高度足够的垂直盒子.
            VerticalBox vBox = new VerticalBox();
            Extension ext = tf.getExtension(c, style); // extension info; 获得扩展信息. TODO: 了解extension
            
            if (ext.hasTop()) { // insert top part; 在垂直盒子中添加 top 部分, 如果有的话.
                c = ext.getTop();
                vBox.add(new CharBox(c));
            }
            
            boolean middle = ext.hasMiddle();
            if (middle) { // insert middle part; 添加中间部分, 如果有的话.
                c = ext.getMiddle();
                vBox.add(new CharBox(c));
            }
            
            if (ext.hasBottom()) { // insert bottom part; 添加底下部分, 如果有的话.
                c = ext.getBottom();
                vBox.add(new CharBox(c));
            }
            
            // insert repeatable part until tall enough; 插入重复部分, 直到高度达到要求的 minHeight
            c = ext.getRepeat();
            CharBox rep = new CharBox(c);
            do {
                if (ext.hasTop() && ext.hasBottom()) { // 如果有 top,bottom, 则在中间插入.
                    vBox.add(1, rep);
                    if (middle) // 有 middle, 则需要配对的插入.
                        vBox.add(vBox.getSize() - 1, rep);
                } else if (ext.hasBottom()) // 否则, 如果只有 bottom, 则在上面插入.
                    vBox.add(0, rep);
                else // 否则应该是只有 top 或 middle 了, 则在下面插入.
                    vBox.add(rep);
            } while (vBox.getHeight() + vBox.getDepth() < minHeight); // 直到高度达到要求.
            
            return vBox; // 返回此 垂直盒子. (这里没有设置垂直盒子的 depth,height, 因为不用基线对齐??)
        } else
        	// 没有扩展字符, 则返回该字符的最高的那个版本.
            // no extensions, so return tallest possible character
            return new CharBox(c);
    }
}
