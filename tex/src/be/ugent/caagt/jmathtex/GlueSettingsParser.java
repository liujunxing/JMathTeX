/* GlueSettingsParser.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import be.ugent.caagt.jmathtex.ex.ResourceParseException;
import be.ugent.caagt.jmathtex.ex.XMLResourceParseException;

/**
 * Parses the glue settings (different types and rules) from an XML-file.
 */
class GlueSettingsParser {
    
    private static final String RESOURCE_NAME = "GlueSettings.xml";
    
    /** 从 char symbol type 名字到值的映射; 如 "ord" => 0 */
    private final Map<String,Integer> typeMappings = new HashMap<String,Integer>();
    
    /** display style 名字到值的映射, 如 "script" => 2 */
    private final Map<String,Integer> styleMappings = new HashMap<String,Integer>();
    
    /** 从粘连(glue)类型名字到索引(glueTypes[]数组的索引)的映射. */
    private final Map<String,Integer> glueTypeMappings = new HashMap<String,Integer>();
    /** */
    private Glue[] glueTypes;
    
    private Element root;
    
    // 构造就即刻解析.
    public GlueSettingsParser() throws ResourceParseException {
        try {
            setTypeMappings();	// 设置类型名字=>类型整数的映射.
            setStyleMappings();	// 设置显示样式(display style) => 显示类型值/2 的映射.
            
            root = new SAXBuilder().build(
                    GlueSettingsParser.class.getResourceAsStream(RESOURCE_NAME))
                    .getRootElement();
            
            parseGlueTypes();
        } catch (IOException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        } catch (JDOMException e) { // JDOMException or IOException
            throw new XMLResourceParseException(RESOURCE_NAME, e);
        }
    }
    
    // 注意这里显示类型值都 / (除以) 2.
    private void setStyleMappings() {
        styleMappings.put("display", TeXConstants.STYLE_DISPLAY / 2);
        styleMappings.put("text", TeXConstants.STYLE_TEXT / 2);
        styleMappings.put("script", TeXConstants.STYLE_SCRIPT / 2);
        styleMappings.put("script_script", TeXConstants.STYLE_SCRIPT_SCRIPT / 2); // autoboxing
    }
    
    private void parseGlueTypes() throws ResourceParseException {
        List<Glue> glueTypesList = new ArrayList<Glue> ();
        Element types = root.getChild("GlueTypes"); // 下含几个 <GlueType> 子节点, 看起来是名字到 Glue 的表示.
        int defaultIndex = -1;
        int index = 0;
        if (types != null) { // element present
            for (Object obj : types.getChildren("GlueType")) { // 遍历 <GlueType> 子节点
                Element type = (Element) obj;
                // retrieve required attribute value, throw exception if not set
                // 获取必须有的属性 "name", 如 <GlueType name="thin" ... >
                String name = getAttrValueAndCheckIfNotNull("name", type);
                Glue glue = createGlue(type, name); // 根据 XML 中的配置创建 Glue 对象(解析出 space,stretch,shrink 值)
                if (name.equalsIgnoreCase("default")) // default must have value
                    defaultIndex = index;
                glueTypesList.add(glue);
                index ++;
            }
        }
        if (defaultIndex < 0) {
            // create a default glue object if missing; 也就是必须要有 default 的粘连.
            defaultIndex = index;
            glueTypesList.add(new Glue(0,0,0,"default"));
        }
        
        // 转换为数组.
        glueTypes = glueTypesList.toArray(new Glue[glueTypesList.size()]);
        
        // make sure default glue is at the front; 确保 default 的粘连在第一个位置.
        if (defaultIndex > 0) {
            Glue tmp = glueTypes[defaultIndex];
            glueTypes[defaultIndex] = glueTypes[0];
            glueTypes[0] = tmp;
        }
        
        // make reverse map; 制作反向映射表 -- 从名字到索引的映射.
        for (int i = 0; i < glueTypes.length; i++)
            glueTypeMappings.put(glueTypes[i].getName(), i);
    }
    
