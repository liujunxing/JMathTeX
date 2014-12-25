/* DefaultTeXFontParser.java
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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
//import java.util.logging.Level;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import be.ugent.caagt.jmathtex.ex.ResourceParseException;
import be.ugent.caagt.jmathtex.ex.XMLResourceParseException;

/**
 * Parses the font information from an XML-file.
 */
public class DefaultTeXFontParser {
	public static final Logger logger = Logger.getLogger(DefaultTeXFontParser.class.getName());
    
    /**
     * Number of font ids in a single font description file.
     */
    private static final int NUMBER_OF_FONT_IDS = 4;
    
    /** <Char> 节点下子节点解析器要实现的接口定义. */
    private static interface CharChildParser { // NOPMD; ??  PMD 什么意思.
    	/**
    	 * @param ch - 一般用作字符索引
    	 * @param info -- 解析后结果放在里面
    	 */
        public void parse(Element el, char ch, FontInfo info) throws XMLResourceParseException;
    }
    
    /** <Char> 节点下 <Extension> 子节点解析器. 这应该是表示那种可组合(垂直拼装)符号. */
    private static class ExtensionParser implements CharChildParser {
        
        
        ExtensionParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        		throws ResourceParseException {
            int[] extensionChars = new int[4];
            // get required integer attributes; 例子: <Extension rep="62" bot="58" top="57" />
            extensionChars[DefaultTeXFont.REP] = DefaultTeXFontParser
                    .getIntAndCheck("rep", el); // 属性 rep ?什么意思, 应该是重复次数.
            // get optional integer attributes; 另有可选属性 top, mid, bot
            extensionChars[DefaultTeXFont.TOP] = DefaultTeXFontParser
                    .getOptionalInt("top", el, DefaultTeXFont.NONE);
            extensionChars[DefaultTeXFont.MID] = DefaultTeXFontParser
                    .getOptionalInt("mid", el, DefaultTeXFont.NONE);
            extensionChars[DefaultTeXFont.BOT] = DefaultTeXFontParser
                    .getOptionalInt("bot", el, DefaultTeXFont.NONE);
            
            // parsing OK, add extension info
            info.setExtension(ch, extensionChars);
        }
    }
    
    /** <Char> 节点下 <Kern> 子节点解析器. 应该是表示 字距调整. */
    private static class KernParser implements CharChildParser {
        
        KernParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        		throws ResourceParseException {
            // get required integer attribute; 例子 <Kern code="63" val="0.1111112" />
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            // get required float attribute; 调整值.
            float kernAmount = DefaultTeXFontParser.getFloatAndCheck("val", el);
            
            // parsing OK, add kern info
            info.addKern(ch, (char) code, kernAmount);
        }
    }
    
    /** <Char> 节点下 <Lig> 子节点解析器. 应表示 连排. */
    private static class LigParser implements CharChildParser {
        
        LigParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            // get required integer attributes
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            int ligCode = DefaultTeXFontParser.getIntAndCheck("ligCode", el);
            
