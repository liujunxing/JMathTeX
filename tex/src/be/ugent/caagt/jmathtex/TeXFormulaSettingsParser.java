/* TeXFormulaSettingsParser.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import be.ugent.caagt.jmathtex.ex.ResourceParseException;
import be.ugent.caagt.jmathtex.ex.XMLResourceParseException;

/**
 * 从配置文件(当前是 TeXFormulaSettings.xml) 中加载预定义的 TeX公式.
 * 
 * 该配置文件含三个主要的子节点:
 *   TextStyles -- different textstyles that used by the parser 
 *   CharacterToSymbolMappings -- non-alphanumeric character-to-symbol-mappings
 *   CharacterToDelimiterMappings -- character-to-delimiter-mappings These are used in 
 *     the method "embrace(char,char)" from the class "Formula". 
 *     The symbolnames must be defined in "TeXSymbols.xml" as delimiters (del="true")!! 
 * 
 * Parses predefined TeXFormula's from an XML-file.
 */
public class TeXFormulaSettingsParser {
    
    public static final String RESOURCE_NAME = "TeXFormulaSettings.xml";
    public static final String CHARTODEL_MAPPING_EL = "Map";
    
    private Element root;
    
    public TeXFormulaSettingsParser() throws ResourceParseException {
        try {
            root = new SAXBuilder().build(
                    TeXFormulaSettingsParser.class.getResourceAsStream(RESOURCE_NAME))
                    .getRootElement();
            
        } catch (Exception e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    /**
     * 从配置文件中加载 <CharacterToSymbolMappings> 子节点, 该子节点下面例子为:
     *   <Map symbol="plus" char="+" />
     * 
     * @return 解析的结果: 一个数组 String[], 索引为字符(char)的编码值, 字符串为该字符的符号名字.
     * @throws ResourceParseException
     */
    public String[] parseSymbolMappings() throws ResourceParseException {
    	// 构造一个数组, 最大 256 个字符编码 => 名字的映射.
        String[] mappings = new String[FontInfo.NUMBER_OF_CHAR_CODES];
        
        // 找到 <CharacterToSymbolMappings> 子节点.
        Element charToSymbol = root.getChild("CharacterToSymbolMappings");
        if (charToSymbol != null) // element present; 如果有则 addToMap()
            addToMap(charToSymbol.getChildren("Map"), mappings);
        return mappings;
    }
    
    /**
     * 从配置文件加载 <CharacterToDelimiterMappings> 子节点, 例子为:
     *   <Map symbol="lbrack" char="(" />
     * 
     * @return 从字符值到 String 的映射. 如 return['('] = "lbrack" 
     * @throws ResourceParseException
     */
    public String[] parseDelimiterMappings() throws ResourceParseException {
        String[] mappings = new String[FontInfo.NUMBER_OF_CHAR_CODES];
        Element charToDelimiter = root.getChild("CharacterToDelimiterMappings");
        if (charToDelimiter != null) // element present
            addToMap(charToDelimiter.getChildren(CHARTODEL_MAPPING_EL),
                    mappings);
        return mappings;
    }
    
    /**
     * 将 <Map> 子节点解析之后放入 table 映射中.
     * @param mapList <CharacterToSymbolMappings> 节点下 <Map> 子节点列表.
     * @param table
     * @throws ResourceParseException
     */
    @SuppressWarnings("unchecked")
	private static void addToMap(List mapList, String[] table) throws ResourceParseException {
        for (Object obj : mapList) { // 遍历所有 <Map> 子节点.
            Element map = (Element) obj; // 例如 <Map symbol="minus" char="-" />
            String ch = map.getAttributeValue("char");       // 对应字符
            String symbol = map.getAttributeValue("symbol"); // 符号名
            // both attributes are required! 异常处理很多...
            if (ch == null)
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "char", null);
            else if (symbol == null)
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "symbol", null);
            if (ch.length() == 1) // valid element found; 只允许 char 为一个字符.
                table[ch.charAt(0)] =  symbol;
            else
                // only single-character mappings allowed, ignore others
                throw new XMLResourceParseException(RESOURCE_NAME, map.getName(),
                        "char",
                        "must have a value that contains exactly 1 character!");
        }
    }
    
    /**
     * 解析子节点 <TextStyles>
     * @return 返回一个集合, 内容为所有 TextStyle 的名字. 当前配置有 "mathrm", "mathit", "mathcal"
     * @throws ResourceParseException
     */
    public Set<String> parseTextStyles() throws ResourceParseException {
        Set<String> res = new HashSet<String>();
        Element textStyles = root.getChild("TextStyles");
        if (textStyles != null) { // element present
        	// 遍历 <TextStyle> 子节点, 如 <TextStyle name="mathrm" />
            for (Object obj : textStyles.getChildren("TextStyle")) {
                Element style = (Element) obj; // <TextStyle> 子节点
                String name = style.getAttributeValue("name");
                if (name == null)
                    throw new XMLResourceParseException(RESOURCE_NAME, style
                            .getName(), "name", null);
                else
                    res.add(name); // 加入到集合中.
            }
        }
        return res;
    }
}