    private Glue createGlue(Element type, String name) throws ResourceParseException {
        final String[] names = { "space", "stretch", "shrink" };
        float[] values = new float[names.length];
        for (int i = 0; i < names.length; i++) {
            double val = 0; // default value if attribute not present
            String attrVal = null;
            try {
                attrVal = type.getAttributeValue(names[i]);
                if (attrVal != null) // attribute present
                    val = Double.parseDouble(attrVal);
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(RESOURCE_NAME, "GlueType",
                        names[i], "has an invalid real value '" + attrVal + "'!");
            }
            values[i] = (float) val;
        }
        return new Glue(values[0], values[1], values[2], name);
    }
    
    // 设置从 类型名字 到 TYPE_xxx 的映射. 这些名字和在别的配置文件中看到的是一样的.
    private void setTypeMappings() {
        typeMappings.put("ord",   TeXConstants.TYPE_ORDINARY);
        typeMappings.put("op",    TeXConstants.TYPE_BIG_OPERATOR);
        typeMappings.put("bin",   TeXConstants.TYPE_BINARY_OPERATOR);
        typeMappings.put("rel",   TeXConstants.TYPE_RELATION);
        typeMappings.put("open",  TeXConstants.TYPE_OPENING);
        typeMappings.put("close", TeXConstants.TYPE_CLOSING);
        typeMappings.put("punct", TeXConstants.TYPE_PUNCTUATION);
        typeMappings.put("inner", TeXConstants.TYPE_INNER); // autoboxing
    }
    
    public Glue[] getGlueTypes() {
        return glueTypes;
    }
    
    /** 
     * 加载 GlueTable 并返回. GlueTable 索引为 [left_type][right_type][display_style], 
     *   值为 glue_type 索引. (意义应该是根据左右 atom 的类型, 以及当前显示样式, 选择合适的粘连间距)
     * (从 Glue 中调用)
     * 
     * @return
     * @throws ResourceParseException
     */
    public int[][][] createGlueTable() throws ResourceParseException {
        int size = typeMappings.size();
        // table 大小: type*type*style (=8*8*4)
        int[][][] table = new int[size][size][styleMappings.size()];
        Element glueTable = root.getChild("GlueTable"); // <GlueTable> 节点, 下含多个 <Glue> 子节点
        if (glueTable != null) { // element present
            // iterate all the "Glue"-elements; 遍历所有 <Glue> 子节点, 例子: <Glue gluetype="thin" righttype="op" lefttype="ord" >
            for (Object obj : glueTable.getChildren("Glue")) {
                Element glue = (Element) obj;
                // retrieve required attribute values and throw exception if they're not set
                String left = getAttrValueAndCheckIfNotNull("lefttype", glue); 
                String right = getAttrValueAndCheckIfNotNull("righttype", glue);
                String type = getAttrValueAndCheckIfNotNull("gluetype", glue);
                
                // iterate all the "Style"-elements; <Glue> 下面还有多个 <Style> 子节点, 遍历它. 
                for (Object object : glue.getChildren("Style")) {
                    Element style = (Element) object;
                    String styleName = getAttrValueAndCheckIfNotNull("name", style);
                    // retrieve mappings; 分别从名字映射到数字值.
                    Object l = (Integer) typeMappings.get(left); 	// lefttype
                    Object r = (Integer) typeMappings.get(right); 	// righttype
                    Object st = (Integer) styleMappings.get(styleName);	// display style
                    Object val = (Integer) glueTypeMappings.get(type);	// glue type
                    
                    // throw exception if unknown value set
                    checkMapping(l, "Glue", "lefttype", left);
                    checkMapping(r, "Glue", "righttype", right);
                    checkMapping(val, "Glue", "gluetype", type);
                    checkMapping(st, "Style", "name", styleName);
                    
                    // put value in table; 放入表格中, 以 lefttype,righttype,display style 为索引以能找到 glue_type
                    table[((Integer) l).intValue()][((Integer) r).intValue()][((Integer) st)
                    .intValue()] = ((Integer) val).intValue();
                }
            }
        }
        return table;
    }
    
    private static void checkMapping(Object val, String elementName,
            String attrName, String attrValue) throws ResourceParseException {
        if (val == null)
            throw new XMLResourceParseException(RESOURCE_NAME, elementName,
                    attrName, "has an unknown value '" + attrValue + "'!");
    }
    
    private static String getAttrValueAndCheckIfNotNull(String attrName,
            Element element) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null)
            throw new XMLResourceParseException(RESOURCE_NAME, element.getName(),
                    attrName, null);
        return attrValue;
    }
}