            // parsing OK, add ligature info
            info.addLigature(ch, (char) code, (char) ligCode);
        }
    }
    
    /** <Char> 下面 <NextLarger> 子节点 */
    private static class NextLargerParser implements CharChildParser {
        
        NextLargerParser() {
            // avoid generation of access class
        }
        
        public void parse(Element el, char ch, FontInfo info)
        throws ResourceParseException {
            // get required integer attributes; 例子: <NextLarger fontId=2 code=177 />
            int fontId = DefaultTeXFontParser.getIntAndCheck("fontId", el);
            int code = DefaultTeXFontParser.getIntAndCheck("code", el);
            
            // parsing OK, add "next larger" info
            info.setNextLarger(ch, (char) code, fontId);
        }
    }
    
    /** 此配置文件在外面的文件名. */
    public static final String RESOURCE_NAME = "DefaultTeXFont.xml";
    
    /** 元素名字 */
    public static final String STYLE_MAPPING_EL = "TextStyleMapping";
    public static final String SYMBOL_MAPPING_EL = "SymbolMapping";
    public static final String GEN_SET_EL = "GeneralSettings";
    public static final String MUFONTID_ATTR = "mufontid";
    public static final String SPACEFONTID_ATTR = "spacefontid";
    
    /** 从名字到数字的映射, 如 "mathrm" => 0 等. */
    private static Map<String, Integer> rangeTypeMappings = new HashMap<String, Integer>();
    
    /** 解析 <Char> 下面的4种子节点 (<NextLarger>, <Kern>, <Lig>, <Extension> 的处理器的映射, 从名字到处理器的映射) */
    private static Map<String, CharChildParser>
            charChildParsers = new HashMap<String, CharChildParser>();
    
    private Map<String, CharFont[]> parsedTextStyles;
    
    private Element root;
    
    static {
        // string-to-constant mappings; 从字符串映射到常量.(当前是 numbers=>0, capitals=>1, small=>2)
        setRangeTypeMappings();
        // parsers for the child elements of a "Char"-element
        setCharChildParsers();
    }
    
    public DefaultTeXFontParser() throws ResourceParseException {
        try {
            root = new SAXBuilder().build(
                    DefaultTeXFontParser.class.getResourceAsStream(RESOURCE_NAME)
                    ).getRootElement();
            
            // 先解析 textstyles 在其它项目之前, 因为在解析缺省 text style 时会使用.
            // parse textstyles ahead of the rest, because it's used while
            // parsing the default text style
            parsedTextStyles = parseStyleMappings();
            //logger.log(Level.INFO, "加载 <TextStyleMappings> 结果为: " + parsedTextStyles.toString());
        } catch (JDOMException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        } catch (IOException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    private static void setCharChildParsers() {
        charChildParsers.put("Kern", new KernParser());
        charChildParsers.put("Lig", new LigParser());
        charChildParsers.put("NextLarger", new NextLargerParser());
        charChildParsers.put("Extension", new ExtensionParser());
    }
    
    public FontInfo[] parseFontDescriptions() throws ResourceParseException {
        FontInfo[] res = new FontInfo[NUMBER_OF_FONT_IDS]; // NUMBER_OF_FONT_IDS=4 表示现在有4种字体: "cmmi10", "cmr10", "cmex10", "cmsy10"
        Element fontDescriptions = root.getChild("FontDescriptions");
        if (fontDescriptions != null) { // element present
            // iterate all "Font"-elements; 遍历下面的所有 <Font> 元素.
            for (Object obj : fontDescriptions.getChildren("Font")) {
                Element font = (Element) obj;
                // get required string attribute; 得到字体名, 例子 <Font name="cmmi10.ttf" ... >
                String fontName = getAttrValueAndCheckIfNotNull("name", font);
                // get required integer attribute; 字体标识(?fontId), 例子: <Font name="cmmi10.ttf" id="0">
                int fontId = getIntAndCheck("id", font);
                if (fontId < 0) // id must be greater than 0!!
                    throw new XMLResourceParseException(RESOURCE_NAME, "Font", "id",
                            "must have a positive integer value!");
                // get required real attributes; 其它属性有 <Font quad skewChar xHeight space
                float space = getFloatAndCheck("space", font);
                float xHeight = getFloatAndCheck("xHeight", font);
                float quad = getFloatAndCheck("quad", font);
                
                // get optional integer attribute; 可选 skewChar属性. 例如 cmr10 没有此属性.
                int skewChar = getOptionalInt("skewChar", font, -1);
                
                // try reading the font; 加载改字体到 java 中. 使用 awt.Font 对象.
                Font f = createFont(fontName);
                //logger.log(Level.INFO, "加载字体 " + fontName + " 结果为: " + f.toString());
                // create FontInfo-object; 创建 jmathtex.FontInfo 对象, 其包装对字体信息的认识.
                FontInfo info = new FontInfo(fontId, f, xHeight, space, quad);
                if (skewChar != -1) // attribute set
                    info.setSkewChar((char) skewChar);
                
                // process all "Char"-elements; 读取所有的 <Char> 子节点(在 <Font> 节点下).
                for (Object object : font.getChildren("Char"))
                    processCharElement((Element) object, info);
                
                // parsing OK, add to table
                if (res[fontId] == null) {
                    res[fontId] = info;
                    //logger.log(Level.INFO, "加载 <Font> 结果为: " + info.toString());
                }
                else
                    throw new XMLResourceParseException(RESOURCE_NAME, "Font", "id",
                            "occurs more than once");
            }
        }
        return res;
    }
    
    /** 处理 <FontDescriptions> 下面 <Font> 下面 <Char> 子节点. */
    private static void processCharElement(Element charElement, FontInfo info)
    throws ResourceParseException {
        // retrieve required integer attribute; 获取 code 属性, 表示此字符代码值.
        char ch = (char) getIntAndCheck("code", charElement);
        // retrieve optional float attributes; 尺寸信息包括: width(宽度), height(高度), depth(深度), italic(倾斜调整值)
        float[] metrics = new float[4];
        metrics[DefaultTeXFont.WIDTH] = getOptionalFloat("width", charElement, 0);
        metrics[DefaultTeXFont.HEIGHT] = getOptionalFloat("height", charElement,
                0);
        metrics[DefaultTeXFont.DEPTH] = getOptionalFloat("depth", charElement, 0);
        metrics[DefaultTeXFont.IT] = getOptionalFloat("italic", charElement, 0);
        // set metrics
        info.setMetrics(ch, metrics);
        
        // process children; 处理 <Char> 的子节点: <Kern>, <Lig>, <Extension>, <NextLarger> 
        for (Object obj : charElement.getChildren()) {
            Element el = (Element)obj;
            Object parser = charChildParsers.get(el.getName());
            if (parser == null) // unknown element
                throw new XMLResourceParseException(RESOURCE_NAME
                        + ": a <Char>-element has an unknown childelement '"
                        + el.getName() + "'!");
            else
                // process the child element
                ((CharChildParser) parser).parse(el, ch, info);
        }
    }
    
    /** 根据指定名字(例子 name="cmmi10.ttf") 创建字体? */
    private Font createFont(String name) throws ResourceParseException {
        InputStream fontIn = null;
        try {
            fontIn = DefaultTeXFontParser.class.getResourceAsStream(name);
            return Font.createFont(java.awt.Font.TRUETYPE_FONT, fontIn);
        } catch (Exception e) {
            throw new XMLResourceParseException(RESOURCE_NAME
                    + ": error reading font '" + name + "'. Error message: "
                    + e.getMessage());
        } finally {
            try {
                if (fontIn != null)
                    fontIn.close();
            } catch (IOException ioex) {
                throw new RuntimeException("Close threw exception", ioex);
            }
        }
    }
    
    public Map<String,CharFont> parseSymbolMappings() throws ResourceParseException {
        Map<String,CharFont> res = new HashMap<String,CharFont>();
        Element symbolMappings = root.getChild("SymbolMappings"); // 节点 <SymbolMappings>
        if (symbolMappings == null)
            // "SymbolMappings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "SymbolMappings");
        else { // element present
            // iterate all mappings; 枚举所有 <SymbolMapping> 子节点. 例子 <SymbolMapping name="comma" fontId="0" ch="59" />
            for (Object obj : symbolMappings.getChildren(SYMBOL_MAPPING_EL)) {
                Element mapping = (Element) obj;
                // get string attribute; 名字, 如 "comma" => 映射到 CharFont{ch=59, fontId=0}
                String symbolName = getAttrValueAndCheckIfNotNull("name", mapping);
                // get integer attributes; 得到 ch 属性和 fontId 属性; ?? fontId 表示什么意思呢? 也许需要看 <<TeX 原本>>
                int ch = getIntAndCheck("ch", mapping), fontId = getIntAndCheck(
                        "fontId", mapping);
                // put mapping in table; 放入 name => CharFont 映射表
                res.put(symbolName, new CharFont((char) ch, fontId));
            }
        }
        
        // "sqrt" must allways be present (used internally only!)
        if (res.get("sqrt") == null)
            throw new XMLResourceParseException(
                    RESOURCE_NAME
                    + ": the required mapping <SymbolMap name=\"sqrt\" ... /> is not found!");
        else {
        	//logger.log(Level.INFO, "加载 <SymbolMappings> 结果为: " + res.toString());
            // parsing OK
            return res;
        }
    }
    
    public String[] parseDefaultTextStyleMappings()
    throws ResourceParseException {
        String[] res = new String[3];
        Element defaultTextStyleMappings = root
                .getChild("DefaultTextStyleMapping");
        if (defaultTextStyleMappings == null)
            // "DefaultTextStyleMappings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME,
                    "DefaultTextStyleMapping");
        else { // element present
            // iterate all mappings; 节点 <DefaultTextStyleMapping> 下面有多个 <MapStyle> 子节点.
            for (Object obj : defaultTextStyleMappings.getChildren("MapStyle")) {
                Element mapping = (Element) obj;
                // get range name and check if it's valid; 例子: <MapStyle code="numbers" textStyle="mathrm" />
                String code = getAttrValueAndCheckIfNotNull("code", mapping);
                Object codeMapping = rangeTypeMappings.get(code);
                if (codeMapping == null) // unknown range name
                    throw new XMLResourceParseException(RESOURCE_NAME, "MapStyle",
                            "code", "contains an unknown \"range name\" '" + code
                            + "'!");
                // get mapped style and check if it exists
                String textStyleName = getAttrValueAndCheckIfNotNull("textStyle",
                        mapping);
                Object styleMapping = parsedTextStyles.get(textStyleName);
                if (styleMapping == null) // unknown text style
                    throw new XMLResourceParseException(RESOURCE_NAME, "MapStyle",
                            "textStyle", "contains an unknown text style '"
                            + textStyleName + "'!");
                // now check if the range is defined within the mapped text style
                CharFont[] charFonts = (CharFont[]) parsedTextStyles
                        .get(textStyleName);
                int index = ((Integer) codeMapping).intValue();
                if (charFonts[index] == null) // range not defined
                    throw new XMLResourceParseException(RESOURCE_NAME
                            + ": the default text style mapping '" + textStyleName
                            + "' for the range '" + code
                            + "' contains no mapping for that range!");
                else
                    // everything OK, put mapping in table
                    res[index] = textStyleName;
            }
        }
        //logger.log(Level.INFO, "加载 <DefaultTextStyleMapping> 结果为: " + res.toString());
        return res;
    }
    
    public Map<String,Float> parseParameters() throws ResourceParseException {
        Map<String,Float> res = new HashMap<String,Float>();
        Element parameters = root.getChild("Parameters"); // <Parameters> 子节点, 其有很多参数(属性). 
        if (parameters == null)
            // "Parameters" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "Parameters");
        else { // element present
            // iterate all attributes
            for (Object obj : parameters.getAttributes()) {
                String name = ((Attribute) obj).getName();
                // set float value (if valid); 这些属性都当做浮点数(float)看待. 如 bigopspacing5=0.1, num1=0.676508; 这些神奇的数字可能来自于 TeX
                res.put(name, new Float(getFloatAndCheck(name, parameters)));
            }
            //logger.log(Level.INFO, "加载 <Parameters> 结果 Map 为:" + res.toString());
            return res;
        }
    }
    
    public Map<String,Number> parseGeneralSettings() throws ResourceParseException {
        Map <String,Number>res = new HashMap<String,Number>();
        // TODO: must this be 'Number' ?  节点 <GeneralSettings> 当前看到都是数字, 也许有别的??
        Element generalSettings = root.getChild("GeneralSettings");
        if (generalSettings == null)
            // "GeneralSettings" is required!
            throw new XMLResourceParseException(RESOURCE_NAME, "GeneralSettings");
        else { // element present
            // set required int values (if valid); 例子: 属性 mufontid="3", spacefontid="1"
            res.put(MUFONTID_ATTR, getIntAndCheck(MUFONTID_ATTR,
                    generalSettings)); // autoboxing
            res.put(SPACEFONTID_ATTR, getIntAndCheck(SPACEFONTID_ATTR,
                    generalSettings)); // autoboxing
            // set required float values (if valid); 例子: scriptfactor="0.7", scriptscriptfactor="0.5"
            res.put("scriptfactor", getFloatAndCheck("scriptfactor",
                    generalSettings)); // autoboxing
            res.put("scriptscriptfactor", getFloatAndCheck(
                    "scriptscriptfactor", generalSettings)); // autoboxing
            
        }
        //logger.log(Level.INFO, "加载 <GeneralSettings> 结果为: " + res);
        return res;
    }
    
    public Map<String,CharFont[]> parseTextStyleMappings() {
        return parsedTextStyles;
    }
    
    /**
     * 解析 {TextStyleMappings} 项, 下面含多个 {TextStyleMapping name='mathrm'} 等;
     *   每个 {TextStyleMapping} 下面有 {MapRange start=xx fontId=xx code=xxx}
     * @return 从 TextStyle 名字(如 mathrm,mathit,mathcal) 到 CharFont[] 的映射. (和文件中 TextStyleMappings 节点下内容一致)
     * @throws ResourceParseException
     */
    @SuppressWarnings("unchecked")
	private Map<String,CharFont[]> parseStyleMappings() throws ResourceParseException {
        Map<String,CharFont[]> res = new HashMap<String,CharFont[]>();
        Element textStyleMappings = root.getChild("TextStyleMappings"); // 得到 <TextStyleMappings> 节点
        if (textStyleMappings == null)
            // "TextStyleMappings" is required! 必须要有!
            throw new XMLResourceParseException(RESOURCE_NAME, "TextStyleMappings");
        else { // element present
            // iterate all mappings; 枚举 <TextStyleMappings> 的 <TextStyleMapping> 子元素
            for (Object obj : textStyleMappings.getChildren(STYLE_MAPPING_EL)) {
                Element mapping = (Element) obj; // ?可能不是 element 的?
                // get required string attribute. 例子: <TextStyleMapping name="mathrm">
                String textStyleName = getAttrValueAndCheckIfNotNull("name",
                        mapping); // 例子: textStyleName="mathrm"
                List mapRangeList = mapping.getChildren("MapRange"); // 下面含 <MapRange> 子节点.
                // iterate all mapping ranges. 枚举所有 <MapRange> 子节点.
                CharFont[] charFonts = new CharFont[3]; // 下面最多 3 个 <MapRange> 子节点??
                for (Object object : mapRangeList) {
                    Element mapRange = (Element) object; // 例子: 节点 <MapRange start="48" fontId="1" code="numbers" />
                    // get required integer attributes
                    int fontId = getIntAndCheck("fontId", mapRange); // fontId 属性. 如果没有则抛出异常!
                    int ch = getIntAndCheck("start", mapRange); // start 属性, 似乎是字符位置.如 48为数字0,65为字母'A',97为字母'a'
                    // get required string attribute and check if it's a known range
                    String code = getAttrValueAndCheckIfNotNull("code", mapRange); // 48="numbers", 65="capitals", 97="small"
                    Object codeMapping = rangeTypeMappings.get(code); // 字符串 "numbers" 等映射为内部数字表示. 当前只有 0,1,2 三种值.
                    if (codeMapping == null)
                        throw new XMLResourceParseException(RESOURCE_NAME,
                                "MapRange", "code",
                                "contains an unknown \"range name\" '" + code + "'!");
                    else // 这里数组索引值可能越界!
                        charFonts[((Integer) codeMapping).intValue()] = new CharFont(
                                (char) ch, fontId); // 例如 CharFont{'0', 1}, CharFont{'A', 1} 等
                }
                res.put(textStyleName, charFonts);
            }
        }
        return res;
    }
    
    private static void setRangeTypeMappings() {
        rangeTypeMappings.put("numbers", DefaultTeXFont.NUMBERS); // autoboxing
        rangeTypeMappings.put("capitals", DefaultTeXFont.CAPITALS); // autoboxing
        rangeTypeMappings.put("small", DefaultTeXFont.SMALL); // autoboxing
    }
    
    private static String getAttrValueAndCheckIfNotNull(String attrName,
            Element element) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null)
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, null);
        return attrValue;
    }
    
    // 为什么是 public 的? 别处有使用?
    public static float getFloatAndCheck(String attrName, Element element)
    throws ResourceParseException {
        String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);
        
        // try parsing string to float value
        float res = 0;
        try {
            res = (float) Double.parseDouble(attrValue);
        } catch (NumberFormatException e) {
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, "has an invalid real value!");
        }
        // parsing OK
        return res;
    }
    
    public static int getIntAndCheck(String attrName, Element element)
    throws ResourceParseException {
        String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);
        
        // try parsing string to integer value
        int res = 0;
        try {
            res = Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, "has an invalid integer value!");
        }
        // parsing OK
        return res;
    }
    
    public static int getOptionalInt(String attrName, Element element,
            int defaultValue) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null) // attribute not present
            return defaultValue;
        else {
            // try parsing string to integer value
            int res = 0;
            try {
                res = Integer.parseInt(attrValue);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, element
                        .getName(), attrName, "has an invalid integer value!");
            }
            // parsing OK
            return res;
        }
    }
    
    public static float getOptionalFloat(String attrName, Element element,
            float defaultValue) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null) // attribute not present
            return defaultValue;
        else {
            // try parsing string to float value
            float res = 0;
            try {
                res = (float) Double.parseDouble(attrValue);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, element
                        .getName(), attrName, "has an invalid float value!");
            }
            // parsing OK
            return res;
        }
    }
}
