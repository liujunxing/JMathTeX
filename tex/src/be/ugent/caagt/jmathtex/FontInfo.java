/* FontInfo.java
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

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * 包含单个字体(font)的所有字体信息.
 * Contains all the font information for 1 font.
 */
class FontInfo {
    
    /**
     * 在 TeX 字体中最大的字符编码值. (为支持中文, 可能要改变一些东西)
     * Maximum number of character codes in a TeX font.
     */
    public static final int NUMBER_OF_CHAR_CODES = 256;
    
    /**
     * 表示一对字符, 表示 leftChar + rightChar; 可用在 Map 等数据结构中做为 key. 以表示 lig/kern 数据.
     */
    private static class CharCouple {
        
        private final char left, right;
        
        CharCouple(char l, char r) {
            left = l;
            right = r;
        }
        
        public boolean equals(Object o) {
            CharCouple lig = (CharCouple) o;
            return left == lig.left && right == lig.right;
        }
        
        public int hashCode() {
            return (left + right) % 128;
        }
    
        public String toString() {
        	return "CharCouple{left=" + left + ", right=" + right + "}";
        }
    }
    
    // ID; 字体标识.
    private final int fontId;
    
    // font; 系统字体对象.
    private final Font font;
    
    /** 尺寸信息; 每字符一个 float[4], 分别为 width,height,depth,italic 值. 最多支持 256 个. */
    private final float[][] metrics = new float[NUMBER_OF_CHAR_CODES][];
    
    /** 连排信息 */
    private final Map<CharCouple, Character> lig = new HashMap<CharCouple, Character>();
    
    /** 字距调整 */
    private final Map<CharCouple, Float> kern = new HashMap<CharCouple, Float>();
    
    /** 此字符(数学符号)的下一级更大的符号. */
    private final CharFont[] nextLarger = new CharFont[NUMBER_OF_CHAR_CODES];
    
    /** 一些符号(应主要是定界符)的扩展字符定义, 每个扩展可能包含 top,middle,bottom,rep 4 个组成部分. */
    private final int[][] extensions = new int[NUMBER_OF_CHAR_CODES][];
    
    // skew character of the font (used for positioning accents)
    private char skewChar = (char) -1;
    
    // general parameters for this font; 字体的一般参数. 从配置文件中加载.
    private final float xHeight; 
    private final float space;
    private final float quad;
    
    public FontInfo(int fontId, Font font, float xHeight, float space, float quad) {
        this.fontId = fontId;
        this.font = font;
        this.xHeight = xHeight;
        this.space = space;
        this.quad = quad;
    }
    
    /**
     * 添加一个新的 kern(字距调整) 条目.
     * @param left
     *           left character (左边的字符, 如 'W')
     * @param right
     *           right character (右边的字符, 如 'A'; 本例子中 WA 需要字距调整.)
     * @param k
     *           kern value (字距调整值)
     */
    public void addKern(char left, char right, float k) {
        kern.put(new CharCouple(left, right), new Float(k));
    }
    
    /**
     * 添加一个 ligature.
     * @param left
     *           left character (左边的字符, 如 'f')
     * @param right
     *           right character (右边的字符, 如 'i')
     * @param ligChar
     *           ligature to replace left and right character (连排之后的字符, 如 'fi' 成为一个字符)
     */
    public void addLigature(char left, char right, char ligChar) {
        lig.put(new CharCouple(left, right), new Character(ligChar));
    }
    
    public int[] getExtension(char ch) {
        return extensions[ch];
    }
    
    /** 得到 kern 信息 */
    public float getKern(char left, char right, float factor) {
        Object obj = kern.get(new CharCouple(left, right));
        if (obj == null)
            return 0;
        else
            return ((Float) obj).floatValue() * factor;
    }
    
    /** 得到 ligature 信息 */
    public CharFont getLigature(char left, char right) {
        Object obj = lig.get(new CharCouple(left, right));
        if (obj == null)
            return null;
        else
            return new CharFont(((Character) obj).charValue(), fontId);
    }
    
    /** 
     * 得到字符 c 的尺寸信息. 如果想支持中文, 可能还需要更多兼容处理! 
     */
    public float[] getMetrics(char c) {
        return metrics[c];
    }
    
    public CharFont getNextLarger(char ch) {
        return nextLarger[ch];
    }
    
    /**
     * 得到本字体的 quad 乘以 factor 的值. (factor 不应该当参数传递.)
     * @param factor
     * @return
     */
    public float getQuad(float factor) {
        return quad * factor;
    }
    
    /**
     * @return the skew character of the font (for the correct positioning of
     *         accents)
     */
    public char getSkewChar() {
        return skewChar;
    }
    
    public float getSpace(float factor) {
        return space * factor;
    }
    
    public float getXHeight(float factor) {
        return xHeight * factor;
    }
    
    public boolean hasSpace() {
        return space > TeXFormula.PREC;
    }
    
    public void setExtension(char ch, int[] ext) {
        extensions[ch] =  ext;
    }
    
    public void setMetrics(char c, float[] arr) {
        metrics[c] = arr;
    }
    
    public void setNextLarger(char ch, char larger, int fontLarger) {
        nextLarger[ch] = new CharFont(larger, fontLarger);
    }
    
    public void setSkewChar(char c) {
        skewChar = c;
    }
    
    public int getId() {
        return fontId;
    }
    
    public Font getFont() {
        return font;
    }

    @Override
    public String toString() {
    	return "FontInfo{fontId=" + this.fontId + ", font=" + this.font + "}";
    }
}
