/* DefaultTeXFont.java
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
import java.util.Map;

import be.ugent.caagt.jmathtex.ex.SymbolMappingNotFoundException;
import be.ugent.caagt.jmathtex.ex.TextStyleMappingNotFoundException;
import be.ugent.caagt.jmathtex.ex.XMLResourceParseException;

/**
 * 夫 TeXFont 接口的缺省实现. 所有的字体信息从 xml 文件中加载.
 * The default implementation of the TeXFont-interface. All font information is read
 * from an xml-file.
 */
public class DefaultTeXFont implements TeXFont {
    
	/** 
	 * 从 NUMBER,CAPITALS,SMALL 索引到字体样式 "mathrm", "mathit", "mathit" 的映射表格.
	 * 一般的, 数字在数学公式中以 roman 格式出现, 字母是 italic 格式, 标点是 roman 的. (见TeXBook 第16章)
	 */
    private static String[] defaultTextStyleMappings;
    
    /**
     * No extension part for that kind (TOP,MID,REP or BOT)
     */
    protected static final int NONE = -1;
    
    /** =0, 对应(配置的)名为 "numbers", 表示 char 字符中的数字 0..9, 其选用字体 cmr(正常罗马字体) */
    protected final static int NUMBERS = 0;  // 数字 0..9
    /** =1, 对应(配置的)名为 "capitals", 表示 char 字符中的大写字母 A..Z, 一般用字体 cmmi(数学斜体) */
    protected final static int CAPITALS = 1; // 大写字母 A..Z
    /** =2, 对应(配置的)名为 "small", 表示 char 字符中的小写字符 a..z, 一般用字体 cmmi(数学斜体) */
    protected final static int SMALL = 2;    // 小写字母 a..z
    
    /** 
     * 从 text style 名字(如 mathrm, mathit, mathcal) 到对应的 CharFont[3] 的映射. 
     *   其中 CharFont[3] 分别为 NUMBERS,CAPITALS,SMALL 索引的 CharFont.
     * 语义上, 是什么含义呢?? 
     * 在 <cinit> 中调用 parser.parseTextStyleMappings() 进行的初始化. 
     */
    private static Map<String, CharFont[]> textStyleMappings;
    
    /**
     * 从字符的英文名字(如 + 的名字为 plus) 到其 CharFont(含char,fontid) 的映射.
     * 在 <cinit> 中调用 parser.parseSymbolMappings() 进行的初始化. 
     */
    private static Map<String, CharFont>  symbolMappings;
    
    /**
     * 每个字体一个 FontInfo, 存放所有字体的信息. 当前有 cmmi10, cmr10, cmex10, cmsy10
     * 都是 ttf 字体.
     */
    private static FontInfo[] fontInfo;
    
    /**
     * 在 TeX 算法中使用的通用参数, 特定为 cm 字体族. 
     * 在配置文件 DefaultTeXFont.xml 中 <Parameters> 中配置, 当前有:<pre>
     *   bigopspacing5=0.1
     *   bigopspacing4=0.6
     *   bigopspacing3=0.2
     *   bigopspacing2=0.166667
     *   bigopspacing1=0.111112
     *   defaultrulethickness=0.039999
     *   axisheight=0.25
     *   subdrop=0.05
     *   supdrop=0.386108
     *   sub2=0.247217
     *   sub1=0.15
     *   sup3=0.288889
     *   sup2=0.362892
     *   sup1=0.412892
     *   denom2=0.344841
     *   denom1=0.685951
     *   num3=0.443731
     *   num2=0.393732
     *   num1=0.676508 </pre>
     *     
     * general parameters used in the TeX algorithms, specific for the computer
     * modern font family.
     */
    private static Map<String, Float> parameters;
    
    /**
     * 一般设置. 当前有:<pre>
     *   scriptscriptfactor=0.5
     *   scriptfactor=0.7
     *   spacefontid=1
     *   mufontid=3 </pre>
     *   
     * general settings
     */
    private static Map<String, Number> generalSettings;
    
    protected static final int TOP = 0, MID = 1, REP = 2, BOT = 3;
    
    protected static final int WIDTH = 0, HEIGHT = 1, DEPTH = 2, IT = 3;
    
