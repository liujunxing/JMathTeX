/* TeXEnvironment.java
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

package be.ugent.caagt.jmathtex;

import java.awt.Color;

/**
 * 包括当前用于绘制公式的 TeXFont 对象, 颜色设置, 当前样式. 此对象用于 createBox() 方法. 
 * 
 * Contains the used TeXFont-object, color settings and the current style in which a
 * formula must be drawn. It's used in the createBox-methods. Contains methods that
 * apply the style changing rules for subformula's.
 */
public class TeXEnvironment {
    
    // colors
    private Color background = null, color = null;
    
    /** current style (当前显示形式) */
    private int style = TeXConstants.STYLE_DISPLAY;
    
    /** TeXFont used */
    private TeXFont tf;
    
    // last used font
    private int lastFontId = TeXFont.NO_FONT;
    
    /**
     * 构造新实例.
     * @param style
     * @param tf
     */
    public TeXEnvironment(int style, TeXFont tf) {
        this(style, tf, null, null);
    }
    
    private TeXEnvironment(int style, TeXFont tf, Color bg, Color c) {
        // check if style is valid
        // if not : DISPLAY = default value
        if (style == TeXConstants.STYLE_DISPLAY || style == TeXConstants.STYLE_TEXT
                || style == TeXConstants.STYLE_SCRIPT || style == TeXConstants.STYLE_SCRIPT_SCRIPT)
            this.style = style;
        else
            this.style = TeXConstants.STYLE_DISPLAY;
        
        this.tf = tf;
        background = bg;
        color = c;
    }
    
    /**
     * 新建一个 TeXEnvironment 的复制(拷贝), 与此实例的值完全一样.
     * @return
     */
    public TeXEnvironment copy() {
        return new TeXEnvironment(style, tf, background, color);
    }
    
    /**
     * 返回(新建的)环境的拷贝, 但是其样式是窄化的(cramped,中文版翻译为近似的). 
     * style 值列表如下: <pre>
     *   原来的 style 值		新的 cramped style 值
     *    D=0				D'=1
     *    D'=1				D'=1
     *    T=2				T'=3
     *    T'=3				T'=3
     *    S=4				S'=5
     *    S'=5				S'=5
     *    SS=6				SS'=7
     *    SS'=7				SS'=7 </pre>
     *    
     * @return a copy of the environment, but in a cramped(狭窄的) style.
     */
    public TeXEnvironment crampStyle() {
        TeXEnvironment s = copy();
        s.style = (style % 2 == 1 ? style : style + 1);
        return s;
    }
    
    /**
     * 为分母创建所需的环境拷贝. 根据 TeXBook 第17章规则, 列表如下:
     *    分式 a/b style      分子 a 的 style         分母 b 的 style
     *    D=0                T=2                    T'=3
     *    D'=1               T'=3                   T'=3
     *    T=2                S=4                    S'=5
     *    T'=3               S'=5                   S'=5
     *    S=4,SS=6           SS=6                   SS'=7
     *    S'=5,SS'=7         SS'=7                  SS'=7
     * 这里的公式计算出来 `应该' 就是表中的值. 但是就是不容易懂吧...
     * @return a copy of the environment, but in denominator(分母) style.
     */
    public TeXEnvironment denomStyle() {
        TeXEnvironment s = copy();
        // 神奇的公式, 我觉得弄成表格是不是更容易理解??
        s.style = 2 * (style / 2) + 1 + 2 - 2 * (style / 6);
        return s;
    }
    
    /**
     * 参见 denomStyle() 的说明.
     * @return a copy of the environment, but in numerator(分子) style.
     */
    public TeXEnvironment numStyle() {
        TeXEnvironment s = copy();
        s.style = style + 2 - 2 * (style / 6);
        return s;
    }
    
    /**
     *
     * @return the background color setting
     */
    public Color getBackground() {
        return background;
    }
    
    /**
     *
     * @return the foreground color setting
     */
    public Color getColor() {
        return color;
    }
    
    /**
     *
     * @return the point size of the TeXFont
     */
    public float getSize() {
        return tf.getSize();
    }
    
    /**
     *
     * @return the current style
     */
    public int getStyle() {
        return style;
    }
    
    /**
     *
     * @return the TeXFont to be used
     */
    public TeXFont getTeXFont() {
        return tf;
    }
    
    /**
     * Resets the color settings.
     *
     */
    public void reset() {
        color = null;
        background = null;
    }
    
    /**
     *
     * @return a copy of the environment, but with the style changed for roots
     */
    public TeXEnvironment rootStyle() {
        TeXEnvironment s = copy();
        s.style = TeXConstants.STYLE_SCRIPT_SCRIPT;
        return s;
    }
    
    /**
     *
     * @param c the background color to be set
     */
    public void setBackground(Color c) {
        background = c;
    }
    
    /**
     *
     * @param c the foreground color to be set
     */
    public void setColor(Color c) {
        color = c;
    }
    
    /**
     * 得到此 env 的拷贝, 设置以下标的显式形式(style)
     * 
     * 根据 TeXBook 第17章(17.4之后)的说明, 有如下关系:
     *  in a formula of style       the subscript style is
     *  (现有公式的显式形式)           (则下标的显式样式)
     *   D=0,T=2                     S'=5
     *   D'=1,T'=3                   S'=5
     *   S=4,SS=6                    SS'=7
     *   S'=5,SS'=7                  SS'=7
     *   
     * @return a copy of the environment, but in subscript(下标) style.
     */
    public TeXEnvironment subStyle() {
        TeXEnvironment s = copy();
        s.style = 2 * (style / 4) + 4 + 1;
        return s;
    }
    
    /**
     * 得到此 env 的拷贝, 但是是以上标的显式形式(style)
     * 
     * 根据 TeXBook 第17章(17.4之后)的说明, 有如下关系:
     *  in a formula of style       the superscript style is
     *  (现有公式的显式形式)           (则上标的显式样式)
     *    D=0,T=2                   S=4
     *    D'=1,T'=3                 S'=5
     *    S=4,SS=6                  SS=6
     *    S'=5,SS'=7                SS'=7
	 * 此函数中的公式算出的值即如上表所示. 只是不好理解, 不如写成如上表格形式.
     * @return a copy of the environment, but in superscript(上标) style.
     */
    public TeXEnvironment supStyle() {
        TeXEnvironment s = copy();
        s.style = 2 * (style / 4) + 4 + (style % 2);
        return s;
    }
    
    public float getSpace() {
        return tf.getSpace(style);
    }
    
    public void setLastFontId(int id) {
        lastFontId = id;
    }
    
    public int getLastFontId() {
        // if there was no last font id (whitespace boxes only), use default "mu font"
        return (lastFontId == TeXFont.NO_FONT ? tf.getMuFontId() : lastFontId);
    }
}
