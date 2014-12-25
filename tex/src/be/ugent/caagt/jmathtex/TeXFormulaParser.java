/* TeXFormulaParser.java
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
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.jdom.Element;

import be.ugent.caagt.jmathtex.ex.ResourceParseException;
import be.ugent.caagt.jmathtex.ex.XMLResourceParseException;

/**
 * 解析 XML 配置文件中的一个 'TeXForumla' 元素, 其表示一个预定义的 TeXFormula .
 * Parses a "TeXFormula"-element representing a predefined TeXFormula's from an XML-file.
 */
@SuppressWarnings("unchecked")
class TeXFormulaParser {
    
	/** 解析 <TeXFormula> 下子节点的解析器的接口定义. */
    private interface ActionParser { // NOPMD
        public void parse(Element el) throws ResourceParseException;
    }
    
    /**
     * 解析子节点 <CreateTeXFormula> under <TeXFormula> 的辅助类. 
     * 例子: <CreateTeXFormula name="f">
     *        <Argument value="\not\equals" type="String" />
     *      </CreateTeXFormula>
     */
    private class CreateTeXFormulaParser implements ActionParser {
        
        CreateTeXFormulaParser () {
            // avoids creation of special accessor type
        }
        
        /** 解析并将结果放入到 tempFormulas 中. */
        public void parse(Element el) throws ResourceParseException {
            // get required string attribute; 如 <CreateTeXFormula name="f">, 得到名字 "f"
            String name = getAttrValueAndCheckIfNotNull("name", el);
            
            // parse arguments; 解析参数子节点, 如: <Argument value="\not\equals" type="String" />
            List args = el.getChildren("Argument");
            // get argument classes and values; 得到参数类型(type)和值(value).
            Class[] argClasses = getArgumentClasses(args); // 得到所有参数类型对应的 java Class.
            Object[] argValues = getArgumentValues(args);  // 参数值.
            
            // 这里 argClasses 用于查找 TeXFormula 适合的重载构造函数; argValues 用于调用该重载函数.
            
            // create TeXFormula object; 构造一个 TeXFormula 对象, 使用所给名字放到临时 map 中.
            try {
            	/* 这里使用了反射, 来使用指定的参数 argValues 构造 TeXFormula 对象.
            	 * 根据配置看, 重载的构造函数可能会有几种:
            	 *   TeXFormula(String s) -- 有一个字符串参数.
            	 * 实际配置文件中只见到有这种的. 也许还有别的?? 或未来支持别的??
            	 * 这样未免有点复杂..., 虽然看起来扩展性挺好...?
            	 */ 
                TeXFormula f = (TeXFormula) TeXFormula.class.getConstructor(
                        argClasses).newInstance(argValues);
                // succesfully created, so add to "temporary formula's"-hashtable
                tempFormulas.put(name, f);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        "Error creating the temporary TeXFormula '" + name
                        + "' while constructing the predefined TeXFormula '"
                        + formulaName + "'!", e);
            }
        }
    }
    
    /**
     * 解析 <TeXFormula> 下子节点 <MethodInvocation> 的辅助类.
     * MethodInvocation 子节点下面是 0到多个 <Argument> 子节点, 表示调用指定函数的参数.
     *
     */
    private class MethodInvocationParser implements ActionParser {
        
        MethodInvocationParser () {
            // avoids creation of special accessor type
        }
        
        public void parse(Element el) throws ResourceParseException {
            // get required string attributes
            String methodName = getAttrValueAndCheckIfNotNull("name", el); // 要调用的函数名.
            String objectName = getAttrValueAndCheckIfNotNull(ARG_OBJ_ATTR, el); // 对该对象调用. ARG_OBJ_ATTR="formula"
            // check if temporary TeXFormula exists
            Object object = tempFormulas.get(objectName); // 得到该名字指定的对象. 一般是 TeXFormula...
            if (object == null) // doesn't exist
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_OBJ_ATTR,
                        "has an unknown temporary TeXFormula name as value : '"
                        + objectName + "'!");
            else {
                // parse arguments
                List args = el.getChildren("Argument");
                // get argument classes and values; 得到调用的参数类型和值.
                Class[] argClasses = getArgumentClasses(args); // 参数类型, 通过它找到可能重载的函数.
                Object[] argValues = getArgumentValues(args);  // 参数值, 传递给函数.
                // invoke method
                try {
                    TeXFormula.class.getMethod(methodName, argClasses).invoke(
                            (TeXFormula) object, argValues); // 用上述参数值调用该函数. 可能这也是为什么 TeXFormula 有大量函数的原因?
                } catch (Exception e) {
                    throw new XMLResourceParseException(
                            "Error invoking the method '" + methodName
                            + "' on the temporary TeXFormula '" + objectName
                            + "' while constructing the predefined TeXFormula '"
                            + formulaName + "'!", e);
                }
            }
        }
    }
    
    /**
     * <TeXFormula> 的子节点 <Return> 的动作解析器.
     * "Return" 子节点有一个必选属性 name, 表示要返回的 (?都是 TeXFormula) 对象名字.
     *
     */
    private class ReturnParser implements ActionParser {
        
        ReturnParser () {
            // avoids creation of special accessor type
        }
        
        public void parse(Element el) throws ResourceParseException {
            // get required string attribute; 得到 name 属性
            String name = getAttrValueAndCheckIfNotNull("name", el);
            Object res = tempFormulas.get(name); // 得到该名字指定的对象. 一般是 TeXFormula?
            if (res == null)
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, RETURN_EL, "name",
                        "contains an unknown temporary TeXFormula variable name '"
                        + name + "' for the predefined TeXFormula '"
                        + formulaName + "'!");
            else
                result = (TeXFormula) res; // 这里 `限定' 了返回的对象都是 TeXFormula.
        }
    }
    

    /** 根据不同类型解析 <Argument> 子节点的解析器的接口定义 */
    private interface ArgumentValueParser { // NOPMD
        public Object parseValue(String value, String type) throws ResourceParseException;
    }

    /**
     * 解析浮点值. 
     *
     */
    private class FloatValueParser implements ArgumentValueParser {
        
        FloatValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type) throws ResourceParseException {
            checkNullValue(value, type);
            try {
                return new Float(Float.parseFloat(value));
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!", e);
            }
        }
    }
    
    private class CharValueParser implements ArgumentValueParser {
        
        CharValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            if (value.length() == 1)
                return new Character(value.charAt(0));
            else
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR,
                        "must have a value that consists of exactly 1 character!");
        }
    }
    
    /**
     * 布尔值解析器. 只支持 true/false 两种值.
     *
     */
    private class BooleanValueParser implements ArgumentValueParser {
        
        BooleanValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type) throws ResourceParseException {
            checkNullValue(value, type);
            if ("true".equals(value))
                return Boolean.TRUE;
            else if ("false".equals(value))
                return Boolean.FALSE;
            else
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!");
            
        }
    }
    
    /**
     * 整数值解析器. 
     *
     */
    private class IntValueParser implements ArgumentValueParser {
        
        IntValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type) throws ResourceParseException {
            checkNullValue(value, type);
            try {
                int val = Integer.parseInt(value);
                return new Float((int) val); // 转为浮点值.
            } catch (NumberFormatException e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an invalid '" + type + "'-value : '"
                        + value + "'!", e);
            }
        }
    }
    
    /**
     * 字符串值的解析器. 这个简单. 
     *
     */
    private class StringValueParser implements ArgumentValueParser {
        
        StringValueParser  () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type) throws ResourceParseException {
            return value;
        }
    }
    
    /**
     * 参数 <Argument> 当 type="TeXFormula" 时值的解析器. (实际配置文件中未见该类型的配置项??)
     *
     */
    private class TeXFormulaValueParser implements ArgumentValueParser {
        
        TeXFormulaValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type) throws ResourceParseException {
            if (value == null) // null pointer argument
                return null;
            else {
                Object formula = tempFormulas.get(value);
                if (formula == null) // unknown temporary TeXFormula!
                    throw new XMLResourceParseException(
                            PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                            ARG_VAL_ATTR,
                            "has an unknown temporary TeXFormula name as value : '"
                            + value + "'!");
                else
                    return (TeXFormula) formula;
            }
        }
    }
    
    /**
     * 参数子节点(<Argument>) 类型为 "TeXConstants" 时值的解析器.
     */
    private class TeXConstantsValueParser implements ArgumentValueParser {
        
        TeXConstantsValueParser () {
            // avoids creation of special accessor type
        }
        
        // 解析值. 如 type=TeXConstants value=UNIT_MU; 其从 TeXConstants 里面用反射获取. 
        public Object parseValue(String value, String type) throws ResourceParseException {
            checkNullValue(value, type);
            try {
                // get constant value (if present); 从 TeXConstants 里面查找是否有该常量定义. (反射)
                int constant = TeXConstants.class.getDeclaredField(value).getInt(
                        null);
                // return constant integer value
                return Integer.valueOf(constant);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR, "has an unknown constant name as value : '"
                        + value + "'!", e);
            }
        }
    }
    
    /**
     * 同样十分不幸, XML 中没有这种配置项. 
     *
     */
    private class ColorConstantValueParser implements ArgumentValueParser {
        
        ColorConstantValueParser () {
            // avoids creation of special accessor type
        }
        
        public Object parseValue(String value, String type)
        throws ResourceParseException {
            checkNullValue(value, type);
            try {
                // return Color constant (if present)
                return Color.class.getDeclaredField(value).get(null);
            } catch (Exception e) {
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                        ARG_VAL_ATTR,
                        "has an unknown color constant name as value : '" + value
                        + "'!", e);
            }
        }
    }
    
    
    
    // ?为什么这几个字符串要弄成常量, 而别的却不弄呢??
    private static final String ARG_VAL_ATTR = "value";
    private static final String RETURN_EL = "Return";
    private static final String ARG_OBJ_ATTR = "formula";
    
    private static Map<String, Class<?>> classMappings = new HashMap<String, Class<?>>();
    
    /**
     * 类型名字 => 映射到各类型 `参数' 的解析器.
     */
    private final Map<String, ArgumentValueParser> argValueParsers = new HashMap<String, ArgumentValueParser>();
    
    /**
     * 各种 <TeXFormula> 下面的子节点的解析器, 根据节点名字得到该对应的解析器.
     * 现在 <TeXFormula> 节点下面共有如下种类的子节点:
     *   <CreateTeXFormula> -- 对应类 CreateTeXFormulaParser
     *   <MethodInvocation> -- 对应类 MethodInvocationParser
     *   <Return> (RETURN_EL) -- 对应类 ReturnParser
     *   
     */
    private final Map<String, ActionParser> actionParsers = new HashMap<String, ActionParser>();
    
    private final Map<String, TeXFormula> tempFormulas = new HashMap<String, TeXFormula>();
    
    private TeXFormula result = new TeXFormula();
    
    private final String formulaName;
    
    private final Element formula;
    
    // 静态初始化. ?? 为什么 classMappings 可以静态初始化, 而 actionParsers, argValueParsers 却不能呢?
    // 而且 classMappings, argValueParsers 的键是一样, 难道不可以用一个 class 来完成所有的 type, value 的解析呢?
    static {
        // string-to-class mappings; 字符串名字-到-类的映射.
        classMappings.put("TeXConstants", int.class);        // all integer constants
        classMappings.put("TeXFormula", TeXFormula.class);
        classMappings.put("String", String.class);
        classMappings.put("float", float.class);
        classMappings.put("int", int.class);
        classMappings.put("boolean", boolean.class);
        classMappings.put("char", char.class);
        classMappings.put("ColorConstant", Color.class);
    }
    
    /**
     * 构造.
     * @param name -- 此公式的名字.
     * @param formula -- XML 子节点 <TeXFormula>, 下面可能还含有多个不同的子节点.
     */
    public TeXFormulaParser(String name, Element formula) {
        formulaName = name;
        this.formula = formula;
        
        // action parsers; 子节点名-to-解析器 映射.
        actionParsers.put("CreateTeXFormula", new CreateTeXFormulaParser());
        actionParsers.put("MethodInvocation", new MethodInvocationParser());
        actionParsers.put(RETURN_EL, new ReturnParser()); // RETURN_EL="Return"
        
        // argument value parsers; 子节点 <Argument> 的属性 type 的解析. 这里似乎要使用动态 java 反射来调用...
        argValueParsers.put("TeXConstants", new TeXConstantsValueParser());
        argValueParsers.put("TeXFormula", new TeXFormulaValueParser());
        argValueParsers.put("String", new StringValueParser());
        argValueParsers.put("float", new FloatValueParser());
        argValueParsers.put("int", new IntValueParser());
        argValueParsers.put("boolean", new BooleanValueParser());
        argValueParsers.put("char", new CharValueParser());
        argValueParsers.put("ColorConstant", new ColorConstantValueParser());
    }
    
    /**
     * 解析一个 <TeXFormula> 节点, 并返回解析结果.
     * @return
     * @throws ResourceParseException
     */
    public TeXFormula parse() throws ResourceParseException {
        // parse and execute actions; 解析及执行动作.
        for (Object obj : formula.getChildren()) { // 遍历所有 <TeXFormula> 下面的子节点.
            Element el = (Element) obj;
            ActionParser p = actionParsers.get(el.getName()); // 找到对应该子节点名字的解析器.
            if (p != null) // ignore unknown elements
               p.parse(el); // 用对应的解析器负责解析~~~, 当前有三种.
        }
        
        // 最后返回结果为 result. (其在 "Return" 子节点解析器中设置)
        return result;
    }
    
    /** 解析指定列表中子节点的 value 属性值. */
	private Object[] getArgumentValues(List args) {
        Object[] res = new Object[args.size()];
        int i = 0;
        for (Object obj : args) {
            Element arg = (Element) obj;
            // get required string attribute; 得到属性 type, (其实在 getArgumentClasses 也读取过的?)
            String type = getAttrValueAndCheckIfNotNull("type", arg);
            // get value, not present means a nullpointer; 获得 value 值
            String value = arg.getAttributeValue(ARG_VAL_ATTR); // ARG_VAL_ATTR="value"
            
            // parse value, hashtable will certainly contain a parser for the class type,
            // because the class types have been checked before!
            ArgumentValueParser parser = argValueParsers.get(type); // 得到对应类型的值的解析器.
            res[i] = parser.parseValue(value, type); // 解析该值.
            i++;
        }
        return res;
    }
    
	/** 解析指定列表中 (是<Argument>) 子节点的属性 type, 并转换为 java 的 Class 对象. */
    private static Class[] getArgumentClasses(List args) throws ResourceParseException {
        Class[] res = new Class[args.size()]; // 参数数量个.(可能有多个参数)
        int i = 0;
        for (Object obj : args) { // 如 <Argument value="\not\equals" type="String" />
            Element arg = (Element) obj;
            // get required string attribute; 属性 type.
            String type = getAttrValueAndCheckIfNotNull("type", arg);
            // find class mapping; 根据 type 字符串名字映射到 Class 类. ==> 映射为 java 内部的类了.
            Object cl = classMappings.get(type);
            if (cl == null) // no class mapping found
                throw new XMLResourceParseException(
                        PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument", "type",
                        "has an invalid class name value!");
            else
                res[i] = (Class) cl;
            i++;
        }
        return res;
    }
    
    private static void checkNullValue(String value, String type)
    throws ResourceParseException {
        if (value == null)
            throw new XMLResourceParseException(
                    PredefinedTeXFormulaParser.RESOURCE_NAME, "Argument",
                    ARG_VAL_ATTR, "is required for an argument of type '" + type
                    + "'!");
    }
    
    private static String getAttrValueAndCheckIfNotNull(String attrName,
            Element element) throws ResourceParseException {
        String attrValue = element.getAttributeValue(attrName);
        if (attrValue == null)
            throw new XMLResourceParseException(
                    PredefinedTeXFormulaParser.RESOURCE_NAME, element.getName(),
                    attrName, null);
        return attrValue;
    }
}