    static {
        DefaultTeXFontParser parser = new DefaultTeXFontParser();
        // general font parameters; 节点 <Parameters>
        parameters = parser.parseParameters();
        // general settings; 节点 <GeneralSettings>
        generalSettings = parser.parseGeneralSettings();
        // text style mappings; 节点 <TextStyleMappings>, 在构造的时候已经加载了, 其实可以放在这里加载??
        textStyleMappings = parser.parseTextStyleMappings();
        // default text style : style mappings; 节点 <DefaultTextStyleMapping>
        defaultTextStyleMappings = parser.parseDefaultTextStyleMappings();
        // symbol mappings; 节点 <SymbolMappings> 
        symbolMappings = parser.parseSymbolMappings();
        // fonts + font descriptions; 节点 <FontDescriptions>, 内含几个 <Font> 节点.
        fontInfo = parser.parseFontDescriptions();
        
        // check if mufontid exists
        int muFontId = generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR).intValue();
        if (muFontId < 0 || muFontId >= fontInfo.length || fontInfo[muFontId] == null)
            throw new XMLResourceParseException(
                    DefaultTeXFontParser.RESOURCE_NAME,
                    DefaultTeXFontParser.GEN_SET_EL,
                    DefaultTeXFontParser.MUFONTID_ATTR,
                    "contains an unknown font id!");
    }
    
    /**
     * 在构造的时候给出, 或使用 deriveFont() 产生一个.
     */
    private final float size; // standard size
    
    public DefaultTeXFont(float pointSize) {
        size = pointSize;
    }
    
    public TeXFont deriveFont(float size) {
        return new DefaultTeXFont(size);
    }
    
    /**
     * 得到轴的高度? axisheight 现在配置为 0.25; 乘以大小比例因子.
     * @param style - display style 显示形式. 根据配置, 以及 style 对应的比例
     */
    public float getAxisHeight(int style) {
        return getParameter("axisheight") * getSizeFactor(style)
        		* PIXELS_PER_POINT;
    }
    
    
    /**
     * 得到参数中配置的 bigopspacing1 参数(当前配置为=0.111112), 乘以某些值.
     * 这里使用了 PIXELS_PER_POINT, 可能只是为屏幕显示用吗???
     * bigop 的这种参数由1-5 共5个, 可能是不同情况用的.
     * 
     * 另外就是都乘上了 factor, ppp. 也就是按(字体)比例的.
     */
    public float getBigOpSpacing1(int style) {
        return getParameter("bigopspacing1") * getSizeFactor(style)
        		* PIXELS_PER_POINT;
    }
    
    public float getBigOpSpacing2(int style) {
        return getParameter("bigopspacing2") * getSizeFactor(style)
        * PIXELS_PER_POINT;
    }
    
    public float getBigOpSpacing3(int style) {
        return getParameter("bigopspacing3") * getSizeFactor(style)
        * PIXELS_PER_POINT;
    }
    
    public float getBigOpSpacing4(int style) {
        return getParameter("bigopspacing4") * getSizeFactor(style)
        * PIXELS_PER_POINT;
    }
    
    public float getBigOpSpacing5(int style) {
        return getParameter("bigopspacing5") * getSizeFactor(style)
        * PIXELS_PER_POINT;
    }
    
    /*
     * getChar 的几个调用关系, 如下列表示:
     * 
     *   getChar3 => getChar_internal => getChar2 (还有别的分支,暂略)
     *   getChar2 直接构造一个 Char 对象, 不再调入别的函数.
     *   getChar1 => getChar2
     *   
     * 1. 根据此表, getChar2 参数为 (CharFont, style); 因而, 实际上本质是, 
     *   通过 CharFont 得到字符位置和字体索引, style 得到字尺寸, 以合成为 Char-对象所需信息.
     * 2. 而 getChar1(symbolName, style) 是通过 symbolName到CharFont 的映射得到 CharFont,
     *   从而变成 getChar2() 所需参数.
     * 3. getChar_internal(c, CharFont[], style) 通过 c 计算出其字符类(character class),
     *   然后查 CharFont[] 得到该类第一个字符的 CharFont, 加上偏移得到实际 CharFont
     *   从而变成 getChar2() 所需参数.
     * 4. getChar3(c, textStyle_name, style) 通过 textStyleMappings 
     *   根据 textStyle_name 得到 CharFont[], 变成 getChar_internal() 所需参数. 
     */
    
    /**
     * 根据 textStyle_name 可以得到 CharFont[] 的映射. 指明该字体形式中, 字母/数字所用的字体(fontId) 
     *   和位置(start+offset).
     * 从 getDefaultChar() 调用过来. 
     * 
     * Get a Char-object specifying the given character in the given text style with
     * metric information depending on the given "style".
     * 
     * @param textStyle_name - 夫 textStyle name(文本样式名), 当前取值为 mathrm, mathit, mathcal.
     *    the text style in which the character should be drawn
     * 
     */
    public Char getChar3(char c, String textStyle_name, int style) throws TextStyleMappingNotFoundException {
        CharFont[] mapping = textStyleMappings.get(textStyle_name);
        if (mapping == null) // text style mapping not found
            throw new TextStyleMappingNotFoundException(textStyle_name);
        
        // 调入 getChar() 的另一个重载版本.
        return getChar_internal(c, mapping, style);
    }
    
    /**
     * 已知 cf={char, fontid}, 以及 display style; 得到包含了系统字体, 字尺寸信息的 Char-对象.
     * 
     * 根据 cf 可以得到对应的 fontInfo, 从而得到 font, metrics 
     * 根据 style 可以得到字尺寸, 和 font 合成之后得到 deriveFont, 其具有所需尺寸
     *   (用于绘制, 不用于计算公式尺寸).
     * 
     * 然后合成为 Char 对象. 
     */
    public Char getChar2(CharFont cf, int style) {
    	// 字尺寸信息, 根据 display style 得到. (比例因子为 1, 0.7, 0.5 几种)
        float size = getSizeFactor(style);
        // cf.fontId 为指向 fontInfo[] 的索引.
        FontInfo info = fontInfo[cf.fontId];
        Font font = info.getFont();
        
        // 这里给出的 Char 里面的 fontCode 就是 CharFont(cf) 里面的 fontId. 为什么不用相同的名字呢???
        return new Char(cf.c, font.deriveFont(size), cf.fontId,
        		getMetrics(cf, size));
    }
 
    /**
     * 得到指定 symbolName 的符号在 style 下对应的 Char-对象.
     */
    public Char getChar1(String symbolName, int style) throws SymbolMappingNotFoundException {
    	// 从 symbolMappings 中找到该名字对应的 {ch,fontid} 对.
        CharFont cf = symbolMappings.get(symbolName);
        if (cf == null) // no symbol mapping found!
            throw new SymbolMappingNotFoundException(symbolName);
        else
            return getChar2(cf, style);
    }

    
    /**
     * getChar() 的有多个重载版本. (不好分辨)
     * 1. 根据 c 的范围, 计算出其 Character Class(0..2)
     * 2. cf[c] 如果存在, 则计算出 ch, fontId 调入重载版本的 getChar()
     * 
     * @param c
     * @param cf
     * @param style
     * @return
     */
    private Char getChar_internal(char c, CharFont[] cf, int style) {
        int kind, offset;
        if (c >= '0' && c <= '9') { // 不烦恼吗? 这里又计算一遍 cc, 当然也计算 offset 了.
            kind = NUMBERS;
            offset = c - '0';
        } else if (c >= 'a' && c <= 'z') {
            kind = SMALL;
            offset = c - 'a';
        } else {
            kind = CAPITALS;
            offset = c - 'A';
        }
        
        // 如果没有该字符对应的设置(MapRange), 则使用缺省配置的 <DefaultTextStyleMapping>, 缺省的总是有的.
        // if the mapping for the character's range, then use the default style
        if (cf[kind] == null)
            return getDefaultChar(c, style);
        else {
        	// c2 表示字符 c 在字体 fontId 中的实际位置. (一般和 c 相同)
        	char c2 = (char) (cf[kind].c + offset);
        	// cfnt 表示 {c2, fontid}, 即字符和字体标识的组合.
        	CharFont cfnt = new CharFont(c2, cf[kind].fontId);
        	// 又调入一层, 很讨厌吧.
            return getChar2(cfnt, style);
        }
    }
    
   
    /**
     * 计算字符 c 的所属字符类(Character Class).
     * 字符 c 是 0..9, 则字符类 cc 为 NUMBERS(=0).
     *       是 A..Z, 则字符类 cc 为 CAPITALS(=1).
     *       是 a..z, 则字符类 cc 为 SMALL(=2).
     * @param c
     * @return 字符 c 所属的字符类. -1 表示不属于所知字符类.
     */
    public static int getCharClass(char c) {
    	if (c >= '0' && c <= '9')
    		return NUMBERS;
    	else if (c >= 'a' && c <= 'z')
    		return SMALL;
    	else if (c >= 'A' && c <= 'Z')
    		return CAPITALS;
    	else
    		return -1;
    }
    
    /**
     * 根据 c, style 得到包含了字体, 字大小信息的 Char-对象. (见接口定义处注释)
     * 
     * @param c - 只能是字母,数字.
     * @param style - 显示样式, 如 D, T', SS 等.
     */
    public Char getDefaultChar(char c, int style) {
    	int cc; // 字符 c 的 Character Class.(字符类)
    	cc = getCharClass(c);
    	
    	// 缺省的字体样式(text style, 配置在节点 <DefaultTextStyleMapping>, 其总是存在的.
        // these default text style mappings will allways exist,
        // because it's checked during parsing
    	String tstyle_name = defaultTextStyleMappings[cc]; // 得到名字. 如 'mathit'
        return getChar3(c, tstyle_name, style);
    }
    
    /**
     * 获得缺省的尺子(rule)的宽度(当前配置为=0.039999). (用于根式, 分式等?)
     */
    public float getDefaultRuleThickness(int style) {
    	// 首先得到 Parameters 中配置的 defaultrulethickness, 乘以 style 对应的大小比例,
    	// 再乘以点到像素的转换比例(用于屏幕显示??)
        return getParameter("defaultrulethickness") * getSizeFactor(style)
        * PIXELS_PER_POINT;
    }
    
    public float getDenom1(int style) {
        return getParameter("denom1") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getDenom2(int style) {
        return getParameter("denom2") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public Extension getExtension(Char c, int style) {
        Font f = c.getFont();
        int fc = c.getFontCode();
        float s = getSizeFactor(style);
        
        // construct Char for every part
        FontInfo info = fontInfo[fc];
        int[] ext = info.getExtension(c.getChar()); // 此字符的扩展信息, 如 rep=62,bot=58,top=56
        Char[] parts = new Char[ext.length]; // 各个部分.
        for (int i = 0; i < ext.length; i++) {
            if (ext[i] == NONE)
                parts[i] = null;
            else // 得到各个部分. 可能包括: top,mid,bot,rep.
                parts[i] = new Char((char) ext[i], f, fc, 
                		getMetrics(new CharFont((char) ext[i], fc), s));
        }
        
        // 使用各个部分, 构造 Extension 对象. 并返回.
        return new Extension(parts[TOP], parts[MID], parts[REP], parts[BOT]);
    }
    
    public float getKern(CharFont left, CharFont right, int style) {
        if (left.fontId == right.fontId){
            FontInfo info = fontInfo[left.fontId];
            return info.getKern(left.c, right.c, getSizeFactor(style)
            * PIXELS_PER_POINT);
        } else
            return 0;
    }
    
    public CharFont getLigature(CharFont left, CharFont right) {
        if (left.fontId == right.fontId) {
            FontInfo info =  fontInfo[left.fontId];
            return info.getLigature(left.c, right.c);
        } else
            return null;
    }
    
    private Metrics getMetrics(CharFont cf, float size) {
        FontInfo info = fontInfo[cf.fontId];
        float[] m = info.getMetrics(cf.c);
        return new Metrics(m[WIDTH], m[HEIGHT], m[DEPTH], m[IT], size
                * PIXELS_PER_POINT);
    }
    
    public int getMuFontId() {
        return generalSettings.get(DefaultTeXFontParser.MUFONTID_ATTR).intValue();
    }
    
    public Char getNextLarger(Char c, int style) {
        FontInfo info = fontInfo[c.getFontCode()];
        CharFont ch = info.getNextLarger(c.getChar());
        FontInfo newInfo = fontInfo[ch.fontId];
        return new Char(ch.c, newInfo.getFont().deriveFont(getSizeFactor(style)),
                ch.fontId, getMetrics(ch, getSizeFactor(style)));
    }
    
    public float getNum1(int style) {
        return getParameter("num1") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getNum2(int style) {
        return getParameter("num2") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getNum3(int style) {
        return getParameter("num3") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getQuad(int style, int fontCode) {
        FontInfo info = fontInfo[fontCode];
        return info.getQuad(getSizeFactor(style) * PIXELS_PER_POINT);
    }
    
    public float getSize() {
        return size;
    }
    
    public float getSkew(CharFont cf, int style) {
        FontInfo info = fontInfo[cf.fontId];
        char skew = info.getSkewChar();
        if (skew == -1)
            return 0;
        else
            return getKern(cf, new CharFont(skew, cf.fontId), style);
    }
    
    public float getSpace(int style) {
        int spaceFontId = generalSettings
                .get(DefaultTeXFontParser.SPACEFONTID_ATTR).intValue();
        FontInfo info = fontInfo[spaceFontId];
        return info.getSpace(getSizeFactor(style) * PIXELS_PER_POINT);
    }
    
    public float getSub1(int style) {
        return getParameter("sub1") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSub2(int style) {
        return getParameter("sub2") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSubDrop(int style) {
        return getParameter("subdrop") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSup1(int style) {
        return getParameter("sup1") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSup2(int style) {
        return getParameter("sup2") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSup3(int style) {
        return getParameter("sup3") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    public float getSupDrop(int style) {
        return getParameter("supdrop") * getSizeFactor(style) * PIXELS_PER_POINT;
    }
    
    /**
     * 得到指定字体(fontCode=fontId)的指定显示样式的 XHeight 配置高度.
     * 
     */
    public float getXHeight(int style, int fontCode) {
    	// 根据 fontCode(fontId) 得到该字体信息对象.
        FontInfo info = fontInfo[fontCode];
        // 得到其 XHeight*size_factor*... 转换比例.
        return info.getXHeight(getSizeFactor(style) * PIXELS_PER_POINT);
    }
    
    public boolean hasNextLarger(Char c) {
        FontInfo info = fontInfo[c.getFontCode()];
        return (info.getNextLarger(c.getChar()) != null);
    }
    
    public boolean hasSpace(int font) {
        FontInfo info = fontInfo[font];
        return info.hasSpace();
    }
    
    public boolean isExtensionChar(Char c) {
        FontInfo info = fontInfo[c.getFontCode()];
        return info.getExtension(c.getChar()) != null;
    }
    
    /**
     * 得到指定名字的参数. (浮点类型的)
     * @param parameterName
     * @return
     */
    private static float getParameter(String parameterName) {
        Object param = parameters.get(parameterName);
        if (param == null)
            return 0;
        else
            return ((Float) param).floatValue();
    }
    
    /**
     * 根据 style(显示样式) 得到 size factor; 参见 确定表 Table-17.4:
     * 根据显示样式:     大小比例(当前的配置值)
     *   D,D',T,T'    	1   (text size)
     *   S,S'         	0.7 (配置为 GeneralSettings scriptfactor=0.7)
     *   SS,SS'  		0.5 (配置为 GeneralSettings scriptscriptfactor=0.5)
     */
    public static float getSizeFactor(int style) {
        if (style < TeXConstants.STYLE_SCRIPT) // 是 D,D',T,T' 则 factor=1.0
            return 1;
        else if (style < TeXConstants.STYLE_SCRIPT_SCRIPT) // 是 S, S' 则 factor=0.7 
            return generalSettings.get("scriptfactor").floatValue();
        else 	// 是 SS, SS' 则 factor=0.5 
            return generalSettings.get("scriptscriptfactor").floatValue();
    }
    
    public void dump() {
    	System.out.println("TODO: DefaultTeXFont.dump()");
    }
}
